package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_DAEVA_MEMBERSHIP extends AionServerPacket
{
    private int action;
    private int id;
    private int unk;
	
    public SM_DAEVA_MEMBERSHIP(int action, int id, int unk) {
        this.action = action;
        this.id = id;
        this.unk = unk;
    }
	
    protected void writeImpl(AionConnection con) {
        writeC(this.action);
        writeC(this.id); //level
        writeQ(this.unk); //unk 20
    }
}