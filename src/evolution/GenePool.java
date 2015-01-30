package evolution;

import util.BinarySearch;
import util.LazyValue;
import util.Scores;

import java.util.Random;
import java.util.function.Function;

public class GenePool {
    private final static Random RANDOM = new Random();

    private final EvaluatedGenome[] evaluatedGenomes;
    private final Function<Genome, Scores> fitnessFunction;
    private final LazyValue<EvaluatedGenome> bestGenome;
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

    private LazyValue<EvaluatedGenome> getLazyBestGenome() {
        return new LazyValue<EvaluatedGenome>() {
            @Override
            protected EvaluatedGenome compute() {
                EvaluatedGenome bestGenome = null;
                for (final EvaluatedGenome evaluatedGenome : evaluatedGenomes) {
                    if (bestGenome == null || evaluatedGenome.getFitness() > bestGenome.getFitness()) {
                        bestGenome = evaluatedGenome;
                    }
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

    public EvaluatedGenome getBestGenome() {
        return bestGenome.get();
    }

    public double[] getQualityAccumulation() {
        return qualityAccumulation.get();
    }

    public GenePool getNextGeneration() {
        final EvaluatedGenome[] nextGeneration = new EvaluatedGenome[evaluatedGenomes.length];
        int genomeCnt = 0;

        // Keep best genome
        final EvaluatedGenome bestGenome = getBestGenome();
        nextGeneration[genomeCnt++] = new EvaluatedGenome(bestGenome.getGenome(), fitnessFunction,
                bestGenome.getFitness());

        // Recombine genomes
        while (genomeCnt < nextGeneration.length) {
            final EvaluatedGenome parent1 = selectRandomGenome();
            final EvaluatedGenome parent2 = selectRandomGenome();
            // final double fitnessBase = Math.max(parent1.getFitness(), parent2.getFitness());
            final double fitnessBase = (parent1.getFitness() + parent2.getFitness()) / 2;
            final int offspringCnt = genomeCnt + 1 < nextGeneration.length ? 2 : 1;
            final Genome[] offspring = Genome.recombine(parent1.getGenome(), parent2.getGenome(), offspringCnt);
            for (int i = 0; i < offspringCnt; ++i) {
                nextGeneration[genomeCnt++] = new EvaluatedGenome(offspring[i], fitnessFunction, fitnessBase);
            }
        }

        return new GenePool(nextGeneration, fitnessFunction);
    }

    public static EvaluatedGenome findBestGenome(final int genePoolSize,
                                                 final int generations,
                                                 final Function<Genome, Scores> fitnessFunction) {
        GenePool genePool = new GenePool(genePoolSize, fitnessFunction);
        for (int i = 2; i <= generations; ++i) {
            genePool = genePool.getNextGeneration();

            // TODO: No printing; call callback instead
            if (i % 100 == 0) {
                final EvaluatedGenome bestGenome = genePool.getBestGenome();
                if (bestGenome != null) {
                    System.out.println("Best genome in generation " + i + " (genome length = " + bestGenome.getGenome().getGenomeLength() + "): " + bestGenome.getFitness());
                }
            }
        }

        return genePool.getBestGenome();
    }

    private EvaluatedGenome selectRandomGenome() {
        if (evaluatedGenomes.length > 0) {
            final double[] qualityAccumulation = getQualityAccumulation();
            final double selector = RANDOM.nextDouble() * qualityAccumulation[evaluatedGenomes.length - 1];
            final int index = BinarySearch.findInRange(selector, qualityAccumulation);
            return index == BinarySearch.NOT_FOUND ? null : evaluatedGenomes[index];
        }

        return null;
    }
}
