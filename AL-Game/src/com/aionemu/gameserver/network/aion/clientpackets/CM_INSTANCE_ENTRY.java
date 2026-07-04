package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.InstanceEntryCostEnum;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.InstanceEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_INSTANCE_ENTRY extends AionClientPacket
{
    private static Logger log = LoggerFactory.getLogger(CM_INSTANCE_ENTRY.class);
	
    @SuppressWarnings("unused")
    private int synchId;
    private InstanceEntryCostEnum type;
	
    public CM_INSTANCE_ENTRY(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        this.synchId = readD();
        readD();
		//0: Kinah.
		//1: Pc Cafe Coin.
        //2 : Luna
        this.type = InstanceEntryCostEnum.getCotstId(readC());
    }
	
    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        int worldId = DataManager.INSTANCE_COOLTIME_DATA.getSynchId(this.synchId);
        InstanceEntryService.getInstance().onResetInstanceEntry(player, worldId, this.type);
    }
}