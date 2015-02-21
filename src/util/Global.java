package util;

import java.util.Random;

public abstract class Global {
    public static final Random RANDOM = new Random();

    public static double nextRandomDoubleInclOne() {
        return RANDOM.nextInt(1073741824) / 1073741823.0;
    }
}
