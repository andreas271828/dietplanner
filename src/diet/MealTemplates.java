package diet;

import evolution.Genome;
import util.Limits2;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import static diet.Meal.meal;

public abstract  class MealTemplates {
    public static ArrayList<Meal> computeMeals(final ArrayList<MealTemplate> mealTemplates,
                                               final int numberOfMeals,
                                               final Genome genome) {
        final ArrayList<Meal> meals = new ArrayList<Meal>();
        final Genome.Iterator genomeIt = genome.getIterator();
        int templateIndex = 0;
        for (int i = 0; i < numberOfMeals; ++i) {
            templateIndex = getNextIndex(mealTemplates, templateIndex, genomeIt.getNextGene());
            final MealTemplate mealTemplate = mealTemplates.get(templateIndex);
            final FoodItems ingredients = new FoodItems();
            mealTemplate.getIngredients().forEach(new BiConsumer<FoodItem, Limits2>() {
                @Override
                public void accept(final FoodItem foodItem, final Limits2 limits) {
                    final double minAmount = limits.getMin();
                    final double maxAmount = limits.getMax();
                    final double amount = minAmount + genomeIt.getNextGene() * (maxAmount - minAmount);
                    final double roundedAmount = foodItem.roundToPortions(amount);
                    ingredients.set(foodItem, roundedAmount);
                }
            });
            meals.add(meal(mealTemplate, ingredients));
        }

        return meals;
    }

    /**
     * @param start    Start index
     * @param fraction Fraction (between 0 and 1, both inclusive) of maximum distance
     * @return New index
     */
    private static int getNextIndex(final ArrayList<MealTemplate> mealTemplates, int start, double fraction) {
        final int size = mealTemplates.size();
        final int maxDistance = size - 1;
        final int distance = (int) Math.round(fraction * maxDistance);
        return (start + distance) % size;
    }
}
