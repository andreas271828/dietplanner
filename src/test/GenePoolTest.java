package test;

import diet.*;
import evolution.GenePool;
import evolution.Genome;
import util.Evaluation;
import util.Pair;
import util.Scores;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static diet.DietPlan.dietPlan;
import static diet.FoodItem.*;
import static diet.MealTemplate.STANDARD_DAY_MIX_AS_LIST;
import static diet.MealTemplates.computeMeals;

public class GenePoolTest {
    public static void runTests() {
        final ArrayList<MealTemplate> mealTemplates = STANDARD_DAY_MIX_AS_LIST;
        final int numberOfMeals = 7;
        final Requirements requirements = new Requirements(PersonalDetails.ANDREAS, 7, numberOfMeals);
        final Function<Genome, Scores> fitnessFunction = getFitnessFunction(mealTemplates, requirements);
        final Optional<Evaluation<Genome>> bestGenome = GenePool.findBestGenome(10, fitnessFunction,
                new Function<Pair<Integer, GenePool>, Boolean>() {
                    @Override
                    public Boolean apply(final Pair<Integer, GenePool> generationInfo) {
                        final int generation = generationInfo.a();
                        final Optional<Evaluation<Genome>> bestGenome = generationInfo.b().getBestGenome();
                        bestGenome.ifPresent(new Consumer<Evaluation<Genome>>() {
                            @Override
                            public void accept(Evaluation<Genome> bestGenome) {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("Best genome in generation ");
                                sb.append(generation);
                                sb.append(" (genome length = ");
                                sb.append(bestGenome.getObject().getGenomeLength());
                                sb.append("): ");
                                sb.append(bestGenome.getTotalScore());
                                System.out.println(sb);
                            }
                        });
                        return generation < 500;
                    }
                });
        bestGenome.ifPresent(new Consumer<Evaluation<Genome>>() {
            @Override
            public void accept(final Evaluation<Genome> bestGenome) {
                final DietPlan dietPlan = dietPlan(computeMeals(mealTemplates, numberOfMeals, bestGenome.getObject()));
                final Scores scores = bestGenome.getScores();

                System.out.println();
                System.out.println(dietPlan);
                System.out.println();
                System.out.println("Scores:");
                System.out.println(scores);
                System.out.println("Total score: " + scores.getTotalScore() + " / " + scores.getWeightSum());
            }
        });
    }

    private static ArrayList<MealTemplate> getMealTemplatesAnything() {
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();

        mealTemplates.add(new MealTemplate("Anything") {
            @Override
            protected void addIngredients() {
                for (FoodItem foodItem : FoodItem.values()) {
                    addIngredientByWeight(foodItem, 0.0, 100.0);
                }
            }
        });

        return mealTemplates;
    }

    private static ArrayList<MealTemplate> getMealTemplates() {
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();

        final Ingredients basicSaladIngredients = new Ingredients();
        basicSaladIngredients.add(COLES_APPLE_RED_DELICIOUS, 0.0, 2.0);
        basicSaladIngredients.add(COLES_LEMON, 0.0, 0.5);
        basicSaladIngredients.addByWeight(COLES_OIL_OLIVE, 1.0, 100.0);
        basicSaladIngredients.addByWeight(COLES_SPINACH, 20.0, 200.0);
        basicSaladIngredients.add(COLES_CAPSICUM_RED, 0.0, 1.0);
        basicSaladIngredients.add(COLES_CARROT, 0.0, 1.0);

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
        mealTemplates.add(new MealTemplate("Avocado Plus") {
            @Override
            protected void addIngredients() {
                addIngredient(COLES_AVOCADO, 0.5, 2.0);
                addIngredient(COLES_LEMON, 0.1, 0.5);
                addIngredientByWeight(COLES_SALT_SEA, 0.1, 5.0);
                addIngredientByWeight(COLES_PEPPER, 0.1, 3);
                addIngredientByWeight(COLES_OIL_OLIVE, 0.0, 20.0);
            }
        });
        mealTemplates.add(new MealTemplate("Stir-fry") {
            @Override
            protected void addIngredients() {
                addIngredient(COLES_BROCCOLI, 0.0, 1.0);
                addIngredient(COLES_CARROT, 0.0, 1.0);
                addIngredientByWeight(COLES_SALT_SEA, 0.0, 10.0);
                addIngredientByWeight(COLES_COCONUT_OIL_ORGANIC, 0.0, 50.0);
            }
        });
        mealTemplates.add(new MealTemplate("Salami Plus") {
            @Override
            protected void addIngredients() {
                addIngredientByWeight(COLES_SALAMI_HUNGARIAN, 50.0, 500.0);
                addIngredientByWeight(COLES_CHEESE_COLBY, 0.0, 200.0);
                addIngredientByWeight(COLES_SPINACH, 0.0, 200.0);
            }
        });

        return mealTemplates;
    }

    private static Function<Genome, Scores> getFitnessFunction(final ArrayList<MealTemplate> mealTemplates,
                                                               final Requirements requirements) {
        return new Function<Genome, Scores>() {
            @Override
            public Scores apply(final Genome genome) {
                final DietPlan dietPlan = dietPlan(computeMeals(mealTemplates, requirements.getNumberOfMeals(), genome));
                return dietPlan.getScores(requirements);
            }
        };
    }
}
