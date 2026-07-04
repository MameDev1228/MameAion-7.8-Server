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

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Alcapwnd
 */
public class CmdRemoveSkillDelayAll extends AbstractGMHandler {

    public CmdRemoveSkillDelayAll(Player admin) {
        super(admin, "");
        run();
    }

    //TODO its a little bit odd
    private void run() {
        /*Player t = target != null ? target : admin;
        if (t.getRemoveSkillDelay() == 1) {
			t.setRemoveSkillDelay(0);
			PacketSendUtility.sendMessage(t, "Now you got your normal Skill Cooldowns!");
		} else if (t.getRemoveSkillDelay() == 0) {
			t.setRemoveSkillDelay(1);
			PacketSendUtility.sendMessage(t, "Now you wont have any Skill Cooldowns!");
		}*/
    }

}
