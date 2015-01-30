package util;

public class BinarySearch {
    public final static int NOT_FOUND = -1;

    public static int findInRange(double selector, double[] ranges) {
        int lowerBound = 0;
        int upperBound = ranges.length - 1;
        while (lowerBound <= upperBound) {
            final int midpoint = (lowerBound + upperBound) / 2;
            final double start = midpoint > 0 ? ranges[midpoint - 1] : 0;
            final double end = ranges[midpoint];
            if (selector >= start && selector < end) {
                return midpoint;
            } else if (selector < start) {
                upperBound = midpoint - 1;
            } else {
                lowerBound = midpoint + 1;
            }
        }
        return NOT_FOUND;
    }
}
