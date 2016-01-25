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

public class Score {
    private final double score;
    private final double weight;

    public static Score score(final double score, final double weight) {
        return new Score(score, weight);
    }

    private Score(final double score, final double weight) {
        this.score = score;
        this.weight = weight;
    }

    public double getScore() {
        return score;
    }

    public double getWeight() {
        return weight;
    }

    public double getWeightedScore() {
        return score * weight;
    }

    public double getDiffFromWeight() {
        return getWeight() - getWeightedScore();
    }

    @Override
    public String toString() {
        return "<" + getWeightedScore() + " / " + weight + ">";
    }
}
