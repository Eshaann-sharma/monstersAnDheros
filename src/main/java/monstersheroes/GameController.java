package monstersheroes;

import monstersheroes.characters.*;
import monstersheroes.data.GameData;
import monstersheroes.items.*;
import monstersheroes.market.Market;
import monstersheroes.market.MarketManager;
import monstersheroes.party.Party;
import monstersheroes.util.InputHandler;
import monstersheroes.util.AsciiArt;
import monstersheroes.world.*;
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
    // store the active hero as a zero-based party index
    private int activeHero;
    private final Set<Integer> heroesWhoActed = new HashSet<>();
    private Market nexusMarket;

    public GameController(InputHandler input, GameData data) {
        this.input = input; this.data = data;
    }

    public void start() {
        printTitle();
        createParty();
        // assign starting positions and share one market across the heroes
        world = new World(random, chooseStartingLanes());
        nexusMarket = new Market(data, random, new HashSet<>());
        System.out.println("\nYour party enters the world. Good luck, heroes!");
        world.printMap();
        printHelp();

        while (running && party.hasLivingHeroes()) {
            char command = input.readCommand("\nH" + (activeHero + 1) + " (W/A/S/D, C, X, F, I, V, E, P, M, T, H, Q): ");
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
                case 'C' -> clearObstacle();
                case 'X' -> finishBoardAction();
                case 'F' -> selectActiveHero();
                case 'T' -> world.printMap();
                case 'H' -> printHelp();
                case 'Q' -> running = false;
                default -> System.out.println("Unknown command. Press H to see the instructions.");
            }
        }
        // report victory after the active hero reaches the enemy nexus
        if (gameWon) System.out.println("\nVictory! A hero reached the monsters' Nexus.");
        else if (!party.hasLivingHeroes()) AsciiArt.printDefeat();
        else System.out.println("Thanks for playing Legends of Valor!");
    }

    private void createParty() {
        // create one hero for each starting lane
        int count = Party.MAX_SIZE;
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

    // assigns a distinct starting lane to each hero in party order
    private List<Integer> chooseStartingLanes() {
        List<Integer> lanes = new ArrayList<>();
        for (int hero = 1; hero <= Party.MAX_SIZE; hero++) {
            while (true) {
                int lane = input.readInt("Starting lane for H" + hero + " (1=left, 2=middle, 3=right): ",
                        1, World.LANE_COUNT) - 1;
                if (!lanes.contains(lane)) {
                    lanes.add(lane);
                    break;
                }
                System.out.println("Another hero is already assigned to that lane.");
            }
        }
        return lanes;
    }

    // moves the active hero and checks whether the move wins the game
    private void move(int dx, int dy) {
        if (!world.moveHero(activeHero, dx, dy)) return;
        Tile tile = world.getHeroTile(activeHero);
        Hero hero = party.getHeroes().get(activeHero);
        Hero.Skill boost = tile.getBoostedSkill();
        hero.setTerrainBoost(boost);
        world.printMap();
        if (boost != null) {
            System.out.printf("%s gains +10%% %s while on %s.%n",
                    hero.getName(), boost.name().toLowerCase(Locale.ROOT), tile.getDescription());
        }
        if (tile instanceof NexusTile nexus && !nexus.isHeroNexus()) {
            gameWon = true;
            running = false;
            return;
        }
        finishBoardAction();
    }

    // searches only the selected neighboring space for an obstacle
    private void clearObstacle() {
        char direction = input.readCommand("Clear in which direction (W/A/S/D, X to cancel): ");
        boolean cleared = switch (direction) {
            case 'W' -> world.clearObstacle(activeHero, 0, -1);
            case 'A' -> world.clearObstacle(activeHero, -1, 0);
            case 'S' -> world.clearObstacle(activeHero, 0, 1);
            case 'D' -> world.clearObstacle(activeHero, 1, 0);
            default -> false;
        };
        if (direction == 'X') return;
        if (!cleared) {
            System.out.println("Choose a direction with an adjacent obstacle.");
            return;
        }
        System.out.println("Obstacle cleared. The space is now plain.");
        world.printMap();
        finishBoardAction();
    }

    // advances to the next hero who has a board action left this round
    private void finishBoardAction() {
        heroesWhoActed.add(activeHero);
        if (heroesWhoActed.size() == party.getHeroes().size()) {
            heroesWhoActed.clear();
        }
        do {
            activeHero = (activeHero + 1) % party.getHeroes().size();
        } while (heroesWhoActed.contains(activeHero));
    }

    // allows switching to a hero who still has a board action
    private void selectActiveHero() {
        int selected = input.readInt("Control hero (1-3): ", 1, Party.MAX_SIZE) - 1;
        if (heroesWhoActed.contains(selected)) {
            System.out.println("That hero already used their board action this round.");
            return;
        }
        activeHero = selected;
    }

    // opens the market for the active hero while at the heroes' nexus
    private void enterMarket() {
        if (!(world.getHeroTile(activeHero) instanceof NexusTile nexus) || !nexus.isHeroNexus()) {
            System.out.println("The active hero must be at the heroes' Nexus to trade.");
            return;
        }
        AsciiArt.printMarket();
        marketManager.tradeWithHero(nexusMarket, party.getHeroes().get(activeHero), input);
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

    // lists the current board controls and inventory commands
    private void printHelp() {
        System.out.println("\nHOW TO PLAY");
        System.out.println("W/A/S/D : Move the active hero up/left/down/right");
        System.out.println("C       : Clear an adjacent obstacle using this hero's board action");
        System.out.println("X       : Pass this hero's board action");
        System.out.println("F       : Select a hero who has a board action left");
        System.out.println("Bush +10% DEX, Cave +10% AGI, Koulou +10% STR while occupied");
        System.out.println("I       : Show party statistics");
        System.out.println("V       : View hero inventories");
        System.out.println("E       : Equip a weapon or armor");
        System.out.println("P       : Use a potion");
        System.out.println("M       : Trade at the heroes' Nexus");
        System.out.println("T       : Display the board");
        System.out.println("H       : Show these instructions");
        System.out.println("Q       : Quit the game");
    }
}
