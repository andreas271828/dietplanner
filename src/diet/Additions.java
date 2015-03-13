package diet;

import util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Additions {
    private final Map<Addition, Integer> additions = new HashMap<Addition, Integer>();

    public void add(final Addition addition) {
        // TODO: Keep index for use count for cleaning up?
        additions.put(addition, 1);

        final Optional<Pair<Addition, Addition>> maybeBases = addition.getBases();
        if (maybeBases.isPresent()) {
            final Addition base1 = maybeBases.get().a();
            final Addition base2 = maybeBases.get().b();
            additions.put(base1, additions.get(base1) + 1);
            additions.put(base2, additions.get(base2) + 1);
        }
    }

    @Override
    public String toString() {
        return additions.toString();
    }
}
