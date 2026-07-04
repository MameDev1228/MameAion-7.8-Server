package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_DAEVANION_SKILL_COMBINE extends AionServerPacket {

    private int count;
    private int result;

    public SM_DAEVANION_SKILL_COMBINE(int count, int result) {
        this.count = count;
        this.result = result;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(1);
        writeD(this.result);
        writeD(this.result); //itemTemplateId?
        writeD(this.result); //itemTemplateId?
    }
}
