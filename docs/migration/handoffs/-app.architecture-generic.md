# :app architecture migration handoff (generic MVI/UDF)

## 1. Target module and scope
- **Module:** `:app`
- **Date:** `2026-08-27`
- **Baseline commit SHA:** `9982bca42d656f2f4d6d7d04ff626f20992c56ee`
- **In scope:** all current app screens and ViewModels (`Intent`, `CustomIntent`, `History`, `Presets`, `IntentDetails`, `ResponseEvents`)
- **Out of scope:** Compose migration (already Compose), navigation framework replacement, redesign beyond parity
- **Compatibility constraint (confirmed):** keep navigation contracts compatible; internal module APIs can change

### Files inspected
- `app/build.gradle.kts`
- `settings.gradle.kts`
- `app/src/main/java/com/simprints/intentlauncher/ui/Screen.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/root/RootScreen.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/root/RootActivity.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/intent/IntentViewModel.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/intent/IntentViewState.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/intent/IntentScreen.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/custom/CustomIntentViewModel.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/custom/CustomIntentViewState.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/custom/CustomIntentScreen.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/history/HistoryViewModel.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/history/HistoryScreen.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/presets/PresetsViewModel.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/presets/PresetsScreen.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/details/IntentDetailsViewModel.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/details/IntentDetailsScreen.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/events/ResponseEventsScreenViewModel.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/events/ResponseEventsViewState.kt`
- `app/src/main/java/com/simprints/intentlauncher/ui/events/ResponseEventsScreen.kt`
- `app/src/main/java/com/simprints/intentlauncher/data/store/ProjectDataCache.kt`
- `app/src/main/java/com/simprints/intentlauncher/domain/IntentCallRepository.kt`
- `app/src/main/java/com/simprints/intentlauncher/domain/IntentPresetRepository.kt`
- `app/src/main/java/com/simprints/intentlauncher/domain/IntentResultParser.kt`
- `app/src/main/java/com/simprints/intentlauncher/domain/IntentCall.kt`
- `app/src/main/java/com/simprints/intentlauncher/domain/IntentResult.kt`
- `app/src/main/java/com/simprints/intentlauncher/di/AppModule.kt`
- `app/src/main/java/com/simprints/intentlauncher/tools/CollectLifecycleAwareExt.kt`

## 2. Current-state inventory
- **UI framework:** Jetpack Compose (no Fragments/XML in active UI paths).
- **ViewModel style:** mixed; mostly direct mutable methods (`updateX`, `save`, `delete`) with ad hoc coroutine launches.
- **State transport:**
  - `IntentViewModel` and `CustomIntentViewModel`: `SavedStateHandle.getStateFlow(KEY_VIEW_STATE, ParcelableState)` + `de.palm.composestateevents`.
  - Other ViewModels: `MutableStateFlow` + direct exposure.
- **One-off events:** `StateEventWithContent` + `EventEffect`, plus imperative UI side effects inside composables.
- **Navigation:** route string contract in `Screen` sealed class.
- **Domain/data:** repositories expose suspend + Flow; mapping logic and parsing logic present but untested.
- **Tests:** no `app/src/test` or `app/src/androidTest` tests currently present.

## 3. Main architecture issues
1. No consistent MVI/UDF contract across screens (state, actions, effects are not explicit).
2. Event wrappers and mutable Compose state (`MutableState<Boolean>`) are embedded in state objects, including parcelized state.
3. Significant imperative screen logic remains in composables (launching intents, catching launch errors, clipboard/toast orchestration, window soft input handling coupling).
4. `ResponseEventsScreenViewModel` performs lazy load side effects inside `combine` transformation, reducing predictability and testability.
5. Process-death state is implicit and coarse (`KEY_VIEW_STATE` parcelable blobs) rather than explicit persisted fields.
6. No ViewModel/domain unit test safety net for behavior parity.

