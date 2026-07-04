package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_SERVER_SERIAL_CHECK extends AionServerPacket
{
    private int action;
    private String Key1;
    private String Key2;
    private String Key3;
    private int pos;
    private int KeyLenght;
	
    public SM_SERVER_SERIAL_CHECK(int action, String Key1, String Key2, String Key3, int pos, int KeyLenght) {
        this.action = action;
        this.Key1 = Key1;
        this.Key2 = Key2;
        this.Key3 = Key3;
        this.pos = pos;
        this.KeyLenght = KeyLenght;
    }
	
    protected void writeImpl(AionConnection con) {
        writeC(action);
        writeS(Key1);
        writeS(Key2);
        writeS(Key3);
        writeH(pos);
        writeC(KeyLenght);
    }
}