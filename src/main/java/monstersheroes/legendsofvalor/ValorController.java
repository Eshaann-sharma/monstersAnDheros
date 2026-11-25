package monstersheroes.legendsofvalor;

import monstersheroes.characters.Hero;
import monstersheroes.characters.Paladin;
import monstersheroes.characters.Sorcerer;
import monstersheroes.characters.Warrior;
import monstersheroes.data.GameData;
import monstersheroes.items.*;
import monstersheroes.market.Market;
import monstersheroes.market.MarketManager;
import monstersheroes.party.Party;
import monstersheroes.util.InputHandler;

import java.util.*;

/** Console interface for Legends of Valor. The board and action rules live in {@link ValorGame}. */
public final class ValorController {
    private final InputHandler input;
    private final GameData data;
    private final Random random = new Random();
    private final MarketManager marketManager = new MarketManager();
    private boolean running = true;
    private ValorGame game;
    private Market market;

    public ValorController(InputHandler input, GameData data) {
        this.input = input;
        this.data = data;
    }

    public void start() {
        System.out.println("\n========== LEGENDS OF VALOR ==========");
        Party party = createHeroes();
        List<Lane> lanes = chooseLanes();
        System.out.println("\nChoose monster spawn difficulty:");
        System.out.println("1. Easy (new wave every 6 rounds)");
        System.out.println("2. Medium (new wave every 4 rounds)");
        System.out.println("3. Hard (new wave every 2 rounds)");
        int difficulty = input.readInt("Difficulty: ", 1, 3);
        int spawnInterval = switch (difficulty) { case 1 -> 6; case 2 -> 4; default -> 2; };
        game = new ValorGame(party, data, lanes, random, spawnInterval);
        market = new Market(data, random, new HashSet<>());
        printInstructions();

        while (running && !game.isGameOver()) {
            System.out.println("\n================ ROUND " + game.roundNumber() + " ================");
            printBoard();
            for (ValorHeroUnit hero : game.heroes()) {
                if (!running || game.isGameOver()) break;
                if (hero.hero().isAlive()) takeHeroTurn(hero);
                else System.out.println(hero.hero().getName() + " is down and will respawn next round.");
            }
            if (!running || game.isGameOver()) break;

            System.out.println("\n--- Monsters' turn ---");
            game.monsterTurn();
            if (game.isGameOver()) break;
            game.completeRound();
        }

        if (!running) System.out.println("Valor match ended by the player. Returning to the main menu.");
        else if (game.outcome() == ValorGame.Outcome.HEROES_WIN)
            System.out.println("\nVICTORY! A hero reached the monsters' Nexus.");
        else if (game.outcome() == ValorGame.Outcome.MONSTERS_WIN)
            System.out.println("\nDEFEAT! A monster reached the heroes' Nexus.");
    }

    private Party createHeroes() {
        Party party = new Party();
        for (int i = 1; i <= 3; i++) {
            System.out.println("\nCreate hero " + i + " of 3");
            String name;
            do { name = input.readLine("Hero name: "); } while (name.isBlank());
            System.out.println("Choose a class: 1. Warrior  2. Sorcerer  3. Paladin");
            int type = input.readInt("Class: ", 1, 3);
            Hero hero = switch (type) {
                case 1 -> new Warrior(name, data.getHeroStats("warrior"));
                case 2 -> new Sorcerer(name, data.getHeroStats("sorcerer"));
                default -> new Paladin(name, data.getHeroStats("paladin"));
            };
            for (int potion = 0; potion < 5; potion++) {
                hero.getInventory().addItem(new Potion("Health Potion", 60, 1, 50, StatType.HP));
                hero.getInventory().addItem(new Potion("Mana Potion", 60, 1, 50, StatType.MP));
            }
            party.addHero(hero);
            System.out.println(hero.getName() + " the " + hero.getHeroClass() + " joins the team.");
        }
        return party;
    }

    private List<Lane> chooseLanes() {
        List<Lane> lanes = new ArrayList<>();
        EnumMap<Lane, Integer> counts = new EnumMap<>(Lane.class);
        Lane[] choices = Lane.values();
        for (int i = 1; i <= 3; i++) {
            while (true) {
                System.out.println("\nChoose a starting lane for hero " + i + ": 1. Top  2. Middle  3. Bottom");
                Lane selected = choices[input.readInt("Lane: ", 1, 3) - 1];
                if (counts.getOrDefault(selected, 0) >= 2) {
                    System.out.println("A lane has only two Nexus spaces; choose another lane.");
                    continue;
                }
                counts.merge(selected, 1, Integer::sum);
                lanes.add(selected);
                break;
            }
        }
        return lanes;
    }

