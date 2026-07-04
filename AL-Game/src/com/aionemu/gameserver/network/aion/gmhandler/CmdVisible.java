/*
 * =====================================================================================*
 * This file is part of Archsoft (Archsoft Home Software Development)                   *
 * Aion - Archsoft Development is closed Aion Project that use Old Aion Project Base    *
 * Like Aion-Unique, Aion-Lightning, Aion-Engine, Aion-Core, Aion-Extreme,              *
 * Aion-NextGen, Aion-Ger, U3J, Encom And other Aion project, All Credit Content        *
 * That they make is belong to them/Copyright is belong to them. And All new Content    *
 * that Archsoft make the copyright is belong to Archsoft.                              *
 * You may have agreement with Archsoft Development, before use this Engine/Source      *
 * You have agree with all of Term of Services agreement with Archsoft Development      *
 * =====================================================================================*
 */

package com.aionemu.gameserver.network.aion.gmhandler;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author Alcapwnd
 */
public class CmdVisible extends AbstractGMHandler {

    public CmdVisible(Player admin, String params) {
        super(admin, params);
        run();
    }

    private void run() {
        if (admin.getAccessLevel() < AdminConfig.GM_PANEL) {
            AuditLogger.info(admin, "Player trying send Wish Command.");
            return;
        }
        admin.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
        admin.unsetVisualState(CreatureVisualState.HIDE20);
        PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
        PacketSendUtility.sendMessage(admin, "You are invisible.");
    }

}