package diet;

import util.LazyValue;
import util.Limits4;

import static util.Limits4.*;

public class Requirements {
    private final int numberOfMeals;
    private final LazyValue<Limits4> energyLimits; // kJ
    private final LazyValue<Limits4> mealAlcoholLimits; // g
    private final LazyValue<Limits4> mealEnergyLimits; // kJ

    public Requirements(final PersonalDetails personalDetails, final double days, final int numberOfMeals) {
        this.numberOfMeals = numberOfMeals;

        final double defaultTolerance = 0.05;
        final double energyDemand = getEnergyDemandPerDay(personalDetails) * days;

        energyLimits = new LazyValue<Limits4>() {
            @Override
            protected Limits4 compute() {
                return limits4ORC(energyDemand, defaultTolerance);
            }
        };

        mealAlcoholLimits = new LazyValue<Limits4>() {
            @Override
            protected Limits4 compute() {
                return limits4UC(0.5);
            }
        };

        mealEnergyLimits = new LazyValue<Limits4>() {
            @Override
            protected Limits4 compute() {
                return limits4RORC(energyDemand / numberOfMeals, 0.3, 0.6);
            }
        };
    }

    public int getNumberOfMeals() {
        return numberOfMeals;
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

    private static double getEnergyDemandPerDay(final PersonalDetails personalDetails) {
        // http://www.nrv.gov.au/dietary-energy
        // TODO: Infants, children, adolescents
        final double bmr = personalDetails.getBasalMetabolicRate();
        final double pal = personalDetails.getPhysicalActivityLevel();
        return bmr * pal;
    }
}
