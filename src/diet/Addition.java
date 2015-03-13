package diet;

import util.Evaluation;
import util.Pair;

import java.util.Optional;

import static util.Evaluation.evaluation;

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
        // TODO: Create a new diet plan with both base additions (add addition2 to diet plan of addition1).
        final Optional<DietPlan> maybeDietPlan = Optional.empty();
        if (maybeDietPlan.isPresent()) {
            // TODO: Do similar stuff like in basic case (extract methods).
            final Evaluation<DietPlan> evaluation = evaluation(maybeDietPlan.get(), dietPlanEvaluation.getEvaluationFunction());
            final double baseScore = dietPlanEvaluation.getTotalScore();
            final double score = evaluation.getTotalScore();
            if (score > baseScore) {
                final Addition addition = new Addition() {
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

    public abstract Evaluation<DietPlan> getEvaluation();
}
