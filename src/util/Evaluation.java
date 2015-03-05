package util;

import diet.Requirement;
import diet.Score;
import diet.Scores;

import java.util.Optional;
import java.util.function.Function;

public class Evaluation<T> {
    private final T object;
    private final Function<T, Scores> evaluationFunction;
    private final LazyValue<Scores> scores;

    public static <T> Evaluation<T> evaluation(final T object,
                                               final Function<T, Scores> evaluationFunction) {
        return new Evaluation<T>(object, evaluationFunction);
    }

    public static <T> Evaluation<T> evaluation(final T object,
                                               final Function<T, Scores> evaluationFunction,
                                               final Scores scores) {
        return new Evaluation<T>(object, evaluationFunction, scores);
    }

    private Evaluation(final T object,
                       final Function<T, Scores> evaluationFunction) {
        this.object = object;
        this.evaluationFunction = evaluationFunction;
        scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return evaluationFunction.apply(object);
            }
        };
    }

    private Evaluation(final T object,
                       final Function<T, Scores> evaluationFunction,
                       final Scores scores) {
        this.object = object;
        this.evaluationFunction = evaluationFunction;
        this.scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return scores;
            }
        };
    }

    public T getObject() {
        return object;
    }

    public Function<T, Scores> getEvaluationFunction() {
        return evaluationFunction;
    }

    public Scores getScores() {
        return scores.get();
    }

    public Score getScore(final Pair<Requirement, Integer> scoreId) {
        return getScores().getScore(scoreId);
    }

    public double getTotalScore() {
        return getScores().getTotalScore();
    }

    public Optional<Pair<Requirement, Integer>> getWorstScore() {
        return getScores().getWorstScore();
    }
}
