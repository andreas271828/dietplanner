package evolution;

import util.LazyValue;
import util.Scores;

import java.util.function.Function;

public class EvaluatedGenome {
    private final Genome genome;
    private final LazyValue<Scores> scores;

    public EvaluatedGenome(final Genome genome,
                           final Function<Genome, Scores> fitnessFunction) {
        this.genome = genome;
        scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return fitnessFunction.apply(genome);
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
}
