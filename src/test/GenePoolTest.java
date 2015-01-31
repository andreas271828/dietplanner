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

public class GenePoolTest {
    public static void runTests() {
        final MealTemplates mealTemplates = getMealTemplates();
        final int numberOfMeals = 3;
        final Requirements requirements = new Requirements(PersonalDetails.ANDREAS, 1, numberOfMeals);
        final Function<Genome, Scores> fitnessFunction = getFitnessFunction(mealTemplates, requirements);
        final Optional<EvaluatedGenome> bestGenome = GenePool.findBestGenome(100, 50, fitnessFunction);
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
            }
        });
    }

    private static MealTemplates getMealTemplates() {
        final MealTemplates mealTemplates = new MealTemplates();

        mealTemplates.add(new MealTemplate("Anything") {
            @Override
            protected void addIngredients() {
                for (FoodItem foodItem : FoodItem.values()) {
                    addIngredient(foodItem, 0, foodItem.getAmountFromPortions(10));
                }
            }
        });

        return mealTemplates;
    }

    private static Function<Genome, Scores> getFitnessFunction(final MealTemplates mealTemplates, final Requirements requirements) {
        return new Function<Genome, Scores>() {
            @Override
            public Scores apply(Genome genome) {
                final Scores scores = new Scores();

                final int numberOfMeals = requirements.getNumberOfMeals();

                // Criteria for complete diet plan
                final DietPlan dietPlan = dietPlan(mealTemplates.computeMeals(numberOfMeals, genome));
                final FoodProperties dietPlanProperties = dietPlan.getProperties();
                final Optional<Integer> noMeal = Optional.empty();
                addScore(scores, Requirement.ALPHA_LINOLENIC_ACID, requirements, dietPlanProperties.get(FoodProperty.ALPHA_LINOLENIC_ACID), noMeal);
                addScore(scores, Requirement.ENERGY, requirements, dietPlanProperties.get(FoodProperty.ENERGY), noMeal);

                // Criteria for individual meals
                for (int i = 0; i < numberOfMeals; ++i) {
                    final Meal meal = dietPlan.getMeal(i);
                    final FoodProperties mealProperties = meal.getProperties();
                    final Optional<Integer> mealIndex = Optional.of(i);
                    addScore(scores, Requirement.MEAL_ALCOHOL, requirements, mealProperties.get(FoodProperty.ALCOHOL), mealIndex);
                    addScore(scores, Requirement.ENERGY, requirements, mealProperties.get(FoodProperty.ENERGY), mealIndex);
                }

                return scores;
            }
        };
    }

    private static void addScore(final Scores scores,
                                 final Requirement requirement,
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
                scores.addScore(sb.toString(), score);
            }
        });
    }
}
