# Audio instructions

To add the "Jele" sound and other audio assets, place wave files into the `app/src/main/res/raw/` directory in your Android project.

Suggested filenames:
- `app/src/main/res/raw/jele_sound.wav`  (used as the main "jele" sound for pickups/jump/start)

If you don't add any file, the game uses a short generated beep as a fallback.

How to add the raw folder and file in Android Studio:
1. In Android view, right-click `app/src/main` → New → Android Resource Directory.
2. Resource type: `raw` → OK.
3. Right-click `res/raw` → New → File → name it `jele_sound.wav` and paste your audio file.

Short tips on creating a "jele"-like sound:
- A short bright pluck or bell (0.1–0.3s) works well.
- Use a high frequency with quick decay.
- Export as 16-bit WAV for best compatibility.
