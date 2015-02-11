package test;

import diet.*;
import evolution.EvaluatedGenome;
import evolution.GenePool;
import evolution.Genome;
import util.Limits4;
import util.ScoreFunctions;
import util.Scores;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static diet.DietPlan.dietPlan;
import static diet.FoodItem.*;

public class GenePoolTest {
    public static void runTests() {
        final MealTemplates mealTemplates = getMealTemplates();
        final int numberOfMeals = 21;
        final Requirements requirements = new Requirements(PersonalDetails.ANDREAS, 7, numberOfMeals);
        final Function<Genome, Scores> fitnessFunction = getFitnessFunction(mealTemplates, requirements);
        final Optional<EvaluatedGenome> bestGenome = GenePool.findBestGenome(10, 500, fitnessFunction);
        bestGenome.ifPresent(new Consumer<EvaluatedGenome>() {
            @Override
            public void accept(final EvaluatedGenome bestGenome) {
                final DietPlan dietPlan = dietPlan(mealTemplates.computeMeals(numberOfMeals, bestGenome.getGenome()));
                final Scores scores = bestGenome.getScores();

                System.out.println();
                System.out.println("Meals:");
                System.out.println(dietPlan.getMeals());
                System.out.println();
                System.out.println("Foods items:");
                System.out.println(dietPlan.getFoodItems());
                System.out.println();
                System.out.println("Properties:");
                System.out.println(dietPlan.getProperties());
                System.out.println();
                System.out.println("Scores:");
                System.out.println(scores);
                System.out.println("Total score: " + scores.getTotalScore() + " / " + scores.getWeightSum());
            }
        });
    }

    private static MealTemplates getMealTemplatesAnything() {
        final MealTemplates mealTemplates = new MealTemplates();

        mealTemplates.add(new MealTemplate("Anything") {
            @Override
            protected void addIngredients() {
                for (FoodItem foodItem : FoodItem.values()) {
                    addIngredient(foodItem, 0, foodItem.toAmount(100));
                }
            }
        });

        return mealTemplates;
    }

    private static MealTemplates getMealTemplates() {
        final MealTemplates mealTemplates = new MealTemplates();

        final Ingredients basicSaladIngredients = new Ingredients();
        basicSaladIngredients.add(COLES_APPLE_RED_DELICIOUS, 0.0, 2.0);
        basicSaladIngredients.add(COLES_LEMON, 0.0, 0.5);
        basicSaladIngredients.addByWeight(COLES_OIL_OLIVE, 1.0, 100.0);
        basicSaladIngredients.addByWeight(COLES_SPINACH, 20.0, 200.0);

        mealTemplates.add(new MealTemplate("Salad with mayonnaise") {
            @Override
            protected void addIngredients() {
                addIngredients(basicSaladIngredients);
                addIngredientByWeight(COLES_MAYONNAISE, 20.0, 300.0);
            }
        });
        mealTemplates.add(new MealTemplate("Salad with sour cream") {
            @Override
            protected void addIngredients() {
                addIngredients(basicSaladIngredients);
                addIngredientByWeight(COLES_CREAM_SOUR, 100.0, 500.0);
            }
        });

        return mealTemplates;
    }

