package util;

import util.Pair;

import java.util.ArrayList;

public class Scores {
    private ArrayList<Pair<String, Double>> scores = new ArrayList<Pair<String, Double>>();
    private double totalScore = 0;

    public void addScore(final String name, final double score) {
        scores.add(new Pair<String, Double>(name, score));
        totalScore += score;
    }

    public double getTotalScore() {
        return totalScore;
    }

    @Override
    public String toString() {
        return "<" + scores.toString() + ", " + totalScore + ">";
    }
}
