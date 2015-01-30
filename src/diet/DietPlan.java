package diet;

import util.LazyValue;

import java.util.ArrayList;

public class DietPlan {
    private final ArrayList<Meal> meals;
    private final LazyValue<FoodItems> foodItems;
    private final LazyValue<FoodProperties> properties;

    public static DietPlan dietPlan(final ArrayList<Meal> meals) {
        return new DietPlan(meals);
    }

    private DietPlan(final ArrayList<Meal> meals) {
        this.meals = meals;

        foodItems = new LazyValue<FoodItems>() {
            @Override
            protected FoodItems compute() {
                final FoodItems foodItems = new FoodItems();
                final ArrayList<Meal> meals = getMeals();
                for (final Meal meal : meals) {
                    foodItems.add(meal.getIngredients());
                }
                return foodItems;
            }
        };

        properties = new LazyValue<FoodProperties>() {
            @Override
            protected FoodProperties compute() {
                final FoodProperties properties = new FoodProperties();
                final ArrayList<Meal> meals = getMeals();
                for (final Meal meal : meals) {
                    properties.add(meal.getProperties());
                }
                return properties;
            }
        };
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public Meal getMeal(int mealIndex) {
        return getMeals().get(mealIndex);
    }

    public FoodItems getFoodItems() {
        return foodItems.get();
    }

    public FoodProperties getProperties() {
        return properties.get();
    }
}
