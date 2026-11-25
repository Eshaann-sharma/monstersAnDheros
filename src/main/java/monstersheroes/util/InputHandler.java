package monstersheroes.util;

import java.util.Scanner;

/** Reads the player's commands and checks that menu choices are valid. */
public class InputHandler {
    private final Scanner scanner;

    public InputHandler() {
        scanner = new Scanner(System.in);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String input = readLine(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {
                // Re-prompt below.
            }
            System.out.printf("Please enter a whole number between %d and %d.%n", min, max);
        }
    }

    public int readIntAllowZero(String prompt, int min, int max) {
        return readInt(prompt, min, max);
    }

    public char readCommand(String prompt) {
        String input = readLine(prompt);
        return input.isEmpty() ? '\0' : Character.toUpperCase(input.charAt(0));
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            char answer = readCommand(prompt + " (y/n): ");
            if (answer == 'Y') return true;
            if (answer == 'N') return false;
            System.out.println("Please enter Y or N.");
        }
    }
}
