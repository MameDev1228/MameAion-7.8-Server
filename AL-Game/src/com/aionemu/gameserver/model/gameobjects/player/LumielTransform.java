package com.aionemu.gameserver.model.gameobjects.player;

public class LumielTransform {

    private int id;
    private long points;

    public LumielTransform(int id, long points) {
        this.id = id;
        this.points = points;
    }


    public int getId() {
        return id;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }
}
