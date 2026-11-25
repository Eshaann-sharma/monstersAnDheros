package monstersheroes.legendsofvalor;

import monstersheroes.characters.Hero;

/** A reusable Hero placed on the Valor board, with a permanent recall Nexus. */
public final class ValorHeroUnit {
    private final int number;
    private final Hero hero;
    private final ValorPosition home;
    private ValorPosition position;
    private int holyLightRoundsRemaining;
    private int holyLightStartedRound = -1;

    ValorHeroUnit(int number, Hero hero, ValorPosition home) {
        this.number = number;
        this.hero = hero;
        this.home = home;
        this.position = home;
    }

    public int number() { return number; }
    public Hero hero() { return hero; }
    public ValorPosition home() { return home; }
    public ValorPosition position() { return position; }
    public int holyLightRoundsRemaining() { return holyLightRoundsRemaining; }
    public boolean hasHolyLight() { return holyLightRoundsRemaining > 0; }
    void moveTo(ValorPosition destination) { position = destination; }

    void activateHolyLight(int currentRound) {
        holyLightRoundsRemaining = 2;
        holyLightStartedRound = currentRound;
    }

    void completeRound(int completedRound) {
        if (holyLightRoundsRemaining > 0 && completedRound > holyLightStartedRound) holyLightRoundsRemaining--;
    }
}
