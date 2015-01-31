package test;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
//        System.out.println("You have 10 seconds to start the profiler.");
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("Tests start.");

        (new Thread(new Runnable() {
            @Override
            public void run() {
                final long startTime = System.nanoTime();
                runTests();
                final long endTime = System.nanoTime();
                System.out.println();
                System.out.println("Tests finished. Time elapsed: " + ((endTime - startTime) / 1e9) + " sec");
            }
        })).start();
    }

    private static void runTests() {
        // BinarySearchTest.runTests();
        // ScoreFunctionsTest.runTests();

        GenePoolTest.runTests();
        // GenomeTest.runTests();
    }

    public static void test(final double expected, final double actual, final double tolerance) {
        if (Math.abs(expected - actual) > tolerance) {
            System.out.println("Failed test: " + actual + " != " + expected);
        }
    }

    public static void test(final Object expected, final Object actual) {
        if (!expected.equals(actual)) {
            System.out.println("Failed test: " + actual + " != " + expected);
        }
    }

    public static void testArray(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            System.out.println("Failed test: " + Arrays.toString(actual) + " != " + Arrays.toString(expected));
        }
    }
}
