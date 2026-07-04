package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.monsterCore.MonsterCore;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_MONSTER_CORE_LIST extends AionServerPacket {

    private Player player;

    public SM_MONSTER_CORE_LIST(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(player.getPlayerMonsterCore().size()); //size
        for(MonsterCore core : player.getPlayerMonsterCore().values()) {
            writeD(core.getId()); // Cube
            writeD(core.getLevel()); // Rank
            writeD(core.getCollect()); // Level
            writeD(core.getLevel() == core.getMt().getMaxRank() ? 0 : (int) player.getInventory().getItemCountByItemId(core.getMt().getItemId()));
        }
    }
}