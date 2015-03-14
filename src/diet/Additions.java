package diet;

import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static util.Global.RANDOM;

public class Additions {
    private final ArrayList<Addition> additions = new ArrayList<Addition>();
    private final Map<Addition, Integer> additionUsages = new HashMap<Addition, Integer>();

    public void add(final Addition addition) {
        additions.add(addition);

        additionUsages.put(addition, 1);
        final Optional<Pair<Addition, Addition>> maybeBases = addition.getBases();
        if (maybeBases.isPresent()) {
            final Addition base1 = maybeBases.get().a();
            final Addition base2 = maybeBases.get().b();
            additionUsages.put(base1, additionUsages.get(base1) + 1);
            additionUsages.put(base2, additionUsages.get(base2) + 1);
        }
    }

    public Optional<Addition> getRandom() {
        if (additions.size() > 0) {
            return Optional.of(additions.get(RANDOM.nextInt(additions.size())));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return additionUsages.toString();
    }
}
