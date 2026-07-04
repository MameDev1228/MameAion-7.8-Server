package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.player.PlayerFameService;

public class SM_PLAYER_FAME extends AionServerPacket {

    private final Player player;

    public SM_PLAYER_FAME(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(1);
        writeD(PlayerFameService.getInstance().MAX_LEVEL);
        writeH(player.getPlayerFame().size());
        for(PlayerFame fame : player.getPlayerFame().values()) {
            writeD(fame.getId());
            writeD(fame.getLevel());
            writeD(0);
            writeQ(fame.getExp());
            writeQ(fame.getMaxExp());
            writeQ(fame.getExpLoss());
        }
    }
}
