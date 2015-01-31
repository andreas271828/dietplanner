package diet;

import util.LazyValue;
import util.Limits4;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

import static diet.PersonalDetails.Gender.FEMALE;
import static diet.PersonalDetails.Gender.MALE;
import static util.Limits4.*;

public class Requirements {
    private final static double DEFAULT_TOLERANCE = 0.05;

    private final PersonalDetails personalDetails;
    private final double days;
    private final int numberOfMeals;

    private final EnumMap<Requirement, LazyValue<Optional<Limits4>>> requirements;

    private final LazyValue<Optional<Double>> alphaLinolenicAcidAIPerDay; // g
    private final LazyValue<Optional<Double>> energyDemandPerDay; // kJ

    public Requirements(final PersonalDetails personalDetails, final double days, final int numberOfMeals) {
        this.personalDetails = personalDetails;
        this.days = days;
        this.numberOfMeals = numberOfMeals;

        requirements = new EnumMap<Requirement, LazyValue<Optional<Limits4>>>(Requirement.class);
        requirements.put(Requirement.ALPHA_LINOLENIC_ACID, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeAlphaLinolenicAcidLimits();
            }
        });
        requirements.put(Requirement.ENERGY, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeEnergyLimits();
            }
        });
        requirements.put(Requirement.MEAL_ALCOHOL, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeMealAlcoholLimits();
            }
        });
        requirements.put(Requirement.MEAL_ENERGY, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeMealEnergyLimits();
            }
        });

        alphaLinolenicAcidAIPerDay = new LazyValue<Optional<Double>>() {
            @Override
            protected Optional<Double> compute() {
                return computeAlphaLinolenicAcidAIPerDay();
            }
        };
        energyDemandPerDay = new LazyValue<Optional<Double>>() {
            @Override
            protected Optional<Double> compute() {
                return computeEnergyDemandPerDay();
            }
        };
    }

    public int getNumberOfMeals() {
        return numberOfMeals;
    }

    public Optional<Limits4> getLimits(final Requirement requirement) {
        return requirements.containsKey(requirement) ? requirements.get(requirement).get() : Optional.<Limits4>empty();
    }

    private Optional<Limits4> limitsFromValuePerDay(final LazyValue<Optional<Double>> maybeValuePerDay,
                                                    final Function<Double, Limits4> computeLimits) {
        return maybeValuePerDay.get().map(new Function<Double, Double>() {
            @Override
            public Double apply(final Double valuePerDay) {
                return valuePerDay * days;
            }
        }).map(computeLimits);
    }

    private Optional<Limits4> computeAlphaLinolenicAcidLimits() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        return limitsFromValuePerDay(alphaLinolenicAcidAIPerDay, new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0.8 * total, 0.9 * total, Double.MAX_VALUE, Double.MAX_VALUE);
            }
        });
    }

    private Optional<Limits4> computeEnergyLimits() {
        // http://www.nrv.gov.au/dietary-energy
        return limitsFromValuePerDay(energyDemandPerDay, new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4ORC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    private Optional<Limits4> computeMealAlcoholLimits() {
        return Optional.of(limits4UC(0.5)); // g
    }

    private Optional<Limits4> computeMealEnergyLimits() {
        return limitsFromValuePerDay(energyDemandPerDay, new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4RORC(total / numberOfMeals, 0.3, 0.6);
            }
        });
    }

    private Optional<Double> computeAlphaLinolenicAcidAIPerDay() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        final double age = personalDetails.getAge();
        if (age >= 1) {
            if (age < 4) {
                return Optional.of(0.5);
            } else if (age < 9) {
                return Optional.of(0.8);
            } else {
                final PersonalDetails.Gender gender = personalDetails.getGender();
                if (gender == MALE) {
                    if (age < 14) {
                        return Optional.of(1.0);
                    } else if (age < 19) {
                        return Optional.of(1.2);
                    } else {
                        return Optional.of(1.3);
                    }
                } else if (gender == FEMALE) {
                    final boolean pregnancy = personalDetails.getPregnancy();
                    final boolean lactation = personalDetails.getLactation();
                    if (pregnancy) {
                        return Optional.of(1.0);
                    } else if (lactation) {
                        return Optional.of(1.2);
                    } else {
                        return Optional.of(0.8);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Double> computeEnergyDemandPerDay() {
        // http://www.nrv.gov.au/dietary-energy
        // TODO: Infants, children, adolescents
        final double bmr = personalDetails.getBasalMetabolicRate();
        final double pal = personalDetails.getPhysicalActivityLevel();
        return Optional.of(bmr * pal); // kJ
    }
}
