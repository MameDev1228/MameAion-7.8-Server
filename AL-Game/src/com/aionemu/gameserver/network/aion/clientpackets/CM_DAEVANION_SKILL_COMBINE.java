package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.enchant.EnchantService;

import java.util.ArrayList;

public class CM_DAEVANION_SKILL_COMBINE extends AionClientPacket {

    public CM_DAEVANION_SKILL_COMBINE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }
    private ArrayList<Integer> sacrificeBook = new ArrayList<Integer>();
    private int count;

    @Override
    protected void readImpl() {
        readD();
        this.count = readH();
        for (int i = 0; i < count; i++) {
            sacrificeBook.add(readD());
        }
    }

    @Override
    protected void runImpl() {
        EnchantService.combineDaevanionBook(getConnection().getActivePlayer(), sacrificeBook);
    }
}
