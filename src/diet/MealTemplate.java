package diet;

import util.Limits2;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiConsumer;

import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class MealTemplate {
    private static final Random RANDOM = new Random();

    private final String name;
    private Ingredients ingredients;

    public MealTemplate(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected abstract void addIngredients();

    protected void addIngredient(final FoodItem foodItem, final double minAmount, final double maxAmount) {
        ingredients.add(foodItem, minAmount, maxAmount);
    }

    protected void addIngredientByWeight(final FoodItem foodItem, final double minWeight, final double maxWeight) {
        ingredients.addByWeight(foodItem, minWeight, maxWeight);
    }

    protected void addIngredients(final Ingredients ingredients) {
        this.ingredients.addAll(ingredients);
    }

    public Ingredients getIngredients() {
        if (ingredients == null) {
            ingredients = new Ingredients();
            addIngredients();
        }
        return ingredients;
    }

    public ArrayList<Meal> getRandomMeals(final int numberOfMeals) {
        final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            final FoodItems ingredients = new FoodItems();
            getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double minAmount = limits.getMin();
                    final double maxAmount = limits.getMax();
                    final double relAmount = RANDOM.nextInt(1000001) / 1000000.0;
                    final double amount = minAmount + relAmount * (maxAmount - minAmount);
                    final double roundedAmount = foodItem.roundToPortions(amount);
                    if (roundedAmount > 1e-6) {
                        ingredients.set(foodItem, roundedAmount);
                    }
                }
            });
            meals.add(new Meal(getName(), ingredients));
        }
        return meals;
    }

    public ArrayList<Meal> getMinimalistMeals(final int numberOfMeals) {
        final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            final FoodItems ingredients = new FoodItems();
            getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double roundedAmount = foodItem.roundToPortions(limits.getMin());
                    if (roundedAmount > 1e-6) {
                        ingredients.set(foodItem, roundedAmount);
                    }
                }
            });
            meals.add(new Meal(getName(), ingredients));
        }
        return meals;
    }

    public ArrayList<Meal> applyChanges(final ArrayList<Meal> origMeals, final ArrayList<FoodItems> changes, final boolean reverse) {
        final ArrayList<Meal> meals = new ArrayList<Meal>(origMeals.size());
        for (int i = 0; i < origMeals.size(); ++i) {
            final Meal origMeal = origMeals.get(i);
            final FoodItems mealChanges = changes.get(i);
            final FoodItems ingredients = new FoodItems();
            getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double origAmount = origMeal.getIngredients().get(foodItem);
                    final double change = reverse ? -mealChanges.get(foodItem) : mealChanges.get(foodItem);
                    final double amount = min(max(origAmount + change, limits.getMin()), limits.getMax());
                    final double roundedAmount = foodItem.roundToPortions(amount);
                    if (roundedAmount > 1e-6) {
                        ingredients.set(foodItem, roundedAmount);
                    }
                }
            });
            meals.add(new Meal(origMeal.getName(), ingredients));
        }
        return meals;
    }

    public ArrayList<FoodItems> getRandomChanges(final double rate, final int numberOfMeals) {
        final ArrayList<FoodItems> changes = new ArrayList<FoodItems>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            final FoodItems mealChanges = new FoodItems();
            getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double direction = RANDOM.nextBoolean() ? 1.0 : -1.0;
                    final double change = direction * rate * (limits.getMax() - limits.getMin());
                    if (change > 1e-6) {
                        mealChanges.set(foodItem, change);
                    }
                }
            });
            changes.add(mealChanges);
        }
        return changes;
    }

    @Override
    public String toString() {
        return name;
    }
}
