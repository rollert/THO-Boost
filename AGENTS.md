# Personal Boost V4 working rules

This is Thomas' personal Boost variant. Follow the parent AAPS workspace rules.

- Check exact repository, branch and dirty state before edits or builds.
- `origin` is `rollert/Boost-THO` (renamed existing fork); `upstream` is `tim2000s/Boost`. Integrated personal and GitHub default branch: `tho/main`.
- Keep the old AAPS-3.4-BOOST reference and archive branch unchanged.
- Read `docs/tho/CUSTOMIZATIONS.md` before upstream integration. Every retained behavior needs a production-path test or explicit evidence that upstream already implements it.
- Update on a short `update/<release>` branch from a pinned upstream tag, test, then merge without rewriting published personal history.
- Use the workspace Boost-THO build profile (JDK21, one worker, 2 GiB Gradle/Kotlin heaps). Do not run Android Studio and CLI builds concurrently.
- Auto-config evaluates once daily over four days; do not conflate that with independent live safety windows.
- Keep product patches minimal to limit future upstream merge conflicts; avoid unrelated refactoring.
- Runtime acceptance requires real Android/Bluetooth hardware. Thomas explicitly excluded emulator testing: do not create or launch emulators for this project.
- Build and test exclusively locally. Do not add or trigger GitHub builds unless Thomas changes this decision. GitHub is used for source/history only.
- Release signing, destructive cleanup, force-push and therapy-phone installation require separate explicit authorization under workspace policy.
- Keep private keys, health data and local configuration out of Git and artifact folders.
- Workflow and validation evidence: `docs/tho/WORKFLOW.md`, `VALIDATION.md`, `MIGRATION.md`.
