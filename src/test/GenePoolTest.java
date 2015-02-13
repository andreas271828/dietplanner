package test;

import diet.*;
import evolution.EvaluatedGenome;
import evolution.GenePool;
import evolution.Genome;
import util.Limits4;
import util.Pair;
import util.ScoreFunctions;
import util.Scores;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static diet.DietPlan.dietPlan;
import static diet.FoodItem.*;

public class GenePoolTest {
    public static void runTests() {
        final MealTemplates mealTemplates = getMealTemplatesDayMix();
        final int numberOfMeals = 7;
        final Requirements requirements = new Requirements(PersonalDetails.ANDREAS, 7, numberOfMeals);
        final Function<Genome, Scores> fitnessFunction = getFitnessFunction(mealTemplates, requirements);
        final Optional<EvaluatedGenome> bestGenome = GenePool.findBestGenome(10, fitnessFunction,
                new Function<Pair<Integer, GenePool>, Boolean>() {
                    @Override
                    public Boolean apply(final Pair<Integer, GenePool> generationInfo) {
                        final int generation = generationInfo.a();
                        final Optional<EvaluatedGenome> bestGenome = generationInfo.b().getBestGenome();
                        bestGenome.ifPresent(new Consumer<EvaluatedGenome>() {
                            @Override
                            public void accept(EvaluatedGenome bestGenome) {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("Best genome in generation ");
                                sb.append(generation);
                                sb.append(" (genome length = ");
                                sb.append(bestGenome.getGenome().getGenomeLength());
                                sb.append("): ");
                                sb.append(bestGenome.getFitness());
                                System.out.println(sb);
                            }
                        });
                        return generation < 500;
                    }
                });
        bestGenome.ifPresent(new Consumer<EvaluatedGenome>() {
            @Override
            public void accept(final EvaluatedGenome bestGenome) {
                final DietPlan dietPlan = dietPlan(mealTemplates.computeMeals(numberOfMeals, bestGenome.getGenome()));
                final Scores scores = bestGenome.getScores();

                System.out.println();
                System.out.println("Meals:");
                System.out.println(dietPlan.getMeals());
                System.out.println();
                System.out.println("Foods items:");
                System.out.println(dietPlan.getFoodItems());
                System.out.println();
                System.out.println("Properties:");
                System.out.println(dietPlan.getProperties());
                System.out.println();
                System.out.println("Scores:");
                System.out.println(scores);
                System.out.println("Total score: " + scores.getTotalScore() + " / " + scores.getWeightSum());
            }
        });
    }

    private static MealTemplates getMealTemplatesAnything() {
        final MealTemplates mealTemplates = new MealTemplates();

        mealTemplates.add(new MealTemplate("Anything") {
            @Override
            protected void addIngredients() {
                for (FoodItem foodItem : FoodItem.values()) {
                    addIngredientByWeight(foodItem, 0.0, 100.0);
                }
            }
        });

        return mealTemplates;
    }

