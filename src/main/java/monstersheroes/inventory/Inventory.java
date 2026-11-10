package monstersheroes.inventory;

import monstersheroes.items.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Stores a hero's items and keeps the list limited to the chosen item type. */
public class Inventory<T extends Item> {
    private final List<T> items = new ArrayList<>();

    public void addItem(T item) { if (item != null) items.add(item); }
    public boolean removeItem(T item) { return items.remove(item); }
    public List<T> getItems() { return Collections.unmodifiableList(items); }
    public boolean isEmpty() { return items.isEmpty(); }
    public int size() { return items.size(); }
    public T get(int index) { return items.get(index); }
    public boolean contains(T item) { return items.contains(item); }
}
