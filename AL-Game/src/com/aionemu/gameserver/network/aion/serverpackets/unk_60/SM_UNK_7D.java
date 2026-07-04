package com.aionemu.gameserver.network.aion.serverpackets.unk_60;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_UNK_7D extends AionServerPacket {

    private int type;

    public SM_UNK_7D(int type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        switch (type) {
            case 1:
                writeB("28 00 00 00 10 00 D9 D3 28 00 00 00 28 00 00 00 00 00 00 00 29 5F A2 11 00 00 00 00 19 CF AD 2D FE 3E 99 01 F0 23 D0 BF DC 83 5F 81");
                break;
            case 2:
                writeB("38 00 00 00 F2 C8 19 AB 38 00 00 00 38 00 00 00 00 00 00 00 D2 73 16 A4 00 00 00 00 30 2D 18 86 2D 42 EA 5B C0 28 B0 A2 6F 0C B4 DE 06 82 42 3A 3E 46 A7 1C 73 45 A1 8A 8B 64 25 58");
                break;
            case 3:
                writeB("18 00 00 00 01 1B B1 7B 18 00 00 00 18 00 00 00 00 00 00 00 1A 22 5B 27 00 00 00 00");
                break;
        }
    }
}
