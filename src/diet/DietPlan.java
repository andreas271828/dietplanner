package diet;

import util.LazyValue;
import util.Limits2;
import util.Pair;

import java.util.ArrayList;

import static diet.Meal.meal;
import static diet.Meal.randomMeal;
import static util.Global.RANDOM;
import static util.Global.nextRandomDoubleInclOne;

public class DietPlan {
    private final ArrayList<Meal> meals;
    private final LazyValue<FoodItems> foodItems;
    private final LazyValue<FoodProperties> properties;
    private final LazyValue<Double> costs;

    public static DietPlan dietPlan(final ArrayList<Meal> meals) {
        return new DietPlan(meals);
    }

    private DietPlan(final ArrayList<Meal> meals) {
        this.meals = meals;

        foodItems = new LazyValue<FoodItems>() {
            @Override
            protected FoodItems compute() {
                final FoodItems foodItems = new FoodItems();
                final ArrayList<Meal> meals = getMeals();
                for (final Meal meal : meals) {
                    foodItems.add(meal.getIngredients());
                }
                return foodItems;
            }
        };

        properties = new LazyValue<FoodProperties>() {
            @Override
            protected FoodProperties compute() {
                final FoodProperties properties = new FoodProperties();
                final ArrayList<Meal> meals = getMeals();
                for (final Meal meal : meals) {
                    properties.add(meal.getProperties());
                }
                return properties;
            }
        };

        costs = new LazyValue<Double>() {
            @Override
            protected Double compute() {
                double costs = 0;
                final ArrayList<Meal> meals = getMeals();
                for (final Meal meal : meals) {
                    costs += meal.getCosts();
                }
                return costs;
            }
        };
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public int getNumberOfMeals() {
        return meals.size();
    }

    public Meal getMeal(int mealIndex) {
        return getMeals().get(mealIndex);
    }

    public FoodItems getFoodItems() {
        return foodItems.get();
    }

    public FoodProperties getProperties() {
        return properties.get();
    }

    public double getCosts() {
        return costs.get();
    }

    public DietPlan mate(final DietPlan partner,
                         final double mealMutationRate,
                         final ArrayList<MealTemplate> mealTemplates,
                         final double ingredientMutationRate) {
        // TODO: Refactor - this code has some reoccurring patterns
        final ArrayList<Meal> meals1 = getMeals();
        final ArrayList<Meal> meals2 = partner.getMeals();
        final int numberOfMeals1 = meals1.size();
        final int numberOfMeals2 = meals2.size();
        final int minNumberOfMeals = Math.min(numberOfMeals1, numberOfMeals2);
        final int crossoverMeal = RANDOM.nextInt(minNumberOfMeals + 1);
        final ArrayList<Meal> meals3 = new ArrayList<Meal>();
        for (int i = 0; i < crossoverMeal; ++i) {
            if (RANDOM.nextDouble() < mealMutationRate) {
                meals3.add(randomMeal(mealTemplates.get(RANDOM.nextInt(mealTemplates.size()))));
            } else {
                final Meal meal = meals1.get(i);
                final MealTemplate mealTemplate = meal.getTemplate();
                final FoodItems ingredients = new FoodItems();
                final ArrayList<Pair<FoodItem, Limits2>> foodList = mealTemplate.getIngredients().getList();
                boolean mutatedMeal = false;
                for (final Pair<FoodItem, Limits2> food : foodList) {
                    final FoodItem foodItem = food.a();
                    if (RANDOM.nextDouble() < ingredientMutationRate) {
                        final Limits2 limits = food.b();
                        // TODO: This is a copy of what happens in Meal.randomMeal() and should be extracted in a separate method (and called here and in Meal.randomMeal())
                        final double minAmount = limits.getMin();
                        final double maxAmount = limits.getMax();
                        final double relAmount = nextRandomDoubleInclOne();
                        final double amount = minAmount + relAmount * (maxAmount - minAmount);
                        final double roundedAmount = foodItem.roundToPortions(amount);
                        if (roundedAmount > 1e-6) {
                            ingredients.set(foodItem, roundedAmount);
                        }
                        mutatedMeal = true;
                    } else {
                        ingredients.set(foodItem, meal.getAmount(foodItem));
                    }
                }
                if (mutatedMeal) {
                    meals3.add(meal(mealTemplate, ingredients));
                } else {
                    meals3.add(meal);
                }
            }
        }
        if (crossoverMeal < numberOfMeals2) {
            if (RANDOM.nextDouble() < mealMutationRate) {
                meals3.add(randomMeal(mealTemplates.get(RANDOM.nextInt(mealTemplates.size()))));
            } else if (crossoverMeal < numberOfMeals1) {
                final Meal meal1 = meals1.get(crossoverMeal);
                final Meal meal2 = meals2.get(crossoverMeal);
                final MealTemplate mealTemplate = meal1.getTemplate();
                if (mealTemplate == meal2.getTemplate()) {
                    final FoodItems ingredients = new FoodItems();
                    final ArrayList<Pair<FoodItem, Limits2>> foodList = mealTemplate.getIngredients().getList();
                    final int numberOfFoods = foodList.size();
                    final int crossoverFood = RANDOM.nextInt(numberOfFoods + 1);
                    for (int i = 0; i < numberOfFoods; ++i) {
                        final FoodItem foodItem = foodList.get(i).a();
                        // TODO: ingredient mutation (see above)
                        if (i < crossoverFood) {
                            ingredients.set(foodItem, meal1.getAmount(foodItem));
                        } else {
                            ingredients.set(foodItem, meal2.getAmount(foodItem));
                        }
                    }
                    meals3.add(meal(mealTemplate, ingredients));
                } else {
                    meals3.add(meal2);
                }
            } else {
                meals3.add(meals2.get(crossoverMeal));
            }
            for (int i = crossoverMeal + 1; i < numberOfMeals2; ++i) {
                if (RANDOM.nextDouble() < mealMutationRate) {
                    meals3.add(randomMeal(mealTemplates.get(RANDOM.nextInt(mealTemplates.size()))));
                } else {
                    // TODO: ingredient mutation (see above)
                    meals3.add(meals2.get(i));
                }
            }
        }

        return dietPlan(meals3);
    }

    @Override
    public String toString() {
        return "Meals:" + '\n' + getMeals() + '\n' + "Food items:" + '\n' + getFoodItems() + '\n' + "Properties:" +
                '\n' + getProperties() + '\n' + "Costs:" + '\n' + String.format("AUD %1$,.2f", getCosts());
    }
}
