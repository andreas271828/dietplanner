package diet;

import util.Limits2;
import util.Pair;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import static util.Limits2.limits2;

public class Ingredients {
    final ArrayList<Pair<FoodItem, Limits2>> ingredients = new ArrayList<Pair<FoodItem, Limits2>>();

    public void add(final FoodItem foodItem, final double minAmount, final double maxAmount) {
        ingredients.add(new Pair<FoodItem, Limits2>(foodItem, limits2(minAmount, maxAmount)));
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
        this.ingredients.addAll(ingredients.ingredients);
    }

    public void forEach(final BiConsumer<FoodItem, Limits2> action) {
        for (final Pair<FoodItem, Limits2> ingredient : ingredients) {
            action.accept(ingredient.a(), ingredient.b());
        }
    }
}
