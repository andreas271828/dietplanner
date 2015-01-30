package diet;

import util.ItemList;

import java.util.function.BiConsumer;

public class FoodItems extends ItemList<FoodItem> {
    public FoodItems() {
        super(FoodItem.class);
    }

    public FoodProperties getProperties() {
        final FoodProperties properties = new FoodProperties();
        final BiConsumer<FoodItem, Double> addFoodItemToProperties = new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(FoodItem foodItem, Double amount) {
                properties.addScaled(foodItem.getProperties(), amount);
            }
        };
        forEach(addFoodItemToProperties);
        return properties;
    }
}
