package util;

public class Limits2 {
    private final double min;
    private final double max;

    public static Limits2 limits2(double min, double max) {
        return new Limits2(min, max);
    }

    private Limits2(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}