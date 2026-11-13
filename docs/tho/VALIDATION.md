# THO V4 validation

Status: unmodified production baseline built and tested. Personal behavior migration pending.

## Recorded environment

- Windows 11, Git 2.55.0.windows.5.
- JAVA_HOME for baseline: Microsoft JDK 21.0.11; JVM target required by source: 21.
- Gradle wrapper 9.8.2 successfully downloaded and executed `--version`; launcher JVM 21.0.11.
- Baseline source requests compileSdk 37; local SDK initially contains platforms 34/35/36 and build-tools 33.0.1/34.0.0/35.0.0. Resolve required SDK components without editing source versions.
- Android Studio MCP currently selects old AAPS-3.4-BOOST. No device connected. CLI build does not depend on IDE project selection.
- `tasks --all` fails on an upstream Compose release-preview task whose unit tests are disabled. Targeted debug/host tasks work without modifying upstream source.
- Installed by Gradle using existing accepted licenses: Build Tools 36.0.0 and Android Platform 37.0 revision 2.
- Host has 16 GiB RAM. Upstream memory defaults exhausted local headroom; initial run was cancelled. The successful command below limits workers and heap without changing source.

## Successful unmodified production baseline

Commit `4cb138e07f` contains documentation only above upstream `c2d2c248dc`; production source is unchanged.

```powershell
.\gradlew.bat :app:assembleFullDebug :plugins:aps:testAndroidHostTest :plugins:constraints:testAndroidHostTest --no-parallel --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx3g -XX:+UseParallelGC -Xss4m' '-Pkotlin.daemon.jvm.options=-Xmx2g' --console=plain
```

- BUILD SUCCESSFUL in 7m 41s; 981 actionable tasks, 202 executed, 779 up-to-date (earlier cold compilation retained).
- APS: 112 XML suites, 1,130 tests, zero failures/errors/skipped.
- Constraints: 14 XML suites, 126 tests, zero failures/errors/skipped.
- Reports copied outside source into `_migration-backups/2026-11-13/baseline-test-results` before personal test runs.
- Baseline APK: `app-full-debug.apk`, 243,181,289 bytes; package `info.nightscout.androidaps`; version 4.0.0 (1500); minSdk 31, targetSdk 35.
- Embedded Boost version: `6.3.2+tho/main.4cb138e07f`.
- APK SHA-256: `0FD447CA97BF111FD784B200B25A8ED8828130B483650CAE1D55FAA2F190F10A`.
- Verified Android Debug signature, certificate SHA-256 `99b778c718c239d00bda6b15f8f8a94dcac002dea77b489fd33e0a8ac8817e86`.
- Export: BOOST/THO-V4/baseline-4.0.0-boost-6.3.2, including signature and checksum. This is an upstream comparison artifact, not a personal migrated build.
- Existing API34 emulator AVD discovered; not launched during memory-heavy build.

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
