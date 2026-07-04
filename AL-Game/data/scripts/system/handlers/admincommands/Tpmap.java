package admincommands;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.WorldMapType;

public class Tpmap extends AdminCommand {

    private static final MapPoint[] POINTS = new MapPoint[] {
        new MapPoint("sanctum", 110010000, 1322, 1511, 568),
        new MapPoint("pandaemonium", 120010000, 1679, 1400, 195),
        new MapPoint("poeta", 210010000, 829, 1231, 118),
        new MapPoint("heiron", 210040000, 2540, 343, 411),
        new MapPoint("inggison", 210050000, 1325, 1240, 310),
        new MapPoint("ishalgen", 220010000, 579, 2445, 279),
        new MapPoint("beluslan", 220040000, 398, 400, 222),
        new MapPoint("gelkmaros", 220070000, 1763, 2911, 554),
        new MapPoint("katalam", 800030000, 349, 2695, 143, 361, 383, 281),
        new MapPoint("crimson katalam", 800030000, 349, 2695, 143, 361, 383, 281),
        new MapPoint("north katalam", 800030000, 349, 2695, 143, 361, 383, 281),
        new MapPoint("lakrum", 800050000, 2670, 473, 323, 2916, 2484, 313),
        new MapPoint("demaha", 800060000, 578, 698, 723),
        new MapPoint("crimson danaria", 800040000, 1500, 1500, 300),
        new MapPoint("underpass", 800070000, 1000, 1000, 300)
    };

    public Tpmap() {
        super("tpmap");
    }

    @Override
    public void execute(Player admin, String... params) {
        if (params == null || params.length < 1) {
            PacketSendUtility.sendMessage(admin, "syntax //tpmap <mapname|mapid>");
            listKnown(admin, null);
            return;
        }
        String query = join(params).trim();
        MapPoint point = findKnownPoint(query);
        if (point != null) {
            point.teleport(admin);
            return;
        }
        if (isNumber(query)) {
            int worldId = Integer.parseInt(query);
            if (WorldMapType.getWorld(worldId) == null) {
                PacketSendUtility.sendMessage(admin, "Unknown map id: " + worldId);
                return;
            }
            PacketSendUtility.sendMessage(admin, "Map id exists, but no safe default point is registered: " + worldId);
            PacketSendUtility.sendMessage(admin, "Use //moveto " + worldId + " <x> <y> <z> or //tpmap with a known name.");
            return;
        }
        listKnown(admin, query);
    }

    private MapPoint findKnownPoint(String query) {
        String key = normalize(query);
        for (MapPoint point : POINTS) {
            if (normalize(point.name).equals(key)) {
                return point;
            }
        }
        for (MapPoint point : POINTS) {
            if (normalize(point.name).contains(key) || key.contains(normalize(point.name))) {
                return point;
            }
        }
        return null;
    }

    private void listKnown(Player admin, String query) {
        PacketSendUtility.sendMessage(admin, "Map name not matched: " + (query == null ? "" : query));
        PacketSendUtility.sendMessage(admin, "Known tpmap names: sanctum, pandaemonium, poeta, heiron, inggison, gelkmaros, lakrum, katalam, demaha");
        PacketSendUtility.sendMessage(admin, "Matching world_maps:");
        String q = query == null ? "" : normalize(query);
        int count = 0;
        for (WorldMapTemplate template : DataManager.WORLD_MAPS_DATA) {
            if (template == null || template.getName() == null) {
                continue;
            }
            String name = template.getName();
            if (q.length() == 0 || normalize(name).contains(q)) {
                PacketSendUtility.sendMessage(admin, template.getMapId() + " : " + name);
                if (++count >= 20) {
                    PacketSendUtility.sendMessage(admin, "...too many results. Type more letters.");
                    break;
                }
            }
        }
        if (count == 0) {
            PacketSendUtility.sendMessage(admin, "No world_maps match. Use //moveto <worldId> <x> <y> <z> if you know coordinates.");
        }
    }

    private String join(String[] params) {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(param);
        }
        return sb.toString();
    }

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase().replace("_", " ").replace("-", " ").replaceAll("\\s+", " ").trim();
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax //tpmap <mapname|mapid>");
    }

    private static final class MapPoint {
        private final String name;
        private final int worldId;
        private final float elyosX;
        private final float elyosY;
        private final float elyosZ;
        private final float asmoX;
        private final float asmoY;
        private final float asmoZ;

        private MapPoint(String name, int worldId, float x, float y, float z) {
            this(name, worldId, x, y, z, x, y, z);
        }

        private MapPoint(String name, int worldId, float elyosX, float elyosY, float elyosZ, float asmoX, float asmoY, float asmoZ) {
            this.name = name;
            this.worldId = worldId;
            this.elyosX = elyosX;
            this.elyosY = elyosY;
            this.elyosZ = elyosZ;
            this.asmoX = asmoX;
            this.asmoY = asmoY;
            this.asmoZ = asmoZ;
        }

        private void teleport(Player player) {
            if (player.getRace() == Race.ASMODIANS) {
                TeleportService2.teleportTo(player, worldId, asmoX, asmoY, asmoZ);
            } else {
                TeleportService2.teleportTo(player, worldId, elyosX, elyosY, elyosZ);
            }
            PacketSendUtility.sendMessage(player, "Teleported to " + name + " [" + worldId + "]");
        }
    }
}
