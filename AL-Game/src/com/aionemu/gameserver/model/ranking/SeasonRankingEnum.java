package com.aionemu.gameserver.model.ranking;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public enum  SeasonRankingEnum
{
    INFINITY(102),
    ARENA_6VS6(541),
    GLORY_POINT(1000),
	DAMAGE(461);
	
    private int tableId;
	
    private SeasonRankingEnum(int tableId) {
        this.tableId = tableId;
    }
	
    public int getId() {
        return tableId;
    }
}