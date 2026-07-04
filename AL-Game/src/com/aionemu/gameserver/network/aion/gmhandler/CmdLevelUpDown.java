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

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Alcapwnd
 */
public class CmdLevelUpDown extends AbstractGMHandler {

    private LevelUpDownState state;

    public CmdLevelUpDown(Player admin, String params, LevelUpDownState state) {
        super(admin, params);
        this.state = state;
        run();
    }

    public void run() {
        Player t = target != null ? target : admin;
        Integer level = Integer.parseInt(params);

        if (state == LevelUpDownState.DOWN) {
            if (t.getCommonData().getLevel() - level >= 1) {
                int newLevel = t.getCommonData().getLevel() - level;
                t.getCommonData().setLevel(newLevel);
            } else {
                PacketSendUtility.sendMessage(admin, "The value of <level> will plus calculated to the current player level!");
            }
        } else if (state == LevelUpDownState.UP) {
            if (t.getCommonData().getLevel() + level <= GSConfig.PLAYER_MAX_LEVEL) {
                int newLevel = t.getCommonData().getLevel() + level;
                t.getCommonData().setLevel(newLevel);
            } else {
                PacketSendUtility.sendMessage(admin, "The value of <level> will plus calculated to the current player level!");
            }
        }
    }

    public enum LevelUpDownState {
        UP,
        DOWN
    }

}
