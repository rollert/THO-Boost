# Personal Boost workflow

Status: personal V4 test candidate. Verified local build/test commands below; device and signing status remain explicit in VALIDATION.md.

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

From the AAPS workspace root, use the tested per-repo profile:

```powershell
& .\_codex\tooling\Build-AapsArtifact.ps1 -ZoneName AAPS-BOOST -RepoName Boost-THO
```

It builds only the phone app's fullDebug task, supplies JDK/SDK/memory settings locally to that process and restores the environment. Exports go to `BOOST\THO-V4` with date and commit in the filename. Release signing remains separately authorized; the helper rejects release builds.

For tests from the Boost-THO root:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot'
$env:ANDROID_HOME = 'C:\Users\linus\AppData\Local\Android\Sdk'
$env:ANDROID_USER_HOME = 'C:\Users\linus\.android'
$env:ANDROID_PREFS_ROOT = $null
.\gradlew.bat :plugins:aps:testAndroidHostTest :plugins:constraints:testAndroidHostTest :core:keys:testAndroidHostTest --no-parallel --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx2g -XX:+UseParallelGC -Xss4m' '-Pkotlin.daemon.jvmargs=-Xmx2g' --console=plain
```

`THO validate personal Boost` checks out the triggering commit of this fork and builds a debug APK with reports, without release secrets. Stock `Boost CI` explicitly checks out Tim's release tag: it does not build THO code. Do not use stock Boost CI as a personal build or overwrite existing branches with GitHub Sync Fork.

For Android Studio, open only the new Boost-THO project before using its MCP tools and do not run a second Gradle build alongside the CLI. This 16 GiB machine needs the conservative build limits above; source upstream defaults use substantially more memory.

## What becomes the new source of truth

After technical and personal device acceptance, develop only on the new personal line; the old checkout is historical evidence. Backup files stay on this local disk at Thomas' request. They are not part of the daily workflow and are not automatically deleted.
