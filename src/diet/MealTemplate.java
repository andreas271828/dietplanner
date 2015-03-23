package diet;

import java.util.ArrayList;

import static diet.FoodItem.*;
import static diet.Meal.meal;

public abstract class MealTemplate {
    public static final MealTemplate STANDARD_DAY_MIX = getStandardDayMixTemplate();
    public static final MealTemplate TEST_MIX = getTestMixTemplate();
    public static final MealTemplate SALAD = getSaladTemplate();
    public static final MealTemplate STIR_FRY_WITH_RICE = getStirFryWithRiceTemplate();
    public static final MealTemplate STIR_FRY_WITH_PASTA = getStirFryWithPastaTemplate();

    private final String name;
    private Ingredients ingredients;

    private MealTemplate(final String name) {
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

    public double getRoundedMinAmount(final FoodItem ingredient) {
        return getIngredients().getRoundedMinAmount(ingredient);
    }

    public double getRoundedMaxAmount(final FoodItem ingredient) {
        return getIngredients().getRoundedMaxAmount(ingredient);
    }

    public Meal getMinimalMeal() {
        return meal(this, getIngredients().getMinAmounts());
    }

    public ArrayList<Meal> getMinimalMeals(final int numberOfMeals) {
        final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            meals.add(getMinimalMeal());
        }
        return meals;
    }

    @Override
    public String toString() {
        return name;
    }

