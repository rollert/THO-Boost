# Personal Boost workflow

Status 2026-11-14: signed personal V4 reference, with successful initial operation reported by Thomas. Specific remaining device observations are listed in VALIDATION.md. Builds/tests stay local.

## Repository roles

- `origin`: `https://github.com/rollert/Boost-THO.git` (same fork, renamed from `Boost-in-AAPS_3.4`).
- `upstream`: `tim2000s/Boost`.
- `tho/main`: personal integrated line and GitHub default branch.
- `tho-v4-6.3.2-signed1`: fixed source reference for Thomas' signed APK; `tho-v4-6.3.2-rc1` remains the earlier debug reference.
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

This helper produces an Android-Debug-signed test APK. For regular updates, Thomas builds/signs locally in Android Studio using his existing key, as for the signed1 reference. Archive the signed APK with its source commit, embedded build version, SHA256 and public certificate fingerprint; never store the keystore or password in Git/artifact folders. Do not replace the working signing key or uninstall the current app to bypass a signature mismatch.

For tests from the Boost-THO root:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot'
$env:ANDROID_HOME = 'C:\Users\linus\AppData\Local\Android\Sdk'
$env:ANDROID_USER_HOME = 'C:\Users\linus\.android'
$env:ANDROID_PREFS_ROOT = $null
.\gradlew.bat :plugins:aps:testAndroidHostTest :plugins:constraints:testAndroidHostTest :core:keys:testAndroidHostTest --no-parallel --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx2g -XX:+UseParallelGC -Xss4m' '-Pkotlin.daemon.jvmargs=-Xmx2g' --console=plain
```

Thomas chose exclusively local tests/builds. The added `THO validate personal Boost` workflow was disabled and removed; its first run was cancelled, not counted as passed. All inherited workflows are also disabled. GitHub stores source and history only. Do not enable/trigger GitHub builds or overwrite personal branches with GitHub Sync Fork.

For Android Studio, open only the new Boost-THO project before using its MCP tools and do not run a second Gradle build alongside the CLI. This 16 GiB machine needs the conservative build limits above; source upstream defaults use substantially more memory.

Android Studio is now2026.2.2, verified from local product metadata on2026-11-14. The old2026.1.4/AGP9.5 version mismatch is resolved by the IDE update; Thomas has built the signed release. Google's [compatibility table](https://developer.android.com/build/releases/about-agp) lists2026.2.2 as supporting AGP9.5. No project dependency downgrade was applied; an additional IDE-sync check was not performed at closeout.

## What becomes the new source of truth

Continue development on the new personal line. The old checkout is historical evidence. Keep the signed1 reference unchanged and record later accepted builds separately. Backup files stay on this local disk at Thomas' request; they are not part of the daily workflow and are not automatically deleted. Initial operation does not substitute for the specific AutoConfig/persistence observations still open in VALIDATION.md.
