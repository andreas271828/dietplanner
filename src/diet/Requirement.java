package diet;

public enum Requirement {
    ALPHA_LINOLENIC_ACID("Alpha-linolenic acid", false),
    CALCIUM("Calcium", false),
    CARBOHYDRATES("Carbohydrates", false),
    CHOLESTEROL("Cholesterol", false),
    COSTS("Costs", false),
    DIETARY_FIBRE("Dietary fibre", false),
    ENERGY("Energy", false),
    FAT("Fat", false),
    FOLATES("Folates", false),
    IODINE("Iodine", false),
    IRON("Iron", false),
    LINOLEIC_ACID("Linoleic acid", false),
    MAGNESIUM("Magnesium", false),
    NIACIN_DERIVED_EQUIVALENTS("Niacin derived equivalents", false),
    OMEGA_3_FATTY_ACIDS("Omega 3 fatty acids", false),
    PHOSPHORUS("Phosphorus", false),
    POTASSIUM("Potassium", false),
    PROTEIN("Protein", false),
    RIBOFLAVIN("Riboflavin", false),
    SELENIUM("Selenium", false),
    SODIUM("Sodium", false),
    SUGARS("Sugars", false),
    THIAMIN("Thiamin", false),
    TRANS_FATTY_ACIDS("Trans fatty acids", false),
    TRYPTOPHAN("Tryptophan", false),
    VEGAN("Vegan", false),
    VEGETARIAN("Vegetarian", false),
    VITAMIN_A_RETINOL_EQUIVALENTS("Vitamin A retinol equivalents", false),
    VITAMIN_B12("Vitamin B12", false),
    VITAMIN_B6("Vitamin B6", false),
    VITAMIN_C("Vitamin C", false),
    VITAMIN_E("Vitamin E", false),
    ZINC("Zinc", false),
    MEAL_ALCOHOL("Alcohol in meal", true),
    MEAL_CAFFEINE("Caffeine in meal", true),
    MEAL_CARBOHYDRATES("Carbohydrates in meal", true),
    MEAL_ENERGY("Energy in meal", true),
    MEAL_FAT("Fat in meal", true),
    MEAL_PROTEIN("Protein in meal", true);

    private final String name;
    private final boolean isMealRequirement;

    Requirement(final String name, boolean isMealRequirement) {
        this.name = name;
        this.isMealRequirement = isMealRequirement;
    }

    public String getName() {
        return name;
    }

    public boolean isMealRequirement() {
        return isMealRequirement;
    }

    @Override
    public String toString() {
        return name;
    }
}
