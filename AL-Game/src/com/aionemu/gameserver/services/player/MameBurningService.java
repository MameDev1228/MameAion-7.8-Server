package com.aionemu.gameserver.services.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.PlayerBindPointDAO;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECIPE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * MameAion always-on burning flow.
 *
 * Lv9 -> class-change window -> Lv20/stigma slots -> Lv80 -> starter kit.
 * Progress is tracked in server_variables, so no schema change is required.
 */
public final class MameBurningService {

    private static final Logger log = LoggerFactory.getLogger(MameBurningService.class);

    private static final String KEY_PREFIX = "mame_burning_stage:";
    private static final int STAGE_NONE = 0;
    private static final int STAGE_WAIT_CLASS = 10;
    private static final int STAGE_COMPLETED = 100;

    private MameBurningService() {
    }

    public static void onPlayerLogin(Player player) {
        if (!CustomConfig.MAME_BURNING_ENABLED || player == null || !player.isOnline()) {
            return;
        }
        ThreadPoolManager.getInstance().schedule(() -> startOrResume(player), CustomConfig.MAME_BURNING_LOGIN_DELAY_MS);
    }

    public static boolean isWaitingForClassSelection(Player player) {
        return player != null && getStage(player) == STAGE_WAIT_CLASS && player.getPlayerClass().isStartingClass();
    }

    public static void onClassSelected(Player player) {
        if (!CustomConfig.MAME_BURNING_ENABLED || player == null || player.getPlayerClass().isStartingClass()) {
            return;
        }
        int stage = getStage(player);
        if (stage == STAGE_WAIT_CLASS || (stage != STAGE_COMPLETED && player.getLevel() < CustomConfig.MAME_BURNING_TARGET_LEVEL)) {
            ThreadPoolManager.getInstance().schedule(() -> completeAfterClassSelection(player), 500);
        }
    }

    public static String getStatus(Player player) {
        int stage = getStage(player);
        if (stage == STAGE_WAIT_CLASS) {
            return "WAIT_CLASS";
        }
        if (stage == STAGE_COMPLETED) {
            return "COMPLETED";
        }
        return "NONE";
    }

    private static void startOrResume(Player player) {
        if (!player.isOnline()) {
            return;
        }
        int stage = getStage(player);
        if (stage == STAGE_COMPLETED) {
            return;
        }
        if (player.getLevel() >= CustomConfig.MAME_BURNING_TARGET_LEVEL && !player.getPlayerClass().isStartingClass()) {
            setStage(player, STAGE_COMPLETED);
            return;
        }
        if (player.getPlayerClass().isStartingClass()) {
            grantLevel(player, 9);
            setStage(player, STAGE_WAIT_CLASS);
            PacketSendUtility.sendBrightYellowMessageOnCenter(player, "バーニング支援: レベル9になりました。職業を選択してください。");
            ClassChangeService.showClassChangeDialog(player);
            return;
        }
        completeAfterClassSelection(player);
    }

    private static void completeAfterClassSelection(Player player) {
        if (!player.isOnline() || player.getPlayerClass().isStartingClass()) {
            return;
        }
        if (getStage(player) == STAGE_COMPLETED) {
            return;
        }

        grantLevel(player, 20);
        unlockStigmaSlots(player);
        grantLevel(player, CustomConfig.MAME_BURNING_TARGET_LEVEL);
        refreshPlayer(player);
        ensureStarterInventorySpace(player);

        if (CustomConfig.MAME_BURNING_GRANT_STARTER_KIT) {
            grantStarterKit(player);
        }
        if (CustomConfig.MAME_BURNING_GRANT_STIGMAS) {
            grantClassStigmas(player);
        }
        refreshPlayer(player);
        setStage(player, STAGE_COMPLETED);
        PacketSendUtility.sendBrightYellowMessageOnCenter(player, "バーニング支援が完了しました。拠点へ移動します。");
        teleportToFactionBase(player);
        log.info("[MAME-BURNING] completed player=" + player.getName() + " class=" + player.getPlayerClass() + " level=" + player.getLevel());
    }

    private static void grantLevel(Player player, int targetLevel) {
        targetLevel = Math.min(targetLevel, DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel());
        if (player.getLevel() >= targetLevel) {
            return;
        }
        player.getCommonData().setLevel(targetLevel);
        player.getLifeStats().synchronizeWithMaxStats();
        player.getLifeStats().updateCurrentStats();
        SkillLearnService.addMissingSkills(player);
        PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
        PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
        log.info("[MAME-BURNING] level grant target=" + targetLevel + " for " + player.getName());
    }

