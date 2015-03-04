package util;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ItemList<K extends Enum<K>> {
    private final EnumMap<K, Double> items;

    public ItemList(final Class<K> itemType) {
        this.items = new EnumMap<K, Double>(itemType);
    }

    public ItemList(final ItemList<K> itemList, final K item, double change) {
        items = itemList.items.clone();
        set(item, get(item) + change);
    }

    public double get(final K item) {
        return items.containsKey(item) ? items.get(item) : 0.0;
    }

    public void set(final K item, final double amount) {
        if (amount <= 0.0) {
            items.remove(item);
        } else {
            items.put(item, amount);
        }
    }

    public void add(final ItemList<K> toAdd) {
        for (final Map.Entry<K, Double> entry : toAdd.items.entrySet()) {
            final K item = entry.getKey();
            set(item, get(item) + entry.getValue());
        }
    }

    public void addScaled(final ItemList<K> toAdd, final double factor) {
        for (final Map.Entry<K, Double> entry : toAdd.items.entrySet()) {
            final K item = entry.getKey();
            set(item, get(item) + entry.getValue() * factor);
        }
    }

    public void forEach(final BiConsumer<K, Double> action) {
        for (final Map.Entry<K, Double> entry : items.entrySet()) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
