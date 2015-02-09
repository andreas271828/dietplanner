package diet;

import util.Limits2;
import util.Pair;

import java.util.ArrayList;

import static util.Limits2.limits2;

public abstract class MealTemplate {
    private final String name;
    private ArrayList<Pair<FoodItem, Limits2>> ingredients;

    public MealTemplate(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected abstract void addIngredients();

    protected void addIngredient(final FoodItem foodItem, final double minAmount, final double maxAmount) {
        ingredients.add(new Pair<FoodItem, Limits2>(foodItem, limits2(minAmount, maxAmount)));
    }

    protected void addIngredients(final ArrayList<Pair<FoodItem, Limits2>> ingredients) {
        this.ingredients.addAll(ingredients);
    }

    public ArrayList<Pair<FoodItem, Limits2>> getIngredients() {
        if (ingredients == null) {
            ingredients = new ArrayList<Pair<FoodItem, Limits2>>();
            addIngredients();
        }

        return ingredients;
    }

    @Override
    public String toString() {
        return name;
    }
}
