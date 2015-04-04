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

        final SwingWorker<Optional<Evaluation<DietPlan>>, Optional<Evaluation<DietPlan>>> optimizationThread = createOptimizationThread();
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

    private SwingWorker<Optional<Evaluation<DietPlan>>, Optional<Evaluation<DietPlan>>> createOptimizationThread() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Optional<Evaluation<DietPlan>>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                final int startPopulationSize = 10;
                final int maxPopulationSize = 100;
                final int numberOfPopulations = 5;
                final DietPlan startDietPlan = createStartDietPlan();
                final Function<DietPlan, Scores> evaluationFunction = new Function<DietPlan, Scores>() {
                    @Override
                    public Scores apply(final DietPlan dietPlan) {
                        return dietPlan.getScores(REQUIREMENTS);
                    }
                };
                return optimize(new Supplier<ArrayList<Evaluation<DietPlan>>>() {
                    @Override
                    public ArrayList<Evaluation<DietPlan>> get() {
                        final ArrayList<Evaluation<DietPlan>> startPopulation = new ArrayList<Evaluation<DietPlan>>(startPopulationSize);
                        for (int i = 0; i < startPopulationSize && !isCancelled(); ++i) {
                            final Evaluation<DietPlan> evaluation = createIndividual(startDietPlan, evaluationFunction, Optional.<Pair<Requirement, Integer>>empty(), i);
                            startPopulation.add(evaluation);
                        }
                        return startPopulation;
                    }
                }, maxPopulationSize, numberOfPopulations, new Comparator<Evaluation<DietPlan>>() {
                    @Override
                    public int compare(final Evaluation<DietPlan> evaluation1, final Evaluation<DietPlan> evaluation2) {
                        return Double.compare(evaluation2.getTotalScore(), evaluation1.getTotalScore()); // Descending order
                    }
                }, new Consumer<Optional<Evaluation<DietPlan>>>() {
                    @Override
                    public void accept(final Optional<Evaluation<DietPlan>> maybeEvaluation) {
                        publish(maybeEvaluation);
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
                });
            }

            @Override
            protected void process(final List<Optional<Evaluation<DietPlan>>> chunks) {
                best = chunks.get(chunks.size() - 1);
                best.ifPresent(new Consumer<Evaluation<DietPlan>>() {
                    @Override
                    public void accept(final Evaluation<DietPlan> evaluation) {
                        System.out.println("Best score: " + evaluation.getTotalScore());
                    }
                });
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

    private static Evaluation<DietPlan> createIndividual(final DietPlan startDietPlan,
                                                         final Function<DietPlan, Scores> evaluationFunction,
                                                         final Optional<Pair<Requirement, Integer>> maybeScoreId,
                                                         final int index) {
        Evaluation<DietPlan> evaluation = evaluation(startDietPlan, evaluationFunction);
        double bestScore = evaluation.getScore(maybeScoreId);
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
                    final double newScore = newEvaluation.getScore(maybeScoreId);
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

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DietPlanner();
            }
        });
    }
}
