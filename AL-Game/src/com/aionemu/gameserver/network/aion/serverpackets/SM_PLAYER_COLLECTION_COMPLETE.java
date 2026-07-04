package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_PLAYER_COLLECTION_COMPLETE extends AionServerPacket {

    private final PlayerCollectionEntry entry;

    public SM_PLAYER_COLLECTION_COMPLETE(PlayerCollectionEntry entry) {
        this.entry = entry;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(entry.getId());
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
    }
}
