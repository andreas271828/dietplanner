package diet;

public enum Requirement {
    ALPHA_LINOLENIC_ACID("Alpha-linolenic acid"),
    ENERGY("Energy"),
    MEAL_ALCOHOL("Alcohol"),
    MEAL_ENERGY("Energy");

    private final String name;

    Requirement(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
