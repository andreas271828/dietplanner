package diet;

import util.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import static diet.Meal.*;
import static util.Global.RANDOM;

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

    public DietPlan getWithChange(final int mealIndex, final FoodItem ingredient, final double change) {
        // TODO: Lazy values can be set using a new private constructor - the modifications are easy to calculate here.
        final int numberOfMeals = meals.size();
        final ArrayList<Meal> changedMeals = new ArrayList<Meal>(numberOfMeals);
        changedMeals.addAll(meals.subList(0, mealIndex));
        changedMeals.add(mealWithChange(meals.get(mealIndex), ingredient, change));
        changedMeals.addAll(meals.subList(mealIndex + 1, numberOfMeals));
        return new DietPlan(changedMeals);
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
                    final ArrayList<Pair<FoodItem, Limits2>> foodList = mealTemplate.getIngredients().getList();
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
                final Optional<Meal> maybeMutatedMeal = mutatedMeal(meal, ingredientMutationRate);
                to.add(maybeMutatedMeal.orElse(meal));
            }
        }
    }

    public Scores getScores(final Requirements requirements) {
        final Scores scores = new Scores();

        // Criteria for complete diet plan
        final FoodProperties dietPlanProperties = getProperties();
        final Optional<Integer> noMealIndex = Optional.empty();
        // addScore(scores, Requirement.ALPHA_LINOLENIC_ACID, requirements, dietPlanProperties.get(FoodProperty.ALPHA_LINOLENIC_ACID), noMealIndex);
        // addScore(scores, Requirement.CALCIUM, requirements, dietPlanProperties.get(FoodProperty.CALCIUM), noMealIndex);
        addScore(scores, Requirement.CARBOHYDRATES, requirements, dietPlanProperties.get(FoodProperty.CARBOHYDRATES), noMealIndex);
        // addScore(scores, Requirement.CHOLESTEROL, requirements, dietPlanProperties.get(FoodProperty.CHOLESTEROL), noMealIndex);
        // addScore(scores, Requirement.COSTS, requirements, getCosts(), noMealIndex);
        // addScore(scores, Requirement.DIETARY_FIBRE, requirements, dietPlanProperties.get(FoodProperty.DIETARY_FIBRE), noMealIndex);
        addScore(scores, Requirement.ENERGY, requirements, dietPlanProperties.get(FoodProperty.ENERGY), noMealIndex);
        // addScore(scores, Requirement.FAT, requirements, dietPlanProperties.get(FoodProperty.FAT), noMealIndex);
        // addScore(scores, Requirement.FOLATES, requirements, dietPlanProperties.get(FoodProperty.TOTAL_FOLATES), noMealIndex);
        // addScore(scores, Requirement.IODINE, requirements, dietPlanProperties.get(FoodProperty.IODINE), noMealIndex);
        // addScore(scores, Requirement.IRON, requirements, dietPlanProperties.get(FoodProperty.IRON), noMealIndex);
        // addScore(scores, Requirement.LINOLEIC_ACID, requirements, dietPlanProperties.get(FoodProperty.LINOLEIC_ACID), noMealIndex);
        // addScore(scores, Requirement.MAGNESIUM, requirements, dietPlanProperties.get(FoodProperty.MAGNESIUM), noMealIndex);
        // addScore(scores, Requirement.NIACIN_DERIVED_EQUIVALENTS, requirements, dietPlanProperties.get(FoodProperty.NIACIN_DERIVED_EQUIVALENTS), noMealIndex);
        // addScore(scores, Requirement.OMEGA_3_FATTY_ACIDS, requirements, dietPlanProperties.get(FoodProperty.OMEGA_3_FATTY_ACIDS), noMealIndex);
        // addScore(scores, Requirement.PHOSPHORUS, requirements, dietPlanProperties.get(FoodProperty.PHOSPHORUS), noMealIndex);
        // addScore(scores, Requirement.POTASSIUM, requirements, dietPlanProperties.get(FoodProperty.POTASSIUM), noMealIndex);
        addScore(scores, Requirement.PROTEIN, requirements, dietPlanProperties.get(FoodProperty.PROTEIN), noMealIndex);
        // addScore(scores, Requirement.RIBOFLAVIN, requirements, dietPlanProperties.get(FoodProperty.RIBOFLAVIN), noMealIndex);
        // addScore(scores, Requirement.SELENIUM, requirements, dietPlanProperties.get(FoodProperty.SELENIUM), noMealIndex);
        // addScore(scores, Requirement.SODIUM, requirements, dietPlanProperties.get(FoodProperty.SODIUM), noMealIndex);
        // addScore(scores, Requirement.SUGARS, requirements, dietPlanProperties.get(FoodProperty.SUGARS), noMealIndex);
        // addScore(scores, Requirement.THIAMIN, requirements, dietPlanProperties.get(FoodProperty.THIAMIN), noMealIndex);
        // addScore(scores, Requirement.TRANS_FATTY_ACIDS, requirements, dietPlanProperties.get(FoodProperty.TRANS_FATTY_ACIDS), noMealIndex);
        // addScore(scores, Requirement.TRYPTOPHAN, requirements, dietPlanProperties.get(FoodProperty.TRYPTOPHAN), noMealIndex);
        // addScore(scores, Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_A_RETINOL_EQUIVALENTS), noMealIndex);
        // addScore(scores, Requirement.VITAMIN_B12, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_B12), noMealIndex);
        // addScore(scores, Requirement.VITAMIN_B6, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_B6), noMealIndex);
        // addScore(scores, Requirement.VITAMIN_C, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_C), noMealIndex);
        // addScore(scores, Requirement.VITAMIN_E, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_E), noMealIndex);
        // addScore(scores, Requirement.ZINC, requirements, dietPlanProperties.get(FoodProperty.ZINC), noMealIndex);

        // Criteria for individual meals
        final int numberOfMeals = getNumberOfMeals();
        for (int i = 0; i < numberOfMeals; ++i) {
            final Meal meal = getMeal(i);
            final FoodProperties mealProperties = meal.getProperties();
            final Optional<Integer> mealIndex = Optional.of(i);
            // addScore(scores, Requirement.MEAL_ALCOHOL, requirements, mealProperties.get(FoodProperty.ALCOHOL), mealIndex);
            // addScore(scores, Requirement.MEAL_CAFFEINE, requirements, mealProperties.get(FoodProperty.CAFFEINE), mealIndex);
            // addScore(scores, Requirement.MEAL_CARBOHYDRATES, requirements, mealProperties.get(FoodProperty.CARBOHYDRATES), mealIndex);
            // addScore(scores, Requirement.MEAL_ENERGY, requirements, mealProperties.get(FoodProperty.ENERGY), mealIndex);
            // addScore(scores, Requirement.MEAL_FAT, requirements, mealProperties.get(FoodProperty.FAT), mealIndex);
            // addScore(scores, Requirement.MEAL_PROTEIN, requirements, mealProperties.get(FoodProperty.PROTEIN), mealIndex);
        }

        return scores;
    }

    private static void addScore(final Scores scores,
                                 final Requirement requirement,
                                 final Requirements requirements,
                                 final double value,
                                 final Optional<Integer> mealIndex) {
        requirements.getParams(requirement).ifPresent(new Consumer<ScoreParams>() {
            @Override
            public void accept(final ScoreParams scoreParams) {
                final double score = ScoreFunctions.standard(value, scoreParams, 1000 * scoreParams.getUpperCritical());
                final StringBuilder sb = new StringBuilder(requirement.getName());
                mealIndex.ifPresent(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer mealIndex) {
                        sb.append(" (meal ");
                        sb.append(mealIndex + 1);
                        sb.append(")");
                    }
                });
                scores.addScore(score, scoreParams.getWeight(), sb.toString());
            }
        });
    }

    @Override
    public String toString() {
        return "Meals:" + '\n' + getMeals() + '\n' + "Food items:" + '\n' + getFoodItems() + '\n' + "Properties:" +
                '\n' + getProperties() + '\n' + "Costs:" + '\n' + String.format("AUD %1$,.2f", getCosts());
    }

    public double getDifference(final DietPlan dietPlan) {
        double difference = 0.0;
        final int maxNumberOfMeals = Math.max(getNumberOfMeals(), dietPlan.getNumberOfMeals());
        for (int i = 0; i < maxNumberOfMeals; ++i) {
            if (!getMeal(i).getTemplate().equals(dietPlan.getMeal(i).getTemplate())) {
                difference += 1.0;
            }
        }
        return difference;
    }
}
