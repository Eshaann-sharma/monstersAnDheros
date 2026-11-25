package monstersheroes.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Prints the title, monster drawings, and other pictures and messages shown during the game. */
public final class AsciiArt {
    private AsciiArt() { }

    public static void printTitle() {
        System.out.println("+----------------------------------------------------------+");
        System.out.println("|  _     _____ ____ _____ _   _ ____  ____                 |");
        System.out.println("| | |   | ____/ ___|_   _| | | |  _ \\/ ___|                |");
        System.out.println("| | |   |  _| \\___ \\ | | | | | | | | |\\___ \\                |");
        System.out.println("| | |___| |___ ___) || | | |_| | |_| |___) |               |");
        System.out.println("| |_____|_____|____/ |_|  \\___/|____/|____/                |");
        System.out.println("|                   MONSTERS & HEROES                      |");
        System.out.println("+----------------------------------------------------------+");
        System.out.println("                 Prepare for your adventure!                ");
    }

    public static void printBattleStart() {
        System.out.println("             /\\                 /\\             ");
        System.out.println("            /  \\   BATTLE!     /  \\            ");
        System.out.println("       ____/____\\_____________/____\\____       ");
        System.out.println("          HEROES          VS       MONSTERS       ");
    }

    /** Prints the supplied detailed monster illustration from data/art. */
    public static void printMonster(String monsterType) {
        String type = monsterType == null ? "" : monsterType.toLowerCase();
        if (!type.equals("dragon") && !type.equals("spirit") && !type.equals("exoskeleton")) {
            System.out.println("             A mysterious monster appears!  ");
            return;
        }

        Path artFile = Path.of("data", "art", type + ".txt");
        try {
            if (Files.isRegularFile(artFile)) {
                String art = Files.readString(artFile, StandardCharsets.UTF_8);
                System.out.print(art);
                if (!art.endsWith("\n")) System.out.println();
                return;
            }
        } catch (IOException exception) {
            System.out.println("[Could not load " + type + " illustration: " + exception.getMessage() + "]");
        }

        // Keep a small fallback so the game remains playable if art files are moved.
        switch (type) {
            case "dragon" -> System.out.println("              DRAGON              ");
            case "spirit" -> System.out.println("               .-~~~~-.            ");
            case "exoskeleton" -> System.out.println("              .-.___.-.            ");
            default -> System.out.println("             A mysterious monster appears!");
        }
    }

    /** Prints the supplied full-size portrait for a portal boss. */
    public static void printBoss(String bossName) {
        String file = switch (bossName.toLowerCase(java.util.Locale.ROOT)) {
            case "skeleton king" -> "skeleton-king.txt";
            case "gengar" -> "gengar.txt";
            default -> "chrysophylax.txt";
        };
        Path artFile = Path.of("data", "art", "bosses", file);
        try {
            System.out.print(Files.readString(artFile, StandardCharsets.UTF_8));
            System.out.println();
        } catch (IOException exception) {
            System.out.println("[Could not load boss illustration: " + exception.getMessage() + "]");
        }
    }

    public static void printCriticalHit() {
        System.out.println("\n+---------------------------------------+");
        System.out.println("|             CRITICAL HIT!             |");
        System.out.println("|                  ! !                  |");
        System.out.println("|                 /___\\                 |");
        System.out.println("|          DOUBLE DAMAGE DEALT!         |");
        System.out.println("+---------------------------------------+");
    }

    public static void printPerfectDodge() {
        System.out.println("\n       .----.       ");
        System.out.println("      /  /\\  \\      ");
        System.out.println("     |  (  )  |     ");
        System.out.println("      \\  \\/  /      ");
        System.out.println("       '----'       ");
        System.out.println("       PERFECT DODGE!       ");
    }

    public static void printHealingLight() {
        System.out.println("\n          .  *  .  *  .");
        System.out.println("             \\ | /");
        System.out.println("          --  HEAL  --");
        System.out.println("             / | \\");
        System.out.println("          .  *  .  *  .");
    }

    public static void printVictory() {
        System.out.println("\n  \\O/   VICTORY!   \\O/");
        System.out.println("   |               |");
        System.out.println("  / \\             / \\");
        System.out.println("The monsters have been defeated!");
    }

    public static void printDefeat() {
        System.out.println("\n       .--------.");
        System.out.println("       | GAME   |");
        System.out.println("       | OVER   |");
        System.out.println("       '--------'");
        System.out.println("Your party has fallen. Regroup and try again.");
    }

    public static void printMarket() {
        System.out.println("\n       ___________________________");
        System.out.println("      /  THE ADVENTURERS' MARKET  \\");
        System.out.println("     /____________________________\\");
    }
}
