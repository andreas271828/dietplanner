package util;

public abstract class ScoreFunctions {
    public static double standard(final double value, final Limits4 limits, final double upperBound) {
        final double criticalScore = 0.1;
        final double lowerOptimal = limits.getLowerOptimal();
        final double upperOptimal = limits.getUpperOptimal();
        if (value < lowerOptimal) {
            final double lowerCritical = limits.getLowerCritical();
            if (value < lowerCritical) {
                return criticalScore * value / lowerCritical;
            } else {
                return criticalScore + (1 - criticalScore) * (value - lowerCritical) / (lowerOptimal - lowerCritical);
            }
        } else if (value > upperOptimal) {
            final double upperCritical = limits.getUpperCritical();
            if (value > upperCritical) {
                return value >= upperBound ? 0 : criticalScore * (upperBound - value) / (upperBound - upperCritical);
            } else {
                return criticalScore + (1 - criticalScore) * (upperCritical - value) / (upperCritical - upperOptimal);
            }
        } else {
            return 1;
        }
    }
}
