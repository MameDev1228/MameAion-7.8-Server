package com.aionemu.gameserver.model.gameobjects.player.fame;

import com.aionemu.gameserver.model.DescriptionId;

public enum FameEnum
{
    INGGISON(1, 210050000, 1831919),
    GELKMAROS(2, 220070000, 1831921),
    SILENTERA_CANYON(3, 600010000, 1831923),
    LAKRUM(4, 800050000, 1831927),
    DEMAHA(5, 800060000, 1831929),
    CRIMSON_KATALAM(6, 800030000, 1831925),
    CRIMSON_DANARIA(6, 800040000, 1831925),
	IDUNDERPASS_B1(7, 800070000, 1832873);
	
    private int value;
    private int worldId;
    private DescriptionId descriptionId;

    FameEnum(int value, int worlId, int descriptionId) {
        this.value = value;
        this.descriptionId = new DescriptionId(descriptionId);
        this.worldId = worlId;
    }

    public static FameEnum getFameById(int value) {
        for (FameEnum pc : values()) {
            if (pc.getValue() == value) {
                return pc;
            }
        }
        throw new IllegalArgumentException("There is no fame class with id " + value);
    }

    public int getValue() {
        return value;
    }

    public int getWorldId() {
        return worldId;
    }

    public DescriptionId getDescriptionId() {
        return descriptionId;
    }
}
