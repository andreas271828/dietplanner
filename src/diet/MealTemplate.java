package diet;

import static diet.FoodItem.*;
import static diet.Meal.meal;

public enum MealTemplate {
    SALAD("Salad", getSaladIngredients()),
    STIR_FRY_WITH_PASTA("Stir-fry with pasta", getStirFryWithPastaIngredients()),
    STIR_FRY_WITH_RICE("Stir-fry with rice", getStirFryWithRiceIngredients()),

    STANDARD_DAY_MIX("Standard Day Mix", getStandardDayMixIngredients()),
    TEST_MIX("Test Mix", getTestMixIngredients());

    private final String name;
    private final Ingredients ingredients;

    MealTemplate(final String name, final Ingredients ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public Ingredients getIngredients() {
        return ingredients;
    }

    public double getRoundedMinAmount(final FoodItem ingredient) {
        return ingredients.getRoundedMinAmount(ingredient);
    }

    public double getRoundedMaxAmount(final FoodItem ingredient) {
        return ingredients.getRoundedMaxAmount(ingredient);
    }

    public Meal getMinimalMeal() {
        return meal(this, ingredients.getMinAmounts());
    }

    @Override
    public String toString() {
        return name;
    }

    private static Ingredients getSaladIngredients() {
        final Ingredients ingredients = new Ingredients();
        ingredients.add(COLES_APPLE_RED_DELICIOUS, 0.0, 2.0);
        ingredients.add(COLES_CAPSICUM_RED, 0.0, 1.0);
        ingredients.add(COLES_CARROT, 0.0, 2.0);
        ingredients.add(COLES_LEMON, 0.0, 0.5);
        ingredients.addByWeight(COLES_MAYONNAISE, 0.0, 300.0);
        ingredients.addByWeight(COLES_OIL_OLIVE, 1.0, 100.0);
        ingredients.addByWeight(COLES_SALT_SEA, 0.0, 5.0);
        ingredients.addByWeight(COLES_SPINACH, 0.0, 200.0);
        ingredients.add(COLES_TUNA_IN_OIL_DRAINED, 0.0, 2.0);
        return ingredients;
    }

    private static Ingredients getStirFryWithRiceIngredients() {
        final Ingredients ingredients = new Ingredients();
        ingredients.addAll(getStirFryIngredients());
        ingredients.addByWeight(COLES_RICE_BROWN, 0.0, 200.0);
        return ingredients;
    }

    private static Ingredients getStirFryWithPastaIngredients() {
        final Ingredients ingredients = new Ingredients();
        ingredients.addAll(getStirFryIngredients());
        ingredients.addByWeight(COLES_PASTA_WHOLEMEAL, 0.0, 200.0);
        return ingredients;
    }

    private static Ingredients getStirFryIngredients() {
        final Ingredients ingredients = new Ingredients();
        ingredients.addByWeight(COLES_BROCCOLI, 0.0, 300.0);
        ingredients.add(COLES_CARROT, 0.0, 2.0);
        ingredients.addByWeight(COLES_COCONUT_OIL_ORGANIC, 0.0, 50.0);
        return ingredients;
    }

    private static Ingredients getStandardDayMixIngredients() {
        final Ingredients ingredients = new Ingredients();
        ingredients.add(COLES_APPLE_RED_DELICIOUS, 0.0, 3.0);
        ingredients.addByWeight(COLES_ASPARAGUS_GREEN, 0.0, 200.0);
        ingredients.add(COLES_AVOCADO, 0.0, 3.0);
        ingredients.addByWeight(COLES_BACON, 0.0, 200.0);
        ingredients.add(COLES_BANANA, 0.0, 3.0);
        ingredients.addByWeight(COLES_BASIL_DRIED, 0.0, 5.0);
        ingredients.addByWeight(COLES_BASIL, 0.0, 10.0);
        ingredients.addByWeight(COLES_BEAN_GREEN, 0.0, 400.0);
        ingredients.addByWeight(COLES_BEEF_FILLET_STEAK, 0.0, 400.0);
        ingredients.addByWeight(COLES_BEEF_HEART, 0.0, 400.0);
        ingredients.addByWeight(COLES_BEEF_KIDNEY, 0.0, 400.0);
        ingredients.addByWeight(COLES_BEEF_ROASTING_CUTS, 0.0, 400.0);
        ingredients.addByWeight(COLES_BEEF_RUMP_STEAK, 0.0, 400.0);
        ingredients.addByWeight(COLES_BEEF_T_BONE_STEAK, 0.0, 400.0);
        ingredients.add(COLES_BEETROOT, 0.0, 3.0);
        ingredients.addByWeight(COLES_BLUEBERRY_FROZEN, 0.0, 300.0);
        ingredients.addByWeight(COLES_BOK_CHOY, 0.0, 300.0);
        ingredients.addByWeight(COLES_BREAD_MIXED_GRAIN, 0.0, 400.0);
        ingredients.addByWeight(COLES_BROCCOLI, 0.0, 400.0);
        ingredients.addByWeight(COLES_BRUSSELS_SPROUT, 0.0, 300.0);
        ingredients.addByWeight(COLES_BUTTER, 0.0, 200.0);
        ingredients.add(COLES_CABBAGE_RED, 0.0, 1.0);
        ingredients.add(COLES_CABBAGE_WHITE, 0.0, 1.0);
        ingredients.add(COLES_CAPSICUM_GREEN, 0.0, 2.0);
        ingredients.add(COLES_CAPSICUM_RED, 0.0, 2.0);
        ingredients.add(COLES_CARROT, 0.0, 3.0);
        ingredients.add(COLES_CAULIFLOWER, 0.0, 1.0);
        ingredients.addByWeight(COLES_CELERY, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_CHEDDAR, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_COLBY, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_COTTAGE_REDUCED_FAT, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_CREAM, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_FETTA, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_GOAT, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_GOUDA, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_HALOUMI, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_MOZZARELLA, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_PARMESAN, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_RICOTTA, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHEESE_SWISS, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHICKEN_BREAST_FREE_RANGE, 0.0, 500.0);
        ingredients.addByWeight(COLES_CHICKEN_DRUMSTICK_FREE_RANGE, 0.0, 500.0);
        ingredients.addByWeight(COLES_CHICKEN_LIVER, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHICKEN_THIGH_FREE_RANGE, 0.0, 500.0);
        ingredients.add(COLES_CHICKEN_FREE_RANGE, 0.0, 750.0);
        ingredients.addByWeight(COLES_CHICKEN_WING, 0.0, 500.0);
        ingredients.addByWeight(COLES_CHICKPEA, 0.0, 400.0);
        ingredients.addByWeight(COLES_CHOCOLATE_DARK_ORGANIC, 0.0, 200.0);
        ingredients.addByWeight(COLES_COCONUT_OIL_ORGANIC, 0.0, 100.0);
        ingredients.addByWeight(COLES_CREAM_PURE, 0.0, 300.0);
        ingredients.addByWeight(COLES_CREAM_DOUBLE, 0.0, 300.0);
        ingredients.addByWeight(COLES_CREAM_SOUR, 0.0, 500.0);
        ingredients.add(COLES_CUCUMBER, 0.0, 2.0);
        ingredients.addByWeight(COLES_DILL, 0.0, 20.0);
        ingredients.addByWeight(COLES_EGG_CHICKEN_FREE_RANGE, 0.0, 300.0);
        ingredients.addByWeight(COLES_GARLIC, 0.0, 50.0);
        ingredients.addByWeight(COLES_GHEE, 0.0, 100.0);
        ingredients.addByWeight(COLES_GINGER, 0.0, 50.0);
        ingredients.addByWeight(COLES_HAM_LEG, 0.0, 400.0);
        ingredients.addByWeight(COLES_HONEY, 0.0, 100.0);
        ingredients.addByWeight(COLES_KALE, 0.0, 300.0);
        ingredients.addByWeight(COLES_KANGAROO, 0.0, 500.0);
        ingredients.add(COLES_KIWIFRUIT, 0.0, 2.0);
        ingredients.addByWeight(COLES_LEEK, 0.0, 200.0);
        ingredients.add(COLES_LEMON, 0.0, 3.0);
        ingredients.addByWeight(COLES_LENTIL, 0.0, 500.0);
        ingredients.addByWeight(COLES_LETTUCE, 0.0, 400.0);
        ingredients.add(COLES_LIME, 0.0, 2.0);
        ingredients.addByWeight(COLES_MACKEREL, 0.0, 500.0);
        ingredients.add(COLES_MANDARIN, 0.0, 5.0);
        ingredients.add(COLES_MANGO, 0.0, 3.0);
        ingredients.addByWeight(COLES_MAYONNAISE, 0.0, 400.0);
        ingredients.addByWeight(COLES_MILK_ALMOND, 0.0, 500.0);
        ingredients.addByWeight(COLES_MILK_COW, 0.0, 500.0);
        ingredients.addByWeight(COLES_MILK_GOAT, 0.0, 500.0);
        ingredients.addByWeight(COLES_MUESLI_FRUIT, 0.0, 500.0);
        ingredients.addByWeight(COLES_MUESLI_FRUIT_NUTS, 0.0, 500.0);
        ingredients.addByWeight(COLES_MUSHROOM, 0.0, 400.0);
        ingredients.add(COLES_MUSSEL_DRAINED, 0.0, 1.0);
        ingredients.addByWeight(COLES_NUT_ALMOND, 0.0, 200.0);
        ingredients.addByWeight(COLES_NUT_CASHEW, 0.0, 200.0);
        ingredients.addByWeight(COLES_NUT_MACADAMIA, 0.0, 200.0);
        ingredients.addByWeight(COLES_NUT_PECAN, 0.0, 200.0);
        ingredients.addByWeight(COLES_NUT_WALNUT, 0.0, 200.0);
        ingredients.addByWeight(COLES_OIL_OLIVE, 0.0, 300.0);
        ingredients.addByWeight(COLES_OKRA, 0.0, 300.0);
        ingredients.addByWeight(COLES_OLIVE_GREEN, 0.0, 200.0);
        ingredients.add(COLES_ONION, 0.0, 2.0);
        ingredients.add(COLES_ORANGE, 0.0, 4.0);
        ingredients.addByWeight(COLES_OREGANO, 0.0, 5.0);
        ingredients.addByWeight(COLES_PAPRIKA, 0.0, 10.0);
        ingredients.addByWeight(COLES_PARSLEY, 0.0, 20.0);
        ingredients.addByWeight(COLES_PASTA_WHOLEMEAL, 0.0, 400.0);
        ingredients.addByWeight(COLES_PEA_FROZEN, 0.0, 300.0);
        ingredients.addByWeight(COLES_PEPPER, 0.0, 10.0);
        ingredients.addByWeight(COLES_POTATO, 0.0, 500.0);
        ingredients.addByWeight(COLES_PRAWN_KING, 0.0, 300.0);
        ingredients.addByWeight(COLES_PUMPKIN, 0.0, 500.0);
        ingredients.addByWeight(COLES_TAMARI, 0.0, 100.0);
        ingredients.addByWeight(COLES_QUINOA_ORGANIC, 0.0, 400.0);
        ingredients.addByWeight(COLES_RADISH, 0.0, 200.0);
        ingredients.addByWeight(COLES_RASPBERRY_FROZEN, 0.0, 300.0);
        ingredients.addByWeight(COLES_RHUBARB, 0.0, 300.0);
        ingredients.addByWeight(COLES_RICE_BROWN, 0.0, 400.0);
        ingredients.addByWeight(COLES_ROCKET, 0.0, 400.0);
        ingredients.addByWeight(COLES_SALAMI_HUNGARIAN, 0.0, 400.0);
        ingredients.addByWeight(COLES_SALMON, 0.0, 400.0);
        ingredients.addByWeight(COLES_SALMON_SMOKED, 0.0, 300.0);
        ingredients.addByWeight(COLES_SALT_SEA, 0.0, 10.0);
        ingredients.add(COLES_SARDINE_IN_OIL_UNDRAINED, 0.0, 1.0);
        ingredients.addByWeight(COLES_SAUSAGE_BEEF, 0.0, 400.0);
        ingredients.addByWeight(COLES_SAUSAGE_CHORIZO, 0.0, 400.0);
        ingredients.addByWeight(COLES_SAUSAGE_PORK, 0.0, 400.0);
        ingredients.addByWeight(COLES_SEED_CHIA, 0.0, 300.0);
        ingredients.addByWeight(COLES_SEED_LINSEED, 0.0, 300.0);
        ingredients.addByWeight(COLES_SEED_SUNFLOWER, 0.0, 300.0);
        ingredients.addByWeight(COLES_SHALLOT, 0.0, 300.0);
        ingredients.addByWeight(COLES_SNOW_PEA, 0.0, 300.0);
        ingredients.addByWeight(COLES_SPINACH, 0.0, 400.0);
        ingredients.add(COLES_SQUASH, 0.0, 3.0);
        ingredients.addByWeight(COLES_SQUID, 0.0, 400.0);
        ingredients.addByWeight(COLES_STRAWBERRY_FROZEN, 0.0, 300.0);
        ingredients.addByWeight(COLES_STRAWBERRY, 0.0, 300.0);
        ingredients.addByWeight(COLES_SWEET_POTATO, 0.0, 500.0);
        ingredients.addByWeight(COLES_SWEETCORN_FROZEN, 0.0, 300.0);
        ingredients.addByWeight(COLES_TEA_CHAI, 0.0, 10.0);
        ingredients.addByWeight(COLES_TEA_GREEN, 0.0, 10.0);
        ingredients.addByWeight(COLES_TEA_CHAMOMILE, 0.0, 10.0);
        ingredients.addByWeight(COLES_TEA_MINT, 0.0, 10.0);
        ingredients.addByWeight(COLES_TEA_BLACK, 0.0, 10.0);
        ingredients.addByWeight(COLES_TOMATO_CHERRY, 0.0, 400.0);
        ingredients.add(COLES_TOMATO, 0.0, 400.0);
        ingredients.addByWeight(COLES_TROUT, 0.0, 400.0);
        ingredients.add(COLES_TUNA_IN_OIL_DRAINED, 0.0, 1.0);
        ingredients.add(COLES_TUNA_IN_WATER_DRAINED, 0.0, 1.0);
        ingredients.addByWeight(COLES_VEAL, 0.0, 400.0);
        ingredients.addByWeight(COLES_WATERCRESS, 0.0, 200.0);
        ingredients.addByWeight(COLES_YOGHURT_GREEK_STYLE_NATURAL, 0.0, 400.0);
        ingredients.add(COLES_ZUCCHINI, 0.0, 1.0);
        return ingredients;
    }

    private static Ingredients getTestMixIngredients() {
        final Ingredients ingredients = new Ingredients();
        ingredients.addByWeight(TEST_CARBOHYDRATES, 0.0, 1000.0);
        ingredients.addByWeight(TEST_FAT, 0.0, 1000.0);
        ingredients.addByWeight(TEST_PROTEIN, 0.0, 1000.0);
        return ingredients;
    }
}
