package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_PLAYER_COLLECTION_FINISH extends AionServerPacket {

    private final Player player;

    public SM_PLAYER_COLLECTION_FINISH(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(this.player.getPlayerCollection().getCompleteCollection().size());//size
        for (PlayerCollectionEntry finish : this.player.getPlayerCollection().getCompleteCollection()) {
            writeD(finish.getId());
        }
    }
}
