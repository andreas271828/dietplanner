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
import util.Limits2;
import util.Mutable;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static diet.DietPlan.dietPlan;
import static java.lang.Math.min;
import static util.Global.RANDOM;
import static util.Mutable.mutable;
import static util.Pair.pair;

public class DietPlanTemplate {
    private final LazyValue<DietPlan> minimalDietPlan;
    private final LazyValue<ArrayList<Pair<Integer, FoodItem>>> variableIngredients;

    public static DietPlanTemplate dietPlanTemplate(final ArrayList<Pair<ArrayList<MealTemplate>, Limits2>> mealTemplateOptions,
                                                    final int numberOfMeals) {
        final Map<MealTemplate, Integer> mealTemplates = new HashMap<MealTemplate, Integer>(numberOfMeals);
        final ArrayList<Pair<ArrayList<MealTemplate>, Limits2>> options =
                new ArrayList<Pair<ArrayList<MealTemplate>, Limits2>>(mealTemplateOptions);
        final Mutable<Integer> mealCount = mutable(0);
        for (final Pair<ArrayList<MealTemplate>, Limits2> option : options) {
            final int minCount = (int) Math.ceil(option.b().getMin() * numberOfMeals);
            final int addTo = min(mealCount.get() + minCount, numberOfMeals);
            while (mealCount.get() < addTo) {
                final int maxAdd = addTo - mealCount.get();
                addMealTemplates(mealTemplates, mealCount, option, maxAdd);
            }
        }
        while (mealCount.get() < numberOfMeals && !options.isEmpty()) {
            final int optionsIndex = RANDOM.nextInt(options.size());
            final Pair<ArrayList<MealTemplate>, Limits2> option = options.get(optionsIndex);
            final Mutable<Integer> oldCount = mutable(0);
            for (final MealTemplate mealTemplate : option.a()) {
                final Integer val = mealTemplates.get(mealTemplate);
                if (val != null) {
                    oldCount.set(oldCount.get() + val);
                }
            }
            final int maxCount = (int) (option.b().getMax() * numberOfMeals);
            if (oldCount.get() < maxCount) {
                final int maxAdd = min(maxCount - oldCount.get(), numberOfMeals - mealCount.get());
                addMealTemplates(mealTemplates, mealCount, option, maxAdd);
            } else {
                options.remove(optionsIndex);
            }
        }
        return new DietPlanTemplate(mealTemplates);
    }

    private static void addMealTemplates(final Map<MealTemplate, Integer> mealTemplates,
                                         final Mutable<Integer> mealCount,
                                         final Pair<ArrayList<MealTemplate>, Limits2> option,
                                         final int maxAdd) {
        final int index = RANDOM.nextInt(option.a().size());
        final MealTemplate mealTemplate = option.a().get(index);
        final Integer val = mealTemplates.get(mealTemplate);
        final int add = RANDOM.nextInt(maxAdd) + 1;
        mealTemplates.put(mealTemplate, (val == null ? 0 : val) + add);
        mealCount.set(mealCount.get() + add);
    }

    private DietPlanTemplate(final Map<MealTemplate, Integer> mealTemplates) {
        final ArrayList<MealTemplate> mealTemplateList = new ArrayList<MealTemplate>();
        for (final Map.Entry<MealTemplate, Integer> mealTemplate : mealTemplates.entrySet()) {
            for (int i = 0; i < mealTemplate.getValue(); ++i) {
                mealTemplateList.add(mealTemplate.getKey());
            }
        }

        minimalDietPlan = new LazyValue<DietPlan>() {
            @Override
            protected DietPlan compute() {
                final ArrayList<Meal> meals = new ArrayList<Meal>();
                for (final MealTemplate mealTemplate : mealTemplateList) {
                    meals.add(mealTemplate.getMinimalMeal());
                }
                return dietPlan(DietPlanTemplate.this, meals);
            }
        };

        variableIngredients = new LazyValue<ArrayList<Pair<Integer, FoodItem>>>() {
            @Override
            protected ArrayList<Pair<Integer, FoodItem>> compute() {
                final ArrayList<Pair<Integer, FoodItem>> variableIngredients = new ArrayList<Pair<Integer, FoodItem>>();
                final int numberOfMeals = mealTemplateList.size();
                for (int i = 0; i < numberOfMeals; ++i) {
                    final int mealIndex = i;
                    final Ingredients ingredients = mealTemplateList.get(mealIndex).getIngredients();
                    ingredients.forEach(new BiConsumer<FoodItem, Limits2>() {
                        @Override
                        public void accept(final FoodItem foodItem, final Limits2 limits) {
                            variableIngredients.add(pair(mealIndex, foodItem));
                        }
                    });
                }
                return variableIngredients;
            }
        };
    }

    public DietPlan getMinimalDietPlan() {
        return minimalDietPlan.get();
    }

    public ArrayList<Pair<Integer, FoodItem>> getVariableIngredients() {
        return variableIngredients.get();
    }
}
