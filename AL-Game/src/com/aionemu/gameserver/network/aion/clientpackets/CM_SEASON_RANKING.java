package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SEASON_RANKING;
import com.aionemu.gameserver.services.ranking.SeasonRankingUpdateService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.List;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public class CM_SEASON_RANKING extends AionClientPacket
{
    private int tableId;
    
    public CM_SEASON_RANKING(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        tableId = readD();
    }
	
    @Override
    protected void runImpl() {
        List<SM_SEASON_RANKING> results = SeasonRankingUpdateService.getInstance().getPlayers(tableId);
        for (SM_SEASON_RANKING packet: results) {
            sendPacket(packet);
		}
    }
}