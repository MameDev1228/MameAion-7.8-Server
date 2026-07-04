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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Magenik, Antraxx, Alcapwnd
 */
abstract public class AbstractGMHandler {

    protected String params;
    protected Player admin;
    protected Player target;

    public AbstractGMHandler(Player admin, String params) {
        this.admin = admin;
        this.params = params;
        getTarget();
    }

    public void getTarget() {
        VisibleObject t = admin.getTarget();
        if (t instanceof Player) {
            target = target;
            return;
        }
        target = null;
    }

    public boolean checkTarget() {
        if (target != null) {
            return true;
        }
        PacketSendUtility.sendMessage(admin, "Target not found or target is not an player");
        return false;
    }

}
