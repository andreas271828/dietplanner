package evolution;

import util.Evaluation;
import util.Evaluations;
import util.Scores;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import static evolution.Genome.genome;
import static util.Evaluation.evaluation;

public class Species {
    private static final int MAX_GENOME_LENGTH = 10000;
    private static final Random RANDOM = new Random();

    private final int age;
    private final Evaluations<Genome> evaluations;
    private final Function<Genome, Scores> fitnessFunction;

    public static Species species(final int size, final Function<Genome, Scores> fitnessFunction) {
        return new Species(size, fitnessFunction);
    }

    private Species(final int size, final Function<Genome, Scores> fitnessFunction) {
        age = 0;

        final ArrayList<Evaluation<Genome>> evaluatedGenomes = new ArrayList<Evaluation<Genome>>(size);
        final int genomeLength = getRandomGenomeLength();
        for (int i = 0; i < size; ++i) {
            evaluatedGenomes.add(evaluation(genome(genomeLength), fitnessFunction));
        }
        evaluations = Evaluations.evaluations(evaluatedGenomes);

        this.fitnessFunction = fitnessFunction;
    }

    private Species(final int age,
                    final ArrayList<Evaluation<Genome>> evaluatedGenomes,
                    final Function<Genome, Scores> fitnessFunction) {
        this.age = age;
        evaluations = Evaluations.evaluations(evaluatedGenomes);
        this.fitnessFunction = fitnessFunction;
    }

    public int getAge() {
        return age;
    }

    private int getRandomGenomeLength() {
        final double random = RANDOM.nextDouble();
        return (int) (random * random * MAX_GENOME_LENGTH) + 1;
    }

    public Optional<Evaluation<Genome>> getBestGenome() {
        return evaluations.getBest();
    }

    public double getFitness() {
        return getBestGenome().map(new Function<Evaluation<Genome>, Double>() {
            @Override
            public Double apply(final Evaluation<Genome> bestGenome) {
                return bestGenome.getTotalScore();
            }
        }).orElse(0.0);
    }

    public Species getNextGeneration() {
        final int nextGenerationSize = evaluations.getEvaluationsCount();
        final ArrayList<Evaluation<Genome>> nextGeneration = new ArrayList<Evaluation<Genome>>(nextGenerationSize);

        // Keep best genome
        getBestGenome().ifPresent(new Consumer<Evaluation<Genome>>() {
            @Override
            public void accept(final Evaluation<Genome> evaluatedGenome) {
                nextGeneration.add(evaluatedGenome);
            }
        });

        // Recombine genomes
        while (nextGeneration.size() < nextGenerationSize) {
            final Optional<Genome> parent1 = evaluations.selectProbabilistically();
            final Optional<Genome> parent2 = evaluations.selectProbabilistically();
            final int offspringCnt = nextGeneration.size() + 1 < nextGenerationSize ? 2 : 1;
            final Genome[] offspring = Genome.recombine(parent1, parent2, offspringCnt);
            for (final Genome genome : offspring) {
                nextGeneration.add(evaluation(genome, fitnessFunction));
            }
        }

        return new Species(age + 1, nextGeneration, fitnessFunction);
    }
}
