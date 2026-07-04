package com.aionemu.gameserver.model.gameobjects.player.achievement;

public enum AchievementState {

    START(1),
    REWARD(2),
    COMPLETE(3);

    private int value;

    AchievementState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AchievementState getPlayerClassById(int classId) {
        for (AchievementState pc : values()) {
            if (pc.getValue() == classId) {
                return pc;
            }
        }
        throw new IllegalArgumentException("There is no AchievementState with id " + classId);
    }

}
