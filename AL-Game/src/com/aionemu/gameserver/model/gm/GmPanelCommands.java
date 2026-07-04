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

package com.aionemu.gameserver.model.gm;

/**
 * @author Ever' - Magenik
 */
public enum GmPanelCommands {

    /**
     * @STANDARD FUNCTION TAB
     */
    REMOVE_SKILL_DELAY_ALL,
    ITEMCOOLTIME,
    CLEARUSERCOOLT,
    SET_MAKEUP_BONUS,
    SET_VITALPOINT,
    SET_DISABLE_ITEMUSE_GAUGE,
    PARTYRECALL,
    ATTRBONUS,
    TELEPORTTO,
    RESURRECT,
    INVISIBLE,
    VISIBLE,
    /**
     * @CHARACTER SETTING TAB
     */
    LEVELDOWN,
    LEVELUP,
    CHANGECLASS,
    CLASSUP,
    DELETECQUEST,
    ADDQUEST,
    ENDQUEST,
    SETINVENTORYGROWTH,
    SKILLPOINT,
    COMBINESKILL,
    ADDSKILL,
    DELETESKILL,
    GIVETITLE,
    /**
     * @OVERALL FUNCTION TAB
     */
    ENCHANT100,
    FREEFLY,
    /**
     * @NPC QUEST ITEM TAB
     */
    TELEPORT_TO_NAMED,
    WISH,
    WISHID,
    DELETE_ITEMS,
    /**
     * @PLAYER INFO
     */
    BOOKMARK_ADD,
    SEARCH,
    RESET_INVENT,
    REMOVE_TITLE,
    SET_ENCHANTCOUNT,

    ;

    public static GmPanelCommands getValue(String command) {
        for (GmPanelCommands value : values()) {
            if (value.name().equals(command.toUpperCase())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid GmPanelCommands id: " + command);
    }
}
