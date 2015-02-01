package diet;

import util.LazyValue;
import util.Limits2;
import util.Limits4;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

import static diet.PersonalDetails.Gender.FEMALE;
import static diet.PersonalDetails.Gender.MALE;
import static util.Limits2.limits2;
import static util.Limits4.*;

public class Requirements {
    private final static double DEFAULT_TOLERANCE = 0.05;

    private final PersonalDetails personalDetails;
    private final double days;
    private final int numberOfMeals;

    private final EnumMap<Requirement, LazyValue<Optional<Limits4>>> requirements;

    private final LazyValue<Optional<Double>> alphaLinolenicAcidAIPerDay; // g
    private final LazyValue<Optional<Double>> carbohydratesLimitPerDay; // g
    private final LazyValue<Optional<Double>> energyDemandPerDay; // kJ
    private final LazyValue<Optional<Limits2>> ironLimitsPerDay; // mg

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
        requirements.put(Requirement.CALCIUM, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.CARBOHYDRATES, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeCarbohydratesLimits();
            }
        });
        requirements.put(Requirement.CHOLESTEROL, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.COSTS, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeCostsLimits();
            }
        });
        requirements.put(Requirement.DIETARY_FIBRE, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.ENERGY, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeEnergyLimits();
            }
        });
        requirements.put(Requirement.FAT, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.FOLATES, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.IODINE, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.IRON, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeIronLimits();
            }
        });
        requirements.put(Requirement.LINOLEIC_ACID, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.MAGNESIUM, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.NIACIN_DERIVED_EQUIVALENTS, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.OMEGA_3_FATTY_ACIDS, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.PHOSPHORUS, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.POTASSIUM, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.PROTEIN, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.RIBOFLAVIN, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.SELENIUM, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.SODIUM, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.SUGARS, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.THIAMIN, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.TRANS_FATTY_ACIDS, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.TRYPTOPHAN, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.VEGAN, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.VEGETARIAN, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.VITAMIN_B12, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.VITAMIN_B6, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.VITAMIN_C, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.VITAMIN_E, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.ZINC, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.MEAL_ALCOHOL, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeMealAlcoholLimits();
            }
        });
        requirements.put(Requirement.MEAL_CAFFEINE, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.MEAL_CARBOHYDRATES, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeMealCarbohydratesLimits();
            }
        });
        requirements.put(Requirement.MEAL_ENERGY, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return computeMealEnergyLimits();
            }
        });
        requirements.put(Requirement.MEAL_FAT, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });
        requirements.put(Requirement.MEAL_PROTEIN, new LazyValue<Optional<Limits4>>() {
            @Override
            protected Optional<Limits4> compute() {
                return noLimits();
            }
        });


        alphaLinolenicAcidAIPerDay = new LazyValue<Optional<Double>>() {
            @Override
            protected Optional<Double> compute() {
                return computeAlphaLinolenicAcidAIPerDay();
            }
        };
        carbohydratesLimitPerDay = new LazyValue<Optional<Double>>() {
            @Override
            protected Optional<Double> compute() {
                return computeCarbohydratesLimitPerDay();
            }
        };
        energyDemandPerDay = new LazyValue<Optional<Double>>() {
            @Override
            protected Optional<Double> compute() {
                return computeEnergyDemandPerDay();
            }
        };
        ironLimitsPerDay = new LazyValue<Optional<Limits2>>() {
            @Override
            protected Optional<Limits2> compute() {
                return computeIronLimitsPerDay();
            }
        };
    }

    public int getNumberOfMeals() {
        return numberOfMeals;
    }

    public Optional<Limits4> getLimits(final Requirement requirement) {
        return requirements.containsKey(requirement) ? requirements.get(requirement).get() : Optional.<Limits4>empty();
    }

    private static Optional<Limits4> noLimits() {
        return Optional.empty();
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

    private Optional<Limits4> limitsFromLimitsPerDay(final LazyValue<Optional<Limits2>> maybeLimitsPerDay,
                                                     final Function<Limits2, Limits4> computeLimits) {
        return maybeLimitsPerDay.get().map(new Function<Limits2, Limits2>() {
            @Override
            public Limits2 apply(final Limits2 limitsPerDay) {
                return limits2(limitsPerDay.getMin() * days, limitsPerDay.getMax() * days);
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

    private Optional<Limits4> computeCarbohydratesLimits() {
        return limitsFromValuePerDay(carbohydratesLimitPerDay, new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0, 0, 0.9 * total, total);
            }
        });
    }

    private Optional<Limits4> computeCostsLimits() {
        return Optional.of(limits4UC(25 * days)); // AUD
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

    private Optional<Limits4> computeIronLimits() {
        return limitsFromLimitsPerDay(ironLimitsPerDay, new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    private Optional<Limits4> computeMealAlcoholLimits() {
        return Optional.of(limits4UC(0.5)); // g
    }

    private Optional<Limits4> computeMealCarbohydratesLimits() {
        return limitsFromValuePerDay(carbohydratesLimitPerDay, new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                final double upperCritical = 1.2 * total / numberOfMeals;
                return limits4(0, 0, 0.8 * upperCritical, upperCritical);
            }
        });
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

    private Optional<Double> computeCarbohydratesLimitPerDay() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return Optional.of(personalDetails.getCarbohydratesLimit());
    }

    private Optional<Double> computeEnergyDemandPerDay() {
        // http://www.nrv.gov.au/dietary-energy
        // TODO: Infants, children, adolescents
        final double bmr = personalDetails.getBasalMetabolicRate();
        final double pal = personalDetails.getPhysicalActivityLevel();
        return Optional.of(bmr * pal); // kJ
    }

    private Optional<Limits2> computeIronLimitsPerDay() {
        // http://www.nrv.gov.au/nutrients/iron
        // TODO: Infants, children, adolescents, pregnancy, lactation
        final PersonalDetails.Gender gender = personalDetails.getGender();
        final double age = personalDetails.getAge();
        final double lowerLimit = (gender == FEMALE && age >= 19 && age < 51) ? 18 : 8;
        final double upperLimit = 45;
        return Optional.of(limits2(lowerLimit, upperLimit));
    }
}
