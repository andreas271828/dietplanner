package diet;

import util.Limits2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static util.Limits2.limits2;

public enum PersonalDetails {
    ANDREAS(Gender.MALE, "14/08/1982", 1.91, 80.0, 2.0, 60.0, 1.0, Optional.of(limits2(3000.0, 5000.0)), false, false, false, false);

    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN;
    }

    private final Gender gender;
    private final double age; // years
    private final double bodyHeight; // m
    private final double idealBodyWeight; // kg
    private final double physicalActivityLevel; // http://www.nrv.gov.au/dietary-energy
    private final double maxCarbohydrates; // g per day
    private final double proteinTarget; // g per kg of ideal body weight per day
    private final Optional<Limits2> sodiumLimits; // mg per day
    private final boolean pregnancy;
    private final boolean lactation;
    private final boolean vegetarian;
    private final boolean vegan;

    PersonalDetails(final Gender gender,
                    final String dateOfBirth, final double bodyHeight, final double idealBodyWeight,
                    final double physicalActivityLevel,
                    final double maxCarbohydrates,
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

    public double getAge() {
        return age;
    }

    public double getIdealBodyWeight() {
        return idealBodyWeight;
    }

    public double getPhysicalActivityLevel() {
        return physicalActivityLevel;
    }

    public double getMaxCarbohydrates() {
        return maxCarbohydrates;
    }

    public double getProteinTarget() {
        return proteinTarget; // g per kg of ideal body weight per day
    }

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
     * @return Basal metabolic rate in kJ per day
     */
    public double getBasalMetabolicRate() {
        // Mifflin-St. Jeor equation
        final double a = gender == Gender.MALE ? 5 : -161;
        final double b = 10 * idealBodyWeight + 625 * bodyHeight - 5 * age;
        return a + b * 4.184;
    }
}
