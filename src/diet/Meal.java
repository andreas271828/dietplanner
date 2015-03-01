package diet;

import util.LazyValue;
import util.Limits2;
import util.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;

import static util.Global.RANDOM;

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
                ingredients.set(foodItem, foodItem.getRandomAmount(limits));
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

    public Meal getWithChange(final FoodItem ingredient, final double change) {
        // TODO: Lazy values can be set using a new private constructor - the modifications are easy to calculate here.
        final FoodItems ingredients = getIngredients().getWithChange(ingredient, change);
        return new Meal(getTemplate(), ingredients);
    }

    public Optional<Meal> getMutated(final double mutationRate) {
        final FoodItems ingredients = new FoodItems();
        final MealTemplate mealTemplate = getTemplate();
        final ArrayList<Pair<FoodItem, Limits2>> foodList = mealTemplate.getIngredients().getList();
        boolean mutatedMeal = false;
        for (final Pair<FoodItem, Limits2> food : foodList) {
            final FoodItem foodItem = food.a();
            if (RANDOM.nextDouble() < mutationRate) {
                ingredients.set(foodItem, foodItem.getRandomAmount(food.b()));
                mutatedMeal = true;
            } else {
                ingredients.set(foodItem, getAmount(foodItem));
            }
        }
        return mutatedMeal ? Optional.of(meal(mealTemplate, ingredients)) : Optional.<Meal>empty();
    }

    @Override
    public String toString() {
        return getName() + ": " + getIngredients().toString();
    }
}
