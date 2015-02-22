package util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static util.Global.RANDOM;

public class Evaluations<T> {
    private final ArrayList<Evaluation<T>> evaluations;
    private final int useCount;
    private boolean isSorted;
    private final LazyValue<Optional<Evaluation<T>>> best;
    private final LazyValue<double[]> scoreAccumulation;

    public static <T> Evaluations<T> evaluations(final ArrayList<Evaluation<T>> evaluations) {
        return new Evaluations<T>(evaluations, evaluations.size());
    }

    public static <T> Evaluations<T> evaluations(final ArrayList<Evaluation<T>> evaluations, final int useCount) {
        return new Evaluations<T>(evaluations, useCount);
    }

    private Evaluations(final ArrayList<Evaluation<T>> evaluations, final int useCount) {
        this.evaluations = evaluations;
        this.useCount = useCount;
        isSorted = false;
        best = getLazyBest();
        scoreAccumulation = getLazyScoreAccumulation();
    }

    private LazyValue<Optional<Evaluation<T>>> getLazyBest() {
        return new LazyValue<Optional<Evaluation<T>>>() {
            @Override
            protected Optional<Evaluation<T>> compute() {
                if (isSorted) {
                    return useCount > 0 ? Optional.of(evaluations.get(0)) : Optional.<Evaluation<T>>empty();
                } else {
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
            }
        };
    }

    private LazyValue<double[]> getLazyScoreAccumulation() {
        return new LazyValue<double[]>() {
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

    public ArrayList<Evaluation<T>> getEvaluations() {
        if (useCount < evaluations.size()) {
            sort();
            evaluations.subList(useCount, evaluations.size()).clear();
        }
        return evaluations;
    }

    public int getEvaluationsCount() {
        return useCount;
    }

    public Optional<Evaluation<T>> getBest() {
        return best.get();
    }

    public Optional<T> selectProbabilistically() {
        final ArrayList<Evaluation<T>> evaluations = getEvaluations();
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
        } else {
            return Optional.empty();
        }
    }

    private void sort() {
        if (!isSorted) {
            evaluations.sort(new Comparator<Evaluation<T>>() {
                @Override
                public int compare(final Evaluation<T> evaluation1, final Evaluation<T> evaluation2) {
                    return Double.compare(evaluation2.getTotalScore(), evaluation1.getTotalScore()); // Descending order
                }
            });
            isSorted = true;
            scoreAccumulation.invalidate();
        }
    }
}
