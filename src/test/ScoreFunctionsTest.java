package test;

import diet.ScoreFunctions;
import diet.ScoreParams;

import static diet.ScoreParams.scoreParams;

public class ScoreFunctionsTest {
    private static final double TEST_TOLERANCE = 1e-9;
    
    public static void runTests() {
        runStandardTests();
    }

    private static void runStandardTests() {
        // Sharp limits
        testStandard(1.0, 0.0, 0.0, 0.0, 5.0, 5.0);
        testStandard(1.0, 0.1, 0.0, 0.0, 5.0, 5.0);
        testStandard(1.0, 4.9, 0.0, 0.0, 5.0, 5.0);
        testStandard(1.0, 5.0, 0.0, 0.0, 5.0, 5.0);
        testStandard(0.0, 5.1, 0.0, 0.0, 5.0, 5.0);
        testStandard(0.0, 0.9, 1.0, 1.0, 5.0, 5.0);
        testStandard(1.0, 1.0, 1.0, 1.0, 5.0, 5.0);

        // No lower limit (optimal range starts at 0.0)
        testStandard(1.0, 0.0, 0.0, 0.0, 4.0, 5.0);
        testStandard(1.0, 1.0, 0.0, 0.0, 4.0, 5.0);
        testStandard(0.999999999, 2.0, 0.0, 0.0, 4.0, 5.0);
        testStandard(0.999998998, 3.0, 0.0, 0.0, 4.0, 5.0);
        testStandard(0.999, 4.0, 0.0, 0.0, 4.0, 5.0);
        testStandard(0.5, 5.0, 0.0, 0.0, 4.0, 5.0);
        testStandard(0.001, 6.0, 0.0, 0.0, 4.0, 5.0);
        testStandard(0.000001002, 7.0, 0.0, 0.0, 4.0, 5.0);

        // No upper limit (optimal range ends at infinity)
        testStandard(0.001, 0.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        testStandard(0.5, 1.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        testStandard(0.999, 2.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        testStandard(0.999998998, 3.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        testStandard(0.999999999, 4.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        testStandard(1.0, 5.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        testStandard(1.0, 6.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        // Smooth lower and upper limits
        testStandard(0.001, 0.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.5, 1.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.999, 2.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.999998998, 3.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.999, 4.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.5, 5.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.001, 6.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.000001002, 7.0, 1.0, 2.0, 4.0, 5.0);
        testStandard(0.999, 2.0, 1.0, 2.0, 2.0, 3.0);
        testStandard(0.5, 1.0, 1.0, 2.0, 2.0, 3.0);
        testStandard(0.5, 3.0, 1.0, 2.0, 2.0, 3.0);
    }

    private static void testStandard(final double expected,
                                     final double value,
                                     final double lowerCritical,
                                     final double lowerOptimal,
                                     final double upperOptimal,
                                     final double upperCritical) {
        final double score = computeScore(value, lowerCritical, lowerOptimal, upperOptimal, upperCritical);
        Test.test(expected, score, TEST_TOLERANCE);
    }

    private static double computeScore(final double value,
                                       final double lowerCritical,
                                       final double lowerOptimal,
                                       final double upperOptimal,
                                       final double upperCritical) {
        final ScoreParams scoreParams = scoreParams(lowerCritical, lowerOptimal, upperOptimal, upperCritical, 1.0);
        return ScoreFunctions.standard(value, scoreParams);
    }
}