    private static MealTemplates getMealTemplatesDayMix() {
        final MealTemplates mealTemplates = new MealTemplates();

        mealTemplates.add(new MealTemplate("Day Mix") {
            @Override
            protected void addIngredients() {
                addIngredient(COLES_APPLE_RED_DELICIOUS, 0.0, 3.0);
                addIngredientByWeight(COLES_ASPARAGUS_GREEN, 0.0, 200.0);
                addIngredient(COLES_AVOCADO, 0.0, 3.0);
                addIngredientByWeight(COLES_BACON, 0.0, 200.0);
                addIngredient(COLES_BANANA, 0.0, 3.0);
                addIngredientByWeight(COLES_BASIL_DRIED, 0.0, 5.0);
                addIngredientByWeight(COLES_BASIL, 0.0, 10.0);
                addIngredientByWeight(COLES_BEAN_GREEN, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_FILLET_STEAK, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_HEART, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_KIDNEY, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_ROASTING_CUTS, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_RUMP_STEAK, 0.0, 400.0);
                addIngredientByWeight(COLES_BEEF_T_BONE_STEAK, 0.0, 400.0);
                addIngredient(COLES_BEETROOT, 0.0, 3.0);
                addIngredientByWeight(COLES_BLUEBERRY_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_BOK_CHOY, 0.0, 300.0);
                addIngredientByWeight(COLES_BREAD_MIXED_GRAIN, 0.0, 400.0);
                addIngredientByWeight(COLES_BROCCOLI, 0.0, 400.0);
                addIngredientByWeight(COLES_BRUSSELS_SPROUT, 0.0, 300.0);
                addIngredientByWeight(COLES_BUTTER, 0.0, 200.0);
                addIngredient(COLES_CABBAGE_RED, 0.0, 1.0);
                addIngredient(COLES_CABBAGE_WHITE, 0.0, 1.0);
                addIngredient(COLES_CAPSICUM_GREEN, 0.0, 2.0);
                addIngredient(COLES_CAPSICUM_RED, 0.0, 2.0);
                addIngredient(COLES_CARROT, 0.0, 3.0);
                addIngredient(COLES_CAULIFLOWER, 0.0, 1.0);
                addIngredientByWeight(COLES_CELERY, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_CHEDDAR, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_COLBY, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_COTTAGE_REDUCED_FAT, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_CREAM, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_FETTA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_GOAT, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_GOUDA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_HALOUMI, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_MOZZARELLA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_PARMESAN, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_RICOTTA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHEESE_SWISS, 0.0, 400.0);
                addIngredientByWeight(COLES_CHICKEN_BREAST_FREE_RANGE, 0.0, 500.0);
                addIngredientByWeight(COLES_CHICKEN_DRUMSTICK_FREE_RANGE, 0.0, 500.0);
                addIngredientByWeight(COLES_CHICKEN_LIVER, 0.0, 400.0);
                addIngredientByWeight(COLES_CHICKEN_THIGH_FREE_RANGE, 0.0, 500.0);
                addIngredient(COLES_CHICKEN_FREE_RANGE, 0.0, 750.0);
                addIngredientByWeight(COLES_CHICKEN_WING, 0.0, 500.0);
                addIngredientByWeight(COLES_CHICKPEA, 0.0, 400.0);
                addIngredientByWeight(COLES_CHOCOLATE_DARK_ORGANIC, 0.0, 200.0);
                addIngredientByWeight(COLES_COCONUT_OIL_ORGANIC, 0.0, 100.0);
                addIngredientByWeight(COLES_CREAM_PURE, 0.0, 300.0);
                addIngredientByWeight(COLES_CREAM_DOUBLE, 0.0, 300.0);
                addIngredientByWeight(COLES_CREAM_SOUR, 0.0, 500.0);
                addIngredient(COLES_CUCUMBER, 0.0, 2.0);
                addIngredientByWeight(COLES_DILL, 0.0, 20.0);
                addIngredientByWeight(COLES_EGG_CHICKEN_FREE_RANGE, 0.0, 300.0);
                addIngredientByWeight(COLES_GARLIC, 0.0, 50.0);
                addIngredientByWeight(COLES_GHEE, 0.0, 100.0);
                addIngredientByWeight(COLES_GINGER, 0.0, 50.0);
                addIngredientByWeight(COLES_HAM_LEG, 0.0, 400.0);
                addIngredientByWeight(COLES_HONEY, 0.0, 100.0);
                addIngredientByWeight(COLES_KALE, 0.0, 300.0);
                addIngredientByWeight(COLES_KANGAROO, 0.0, 500.0);
                addIngredient(COLES_KIWIFRUIT, 0.0, 2.0);
                addIngredientByWeight(COLES_LEEK, 0.0, 200.0);
                addIngredient(COLES_LEMON, 0.0, 3.0);
                addIngredientByWeight(COLES_LENTIL, 0.0, 500.0);
                addIngredientByWeight(COLES_LETTUCE, 0.0, 400.0);
                addIngredient(COLES_LIME, 0.0, 2.0);
                addIngredientByWeight(COLES_MACKEREL, 0.0, 500.0);
                addIngredient(COLES_MANDARIN, 0.0, 5.0);
                addIngredient(COLES_MANGO, 0.0, 3.0);
                addIngredientByWeight(COLES_MAYONNAISE, 0.0, 400.0);
                addIngredientByWeight(COLES_MILK_ALMOND, 0.0, 500.0);
                addIngredientByWeight(COLES_MILK_COW, 0.0, 500.0);
                addIngredientByWeight(COLES_MILK_GOAT, 0.0, 500.0);
                addIngredientByWeight(COLES_MUESLI_FRUIT, 0.0, 500.0);
                addIngredientByWeight(COLES_MUESLI_FRUIT_NUTS, 0.0, 500.0);
                addIngredientByWeight(COLES_MUSHROOM, 0.0, 400.0);
                addIngredient(COLES_MUSSEL_DRAINED, 0.0, 1.0);
                addIngredientByWeight(COLES_NUT_ALMOND, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_CASHEW, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_MACADAMIA, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_PECAN, 0.0, 200.0);
                addIngredientByWeight(COLES_NUT_WALNUT, 0.0, 200.0);
                addIngredientByWeight(COLES_OIL_OLIVE, 0.0, 200.0);
                addIngredientByWeight(COLES_OKRA, 0.0, 300.0);
                addIngredientByWeight(COLES_OLIVE_GREEN, 0.0, 200.0);
                addIngredient(COLES_ONION, 0.0, 2.0);
                addIngredient(COLES_ORANGE, 0.0, 4.0);
                addIngredientByWeight(COLES_OREGANO, 0.0, 5.0);
                addIngredientByWeight(COLES_PAPRIKA, 0.0, 10.0);
                addIngredientByWeight(COLES_PARSLEY, 0.0, 20.0);
                addIngredientByWeight(COLES_PASTA_WHOLEMEAL, 0.0, 400.0);
                addIngredientByWeight(COLES_PEA_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_PEPPER, 0.0, 10.0);
                addIngredientByWeight(COLES_POTATO, 0.0, 500.0);
                addIngredientByWeight(COLES_PRAWN_KING, 0.0, 300.0);
                addIngredientByWeight(COLES_PUMPKIN, 0.0, 500.0);
                addIngredientByWeight(COLES_TAMARI, 0.0, 100.0);
                addIngredientByWeight(COLES_QUINOA_ORGANIC, 0.0, 400.0);
                addIngredientByWeight(COLES_RADISH, 0.0, 200.0);
                addIngredientByWeight(COLES_RASPBERRY_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_RHUBARB, 0.0, 300.0);
                addIngredientByWeight(COLES_RICE_BROWN, 0.0, 400.0);
                addIngredientByWeight(COLES_ROCKET, 0.0, 400.0);
                addIngredientByWeight(COLES_SALAMI_HUNGARIAN, 0.0, 400.0);
                addIngredientByWeight(COLES_SALMON, 0.0, 400.0);
                addIngredientByWeight(COLES_SALMON_SMOKED, 0.0, 300.0);
                addIngredientByWeight(COLES_SALT_SEA, 0.0, 10.0);
                addIngredient(COLES_SARDINE_IN_OIL_UNDRAINED, 0.0, 1.0);
                addIngredientByWeight(COLES_SAUSAGE_BEEF, 0.0, 400.0);
                addIngredientByWeight(COLES_SAUSAGE_CHORIZO, 0.0, 400.0);
                addIngredientByWeight(COLES_SAUSAGE_PORK, 0.0, 400.0);
                addIngredientByWeight(COLES_SEED_CHIA, 0.0, 300.0);
                addIngredientByWeight(COLES_SEED_LINSEED, 0.0, 300.0);
                addIngredientByWeight(COLES_SEED_SUNFLOWER, 0.0, 300.0);
                addIngredientByWeight(COLES_SHALLOT, 0.0, 300.0);
                addIngredientByWeight(COLES_SNOW_PEA, 0.0, 300.0);
                addIngredientByWeight(COLES_SPINACH, 0.0, 400.0);
                addIngredient(COLES_SQUASH, 0.0, 3.0);
                addIngredientByWeight(COLES_SQUID, 0.0, 400.0);
                addIngredientByWeight(COLES_STRAWBERRY_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_STRAWBERRY, 0.0, 300.0);
                addIngredientByWeight(COLES_SWEET_POTATO, 0.0, 500.0);
                addIngredientByWeight(COLES_SWEETCORN_FROZEN, 0.0, 300.0);
                addIngredientByWeight(COLES_TEA_CHAI, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_GREEN, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_CHAMOMILE, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_MINT, 0.0, 10.0);
                addIngredientByWeight(COLES_TEA_BLACK, 0.0, 10.0);
                addIngredientByWeight(COLES_TOMATO_CHERRY, 0.0, 400.0);
                addIngredient(COLES_TOMATO, 0.0, 400.0);
                addIngredientByWeight(COLES_TROUT, 0.0, 400.0);
                addIngredient(COLES_TUNA_IN_OIL_DRAINED, 0.0, 1.0);
                addIngredient(COLES_TUNA_IN_WATER_DRAINED, 0.0, 1.0);
                addIngredientByWeight(COLES_VEAL, 0.0, 400.0);
                addIngredientByWeight(COLES_WATERCRESS, 0.0, 200.0);
                addIngredientByWeight(COLES_YOGHURT_GREEK_STYLE_NATURAL, 0.0, 400.0);
                addIngredient(COLES_ZUCCHINI, 0.0, 1.0);
            }
        });

        return mealTemplates;
    }