    private void takeHeroTurn(ValorHeroUnit unit) {
        boolean complete = false;
        while (running && !game.isGameOver() && !complete && unit.hero().isAlive()) {
            System.out.println("\n" + unit.hero().getCombatSummary() + " | Lane " + unit.position().lane()
                    + " | Space " + coordinate(unit.position()));
            if (unit.holyLightRoundsRemaining() > 0)
                System.out.println("Holy Light: +50% damage for " + unit.holyLightRoundsRemaining() + " round(s).");
            System.out.println("1. Attack (Basic / Class special - " + ValorGame.CLASS_SPECIAL_MANA_COST
                    + " MP)   2. Cast spell   3. Use potion   4. Equip weapon/armor");
            System.out.println("5. Move   6. Teleport   7. Recall   8. Remove adjacent obstacle");
            System.out.println("9. Pass   10. View board/status" + (game.canUseMarket(unit) ? "   11. Market" : ""));
            int choice = input.readInt("Action (0 quits): ", 0, 11);
            switch (choice) {
                case 0 -> running = false;
                case 1 -> complete = attack(unit);
                case 2 -> complete = castSpell(unit);
                case 3 -> complete = usePotion(unit);
                case 4 -> complete = equip(unit);
                case 5 -> complete = move(unit);
                case 6 -> complete = teleport(unit);
                case 7 -> {
                    complete = game.recall(unit);
                    if (complete) System.out.println(unit.hero().getName() + " recalls to their original Nexus.");
                    else System.out.println("Recall is blocked by another hero at the spawn space.");
                }
                case 8 -> complete = removeObstacle(unit);
                case 9 -> { System.out.println(unit.hero().getName() + " passes."); complete = true; }
                case 10 -> { printBoard(); printStatus(); }
                case 11 -> {
                    if (!game.canUseMarket(unit)) System.out.println("Move to the center market row or heroes' Nexus to trade.");
                    else marketManager.enterForHero(market, unit.hero(), input);
                }
            }
        }
    }

    private boolean attack(ValorHeroUnit attacker) {
        System.out.println("1. Basic Attack");
        System.out.println("2. Class special attack (" + ValorGame.CLASS_SPECIAL_MANA_COST + " MP)");
        int type = input.readInt("Attack type (0 cancels): ", 0, 2);
        if (type == 0) return false;
        if (type == 2) return specialAttack(attacker);
        List<ValorMonsterUnit> targets = game.monstersInRange(attacker);
        if (targets.isEmpty()) { System.out.println("No monster is in attack range (same space or one orthogonal step)."); return false; }
        System.out.println("Monsters in range:");
        for (int i = 0; i < targets.size(); i++) printMonsterChoice(i + 1, targets.get(i));
        ValorMonsterUnit target = targets.get(input.readInt("Target: ", 1, targets.size()) - 1);
        return game.attack(attacker, target);
    }

    private boolean specialAttack(ValorHeroUnit attacker) {
        String heroClass = attacker.hero().getHeroClass();
        if (heroClass.equalsIgnoreCase("Warrior")) {
            List<ValorMonsterUnit> targets = game.monstersInRange(attacker);
            if (targets.isEmpty()) { System.out.println("Spin Attack needs at least one monster in normal attack range."); return false; }
            if (!attacker.hero().hasEnoughMana(ValorGame.CLASS_SPECIAL_MANA_COST)) {
                printInsufficientSpecialMana(attacker);
                return false;
            }
            System.out.println("Spin Attack will hit every monster in range (" + targets.size() + ").");
            return game.spinAttack(attacker);
        }
        if (heroClass.equalsIgnoreCase("Sorcerer")) {
            List<ValorMonsterUnit> targets = game.fireHelperTargets(attacker);
            if (targets.isEmpty()) { System.out.println("Fire Helper has no target in another lane within rows "
                    + (attacker.position().row() - 1) + " to " + (attacker.position().row() + 1) + "."); return false; }
            if (!attacker.hero().hasEnoughMana(ValorGame.CLASS_SPECIAL_MANA_COST)) {
                printInsufficientSpecialMana(attacker);
                return false;
            }
            System.out.println("Fire Helper targets in other lanes:");
            for (int i = 0; i < targets.size(); i++) printMonsterChoice(i + 1, targets.get(i));
            ValorMonsterUnit target = targets.get(input.readInt("Target: ", 1, targets.size()) - 1);
            return game.fireHelperAttack(attacker, target);
        }
        if (heroClass.equalsIgnoreCase("Paladin")) {
            if (!attacker.hero().hasEnoughMana(ValorGame.CLASS_SPECIAL_MANA_COST)) {
                printInsufficientSpecialMana(attacker);
                return false;
            }
            boolean activated = game.activateHolyLight(attacker);
            if (activated) System.out.println("Holy Light activates (-" + ValorGame.CLASS_SPECIAL_MANA_COST
                    + " MP): damage is increased by 50% for two full rounds.");
            return activated;
        }
        System.out.println("This hero has no class special attack.");
        return false;
    }

