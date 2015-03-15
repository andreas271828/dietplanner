package diet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import static util.Global.RANDOM;

public class Additions {
    public static final double DEFAULT_VALUE = 1.0;

    private final Map<Addition, Double> additions = new HashMap<Addition, Double>();
    private double valueSum;

    public void add(final Addition addition, final double value) {
        additions.put(addition, value);
        valueSum += value;
    }

    public void add(final Addition addition) {
        add(addition, DEFAULT_VALUE);
    }

    public void addValue(final Addition addition, final double value) {
        additions.put(addition, additions.get(addition) + value);
        valueSum += value;
    }

    public void scaleValue(final Addition addition, final double factor) {
        final double oldValue = additions.get(addition);
        final double newValue = oldValue * factor;
        additions.put(addition, newValue);
        valueSum += newValue - oldValue;
    }

    public Optional<Addition> getRandom() {
        final double selector = RANDOM.nextDouble() * valueSum;
        double cumulativeValue = 0.0;
        final Set<Entry<Addition, Double>> entrySet = additions.entrySet();
        for (final Entry<Addition, Double> entry : entrySet) {
            cumulativeValue += entry.getValue();
            if (selector < cumulativeValue) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
}
