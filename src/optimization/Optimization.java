package optimization;

import util.Evaluation;
import util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static util.Global.RANDOM;
import static util.Pair.pair;

public abstract class Optimization {
    public static <T> Optional<Evaluation<T>> optimize(final Supplier<ArrayList<Evaluation<T>>> startPopulation,
                                                       final int maxPopulationSize,
                                                       final int numberOfPopulations,
                                                       final Comparator<Evaluation<T>> evaluationComparator,
                                                       final Consumer<Evaluation<T>> updateBestCallback,
                                                       final Supplier<Boolean> abortCondition,
                                                       final Function<Pair<Evaluation<T>, Evaluation<T>>, Evaluation<T>> mateFunction,
                                                       final double populationMixRate) {
        Optional<Evaluation<T>> maybeBest = Optional.empty();
        final ArrayList<ArrayList<Evaluation<T>>> populations = new ArrayList<ArrayList<Evaluation<T>>>(numberOfPopulations);
        for (int i = 0; i < numberOfPopulations; ++i) {
            final ArrayList<Evaluation<T>> population = startPopulation.get();
            populations.add(population);
            Collections.sort(population, evaluationComparator);
            maybeBest = updateBest(maybeBest, population.get(0), evaluationComparator, updateBestCallback);
        }

        while (!abortCondition.get()) {
            for (int i = 0; i < numberOfPopulations && !abortCondition.get(); ++i) {
                final Evaluation<T> parent1 = getRandomParent(populations, i, 0.0);
                final Evaluation<T> parent2 = getRandomParent(populations, i, populationMixRate);
                if (parent1 != parent2) {
                    final Evaluation<T> child = mateFunction.apply(pair(parent1, parent2));
                    final ArrayList<Evaluation<T>> population = populations.get(i);
                    final int index = Collections.binarySearch(population, child, evaluationComparator);
                    final int insertionIndex = index < 0 ? -index - 1 : index;
                    population.add(insertionIndex, child);
                    final int newPopulationSize = population.size();
                    if (newPopulationSize > maxPopulationSize) {
                        population.remove(newPopulationSize - 1);
                    }
                    if (insertionIndex == 0) {
                        maybeBest = updateBest(maybeBest, population.get(0), evaluationComparator, updateBestCallback);
                    }
                }
            }
        }

        return maybeBest;
    }

    private static <T> Optional<Evaluation<T>> updateBest(final Optional<Evaluation<T>> maybeBest,
                                                          final Evaluation<T> curBest,
                                                          final Comparator<Evaluation<T>> evaluationComparator,
                                                          final Consumer<Evaluation<T>> updateBestCallback) {
        final Optional<Evaluation<T>> newMaybeBest;
        if (!maybeBest.isPresent() || evaluationComparator.compare(maybeBest.get(), curBest) > 0) {
            newMaybeBest = Optional.of(curBest);
            updateBestCallback.accept(curBest);
        } else {
            newMaybeBest = maybeBest;
        }
        return newMaybeBest;
    }

    private static <T> Evaluation<T> getRandomParent(final ArrayList<ArrayList<Evaluation<T>>> populations,
                                                     final int populationIndex,
                                                     final double populationMixRate) {
        final int parentPopulationIndex = RANDOM.nextDouble() < populationMixRate ?
                RANDOM.nextInt(populations.size()) :
                populationIndex;
        final ArrayList<Evaluation<T>> population = populations.get(parentPopulationIndex);
        final int parentIndex = RANDOM.nextInt(population.size());
        return population.get(parentIndex);
    }
}
