package com.aionemu.gameserver.network.aion.serverpackets.unk_60;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_UNK_106 extends AionServerPacket {

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(9);
        writeH(3);
        writeC(0);
        writeD(0);
    }
}
