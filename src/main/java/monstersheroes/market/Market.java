package monstersheroes.market;

import monstersheroes.data.GameData;
import monstersheroes.items.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Keeps the items for sale at one market on the map. */
public class Market {
    private final List<Item> stock = new ArrayList<>();

    public Market(GameData data, Random random, java.util.Set<String> usedStockSignatures) {
        List<GameData.ItemDefinition> definitions = data.getItemDefinitions();
        int count = Math.min(6, definitions.size());
        List<GameData.ItemDefinition> shuffled = new ArrayList<>(definitions);
        String signature = "";
        boolean unique = false;
        for (int attempt = 0; attempt < 200 && !unique; attempt++) {
            java.util.Collections.shuffle(shuffled, random);
            signature = shuffled.subList(0, count).stream()
                    .map(definition -> definition.name).sorted()
                    .collect(java.util.stream.Collectors.joining("|"));
            unique = usedStockSignatures.add(signature);
        }
        for (int i = 0; i < count; i++) stock.add(shuffled.get(i).createItem());
    }

    public List<Item> getStock() { return stock; }
    public void addItem(Item item) { stock.add(item); }
    public boolean removeItem(Item item) { return stock.remove(item); }
}
