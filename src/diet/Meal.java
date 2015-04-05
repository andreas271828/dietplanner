package diet;

import util.LazyValue;
import util.Limits2;
import util.Mutable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static util.Global.RANDOM;

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

    public Meal getWithMutations(final Optional<Meal> maybeCrossoverMeal, final double mutationRate) {
        final FoodItems foodItems = new FoodItems();
        final MealTemplate mealTemplate = getTemplate();
        final Ingredients ingredients = mealTemplate.getIngredients();
        final int crossoverIngredientIndex = maybeCrossoverMeal.map(new Function<Meal, Integer>() {
            @Override
            public Integer apply(final Meal crossoverMeal) {
                return mealTemplate.equals(crossoverMeal.getTemplate()) ? RANDOM.nextInt(ingredients.getCount() + 1) : 0;
            }
        }).orElse(0);
        final Mutable<Integer> ingredientIndex = Mutable.mutable(0);
        ingredients.forEach(new BiConsumer<FoodItem, Limits2>() {
            @Override
            public void accept(final FoodItem foodItem, final Limits2 limits) {
                final int i = ingredientIndex.get();
                ingredientIndex.set(i + 1);

                final double amount;
                if (RANDOM.nextDouble() < mutationRate) {
                    final double minAmount = foodItem.roundToPortions(limits.getMin());
                    final double maxAmount = foodItem.roundToPortions(limits.getMax());
                    amount = foodItem.getRandomAmount(minAmount, maxAmount);
                } else {
                    final Meal meal = i < crossoverIngredientIndex ? maybeCrossoverMeal.get() : Meal.this;
                    amount = meal.getAmount(foodItem);
                }
                foodItems.set(foodItem, amount);
            }
        });
        return meal(mealTemplate, foodItems);
    }

    @Override
    public String toString() {
        return getName() + ": " + getIngredients().toString();
    }
}
