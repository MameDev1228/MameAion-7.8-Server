package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ESTIMA_BUFF extends AionServerPacket {

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(0);
    }
}
