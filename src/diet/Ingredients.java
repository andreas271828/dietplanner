package diet;

import util.Limits2;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.function.BiConsumer;

import static util.Limits2.limits2;

public class Ingredients {
    private final EnumMap<FoodItem, Limits2> ingredients = new EnumMap<FoodItem, Limits2>(FoodItem.class);

    public void add(final FoodItem foodItem, final double minAmount, final double maxAmount) {
        ingredients.put(foodItem, limits2(minAmount, maxAmount));
    }

    /**
     * @param foodItem  Food item
     * @param minWeight Minimum weight in g
     * @param maxWeight Maximum weight in g
     */
    public void addByWeight(final FoodItem foodItem, final double minWeight, final double maxWeight) {
        add(foodItem, foodItem.toAmount(minWeight), foodItem.toAmount(maxWeight));
    }

    public void addAll(final Ingredients ingredients) {
        this.ingredients.putAll(ingredients.ingredients);
    }

    public int getCount() {
        return ingredients.size();
    }

    public ArrayList<FoodItem> getFoodItems() {
        return new ArrayList<FoodItem>(ingredients.keySet());
    }

    public void forEach(final BiConsumer<FoodItem, Limits2> action) {
        ingredients.forEach(action);
    }

    public double getMinAmount(final FoodItem ingredient) {
        final Limits2 limits = ingredients.get(ingredient);
        return limits == null ? 0.0 : limits.getMin();
    }

    public double getMaxAmount(final FoodItem ingredient) {
        final Limits2 limits = ingredients.get(ingredient);
        return limits == null ? 0.0 : limits.getMax();
    }

    public FoodItems getMinAmounts() {
        final FoodItems foodItems = new FoodItems();
        forEach(new BiConsumer<FoodItem, Limits2>() {
            @Override
            public void accept(final FoodItem foodItem, final Limits2 limits) {
                // TODO: Always round up!
                foodItems.set(foodItem, foodItem.roundToPortions(limits.getMin()));
            }
        });
        return foodItems;
    }
}
