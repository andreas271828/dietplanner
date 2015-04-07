package gui;

import diet.*;
import util.Evaluation;
import util.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static diet.DietPlan.dietPlan;
import static diet.MealTemplate.*;
import static optimization.Optimization.optimize;
import static util.Evaluation.evaluation;
import static util.Global.RANDOM;

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

        final SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> optimizationThread = createOptimizationThread3();
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

    private Function<DietPlan, Scores> getEvaluationFunction() {
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

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DietPlanner();
            }
        });
    }
}
