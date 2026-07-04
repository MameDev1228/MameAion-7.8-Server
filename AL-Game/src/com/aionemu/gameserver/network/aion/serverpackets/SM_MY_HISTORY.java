package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.ranking.*;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public class SM_MY_HISTORY extends AionServerPacket
{
    private int tableId;
    private GloryPointRank glory;
    private InfinityRank infinity;
	private DamageRank damage;
    private Arena6VS6Ranking arena6VS6;
	
    public SM_MY_HISTORY(int tableId, GloryPointRank ranking) {
        this.tableId = tableId;
        this.glory = ranking;
    }
	
    public SM_MY_HISTORY(int tableId, InfinityRank ranking) {
        this.tableId = tableId;
        this.infinity = ranking;
    }
	
    public SM_MY_HISTORY(int tableId, Arena6VS6Ranking ranking) {
        this.tableId = tableId;
        this.arena6VS6 = ranking;
    }

	public SM_MY_HISTORY(int tableId, DamageRank ranking) {
        this.tableId = tableId;
        this.damage = ranking;
    }
	
    protected void writeImpl(AionConnection paramAionConnection) {
        writeD(tableId);
        switch (tableId) {
            case 541: //Arena Of Discipline.
                writeD(arena6VS6.getRank()); //actual Rank
                writeD(arena6VS6.getPoints()); //current Points
                writeD(1);
                writeD(arena6VS6.getPossitionMatch()); //possition match
                writeD(arena6VS6.getBestRank()); //lasted rank
                writeD(arena6VS6.getLastPoints()); //last Points
                writeD(arena6VS6.getLowPoints()); //low points
                writeD(arena6VS6.getHighPoints()); //hight points
            return;
            case 1000: //Glory Point Rank.
                writeD(glory.getRank()); //actual Rank
                writeD(glory.getPoints()); //current Points
                writeD(1);
                writeD(glory.getPossitionMatch()); //possition match
                writeD(glory.getBestRank()); //lasted rank
                writeD(glory.getLastPoints()); //last Points
                writeD(glory.getLowPoints()); //low points
                writeD(glory.getHighPoints()); //hight points
            return;
            case 102: //Infinity.
                writeD(infinity.getRank()); //actual Rank
                writeD(infinity.getCurrentTime()); //current Points
                writeD(1);
                writeD(0); //possition match
                writeD(infinity.getBestRank()); //lasted rank
                writeD(infinity.getLastTime()); //last Points
                writeD(infinity.getLowRank()); //low points
                writeD(infinity.getBestTime()); //hight points
            return;
			case 461: //Damage.
                writeD(damage.getRank()); //actual Rank
                writeD(damage.getCurrentScore()); //current Points
                writeD(1);
                writeD(0); //possition match
                writeD(damage.getBestRank()); //lasted rank
                writeD(damage.getLastScore()); //last Points
                writeD(damage.getLowRank()); //low points
                writeD(damage.getBestScore()); //hight points
            return;
            default:
                writeD(0); //actual Rank
                writeD(0); //current Points
                writeD(0);
                writeD(0);
                writeD(0);//lasted rank
                writeD(0); //last Points
                writeD(0); //low points
                writeD(0); //hight points
            return;
        }
    }
}