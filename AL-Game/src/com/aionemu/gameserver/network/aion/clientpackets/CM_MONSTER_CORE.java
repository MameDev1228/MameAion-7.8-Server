package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.account.MonsterCoreService;

public class CM_MONSTER_CORE extends AionClientPacket
{
    private int coreId;
	
    public CM_MONSTER_CORE(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        this.coreId = readD();
    }
	
    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        int count = (int) player.getInventory().getItemCountByItemId(DataManager.MONSTER_CORE_DATA.getMonsterCoreId(coreId).getItemId());
        if (player != null) {
            for (int i = 0; i < count; i++) {
                MonsterCoreService.getInstance().onUseMonsterCore(player, this.coreId);
            }
        }
    }
}