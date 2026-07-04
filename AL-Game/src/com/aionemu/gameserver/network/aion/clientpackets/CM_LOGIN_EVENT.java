package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

import com.aionemu.gameserver.services.LoginEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_LOGIN_EVENT extends AionClientPacket
{
    private int passportId;

    public CM_LOGIN_EVENT(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        passportId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        if (player == null) {
            return;
        }
        LoginEventService.getInstance().getReward(player, passportId);
    }
}
