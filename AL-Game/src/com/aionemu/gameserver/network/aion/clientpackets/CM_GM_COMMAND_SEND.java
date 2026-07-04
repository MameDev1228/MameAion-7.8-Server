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

package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gm.GmPanelCommands;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.gmhandler.*;
import com.aionemu.gameserver.network.aion.gmhandler.CmdLevelUpDown.LevelUpDownState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ever', Magenik, Alcapwnd
 */
public class CM_GM_COMMAND_SEND extends AionClientPacket {

    private String cmd = "";
    private String params = "";
    private Player admin;

    public CM_GM_COMMAND_SEND(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        admin = getConnection().getActivePlayer();
        String clientCmd = readS();

        int index = clientCmd.indexOf(" ");

        cmd = clientCmd;
        if (index >= 0) {
            cmd = clientCmd.substring(0, index).toUpperCase();
            params = clientCmd.substring(index + 1);
        }
    }

    @Override
    protected void runImpl() {
        if (admin == null) {
            return;
        }

        // check accesslevel - not needed but to be sure
        if (admin.getAccessLevel() < AdminConfig.GM_PANEL) {
            return;
        }

        switch (GmPanelCommands.getValue(cmd)) {
            case REMOVE_SKILL_DELAY_ALL:
                //new CmdRemoveSkillDelayAll(admin); // TODO
                break;
            case ITEMCOOLTIME:
                new CmdItemCoolTime(admin);
                break;
            case ATTRBONUS:
                new CmdAttrBonus(admin, params);
                break;
            case TELEPORTTO:
                new CmdTeleportTo(admin, params);
                break;
            case TELEPORT_TO_NAMED:
                new CmdTeleportToNamed(admin, params);
                break;
            case RESURRECT:
                new CmdResurrect(admin, "");
                break;
            case INVISIBLE:
                new CmdInvisible(admin, "");
                break;
            case VISIBLE:
                new CmdVisible(admin, "");
                break;
            case LEVELDOWN:
                new CmdLevelUpDown(admin, params, LevelUpDownState.DOWN);
                break;
            case LEVELUP:
                new CmdLevelUpDown(admin, params, LevelUpDownState.UP);
                break;
            case WISHID:
                new CmdWishId(admin, params);
                break;
            case DELETECQUEST:
                new CmdDeleteQuest(admin, params);
                break;
            case GIVETITLE:
                new CmdGiveTitle(admin, params);
                break;
            case DELETE_ITEMS:
                PacketSendUtility.sendMessage(admin, "Invalid command: " + cmd);
                break;
            case CHANGECLASS:
                new CmdChangeClass(admin, params);
                break;
            case CLASSUP:
                new CmdChangeClass(admin, params);
                break;
            case WISH:
                new CmdWish(admin, params);
                break;
            case ADDQUEST:
                new CmdStartQuest(admin, params);
                break;
            case ENDQUEST:
                new CmdEndQuest(admin, params);
                break;
            case ADDSKILL:
                new CmdAddSkill(admin, params);
                break;
            case SETINVENTORYGROWTH:
            case SKILLPOINT:
            case COMBINESKILL:
            case DELETESKILL:
            case ENCHANT100:
            case SEARCH:
            case BOOKMARK_ADD:
                PacketSendUtility.sendMessage(admin, "Invalid command: " + cmd);
                break;
            case FREEFLY:
                PacketSendUtility.sendMessage(admin, "Freefly On");
                break;
            default:
                PacketSendUtility.sendMessage(admin, "Invalid command: " + cmd);
                break;
        }
    }

}