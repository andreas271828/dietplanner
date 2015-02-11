package util;

import java.util.ArrayList;

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

    @Override
    public String toString() {
        return "<" + scores.toString() + ", " + totalScore + ">";
    }
}
