package diet;

public class ScoreParams {
    private final double lowerCritical;
    private final double lowerOptimal;
    private final double upperOptimal;
    private final double upperCritical;
    private final double weight;

    public static ScoreParams scoreParams(final double lowerCritical,
                                          final double lowerOptimal,
                                          final double upperOptimal,
                                          final double upperCritical,
                                          final double weight) {
        return new ScoreParams(lowerCritical, lowerOptimal, upperOptimal, upperCritical, weight);
    }

    public static ScoreParams scoreParamsORC(final double optimal,
                                             final double relCritical,
                                             final double weight) {
        return scoreParams((1 - relCritical) * optimal, optimal, optimal, (1 + relCritical) * optimal, weight);
    }

    public static ScoreParams scoreParamsRORC(final double optimalCentre,
                                              final double relOptimal,
                                              final double relCritical,
                                              final double weight) {
        return scoreParams((1 - relCritical) * optimalCentre, (1 - relOptimal) * optimalCentre,
                (1 + relOptimal) * optimalCentre, (1 + relCritical) * optimalCentre, weight);
    }

    public static ScoreParams scoreParamsLOUORC(final double lowerOptimal,
                                                final double upperOptimal,
                                                final double relCritical,
                                                final double weight) {
        return scoreParams((1 - relCritical) * lowerOptimal, lowerOptimal, upperOptimal,
                (1 + relCritical) * upperOptimal, weight);
    }

    public static ScoreParams scoreParamsUC(final double upperCritical,
                                            final double weight) {
        return scoreParams(0, 0, 0, upperCritical, weight);
    }

    public static ScoreParams scoreParamsLORLC(final double lowerOptimal,
                                               final double relLowerCritical,
                                               final double weight) {
        return scoreParams((1 - relLowerCritical) * lowerOptimal, lowerOptimal, Double.MAX_VALUE, Double.MAX_VALUE, weight);
    }

    public static ScoreParams scoreParamsUORUC(final double upperOptimal,
                                               final double relUpperCritical,
                                               final double weight) {
        return scoreParams(0.0, 0.0, upperOptimal, (1.0 + relUpperCritical) * upperOptimal, weight);
    }

    private ScoreParams(final double lowerCritical,
                        final double lowerOptimal,
                        final double upperOptimal,
                        final double upperCritical,
                        final double weight) {
        this.lowerCritical = lowerCritical;
        this.lowerOptimal = lowerOptimal;
        this.upperOptimal = upperOptimal;
        this.upperCritical = upperCritical;
        this.weight = weight;
    }

    public double getLowerCritical() {
        return lowerCritical;
    }

    public double getLowerOptimal() {
        return lowerOptimal;
    }

    public double getUpperOptimal() {
        return upperOptimal;
    }

    public double getUpperCritical() {
        return upperCritical;
    }

    public double getWeight() {
        return weight;
    }
}
