package gui;

import diet.*;
import util.Evaluation;
import util.Mutable;
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
import static util.Mutable.mutable;

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

        final SwingWorker<Evaluation<DietPlan>, Evaluation<DietPlan>> optimizationThread = createOptimizationThread();
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
                        System.out.println(dietPlan);
                        System.out.println("Scores:");
                        System.out.println(scores);
                        System.out.println("Sorted relative scores:");
                        System.out.println(relScores);
                        System.out.println("Total score: " + scores.getTotalScore() + " of " + scores.getWeightSum());
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

    private SwingWorker<Evaluation<DietPlan>, Evaluation<DietPlan>> createOptimizationThread() {
        return new SwingWorker<Evaluation<DietPlan>, Evaluation<DietPlan>>() {
            @Override
            protected Evaluation<DietPlan> doInBackground() throws Exception {
                final DietPlan startDietPlan = createStartDietPlan();
                final Function<DietPlan, Scores> evaluationFunction = new Function<DietPlan, Scores>() {
                    @Override
                    public Scores apply(final DietPlan dietPlan) {
                        return dietPlan.getScores(REQUIREMENTS);
                    }
                };
                final int startPopulationSize = 10;
                final ArrayList<Evaluation<DietPlan>> startPopulation = new ArrayList<Evaluation<DietPlan>>(startPopulationSize);
                for (int i = 0; i < startPopulationSize && !isCancelled(); ++i) {
                    final Evaluation<DietPlan> evaluation = createIndividual(startDietPlan, evaluationFunction, Optional.<Pair<Requirement, Integer>>empty(), i);
                    startPopulation.add(evaluation);
                }
                final int maxPopulationSize = 1000;
                final Mutable<Integer> iterations = mutable(0);
                return optimize(startPopulation, maxPopulationSize, new Comparator<Evaluation<DietPlan>>() {
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
                        final int i = iterations.get();
                        iterations.set(i + 1);
                        final double mutationRate = i % 20 == 0 ? 0.05 : 0.001;
                        return evaluation(dietPlan1.mate(dietPlan2, mutationRate), evaluationFunction);
                    }
                });
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                final Evaluation<DietPlan> lastChunk = chunks.get(chunks.size() - 1);
                best = Optional.of(lastChunk);
                System.out.println("Best score: " + lastChunk.getTotalScore());
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
