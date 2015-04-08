package diet;

import util.Mutable;
import util.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static diet.Score.score;
import static util.Mutable.mutable;
import static util.Pair.pair;

public class Scores {
    private final Map<Requirement, ArrayList<Score>> scores = new HashMap<Requirement, ArrayList<Score>>();
    private double totalScore = 0.0;
    private double weightSum = 0.0;
    private Optional<Pair<Requirement, Integer>> worstScore = Optional.empty();

    public Score getScore(final Requirement requirement, final int index) {
        return scores.get(requirement).get(index);
    }

    public Score getScore(final Requirement requirement) {
        return getScore(requirement, 0);
    }

    public Score getScore(final Pair<Requirement, Integer> scoreId) {
        return getScore(scoreId.a(), scoreId.b());
    }

    public double getScore(final Optional<Pair<Requirement, Integer>> maybeScoreId) {
        return maybeScoreId.map(new Function<Pair<Requirement, Integer>, Double>() {
            @Override
            public Double apply(final Pair<Requirement, Integer> scoreId) {
                return getScore(scoreId).getScore();
            }
        }).orElse(totalScore);
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getWeightSum() {
        return weightSum;
    }

    public Optional<Pair<Requirement, Integer>> getWorstScore() {
        return worstScore;
    }

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

        boolean isWorstScore = false;
        if (worstScore.isPresent()) {
            final Score worstScoreDetails = getScore(worstScore.get().a(), worstScore.get().b());
            if (scoreDetails.getDiffFromWeight() > worstScoreDetails.getDiffFromWeight()) {
                isWorstScore = true;
            }
        } else {
            isWorstScore = true;
        }
        if (isWorstScore) {
            worstScore = Optional.of(pair(requirement, requirementScores.size() - 1));
        }
    }

    public void addStandardScore(final Requirement requirement,
                                 final double value,
                                 final Requirements requirements) {
        requirements.getParams(requirement).ifPresent(new Consumer<ScoreParams>() {
            @Override
            public void accept(final ScoreParams scoreParams) {
                final double score = ScoreFunctions.standard(value, scoreParams);
                addScore(requirement, score, scoreParams.getWeight());
            }
        });
    }

    public void forEach(final BiConsumer<Requirement, ArrayList<Score>> action) {
        scores.forEach(action);
    }

    public Optional<Pair<Requirement, Integer>> selectScoreByDiff(final double sel) {
        final Mutable<Optional<Pair<Requirement, Integer>>> maybeScoreId = mutable(Optional.<Pair<Requirement, Integer>>empty());
        final Mutable<Double> sum = mutable(0.0);
        scores.forEach(new BiConsumer<Requirement, ArrayList<Score>>() {
            @Override
            public void accept(Requirement requirement, ArrayList<Score> scores) {
                final int scoresSize = scores.size();
                for (int i = 0; i < scoresSize && !maybeScoreId.get().isPresent(); ++i) {
                    final double diff = scores.get(i).getDiffFromWeight();
                    final double newSum = sum.get() + diff;
                    if (sel < newSum) {
                        maybeScoreId.set(Optional.of(Pair.pair(requirement, i)));
                    } else {
                        sum.set(newSum);
                    }
                }
            }
        });
        return maybeScoreId.get();
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
