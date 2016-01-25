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

import util.LazyValue;

public class Meal {
    private final MealTemplate template;
    private final FoodItems ingredients;
    private final LazyValue<FoodProperties> properties;
    private final LazyValue<Double> costs;

    public static Meal meal(final MealTemplate template, final FoodItems ingredients) {
        return new Meal(template, ingredients);
    }

    private Meal(final MealTemplate template, final FoodItems ingredients) {
        this.template = template;
        this.ingredients = ingredients;

        properties = new LazyValue<FoodProperties>() {
            @Override
            protected FoodProperties compute() {
                return ingredients.getProperties();
            }
        };

        costs = new LazyValue<Double>() {
            @Override
            protected Double compute() {
                return ingredients.getCosts();
            }
        };
    }

    public MealTemplate getTemplate() {
        return template;
    }

    public String getName() {
        return template.getName();
    }

    public FoodItems getIngredients() {
        return ingredients;
    }

    public double getAmount(final FoodItem foodItem) {
        return ingredients.get(foodItem);
    }

    public FoodProperties getProperties() {
        return properties.get();
    }

    public double getCosts() {
        return costs.get();
    }

    public Meal getWithChange(final FoodItem ingredient, final double newAmount) {
        // TODO: Lazy values can be set using a new private constructor - the modifications are easy to calculate here.
        final FoodItems ingredients = getIngredients().getWithChange(ingredient, newAmount);
        return new Meal(getTemplate(), ingredients);
    }

    @Override
    public String toString() {
        return getName() + ": " + getIngredients().toString();
    }
}
