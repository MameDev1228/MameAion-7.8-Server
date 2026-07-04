package com.aionemu.gameserver.model.gameobjects.player.fame;

public enum  FameExp {

    LEVEL1(1, 128520),
    LEVEL2(2, 257040),
    LEVEL3(3, 385560),
    LEVEL4(4, 514080),
    LEVEL5(5, 642600),
    LEVEL6(6, 742600),
    LEVEL7(7, 842600),
    LEVEL8(8, 942600),
    LEVEL9(9, 1285200);

    int level;
    long exp;

    FameExp(int level, long exp) {
        this.level = level;
        this.exp = exp;
    }

    public static FameExp getFameExp(int value) {
        for (FameExp pc : values()) {
            if (pc.getLevel() == value) {
                return pc;
            }
        }
        throw new IllegalArgumentException("There is no fame level with id " + value);
    }

    public long getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }
}
