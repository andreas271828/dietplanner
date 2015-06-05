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

    public static DietPlanTemplate dietPlanTemplate(final ArrayList<Pair<MealTemplate, Limits2>> mealTemplateOptions,
                                                    final int numberOfMeals) {
        final Map<MealTemplate, Integer> mealTemplates = new HashMap<MealTemplate, Integer>(numberOfMeals);
        final ArrayList<Pair<MealTemplate, Limits2>> options =
                new ArrayList<Pair<MealTemplate, Limits2>>(mealTemplateOptions);
        final Mutable<Integer> mealCount = mutable(0);
        for (final Pair<MealTemplate, Limits2> option : options) {
            final int minCount = (int) Math.ceil(option.b().getMin() * numberOfMeals);
            final int oldMealCount = mealCount.get();
            final int availableCount = numberOfMeals - oldMealCount;
            final int addCount = min(minCount, availableCount);
            if (addCount > 0) {
                mealTemplates.put(option.a(), addCount);
                mealCount.set(oldMealCount + addCount);
            }
        }
        while (mealCount.get() < numberOfMeals && !options.isEmpty()) {
            final int index = RANDOM.nextInt(options.size());
            final Pair<MealTemplate, Limits2> option = options.get(index);
            final Integer oldValue = mealTemplates.get(option.a());
            final int oldCount = oldValue == null ? 0 : oldValue;
            final int maxCount = (int) (option.b().getMax() * numberOfMeals);
            if (oldCount < maxCount) {
                mealTemplates.put(option.a(), oldCount + 1);
                mealCount.set(mealCount.get() + 1);
            } else {
                options.remove(index);
            }
        }
        return new DietPlanTemplate(mealTemplates);
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
