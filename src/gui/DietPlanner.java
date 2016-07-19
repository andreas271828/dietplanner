/**********************************************************************
 DietPlanner

 Copyright (C) 2015-2016 Andreas Huemer

 This file is part of DietPlanner.

 DietPlanner is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at
 your option) any later version.

 DietPlanner is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/
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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static diet.DietPlanTemplate.dietPlanTemplate;
import static java.lang.Math.max;
import static util.Evaluation.evaluation;
import static util.Global.RANDOM;
import static util.Limits2.limits2;
import static util.Mutable.mutable;
import static util.Pair.pair;

public class DietPlanner extends JFrame {
    private static final Requirements REQUIREMENTS = new Requirements(PersonalDetails.ANDREAS, 4, 12);
    private static final ArrayList<Pair<ArrayList<MealTemplate>, Limits2>> MEAL_TEMPLATES = getMealTemplates();

    private Optional<Evaluation<DietPlan>> best = Optional.empty();
    private final long startTime;

    private JPanel panel;
    private JButton stopButton;

    private DietPlanner() {
        super("DietPlanner");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(panel);

        final SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> optimizationThread = createOptimizationThread();
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                optimizationThread.cancel(false);

                final long endTime = System.nanoTime();
                System.out.println();
                System.out.println("Optimisation finished. Time elapsed: " + ((endTime - startTime) / 1e9) + " sec");
                System.out.println();

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

        startTime = System.nanoTime();
        optimizationThread.execute();
    }

    private SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>> createOptimizationThread() {
        return new SwingWorker<Optional<Evaluation<DietPlan>>, Evaluation<DietPlan>>() {
            @Override
            protected Optional<Evaluation<DietPlan>> doInBackground() throws Exception {
                final int numberOfCandidates = 1000;
                final int numberOfActions = 20;
                final int eliminationInterval = 50;

                final ArrayList<Pair<Evaluation<DietPlan>, Double>> candidates =
                        new ArrayList<Pair<Evaluation<DietPlan>, Double>>(numberOfCandidates);
                final Function<DietPlan, Scores> evaluationFunction = getEvaluationFunction();
                for (int i = 0; i < numberOfCandidates; ++i) {
                    final DietPlan dietPlan = createStartDietPlan();
                    final Evaluation<DietPlan> evaluation = evaluation(dietPlan, evaluationFunction);
                    final double totalScore = evaluation.getTotalScore();
                    candidates.add(pair(evaluation, totalScore));
                }

                int iterations = 0;
                while (!isCancelled()) {
                    final ArrayList<Pair<Evaluation<DietPlan>, Double>> oldCandidates =
                            new ArrayList<Pair<Evaluation<DietPlan>, Double>>(candidates);
                    candidates.clear();
                    for (final Pair<Evaluation<DietPlan>, Double> oldCandidate : oldCandidates) {
                        final Evaluation<DietPlan> evaluation = oldCandidate.a();
                        final DietPlan dietPlan = evaluation.getObject();
                        final ArrayList<Pair<Integer, FoodItem>> variableIngredients = dietPlan.getVariableIngredients();
                        final Scores scores = evaluation.getScores();
                        final Optional<Pair<Requirement, Integer>> maybeScoreId = scores.selectScoreByDiff(RANDOM.nextDouble());
                        final double oldScore = evaluation.getScore(maybeScoreId);
                        final double oldTotalScore = evaluation.getTotalScore();

                        final Mutable<Optional<Evaluation<DietPlan>>> maybeNewCandidate =
                                mutable(Optional.<Evaluation<DietPlan>>empty());
                        for (int i = 0; i < numberOfActions; ++i) {
                            final boolean add = RANDOM.nextBoolean();
                            final int ingredientIndex = RANDOM.nextInt(variableIngredients.size());
                            final Pair<Integer, FoodItem> ingredientId = variableIngredients.get(ingredientIndex);
                            final Optional<DietPlan> maybeNewDietPlan = add ?
                                    dietPlan.addPortion(ingredientId) :
                                    dietPlan.removePortion(ingredientId);
                            maybeNewDietPlan.ifPresent(new Consumer<DietPlan>() {
                                @Override
                                public void accept(final DietPlan newDietPlan) {
                                    final Evaluation<DietPlan> newCandidate = evaluation(newDietPlan, evaluationFunction);
                                    final double newScore = newCandidate.getScore(maybeScoreId);
                                    if (newScore > oldScore) {
                                        final boolean useNewCandidate;
                                        if (!maybeNewCandidate.get().isPresent()) {
                                            useNewCandidate = true;
                                        } else {
                                            final double newTotalScore = newCandidate.getTotalScore();

                                            final Evaluation<DietPlan> otherCandidate = maybeNewCandidate.get().get();
                                            final double otherScore = otherCandidate.getScore(maybeScoreId);
                                            final double otherTotalScore = otherCandidate.getTotalScore();

                                            if (newTotalScore >= oldTotalScore) {
                                                useNewCandidate = otherTotalScore < oldTotalScore || newScore > otherScore;
                                            } else {
                                                useNewCandidate = otherTotalScore < oldTotalScore && newTotalScore > otherTotalScore;
                                            }
                                        }

                                        if (useNewCandidate) {
                                            maybeNewCandidate.set(Optional.of(newCandidate));
                                            publish(newCandidate);
                                        }
                                    }
                                }
                            });
                        }

                        final Evaluation<DietPlan> newCandidate = maybeNewCandidate.get().orElse(evaluation);
                        final double peakTotalScore = max(oldCandidate.b(), newCandidate.getTotalScore());
                        candidates.add(pair(newCandidate, peakTotalScore));
                    }

                    ++iterations;
                    if (candidates.size() > 1 && iterations % eliminationInterval == 0) {
                        int worstCandidateIndex = 0;
                        double worstCandidateScore = candidates.get(0).b();
                        for (int i = 1; i < candidates.size(); ++i) {
                            final double candidateScore = candidates.get(i).b();
                            if (candidateScore < worstCandidateScore) {
                                worstCandidateIndex = i;
                                worstCandidateScore = candidateScore;
                            }
                        }
                        candidates.remove(worstCandidateIndex);
                        System.out.println("Candidates left: " + candidates.size());
                    }
                }

                return Optional.empty();
            }

            @Override
            protected void process(final List<Evaluation<DietPlan>> chunks) {
                for (final Evaluation<DietPlan> evaluation : chunks) {
                    if (!best.isPresent() || evaluation.getTotalScore() > best.get().getTotalScore()) {
                        best = Optional.of(evaluation);

                        final Scores scores = evaluation.getScores();
                        final double totalScore = scores.getTotalScore();
                        final double weightSum = scores.getWeightSum();
                        final long endTime = System.nanoTime();
                        final double seconds = (endTime - startTime) / 1e9;
                        System.out.println("Total score of best diet plan: " + totalScore + " / " + weightSum +
                                " (" + (100.0 * totalScore / weightSum) + "%); Time elapsed: " + seconds + " sec");
                    }
                }
            }
        };
    }

    private static ArrayList<Pair<ArrayList<MealTemplate>, Limits2>> getMealTemplates() {
        final ArrayList<Pair<ArrayList<MealTemplate>, Limits2>> mealTemplates =
                new ArrayList<Pair<ArrayList<MealTemplate>, Limits2>>();

        /* Generic meals
        final ArrayList<MealTemplate> genericList = new ArrayList<MealTemplate>();
        genericList.add(MealTemplate.GENERIC_MEAL);
        mealTemplates.add(pair(genericList, limits2(0.0, 1.0)));
        */

        final ArrayList<MealTemplate> breakfastList = new ArrayList<MealTemplate>();
        breakfastList.add(MealTemplate.RANDOM_MIX);
        breakfastList.add(MealTemplate.AVOCADO_ON_TOAST);
        breakfastList.add(MealTemplate.BOILED_EGGS);
        breakfastList.add(MealTemplate.FRIED_EGGS);
        breakfastList.add(MealTemplate.AVOCADO_ON_TOAST_WITH_FRIED_EGGS);
        breakfastList.add(MealTemplate.BOILED_EGGS_WITH_RANDOM_MIX);
        breakfastList.add(MealTemplate.FRIED_EGGS_WITH_RANDOM_MIX);
        mealTemplates.add(pair(breakfastList, limits2(0.0, 1.0)));

        /*
        final ArrayList<MealTemplate> breakfastList = new ArrayList<MealTemplate>();
        breakfastList.add(MealTemplate.BREAKFAST_1);
        breakfastList.add(MealTemplate.BREAKFAST_2_WITH_CREAM);
        breakfastList.add(MealTemplate.BREAKFAST_2_WITH_DOUBLE_CREAM);
        breakfastList.add(MealTemplate.BREAKFAST_2_WITH_YOGHURT);
        breakfastList.add(MealTemplate.SMOOTHIE_1_WITH_ALMONDS);
        breakfastList.add(MealTemplate.SMOOTHIE_1_WITH_YOGHURT);
        mealTemplates.add(pair(breakfastList, limits2(0.3, 1.0)));

        final ArrayList<MealTemplate> saladList = new ArrayList<MealTemplate>();
        saladList.add(MealTemplate.SALAD_1_WITH_MAYONNAISE);
        saladList.add(MealTemplate.SALAD_1_WITH_SOUR_CREAM);
        saladList.add(MealTemplate.SALAD_1_WITH_YOGHURT);
        mealTemplates.add(pair(saladList, limits2(0.1, 1.0)));

        final ArrayList<MealTemplate> stirFryList = new ArrayList<MealTemplate>();
        stirFryList.add(MealTemplate.STIR_FRY_1);
        mealTemplates.add(pair(stirFryList, limits2(0.1, 0.3)));
        */

        /*
        final ArrayList<MealTemplate> breakfastList = new ArrayList<MealTemplate>();
        breakfastList.add(MealTemplate.AVOCADO_ON_TOAST);
        breakfastList.add(MealTemplate.MUESLI);
        breakfastList.add(MealTemplate.SNACK);
        mealTemplates.add(pair(breakfastList, limits2(0.3, 1.0)));

        final ArrayList<MealTemplate> stirFryList = new ArrayList<MealTemplate>();
        stirFryList.add(MealTemplate.STIR_FRY_WITH_GNOCCHI);
        stirFryList.add(MealTemplate.STIR_FRY_WITH_PASTA);
        stirFryList.add(MealTemplate.STIR_FRY_WITH_RICE);
        mealTemplates.add(pair(stirFryList, limits2(0.0, 0.4)));

        final ArrayList<MealTemplate> otherList = new ArrayList<MealTemplate>();
        otherList.add(MealTemplate.SALAD);
        otherList.add(MealTemplate.SMOOTHIE);
        mealTemplates.add(pair(otherList, limits2(0.0, 1.0)));
        */

        return mealTemplates;
    }

    private static DietPlan createStartDietPlan() {
        final int numberOfMeals = REQUIREMENTS.getNumberOfMeals();
        final DietPlanTemplate dietPlanTemplate = dietPlanTemplate(MEAL_TEMPLATES, numberOfMeals);
        return dietPlanTemplate.getMinimalDietPlan();
    }

    private static Function<DietPlan, Scores> getEvaluationFunction() {
        return new Function<DietPlan, Scores>() {
            @Override
            public Scores apply(final DietPlan dietPlan) {
                return dietPlan.getScores(REQUIREMENTS);
            }
        };
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DietPlanner();
            }
        });
    }
}
