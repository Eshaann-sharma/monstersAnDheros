package monstersheroes.data;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/** Reads the starting character and item details from the files in the data folder. */
public class GameDataLoader {
    public GameData load(Path directory) {
        GameData data = new GameData();
        loadHeroes(directory.resolve("heroes.txt"), data);
        loadMonsters(directory.resolve("monsters.txt"), data);
        loadItems(directory.resolve("items.txt"), data);
        return data;
    }

    private void loadHeroes(Path file, GameData data) {
        readLines(file).forEach(line -> {
            String[] pair = line.split("=", 2);
            String[] values = pair[1].split(",");
            data.addHeroStats(pair[0].trim(), new GameData.HeroStats(
                    Double.parseDouble(values[0].trim()), Double.parseDouble(values[1].trim()),
                    Double.parseDouble(values[2].trim()), Double.parseDouble(values[3].trim())));
        });
    }

    private void loadMonsters(Path file, GameData data) {
        readLines(file).forEach(line -> {
            String[] pair = line.split("=", 2);
            String[] values = pair[1].split(",");
            data.addMonsterStats(pair[0].trim(), new GameData.MonsterStats(
                    Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()),
                    Integer.parseInt(values[2].trim())));
        });
    }

    private void loadItems(Path file, GameData data) {
        readLines(file).forEach(line -> {
            String[] p = line.split("\\|", -1);
            if (p.length != 8) throw new IllegalArgumentException("Bad item definition: " + line);
            data.addItemDefinition(new GameData.ItemDefinition(p[0].trim(), p[1].trim(),
                    Integer.parseInt(p[2].trim()), Integer.parseInt(p[3].trim()),
                    Integer.parseInt(p[4].trim()), Integer.parseInt(p[5].trim()),
                    Integer.parseInt(p[6].trim()), p[7].trim()));
        });
    }

    private List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read required game data file: " + file.toAbsolutePath(), e);
        }
    }
}
