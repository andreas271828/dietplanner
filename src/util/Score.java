package util;

public class Score {
    private final double score;
    private final double weight;
    private final String label;

    public static Score score(final double score, final double weight, final String label) {
        return new Score(score, weight, label);
    }

    private Score(final double score, final double weight, final String label) {
        this.score = score;
        this.weight = weight;
        this.label = label;
    }

    public double getWeightedScore() {
        return score * weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "<" + label + ": " + getWeightedScore() + " / " + weight + ">";
    }
}
