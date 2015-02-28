package test;

import diet.*;
import util.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static diet.DietPlan.dietPlan;
import static diet.DietPlanChange.dietPlanChange;
import static diet.MealTemplate.STANDARD_DAY_MIX;
import static diet.MealTemplate.TEST_MIX;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static util.Evaluation.evaluation;
import static util.Evaluations.evaluations;
import static util.Global.RANDOM;
import static util.Global.nextRandomDoubleInclOne;

public class OptimizationTest {
    private static final Optional<ArrayList<FoodItems>> NO_CHANGE = Optional.empty();
    private static final Requirements REQUIREMENTS = new Requirements(PersonalDetails.ANDREAS, 7, 7);
    private static final int NUMBER_OF_MEALS = REQUIREMENTS.getNumberOfMeals();
    private static final Function<DietPlanChange, Scores> FITNESS_FUNCTION_1 = getFitnessFunction1(REQUIREMENTS);
    private static final Function<DietPlan, Scores> FITNESS_FUNCTION_2 = getFitnessFunction2(REQUIREMENTS);

    public static void runTests() {
        // test1();
        // test2();
        test3();
    }

    private static Function<DietPlanChange, Scores> getFitnessFunction1(final Requirements requirements) {
        return new Function<DietPlanChange, Scores>() {
            @Override
            public Scores apply(final DietPlanChange dietPlanChange) {
                return dietPlanChange.getDietPlan().getScores(requirements);
            }
        };
    }

    private static Function<DietPlan, Scores> getFitnessFunction2(final Requirements requirements) {
        return new Function<DietPlan, Scores>() {
            @Override
            public Scores apply(final DietPlan dietPlan) {
                return dietPlan.getScores(requirements);
            }
        };
    }

    private static void test1() {
        final ArrayList<Evaluation<DietPlanChange>> generation0 = new ArrayList<Evaluation<DietPlanChange>>();
        for (int i = 0; i < 50; ++i) {
            final DietPlan dietPlan = dietPlan(STANDARD_DAY_MIX.getRandomMeals(NUMBER_OF_MEALS));
            final DietPlanChange dietPlanChange = dietPlanChange(dietPlan, NO_CHANGE);
            generation0.add(evaluation(dietPlanChange, FITNESS_FUNCTION_1));
        }
        Evaluations<DietPlanChange> curGeneration = evaluations(generation0);
        for (int i = 2; i <= 100; ++i) {
            curGeneration = getNextGeneration(curGeneration);
        }

        curGeneration.getBest().ifPresent(new Consumer<Evaluation<DietPlanChange>>() {
            @Override
            public void accept(final Evaluation<DietPlanChange> evaluatedDietPlanChange) {
                System.out.println(evaluatedDietPlanChange.getObject().getDietPlan());
                System.out.println("Scores:");
                System.out.println(evaluatedDietPlanChange.getScores());
                final Scores scores = evaluatedDietPlanChange.getScores();
                System.out.println("Total score: " + scores.getTotalScore() + " of " + scores.getWeightSum());
            }
        });
    }

    private static Evaluations<DietPlanChange> getNextGeneration(final Evaluations<DietPlanChange> evaluatedGeneration) {
        final ArrayList<Evaluation<DietPlanChange>> nextGeneration = new ArrayList<Evaluation<DietPlanChange>>();

        // Repeat successful mutations
        final ArrayList<Evaluation<DietPlanChange>> prevGeneration = evaluatedGeneration.getEvaluations();
        for (Evaluation<DietPlanChange> evaluation : prevGeneration) {
            final Optional<ArrayList<FoodItems>> changes = evaluation.getObject().getChanges();
            if (changes.isPresent()) {
                final DietPlan dietPlan = evaluation.getObject().getDietPlan();
                final ArrayList<Meal> meals = dietPlan.getMeals();
                final DietPlan newDietPlan = dietPlan(STANDARD_DAY_MIX.applyChanges(meals, changes.get()));
                final DietPlanChange newDietPlanChange = dietPlanChange(newDietPlan, changes);
                final Evaluation<DietPlanChange> newEvaluation = evaluation(newDietPlanChange, FITNESS_FUNCTION_1);
                if (newEvaluation.getTotalScore() > evaluation.getTotalScore()) {
                    nextGeneration.add(newEvaluation);
                } else {
                    // Don't try the same change again
                    final DietPlanChange dietPlanWithoutChange = dietPlanChange(dietPlan, NO_CHANGE);
                    nextGeneration.add(evaluation(dietPlanWithoutChange, evaluation.getScores()));
                }
            }
        }

        // Create mixed diet plans
        final int generationSize = prevGeneration.size();
        for (int i = 0; i < generationSize; ++i) {
            final Optional<DietPlanChange> maybeDietPlanChange1 = evaluatedGeneration.selectProbabilistically();
            final Optional<DietPlanChange> maybeDietPlanChange2 = evaluatedGeneration.selectProbabilistically();
            if (maybeDietPlanChange1.isPresent() && maybeDietPlanChange2.isPresent()) {
                final ArrayList<Meal> meals1 = maybeDietPlanChange1.get().getDietPlan().getMeals();
                final ArrayList<Meal> meals2 = maybeDietPlanChange2.get().getDietPlan().getMeals();
                final DietPlan dietPlan = dietPlan(STANDARD_DAY_MIX.getRandomMix(meals1, meals2));
                final DietPlanChange dietPlanChange = dietPlanChange(dietPlan, NO_CHANGE);
                nextGeneration.add(evaluation(dietPlanChange, FITNESS_FUNCTION_1));
            }
        }

        // Create mutated diet plans
        for (final Evaluation<DietPlanChange> evaluatedDietPlanChange : prevGeneration) {
            final ArrayList<Meal> meals = evaluatedDietPlanChange.getObject().getDietPlan().getMeals();
            final ArrayList<FoodItems> changes = STANDARD_DAY_MIX.getRandomChanges(nextRandomDoubleInclOne(), NUMBER_OF_MEALS);
            final DietPlan dietPlan = dietPlan(STANDARD_DAY_MIX.applyChanges(meals, changes));
            final DietPlanChange dietPlanChange = dietPlanChange(dietPlan, Optional.of(changes));
            nextGeneration.add(evaluation(dietPlanChange, FITNESS_FUNCTION_1));
        }

        return evaluations(nextGeneration, generationSize);
    }

