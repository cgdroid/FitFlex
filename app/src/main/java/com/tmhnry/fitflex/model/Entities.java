package com.tmhnry.fitflex.model;

import android.content.Context;

import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entities {
    private static Entities instance;
    private List<Entity> entities;
    private Map<Integer, List<Entity.Session>> batches;

    private Entities(List<Entity> entities, Map<Integer, List<Entity.Session>> batches) {
        this.entities = entities;
        this.batches = batches;
    }

    private Entities(List<Entity> entities) {
        this.entities = entities;
        this.batches = new HashMap<>();
    }

    public Map<Integer, List<Entity.Session>> getBatches() {
        return batches;
    }

    public List<Entity.Session> getSessions(int batchId) {
        return batches.containsKey(batchId) ? batches.get(batchId) : new ArrayList<>();
    }

    public enum Category {
        BELLY_TIER_ONE(0, R.drawable.img_belly_workout, 0),
        BELLY_TIER_TWO(1, R.drawable.img_belly_workout, 0),
        BELLY_TIER_THREE(2, R.drawable.img_belly_workout, 0),
        BELLY_TIER_FOUR(3, R.drawable.img_belly_workout, 0),
        BELLY_TIER_FIVE(4, R.drawable.img_belly_workout, 0),
        BELLY_TIER_SIX(5, R.drawable.img_belly_workout, 0),
        IMMUNITY_BOOSTER(6, R.drawable.img_immunity_booster, 1),
        STAY_IN_SHAPE(7, R.drawable.img_stay_in_shape, 2),
        BAND_WORKOUT(8, R.drawable.img_band_workout, 1);

        private int code;
        private int img;
        private int difficulty;

        Category(int code, int img, int difficulty) {
            this.code = code;
            this.img = img;
            this.difficulty = difficulty;
        }

        public int getCode() {
            return code;
        }

        public int getImg() {
            return img;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public static Category getCategory(int code) {
            switch (code) {
                case 0:
                    return BELLY_TIER_ONE;
                case 1:
                    return BELLY_TIER_TWO;
                case 2:
                    return BELLY_TIER_THREE;
                case 3:
                    return BELLY_TIER_FOUR;
                case 4:
                    return BELLY_TIER_FIVE;
                case 5:
                    return BELLY_TIER_SIX;
                case 6:
                    return IMMUNITY_BOOSTER;
                case 7:
                    return STAY_IN_SHAPE;
                default:
                    return BAND_WORKOUT;
            }
        }
    }

    public static Entities getInstance() {
        assert (instance != null);
        return instance;
    }


    public static Entities getInstance(Context context) {

        if (instance == null) {
            int id = 0;
            List<Entity> entities = new ArrayList<>();

            String[] jsonStrings = Tools.getDatasets(context);
            for (String js : jsonStrings) {
                Map<String, Object> data = fromJson(context, js);
                for (String sid : data.keySet()) {
                    Map<String, Object> body = new HashMap<>();
                    try {
                        body = (HashMap<String, Object>) data.get(sid);
                        entities.add(Entity.fromJson(id, body));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    id++;
                }
            }
            instance = new Entities(entities);
        }
        return instance;
    }

    public List<Entity> getEntities(Category category) {
        List<Entity> matches = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getCategory().equals(category)) {
                matches.add(entity);
            }
        }
        return matches;
    }

    public List<Entity> getEntities(int categoryId) {
        List<Entity> matches = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getCategory().getCode() == categoryId) {
                matches.add(entity);
            }
        }
        return matches;
    }


    private static Map<String, Object> fromJson(Context context, String js) {
        Map<String, Object> data = new HashMap<>();
        try {
            data = new ObjectMapper().readValue(js, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
