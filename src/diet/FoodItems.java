package diet;

import util.ItemList;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class FoodItems extends ItemList<FoodItem> {
    public FoodItems() {
        super(FoodItem.class);
    }

    public FoodProperties getProperties() {
        final FoodProperties properties = new FoodProperties();
        forEach(new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(FoodItem foodItem, Double amount) {
                properties.addScaled(foodItem.getProperties(), amount);
            }
        });
        return properties;
    }

    public double getCosts() {
        double costs = 0;
        final Iterator<Map.Entry<FoodItem, Double>> iterator = getIterator();
        while (iterator.hasNext()) {
            final Map.Entry<FoodItem, Double> entry = iterator.next();
            costs += entry.getKey().getPrice() * entry.getValue();
        }
        return costs;
    }
}
