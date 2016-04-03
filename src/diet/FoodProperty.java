/**********************************************************************
 DietPlanner

 Copyright (C) 2015-2016 Andreas Huemer

 This file is part of DietPlanner.

 DietPlanner is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at
 your option) any later version.

 DietPlanner is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/
package diet;

public enum FoodProperty {
    ALCOHOL("Alcohol (g)"),
    ALPHA_LINOLENIC_ACID("Alpha-linolenic acid (g)"),
    ALPHA_TOCOPHEROL("Alpha-tocopherol (mg)"),
    ASH("Ash (g)"),
    BETA_CAROTENE("Beta-carotene (\u00B5g)"),
    CAFFEINE("Caffeine (mg)"),
    CALCIUM("Calcium (Ca) (mg)"),
    CARBOHYDRATES_WITHOUT_SUGAR_ALCOHOL("Available carbohydrates, without sugar alcohol (g)"),
    CARBOHYDRATES("Available carbohydrates, with sugar alcohols (g)"),
    CHOLESTEROL("Cholesterol (mg)"),
    DIETARY_FIBRE("Dietary fibre (g)"),
    DIETARY_FOLATE_EQUIVALENTS("Dietary folate equivalents (\u00B5g)"),
    DOCOSAHEXAENOIC("C22:6w3 Docosahexaenoic (mg)"),
    DOCOSAPENTAENOIC("C22:5w3 Docosapentaenoic (mg)"),
    EICOSAPENTAENOIC("C20:5w3 Eicosapentaenoic (mg)"),
    ENERGY_WITHOUT_DIETARY_FIBRE("Energy, without dietary fibre (kJ)"),
    ENERGY("Energy, with dietary fibre (kJ)"),
    FAT("Total fat (g)"),
    FOLATE("Folate, natural (\u00B5g)"),
    FOLIC_ACID("Folic acid (\u00B5g)"),
    IODINE("Iodine (I) (\u00B5g)"),
    IRON("Iron (Fe) (mg)"),
    LINOLEIC_ACID("Linoleic acid (g)"),
    MAGNESIUM("Magnesium (Mg) (mg)"),
    MOISTURE("Moisture (g)"),
    MONOUNSATURATED_FAT("Total monounsaturated fat (g)"),
    NIACIN_DERIVED_EQUIVALENTS("Niacin derived equivalents (mg)"),
    NIACIN("Niacin (B3) (mg)"),
    OMEGA_3_FATTY_ACIDS("Total long chain omega 3 fatty acids (mg)"),
    PHOSPHORUS("Phosphorus (P) (mg)"),
    POLYUNSATURATED_FAT("Total polyunsaturated fat (g)"),
    POTASSIUM("Potassium (K) (mg)"),
    PROTEIN("Protein (g)"),
    PROVITAMIN_A("Provitamin A (b-carotene equivalents) (\u00B5g)"),
    RIBOFLAVIN("Riboflavin (B2) (mg)"),
    SATURATED_FAT("Total saturated fat (g)"),
    SELENIUM("Selenium (Se) (\u00B5g)"),
    SODIUM("Sodium (Na) (mg)"),
    STARCH("Starch (g)"),
    SUGARS("Total sugars (g)"),
    THIAMIN("Thiamin (B1) (mg)"),
    TOTAL_FOLATES("Total Folates (\u00B5g)"),
    TRANS_FATTY_ACIDS("Total trans fatty acids (mg)"),
    TRYPTOPHAN("Tryptophan (mg)"),
    VEGAN("Vegan (fraction)"),
    VEGETARIAN("Vegetarian (fraction)"),
    VITAMIN_A_RETINOL_EQUIVALENTS("Vitamin A retinol equivalents (\u00B5g)"),
    VITAMIN_A("Preformed vitamin A (retinol) (\u00B5g)"),
    VITAMIN_B12("Vitamin B12 (\u00B5g)"),
    VITAMIN_B6("Vitamin B6 (mg)"),
    VITAMIN_C("Vitamin C (mg)"),
    VITAMIN_E("Vitamin E (mg)"),
    ZINC("Zinc (Zn) (mg)");

    public static final double PROTEIN_ENERGY = 16.7; // kJ / g
    public static final double FAT_ENERGY = 37.7; // kJ / g
    public static final double CARBOHYDRATES_ENERGY = 16.7; // kJ / g

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
