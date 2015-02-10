package diet;

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

    @Override
    public String toString() {
        return name;
    }
}
