package com.tmhnry.fitflex.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Entity {
    private final int id;
    private final float calorie;
    private final int typeCount;
    private final String img;
    private final String description;
    private final Entities.Category category;
    private final String name;
    private final Type type;
    private final boolean isAuto;
    private List<Session> sessions;


    private Entity(int id, Entities.Category category, String name, String description, float calorie, Type type, int typeCount, String img, boolean isAuto) {
        this.id = id;
        this.typeCount = typeCount;
        this.calorie = calorie;
        this.description = description;
        this.category = category;
        this.name = name;
        this.type = type;
        this.img = img;
        this.isAuto = isAuto;
        this.sessions = new ArrayList<>();
    }

    public static Entity fromJson(int id, Map<String, Object> body) {
        String description = (String) body.get("description");
        String name = (String) body.get("name");
        String img = (String) body.get("img");

        Object category = body.get("category");
        if (category == null || ((String) category).equals("Immunity Booster")) {
            category = Entities.Category.IMMUNITY_BOOSTER;
        } else if (((String) category).equals("Stay In Shape")) {
            category = Entities.Category.STAY_IN_SHAPE;
        } else if (((String) category).equals("Band Workout")) {
            category = Entities.Category.BAND_WORKOUT;
        } else if (((String) category).equals("Belly Tier One")) {
            category = Entities.Category.BELLY_TIER_ONE;
        } else if (((String) category).equals("Belly Tier Two")) {
            category = Entities.Category.BELLY_TIER_TWO;
        } else if (((String) category).equals("Belly Tier Three")) {
            category = Entities.Category.BELLY_TIER_THREE;
        } else if (((String) category).equals("Belly Tier Four")) {
            category = Entities.Category.BELLY_TIER_FOUR;
        } else if (((String) category).equals("Belly Tier Five")) {
            category = Entities.Category.BELLY_TIER_FIVE;
        } else {
            category = Entities.Category.BELLY_TIER_SIX;
        }

        Object type = body.get("type");
        Object typeCount = body.get("count");
        if (type == null || ((String) type).equals("TIMED")) {
            type = Type.TIMED;
            typeCount = (typeCount == null ? 30 : typeCount);
        } else {
            type = Type.COUNT;
            typeCount = (typeCount == null ? 12 : typeCount);
        }

        Object isAuto = body.get("auto");
        if (((Type) type).equals(Type.TIMED)) {
            isAuto = false;
        } else if (isAuto == null || !((boolean) isAuto)) {
            isAuto = false;
        } else {
            isAuto = true;
        }

        Object calorie = body.get("equivalent-calorie");
        Float exactCalorie = 0f;
        Float exactTypeCount = 0f;
//        if (calorie == null) {
//            calorie = 2;
//        }
//        if(calorie instanceof Integer){
//            calorieExact = new Float((int) calorie);
//        }
//        if(calorie instanceof Double){
//            calorieExact = new Float((double) calorie);
//        }
        if(type == Type.COUNT){
            typeCount = 2 * (int) typeCount;
        }
        if(typeCount instanceof Integer){
            exactTypeCount = new Float((Integer) typeCount);
        }

        exactCalorie = exactTypeCount / 3600f * 460;

        return new Entity(id, (Entities.Category) category, name,
                description, exactCalorie,
                (Type) type, (int) typeCount,
                img, (boolean) isAuto);

    }

    public List<Session> getSessions() {
        return sessions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getIsAuto() {
        return isAuto;
    }

    public String getImg() {
        return img;
    }

    public String getDescription() {
        return description;
    }

    public Entities.Category getCategory() {
        return category;
    }

    public int getTypeCount() {
        return typeCount;
    }

    public Type getType() {
        return type;
    }

    public float getEquivalentCalorie() {
        return calorie;
    }

    public enum Type {
        TIMED,
        COUNT
    }

    public class Session {
        private long duration;
        private int batchId;
        private float rate;
        private Date date;

        public Session(int batchId, Date date, long duration) {
            this.date = date;
            this.batchId = batchId;
            this.duration = duration;
        }

        public Session(int batchId, Date date, long duration, int repetitions) {
            assert (type == Type.COUNT);
            this.batchId = batchId;
            this.date = date;
            this.duration = duration;
            rate = (float) repetitions / typeCount;
        }

        public float getCalories() {
            if (type.equals(Type.TIMED)) {
                return (float) Math.min(duration, typeCount * 1000) / (typeCount * 1000) * calorie;
            }
            return rate * calorie;
        }

        public String getName() {
            return name;
        }

        public long getDuration() {
            return duration;
        }

        public Date getDate() {
            return date;
        }
    }

}

