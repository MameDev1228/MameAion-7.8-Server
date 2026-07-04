package com.aionemu.gameserver.model.gameobjects.player.fame;

import com.aionemu.gameserver.services.player.PlayerFameService;

public class PlayerFame {

    private int id;
    private int level;
    private long exp;
    private long expLoss;
    private FameEnum fameEnum;
    private int playerId;

    public PlayerFame(int id, int level, long exp, long expLoss, int playerId) {
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.expLoss = expLoss;
        this.fameEnum = FameEnum.getFameById(id);
        this.playerId = playerId;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public Long getMaxExp() {
        return PlayerFameService.getInstance().getExpForLevel(this.level);
    }

    public FameEnum getFameEnum() {
        return fameEnum;
    }

    public long getExpLoss() {
        return expLoss;
    }

    public void setExpLoss(long expLoss) {
        this.expLoss = expLoss;
    }

    public int getPlayerId() {
        return playerId;
    }
}
