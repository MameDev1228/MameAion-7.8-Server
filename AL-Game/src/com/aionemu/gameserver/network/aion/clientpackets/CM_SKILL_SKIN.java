package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wanke on 16/05/2017.
 */

public class CM_SKILL_SKIN extends AionClientPacket
{
    private int SkillId;
    private int SkillSkinId;

    private static final Logger log = LoggerFactory.getLogger(CM_SKILL_SKIN.class);

    public CM_SKILL_SKIN(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    protected void readImpl() {
        SkillId = readH();
        SkillSkinId = readH();
    }

    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        /*if (SkillSkinId > 0) {
            player.getSkillSkinList().setActive(SkillSkinId);
        } else {
            player.getSkillSkinList().setDeactive(SkillId);
        }*/
    }
}