    private static MealTemplates getMealTemplates() {
        final MealTemplates mealTemplates = new MealTemplates();

        final Ingredients basicSaladIngredients = new Ingredients();
        basicSaladIngredients.add(COLES_APPLE_RED_DELICIOUS, 0.0, 2.0);
        basicSaladIngredients.add(COLES_LEMON, 0.0, 0.5);
        basicSaladIngredients.addByWeight(COLES_OIL_OLIVE, 1.0, 100.0);
        basicSaladIngredients.addByWeight(COLES_SPINACH, 20.0, 200.0);
        basicSaladIngredients.add(COLES_CAPSICUM_RED, 0.0, 1.0);
        basicSaladIngredients.add(COLES_CARROT, 0.0, 1.0);

        mealTemplates.add(new MealTemplate("Salad with mayonnaise") {
            @Override
            protected void addIngredients() {
                addIngredients(basicSaladIngredients);
                addIngredientByWeight(COLES_MAYONNAISE, 20.0, 300.0);
            }
        });
        mealTemplates.add(new MealTemplate("Salad with sour cream") {
            @Override
            protected void addIngredients() {
                addIngredients(basicSaladIngredients);
                addIngredientByWeight(COLES_CREAM_SOUR, 100.0, 500.0);
            }
        });
        mealTemplates.add(new MealTemplate("Avocado Plus") {
            @Override
            protected void addIngredients() {
                addIngredient(COLES_AVOCADO, 0.5, 2.0);
                addIngredient(COLES_LEMON, 0.1, 0.5);
                addIngredientByWeight(COLES_SALT_SEA, 0.1, 5.0);
                addIngredientByWeight(COLES_PEPPER, 0.1, 3);
                addIngredientByWeight(COLES_OIL_OLIVE, 0.0, 20.0);
            }
        });
        mealTemplates.add(new MealTemplate("Stir-fry") {
            @Override
            protected void addIngredients() {
                addIngredient(COLES_BROCCOLI, 0.0, 1.0);
                addIngredient(COLES_CARROT, 0.0, 1.0);
                addIngredientByWeight(COLES_SALT_SEA, 0.0, 10.0);
                addIngredientByWeight(COLES_COCONUT_OIL_ORGANIC, 0.0, 50.0);
            }
        });
        mealTemplates.add(new MealTemplate("Salami Plus") {
            @Override
            protected void addIngredients() {
                addIngredientByWeight(COLES_SALAMI_HUNGARIAN, 50.0, 500.0);
                addIngredientByWeight(COLES_CHEESE_COLBY, 0.0, 200.0);
                addIngredientByWeight(COLES_SPINACH, 0.0, 200.0);
            }
        });

        return mealTemplates;
    }

