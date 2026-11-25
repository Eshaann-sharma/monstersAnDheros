package monstersheroes.legendsofvalor;

import monstersheroes.battle.MonsterFactory;
import monstersheroes.characters.Hero;
import monstersheroes.characters.Monster;
import monstersheroes.data.GameData;
import monstersheroes.items.*;
import monstersheroes.party.Party;
import monstersheroes.util.CombatBalance;

import java.util.*;

/** Rules and state for Legends of Valor, isolated from the exploration game's turn system. */
public final class ValorGame {
    public enum Outcome { IN_PROGRESS, HEROES_WIN, MONSTERS_WIN }

    private static final int GOLD_PER_LEVEL = 100;
    private static final int XP_PER_MONSTER = 2;
    public static final int CLASS_SPECIAL_MANA_COST = 20;

    private final ValorBoard board;
    private final Party party;
    private final GameData data;
    private final Random random;
    private final int spawnInterval;
    private final List<ValorHeroUnit> heroes = new ArrayList<>();
    private final List<ValorMonsterUnit> monsters = new ArrayList<>();
    private final List<PendingSpawn> pendingSpawns = new ArrayList<>();
    private int nextMonsterNumber = 1;
    private int roundNumber = 1;
    private Outcome outcome = Outcome.IN_PROGRESS;

    public ValorGame(Party party, GameData data, List<Lane> startingLanes, Random random, int spawnInterval) {
        if (party == null || data == null || random == null || party.getHeroes().size() != 3
                || startingLanes == null || startingLanes.size() != 3 || spawnInterval < 1) {
            throw new IllegalArgumentException("Valor requires three heroes, three lanes, and a positive spawn interval.");
        }
        this.party = party;
        this.data = data;
        this.random = random;
        this.spawnInterval = spawnInterval;
        this.board = new ValorBoard(random);

        EnumMap<Lane, Integer> laneCounts = new EnumMap<>(Lane.class);
        int heroNumber = 1;
        for (int i = 0; i < party.getHeroes().size(); i++) {
            Hero hero = party.getHeroes().get(i);
            Lane lane = Objects.requireNonNull(startingLanes.get(i), "Hero lane cannot be null.");
            int laneSlot = laneCounts.merge(lane, 1, Integer::sum) - 1;
            if (laneSlot > 1) throw new IllegalArgumentException("Only two heroes can start in one lane.");
            int homeColumn = laneSlot == 0 ? lane.firstColumn() : lane.secondColumn();
            ValorHeroUnit unit = new ValorHeroUnit(heroNumber++, hero, new ValorPosition(ValorBoard.ROWS - 1, homeColumn));
            heroes.add(unit);
            applyTerrainBonus(unit);
        }

        List<Monster> initial = new MonsterFactory(data, random).createFor(party, 0);
        for (int i = 0; i < Lane.values().length; i++) {
            Lane lane = Lane.values()[i];
            addMonster(initial.get(i), new ValorPosition(0, lane.secondColumn()));
        }
    }

    public ValorBoard board() { return board; }
    public Party party() { return party; }
    public List<ValorHeroUnit> heroes() { return Collections.unmodifiableList(heroes); }
    public List<ValorMonsterUnit> monsters() { return Collections.unmodifiableList(monsters); }
    public int roundNumber() { return roundNumber; }
    public int spawnInterval() { return spawnInterval; }
    public Outcome outcome() { return outcome; }
    public boolean isGameOver() { return outcome != Outcome.IN_PROGRESS; }

    public List<ValorMonsterUnit> monstersInRange(ValorHeroUnit hero) {
        return monsters.stream().filter(unit -> unit.monster().isAlive())
                .filter(unit -> hero.position().distanceTo(unit.position()) <= 1).toList();
    }

    /** Mage fire reaches one monster in another lane, at the same row or one row away. */
    public List<ValorMonsterUnit> fireHelperTargets(ValorHeroUnit hero) {
        if (hero == null || !heroes.contains(hero) || !hero.hero().isAlive()) return List.of();
        return monsters.stream().filter(unit -> unit.monster().isAlive())
                .filter(unit -> unit.position().lane() != hero.position().lane())
                .filter(unit -> Math.abs(unit.position().row() - hero.position().row()) <= 1).toList();
    }

    public boolean canUseMarket(ValorHeroUnit hero) {
        if (hero == null || !heroes.contains(hero) || !hero.hero().isAlive()) return false;
        return board.isMarket(hero.position())
                || (hero.position().row() == ValorBoard.ROWS - 1 && board.isNexus(hero.position()));
    }

