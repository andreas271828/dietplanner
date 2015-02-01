package util;

public class Limits4 {
    private final double lowerCritical;
    private final double lowerOptimal;
    private final double upperOptimal;
    private final double upperCritical;

    public static Limits4 limits4(final double lowerCritical,
                                  final double lowerOptimal,
                                  final double upperOptimal,
                                  final double upperCritical) {
        return new Limits4(lowerCritical, lowerOptimal, upperOptimal, upperCritical);
    }

    public static Limits4 limits4ORC(final double optimal, final double relCritical) {
        return new Limits4((1 - relCritical) * optimal, optimal, optimal, (1 + relCritical) * optimal);
    }

    public static Limits4 limits4RORC(final double optimalCentre,
                                      final double relOptimal,
                                      final double relCritical) {
        return new Limits4((1 - relCritical) * optimalCentre, (1 - relOptimal) * optimalCentre,
                (1 + relOptimal) * optimalCentre, (1 + relCritical) * optimalCentre);
    }

    public static Limits4 limits4LOUORC(final double lowerOptimal,
                                        final double upperOptimal,
                                        final double relCritical) {
        return new Limits4((1 - relCritical) * lowerOptimal, lowerOptimal, upperOptimal, (1 + relCritical) * upperOptimal);
    }

    public static Limits4 limits4UC(final double upperCritical) {
        return new Limits4(0, 0, 0, upperCritical);
    }

    private Limits4(final double lowerCritical,
                    final double lowerOptimal,
                    final double upperOptimal,
                    final double upperCritical) {
        this.lowerCritical = lowerCritical;
        this.lowerOptimal = lowerOptimal;
        this.upperOptimal = upperOptimal;
        this.upperCritical = upperCritical;
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
}
