package diet;

import util.LazyValue;

public class Meal {
    private String name;
    private FoodItems ingredients;
    private final LazyValue<FoodProperties> properties;
    private final LazyValue<Double> costs;

    public Meal(final String name, final FoodItems ingredients) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public FoodItems getIngredients() {
        return ingredients;
    }

    public FoodProperties getProperties() {
        return properties.get();
    }

    public double getCosts() {
        return costs.get();
    }

    @Override
    public String toString() {
        return name + ": " + ingredients.toString();
    }
}
