package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Created by wanke on 14/02/2017.
 */
public class SM_0x125 extends AionServerPacket
{
    @Override
    protected void writeImpl(AionConnection con) {
        writeC(0);
        writeC(1);
    }
}