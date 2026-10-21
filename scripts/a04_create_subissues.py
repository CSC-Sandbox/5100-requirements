#!/usr/bin/env python3
"""Create and attach A04 native GitHub sub-issues, safely rerunnable."""
import json
import os
import sys
import time
import urllib.error
import urllib.request

OWNER, REPO = "CSC-Sandbox", "5100-requirements"
API = "https://api.github.com"
TOKEN = os.environ["GH_TOKEN"]
STORIES = {
    272: ("GazeClusterLambda", "POST /5100/gaze-clusters"),
    273: ("EncryptMessageLambda", "GET /5100/encrypt"),
    264: ("DominantAffectLambda", "POST /5100/dominant-affect"),
    274: ("NearestSampleLambda", "POST /5100/nearest-sample"),
    265: ("RobotDistanceLambda", "GET /5100/robot-distance"),
    266: ("RobotReachabilityLambda", "GET /5100/robot-reachability"),
    267: ("LidarClosestLambda", "POST /5100/lidar-closest"),
    268: ("LidarSafetyLambda", "POST /5100/lidar-safety"),
    269: ("DataFreshnessLambda", "GET /5100/data-freshness"),
    275: ("MovingAverageLambda", "POST /5100/moving-average"),
    270: ("CoordinateNormalizerLambda", "GET /5100/coordinate-normalizer"),
    271: ("EventRateLambda", "GET /5100/event-rate"),
}
TASKS = [
    ("REST contract and UUID lookup", "Preserve the original endpoint behavior and response fields. Add a UUID to successful results and support GET retrieval by UUID without breaking the original request contract. Include positive and invalid-input JUnit tests. Acceptance: existing endpoint tests pass; POST/GET or existing GET operation works; GET by UUID returns the same stored result."),
    ("DynamoDB persistence", "Persist each successful result in DynamoDB table csc5100-results with the generated UUID as the String partition key. Acceptance: one item per operation; lookup by UUID returns the original values; failures have useful errors."),
    ("S3 JSON persistence", "Store the same result as JSON in S3 bucket csc5100-results using a student/function-specific prefix and UUID.json object key. Acceptance: UUID and result agree with DynamoDB; no secrets in repository."),
    ("SNS event notification", "Publish a JSON event with UUID, operation, and result to SNS topic csc5100-events. SNS and SQS are independent branches. Acceptance: show publishing evidence and test failure handling."),
    ("SQS event processing and MQTT forwarding", "Send JSON event with UUID to SQS queue csc5100-processing. Verify instructor consumer forwards the message to AWS IoT Core and HiveMQ on the configured event topic. Acceptance: provide SQS and MQTT evidence with matching UUID; do not implement SNS-to-SQS wiring."),
    ("Java MQTT desktop client and JUnit tests", "Implement a Java Eclipse Paho desktop console or GUI application that subscribes to your assigned function-specific topic under csc5100/events/, parses and displays JSON, and publishes an acknowledgment to csc5100/status/<uuid> (QoS 1). Include JUnit unit tests for parsing/ACK logic and a tagged HiveMQ live integration test using unique IDs, CountDownLatch, and a bounded timeout. Acceptance: Java source, passing tests, and console/screenshot evidence from a real REST-triggered event; synthetic data only on public broker."),
    ("End-to-end verification and A04 PR", "Submit one PR titled A04 - <LambdaName> with body A04 Story: #<parent>. Include curl requests/responses, JUnit results, DynamoDB and S3 evidence, SNS/SQS and both MQTT paths, desktop receipt, and ACK. Close all seven native sub-issues only when complete. Acceptance: reviewer can reproduce results and PR validator recognizes seven closed sub-issues."),
]

def call(method, path, payload=None):
    data = None if payload is None else json.dumps(payload).encode()
    req = urllib.request.Request(API + path, data=data, method=method, headers={
        "Authorization": "Bearer " + TOKEN, "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28", "Content-Type": "application/json",
        "User-Agent": "csc5100-a04-subissue-creator"})
    try:
        with urllib.request.urlopen(req, timeout=25) as resp:
            raw = resp.read()
            return json.loads(raw) if raw else {}
    except urllib.error.HTTPError as e:
        raise RuntimeError(f"{method} {path}: HTTP {e.code}: {e.read().decode()[:500]}") from e

def main():
    selected = sys.argv[1]
    ids = [272] if selected == "pilot" else list(STORIES) if selected == "all" else []
    if not ids:
        raise ValueError("Use pilot or all")
    for parent in ids:
        name, endpoint = STORIES[parent]
        parent_path = f"/repos/{OWNER}/{REPO}/issues/{parent}"
        parent_issue = call("GET", parent_path)
        if parent_issue["state"] != "open":
            raise RuntimeError(f"Story #{parent} is not open")
        existing = call("GET", parent_path + "/sub_issues?per_page=100")
        linked = {i["title"]: i["number"] for i in existing}
        assignees = [a["login"] for a in parent_issue.get("assignees", [])]
        print(f"Story #{parent}: {name} / {endpoint}; assignees={assignees}; linked={len(linked)}", flush=True)
        for n, (label, details) in enumerate(TASKS, 1):
            title = f"A04 #{parent} Task {n} — {label}"
            if title in linked:
                print(f"  Already linked #{linked[title]}: {title}", flush=True)
                continue
            # Check for a previously created, but not yet linked, task to make retries safe.
            query = urllib.parse.quote(f'repo:{OWNER}/{REPO} in:title "{title}"')
            matches = call("GET", f"/search/issues?q={query}&per_page=100")["items"]
            exact = [i for i in matches if i["title"] == title]
            if len(exact) > 1:
                raise RuntimeError(f"Duplicate tasks found for {title}; resolve manually")
            if exact:
                child = exact[0]["number"]
            else:
                payload = {"title": title, "body": f"Parent Story: #{parent}\n\n**Lambda:** `{name}`\n\n**Task {n} acceptance criteria**\n{details}"}
                if assignees:
                    payload["assignees"] = assignees
                child = call("POST", f"/repos/{OWNER}/{REPO}/issues", payload)["number"]
                print(f"  Created #{child}", flush=True)
            call("POST", parent_path + "/sub_issues", {"sub_issue_id": call("GET", f"/repos/{OWNER}/{REPO}/issues/{child}")["id"]})
            print(f"  Linked #{child} as native sub-issue", flush=True)
        final = call("GET", parent_path + "/sub_issues?per_page=100")
        if len(final) != 7:
            raise RuntimeError(f"Story #{parent}: expected 7 native sub-issues, found {len(final)}")
        print(f"Verified Story #{parent}: 7 native sub-issues", flush=True)

if __name__ == "__main__":
    import urllib.parse
    main()
