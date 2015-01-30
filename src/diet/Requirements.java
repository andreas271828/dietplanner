package diet;

import util.LazyValue;
import util.Limits4;

import static diet.PersonalDetails.Gender.FEMALE;
import static diet.PersonalDetails.Gender.MALE;
import static util.Limits4.*;

public class Requirements {
    private final static double DEFAULT_TOLERANCE = 0.05;

    private final PersonalDetails personalDetails;
    private final double days;
    private final int numberOfMeals;

    private final LazyValue<Limits4> alphaLinolenicAcidLimits; // g
    private final LazyValue<Limits4> energyLimits; // kJ

    private final LazyValue<Limits4> mealAlcoholLimits; // g
    private final LazyValue<Limits4> mealEnergyLimits; // kJ

    private final LazyValue<Double> alphaLinolenicAcidAIPerDay; // g
    private final LazyValue<Double> energyDemandPerDay; // kJ

    public Requirements(final PersonalDetails personalDetails, final double days, final int numberOfMeals) {
        this.personalDetails = personalDetails;
        this.days = days;
        this.numberOfMeals = numberOfMeals;

        alphaLinolenicAcidLimits = new LazyValue<Limits4>() {
            @Override
            protected Limits4 compute() {
                return computeAlphaLinolenicAcidLimits();
            }
        };
        energyLimits = new LazyValue<Limits4>() {
            @Override
            protected Limits4 compute() {
                return computeEnergyLimits();
            }
        };

        mealAlcoholLimits = new LazyValue<Limits4>() {
            @Override
            protected Limits4 compute() {
                return computeMealAlcoholLimits();
            }
        };
        mealEnergyLimits = new LazyValue<Limits4>() {
            @Override
            protected Limits4 compute() {
                return computeMealEnergyLimits();
            }
        };

        alphaLinolenicAcidAIPerDay = new LazyValue<Double>() {
            @Override
            protected Double compute() {
                return computeAlphaLinolenicAcidAIPerDay();
            }
        };
        energyDemandPerDay = new LazyValue<Double>() {
            @Override
            protected Double compute() {
                return computeEnergyDemandPerDay();
            }
        };
    }

    public int getNumberOfMeals() {
        return numberOfMeals;
    }

    public Limits4 getAlphaLinolenicAcidLimits() {
        return alphaLinolenicAcidLimits.get();
    }

    public Limits4 getEnergyLimits() {
        return energyLimits.get();
    }

    public Limits4 getMealAlcoholLimits() {
        return mealAlcoholLimits.get();
    }

    public Limits4 getMealEnergyLimits() {
        return mealEnergyLimits.get();
    }

    private Limits4 computeAlphaLinolenicAcidLimits() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        final Double aiPerDay = alphaLinolenicAcidAIPerDay.get();
        final Double ai = aiPerDay == null ? null : aiPerDay * days;
        return ai == null ? null : limits4(0.8 * ai, 0.9 * ai, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private Limits4 computeEnergyLimits() {
        // http://www.nrv.gov.au/dietary-energy
        final Double demandPerDay = energyDemandPerDay.get();
        final Double demand = demandPerDay == null ? null : demandPerDay * days;
        return demand == null ? null : limits4ORC(demand, DEFAULT_TOLERANCE);
    }

    private Limits4 computeMealAlcoholLimits() {
        return limits4UC(0.5);
    }

    private Limits4 computeMealEnergyLimits() {
        final Double demandPerDay = energyDemandPerDay.get();
        final Double demandPerMeal = demandPerDay == null ? null : demandPerDay * days / numberOfMeals;
        return demandPerMeal == null ? null : limits4RORC(demandPerMeal, 0.3, 0.6);
    }

    private Double computeAlphaLinolenicAcidAIPerDay() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        final double age = personalDetails.getAge();
        if (age >= 1) {
            if (age < 4) {
                return 0.5;
            } else if (age < 9) {
                return 0.8;
            } else {
                final PersonalDetails.Gender gender = personalDetails.getGender();
                if (gender == MALE) {
                    if (age < 14) {
                        return 1.0;
                    } else if (age < 19) {
                        return 1.2;
                    } else {
                        return 1.3;
                    }
                } else if (gender == FEMALE) {
                    final boolean pregnancy = personalDetails.getPregnancy();
                    final boolean lactation = personalDetails.getLactation();
                    if (pregnancy) {
                        return 1.0;
                    } else if (lactation) {
                        return 1.2;
                    } else {
                        return 0.8;
                    }
                }
            }
        }
        return null;
    }

    private Double computeEnergyDemandPerDay() {
        // http://www.nrv.gov.au/dietary-energy
        // TODO: Infants, children, adolescents
        final double bmr = personalDetails.getBasalMetabolicRate();
        final double pal = personalDetails.getPhysicalActivityLevel();
        return bmr * pal;
    }
}
