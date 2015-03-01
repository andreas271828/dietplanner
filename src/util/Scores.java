package util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static util.Pair.pair;
import static util.Score.score;

public class Scores {
    private ArrayList<Score> scores = new ArrayList<Score>();
    private double totalScore = 0.0;

    public void addScore(final double score, final double weight, final String label) {
        final Score scoreDetails = score(score, weight, label);
        scores.add(scoreDetails);
        totalScore += scoreDetails.getWeightedScore();
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getWeightSum() {
        double weightSum = 0.0;
        for (final Score score : scores) {
            weightSum += score.getWeight();
        }
        return weightSum;
    }

    public List<Pair<Score, Double>> getRelativeScores() {
        final List<Pair<Score, Double>> relScores = new ArrayList<Pair<Score, Double>>();
        for (final Score score : scores) {
            relScores.add(pair(score, score.getScore()));
        }
        relScores.sort(new Comparator<Pair<Score, Double>>() {
            @Override
            public int compare(final Pair<Score, Double> score1, final Pair<Score, Double> score2) {
                return score1.b().compareTo(score2.b());
            }
        });
        return relScores;
    }

    @Override
    public String toString() {
        return "<" + scores.toString() + ", " + totalScore + ">";
    }
}
