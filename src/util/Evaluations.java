package util;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static util.Global.RANDOM;

public class Evaluations<T> {
    private final ArrayList<Evaluation<T>> evaluations;
    private final LazyValue<Optional<Evaluation<T>>> best;
    private final LazyValue<double[]> scoreAccumulation;

    public Evaluations(final ArrayList<Evaluation<T>> evaluations) {
        this.evaluations = evaluations;

        best = new LazyValue<Optional<Evaluation<T>>>() {
            @Override
            protected Optional<Evaluation<T>> compute() {
                Optional<Evaluation<T>> best = Optional.empty();
                for (final Evaluation<T> evaluation : evaluations) {
                    best = Optional.of(best.filter(new Predicate<Evaluation<T>>() {
                        @Override
                        public boolean test(final Evaluation<T> best) {
                            return best.getTotalScore() >= evaluation.getTotalScore();
                        }
                    }).orElse(evaluation));
                }
                return best;
            }
        };

        scoreAccumulation = new LazyValue<double[]>() {
            @Override
            protected double[] compute() {
                final int evaluationsCnt = evaluations.size();
                final double[] scoreAccumulation = new double[evaluationsCnt];
                for (int i = 0; i < evaluationsCnt; ++i) {
                    final double score = evaluations.get(i).getTotalScore();
                    scoreAccumulation[i] = score + (i > 0 ? scoreAccumulation[i - 1] : 0);
                }
                return scoreAccumulation;
            }
        };
    }

    public int getEvaluationsCount() {
        return evaluations.size();
    }

    public Optional<Evaluation<T>> getBest() {
        return best.get();
    }

    public Optional<T> selectProbabilistically() {
        final int evaluationsCnt = evaluations.size();
        if (evaluationsCnt > 0) {
            final double[] scoreAcc = scoreAccumulation.get();
            final double selector = RANDOM.nextDouble() * scoreAcc[evaluationsCnt - 1];
            return BinarySearch.findInRange(selector, scoreAcc).map(new Function<Integer, T>() {
                @Override
                public T apply(final Integer index) {
                    return evaluations.get(index).getObject();
                }
            });
        }

        return Optional.empty();
    }
}
