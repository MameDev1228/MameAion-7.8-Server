package com.aionemu.gameserver.network.aion.serverpackets.unk_60;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_UNK_A5 extends AionServerPacket {

    private int type;

    public SM_UNK_A5(int type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(type);
        writeH(0);
    }
}
