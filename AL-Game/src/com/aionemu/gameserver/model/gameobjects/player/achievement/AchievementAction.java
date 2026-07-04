package com.aionemu.gameserver.model.gameobjects.player.achievement;

import com.aionemu.gameserver.utils.idfactory.IDFactory;

import java.sql.Timestamp;

public class AchievementAction {

    private int objectId;
    private int id;
    private AchievementType type;
    private AchievementState state;
    private int step;
    private Timestamp startDate;
    private Timestamp endateDate;
    private int achievementObjectId;


    public AchievementAction(int id, AchievementType type, AchievementState state, int step, Timestamp startDate, Timestamp endateDate, int achievementObjectId) {
        this.objectId = IDFactory.getInstance().nextId();
        this.id = id;
        this.type = type;
        this.state = state;
        this.step = step;
        this.startDate = startDate;
        this.endateDate = endateDate;
        this.achievementObjectId = achievementObjectId;
    }

    public AchievementAction(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AchievementType getType() {
        return type;
    }

    public void setType(AchievementType type) {
        this.type = type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndateDate() {
        return endateDate;
    }

    public void setEndateDate(Timestamp endateDate) {
        this.endateDate = endateDate;
    }

    public int getAchievementObjectId() {
        return achievementObjectId;
    }

    public void setAchievementObjectId(int achievementObjectId) {
        this.achievementObjectId = achievementObjectId;
    }

    public AchievementState getState() {
        return state;
    }

    public void setState(AchievementState state) {
        this.state = state;
    }
}
