package diet;

import util.Limits2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static util.Limits2.limits2;

public enum PersonalDetails {
    ANDREAS(Gender.MALE, "14/08/1982", 1.91, 80.0, 2.0, Optional.<Double>empty(), Optional.of(15.0), 1.0, Optional.of(limits2(3000.0, 5000.0)), false, false, false, false),
    ANDREAS_LOW_CARB(Gender.MALE, "14/08/1982", 1.91, 80.0, 2.0, Optional.of(60.0), Optional.<Double>empty(), 1.0, Optional.of(limits2(3000.0, 5000.0)), false, false, false, false);

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
    private final Optional<Double> maxFat; // % of energy intake per day
    private final double proteinTarget; // g per kg of ideal body weight per day
    private final Optional<Limits2> sodiumLimits; // mg per day
    private final boolean pregnancy;
    private final boolean lactation;
    private final boolean vegetarian;
    private final boolean vegan;

    /**
     * @param gender                Gender
     * @param dateOfBirth           "dd/mm/yyyy"
     * @param bodyHeight            m
     * @param idealBodyWeight       kg
     * @param physicalActivityLevel http://www.nrv.gov.au/dietary-energy
     * @param maxCarbohydrates      g per day
     * @param maxFat                % of energy intake per day
     * @param proteinTarget         g per kg of ideal body weight per day
     * @param sodiumLimits          mg per day
     * @param pregnancy             boolean
     * @param lactation             boolean
     * @param vegetarian            boolean
     * @param vegan                 boolean
     */
    PersonalDetails(final Gender gender,
                    final String dateOfBirth,
                    final double bodyHeight,
                    final double idealBodyWeight,
                    final double physicalActivityLevel,
                    final Optional<Double> maxCarbohydrates,
                    final Optional<Double> maxFat,
                    final double proteinTarget,
                    final Optional<Limits2> sodiumLimits,
                    final boolean pregnancy,
                    final boolean lactation,
                    final boolean vegetarian,
                    final boolean vegan) {
        this.gender = gender;
        this.age = convertDateOfBirthToAge(dateOfBirth);
        this.bodyHeight = bodyHeight;
        this.idealBodyWeight = idealBodyWeight;
        this.physicalActivityLevel = physicalActivityLevel;
        this.maxCarbohydrates = maxCarbohydrates;
        this.maxFat = maxFat;
        this.proteinTarget = proteinTarget;
        this.sodiumLimits = sodiumLimits;
        this.pregnancy = pregnancy;
        this.lactation = lactation;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
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
     * @return % of energy intake per day
     */
    public Optional<Double> getMaxFat() {
        return maxFat;
    }

    /**
     * @return g per kg of ideal body weight per day
     */
    public double getProteinTarget() {
        return proteinTarget;
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

    /**
     * @return kJ per day
     */
    public double getBasalMetabolicRate() {
        // Mifflin-St. Jeor equation
        final double a = gender == Gender.MALE ? 5 : -161;
        final double b = 10 * idealBodyWeight + 625 * bodyHeight - 5 * age;
        return a + b * 4.184;
    }
}
