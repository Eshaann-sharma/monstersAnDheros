package monstersheroes;

import monstersheroes.battle.MonsterFactory;
import monstersheroes.characters.*;
import monstersheroes.data.GameData;
import monstersheroes.data.GameDataLoader;
import monstersheroes.items.Weapon;
import monstersheroes.market.Market;
import monstersheroes.market.MarketManager;
import monstersheroes.party.Party;
import monstersheroes.util.InputHandler;
import monstersheroes.world.PortalTile;
import monstersheroes.world.World;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

/** Dependency-free feature checks. Run with assertions enabled from the project root. */
public final class FeatureSmokeTest {
    public static void main(String[] args) {
        GameData data = new GameDataLoader().load(Path.of("data"));
        checkWeaponClassRestrictions(data);
        checkPortalAndDepthRosters(data);
        checkPortalMenuAndDescentGate(data);
        System.out.println("FeatureSmokeTest: all checks passed.");
    }

    private static void checkWeaponClassRestrictions(GameData data) {
        Weapon staff = (Weapon) data.getItemDefinitions().stream()
                .filter(item -> item.name.equals("Magic Staff")).findFirst().orElseThrow().createItem();
        Hero sorcerer = new Sorcerer("Sage", data.getHeroStats("sorcerer"));
        Hero warrior = new Warrior("Fighter", data.getHeroStats("warrior"));
        sorcerer.equipWeapon(staff, true);
        boolean equipBlocked = false;
        try { warrior.equipWeapon(staff, true); }
        catch (IllegalArgumentException expected) { equipBlocked = true; }
        assert equipBlocked : "Warriors must not equip a Magic Staff";

        Market market = new Market(data, new Random(4), new java.util.HashSet<>());
        market.getStock().clear();
        market.addItem(staff);
        warrior.addGold(1000);
        System.setIn(new ByteArrayInputStream("1\n1\n1\n0\n0\n".getBytes(StandardCharsets.UTF_8)));
        new MarketManager().enter(market, partyOf(warrior), new InputHandler());
        assert market.getStock().contains(staff) : "Restricted weapon should remain in market stock";
        assert !warrior.getInventory().contains(staff) : "Warrior must not purchase a Magic Staff";
        assert warrior.getGold() == 1500 : "Rejected purchase must not charge the hero";

        Weapon purchaseStaff = (Weapon) data.getItemDefinitions().stream()
                .filter(item -> item.name.equals("Magic Staff")).findFirst().orElseThrow().createItem();
        Hero buyer = new Sorcerer("Buyer", data.getHeroStats("sorcerer"));
        Market sorcererMarket = new Market(data, new Random(5), new java.util.HashSet<>());
        sorcererMarket.getStock().clear();
        sorcererMarket.addItem(purchaseStaff);
        System.setIn(new ByteArrayInputStream("1\n1\n1\n0\n0\n".getBytes(StandardCharsets.UTF_8)));
        new MarketManager().enter(sorcererMarket, partyOf(buyer), new InputHandler());
        assert buyer.getInventory().contains(purchaseStaff) : "Sorcerer should be allowed to purchase a Magic Staff";
        assert buyer.getGold() == 200 : "Successful purchase should charge the listed price";
    }

    private static void checkPortalMenuAndDescentGate(GameData data) {
        System.setIn(new ByteArrayInputStream("1\nPortalTester\n1\nD\n3\n2\nQ\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
            new GameController(new InputHandler(), data).start();
        } finally {
            System.setOut(original);
        }
        String output = captured.toString(StandardCharsets.UTF_8);
        assert output.contains("DEPTH 0 PORTAL") : "Stepping onto the portal should open its menu";
        assert output.contains("That portal choice is not available yet.") : "Descent should be locked until the boss is defeated";
    }

    private static void checkPortalAndDepthRosters(GameData data) {
        String[][] expected = {
                {"Iron Exoskeleton", "Undead Zombie", "Skull Eater"},
                {"Ghost Face", "Wailing Spirit"},
                {"Ember Dragon", "Ice Dragon", "Old Dragon"}
        };
        Hero hero = new Warrior("Tester", data.getHeroStats("warrior"));
        Party party = partyOf(hero);
        MonsterFactory factory = new MonsterFactory(data, new Random(7));
        for (int depth = 0; depth < 3; depth++) {
            World world = new World(8, data, new Random(depth), depth);
            assert world.move(1, 0) : "Portal should be adjacent to starting point";
            assert world.getCurrentTile() instanceof PortalTile : "Expected reachable portal";
            List<Monster> monsters = factory.createFor(party, depth);
            assert monsters.size() == 1 : "Common encounter size must match party size";
            String name = monsters.get(0).getName();
            assert List.of(expected[depth]).stream().anyMatch(name::startsWith) : "Wrong roster: " + name;
            Monster boss = factory.createBoss(party, depth);
            assert boss.getMaxHP() > monsters.getFirst().getMaxHP() : "Boss needs higher HP";
            assert boss.getAttackDamage() > monsters.getFirst().getAttackDamage() : "Boss needs higher damage";
            assert factory.createBoss(party, depth) != null : "Boss factory should return one boss";
        }
    }

    private static Party partyOf(Hero hero) {
        Party party = new Party();
        party.addHero(hero);
        return party;
    }
}