## 4. Per-screen target MVI/UDF contract

Use the standard shape (`UiState`, `UiAction`, `UiEffect`) per screen; all events are routed through `onIntent(action)`.

### 4.1 Intent screen (`IntentViewModel` / `IntentScreen`)
- **Target UiState**
  - input fields: `projectId`, `userId`, `moduleId`, `metadata`, `guid`, `sessionId`
  - result fields: `resultText`, `responseJson`, `responseIntentId`, `events`
  - UI flags: `isLoading`, `isWrongMetadataDialogVisible`
  - transient processing marker for launched call (`pendingIntentCallId` or equivalent internal field)
- **Target UiAction**
  - `InitialLoad`
  - `ProjectIdChanged`, `UserIdChanged`, `ModuleIdChanged`, `MetadataChanged`, `GuidChanged`, `SessionIdChanged`
  - `EnrollClicked`, `IdentifyClicked`, `VerifyClicked`, `ConfirmClicked`, `EnrolLastClicked`
  - `IntentResultReceived(response: SimprintsResponse?)`
  - `ClearFieldsClicked`
  - `WrongMetadataDialogDismissed`
  - `OpenResponseEventsClicked(intentId: String)` (optional: can remain direct UI navigation)
- **Target UiEffect**
  - `LaunchSimprintsIntent(request: SimprintsRequest)` (replaces `showIntent` wrapper)
  - `ShowWrongMetadata(message)` (or use state-only dialog flag consistently)
  - `NavigateToCustomIntent` / `NavigateToResponseEvents(intentId)` if navigation is moved into ViewModel

### 4.2 Custom Intent screen (`CustomIntentViewModel` / `CustomIntentScreen`)
- **Target UiState**
  - `action`, `extras`, `resultText`, `isLoading`
  - optional parsed preview or validation state for extras lines
- **Target UiAction**
  - `InitialLoad`
  - `ActionChanged`, `ExtrasChanged`
  - `LaunchClicked`
  - `IntentResultReceived(code: Int, data: Intent?)`
  - `ClearFieldsClicked`
- **Target UiEffect**
  - `LaunchCustomIntent(intent: Intent)`
  - `ShowError(message)` for invalid launch/action-app-not-found mapping if desired as one-off

### 4.3 History screen (`HistoryViewModel` / `HistoryScreen`)
- **Target UiState**
  - `items: List<IntentCall>`
  - `isLoading`, `errorMessage`
- **Target UiAction**
  - `InitialLoad`
  - `HistoryItemClicked(intentId: String)` (optional if VM handles navigation effects)
- **Target UiEffect**
  - `NavigateToIntentDetails(intentId: String)` when click handling is VM-driven

### 4.4 Presets screen (`PresetsViewModel` / `PresetsScreen`)
- **Target UiState**
  - `items: List<Preset>`
  - `isLoading`, `errorMessage`
  - `pendingDeletePresetId` / dialog visibility by item id
- **Target UiAction**
  - `InitialLoad`
  - `UsePresetClicked(preset: Preset)`
  - `DeletePresetClicked(presetId: String)`
  - `DeleteConfirmed(presetId: String)`
  - `DeleteDismissed`
- **Target UiEffect**
  - `NavigateToIntent`
  - `ShowToast(message)` (or state-driven snackbar/text)

### 4.5 Intent Details screen (`IntentDetailsViewModel` / `IntentDetailsScreen`)
- **Target UiState**
  - `intentId`, `intentCall`, `isLoading`, `errorMessage`
  - `isSavePresetDialogVisible`
- **Target UiAction**
  - `InitialLoad(intentId: String)`
  - `CopyFieldsClicked`
  - `SavePresetClicked(name: String)`
  - `DeleteIntentClicked`
  - `SavePresetDialogShown`, `SavePresetDialogDismissed`
- **Target UiEffect**
  - `NavigateUp`
  - `NavigateToIntent`
  - `ShowToast("Preset saved")`

