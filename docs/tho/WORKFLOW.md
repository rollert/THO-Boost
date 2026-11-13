# Personal Boost workflow

Status: repository policy set; exact V4 test/build commands await baseline verification.

## Repository roles

- `origin`: Thomas' existing `rollert/Boost-in-AAPS_3.4` fork.
- `upstream`: `tim2000s/Boost`.
- `tho/main`: personal integrated line. During initial migration it is not yet a validated app.
- `port/<topic>`: initial migration; later `feature/<topic>` for personal development.
- `update/<release>`: upstream integration and verification before changing the personal main line.
- `archive/tho-aaps3-2026-11-13`: preserve forever; do not sync, rebase or replace it.

## Before every change/build

Confirm repo root, branch and clean/dirty state. Android Studio MCP must select this clone before IDE edits/builds. Do not edit the old reference or run both IDE and CLI builds simultaneously.

## Upstream updates

1. Clean `tho/main`, `git fetch upstream --tags`.
2. Select a specific reviewed upstream tag and record its SHA.
3. `git switch -c update/<release> tho/main`.
4. `git merge <selected-tag>`; review changes against every CUSTOMIZATIONS.md ID.
5. Execute verified tests/builds from VALIDATION.md. Record reports and intended behavior changes.
6. Only after passing, merge the update branch into `tho/main`, push explicitly to origin and create a personal candidate tag.
7. If verification fails, preserve the update branch for diagnosis and leave the last validated personal release intact. Do not use destructive resets or discard personal commits via GitHub Sync Fork.

## Personal changes

One behavior per short branch; add production behavior tests, implement minimally, update CUSTOMIZATIONS.md, run relevant suites and integrate only with evidence. Preserve released history; no force-push workflow.

## Builds

Stock `boost-ci.yml` builds an upstream tag, not personal code; do not use it to validate this branch. Until a personal unsignied validation workflow is established, use local wrapper commands recorded in VALIDATION.md. Release signing requires an explicit separate instruction; do not export secrets. APK exports go under the workspace BOOST artifact target with commit and checksum metadata.
