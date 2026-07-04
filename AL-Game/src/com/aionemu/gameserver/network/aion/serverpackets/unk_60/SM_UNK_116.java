package com.aionemu.gameserver.network.aion.serverpackets.unk_60;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_UNK_116 extends AionServerPacket {

    private String bytes;

    public SM_UNK_116(String bytes) {
        this.bytes = bytes;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeB(bytes);
    }
}
