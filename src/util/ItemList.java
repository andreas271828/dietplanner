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
package util;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ItemList<K extends Enum<K>> {
    private final EnumMap<K, Double> items;

    public ItemList(final Class<K> itemType) {
        this.items = new EnumMap<K, Double>(itemType);
    }

    public ItemList(final ItemList<K> itemList, final K item, double newAmount) {
        items = itemList.items.clone();
        set(item, newAmount);
    }

    public double get(final K item) {
        final Double amount = items.get(item);
        return amount == null ? 0.0 : amount;
    }

    public void set(final K item, final double amount) {
        if (amount <= 0.0) {
            items.remove(item);
        } else {
            items.put(item, amount);
        }
    }

    public void add(final ItemList<K> toAdd) {
        for (final Map.Entry<K, Double> entry : toAdd.items.entrySet()) {
            final K item = entry.getKey();
            set(item, get(item) + entry.getValue());
        }
    }

    public void addScaled(final ItemList<K> toAdd, final double factor) {
        for (final Map.Entry<K, Double> entry : toAdd.items.entrySet()) {
            final K item = entry.getKey();
            set(item, get(item) + entry.getValue() * factor);
        }
    }

    public void forEach(final BiConsumer<K, Double> action) {
        for (final Map.Entry<K, Double> entry : items.entrySet()) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