    private static void test2() {
        Optional<Evaluation<DietPlan>> bestDietPlan = Optional.empty();

        // Create random initial population
        final int populationSize = 100;
        final int maxPopulationSize = 200;
        final MealTemplate mealTemplate = TEST_MIX;
        final ArrayList<Evaluation<DietPlan>> population = new ArrayList<Evaluation<DietPlan>>();
        for (int i = 0; i < populationSize; ++i) {
            final DietPlan dietPlan = dietPlan(mealTemplate.getMinimalistMeals(NUMBER_OF_MEALS));
            population.add(evaluation(dietPlan, FITNESS_FUNCTION_2));
        }

        // Evolve population
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();
        mealTemplates.add(mealTemplate);
        for (int i = 0; i < 1000000; ++i) {
            final int index1 = RANDOM.nextInt(population.size());
            final int index2 = RANDOM.nextInt(population.size());
            final Evaluation<DietPlan> parent1 = population.get(index1);
            final Evaluation<DietPlan> parent2 = population.get(index2);
            final double fitness1 = parent1.getTotalScore();
            final double fitness2 = parent2.getTotalScore();
            if (!bestDietPlan.isPresent() || fitness1 > bestDietPlan.get().getTotalScore()) {
                bestDietPlan = Optional.of(parent1);
            }
            if (fitness2 > bestDietPlan.get().getTotalScore()) {
                bestDietPlan = Optional.of(parent2);
            }
            final double bestFitness = bestDietPlan.get().getTotalScore();
            final double relFitness1 = fitness1 / bestFitness;
            final double relFitness2 = fitness2 / bestFitness;
            final double difference = parent1.getObject().getDifference(parent2.getObject());
            final double differenceFactor = pow(0.9, difference);
            if (RANDOM.nextDouble() < relFitness1 * relFitness2 * differenceFactor) {
                final DietPlan offspring = parent1.getObject().mate(parent2.getObject(), 0.002, mealTemplates, 0.01);
                population.add(evaluation(offspring, FITNESS_FUNCTION_2));
            }

            final double curPopulationSize = min(population.size(), maxPopulationSize);
            final double survivalFactor = (maxPopulationSize - curPopulationSize) / (maxPopulationSize - populationSize);
            final boolean remove1 = RANDOM.nextDouble() >= relFitness1 * survivalFactor;
            final boolean remove2 = RANDOM.nextDouble() >= relFitness2 * survivalFactor;
            if (remove1) {
                population.remove(index1);
                if (remove2) {
                    if (index2 < index1) {
                        population.remove(index2);
                    } else if (index2 > index1) {
                        population.remove(index2 - 1);
                    }
                }
            } else if (remove2) {
                population.remove(index2);
            }

            if (i % 1000 == 0) {
                System.out.println(i + ": " + bestDietPlan.get().getTotalScore());
            }
        }

        bestDietPlan.ifPresent(new Consumer<Evaluation<DietPlan>>() {
            @Override
            public void accept(final Evaluation<DietPlan> evaluatedDietPlan) {
                final DietPlan dietPlan = evaluatedDietPlan.getObject();
                final Scores scores = evaluatedDietPlan.getScores();
                System.out.println(dietPlan);
                System.out.println("Scores:");
                System.out.println(scores);
                System.out.println("Total score: " + scores.getTotalScore() + " of " + scores.getWeightSum());
            }
        });
    }

    private static void test3() {
        final DietPlan dietPlan = dietPlan(TEST_MIX.getMinimalistMeals(NUMBER_OF_MEALS));
        final ArrayList<Meal> meals = dietPlan.getMeals();
        final int mealIndex = RANDOM.nextInt(meals.size());
        final Meal meal = meals.get(mealIndex);
        final ArrayList<Pair<FoodItem, Limits2>> ingredients = meal.getTemplate().getIngredients().getList();
        final int ingredientIndex = RANDOM.nextInt(ingredients.size());
        final Pair<FoodItem, Limits2> ingredient = ingredients.get(ingredientIndex);
        final FoodItem foodItem = ingredient.a();
        final double amountToAdd = (1.0 - RANDOM.nextDouble()) * (ingredient.b().getMax() - meal.getAmount(foodItem));
        final double change = foodItem.roundToPortions(amountToAdd);
        final DietPlan dietPlan2 = change > 0.0 ? dietPlan.getWithChange(mealIndex, foodItem, change) : dietPlan;

        final Evaluation<DietPlan> dietPlanEvaluation = evaluation(dietPlan2, FITNESS_FUNCTION_2);
        final Scores scores = dietPlanEvaluation.getScores();
        System.out.println(dietPlan2);
        System.out.println("Scores:");
        System.out.println(scores);
        System.out.println("Total score: " + scores.getTotalScore() + " of " + scores.getWeightSum());
    }
}
