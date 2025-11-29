# Material3 UI Theme Screenshot Tests

This directory contains golden reference images for Material3 UI theme screenshot tests.

## Golden Images

- `list_habits_light.png` - Light theme for habit list screen
- `list_habits_dark.png` - Dark theme for habit list screen
- `list_habits_pure_black.png` - Pure black theme for habit list screen
- `show_habit_light.png` - Light theme for habit detail screen
- `show_habit_dark.png` - Dark theme for habit detail screen
- `show_habit_pure_black.png` - Pure black theme for habit detail screen

## Regenerating Golden Images

To regenerate golden images after intentional UI changes:

1. Run the tests to generate new screenshots (tests will fail but save actual renders):
   ```bash
   ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=org.isoron.uhabits.material.MaterialThemeScreenshotTest
   ```

2. Pull the generated screenshots from the device:
   ```bash
   adb pull /data/data/org.isoron.uhabits/test-screenshots/views/habits/material/ ./new_screenshots/
   ```

3. Review the new screenshots for correctness

4. Replace the old golden images with the new ones:
   ```bash
   cp ./new_screenshots/views/habits/material/*.png ./src/androidTest/assets/views/habits/material/
   ```

5. Run the tests again to verify they pass:
   ```bash
   ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=org.isoron.uhabits.material.MaterialThemeScreenshotTest
   ```

## Adjusting Similarity Cutoff

If tests fail due to minor color variations or anti-aliasing differences, you can adjust the `similarityCutoff` value in `MaterialThemeScreenshotTest.kt`. Current value: `0.00020`

Lower cutoff = stricter matching (catches more regressions)
Higher cutoff = more lenient (fewer false positives)

## Test Location

Tests: `uhabits-android/src/androidTest/java/org/isoron/uhabits/material/MaterialThemeScreenshotTest.kt`
