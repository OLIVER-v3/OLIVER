<p align="center"><img src="logo.png" width="200" alt="OLIVER"></p>

# OLIVER

Keyboard & mouse mapper for mobile games (in the style of GG Mouse Pro).

## Status
- [x] App shell, logo, name **OLIVER**
- [x] Keymap editor: tap to add a button, bind **any** key / mouse button / wheel, drag to move, long-press to delete
- [x] 16 key functions (tap, hold, toggle, turbo, macro, camera look, fire-look, scope, ...)
- [x] Conflict checker, JSON profiles (default: `freefire`)
- [x] Camera-look maths (`CameraLookHandler`)
- [ ] Phase 2: activation (Wireless ADB / Shizuku) + touch injection server
- [ ] Phase 2: overlay + pointer, macro engine

## Build on GitHub
Push to `main` — the **Build OLIVER APK** workflow runs and uploads `OLIVER-debug-apk`
(Actions tab -> latest run -> Artifacts).

## Build locally
Android Studio (Hedgehog+) -> open this folder -> Run. Or `gradle assembleDebug` with Gradle 8.9 + JDK 17.

## Folder layout
```
OLIVER/
├── .github/workflows/build-apk.yml
├── app/src/main/
│   ├── java/com/jerry/oliver/
│   │   ├── MainActivity.kt
│   │   ├── editor/   EditorActivity, EditorView, BindingDialog
│   │   ├── keymap/   KeymapModel, KeyCatalog, ConflictChecker, KeymapStore
│   │   └── input/    CameraLookHandler
│   ├── assets/profiles/freefire.json
│   └── res/          layouts, themes, launcher icons (from logo)
├── logo.png
└── build.gradle.kts, settings.gradle.kts, gradle.properties
```
