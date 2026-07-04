package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_LUMIEL_TRANSFORM_EXP extends AionServerPacket {

    private int lumielId;
    private long exp;

    public SM_LUMIEL_TRANSFORM_EXP(int lumielId, long exp) {
        this.lumielId = lumielId;
        this.exp = exp;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.lumielId); //size
        writeD((int) this.exp); //id
    }
}
