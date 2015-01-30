package evolution;

import util.LazyValue;
import util.Scores;

import java.util.function.Function;

public class EvaluatedGenome {
    private final Genome genome;
    private final LazyValue<Scores> scores;
    private final LazyValue<Double> bonus;

    public EvaluatedGenome(final Genome genome,
                           final Function<Genome, Scores> fitnessFunction,
                           final double fitnessBase) {
        this.genome = genome;

        scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return fitnessFunction.apply(genome);
            }
        };

        this.bonus = new LazyValue<Double>() {
            @Override
            protected Double compute() {
                return getFitness() - fitnessBase;
            }
        };
    }

    public Genome getGenome() {
        return genome;
    }

    public Scores getScores() {
        return scores.get();
    }

    public double getFitness() {
        return getScores().getTotalScore();
    }

    public double getBonus() {
        return bonus.get();
    }

    public double getQuality() {
        final double quality = getFitness() + getBonus();
        return quality > 0 ? quality : 0;
    }
}