    private static Function<Genome, Scores> getFitnessFunction(final MealTemplates mealTemplates, final Requirements requirements) {
        return new Function<Genome, Scores>() {
            @Override
            public Scores apply(final Genome genome) {
                final Scores scores = new Scores();

                final int numberOfMeals = requirements.getNumberOfMeals();

                // Criteria for complete diet plan
                final DietPlan dietPlan = dietPlan(mealTemplates.computeMeals(numberOfMeals, genome));
                final FoodProperties dietPlanProperties = dietPlan.getProperties();
                final Optional<Integer> noMeal = Optional.empty();
                addScore(scores, Requirement.ALPHA_LINOLENIC_ACID, 1.0, requirements, dietPlanProperties.get(FoodProperty.ALPHA_LINOLENIC_ACID), noMeal);
                addScore(scores, Requirement.CALCIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.CALCIUM), noMeal);
                addScore(scores, Requirement.CARBOHYDRATES, 10.0, requirements, dietPlanProperties.get(FoodProperty.CARBOHYDRATES), noMeal);
                addScore(scores, Requirement.CHOLESTEROL, 1.0, requirements, dietPlanProperties.get(FoodProperty.CHOLESTEROL), noMeal);
                addScore(scores, Requirement.COSTS, 1.0, requirements, dietPlan.getCosts(), noMeal);
                addScore(scores, Requirement.DIETARY_FIBRE, 1.0, requirements, dietPlanProperties.get(FoodProperty.DIETARY_FIBRE), noMeal);
                addScore(scores, Requirement.ENERGY, 10.0, requirements, dietPlanProperties.get(FoodProperty.ENERGY), noMeal);
                addScore(scores, Requirement.FAT, 1.0, requirements, dietPlanProperties.get(FoodProperty.FAT), noMeal);
                addScore(scores, Requirement.FOLATES, 1.0, requirements, dietPlanProperties.get(FoodProperty.TOTAL_FOLATES), noMeal);
                addScore(scores, Requirement.IODINE, 1.0, requirements, dietPlanProperties.get(FoodProperty.IODINE), noMeal);
                addScore(scores, Requirement.IRON, 1.0, requirements, dietPlanProperties.get(FoodProperty.IRON), noMeal);
                addScore(scores, Requirement.LINOLEIC_ACID, 1.0, requirements, dietPlanProperties.get(FoodProperty.LINOLEIC_ACID), noMeal);
                addScore(scores, Requirement.MAGNESIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.MAGNESIUM), noMeal);
                addScore(scores, Requirement.NIACIN_DERIVED_EQUIVALENTS, 1.0, requirements, dietPlanProperties.get(FoodProperty.NIACIN_DERIVED_EQUIVALENTS), noMeal);
                addScore(scores, Requirement.OMEGA_3_FATTY_ACIDS, 1.0, requirements, dietPlanProperties.get(FoodProperty.OMEGA_3_FATTY_ACIDS), noMeal);
                addScore(scores, Requirement.PHOSPHORUS, 1.0, requirements, dietPlanProperties.get(FoodProperty.PHOSPHORUS), noMeal);
                addScore(scores, Requirement.POTASSIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.POTASSIUM), noMeal);
                addScore(scores, Requirement.PROTEIN, 10.0, requirements, dietPlanProperties.get(FoodProperty.PROTEIN), noMeal);
                addScore(scores, Requirement.RIBOFLAVIN, 1.0, requirements, dietPlanProperties.get(FoodProperty.RIBOFLAVIN), noMeal);
                addScore(scores, Requirement.SELENIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.SELENIUM), noMeal);
                addScore(scores, Requirement.SODIUM, 1.0, requirements, dietPlanProperties.get(FoodProperty.SODIUM), noMeal);
                addScore(scores, Requirement.SUGARS, 1.0, requirements, dietPlanProperties.get(FoodProperty.SUGARS), noMeal);
                addScore(scores, Requirement.THIAMIN, 1.0, requirements, dietPlanProperties.get(FoodProperty.THIAMIN), noMeal);
                addScore(scores, Requirement.TRANS_FATTY_ACIDS, 1.0, requirements, dietPlanProperties.get(FoodProperty.TRANS_FATTY_ACIDS), noMeal);
                addScore(scores, Requirement.TRYPTOPHAN, 1.0, requirements, dietPlanProperties.get(FoodProperty.TRYPTOPHAN), noMeal);
                addScore(scores, Requirement.VITAMIN_A_RETINOL_EQUIVALENTS, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_A_RETINOL_EQUIVALENTS), noMeal);
                addScore(scores, Requirement.VITAMIN_B12, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_B12), noMeal);
                addScore(scores, Requirement.VITAMIN_B6, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_B6), noMeal);
                addScore(scores, Requirement.VITAMIN_C, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_C), noMeal);
                addScore(scores, Requirement.VITAMIN_E, 1.0, requirements, dietPlanProperties.get(FoodProperty.VITAMIN_E), noMeal);
                addScore(scores, Requirement.ZINC, 1.0, requirements, dietPlanProperties.get(FoodProperty.ZINC), noMeal);

                // Criteria for individual meals
                for (int i = 0; i < numberOfMeals; ++i) {
                    final Meal meal = dietPlan.getMeal(i);
                    final FoodProperties mealProperties = meal.getProperties();
                    final Optional<Integer> mealIndex = Optional.of(i);
                    addScore(scores, Requirement.MEAL_ALCOHOL, 1.0, requirements, mealProperties.get(FoodProperty.ALCOHOL), mealIndex);
                    addScore(scores, Requirement.MEAL_CAFFEINE, 1.0, requirements, mealProperties.get(FoodProperty.CAFFEINE), mealIndex);
                    addScore(scores, Requirement.MEAL_CARBOHYDRATES, 1.0, requirements, mealProperties.get(FoodProperty.CARBOHYDRATES), mealIndex);
                    addScore(scores, Requirement.MEAL_ENERGY, 1.0, requirements, mealProperties.get(FoodProperty.ENERGY), mealIndex);
                    addScore(scores, Requirement.MEAL_FAT, 1.0, requirements, mealProperties.get(FoodProperty.FAT), mealIndex);
                    addScore(scores, Requirement.MEAL_PROTEIN, 1.0, requirements, mealProperties.get(FoodProperty.PROTEIN), mealIndex);
                }

                return scores;
            }
        };
    }

    private static void addScore(final Scores scores,
                                 final Requirement requirement,
                                 final double weight,
                                 final Requirements requirements,
                                 final double value,
                                 final Optional<Integer> mealIndex) {
        requirements.getLimits(requirement).ifPresent(new Consumer<Limits4>() {
            @Override
            public void accept(final Limits4 limits) {
                final double score = ScoreFunctions.standard(value, limits, 1000 * limits.getUpperCritical());
                final StringBuilder sb = new StringBuilder(requirement.getName());
                mealIndex.ifPresent(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer mealIndex) {
                        sb.append(" (meal ");
                        sb.append(mealIndex + 1);
                        sb.append(")");
                    }
                });
                scores.addScore(score, weight, sb.toString());
            }
        });
    }
}
