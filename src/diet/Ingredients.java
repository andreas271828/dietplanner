package diet;

import util.Limits2;
import util.Pair;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.BiConsumer;

import static util.Limits2.limits2;
import static util.Pair.pair;

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

    public void forEach(final BiConsumer<FoodItem, Limits2> action) {
        ingredients.forEach(action);
    }

    public List<Pair<FoodItem, Limits2>> asList() {
        final List<Pair<FoodItem, Limits2>> list = new ArrayList<Pair<FoodItem, Limits2>>();
        forEach(new BiConsumer<FoodItem, Limits2>() {
            @Override
            public void accept(final FoodItem foodItem, final Limits2 limits) {
                list.add(pair(foodItem, limits));
            }
        });
        return list;
    }

    public double getMinAmount(final FoodItem ingredient) {
        final Limits2 limits = ingredients.get(ingredient);
        return limits == null ? 0.0 : limits.getMin();
    }

    public double getRoundedMinAmount(final FoodItem ingredient) {
        return ingredient.roundToPortions(getMinAmount(ingredient));
    }

    public double getMaxAmount(final FoodItem ingredient) {
        final Limits2 limits = ingredients.get(ingredient);
        return limits == null ? 0.0 : limits.getMax();
    }

    public double getRoundedMaxAmount(final FoodItem ingredient) {
        return ingredient.roundToPortions(getMaxAmount(ingredient));
    }
}
