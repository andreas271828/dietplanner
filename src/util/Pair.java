package util;

public class Pair<A, B> {
    private final A a;
    private final B b;

    public static <A, B> Pair<A, B> pair(final A a, final B b) {
        return new Pair<A, B>(a, b);
    }

    private Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public A a() {
        return a;
    }

    public B b() {
        return b;
    }

    @Override
    public int hashCode() {
        // TODO: Faster hash algorithm
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "<" + a.toString() + ", " + b.toString() + ">";
    }
}
