package diet;

import util.Evaluation;
import util.LazyValue;
import util.Limits2;
import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static diet.Addition.addition;
import static diet.Meal.meal;
import static diet.Meal.randomMeal;
import static util.Evaluation.evaluation;
import static util.Global.RANDOM;
import static util.Pair.pair;

public class DietPlan {
    private final ArrayList<Meal> meals;
    private final LazyValue<FoodItems> foodItems;
    private final LazyValue<FoodProperties> properties;
    private final LazyValue<Double> costs;

    public static DietPlan dietPlan(final ArrayList<Meal> meals) {
        return new DietPlan(meals);
    }

    private DietPlan(final ArrayList<Meal> meals) {
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
                double costs = 0;
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
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = new ArrayList<Pair<Integer, FoodItem>>();
        final int numberOfMeals = getNumberOfMeals();
        for (int i = 0; i < numberOfMeals; ++i) {
            final int mealIndex = i;
            final Ingredients ingredients = getMeal(i).getTemplate().getIngredients();
            ingredients.forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    variableIngredients.add(pair(mealIndex, foodItem));
                }
            });
        }
        return variableIngredients;
    }

    public Additions getBasicAdditions(final Function<DietPlan, Scores> evaluationFunction) {
        final Additions additions = new Additions();
        final Evaluation<DietPlan> dietPlanEvaluation = evaluation(this, evaluationFunction);
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = getVariableIngredients();
        for (final Pair<Integer, FoodItem> variableIngredient : variableIngredients) {
            final Optional<Addition> maybeAddition = addition(variableIngredient, dietPlanEvaluation);
            if (maybeAddition.isPresent()) {
                final Addition addition = maybeAddition.get();
                additions.add(addition, addition.getEvaluation().getTotalScore());
            }
        }
        return additions;
    }

    public DietPlan getWithChange(final int mealIndex, final FoodItem ingredient, final double change) {
        // TODO: Lazy values can be set using a new private constructor - the modifications are easy to calculate here.
        final int numberOfMeals = meals.size();
        final ArrayList<Meal> changedMeals = new ArrayList<Meal>(numberOfMeals);
        changedMeals.addAll(meals.subList(0, mealIndex));
        changedMeals.add(meals.get(mealIndex).getWithChange(ingredient, change));
        changedMeals.addAll(meals.subList(mealIndex + 1, numberOfMeals));
        return new DietPlan(changedMeals);
    }

    public Optional<DietPlan> addPortion(final int mealIndex, final FoodItem ingredient) {
        final Meal meal = meals.get(mealIndex);
        final double curAmount = meal.getAmount(ingredient);
        final double maxAmount = meal.getTemplate().getRoundedMaxAmount(ingredient);
        if (curAmount < maxAmount) {
            final DietPlan dietPlan = getWithChange(mealIndex, ingredient, ingredient.getPortionAmount());
            return Optional.of(dietPlan);
        } else {
            return Optional.empty();
        }
    }

    public Optional<DietPlan> addPortion(final Pair<Integer, FoodItem> ingredientId) {
        return addPortion(ingredientId.a(), ingredientId.b());
    }

    public Optional<DietPlan> addAddition(final Addition addition) {
        // TODO: Make method more efficient
        Optional<DietPlan> maybeDietPlan = Optional.of(this);
        final ArrayList<Pair<Integer, FoodItem>> additionalIngredients = addition.getIngredients();
        for (final Pair<Integer, FoodItem> ingredientId : additionalIngredients) {
            maybeDietPlan = maybeDietPlan.get().addPortion(ingredientId);
            if (!maybeDietPlan.isPresent()) {
                return maybeDietPlan;
            }
        }
        return maybeDietPlan;
    }

    public Optional<DietPlan> removePortion(final int mealIndex, final FoodItem ingredient) {
        final Meal meal = meals.get(mealIndex);
        final double curAmount = meal.getAmount(ingredient);
        final double minAmount = meal.getTemplate().getRoundedMinAmount(ingredient);
        if (curAmount > minAmount) {
            final DietPlan dietPlan = getWithChange(mealIndex, ingredient, -ingredient.getPortionAmount());
            return Optional.of(dietPlan);
        } else {
            return Optional.empty();
        }
    }

    public Optional<DietPlan> removePortion(final Pair<Integer, FoodItem> ingredientId) {
        return removePortion(ingredientId.a(), ingredientId.b());
    }

    public Optional<DietPlan> removePortions(final List<Pair<Integer, FoodItem>> removeList) {
        // TODO: Implement faster version that considers all items to remove at once
        DietPlan dietPlan = this;
        for (final Pair<Integer, FoodItem> remove : removeList) {
            final Optional<DietPlan> maybeDietPlan = dietPlan.removePortion(remove);
            if (maybeDietPlan.isPresent()) {
                dietPlan = maybeDietPlan.get();
            }
        }
        return dietPlan == this ? Optional.<DietPlan>empty() : Optional.of(dietPlan);
    }

    public DietPlan mate(final DietPlan partner,
                         final double mealMutationRate,
                         final ArrayList<MealTemplate> mealTemplates,
                         final double ingredientMutationRate) {
        final ArrayList<Meal> meals1 = getMeals();
        final ArrayList<Meal> meals2 = partner.getMeals();
        final int numberOfMeals1 = meals1.size();
        final int numberOfMeals2 = meals2.size();
        final int minNumberOfMeals = Math.min(numberOfMeals1, numberOfMeals2);
        final int crossoverMeal = RANDOM.nextInt(minNumberOfMeals + 1);
        final ArrayList<Meal> meals3 = new ArrayList<Meal>();
        copyMealsWithMutations(meals3, meals1, 0, crossoverMeal, mealMutationRate,
                mealTemplates, ingredientMutationRate);
        if (crossoverMeal < numberOfMeals2) {
            if (RANDOM.nextDouble() < mealMutationRate) {
                meals3.add(randomMeal(mealTemplates.get(RANDOM.nextInt(mealTemplates.size()))));
            } else if (crossoverMeal < numberOfMeals1) {
                final Meal meal1 = meals1.get(crossoverMeal);
                final Meal meal2 = meals2.get(crossoverMeal);
                final MealTemplate mealTemplate = meal1.getTemplate();
                if (mealTemplate.equals(meal2.getTemplate())) {
                    final FoodItems ingredients = new FoodItems();
                    final List<Pair<FoodItem, Limits2>> foodList = mealTemplate.getIngredients().asList();
                    final int numberOfFoods = foodList.size();
                    final int crossoverFood = RANDOM.nextInt(numberOfFoods + 1);
                    for (int i = 0; i < numberOfFoods; ++i) {
                        final Pair<FoodItem, Limits2> food = foodList.get(i);
                        final FoodItem foodItem = food.a();
                        if (RANDOM.nextDouble() < ingredientMutationRate) {
                            ingredients.set(foodItem, foodItem.getRandomAmount(food.b()));
                        } else if (i < crossoverFood) {
                            ingredients.set(foodItem, meal1.getAmount(foodItem));
                        } else {
                            ingredients.set(foodItem, meal2.getAmount(foodItem));
                        }
                    }
                    meals3.add(meal(mealTemplate, ingredients));
                } else {
                    meals3.add(meal2);
                }
            } else {
                meals3.add(meals2.get(crossoverMeal));
            }
            copyMealsWithMutations(meals3, meals2, crossoverMeal + 1, numberOfMeals2, mealMutationRate,
                    mealTemplates, ingredientMutationRate);
        }

        return dietPlan(meals3);
    }

    private void copyMealsWithMutations(final ArrayList<Meal> to,
                                        final ArrayList<Meal> from,
                                        final int start,
                                        final int end,
                                        final double mealMutationRate,
                                        final ArrayList<MealTemplate> mealTemplates,
                                        final double ingredientMutationRate) {
        for (int i = start; i < end; ++i) {
            if (RANDOM.nextDouble() < mealMutationRate) {
                to.add(randomMeal(mealTemplates.get(RANDOM.nextInt(mealTemplates.size()))));
            } else {
                final Meal meal = from.get(i);
                final Optional<Meal> maybeMutatedMeal = meal.getMutated(ingredientMutationRate);
                to.add(maybeMutatedMeal.orElse(meal));
            }
        }
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
        return "Meals:" + '\n' + getMeals() + '\n' + "Food items:" + '\n' + getFoodItems() + '\n' + "Properties:" +
                '\n' + getProperties() + '\n' + "Costs:" + '\n' + String.format("AUD %1$,.2f", getCosts());
    }
}
