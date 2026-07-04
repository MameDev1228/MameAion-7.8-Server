package com.aionemu.gameserver.services.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * MameAion always-on burning starter data.
 *
 * Stigma grants intentionally use the old 4.8 burning class lists for all
 * classes that existed in 4.8, because those lists are the known-good complete
 * "usable stigma" sets. VANDAL did not exist in 4.8, so it uses the 7.x
 * stigma-box item list / static-data fallback. Missing item ids are skipped at
 * grant time.
 */
public final class MameBurningStarterData {

    public static final long KINAH = 100_000_000L;
    public static final int TARGET_STIGMA_SLOTS = 9;

    private MameBurningStarterData() {
    }

    public static List<StarterItem> getStarterItems(PlayerClass playerClass) {
        List<StarterItem> items = new ArrayList<StarterItem>();

        // Safe 7.x consumables. Inventory is expanded before these are granted.
        // Do NOT grant stigma_shard (141000001). In this client/static combo, dropping it can crash the client.
        items.add(new StarterItem(162000124, 500L, 0));    // potion_hp_mp_70a
        items.add(new StarterItem(162000122, 300L, 0));    // potion_hp_70a
        items.add(new StarterItem(162000123, 300L, 0));    // potion_mp_70a
        items.add(new StarterItem(164000076, 200L, 0));    // scroll_speed_run_50a
        items.add(new StarterItem(164000073, 200L, 0));    // scroll_speed_atk_50a
        items.add(new StarterItem(164000134, 200L, 0));    // scroll_speed_casting_50a
        items.add(new StarterItem(164000079, 100L, 0));    // scroll_speed_fly_50a
        items.add(new StarterItem(164000259, 200L, 0));    // scroll_shield_all_70a
        items.add(new StarterItem(164000260, 200L, 0));    // scroll_regist_magic_70a
        items.add(new StarterItem(160020013, 100L, 0));    // jelly_4000_dpheal
        items.add(new StarterItem(190099000, 100L, 0));    // Transformation Scroll (Common)

        // Ancient Fighting Daeva starter boxes requested for MameAion.
        // These are boxes, so the player can choose/open the matching class piece.
        items.add(new StarterItem(188070610, 3L, 0)); // Weapon box x3 (main/off-hand/alternate)
        items.add(new StarterItem(188070619, 5L, 0)); // Armor box x5
        items.add(new StarterItem(188070628, 1L, 0)); // Wing box
        items.add(new StarterItem(188059019, 6L, 0)); // Accessory box x6 (necklace, belt, 2 earrings, 2 rings)
        items.add(new StarterItem(188070649, 1L, 0)); // Bracelet box
        items.add(new StarterItem(188070644, 1L, 0)); // Plume box

        return items;
    }

    public static List<Integer> getStigmas(PlayerClass playerClass, int targetLevel) {
        if (playerClass == PlayerClass.VANDAL) {
            List<Integer> vandal = getVandalBoxStigmas();
            if (!vandal.isEmpty()) {
                return vandal;
            }
            return getCurrentStaticStigmas(playerClass, targetLevel);
        }
        List<Integer> legacy48 = getLegacy48Stigmas(playerClass);
        if (!legacy48.isEmpty()) {
            return legacy48;
        }
        return getCurrentStaticStigmas(playerClass, targetLevel);
    }

    /**
     * VANDAL did not exist in the 4.8 server. These ids are the normal Vandal
     * stigma set used by the 7.x class stigma boxes (pa = paint/vandal family).
     */
    private static List<Integer> getVandalBoxStigmas() {
        return Arrays.asList(
            140001493, 140001494, 140001495, 140001496,
            140001497, 140001498, 140001499, 140001500,
            140001501, 140001502, 140001503, 140001504,
            140001505, 140001506, 140001507, 140001508
        );
    }

    public static List<Integer> getCurrentStaticStigmas(PlayerClass playerClass, int targetLevel) {
        if (playerClass == null || playerClass.isStartingClass() || DataManager.ITEM_DATA == null || DataManager.ITEM_DATA.getAllItems() == null) {
            return Collections.emptyList();
        }

        List<ItemTemplate> templates = new ArrayList<ItemTemplate>();
        for (ItemTemplate template : DataManager.ITEM_DATA.getAllItems().values()) {
            if (isCurrentBurningStigma(template, playerClass, targetLevel)) {
                templates.add(template);
            }
        }
        Collections.sort(templates, Comparator.comparingInt(ItemTemplate::getTemplateId));

        List<Integer> result = new ArrayList<Integer>(templates.size());
        for (ItemTemplate template : templates) {
            result.add(template.getTemplateId());
        }
        return result;
    }

