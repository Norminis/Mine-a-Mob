# Mine-a-Mob (Forge 1.20.1)

A Forge 1.20.1 mod that attempts to spawn one random vanilla mob whenever a player breaks a block. There is no added ding/sound effect.

## Build the JAR

This project targets Minecraft 1.20.1, Forge 47.4.28, and Java 17. It must be built with ForgeGradle; do not use the earlier hand-patched JARs.

### Easiest build without installing Java/Gradle locally

1. Create a new GitHub repository and upload the contents of this folder to it.
2. Open the repository's **Actions** tab and enable Actions if prompted.
3. Run **Build Forge mod** with **Run workflow**, or push a commit to `main`/`master`.
4. When the workflow finishes, open that run and download the `Mine-a-Mob-Forge-1.20.1` artifact. It contains the compiled JAR.
5. Put the JAR in your Modrinth profile's `mods` folder.

### Local build

Install JDK 17 and Gradle 8.1.1, open a terminal in this folder, and run `gradle build`. The JAR will be in `build/libs/`.

## Notes

- Forge-only; it is not a Fabric mod.
- The mob selection is restricted to vanilla entity types.
- This source project has not been compiled in this environment because external network access is unavailable here. Build it before use; do not treat the source ZIP as a ready-to-install mod JAR.
