package evolution;

import util.LazyValue;
import util.Scores;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenePool {
    private final static int SPECIES_SIZE = 10;
    private final static double NEW_SPECIES_RATE = 0.1;

    private final Species[] species;
    private boolean sorted = false;
    private final Function<Genome, Scores> fitnessFunction;
    private final LazyValue<Optional<Species>> bestSpecies;

    public GenePool(final int speciesCnt, final Function<Genome, Scores> fitnessFunction) {
        species = new Species[speciesCnt];
        for (int i = 0; i < speciesCnt; ++i) {
            species[i] = Species.species(SPECIES_SIZE, fitnessFunction);
        }
        this.fitnessFunction = fitnessFunction;
        bestSpecies = getLazyBestSpecies();
    }

    private GenePool(final Species[] species, final Function<Genome, Scores> fitnessFunction) {
        this.species = species;
        this.fitnessFunction = fitnessFunction;
        bestSpecies = getLazyBestSpecies();
    }

    private LazyValue<Optional<Species>> getLazyBestSpecies() {
        return new LazyValue<Optional<Species>>() {
            @Override
            protected Optional<Species> compute() {
                Optional<Species> bestSpecies = Optional.empty();
                for (final Species oneSpecies : species) {
                    bestSpecies = Optional.of(bestSpecies.filter(new Predicate<Species>() {
                        @Override
                        public boolean test(final Species bestSpecies) {
                            return bestSpecies.getFitness() >= oneSpecies.getFitness();
                        }
                    }).orElse(oneSpecies));
                }
                return bestSpecies;
            }
        };
    }

    public Optional<EvaluatedGenome> getBestGenome() {
        return bestSpecies.get().flatMap(new Function<Species, Optional<EvaluatedGenome>>() {
            @Override
            public Optional<EvaluatedGenome> apply(final Species species) {
                return species.getBestGenome();
            }
        });
    }

    public GenePool getNextGeneration() {
        sortSpecies();

        final Species[] nextGeneration = new Species[species.length];
        int speciesCnt = 0;

        // Keep best species (get next generation)
        final int keepCnt = (int) Math.round((1 - NEW_SPECIES_RATE) * species.length);
        while (speciesCnt < keepCnt) {
            nextGeneration[speciesCnt] = species[speciesCnt].getNextGeneration();
            ++speciesCnt;
        }

        // Create new species
        while (speciesCnt < species.length) {
            nextGeneration[speciesCnt++] = Species.species(SPECIES_SIZE, fitnessFunction);
        }

        return new GenePool(nextGeneration, fitnessFunction);
    }

    private void sortSpecies() {
        if (!sorted) {
            // Sort by fitness (species with higher fitness come first)
            Arrays.sort(species, new Comparator<Species>() {
                @Override
                public int compare(final Species species1, final Species species2) {
                    final double fitnessDiff = species1.getFitness() - species2.getFitness();
                    return fitnessDiff > 0.0 ? -1 : (fitnessDiff < 0.0 ? 1 : 0);
                }
            });
            sorted = true;
        }
    }

    public static Optional<EvaluatedGenome> findBestGenome(final int speciesCnt,
                                                           final int generations,
                                                           final Function<Genome, Scores> fitnessFunction) {
        GenePool genePool = new GenePool(speciesCnt, fitnessFunction);
        for (int i = 2; i <= generations; ++i) {
            genePool = genePool.getNextGeneration();

            // TODO: No printing; call callback instead
            if (i % 100 == 0) {
                final Optional<EvaluatedGenome> bestGenome = genePool.getBestGenome();
                final int generation = i;
                bestGenome.ifPresent(new Consumer<EvaluatedGenome>() {
                    @Override
                    public void accept(EvaluatedGenome bestGenome) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Best genome in generation ");
                        sb.append(generation);
                        sb.append(" (genome length = ");
                        sb.append(bestGenome.getGenome().getGenomeLength());
                        sb.append("): ");
                        sb.append(bestGenome.getFitness());
                        System.out.println(sb);
                    }
                });
            }
        }

        return genePool.getBestGenome();
    }
}
