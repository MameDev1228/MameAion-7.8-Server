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
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;

/**
 * @author Alcapwnd
 */
public class CmdAddSkill extends AbstractGMHandler {

    public CmdAddSkill(Player admin, String params) {
        super(admin, params);
        run();
    }

    private void run() {
        Player t = admin;
        if (t.getAccessLevel() < AdminConfig.GM_PANEL) {
            AuditLogger.info(t, "Player trying send add skill Command.");
            return;
        }
        if (admin.getTarget() != null && admin.getTarget() instanceof Player)
            t = World.getInstance().findPlayer(Util.convertName(admin.getTarget().getName()));

        if (params == null)
            return;

        for (SkillTemplate template : DataManager.SKILL_DATA.getSkillTemplates()) {
            if (template.getName() != null && template.getName().equalsIgnoreCase(params)) {
                PacketSendUtility.sendMessage(admin, "You added Skill " + template.getName() + "to " + t.getName());
                PacketSendUtility.sendMessage(t, "Admin has add Skill " + template.getName() + "to you.");
                t.getSkillList().addSkill(t, template.getSkillId(), 1);
            }
        }
    }
}