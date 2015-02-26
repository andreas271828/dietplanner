package diet;

import util.Limits2;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import static diet.Meal.meal;
import static diet.Meal.randomMeal;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static util.Global.RANDOM;
import static util.Global.nextRandomDoubleInclOne;

public abstract class MealTemplate {
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
            meals.add(randomMeal(this));
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
                    ingredients.set(foodItem, roundedAmount);
                }
            });
            meals.add(meal(this, ingredients));
        }
        return meals;
    }

    public ArrayList<Meal> applyChanges(final ArrayList<Meal> origMeals, final ArrayList<FoodItems> changes) {
        final int origMealsSize = origMeals.size();
        final ArrayList<Meal> meals = new ArrayList<Meal>(origMealsSize);
        for (int i = 0; i < origMealsSize; ++i) {
            final Meal origMeal = origMeals.get(i);
            final FoodItems mealChanges = changes.get(i);
            final FoodItems ingredients = new FoodItems();
            getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double origAmount = origMeal.getIngredients().get(foodItem);
                    final double change = mealChanges.get(foodItem);
                    final double amount = min(max(origAmount + change, limits.getMin()), limits.getMax());
                    final double roundedAmount = foodItem.roundToPortions(amount);
                    ingredients.set(foodItem, roundedAmount);
                }
            });
            meals.add(meal(this, ingredients));
        }
        return meals;
    }

    /**
     * @param magnitude     Maximum change, where 1.0 means 100% of possible range
     * @param numberOfMeals Number of meals
     * @return Food item changes (values can be negative)
     */
    public ArrayList<FoodItems> getRandomChanges(final double magnitude, final int numberOfMeals) {
        final ArrayList<FoodItems> changes = new ArrayList<FoodItems>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            final FoodItems mealChanges = new FoodItems();
            getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double randVal = 2.0 * nextRandomDoubleInclOne() - 1.0;
                    final double change = randVal * magnitude * (limits.getMax() - limits.getMin());
                    mealChanges.set(foodItem, change);
                }
            });
            changes.add(mealChanges);
        }
        return changes;
    }

    public ArrayList<Meal> getRandomMix(final ArrayList<Meal> meals1, final ArrayList<Meal> meals2) {
        final int numberOfMeals = meals1.size();
        final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            meals.add(RANDOM.nextBoolean() ? meals1.get(i) : meals2.get(i));
        }
        return meals;
    }

    @Override
    public String toString() {
        return name;
    }
}
