package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.LunaShopService;

public class CM_LUNA_BUFF extends AionClientPacket {

    private int buffId;

    public CM_LUNA_BUFF(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.buffId = readD();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
        readC();
    }

    @Override
    protected void runImpl() {
        LunaShopService.getInstance().buyLunaBuff(getConnection().getActivePlayer(), this.buffId);
    }
}
