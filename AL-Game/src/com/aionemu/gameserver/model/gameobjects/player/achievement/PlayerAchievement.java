package com.aionemu.gameserver.model.gameobjects.player.achievement;

import com.aionemu.gameserver.utils.idfactory.IDFactory;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.util.Map;

public class PlayerAchievement {

    private int objectId;
    private int id;
    private AchievementType type;
    private AchievementState state;
    private int step;
    private Timestamp startDate;
    private Timestamp endateDate;
    private Map<Integer, AchievementAction> actionMap = new FastMap<Integer, AchievementAction>();

    public PlayerAchievement(int id, AchievementType type, AchievementState state, int step, Timestamp startDate, Timestamp endateDate) {
        this.objectId = IDFactory.getInstance().nextId();
        this.id = id;
        this.type = type;
        this.state = state;
        this.step = step;
        this.startDate = startDate;
        this.endateDate = endateDate;
    }

    public PlayerAchievement(int objectId) {
        this.objectId = objectId;
    }

    public Map<Integer, AchievementAction> getActionMap() {
        return actionMap;
    }

    public void setActionMap(Map<Integer, AchievementAction> actionMap) {
        this.actionMap = actionMap;
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

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public void setEndateDate(Timestamp endateDate) {
        this.endateDate = endateDate;
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

    public Timestamp getEndateDate() {
        return endateDate;
    }

    public AchievementState getState() {
        return state;
    }

    public void setState(AchievementState state) {
        this.state = state;
    }
}
