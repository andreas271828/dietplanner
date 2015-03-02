package util;

import diet.Requirement;
import diet.Score;
import diet.Scores;

import java.util.Optional;
import java.util.function.Function;

public class Evaluation<T> {
    private final T object;
    private final LazyValue<Scores> scores;

    public static <T> Evaluation<T> evaluation(final T object,
                                               final Function<T, Scores> evaluationFunction) {
        return new Evaluation<T>(object, evaluationFunction);
    }

    public static <T> Evaluation<T> evaluation(final T object, final Scores scores) {
        return new Evaluation<T>(object, scores);
    }

    private Evaluation(final T object,
                       final Function<T, Scores> evaluationFunction) {
        this.object = object;
        scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return evaluationFunction.apply(object);
            }
        };
    }

    private Evaluation(final T object, final Scores scores) {
        this.object = object;
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
