# Device Health App - Memory & Context

## Project Overview
- **App Name**: Device Health
- **Platform**: Native Android (Kotlin + Jetpack Compose)
- **Architecture**: MVVM + Repository (Hilt, Room, DataStore)
- **Primary Goal**: Track device health (RAM, Storage, Uptime) and allow manual logging of lag events (via app or Quick Settings tile), providing deterministic recommendations for optimization.

## Current Status
- Project initialized.
- Git repository initialized and checked out to `release_v1.0.0`.
- Basic `Dev_plan` has been reviewed and updated to target API 34.
- Scaffolded the base Android app (`com.devicehealth.assistant`).
- Implemented **Phase 1: Core Dashboard** (RAM, Storage, Uptime).
- Implemented **Phase 2: App Analysis** (Usage Access onboarding, App sizes, Recommendations).
- Added DataStore preferences for configurable app thresholds and storage warning percentages.
- Implemented **Phase 3: Lag Tracking** (Room DB, Quick Settings Tile, Manual log history screen).
- Implemented **Phase 4: Recommendations** (Deterministic rule engine for device optimization).
- Implemented **Phase 5: Quality** (Unit tests, JankStats integration).

## Next Steps
- Project is fully complete according to the initial MVP plan.

## Key Constraints
- Keep all diagnostic data on-device by default (offline MVP).
- Avoid unnecessary permissions (no internet, no accessibility services, no continuous foreground services).
