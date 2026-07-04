package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.monsterCore.MonsterCore;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_MONSTER_CORE_ADD extends AionServerPacket {

    private MonsterCore core;
    private Player player;

    public SM_MONSTER_CORE_ADD(Player player, MonsterCore core) {
        this.core = core;
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(core.getId()); // Cube
        writeD(core.getLevel()); // Rank
        writeD(core.getCollect()); // Level
        writeD(core.getLevel() == core.getMt().getMaxRank() ? 0 : (int) player.getInventory().getItemCountByItemId(core.getMt().getItemId()));
    }
}
