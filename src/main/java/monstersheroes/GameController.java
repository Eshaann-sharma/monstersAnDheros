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

    public GameController(InputHandler input, GameData data) {
        this.input = input; this.data = data;
    }

    public void start() {
        printTitle();
        createParty();
        world = new World(8, data, random);
        System.out.println("\nYour party enters the world. Good luck, heroes!");
        world.printMap();
        printHelp();

        while (running && party.hasLivingHeroes()) {
            char command = input.readCommand("\nCommand (W/A/S/D, I, V, E, P, M, T, H, Q): ");
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
                case 'T' -> world.printMap();
                case 'H' -> printHelp();
                case 'Q' -> running = false;
                default -> System.out.println("Unknown command. Press H to see the instructions.");
            }
        }
        if (!party.hasLivingHeroes()) AsciiArt.printDefeat();
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
                Battle battle = new Battle(party, new MonsterFactory(data, random).createFor(party), input, random);
                if (!battle.run()) running = false;
            } else {
                System.out.println("The area is quiet. No monsters appeared this time.");
            }
        } else if (tile instanceof MarketTile) {
            System.out.println("A market is here. Press M to trade.");
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
                .filter(Potion.class::isInstance).map(Potion.class::cast).filter(p -> !p.isConsumed()).toList();
        if (potions.isEmpty()) { System.out.println("This hero has no potions."); return; }
        for (int i = 0; i < potions.size(); i++) System.out.println((i + 1) + ". " + potions.get(i));
        int choice = input.readInt("Choose potion (0 to cancel): ", 0, potions.size());
        if (choice == 0) return;
        Potion potion = potions.get(choice - 1);
        potion.applyTo(hero);
        hero.getInventory().removeItem(potion);
        System.out.println(hero.getName() + " used " + potion.getName() + ".");
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
        System.out.println("T       : Display the world map");
        System.out.println("H       : Show these instructions");
        System.out.println("Q       : Quit the game");
        System.out.println("In battle, each living hero chooses an action. Option 5 opens that hero class's unique abilities.");
        System.out.println("Sorcerers can use Healing Light to restore 10 HP to one living party member (including themselves).");
        System.out.println("When a monster attacks, guess its hidden number from 1 to 7. A correct guess guarantees a dodge;");
        System.out.println("if incorrect, the hero still has their normal agility-based dodge chance.");
        System.out.println("Hero and monster attacks have a 15% critical-hit chance for double damage.");
        System.out.println("Critical hits, monsters, dodges, markets, and the title screen use ASCII art.");
    }
}
