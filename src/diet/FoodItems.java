package diet;

import util.ItemList;
import util.Mutable;

import java.util.function.BiConsumer;

import static util.Mutable.mutable;

public class FoodItems extends ItemList<FoodItem> {
    public FoodItems() {
        super(FoodItem.class);
    }

    private FoodItems(final FoodItems foodItems, final FoodItem foodItem, final double newAmount) {
        super(foodItems, foodItem, newAmount);
    }

    public FoodProperties getProperties() {
        final FoodProperties properties = new FoodProperties();
        forEach(new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(final FoodItem foodItem, final Double amount) {
                properties.addScaled(foodItem.getProperties(), amount);
            }
        });
        return properties;
    }

    public double getEnergy() {
        return getProperties().get(FoodProperty.ENERGY);
    }

    public double getCosts() {
        final Mutable<Double> costs = mutable(0.0);
        forEach(new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(final FoodItem foodItem, final Double amount) {
                costs.set(costs.get() + foodItem.getPrice() * amount);
            }
        });
        return costs.get();
    }

    public FoodItems getWithChange(final FoodItem foodItem, final double newAmount) {
        return new FoodItems(this, foodItem, newAmount);
    }
}
