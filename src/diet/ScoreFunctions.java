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

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.min;

public abstract class ScoreFunctions {
    private static final double LN_999 = log(999.0); // log(999.0) == -log(0.001 / 0.999)

    public static double standard(final double value, final ScoreParams scoreParams) {
        /*  We use two logistic functions, one to define minimum values and one to define maximum values.
            The final score is the minimum of the result of both functions.
            Lower and upper critical values define the point where the slope is greatest (sigmoid's midpoint).
            A value is regarded as "optimal" if the result of the logistic function is at least 0.999.
         */
        final double score1;
        final double lowerCritical = scoreParams.getLowerCritical();
        final double lowerOptimal = scoreParams.getLowerOptimal();
        if (lowerOptimal > lowerCritical) {
            // Calculate k that is necessary to reach 0.999 at the limit of the optimal range.
            final double k1 = LN_999 / (lowerOptimal - lowerCritical);
            score1 = 1.0 / (1.0 + exp(-k1 * (value - lowerCritical)));
        } else {
            score1 = value < lowerCritical ? 0.0 : 1.0;
        }

        final double score2;
        final double upperCritical = scoreParams.getUpperCritical();
        final double upperOptimal = scoreParams.getUpperOptimal();
        if (upperOptimal < upperCritical) {
            // Calculate k that is necessary to reach 0.999 at the limit of the optimal range.
            final double k2 = LN_999 / (upperCritical - upperOptimal);
            score2 = 1.0 / (1.0 + exp(-k2 * (upperCritical - value)));
        } else {
            score2 = value > upperCritical ? 0.0 : 1.0;
        }

        return min(score1, score2);
    }
}
