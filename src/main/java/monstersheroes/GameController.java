package monstersheroes;

import monstersheroes.battle.Battle;
import monstersheroes.battle.MonsterFactory;
import monstersheroes.characters.*;
import monstersheroes.data.GameData;
import monstersheroes.data.GameDataLoader;
import monstersheroes.items.*;
import monstersheroes.market.MarketManager;
import monstersheroes.party.Party;
import monstersheroes.util.InputHandler;
import monstersheroes.util.AsciiArt;
import monstersheroes.world.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/** Runs the game, handles the menus, and connects the different parts of the adventure. */
public class GameController {
    private final InputHandler input;
    private final GameData data;
    private final Random random = new Random();
    private final Party party = new Party();
    private final MarketManager marketManager = new MarketManager();
    private World world;
    private boolean running = true;
    private boolean gameWon;
    private int depth;
    private final boolean[] bossChallenged = new boolean[3];
    private final boolean[] bossDefeated = new boolean[3];

    public GameController(InputHandler input, GameData data) {
        this.input = input; this.data = data;
    }

    public void start() {
        printTitle();
        createParty();
        world = new World(8, data, random, depth);
        System.out.println("\nYour party enters the world. Good luck, heroes!");
        world.printMap();
        printHelp();

        while (running && party.hasLivingHeroes()) {
            char command = input.readCommand("\nCommand (W/A/S/D, I, V, E, P, M, R, T, H, Q): ");
            switch (command) {
                case 'W' -> move(0, -1);
                case 'A' -> move(-1, 0);
                case 'S' -> move(0, 1);
                case 'D' -> move(1, 0);
                case 'I' -> showPartyInfo();
                case 'V' -> showInventories();
                case 'E' -> equipOutsideBattle();
                case 'P' -> usePotionOutsideBattle();
                case 'M' -> enterMarket();
                case 'R' -> interactWithPortal();
                case 'T' -> world.printMap();
                case 'H' -> printHelp();
                case 'Q' -> running = false;
                default -> System.out.println("Unknown command. Press H to see the instructions.");
            }
        }
        if (gameWon) System.out.println("\nCHAMPIONS! You defeated Chrysophylax on Depth 2 and won the game!");
        else if (!party.hasLivingHeroes()) AsciiArt.printDefeat();
        else System.out.println("Thanks for playing Legends: Monsters and Heroes!");
    }

    private void createParty() {
        int count = input.readInt("How many heroes will join your party? (1-3): ", 1, Party.MAX_SIZE);
        for (int i = 1; i <= count; i++) {
            System.out.println("\nCreate hero " + i + " of " + count);
            String name;
            do { name = input.readLine("Hero name: "); } while (name.isBlank());
            System.out.println("Choose a class: 1. Warrior  2. Sorcerer  3. Paladin");
            int type = input.readInt("Class: ", 1, 3);
            Hero hero = switch (type) {
                case 1 -> new Warrior(name, data.getHeroStats("warrior"));
                case 2 -> new Sorcerer(name, data.getHeroStats("sorcerer"));
                default -> new Paladin(name, data.getHeroStats("paladin"));
            };
            // Give every hero five health and five mana potions.
            for (int p = 0; p < 5; p++) {
                hero.getInventory().addItem(
                        new Potion("Health Potion", 60, 1, 50, StatType.HP)
                );

                hero.getInventory().addItem(
                        new Potion("Mana Potion", 60, 1, 50, StatType.MP)
                );
            }

            party.addHero(hero);
            System.out.println(hero.getName() + " the " + hero.getHeroClass() + " joins the party! Starting gold: " + hero.getGold());
        }
    }

    private void move(int dx, int dy) {
        if (!world.move(dx, dy)) {
            // Keep the current position visible even when a movement is blocked.
            world.printMap();
            return;
        }

        // Redraw immediately after every successful move, before resolving the tile.
        world.printMap();
        Tile tile = world.getCurrentTile();
        if (tile instanceof CommonTile) {
            System.out.println("You are in a common area. Scouting for monsters...");
            if (random.nextInt(100) < 25) {
                System.out.println("An encounter! Monsters are approaching!");
                Battle battle = new Battle(party, new MonsterFactory(data, random).createFor(party, depth), input, random);
                if (!battle.run()) running = false;
            } else {
                System.out.println("The area is quiet. No monsters appeared this time.");
            }
        } else if (tile instanceof MarketTile) {
            System.out.println("A market is here. Press M to trade.");
        } else if (tile instanceof PortalTile) {
            interactWithPortal();
        }
    }

    private void interactWithPortal() {
        if (!(world.getCurrentTile() instanceof PortalTile)) {
            System.out.println("The party must stand on a portal tile to use it.");
            return;
        }
        while (running && party.hasLivingHeroes()) {
            System.out.println("\n========== DEPTH " + depth + " PORTAL ==========");
            if (bossChallenged[depth]) {
                System.out.println(bossDefeated[depth] ? "The floor boss has been defeated." : "The floor boss has already been challenged.");
            } else System.out.println("1. Challenge this depth's boss");
            System.out.println("2. Explore this depth");
            if (bossDefeated[depth] && depth < 2) System.out.println("3. Descend to depth " + (depth + 1) + " (one way)");
            if (depth == 2 && bossDefeated[depth]) return;
            int choice = input.readInt("Choose: ", 1, 3);
            if (choice == 1 && !bossChallenged[depth]) {
                bossChallenged[depth] = true;
                Monster boss = new MonsterFactory(data, random).createBoss(party, depth);
                boolean victory = new Battle(party, List.of(boss), input, random).run();
                if (!party.hasLivingHeroes()) { running = false; return; }
                if (!victory) { running = false; return; }
                if (victory) {
                    bossDefeated[depth] = true;
                    if (depth == 2) { gameWon = true; running = false; return; }
                }
                continue;
            }
            if (choice == 2) return;
            if (choice == 3 && bossDefeated[depth] && depth < 2) {
                depth++;
                world = new World(8, data, random, depth);
                System.out.println("The portal seals behind you. The party cannot return to earlier depths.");
                world.printMap();
                return;
            }
            System.out.println("That portal choice is not available yet.");
        }
    }

