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
package util;

import diet.Requirement;
import diet.Scores;

import java.util.Optional;
import java.util.function.Function;

public class Evaluation<T> {
    private final T object;
    private final LazyValue<Scores> scores;

    public static <T> Evaluation<T> evaluation(final T object, final Function<T, Scores> evaluationFunction) {
        return new Evaluation<T>(object, evaluationFunction);
    }

    public static <T> Evaluation<T> evaluation(final T object, final Scores scores) {
        return new Evaluation<T>(object, scores);
    }

    private Evaluation(final T object, final Function<T, Scores> evaluationFunction) {
        this.object = object;
        scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return evaluationFunction.apply(object);
            }
        };
    }

    private Evaluation(final T object, final Scores scores) {
        this.object = object;
        this.scores = new LazyValue<Scores>() {
            @Override
            protected Scores compute() {
                return scores;
            }
        };
    }

    public T getObject() {
        return object;
    }

    public Scores getScores() {
        return scores.get();
    }

    public double getScore(final Optional<Pair<Requirement, Integer>> maybeScoreId) {
        return getScores().getScore(maybeScoreId);
    }

    public double getTotalScore() {
        return getScores().getTotalScore();
    }
}