    public List<ValorHeroUnit> heroesInRange(ValorMonsterUnit monster) {
        return heroes.stream().filter(unit -> unit.hero().isAlive())
                .filter(unit -> monster.position().distanceTo(unit.position()) <= 1).toList();
    }

    /** Moves one hero one orthogonal space, enforcing the lane and blocking rules. */
    public boolean moveHero(ValorHeroUnit unit, int rowDelta, int columnDelta) {
        if (!heroes.contains(unit) || !unit.hero().isAlive()
                || Math.abs(rowDelta) + Math.abs(columnDelta) != 1) return false;
        ValorPosition target = new ValorPosition(unit.position().row() + rowDelta,
                unit.position().column() + columnDelta);
        if (unit.position().lane() != safeLane(target) || !board.isTraversable(target)
                || occupiedByOtherHero(target, unit) || reservedForOtherHero(target, unit)) return false;

        // A hero cannot move north past a living monster in the same lane.
        if (rowDelta < 0 && monsters.stream().anyMatch(monster -> monster.monster().isAlive()
                && monster.position().lane() == unit.position().lane()
                && target.row() < monster.position().row())) return false;
        moveHeroTo(unit, target);
        return true;
    }

    /** Removes one adjacent obstacle; clearing it consumes the hero's action. */
    public boolean removeObstacle(ValorHeroUnit unit, ValorPosition target) {
        if (!heroes.contains(unit) || !unit.hero().isAlive() || target == null || !unit.position().isAdjacentTo(target)) return false;
        return board.removeObstacle(target);
    }

    /** Returns all legal teleport destinations adjacent to another hero in a different lane. */
    public List<ValorPosition> teleportDestinations(ValorHeroUnit unit, ValorHeroUnit targetHero) {
        if (unit == null || targetHero == null || unit == targetHero || !unit.hero().isAlive()
                || !targetHero.hero().isAlive() || unit.position().lane() == targetHero.position().lane()) return List.of();
        List<ValorPosition> result = new ArrayList<>();
        for (ValorPosition candidate : adjacentPositions(targetHero.position())) {
            if (isLegalTeleportDestination(unit, targetHero, candidate)) result.add(candidate);
        }
        return List.copyOf(result);
    }

    /** Teleports beside a living teammate in another lane, subject to the assignment's restrictions. */
    public boolean teleport(ValorHeroUnit unit, ValorHeroUnit targetHero, ValorPosition destination) {
        if (!teleportDestinations(unit, targetHero).contains(destination)) return false;
        moveHeroTo(unit, destination);
        return true;
    }

    /** Returns this hero to the exact Nexus space where they spawned. */
    public boolean recall(ValorHeroUnit unit) {
        if (!heroes.contains(unit) || !unit.hero().isAlive()
                || occupiedByOtherHero(unit.home(), unit)) return false;
        moveHeroTo(unit, unit.home());
        return true;
    }

    /** Resolves a single in-range physical attack and applies per-monster rewards on a kill. */
    public boolean attack(ValorHeroUnit attacker, ValorMonsterUnit target) {
        if (!heroes.contains(attacker) || !attacker.hero().isAlive() || !monsters.contains(target)
                || !target.monster().isAlive() || attacker.position().distanceTo(target.position()) > 1) return false;
        int damage = attacker.hero().getAttackDamage();
        consumeWeaponUse(attacker.hero());
        strikeMonster(attacker, target, damage);
        return true;
    }

    /** Warrior's Spin Attack strikes every monster in normal attack range. */
    public boolean spinAttack(ValorHeroUnit attacker) {
        if (!heroes.contains(attacker) || !attacker.hero().isAlive()
                || !attacker.hero().getHeroClass().equalsIgnoreCase("Warrior")) return false;
        List<ValorMonsterUnit> targets = monstersInRange(attacker);
        if (targets.isEmpty() || !attacker.hero().hasEnoughMana(CLASS_SPECIAL_MANA_COST)) return false;
        attacker.hero().spendMana(CLASS_SPECIAL_MANA_COST);
        int damage = attacker.hero().getAttackDamage();
        consumeWeaponUse(attacker.hero());
        System.out.printf("%s uses Spin Attack (-%d MP) against %d monster(s)!%n",
                attacker.hero().getName(), CLASS_SPECIAL_MANA_COST, targets.size());
        for (ValorMonsterUnit target : targets) if (monsters.contains(target)) strikeMonster(attacker, target, damage);
        return true;
    }

