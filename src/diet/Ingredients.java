/**********************************************************************
 DietPlanner

 Copyright (C) 2015-2016 Andreas Huemer

 This file is part of DietPlanner.

 DietPlanner is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at
 your option) any later version.

 DietPlanner is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/
package diet;

import util.Limits2;

import java.util.EnumMap;
import java.util.function.BiConsumer;

import static java.lang.Math.ceil;
import static util.Limits2.limits2;

public class Ingredients {
    private final EnumMap<FoodItem, Limits2> ingredients = new EnumMap<FoodItem, Limits2>(FoodItem.class);

    public void add(final FoodItem foodItem, final double minAmount, final double maxAmount) {
        ingredients.put(foodItem, limits2(minAmount, maxAmount));
    }

    /**
     * @param foodItem  Food item
     * @param minWeight Minimum weight in g
     * @param maxWeight Maximum weight in g
     */
    public void addByWeight(final FoodItem foodItem, final double minWeight, final double maxWeight) {
        add(foodItem, foodItem.weightToAmount(minWeight), foodItem.weightToAmount(maxWeight));
    }

    public void addAll(final Ingredients ingredients) {
        this.ingredients.putAll(ingredients.ingredients);
    }

    public int getCount() {
        return ingredients.size();
    }

    public void forEach(final BiConsumer<FoodItem, Limits2> action) {
        ingredients.forEach(action);
    }

    public double getMinAmount(final FoodItem ingredient) {
        final Limits2 limits = ingredients.get(ingredient);
        return limits == null ? 0.0 : limits.getMin();
    }

    public double getMaxAmount(final FoodItem ingredient) {
        final Limits2 limits = ingredients.get(ingredient);
        return limits == null ? 0.0 : limits.getMax();
    }

    public FoodItems getMinFoodItems() {
        final FoodItems foodItems = new FoodItems();
        forEach(new BiConsumer<FoodItem, Limits2>() {
            @Override
            public void accept(final FoodItem foodItem, final Limits2 limits) {
                final int portionsPerItem = foodItem.amountToPortions(1.0);
                final int minPortions = (int) ceil(limits.getMin() * portionsPerItem);
                foodItems.set(foodItem, foodItem.portionsToAmount(minPortions));
            }
        });
        return foodItems;
    }
}
