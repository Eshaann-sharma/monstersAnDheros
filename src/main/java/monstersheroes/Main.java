package monstersheroes;

import monstersheroes.data.GameData;
import monstersheroes.data.GameDataLoader;
import monstersheroes.util.InputHandler;
import monstersheroes.legendsofvalor.ValorController;
import java.nio.file.Path;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Starts the game and loads the default information from the data files. */
public class Main {
    public static void main(String[] args) {
        // Keep the supplied Unicode monster illustrations intact on Java 17 terminals.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        try {
            Path dataDirectory = args.length > 0 ? Path.of(args[0]) : Path.of("data");
            GameData data = new GameDataLoader().load(dataDirectory);
            InputHandler input = new InputHandler();
            boolean choosingGame = true;
            while (choosingGame) {
                System.out.println("\n========== LEGENDS MAIN MENU ==========");
                System.out.println("1. Legends: Monsters and Heroes");
                System.out.println("2. Legends of Valor");
                System.out.println("3. Exit");
                int mode = input.readInt("Choose a game: ", 1, 3);
                switch (mode) {
                    case 1 -> new GameController(input, data).start();
                    case 2 -> new ValorController(input, data).start();
                    case 3 -> choosingGame = false;
                    default -> throw new IllegalStateException("Unexpected menu choice: " + mode);
                }
            }
            System.out.println("Goodbye!");
        } catch (Exception e) {
            System.err.println("Unable to start the game: " + e.getMessage());
            System.err.println("Run the game from the project root so the ./data folder can be found.");
            e.printStackTrace();
        }
    }
}
