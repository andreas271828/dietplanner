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

import java.util.function.BiConsumer;

import static java.lang.Math.round;

public enum FoodItem {
    COLES_ALPINE_COCONUT_MILK_YOGHURT(Food.ALPINE_COCONUT_MILK_YOGHURT, 400, 8, 14, 6.60),
    COLES_APPLE_RED_DELICIOUS(Food.APPLE_RED_DELICIOUS_UNPEELED_RAW, 180, 2, 14, 0.99),
    COLES_APPLE_RED_DELICIOUS_1KG(Food.APPLE_RED_DELICIOUS_UNPEELED_RAW, 1000, 12, 14, 5.30),
    COLES_ASPARAGUS_GREEN(Food.ASPARAGUS_GREEN_RAW, 200, 4, 7, 2.70),
    COLES_AVOCADO(Food.AVOCADO_RAW, 225, 2, 7, 1.80),
    COLES_AVOCADO_ORGANIC(Food.AVOCADO_RAW, 225, 2, 7, 4.50),
    COLES_BACON(Food.BACON_MIDDLE_RASHER_UNTRIMMED_RAW, 1000, 10, 21, 7.29),
    COLES_BANANA(Food.BANANA_CAVENDISH_PEELED_RAW, 180, 1, 7, 0.45),
    COLES_BANANA_ORGANIC(Food.BANANA_CAVENDISH_PEELED_RAW, 500, 3, 7, 4.50),
    COLES_BASIL_DRIED(Food.BASIL_DRIED, 15, 30, 70, 1.49),
    COLES_BASIL(Food.BASIL_GREEN_RAW, 20, 5, 7, 2.98),
    COLES_BEAN_GREEN(Food.BEAN_GREEN_FRESH_OR_FROZEN_RAW, 200, 4, 7, 0.80),
    COLES_BEEF_FILLET_STEAK(Food.BEEF_FILLET_STEAK_UNTRIMMED_RAW, 250, 2, 7, 10.25),
    COLES_BEEF_FILLET_STEAK_ORGANIC(Food.BEEF_FILLET_STEAK_UNTRIMMED_RAW, 375, 2, 7, 17.25),
    COLES_BEEF_HEART(Food.BEEF_HEART_RAW, 700, 3, 7, 3.50),
    COLES_BEEF_KIDNEY(Food.BEEF_KIDNEY_RAW, 950, 4, 7, 4.75),
    COLES_BEEF_ROASTING_CUTS(Food.BEEF_ROASTING_CUTS_UNTRIMMED_RAW, 1.5, 5, 7, 21.00),
    COLES_BEEF_RUMP_STEAK(Food.BEEF_RUMP_STEAK_UNTRIMMED_RAW, 450, 2, 7, 7.65),
    COLES_BEEF_RUMP_STEAK_ORGANIC(Food.BEEF_RUMP_STEAK_UNTRIMMED_RAW, 400, 2, 7, 11.20),
    COLES_BEEF_T_BONE_STEAK(Food.BEEF_T_BONE_STEAK_UNTRIMMED_RAW, 800, 4, 7, 17.60),
    COLES_BEETROOT(Food.BEETROOT_PURPLE_PEELED_FRESH_OR_FROZEN_RAW, 170, 2, 7, 0.68),
    COLES_BEETROOT_ORGANIC(Food.BEETROOT_PURPLE_PEELED_FRESH_OR_FROZEN_RAW, 500, 6, 7, 4.00),
    COLES_BLUEBERRY_FROZEN(Food.BLUEBERRY_PURCHASED_FROZEN, 1000, 20, 70, 10.45),
    COLES_BOK_CHOY(Food.BOK_CHOY_OR_CHOY_SUM_RAW, 150, 2, 7, 1.50),
    COLES_BREAD_FROM_WHOLEMEAL_FLOUR(Food.BREAD_FROM_WHOLEMEAL_FLOUR_COMMERCIAL, 700, 10, 14, 1.50),
    COLES_BREAD_MIXED_GRAIN(Food.BREAD_MIXED_GRAIN_COMMERCIAL, 850, 10, 14, 3.50),
    COLES_BROCCOLI(Food.BROCCOLI_FRESH_OR_FROZEN_RAW, 340, 4, 7, 1.33),
    COLES_BRUSSELS_SPROUT(Food.BRUSSELS_SPROUT_FRESH_OR_FROZEN_RAW, 400, 4, 7, 3.19),
    COLES_BUTTER(Food.BUTTER_PLAIN_NO_ADDED_SALT, 250, 25, 21, 1.39),
    COLES_CABBAGE_RED(Food.CABBAGE_RED_RAW, 500, 4, 7, 2.90),
    COLES_CABBAGE_WHITE(Food.CABBAGE_WHITE_RAW, 500, 4, 7, 2.90),
    COLES_CAPSICUM_GREEN(Food.CAPSICUM_GREEN_FRESH_OR_FROZEN_RAW, 250, 2, 7, 1.25),
    COLES_CAPSICUM_RED(Food.CAPSICUM_RED_FRESH_OR_FROZEN_RAW, 320, 2, 7, 1.28),
    COLES_CARROT(Food.CARROT_MATURE_PEELED_OR_UNPEELED_FRESH_OR_FROZEN_RAW, 170, 1, 14, 0.39),
    COLES_CARROT_ORGANIC(Food.CARROT_MATURE_PEELED_OR_UNPEELED_FRESH_OR_FROZEN_RAW, 1000, 6, 14, 5.00),
    COLES_CAULIFLOWER(Food.CAULIFLOWER_FRESH_OR_FROZEN_RAW, 400, 4, 7, 4.98),
    COLES_CELERY(Food.CELERY_FRESH_OR_FROZEN_RAW, 300, 6, 14, 1.50),
    COLES_CHEESE_CHEDDAR(Food.CHEESE_CHEDDAR_NATURAL_PLAIN_REGULAR_FAT, 1000, 10, 21, 7.15),
    COLES_CHEESE_COLBY(Food.CHEESE_COLBY_STYLE, 1000, 10, 21, 12.93),
    COLES_CHEESE_COTTAGE_REDUCED_FAT(Food.CHEESE_COTTAGE_REDUCED_FAT, 500, 5, 14, 3.19),
    COLES_CHEESE_CREAM(Food.CHEESE_CREAM_PLAIN_REGULAR_FAT, 250, 5, 14, 3.00),
    COLES_CHEESE_FETTA(Food.CHEESE_FETTA_REGULAR_FAT, 180, 3, 14, 6.11),
    COLES_CHEESE_FETTA_ORGANIC(Food.CHEESE_FETTA_REGULAR_FAT, 180, 3, 14, 7.03),
    COLES_CHEESE_GOAT(Food.CHEESE_GOAT_SOFT, 120, 2, 14, 7.15),
    COLES_CHEESE_GOUDA(Food.CHEESE_GOUDA, 200, 4, 14, 6.22),
    COLES_CHEESE_HALOUMI(Food.CHEESE_HALOUMI, 180, 3, 14, 7.70),
    COLES_CHEESE_HALOUMI_ORGANIC(Food.CHEESE_HALOUMI, 180, 3, 14, 7.65),
    COLES_CHEESE_MOZZARELLA(Food.CHEESE_MOZZARELLA_REGULAR_FAT, 500, 5, 14, 8.24),
    COLES_CHEESE_PARMESAN(Food.CHEESE_PARMESAN_FRESH, 250, 10, 14, 6.70),
    COLES_CHEESE_RICOTTA(Food.CHEESE_RICOTTA_REGULAR_FAT, 125, 5, 14, 1.06),
    COLES_CHEESE_SWISS(Food.CHEESE_SWISS, 200, 4, 14, 5.45),
    COLES_CHICKEN_BREAST(Food.CHICKEN_BREAST_FLESH_RAW, 500, 4, 7, 6.00),
    COLES_CHICKEN_BREAST_FREE_RANGE(Food.CHICKEN_BREAST_FLESH_RAW, 630, 4, 7, 11.65),
    COLES_CHICKEN_BREAST_ORGANIC(Food.CHICKEN_BREAST_FLESH_RAW, 600, 4, 7, 18.84),
    COLES_CHICKEN_DRUMSTICK(Food.CHICKEN_DRUMSTICK_FLESH_SKIN_FAT_RAW, 600, 4, 7, 3.48),
    COLES_CHICKEN_DRUMSTICK_FREE_RANGE(Food.CHICKEN_DRUMSTICK_FLESH_SKIN_FAT_RAW, 630, 4, 7, 4.28),
    COLES_CHICKEN_DRUMSTICK_ORGANIC(Food.CHICKEN_DRUMSTICK_FLESH_SKIN_FAT_RAW, 500, 4, 7, 3.75),
    COLES_CHICKEN_LIVER(Food.CHICKEN_LIVER_RAW, 500, 4, 7, 3.75),
    COLES_CHICKEN_THIGH(Food.CHICKEN_THIGH_FLESH_RAW, 550, 4, 7, 6.05),
    COLES_CHICKEN_THIGH_FREE_RANGE(Food.CHICKEN_THIGH_FLESH_RAW, 630, 4, 7, 10.71),
    COLES_CHICKEN_THIGH_ORGANIC(Food.CHICKEN_THIGH_FLESH_RAW, 450, 4, 7, 12.24),
    COLES_CHICKEN(Food.CHICKEN_WHOLE_FLESH_SKIN_FAT_RAW, 1300, 4, 7, 7.02),
    COLES_CHICKEN_FREE_RANGE(Food.CHICKEN_WHOLE_FLESH_SKIN_FAT_RAW, 1500, 4, 7, 11.65),
    COLES_CHICKEN_ORGANIC(Food.CHICKEN_WHOLE_FLESH_SKIN_FAT_RAW, 1700, 4, 7, 20.23),
    COLES_CHICKEN_WING(Food.CHICKEN_WING_FLESH_SKIN_FAT_RAW, 500, 4, 7, 1.99),
    COLES_CHICKPEA(Food.CHICKPEA_CANNED_DRAINED, 400, 2, 7, 0.80),
    COLES_CHOCOLATE_DARK_ORGANIC(Food.CHOCOLATE_DARK_HIGH_COCOA_SOLIDS, 100, 5, 14, 4.51),
    COLES_COCONUT_OIL_ORGANIC(Food.COCONUT_OIL, 300, 30, 35, 12.27),
    COLES_CREAM_PURE(Food.CREAM_PURE, 300, 2, 7, 3.08),
    COLES_CREAM_DOUBLE(Food.CREAM_RICH_OR_DOUBLE_THICK, 300, 2, 7, 3.50),
    COLES_CREAM_SOUR(Food.CREAM_SOUR_REGULAR_FAT, 500, 2, 7, 2.42),
    COLES_CUCUMBER(Food.CUCUMBER_COMMON_UNPEELED_RAW, 320, 2, 7, 0.90),
    COLES_DILL(Food.DILL_RAW, 20, 5, 7, 2.98),
    COLES_EGG_CHICKEN_FREE_RANGE(Food.EGG_CHICKEN_WHOLE_RAW, 700, 12, 14, 5.00),
    COLES_EGG_CHICKEN_ORGANIC(Food.EGG_CHICKEN_WHOLE_RAW, 550, 10, 14, 8.00),
    COLES_GARLIC(Food.GARLIC_PEELED_OR_UNPEELED_FRESH_OR_FROZEN_RAW, 60, 8, 35, 1.20),
    COLES_GHEE(Food.GHEE_CLARIFIED_BUTTER, 300, 30, 35, 6.13),
    COLES_GINGER(Food.GINGER_PEELED_FRESH_OR_FROZEN_RAW, 120, 12, 70, 3.24),
    COLES_GNOCCHI_POTATO(Food.GNOCCHI_POTATO_COMMERCIALLY_PREPARED_BOILED, 500, 4, 7, 3.85),
    COLES_HAM_LEG(Food.HAM_LEG_LEAN_FAT, 125, 5, 7, 1.81),
    COLES_HONEY(Food.HONEY, 500, 50, 35, 7.32),
    COLES_KALE(Food.KALE_RAW, 150, 5, 7, 2.50),
    COLES_KALE_ORGANIC(Food.KALE_RAW, 150, 5, 7, 5.00),
    COLES_KANGAROO(Food.KANGAROO_RUMP_RAW, 380, 3, 7, 6.65),
    COLES_KIWIFRUIT(Food.KIWIFRUIT_GREEN_HAYWARD_PEELED_RAW, 100, 2, 7, 0.60),
    COLES_LEEK(Food.LEEK_RAW, 150, 5, 7, 2.98),
    COLES_LEMON(Food.LEMON_PEELED_RAW, 160, 8, 7, 1.50),
    COLES_LENTIL(Food.LENTIL_DRIED, 400, 2, 7, 0.80),
    COLES_LETTUCE(Food.LETTUCE_RAW_NOT_FURTHER_DEFINED, 500, 5, 7, 2.98),
    COLES_LIME(Food.LIME_PEELED_RAW, 120, 6, 7, 1.10),
    COLES_MACKEREL(Food.MACKEREL_CANNED, 125, 1, 7, 2.64),
    COLES_MANDARIN(Food.MANDARIN_PEELED_RAW, 130, 1, 7, 1.30),
    COLES_MANGO(Food.MANGO_PEELED_RAW, 300, 2, 7, 3.00),
    COLES_MAYONNAISE(Food.MAYONNAISE_COMMERCIAL_REGULAR_FAT, 445, 10, 21, 2.85),
    COLES_MILK_ALMOND(Food.MILK_ALMOND_FLUID, 1000, 20, 7, 3.62),
    COLES_MILK_COW(Food.MILK_COW_FLUID_REGULAR_FAT, 1000, 20, 7, 1.25),
    COLES_MILK_GOAT(Food.MILK_GOAT_FLUID_REGULAR_FAT, 1000, 20, 7, 4.95),
    COLES_MUESLI_FRUIT(Food.MUESLI_COMMERCIAL_UNTOASTED_OR_NATURAL_STYLE_ADDED_DRIED_FRUIT_UNFORTIFIED, 750, 6, 21, 3.85),
    COLES_MUESLI_FRUIT_NUTS(Food.MUESLI_COMMERCIAL_UNTOASTED_OR_NATURAL_STYLE_ADDED_DRIED_FRUIT_NUTS_UNFORTIFIED, 550, 5, 21, 5.50),
    COLES_MUSHROOM(Food.MUSHROOM_COMMON_FRESH_OR_FROZEN_RAW, 200, 4, 7, 2.00),
    COLES_MUSSEL_DRAINED(Food.MUSSEL_SMOKED_CANNED_IN_OIL_DRAINED, 85, 1, 7, 2.31),
    COLES_NUT_ALMOND(Food.NUT_ALMOND_WITH_OR_WITHOUT_SKIN_RAW_UNSALTED, 200, 10, 21, 4.20),
    COLES_NUT_CASHEW(Food.NUT_CASHEW_RAW_UNSALTED, 350, 15, 21, 9.35),
    COLES_NUT_MACADAMIA(Food.NUT_MACADAMIA_RAW_UNSALTED, 350, 15, 21, 15.00),
    COLES_NUT_PECAN(Food.NUT_PECAN_RAW_UNSALTED, 300, 15, 21, 11.00),
    COLES_NUT_WALNUT(Food.NUT_WALNUT_RAW_UNSALTED, 100, 5, 21, 2.70),
    COLES_OIL_OLIVE(Food.OIL_OLIVE, 928, 100, 70, 7.00),
    COLES_OKRA(Food.OKRA_RAW, 200, 4, 7, 2.30),
    COLES_OLIVE_GREEN(Food.OLIVE_GREEN_OR_BLACK_DRAINED, 350, 14, 28, 2.86),
    COLES_ONION(Food.ONION_MATURE_BROWN_SKINNED_PEELED_RAW, 180, 2, 7, 0.54),
    COLES_ORANGE(Food.ORANGE_PEELED_RAW_NOT_FURTHER_DEFINED, 380, 1, 7, 1.89),
    COLES_OREGANO(Food.OREGANO_OR_MARJORAM_DRIED, 60, 60, 70, 3.12),
    COLES_PAPRIKA(Food.PAPRIKA_DRY_POWDER, 190, 190, 70, 3.96),
    COLES_PARSLEY(Food.PARSLEY_CONTINENTAL_RAW, 25, 5, 7, 2.78),
    COLES_PASTA(Food.PASTA_WHITE_WHEAT_FLOUR_PLAIN_DRY, 500, 5, 35, 0.65),
    COLES_PASTA_WHOLEMEAL(Food.PASTA_WHOLEMEAL_WHEAT_FLOUR_PLAIN_DRY, 500, 5, 35, 2.48),
    COLES_PEA_FROZEN(Food.PEA_GREEN_FRESH_OR_FROZEN_RAW, 1000, 20, 70, 2.19),
    COLES_PEPPER(Food.PEPPER_GROUND_BLACK_OR_WHITE, 190, 190, 70, 6.02),
    COLES_PORK_SPARE_RIBS(Food.PORK_SPARE_RIBS_UNTRIMMED_RAW, 300, 3, 7, 5.10),
    COLES_POTATO(Food.POTATO_PEELED_BOILED_MICROWAVED_OR_STEAMED_DRAINED, 170, 2, 7, 0.51),
    COLES_PRAWN_KING(Food.PRAWN_KING_OR_MEDIUM_RAW_GREEN, 250, 2, 7, 5.50),
    COLES_PUMPKIN(Food.PUMPKIN_PEELED_FRESH_OR_FROZEN_RAW, 700, 4, 7, 1.26),
    COLES_TAMARI(Food.PUREHARVEST_ORGANIC_TAMARI, 250, 50, 70, 4.24),
    COLES_QUINOA_ORGANIC(Food.QUINOA_UNCOOKED, 1000, 10, 21, 19.25),
    COLES_RADISH(Food.RADISH_PEELED_OR_UNPEELED_RAW, 180, 3, 7, 2.98),
    COLES_RASPBERRY_FROZEN(Food.RASPBERRY_PURCHASED_FROZEN, 1000, 20, 70, 10.45),
    COLES_RHUBARB(Food.RHUBARB_STALK_RAW, 200, 4, 7, 6.90),
    COLES_RICE_BROWN(Food.RICE_BROWN_UNCOOKED, 5000, 100, 70, 14.96),
    COLES_ROCKET(Food.ROCKET_RAW, 200, 4, 7, 2.60),
    COLES_SALAMI_HUNGARIAN(Food.SALAMI_HUNGARIAN, 250, 5, 7, 6.50),
    COLES_SALMON(Food.SALMON_RAW_NOT_FURTHER_DEFINED, 250, 2, 7, 7.48),
    COLES_SALMON_SMOKED(Food.SALMON_SMOKED_SLICED, 300, 6, 7, 14.91),
    COLES_SALT_SEA(Food.SALT_SEA, 500, 500, 70, 2.19),
    COLES_SARDINE_IN_OIL_UNDRAINED(Food.SARDINE_CANNED_IN_OIL_UNDRAINED, 106, 1, 7, 1.82),
    COLES_SAUSAGE_BEEF(Food.SAUSAGE_BEEF_RAW, 560, 4, 7, 5.00),
    COLES_SAUSAGE_BEEF_ORGANIC(Food.SAUSAGE_BEEF_RAW, 480, 4, 7, 7.00),
    COLES_SAUSAGE_CHORIZO(Food.SAUSAGE_CHORIZO_UNCOOKED, 500, 4, 7, 8.00),
    COLES_SAUSAGE_PORK(Food.SAUSAGE_PORK_RAW, 560, 4, 7, 5.00),
    COLES_SEED_CHIA(Food.SEED_CHIA_DRIED, 300, 6, 21, 12.09),
    COLES_SEED_LINSEED(Food.SEED_LINSEED_OR_FLAXSEED, 500, 20, 21, 5.28),
    COLES_SEED_SUNFLOWER(Food.SEED_SUNFLOWER_UNSALTED, 200, 8, 21, 2.37),
    COLES_SHALLOT(Food.SHALLOT_PEELED_RAW, 200, 5, 7, 2.60),
    COLES_SNOW_PEA(Food.SNOW_PEA_FRESH_OR_FROZEN_RAW, 100, 2, 7, 0.70),
    COLES_SPINACH(Food.SPINACH_FRESH_RAW, 200, 4, 7, 2.60),
    COLES_SQUASH(Food.SQUASH_BUTTON_FRESH_OR_FROZEN_RAW, 50, 1, 7, 0.50),
    COLES_SQUID(Food.SQUID_OR_CALAMARI_RAW, 500, 2, 7, 7.50),
    COLES_STRAWBERRY_FROZEN(Food.STRAWBERRY_PURCHASED_FROZEN, 1000, 20, 70, 10.99),
    COLES_STRAWBERRY(Food.STRAWBERRY_RAW, 250, 5, 7, 4.00),
    COLES_SWEET_POTATO(Food.SWEET_POTATO_ORANGE_FLESH_PEELED_OR_UNPEELED_FRESH_OR_FROZEN_BOILED_MICROWAVED_OR_STEAMED_DRAINED, 350, 4, 7, 1.74),
    COLES_SWEETCORN_FROZEN(Food.SWEETCORN_KERNELS_FRESH_OR_FROZEN_RAW, 1000, 20, 70, 2.70),
    COLES_TEA_CHAI(Food.TEA_CHAI_PLAIN_WITHOUT_MILK, 100, 50, 70, 8.79),
    COLES_TEA_GREEN(Food.TEA_GREEN_PLAIN_WITHOUT_MILK, 150, 100, 70, 8.36),
    COLES_TEA_CHAMOMILE(Food.TEA_HERBAL_CHAMOMILE_WITHOUT_MILK, 10, 10, 70, 2.74),
    COLES_TEA_MINT(Food.TEA_HERBAL_MINT_WITHOUT_MILK, 75, 50, 70, 5.31),
    COLES_TEA_BLACK(Food.TEA_REGULAR_BLACK_BREWED_FROM_LEAF_OR_TEABAGS_PLAIN_WITHOUT_MILK, 200, 100, 70, 9.45),
    COLES_TOMATO_CHERRY(Food.TOMATO_CHERRY_OR_GRAPE_RAW, 250, 5, 7, 2.98),
    COLES_TOMATO(Food.TOMATO_COMMON_RAW, 110, 1, 7, 0.55),
    COLES_TROUT(Food.TROUT_RAINBOW_RAW, 275, 2, 7, 13.00),
    COLES_TUNA_IN_OIL_DRAINED(Food.TUNA_UNFLAVOURED_CANNED_IN_VEGETABLE_OIL_DRAINED, 185, 1, 7, 1.50),
    COLES_TUNA_IN_WATER_DRAINED(Food.TUNA_UNFLAVOURED_CANNED_IN_WATER_DRAINED, 185, 1, 7, 1.50),
    COLES_VEAL(Food.VEAL_LOIN_CHOP_UNTRIMMED_RAW, 900, 4, 7, 9.89),
    COLES_WATERCRESS(Food.WATERCRESS_RAW, 20, 4, 7, 2.98),
    COLES_YOGHURT_GREEK_STYLE_NATURAL(Food.YOGHURT_GREEK_STYLE_NATURAL, 1000, 10, 14, 6.92),
    COLES_YOGHURT_GREEK_STYLE_NATURAL_LIGHT(Food.YOGHURT_GREEK_STYLE_REGULAR_FAT_NATURAL, 1000, 10, 14, 6.92),
    COLES_ZUCCHINI(Food.ZUCCHINI_GREEN_SKIN_FRESH_OR_FROZEN_PEELED_OR_UNPEELED_RAW, 210, 2, 7, 1.26),
    COLES_ZUCCHINI_ORGANIC(Food.ZUCCHINI_GREEN_SKIN_FRESH_OR_FROZEN_PEELED_OR_UNPEELED_RAW, 500, 4, 7, 5.00),

