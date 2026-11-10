# Legends: Monsters and Heroes (Java)

A console-based Java implementation of the CS 611 Legends: Monsters and Heroes assignment, with the additional **1–7 predictive dodge** mechanic.

## Requirements

- JDK 17 or newer
- A terminal / command prompt

No external libraries are required.

## Run

The project does not require a `run.sh` or `run.bat` file. Compile and launch it directly from the project folder using the commands below.

### macOS / Linux

Open Terminal in the `MonstersAndHeroes` project folder and run:

```bash
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -cp out monstersheroes.Main
```

### Windows (Command Prompt)

Open Command Prompt in the `MonstersAndHeroes` project folder and run:

```bat
chcp 65001
dir /s /b src\main\java\*.java > sources.txt
if not exist out mkdir out
javac -encoding UTF-8 -d out @sources.txt
java -cp out monstersheroes.Main
```

The first command switches Command Prompt to UTF-8 so the monster illustrations display correctly. The commands compile all Java source files into the `out` folder and then start the game.

### IntelliJ IDEA or another IDE

Open the project and run `monstersheroes.Main`. Set the working directory to the project root (the folder containing `data`) so the game can load its data files and monster illustrations.

## Controls

| Key | Action |
|---|---|
| W / A / S / D | Move the party up / left / down / right; the map redraws after each movement attempt |
| I | Display hero statistics |
| V | Display hero inventories |
| E | Equip a weapon or armor outside battle |
| P | Use a potion outside battle |
| M | Enter a market (only while standing on a market tile) |
| T | Display the map |
| H | Display help |
| Q | Quit |

The party can contain 1–3 heroes. Choose a Warrior, Sorcerer, or Paladin for each hero. The world map is automatically redrawn after every movement command, including blocked moves, so the party marker and tile layout remain visible.

## Battle rules and predictive dodge

