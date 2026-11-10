package monstersheroes.market;

import monstersheroes.characters.Hero;
import monstersheroes.items.Item;
import monstersheroes.party.Party;
import monstersheroes.util.InputHandler;
import java.util.List;

/** Shows the market options and handles buying, selling, and repairing items. */
public class MarketManager {
    public void enter(Market market, Party party, InputHandler input) {
        while (true) {
            System.out.println("\n========== MARKET ==========");
            System.out.println("Each hero has an individual wallet and inventory.");
            for (int i = 0; i < party.getHeroes().size(); i++) {
                Hero h = party.getHeroes().get(i);
                System.out.printf("%d. %s (Gold: %d)%n", i + 1, h.getName(), h.getGold());
            }
            System.out.println("0. Leave market");
            int choice = input.readInt("Choose a hero: ", 0, party.getHeroes().size());
            if (choice == 0) return;
            tradeWithHero(market, party.getHeroes().get(choice - 1), input);
        }
    }

    private void tradeWithHero(Market market, Hero hero, InputHandler input) {
        boolean done = false;
        while (!done) {
            System.out.printf("\n--- %s's Market Visit (Gold: %d) ---%n", hero.getName(), hero.getGold());
            System.out.println("1. Buy   2. Sell   3. Repair broken equipment   4. View inventory   0. Back");
            int action = input.readInt("Choose: ", 0, 4);
            switch (action) {
                case 0 -> done = true;
                case 1 -> buy(market, hero, input);
                case 2 -> sell(market, hero, input);
                case 3 -> repair(hero, input);
                case 4 -> printInventory(hero);
            }
        }
    }

    private void buy(Market market, Hero hero, InputHandler input) {
        List<Item> stock = market.getStock();
        if (stock.isEmpty()) { System.out.println("This market is out of stock."); return; }
        System.out.println("Items for sale:");
        for (int i = 0; i < stock.size(); i++) System.out.println((i + 1) + ". " + stock.get(i));
        int choice = input.readInt("Item to buy (0 to cancel): ", 0, stock.size());
        if (choice == 0) return;
        Item item = stock.get(choice - 1);
        if (item.getLevel() > hero.getLevel()) { System.out.println("Your level is too low for this item."); return; }
        if (item instanceof monstersheroes.items.Weapon weapon && !weapon.canBeUsedBy(hero)) {
            System.out.println("This weapon is restricted to: " + weapon.getAllowedClassesDescription() + ".");
            return;
        }
        if (!hero.spendGold(item.getPrice())) { System.out.println("Not enough gold."); return; }
        market.removeItem(item);
        hero.getInventory().addItem(item);
        System.out.println("Purchased " + item.getName() + ".");
    }

    private void sell(Market market, Hero hero, InputHandler input) {
        List<Item> items = hero.getInventory().getItems();
        if (items.isEmpty()) { System.out.println("Your inventory is empty."); return; }
        System.out.println("Your items:");
        for (int i = 0; i < items.size(); i++) System.out.println((i + 1) + ". " + items.get(i));
        int choice = input.readInt("Item to sell (0 to cancel): ", 0, items.size());
        if (choice == 0) return;
        Item item = items.get(choice - 1);
        hero.unequip(item);
        hero.getInventory().removeItem(item);
        int salePrice = item.getPrice() / 2;
        hero.addGold(salePrice);
        market.addItem(item);
        System.out.printf("Sold %s for %d gold (half its purchase price).%n", item.getName(), salePrice);
    }

    private void repair(Hero hero, InputHandler input) {
        List<Item> broken = hero.getInventory().getItems().stream()
                .filter(item -> item.getRemainingUses() == 0 && (item instanceof monstersheroes.items.Weapon || item instanceof monstersheroes.items.Armor))
                .toList();
        if (broken.isEmpty()) { System.out.println("You have no broken weapons or armor to repair."); return; }
        System.out.println("Broken equipment:");
        for (int i = 0; i < broken.size(); i++) System.out.println((i + 1) + ". " + broken.get(i));
        int choice = input.readInt("Equipment to repair (0 to cancel): ", 0, broken.size());
        if (choice == 0) return;
        Item item = broken.get(choice - 1);
        int cost = item.getPrice() / 2;
        if (!hero.spendGold(cost)) { System.out.println("Not enough gold to repair this item. Cost: " + cost); return; }
        item.repair();
        System.out.println(item.getName() + " repaired for " + cost + " gold.");
    }

    private void printInventory(Hero hero) {
        System.out.println("\nInventory of " + hero.getName() + ":");
        if (hero.getInventory().isEmpty()) System.out.println("(empty)");
        else hero.getInventory().getItems().forEach(System.out::println);
    }
}
