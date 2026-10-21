#!/usr/bin/env python3
"""Sync A04 native issues to Sprint 3 GitHub Project fields, using #272 as reference.

Requires A04_PROJECT_TOKEN: classic PAT with repo + project scopes, or fine-grained
token with Issues write and organization Projects read/write access.
Runs after native sub-issues have been created; safe to rerun.
"""
import json
import os
import sys
import urllib.request
import urllib.error

ORG, REPO = "CSC-Sandbox", "5100-requirements"
STORIES = [264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275]
TOKEN = os.environ["A04_PROJECT_TOKEN"]
API = "https://api.github.com/graphql"

def gql(query, variables=None):
    req = urllib.request.Request(API, data=json.dumps({"query":query,"variables":variables or {}}).encode(), headers={
        "Authorization": "Bearer " + TOKEN, "Content-Type": "application/json",
        "Accept": "application/vnd.github+json", "X-GitHub-Api-Version": "2022-11-28"})
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            data = json.load(response)
    except urllib.error.HTTPError as error:
        raise RuntimeError(f"GitHub GraphQL HTTP {error.code}: {error.read().decode()[:600]}") from error
    if data.get("errors"):
        raise RuntimeError("GitHub GraphQL: " + json.dumps(data["errors"])[:800])
    return data["data"]

ISSUE = """query($owner:String!,$repo:String!,$number:Int!){
 repository(owner:$owner,name:$repo){issue(number:$number){
 id number title issueType{id name}
 subIssues(first:100){nodes{id number title issueType{id name}}}
 projectItems(first:100){nodes{id project{id title number}
 fieldValues(first:100){nodes{
 ... on ProjectV2ItemFieldSingleSelectValue{field{... on ProjectV2SingleSelectField{id name}} optionId name}
 ... on ProjectV2ItemFieldIterationValue{field{... on ProjectV2IterationField{id name}} iterationId title}
 ... on ProjectV2ItemFieldNumberValue{field{... on ProjectV2Field{id name}} number}
 }}}}
 }}}"""
FIELDS = """query($id:ID!){node(id:$id){... on ProjectV2{
 id title fields(first:100){nodes{
 ... on ProjectV2SingleSelectField{id name options{id name}}
 ... on ProjectV2IterationField{id name configuration{iterations{id title} completedIterations{id title}}}
 ... on ProjectV2Field{id name dataType}
 }}}}}"""
TYPES = """query($org:String!){organization(login:$org){issueTypes(first:100){nodes{id name}}}}"""
ADD = """mutation($project:ID!,$content:ID!){addProjectV2ItemById(input:{projectId:$project,contentId:$content}){item{id}}}"""
UPDATE_FIELD = """mutation($input:UpdateProjectV2ItemFieldValueInput!){updateProjectV2ItemFieldValue(input:$input){projectV2Item{id}}}"""
UPDATE_TYPE = """mutation($id:ID!,$type:ID!){updateIssue(input:{id:$id,issueTypeId:$type}){issue{id}}}"""

def issue(number):
    result = gql(ISSUE, {"owner":ORG,"repo":REPO,"number":number})
    value = result["repository"]["issue"]
    if value is None:
        raise RuntimeError(f"Missing issue #{number}")
    return value

def field_map(item):
    result = {}
    for value in item["fieldValues"]["nodes"]:
        field = value.get("field")
        if field:
            result[field["name"].lower()] = value
    return result

def main():
    template = issue(272)
    if len(template["subIssues"]["nodes"]) != 7:
        raise RuntimeError("Pilot #272 must have exactly 7 native sub-issues before syncing")
    candidates = [p for p in template["projectItems"]["nodes"] if
                  "sprint" in field_map(p) and "priority" in field_map(p)]
    if len(candidates) != 1:
        raise RuntimeError("Expected one project item on #272 with Sprint and Priority fields; found " + str(len(candidates)))
    reference = candidates[0]
    project_id = reference["project"]["id"]
    fields = gql(FIELDS, {"id":project_id})["node"]["fields"]["nodes"]
    field_by_name = {f["name"].lower():f for f in fields if f}
    sprint_field = field_by_name.get("sprint")
    if not sprint_field:
        # Some projects call this field Iteration instead of Sprint.
        sprint_field = field_by_name.get("iteration")
    priority_field = field_by_name.get("priority")
    estimate_field = field_by_name.get("estimate")
    if not all((sprint_field,priority_field,estimate_field)):
        raise RuntimeError("Project must contain Sprint/Iteration, Priority and Estimate fields")
    if "configuration" not in sprint_field:
        raise RuntimeError("Sprint field must be an iteration field")
    iterations = sprint_field["configuration"]["iterations"] + sprint_field["configuration"]["completedIterations"]
    sprint = [i for i in iterations if i["title"].strip().lower() == "sprint 3"]
    if len(sprint) != 1:
        raise RuntimeError("Expected exactly one iteration titled Sprint 3")
    medium = [o for o in priority_field["options"] if o["name"].lower() == "medium"]
    if len(medium) != 1:
        raise RuntimeError("Expected Priority option Medium")
    types = gql(TYPES, {"org":ORG})["organization"]["issueTypes"]["nodes"]
    type_map = {t["name"].lower():t["id"] for t in types}
    if "story" not in type_map or "task" not in type_map:
        raise RuntimeError("Organization must have Story and Task issue types")
    print(f'Using project {reference["project"]["title"]}; Sprint 3; Priority Medium; Estimate 8',flush=True)
    for number in STORIES:
        parent = issue(number)
        children = parent["subIssues"]["nodes"]
        if len(children) != 7:
            raise RuntimeError(f"Story #{number} has {len(children)} sub-issues; expected 7. Run the native sub-issue workflow first.")
        if number == 272:
            print("Pilot #272: leave existing project settings unchanged",flush=True)
            continue
        for node, kind in [(parent,"story")] + [(c,"task") for c in children]:
            if not node.get("issueType") or node["issueType"]["name"].lower() != kind:
                gql(UPDATE_TYPE,{"id":node["id"],"type":type_map[kind]})
            # Sub-issues are returned without projectItems: fetch them individually.
            full = parent if kind == "story" else issue(node["number"])
            items = [p for p in full["projectItems"]["nodes"] if p["project"]["id"] == project_id]
            if len(items) > 1:
                raise RuntimeError(f'Duplicate project items for #{node["number"]}')
            item_id = items[0]["id"] if items else gql(ADD,{"project":project_id,"content":node["id"]})["addProjectV2ItemById"]["item"]["id"]
            values = [
                (sprint_field["id"],{"iterationId":sprint[0]["id"]}),
                (priority_field["id"],{"singleSelectOptionId":medium[0]["id"]}),
            ]
            if kind == "task":
                values.append((estimate_field["id"],{"number":8}))
            for field_id,value in values:
                gql(UPDATE_FIELD,{"input":{"projectId":project_id,"itemId":item_id,"fieldId":field_id,"value":value}})
            print(f'Updated #{node["number"]}: {kind}, Sprint 3, Medium' + (", estimate 8" if kind=="task" else ""),flush=True)

if __name__ == "__main__":
    main()
