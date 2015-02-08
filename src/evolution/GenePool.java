package evolution;

import util.LazyValue;
import util.Scores;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenePool {
    private static final int SPECIES_SIZE = 100;
    private static final Random RANDOM = new Random();

    private final Species[] species;
    private final Function<Genome, Scores> fitnessFunction;
    private final LazyValue<Optional<Species>> bestSpecies;

    public GenePool(final int speciesCnt, final Function<Genome, Scores> fitnessFunction) {
        if (speciesCnt < 1) {
            throw new IllegalArgumentException("speciesCnt must be greater than 0.");
        }

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
        final Species[] nextGeneration = new Species[species.length];
        int speciesCnt = 0;

        // Some species make it to the next generation, others die out.
        // The probability to survive depends on fitness and age.
        double bestFitness = 0.0;
        double worstFitness = 0.0;
        int youngestAge = 0;
        int oldestAge = 0;
        for (int i = 0; i < species.length; ++i) {
            final double speciesFitness = species[i].getFitness();
            final int speciesAge = species[i].getAge();
            if (i == 0) {
                bestFitness = speciesFitness;
                worstFitness = speciesFitness;
                youngestAge = speciesAge;
                oldestAge = speciesAge;
            } else {
                if (speciesFitness > bestFitness) {
                    bestFitness = speciesFitness;
                } else if (speciesFitness < worstFitness) {
                    worstFitness = speciesFitness;
                }
                if (speciesAge < youngestAge) {
                    youngestAge = speciesAge;
                } else if (speciesAge > oldestAge) {
                    oldestAge = speciesAge;
                }
            }
        }
        final double fitnessRange = bestFitness - worstFitness;
        final int ageRange = oldestAge - youngestAge;
        for (final Species oneSpecies : species) {
            final double fitnessBonus = fitnessRange > 0.0 ? (oneSpecies.getFitness() - worstFitness) / fitnessRange : 1.0;
            final double ageBonus = ageRange > 0 ? (double) (oldestAge - oneSpecies.getAge()) / ageRange : 0.0;
            if (RANDOM.nextDouble() < fitnessBonus + ageBonus) {
                nextGeneration[speciesCnt++] = oneSpecies.getNextGeneration();
            }
        }

        // Create new species
        while (speciesCnt < species.length) {
            nextGeneration[speciesCnt++] = Species.species(SPECIES_SIZE, fitnessFunction);
        }

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