- A common tile has a 25% chance to trigger a battle when entered.
- The number of monsters equals the number of heroes. Monster level is the highest hero level in the party.
- Heroes act first. Each living hero can attack, cast a spell, use a potion, equip an item, use a class-specific ability, inspect the battle, skip their turn, or quit.
- **Class abilities:** choose option 5 during a hero's turn. Each class has its own three abilities:
  - **Warrior:** Power Strike (8 MP, 1.8x physical damage to one monster); Whirlwind (14 MP, 0.8x physical damage to all monsters); Armor Breaker (11 MP, 1.35x physical damage and reduces the target's defense by 25%).
  - **Sorcerer:** Arcane Bolt (10 MP, 1.55x arcane damage to one monster); Frost Nova (18 MP, 0.72x arcane damage to all monsters and reduces their attack damage by 20%); Healing Light (8 MP, restores exactly 10 HP to one living party member).
  - **Paladin:** Holy Smite (10 MP, 1.6x holy damage to one monster); Radiant Wave (16 MP, 0.7x holy damage to all monsters); Judgement (20 MP, 2.0x holy damage and reduces the target's dodge chance by 35%).
- **Sorcerer healing:** Healing Light can target any living party member, including the Sorcerer. It restores exactly 10 HP, is not capped by maximum HP, and costs 8 MP. Fainted heroes cannot be selected because the assignment says they cannot act for the remainder of that battle.
- Monsters then attack living heroes. During a battle, option 7 quits the game.
- **Predictive dodge:** before each monster attack resolves, enter a guess from 1 to 7. The monster independently generates a hidden random number from 1 to 7. A matching guess guarantees a complete dodge. If the guess is wrong, the hero still gets the assignment's agility-based dodge roll.
- **Critical hits:** hero weapon attacks, hero spells, and monster attacks each have a 15% critical-hit chance. A critical hit doubles damage before the target's defense or armor reduction.
- **Faster combat:** hero weapon damage uses a 0.20 strength/weapon multiplier (up from 0.05), spell damage is multiplied by 1.75, and monster base damage is multiplied by 2.0. These values are centralized in `CombatBalance.java` for easy tuning.
- Armor reduces incoming damage. Monsters can dodge hero attacks and spells.
- Console art appears on the title screen, at battle introductions, for critical hits and perfect dodges, at markets, and for victory or defeat. The supplied detailed dragon, spirit, and exoskeleton illustrations are stored as UTF-8 files in `data/art/` and shown when those monsters appear. The game explicitly writes UTF-8 to standard output. On Windows Command Prompt, run `chcp 65001` before starting the game so the illustrations display correctly.
- The world map has been redesigned as a compact grid with one outer border, aligned row/column labels, and a legend: `@` is the party, `.` is an open area, `$` is a market, and `#` is blocked terrain. It redraws after each movement attempt.
- Living heroes recover 10% of their current HP and MP at the end of a round.
- If the party wins, heroes who did not faint receive XP and gold. Fainted heroes are revived with half of their level-based HP and half of their current maximum mana, but receive no reward.

## Object-oriented features demonstrated

- **Abstract classes:** `Combatant`, `Hero`, `Monster`, `Item`, `Spell`, `Tile`.
- **Inheritance:** hero classes, monster classes, item classes, spell classes, and tile classes.
- **Interfaces:** `Attackable`, `Equipable`, `Consumable`, `Levelable`, `DodgeRule`, `BattleAction`, and `HeroAbility`.
- **Generics:** `Inventory<T extends Item>`.
- **Polymorphism:** specialized heroes, monsters, spells, and tiles are handled through base types and interfaces.
- **Composition:** a hero owns an inventory; a world owns its tiles; a market owns its stock.
- **Strategy pattern:** `DodgeRule` makes the dodge implementation replaceable.
- **Combat tuning:** `CombatBalance` centralizes damage multipliers and critical-hit settings rather than scattering balance values throughout the code.
- **Console presentation:** `AsciiArt` centralizes reusable ASCII illustrations.
- **Action classes:** `AttackAction`, `CastSpellAction`, `UsePotionAction`, `EquipAction`, and `UseHeroAbilityAction` delegate behavior to the battle engine.
- **Class abilities:** `SpecialAttackAbility` configures single-target/area attacks and optional debuffs; `HealingAbility` implements the Sorcerer's fixed 10-HP support move. `Hero.getSpecialAbilities()` is polymorphic and each concrete hero class supplies its own set.

## Default data files

The game loads its starting templates from:

- `data/heroes.txt`
- `data/monsters.txt`
- `data/items.txt`

These files are read when a new game starts and are never modified by gameplay. Character progression and item durability are stored on runtime objects, so a new program run starts from the defaults again.

### Item file format

Each non-comment line in `items.txt` follows:

```text
TYPE|Name|Price|Level|Value1|Value2|Uses|Extra
```

- `WEAPON`: `Value1` is damage; `Value2` is hands required.
- `ARMOR`: `Value1` is damage reduction.
- `POTION`: `Value1` is the effect amount; `Extra` is `HP`, `MP`, `STRENGTH`, `DEXTERITY`, or `AGILITY`.
- `SPELL`: `Value1` is base damage; `Value2` is mana cost; `Extra` is `ICE`, `FIRE`, or `LIGHTNING`.

## Design notes / simplifications

- The console interface uses a full-party battle system.
- Each one-handed weapon can be equipped with both hands for a 1.5x weapon-damage bonus. Two-handed weapons must use both hands.
- A hero starts with 500 gold and an empty inventory. Unarmed attacks are possible until a weapon is purchased.
- Market stock is generated separately for each market tile from the item templates.
- Spell and potion items are single-use. Weapons and armor have finite durability and can be repaired at a market for half their purchase price.
- Monster dodge is represented as a percentage in `monsters.txt`; hero passive dodge chance uses `agility * 0.002` (capped at 75%).
- Monster base damage and defense scale linearly with their level. Monster HP is `level * 100`; monster damage is then scaled by the combat balance multiplier.
- Experience is cumulative and never deducted. The cumulative thresholds to reach successive levels are 10 XP, then 30 XP, then 60 XP, and so on (the next increment is current level × 10).
- The helper ZIP mentioned in the assignment was not included with the PDF upload, so this project includes its own editable starter data in the `data` folder.

## Important source files

- `GameController`: main game loop and console controls.
- `World`: grid generation, movement, and map display.
- `Battle`: round orchestration and damage resolution.
- `NumberGuessDodge`: player-controlled number-guess dodge strategy.
- `HeroAbility`: class-specific active skills; see `battle/abilities/` and each hero subclass.
- `GameDataLoader`: reads default character, monster, and item data.

### Combat balance settings

Adjust the following constants in `src/main/java/monstersheroes/util/CombatBalance.java` to customize difficulty:

- `HERO_WEAPON_DAMAGE_MULTIPLIER`
- `SPELL_DAMAGE_MULTIPLIER`
- `MONSTER_DAMAGE_MULTIPLIER`
- `CRITICAL_HIT_CHANCE`
- `CRITICAL_HIT_MULTIPLIER`


## Map and monster illustrations

The map is displayed after every movement attempt. It uses a compact layout instead of drawing a separate box around every tile. Detailed Unicode/Braille illustrations for the Dragon, Spirit, and Exoskeleton are loaded from `data/art/dragon.txt`, `data/art/spirit.txt`, and `data/art/exoskeleton.txt`. Keep these files in place when moving or packaging the project; the game prints a small fallback label if an art file is missing.
