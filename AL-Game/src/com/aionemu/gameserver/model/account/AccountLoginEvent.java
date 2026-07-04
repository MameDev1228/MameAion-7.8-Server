package com.aionemu.gameserver.model.account;

public class AccountLoginEvent {

    private int id;
    private int stamp;
    private int collected;
    private boolean available;

    public AccountLoginEvent(int id, int stamp, boolean available, int collected) {
        this.id = id;
        this.stamp = stamp;
        this.available = available;
        this.collected = collected;
    }

    public int getStamp() {
        return stamp;
    }

    public void setStamp(int stamp) {
        this.stamp = stamp;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean claimed) {
        this.available = claimed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }
}
