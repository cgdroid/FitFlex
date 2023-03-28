package com.tmhnry.fitflex.model;

import android.content.Context;

import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.miscellaneous.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Foods {
    private static final String NAME = "name";
    private static final String CATEGORY = "category";
    private static final String QUANTITY = "quantity";
    private static final String GRAMS = "grams";
    private static final String CALORIES = "calories";
    private static final String PROTEIN = "protein";
    private static final String FAT = "fat";
    private static final String SATURATED_FAT = "saturated-fat";
    private static final String FIBER = "fiber";
    private static final String CARBS = "carbs";

    private static Foods instance;
    private List<Model> models;
    private List<Category> categories;

    private Foods(List<Model> models) {
        this.models = models;
        this.categories = new ArrayList<>();
        categories.add(Category.DAIRY_PRODUCTS);
        categories.add(Category.FATS_OILS_SHORTENINGS);
        categories.add(Category.MEAT_POULTRY);
        categories.add(Category.FISH_SEAFOOD);
        categories.add(Category.VEGETABLES_A_E);
        categories.add(Category.VEGETABLES_F_P);
        categories.add(Category.VEGETABLES_R_Z);
        categories.add(Category.FRUITS_A_F);
        categories.add(Category.FRUITS_G_P);
        categories.add(Category.FRUITS_R_Z);
        categories.add(Category.BREADS_CEREALS_FASTFOOD_GRAINS);
        categories.add(Category.SOUPS);
        categories.add(Category.DESSERTS_SWEETS);
        categories.add(Category.JAMS_JELLIES);
        categories.add(Category.SEEDS_AND_NUTS);
        categories.add(Category.DRINKS_ALCOHOL_BEVERAGES);
    }

    public static List<Model> getModels(Category category) {
        List<Model> matches = new ArrayList<>();

        for (Model model : instance.models) {
            if (model.getCategory().equals(category)) {
                matches.add(model);
            }
        }
        return matches;

    }

    public static List<Model> getModels(int categoryId) {
        List<Model> matches = new ArrayList<>();

        for (Model model : instance.models) {
            if (model.getCategory().code == categoryId) {
                matches.add(model);
            }
        }
        return matches;

    }

    public static List<Category> getCategories() {
        return instance.categories;
    }

    public enum Category {
        DAIRY_PRODUCTS(0, "Dairy Products", R.drawable.img_dairy_products),
        FATS_OILS_SHORTENINGS(1, "Fats, Oils, Shortenings", R.drawable.img_fats_oils_shortenings),
        MEAT_POULTRY(2, "Meat, Poultry", R.drawable.img_meat_poultry),
        FISH_SEAFOOD(3, "Fish, Seafood", R.drawable.img_fish_seafood),
        VEGETABLES_A_E(4, "Vegetables A-E", R.drawable.img_vegetables),
        VEGETABLES_F_P(5, "Vegetables F-P", R.drawable.img_vegetables),
        VEGETABLES_R_Z(6, "Vegetables R-Z", R.drawable.img_vegetables),
        FRUITS_A_F(7, "Fruits A-F", R.drawable.img_fruits),
        FRUITS_G_P(8, "Fruits G-P", R.drawable.img_fruits),
        FRUITS_R_Z(9, "Fruits R-Z", R.drawable.img_fruits),
        BREADS_CEREALS_FASTFOOD_GRAINS(10, "Breads, Cereals, Fastfood, Grains", R.drawable.img_breads_cereals_fastfood_grains),
        SOUPS(11, "Soups", R.drawable.img_soups),
        DESSERTS_SWEETS(12, "Desserts, Sweets", R.drawable.img_desserts_sweets),
        JAMS_JELLIES(13, "Jams, Jellies", R.drawable.img_jams_jellies),
        SEEDS_AND_NUTS(14, "Seeds and Nuts", R.drawable.img_seeds_nuts),
        DRINKS_ALCOHOL_BEVERAGES(15, "Drinks, Alcohol, Beverages", R.drawable.img_drinks_alcohol_beverages);

        private String name;
        private int code;
        private int imageId;

        Category(int code, String name, int imageId) {
            this.name = name;
            this.code = code;
            this.imageId = imageId;
        }

        public double getAverageCalories(){
            double calories = 0;

            List<Foods.Model> models = Foods.getModels(this);
            for(Foods.Model model : models){
                calories += model.getCalories();
            }

            return calories / models.size();
        }

        public static Category getCategory(int categoryId){
            switch (categoryId){
                case 0:
                    return DAIRY_PRODUCTS;
                case 1:
                    return FATS_OILS_SHORTENINGS;
                case 2:
                    return MEAT_POULTRY;
                case 3:
                    return  FISH_SEAFOOD;
                case 4:
                    return VEGETABLES_A_E;
                case 5:
                    return  VEGETABLES_F_P;
                case 6:
                    return VEGETABLES_R_Z;
                case 7:
                    return FRUITS_A_F;
                case 8:
                    return FRUITS_G_P;
                case 9:
                    return FRUITS_R_Z;
                case 10:
                    return BREADS_CEREALS_FASTFOOD_GRAINS;
                case 11:
                    return SOUPS;
                case 12:
                    return DESSERTS_SWEETS;
                case 13:
                    return  JAMS_JELLIES;
                case 14:
                    return SEEDS_AND_NUTS;
                default:
                    return DRINKS_ALCOHOL_BEVERAGES;
            }
        }

        public int getImageId() {
            return imageId;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    private static Category getCategory(String category) {

        if (category.equals(Category.DAIRY_PRODUCTS.getName())) {
            return Category.DAIRY_PRODUCTS;
        }
        if (category.equals(Category.FATS_OILS_SHORTENINGS.getName())) {
            return Category.FATS_OILS_SHORTENINGS;
        }
        if (category.equals(Category.MEAT_POULTRY.getName())) {
            return Category.MEAT_POULTRY;
        }
        if (category.equals(Category.FISH_SEAFOOD.getName())) {
            return Category.FISH_SEAFOOD;
        }
        if (category.equals(Category.VEGETABLES_A_E.getName())) {
            return Category.VEGETABLES_A_E;
        }
        if (category.equals(Category.VEGETABLES_F_P.getName())) {
            return Category.VEGETABLES_F_P;
        }
        if (category.equals(Category.VEGETABLES_R_Z.getName())) {
            return Category.VEGETABLES_R_Z;
        }
        if (category.equals(Category.FRUITS_A_F.getName())) {
            return Category.FRUITS_A_F;
        }
        if (category.equals(Category.FRUITS_G_P.getName())) {
            return Category.FRUITS_G_P;
        }
        if (category.equals(Category.FRUITS_R_Z.getName())) {
            return Category.FRUITS_R_Z;
        }
        if (category.equals(Category.BREADS_CEREALS_FASTFOOD_GRAINS.getName())) {
            return Category.BREADS_CEREALS_FASTFOOD_GRAINS;
        }
        if (category.equals(Category.SOUPS.getName())) {
            return Category.SOUPS;
        }
        if (category.equals(Category.DESSERTS_SWEETS.getName())) {
            return Category.DESSERTS_SWEETS;
        }
        if (category.equals(Category.JAMS_JELLIES.getName())) {
            return Category.JAMS_JELLIES;
        }
        if (category.equals(Category.SEEDS_AND_NUTS.getName())) {
            return Category.SEEDS_AND_NUTS;
        }
        return Category.DRINKS_ALCOHOL_BEVERAGES;

    }

    public class Model {
        private final String name;
        private final String quantity;
        private final Category category;
        private final double grams;
        private final double calories;
        private final double protein;
        private final double fat;
        private final double saturatedFat;
        private final double fiber;
        private final double carbs;

        public String getName() {
            return name;
        }

        public double getGrams() {
            return grams;
        }

        public double getCalories() {
            return calories;
        }

        public double getProtein() {
            return protein;
        }

        public double getFat() {
            return fat;
        }

        public double getCarbs() {
            return carbs;
        }

        public double getSaturatedFat() {
            return saturatedFat;
        }

        public double getFiber() {
            return fiber;
        }

        public String getQuantity() {
            return quantity;
        }

        public Model(
                String name,
                String quantity,
                double grams,
                double calories,
                double protein,
                double fat,
                double saturatedFat,
                double fiber,
                double carbs,
                String category
        ) {
            this.name = name;
            this.quantity = quantity;
            this.grams = grams;
            this.calories = calories;
            this.protein = protein;
            this.fat = fat;
            this.saturatedFat = saturatedFat;
            this.fiber = fiber;
            this.carbs = carbs;
            this.category = Foods.getCategory(category);
        }



        public Category getCategory() {
            return category;
        }
    }

    private void addModel(Map<String, Object> properties) {
        instance.models.add(instance.new Model((String) properties.get(NAME),
                (String) properties.get(QUANTITY),
                (Double) properties.get(GRAMS),
                (Double) properties.get(CALORIES),
                (Double) properties.get(PROTEIN),
                (Double) properties.get(FAT),
                (Double) properties.get(SATURATED_FAT),
                (Double) properties.get(FIBER),
                (Double) properties.get(CARBS),
                (String) properties.get(CATEGORY)
        ));
    }


    public static class Retriever implements Runnable {
        private Context context;
        private int data;

        public Retriever(Context context) {
            this.context = context;
        }

        public Retriever setData(int data) {
            this.data = data;
            return this;
        }

    private static final int PROPERTY_INT = 0;
    private static final int PROPERTY_STRING = 1;
    private static final int PROPERTY_DOUBLE = 2;

    private void updateProperty(int type,
                                String[] tokens,
                                AtomicInteger index,
                                String propertyName,
                                Map<String, Object> properties) {

        assert (properties.containsKey(propertyName));

        if (type == PROPERTY_DOUBLE) {
            String property = tokens[index.get()];
            index.incrementAndGet();

            if (property.contains("\"")) {
                while (!(tokens.length < index.get() + 1) && !(tokens[index.get()].contains("\""))) {
                    property += ("," + tokens[index.get()]);
                    index.incrementAndGet();
                }
                property += ("," + tokens[index.get()]);
                index.incrementAndGet();
            }

            property = property.replaceAll("[\\(\\)\"]", "");
            property = property.replaceAll(",", "");

            properties.replace(propertyName, Tools.toDouble(property));
        }

        if (type == PROPERTY_STRING) {
            String property = tokens[index.get()];


            index.incrementAndGet();
            if (property.contains("\"") && (property.indexOf("\"") == property.lastIndexOf("\""))) {
                while (!(tokens.length < index.get() + 1) && !(tokens[index.get()].contains("\""))) {
                    property += ("," + tokens[index.get()]);
                    index.incrementAndGet();
                }
                if (!(tokens.length < index.get() + 1)) {
                    property += ("," + tokens[index.get()]);
                }
                index.incrementAndGet();
            }
//
            property = property.replaceAll("[\\(\\)\"]", "");
            property = property.substring(0, 1).toUpperCase() + property.substring(1, property.length());

            properties.replace(propertyName, property);
        }
    }

    @Override
    public void run() {
        if (instance == null) {
            instance = new Foods(new ArrayList<Model>());

            InputStream is = context.getResources().openRawResource(data);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );

            String line = "";
            try {
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");

                    Map<String, Object> properties = new HashMap<>();
                    properties.put(NAME, "");
                    properties.put(CATEGORY, "");
                    properties.put(QUANTITY, "");
                    properties.put(GRAMS, new Double(0));
                    properties.put(CALORIES, new Double(0));
                    properties.put(PROTEIN, new Double(0));
                    properties.put(FAT, new Double(0));
                    properties.put(SATURATED_FAT, new Double(0));
                    properties.put(FIBER, new Double(0));
                    properties.put(CARBS, new Double(0));

                    AtomicInteger n = new AtomicInteger(0);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }
//
                    updateProperty(PROPERTY_STRING, tokens, n, NAME, properties);
//
                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_STRING, tokens, n, QUANTITY, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_DOUBLE, tokens, n, GRAMS, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_DOUBLE, tokens, n, CALORIES, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_DOUBLE, tokens, n, PROTEIN, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_DOUBLE, tokens, n, FAT, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_DOUBLE, tokens, n, SATURATED_FAT, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_DOUBLE, tokens, n, FIBER, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_DOUBLE, tokens, n, CARBS, properties);

                    if (tokens.length < n.get() + 1) {
                        instance.addModel(properties);
                        break;
                    }

                    updateProperty(PROPERTY_STRING, tokens, n, CATEGORY, properties);

                    instance.addModel(properties);
                }
            } catch (IOException e) {
//                    Log.wtf(context.toString(), "Error reading data file on line " + line, e);
//                    e.printStackTrace();
            }
        }
    }
}
}
