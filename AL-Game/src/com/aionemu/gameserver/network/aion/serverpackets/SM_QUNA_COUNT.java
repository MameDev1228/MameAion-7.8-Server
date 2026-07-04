package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_QUNA_COUNT extends AionServerPacket
{
    private int action;
    private int type;
    private long count;

    public SM_QUNA_COUNT(int action, int type, long count) {
        this.action = action;
        this.type = type;
        this.count = count;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(this.action);
        writeC(this.type);
        writeQ(this.count);
    }
}