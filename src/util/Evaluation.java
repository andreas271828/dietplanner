package util;

import java.util.function.Function;

public class Evaluation<T> {
    private final T object;
    private final LazyValue<Scores> scores;

    public Evaluation(final T object,
                      final Function<T, Scores> evaluationFunction) {
        this.object = object;
        scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return evaluationFunction.apply(object);
            }
        };
    }

    public T getObject() {
        return object;
    }

    public Scores getScores() {
        return scores.get();
    }

    public double getTotalScore() {
        return getScores().getTotalScore();
    }
}
