package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_TEST extends AionServerPacket {

    private int unk;
    private int unk2;

    public SM_TEST(int unk, int unk2) {
        this.unk = unk;
        this.unk2 = unk2;
    }


    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.unk); ////object Id ??
        writeC(this.unk2);
    }
}
