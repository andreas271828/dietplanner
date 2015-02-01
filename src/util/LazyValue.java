package util;

public abstract class LazyValue<A> {
    private A value;
    private boolean computed;

    public A get() {
        if (!computed) {
            value = compute();
            computed = true;
        }

        return value;
    }

    abstract protected A compute();
}