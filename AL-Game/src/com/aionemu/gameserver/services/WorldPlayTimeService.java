/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 */
package com.aionemu.gameserver.services;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastMap;

import java.util.Map;
import java.util.concurrent.Future;

public class WorldPlayTimeService
{
    private Future<?> checkPlayTimeTask;
    private final Map<Integer, Player> players = new FastMap<Integer, Player>();;

    public void onStart() {
        checkWorldPlayTime();
    }

    public void onEnterWorld(Player player) {
        switch (player.getWorldId()) {
            case 800030000: //Crimson Katalam.
            case 800040000: //Crimson Danaria.
            case 800050000: //Lakrum.
            case 800060000: //Demaha.
            case 800070000: //Underpass B1.
                if (isTimelessWorld(player.getWorldId())) {
                    ensureTimelessTime(player);
                    PacketSendUtility.sendPacket(player, new SM_WORLD_PLAYTIME(player));
                    players.remove(player.getObjectId());
                    return;
                }
                if (!this.players.containsKey(player.getObjectId())) {
                    this.players.put(player.getObjectId(), player);
                }
            break;
            default:
                if (this.players.containsKey(player.getObjectId())) {
                    this.players.remove(player.getObjectId());
                }
            break;
        }
    }

    public void checkWorldPlayTime() {
        checkPlayTimeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Player player : players.values()) {
                    if (isTimelessWorld(player.getWorldId())) {
                        ensureTimelessTime(player);
                        PacketSendUtility.sendPacket(player, new SM_WORLD_PLAYTIME(player));
                        continue;
                    }
                    if (player.getCommonData().getWorldPlayTime() == 0) {
                        player.setWorldPlayTime(0);
                        PacketSendUtility.sendPacket(player, new SM_WORLD_PLAYTIME(player));
                        //There is not enough rift dimension hourglass time for enter.
                        PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1405813));
                        if (player.getRace() == Race.ELYOS) {
                            TeleportService2.teleportTo(player, 210050000, 1306.0f, 238.0f, 595.0f, (byte) 17);
                        } else {
                            TeleportService2.teleportTo(player, 220070000, 1788.0f, 2917.0f, 554.0f, (byte) 99);
                        }
                    } else {
                        player.getCommonData().setWorldPlayTime(player.getCommonData().getWorldPlayTime() - 1);
                        PacketSendUtility.sendPacket(player, new SM_WORLD_PLAYTIME(player));
                    }
                }
            }
        }, 60 * 1000, 60 * 1000); //...1Min
    }

    public static boolean isTimelessWorld(int worldId) {
        if (!CustomConfig.MAME_WORLDPLAYTIME_TIMELESS) {
            return false;
        }
        String worlds = CustomConfig.MAME_WORLDPLAYTIME_TIMELESS_WORLDS;
        if (worlds == null || worlds.trim().isEmpty()) {
            return false;
        }
        for (String token : worlds.split(",")) {
            try {
                if (Integer.parseInt(token.trim()) == worldId) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    public static void ensureTimelessTime(Player player) {
        if (player == null || !isTimelessWorld(player.getWorldId())) {
            return;
        }
        int minutes = Math.max(1, CustomConfig.MAME_WORLDPLAYTIME_DEFAULT_MINUTES);
        if (player.getCommonData().getWorldPlayTime() < minutes) {
            player.getCommonData().setWorldPlayTime(minutes);
        }
    }

    public static WorldPlayTimeService getInstance() {
        return SingletonHolder.instance;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final WorldPlayTimeService instance = new WorldPlayTimeService();
    }
}