    /** Mage's Fire Helper attacks one selected enemy from a nearby row in another lane. */
    public boolean fireHelperAttack(ValorHeroUnit attacker, ValorMonsterUnit target) {
        if (attacker == null || target == null || !attacker.hero().getHeroClass().equalsIgnoreCase("Sorcerer")
                || !fireHelperTargets(attacker).contains(target)
                || !attacker.hero().hasEnoughMana(CLASS_SPECIAL_MANA_COST)) return false;
        attacker.hero().spendMana(CLASS_SPECIAL_MANA_COST);
        int damage = attacker.hero().getAttackDamage();
        consumeWeaponUse(attacker.hero());
        System.out.printf("%s calls Fire Helper across lanes (-%d MP)!%n",
                attacker.hero().getName(), CLASS_SPECIAL_MANA_COST);
        strikeMonster(attacker, target, damage);
        return true;
    }

    /** Paladin's Holy Light boosts the Paladin's attack and spell damage for two full rounds. */
    public boolean activateHolyLight(ValorHeroUnit unit) {
        if (unit == null || !heroes.contains(unit) || !unit.hero().isAlive()
                || !unit.hero().getHeroClass().equalsIgnoreCase("Paladin")
                || !unit.hero().hasEnoughMana(CLASS_SPECIAL_MANA_COST)) return false;
        unit.hero().spendMana(CLASS_SPECIAL_MANA_COST);
        unit.activateHolyLight(roundNumber);
        return true;
    }

    /** Casts one existing spell at one in-range monster. */
    public boolean castSpell(ValorHeroUnit caster, ValorMonsterUnit target, Spell spell) {
        Hero hero = caster.hero();
        if (!heroes.contains(caster) || !hero.isAlive() || !monsters.contains(target) || !target.monster().isAlive()
                || caster.position().distanceTo(target.position()) > 1 || spell == null
                || !hero.getInventory().contains(spell) || spell.isConsumed() || !hero.hasEnoughMana(spell.getManaCost())) return false;
        hero.spendMana(spell.getManaCost());
        spell.consume();
        hero.getInventory().removeItem(spell);
        if (target.monster().dodges(random)) {
            System.out.println(target.monster().getName() + " dodged the spell; mana was still spent.");
            return true;
        }
        int damage = spell.calculateDamage(hero);
        if (caster.hasHolyLight()) damage = (int) Math.round(damage * 1.5);
        boolean critical = random.nextDouble() < CombatBalance.CRITICAL_HIT_CHANCE;
        if (critical) damage = (int) Math.round(damage * CombatBalance.CRITICAL_HIT_MULTIPLIER);
        int applied = target.monster().takeCombatDamage(damage);
        spell.applyEffect(target.monster());
        System.out.printf("%s casts %s on %s for %d damage%s.%n", hero.getName(), spell.getName(),
                target.monster().getName(), applied, critical ? " (critical hit)" : "");
        if (!target.monster().isAlive()) defeatMonster(target);
        return true;
    }

    public boolean usePotion(ValorHeroUnit unit, Potion potion) {
        Hero hero = unit.hero();
        if (!heroes.contains(unit) || !hero.isAlive() || potion == null || potion.isConsumed()
                || !hero.getInventory().contains(potion)) return false;
        int actual = potion.getActualEffectAmount(hero);
        potion.applyTo(hero);
        hero.getInventory().removeItem(potion);
        System.out.printf("%s uses %s; %s changes by %d.%n", hero.getName(), potion.getName(), potion.getStatType(), actual);
        return true;
    }

    public boolean equip(ValorHeroUnit unit, Item item, boolean bothHands) {
        Hero hero = unit.hero();
        if (!heroes.contains(unit) || !hero.isAlive() || item == null || !hero.getInventory().contains(item)) return false;
        try {
            if (item instanceof Weapon weapon) hero.equipWeapon(weapon, bothHands);
            else if (item instanceof Armor armor) hero.equipArmor(armor);
            else return false;
            return true;
        } catch (IllegalArgumentException invalidEquipment) {
            System.out.println(invalidEquipment.getMessage());
            return false;
        }
    }

    /** Runs one monster phase. Each living monster attacks once if possible, otherwise advances. */
    public void monsterTurn() {
        for (ValorMonsterUnit monster : new ArrayList<>(monsters)) {
            if (isGameOver()) return;
            if (!monsters.contains(monster) || !monster.monster().isAlive()) continue;
            List<ValorHeroUnit> targets = heroesInRange(monster);
            if (!targets.isEmpty()) attackHero(monster, targets.get(random.nextInt(targets.size())));
            else advanceMonster(monster);
        }
    }

