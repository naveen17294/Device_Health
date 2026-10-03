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

## Next Steps
- Implement Phase 3: Lag tracking (Manual lag-report button, Quick Settings tile, Room event history).

## Key Constraints
- Keep all diagnostic data on-device by default (offline MVP).
- Avoid unnecessary permissions (no internet, no accessibility services, no continuous foreground services).
