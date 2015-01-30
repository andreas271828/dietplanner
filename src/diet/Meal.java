package diet;

import util.LazyValue;

public class Meal {
    private String name;
    private FoodItems ingredients;
    private final LazyValue<FoodProperties> properties;

    public Meal(final String name, final FoodItems ingredients) {
        this.name = name;
        this.ingredients = ingredients;

        properties = new LazyValue<FoodProperties>() {
            @Override
            protected FoodProperties compute() {
                return ingredients.getProperties();
            }
        };
    }

    public FoodItems getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return name + ": " + ingredients.toString();
    }

    public FoodProperties getProperties() {
        return properties.get();
    }
}
