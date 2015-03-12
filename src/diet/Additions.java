package diet;

import util.Pair;

import java.util.ArrayList;

public class Additions {
    private final ArrayList<Pair<Integer, Addition>> additions = new ArrayList<Pair<Integer, Addition>>();

    public void add(final Addition addition) {
        // TODO: Increase usage count of addition.getBase() (or both bases) and move to appropriate location in the list.

        // Insert addition at the end with one use.
        additions.add(Pair.pair(1, addition));
    }
}
