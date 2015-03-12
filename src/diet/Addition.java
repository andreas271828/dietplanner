package diet;

import util.Evaluation;
import util.Pair;

import java.util.Optional;

import static util.Evaluation.evaluation;

public class Addition {
    private final Pair<Integer, FoodItem> ingredientId;
    private final Optional<Addition> base;
    private final Evaluation<DietPlan> evaluation;

    public static Optional<Addition> addition(final Pair<Integer, FoodItem> ingredientId,
                                              final Evaluation<DietPlan> dietPlanEvaluation) {
        return addition(ingredientId, Optional.<Addition>empty(), dietPlanEvaluation);
    }

    public static Optional<Addition> addition(final Pair<Integer, FoodItem> ingredientId,
                                              final Addition base) {
        // TODO: Combine two additions instead of addition and ingredient (one of them can be a basic one; addition can either have an ingredient or two additions; score must be better than both individual scores)?!
        return addition(ingredientId, Optional.of(base), base.getEvaluation());
    }

    private static Optional<Addition> addition(final Pair<Integer, FoodItem> ingredientId,
                                               final Optional<Addition> base,
                                               final Evaluation<DietPlan> dietPlanEvaluation) {
        final Optional<DietPlan> maybeDietPlan = dietPlanEvaluation.getObject().addPortion(ingredientId);
        if (maybeDietPlan.isPresent()) {
            final Evaluation<DietPlan> evaluation = evaluation(maybeDietPlan.get(), dietPlanEvaluation.getEvaluationFunction());
            final double baseScore = dietPlanEvaluation.getTotalScore();
            final double score = evaluation.getTotalScore();
            if (score > baseScore) {
                return Optional.of(new Addition(ingredientId, base, evaluation));
            }
        }
        return Optional.empty();
    }

    private Addition(final Pair<Integer, FoodItem> ingredientId,
                     final Optional<Addition> base,
                     final Evaluation<DietPlan> evaluation) {
        this.ingredientId = ingredientId;
        this.base = base;
        this.evaluation = evaluation;
    }

    public Optional<Addition> getBase() {
        return base;
    }

    public Evaluation<DietPlan> getEvaluation() {
        return evaluation;
    }
}