    /** Ends the round, regenerates survivors, respawns the fallen, and queues periodic waves. */
    public void completeRound() {
        if (isGameOver()) return;
        for (ValorHeroUnit unit : heroes) unit.completeRound(roundNumber);
        for (ValorHeroUnit unit : heroes) {
            Hero hero = unit.hero();
            if (!hero.isAlive()) continue;
            hero.heal(hero.getMaxHP() * 0.10);
            hero.restoreMana(hero.getMaxMana() * 0.10);
        }
        roundNumber++;
        respawnFallenHeroes();
        if ((roundNumber - 1) % spawnInterval == 0) queueSpawnWave();
        placePendingSpawns();
    }

    private boolean isLegalTeleportDestination(ValorHeroUnit unit, ValorHeroUnit targetHero, ValorPosition candidate) {
        if (!candidate.isAdjacentTo(targetHero.position()) || !board.isTraversable(candidate)
                || occupiedByOtherHero(candidate, unit) || reservedForOtherHero(candidate, unit)
                || candidate.row() < targetHero.position().row()) return false;
        Lane lane = safeLane(candidate);
        if (lane == null) return false;
        // The destination cannot be behind a living monster already advancing in that lane.
        return monsters.stream().noneMatch(monster -> monster.monster().isAlive()
                && monster.position().lane() == lane && monster.position().column() == candidate.column()
                && candidate.row() > monster.position().row());
    }

    private void moveHeroTo(ValorHeroUnit unit, ValorPosition target) {
        unit.moveTo(target);
        applyTerrainBonus(unit);
        if (target.row() == 0) outcome = Outcome.HEROES_WIN;
    }

    private void applyTerrainBonus(ValorHeroUnit unit) {
        double strength = 0, dexterity = 0, agility = 0;
        switch (board.terrainAt(unit.position())) {
            case BUSH -> dexterity = 0.10;
            case CAVE -> agility = 0.10;
            case KOULOU -> strength = 0.10;
            default -> { }
        }
        unit.hero().setTerrainBonuses(strength, dexterity, agility);
    }

    private boolean occupiedByOtherHero(ValorPosition target, ValorHeroUnit moving) {
        return heroes.stream().anyMatch(other -> other != moving && other.hero().isAlive()
                && other.position().equals(target));
    }

    private boolean reservedForOtherHero(ValorPosition target, ValorHeroUnit moving) {
        return target.row() == ValorBoard.ROWS - 1
                && heroes.stream().anyMatch(other -> other != moving && other.home().equals(target));
    }

    private void attackHero(ValorMonsterUnit attacker, ValorHeroUnit target) {
        Monster monster = attacker.monster();
        Hero hero = target.hero();
        if (random.nextDouble() < hero.getAgilityDodgeChance()) {
            System.out.printf("%s attacks %s, but the hero dodges.%n", monster.getName(), hero.getName());
            return;
        }
        int damage = monster.getAttackDamage();
        boolean critical = random.nextDouble() < CombatBalance.CRITICAL_HIT_CHANCE;
        if (critical) damage = (int) Math.round(damage * CombatBalance.CRITICAL_HIT_MULTIPLIER);
        Armor armor = hero.getEquippedArmor();
        if (armor != null && armor.isUsable()) {
            damage = Math.max(1, damage - armor.getDamageReduction());
            armor.consumeUse();
        }
        hero.takeDamage(damage);
        System.out.printf("%s hits %s for %d damage%s (HP %.0f).%n", monster.getName(), hero.getName(), damage,
                critical ? " (critical)" : "", hero.getHP());
        if (!hero.isAlive()) {
            hero.setTerrainBonuses(0, 0, 0);
            System.out.println(hero.getName() + " falls and will respawn at their Nexus next round.");
        }
    }

    private void strikeMonster(ValorHeroUnit attacker, ValorMonsterUnit target, int baseDamage) {
        if (!monsters.contains(target) || !target.monster().isAlive()) return;
        Hero hero = attacker.hero();
        Monster monster = target.monster();
        int damage = attacker.hasHolyLight() ? (int) Math.round(baseDamage * 1.5) : baseDamage;
        if (monster.dodges(random)) {
            System.out.println(monster.getName() + " dodged the attack!");
            return;
        }
        boolean critical = random.nextDouble() < CombatBalance.CRITICAL_HIT_CHANCE;
        if (critical) damage = (int) Math.round(damage * CombatBalance.CRITICAL_HIT_MULTIPLIER);
        int applied = monster.takeCombatDamage(damage);
        System.out.printf("%s attacks %s for %d damage%s. Monster HP: %.0f%n", hero.getName(), monster.getName(),
                applied, critical ? " (critical hit)" : "", monster.getHP());
        if (!monster.isAlive()) defeatMonster(target);
    }

    private void consumeWeaponUse(Hero hero) {
        Weapon weapon = hero.getEquippedWeapon();
        if (weapon != null && weapon.isUsable()) weapon.consumeUse();
    }

