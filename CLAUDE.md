# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build
./gradlew assembleDebug       # Debug APK
./gradlew assembleRelease     # Release APK
./gradlew build               # Full build (all variants)
./gradlew clean               # Clean build artifacts

# Testing (framework not yet configured)
./gradlew test                # Unit tests
./gradlew connectedAndroidTest # Instrumented tests

# Code quality
./gradlew lint                # Lint checks
```

> Note: Testing dependencies are not yet added to `libs.versions.toml` or `app/build.gradle.kts`. The `testInstrumentationRunner` is declared but the runner dependency is missing.

## Architecture

**Pattern:** MVVM + Repository + Hilt DI
**UI:** Jetpack Compose with Material3
**Navigation:** Jetpack Navigation Compose (`NavGraph.kt`)

### Data Flow

```
Composable Screen ↔ ViewModel (StateFlow) ↔ Repository ↔ Room DB
                                                        ↔ DataStore (preferences)
```

### Layer Responsibilities

- **`ui/screen/<feature>/`** — Each feature has a `Screen.kt` (Composable) and `ViewModel.kt`. ViewModels expose `StateFlow` and receive user events.
- **`data/repository/`** — `ExerciseRepository` abstracts Room DAO and is the single source of truth for exercise records.
- **`data/preferences/`** — `UserPreferencesRepository` wraps DataStore for user settings (exercise duration, reps, etc.).
- **`data/db/`** — Room setup: `AppDatabase`, `ExerciseRecord` entity, `ExerciseRecordDao`, `Converters`.
- **`di/AppModule.kt`** — Hilt `@Module` providing Room DB and DAO singletons.
- **`service/TimerService.kt`** — Foreground service managing the exercise timer (runs when app is backgrounded).
- **`TimerState.kt`** — Shared state model used across timer-related components.

### Key Entry Points

- `KegelApplication.kt` — `@HiltAndroidApp` application class
- `MainActivity.kt` — Single activity, hosts the Compose `NavHost`
- `ui/navigation/NavGraph.kt` — All route definitions and screen wiring

## Tech Stack Versions

All versions are centralized in `gradle/libs.versions.toml`. Key versions:
- Kotlin 2.0.21, AGP 8.13.2, Compose BOM 2024.12.01
- Room 2.6.1, Hilt 2.52, Navigation Compose 2.8.4
- Min SDK 31 (Android 12), Target/Compile SDK 35
