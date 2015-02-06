package evolution;

import util.LazyValue;
import util.Pair;
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
    private final Function<Genome, Scores> fitnessFunction;
    private final LazyValue<Pair<Optional<Species>, Optional<Species>>> bestWorstSpecies;

    public GenePool(final int speciesCnt, final Function<Genome, Scores> fitnessFunction) {
        species = new Species[speciesCnt];
        for (int i = 0; i < speciesCnt; ++i) {
            species[i] = Species.species(SPECIES_SIZE, fitnessFunction);
        }
        this.fitnessFunction = fitnessFunction;
        bestWorstSpecies = getLazyBestWorstSpecies();
    }

    private GenePool(final Species[] species, final Function<Genome, Scores> fitnessFunction) {
        this.species = species;
        this.fitnessFunction = fitnessFunction;
        bestWorstSpecies = getLazyBestWorstSpecies();
    }

    private LazyValue<Pair<Optional<Species>, Optional<Species>>> getLazyBestWorstSpecies() {
        return new LazyValue<Pair<Optional<Species>, Optional<Species>>>() {
            @Override
            protected Pair<Optional<Species>, Optional<Species>> compute() {
                Optional<Species> bestSpecies = Optional.empty();
                Optional<Species> worstSpecies = Optional.empty();
                for (final Species oneSpecies : species) {
                    bestSpecies = Optional.of(bestSpecies.filter(new Predicate<Species>() {
                        @Override
                        public boolean test(final Species bestSpecies) {
                            return bestSpecies.getFitness() >= oneSpecies.getFitness();
                        }
                    }).orElse(oneSpecies));
                    worstSpecies = Optional.of(worstSpecies.filter(new Predicate<Species>() {
                        @Override
                        public boolean test(final Species worstSpecies) {
                            return worstSpecies.getFitness() <= oneSpecies.getFitness();
                        }
                    }).orElse(oneSpecies));
                }
                return new Pair<Optional<Species>, Optional<Species>>(bestSpecies, worstSpecies);
            }
        };
    }

    public Optional<EvaluatedGenome> getBestGenome() {
        return bestWorstSpecies.get().a().flatMap(new Function<Species, Optional<EvaluatedGenome>>() {
            @Override
            public Optional<EvaluatedGenome> apply(final Species species) {
                return species.getBestGenome();
            }
        });
    }

    public GenePool getNextGeneration() {
        // Sort current generation by fitness (species with higher fitness come first)
        Arrays.sort(species, new Comparator<Species>() {
            @Override
            public int compare(final Species species1, final Species species2) {
                final double fitnessDiff = species1.getFitness() - species2.getFitness();
                return fitnessDiff > 0.0 ? -1 : (fitnessDiff < 0.0 ? 1 : 0);
            }
        });

        final Species[] nextGeneration = new Species[species.length];
        // TODO: Keep best SPECIES_SIZE - 1 species and create 1 new one (or better, use NEW_SPECIES_RATE)
        // TODO: Make sure that this (especially the sorting) is only called once! Sort where set?!

        return new GenePool(nextGeneration, fitnessFunction);
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
