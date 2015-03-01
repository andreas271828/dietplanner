package diet;

public enum Requirement {
    ALPHA_LINOLENIC_ACID("Alpha-linolenic acid"),
    CALCIUM("Calcium"),
    CARBOHYDRATES("Carbohydrates"),
    CHOLESTEROL("Cholesterol"),
    COSTS("Costs"),
    DIETARY_FIBRE("Dietary fibre"),
    ENERGY("Energy"),
    FAT("Fat"),
    FOLATES("Folates"),
    IODINE("Iodine"),
    IRON("Iron"),
    LINOLEIC_ACID("Linoleic acid"),
    MAGNESIUM("Magnesium"),
    NIACIN_DERIVED_EQUIVALENTS("Niacin derived equivalents"),
    OMEGA_3_FATTY_ACIDS("Omega 3 fatty acids"),
    PHOSPHORUS("Phosphorus"),
    POTASSIUM("Potassium"),
    PROTEIN("Protein"),
    RIBOFLAVIN("Riboflavin"),
    SELENIUM("Selenium"),
    SODIUM("Sodium"),
    SUGARS("Sugars"),
    THIAMIN("Thiamin"),
    TRANS_FATTY_ACIDS("Trans fatty acids"),
    TRYPTOPHAN("Tryptophan"),
    VEGAN("Vegan"),
    VEGETARIAN("Vegetarian"),
    VITAMIN_A_RETINOL_EQUIVALENTS("Vitamin A retinol equivalents"),
    VITAMIN_B12("Vitamin B12"),
    VITAMIN_B6("Vitamin B6"),
    VITAMIN_C("Vitamin C"),
    VITAMIN_E("Vitamin E"),
    ZINC("Zinc"),
    MEAL_ALCOHOL("Alcohol in meal"),
    MEAL_CAFFEINE("Caffeine in meal"),
    MEAL_CARBOHYDRATES("Carbohydrates in meal"),
    MEAL_ENERGY("Energy in meal"),
    MEAL_FAT("Fat in meal"),
    MEAL_PROTEIN("Protein in meal");

    private final String name;

    Requirement(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
