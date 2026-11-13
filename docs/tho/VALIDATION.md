# THO V4 validation

Status: baseline environment discovery. No personal V4 behavior changes or APK claimed verified.

## Recorded environment

- Windows 11, Git 2.55.0.windows.5.
- JAVA_HOME for baseline: Microsoft JDK 21.0.11; JVM target required by source: 21.
- Gradle wrapper 9.8.2 successfully downloaded and executed `--version`; launcher JVM 21.0.11.
- Baseline source requests compileSdk 37; local SDK initially contains platforms 34/35/36 and build-tools 33.0.1/34.0.0/35.0.0. Resolve required SDK components without editing source versions.
- Android Studio MCP currently selects old AAPS-3.4-BOOST. No device connected. CLI build does not depend on IDE project selection.
- Baseline `tasks --all --console=plain` running; task names and build feasibility not established yet.

## Baseline shell environment

```powershell
$env:JAVA_HOME = 'C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot'
$env:ANDROID_HOME = 'C:\Users\linus\AppData\Local\Android\Sdk'
$env:ANDROID_USER_HOME = 'C:\Users\linus\.android'
$env:ANDROID_PREFS_ROOT = $null
```

The last assignment removes a known environment conflict only for the process, without changing system settings.
Logs reside outside source under sibling `_migration-backups/2026-11-13/logs`.

## Required evidence before candidate status

- Unmodified V4 build and actual nonzero test report counts.
- Per-customization RED → GREEN behavior tests, or existing-upstream behavior proof.
- Relevant combined suites, no disabled tests hiding failures.
- APK package/version/signature/commit/checksum identity.
- Isolated startup, settings, persistence and migration checks on test device/emulator.
- Explicitly list incomplete signing, hardware, data and clinical-use assumptions.
