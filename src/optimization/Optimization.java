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
                                                       final Consumer<Optional<Evaluation<T>>> updateBestCallback,
                                                       final Supplier<Boolean> abortCondition,
                                                       final Function<Pair<Evaluation<T>, Evaluation<T>>, Evaluation<T>> mateFunction) {
        Optional<Evaluation<T>> maybeBest = Optional.empty();
        final ArrayList<ArrayList<Evaluation<T>>> populations = new ArrayList<ArrayList<Evaluation<T>>>(numberOfPopulations);
        for (int i = 0; i < numberOfPopulations; ++i) {
            final ArrayList<Evaluation<T>> population = startPopulation.get();
            populations.add(population);
            Collections.sort(population, evaluationComparator);
            maybeBest = updateBest(maybeBest, population.get(0), evaluationComparator, updateBestCallback);
        }

        // TODO: Mix populations
        while (!abortCondition.get()) {
            for (int i = 0; i < numberOfPopulations && !abortCondition.get(); ++i) {
                final ArrayList<Evaluation<T>> population = populations.get(i);
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
                                                          final Consumer<Optional<Evaluation<T>>> updateBestCallback) {
        final Optional<Evaluation<T>> newMaybeBest;
        if (!maybeBest.isPresent() || evaluationComparator.compare(maybeBest.get(), curBest) > 0) {
            newMaybeBest = Optional.of(curBest);
            updateBestCallback.accept(newMaybeBest);
        } else {
            newMaybeBest = maybeBest;
        }
        return newMaybeBest;
    }
}
