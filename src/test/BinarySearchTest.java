package test;

import util.BinarySearch;

import java.util.Optional;

public class BinarySearchTest {
    public static void runTests() {
        runFindInRangeTests();
    }

    private static void runFindInRangeTests() {
        Test.test(Optional.of(0), BinarySearch.findInRange(0.5, new double[]{1, 2, 3}));
        Test.test(Optional.of(1), BinarySearch.findInRange(1.5, new double[]{1, 2, 3}));
        Test.test(Optional.of(2), BinarySearch.findInRange(2.5, new double[]{1, 2, 3}));
        Test.test(Optional.empty(), BinarySearch.findInRange(3.5, new double[]{1, 2, 3}));
        Test.test(Optional.of(3), BinarySearch.findInRange(3.5, new double[]{1, 2, 3, 4}));
    }
}
