package test;

import diet.*;
import util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static diet.DietPlan.dietPlan;
import static diet.DietPlanChange.dietPlanChange;
import static diet.MealTemplate.STANDARD_DAY_MIX;
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
        test2();
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
        final int populationSize = 50;
        final MealTemplate mealTemplate = STANDARD_DAY_MIX;
        final int numberOfMeals = NUMBER_OF_MEALS;
        final Function<DietPlan, Scores> fitnessFunction = FITNESS_FUNCTION_2;
        final ArrayList<Evaluation<DietPlan>> population = new ArrayList<Evaluation<DietPlan>>();
        for (int i = 0; i < populationSize; ++i) {
            population.add(generateCandidate3(mealTemplate, numberOfMeals, fitnessFunction));
            System.out.println("Generated diet plan " + (i + 1) + ".");
        }

        // Evolve population
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();
        mealTemplates.add(mealTemplate);
        final double baseMealMutationRate = 0.01;
        final double baseIngredientMutationRate = 0.05;
        final double maxMutationFactor = 5.0;
        double mutationFactor = 1.0;
        for (int i = 0; i < 100 && !population.isEmpty(); ++i) {
            final int curPopulationSize = population.size();
            final int index1 = RANDOM.nextInt(curPopulationSize);
            final int index2 = RANDOM.nextInt(curPopulationSize);
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
            final double mealMutationRate = baseMealMutationRate * mutationFactor;
            final double ingredientMutationRate = baseIngredientMutationRate * mutationFactor;
            final DietPlan dietPlan = parent1.getObject().mate(parent2.getObject(), mealMutationRate, mealTemplates, ingredientMutationRate);
            final Evaluation<DietPlan> offspring = evaluation(dietPlan, fitnessFunction);
            final double fitness3 = offspring.getTotalScore();
            if (fitness3 > fitness1 || fitness3 > fitness2) {
                population.add(offspring);
                population.remove(fitness2 > fitness1 ? index1 : index2);
                mutationFactor = 1.0;
            } else if (mutationFactor < maxMutationFactor) {
                mutationFactor *= 1.1;
                if (mutationFactor > maxMutationFactor) {
                    mutationFactor = maxMutationFactor;
                }
            }

            if ((i + 1) % 1 == 0) {
                final Scores scores = bestDietPlan.get().getScores();
                System.out.println("Score after " + (i + 1) + " iterations: " + scores.getTotalScore() + " of " + scores.getWeightSum());
            }
        }

        bestDietPlan.ifPresent(new Consumer<Evaluation<DietPlan>>() {
            @Override
            public void accept(final Evaluation<DietPlan> evaluatedDietPlan) {
                printDietPlanEvaluation(evaluatedDietPlan);
            }
        });
    }

    private static Evaluation<DietPlan> generateCandidate1(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        return evaluation(dietPlan(mealTemplate.getMinimalistMeals(numberOfMeals)), fitnessFunction);
    }

    private static Evaluation<DietPlan> generateCandidate2(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        final DietPlan minDietPlan = dietPlan(mealTemplate.getMinimalistMeals(numberOfMeals));
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = minDietPlan.getVariableIngredients();
        Evaluation<DietPlan> bestDietPlan = evaluation(minDietPlan, fitnessFunction);
        while (!variableIngredients.isEmpty()) {
            final DietPlan dietPlan = bestDietPlan.getObject();
            final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
            final Pair<Integer, FoodItem> variableIngredient = variableIngredients.get(variableIngredientIndex);
            final int mealIndex = variableIngredient.a();
            final FoodItem foodItem = variableIngredient.b();
            final Meal meal = dietPlan.getMeal(mealIndex);
            final double maxAmount = meal.getTemplate().getMaxAmount(foodItem);
            final double maxAddition = maxAmount - meal.getAmount(foodItem);
            final double portionAmount = foodItem.getPortionAmount();
            final int maxAdditionalPortions = (int) Math.round(maxAddition / portionAmount);
            if (maxAdditionalPortions > 0) {
                int additionalPortions = RANDOM.nextInt(maxAdditionalPortions) + 1;
                while (additionalPortions > 0) {
                    final double change = additionalPortions * portionAmount;
                    final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, change);
                    final Evaluation<DietPlan> dietPlanEvaluation = evaluation(newDietPlan, fitnessFunction);
                    if (dietPlanEvaluation.getTotalScore() > bestDietPlan.getTotalScore()) {
                        bestDietPlan = dietPlanEvaluation;
                        additionalPortions = 0;
                    } else {
                        additionalPortions /= 2;
                        if (additionalPortions == 0) {
                            variableIngredients.remove(variableIngredientIndex);
                        }
                    }
                }
            } else {
                variableIngredients.remove(variableIngredientIndex);
            }
        }
        return bestDietPlan;
    }

    private static Evaluation<DietPlan> generateCandidate3(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        final DietPlan minDietPlan = dietPlan(mealTemplate.getMinimalistMeals(numberOfMeals));
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = minDietPlan.getVariableIngredients();
        Evaluation<DietPlan> bestDietPlan = evaluation(minDietPlan, fitnessFunction);
        while (!variableIngredients.isEmpty()) {
            final DietPlan dietPlan = bestDietPlan.getObject();
            final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
            final Pair<Integer, FoodItem> variableIngredient = variableIngredients.get(variableIngredientIndex);
            final int mealIndex = variableIngredient.a();
            final FoodItem foodItem = variableIngredient.b();
            final Meal meal = dietPlan.getMeal(mealIndex);
            final double maxAmount = meal.getTemplate().getMaxAmount(foodItem);
            final double roundedMaxAmount = foodItem.roundToPortions(maxAmount);
            if (roundedMaxAmount > meal.getAmount(foodItem)) {
                final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, foodItem.getPortionAmount());
                final Evaluation<DietPlan> dietPlanEvaluation = evaluation(newDietPlan, fitnessFunction);
                if (dietPlanEvaluation.getTotalScore() > bestDietPlan.getTotalScore()) {
                    bestDietPlan = dietPlanEvaluation;
                } else {
                    variableIngredients.remove(variableIngredientIndex);
                }
            } else {
                variableIngredients.remove(variableIngredientIndex);
            }
        }
        return bestDietPlan;
    }

    private static void printDietPlanEvaluation(final Evaluation<DietPlan> dietPlanEvaluation) {
        final DietPlan dietPlan = dietPlanEvaluation.getObject();
        final Scores scores = dietPlanEvaluation.getScores();
        final List<Pair<Score, Double>> relScores = scores.getRelativeScores();
        System.out.println(dietPlan);
        System.out.println("Scores:");
        System.out.println(scores);
        System.out.println("Sorted relative scores:");
        System.out.println(relScores);
        System.out.println("Total score: " + scores.getTotalScore() + " of " + scores.getWeightSum());
        System.out.println();
    }
}
