package diet;

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
    private static final double DEFAULT_TOLERANCE = 0.05;

    private final PersonalDetails personalDetails;
    private final double days;
    private final int numberOfMeals;

    private final EnumMap<Requirement, Optional<Limits4>> requirements;

    public Requirements(final PersonalDetails personalDetails, final double days, final int numberOfMeals) {
        this.personalDetails = personalDetails;
        this.days = days;
        this.numberOfMeals = numberOfMeals;

        requirements = new EnumMap<Requirement, Optional<Limits4>>(Requirement.class);
        requirements.put(Requirement.ALPHA_LINOLENIC_ACID, getAlphaLinolenicAcidLimits());
        requirements.put(Requirement.CALCIUM, getCalciumLimits());
        requirements.put(Requirement.CARBOHYDRATES, getCarbohydratesLimits());
        requirements.put(Requirement.CHOLESTEROL, getCholesterolLimits());
        requirements.put(Requirement.COSTS, getCostsLimits());
        requirements.put(Requirement.DIETARY_FIBRE, getDietaryFibreLimits());
        requirements.put(Requirement.ENERGY, getEnergyLimits());
        requirements.put(Requirement.FAT, getFatLimits());
        requirements.put(Requirement.FOLATES, getFolatesLimits());
        requirements.put(Requirement.IODINE, getIodineLimits());
        requirements.put(Requirement.IRON, getIronLimits());
        requirements.put(Requirement.LINOLEIC_ACID, getLinoleicAcidLimits());
        requirements.put(Requirement.MAGNESIUM, getMagnesiumLimits());
        requirements.put(Requirement.NIACIN_DERIVED_EQUIVALENTS, getNiacinDerivedEquivalentsLimits());
        requirements.put(Requirement.OMEGA_3_FATTY_ACIDS, getOmega3FattyAcidsLimits());
        requirements.put(Requirement.PHOSPHORUS, getPhosphorusLimits());
        requirements.put(Requirement.POTASSIUM, getPotassiumLimits());
        requirements.put(Requirement.PROTEIN, getProteinLimits());
        requirements.put(Requirement.RIBOFLAVIN, getRiboflavinLimits());
        requirements.put(Requirement.SELENIUM, getSeleniumLimits());
        requirements.put(Requirement.SODIUM, getSodiumLimits());
        requirements.put(Requirement.SUGARS, getSugarsLimits());
        requirements.put(Requirement.THIAMIN, getThiaminLimits());
        requirements.put(Requirement.TRANS_FATTY_ACIDS, getTransFattyAcidsLimits());
        requirements.put(Requirement.TRYPTOPHAN, getTryptophanLimits());
        requirements.put(Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, getVitaminARetinolEquivalentsLimits());
        requirements.put(Requirement.VITAMIN_B12, getVitaminB12Limits());
        requirements.put(Requirement.VITAMIN_B6, getVitaminB6Limits());
        requirements.put(Requirement.VITAMIN_C, getVitaminCLimits());
        requirements.put(Requirement.VITAMIN_E, getVitaminELimits());
        requirements.put(Requirement.ZINC, getZincLimits());
        requirements.put(Requirement.MEAL_ALCOHOL, getMealAlcoholLimits());
        requirements.put(Requirement.MEAL_CAFFEINE, getMealCaffeineLimits());
        requirements.put(Requirement.MEAL_CARBOHYDRATES, getMealCarbohydratesLimits());
        requirements.put(Requirement.MEAL_ENERGY, getMealEnergyLimits());
        requirements.put(Requirement.MEAL_FAT, getMealFatLimits());
        requirements.put(Requirement.MEAL_PROTEIN, getMealProteinLimits());
    }

    public int getNumberOfMeals() {
        return numberOfMeals;
    }

    public Optional<Limits4> getLimits(final Requirement requirement) {
        return requirements.containsKey(requirement) ? requirements.get(requirement) : Optional.<Limits4>empty();
    }

    private Optional<Limits4> getLimitsFromValuePerDay(final Optional<Double> maybeValuePerDay,
                                                       final Function<Double, Limits4> getLimits) {
        return maybeValuePerDay.map(new Function<Double, Double>() {
            @Override
            public Double apply(final Double valuePerDay) {
                return valuePerDay * days;
            }
        }).map(getLimits);
    }

    private Optional<Limits4> getLimitsFromLimitsPerDay(final Optional<Limits2> maybeLimitsPerDay,
                                                        final Function<Limits2, Limits4> getLimits) {
        return maybeLimitsPerDay.map(new Function<Limits2, Limits2>() {
            @Override
            public Limits2 apply(final Limits2 limitsPerDay) {
                return limits2(limitsPerDay.getMin() * days, limitsPerDay.getMax() * days);
            }
        }).map(getLimits);
    }

    /**
     * @return g
     */
    private Optional<Limits4> getAlphaLinolenicAcidLimits() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        return getLimitsFromValuePerDay(getAlphaLinolenicAcidAIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0.8 * total, 0.9 * total, Double.MAX_VALUE, Double.MAX_VALUE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getCalciumLimits() {
        // http://www.nrv.gov.au/nutrients/calcium
        return getLimitsFromLimitsPerDay(getCalciumRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Limits4> getCarbohydratesLimits() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return getLimitsFromValuePerDay(getMaxCarbohydratesPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0, 0, 0.9 * total, total);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getCholesterolLimits() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.health.gov/dietaryguidelines/2010.asp
        return Optional.empty();
    }

    /**
     * @return AUD
     */
    private Optional<Limits4> getCostsLimits() {
        return Optional.of(limits4UC(25 * days));
    }

    /**
     * @return g
     */
    private Optional<Limits4> getDietaryFibreLimits() {
        // http://www.nrv.gov.au/nutrients/dietary-fibre
        return getLimitsFromValuePerDay(getDietaryFibreAIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0.8 * total, 0.9 * total, Double.MAX_VALUE, Double.MAX_VALUE);
            }
        });
    }

    /**
     * @return kJ
     */
    private Optional<Limits4> getEnergyLimits() {
        // http://www.nrv.gov.au/dietary-energy
        return getLimitsFromValuePerDay(getEnergyDemandPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4ORC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Limits4> getFatLimits() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.cdc.gov/nutrition/everyone/basics/fat/index.html?s_cid=tw_ob294
        return Optional.empty();
    }

    /**
     * @return µg
     */
    private Optional<Limits4> getFolatesLimits() {
        // http://www.nrv.gov.au/nutrients/folate
        return getLimitsFromValuePerDay(getFolatesRDIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4LORLC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<Limits4> getIodineLimits() {
        // http://www.nrv.gov.au/nutrients/iodine
        return getLimitsFromValuePerDay(getIodineRDIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4LORLC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getIronLimits() {
        // http://www.nrv.gov.au/nutrients/iron
        return getLimitsFromLimitsPerDay(getIronRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Limits4> getLinoleicAcidLimits() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        return getLimitsFromValuePerDay(getLinoleicAcidAIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0.8 * total, 0.9 * total, Double.MAX_VALUE, Double.MAX_VALUE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getMagnesiumLimits() {
        // http://www.nrv.gov.au/nutrients/magnesium
        return getLimitsFromValuePerDay(getMagnesiumRDIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4LORLC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getNiacinDerivedEquivalentsLimits() {
        // http://www.nrv.gov.au/nutrients/niacin
        return getLimitsFromLimitsPerDay(getNiacinDerivedEquivalentsRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getOmega3FattyAcidsLimits() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        return getLimitsFromLimitsPerDay(getOmega3FattyAcidsAIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4(0.8 * totalLimits.getMin(), 0.9 * totalLimits.getMin(),
                        totalLimits.getMax(), (1 + DEFAULT_TOLERANCE) * totalLimits.getMax());
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getPhosphorusLimits() {
        // http://www.nrv.gov.au/nutrients/phosphorus
        return getLimitsFromLimitsPerDay(getPhosphorusRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getPotassiumLimits() {
        // http://www.nrv.gov.au/nutrients/potassium
        return getLimitsFromValuePerDay(getPotassiumAIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0.8 * total, 0.9 * total, Double.MAX_VALUE, Double.MAX_VALUE);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Limits4> getProteinLimits() {
        // http://www.nrv.gov.au/nutrients/protein
        // http://www.ausport.gov.au/ais/nutrition/factsheets/basics/protein_-_how_much
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return getLimitsFromValuePerDay(getProteinTargetPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4ORC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getRiboflavinLimits() {
        // http://www.nrv.gov.au/nutrients/riboflavin
        return getLimitsFromValuePerDay(getRiboflavinRDIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4LORLC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<Limits4> getSeleniumLimits() {
        // http://www.nrv.gov.au/nutrients/selenium
        return getLimitsFromLimitsPerDay(getSeleniumRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getSodiumLimits() {
        // http://www.nrv.gov.au/nutrients/sodium
        return getLimitsFromLimitsPerDay(getSodiumAIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4(0.8 * totalLimits.getMin(), 0.9 * totalLimits.getMin(),
                        totalLimits.getMax(), (1 + DEFAULT_TOLERANCE) * totalLimits.getMax());
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Limits4> getSugarsLimits() {
        // http://www.mydailyintake.net/nutrients/
        return getLimitsFromValuePerDay(getMaxSugarsPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4(0, 0, 0.5 * total, total);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getThiaminLimits() {
        // http://www.nrv.gov.au/nutrients/thiamin
        return getLimitsFromValuePerDay(getThiaminRDIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4LORLC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getTransFattyAcidsLimits() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.health.gov/dietaryguidelines/2010.asp
        return Optional.empty();
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getTryptophanLimits() {
        // http://en.wikipedia.org/wiki/Essential_amino_acid
        return getLimitsFromValuePerDay(getTryptophanRDIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4LORLC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<Limits4> getVitaminARetinolEquivalentsLimits() {
        // http://www.nrv.gov.au/nutrients/vitamin-a
        return getLimitsFromLimitsPerDay(getVitaminARetinolEquivalentsRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<Limits4> getVitaminB12Limits() {
        // http://www.nrv.gov.au/nutrients/vitamin-b12
        return getLimitsFromValuePerDay(getVitaminB12RDIPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4LORLC(total, DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getVitaminB6Limits() {
        // http://www.nrv.gov.au/nutrients/vitamin-b6
        return getLimitsFromLimitsPerDay(getVitaminB6RDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getVitaminCLimits() {
        // http://www.nrv.gov.au/nutrients/vitamin-c
        return getLimitsFromLimitsPerDay(getVitaminCRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getVitaminELimits() {
        // http://www.nrv.gov.au/nutrients/vitamin-e
        return getLimitsFromLimitsPerDay(getVitaminEAIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4(0.8 * totalLimits.getMin(), 0.9 * totalLimits.getMin(),
                        totalLimits.getMax(), (1 + DEFAULT_TOLERANCE) * totalLimits.getMax());
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getZincLimits() {
        // http://www.nrv.gov.au/nutrients/zinc
        return getLimitsFromLimitsPerDay(getZincRDIAndULPerDay(), new Function<Limits2, Limits4>() {
            @Override
            public Limits4 apply(final Limits2 totalLimits) {
                return limits4LOUORC(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Limits4> getMealAlcoholLimits() {
        return Optional.of(limits4UC(0.5));
    }

    /**
     * @return mg
     */
    private Optional<Limits4> getMealCaffeineLimits() {
        return Optional.of(limits4UC(10));
    }

    /**
     * @return g
     */
    private Optional<Limits4> getMealCarbohydratesLimits() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return getLimitsFromValuePerDay(getMaxCarbohydratesPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                final double upperCritical = 1.2 * total / numberOfMeals;
                return limits4(0, 0, 0.8 * upperCritical, upperCritical);
            }
        });
    }

    /**
     * @return kJ
     */
    private Optional<Limits4> getMealEnergyLimits() {
        // http://www.nrv.gov.au/dietary-energy
        return getLimitsFromValuePerDay(getEnergyDemandPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4RORC(total / numberOfMeals, 0.3, 0.6);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Limits4> getMealFatLimits() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.cdc.gov/nutrition/everyone/basics/fat/index.html?s_cid=tw_ob294
        return Optional.empty();
    }

    /**
     * @return g
     */
    private Optional<Limits4> getMealProteinLimits() {
        // http://www.nrv.gov.au/nutrients/protein
        // http://www.ausport.gov.au/ais/nutrition/factsheets/basics/protein_-_how_much
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return getLimitsFromValuePerDay(getProteinTargetPerDay(), new Function<Double, Limits4>() {
            @Override
            public Limits4 apply(final Double total) {
                return limits4RORC(total / numberOfMeals, 0.3, 0.6);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<Double> getAlphaLinolenicAcidAIPerDay() {
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

    /**
     * @return mg
     */
    private Optional<Limits2> getCalciumRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/calcium
        // TODO: Consider age, etc.
        return Optional.of(limits2(1000, 2500));
    }

    /**
     * @return g
     */
    private Optional<Double> getMaxCarbohydratesPerDay() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return Optional.of(personalDetails.getMaxCarbohydrates());
    }

    /**
     * @return g
     */
    private Optional<Double> getDietaryFibreAIPerDay() {
        // http://www.nrv.gov.au/nutrients/dietary-fibre
        // TODO: Consider age, etc.
        return Optional.of(30.0);
    }

    /**
     * @return kJ
     */
    private Optional<Double> getEnergyDemandPerDay() {
        // http://www.nrv.gov.au/dietary-energy
        // TODO: Infants, children, adolescents
        final double bmr = personalDetails.getBasalMetabolicRate();
        final double pal = personalDetails.getPhysicalActivityLevel();
        return Optional.of(bmr * pal);
    }

    /**
     * @return µg
     */
    private Optional<Double> getFolatesRDIPerDay() {
        // http://www.nrv.gov.au/nutrients/folate
        // TODO: Consider age, etc.
        return Optional.of(400.0);
    }

    /**
     * @return µg
     */
    private Optional<Double> getIodineRDIPerDay() {
        // http://www.nrv.gov.au/nutrients/iodine
        // TODO: Consider age, etc.
        return Optional.of(150.0);
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getIronRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/iron
        // TODO: Infants, children, adolescents, pregnancy, lactation
        final PersonalDetails.Gender gender = personalDetails.getGender();
        final double age = personalDetails.getAge();
        final double lowerLimit = (gender == FEMALE && age >= 19 && age < 51) ? 18 : 8;
        final double upperLimit = 45;
        return Optional.of(limits2(lowerLimit, upperLimit));
    }

    /**
     * @return g
     */
    private Optional<Double> getLinoleicAcidAIPerDay() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        // TODO: Consider age, etc.
        return Optional.of(13.0);
    }

    /**
     * @return mg
     */
    private Optional<Double> getMagnesiumRDIPerDay() {
        // http://www.nrv.gov.au/nutrients/magnesium
        // TODO: Gender, age, pregnancy, lactation
        return Optional.of(420.0);
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getNiacinDerivedEquivalentsRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/niacin
        // TODO: Consider age, etc.
        return Optional.of(limits2(16.0, 900.0));
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getOmega3FattyAcidsAIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        // TODO: Consider age, etc.
        return Optional.of(limits2(160.0, 3000.0));
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getPhosphorusRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/phosphorus
        // TODO: Consider age, etc.
        return Optional.of(limits2(1000.0, 4000.0));
    }

    /**
     * @return mg
     */
    private Optional<Double> getPotassiumAIPerDay() {
        // http://www.nrv.gov.au/nutrients/potassium
        // TODO: Consider age, etc.
        return Optional.of(3800.0);
    }

    /**
     * @return g
     */
    private Optional<Double> getProteinTargetPerDay() {
        // http://www.nrv.gov.au/nutrients/protein
        // http://www.ausport.gov.au/ais/nutrition/factsheets/basics/protein_-_how_much
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return Optional.of(personalDetails.getProteinTarget() * personalDetails.getIdealBodyWeight());
    }

    /**
     * @return mg
     */
    private Optional<Double> getRiboflavinRDIPerDay() {
        // http://www.nrv.gov.au/nutrients/riboflavin
        // TODO: Consider age, etc.
        return Optional.of(1.3);
    }

    /**
     * @return µg
     */
    private Optional<Limits2> getSeleniumRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/selenium
        // TODO: Consider age, etc.
        return Optional.of(limits2(70.0, 400.0));
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getSodiumAIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/sodium
        // http://ketodietapp.com/Blog/post/2013/04/16/Keto-flu-and-Sufficient-Intake-of-Electrolytes
        // TODO: Consider age, etc.
        final Limits2 sodiumLimits = personalDetails.getSodiumLimits().orElse(limits2(460.0, 2300.0));
        return Optional.of(sodiumLimits);
    }

    /**
     * @return g
     */
    private Optional<Double> getMaxSugarsPerDay() {
        // http://www.mydailyintake.net/nutrients/
        // TODO: Consider age, etc.
        return Optional.of(90.0);
    }

    /**
     * @return mg
     */
    private Optional<Double> getThiaminRDIPerDay() {
        // http://www.nrv.gov.au/nutrients/thiamin
        // TODO: Consider age, etc.
        return Optional.of(1.2);
    }

    /**
     * @return mg
     */
    private Optional<Double> getTryptophanRDIPerDay() {
        // http://en.wikipedia.org/wiki/Essential_amino_acid
        // TODO: Consider age, etc.
        return Optional.of(4.0 * personalDetails.getIdealBodyWeight());
    }

    /**
     * @return µg
     */
    private Optional<Limits2> getVitaminARetinolEquivalentsRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/vitamin-a
        // TODO: Consider age, etc.
        return Optional.of(limits2(900.0, 3000.0));
    }

    /**
     * @return µg
     */
    private Optional<Double> getVitaminB12RDIPerDay() {
        // http://www.nrv.gov.au/nutrients/vitamin-b12
        // TODO: Consider age, etc.
        return Optional.of(2.4);
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getVitaminB6RDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/vitamin-b6
        // TODO: Consider age, etc.
        return Optional.of(limits2(1.3, 50.0));
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getVitaminCRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/vitamin-c
        // TODO: Consider age, etc.
        return Optional.of(limits2(45.0, 1000.0));
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getVitaminEAIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/vitamin-e
        // TODO: Consider age, etc.
        return Optional.of(limits2(10.0, 300.0));
    }

    /**
     * @return mg
     */
    private Optional<Limits2> getZincRDIAndULPerDay() {
        // http://www.nrv.gov.au/nutrients/zinc
        // TODO: Consider age, etc.
        return Optional.of(limits2(14.0, 40.0));
    }
}
