package com.aionemu.gameserver.model.gameobjects.item;

public class ItemRndBonus {

    private int bonus;
    private int value;

    public ItemRndBonus(int bonus, int value) {
        this.bonus = bonus;
        this.value = value;
    }

    public int getBonus() {
        return bonus;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
