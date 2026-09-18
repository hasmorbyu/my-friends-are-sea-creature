# My Friends Are Sea Creatures

A funny, casual, Talking-Tom-style virtual pet game where your real-life friends become cartoon
sea creatures. Add a friend, give them a photo, answer a silly personality quiz about them, and
the game randomly (but not deterministically) assigns them a sea creature whose cartoon body wears
their actual face. Feed them, play with them, watch them bicker with each other, and grow your
aquarium.

## Features

- **Add Friend flow**: name -> photo -> 9-question personality quiz -> weighted-random creature
  reveal -> added to the aquarium.
- **12 sea creatures** (Octopus, Crab, Shark, Pufferfish, Sea Turtle, Dolphin, Jellyfish, Squid,
  Clownfish, Lobster, Whale, Shrimp), each with its own personality flavor, movement style,
  preferred food and funny dialogue lines.
- **Real face on a cartoon body**: the friend's photo is cropped to a circle and composited onto
  their creature's procedurally-drawn body.
- **Weighted random creature assignment**: personality nudges probabilities, it never hard-picks
  a creature outright - two chaotic friends can still get completely different creatures.
- **Virtual pet stats**: Health, Hunger, Happiness, Cleanliness, Energy (0-100), with Feed/Play/
  Clean/Rest actions and passive decay over time. Neglect produces funny in-game complaints, never
  permanent harm.
- **Relationships**: every pair of friends has a -100..100 relationship score (Besties/Friends/
  Neutral/Rivals) nudged by random events.
- **Data-driven random event engine**: food theft, races, arguments, treasure finds, and more -
  new events are added as data, not new code paths in the game loop.
- **Aquarium progression**: coins earned from care/events/mini-games unlock bigger tanks (Basic
  Tank -> Larger Tank -> Coral Reef -> Treasure Chest -> Shipwreck -> Castle -> Deep Sea Area).
- **Bubble Pop mini-game** rewarding coins and happiness.
- **JSON save/load** that survives restarts, with graceful fallback on a corrupted save file.
- **Terminal mode** exercising the same shared game logic with a plain text menu.

## Technology stack

- Java 17, LibGDX 1.12.1, Gradle (via the wrapper - no local Gradle install required)
- `core` module: all shared game logic, rendering, and UI (LibGDX scene2d.ui with a runtime-
  generated Skin, so no external UI atlas is needed)
- `lwjgl3` module: desktop (Linux/Windows) launcher + terminal mode entry point
- `android` module: Android launcher activity, manifest, and Android-specific photo picker

## Project structure

```
browl/
├── core/           shared game logic, screens, UI (Java, LibGDX)
├── lwjgl3/         desktop launcher + terminal mode (JavaFX-free, plain Swing file chooser)
├── android/        Android launcher activity + manifest
├── assets/         runtime assets (currently minimal - most visuals are generated at runtime)
├── gradlew, gradlew.bat, gradle/wrapper/
├── settings.gradle, build.gradle, gradle.properties
└── run.sh
```

## Requirements

- Java 17+ (JDK). Verify with `java -version`.
- No Gradle install needed - the checked-in wrapper (`./gradlew`) downloads the right Gradle
  version on first use.
- For Android builds only: Android SDK with `ANDROID_HOME` (or `ANDROID_SDK_ROOT`) set. Without
  it, the `android` module is automatically excluded from the build (see `settings.gradle`), so
  desktop/terminal builds work with zero Android tooling installed.

## Desktop: run it

```bash
chmod +x run.sh
./run.sh
```

This runs `./gradlew lwjgl3:run`, which launches the graphical desktop game in an
`960x600` window.

## Terminal mode

```bash
./run.sh --terminal
```

or directly:

```bash
./gradlew lwjgl3:terminal
```

Gives a plain-text menu (add friend, view friends, feed, play, relationships, trigger an event,
view the aquarium, save, exit) that runs the exact same core game logic (`core` module classes) as
the graphical app, minus photo selection and rendering. Useful for testing, debugging, or just
poking at the simulation without a GUI.

## Android: build the APK

Requires Android Studio or a standalone Android SDK with `ANDROID_HOME` set.

```bash
export ANDROID_HOME=/path/to/Android/sdk
./gradlew android:assembleDebug
```

The debug APK lands at `android/build/outputs/apk/debug/android-debug.apk`. Install it with:

```bash
adb install android/build/outputs/apk/debug/android-debug.apk
```

