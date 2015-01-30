package test;

import util.BinarySearch;

public class BinarySearchTest {
    public static void runTests() {
        runFindInRangeTests();
    }

    private static void runFindInRangeTests() {
        Test.test(0, BinarySearch.findInRange(0.5, new double[]{1, 2, 3}));
        Test.test(1, BinarySearch.findInRange(1.5, new double[]{1, 2, 3}));
        Test.test(2, BinarySearch.findInRange(2.5, new double[]{1, 2, 3}));
        Test.test(BinarySearch.NOT_FOUND, BinarySearch.findInRange(3.5, new double[]{1, 2, 3}));
        Test.test(3, BinarySearch.findInRange(3.5, new double[]{1, 2, 3, 4}));
    }
}
