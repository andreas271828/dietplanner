package test;

import diet.*;
import util.Evaluation;
import util.Evaluations;
import util.Pair;

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
                    nextGeneration.add(evaluation(dietPlanWithoutChange, FITNESS_FUNCTION_1, evaluation.getScores()));
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
            population.add(generateCandidate8(mealTemplate, numberOfMeals, fitnessFunction));
            System.out.println("Generated diet plan " + (i + 1) + ".");
        }

        // Evolve population
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();
        mealTemplates.add(mealTemplate);
        final double baseMealMutationRate = 0.0;
        final double baseIngredientMutationRate = 0.0;
        final double maxMutationFactor = 5.0;
        double mutationFactor = 1.0;
        int betterOffspring = 0;
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
                ++betterOffspring;
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
        System.out.println("Better offspring: " + betterOffspring);
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
        return improveCandidate(evaluation(minDietPlan, fitnessFunction), fitnessFunction, true);
    }

    private static Evaluation<DietPlan> generateCandidate4(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        final DietPlan minDietPlan = dietPlan(mealTemplate.getMinimalistMeals(numberOfMeals));
        Evaluation<DietPlan> curDietPlan = evaluation(minDietPlan, fitnessFunction);
        Evaluation<DietPlan> bestDietPlan = curDietPlan;
        Optional<Pair<Requirement, Integer>> maybeWorstScoreId = curDietPlan.getWorstScore();
        boolean continueSearch = true;
        while (continueSearch && maybeWorstScoreId.isPresent()) {
            final Pair<Requirement, Integer> worstScoreId = maybeWorstScoreId.get();
            final DietPlan dietPlan = curDietPlan.getObject();
            final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
            while (continueSearch && !variableIngredients.isEmpty()) {
                final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
                final Pair<Integer, FoodItem> variableIngredient = variableIngredients.get(variableIngredientIndex);
                final int mealIndex = variableIngredient.a();
                final FoodItem foodItem = variableIngredient.b();
                final Meal meal = dietPlan.getMeal(mealIndex);
                final double maxAmount = meal.getTemplate().getMaxAmount(foodItem);
                final double roundedMaxAmount = foodItem.roundToPortions(maxAmount);
                final double minAmount = meal.getTemplate().getMinAmount(foodItem);
                final double roundedMinAmount = foodItem.roundToPortions(minAmount);
                final double foodItemAmount = meal.getAmount(foodItem);
                final double portionAmount = foodItem.getPortionAmount();
                final double oldScore = curDietPlan.getScore(worstScoreId).getScore();
                if (roundedMaxAmount > foodItemAmount) {
                    final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, portionAmount);
                    final Evaluation<DietPlan> dietPlanEvaluation = evaluation(newDietPlan, fitnessFunction);
                    final double newScore = dietPlanEvaluation.getScore(worstScoreId).getScore();
                    if (newScore > oldScore) {
                        curDietPlan = dietPlanEvaluation;
                        continueSearch = false;
                    }
                }
                if (continueSearch && roundedMinAmount < foodItemAmount) {
                    final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, -portionAmount);
                    final Evaluation<DietPlan> dietPlanEvaluation = evaluation(newDietPlan, fitnessFunction);
                    final double newScore = dietPlanEvaluation.getScore(worstScoreId).getScore();
                    if (newScore > oldScore) {
                        curDietPlan = dietPlanEvaluation;
                        continueSearch = false;
                    }
                }
                if (continueSearch) {
                    variableIngredients.remove(variableIngredientIndex);
                }
            }
            if (!continueSearch) {
                // If search for improvement was successful (aborted), continue search with updated worst score.
                maybeWorstScoreId = curDietPlan.getWorstScore();
                continueSearch = true;
            } else {
                continueSearch = false;
            }
            if (curDietPlan.getTotalScore() > bestDietPlan.getTotalScore()) {
                bestDietPlan = curDietPlan;
                System.out.println(bestDietPlan.getTotalScore());
            }
        }
        return bestDietPlan;
    }

    private static Evaluation<DietPlan> generateCandidate5(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        final DietPlan minDietPlan = dietPlan(mealTemplate.getMinimalistMeals(numberOfMeals));
        Evaluation<DietPlan> bestDietPlan = evaluation(minDietPlan, fitnessFunction);
        Optional<Pair<Requirement, Integer>> maybeWorstScoreId = bestDietPlan.getWorstScore();
        boolean continueSearch = true;
        while (continueSearch && maybeWorstScoreId.isPresent()) {
            final Pair<Requirement, Integer> worstScoreId = maybeWorstScoreId.get();
            final DietPlan dietPlan = bestDietPlan.getObject();
            final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
            while (continueSearch && !variableIngredients.isEmpty()) {
                final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
                final Pair<Integer, FoodItem> variableIngredient = variableIngredients.get(variableIngredientIndex);
                final int mealIndex = variableIngredient.a();
                final FoodItem foodItem = variableIngredient.b();
                final Meal meal = dietPlan.getMeal(mealIndex);
                final double maxAmount = meal.getTemplate().getMaxAmount(foodItem);
                final double roundedMaxAmount = foodItem.roundToPortions(maxAmount);
                final double foodItemAmount = meal.getAmount(foodItem);
                final double portionAmount = foodItem.getPortionAmount();
                final double oldScore = bestDietPlan.getScore(worstScoreId).getScore();
                if (roundedMaxAmount > foodItemAmount) {
                    final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, portionAmount);
                    final Evaluation<DietPlan> dietPlanEvaluation = evaluation(newDietPlan, fitnessFunction);
                    final double newScore = dietPlanEvaluation.getScore(worstScoreId).getScore();
                    if (newScore > oldScore) {
                        bestDietPlan = dietPlanEvaluation;
                        continueSearch = false;
                    }
                }
                if (continueSearch) {
                    variableIngredients.remove(variableIngredientIndex);
                }
            }
            if (!continueSearch) {
                // If search for improvement was successful (aborted), continue search with updated worst score.
                maybeWorstScoreId = bestDietPlan.getWorstScore();
                continueSearch = true;
            } else {
                continueSearch = false;
            }
            System.out.println(bestDietPlan.getTotalScore());
        }
        return bestDietPlan;
    }

    private static Evaluation<DietPlan> generateCandidate6(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        Evaluation<DietPlan> bestDietPlan = generateCandidate3(mealTemplate, numberOfMeals, fitnessFunction);
        return improveCandidate(bestDietPlan, fitnessFunction, false);
    }

    private static Evaluation<DietPlan> generateCandidate7(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        final DietPlan minDietPlan = dietPlan(mealTemplate.getMinimalistMeals(numberOfMeals));
        Evaluation<DietPlan> bestDietPlan = evaluation(minDietPlan, fitnessFunction);
        boolean continueImprovement = true;
        while (continueImprovement) {
            final double oldScore = bestDietPlan.getTotalScore();
            bestDietPlan = improveCandidate(bestDietPlan, fitnessFunction, true);
            bestDietPlan = improveCandidate(bestDietPlan, fitnessFunction, false);
            continueImprovement = bestDietPlan.getTotalScore() > oldScore;
            System.out.println(bestDietPlan.getTotalScore());
        }
        return bestDietPlan;
    }

    private static Evaluation<DietPlan> generateCandidate8(final MealTemplate mealTemplate,
                                                           final int numberOfMeals,
                                                           final Function<DietPlan, Scores> fitnessFunction) {
        final DietPlan minDietPlan = dietPlan(mealTemplate.getMinimalistMeals(numberOfMeals));
        Evaluation<DietPlan> bestDietPlan = evaluation(minDietPlan, fitnessFunction);
        bestDietPlan = improveCandidate(bestDietPlan, fitnessFunction, true);
        System.out.println(bestDietPlan.getTotalScore());
        boolean continueImprovement = true;
        while (continueImprovement) {
            continueImprovement = false;
            final Optional<Pair<Requirement, Integer>> maybeWorstScoreId = bestDietPlan.getWorstScore();
            Evaluation<DietPlan> newBestDietPlan = improveCandidateForScore(bestDietPlan, maybeWorstScoreId.get(), fitnessFunction, false);
            if (newBestDietPlan != bestDietPlan) {
                // TODO: Don't add ingredient that was removed in improveCandidateForScore()
                newBestDietPlan = improveCandidate(newBestDietPlan, fitnessFunction, true);
                if (newBestDietPlan.getTotalScore() > bestDietPlan.getTotalScore()) {
                    bestDietPlan = newBestDietPlan;
                    continueImprovement = true;
                    System.out.println(bestDietPlan.getTotalScore());
                } else {
                    final Optional<Pair<Requirement, Integer>> maybeWorstScoreId2 = newBestDietPlan.getWorstScore();
                    // TODO: Call improveCandidateForScore() with original newBestDietPlan (before adding other values)
                    newBestDietPlan = improveCandidateForScore(newBestDietPlan, maybeWorstScoreId2.get(), fitnessFunction, false);
                    // TODO: Continue in a loop (remember all ingredients that shouldn't be added anymore)
                }
            }
        }
        return bestDietPlan;
    }

    private static Evaluation<DietPlan> improveCandidate(final Evaluation<DietPlan> candidate,
                                                         final Function<DietPlan, Scores> fitnessFunction,
                                                         final boolean increaseAmounts) {
        Evaluation<DietPlan> bestDietPlan = candidate;
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = candidate.getObject().getVariableIngredients();
        while (!variableIngredients.isEmpty()) {
            final DietPlan dietPlan = bestDietPlan.getObject();
            final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
            final Pair<Integer, FoodItem> variableIngredient = variableIngredients.get(variableIngredientIndex);
            final int mealIndex = variableIngredient.a();
            final Meal meal = dietPlan.getMeal(mealIndex);
            final FoodItem foodItem = variableIngredient.b();
            final double curAmount = meal.getAmount(foodItem);
            final double change;
            if (increaseAmounts) {
                final double maxAmount = meal.getTemplate().getRoundedMaxAmount(foodItem);
                change = curAmount < maxAmount ? foodItem.getPortionAmount() : 0.0;
            } else {
                final double minAmount = meal.getTemplate().getRoundedMinAmount(foodItem);
                change = curAmount > minAmount ? -foodItem.getPortionAmount() : 0.0;
            }
            if (change != 0.0) {
                final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, change);
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

    private static Evaluation<DietPlan> improveCandidateForScore(final Evaluation<DietPlan> candidate,
                                                                 final Pair<Requirement, Integer> scoreId,
                                                                 final Function<DietPlan, Scores> fitnessFunction,
                                                                 final boolean increaseAmounts) {
        final double oldScore = candidate.getScore(scoreId).getScore();
        final DietPlan dietPlan = candidate.getObject();
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
        while (!variableIngredients.isEmpty()) {
            final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
            final Pair<Integer, FoodItem> variableIngredient = variableIngredients.get(variableIngredientIndex);
            final int mealIndex = variableIngredient.a();
            final Meal meal = dietPlan.getMeal(mealIndex);
            final FoodItem foodItem = variableIngredient.b();
            final double curAmount = meal.getAmount(foodItem);
            final double change;
            if (increaseAmounts) {
                final double maxAmount = meal.getTemplate().getRoundedMaxAmount(foodItem);
                change = curAmount < maxAmount ? foodItem.getPortionAmount() : 0.0;
            } else {
                final double minAmount = meal.getTemplate().getRoundedMinAmount(foodItem);
                change = curAmount > minAmount ? -foodItem.getPortionAmount() : 0.0;
            }
            if (change != 0.0) {
                final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, change);
                final Evaluation<DietPlan> dietPlanEvaluation = evaluation(newDietPlan, fitnessFunction);
                if (dietPlanEvaluation.getScore(scoreId).getScore() > oldScore) {
                    return dietPlanEvaluation;
                } else {
                    variableIngredients.remove(variableIngredientIndex);
                }
            } else {
                variableIngredients.remove(variableIngredientIndex);
            }
        }
        return candidate;
    }

    private static void test3() {
        final DietPlan minDietPlan = dietPlan(STANDARD_DAY_MIX.getMinimalistMeals(NUMBER_OF_MEALS));
        final Evaluation<DietPlan> minDietPlanEvaluation = evaluation(minDietPlan, FITNESS_FUNCTION_2);
        printScore(minDietPlanEvaluation.getScores());
        //printDietPlanEvaluation(addBestIngredients1(minDietPlanEvaluation, minDietPlanEvaluation));
        //printDietPlanEvaluation(addBestIngredients2(minDietPlanEvaluation));
        //printDietPlanEvaluation(addBestIngredients3(minDietPlanEvaluation, minDietPlanEvaluation));
        //printDietPlanEvaluation(addBestIngredients4(minDietPlanEvaluation));
        printDietPlanEvaluation(addBestIngredients5(minDietPlanEvaluation));
    }

    private static Evaluation<DietPlan> addBestIngredients1(final Evaluation<DietPlan> base,
                                                            final Evaluation<DietPlan> best) {
        Evaluation<DietPlan> newBest = best;
        final DietPlan dietPlan = base.getObject();
        final double totalScore = base.getTotalScore();
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
        while (!variableIngredients.isEmpty()) {
            final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
            final Pair<Integer, FoodItem> variableIngredient = variableIngredients.get(variableIngredientIndex);
            final int mealIndex = variableIngredient.a();
            final Meal meal = dietPlan.getMeal(mealIndex);
            final FoodItem foodItem = variableIngredient.b();
            final double curAmount = meal.getAmount(foodItem);
            final double maxAmount = meal.getTemplate().getRoundedMaxAmount(foodItem);
            if (curAmount < maxAmount) {
                final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, foodItem.getPortionAmount());
                final Evaluation<DietPlan> newDietPlanEvaluation = evaluation(newDietPlan, base.getEvaluationFunction());
                final double newTotalScore = newDietPlanEvaluation.getTotalScore();
                if (newTotalScore > totalScore) {
                    if (newTotalScore > newBest.getTotalScore()) {
                        newBest = newDietPlanEvaluation;
                        printScore(newBest.getScores());
                    }
                    newBest = addBestIngredients1(newDietPlanEvaluation, newBest);
                }
            }
            variableIngredients.remove(variableIngredientIndex);
        }
        return newBest;
    }

    private static Evaluation<DietPlan> addBestIngredients2(final Evaluation<DietPlan> base) {
        Evaluation<DietPlan> newBest = base;
        final DietPlan dietPlan = base.getObject();
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
        for (final Pair<Integer, FoodItem> variableIngredient : variableIngredients) {
            final int mealIndex = variableIngredient.a();
            final Meal meal = dietPlan.getMeal(mealIndex);
            final FoodItem foodItem = variableIngredient.b();
            final double curAmount = meal.getAmount(foodItem);
            final double maxAmount = meal.getTemplate().getRoundedMaxAmount(foodItem);
            if (curAmount < maxAmount) {
                final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, foodItem.getPortionAmount());
                final Evaluation<DietPlan> newDietPlanEvaluation = evaluation(newDietPlan, base.getEvaluationFunction());
                final double newTotalScore = newDietPlanEvaluation.getTotalScore();
                if (newTotalScore > newBest.getTotalScore()) {
                    newBest = newDietPlanEvaluation;
                    printScore(newBest.getScores());
                }
            }
        }
        return newBest == base ? base : addBestIngredients2(newBest);
    }

    private static Evaluation<DietPlan> addBestIngredients3(final Evaluation<DietPlan> base,
                                                            final Evaluation<DietPlan> best) {
        Evaluation<DietPlan> newBest = base;
        Evaluation<DietPlan> newGlobalBest = best;
        final DietPlan dietPlan = base.getObject();
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
        for (final Pair<Integer, FoodItem> variableIngredient : variableIngredients) {
            final int mealIndex = variableIngredient.a();
            final Meal meal = dietPlan.getMeal(mealIndex);
            final FoodItem foodItem = variableIngredient.b();
            final double curAmount = meal.getAmount(foodItem);
            final double maxAmount = meal.getTemplate().getRoundedMaxAmount(foodItem);
            if (curAmount < maxAmount) {
                final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, foodItem.getPortionAmount());
                final Evaluation<DietPlan> newDietPlanEvaluation = evaluation(newDietPlan, base.getEvaluationFunction());
                final double newTotalScore = newDietPlanEvaluation.getTotalScore();
                if (newTotalScore > newBest.getTotalScore()) {
                    newBest = newDietPlanEvaluation;
                    if (newTotalScore > newGlobalBest.getTotalScore()) {
                        newGlobalBest = newDietPlanEvaluation;
                        printScore(newDietPlanEvaluation.getScores());
                    }
                }
            }
        }
        if (newBest == base) {
            Optional<Evaluation<DietPlan>> maybeNewBest = Optional.empty();
            for (final Pair<Integer, FoodItem> variableIngredient : variableIngredients) {
                final int mealIndex = variableIngredient.a();
                final Meal meal = dietPlan.getMeal(mealIndex);
                final FoodItem foodItem = variableIngredient.b();
                final double curAmount = meal.getAmount(foodItem);
                final double minAmount = meal.getTemplate().getRoundedMinAmount(foodItem);
                if (curAmount > minAmount) {
                    final DietPlan newDietPlan = dietPlan.getWithChange(mealIndex, foodItem, -foodItem.getPortionAmount());
                    final Evaluation<DietPlan> newDietPlanEvaluation = evaluation(newDietPlan, base.getEvaluationFunction());
                    final double newTotalScore = newDietPlanEvaluation.getTotalScore();
                    if (!maybeNewBest.isPresent() || newTotalScore > maybeNewBest.get().getTotalScore()) {
                        maybeNewBest = Optional.of(newDietPlanEvaluation);
                        if (newTotalScore > newGlobalBest.getTotalScore()) {
                            newGlobalBest = newDietPlanEvaluation;
                            printScore(newDietPlanEvaluation.getScores());
                        }
                    }
                }
            }
            if (maybeNewBest.isPresent()) {
                newGlobalBest = addBestIngredients3(maybeNewBest.get(), newGlobalBest);
            }
        } else {
            newGlobalBest = addBestIngredients3(newBest, newGlobalBest);
        }
        return newGlobalBest;
    }

    private static Evaluation<DietPlan> addBestIngredients4(final Evaluation<DietPlan> base,
                                                            final Evaluation<DietPlan> globalBest) {
        Evaluation<DietPlan> best = base;
        boolean continueAdding = true;
        while (continueAdding) {
            continueAdding = false;
            final DietPlan dietPlan = best.getObject();
            final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
            for (final Pair<Integer, FoodItem> variableIngredient : variableIngredients) {
                final Optional<DietPlan> maybeNewDietPlan = dietPlan.addPortion(variableIngredient.a(), variableIngredient.b());
                if (maybeNewDietPlan.isPresent()) {
                    final Evaluation<DietPlan> cur = evaluation(maybeNewDietPlan.get(), best.getEvaluationFunction());
                    // TODO: Consider cases with equal scores (it could take several times to get improvement)?
                    if (cur.getTotalScore() > best.getTotalScore()) {
                        best = cur;
                        continueAdding = true;
                        if (best.getTotalScore() > globalBest.getTotalScore()) {
                            printScore(best.getScores());
                        }
                    }
                }
            }
        }
        return best;
    }

    private static Evaluation<DietPlan> addBestIngredients5(final Evaluation<DietPlan> base) {
        Evaluation<DietPlan> best = addBestIngredients4(base, base);
        boolean continueChanging = true;
        while (continueChanging) {
            continueChanging = false;
            final DietPlan dietPlan = best.getObject();
            final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
            while (!continueChanging) {
                final ArrayList<Pair<Integer, FoodItem>> removeList = new ArrayList<Pair<Integer, FoodItem>>();
                do {
                    final int variableIngredientIndex = RANDOM.nextInt(variableIngredients.size());
                    removeList.add(variableIngredients.get(variableIngredientIndex));
                } while (RANDOM.nextDouble() < 0.8); // TODO: Different probability to continue (increase every time no improvement was made?)?
                final Optional<DietPlan> maybeNewDietPlan = dietPlan.removePortions(removeList);
                if (maybeNewDietPlan.isPresent()) {
                    final Evaluation<DietPlan> cur = evaluation(maybeNewDietPlan.get(), best.getEvaluationFunction());
                    if (cur.getTotalScore() > best.getTotalScore()) {
                        best = cur;
                        continueChanging = true;
                        printScore(best.getScores());
                    }
                    final Evaluation<DietPlan> newBest = addBestIngredients4(cur, best);
                    if (newBest.getTotalScore() > best.getTotalScore()) {
                        best = newBest;
                        continueChanging = true;
                    }
                }
            }
            if (best.getTotalScore() >= best.getScores().getWeightSum()) {
                // TODO: Prioritise worst individual scores (over the total score) when trying to find better alternatives?
                // TODO: Start several threads and kill unsuccessful ones (or when we are happy to read the result)
                continueChanging = false;
            }
        }
        return best;
    }

    private static void printScore(final Scores scores) {
        System.out.println("Score: " + scores.getTotalScore() + " / " + scores.getWeightSum());
    }

    private static void printDietPlanEvaluation(final Evaluation<DietPlan> dietPlanEvaluation) {
        final DietPlan dietPlan = dietPlanEvaluation.getObject();
        final Scores scores = dietPlanEvaluation.getScores();
        final List<Pair<Pair<Requirement, Integer>, Double>> relScores = scores.getRelativeScores();
        System.out.println(dietPlan);
        System.out.println("Scores:");
        System.out.println(scores);
        System.out.println("Sorted relative scores:");
        System.out.println(relScores);
        System.out.println("Total score: " + scores.getTotalScore() + " of " + scores.getWeightSum());
        System.out.println();
    }
}
