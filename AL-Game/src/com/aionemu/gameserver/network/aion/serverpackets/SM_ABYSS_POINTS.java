package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ABYSS_POINTS extends AionServerPacket
{
    @Override
    protected void writeImpl(AionConnection con) {
        writeB(new byte[12]);
        writeD(1200);
        writeB(new byte[8]);
        writeD(4220);
        writeB(new byte[8]);
        writeD(10990);
        writeB(new byte[8]);
        writeD(23500);
        writeB(new byte[8]);
        writeD(42780);
        writeB(new byte[8]);
        writeD(69700);
        writeB(new byte[8]);
        writeD(105600);
        writeB(new byte[8]);
        writeD(150800);
        writeB(new byte[16]);
        writeD(2650);
        writeB(new byte[8]);
        writeD(7788);
        writeB(new byte[8]);
        writeD(13261);
        writeB(new byte[8]);
        writeD(19971);
        writeB(new byte[8]);
        writeD(32418);
        writeB(new byte[8]);
        writeD(42252);
        writeB(new byte[8]);
        writeD(50178);
        writeB(new byte[8]);
        writeD(59084);
        writeB(new byte[8]);
        writeD(66306);
    }
}