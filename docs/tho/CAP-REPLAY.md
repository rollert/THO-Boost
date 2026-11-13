# Smaller cap movements: bounded engineering study

Thomas requested more responsive actual automatic adjustments, with evaluation still once daily. Relevant Auto switches were enabled in his old app. This study concerns cap deadbands; the separately tested first-window tightening rule is THO-08.

## Method

Executed direct calls to the actual Kotlin `BoostV5AutoConfig.compute` and `BoostV5AutoConfigApply.redrive` from commit `b7cbb3fbd6`. Nine deterministic synthetic 64-day scenarios, each 60 daily evaluations with rolling four-day profiles: stable, slow rise/fall, alternating block noise, step rise/fall, two guarded-rise scenarios and mixed sinusoidal noise. Manual samples and CGM counts satisfy production eligibility; missing-CGM rejection is separately checked.

Only cap deadbands vary in an isolated test worker, restored afterward. The primer band and all production guards remain unchanged. The experimental fixture is kept outside the app source in the local migration backup; it is not shipped or included in normal tests.

| Band multiplier | Committed/confirmed band U | Per-shot cap writes | Direction reversals | All cap writes including derived caps |
|---|---|---:|---:|---:|
| 100% |0.07 /0.47|20|0|49|
| 75% |0.0525 /0.3525|24|0|55|
| 50% |0.035 /0.235|61|13|130|

All tested variants had zero measured step-bound or TBR-guard violations. Stable and sinusoidal scenarios produced no cap writes. The 75% variant first responded to slow drift 2–6 days sooner. Halving produced 27 committed-cap writes with 13 reversals in the alternating-driver scenario, plus corresponding cumulative-cap reversals.

## Interpretation

The tested75% candidate improves sensitivity to some slow synthetic changes without additional reversals in this small scenario set. Halving is a poor blanket candidate because of the observed oscillation. This does not establish a four-day physiological noise calibration or better glucose outcomes.

The historical eight-user study in old Git commit `b08a10836f`, `backtesting/scripts/2026-08-autoconfig-redrive`, calibrated14–28day windows, not4days. Its raw local database is currently unreachable; existing coarse trajectory exports cannot reconstruct valid daily four-day patient windows. No real patient replay was claimed.

Production bands remain unchanged. The 75% candidate is retained for a later comparison with suitable authorized raw data. A fixed-glucose retrospective replay still cannot establish a counterfactual treatment benefit, because changed insulin would change subsequent glucose and dose distributions.

## Why glucose plateaus may produce no cap change

The cap formulas follow delivered SMB distributions, manual NORMAL boluses and TDD; mean glucose is collected but does not directly drive them. With fewer than eight normal boluses, the manualp90 term is unavailable. The SMB fallback and its floor may remain unchanged despite a plateau. Lowering a write deadband cannot create a new target when the derivation itself stays constant.

## Local evidence

`AAPS-BOOST/_migration-backups/2026-11-13/cap-replay-study/` holds `README.md`, `actual-production-results.csv` (108 rows), `actual-production-test-report.xml` (one executed test, zero failures/errors) and `executed-fixture.kt`. These contain synthetic inputs/results, not a personal glucose export.