    TEST_CARBOHYDRATES(Food.TEST_CARBOHYDRATES, 1000, 1000, 7, 0.0),
    TEST_FAT(Food.TEST_FAT, 1000, 1000, 7, 0.0),
    TEST_PROTEIN(Food.TEST_PROTEIN, 1000, 1000, 7, 0.0);

    private final Food food;
    private final double weight;
    private final int portions;
    private final int shelfLife;
    private final double price;
    private final LazyValue<FoodProperties> properties;

    /**
     * @param food      Food
     * @param weight    Item weight in g
     * @param portions  Portions per item (After how many meals should an item be finished at the latest?)
     * @param shelfLife Shelf life (After how many days should an item be finished?)
     * @param price     AUD
     */
    FoodItem(final Food food, final double weight, final int portions, final int shelfLife, double price) {
        this.food = food;
        this.weight = weight;
        this.portions = portions;
        this.shelfLife = shelfLife;
        this.price = price;

        properties = new LazyValue<FoodProperties>() {
            @Override
            protected FoodProperties compute() {
                final FoodProperties properties = new FoodProperties();
                final FoodProperties foodProperties = food.getProperties();
                final double weightFactor = weight / 100.0; // 100g to item weight
                foodProperties.forEach(new BiConsumer<FoodProperty, Double>() {
                    @Override
                    public void accept(final FoodProperty foodProperty, final Double amount) {
                        properties.set(foodProperty, amount * weightFactor);
                    }
                });
                return properties;
            }
        };
    }

    public String getName() {
        return food.getName();
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public double getPrice() {
        return price;
    }

    public FoodProperties getProperties() {
        return properties.get();
    }

    public double getProperty(final FoodProperty foodProperty) {
        return getProperties().get(foodProperty);
    }

    /**
     * @param weight Weight in g
     * @return amount (number of items)
     */
    public double weightToAmount(final double weight) {
        return weight / this.weight;
    }

    /**
     * @param amount Amount (number of items)
     * @return weight in g
     */
    public double amountToWeight(final double amount) {
        return amount * weight;
    }

    /**
     * @param portions Portions
     * @return amount (number of items)
     */
    public double portionsToAmount(final int portions) {
        return (double) portions / this.portions;
    }

    /**
     * @param amount Amount (number of items)
     * @return portions
     */
    public int amountToPortions(final double amount) {
        return (int) round(amount * portions);
    }

    @Override
    public String toString() {
        return "<" + food.getName() + "; " + weight + "g>";
    }
}
