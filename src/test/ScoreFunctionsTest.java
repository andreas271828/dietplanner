package test;

import diet.ScoreParams;
import diet.ScoreFunctions;

import static diet.ScoreParams.scoreParams;

public class ScoreFunctionsTest {
    public static void runTests() {
        runStandardTests();
    }

    private static void runStandardTests() {
        final double tolerance = 1e-6;
        final double upperBound = 1e6;
        testStandard(0, 0, 1, 2, 3, 4, tolerance);
        testStandard(0, 0, 1, 2, 3, 4, tolerance);
        testStandard(0.02, 0.2, 1, 2, 3, 4, tolerance);
        testStandard(0.1, 1, 1, 2, 3, 4, tolerance);
        testStandard(0.91, 1.9, 1, 2, 3, 4, tolerance);
        testStandard(1, 2, 1, 2, 3, 4, tolerance);
        testStandard(1, 2.3, 1, 2, 3, 4, tolerance);
        testStandard(1, 3, 1, 2, 3, 4, tolerance);
        testStandard(0.73, 3.3, 1, 2, 3, 4, tolerance);
        testStandard(0.1, 4, 1, 2, 3, 4, tolerance);
        Test.test(true, ScoreFunctions.standard(10, scoreParams(1, 2, 3, 4, 1.0)) < 0.1);
        testStandard(1, 2, 1, 2, 2, 3, tolerance);
        testStandard(0.55, 2.5, 1, 2, 2, 3, tolerance);
        testStandard(1, 0, 0, 0, 1, 2, tolerance);
        testStandard(1, 0.3, 0, 0, 1, 2, tolerance);
        testStandard(0.73, 1.3, 0, 0, 1, 2, tolerance);
        testStandard(1, 0, 0, 0, 0, 1, tolerance);
        testStandard(0.73, 0.3, 0, 0, 0, 1, tolerance);
        testStandard(0, 0, 1, 2, Double.MAX_VALUE, Double.MAX_VALUE, tolerance);
        testStandard(0.09, 0.9, 1, 2, Double.MAX_VALUE, Double.MAX_VALUE, tolerance);
        testStandard(0.91, 1.9, 1, 2, Double.MAX_VALUE, Double.MAX_VALUE, tolerance);
        testStandard(1, 2, 1, 2, Double.MAX_VALUE, Double.MAX_VALUE, tolerance);
        testStandard(1, 20, 1, 2, Double.MAX_VALUE, Double.MAX_VALUE, tolerance);
        testStandard(1, Double.MAX_VALUE, 1, 2, Double.MAX_VALUE, Double.MAX_VALUE, tolerance);
    }

    private static void testStandard(double expected,
                                     double value,
                                     double lowerCritical,
                                     double lowerOptimal,
                                     double upperOptimal,
                                     double upperCritical,
                                     double tolerance) {
        final ScoreParams scoreParams = scoreParams(lowerCritical, lowerOptimal, upperOptimal, upperCritical, 1.0);
        final double score = ScoreFunctions.standard(value, scoreParams);
        Test.test(expected, score, tolerance);
    }
}