    private static void unlockStigmaSlots(Player player) {
        if (player.getCommonData().getAdvencedStigmaSlotSize() < MameBurningStarterData.TARGET_STIGMA_SLOTS) {
            player.getCommonData().setAdvencedStigmaSlotSize(MameBurningStarterData.TARGET_STIGMA_SLOTS);
        }
        if (player.getRace() == Race.ELYOS && !player.isCompleteQuest(1929)) {
            ClassChangeService.completeQuest(player, 1929);
        } else if (player.getRace() == Race.ASMODIANS && !player.isCompleteQuest(2900)) {
            ClassChangeService.completeQuest(player, 2900);
        }
        PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.stigmaSlots(player.getCommonData().getAdvencedStigmaSlotSize()));
    }

    private static void ensureStarterInventorySpace(Player player) {
        // Granting starter boxes + all stigmas can exceed the starter cube. Expand first.
        int beforeNpc = player.getNpcExpands();
        int beforeQuest = player.getQuestExpands();
        int guard = 0;
        while (player.getNpcExpands() < 18 && CubeExpandService.canExpand(player) && guard++ < 32) {
            CubeExpandService.expand(player, true);
        }
        PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(StorageType.CUBE, player));
        log.info("[MAME-BURNING][CUBE] npcExpands=" + beforeNpc + "->" + player.getNpcExpands()
            + " questExpands=" + beforeQuest + "->" + player.getQuestExpands()
            + " freeSlots=" + player.getInventory().getFreeSlots() + " player=" + player.getName());
    }

    private static void teleportToFactionBase(Player player) {
        try {
            if (player.getRace() == Race.ASMODIANS) {
                setBindPoint(player, 220070000, 1788.0000f, 2917.0000f, 554.0000f, (byte) 99);
                TeleportService2.teleportTo(player, 220070000, 1788.0000f, 2917.0000f, 554.0000f, (byte) 99);
                log.info("[MAME-BURNING] completed teleport target=Gelkmaros player=" + player.getName());
            } else {
                setBindPoint(player, 210050000, 1306.0000f, 238.0000f, 595.0000f, (byte) 17);
                TeleportService2.teleportTo(player, 210050000, 1306.0000f, 238.0000f, 595.0000f, (byte) 17);
                log.info("[MAME-BURNING] completed teleport target=Inggison player=" + player.getName());
            }
        } catch (Exception e) {
            log.warn("[MAME-BURNING] failed to teleport player to faction base: " + player.getName(), e);
        }
    }

    private static void setBindPoint(Player player, int worldId, float x, float y, float z, byte heading) {
        try {
            BindPointPosition bindPoint = new BindPointPosition(worldId, x, y, z, heading);
            bindPoint.setPersistentState(PersistentState.NEW);
            player.setBindPoint(bindPoint);
            DAOManager.getDAO(PlayerBindPointDAO.class).store(player);
            TeleportService2.sendSetBindPoint(player);
            log.info("[MAME-BURNING][BIND] world=" + worldId + " x=" + x + " y=" + y + " z=" + z + " player=" + player.getName());
        } catch (Exception e) {
            log.warn("[MAME-BURNING] failed to set bind point: " + player.getName(), e);
        }
    }

    private static void grantStarterKit(Player player) {
        addIfExists(player, ItemId.KINAH.value(), MameBurningStarterData.KINAH, 0, "starter-kinah");
        for (MameBurningStarterData.StarterItem item : MameBurningStarterData.getStarterItems(player.getPlayerClass())) {
            addIfExists(player, item.getItemId(), item.getCount(), item.getEnchantLevel(), "starter");
        }
        if (CustomConfig.MAME_BURNING_GRANT_AP > 0) {
            AbyssPointsService.addAp(player, CustomConfig.MAME_BURNING_GRANT_AP);
        }
        if (CustomConfig.MAME_BURNING_GRANT_GP > 0) {
            AbyssPointsService.addGp(player, CustomConfig.MAME_BURNING_GRANT_GP);
        }
    }

    private static void grantClassStigmas(Player player) {
        List<Integer> stigmaIds = MameBurningStarterData.getStigmas(player.getPlayerClass(), CustomConfig.MAME_BURNING_TARGET_LEVEL);
        int granted = 0;
        for (int itemId : stigmaIds) {
            if (addIfExists(player, itemId, 1, 0, "stigma")) {
                granted++;
            }
        }
        if (stigmaIds.isEmpty()) {
            log.warn("[MAME-BURNING][STIGMA] no usable stigma list player=" + player.getName() + " class=" + player.getPlayerClass());
        } else {
            log.info("[MAME-BURNING][STIGMA] usable=" + stigmaIds.size() + " granted=" + granted + " player=" + player.getName() + " class=" + player.getPlayerClass());
        }
    }

    private static boolean addIfExists(Player player, int itemId, long count, int enchantLevel, String reason) {
        if (itemId == 141000001) {
            log.warn("[MAME-BURNING][BLOCKED_ITEM] blocked unsafe stigma_shard itemId=141000001 reason=" + reason + " player=" + player.getName() + " class=" + player.getPlayerClass());
            return false;
        }
        if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
            log.warn("[MAME-BURNING][MISSING_ITEM] reason=" + reason + " itemId=" + itemId + " player=" + player.getName() + " class=" + player.getPlayerClass());
            return false;
        }
        long notAdded;
        if (enchantLevel > 0) {
            notAdded = ItemService.addItemAndEnchant(player, itemId, count, enchantLevel);
        } else {
            notAdded = ItemService.addItem(player, itemId, count);
        }
        return notAdded <= 0;
    }

    private static void refreshPlayer(Player player) {
        SkillLearnService.addMissingSkills(player);
        player.getLifeStats().synchronizeWithMaxStats();
        player.getLifeStats().updateCurrentStats();
        player.getController().updatePassiveStats();
        PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
        PacketSendUtility.sendPacket(player, new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));
        PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
    }

    private static int getStage(Player player) {
        return DAOManager.getDAO(ServerVariablesDAO.class).load(KEY_PREFIX + player.getObjectId());
    }

    private static void setStage(Player player, int stage) {
        DAOManager.getDAO(ServerVariablesDAO.class).store(KEY_PREFIX + player.getObjectId(), stage);
    }
}
