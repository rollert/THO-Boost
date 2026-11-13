# THO customization register

Status: production port and host-test verification complete; final APK/device checks are recorded separately in VALIDATION.md. This is a technical test candidate, not a claim of clinical validation.

## Decision register

| ID | Intended behavior from old personal source | V4 assessment and next proof | Status |
|---|---|---|---|
| THO-01 | TDD blend W8H/7D/1D = 80/10/10; Boost pull-down only below 15% of 7D | Shared production TddBlend is called by active V6 engine and standalone SMB. SMB deliberately has no pull-down. ThoTddPolicyTest covers stable/rapid-drop/boundary examples. Old hidden V2/V3-family implementations were removed by V4; no obsolete plugin restored. | Ported, tested |
| THO-02 | DynISF guard: corrupt 7D below 35% of profile-implied TDD, or blended TDD below 15% | Actual production helper tested, including nonfinite delivery and rapid drop with healthy week history. Engine keeps the profile fallback on rejection. | Ported, tested |
| THO-03 | Daily auto-config from four days; minimum two valid TDD days and 700 BG samples; eight manual bolus samples | Production derivation, actual query inputs and schedule use four days/daily. Positive NORMAL records count toward eight; SMBs remain separate and supply fallback. ThoAutoConfigWindowTest covers sample/schedule boundaries. | Ported, tested |
| THO-04 | Explicit per-parameter Auto ownership: ON returns to auto next cycle; OFF freezes value | V4-native preference switches reuse old registered storage prefix; EN/DE text. Tracked and computed cap freezes, state restart, schema rescue and queued reset are tested. Fresh derivation clears stale tracking. New V4 tranche threshold retains its own evidence policy. | Ported, tested |
| THO-05 | DST suspension reduced from three hours to one | Existing V4 running-mode API retained; spring/autumn elapsed-time boundaries 59/60/61 minutes tested. | Ported, tested |
| THO-06 | Detect insufficient history over the short window | Thomas explicitly retained four-day detection with two-day/700-reading floor. Boost's seven-day component already permits missing days. Keep V4's bounded 30-day fetch on an actual detected gap, preserving its newer consumers and retry/install restrictions; this does not require 30 days for derivation. | Adapted to V4, tested |
| THO-07 | Build settings, resources and documentation supporting the above | Parallel tooling setting already upstream; no duplicate. Legacy SMB test comparator aligned with80/10/10. Old historical patch remains inactive. Personal docs/local build profile added. GitHub CI removed per Thomas' local-only decision. | Adapted |
| THO-08 | Respond sooner to adverse windows while checking once daily | Existing-derived HypoCaution increases and Aggression decreases apply on first daily evaluation. Loosening still needs confirmation and TBR guard; step bounds and neutral ceiling remain. ThoDailyTighteningTest covers first change, reversal and guards. | Additional user-approved change, tested |
| THO-09 | Investigate smaller cap changes through replays | Actual production replay compared100%/75%/50% bands on synthetic daily4d profiles. More writes and reversals at50%; no clinical calibration at4d. Keep production bands for now. See CAP-REPLAY.md. | Investigation complete; bands unchanged |

## Old final intent and deferred ideas

Sources: old commits and private IDE handovers dated 2026-09-02/03, subsequently reasserted by `3044e5b8ba`.

- 80/10/10 and 15% are the final old settings; earlier 20% and 3/14 cadence commits are intermediate steps, not separate features.
- V6 reused the Boost V1 engine for sensitivity; plugin directory names do not determine which code is active.
- Old healthy-history reference: 7D = 40 U/day, profile ISF = 45 mg/dL/U; W8H = 8 must not be rejected solely because blend is below 35% of implied TDD.
- Keep SMB's deliberate absence of a pull-down unless a new decision is justified.
- Layer-2 ratio changes, adaptive blend weights, and MAX-ISF redesign were not approved completed old features. Inspect existing V4 clamps for compatibility; do not silently add deferred ideas.
- Overlapping four-day daily windows weaken statistical independence of repeated confirmation. Preserve and test current deadbands/raise guards; document the tradeoff without claiming clinical validation.

## Complete old net-diff file coverage

Paths below are relative to the old repository; old root prefixes shortened only in this table.

