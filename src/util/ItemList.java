package util;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class ItemList<K extends Enum<K>> {
    private final EnumMap<K, Double> items;

    public ItemList(final Class<K> itemType) {
        this.items = new EnumMap<K, Double>(itemType);
    }

    public double get(final K item) {
        return items.containsKey(item) ? items.get(item) : 0;
    }

    public void set(final K item, final double amount) {
        items.put(item, amount);
    }

    public void add(final ItemList<K> toAdd) {
        for (final Map.Entry<K, Double> entry : toAdd.items.entrySet()) {
            final K item = entry.getKey();
            items.put(item, get(item) + entry.getValue());
        }
    }

    public void addScaled(final ItemList<K> toAdd, final double factor) {
        for (final Map.Entry<K, Double> entry : toAdd.items.entrySet()) {
            final K item = entry.getKey();
            items.put(item, get(item) + entry.getValue() * factor);
        }
    }

    public void forEach(final BiConsumer<K, Double> action) {
        for (final Map.Entry<K, Double> entry : items.entrySet()) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }

    // TODO: Not happy with this
    protected Iterator<Map.Entry<K, Double>> getIterator() {
        return items.entrySet().iterator();
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
