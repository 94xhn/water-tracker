# WaterTracker

A local-first Android water tracking app — zero ads, zero subscriptions, zero cloud.

## Features

- **Progress arc** — circular progress showing today's effective hydration vs. goal
- **6 drink types** — Water, Coffee (×0.8), Tea (×0.9), Juice (×0.9), Milk (×0.9), Soda (×0.75) with hydration factors
- **Streak tracker** — consecutive days hitting your daily goal
- **Goal celebration** — arc turns green + "🎉 Goal reached!" when you hit your target; one-time notification
- **Smart reminders** — periodic notifications with one-tap quick-log actions (200ml / 250ml); rescheduled automatically after device reboot
- **7-day history** — per-day cards with progress bars and a weekly summary (daily avg, goal-met days)
- **Undo last drink** — instantly remove the most recent entry
- **CSV export** — share all your data as a CSV file
- **Weight-based goal** — enter your weight and get a personalized daily goal recommendation
- **Customizable cup sizes** — configure 4 quick-add buttons to match your cups

## Tech Stack

- Kotlin + Jetpack Compose (Material 3)
- Room 2.6.1 (SQLite, with migrations)
- Kotlin Flows + StateFlow
- Manual DI (no Hilt)
- AlarmManager for reminders
- No external network permissions

## Building

```bash
./gradlew assembleDebug
```

Requires Android Studio Ladybug or later (JBR 21+).

## License

MIT
