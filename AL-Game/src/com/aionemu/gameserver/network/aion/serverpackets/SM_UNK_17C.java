package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_UNK_17C extends AionServerPacket
{
    @Override
    protected void writeImpl(AionConnection con) {
        writeB("010032CDE9000000000001010000000000000000000000000000000000000000000000");
    }
}