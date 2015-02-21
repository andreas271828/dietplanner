package diet;

import java.util.ArrayList;
import java.util.Optional;

public class DietPlanChange {
    private final DietPlan dietPlan;
    private final Optional<ArrayList<FoodItems>> changes;

    public static DietPlanChange dietPlanChange(final DietPlan dietPlan, final Optional<ArrayList<FoodItems>> changes) {
        return new DietPlanChange(dietPlan, changes);
    }

    private DietPlanChange(final DietPlan dietPlan, final Optional<ArrayList<FoodItems>> changes) {
        this.dietPlan = dietPlan;
        this.changes = changes;
    }

    public DietPlan getDietPlan() {
        return dietPlan;
    }
}
