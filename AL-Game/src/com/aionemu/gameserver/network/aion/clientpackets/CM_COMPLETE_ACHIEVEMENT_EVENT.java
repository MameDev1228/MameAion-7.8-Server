package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.AchievementService;

public class CM_COMPLETE_ACHIEVEMENT_EVENT extends AionClientPacket {

    private int templateId;
    private long ObjectId;

    public CM_COMPLETE_ACHIEVEMENT_EVENT(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.templateId = readD();
        this.ObjectId = readQ();
        readC();
    }

    @Override
    protected void runImpl() {
        AchievementService.getInstance().completeAchievementEvent(getConnection().getActivePlayer(), this.templateId);
    }
}
