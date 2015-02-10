package diet;

import evolution.Genome;
import util.Limits2;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class MealTemplates {
    private final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>();

    public boolean add(MealTemplate mealTemplate) {
        return mealTemplates.add(mealTemplate);
    }

    public ArrayList<Meal> computeMeals(final int numberOfMeals, final Genome genome) {
        final ArrayList<Meal> meals = new ArrayList<Meal>();
        final Genome.Iterator genomeIt = genome.getIterator();
        int templateIndex = 0;
        for (int i = 0; i < numberOfMeals; ++i) {
            templateIndex = getNextIndex(templateIndex, genomeIt.getNextGene());
            final MealTemplate mealTemplate = mealTemplates.get(templateIndex);
            final FoodItems ingredients = new FoodItems();
            mealTemplate.getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double minAmount = limits.getMin();
                    final double maxAmount = limits.getMax();
                    final double amount = minAmount + genomeIt.getNextGene() * (maxAmount - minAmount);
                    final double roundedAmount = foodItem.roundToPortions(amount);
                    if (amount > 1e-6) {
                        ingredients.set(foodItem, roundedAmount);
                    }
                }
            });
            meals.add(new Meal(mealTemplate.getName(), ingredients));
        }

        return meals;
    }

    /**
     * @param start    Start index
     * @param fraction Fraction (between 0 and 1, both inclusive) of maximum distance
     * @return New index
     */
    private int getNextIndex(int start, double fraction) {
        final int size = mealTemplates.size();
        final int maxDistance = size - 1;
        final int distance = (int) Math.round(fraction * maxDistance);
        return (start + distance) % size;
    }
}
