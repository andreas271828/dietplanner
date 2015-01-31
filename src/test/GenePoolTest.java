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
        final Optional<EvaluatedGenome> bestGenome = GenePool.findBestGenome(100, 500, fitnessFunction);
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

                final Optional<Limits4> alphaLinolenicAcidLimits = requirements.getAlphaLinolenicAcidLimits();
                final Optional<Limits4> energyLimits = requirements.getEnergyLimits();
                final Optional<Limits4> mealAlcoholLimits = requirements.getMealAlcoholLimits();
                final Optional<Limits4> mealEnergyLimits = requirements.getMealEnergyLimits();

                // Criteria for complete diet plan
                final DietPlan dietPlan = dietPlan(mealTemplates.computeMeals(numberOfMeals, genome));
                final FoodProperties dietPlanProperties = dietPlan.getProperties();
                addScore(scores, "Alpha-linolenic acid", dietPlanProperties.get(FoodProperty.ALPHA_LINOLENIC_ACID), alphaLinolenicAcidLimits);
                addScore(scores, "Energy", dietPlanProperties.get(FoodProperty.ENERGY), energyLimits);

                // Criteria for individual meals
                for (int i = 0; i < numberOfMeals; ++i) {
                    final Meal meal = dietPlan.getMeal(i);
                    final FoodProperties mealProperties = meal.getProperties();
                    addMealScore(scores, "Alcohol", i, mealProperties.get(FoodProperty.ALCOHOL), mealAlcoholLimits);
                    addMealScore(scores, "Energy", i, mealProperties.get(FoodProperty.ENERGY), mealEnergyLimits);
                }

                return scores;
            }
        };
    }

    private static void addScore(final Scores scores,
                                 final String name,
                                 final double value,
                                 final Optional<Limits4> maybeLimits) {
        maybeLimits.ifPresent(new Consumer<Limits4>() {
            @Override
            public void accept(final Limits4 limits) {
                final double score = ScoreFunctions.standard(value, limits, 1000 * limits.getUpperCritical());
                scores.addScore(name, score);
            }
        });
    }

    private static void addMealScore(final Scores scores,
                                     final String name,
                                     final int index,
                                     final double value,
                                     final Optional<Limits4> maybeLimits) {
        addScore(scores, name + " (meal " + (index + 1) + ")", value, maybeLimits);
    }
}
