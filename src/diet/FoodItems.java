package diet;

import util.ItemList;

import java.util.function.BiConsumer;

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

    public double getCosts() {
        final Costs costs = new Costs();
        forEach(new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(final FoodItem foodItem, final Double amount) {
                costs.add(foodItem.getPrice() * amount);
            }
        });
        return costs.get();
    }

    public FoodItems getWithChange(final FoodItem foodItem, final double newAmount) {
        return new FoodItems(this, foodItem, newAmount);
    }

    private class Costs {
        private double costs = 0;

        public void add(final double costs) {
            this.costs += costs;
        }

        public double get() {
            return costs;
        }
    }
}
