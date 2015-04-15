package util;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import static util.Limits2.limits2;

public abstract class Global {
    public static final Random RANDOM = new Random();

    public static double nextRandomDoubleInclOne() {
        return RANDOM.nextInt(1073741824) / 1073741823.0;
    }

    /**
     * @param all       All elements
     * @param selectors Value between 0.0 (incl.) and 1.0 (excl.)
     * @param valFunc   Function for calculating the value of an element
     * @param <T>       Element type
     * @return indices of selected elements
     */
    public static <T> ArrayList<Integer> selectElements(final ArrayList<T> all,
                                                        final ArrayList<Double> selectors,
                                                        final Function<T, Double> valFunc) {
        final ArrayList<Integer> indices = new ArrayList<Integer>();

        final int allSize = all.size();
        if (allSize > 0) {
            final ArrayList<Double> values = new ArrayList<Double>(allSize);

            // Determine values and remember smallest and biggest.
            Optional<Limits2> maybeLimits = Optional.empty();
            for (final T element : all) {
                final double value = valFunc.apply(element);
                values.add(value);
                if (maybeLimits.isPresent()) {
                    final Limits2 limits = maybeLimits.get();
                    final double minVal = limits.getMin();
                    final double maxVal = limits.getMax();
                    if (value < minVal) {
                        maybeLimits = Optional.of(limits2(value, maxVal));
                    } else if (value > maxVal) {
                        maybeLimits = Optional.of(limits2(minVal, value));
                    }
                } else {
                    maybeLimits = Optional.of(limits2(value, value));
                }
            }

            // Scale and aggregate values.
            final Limits2 limits = maybeLimits.get();
            final double minVal = limits.getMin();
            final double maxVal = limits.getMax();
            final double valRange = maxVal - minVal;
            final double scaleFactor = valRange > 0.0 ? 1.0 / valRange : 1.0;
            double valSum = 0.0;
            for (int i = 0; i < allSize; ++i) {
                valSum += (values.get(i) - minVal) * scaleFactor;
                values.set(i, valSum);
            }

            // Scale selectors.
            final int selectorsSize = selectors.size();
            final ArrayList<Double> scaledSelectors = new ArrayList<Double>(selectorsSize);
            for (final Double selector : selectors) {
                scaledSelectors.add(selector * valSum);
            }

            // Select elements.
            for (final Double scaledSelector : scaledSelectors) {
                for (int i = 0; i < allSize; ++i) {
                    if (values.get(i) > scaledSelector) {
                        indices.add(i);
                        break;
                    }
                }
            }
        }

        return indices;
    }
}
