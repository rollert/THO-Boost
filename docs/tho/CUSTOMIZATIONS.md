# THO customization register

Status: assessment in progress. None of the entries below is yet certified as ported or working in V4.

## Decision register

| ID | Intended behavior from old personal source | V4 assessment and next proof | Status |
|---|---|---|---|
| THO-01 | TDD blend W8H/7D/1D = 80/10/10; Boost pull-down only below 15% of 7D | Active Boost engine still uses 33/34/33 and 75%; V6 delegates to this engine. Verify actual calculation via production tests, including boundary discontinuity; standalone SMB has deliberately no pull-down. | Assess/port |
| THO-02 | DynISF guard: corrupt 7D below 35% of profile-implied TDD, or blended TDD below 15% | V4 helper still guards blended TDD at 35%. Need production helper tests plus fallback-path check, finite inputs and healthy-history rapid sensitivity change. The old handover warns about copied test formulas: do not reuse those as proof. | Assess/port |
| THO-03 | Daily auto-config from four days; minimum two valid TDD days and 700 BG samples; eight manual bolus samples | V4 defaults remain 14-day initial /28-day redrive/weekly cadence and ten manual samples. New eligibility/guard logic must remain intact. Verify window queries, sample fallback, boundaries and confirmations. | Assess/port |
| THO-04 | Explicit per-parameter Auto ownership: ON returns to auto next cycle; OFF freezes value | V4 has new ledger/Simple Mode behavior. Old switches cannot be copied without mapping preferences/UI and preserving manual intent across imports/restarts. | Assess/port |
| THO-05 | DST suspension reduced from three hours to one | V4 uses a new running-mode suspension API but still three hours. Evaluate exact one-hour behavior, both transition directions and snooze handling. | Assess/port |
| THO-06 | History backfill sufficient for personal windows, old value seven days | V4 now intentionally requests 30 days and has additional consumers and installation guards. Enumerate all consumers before reducing it; preserving a larger backfill may better meet V4 needs. | Decision pending |
| THO-07 | Build settings, resources and documentation supporting the above | V4 already contains parallel tooling setting; avoid redundant patch. Keep old historical patch inactive. Update personal docs instead of rewriting upstream README. | Assess |

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
