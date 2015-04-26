package gui;

import diet.*;
import util.Evaluation;
import util.Limits2;
import util.Mutable;
import util.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static diet.DietPlan.dietPlan;
import static diet.Meal.meal;
import static diet.MealTemplate.*;
import static java.util.Arrays.asList;
import static optimization.Optimization.optimize;
import static util.Evaluation.evaluation;
import static util.Global.*;
import static util.Mutable.mutable;
import static util.Pair.pair;

public class DietPlanner extends JFrame {
    private static final Requirements REQUIREMENTS = new Requirements(PersonalDetails.ANDREAS, 7, 21);
    private static final ArrayList<MealTemplate> MEAL_TEMPLATES = getMealTemplates();

    private Optional<Evaluation<DietPlan>> best = Optional.empty();

    private JPanel panel;
    private JButton stopButton;

    private DietPlanner() {
        super("DietPlanner");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(panel);

        final SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> optimizationThread = createOptimizationThread7();
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                optimizationThread.cancel(false);

                best.ifPresent(new Consumer<Evaluation<DietPlan>>() {
                    @Override
                    public void accept(final Evaluation<DietPlan> evaluation) {
                        final DietPlan dietPlan = evaluation.getObject();
                        final Scores scores = evaluation.getScores();
                        final List<Pair<Pair<Requirement, Integer>, Double>> relScores = scores.getRelativeScores();

                        System.out.println('\n');
                        System.out.println(dietPlan);

                        System.out.println("Scores:");
                        System.out.println("=======");
                        relScores.forEach(new Consumer<Pair<Pair<Requirement, Integer>, Double>>() {
                            @Override
                            public void accept(final Pair<Pair<Requirement, Integer>, Double> scoreInfo) {
                                final StringBuilder scoreSb = new StringBuilder();
                                final Pair<Requirement, Integer> scoreId = scoreInfo.a();
                                scoreSb.append(scoreId.a().getName());
                                scoreSb.append(" (");
                                scoreSb.append(scoreId.b() + 1);
                                scoreSb.append("): ");
                                scoreSb.append(scoreInfo.b());
                                System.out.println(scoreSb);
                            }
                        });
                        System.out.println();

                        final String totalScoreStr = "Total weighted score: " + scores.getTotalScore() + " of " + scores.getWeightSum();
                        final StringBuilder totalScoreSb = new StringBuilder(totalScoreStr);
                        totalScoreSb.append('\n');
                        for (int i = 0; i < totalScoreStr.length(); ++i) {
                            totalScoreSb.append('=');
                        }
                        System.out.println(totalScoreSb);
                        System.out.println();
                    }
                });

                dispose();
            }
        });

        pack();
        setVisible(true);

        optimizationThread.execute();
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                final int startPopulationSize = 20;
                final int maxPopulationSize = 500;
                final int numberOfPopulations = 1;
                final double populationMixRate = 0.01;
                final DietPlan startDietPlan = createStartDietPlan();
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                return optimize(new Supplier<ArrayList<Evaluation<DietPlan>>>() {
                    @Override
                    public ArrayList<Evaluation<DietPlan>> get() {
                        final ArrayList<Evaluation<DietPlan>> startPopulation = new ArrayList<Evaluation<DietPlan>>(startPopulationSize);
                        for (int i = 0; i < startPopulationSize && !isCancelled(); ++i) {
                            final Evaluation<DietPlan> evaluation = createIndividual2(startDietPlan, evaluationFunction, i);
                            startPopulation.add(evaluation);
                        }
                        return startPopulation;
                    }
                }, maxPopulationSize, numberOfPopulations, new Comparator<Evaluation<DietPlan>>() {
                    @Override
                    public int compare(final Evaluation<DietPlan> evaluation1, final Evaluation<DietPlan> evaluation2) {
                        return Double.compare(evaluation2.getTotalScore(), evaluation1.getTotalScore()); // Descending order
                    }
                }, new Consumer<Evaluation<DietPlan>>() {
                    @Override
                    public void accept(final Evaluation<DietPlan> evaluation) {
                        publish(evaluation);
                    }
                }, new Supplier<Boolean>() {
                    @Override
                    public Boolean get() {
                        return isCancelled();
                    }
                }, new Function<Pair<Evaluation<DietPlan>, Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
                    @Override
                    public Evaluation<DietPlan> apply(final Pair<Evaluation<DietPlan>, Evaluation<DietPlan>> parents) {
                        final DietPlan dietPlan1 = parents.a().getObject();
                        final DietPlan dietPlan2 = parents.b().getObject();
                        return evaluation(dietPlan1.mate(dietPlan2, 0.001), evaluationFunction);
                    }
                }, populationMixRate);
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                best = Optional.of(chunks.get(chunks.size() - 1));
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    System.out.println("Best score: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread2() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                final int maxPopulationSize = 100;
                final ArrayList<Evaluation<DietPlan>> population = new ArrayList<Evaluation<DietPlan>>(maxPopulationSize);
                final ArrayList<Pair<Requirement, Integer>> scoreIds = REQUIREMENTS.getScoreIds();
                final DietPlan startDietPlan = createStartDietPlan();
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                while (!isCancelled()) {
                    final int scoreIdsSize = scoreIds.size();
                    final int scoreIdIndex = RANDOM.nextInt(scoreIdsSize + 1);
                    final Optional<Pair<Requirement, Integer>> maybeScoreId = scoreIdIndex < scoreIdsSize ?
                            Optional.of(scoreIds.get(scoreIdIndex)) :
                            Optional.<Pair<Requirement, Integer>>empty();
                    final int populationSize = population.size();
                    if (populationSize < maxPopulationSize) {
                        final Evaluation<DietPlan> evaluation = createIndividual(startDietPlan, evaluationFunction,
                                populationSize);
                        population.add(evaluation);
                        if (!best.isPresent() || evaluation.getTotalScore() > best.get().getTotalScore()) {
                            best = Optional.of(evaluation);
                            publish(evaluation);
                        }
                    } else {
                        final int parentIndex1 = RANDOM.nextInt(populationSize);
                        final int parentIndex2 = RANDOM.nextInt(populationSize);
                        if (parentIndex1 != parentIndex2) {
                            final DietPlan parent1 = population.get(parentIndex1).getObject();
                            final DietPlan parent2 = population.get(parentIndex2).getObject();
                            final DietPlan offspring = parent1.mate(parent2, 0.001);
                            final Evaluation<DietPlan> evaluation = evaluation(offspring, evaluationFunction);
                            Evaluation<DietPlan> worst = evaluation;
                            for (final Evaluation<DietPlan> cur : population) {
                                final Evaluation<DietPlan> curWorst = worst;
                                final Boolean curIsWorst = maybeScoreId.map(new Function<Pair<Requirement, Integer>, Boolean>() {
                                    @Override
                                    public Boolean apply(final Pair<Requirement, Integer> scoreId) {
                                        final double curScore = cur.getScore(scoreId).getScore();
                                        final double worstScore = curWorst.getScore(scoreId).getScore();
                                        if (curScore == worstScore) {
                                            final double curTotalScore = cur.getTotalScore();
                                            final double worstTotalScore = curWorst.getTotalScore();
                                            return curTotalScore == worstTotalScore ?
                                                    RANDOM.nextBoolean() :
                                                    curTotalScore < worstTotalScore;
                                        } else {
                                            return curScore < worstScore;
                                        }
                                    }
                                }).orElseGet(new Supplier<Boolean>() {
                                    @Override
                                    public Boolean get() {
                                        final double curScore = cur.getTotalScore();
                                        final double worstScore = curWorst.getTotalScore();
                                        return curScore == worstScore ? RANDOM.nextBoolean() : curScore < worstScore;
                                    }
                                });
                                if (curIsWorst) {
                                    worst = cur;
                                }
                            }
                            if (evaluation != worst) {
                                population.remove(worst);
                                population.add(evaluation);
                                if (!best.isPresent() || evaluation.getTotalScore() > best.get().getTotalScore()) {
                                    best = Optional.of(evaluation);
                                    publish(evaluation);
                                }
                            }
                        }
                    }
                }
                return best;
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                best = Optional.of(chunks.get(chunks.size() - 1));
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    System.out.println("Best score: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread3() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                // TODO: It might not be a good idea to manipulate a member variable (best) here.
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                Evaluation<DietPlan> base = evaluation(createStartDietPlan(), evaluationFunction);
                while (!isCancelled()) {
                    final Evaluation<DietPlan> evaluation1 = createIndividual3(base);
                    if (!best.isPresent() || evaluation1.getTotalScore() > best.get().getTotalScore()) {
                        best = Optional.of(evaluation1);
//                        publish(evaluation1);
                        System.out.println("Best score after adding ingredients: " + evaluation1.getTotalScore());
                    }

                    // Determine new startDietPlan
                    Evaluation<DietPlan> evaluation2 = evaluation1;
                    boolean continueRemoving = true;
                    while (continueRemoving) {
                        final DietPlan dietPlan = evaluation2.getObject();
                        final ArrayList<Pair<Integer, FoodItem>> ingredientIds = dietPlan.getIngredientIds();
                        final double totalScore = evaluation2.getTotalScore();
                        continueRemoving = false;
                        while (!continueRemoving && !ingredientIds.isEmpty()) {
                            final int ingredientIndex = RANDOM.nextInt(ingredientIds.size());
                            final Pair<Integer, FoodItem> ingredientId = ingredientIds.get(ingredientIndex);
                            final Optional<DietPlan> maybeNewDietPlan = dietPlan.removePortion(ingredientId);
                            if (maybeNewDietPlan.isPresent()) {
                                final Evaluation<DietPlan> newEvaluation = evaluation(maybeNewDietPlan.get(), evaluationFunction);
                                final double newScore = newEvaluation.getTotalScore();
                                if (newScore > totalScore) {
                                    evaluation2 = newEvaluation;
                                    continueRemoving = true;
                                }
                            }
                            if (!continueRemoving) {
                                ingredientIds.remove(ingredientIndex);
                            }
                        }
                    }
                    base = evaluation2;
                    if (evaluation2.getTotalScore() > best.get().getTotalScore()) {
                        best = Optional.of(evaluation2);
//                        publish(evaluation2);
                        System.out.println("Best score after removing ingredients: " + evaluation2.getTotalScore());
                    }
                }

                return best;
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    System.out.println("Best score: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread4() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                // TODO: It might not be a good idea to manipulate a member variable (best) here.
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                final Mutable<Evaluation<DietPlan>> cur = mutable(createIndividual3(evaluation(createStartDietPlan(), evaluationFunction)));
                best = Optional.of(cur.get());
                System.out.println("Base score: " + cur.get().getTotalScore());
                while (!isCancelled()) {
                    final Evaluation<DietPlan> curEvaluation = cur.get();
                    final Scores scores = curEvaluation.getScores();
                    final double totalDiff = scores.getWeightSum() - scores.getTotalScore();
                    if (totalDiff > 0.0) {
                        final Optional<Pair<Requirement, Integer>> maybeScoreId = scores.selectScoreByDiff(RANDOM.nextDouble());
                        maybeScoreId.ifPresent(new Consumer<Pair<Requirement, Integer>>() {
                            @Override
                            public void accept(final Pair<Requirement, Integer> scoreId) {
                                final DietPlan curDietPlan = curEvaluation.getObject();
                                final ArrayList<Pair<Integer, FoodItem>> variableIngredients = curDietPlan.getVariableIngredients();
                                while (!variableIngredients.isEmpty()) {
                                    final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
                                    final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
                                    final Optional<DietPlan> maybeDietPlan1 = curDietPlan.addPortion(ingredientId);
                                    final Optional<Evaluation<DietPlan>> maybeEvaluation1 = maybeDietPlan1.map(new Function<DietPlan, Evaluation<DietPlan>>() {
                                        @Override
                                        public Evaluation<DietPlan> apply(final DietPlan dietPlan) {
                                            return evaluation(dietPlan, evaluationFunction);
                                        }
                                    });
                                    final Optional<Double> maybeScore1 = maybeEvaluation1.map(new Function<Evaluation<DietPlan>, Double>() {
                                        @Override
                                        public Double apply(final Evaluation<DietPlan> evaluation) {
                                            return evaluation.getScore(scoreId).getScore();
                                        }
                                    });
                                    final Optional<DietPlan> maybeDietPlan2 = curDietPlan.removePortion(ingredientId);
                                    final Optional<Evaluation<DietPlan>> maybeEvaluation2 = maybeDietPlan2.map(new Function<DietPlan, Evaluation<DietPlan>>() {
                                        @Override
                                        public Evaluation<DietPlan> apply(final DietPlan dietPlan) {
                                            return evaluation(dietPlan, evaluationFunction);
                                        }
                                    });
                                    final Optional<Double> maybeScore2 = maybeEvaluation2.map(new Function<Evaluation<DietPlan>, Double>() {
                                        @Override
                                        public Double apply(final Evaluation<DietPlan> evaluation) {
                                            return evaluation.getScore(scoreId).getScore();
                                        }
                                    });

                                    final int bestScore;
                                    final double curScore = curEvaluation.getScore(scoreId).getScore();
                                    if (maybeScore1.isPresent()) {
                                        if (maybeScore2.isPresent() && maybeScore2.get() > maybeScore1.get()) {
                                            bestScore = maybeScore2.get() > curScore ? 2 : 0;
                                        } else {
                                            bestScore = maybeScore1.get() > curScore ? 1 : 0;
                                        }
                                    } else if (maybeScore2.isPresent()) {
                                        bestScore = maybeScore2.get() > curScore ? 2 : 0;
                                    } else {
                                        bestScore = 0;
                                    }
                                    if (bestScore == 1) {
                                        cur.set(maybeEvaluation1.get());
                                        variableIngredients.clear();
                                    } else if (bestScore == 2) {
                                        cur.set(maybeEvaluation2.get());
                                        variableIngredients.clear();
                                    } else {
                                        variableIngredients.remove(ingredientIndex);
                                    }
                                }

                                if (cur.get().getTotalScore() > best.get().getTotalScore()) {
                                    best = Optional.of(cur.get());
                                    System.out.println("Base score: " + cur.get().getTotalScore());
                                }
                            }
                        });
                    }
                }

                return best;
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    System.out.println("Best score: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread5() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                // TODO: It might not be a good idea to manipulate a member variable (best) here.
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                final DietPlan startDietPlan = createStartDietPlan();
                final Evaluation<DietPlan> startEvaluation = evaluation(startDietPlan, evaluationFunction);
                final Scores startScores = startEvaluation.getScores();
                Evaluation<DietPlan> evaluation = startEvaluation;
                best = Optional.of(evaluation);
                Optional<Pair<Requirement, Integer>> maybeScoreId = startScores.selectScoreByDiff(RANDOM.nextDouble());
                while (!isCancelled() && maybeScoreId.isPresent()) {
                    final Pair<Requirement, Integer> scoreId = maybeScoreId.get();
                    final double score = evaluation.getScore(scoreId).getScore();
                    final double totalScore = evaluation.getTotalScore();
                    final DietPlan dietPlan = evaluation.getObject();
                    final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
                    while (!variableIngredients.isEmpty()) {
                        final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
                        final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
                        final Optional<DietPlan> maybeNewDietPlan = dietPlan.addPortion(ingredientId);
                        final Optional<Evaluation<DietPlan>> maybeNewEvaluation = maybeNewDietPlan.flatMap(
                                new Function<DietPlan, Optional<Evaluation<DietPlan>>>() {
                                    @Override
                                    public Optional<Evaluation<DietPlan>> apply(final DietPlan newDietPlan) {
                                        final Evaluation<DietPlan> newEvalution = evaluation(newDietPlan, evaluationFunction);
                                        final double newScore = newEvalution.getScore(scoreId).getScore();
                                        final double newTotalScore = newEvalution.getTotalScore();
                                        if (newScore > score && newTotalScore >= totalScore) {
                                            return Optional.of(newEvalution);
                                        } else {
                                            return Optional.empty();
                                        }
                                    }
                                });
                        if (maybeNewEvaluation.isPresent()) {
                            evaluation = maybeNewEvaluation.get();
                            best = Optional.of(evaluation);
                            publish(evaluation);
                            variableIngredients.clear();
                        } else {
                            variableIngredients.remove(ingredientIndex);
                        }
                    }
                    final Scores scores = evaluation.getScores();
                    maybeScoreId = scores.selectScoreByDiff(RANDOM.nextDouble());
                }

                return best;
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    System.out.println("Best score: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread6() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                // TODO: It might not be a good idea to manipulate a member variable (best) here.
                final int startPopulationSize = 20;
                final int maxPopulationSize = 500;
                final int numberOfPopulations = 1;
                final double populationMixRate = 0.01;
                final DietPlan startDietPlan = createStartDietPlan();
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                final Evaluation<DietPlan> startEvaluation = evaluation(startDietPlan, evaluationFunction);
                return optimize(new Supplier<ArrayList<Evaluation<DietPlan>>>() {
                    @Override
                    public ArrayList<Evaluation<DietPlan>> get() {
                        final ArrayList<Evaluation<DietPlan>> startPopulation = new ArrayList<Evaluation<DietPlan>>(startPopulationSize);
                        for (int i = 0; i < startPopulationSize && !isCancelled(); ++i) {
                            final Evaluation<DietPlan> evaluation = createIndividual4(startEvaluation);
                            startPopulation.add(evaluation);
                        }
                        return startPopulation;
                    }
                }, maxPopulationSize, numberOfPopulations, new Comparator<Evaluation<DietPlan>>() {
                    @Override
                    public int compare(final Evaluation<DietPlan> evaluation1, final Evaluation<DietPlan> evaluation2) {
                        return Double.compare(evaluation2.getTotalScore(), evaluation1.getTotalScore()); // Descending order
                    }
                }, new Consumer<Evaluation<DietPlan>>() {
                    @Override
                    public void accept(final Evaluation<DietPlan> evaluation) {
                        publish(evaluation);
                    }
                }, new Supplier<Boolean>() {
                    @Override
                    public Boolean get() {
                        return isCancelled();
                    }
                }, new Function<Pair<Evaluation<DietPlan>, Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
                    @Override
                    public Evaluation<DietPlan> apply(final Pair<Evaluation<DietPlan>, Evaluation<DietPlan>> parents) {
                        final DietPlan dietPlan1 = parents.a().getObject();
                        final DietPlan dietPlan2 = parents.b().getObject();
                        return evaluation(dietPlan1.mate(dietPlan2, 0.001), evaluationFunction);
                    }
                }, populationMixRate);
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    best = Optional.of(evaluation);
                    System.out.println("Best score: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread7() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                Optional<Evaluation<DietPlan>> maybeBest = Optional.empty();

                // Optimisation parameters
                final int populationSizeL1 = 10;
                final int populationSizeL2 = 100;
                final double mutationRateL1 = 0.001;
                final double mutationRateL2 = 0.001;

                final Function<Evaluation<DietPlan>, Double> memberValFunc =
                        new Function<Evaluation<DietPlan>, Double>() {
                            @Override
                            public Double apply(final Evaluation<DietPlan> member) {
                                // TODO: Prioritize members with balanced scores or those with higher values for scores that have been difficult to optimise so far?
                                return member.getTotalScore();
                            }
                        };
                final Function<ArrayList<Evaluation<DietPlan>>, Double> subpopulationValFunc =
                        new Function<ArrayList<Evaluation<DietPlan>>, Double>() {
                            @Override
                            public Double apply(final ArrayList<Evaluation<DietPlan>> subpopulation) {
                                double maxVal = 0.0;
                                for (final Evaluation<DietPlan> member : subpopulation) {
                                    final double val = memberValFunc.apply(member);
                                    if (val > maxVal) {
                                        maxVal = val;
                                    }
                                }
                                return maxVal;
                            }
                        };

                final int numberOfMeals = REQUIREMENTS.getNumberOfMeals();
                final double energyDemand = REQUIREMENTS.getEnergyDemand();
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();

                // Initialization of population
                final ArrayList<ArrayList<Evaluation<DietPlan>>> population =
                        new ArrayList<ArrayList<Evaluation<DietPlan>>>(populationSizeL1);
                for (int i = 0; !isCancelled() && i < populationSizeL1; ++i) {
                    final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>(numberOfMeals);
                    for (int j = 0; j < numberOfMeals; ++j) {
                        final int mealTemplateIndex = RANDOM.nextInt(MEAL_TEMPLATES.size());
                        mealTemplates.add(MEAL_TEMPLATES.get(mealTemplateIndex));
                    }

                    final ArrayList<Evaluation<DietPlan>> subpopulation =
                            new ArrayList<Evaluation<DietPlan>>(populationSizeL2);
                    for (int j = 0; !isCancelled() && j < populationSizeL2; ++j) {
                        final ArrayList<FoodItems> allIngredients = new ArrayList<FoodItems>(numberOfMeals);
                        final ArrayList<Pair<Integer, FoodItem>> allIngredientIds = new ArrayList<Pair<Integer, FoodItem>>();
                        double energy = 0.0;
                        for (int k = 0; k < numberOfMeals; ++k) {
                            final int mealIndex = k;
                            final MealTemplate mealTemplate = mealTemplates.get(mealIndex);
                            final FoodItems foodItems = mealTemplate.getMinAmounts();
                            allIngredients.add(foodItems);
                            mealTemplate.getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                                @Override
                                public void accept(final FoodItem foodItem, final Limits2 limits) {
                                    allIngredientIds.add(pair(mealIndex, foodItem));
                                }
                            });
                            energy += foodItems.getEnergy();
                        }
                        while (energy < energyDemand && !allIngredientIds.isEmpty()) {
                            final int ingredientIndex = RANDOM.nextInt(allIngredientIds.size());
                            final Pair<Integer, FoodItem> ingredientId = allIngredientIds.get(ingredientIndex);
                            final int mealIndex = ingredientId.a();
                            final FoodItem foodItem = ingredientId.b();
                            final FoodItems ingredients = allIngredients.get(mealIndex);
                            final double addAmount = foodItem.getPortionAmount();
                            final double newAmount = ingredients.get(foodItem) + addAmount;
                            final double maxAmount = mealTemplates.get(mealIndex).getMaxAmount(foodItem);
                            if (newAmount <= maxAmount) {
                                // TODO: Only add if (certain) scores don't get worse? Try with fat and protein.
                                // TODO: Think of a preference mechanism: select ingredient probabilistically, but prefer those that make things least worse.
                                // TODO: Do something similar when creatig new meals during optimization.
                                ingredients.set(foodItem, newAmount);
                                energy += foodItem.getProperty(FoodProperty.ENERGY) * addAmount;
                            } else {
                                allIngredientIds.remove(ingredientIndex);
                            }
                        }
                        final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
                        for (int k = 0; k < numberOfMeals; ++k) {
                            meals.add(meal(mealTemplates.get(k), allIngredients.get(k)));
                        }
                        final DietPlan dietPlan = dietPlan(meals);
                        final Evaluation<DietPlan> member = evaluation(dietPlan, evaluationFunction);
                        subpopulation.add(member);
                        maybeBest = getBest(maybeBest, member, memberValFunc);
                    }
                    population.add(subpopulation);
                }

                // Optimization
                while (!isCancelled()) {
                    // Compute new generation on level 2
                    for (final ArrayList<Evaluation<DietPlan>> subpopulation : population) {
                        final ArrayList<Evaluation<DietPlan>> oldL2 =
                                new ArrayList<Evaluation<DietPlan>>(subpopulation);
                        subpopulation.clear();
                        final int numberOfParents = populationSizeL2 * 2;
                        final ArrayList<Double> parentSelectors = new ArrayList<Double>(numberOfParents);
                        for (int j = 0; j < numberOfParents; ++j) {
                            parentSelectors.add(RANDOM.nextDouble());
                        }
                        final ArrayList<Integer> parentIndices = selectElements(oldL2, parentSelectors, memberValFunc);
                        for (int j = 0; j < populationSizeL2; ++j) {
                            final int parentIndex1 = parentIndices.get(j * 2);
                            final int parentIndex2 = parentIndices.get(j * 2 + 1);
                            if (parentIndex1 == parentIndex2) {
                                subpopulation.add(oldL2.get(parentIndex1));
                            } else {
                                final DietPlan parent1 = oldL2.get(parentIndex1).getObject();
                                final DietPlan parent2 = oldL2.get(parentIndex2).getObject();
                                final DietPlan offspring = parent1.mate(parent2, mutationRateL2);
                                final Evaluation<DietPlan> member = evaluation(offspring, evaluationFunction);
                                subpopulation.add(member);
                                maybeBest = getBest(maybeBest, member, memberValFunc);
                            }
                        }
                    }

                    // Compute new generation on level 1
                    final ArrayList<ArrayList<Evaluation<DietPlan>>> oldL1 =
                            new ArrayList<ArrayList<Evaluation<DietPlan>>>(population);
                    population.clear();
                    final int numberOfParents = populationSizeL1 * 2;
                    final ArrayList<Double> parentSelectors = new ArrayList<Double>(numberOfParents);
                    for (int i = 0; i < numberOfParents; ++i) {
                        parentSelectors.add(RANDOM.nextDouble());
                    }
                    final ArrayList<Integer> parentIndices = selectElements(oldL1, parentSelectors, subpopulationValFunc);
                    for (int i = 0; i < populationSizeL1; ++i) {
                        final int parentIndex1 = parentIndices.get(i * 2);
                        final int parentIndex2 = parentIndices.get(i * 2 + 1);
                        if (parentIndex1 == parentIndex2) {
                            population.add(oldL1.get(parentIndex1));
                        } else {
                            final ArrayList<Evaluation<DietPlan>> subpopulation =
                                    new ArrayList<Evaluation<DietPlan>>(populationSizeL2);
                            final ArrayList<Evaluation<DietPlan>> parent1 = oldL1.get(parentIndex1);
                            final ArrayList<Evaluation<DietPlan>> parent2 = oldL1.get(parentIndex2);
                            final int crossoverIndex = RANDOM.nextInt(numberOfMeals);
                            final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>(numberOfMeals);
                            for (int j = 0; j < crossoverIndex; ++j) {
                                if (RANDOM.nextDouble() < mutationRateL1) {
                                    final int mealTemplateIndex = RANDOM.nextInt(MEAL_TEMPLATES.size());
                                    mealTemplates.add(MEAL_TEMPLATES.get(mealTemplateIndex));
                                } else {
                                    final DietPlan dietPlan = parent1.get(0).getObject();
                                    final Meal meal = dietPlan.getMeal(j);
                                    mealTemplates.add(meal.getTemplate());
                                }
                            }
                            for (int j = crossoverIndex; j < numberOfMeals; ++j) {
                                if (RANDOM.nextDouble() < mutationRateL1) {
                                    final int mealTemplateIndex = RANDOM.nextInt(MEAL_TEMPLATES.size());
                                    mealTemplates.add(MEAL_TEMPLATES.get(mealTemplateIndex));
                                } else {
                                    final DietPlan dietPlan = parent2.get(0).getObject();
                                    final Meal meal = dietPlan.getMeal(j);
                                    mealTemplates.add(meal.getTemplate());
                                }
                            }
                            for (int j = 0; j < populationSizeL2; ++j) {
                                final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
                                final DietPlan dietPlan1 = parent1.get(j).getObject();
                                final DietPlan dietPlan2 = parent2.get(j).getObject();
                                for (int k = 0; k < crossoverIndex; ++k) {
                                    final Meal meal = dietPlan1.getMeal(k);
                                    final MealTemplate mealTemplate = mealTemplates.get(k);
                                    if (meal.getTemplate().equals(mealTemplate)) {
                                        meals.add(meal);
                                    } else {
                                        final FoodItems ingredients = mealTemplate.getMinAmounts();
                                        final ArrayList<FoodItem> foodItems = mealTemplate.getIngredients().getFoodItems();
                                        final double targetEnergy = meal.getEnergy();
                                        double energy = ingredients.getEnergy();
                                        while (energy < targetEnergy && !foodItems.isEmpty()) {
                                            final int foodItemIndex = RANDOM.nextInt(foodItems.size());
                                            final FoodItem ingredient = foodItems.get(foodItemIndex);
                                            final double addAmount = ingredient.getPortionAmount();
                                            final double newAmount = ingredients.get(ingredient) + addAmount;
                                            final double maxAmount = mealTemplate.getMaxAmount(ingredient);
                                            if (newAmount <= maxAmount) {
                                                ingredients.set(ingredient, newAmount);
                                                energy += ingredient.getProperty(FoodProperty.ENERGY) * addAmount;
                                            } else {
                                                foodItems.remove(foodItemIndex);
                                            }
                                        }
                                        meals.add(meal(mealTemplate, ingredients));
                                    }
                                }
                                for (int k = crossoverIndex; k < numberOfMeals; ++k) {
                                    final Meal meal = dietPlan2.getMeal(k);
                                    final MealTemplate mealTemplate = mealTemplates.get(k);
                                    if (meal.getTemplate().equals(mealTemplate)) {
                                        meals.add(meal);
                                    } else {
                                        final FoodItems ingredients = mealTemplate.getMinAmounts();
                                        final ArrayList<FoodItem> foodItems = mealTemplate.getIngredients().getFoodItems();
                                        final double targetEnergy = meal.getEnergy();
                                        double energy = ingredients.getEnergy();
                                        while (energy < targetEnergy && !foodItems.isEmpty()) {
                                            final int foodItemIndex = RANDOM.nextInt(foodItems.size());
                                            final FoodItem ingredient = foodItems.get(foodItemIndex);
                                            final double addAmount = ingredient.getPortionAmount();
                                            final double newAmount = ingredients.get(ingredient) + addAmount;
                                            final double maxAmount = mealTemplate.getMaxAmount(ingredient);
                                            if (newAmount <= maxAmount) {
                                                ingredients.set(ingredient, newAmount);
                                                energy += ingredient.getProperty(FoodProperty.ENERGY) * addAmount;
                                            } else {
                                                foodItems.remove(foodItemIndex);
                                            }
                                        }
                                        meals.add(meal(mealTemplate, ingredients));
                                    }
                                }
                                final DietPlan dietPlan = dietPlan(meals);
                                final Evaluation<DietPlan> member = evaluation(dietPlan, evaluationFunction);
                                subpopulation.add(member);
                                maybeBest = getBest(maybeBest, member, memberValFunc);
                            }
                            population.add(subpopulation);
                        }
                    }
                }

                return maybeBest;
            }

            private Optional<Evaluation<DietPlan>> getBest(final Optional<Evaluation<DietPlan>> oldMaybeBest,
                                                           final Evaluation<DietPlan> evaluation,
                                                           final Function<Evaluation<DietPlan>, Double> valFunc) {
                final Optional<Evaluation<DietPlan>> maybeBest;
                if (!oldMaybeBest.isPresent() || valFunc.apply(evaluation) > valFunc.apply(oldMaybeBest.get())) {
                    maybeBest = Optional.of(evaluation);
                    publish(evaluation);
                } else {
                    maybeBest = oldMaybeBest;
                }
                return maybeBest;
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    best = Optional.of(evaluation);
                    System.out.println("Total score of best diet plan: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread8() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                final DietPlan startDietPlan = createStartDietPlan();
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                final Evaluation<DietPlan> startEvaluation = evaluation(startDietPlan, evaluationFunction);
                final Evaluation<DietPlan> evaluation = createIndividual5(startEvaluation);
                publish(evaluation);
                return Optional.of(evaluation);
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    best = Optional.of(evaluation);
                    System.out.println("Total score of best diet plan: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread9() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                final Map<FoodItem, Double> foodItemValues = getFoodItemValues(new Supplier<Boolean>() {
                    private int i = 0;

                    @Override
                    public Boolean get() {
                        return i++ < 100000;
                    }
                });

                final ArrayList<Pair<FoodItem, Double>> sortedFoodItemValues = new ArrayList<Pair<FoodItem, Double>>();
                foodItemValues.forEach(new BiConsumer<FoodItem, Double>() {
                    @Override
                    public void accept(final FoodItem foodItem, final Double value) {
                        sortedFoodItemValues.add(pair(foodItem, value));
                    }
                });
                sortedFoodItemValues.sort(new Comparator<Pair<FoodItem, Double>>() {
                    @Override
                    public int compare(final Pair<FoodItem, Double> foodItemValue1,
                                       final Pair<FoodItem, Double> foodItemValue2) {
                        return foodItemValue2.b().compareTo(foodItemValue1.b()); // Sort by value in descending order
                    }
                });
                for (final Pair<FoodItem, Double> foodItemValue : sortedFoodItemValues) {
                    System.out.println(foodItemValue.a() + ": " + foodItemValue.b());
                }

                final FoodItem[] allFoodItems = FoodItem.values();
                final FoodItems foodItems = new FoodItems();
                while (foodItems.getEnergy() < REQUIREMENTS.getEnergyDemand()) {
                    final FoodItem foodItem = selectFoodItem(asList(allFoodItems), foodItemValues);
                    foodItems.add(foodItem, foodItem.getPortionAmount());
                }
                final Function<FoodItems, Scores> evaluationFunction = getEvaluationFunction();
                final Evaluation<FoodItems> foodItemsEvaluation = evaluation(foodItems, evaluationFunction);
                System.out.println("Total score: " + foodItemsEvaluation.getTotalScore() +
                        " of " + foodItemsEvaluation.getScores().getWeightSum());

                return Optional.empty();
            }

            private Map<FoodItem, Double> getFoodItemValues(final Supplier<Boolean> continueFunc) {
                // The higher the value of a food item, the better was the score change when adding a portion of the
                // food item at random times.
                final Map<FoodItem, Double> foodItemValues = new HashMap<FoodItem, Double>();
                final FoodItem[] allFoodItems = FoodItem.values();
                final Function<FoodItems, Scores> evaluationFunction = getEvaluationFunction();
                final Evaluation<FoodItems> foodItemsEvaluation = evaluation(new FoodItems(), evaluationFunction);
                while (continueFunc.get()) {
                    final double oldScore = foodItemsEvaluation.getTotalScore();
                    final FoodItem foodItem = allFoodItems[RANDOM.nextInt(allFoodItems.length)];
                    foodItemsEvaluation.getObject().add(foodItem, foodItem.getPortionAmount());
                    foodItemsEvaluation.invalidate();
                    final double newScore = foodItemsEvaluation.getTotalScore();

                    final Double valObj = foodItemValues.get(foodItem);
                    final double value = valObj == null ? 0.0 : valObj;
                    foodItemValues.put(foodItem, value + newScore - oldScore);

                    if (foodItemsEvaluation.getObject().getEnergy() >= REQUIREMENTS.getEnergyDemand()) {
                        System.out.println("Total score: " + foodItemsEvaluation.getTotalScore() +
                                " of " + foodItemsEvaluation.getScores().getWeightSum());
                        foodItemsEvaluation.getObject().clear();
                        foodItemsEvaluation.invalidate();
                    }
                }

                Optional<Double> maybeMinValue = Optional.empty();
                Optional<Double> maybeMaxValue = Optional.empty();
                for (final Map.Entry<FoodItem, Double> foodItemValue : foodItemValues.entrySet()) {
                    final double value = foodItemValue.getValue();
                    if (!maybeMinValue.isPresent() || value < maybeMinValue.get()) {
                        maybeMinValue = Optional.of(value);
                    }
                    if (!maybeMaxValue.isPresent() || value > maybeMaxValue.get()) {
                        maybeMaxValue = Optional.of(value);
                    }
                }
                if (maybeMinValue.isPresent() && maybeMaxValue.isPresent()) {
                    final double minValue = maybeMinValue.get();
                    final double range = maybeMaxValue.get() - minValue;
                    for (final Map.Entry<FoodItem, Double> foodItemValue : foodItemValues.entrySet()) {
                        final double value = range == 0.0 ? 1.0 : (foodItemValue.getValue() - minValue) / range;
                        foodItemValue.setValue(value);
                    }
                }

                return foodItemValues;
            }

            private Function<FoodItems, Scores> getEvaluationFunction() {
                return new Function<FoodItems, Scores>() {
                    @Override
                    public Scores apply(final FoodItems foodItems) {
                        final Scores scores = new Scores();
                        final FoodProperties foodProperties = foodItems.getProperties();
                        scores.addStandardScore(Requirement.ALPHA_LINOLENIC_ACID, foodProperties.get(FoodProperty.ALPHA_LINOLENIC_ACID), REQUIREMENTS);
                        scores.addStandardScore(Requirement.CALCIUM, foodProperties.get(FoodProperty.CALCIUM), REQUIREMENTS);
                        scores.addStandardScore(Requirement.CARBOHYDRATES, foodProperties.get(FoodProperty.CARBOHYDRATES), REQUIREMENTS);
                        scores.addStandardScore(Requirement.CHOLESTEROL, foodProperties.get(FoodProperty.CHOLESTEROL), REQUIREMENTS);
                        scores.addStandardScore(Requirement.COSTS, foodItems.getCosts(), REQUIREMENTS);
                        scores.addStandardScore(Requirement.DIETARY_FIBRE, foodProperties.get(FoodProperty.DIETARY_FIBRE), REQUIREMENTS);
                        scores.addStandardScore(Requirement.ENERGY, foodProperties.get(FoodProperty.ENERGY), REQUIREMENTS);
                        scores.addStandardScore(Requirement.FAT, foodProperties.get(FoodProperty.FAT), REQUIREMENTS);
                        scores.addStandardScore(Requirement.FOLATES, foodProperties.get(FoodProperty.TOTAL_FOLATES), REQUIREMENTS);
                        scores.addStandardScore(Requirement.IODINE, foodProperties.get(FoodProperty.IODINE), REQUIREMENTS);
                        scores.addStandardScore(Requirement.IRON, foodProperties.get(FoodProperty.IRON), REQUIREMENTS);
                        scores.addStandardScore(Requirement.LINOLEIC_ACID, foodProperties.get(FoodProperty.LINOLEIC_ACID), REQUIREMENTS);
                        scores.addStandardScore(Requirement.MAGNESIUM, foodProperties.get(FoodProperty.MAGNESIUM), REQUIREMENTS);
                        scores.addStandardScore(Requirement.NIACIN_DERIVED_EQUIVALENTS, foodProperties.get(FoodProperty.NIACIN_DERIVED_EQUIVALENTS), REQUIREMENTS);
                        scores.addStandardScore(Requirement.OMEGA_3_FATTY_ACIDS, foodProperties.get(FoodProperty.OMEGA_3_FATTY_ACIDS), REQUIREMENTS);
                        scores.addStandardScore(Requirement.PHOSPHORUS, foodProperties.get(FoodProperty.PHOSPHORUS), REQUIREMENTS);
                        scores.addStandardScore(Requirement.POTASSIUM, foodProperties.get(FoodProperty.POTASSIUM), REQUIREMENTS);
                        scores.addStandardScore(Requirement.PROTEIN, foodProperties.get(FoodProperty.PROTEIN), REQUIREMENTS);
                        scores.addStandardScore(Requirement.RIBOFLAVIN, foodProperties.get(FoodProperty.RIBOFLAVIN), REQUIREMENTS);
                        scores.addStandardScore(Requirement.SELENIUM, foodProperties.get(FoodProperty.SELENIUM), REQUIREMENTS);
                        scores.addStandardScore(Requirement.SODIUM, foodProperties.get(FoodProperty.SODIUM), REQUIREMENTS);
                        scores.addStandardScore(Requirement.SUGARS, foodProperties.get(FoodProperty.SUGARS), REQUIREMENTS);
                        scores.addStandardScore(Requirement.THIAMIN, foodProperties.get(FoodProperty.THIAMIN), REQUIREMENTS);
                        scores.addStandardScore(Requirement.TRANS_FATTY_ACIDS, foodProperties.get(FoodProperty.TRANS_FATTY_ACIDS), REQUIREMENTS);
                        scores.addStandardScore(Requirement.TRYPTOPHAN, foodProperties.get(FoodProperty.TRYPTOPHAN), REQUIREMENTS);
                        scores.addStandardScore(Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, foodProperties.get(FoodProperty.VITAMIN_A_RETINOL_EQUIVALENTS), REQUIREMENTS);
                        scores.addStandardScore(Requirement.VITAMIN_B12, foodProperties.get(FoodProperty.VITAMIN_B12), REQUIREMENTS);
                        scores.addStandardScore(Requirement.VITAMIN_B6, foodProperties.get(FoodProperty.VITAMIN_B6), REQUIREMENTS);
                        scores.addStandardScore(Requirement.VITAMIN_C, foodProperties.get(FoodProperty.VITAMIN_C), REQUIREMENTS);
                        scores.addStandardScore(Requirement.VITAMIN_E, foodProperties.get(FoodProperty.VITAMIN_E), REQUIREMENTS);
                        scores.addStandardScore(Requirement.ZINC, foodProperties.get(FoodProperty.ZINC), REQUIREMENTS);
                        return scores;
                    }
                };
            }

            private FoodItem selectFoodItem(final List<FoodItem> foodItems,
                                            final Map<FoodItem, Double> foodItemValues) {
                final int foodItemIndex = selectElement(foodItems, RANDOM.nextDouble(),
                        new Function<FoodItem, Double>() {
                            @Override
                            public Double apply(final FoodItem foodItem) {
                                return foodItemValues.get(foodItem);
                            }
                        });
                return foodItems.get(foodItemIndex);
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    best = Optional.of(evaluation);
                    System.out.println("Total score of best diet plan: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread10() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                // TODO: Get list of variable ingredients from template instead of diet plan and create initital diet
                // plan using that list?
                DietPlan dietPlan = createStartDietPlan();
                final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
                while (dietPlan.getEnergy() < REQUIREMENTS.getEnergyDemand()) {
                    final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
                    final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
                    final Optional<DietPlan> maybeNewDietPlan = dietPlan.addPortion(ingredientId);
                    if (maybeNewDietPlan.isPresent()) {
                        dietPlan = maybeNewDietPlan.get();
                    }
                }

                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                final Evaluation<DietPlan> evaluation = evaluation(dietPlan, evaluationFunction);
                for (final Pair<Integer, FoodItem> ingredientId : variableIngredients) {
                    final Mutable<Double> ingredientDeficit = mutable(0.0);
                    Optional<DietPlan> maybeDietPlan = dietPlan.removePortion(ingredientId);
                    while (maybeDietPlan.isPresent()) {
                        final DietPlan tmpDietPlan = maybeDietPlan.get();
                        final Evaluation<DietPlan> tmpEvaluation = evaluation(tmpDietPlan, evaluationFunction);
                        evaluation.getScores().forEach(new BiConsumer<Requirement, ArrayList<Score>>() {
                            @Override
                            public void accept(final Requirement requirement, final ArrayList<Score> scores) {
                                for (int i = 0; i < scores.size(); ++i) {
                                    final Score score = evaluation.getScore(requirement, i);
                                    final Score tmpScore = tmpEvaluation.getScore(requirement, i);
                                    final double deficit = score.getWeightedScore() - tmpScore.getWeightedScore();
                                    if (deficit > 0.0) {
                                        ingredientDeficit.set(ingredientDeficit.get() + deficit);
                                    }
                                }
                            }
                        });
                        maybeDietPlan = tmpDietPlan.removePortion(ingredientId);
                    }
                    System.out.println("Meal " + (ingredientId.a() + 1) + ", " + ingredientId.b() + ": " + ingredientDeficit.get());
                }

                publish(evaluation);
                return Optional.empty();
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    best = Optional.of(evaluation);
                    System.out.println("Total score of best diet plan: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread11() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                // TODO: Get list of variable ingredients from template instead of diet plan and create initital diet
                // plan using that list?
                final DietPlan startDietPlan = createStartDietPlan();
                final ArrayList<Pair<Integer, FoodItem>> variableIngredients = startDietPlan.getVariableIngredients();
                final ArrayList<Pair<Pair<Integer, FoodItem>, Double>> ingredientValues =
                        new ArrayList<Pair<Pair<Integer, FoodItem>, Double>>(variableIngredients.size());
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                final double baseValue = evaluation(startDietPlan, evaluationFunction).getTotalScore();
                for (final Pair<Integer, FoodItem> ingredientId : variableIngredients) {
                    final Mutable<Optional<DietPlan>> maybeDietPlan = mutable(Optional.of(startDietPlan));
                    final Mutable<Optional<Pair<Double, Double>>> maxValueInfo =
                            mutable(Optional.<Pair<Double, Double>>empty());
                    final Mutable<Double> portions = mutable(0.0);
                    while (maybeDietPlan.get().isPresent()) {
                        maybeDietPlan.set(maybeDietPlan.get().get().addPortion(ingredientId));
                        maybeDietPlan.get().ifPresent(new Consumer<DietPlan>() {
                            @Override
                            public void accept(final DietPlan newDietPlan) {
                                portions.set(portions.get() + 1.0);
                                final Evaluation<DietPlan> evaluation = evaluation(newDietPlan, evaluationFunction);
                                final double value = evaluation.getTotalScore() - baseValue;
                                if (value > maxValueInfo.get().get().a()) {
                                    final Pair<Double, Double> valueInfo = pair(value, portions.get());
                                    maxValueInfo.set(Optional.of(valueInfo));
                                }
                            }
                        });
                    }

                    final double ingredientValue = maxValueInfo.get().map(new Function<Pair<Double, Double>, Double>() {
                        @Override
                        public Double apply(final Pair<Double, Double> valueInfo) {
                            // An ingredient that needs to be selected x times to reach its potential, will receive
                            // x times its basic value.
                            return valueInfo.a() * valueInfo.b();
                        }
                    }).orElse(0.0);
                    ingredientValues.add(pair(ingredientId, ingredientValue));
                }

                for (final Pair<Pair<Integer, FoodItem>, Double> ingredientValue : ingredientValues) {
                    System.out.println(ingredientValue.a() + ": " + ingredientValue.b());
                }

                return Optional.empty();
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    best = Optional.of(evaluation);
                    System.out.println("Total score of best diet plan: " + evaluation.getTotalScore());
                }
            }
        };
    }

    private static ArrayList<MealTemplate> getMealTemplates() {
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();
//        mealTemplates.add(MUESLI);
//        mealTemplates.add(SALAD);
//        mealTemplates.add(SMOOTHIE);
//        mealTemplates.add(SNACK);
//        mealTemplates.add(STIR_FRY_WITH_PASTA);
//        mealTemplates.add(STIR_FRY_WITH_RICE);
        mealTemplates.add(TEST_MIX);
        return mealTemplates;
    }

    private static DietPlan createStartDietPlan() {
        final int numberOfMeals = REQUIREMENTS.getNumberOfMeals();
        final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            final int mealTemplateIndex = RANDOM.nextInt(MEAL_TEMPLATES.size());
            meals.add(MEAL_TEMPLATES.get(mealTemplateIndex).getMinimalMeal());
        }
        return dietPlan(meals);
    }

    private static Function<DietPlan, Scores> getEvaluationFunction() {
        return new Function<DietPlan, Scores>() {
            @Override
            public Scores apply(final DietPlan dietPlan) {
                return dietPlan.getScores(REQUIREMENTS);
            }
        };
    }

    private static Evaluation<DietPlan> createIndividual(final DietPlan startDietPlan,
                                                         final Function<DietPlan, Scores> evaluationFunction,
                                                         final int index) {
        Evaluation<DietPlan> evaluation = evaluation(startDietPlan, evaluationFunction);
        double bestScore = evaluation.getTotalScore();
        boolean continueAdding = true;
        while (continueAdding) {
            final DietPlan dietPlan = evaluation.getObject();
            final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
            continueAdding = false;
            while (!continueAdding && !variableIngredients.isEmpty()) {
                final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
                final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
                final Optional<DietPlan> maybeNewDietPlan = dietPlan.addPortion(ingredientId);
                if (maybeNewDietPlan.isPresent()) {
                    final Evaluation<DietPlan> newEvaluation = evaluation(maybeNewDietPlan.get(), evaluationFunction);
                    final double newScore = newEvaluation.getTotalScore();
                    if (newScore > bestScore) {
                        evaluation = newEvaluation;
                        bestScore = newScore;
                        continueAdding = true;
                    }
                }
                if (!continueAdding) {
                    variableIngredients.remove(ingredientIndex);
                }
            }
        }
        System.out.println("Created candidate " + (index + 1) + ".");
        return evaluation;
    }

    private static Evaluation<DietPlan> createIndividual2(final DietPlan startDietPlan,
                                                          final Function<DietPlan, Scores> evaluationFunction,
                                                          final int index) {
        final ArrayList<Pair<Requirement, Integer>> scoreIds = REQUIREMENTS.getScoreIds();
        final int scoreIdsSize = scoreIds.size();
        Evaluation<DietPlan> evaluation = evaluation(startDietPlan, evaluationFunction);
        boolean continueAdding = true;
        while (continueAdding) {
            final DietPlan dietPlan = evaluation.getObject();
            final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
            continueAdding = false;
            while (!continueAdding && !variableIngredients.isEmpty()) {
                final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
                final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
                final Optional<DietPlan> maybeNewDietPlan = dietPlan.addPortion(ingredientId);
                if (maybeNewDietPlan.isPresent()) {
                    final Evaluation<DietPlan> newEvaluation = evaluation(maybeNewDietPlan.get(), evaluationFunction);
                    for (int i = 0; !continueAdding && i < scoreIdsSize; ++i) {
                        final Pair<Requirement, Integer> scoreId = scoreIds.get(i);
                        if (newEvaluation.getScore(scoreId).getScore() > evaluation.getScore(scoreId).getScore()) {
                            evaluation = newEvaluation;
                            continueAdding = true;
                        }
                    }
                }
                if (!continueAdding) {
                    variableIngredients.remove(ingredientIndex);
                }
            }
        }
//        System.out.println("Created candidate " + (index + 1) + ".");
        return evaluation;
    }

    private static Evaluation<DietPlan> createIndividual3(final Evaluation<DietPlan> base) {
        final ArrayList<Pair<Requirement, Integer>> scoreIds = REQUIREMENTS.getScoreIds();
        final int scoreIdsSize = scoreIds.size();
        final Function<DietPlan, Scores> evaluationFunction = base.getEvaluationFunction();
        Evaluation<DietPlan> evaluation = base;
        boolean continueAdding = true;
        while (continueAdding) {
            final DietPlan dietPlan = evaluation.getObject();
            final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
            continueAdding = false;
            while (!continueAdding && !variableIngredients.isEmpty()) {
                final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
                final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
                final Optional<DietPlan> maybeNewDietPlan = dietPlan.addPortion(ingredientId);
                if (maybeNewDietPlan.isPresent()) {
                    final Evaluation<DietPlan> newEvaluation = evaluation(maybeNewDietPlan.get(), evaluationFunction);
                    for (int i = 0; !continueAdding && i < scoreIdsSize; ++i) {
                        final Pair<Requirement, Integer> scoreId = scoreIds.get(i);
                        if (newEvaluation.getScore(scoreId).getScore() > evaluation.getScore(scoreId).getScore()) {
                            evaluation = newEvaluation;
                            continueAdding = true;
                        }
                    }
                }
                if (!continueAdding) {
                    variableIngredients.remove(ingredientIndex);
                }
            }
        }
        return evaluation;
    }

    private static Evaluation<DietPlan> createIndividual4(final Evaluation<DietPlan> base) {
        final Mutable<DietPlan> dietPlan = mutable(base.getObject());
        final double energyDemand = REQUIREMENTS.getEnergyDemand();
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = base.getObject().getVariableIngredients();
        while (dietPlan.get().getEnergy() < energyDemand) {
            final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
            final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
            final Optional<DietPlan> maybeNewDietPlan = dietPlan.get().addPortion(ingredientId);
            maybeNewDietPlan.ifPresent(new Consumer<DietPlan>() {
                @Override
                public void accept(final DietPlan newDietPlan) {
                    dietPlan.set(newDietPlan);
                }
            });
        }
        return evaluation(dietPlan.get(), base.getEvaluationFunction());
    }

    private static Evaluation<DietPlan> createIndividual5(final Evaluation<DietPlan> base) {
        final double energyDemand = REQUIREMENTS.getEnergyDemand();
        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = base.getObject().getVariableIngredients();
        Evaluation<DietPlan> result = base;
        while (result.getObject().getEnergy() < energyDemand) {
            final Evaluation<DietPlan> curEvaluation = result;
            final Map<Evaluation<DietPlan>, Double> candidateMap = new HashMap<Evaluation<DietPlan>, Double>();
            final Mutable<Optional<Double>> maybeMinValue = mutable(Optional.<Double>empty());
            for (final Pair<Integer, FoodItem> ingredientId : variableIngredients) {
                final Optional<DietPlan> maybeNewDietPlan = curEvaluation.getObject().addPortion(ingredientId);
                maybeNewDietPlan.ifPresent(new Consumer<DietPlan>() {
                    @Override
                    public void accept(final DietPlan newDietPlan) {
                        final Evaluation<DietPlan> newEvaluation = evaluation(newDietPlan, base.getEvaluationFunction());
                        final double value = newEvaluation.getTotalScore(); // TODO: Sum up degradation (compare to curEvaluation)
                        candidateMap.put(newEvaluation, value);
                        if (!maybeMinValue.get().isPresent() || value < maybeMinValue.get().get()) {
                            maybeMinValue.set(Optional.of(value));
                        }
                    }
                });
                // TODO: Remove ingredient from variableIngredients if adding a portion failed (use iterator?)
            }

            final ArrayList<Evaluation<DietPlan>> candidates = new ArrayList<Evaluation<DietPlan>>(candidateMap.keySet());
            final double minValue = maybeMinValue.get().orElse(0.0);
            final Function<Evaluation<DietPlan>, Double> valFunc = new Function<Evaluation<DietPlan>, Double>() {
                @Override
                public Double apply(final Evaluation<DietPlan> evaluation) {
                    return candidateMap.get(evaluation) - minValue;
                }
            };
            final int winnerIndex = selectElement(candidates, RANDOM.nextDouble(), valFunc);
            result = candidates.get(winnerIndex);
            System.out.println(result.getTotalScore());
        }
        return result;
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DietPlanner();
            }
        });
    }
}
