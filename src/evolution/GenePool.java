package evolution;

import util.BinarySearch;
import util.LazyValue;
import util.Scores;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenePool {
    private final static Random RANDOM = new Random();

    private final EvaluatedGenome[] evaluatedGenomes;
    private final Function<Genome, Scores> fitnessFunction;
    private final LazyValue<Optional<EvaluatedGenome>> bestGenome;
    private final LazyValue<double[]> qualityAccumulation;

    public GenePool(final int genePoolSize, final Function<Genome, Scores> fitnessFunction) {
        evaluatedGenomes = new EvaluatedGenome[genePoolSize];
        for (int i = 0; i < genePoolSize; ++i) {
            evaluatedGenomes[i] = new EvaluatedGenome(new Genome(), fitnessFunction, 0);
        }
        this.fitnessFunction = fitnessFunction;
        bestGenome = getLazyBestGenome();
        qualityAccumulation = getLazyQualityAccumulation();
    }

    private GenePool(final EvaluatedGenome[] evaluatedGenomes, final Function<Genome, Scores> fitnessFunction) {
        this.evaluatedGenomes = evaluatedGenomes;
        this.fitnessFunction = fitnessFunction;
        bestGenome = getLazyBestGenome();
        qualityAccumulation = getLazyQualityAccumulation();
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

    private LazyValue<double[]> getLazyQualityAccumulation() {
        return new LazyValue<double[]>() {
            @Override
            protected double[] compute() {
                final double[] qualityAccumulation = new double[evaluatedGenomes.length];
                for (int i = 0; i < evaluatedGenomes.length; ++i) {
                    qualityAccumulation[i] = evaluatedGenomes[i].getQuality() + (i > 0 ? qualityAccumulation[i - 1] : 0);
                }
                return qualityAccumulation;
            }
        };
    }

    public Optional<EvaluatedGenome> getBestGenome() {
        return bestGenome.get();
    }

    public double[] getQualityAccumulation() {
        return qualityAccumulation.get();
    }

    public GenePool getNextGeneration() {
        final EvaluatedGenome[] nextGeneration = new EvaluatedGenome[evaluatedGenomes.length];
        int genomeCnt = 0;

        // Keep best genome
        genomeCnt += getBestGenome().map(new Function<EvaluatedGenome, Integer>() {
            @Override
            public Integer apply(final EvaluatedGenome bestGenome) {
                nextGeneration[0] = new EvaluatedGenome(bestGenome.getGenome(), fitnessFunction, bestGenome.getFitness());
                return 1;
            }
        }).orElse(0);

        // Recombine genomes
        while (genomeCnt < nextGeneration.length) {
            final Optional<EvaluatedGenome> parent1 = selectRandomGenome();
            final Optional<EvaluatedGenome> parent2 = selectRandomGenome();
            final double fitnessParent1 = getFitness(parent1);
            final double fitnessParent2 = getFitness(parent2);
            // final double fitnessBase = Math.max(fitnessParent1, fitnessParent2);
            final double fitnessBase = (fitnessParent1 + fitnessParent2) / 2;
            final int offspringCnt = genomeCnt + 1 < nextGeneration.length ? 2 : 1;
            final Genome[] offspring = Genome.recombine(getGenome(parent1), getGenome(parent2), offspringCnt);
            for (int i = 0; i < offspringCnt; ++i) {
                nextGeneration[genomeCnt++] = new EvaluatedGenome(offspring[i], fitnessFunction, fitnessBase);
            }
        }

        return new GenePool(nextGeneration, fitnessFunction);
    }

    private static Double getFitness(final Optional<EvaluatedGenome> evaluatedGenome) {
        return evaluatedGenome.map(new Function<EvaluatedGenome, Double>() {
            @Override
            public Double apply(final EvaluatedGenome evaluatedGenome) {
                return evaluatedGenome.getFitness();
            }
        }).orElse(0.0);
    }

    private static Optional<Genome> getGenome(final Optional<EvaluatedGenome> evaluatedGenome) {
        return evaluatedGenome.map(new Function<EvaluatedGenome, Optional<Genome>>() {
            @Override
            public Optional<Genome> apply(final EvaluatedGenome evaluatedGenome) {
                return Optional.of(evaluatedGenome.getGenome());
            }
        }).orElse(Optional.<Genome>empty());
    }

    public static Optional<EvaluatedGenome> findBestGenome(final int genePoolSize,
                                                           final int generations,
                                                           final Function<Genome, Scores> fitnessFunction) {
        GenePool genePool = new GenePool(genePoolSize, fitnessFunction);
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

    private Optional<EvaluatedGenome> selectRandomGenome() {
        if (evaluatedGenomes.length > 0) {
            final double[] qualityAccumulation = getQualityAccumulation();
            final double selector = RANDOM.nextDouble() * qualityAccumulation[evaluatedGenomes.length - 1];
            return BinarySearch.findInRange(selector, qualityAccumulation).map(new Function<Integer, EvaluatedGenome>() {
                @Override
                public EvaluatedGenome apply(final Integer index) {
                    return evaluatedGenomes[index];
                }
            });
        }

        return Optional.empty();
    }
}
