package diet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public enum PersonalDetails {
    ANDREAS(Gender.MALE, 81.0, 1.91, "14/08/1982", 2.0, 60.0, 1.0, false, false, false, false);

    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN;
    }

    private final Gender gender;
    private final double bodyWeight; // kg
    private final double bodyHeight; // m
    private final double age; // years
    private final double physicalActivityLevel; // http://www.nrv.gov.au/dietary-energy
    private final double carbohydratesLimit; // g per day
    private final double proteinTarget; // g per kg of ideal body weight per day
    private final boolean pregnancy;
    private final boolean lactation;
    private final boolean vegetarian;
    private final boolean vegan;

    PersonalDetails(final Gender gender,
                    final double bodyWeight,
                    final double bodyHeight,
                    final String dateOfBirth,
                    final double physicalActivityLevel,
                    final double carbohydratesLimit,
                    final double proteinTarget,
                    final boolean pregnancy,
                    final boolean lactation,
                    final boolean vegetarian,
                    final boolean vegan) {
        this.gender = gender;
        this.bodyWeight = bodyWeight;
        this.bodyHeight = bodyHeight;
        this.age = convertDateOfBirthToAge(dateOfBirth);
        this.physicalActivityLevel = physicalActivityLevel;
        this.carbohydratesLimit = carbohydratesLimit;
        this.proteinTarget = proteinTarget;
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

    public double getPhysicalActivityLevel() {
        return physicalActivityLevel;
    }

    public double getCarbohydratesLimit() {
        return carbohydratesLimit;
    }

    public double getProteinTarget() {
        return proteinTarget;
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
        final double b = 10 * bodyWeight + 625 * bodyHeight - 5 * age;
        return a + b * 4.184;
    }
}
