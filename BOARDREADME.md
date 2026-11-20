# Board-Related File Changes

| File | Board change |
| --- | --- |
| [World.java](src/main/java/monstersheroes/world/World.java) | Generates the board, assigns terrain, tracks character positions, validates movement, clears obstacles, and renders the board. |
| [GameController.java](src/main/java/monstersheroes/GameController.java) | Handles lane selection, board commands, turn rotation, terrain changes, and victory when a hero reaches the monsters' Nexus. |
| [Hero.java](src/main/java/monstersheroes/characters/Hero.java) | Applies temporary terrain bonuses when calculating a hero's effective stats. |
| [Tile.java](src/main/java/monstersheroes/world/Tile.java) | Defines tile symbols, descriptions, passability, and optional stat bonuses. |
| [CommonTile.java](src/main/java/monstersheroes/world/CommonTile.java), [InaccessibleTile.java](src/main/java/monstersheroes/world/InaccessibleTile.java) | Represent plain terrain and permanent walls. |
| [NexusTile.java](src/main/java/monstersheroes/world/NexusTile.java) | `N` marks the heroes' and monsters' spawn areas. Heroes can trade at their own Nexus. A hero who reaches the monsters' Nexus wins the game. |
| [ObstacleTile.java](src/main/java/monstersheroes/world/ObstacleTile.java) | `O` blocks movement. A hero can spend one board action to clear an adjacent obstacle. The tile becomes plain terrain and the hero stays in place. |
| [BushTile.java](src/main/java/monstersheroes/world/BushTile.java) | `B` increases the hero's dexterity (DEX) by 10% while the hero occupies the tile. |
| [CaveTile.java](src/main/java/monstersheroes/world/CaveTile.java) | `C` increases the hero's agility (AGI) by 10% while the hero occupies the tile. |
| [KoulouTile.java](src/main/java/monstersheroes/world/KoulouTile.java) | `K` increases the hero's strength (STR) by 10% while the hero occupies the tile. |
