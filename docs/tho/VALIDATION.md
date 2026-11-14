# THO V4 validation

Status 2026-11-14: production port host-tested; Thomas built/signed the release APK and reports successful initial operation. Signature and exact APK source identity were independently inspected locally. Specific observations still open are listed at the end; no blanket clinical validation is claimed.

## Recorded environment

- Windows 11, Git 2.55.0.windows.5.
- JAVA_HOME for baseline: Microsoft JDK 21.0.11; JVM target required by source: 21.
- Gradle wrapper 9.8.2 successfully downloaded and executed `--version`; launcher JVM 21.0.11.
- Baseline source requests compileSdk 37; local SDK initially contains platforms 34/35/36 and build-tools 33.0.1/34.0.0/35.0.0. Resolve required SDK components without editing source versions.
- At baseline, Android Studio MCP selected old AAPS-3.4-BOOST and no device was connected. It was later switched to Boost-THO; current closeout evidence is below. CLI build does not depend on IDE project selection.
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
- Startup, settings, persistence and migration checks on real Android/Bluetooth hardware; emulator testing is explicitly excluded by Thomas.
- Explicitly list incomplete signing, hardware, data and clinical-use assumptions.

## Personal port and independent review

- TDD tests: seven new production-path tests ran before behavior changes, six failed as expected. After implementation, full APS suite 1,137/1,137 passed.
- Daily short-window tests: initial four expected failures; final four-day sufficiency policy confirmed by Thomas and real source `allowMissingDays=true`. Combined APS 1,143/1,143 passed.
- Ownership tests: three expected failures out of four; after V4-native implementation and persisted-state tests, APS 1,149/1,149 passed.
- DST: spring and autumn tests failed at the expected one-hour boundary before the change; full Constraints 128/128 passed afterward.
- Fresh independent read-only review found three Important issues: live14-day safety query coupled to4-day derivation; explicit manual factory-value lost in schema rescue; stale tracking snapshot across ownership reset.
- Four regression assertions reproduced the issues before fixes. After separating live history and making migration/eligibility/snapshots atomic, APS 1,153, Constraints 128 and core:keys 25 had zero failures/errors/skips. Keys' report came from an actually executed candidate-build run; later unchanged task was up-to-date.
- Legacy removed plugin paths were reconciled through the active V6/SMB helpers; no obsolete plugin restored. Both beta personal patches matched the same stable patch ID.

## Additional once-daily response behavior

Thomas explicitly requested more actual automatic adjustments while retaining daily evaluation, and approved faster conservative changes plus cap replay investigation.

- New tests first showed two expected failures: derived tightening waited for confirmation.
- HypoCaution increases and Aggression decreases now apply on first daily evaluation. Step bounds, explicit ownership, existing derivation thresholds, neutral ceiling, and conditions for insulin-adding moves remain.
- Better next-day conditions do not immediately reverse the tightening. Repeated loosening is still held if the TBR guard is tripped.
- Full APS suite 1,157/1,157 passed after updating the two legacy symmetric-confirmation expectations to the new explicit asymmetric policy.
- Separate engineering replay: one executed test, zero failures/errors, 108 result rows. See CAP-REPLAY.md. Experimental mutable-band harness was moved out of the repository before normal tests/builds; production cap bands were restored and never changed in source.

## Final package and runtime

- Source build/tag: `017c446801f94e8a14e646d09c638a5aaee45519` / `tho-v4-6.3.2-rc1`, clean working tree during final build/test. Final combined reports: APS1,157 +Constraints128 +keys25 =1,310, zero failures/errors/skips. FullDebug helper build succeeded in33s; all five process environment values restored. Subsequent documentation commits do not change the APK's source identity.
- APK: `AAPS-BOOST_Boost-THO_full_debug_2026-11-13_017c446801.apk`; package `info.nightscout.androidaps`, version4.0.0/code1500, minSDK31/target35.
- SHA256: `A57FC5DD4FE43071AB5E9B607C54002D02FCCFBE9EDADA3212BA2541FB0081C8`.
- apksigner verification succeeded. Android Debug certificate SHA256: `99b778c718c239d00bda6b15f8f8a94dcac002dea77b489fd33e0a8ac8817e86`.
- Local export under `BOOST/THO-V4/tho-v4-6.3.2-rc1` contains APK, identity manifest and all three XML report sets. Backup logs stay outside source.
- The unfinished [GitHub validation run43513774888](https://github.com/rollert/THO-Boost/actions/runs/43513774888) was cancelled when Thomas chose exclusively local builds. All GitHub workflows are now disabled. No GitHub success is claimed; the 1,310-test and APK evidence above is local.
- Studio was switched to Boost-THO. The earlier2026.1.4/AGP9.5 mismatch was subsequently superseded by installation of Studio2026.2.2, verified2026-11-14. Thomas built the signed release; no extra IDE-sync test was run at closeout.
- Emulator checks were explicitly rejected by Thomas and stopped; emulator software/images were removed at his request. They provide no device acceptance evidence. Further runtime checks use real Android/Bluetooth hardware.

## Signed reference and initial operation — 2026-11-14

- Reference tag: `tho-v4-6.3.2-signed1`, anchored at actual source commit `9ff067f787ad6f663cbf6c027cdc79790147f72e`.
- Original user-built APK: `app/full/release/app-full-release.apk`,153,660,107 bytes. Preserved unchanged; no signing operation was performed by Codex.
- Embedded DEX strings independently confirm build ID `tho-v4-6.3.2-rc1-4-g9ff067f-2026.11.14` (also matches generated Release BuildConfig). The new source-reference tag was created after this build, so the embedded older tag description is retained.
- SHA256: `F56D19BD768F6A7C5BF4C7AB05A9309B7F4652F1EECDEF1C9120CBC788A43E65`.
- apksigner verify exit0; certificate SHA256: `1e9cb0646f74ec12c5ac3ff95a2e3c91b6c145b123b66d712ee0680aa413272b`.
- Package `info.nightscout.androidaps`, version4.0.0/code1500, minSDK31/target35.
- Local export: `BOOST/THO-V4/tho-v4-6.3.2-signed1`, containing signed APK, manifest, SHA256SUMS, acceptance note and local host-test XML reports. Copied APK SHA matches original.
- Product/test sources at9ff067f787 are unchanged from the1,310-test candidate017c446801; differences are documentation, working rules and removal of CI. No redundant test rerun or release rebuild was performed at closeout.
- Thomas reports: built with his key, tested, running well so far. This closes user-reported successful signed build and initial app operation. It does not independently establish which hardware scenarios were exercised or prove compatibility with an older signing key.
- The IDE-referenced keystore has a separate private local backup, with matching file hash and copied access controls. Original untouched; no password read/stored and no new signing performed. This file backup does not claim an additional keystore unlock or old/new certificate comparison.

Remaining targeted observations (not extra code changes):

1. Actual once-daily AutoConfig evaluation, eligible changes and corresponding notifications; no requirement for a parameter change every day.
2. Manual ownership and settings retained after a normal app/device restart.
3. Personal settings import if used, and specific Bluetooth/pump behavior beyond the reported basic operation.

Record these from actual operation without changing therapy parameters merely to provoke a test. Broader cap-sensitivity changes remain a separate decision; CAP-REPLAY.md records why production bands remain unchanged.