    private static Function<Genome, Scores> getFitnessFunction(final MealTemplates mealTemplates, final Requirements requirements) {
        return new Function<Genome, Scores>() {
            @Override
            public Scores apply(final Genome genome) {
                final Scores scores = new Scores();

                final int numberOfMeals = requirements.getNumberOfMeals();

                // Criteria for complete diet plan
                final DietPlan dietPlan = dietPlan(mealTemplates.computeMeals(numberOfMeals, genome));
                final FoodProperties dietPlanProperties = dietPlan.getProperties();
                final Optional<Integer> noMeal = Optional.empty();
                addScore(scores, Requirement.ALPHA_LINOLENIC_ACID, 1.0, requirements, dietPlanProperties.get(FoodProperty.ALPHA_LINOLENIC_ACID), noMeal);
                addScore(scores, Requirement.CALCIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.CALCIUM), noMeal);
                addScore(scores, Requirement.CARBOHYDRATES, 1.0, requirements, dietPlanProperties.get(FoodProperty.CARBOHYDRATES), noMeal);
                addScore(scores, Requirement.CHOLESTEROL, 1.0, requirements, dietPlanProperties.get(FoodProperty.CHOLESTEROL), noMeal);
                addScore(scores, Requirement.COSTS, 1.0, requirements, dietPlan.getCosts(), noMeal);
                addScore(scores, Requirement.DIETARY_FIBRE, 1.0, requirements, dietPlanProperties.get(FoodProperty.DIETARY_FIBRE), noMeal);
                addScore(scores, Requirement.ENERGY, 10.0, requirements, dietPlanProperties.get(FoodProperty.ENERGY), noMeal);
                addScore(scores, Requirement.FAT, 1.0, requirements, dietPlanProperties.get(FoodProperty.FAT), noMeal);
                addScore(scores, Requirement.FOLATES, 1.0, requirements, dietPlanProperties.get(FoodProperty.TOTAL_FOLATES), noMeal);
                addScore(scores, Requirement.IODINE, 1.0, requirements, dietPlanProperties.get(FoodProperty.IODINE), noMeal);
                addScore(scores, Requirement.IRON, 1.0, requirements, dietPlanProperties.get(FoodProperty.IRON), noMeal);
                addScore(scores, Requirement.LINOLEIC_ACID, 1.0, requirements, dietPlanProperties.get(FoodProperty.LINOLEIC_ACID), noMeal);
                addScore(scores, Requirement.MAGNESIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.MAGNESIUM), noMeal);
                addScore(scores, Requirement.NIACIN_DERIVED_EQUIVALENTS, 1.0, requirements, dietPlanProperties.get(FoodProperty.NIACIN_DERIVED_EQUIVALENTS), noMeal);
                addScore(scores, Requirement.OMEGA_3_FATTY_ACIDS, 1.0, requirements, dietPlanProperties.get(FoodProperty.OMEGA_3_FATTY_ACIDS), noMeal);
                addScore(scores, Requirement.PHOSPHORUS, 1.0, requirements, dietPlanProperties.get(FoodProperty.PHOSPHORUS), noMeal);
                addScore(scores, Requirement.POTASSIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.POTASSIUM), noMeal);
                addScore(scores, Requirement.PROTEIN, 1.0, requirements, dietPlanProperties.get(FoodProperty.PROTEIN), noMeal);
                addScore(scores, Requirement.RIBOFLAVIN, 1.0, requirements, dietPlanProperties.get(FoodProperty.RIBOFLAVIN), noMeal);
                addScore(scores, Requirement.SELENIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.SELENIUM), noMeal);
                addScore(scores, Requirement.SODIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.SODIUM), noMeal);
                addScore(scores, Requirement.SUGARS, 1.0, requirements, dietPlanProperties.get(FoodProperty.SUGARS), noMeal);
                addScore(scores, Requirement.THIAMIN, 1.0, requirements, dietPlanProperties.get(FoodProperty.THIAMIN), noMeal);
                addScore(scores, Requirement.TRANS_FATTY_ACIDS, 1.0, requirements, dietPlanProperties.get(FoodProperty.TRANS_FATTY_ACIDS), noMeal);
                addScore(scores, Requirement.TRYPTOPHAN, 1.0, requirements, dietPlanProperties.get(FoodProperty.TRYPTOPHAN), noMeal);
                addScore(scores, Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_A_RETINOL_EQUIVALENTS), noMeal);
                addScore(scores, Requirement.VITAMIN_B12, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_B12), noMeal);
                addScore(scores, Requirement.VITAMIN_B6, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_B6), noMeal);
                addScore(scores, Requirement.VITAMIN_C, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_C), noMeal);
                addScore(scores, Requirement.VITAMIN_E, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_E), noMeal);
                addScore(scores, Requirement.ZINC, 1.0, requirements, dietPlanProperties.get(FoodProperty.ZINC), noMeal);

                // Criteria for individual meals
                for (int i = 0; i < numberOfMeals; ++i) {
                    final Meal meal = dietPlan.getMeal(i);
                    final FoodProperties mealProperties = meal.getProperties();
                    final Optional<Integer> mealIndex = Optional.of(i);
                    addScore(scores, Requirement.MEAL_ALCOHOL, 1.0, requirements, mealProperties.get(FoodProperty.ALCOHOL), mealIndex);
                    addScore(scores, Requirement.MEAL_CAFFEINE, 1.0, requirements, mealProperties.get(FoodProperty.CAFFEINE), mealIndex);
                    addScore(scores, Requirement.MEAL_CARBOHYDRATES, 1.0, requirements, mealProperties.get(FoodProperty.CARBOHYDRATES), mealIndex);
                    addScore(scores, Requirement.MEAL_ENERGY, 1.0, requirements, mealProperties.get(FoodProperty.ENERGY), mealIndex);
                    addScore(scores, Requirement.MEAL_FAT, 1.0, requirements, mealProperties.get(FoodProperty.FAT), mealIndex);
                    addScore(scores, Requirement.MEAL_PROTEIN, 1.0, requirements, mealProperties.get(FoodProperty.PROTEIN), mealIndex);
                }

                return scores;
            }
        };
    }

    private static void addScore(final Scores scores,
                                 final Requirement requirement,
                                 final double weight,
                                 final Requirements requirements,
                                 final double value,
                                 final Optional<Integer> mealIndex) {
        requirements.getLimits(requirement).ifPresent(new Consumer<Limits4>() {
            @Override
            public void accept(final Limits4 limits) {
                final double score = ScoreFunctions.standard(value, limits, 1000 * limits.getUpperCritical());
                final StringBuilder sb = new StringBuilder(requirement.getName());
                mealIndex.ifPresent(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer mealIndex) {
                        sb.append(" (meal ");
                        sb.append(mealIndex + 1);
                        sb.append(")");
                    }
                });
                scores.addScore(score, weight, sb.toString());
            }
        });
    }
}
