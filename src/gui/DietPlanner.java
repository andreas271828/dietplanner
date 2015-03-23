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
import static diet.MealTemplate.SALAD;
import static optimization.Optimization.optimize;
import static util.Evaluation.evaluation;
import static util.Global.RANDOM;
import static util.Mutable.mutable;
import static util.Pair.pair;

public class DietPlanner extends JFrame {
    private static final Requirements REQUIREMENTS = new Requirements(PersonalDetails.ANDREAS, 7, 21);

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
        final Function<DietPlan, Scores> evaluationFunction = new Function<DietPlan, Scores>() {
            @Override
            public Scores apply(final DietPlan dietPlan) {
                return dietPlan.getScores(REQUIREMENTS);
            }
        };
        return new SwingWorker<Evaluation<DietPlan>, Evaluation<DietPlan>>() {
            @Override
            protected Evaluation<DietPlan> doInBackground() throws Exception {
                final int startPopulationSize = 5;
                final int maxPopulationSize = 100;
                final Mutable<Pair<Integer, Integer>> startPopulationProgress = mutable(pair(0, startPopulationSize));
                return optimize(startPopulationSize, maxPopulationSize, new Supplier<Evaluation<DietPlan>>() {
                    @Override
                    public Evaluation<DietPlan> get() {
                        return createIndividual(evaluationFunction, startPopulationProgress);
                    }
                }, new Comparator<Evaluation<DietPlan>>() {
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

    private static Evaluation<DietPlan> createIndividual(final Function<DietPlan, Scores> evaluationFunction,
                                                         final Mutable<Pair<Integer, Integer>> startPopulationProgress) {
        final DietPlan startDietPlan = dietPlan(SALAD.getMinimalMeals(REQUIREMENTS.getNumberOfMeals()));
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
                    if (newEvaluation.getTotalScore() > evaluation.getTotalScore()) {
                        evaluation = newEvaluation;
                        continueAdding = true;
                    }
                }
                if (!continueAdding) {
                    variableIngredients.remove(ingredientIndex);
                }
            }
        }
        final Pair<Integer, Integer> progressInfo = startPopulationProgress.get();
        final int numberOfIndividuals = progressInfo.a() + 1;
        final int startPopulationSize = progressInfo.b();
        startPopulationProgress.set(pair(numberOfIndividuals, startPopulationSize));
        System.out.println("Created candidate " + numberOfIndividuals + " of " + startPopulationSize + ".");
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
