package diet;

import util.Evaluation;
import util.Pair;

import java.util.ArrayList;
import java.util.Optional;

import static util.Evaluation.evaluation;
import static util.Pair.pair;

public abstract class Addition {
    public static Optional<Addition> addition(final Pair<Integer, FoodItem> ingredientId,
                                              final Evaluation<DietPlan> dietPlanEvaluation) {
        final Optional<DietPlan> maybeDietPlan = dietPlanEvaluation.getObject().addPortion(ingredientId);
        if (maybeDietPlan.isPresent()) {
            final Evaluation<DietPlan> evaluation = evaluation(maybeDietPlan.get(), dietPlanEvaluation.getEvaluationFunction());
            final double baseScore = dietPlanEvaluation.getTotalScore();
            final double score = evaluation.getTotalScore();
            if (score > baseScore) {
                final Addition addition = new Addition() {
                    @Override
                    public ArrayList<Pair<Integer, FoodItem>> getIngredients() {
                        final ArrayList<Pair<Integer, FoodItem>> ingredients = new ArrayList<Pair<Integer, FoodItem>>();
                        ingredients.add(ingredientId);
                        return ingredients;
                    }

                    @Override
                    public Optional<Pair<Addition, Addition>> getBases() {
                        return Optional.empty();
                    }

                    @Override
                    public Evaluation<DietPlan> getEvaluation() {
                        return evaluation;
                    }
                };
                return Optional.of(addition);
            }
        }
        return Optional.empty();
    }

    public static Optional<Addition> addition(final Addition addition1, final Addition addition2) {
        final Evaluation<DietPlan> evaluation1 = addition1.getEvaluation();
        final Evaluation<DietPlan> evaluation2 = addition2.getEvaluation();
        final Optional<DietPlan> maybeDietPlan = evaluation1.getObject().addAddition(addition2);
        if (maybeDietPlan.isPresent()) {
            final Evaluation<DietPlan> evaluation = evaluation(maybeDietPlan.get(), evaluation1.getEvaluationFunction());
            final double baseScore1 = evaluation1.getTotalScore();
            final double baseScore2 = evaluation2.getTotalScore();
            final double score = evaluation.getTotalScore();
            if (score > baseScore1 && score > baseScore2) {
                final Addition addition = new Addition() {
                    @Override
                    public ArrayList<Pair<Integer, FoodItem>> getIngredients() {
                        final ArrayList<Pair<Integer, FoodItem>> ingredients = new ArrayList<Pair<Integer, FoodItem>>();
                        ingredients.addAll(addition1.getIngredients());
                        ingredients.addAll(addition2.getIngredients());
                        return ingredients;
                    }

                    @Override
                    public Optional<Pair<Addition, Addition>> getBases() {
                        return Optional.of(pair(addition1, addition2));
                    }

                    @Override
                    public Evaluation<DietPlan> getEvaluation() {
                        return evaluation;
                    }
                };
                return Optional.of(addition);
            }
        }
        return Optional.empty();
    }

    private Addition() {
    }

    public abstract ArrayList<Pair<Integer, FoodItem>> getIngredients();

    public abstract Optional<Pair<Addition, Addition>> getBases();

    public abstract Evaluation<DietPlan> getEvaluation();

    @Override
    public String toString() {
        return getIngredients().toString();
    }
}
