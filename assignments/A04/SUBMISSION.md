# A04 submission policy (proposed)

For A04, each student owns one **[A04]** feature Story with **seven native GitHub sub-issue Tasks**.

- Move Tasks through Todo → In Progress → Done as work progresses.
- For automated validation, **Done means the native sub-issue is closed**. A GitHub Projects status of Done alone is not sufficient; keep the issue state synchronized.
- Only submit the A04 PR after all seven Tasks are Done.
- Use the title `A04 - <Feature> - <GitHubID>` and include the exact line `A04 Story: #123` in the PR description.
- The submission workflow checks the Story and seven sub-issues on PR creation/updates. Incomplete submissions receive a comment and are converted to Draft automatically. Students can mark them Ready for review after completing the Tasks.
- The instructor alone approves and merges. This workflow does not merge code.

**Implementation note:** GitHub cannot prevent PR creation. This is a submission/readiness gate, not a creation restriction. It checks native sub-issue closure, not historical progress activity or the correctness of implementation. Instructor review and Maven Validation remain necessary.

**Activation:** This workflow is intended to be merged before student A04 submissions begin. It does not add a global required check to the branch ruleset, since it intentionally skips non-A04 PRs.
