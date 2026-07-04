package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.LumielTransform;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_LUMIEL_TRASFORM extends AionServerPacket {

    private final Player player;

    public SM_LUMIEL_TRASFORM(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(this.player.getPlayerLumiel().size()); //size
        for (LumielTransform lumiel : this.player.getPlayerLumiel().values()) {
            writeD(lumiel.getId()); //id
            writeQ(lumiel.getPoints()); //exp
            writeQ(0);
            writeC(0);
            writeC(0);
        }
    }
}
