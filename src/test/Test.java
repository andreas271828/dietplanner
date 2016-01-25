/**********************************************************************
 DietPlanner

 Copyright (C) 2015-2016 Andreas Huemer

 This file is part of DietPlanner.

 DietPlanner is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at
 your option) any later version.

 DietPlanner is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/
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
        ScoreFunctionsTest.runTests();
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
