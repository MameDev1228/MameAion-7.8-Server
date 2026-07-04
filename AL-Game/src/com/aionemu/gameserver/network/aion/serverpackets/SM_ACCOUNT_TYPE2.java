package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ACCOUNT_TYPE2 extends AionServerPacket {

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(1);
    }
}
