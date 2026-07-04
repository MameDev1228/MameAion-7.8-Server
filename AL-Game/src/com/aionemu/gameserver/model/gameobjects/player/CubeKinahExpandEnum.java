package com.aionemu.gameserver.model.gameobjects.player;

public enum CubeKinahExpandEnum {

    LEVEL0(0,200),
    LEVEL1(1,2000),
    LEVEL2(2,5000),
    LEVEL3(3,150000),
    LEVEL4(4,450000),
    LEVEL5(5,1300000),
    LEVEL6(6,4000000),
    LEVEL7(7,12000000),
    LEVEL8(8,36000000),
    LEVEL9(9,108000000);

    private int value;
    private int cost;

    CubeKinahExpandEnum(int value, int cost) {
        this.value = value;
        this.cost = cost;
    }

    public static CubeKinahExpandEnum getCostById(int value) {
        for (CubeKinahExpandEnum pc : values()) {
            if (pc.getValue() == value) {
                return pc;
            }
        }
        throw new IllegalArgumentException("There is no fame class with id " + value);
    }

    public int getValue() {
        return value;
    }

    public int getCost() {
        return cost;
    }

}
