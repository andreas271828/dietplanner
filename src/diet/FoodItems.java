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

import util.ItemList;
import util.Mutable;

import java.util.function.BiConsumer;

import static util.Mutable.mutable;

public class FoodItems extends ItemList<FoodItem> {
    public FoodItems() {
        super(FoodItem.class);
    }

    private FoodItems(final FoodItems foodItems, final FoodItem foodItem, final double newAmount) {
        super(foodItems, foodItem, newAmount);
    }

    public FoodProperties getProperties() {
        final FoodProperties properties = new FoodProperties();
        forEach(new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(final FoodItem foodItem, final Double amount) {
                properties.addScaled(foodItem.getProperties(), amount);
            }
        });
        return properties;
    }

    public double getCosts() {
        final Mutable<Double> costs = mutable(0.0);
        forEach(new BiConsumer<FoodItem, Double>() {
            @Override
            public void accept(final FoodItem foodItem, final Double amount) {
                costs.set(costs.get() + foodItem.getPrice() * amount);
            }
        });
        return costs.get();
    }

    public FoodItems getWithChange(final FoodItem foodItem, final double newAmount) {
        return new FoodItems(this, foodItem, newAmount);
    }
}
