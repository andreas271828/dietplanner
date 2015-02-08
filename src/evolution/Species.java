package evolution;

import util.BinarySearch;
import util.LazyValue;
import util.Scores;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import static evolution.Genome.genome;

public class Species {
    private static final int MAX_GENOME_LENGTH = 10000;
    private static final Random RANDOM = new Random();

    private final int age;
    private final EvaluatedGenome[] evaluatedGenomes;
    private final Function<Genome, Scores> fitnessFunction;
    private final LazyValue<Optional<EvaluatedGenome>> bestGenome;
    private final LazyValue<double[]> fitnessAccumulation;

    public static Species species(final int size, final Function<Genome, Scores> fitnessFunction) {
        return new Species(size, fitnessFunction);
    }

    private Species(final int size, final Function<Genome, Scores> fitnessFunction) {
        age = 0;
        evaluatedGenomes = new EvaluatedGenome[size];
        final int genomeLength = getRandomGenomeLength();
        for (int i = 0; i < size; ++i) {
            evaluatedGenomes[i] = new EvaluatedGenome(genome(genomeLength), fitnessFunction);
        }
        this.fitnessFunction = fitnessFunction;
        bestGenome = getLazyBestGenome();
        fitnessAccumulation = getLazyFitnessAccumulation();
    }

    private Species(final int age, final EvaluatedGenome[] evaluatedGenomes, final Function<Genome, Scores> fitnessFunction) {
        this.age = age;
        this.evaluatedGenomes = evaluatedGenomes;
        this.fitnessFunction = fitnessFunction;
        bestGenome = getLazyBestGenome();
        fitnessAccumulation = getLazyFitnessAccumulation();
    }

    public int getAge() {
        return age;
    }

    private int getRandomGenomeLength() {
        final double random = RANDOM.nextDouble();
        return (int) (random * random * MAX_GENOME_LENGTH) + 1;
    }

    private LazyValue<Optional<EvaluatedGenome>> getLazyBestGenome() {
        return new LazyValue<Optional<EvaluatedGenome>>() {
            @Override
            protected Optional<EvaluatedGenome> compute() {
                Optional<EvaluatedGenome> bestGenome = Optional.empty();
                for (final EvaluatedGenome evaluatedGenome : evaluatedGenomes) {
                    bestGenome = Optional.of(bestGenome.filter(new Predicate<EvaluatedGenome>() {
                        @Override
                        public boolean test(final EvaluatedGenome bestGenome) {
                            return bestGenome.getFitness() >= evaluatedGenome.getFitness();
                        }
                    }).orElse(evaluatedGenome));
                }
                return bestGenome;
            }
        };
    }

    private LazyValue<double[]> getLazyFitnessAccumulation() {
        return new LazyValue<double[]>() {
            @Override
            protected double[] compute() {
                final double[] fitnessAccumulation = new double[evaluatedGenomes.length];
                for (int i = 0; i < evaluatedGenomes.length; ++i) {
                    fitnessAccumulation[i] = evaluatedGenomes[i].getFitness() + (i > 0 ? fitnessAccumulation[i - 1] : 0);
                }
                return fitnessAccumulation;
            }
        };
    }

    public Optional<EvaluatedGenome> getBestGenome() {
        return bestGenome.get();
    }

    public double getFitness() {
        return getBestGenome().map(new Function<EvaluatedGenome, Double>() {
            @Override
            public Double apply(final EvaluatedGenome bestGenome) {
                return bestGenome.getFitness();
            }
        }).orElse(0.0);
    }

    public Species getNextGeneration() {
        final EvaluatedGenome[] nextGeneration = new EvaluatedGenome[evaluatedGenomes.length];
        int genomeCnt = 0;

        // Keep best genome
        genomeCnt += getBestGenome().map(new Function<EvaluatedGenome, Integer>() {
            @Override
            public Integer apply(final EvaluatedGenome bestGenome) {
                nextGeneration[0] = new EvaluatedGenome(bestGenome.getGenome(), fitnessFunction);
                return 1;
            }
        }).orElse(0);

        // Recombine genomes
        while (genomeCnt < nextGeneration.length) {
            final Optional<EvaluatedGenome> parent1 = selectRandomGenome();
            final Optional<EvaluatedGenome> parent2 = selectRandomGenome();
            final int offspringCnt = genomeCnt + 1 < nextGeneration.length ? 2 : 1;
            final Genome[] offspring = Genome.recombine(getGenome(parent1), getGenome(parent2), offspringCnt);
            for (int i = 0; i < offspringCnt; ++i) {
                nextGeneration[genomeCnt++] = new EvaluatedGenome(offspring[i], fitnessFunction);
            }
        }

        return new Species(age + 1, nextGeneration, fitnessFunction);
    }

    private static Optional<Genome> getGenome(final Optional<EvaluatedGenome> evaluatedGenome) {
        return evaluatedGenome.map(new Function<EvaluatedGenome, Optional<Genome>>() {
            @Override
            public Optional<Genome> apply(final EvaluatedGenome evaluatedGenome) {
                return Optional.of(evaluatedGenome.getGenome());
            }
        }).orElse(Optional.<Genome>empty());
    }

    private Optional<EvaluatedGenome> selectRandomGenome() {
        if (evaluatedGenomes.length > 0) {
            final double[] fAcc = fitnessAccumulation.get();
            final double selector = RANDOM.nextDouble() * fAcc[evaluatedGenomes.length - 1];
            return BinarySearch.findInRange(selector, fAcc).map(new Function<Integer, EvaluatedGenome>() {
                @Override
                public EvaluatedGenome apply(final Integer index) {
                    return evaluatedGenomes[index];
                }
            });
        }

        return Optional.empty();
    }
}
