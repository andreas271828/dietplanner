package diet;

import util.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static diet.Score.score;
import static util.Pair.pair;

public class Scores {
    private final Map<Requirement, ArrayList<Score>> scores = new HashMap<Requirement, ArrayList<Score>>();
    private double totalScore = 0.0;
    private double weightSum = 0.0;

    public void addScore(final Requirement requirement,
                         final double score,
                         final double weight) {
        final Score scoreDetails = score(score, weight);
        ArrayList<Score> requirementScores = scores.get(requirement);
        if (requirementScores == null) {
            requirementScores = new ArrayList<Score>();
            scores.put(requirement, requirementScores);
        }
        requirementScores.add(scoreDetails);
        totalScore += scoreDetails.getWeightedScore();
        weightSum += scoreDetails.getWeight();
    }

    public void addStandardScore(final Requirement requirement,
                                 final double value,
                                 final Requirements requirements) {
        requirements.getParams(requirement).ifPresent(new Consumer<ScoreParams>() {
            @Override
            public void accept(final ScoreParams scoreParams) {
                final double score = ScoreFunctions.standard(value, scoreParams, 1000 * scoreParams.getUpperCritical());
                addScore(requirement, score, scoreParams.getWeight());
            }
        });
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getWeightSum() {
        return weightSum;
    }

    public List<Pair<Pair<Requirement, Integer>, Double>> getRelativeScores() {
        final List<Pair<Pair<Requirement, Integer>, Double>> relScores = new ArrayList<Pair<Pair<Requirement, Integer>, Double>>();
        scores.forEach(new BiConsumer<Requirement, ArrayList<Score>>() {
            @Override
            public void accept(final Requirement requirement, final ArrayList<Score> scores) {
                final int scoresSize = scores.size();
                for (int i = 0; i < scoresSize; ++i) {
                    relScores.add(pair(pair(requirement, i), scores.get(i).getScore()));
                }
            }
        });
        relScores.sort(new Comparator<Pair<Pair<Requirement, Integer>, Double>>() {
            @Override
            public int compare(final Pair<Pair<Requirement, Integer>, Double> score1,
                               final Pair<Pair<Requirement, Integer>, Double> score2) {
                return score1.b().compareTo(score2.b());
            }
        });
        return relScores;
    }

    @Override
    public String toString() {
        final ArrayList<Pair<Requirement, ArrayList<Score>>> sortedScores = new ArrayList<Pair<Requirement, ArrayList<Score>>>();
        scores.forEach(new BiConsumer<Requirement, ArrayList<Score>>() {
            @Override
            public void accept(final Requirement requirement, final ArrayList<Score> scores) {
                sortedScores.add(pair(requirement, scores));
            }
        });
        sortedScores.sort(new Comparator<Pair<Requirement, ArrayList<Score>>>() {
            @Override
            public int compare(final Pair<Requirement, ArrayList<Score>> score1,
                               final Pair<Requirement, ArrayList<Score>> score2) {
                return score1.a().toString().compareTo(score2.a().toString());
            }
        });
        return "<" + sortedScores.toString() + ", " + totalScore + ">";
    }
}
