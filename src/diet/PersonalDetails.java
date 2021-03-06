/**********************************************************************
 * DietPlanner
 * <p/>
 * Copyright (C) 2015-2016 Andreas Huemer
 * <p/>
 * This file is part of DietPlanner.
 * <p/>
 * DietPlanner is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * <p/>
 * DietPlanner is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/
package diet;

import util.Limits2;
import util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static util.Limits2.limits2;
import static util.Pair.pair;

public enum PersonalDetails {
    // If fat, protein and sugar are limited too much, it's difficult to get enough energy without violating other limits.
    ANDREAS(Gender.MALE,
            "14/08/1982",
            1.91,
            78.0,
            1.6,
            Optional.of(30.0),
            20.0,
            Optional.<Double>empty(),
            limits2(0.9, 1.2),
            Optional.of(limits2(1500.0, 2000.0)),
            false,
            false,
            false,
            false,
            lowerLimitsAndreas(),
            upperLimitsAndreas());

    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN
    }

    private final Gender gender;
    private final double age; // years
    private final double bodyHeight; // m
    private final double idealBodyWeight; // kg
    private final double physicalActivityLevel; // http://www.nrv.gov.au/dietary-energy
    private final Optional<Double> maxCarbohydrates; // g per day
    private final double maxSugars; // g per day
    private final Optional<Double> maxFat; // % of energy intake per day
    private final Limits2 proteinLimits; // g per kg of ideal body weight per day
    private final Optional<Limits2> sodiumLimits; // mg per day
    private final boolean pregnancy;
    private final boolean lactation;
    private final boolean vegetarian;
    private final boolean vegan;
    private final ArrayList<Pair<FoodItem, Double>> lowerLimits;
    private final ArrayList<Pair<FoodItem, Double>> upperLimits;

    /**
     * @param gender                Gender
     * @param dateOfBirth           "dd/mm/yyyy"
     * @param bodyHeight            m
     * @param idealBodyWeight       kg
     * @param physicalActivityLevel http://www.nrv.gov.au/dietary-energy
     * @param maxCarbohydrates      g per day
     * @param maxSugars             g per day
     * @param maxFat                % of energy intake per day
     * @param proteinLimits         g per kg of ideal body weight per day
     * @param sodiumLimits          mg per day
     * @param pregnancy             boolean
     * @param lactation             boolean
     * @param vegetarian            boolean
     * @param vegan                 boolean
     * @param lowerLimits           Minimum amount per day for food items
     * @param upperLimits           Maximum amount per day for food items
     */
    PersonalDetails(final Gender gender,
                    final String dateOfBirth,
                    final double bodyHeight,
                    final double idealBodyWeight,
                    final double physicalActivityLevel,
                    final Optional<Double> maxCarbohydrates,
                    final double maxSugars,
                    final Optional<Double> maxFat,
                    final Limits2 proteinLimits,
                    final Optional<Limits2> sodiumLimits,
                    final boolean pregnancy,
                    final boolean lactation,
                    final boolean vegetarian,
                    final boolean vegan,
                    final ArrayList<Pair<FoodItem, Double>> lowerLimits,
                    final ArrayList<Pair<FoodItem, Double>> upperLimits) {
        this.gender = gender;
        this.age = convertDateOfBirthToAge(dateOfBirth);
        this.bodyHeight = bodyHeight;
        this.idealBodyWeight = idealBodyWeight;
        this.physicalActivityLevel = physicalActivityLevel;
        this.maxCarbohydrates = maxCarbohydrates;
        this.maxSugars = maxSugars;
        this.maxFat = maxFat;
        this.proteinLimits = proteinLimits;
        this.sodiumLimits = sodiumLimits;
        this.pregnancy = pregnancy;
        this.lactation = lactation;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.lowerLimits = lowerLimits;
        this.upperLimits = upperLimits;
    }

    private double convertDateOfBirthToAge(final String dateOfBirth) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final Date dob = sdf.parse(dateOfBirth);
            final Date now = new Date();
            final long msSinceBirth = now.getTime() - dob.getTime();
            return TimeUnit.MILLISECONDS.toDays(msSinceBirth) / 365.2425;
        } catch (final ParseException e) {
            throw new IllegalArgumentException("Illegal date of birth");
        }
    }

    public Gender getGender() {
        return gender;
    }

    /**
     * @return years
     */
    public double getAge() {
        return age;
    }

    /**
     * @return kg
     */
    public double getIdealBodyWeight() {
        return idealBodyWeight;
    }

    /**
     * @return http://www.nrv.gov.au/dietary-energy
     */
    public double getPhysicalActivityLevel() {
        return physicalActivityLevel;
    }

    /**
     * @return g per day
     */
    public Optional<Double> getMaxCarbohydrates() {
        return maxCarbohydrates;
    }

    /**
     * @return g per day
     */
    public double getMaxSugars() {
        return maxSugars;
    }

    /**
     * @return % of energy intake per day
     */
    public Optional<Double> getMaxFat() {
        return maxFat;
    }

    /**
     * @return g per kg of ideal body weight per day
     */
    public Limits2 getProteinLimits() {
        return proteinLimits;
    }

    /**
     * @return mg per day
     */
    public Optional<Limits2> getSodiumLimits() {
        return sodiumLimits;
    }

    public boolean getPregnancy() {
        return pregnancy;
    }

    public boolean getLactation() {
        return lactation;
    }

    public boolean getVegetarian() {
        return vegetarian;
    }

    public boolean getVegan() {
        return vegan;
    }

    public ArrayList<Pair<FoodItem, Double>> getLowerLimits() {
        return lowerLimits;
    }

    public ArrayList<Pair<FoodItem, Double>> getUpperLimits() {
        return upperLimits;
    }

    /**
     * @return kJ per day
     */
    public double getBasalMetabolicRate() {
        // Mifflin-St. Jeor equation
        final double a = gender == Gender.MALE ? 5 : -161;
        final double b = 10 * idealBodyWeight + 625 * bodyHeight - 5 * age;
        return a + b * 4.184;
    }

    private static ArrayList<Pair<FoodItem, Double>> noConstraints() {
        return new ArrayList<Pair<FoodItem, Double>>();
    }

    private static ArrayList<Pair<FoodItem, Double>> lowerLimitsAndreas() {
        // Minimum amounts per day
        final ArrayList<Pair<FoodItem, Double>> lowerLimitsAndreas = new ArrayList<Pair<FoodItem, Double>>();
        return lowerLimitsAndreas;
    }

    private static ArrayList<Pair<FoodItem, Double>> upperLimitsAndreas() {
        // Maximum amounts per day
        final ArrayList<Pair<FoodItem, Double>> upperLimitsAndreas = new ArrayList<Pair<FoodItem, Double>>();
        upperLimitsAndreas.add(pair(FoodItem.COLES_BREAD_MIXED_GRAIN, FoodItem.COLES_CHICKPEA.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_CHEESE_BRIE, 0.5));
        upperLimitsAndreas.add(pair(FoodItem.COLES_CHEESE_CAMEMBERT, 0.5));
        upperLimitsAndreas.add(pair(FoodItem.COLES_CHICKPEA, FoodItem.COLES_CHICKPEA.weightToAmount(80.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_CELERY, FoodItem.COLES_CELERY.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_NUT_ALMOND, FoodItem.COLES_CELERY.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_NUT_BRAZIL, FoodItem.COLES_CELERY.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_NUT_CASHEW, FoodItem.COLES_CELERY.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_NUT_MACADAMIA, FoodItem.COLES_CELERY.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_NUT_PECAN, FoodItem.COLES_CELERY.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_NUT_WALNUT, FoodItem.COLES_CELERY.weightToAmount(100.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_POTATO, FoodItem.COLES_POTATO.weightToAmount(200.0)));
        upperLimitsAndreas.add(pair(FoodItem.COLES_ROCKET, FoodItem.COLES_ROCKET.weightToAmount(40.0)));
        return upperLimitsAndreas;
    }
}
