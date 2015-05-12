package diet;

import util.Limits2;
import util.Pair;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

import static diet.FoodProperty.FAT_ENERGY;
import static diet.PersonalDetails.Gender.FEMALE;
import static diet.PersonalDetails.Gender.MALE;
import static diet.ScoreParams.*;
import static util.Limits2.limits2;
import static util.Pair.pair;

public class Requirements {
    private static final double DEFAULT_WEIGHT = 1.0;
    private static final double DEFAULT_MEAL_PROPERTY_WEIGHT = 0.2;
    private static final double CARBOHYDRATES_WEIGHT = 1.0;
    private static final double COSTS_WEIGHT = 10.0;
    private static final double FAT_WEIGHT = 10.0;
    private static final double ENERGY_WEIGHT = 5.0;
    private static final double PROTEIN_WEIGHT = 10.0;

    private static final double DEFAULT_TOLERANCE = 0.05;

    private final PersonalDetails personalDetails;
    private final double days;
    private final int numberOfMeals;

    private final EnumMap<Requirement, Optional<ScoreParams>> requirements;

    final ArrayList<Pair<FoodItem, Double>> lowerLimits;
    final ArrayList<Pair<FoodItem, Double>> upperLimits;

    public Requirements(final PersonalDetails personalDetails, final double days, final int numberOfMeals) {
        this.personalDetails = personalDetails;
        this.days = days;
        this.numberOfMeals = numberOfMeals;

        requirements = new EnumMap<Requirement, Optional<ScoreParams>>(Requirement.class);
        requirements.put(Requirement.ALPHA_LINOLENIC_ACID, getAlphaLinolenicAcidParams());
        requirements.put(Requirement.CALCIUM, getCalciumParams());
        requirements.put(Requirement.CARBOHYDRATES, getCarbohydratesParams());
        requirements.put(Requirement.CHOLESTEROL, getCholesterolParams());
        requirements.put(Requirement.COSTS, getCostsParams());
        requirements.put(Requirement.DIETARY_FIBRE, getDietaryFibreParams());
        requirements.put(Requirement.ENERGY, getEnergyParams());
        requirements.put(Requirement.FAT, getFatParams());
        requirements.put(Requirement.FOLATES, getFolatesParams());
        requirements.put(Requirement.IODINE, getIodineParams());
        requirements.put(Requirement.IRON, getIronParams());
        requirements.put(Requirement.LINOLEIC_ACID, getLinoleicAcidParams());
        requirements.put(Requirement.MAGNESIUM, getMagnesiumParams());
        requirements.put(Requirement.NIACIN_DERIVED_EQUIVALENTS, getNiacinDerivedEquivalentsParams());
        requirements.put(Requirement.OMEGA_3_FATTY_ACIDS, getOmega3FattyAcidsParams());
        requirements.put(Requirement.PHOSPHORUS, getPhosphorusParams());
        requirements.put(Requirement.POTASSIUM, getPotassiumParams());
        requirements.put(Requirement.PROTEIN, getProteinParams());
        requirements.put(Requirement.RIBOFLAVIN, getRiboflavinParams());
        requirements.put(Requirement.SELENIUM, getSeleniumParams());
        requirements.put(Requirement.SODIUM, getSodiumParams());
        requirements.put(Requirement.SUGARS, getSugarsParams());
        requirements.put(Requirement.THIAMIN, getThiaminParams());
        requirements.put(Requirement.TRANS_FATTY_ACIDS, getTransFattyAcidsParams());
        requirements.put(Requirement.TRYPTOPHAN, getTryptophanParams());
        requirements.put(Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, getVitaminARetinolEquivalentsParams());
        requirements.put(Requirement.VITAMIN_B12, getVitaminB12Params());
        requirements.put(Requirement.VITAMIN_B6, getVitaminB6Params());
        requirements.put(Requirement.VITAMIN_C, getVitaminCParams());
        requirements.put(Requirement.VITAMIN_E, getVitaminEParams());
        requirements.put(Requirement.ZINC, getZincParams());

        requirements.put(Requirement.MEAL_ALCOHOL, getMealAlcoholParams());
        requirements.put(Requirement.MEAL_CAFFEINE, getMealCaffeineParams());
        requirements.put(Requirement.MEAL_CARBOHYDRATES, getMealCarbohydratesParams());
        requirements.put(Requirement.MEAL_ENERGY, getMealEnergyParams());
        requirements.put(Requirement.MEAL_FAT, getMealFatParams());
        requirements.put(Requirement.MEAL_PROTEIN, getMealProteinParams());

        lowerLimits = new ArrayList<Pair<FoodItem, Double>>();
        final ArrayList<Pair<FoodItem, Double>> lowerLimitsPerDay = personalDetails.getLowerLimits();
        for (final Pair<FoodItem, Double> lowerLimitPerDay : lowerLimitsPerDay) {
            lowerLimits.add(pair(lowerLimitPerDay.a(), lowerLimitPerDay.b() * days));
        }

        upperLimits = new ArrayList<Pair<FoodItem, Double>>();
        final ArrayList<Pair<FoodItem, Double>> upperLimitsPerDay = personalDetails.getUpperLimits();
        for (final Pair<FoodItem, Double> upperLimitPerDay : upperLimitsPerDay) {
            upperLimits.add(pair(upperLimitPerDay.a(), upperLimitPerDay.b() * days));
        }
    }

