package diet;

public class Score {
    private final double score;
    private final double weight;

    public static Score score(final double score, final double weight) {
        return new Score(score, weight);
    }

    private Score(final double score, final double weight) {
        this.score = score;
        this.weight = weight;
    }

    public double getScore() {
        return score;
    }

    public double getWeightedScore() {
        return score * weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "<" + getWeightedScore() + " / " + weight + ">";
    }
}
