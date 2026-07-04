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
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.UseableItemObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Alcapwnd
 */
public class CmdItemCoolTime extends AbstractGMHandler {

    public CmdItemCoolTime(Player admin) {
        super(admin, "");
        run();
    }

    private void run() {
        Player playerT = target != null ? target : admin;

        if (playerT.getAccessLevel() < AdminConfig.GM_PANEL) {
            AuditLogger.info(playerT, "Player trying send item cooldown Command.");
            return;
        }
        List<Integer> delayIds = new ArrayList<>();
        if (playerT.getSkillCoolDowns() != null) {
            long currentTime = System.currentTimeMillis();
            for (Entry<Integer, Long> en : playerT.getSkillCoolDowns().entrySet()) {
                delayIds.add(en.getKey());
            }
            for (Integer delayId : delayIds) {
                playerT.setSkillCoolDown(delayId, currentTime);
            }
            PacketSendUtility.sendPacket(playerT, new SM_SKILL_COOLDOWN(playerT, delayIds));
            delayIds.clear();
        }

        if (playerT.getItemCoolDowns() != null) {
            for (Entry<Integer, ItemCooldown> en : playerT.getItemCoolDowns().entrySet()) {
                delayIds.add(en.getKey());
            }
            for (Integer delayId : delayIds) {
                playerT.addItemCoolDown(delayId, 0, 0);
            }
            delayIds.clear();
            PacketSendUtility.sendPacket(playerT, new SM_ITEM_COOLDOWN(playerT.getItemCoolDowns()));
        }

        if (playerT.getHouseRegistry() != null && playerT.getHouseObjectCooldownList().getHouseObjectCooldowns().size() > 0) {
            Iterator<HouseObject<?>> iter = playerT.getHouseRegistry().getObjects().iterator();
            while (iter.hasNext()) {
                HouseObject<?> obj = iter.next();
                if (obj instanceof UseableItemObject) {
                    if (!playerT.getHouseObjectCooldownList().isCanUseObject(obj.getObjectId()))
                        playerT.getHouseObjectCooldownList().addHouseObjectCooldown(obj.getObjectId(), 0);
                }
            }
        }

    }

}