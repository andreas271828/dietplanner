package diet;

public enum FoodProperty {
    ENERGY("Energy, with dietary fibre (kJ)"),
    ENERGY_WITHOUT_DIETARY_FIBRE("Energy, without dietary fibre (kJ)"),
    MOISTURE("Moisture (g)"),
    PROTEIN("Protein (g)"),
    FAT("Total fat (g)"),
    CARBOHYDRATES("Available carbohydrates, with sugar alcohols (g)"),
    CARBOHYDRATES_WITHOUT_SUGAR_ALCOHOL("Available carbohydrates, without sugar alcohol (g)"),
    STARCH("Starch (g)"),
    SUGARS("Total sugars (g)"),
    DIETARY_FIBRE("Dietary fibre (g)"),
    ALCOHOL("Alcohol (g)"),
    ASH("Ash (g)"),
    VITAMIN_A("Preformed vitamin A (retinol) (\u00B5g)"),
    BETA_CAROTENE("Beta-carotene (\u00B5g)"),
    PROVITAMIN_A("Provitamin A (b-carotene equivalents) (\u00B5g)"),
    VITAMIN_A_RETINOL_EQUIVALENTS("Vitamin A retinol equivalents (\u00B5g)"),
    THIAMIN("Thiamin (B1) (mg)"),
    RIBOFLAVIN("Riboflavin (B2) (mg)"),
    NIACIN("Niacin (B3) (mg)"),
    NIACIN_DERIVED_EQUIVALENTS("Niacin derived equivalents (mg)"),
    FOLATE("Folate, natural (\u00B5g)"),
    FOLIC_ACID("Folic acid (\u00B5g)"),
    TOTAL_FOLATES("Total Folates (\u00B5g)"),
    DIETARY_FOLATE_EQUIVALENTS("Dietary folate equivalents (\u00B5g)"),
    VITAMIN_B6("Vitamin B6 (mg)"),
    VITAMIN_B12("Vitamin B12 (\u00B5g)"),
    VITAMIN_C("Vitamin C (mg)"),
    ALPHA_TOCOPHEROL("Alpha-tocopherol (mg)"),
    VITAMIN_E("Vitamin E (mg)"),
    CALCIUM("Calcium (Ca) (mg)"),
    IODINE("Iodine (I) (\u00B5g)"),
    IRON("Iron (Fe) (mg)"),
    MAGNESIUM("Magnesium (Mg) (mg)"),
    PHOSPHORUS("Phosphorus (P) (mg)"),
    POTASSIUM("Potassium (K) (mg)"),
    SELENIUM("Selenium (Se) (\u00B5g)"),
    SODIUM("Sodium (Na) (mg)"),
    ZINC("Zinc (Zn) (mg)"),
    CAFFEINE("Caffeine (mg)"),
    CHOLESTEROL("Cholesterol (mg)"),
    TRYPTOPHAN("Tryptophan (mg)"),
    SATURATED_FAT("Total saturated fat (g)"),
    MONOUNSATURATED_FAT("Total monounsaturated fat (g)"),
    POLYUNSATURATED_FAT("Total polyunsaturated fat (g)"),
    LINOLEIC_ACID("Linoleic acid (g)"),
    ALPHA_LINOLENIC_ACID("Alpha-linolenic acid (g)"),
    EICOSAPENTAENOIC("C20:5w3 Eicosapentaenoic (mg)"),
    DOCOSAPENTAENOIC("C22:5w3 Docosapentaenoic (mg)"),
    DOCOSAHEXAENOIC("C22:6w3 Docosahexaenoic (mg)"),
    OMEGA_3_FATTY_ACIDS("Total long chain omega 3 fatty acids (mg)"),
    TRANS_FATTY_ACIDS("Total trans fatty acids (mg)");

    private final String name;

    FoodProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
