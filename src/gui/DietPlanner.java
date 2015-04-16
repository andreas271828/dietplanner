package gui;

import diet.*;
import util.Evaluation;
import util.Limits2;
import util.Mutable;
import util.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static diet.DietPlan.dietPlan;
import static diet.Meal.meal;
import static diet.MealTemplate.*;
import static optimization.Optimization.optimize;
import static util.Evaluation.evaluation;
import static util.Global.RANDOM;
import static util.Global.selectElements;
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
                    // TODO: It might not be a good idea to manipulate a member variable (best) here.
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
                        while (energy < energyDemand && allIngredientIds.size() > 0) {
                            // TODO: Avoid potential infinite loop - maybe there are no more ingredients that can be added.
                            final int ingredientIndex = RANDOM.nextInt(allIngredientIds.size());
                            final Pair<Integer, FoodItem> ingredientId = allIngredientIds.get(ingredientIndex);
                            final int mealIndex = ingredientId.a();
                            final FoodItem foodItem = ingredientId.b();
                            final FoodItems ingredients = allIngredients.get(mealIndex);
                            final double oldAmount = ingredients.get(foodItem);
                            final double newAmount = oldAmount + foodItem.getPortionAmount();
                            final double maxAmount = mealTemplates.get(mealIndex).getMaxAmount(foodItem);
                            if (newAmount <= maxAmount) {
                                ingredients.set(foodItem, newAmount);
                                energy += foodItem.getProperty(FoodProperty.ENERGY) * (newAmount - oldAmount);
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
                                final DietPlan offspring = parent1.mate(parent2, mutationRateL2); // TODO: mateL2() = crossover anywhere; mutate only by one portion
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
                            for (int j = 0; j < populationSizeL2; ++j) {
                                // TODO: Mutation: Change meal template for a meal and add random ingredients to that meal until energy of diet plan > energy demand.
                                final ArrayList<Meal> meals = new ArrayList<Meal>(numberOfMeals);
                                final DietPlan dietPlan1 = parent1.get(j).getObject();
                                final DietPlan dietPlan2 = parent2.get(j).getObject();
                                for (int k = 0; k < crossoverIndex; ++k) {
                                    meals.add(dietPlan1.getMeal(k));
                                }
                                for (int k = crossoverIndex; k < numberOfMeals; ++k) {
                                    meals.add(dietPlan2.getMeal(k));
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

    private static ArrayList<MealTemplate> getMealTemplates() {
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();
        mealTemplates.add(MUESLI);
        mealTemplates.add(SALAD);
        mealTemplates.add(SMOOTHIE);
        mealTemplates.add(SNACK);
        mealTemplates.add(STIR_FRY_WITH_PASTA);
        mealTemplates.add(STIR_FRY_WITH_RICE);
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

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DietPlanner();
            }
        });
    }
}
