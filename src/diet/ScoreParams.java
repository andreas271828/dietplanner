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

    public static ScoreParams scoreParamsL(final double lowerCritical,
                                           final double lowerOptimal,
                                           final double weight) {
        return scoreParams(lowerCritical, lowerOptimal, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, weight);
    }

    public static ScoreParams scoreParamsU(final double upperOptimal,
                                           final double upperCritical,
                                           final double weight) {
        return scoreParams(0.0, 0.0, upperOptimal, upperCritical, weight);
    }

    public static ScoreParams scoreParamsT(final double lowerOptimal,
                                           final double upperOptimal,
                                           final double tolerance,
                                           final double weight) {
        return scoreParams(lowerCritical(lowerOptimal, tolerance), lowerOptimal,
                upperOptimal, upperCritical(upperOptimal, tolerance), weight);
    }

    public static ScoreParams scoreParamsLT(final double lowerOptimal,
                                            final double tolerance,
                                            final double weight) {
        return scoreParams(lowerCritical(lowerOptimal, tolerance), lowerOptimal,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, weight);
    }

    public static ScoreParams scoreParamsUT(final double upperOptimal,
                                            final double tolerance,
                                            final double weight) {
        return scoreParams(0.0, 0.0, upperOptimal, upperCritical(upperOptimal, tolerance), weight);
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

    public static double lowerCritical(final double lowerOptimal, final double tolerance) {
        return (1.0 - tolerance) * lowerOptimal;
    }

    public static double upperCritical(final double upperOptimal, final double tolerance) {
        return (1.0 + tolerance) * upperOptimal;
    }
}
