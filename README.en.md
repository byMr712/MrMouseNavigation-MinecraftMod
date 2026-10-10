> **Language:** [Русский](README.md) · English

# [MR] Mouse Navigation

![Java 25](https://img.shields.io/badge/Java-25-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-26.1-blue.svg)
![Fabric](https://img.shields.io/badge/Loader-Fabric-blue.svg)
![ModMenu](https://img.shields.io/badge/ModMenu-Supported-blue.svg)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

## About the Mod

**[MR] Mouse Navigation** is a client-side Fabric mod for **Minecraft 26.1**, enabling navigation using mouse side buttons in game interfaces. With it, the lower side mouse button (Mouse 4) acts as "Back", the upper side button (Mouse 5) acts as "Forward", and the middle mouse button (scroll wheel click) allows sending chat messages, while your configured mouse controls in the game remain unchanged!

## Gallery

![Configuration Menu](/images/Mouse_navigation_modmenu_en.png)

## What can this mod do?

1. **Back Button (Mouse Button 4 / XBUTTON1 / Lower Side Button)**:
   - **Screens & Menus (Settings, Chests, Inventory, Pause, World Selection, etc.)**: Closes screen or returns to previous menu (equivalent to `Esc`).
   - **Books & Lecterns**: Flips to previous page (`<`).
   - **Recipe Book**: Flips to previous recipe page.
   - **Creative Inventory**: Switches to previous item category tab.
   - **Advancements**: Switching between advancement tabs (requires the [**Better Advancements**](https://www.curseforge.com/minecraft/mc-mods/better-advancements) mod).
   - **Chat**: Scrolls sent message history backward (equivalent to `↑`).

2. **Forward Button (Mouse Button 5 / XBUTTON2 / Upper Side Button)**:
   - **Books & Lecterns**: Flips to next page (`>`).
   - **Recipe Book**: Flips to next recipe page.
   - **Creative Inventory**: Switches to next item category tab.
   - **Advancements**: Switching between advancement tabs (requires the [**Better Advancements**](https://www.curseforge.com/minecraft/mc-mods/better-advancements) mod).
   - **Chat**: Scrolls sent message history forward (equivalent to `↓`).
   - **Screen History**: Returns forward into nested screens you backed out of.

3. **Middle Mouse Button (Mouse Button 3 / MMB / Scroll Wheel Click)**:
   - **Chat**: Instantly sends the typed chat message (equivalent to `Enter`).

4. **ModMenu Integration & Customization**:
   - Easily toggle any feature individually via **ModMenu**, enable soft click sound feedback, or invert buttons.

## Behavior Table

| Interface | Back Button (Mouse 4) | Forward Button (Mouse 5) | Middle Mouse Button (MMB) |
| :--- | :--- | :--- | :--- |
| Standard menus & containers (Settings, chests, crafting, pause) | Close / Back (`Esc`) | Return forward in history | — |
| Books & lecterns | Previous page (`<`) | Next page (`>`) | — |
| Recipe Book | Previous recipe page | Next recipe page | — |
| Creative inventory | Previous tab | Next tab | — |
| Advancements (requires [**Better Advancements**](https://www.curseforge.com/minecraft/mc-mods/better-advancements)) | Previous tab | Next tab | — |
| Chat | History back (`↑`) | History forward (`↓`) | Send message (`Enter`) |
| In-game (no screen open) | Vanilla keybinding action | Vanilla keybinding action | Vanilla keybinding action |

## Settings (ModMenu)

When **ModMenu** is installed, you can configure the following options:
- **General toggle**: Enable / disable mouse navigation.
- **Screen Back**: Allow Mouse 4 to close screens and menus.
- **Screen Forward**: Allow Mouse 5 to return forward into submenus.
- **Books & Lecterns**: Flip book and lectern pages.
- **Recipe Book**: Flip recipe book pages.
- **Creative Tabs**: Cycle creative item tabs.
- **Advancements**: Switching between advancement tabs (requires the [**Better Advancements**](https://www.curseforge.com/minecraft/mc-mods/better-advancements) mod).
- **Chat History**: Browse sent chat message history.
- **Chat Send (MMB)**: Send chat message on middle mouse button click.
- **Click Sound**: Play a soft click sound on navigation.
- **Invert Buttons**: Swap Mouse 4 and Mouse 5.

Configuration is saved in `.minecraft/config/mrmousenavigation.json`.

## Mod Compatibility

- [**Mouse Tweaks**](https://www.curseforge.com/minecraft/mc-mods/mouse-tweaks): 100% compatible. The mods do not conflict and complement each other seamlessly — **Mouse Tweaks** handles item slot operations (LMB, RMB, wheel scrolling), while **[MR] Mouse Navigation** handles side button navigation (Mouse 4 and Mouse 5) and closing screens.
- [**Better Advancements**](https://www.curseforge.com/minecraft/mc-mods/better-advancements): Fully supports cycling advancement tabs using mouse side buttons.

## Supported Minecraft Versions

The mod is ported and maintained across all versions in separate git branches:
- **1.21.x:** `1.21`, `1.21.1`, `1.21.2`, `1.21.3`, `1.21.4`, `1.21.5`, `1.21.6`, `1.21.7`, `1.21.8`, `1.21.9`, `1.21.10`, `1.21.11`
- **26.x:** `26.1`, `26.1.1`, `26.1.2`, `26.2`, `26.3`

## Installation

1. Download `MrMouseNavigation-Fabric-26.1-byMr712-v1.0.jar` from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mr-mouse-navigation) or [GitHub Releases](https://github.com/byMr712/MrMouseNavigation-MinecraftMod/releases).
2. Ensure you have **Fabric Loader** installed (Fabric API is not required).
3. Place the downloaded `.jar` into your `.minecraft/mods` folder.
4. Launch the game!

## Building from Source

1. Clone the repository and switch to the desired version branch:
   ```bash
   git clone https://github.com/byMr712/MrMouseNavigation-MinecraftMod.git
   cd MrMouseNavigation-MinecraftMod
   git checkout 26.1
   ```
2. Build the project:
   ```bash
   ./gradlew build
   ```
3. The built JAR file will be in `build/libs/MrMouseNavigation-Fabric-26.1-byMr712-v1.0.jar`.

## License

This project is licensed under the **Apache-2.0** license.
