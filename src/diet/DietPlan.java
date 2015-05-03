package diet;

import util.LazyValue;
import util.Limits2;
import util.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;

import static util.Pair.pair;

public class DietPlan {
    private final Optional<DietPlanTemplate> maybeTemplate;
    private final ArrayList<Meal> meals;
    private final LazyValue<FoodItems> foodItems;
    private final LazyValue<FoodProperties> properties;
    private final LazyValue<Double> costs;

    public static DietPlan dietPlan(final ArrayList<Meal> meals) {
        return new DietPlan(Optional.<DietPlanTemplate>empty(), meals);
    }

    public static DietPlan dietPlan(final DietPlanTemplate template, final ArrayList<Meal> meals) {
        return new DietPlan(Optional.of(template), meals);
    }

    private DietPlan(final Optional<DietPlanTemplate> maybeTemplate, final ArrayList<Meal> meals) {
        this.maybeTemplate = maybeTemplate;
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

        costs = new LazyValue<Double>() {
            @Override
            protected Double compute() {
                double costs = 0.0;
                final ArrayList<Meal> meals = getMeals();
                for (final Meal meal : meals) {
                    costs += meal.getCosts();
                }
                return costs;
            }
        };
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public int getNumberOfMeals() {
        return meals.size();
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

    public double getCosts() {
        return costs.get();
    }

    public ArrayList<Pair<Integer, FoodItem>> getVariableIngredients() {
        if (maybeTemplate.isPresent()) {
            return maybeTemplate.get().getVariableIngredients();
        }

        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = new ArrayList<Pair<Integer, FoodItem>>();
        final int numberOfMeals = getNumberOfMeals();
        for (int i = 0; i < numberOfMeals; ++i) {
            final int mealIndex = i;
            final Ingredients ingredients = getMeal(mealIndex).getTemplate().getIngredients();
            ingredients.forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    variableIngredients.add(pair(mealIndex, foodItem));
                }
            });
        }
        return variableIngredients;
    }

    public DietPlan getWithChange(final int mealIndex, final FoodItem ingredient, final double newAmount) {
        // TODO: Lazy values can be set using a new private constructor - the modifications are easy to calculate here.
        final int numberOfMeals = meals.size();
        final ArrayList<Meal> changedMeals = new ArrayList<Meal>(numberOfMeals);
        changedMeals.addAll(meals.subList(0, mealIndex));
        changedMeals.add(meals.get(mealIndex).getWithChange(ingredient, newAmount));
        changedMeals.addAll(meals.subList(mealIndex + 1, numberOfMeals));
        return new DietPlan(maybeTemplate, changedMeals);
    }

    public Optional<DietPlan> addPortion(final int mealIndex, final FoodItem ingredient) {
        final Meal meal = meals.get(mealIndex);
        final int oldPortions = ingredient.amountToPortions(meal.getAmount(ingredient));
        final double newAmount = ingredient.portionsToAmount(oldPortions + 1);
        final double maxAmount = meal.getTemplate().getMaxAmount(ingredient);
        if (newAmount <= maxAmount) {
            final DietPlan dietPlan = getWithChange(mealIndex, ingredient, newAmount);
            return Optional.of(dietPlan);
        } else {
            return Optional.empty();
        }
    }

    public Optional<DietPlan> addPortion(final Pair<Integer, FoodItem> ingredientId) {
        return addPortion(ingredientId.a(), ingredientId.b());
    }

    public Optional<DietPlan> removePortion(final int mealIndex, final FoodItem ingredient) {
        final Meal meal = meals.get(mealIndex);
        final int oldPortions = ingredient.amountToPortions(meal.getAmount(ingredient));
        if (oldPortions > 0) {
            final double newAmount = ingredient.portionsToAmount(oldPortions - 1);
            final double minAmount = meal.getTemplate().getMinAmount(ingredient);
            if (newAmount >= minAmount) {
                final DietPlan dietPlan = getWithChange(mealIndex, ingredient, newAmount);
                return Optional.of(dietPlan);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public Optional<DietPlan> removePortion(final Pair<Integer, FoodItem> ingredientId) {
        return removePortion(ingredientId.a(), ingredientId.b());
    }

    public Scores getScores(final Requirements requirements) {
        final Scores scores = new Scores();

        // Criteria for complete diet plan
        final FoodProperties dietPlanProperties = getProperties();
        scores.addStandardScore(Requirement.ALPHA_LINOLENIC_ACID, dietPlanProperties.get(FoodProperty.ALPHA_LINOLENIC_ACID), requirements);
        scores.addStandardScore(Requirement.CALCIUM, dietPlanProperties.get(FoodProperty.CALCIUM), requirements);
        scores.addStandardScore(Requirement.CARBOHYDRATES, dietPlanProperties.get(FoodProperty.CARBOHYDRATES), requirements);
        scores.addStandardScore(Requirement.CHOLESTEROL, dietPlanProperties.get(FoodProperty.CHOLESTEROL), requirements);
        scores.addStandardScore(Requirement.COSTS, getCosts(), requirements);
        scores.addStandardScore(Requirement.DIETARY_FIBRE, dietPlanProperties.get(FoodProperty.DIETARY_FIBRE), requirements);
        scores.addStandardScore(Requirement.ENERGY, dietPlanProperties.get(FoodProperty.ENERGY), requirements);
        scores.addStandardScore(Requirement.FAT, dietPlanProperties.get(FoodProperty.FAT), requirements);
        scores.addStandardScore(Requirement.FOLATES, dietPlanProperties.get(FoodProperty.TOTAL_FOLATES), requirements);
        scores.addStandardScore(Requirement.IODINE, dietPlanProperties.get(FoodProperty.IODINE), requirements);
        scores.addStandardScore(Requirement.IRON, dietPlanProperties.get(FoodProperty.IRON), requirements);
        scores.addStandardScore(Requirement.LINOLEIC_ACID, dietPlanProperties.get(FoodProperty.LINOLEIC_ACID), requirements);
        scores.addStandardScore(Requirement.MAGNESIUM, dietPlanProperties.get(FoodProperty.MAGNESIUM), requirements);
        scores.addStandardScore(Requirement.NIACIN_DERIVED_EQUIVALENTS, dietPlanProperties.get(FoodProperty.NIACIN_DERIVED_EQUIVALENTS), requirements);
        scores.addStandardScore(Requirement.OMEGA_3_FATTY_ACIDS, dietPlanProperties.get(FoodProperty.OMEGA_3_FATTY_ACIDS), requirements);
        scores.addStandardScore(Requirement.PHOSPHORUS, dietPlanProperties.get(FoodProperty.PHOSPHORUS), requirements);
        scores.addStandardScore(Requirement.POTASSIUM, dietPlanProperties.get(FoodProperty.POTASSIUM), requirements);
        scores.addStandardScore(Requirement.PROTEIN, dietPlanProperties.get(FoodProperty.PROTEIN), requirements);
        scores.addStandardScore(Requirement.RIBOFLAVIN, dietPlanProperties.get(FoodProperty.RIBOFLAVIN), requirements);
        scores.addStandardScore(Requirement.SELENIUM, dietPlanProperties.get(FoodProperty.SELENIUM), requirements);
        scores.addStandardScore(Requirement.SODIUM, dietPlanProperties.get(FoodProperty.SODIUM), requirements);
        scores.addStandardScore(Requirement.SUGARS, dietPlanProperties.get(FoodProperty.SUGARS), requirements);
        scores.addStandardScore(Requirement.THIAMIN, dietPlanProperties.get(FoodProperty.THIAMIN), requirements);
        scores.addStandardScore(Requirement.TRANS_FATTY_ACIDS, dietPlanProperties.get(FoodProperty.TRANS_FATTY_ACIDS), requirements);
        scores.addStandardScore(Requirement.TRYPTOPHAN, dietPlanProperties.get(FoodProperty.TRYPTOPHAN), requirements);
        scores.addStandardScore(Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, dietPlanProperties.get(FoodProperty.VITAMIN_A_RETINOL_EQUIVALENTS), requirements);
        scores.addStandardScore(Requirement.VITAMIN_B12, dietPlanProperties.get(FoodProperty.VITAMIN_B12), requirements);
        scores.addStandardScore(Requirement.VITAMIN_B6, dietPlanProperties.get(FoodProperty.VITAMIN_B6), requirements);
        scores.addStandardScore(Requirement.VITAMIN_C, dietPlanProperties.get(FoodProperty.VITAMIN_C), requirements);
        scores.addStandardScore(Requirement.VITAMIN_E, dietPlanProperties.get(FoodProperty.VITAMIN_E), requirements);
        scores.addStandardScore(Requirement.ZINC, dietPlanProperties.get(FoodProperty.ZINC), requirements);

        // Criteria for individual meals
        final int numberOfMeals = getNumberOfMeals();
        for (int i = 0; i < numberOfMeals; ++i) {
            final Meal meal = getMeal(i);
            final FoodProperties mealProperties = meal.getProperties();
            scores.addStandardScore(Requirement.MEAL_ALCOHOL, mealProperties.get(FoodProperty.ALCOHOL), requirements);
            scores.addStandardScore(Requirement.MEAL_CAFFEINE, mealProperties.get(FoodProperty.CAFFEINE), requirements);
            scores.addStandardScore(Requirement.MEAL_CARBOHYDRATES, mealProperties.get(FoodProperty.CARBOHYDRATES), requirements);
            scores.addStandardScore(Requirement.MEAL_ENERGY, mealProperties.get(FoodProperty.ENERGY), requirements);
            scores.addStandardScore(Requirement.MEAL_FAT, mealProperties.get(FoodProperty.FAT), requirements);
            scores.addStandardScore(Requirement.MEAL_PROTEIN, mealProperties.get(FoodProperty.PROTEIN), requirements);
        }

        return scores;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Meals:\n");
        stringBuilder.append("======\n");
        final ArrayList<Meal> meals = getMeals();
        for (int i = 0; i < meals.size(); ++i) {
            final Meal meal = meals.get(i);
            stringBuilder.append("Meal ");
            stringBuilder.append(i + 1);
            stringBuilder.append(": ");
            stringBuilder.append(meal.getName());
            stringBuilder.append('\n');

            final FoodItems ingredients = meal.getIngredients();
            ingredients.forEach(new BiConsumer<FoodItem, Double>() {
                @Override
                public void accept(final FoodItem foodItem, final Double amount) {
                    stringBuilder.append('\t');
                    stringBuilder.append(foodItem.getName());
                    stringBuilder.append(": ");
                    stringBuilder.append(foodItem.amountToWeight(amount));
                    stringBuilder.append("g (");
                    stringBuilder.append(amount);
                    stringBuilder.append(")\n");
                }
            });
        }
        stringBuilder.append('\n');

        stringBuilder.append("Food items:\n");
        stringBuilder.append("===========\n");
        final FoodItems foodItems = getFoodItems();
        foodItems.forEach(new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(final FoodItem foodItem, final Double amount) {
                stringBuilder.append(foodItem.getName());
                stringBuilder.append(": ");
                stringBuilder.append(foodItem.amountToWeight(amount));
                stringBuilder.append("g (");
                stringBuilder.append(amount);
                stringBuilder.append(")\n");
            }
        });
        stringBuilder.append('\n');

        stringBuilder.append("Properties:\n");
        stringBuilder.append("===========\n");
        final FoodProperties properties = getProperties();
        properties.forEach(new BiConsumer<FoodProperty, Double>() {
            @Override
            public void accept(final FoodProperty foodProperty, final Double amount) {
                stringBuilder.append(foodProperty.getName());
                stringBuilder.append(": ");
                stringBuilder.append(amount);
                stringBuilder.append("\n");
            }
        });
        stringBuilder.append('\n');

        final String costsStr = "Costs: " + String.format("AUD %1$,.2f", getCosts());
        stringBuilder.append(costsStr);
        stringBuilder.append('\n');
        for (int i = 0; i < costsStr.length(); ++i) {
            stringBuilder.append('=');
        }
        stringBuilder.append('\n');

        return stringBuilder.toString();
    }
}
