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

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Alcapwnd
 */
public class CmdChangeClass extends AbstractGMHandler {

    public CmdChangeClass(Player admin, String params) {
        super(admin, params);
        run();
    }

    public void run() {
        Player t = target != null ? target : admin;
        byte classId;
        String ClassChoose = params;
        System.out.println(ClassChoose);
        if (ClassChoose.equalsIgnoreCase("warrior")) {
            classId = 0;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("fighter")) {
            classId = 1;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("knight")) {
            classId = 2;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("scout")) {
            classId = 3;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("assassin")) {
            classId = 4;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("ranger")) {
            classId = 5;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("mage")) {
            classId = 6;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("wizard")) {
            classId = 7;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("elementalist")) {
            classId = 8;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("cleric")) {
            classId = 9;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("priest")) {
            classId = 10;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("chanter")) {
            classId = 11;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("engineer")) {
            classId = 12;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("rider")) {
            classId = 13;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("gunner")) {
            classId = 14;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("artist")) {
            classId = 15;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("bard")) {
            classId = 16;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else if (ClassChoose.equalsIgnoreCase("painter")) {
            classId = 17;
            PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
            admin.getCommonData().setPlayerClass(playerClass);
            admin.getController().upgradePlayer();
            PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
        } else
            PacketSendUtility.sendMessage(admin, "Invalid class switch chosen!");
    }
}
