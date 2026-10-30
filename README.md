# TimeTrace — Android usage journal

TimeTrace is a native Android project (Kotlin-free Java + Android SDK APIs) for keeping a local history of app sessions.

## Features in this first source release

- Usage Access permission flow.
- Reads Android `UsageEvents` and stores completed foreground sessions in a local SQLite database.
- Daily, weekly, monthly and yearly summaries.
- Date picker and per-app session details.
- App list with search and usage sorting.
- Light/dark theme and three accent colors.
- CSV export and JSON backup/restore.
- Optional evening reminder notification.
- Periodic refresh alarm and alarm rescheduling after reboot.
- No account, server, analytics SDK, or internet permission.

## Build the APK

Requirements: Android Studio with Android SDK Platform 36, Build Tools 36.x, and JDK 17 or newer. Open this folder in Android Studio and allow Gradle sync. Then choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

Or from a terminal with Gradle 8.11.1+ installed:

```bash
gradle assembleDebug
```

The debug APK will be created at:

`app/build/outputs/apk/debug/app-debug.apk`

This workspace did not contain the Android SDK, Android build-tools, or Gradle, and outbound package downloads are unavailable. Therefore the source project is provided, but an APK could not be compiled or verified in this environment. Do not rename the project ZIP to `.apk` — it is source code, not an installable package.

## First launch on the phone

1. Install the built APK.
2. Open TimeTrace and tap **Открыть настройки доступа**.
3. Find TimeTrace in Android's Usage Access settings and enable **Разрешить доступ к истории использования**.
4. Return to TimeTrace.
5. On Redmi/Xiaomi, if available in your system version, allow background autostart and set battery usage for TimeTrace to **No restrictions**. Menu labels can differ by MIUI/HyperOS version.
6. Optionally enable notifications for the daily reminder.

## Accuracy and Android limitations

Android only exposes usage events after the user grants Usage Access. TimeTrace stores completed sessions in its own database and checks for new events periodically (nominally every 15 minutes) and when opened. Android Doze, MIUI/HyperOS battery management, force-stop, reboot timing, and vendor scheduling can delay background checks. This is not a privileged system monitor and cannot guarantee second-perfect, uninterrupted recording. A foreground app's current unfinished session is only committed once Android reports the corresponding pause/background event.

On first install, historical events are limited to the event history Android still makes available. TimeTrace cannot recreate months of past activity retroactively. Once saved locally, its own records remain until the user clears app data or deletes the history. The app intentionally excludes its own package from tracked sessions.

## Privacy

All session records are stored locally in `timetrace.db`. Export/backup happens only when the user chooses a destination through Android's document picker. The app declares no internet permission.
