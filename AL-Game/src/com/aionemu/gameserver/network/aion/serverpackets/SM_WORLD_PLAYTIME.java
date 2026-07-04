package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_WORLD_PLAYTIME extends AionServerPacket {

    private Player player;

    public SM_WORLD_PLAYTIME(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.player.getCommonData().getWorldPlayTime());
    }
}