    private void enterMarket() {
        if (!(world.getCurrentTile() instanceof MarketTile marketTile)) {
            System.out.println("You can only enter a market when the party is standing on a market tile.");
            return;
        }
        AsciiArt.printMarket();
        marketManager.enter(marketTile.getMarket(), party, input);
    }

    private Hero chooseHero() {
        for (int i = 0; i < party.getHeroes().size(); i++)
            System.out.println((i + 1) + ". " + party.getHeroes().get(i).getCombatSummary());
        int choice = input.readInt("Choose a hero (0 to cancel): ", 0, party.getHeroes().size());
        return choice == 0 ? null : party.getHeroes().get(choice - 1);
    }

    private void showPartyInfo() {
        System.out.println("\n========== PARTY STATUS ==========");
        party.getHeroes().forEach(Hero::showInfo);
    }

    private void showInventories() {
        for (Hero hero : party.getHeroes()) {
            System.out.println("\nInventory — " + hero.getName() + ":");
            if (hero.getInventory().isEmpty()) System.out.println("  (empty)");
            else hero.getInventory().getItems().forEach(item -> System.out.println("  " + item));
        }
    }

    private void equipOutsideBattle() {
        Hero hero = chooseHero();
        if (hero == null) return;
        List<Item> equipment = hero.getInventory().getItems().stream()
                .filter(item -> item instanceof Weapon || item instanceof Armor).collect(Collectors.toList());
        if (equipment.isEmpty()) { System.out.println("This hero has no equipment in their inventory."); return; }
        for (int i = 0; i < equipment.size(); i++) System.out.println((i + 1) + ". " + equipment.get(i));
        int choice = input.readInt("Choose equipment (0 to cancel): ", 0, equipment.size());
        if (choice == 0) return;
        Item item = equipment.get(choice - 1);
        try {
            if (item instanceof Weapon weapon) {
                boolean bothHands = weapon.getHandsRequired() == 2;
                if (weapon.getHandsRequired() == 1)
                    bothHands = input.readYesNo("Use both hands for increased weapon damage?");
                hero.equipWeapon(weapon, bothHands);
            } else if (item instanceof Armor armor) hero.equipArmor(armor);
            System.out.println(hero.getName() + " equipped " + item.getName() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void usePotionOutsideBattle() {
        Hero hero = chooseHero();
        if (hero == null) return;

        List<Potion> potions = hero.getInventory().getItems().stream()
                .filter(Potion.class::isInstance)
                .map(Potion.class::cast)
                .filter(p -> !p.isConsumed())
                .toList();

        if (potions.isEmpty()) {
            System.out.println("This hero has no potions.");
            return;
        }

        for (int i = 0; i < potions.size(); i++) {
            System.out.println((i + 1) + ". " + potions.get(i));
        }

        int choice = input.readInt(
                "Choose potion (0 to cancel): ", 0, potions.size()
        );

        if (choice == 0) return;

        Potion potion = potions.get(choice - 1);

        if (!hero.getInventory().contains(potion) || potion.isConsumed()) {
            System.out.println("That potion is no longer available.");
            return;
        }

        int restored = potion.getActualEffectAmount(hero);

        potion.applyTo(hero);
        hero.getInventory().removeItem(potion);

        System.out.printf(
                "%s uses %s. %s increases by %d.%n",
                hero.getName(),
                potion.getName(),
                potion.getStatType(),
                restored
        );
    }

    private void printTitle() {
        AsciiArt.printTitle();
    }

    private void printHelp() {
        System.out.println("\nHOW TO PLAY");
        System.out.println("W/A/S/D : Move up/left/down/right");
        System.out.println("I       : Show party statistics");
        System.out.println("V       : View hero inventories");
        System.out.println("E       : Equip a weapon or armor outside battle");
        System.out.println("P       : Use a potion outside battle");
        System.out.println("M       : Enter the market (only on a market tile)");
        System.out.println("R       : Use a portal (also opens automatically when you step onto one)");
        System.out.println("T       : Display the world map");
        System.out.println("H       : Show these instructions");
        System.out.println("Q       : Quit the game");
        System.out.println("Portal bosses are singular and can be challenged once per depth. Defeat each boss before descending.");
        System.out.println("In battle, each living hero chooses an action. Option 5 opens that hero class's unique abilities.");
        System.out.println("Sorcerers can use Healing Light to restore 10 HP to one living party member (including themselves).");
        System.out.println("When a monster attacks, guess its hidden number from 1 to 7. A correct guess guarantees a dodge;");
        System.out.println("if incorrect, the hero still has their normal agility-based dodge chance.");
        System.out.println("Hero and monster attacks have a 15% critical-hit chance for double damage.");
        System.out.println("Critical hits, monsters, dodges, markets, and the title screen use ASCII art.");
    }
}