    private static MealTemplate getStandardDayMixTemplate() {
        return new MealTemplate("Standard Day Mix") {
            @Override
            protected void addIngredients() {
                addIngredient(COLES_APPLE_RED_DELICIOUS, 0.0, 3.0);
                addIngredientByWeight(COLES_ASPARAGUS_GREEN, 0.0, 200.0);
                addIngredient(COLES_AVOCADO, 0.0, 3.0);
                addIngredientByWeight(COLES_BACON, 0.0, 200.0);
                addIngredient(COLES_BANANA, 0.0, 3.0);
                addIngredientByWeight(COLES_BASIL_DRIED, 0.0, 5.0);
                addIngredientByWeight(COLES_BASIL, 0.0, 10.0);
                addIngredientByWeight(COLES_BEAN_GREEN, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_FILLET_STEAK, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_HEART, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_KIDNEY, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_ROASTING_CUTS, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_RUMP_STEAK, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_T_BONE_STEAK, 0.0, 400.0);
                addIngredient(COLES_BEETROOT, 0.0, 3.0);
                addIngredientByWeight(COLES_BLUEBERRY_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_BOK_CHOY, 0.0, 300.0);
                addIngredientByWeight(COLES_BREAD_MIXED_GRAIN, 0.0, 400.0);
                addIngredientByWeight(COLES_BROCCOLI, 0.0, 400.0);
                addIngredientByWeight(COLES_BRUSSELS_SPROUT, 0.0, 300.0);
                addIngredientByWeight(COLES_BUTTER, 0.0, 200.0);
                addIngredient(COLES_CABBAGE_RED, 0.0, 1.0);
                addIngredient(COLES_CABBAGE_WHITE, 0.0, 1.0);
                addIngredient(COLES_CAPSICUM_GREEN, 0.0, 2.0);
                addIngredient(COLES_CAPSICUM_RED, 0.0, 2.0);
                addIngredient(COLES_CARROT, 0.0, 3.0);
                addIngredient(COLES_CAULIFLOWER, 0.0, 1.0);
                addIngredientByWeight(COLES_CELERY, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_CHEDDAR, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_COLBY, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_COTTAGE_REDUCED_FAT, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_CREAM, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_FETTA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_GOAT, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_GOUDA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_HALOUMI, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_MOZZARELLA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_PARMESAN, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_RICOTTA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_SWISS, 0.0, 400.0);
                addIngredientByWeight(COLES_CHICKEN_BREAST_FREE_RANGE, 0.0, 500.0);
                addIngredientByWeight(COLES_CHICKEN_DRUMSTICK_FREE_RANGE, 0.0, 500.0);
                addIngredientByWeight(COLES_CHICKEN_LIVER, 0.0, 400.0);
                addIngredientByWeight(COLES_CHICKEN_THIGH_FREE_RANGE, 0.0, 500.0);
                addIngredient(COLES_CHICKEN_FREE_RANGE, 0.0, 750.0);
                addIngredientByWeight(COLES_CHICKEN_WING, 0.0, 500.0);
                addIngredientByWeight(COLES_CHICKPEA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHOCOLATE_DARK_ORGANIC, 0.0, 200.0);
                addIngredientByWeight(COLES_COCONUT_OIL_ORGANIC, 0.0, 100.0);
                addIngredientByWeight(COLES_CREAM_PURE, 0.0, 300.0);
                addIngredientByWeight(COLES_CREAM_DOUBLE, 0.0, 300.0);
                addIngredientByWeight(COLES_CREAM_SOUR, 0.0, 500.0);
                addIngredient(COLES_CUCUMBER, 0.0, 2.0);
                addIngredientByWeight(COLES_DILL, 0.0, 20.0);
                addIngredientByWeight(COLES_EGG_CHICKEN_FREE_RANGE, 0.0, 300.0);
                addIngredientByWeight(COLES_GARLIC, 0.0, 50.0);
                addIngredientByWeight(COLES_GHEE, 0.0, 100.0);
                addIngredientByWeight(COLES_GINGER, 0.0, 50.0);
                addIngredientByWeight(COLES_HAM_LEG, 0.0, 400.0);
                addIngredientByWeight(COLES_HONEY, 0.0, 100.0);
                addIngredientByWeight(COLES_KALE, 0.0, 300.0);
                addIngredientByWeight(COLES_KANGAROO, 0.0, 500.0);
                addIngredient(COLES_KIWIFRUIT, 0.0, 2.0);
                addIngredientByWeight(COLES_LEEK, 0.0, 200.0);
                addIngredient(COLES_LEMON, 0.0, 3.0);
                addIngredientByWeight(COLES_LENTIL, 0.0, 500.0);
                addIngredientByWeight(COLES_LETTUCE, 0.0, 400.0);
                addIngredient(COLES_LIME, 0.0, 2.0);
                addIngredientByWeight(COLES_MACKEREL, 0.0, 500.0);
                addIngredient(COLES_MANDARIN, 0.0, 5.0);
                addIngredient(COLES_MANGO, 0.0, 3.0);
                addIngredientByWeight(COLES_MAYONNAISE, 0.0, 400.0);
                addIngredientByWeight(COLES_MILK_ALMOND, 0.0, 500.0);
                addIngredientByWeight(COLES_MILK_COW, 0.0, 500.0);
                addIngredientByWeight(COLES_MILK_GOAT, 0.0, 500.0);
                addIngredientByWeight(COLES_MUESLI_FRUIT, 0.0, 500.0);
                addIngredientByWeight(COLES_MUESLI_FRUIT_NUTS, 0.0, 500.0);
                addIngredientByWeight(COLES_MUSHROOM, 0.0, 400.0);
                addIngredient(COLES_MUSSEL_DRAINED, 0.0, 1.0);
                addIngredientByWeight(COLES_NUT_ALMOND, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_CASHEW, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_MACADAMIA, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_PECAN, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_WALNUT, 0.0, 200.0);
                addIngredientByWeight(COLES_OIL_OLIVE, 0.0, 300.0);
                addIngredientByWeight(COLES_OKRA, 0.0, 300.0);
                addIngredientByWeight(COLES_OLIVE_GREEN, 0.0, 200.0);
                addIngredient(COLES_ONION, 0.0, 2.0);
                addIngredient(COLES_ORANGE, 0.0, 4.0);
                addIngredientByWeight(COLES_OREGANO, 0.0, 5.0);
                addIngredientByWeight(COLES_PAPRIKA, 0.0, 10.0);
                addIngredientByWeight(COLES_PARSLEY, 0.0, 20.0);
                addIngredientByWeight(COLES_PASTA_WHOLEMEAL, 0.0, 400.0);
                addIngredientByWeight(COLES_PEA_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_PEPPER, 0.0, 10.0);
                addIngredientByWeight(COLES_POTATO, 0.0, 500.0);
                addIngredientByWeight(COLES_PRAWN_KING, 0.0, 300.0);
                addIngredientByWeight(COLES_PUMPKIN, 0.0, 500.0);
                addIngredientByWeight(COLES_TAMARI, 0.0, 100.0);
                addIngredientByWeight(COLES_QUINOA_ORGANIC, 0.0, 400.0);
                addIngredientByWeight(COLES_RADISH, 0.0, 200.0);
                addIngredientByWeight(COLES_RASPBERRY_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_RHUBARB, 0.0, 300.0);
                addIngredientByWeight(COLES_RICE_BROWN, 0.0, 400.0);
                addIngredientByWeight(COLES_ROCKET, 0.0, 400.0);
                addIngredientByWeight(COLES_SALAMI_HUNGARIAN, 0.0, 400.0);
                addIngredientByWeight(COLES_SALMON, 0.0, 400.0);
                addIngredientByWeight(COLES_SALMON_SMOKED, 0.0, 300.0);
                addIngredientByWeight(COLES_SALT_SEA, 0.0, 10.0);
                addIngredient(COLES_SARDINE_IN_OIL_UNDRAINED, 0.0, 1.0);
                addIngredientByWeight(COLES_SAUSAGE_BEEF, 0.0, 400.0);
                addIngredientByWeight(COLES_SAUSAGE_CHORIZO, 0.0, 400.0);
                addIngredientByWeight(COLES_SAUSAGE_PORK, 0.0, 400.0);
                addIngredientByWeight(COLES_SEED_CHIA, 0.0, 300.0);
                addIngredientByWeight(COLES_SEED_LINSEED, 0.0, 300.0);
                addIngredientByWeight(COLES_SEED_SUNFLOWER, 0.0, 300.0);
                addIngredientByWeight(COLES_SHALLOT, 0.0, 300.0);
                addIngredientByWeight(COLES_SNOW_PEA, 0.0, 300.0);
                addIngredientByWeight(COLES_SPINACH, 0.0, 400.0);
                addIngredient(COLES_SQUASH, 0.0, 3.0);
                addIngredientByWeight(COLES_SQUID, 0.0, 400.0);
                addIngredientByWeight(COLES_STRAWBERRY_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_STRAWBERRY, 0.0, 300.0);
                addIngredientByWeight(COLES_SWEET_POTATO, 0.0, 500.0);
                addIngredientByWeight(COLES_SWEETCORN_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_TEA_CHAI, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_GREEN, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_CHAMOMILE, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_MINT, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_BLACK, 0.0, 10.0);
                addIngredientByWeight(COLES_TOMATO_CHERRY, 0.0, 400.0);
                addIngredient(COLES_TOMATO, 0.0, 400.0);
                addIngredientByWeight(COLES_TROUT, 0.0, 400.0);
                addIngredient(COLES_TUNA_IN_OIL_DRAINED, 0.0, 1.0);
                addIngredient(COLES_TUNA_IN_WATER_DRAINED, 0.0, 1.0);
                addIngredientByWeight(COLES_VEAL, 0.0, 400.0);
                addIngredientByWeight(COLES_WATERCRESS, 0.0, 200.0);
                addIngredientByWeight(COLES_YOGHURT_GREEK_STYLE_NATURAL, 0.0, 400.0);
                addIngredient(COLES_ZUCCHINI, 0.0, 1.0);
            }
        };
    }

