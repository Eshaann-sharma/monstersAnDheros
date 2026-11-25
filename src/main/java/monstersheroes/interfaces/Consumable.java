package monstersheroes.interfaces;

/** Describes an item that can be used up, such as a potion or spell. */
public interface Consumable {
    boolean isConsumed();
    void consume();
}