The Android build shares 100% of the game logic in `core` - only the launcher Activity and the
photo picker (`AndroidPhotoProvider`, using the classic gallery `Intent.ACTION_GET_CONTENT` +
`onActivityResult`, since LibGDX's `AndroidApplication` predates the newer Activity Result APIs)
are platform-specific.

**Verified in this environment**: with an Android SDK installed (API 34 platform + build-tools),
`./gradlew android:assembleDebug` builds a valid, correctly-signed debug APK. It was installed and
launched on a real API 34 x86_64 emulator (booted headless via `emulator -no-window`) and
end-to-end tested with actual touch input: added a friend by name via the on-screen keyboard,
answered all 9 quiz questions by tapping, reached the creature reveal, added the friend to the
aquarium, watched it swim (procedural movement confirmed live), triggered several random events
(coins/relationship/notification changes all visible), and picked a real photo through Android's
system gallery picker (`Intent.ACTION_GET_CONTENT`) - the uploaded photo appeared correctly
composited onto that friend's creature body, distinct from a second friend's generated placeholder
face on a different creature shape. Saving was also verified by reading
`/data/data/com.browl.seacreatures.android/files/SeaCreaturesSave/savegame.json` directly off the
device and confirming it matched the desktop save format.

## How the personality system works

`PersonalityQuiz` (in `core/.../system/`) asks 9 deliberately silly questions. Each answer nudges
a handful of `PersonalityTrait` scores (CHAOS, SOCIAL, LAZY, COMPETITIVE, CALM, DRAMATIC, CURIOUS,
GREEDY, ENERGETIC, INTROVERTED). Scores are summed then scaled/clamped into a 0-100 range per
trait.

## How creature assignment works

`CreatureAssigner` computes, for every `CreatureType`, a compatibility score from the friend's
personality against that creature's `affinity` map, multiplies it by a random factor
(`0.5 + random()`), and does a weighted random pick across all 12 creatures. This means high
compatibility makes a creature *likely*, never *guaranteed* - there is no `if chaos > 80` branch
anywhere in the code.

## How friend photos are stored

When a photo is picked (desktop: Swing `JFileChooser`; Android: system gallery picker), it's
copied into `<save-directory>/photos/<random-uuid>.<ext>` and the friend record stores only that
relative filename - never the original absolute path. `ImageManager` loads and center-crops it
into a circular texture, with a generated placeholder face used automatically if the photo is
missing or fails to load.

## How saving works

`SaveManager` serializes the entire `GameState` (friends, stats, personalities, creature
assignments, relationships, coins, aquarium tier, settings) to `savegame.json` using LibGDX's
`Json` class, via plain `java.io`/`java.nio` file APIs (no `Gdx.app` dependency), so the exact same
save code runs in both the graphical app and terminal mode. A missing or corrupted save file falls
back to a fresh `GameState` instead of crashing.

- Desktop/terminal save location: `~/.seacreatures/` (terminal) or the LibGDX local storage path
  under a `SeaCreaturesSave/` folder (graphical app).
- Android: LibGDX's app-private local storage path.

## Architecture

```
SeaCreaturesGame (Game)
  -> AquariumScreen (Screen)          the main game view + bottom nav
       -> AddFriendDialog             name -> photo -> quiz -> reveal, as a scene2d Window
       -> FriendDetailDialog          stats bars, personality, relationships, care buttons
       -> ShopDialog                  aquarium upgrades
       -> BubblePopDialog             mini-game
       -> CreatureRenderer            procedural body shapes + composited face texture

model/    Friend, CreatureType, Personality, PersonalityTrait, MovementStyle, RelationshipState
system/   StatManager, PersonalityQuiz, CreatureAssigner, EventManager, RelationshipManager,
          SaveManager, ImageManager, PhotoProvider, GameState, CreatureMovement, AquariumUpgrade
ui/       UiFactory (runtime-generated Skin) + the dialogs above
```

Platform launchers (`Lwjgl3Launcher`, `AndroidLauncher`) only wire up a `PhotoProvider`
implementation and hand off to the shared `SeaCreaturesGame`; no game logic is duplicated between
platforms.

## Testing done so far

- `./gradlew core:compileJava lwjgl3:compileJava` - compiles cleanly.
- Terminal mode manually driven end-to-end: added two friends through the full quiz flow, got two
  different creature types, fed/played (verified stat changes), triggered a "helped clean" event
  (verified relationship score changed), saved, restarted the process, and confirmed friends,
  creature types, personalities, stats, event history, and the relationship score all survived
  the restart by inspecting `savegame.json` directly.
- Graphical desktop app (`./gradlew lwjgl3:run`) launched under Xvfb with no exceptions; this also
  caught and fixed a real bug (a missing `TextFieldStyle` in the runtime-generated `Skin` crashed
  the Add Friend name-entry screen).

## Still to do / known gaps

This is a large spec built incrementally; what's *not* yet done:

- No sound effects/music yet (settings toggles exist as data fields but there's no audio wired up).
- Only one mini-game (Bubble Pop) is implemented, not Feed Frenzy/Turtle Race.
- The `assets/` folder is currently mostly empty - almost everything visual is generated at
  runtime (procedural shapes, generated circular photo texture, generated UI skin) rather than
  loaded from image files, which keeps the project asset-free and license-clean but means there's
  no hand-drawn art.
- The Shop and Bubble Pop dialogs were verified to open in the graphical desktop app but not
  clicked all the way through interactively (Add Friend and the aquarium's core loop were fully
  tested on both desktop and Android). Give them a try and report any issues.

## Troubleshooting

- **`./gradlew` fails to download dependencies**: check your internet connection; first run needs
  to fetch LibGDX artifacts from Maven Central.
- **Android module not found by Gradle**: this is expected without `ANDROID_HOME` set - see
  "Android: build the APK" above.
- **Save seems to reset**: check that `savegame.json` isn't corrupted; `SaveManager` logs a warning
  and starts fresh rather than crashing if it can't parse the file.
