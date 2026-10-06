# Store graphics

The images for the Play Console store listing (the text is in `../play-store`). They replace the old `design/` folder.

| Folder or file | Play Console field | Size |
|---|---|---|
| `phone/` | Phone screenshots | 1080 x 2160 |
| `tablet-7in/` | 7-inch tablet screenshots | 1200 x 1920 |
| `tablet-10in/` | 10-inch tablet screenshots | 2560 x 1600 |
| `wear/` | Wear OS screenshots | 454 x 454 |
| `icon-512.png` | App icon | 512 x 512 |
| `feature-graphic-1024x500.png` | Feature graphic | 1024 x 500 |

## How they were made

Plain screen captures from the debug build on emulators (no device frames), with the clock and status bar set with Android's demo mode (9:41, full battery, Wi-Fi). A different date was set on the emulator for each shot so the moon, and so the colours, differ: full moon on 25 October 2026 (gold), a first-quarter moon on 18 October (green), a crescent on 14 October (teal). The Data screen uses a mock location (Vancouver), so the position rows are filled in. The phone shots use a 1080 x 2160 screen so they fit Play's 2:1 limit without cropping.

The feature graphic is the three Wear shots side by side.

To remake them, set the emulator date with `adb shell cmd alarm set-time <epoch ms>`, give it a mock location with `adb shell cmd location providers set-test-provider-location`, and restore the date afterwards.
