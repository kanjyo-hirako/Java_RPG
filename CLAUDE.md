# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java-based 2D RPG game built with Java Swing. The game features character progression, turn-based combat, multiple maps, inventory management, and save/load functionality.

## Code Architecture

### Core Components

1. **Game Entry Point**: `GameApp.java` - Main class that starts the game
2. **Main Menu**: `GameMenu.java` - Handles the main menu with start game, load game, settings, and exit options
3. **Game World**: `GameMap.java` - Core game controller managing maps, player movement, events, and combat
4. **Character System**:
   - `Character.java` - Abstract base class for all characters
   - `Player.java` - Base player class with experience/leveling system
   - `Magician.java` - Concrete player implementation (the hero "cyxxx")
   - `Enemy.java` - Base enemy class
   - `Monster.java` - Regular enemies with flee mechanics
   - `Godzila.java` - Boss enemy with rage mechanic
5. **Combat System**: `BattleEngine.java` - Handles all combat calculations and actions
6. **Inventory System**:
   - `Inventory.java` - Manages player items
   - `Item.java` - Interface for items
   - `HP.java`/`MP.java` - Concrete item implementations
7. **Persistence**:
   - `GameSaver.java` - Save/load game functionality
   - `SaveData.java` - Serializable data container for saved games
8. **Audio**: `AudioPlayer.java` - Background music playback

### Key Design Patterns

- **Inheritance Hierarchy**: Character → Player → Magician and Character → Enemy → Monster/Godzila
- **Delegation**: Combat actions are delegated from characters to BattleEngine
- **Callback Pattern**: Inventory uses callbacks to update UI during combat
- **Serialization**: Game state persistence using Java serialization

## Development Commands

### Build and Run

Using Maven:
```bash
mvn compile
mvn exec:java -Dexec.mainClass="main.java.com.javarpg.GameApp"
```

Or using direct compilation:
```bash
javac -d out src/main/java/com/javarpg/*.java
java -cp out main.java.com.javarpg.GameApp
```

### Testing

Run tests with Maven:
```bash
mvn test
```

## Resource Structure

- `src/main/java/com/javarpg/` - Source code
- `src/main/resources/` - Game resources (images, music, inventory data)
- `src/test/java/com/javarpg/` - Unit tests
- `savegame.dat` - Saved game file
- `Inventory.txt` - Initial inventory configuration

## Important Notes

- The game uses WASD keys for movement
- Q key opens inventory during exploration
- P key saves the game during exploration
- Combat uses number keys and buttons for actions
- Maps are represented as 2D arrays with symbols (# walls, . paths, N NPCs, ! monsters)
- Audio files should be placed in `src/main/resources/music/`
- Image files should be placed in `src/main/resources/images/`