    private void advanceMonster(ValorMonsterUnit unit) {
        ValorPosition current = unit.position();
        ValorPosition forward = new ValorPosition(current.row() + 1, current.column());
        if (board.isTraversable(forward) && !occupiedByOtherMonster(forward, unit)
                && !wouldPassHero(unit, forward)) {
            unit.moveTo(forward);
            if (forward.row() == ValorBoard.ROWS - 1) outcome = Outcome.MONSTERS_WIN;
            return;
        }

        // The monster tries the other cell in its two-wide lane to route around terrain or a blocker.
        int alternateColumn = current.column() == current.lane().firstColumn()
                ? current.lane().secondColumn() : current.lane().firstColumn();
        ValorPosition sidestep = new ValorPosition(current.row(), alternateColumn);
        if (board.isTraversable(sidestep) && !occupiedByOtherMonster(sidestep, unit)) unit.moveTo(sidestep);
    }

    private boolean wouldPassHero(ValorMonsterUnit moving, ValorPosition target) {
        return heroes.stream().anyMatch(hero -> hero.hero().isAlive()
                && hero.position().lane() == moving.position().lane()
                && target.row() > hero.position().row());
    }

    private boolean occupiedByOtherMonster(ValorPosition position, ValorMonsterUnit moving) {
        return monsters.stream().anyMatch(other -> other != moving && other.monster().isAlive()
                && other.position().equals(position));
    }

    private void defeatMonster(ValorMonsterUnit unit) {
        monsters.remove(unit);
        int xp = XP_PER_MONSTER;
        int gold = GOLD_PER_LEVEL * unit.monster().getLevel();
        for (ValorHeroUnit heroUnit : heroes) {
            Hero rewardedHero = heroUnit.hero();
            boolean wasAlive = rewardedHero.isAlive();
            rewardedHero.gainExperience(xp);
            if (!wasAlive && rewardedHero.isAlive()) rewardedHero.takeDamage(rewardedHero.getHP());
            rewardedHero.addGold(gold);
        }
        System.out.printf("%s is defeated. Every hero receives %d XP and %d gold.%n",
                unit.monster().getName(), xp, gold);
    }

    private void respawnFallenHeroes() {
        for (ValorHeroUnit unit : heroes) {
            Hero hero = unit.hero();
            if (hero.isAlive()) continue;
            // Home cells are reserved for their owner, so the exact spawn point stays available.
            hero.heal(hero.getMaxHP());
            hero.restoreMana(hero.getMaxMana());
            unit.moveTo(unit.home());
            applyTerrainBonus(unit);
            System.out.println(hero.getName() + " respawns at their Nexus with full HP and MP.");
        }
    }

    private void queueSpawnWave() {
        List<Monster> wave = new MonsterFactory(data, random).createFor(party, 0);
        for (int i = 0; i < Lane.values().length; i++) {
            pendingSpawns.add(new PendingSpawn(Lane.values()[i], wave.get(i)));
        }
    }

    private void placePendingSpawns() {
        Iterator<PendingSpawn> iterator = pendingSpawns.iterator();
        while (iterator.hasNext()) {
            PendingSpawn pending = iterator.next();
            ValorPosition first = new ValorPosition(0, pending.lane().secondColumn());
            ValorPosition second = new ValorPosition(0, pending.lane().firstColumn());
            ValorPosition destination = !occupiedByAnyMonster(first) ? first
                    : !occupiedByAnyMonster(second) ? second : null;
            if (destination != null) {
                addMonster(pending.monster(), destination);
                System.out.printf("A new level %d monster enters the %s lane.%n",
                        pending.monster().getLevel(), pending.lane());
                iterator.remove();
            }
        }
    }

    private boolean occupiedByAnyMonster(ValorPosition position) {
        return monsters.stream().anyMatch(unit -> unit.monster().isAlive() && unit.position().equals(position));
    }

    private void addMonster(Monster monster, ValorPosition position) {
        monsters.add(new ValorMonsterUnit(nextMonsterNumber++, monster, position));
    }

    private static List<ValorPosition> adjacentPositions(ValorPosition position) {
        return List.of(new ValorPosition(position.row() - 1, position.column()),
                new ValorPosition(position.row(), position.column() - 1),
                new ValorPosition(position.row() + 1, position.column()),
                new ValorPosition(position.row(), position.column() + 1));
    }

    private static Lane safeLane(ValorPosition position) {
        try { return position.lane(); }
        catch (IllegalArgumentException ignored) { return null; }
    }

    private record PendingSpawn(Lane lane, Monster monster) { }
}
