# Legends: Monsters and Heroes

A Java 17+ console RPG. It uses only the JDK and reads game templates and UTF-8 monster art from `data/`.

## Build and run

From the project root on macOS or Linux:

```bash
mkdir -p out
find src/main/java -name '*.java' > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -cp out monstersheroes.Main
```

On Windows Command Prompt, run `chcp 65001` first, then compile with `dir /s /b src\main\java\*.java > sources.txt`, `if not exist out mkdir out`, and `javac -encoding UTF-8 -d out @sources.txt`. Start with `java -cp out monstersheroes.Main`. Keep the working directory at the project root so `data/` can be found. The `Main` class also accepts a data-directory path as its first argument.

Run the dependency-free feature checks from the project root with:

```bash
find src/main/java src/test/java -name '*.java' > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -ea -cp out monstersheroes.FeatureSmokeTest
```

## Controls

| Key | Action |
|---|---|
| W / A / S / D | Move up / left / down / right |
| I | Show party statistics |
| V | View inventories |
| E | Equip a weapon or armor outside battle |
| P | Use a potion outside battle |
| M | Trade while standing on a market tile |
| R | Interact with the portal under the party |
| T | Display the map |
| H | Show help |
| Q | Quit |

The map redraws after movement. `@` marks the party, `.` an open area, `$` a market, `#` blocked terrain, and `O` the portal. Stepping onto a portal opens its menu automatically. A party has 1–3 heroes; each hero can be a Warrior, Sorcerer, or Paladin.

## Depths, encounters, and bosses

The adventure has three one-way depths:

| Depth | Common encounter roster | Portal boss |
|---|---|---|
| 0 | Iron Exoskeleton, Undead Zombie, Skull Eater | Skeleton King |
| 1 | Ghost Face, Wailing Spirit | Gengar |
| 2 | Ember Dragon, Ice Dragon, Old Dragon | Chrysophylax the Crystal Dragon |

Common areas have a 25% chance to start a battle. The number of common monsters matches party size; their type is selected from the current depth's roster. They use the existing exoskeleton, spirit, or dragon family illustration. Common monster level is the higher of the party's highest hero level and the depth's minimum level.

At a portal, choose to challenge the current boss or explore that depth. A portal boss is a single enemy regardless of party size, has substantially higher HP and damage than a common monster, and is only available once on that depth. Defeat the boss before descending. After descending, earlier depths cannot be revisited. Depth 2 has no further descent. Winning the game requires reaching Depth 2 and defeating Chrysophylax. Boss illustrations are preserved in UTF-8 under `data/art/bosses/` and are shown in their portal battles.

## Equipment and markets

Weapons can declare eligible hero classes in the final `Extra` field of `data/items.txt`. Use a comma-separated list such as `Sorcerer`; `-` means all classes. The restriction is checked when buying and equipping, including battle equipment actions. The supplied Magic Staff is Sorcerer-only, so Warriors cannot buy or equip it. Other default weapons remain available to all classes.

Markets support buying, selling, repairing broken equipment, and viewing inventory. Heroes have individual inventories and wallets. Starting potions and all other item templates remain defined by the existing game setup.

Item template rows use:

```text
TYPE|Name|Price|Level|Value1|Value2|Uses|Extra
```

- `WEAPON`: damage, hands required, and optional eligible classes in `Extra`.
- `ARMOR`: damage reduction in `Value1`.
- `POTION`: effect amount in `Value1`; `Extra` is `HP`, `MP`, `STRENGTH`, `DEXTERITY`, or `AGILITY`.
- `SPELL`: base damage, mana cost, and `ICE`, `FIRE`, or `LIGHTNING` in `Extra`.

## Battle and character systems

Heroes act before monsters. In battle, heroes can attack, cast a spell, use a potion, equip equipment, use a class ability, inspect battle status, skip, or quit. Monster attacks use the number-guess dodge mechanic (guess 1–7); a correct guess avoids the attack, while a miss still allows the hero's agility dodge. Hero and monster attacks can critically hit. Armor reduces damage, monsters can dodge, and spells and equipment have finite uses/durability. Markets repair broken gear.

Classes have distinct active abilities. Sorcerers can use Healing Light to restore HP to a living party member. XP is cumulative; surviving heroes receive battle rewards, and fallen heroes are revived with half HP and MP after a party victory but receive no reward.

## Data and source

- `data/heroes.txt`, `data/monsters.txt`, and `data/items.txt` contain default templates.
- `data/art/` contains existing common-monster illustrations and `data/art/bosses/` contains the three boss illustrations.
- `src/main/java/monstersheroes/GameController.java` coordinates game flow; `world/World.java` builds maps; `battle/` runs combat and creates monsters; `market/` handles trading.

No gameplay state is written back to these data files. Start a new game by running the program again.
