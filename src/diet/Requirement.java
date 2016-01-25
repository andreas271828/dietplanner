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
    MEAL_PROTEIN("Protein in meal"),

    FOOD_ITEM_LOWER_LIMIT("Lower limit for food item"),
    FOOD_ITEM_UPPER_LIMIT("Upper limit for food item"),

    WASTE("Waste");

    private final String name;

    Requirement(final String name) {
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