### 4.6 Response Events screen (`ResponseEventsScreenViewModel` / `ResponseEventsScreen`)
- **Target UiState**
  - `events`, `totalEvents`, `query`, `sorting`
  - `isLoading`, `errorMessage`
  - `activeDialog: EventDialogType?` (move current composable-local orchestration into state)
- **Target UiAction**
  - `InitialLoad(intentId: String)`
  - `SearchQueryChanged(query: String)`
  - `SortingChanged(option: EventSortingOption)`
  - `EventOptionsRequested(event: ResponseEvent)`
  - `DialogDismissed`
  - `CopyEventIdClicked`, `CopyEventJsonClicked`, `CopyPayloadJsonClicked`
- **Target UiEffect**
  - `CopyToClipboard(label: String, value: String)`
  - `ShowToast(message)`
  - `ApplySoftInputMode(mode)` only if keeping VM-driven; otherwise keep this UI-owned and documented

## 5. Behavior parity checklist
- Navigation route contracts remain unchanged:
  - `intent`, `history`, `presets`, `history/{intentId}`, `custom`, `responseEvents/{intentId}`
  - `Screen.ResponseEventDetails.createRoute("") -> "responseEvents/null"` behavior preserved.
- Intent launch/result flows remain unchanged for Simprints and custom intents.
- Result parsing, sessionId propagation, and history persistence remain unchanged.
- Preset save/use/delete behavior and copy-fields-to-store behavior remain unchanged.
- Empty-state rendering remains unchanged on History and Presets.
- Response events filtering/sorting semantics remain unchanged.
- Assisted injection path for `ResponseEventsScreenViewModel` keeps `intentId` + `initialSoftInputMode` inputs.
- Process-death behavior is preserved or improved:
  - current persisted fields from `SavedStateHandle` in Intent/Custom flows retained.
  - no loss of editable fields across recreation.

## 6. Compatibility constraints
- **Must stay compatible**
  - Navigation route strings and nav arguments in `Screen.kt`.
  - Data passed between screens via route args (`intentId`).
  - External contract invocations: `SimprintsContract`, `GenericIntentContract`.
- **Can change**
  - Internal ViewModel APIs (`updateX`, `clearFields`, etc.) can be replaced with `onIntent`.
  - Internal state model types and event wrappers (`de.palm.composestateevents`) can be removed.
- **Process-death dependencies to preserve**
  - `IntentViewModel.KEY_VIEW_STATE` field persistence intent.
  - `CustomIntentViewModel.KEY_VIEW_STATE` field persistence intent.
  - Migrate to explicit `SavedStateHandle` primitive keys per field where possible for safer serialization.

## 7. Test coverage delta plan (concrete file-level additions)

### 7.1 ViewModel tests
1. `app/src/test/java/com/simprints/intentlauncher/ui/intent/IntentViewModelTest.kt`
   - action -> state updates for each field action
   - `InitialLoad` hydrates cached fields
   - metadata parse failure path sets dialog state / error effect
   - launch actions emit `LaunchSimprintsIntent` effect
   - `IntentResultReceived` persists call and updates result/session/events
   - `ClearFieldsClicked` clears cache and resets state
2. `app/src/test/java/com/simprints/intentlauncher/ui/custom/CustomIntentViewModelTest.kt`
   - `InitialLoad` restores custom action/extras
   - `LaunchClicked` builds intent extras from lines and emits launch effect
   - invalid/app-not-found result mapping updates state and skips/keeps persistence as required
   - clear action resets cache/state
3. `app/src/test/java/com/simprints/intentlauncher/ui/history/HistoryViewModelTest.kt`
   - initial collection emits repository list
   - empty list and non-empty list state transitions
4. `app/src/test/java/com/simprints/intentlauncher/ui/presets/PresetsViewModelTest.kt`
   - initial preset flow collection
   - use preset writes expected cache values and emits navigation effect
   - delete preset action calls repository and emits toast/state mutation