    private static void printInsufficientSpecialMana(ValorHeroUnit unit) {
        System.out.printf("%s needs %d MP for the class special, but has only %.0f MP.%n",
                unit.hero().getName(), ValorGame.CLASS_SPECIAL_MANA_COST, unit.hero().getMP());
    }

    private boolean castSpell(ValorHeroUnit caster) {
        List<Spell> spells = caster.hero().getInventory().getItems().stream()
                .filter(Spell.class::isInstance).map(Spell.class::cast).filter(spell -> !spell.isConsumed()).toList();
        if (spells.isEmpty()) { System.out.println("No spells are available."); return false; }
        for (int i = 0; i < spells.size(); i++) System.out.println((i + 1) + ". " + spells.get(i));
        Spell spell = spells.get(input.readInt("Spell: ", 1, spells.size()) - 1);
        if (!caster.hero().hasEnoughMana(spell.getManaCost())) {
            System.out.println("Not enough mana for that spell.");
            return false;
        }
        List<ValorMonsterUnit> targets = game.monstersInRange(caster);
        if (targets.isEmpty()) { System.out.println("No monster is in spell range."); return false; }
        for (int i = 0; i < targets.size(); i++) printMonsterChoice(i + 1, targets.get(i));
        ValorMonsterUnit target = targets.get(input.readInt("Target: ", 1, targets.size()) - 1);
        return game.castSpell(caster, target, spell);
    }

    private boolean usePotion(ValorHeroUnit unit) {
        List<Potion> potions = unit.hero().getInventory().getItems().stream()
                .filter(Potion.class::isInstance).map(Potion.class::cast).filter(potion -> !potion.isConsumed()).toList();
        if (potions.isEmpty()) { System.out.println("No potions are available."); return false; }
        for (int i = 0; i < potions.size(); i++) System.out.println((i + 1) + ". " + potions.get(i));
        Potion potion = potions.get(input.readInt("Potion: ", 1, potions.size()) - 1);
        return game.usePotion(unit, potion);
    }

    private boolean equip(ValorHeroUnit unit) {
        List<Item> equipment = unit.hero().getInventory().getItems().stream()
                .filter(item -> item instanceof Weapon || item instanceof Armor).toList();
        if (equipment.isEmpty()) { System.out.println("No weapon or armor is available to equip."); return false; }
        for (int i = 0; i < equipment.size(); i++) System.out.println((i + 1) + ". " + equipment.get(i));
        Item item = equipment.get(input.readInt("Equipment: ", 1, equipment.size()) - 1);
        boolean bothHands = item instanceof Weapon weapon && weapon.getHandsRequired() == 2;
        if (item instanceof Weapon weapon && weapon.getHandsRequired() == 1)
            bothHands = input.readYesNo("Use both hands for increased damage?");
        boolean equipped = game.equip(unit, item, bothHands);
        if (equipped) System.out.println(unit.hero().getName() + " equips " + item.getName() + ".");
        return equipped;
    }

    private boolean move(ValorHeroUnit unit) {
        System.out.println("1. North   2. West   3. South   4. East");
        int direction = input.readInt("Direction: ", 1, 4);
        int row = direction == 1 ? -1 : direction == 3 ? 1 : 0;
        int column = direction == 2 ? -1 : direction == 4 ? 1 : 0;
        if (!game.moveHero(unit, row, column)) {
            System.out.println("That move is blocked by a wall, obstacle, hero, or monster.");
            return false;
        }
        System.out.println(unit.hero().getName() + " moves to " + coordinate(unit.position()) + ".");
        if (game.isGameOver()) System.out.println("A hero has reached the monsters' Nexus!");
        return true;
    }

    private boolean teleport(ValorHeroUnit unit) {
        List<ValorHeroUnit> targets = game.heroes().stream()
                .filter(other -> other != unit && other.hero().isAlive())
                .filter(other -> other.position().lane() != unit.position().lane()).toList();
        if (targets.isEmpty()) { System.out.println("No living teammate is in another lane."); return false; }
        for (int i = 0; i < targets.size(); i++) {
            ValorHeroUnit target = targets.get(i);
            System.out.printf("%d. H%d %s (%s, %s)%n", i + 1, target.number(), target.hero().getName(),
                    target.position().lane(), coordinate(target.position()));
        }
        ValorHeroUnit target = targets.get(input.readInt("Teleport beside: ", 1, targets.size()) - 1);
        List<ValorPosition> destinations = game.teleportDestinations(unit, target);
        if (destinations.isEmpty()) { System.out.println("There is no legal space beside that hero."); return false; }
        for (int i = 0; i < destinations.size(); i++)
            System.out.println((i + 1) + ". " + target.position().lane() + " " + coordinate(destinations.get(i)));
        ValorPosition destination = destinations.get(input.readInt("Destination: ", 1, destinations.size()) - 1);
        boolean moved = game.teleport(unit, target, destination);
        if (moved) System.out.println(unit.hero().getName() + " teleports to " + coordinate(destination) + ".");
        return moved;
    }

