package diet;

import util.LazyValue;
import util.Limits2;

import java.util.function.BiConsumer;

import static util.Global.nextRandomDoubleInclOne;

public class Meal {
    private MealTemplate template;
    private FoodItems ingredients;
    private final LazyValue<FoodProperties> properties;
    private final LazyValue<Double> costs;

    public static Meal meal(final MealTemplate template, final FoodItems ingredients) {
        return new Meal(template, ingredients);
    }

    public static Meal randomMeal(final MealTemplate template) {
        final FoodItems ingredients = new FoodItems();
        template.getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
            @Override
            public void accept(final FoodItem foodItem, final Limits2 limits) {
                final double minAmount = limits.getMin();
                final double maxAmount = limits.getMax();
                final double relAmount = nextRandomDoubleInclOne();
                final double amount = minAmount + relAmount * (maxAmount - minAmount);
                final double roundedAmount = foodItem.roundToPortions(amount);
                if (roundedAmount > 1e-6) {
                    ingredients.set(foodItem, roundedAmount);
                }
            }
        });
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

    @Override
    public String toString() {
        return getName() + ": " + getIngredients().toString();
    }
}
