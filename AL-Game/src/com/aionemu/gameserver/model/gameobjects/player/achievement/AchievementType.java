package com.aionemu.gameserver.model.gameobjects.player.achievement;

public enum  AchievementType {

    DAILY(1),
    WEEKLY(2),
    EVENT_MAIN(4),
    EVENT_SUB(5);

    private int value;

    AchievementType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AchievementType getAchievementTypeByString(String fieldName) {
        for (AchievementType at: values()) {
            if (at.toString().equals(fieldName)) {
                return at;
            }
        }
        return null;
    }

    public static AchievementType getPlayerClassById(int classId) {
        for (AchievementType pc : values()) {
            if (pc.getValue() == classId) {
                return pc;
            }
        }
        throw new IllegalArgumentException("There is no player class with id " + classId);
    }
}