5. `app/src/test/java/com/simprints/intentlauncher/ui/details/IntentDetailsViewModelTest.kt`
   - initial load by `intentId`
   - copy fields action writes store and emits navigate intent effect
   - save preset action and success effect
   - delete action calls repository and emits navigate-up effect
6. `app/src/test/java/com/simprints/intentlauncher/ui/events/ResponseEventsScreenViewModelTest.kt`
   - `InitialLoad` loads and parses events once
   - query filtering behavior
   - sorting option behavior
   - event option and dialog state transitions
   - copy actions emit clipboard/toast effects

### 7.2 Domain tests
1. `app/src/test/java/com/simprints/intentlauncher/domain/IntentResultParserTest.kt`
   - parse from `resultCode + extras`
   - parse from `SimprintsResponse`
2. `app/src/test/java/com/simprints/intentlauncher/domain/IntentCallRepositoryTest.kt`
   - save/get round trip mapping for fields, extras, result
   - list ordering passthrough expectations with DAO fake
   - delete call path
3. `app/src/test/java/com/simprints/intentlauncher/domain/IntentPresetRepositoryTest.kt`
   - save maps fields correctly
   - flow mapping from entity to domain
   - delete path

## 8. Ordered execution plan with stop conditions (rollback-friendly)
1. **Introduce base MVI scaffold**
   - Add module-local `BaseMviViewModel<S, A, E>` aligned with target contract.
   - Stop condition: compiles without screen migration.
2. **Migrate `IntentViewModel` + `IntentScreen`**
   - Replace event wrappers with effect Flow collection in Compose (`LaunchedEffect` + lifecycle-aware collection).
   - Preserve state restoration semantics from current `SavedStateHandle`.
   - Stop condition: Simprints launch and result handling parity retained.
3. **Migrate `CustomIntentViewModel` + `CustomIntentScreen`**
   - Convert to action/effect contract; remove wrapper events.
   - Preserve custom intent cache/load behavior.
   - Stop condition: launch + result + clear parity retained.
4. **Migrate read/list screens (`History`, `Presets`, `IntentDetails`)**
   - Standardize state shape and intent handling.
   - Move remaining imperative business decisions into ViewModels.
   - Stop condition: list, save/delete/use/copy behaviors match baseline.
5. **Migrate `ResponseEventsScreenViewModel` + `ResponseEventsScreen`**
   - Move load out of `combine` side effects into explicit `InitialLoad`.
   - Normalize dialog/search/sort/copy flows through actions/effects/state.
   - Stop condition: event list count/filter/sort/copy UX parity retained.
6. **Add/expand tests incrementally per migrated screen**
   - Fail fast if action/state/effect contracts are not deterministic in tests.
7. **Remove legacy event wrapper dependency usage (`de.palm.composestateevents`)**
   - Only after all dependent screens are migrated.
   - Stop condition: no references remain in `ui/**`.

## 9. Risks and rollback steps

### Risks
- One-off effect handling regressions (double launch or dropped launch).
- Process recreation regressions if saved state persistence is not explicitly ported.
- Navigation side effects moved to VM may cause duplicate navigation if collection is not single-delivery.
- `ResponseEvents` keyboard/soft input behavior can regress if ownership between UI and VM is unclear.
- Existing behavior quirk in `ProjectDataCache.clearCustomIntent()` clearing full preference store may surface during refactor; treat carefully to avoid unintended behavior change unless explicitly fixed.

### Rollback steps
1. Keep migration in small commits per screen.
2. If a screen regresses, revert only that screen migration commit(s), keeping base scaffolding and unaffected screens.
3. If effect delivery regresses globally, temporarily re-enable previous event wrapper for affected screens while preserving new UiState contract.
4. Restore previous ViewModel public methods only as a temporary compatibility shim inside module, then remove once tests pass.

