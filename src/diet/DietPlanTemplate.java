package diet;

import util.LazyValue;
import util.Limits2;
import util.Pair;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import static diet.DietPlan.dietPlan;
import static util.Global.RANDOM;
import static util.Pair.pair;

public class DietPlanTemplate {
    private final LazyValue<DietPlan> minimalDietPlan;
    private final LazyValue<ArrayList<Pair<Integer, FoodItem>>> variableIngredients;

    public static DietPlanTemplate dietPlanTemplate(final ArrayList<MealTemplate> mealTemplateOptions,
                                                    final int numberOfMeals) {
        final ArrayList<MealTemplate> mealTemplates = new ArrayList<MealTemplate>(numberOfMeals);
        for (int i = 0; i < numberOfMeals; ++i) {
            final int mealTemplateIndex = RANDOM.nextInt(mealTemplateOptions.size());
            mealTemplates.add(mealTemplateOptions.get(mealTemplateIndex));
        }
        return new DietPlanTemplate(mealTemplates);
    }

    private DietPlanTemplate(final ArrayList<MealTemplate> mealTemplates) {
        minimalDietPlan = new LazyValue<DietPlan>() {
            @Override
            protected DietPlan compute() {
                final ArrayList<Meal> meals = new ArrayList<Meal>();
                for (final MealTemplate mealTemplate : mealTemplates) {
                    meals.add(mealTemplate.getMinimalMeal());
                }
                return dietPlan(DietPlanTemplate.this, meals);
            }
        };

        variableIngredients = new LazyValue<ArrayList<Pair<Integer, FoodItem>>>() {
            @Override
            protected ArrayList<Pair<Integer, FoodItem>> compute() {
                final ArrayList<Pair<Integer, FoodItem>> variableIngredients = new ArrayList<Pair<Integer, FoodItem>>();
                final int numberOfMeals = mealTemplates.size();
                for (int i = 0; i < numberOfMeals; ++i) {
                    final int mealIndex = i;
                    final Ingredients ingredients = mealTemplates.get(mealIndex).getIngredients();
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