| Old file | IDs / treatment |
|---|---|
| `.gitignore` | THO-07: ignored test output artifact; inspect continued need |
| `README.md` | THO-01/02/07: historical explanation, replace with personal migration documentation |
| `app/src/androidTest/kotlin/app/aaps/plugins/aps/openAPSSMBDynamicISF/DetermineBasalAdapterSMBDynamicISFJS.kt` | THO-01: legacy test adapter; map against surviving V4 test path |
| `core/keys/src/main/kotlin/app/aaps/core/keys/BooleanComposedKey.kt` | THO-04: migrate to commonMain only if ownership implementation requires it |
| `docs/boost-v1-settings.md` | THO-01/02/07: retain intent in personal documentation |
| `docs/v6-advanced-settings.md` | THO-03/04/07: update V4 personal behavior documentation |
| `gradle.properties` | THO-07: parallel sync already in V4; no duplicate needed |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoost/OpenAPSBoostPlugin.kt` | THO-01/02: active engine mapping confirmed |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV2/OpenAPSBoostV2Plugin.kt` | THO-01: check whether V4 removes this hidden plugin; do not resurrect obsolete implementation |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV3/OpenAPSBoostV3Plugin.kt` | THO-01: same removed-plugin assessment |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV3ML/OpenAPSBoostV3MLPlugin.kt` | THO-01: same removed-plugin assessment |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV3MLG3/OpenAPSBoostV3MLG3Plugin.kt` | THO-01: same removed-plugin assessment |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV5/BoostHistorySync.kt` | THO-06: V4 consumer audit required |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV5/BoostV5AutoConfig.kt` | THO-03 |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV5/BoostV5AutoConfigApply.kt` | THO-04 |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSBoostV5/OpenAPSBoostV5Plugin.kt` | THO-03/04 and active V6 integration |
| `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSSMB/OpenAPSSMBPlugin.kt` | THO-01: map surviving SMB implementation |
| `plugins/aps/src/main/res/values/strings.xml` | THO-04: map V4 resources/UI |
| `plugins/aps/src/test/kotlin/app/aaps/plugins/aps/openAPSBoost/DynIsfTddGuardTest.kt` | THO-02: test production helper, not copied formula |
| `plugins/aps/src/test/kotlin/app/aaps/plugins/aps/openAPSBoostV3/BoostV3IsfCalculationTest.kt` | THO-01: replace obsolete/copy tests with active production tests |
| `plugins/aps/src/test/kotlin/app/aaps/plugins/aps/openAPSBoostV5/BoostHistorySyncTest.kt` | THO-06 |
| `plugins/aps/src/test/kotlin/app/aaps/plugins/aps/openAPSBoostV5/BoostV5AutoConfigOwnershipTest.kt` | THO-04: behavior requirements, adapt to V4 ledger |
| `plugins/aps/src/test/kotlin/app/aaps/plugins/aps/openAPSBoostV5/BoostV5AutoConfigTest.kt` | THO-03 |
| `plugins/aps/src/test/kotlin/app/aaps/plugins/aps/openAPSBoostV5/BoostV5RedriveTest.kt` | THO-03/04 |
| `plugins/constraints/src/main/kotlin/app/aaps/plugins/constraints/dstHelper/DstHelperPlugin.kt` | THO-05 |
| `plugins/constraints/src/test/kotlin/app/aaps/plugins/constraints/dstHelper/DstHelperPluginTest.kt` | THO-05 |

Additional local beta branches preserve commits `1167077a36` and `c8d3caf964` (TDD/DST) and README changes. Reconcile them before declaring complete; their branch history is retained in the all-refs bundle.

Reconciliation completed: these beta commits have identical stable patch ID `e19a0882be91d2bd58b8b14c3edc5ad8a516d6b6`; their final intended behavior is covered by THO-01 and THO-05. The table above retains every old net-diff file; path removal in V4 is not an unrecorded loss of an active feature.

## Independent review fixes

The four-day setting also fed the old live fourteen-day hypo gate. The live persistence query now uses an independent fourteen-day window; a test includes lows on day7 and proves they still block the relevant feature. Explicit manual-at-factory flags survive initial schema migration. Redrive reads eligibility and ledgers under the same lock as ownership reset/application; deterministic queued-transaction tests reproduce both stale-state cases. These are integration fixes, not new dosing thresholds.
