package optimization;

import diet.Requirement;
import diet.Score;
import diet.Scores;
import util.Evaluation;
import util.Mutable;
import util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static util.Global.RANDOM;
import static util.Pair.pair;

public abstract class Optimization {
    public static <T> Evaluation<T> optimize(final ArrayList<Evaluation<T>> startPopulation,
                                             final int maxPopulationSize,
                                             final Comparator<Evaluation<T>> evaluationComparator,
                                             final Consumer<Evaluation<T>> updateBestCallback,
                                             final Supplier<Boolean> abortCondition,
                                             final Function<Pair<Evaluation<T>, Evaluation<T>>, Evaluation<T>> mateFunction) {
        final ArrayList<Evaluation<T>> population = new ArrayList<Evaluation<T>>(startPopulation);
        Collections.sort(population, evaluationComparator);
        updateBestCallback.accept(population.get(0));

        while (!abortCondition.get()) {
            final int populationSize = population.size();
            final int parentIndex1 = RANDOM.nextInt(populationSize);
            final int parentIndex2 = RANDOM.nextInt(populationSize);
            if (parentIndex1 != parentIndex2) {
                final Evaluation<T> parent1 = population.get(parentIndex1);
                final Evaluation<T> parent2 = population.get(parentIndex2);
                final Evaluation<T> child = mateFunction.apply(pair(parent1, parent2));
                final int index = Collections.binarySearch(population, child, evaluationComparator);
                final int insertionIndex = index < 0 ? -index - 1 : index;
                population.add(insertionIndex, child);
                final int newPopulationSize = population.size();
                if (newPopulationSize > maxPopulationSize) {
                    final Evaluation<T> best = population.get(0);
                    Optional<Integer> maybeRemovalIndex = Optional.empty();
                    double worstContributionPotential = 0.0;
                    for (int i = newPopulationSize - 1; i > 0; --i) {
                        final Evaluation<T> evaluation = population.get(i);
                        final Scores scores = evaluation.getScores();
                        final Mutable<Double> contributionPotential = Mutable.mutable(0.0);
                        scores.forEach(new BiConsumer<Requirement, ArrayList<Score>>() {
                            @Override
                            public void accept(final Requirement requirement, final ArrayList<Score> scores) {
                                final int scoresSize = scores.size();
                                for (int i = 0; i < scoresSize; ++i) {
                                    final double score = scores.get(i).getWeightedScore();
                                    final double scoreOfBest = best.getScore(requirement, i).getWeightedScore();
                                    final double scoreDiff = score - scoreOfBest;
                                    if (scoreDiff > 0.0) {
                                        contributionPotential.set(contributionPotential.get() + scoreDiff);
                                    }
                                }
                            }
                        });
                        if (!maybeRemovalIndex.isPresent() || contributionPotential.get() < worstContributionPotential) {
                            maybeRemovalIndex = Optional.of(i);
                            worstContributionPotential = contributionPotential.get();
                        }
                    }
                    maybeRemovalIndex.ifPresent(new Consumer<Integer>() {
                        @Override
                        public void accept(final Integer removalIndex) {
                            population.remove(removalIndex.intValue());
                        }
                    });
                }
                if (insertionIndex == 0) {
                    updateBestCallback.accept(population.get(0));
                }
            }
        }

        return population.get(0);
    }
}
