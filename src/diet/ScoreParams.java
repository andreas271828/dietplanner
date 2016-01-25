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
package diet;

public class ScoreParams {
    private final double lowerCritical;
    private final double lowerOptimal;
    private final double upperOptimal;
    private final double upperCritical;
    private final double weight;

    public static ScoreParams scoreParams(final double lowerCritical,
                                          final double lowerOptimal,
                                          final double upperOptimal,
                                          final double upperCritical,
                                          final double weight) {
        return new ScoreParams(lowerCritical, lowerOptimal, upperOptimal, upperCritical, weight);
    }

    public static ScoreParams scoreParamsL(final double lowerCritical,
                                           final double lowerOptimal,
                                           final double weight) {
        return scoreParams(lowerCritical, lowerOptimal, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, weight);
    }

    public static ScoreParams scoreParamsU(final double upperOptimal,
                                           final double upperCritical,
                                           final double weight) {
        return scoreParams(0.0, 0.0, upperOptimal, upperCritical, weight);
    }

    public static ScoreParams scoreParamsT(final double lowerOptimal,
                                           final double upperOptimal,
                                           final double tolerance,
                                           final double weight) {
        return scoreParams(lowerCritical(lowerOptimal, tolerance), lowerOptimal,
                upperOptimal, upperCritical(upperOptimal, tolerance), weight);
    }

    public static ScoreParams scoreParamsLT(final double lowerOptimal,
                                            final double tolerance,
                                            final double weight) {
        return scoreParams(lowerCritical(lowerOptimal, tolerance), lowerOptimal,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, weight);
    }

    public static ScoreParams scoreParamsUT(final double upperOptimal,
                                            final double tolerance,
                                            final double weight) {
        return scoreParams(0.0, 0.0, upperOptimal, upperCritical(upperOptimal, tolerance), weight);
    }

    private ScoreParams(final double lowerCritical,
                        final double lowerOptimal,
                        final double upperOptimal,
                        final double upperCritical,
                        final double weight) {
        this.lowerCritical = lowerCritical;
        this.lowerOptimal = lowerOptimal;
        this.upperOptimal = upperOptimal;
        this.upperCritical = upperCritical;
        this.weight = weight;
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

    public double getWeight() {
        return weight;
    }

    public static double lowerCritical(final double lowerOptimal, final double tolerance) {
        return (1.0 - tolerance) * lowerOptimal;
    }

    public static double upperCritical(final double upperOptimal, final double tolerance) {
        return (1.0 + tolerance) * upperOptimal;
    }
}