    public int getNumberOfMeals() {
        return numberOfMeals;
    }

    public Optional<ScoreParams> getParams(final Requirement requirement) {
        return requirements.containsKey(requirement) ? requirements.get(requirement) : Optional.<ScoreParams>empty();
    }

    public ArrayList<Pair<FoodItem, Double>> getLowerLimits() {
        return lowerLimits;
    }

    public ArrayList<Pair<FoodItem, Double>> getUpperLimits() {
        return upperLimits;
    }

    private Optional<ScoreParams> getParamsFromValuePerDay(final Optional<Double> maybeValuePerDay,
                                                           final Function<Double, ScoreParams> getParams) {
        return maybeValuePerDay.map(new Function<Double, Double>() {
            @Override
            public Double apply(final Double valuePerDay) {
                return valuePerDay * days;
            }
        }).map(getParams);
    }

    private Optional<ScoreParams> getParamsFromLimitsPerDay(final Optional<Limits2> maybeLimitsPerDay,
                                                            final Function<Limits2, ScoreParams> getParams) {
        return maybeLimitsPerDay.map(new Function<Limits2, Limits2>() {
            @Override
            public Limits2 apply(final Limits2 limitsPerDay) {
                return limits2(limitsPerDay.getMin() * days, limitsPerDay.getMax() * days);
            }
        }).map(getParams);
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getAlphaLinolenicAcidParams() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        return getParamsFromValuePerDay(getAlphaLinolenicAcidAIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsL(0.8 * total, 0.9 * total, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getCalciumParams() {
        // http://www.nrv.gov.au/nutrients/calcium
        return getParamsFromLimitsPerDay(getCalciumRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getCarbohydratesParams() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return getParamsFromValuePerDay(getMaxCarbohydratesPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsU(0.95 * total, total, CARBOHYDRATES_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getCholesterolParams() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.health.gov/dietaryguidelines/2010.asp
        return Optional.empty();
    }

    /**
     * @return AUD
     */
    private Optional<ScoreParams> getCostsParams() {
        return Optional.of(scoreParamsUT(15.0 * days, 0.2, COSTS_WEIGHT));
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getDietaryFibreParams() {
        // http://www.nrv.gov.au/nutrients/dietary-fibre
        return getParamsFromValuePerDay(getDietaryFibreAIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsL(0.8 * total, 0.9 * total, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return kJ
     */
    private Optional<ScoreParams> getEnergyParams() {
        // http://www.nrv.gov.au/dietary-energy
        return getParamsFromValuePerDay(getEnergyDemandPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsT(total, total, DEFAULT_TOLERANCE, ENERGY_WEIGHT);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getFatParams() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.cdc.gov/nutrition/everyone/basics/fat/index.html?s_cid=tw_ob294
        return getParamsFromValuePerDay(getMaxFatPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsU(0.95 * total, total, FAT_WEIGHT);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<ScoreParams> getFolatesParams() {
        // http://www.nrv.gov.au/nutrients/folate
        return getParamsFromValuePerDay(getFolatesRDIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsLT(total, DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<ScoreParams> getIodineParams() {
        // http://www.nrv.gov.au/nutrients/iodine
        return getParamsFromValuePerDay(getIodineRDIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsLT(total, DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getIronParams() {
        // http://www.nrv.gov.au/nutrients/iron
        return getParamsFromLimitsPerDay(getIronRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getLinoleicAcidParams() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        return getParamsFromValuePerDay(getLinoleicAcidAIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsL(0.8 * total, 0.9 * total, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getMagnesiumParams() {
        // http://www.nrv.gov.au/nutrients/magnesium
        return getParamsFromValuePerDay(getMagnesiumRDIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsLT(total, DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getNiacinDerivedEquivalentsParams() {
        // http://www.nrv.gov.au/nutrients/niacin
        return getParamsFromLimitsPerDay(getNiacinDerivedEquivalentsRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getOmega3FattyAcidsParams() {
        // http://www.nrv.gov.au/nutrients/fats-total-fat-fatty-acids
        return getParamsFromLimitsPerDay(getOmega3FattyAcidsAIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                final double lowerLimit = totalLimits.getMin();
                final double upperLimit = totalLimits.getMax();
                return scoreParams(0.8 * lowerLimit, 0.9 * lowerLimit, upperLimit,
                        upperCritical(upperLimit, DEFAULT_TOLERANCE), DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getPhosphorusParams() {
        // http://www.nrv.gov.au/nutrients/phosphorus
        return getParamsFromLimitsPerDay(getPhosphorusRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getPotassiumParams() {
        // http://www.nrv.gov.au/nutrients/potassium
        return getParamsFromValuePerDay(getPotassiumAIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsL(0.8 * total, 0.9 * total, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getProteinParams() {
        // http://www.nrv.gov.au/nutrients/protein
        // http://www.ausport.gov.au/ais/nutrition/factsheets/basics/protein_-_how_much
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return getParamsFromLimitsPerDay(getProteinLimitsPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, PROTEIN_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getRiboflavinParams() {
        // http://www.nrv.gov.au/nutrients/riboflavin
        return getParamsFromValuePerDay(getRiboflavinRDIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsLT(total, DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<ScoreParams> getSeleniumParams() {
        // http://www.nrv.gov.au/nutrients/selenium
        return getParamsFromLimitsPerDay(getSeleniumRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getSodiumParams() {
        // http://www.nrv.gov.au/nutrients/sodium
        return getParamsFromLimitsPerDay(getSodiumAIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                final double lowerLimit = totalLimits.getMin();
                final double upperLimit = totalLimits.getMax();
                return scoreParams(0.8 * lowerLimit, 0.9 * lowerLimit, upperLimit,
                        upperCritical(upperLimit, DEFAULT_TOLERANCE), DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getSugarsParams() {
        // http://www.mydailyintake.net/nutrients/
        // http://www.nal.usda.gov/fnic/DRI/DRI_Energy/265-338.pdf
        return getParamsFromValuePerDay(getMaxSugarsPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsU(0.0, total, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getThiaminParams() {
        // http://www.nrv.gov.au/nutrients/thiamin
        return getParamsFromValuePerDay(getThiaminRDIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsLT(total, DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getTransFattyAcidsParams() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.health.gov/dietaryguidelines/2010.asp
        return Optional.empty();
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getTryptophanParams() {
        // http://en.wikipedia.org/wiki/Essential_amino_acid
        return getParamsFromValuePerDay(getTryptophanRDIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsLT(total, DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<ScoreParams> getVitaminARetinolEquivalentsParams() {
        // http://www.nrv.gov.au/nutrients/vitamin-a
        return getParamsFromLimitsPerDay(getVitaminARetinolEquivalentsRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return µg
     */
    private Optional<ScoreParams> getVitaminB12Params() {
        // http://www.nrv.gov.au/nutrients/vitamin-b12
        return getParamsFromValuePerDay(getVitaminB12RDIPerDay(), new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double total) {
                return scoreParamsLT(total, DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getVitaminB6Params() {
        // http://www.nrv.gov.au/nutrients/vitamin-b6
        return getParamsFromLimitsPerDay(getVitaminB6RDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getVitaminCParams() {
        // http://www.nrv.gov.au/nutrients/vitamin-c
        return getParamsFromLimitsPerDay(getVitaminCRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getVitaminEParams() {
        // http://www.nrv.gov.au/nutrients/vitamin-e
        return getParamsFromLimitsPerDay(getVitaminEAIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                final double lowerLimit = totalLimits.getMin();
                final double upperLimit = totalLimits.getMax();
                return scoreParams(0.8 * lowerLimit, 0.9 * lowerLimit, upperLimit,
                        upperCritical(upperLimit, DEFAULT_TOLERANCE), DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getZincParams() {
        // http://www.nrv.gov.au/nutrients/zinc
        return getParamsFromLimitsPerDay(getZincRDIAndULPerDay(), new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 totalLimits) {
                return scoreParamsT(totalLimits.getMin(), totalLimits.getMax(), DEFAULT_TOLERANCE, DEFAULT_WEIGHT);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getMealAlcoholParams() {
        return Optional.of(scoreParamsU(0.0, 0.5, DEFAULT_MEAL_PROPERTY_WEIGHT));
    }

    /**
     * @return mg
     */
    private Optional<ScoreParams> getMealCaffeineParams() {
        return Optional.of(scoreParamsU(0.0, 10.0, DEFAULT_MEAL_PROPERTY_WEIGHT));
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getMealCarbohydratesParams() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return getMaxCarbohydratesPerDay().map(new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double maxPerDay) {
                return scoreParamsUT(0.6 * maxPerDay, DEFAULT_TOLERANCE, DEFAULT_MEAL_PROPERTY_WEIGHT);
            }
        });
    }

    /**
     * @return kJ
     */
    private Optional<ScoreParams> getMealEnergyParams() {
        // http://www.nrv.gov.au/dietary-energy
        // Limit energy intake per meal to energy demand per day.
        return getEnergyDemandPerDay().map(new Function<Double, ScoreParams>() {
            @Override
            public ScoreParams apply(final Double targetPerDay) {
                return scoreParamsT(0.2 * targetPerDay, 0.4 * targetPerDay, DEFAULT_TOLERANCE, DEFAULT_MEAL_PROPERTY_WEIGHT);
            }
        });
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getMealFatParams() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.cdc.gov/nutrition/everyone/basics/fat/index.html?s_cid=tw_ob294
        return Optional.empty();
    }

    /**
     * @return g
     */
    private Optional<ScoreParams> getMealProteinParams() {
        // http://www.nrv.gov.au/nutrients/protein
        // http://www.ausport.gov.au/ais/nutrition/factsheets/basics/protein_-_how_much
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // Limit protein per meal to protein target per day.
        return getProteinLimitsPerDay().map(new Function<Limits2, ScoreParams>() {
            @Override
            public ScoreParams apply(final Limits2 limitsPerDay) {
                return scoreParamsUT(limitsPerDay.getMax(), DEFAULT_TOLERANCE, DEFAULT_MEAL_PROPERTY_WEIGHT);
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
        return personalDetails.getMaxCarbohydrates();
    }

    /**
     * @return g
     */
    private Optional<Double> getMaxFatPerDay() {
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        // http://www.cdc.gov/nutrition/everyone/basics/fat/index.html?s_cid=tw_ob294
        return personalDetails.getMaxFat().flatMap(new Function<Double, Optional<Double>>() {
            @Override
            public Optional<Double> apply(final Double maxFatPercent) {
                return getEnergyDemandPerDay().map(new Function<Double, Double>() {
                    @Override
                    public Double apply(final Double energyDemand) {
                        final double maxFatEnergy = energyDemand * maxFatPercent / 100.0;
                        return maxFatEnergy / FAT_ENERGY;
                    }
                });
            }
        });
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
    private Optional<Limits2> getProteinLimitsPerDay() {
        // http://www.nrv.gov.au/nutrients/protein
        // http://www.ausport.gov.au/ais/nutrition/factsheets/basics/protein_-_how_much
        // Jimmy Moore (2014) Keto Clarity: Your Definitive Guide to the Benefits of a Low-Carb, High-Fat Diet.
        return Optional.of(personalDetails.getProteinLimits().scale(personalDetails.getIdealBodyWeight()));
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
        return Optional.of(personalDetails.getMaxSugars());
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