    private static MealTemplate getTestMixTemplate() {
        return new MealTemplate("Test Mix") {
            @Override
            protected void addIngredients() {
                addIngredientByWeight(TEST_CARBOHYDRATES, 0.0, 1000.0);
                addIngredientByWeight(TEST_FAT, 0.0, 1000.0);
                addIngredientByWeight(TEST_PROTEIN, 0.0, 1000.0);
            }
        };
    }

    private static MealTemplate getSaladTemplate() {
        return new MealTemplate("Salad") {
            @Override
            protected void addIngredients() {
                addIngredient(COLES_APPLE_RED_DELICIOUS, 0.0, 2.0);
                addIngredient(COLES_CAPSICUM_RED, 0.0, 1.0);
                addIngredient(COLES_CARROT, 0.0, 2.0);
                addIngredient(COLES_LEMON, 0.0, 0.5);
                addIngredientByWeight(COLES_MAYONNAISE, 0.0, 300.0);
                addIngredientByWeight(COLES_OIL_OLIVE, 1.0, 100.0);
                addIngredientByWeight(COLES_SALT_SEA, 0.0, 5.0);
                addIngredientByWeight(COLES_SPINACH, 0.0, 200.0);
                addIngredient(COLES_TUNA_IN_OIL_DRAINED, 0.0, 2.0);
            }
        };
    }

    private static MealTemplate getStirFryWithRiceTemplate() {
        return new MealTemplate("Stir-fry with rice") {
            @Override
            protected void addIngredients() {
                addIngredients(getBasicStirFryIngredients());
                addIngredientByWeight(COLES_RICE_BROWN, 0.0, 200.0);
            }
        };
    }

    private static MealTemplate getStirFryWithPastaTemplate() {
        return new MealTemplate("Stir-fry with pasta") {
            @Override
            protected void addIngredients() {
                addIngredients(getBasicStirFryIngredients());
                addIngredientByWeight(COLES_PASTA_WHOLEMEAL, 0.0, 200.0);
            }
        };
    }

    private static Ingredients getBasicStirFryIngredients() {
        final Ingredients basicStirFryIngredients = new Ingredients();
        basicStirFryIngredients.addByWeight(COLES_BROCCOLI, 0.0, 300.0);
        basicStirFryIngredients.add(COLES_CARROT, 0.0, 2.0);
        basicStirFryIngredients.addByWeight(COLES_COCONUT_OIL_ORGANIC, 0.0, 50.0);
        return basicStirFryIngredients;
    }
}
