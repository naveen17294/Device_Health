# Device Health App - Memory & Context

## Project Overview
- **App Name**: Device Health
- **Platform**: Native Android (Kotlin + Jetpack Compose)
- **Architecture**: MVVM + Repository (Hilt, Room, DataStore)
- **Primary Goal**: Track device health (RAM, Storage, Uptime) and allow manual logging of lag events (via app or Quick Settings tile), providing deterministic recommendations for optimization.

## Current Status
- Project initialized.
- Git repository initialized and checked out to `release_v1.0.0`.
- Basic `Dev_plan` has been reviewed.
- Clarifying requirements with the user before starting Phase 1 (Core dashboard).

## Next Steps
- Confirm requirements with user.
- Setup Android project structure (Kotlin, Compose, Min SDK 29).
- Implement Phase 1: Core Dashboard.

## Key Constraints
- Keep all diagnostic data on-device by default (offline MVP).
- Avoid unnecessary permissions (no internet, no accessibility services, no continuous foreground services).
