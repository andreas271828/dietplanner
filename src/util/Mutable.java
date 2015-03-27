package util;

public class Mutable<T> {
    private T t;

    public static <T> Mutable<T> mutable() {
        return new Mutable<T>();
    }

    public static <T> Mutable<T> mutable(final T t) {
        return new Mutable<T>(t);
    }

    private Mutable() {
    }

    private Mutable(final T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(final T t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return t.toString();
    }
}
