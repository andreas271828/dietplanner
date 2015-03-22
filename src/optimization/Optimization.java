package optimization;

import util.Evaluation;
import util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static util.Global.RANDOM;
import static util.Pair.pair;

public abstract class Optimization {
    public static <T> Evaluation<T> optimize(final int startPopulationSize,
                                             final int maxPopulationSize,
                                             final Supplier<Evaluation<T>> individualCreator,
                                             final Comparator<Evaluation<T>> evaluationComparator,
                                             final Consumer<Evaluation<T>> updateBestCallback,
                                             final Supplier<Boolean> abortCondition,
                                             final Function<Pair<Evaluation<T>, Evaluation<T>>, Evaluation<T>> mateFunction) {
        final ArrayList<Evaluation<T>> population = createPopulation(startPopulationSize, individualCreator);
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
                    population.remove(newPopulationSize - 1);
                }
                if (insertionIndex == 0) {
                    updateBestCallback.accept(population.get(0));
                }
            }
        }

        return population.get(0);
    }

    private static <T> ArrayList<Evaluation<T>> createPopulation(final int populationSize,
                                                                 final Supplier<Evaluation<T>> individualCreator) {
        final ArrayList<Evaluation<T>> population = new ArrayList<Evaluation<T>>(populationSize);
        for (int i = 0; i < populationSize; ++i) {
            final Evaluation<T> individual = individualCreator.get();
            population.add(individual);
        }
        return population;
    }
}
