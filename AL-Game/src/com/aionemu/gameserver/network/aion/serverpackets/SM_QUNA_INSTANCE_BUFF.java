package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_QUNA_INSTANCE_BUFF extends AionServerPacket {

    private int buffId;
    private boolean active;

    public SM_QUNA_INSTANCE_BUFF(int buffId, boolean active) {
        this.buffId = buffId;
        this.active = active;
    }

    protected void writeImpl(AionConnection con) {
        writeD(buffId);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
        writeC(active ? 1 : 0);
    }
}
