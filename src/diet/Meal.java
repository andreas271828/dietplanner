package diet;

import util.LazyValue;

public class Meal {
    private final MealTemplate template;
    private final FoodItems ingredients;
    private final LazyValue<FoodProperties> properties;
    private final LazyValue<Double> costs;

    public static Meal meal(final MealTemplate template, final FoodItems ingredients) {
        return new Meal(template, ingredients);
    }

    private Meal(final MealTemplate template, final FoodItems ingredients) {
        this.template = template;
        this.ingredients = ingredients;

        properties = new LazyValue<FoodProperties>() {
            @Override
            protected FoodProperties compute() {
                return ingredients.getProperties();
            }
        };

        costs = new LazyValue<Double>() {
            @Override
            protected Double compute() {
                return ingredients.getCosts();
            }
        };
    }

    public MealTemplate getTemplate() {
        return template;
    }

    public String getName() {
        return template.getName();
    }

    public FoodItems getIngredients() {
        return ingredients;
    }

    public double getAmount(final FoodItem foodItem) {
        return ingredients.get(foodItem);
    }

    public FoodProperties getProperties() {
        return properties.get();
    }

    public double getCosts() {
        return costs.get();
    }

    public Meal getWithChange(final FoodItem ingredient, final double newAmount) {
        // TODO: Lazy values can be set using a new private constructor - the modifications are easy to calculate here.
        final FoodItems ingredients = getIngredients().getWithChange(ingredient, newAmount);
        return new Meal(getTemplate(), ingredients);
    }

    @Override
    public String toString() {
        return getName() + ": " + getIngredients().toString();
    }
}