    private static boolean isCurrentBurningStigma(ItemTemplate template, PlayerClass playerClass, int targetLevel) {
        if (template == null || template.getCategory() != ItemCategory.STIGMA || !template.isClassSpecific(playerClass)) {
            return false;
        }
        int requiredLevel = template.getRequiredLevel(playerClass);
        if (requiredLevel > targetLevel) {
            return false;
        }

        String name = template.getName().toLowerCase(Locale.ROOT);
        if (!name.startsWith("stigma_n_") || name.startsWith("stigma_ne_") || name.startsWith("stigma_a_") || name.contains("_owner") || name.contains("test")) {
            return false;
        }
        return true;
    }

    public static List<Integer> getLegacy48Stigmas(PlayerClass playerClass) {
        switch (playerClass) {
            case GLADIATOR:
                return Arrays.asList(140001109, 140001110, 140001111, 140001112, 140001113, 140001114, 140001115, 140001116, 140001117, 140001103, 140001104, 140001105, 140001106, 140001107, 140001108, 140001118, 140001119);
            case TEMPLAR:
                return Arrays.asList(140001126, 140001127, 140001128, 140001129, 140001130, 140001131, 140001132, 140001133, 140001120, 140001121, 140001122, 140001123, 140001124, 140001125, 140001134, 140001135);
            case ASSASSIN:
                return Arrays.asList(140001142, 140001143, 140001144, 140001145, 140001146, 140001147, 140001148, 140001149, 140001150, 140001136, 140001137, 140001138, 140001139, 140001140, 140001141, 140001151, 140001152);
            case RANGER:
                return Arrays.asList(140001159, 140001160, 140001161, 140001162, 140001163, 140001164, 140001165, 140001166, 140001167, 140001168, 140001169, 140001170, 140001171, 140001153, 140001154, 140001155, 140001156, 140001157, 140001158, 140001172, 140001173);
            case SORCERER:
                return Arrays.asList(140001175, 140001179, 140001180, 140001182, 140001183, 140001186, 140001187, 140001188, 140001189, 140001190, 140001174, 140001176, 140001177, 140001178, 140001181, 140001184, 140001185, 140001191, 140001192);
            case SPIRIT_MASTER:
                return Arrays.asList(140001200, 140001201, 140001202, 140001203, 140001204, 140001205, 140001206, 140001207, 140001208, 140001193, 140001194, 140001195, 140001196, 140001197, 140001198, 140001199, 140001209, 140001210);
            case CLERIC:
                return Arrays.asList(140001236, 140001237, 140001238, 140001239, 140001240, 140001241, 140001242, 140001243, 140001244, 140001228, 140001229, 140001230, 140001231, 140001232, 140001233, 140001234, 140001235, 140001245, 140001246);
            case CHANTER:
                return Arrays.asList(140001217, 140001218, 140001219, 140001220, 140001221, 140001222, 140001223, 140001224, 140001225, 140001211, 140001212, 140001213, 140001214, 140001215, 140001216, 140001226, 140001227);
            case GUNSLINGER:
                return Arrays.asList(140001253, 140001254, 140001255, 140001256, 140001257, 140001258, 140001259, 140001260, 140001261, 140001247, 140001248, 140001249, 140001250, 140001251, 140001252, 140001262, 140001263);
            case AETHERTECH:
                return Arrays.asList(140001270, 140001271, 140001272, 140001273, 140001274, 140001275, 140001276, 140001277, 140001278, 140001264, 140001265, 140001266, 140001267, 140001268, 140001269, 140001279, 140001280);
            case SONGWEAVER:
                return Arrays.asList(140001287, 140001288, 140001289, 140001290, 140001291, 140001292, 140001293, 140001294, 140001295, 140001281, 140001282, 140001283, 140001284, 140001285, 140001286, 140001296, 140001297);
            default:
                return Collections.emptyList();
        }
    }

    public static final class StarterItem {
        private final int itemId;
        private final long count;
        private final int enchantLevel;

        public StarterItem(int itemId, long count, int enchantLevel) {
            this.itemId = itemId;
            this.count = count;
            this.enchantLevel = enchantLevel;
        }

        public int getItemId() {
            return itemId;
        }

        public long getCount() {
            return count;
        }

        public int getEnchantLevel() {
            return enchantLevel;
        }
    }
}
