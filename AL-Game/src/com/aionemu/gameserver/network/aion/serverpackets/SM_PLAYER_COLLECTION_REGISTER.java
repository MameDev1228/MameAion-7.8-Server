package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_PLAYER_COLLECTION_REGISTER extends AionServerPacket {

    private final PlayerCollectionEntry entry;

    public SM_PLAYER_COLLECTION_REGISTER(PlayerCollectionEntry entry) {
        this.entry = entry;
    }

    @Override
    protected void writeImpl(AionConnection con) {
            writeD(entry.getId());
            writeC(entry.isItem1() ?  1 : 0);
            writeC(entry.isItem2() ?  1 : 0);
            writeC(entry.isItem3() ?  1 : 0);
            writeC(entry.isItem4() ?  1 : 0);
            writeC(entry.isItem5() ?  1 : 0);
            writeC(entry.isItem6() ?  1 : 0);
            writeC(entry.isItem7() ?  1 : 0);
            writeC(entry.isItem8() ?  1 : 0);
            writeC(entry.isItem9() ?  1 : 0);
            writeC(entry.isItem10() ?  1 : 0);
            writeC(entry.isItem11() ?  1 : 0);
            writeC(entry.isItem12() ?  1 : 0);
            writeC(entry.isItem13() ?  1 : 0);
            writeC(entry.isItem14() ?  1 : 0);
    }
}