## 10. Validation commands
- Baseline/full checks:
  - `./gradlew --no-daemon test lintDebug`
- App-only unit checks (post test additions):
  - `./gradlew --no-daemon :app:testDebugUnitTest`
- If lint gating blocks due to pre-existing issues, track baseline separately and ensure migration introduces no new lint errors in touched files.

Current baseline note: `lintDebug` already fails before migration due to `NonObservableLocale` in `AccordionLayout.kt`.

## 11. Sign-off
- **Manual review status:** `APPROVED`
- **Reviewer notes / edits:** user-approved in CLI thread on 2026-08-27.

## 12. Execution progress

### Screen 1/6 - Intent screen (`IntentViewModel` + `IntentScreen`)
- **Status:** Completed and ready for review pause.
- **Implemented**
  - Introduced `IntentUiAction` / `IntentUiEffect` contract (`IntentContract.kt`) and single `onIntent(...)` entrypoint.
  - Reworked `IntentViewModel` to immutable `StateFlow<IntentViewState>` plus one-off effect stream.
  - Removed `de.palm.composestateevents` usage from this screen path.
  - Replaced ad hoc VM methods (`updateX`, `enroll`, `verify`, etc.) with action dispatch.
  - Preserved `SavedStateHandle` persistence for intent form/result fields and pending call marker.
  - Updated `IntentScreen` to collect effects lifecycle-aware and launch external contract from effect stream.
  - Updated `ShowErrorAlert` to state-driven API (`Boolean` + dismiss callback) and wired metadata error dismissal via action.
  - Added unit coverage for `IntentViewModel` action/state behavior (field mutation, cached hydration, clear/reset).
- **Behavior parity notes**
  - Navigation route contracts unchanged.
  - Simprints launch remains ActivityResult-driven; trigger source moved from wrapper event to effect flow.
  - Cache load/save and result rendering remain ViewModel-owned.
- **Validation evidence**
  - `:app:testDebugUnitTest` passes.
  - `:app:assembleDebug` passes.
  - `:app:lintDebug` fails on pre-existing `AccordionLayout.kt` `NonObservableLocale` issue.

### Screen 2/6 - Custom Intent screen (`CustomIntentViewModel` + `CustomIntentScreen`)
- **Status:** Completed and ready for review pause.
- **Implemented**
  - Migrated to MVI contract with `CustomIntentViewUiState`, `CustomIntentUiAction`, and `CustomIntentUiEffect`.
  - Reworked `CustomIntentViewModel` to extend `BaseMviViewModel` and expose immutable `state` + one-off `effects`.
  - Replaced direct VM mutation methods with single `onAction(...)` entrypoint.
  - Removed `de.palm.composestateevents` usage from this screen path.
  - Kept cached action/extras restoration and clear behavior.
  - Moved Android `Intent` construction to UI effect handling in `CustomIntentScreen` for cleaner ViewModel/domain boundaries.
  - Switched pending custom call tracking to in-memory VM field (`pendingIntentCall`) to avoid SavedStateHandle parcel/runtime issues while preserving behavior.
  - Added `CustomIntentViewModelTest` coverage for:
    - field updates,
    - cached hydration,
    - launch cache persistence,
    - result handling and repository persistence path,
    - invalid custom intent non-persistence path,
    - clear/reset behavior.
- **Behavior parity notes**
  - Navigation route contract unchanged (`custom`).
  - Custom intent launch still occurs through `GenericIntentContract`; only trigger mechanism changed (effect flow).
  - Result text rendering and history persistence rules preserved (`INVALID_CUSTOM_INTENT` still excluded from save path).
- **Validation evidence**
  - `:app:testDebugUnitTest` passes.
  - `:app:assembleDebug` passes.
  - `:app:lintDebug` fails on the same pre-existing `AccordionLayout.kt` `NonObservableLocale` issue.
