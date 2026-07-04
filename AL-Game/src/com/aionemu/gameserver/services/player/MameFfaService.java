package com.aionemu.gameserver.services.player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Lightweight always-available FFA arena helper.
 *
 * PvP permission is granted only when both players are registered in this
 * service. It does not globally turn the destination map into same-race PvP.
 */
public final class MameFfaService {

    private static final Set<Integer> ACTIVE = ConcurrentHashMap.newKeySet();
    private static final Map<Integer, ReturnPoint> RETURN_POINTS = new ConcurrentHashMap<Integer, ReturnPoint>();

    private MameFfaService() {
    }

    public static boolean isInFfa(Player player) {
        return player != null && ACTIVE.contains(player.getObjectId());
    }

    public static boolean canFight(Player a, Player b) {
        return CustomConfig.MAME_FFA_ENABLED && isInFfa(a) && isInFfa(b) && a.getObjectId() != b.getObjectId();
    }

    public static int getActiveCount() {
        return ACTIVE.size();
    }

    public static void join(Player player) {
        if (!CustomConfig.MAME_FFA_ENABLED) {
            PacketSendUtility.sendMessage(player, "FFA is currently disabled.");
            return;
        }
        if (player == null || !player.isOnline()) {
            return;
        }
        saveReturnPoint(player);
        ACTIVE.add(player.getObjectId());
        TeleportService2.teleportTo(player, CustomConfig.MAME_FFA_WORLD, CustomConfig.MAME_FFA_X, CustomConfig.MAME_FFA_Y, CustomConfig.MAME_FFA_Z, CustomConfig.MAME_FFA_H);
        PacketSendUtility.sendBrightYellowMessageOnCenter(player, "FFAへ参加しました。退出は .ffa leave");
    }

    public static void leave(Player player) {
        if (player == null) {
            return;
        }
        ACTIVE.remove(player.getObjectId());
        ReturnPoint returnPoint = RETURN_POINTS.remove(player.getObjectId());
        if (returnPoint != null && player.isOnline()) {
            TeleportService2.teleportTo(player, returnPoint.worldId, returnPoint.instanceId, returnPoint.x, returnPoint.y, returnPoint.z, returnPoint.heading);
            PacketSendUtility.sendMessage(player, "FFAから退出しました。");
        } else if (player.isOnline()) {
            PacketSendUtility.sendMessage(player, "FFAから退出しました。");
        }
    }

    public static void onLogout(Player player) {
        if (player == null) {
            return;
        }
        ACTIVE.remove(player.getObjectId());
        RETURN_POINTS.remove(player.getObjectId());
    }

    private static void saveReturnPoint(Player player) {
        if (RETURN_POINTS.containsKey(player.getObjectId())) {
            return;
        }
        WorldPosition pos = player.getPosition();
        RETURN_POINTS.put(player.getObjectId(), new ReturnPoint(player.getWorldId(), player.getInstanceId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading()));
    }

    private static final class ReturnPoint {
        private final int worldId;
        private final int instanceId;
        private final float x;
        private final float y;
        private final float z;
        private final byte heading;

        private ReturnPoint(int worldId, int instanceId, float x, float y, float z, byte heading) {
            this.worldId = worldId;
            this.instanceId = instanceId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.heading = heading;
        }
    }
}
