# THO: AAPS 3.x → Boost on AAPS 4.0

Status: personal V4 production changes ported and host-tested; final APK/runtime acceptance is tracked in VALIDATION.md. No therapy-device installation or release signing performed.

## Fixed references

- Old personal source: `3044e5b8ba9e27edf3cbcf53e75084d1cf66015a`.
- Old upstream merge-base: `063e8cec7f8ebb275de81fc7a8fe60fcaa7daabb`.
- Old GitHub dev: `dd60d101a5a5bbbc17e91d6f7f6009ed6109de88`; eleven additional local commits are included in the archive.
- Remote archive: `rollert/Boost-in-AAPS_3.4`, branch `archive/tho-aaps3-2026-11-13`, verified against old personal SHA.
- V4 baseline: `4.0.0-boost-6.3.2`, commit `c2d2c248dc90ca0c80a2b99e42a052a6b7a45593`.
- Personal line: `tho/main`; origin = Thomas' existing fork; upstream = `tim2000s/Boost`.

## Backup evidence

Local backup directory: sibling `_migration-backups/2026-11-13` of the new clone.

- `aaps3-all-refs.bundle`: `git bundle verify` reports complete history.
- Bundle SHA-256: `F33720EF5FA63031A4D2A23100DDB1C2B13908B41A52464659B09182B45FF0EE`.
- Mirror restoration `restore-check.git`: archive SHA matches; both repositories contain 24,704 reachable commits across all refs.
- All old local branches preserved, including Boost-ML-Beta and Boost-ML-Beta-2. Their personal TDD/DST commits require reconciliation with dev, not automatic replay.
- Local configuration backed up privately, outside this repository. No personal signing key identified in old repository scan (only a demo keystore).
- IDE handover documents copied to local backup. They provide historical intent, not proof of current correctness.
- Thomas explicitly chose to keep backup files on this local disk only. No off-drive backup is requested. Validated V4 will become the productive source of truth; old backup deletion is not automatic.

## Decisions

- Retain the existing fork and historical folders. A repository rename is optional after migration.
- Use a separate clone rather than a linked worktree, as agreed, to keep old and new sources independent.
- No force push, no replacement of existing master/dev, no release signing or therapy-phone installation during source migration.
- Stock Boost CI explicitly checks out `tim2000s/Boost` at a release tag. It does **not** build the personal branch. Do not use it as proof of a THO build.
- Start from reproducible unmodified V4; record baseline environment problems separately before porting.
- Keep four-day history-gap detection as Thomas explicitly confirmed. The live hypo gate remains independently fourteen days; its query and the ownership integration fixes have regression tests.
- Additional approved behavior: existing-derived tightening applies on the first daily window, with guarded slower reversal. Investigation of smaller cap bands is complete in CAP-REPLAY.md; production cap bands remain unchanged.
- Keep product changes focused to limit future merge work. Existing notification formatting issues (dimensionless values labeled U and operative value shown as previous value) remain separate reporting limitations.
- Thomas' final tooling decision: exclusively local tests/builds, real Android/Bluetooth hardware for runtime acceptance, no emulator. The added GitHub CI was disabled/removed and its unfinished run cancelled. Emulator software and all active system/AVD/test images were removed via Windows recycle bin; build SDK components remain.

## Execution records

Detailed execution ledger and plan live in workspace `_codex/plans/2026-11-13-boost-v4-{migration,progress}.md`.
Customization decisions: [CUSTOMIZATIONS.md](CUSTOMIZATIONS.md). Verification evidence: [VALIDATION.md](VALIDATION.md). Daily operation: [WORKFLOW.md](WORKFLOW.md).