    private boolean removeObstacle(ValorHeroUnit unit) {
        List<ValorPosition> obstacles = adjacent(unit.position()).stream()
                .filter(position -> game.board().isOnBoard(position))
                .filter(position -> game.board().terrainAt(position) == Terrain.OBSTACLE).toList();
        if (obstacles.isEmpty()) { System.out.println("There is no adjacent obstacle to remove."); return false; }
        for (int i = 0; i < obstacles.size(); i++) System.out.println((i + 1) + ". " + coordinate(obstacles.get(i)));
        ValorPosition target = obstacles.get(input.readInt("Obstacle: ", 1, obstacles.size()) - 1);
        boolean removed = game.removeObstacle(unit, target);
        if (removed) System.out.println("The obstacle becomes plain terrain.");
        return removed;
    }

    private void printBoard() {
        System.out.println("\nBoard: N Nexus | $ market | I wall | O obstacle | P plain | B bush | C cave | K koulou");
        System.out.println("       H# hero, M# monster (both can share a cell)");
        for (int row = 0; row < ValorBoard.ROWS; row++) {
            System.out.printf("%d  ", row);
            for (int column = 0; column < ValorBoard.COLUMNS; column++) {
                if (column == 2 || column == 5) System.out.print(" || ");
                ValorPosition position = new ValorPosition(row, column);
                if (column == 2 || column == 5) {
                    System.out.print("I  ");
                    continue;
                }
                String hero = game.heroes().stream().filter(unit -> unit.hero().isAlive() && unit.position().equals(position))
                        .map(unit -> "H" + unit.number()).findFirst().orElse("--");
                String monster = game.monsters().stream().filter(unit -> unit.monster().isAlive() && unit.position().equals(position))
                        .map(unit -> "M" + unit.number()).findFirst().orElse("--");
                System.out.printf("%c:%-2s/%-2s ", game.board().terrainAt(position).symbol(), hero, monster);
            }
            System.out.println();
        }
    }

    private void printStatus() {
        System.out.println("\nHeroes:");
        for (ValorHeroUnit hero : game.heroes())
            System.out.printf("H%d %s | %s | %s | %s%n", hero.number(), hero.hero().getCombatSummary(),
                    hero.position().lane(), coordinate(hero.position()), hero.hero().isAlive() ? "alive" : "respawning");
        System.out.println("Monsters:");
        for (ValorMonsterUnit monster : game.monsters())
            System.out.printf("M%d %s | %s | %s%n", monster.number(), monster.monster().getCombatSummary(),
                    monster.position().lane(), coordinate(monster.position()));
    }

    private void printMonsterChoice(int number, ValorMonsterUnit unit) {
        System.out.printf("%d. M%d %s | %s | %s%n", number, unit.number(), unit.monster().getCombatSummary(),
                unit.position().lane(), coordinate(unit.position()));
    }

    private static List<ValorPosition> adjacent(ValorPosition position) {
        return List.of(new ValorPosition(position.row() - 1, position.column()),
                new ValorPosition(position.row(), position.column() - 1),
                new ValorPosition(position.row() + 1, position.column()),
                new ValorPosition(position.row(), position.column() + 1));
    }

    private static String coordinate(ValorPosition position) {
        return "(" + position.row() + "," + position.column() + ")";
    }

    private void printInstructions() {
        System.out.println("\nThree heroes take one action each, then each monster attacks or advances south.");
        System.out.println("Attack and spell range is the same cell plus orthogonal neighbors; no diagonal attacks.");
        System.out.println("Attack offers Basic Attack or a class special (20 MP). Spin hits every enemy in range; Fire Helper reaches another lane within 1 row.");
        System.out.println("Holy Light boosts Paladin damage by 50% for two rounds.");
        System.out.println("Movement and obstacle removal each use an action. Market trades from the center row or your Nexus do not.");
        System.out.println("Every round, survivors restore 10% HP/MP and fallen heroes respawn at their own Nexus.");
        System.out.println("Reach row 0 to win. Stop a monster before it reaches row " + (ValorBoard.ROWS - 1) + ".");
    }
}
