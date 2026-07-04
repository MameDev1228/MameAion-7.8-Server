/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.enchant;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.DecomposeStuffData;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatEnchantFunction;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.*;
import com.aionemu.gameserver.model.templates.item.grind.GrindCombine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.utils.MameClientCompatDebug;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndArray;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;

import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EnchantService
{
    private static final Logger log = LoggerFactory.getLogger(EnchantService.class);
	
    public static boolean breakItem(Player player, Item targetItem) {
        Storage inventory = player.getInventory();
        if (inventory.getItemByObjId(targetItem.getObjectId()) == null) {
            return false;
        }
        int BreakKinah = 0;
        int BreakAp = 0;
        if (targetItem.getItemTemplate().getEnchantType() == EnchantType.PVE) {
            BreakKinah = EnchantService.BreakKinah(targetItem);
        } else if (targetItem.getItemTemplate().getEnchantType() == EnchantType.PVP) {
            BreakAp = EnchantService.BreakAp(targetItem);
        } else {
            BreakKinah = EnchantService.BreakKinah(targetItem);
        } if(BreakAp != 0) {
            if (player.getAbyssRank().getAp() < BreakAp) {
                return false;
            }
        } if(BreakKinah != 0) {
            if (player.getInventory().getKinah() < BreakKinah) {
                return false;
            }
        }
        int PVEStone = 0;
        int PVPStone = 0;
        int StoneCount = 0;
        if (targetItem.getEnchantPvPvELevel() >= 0 && targetItem.getEnchantPvPvELevel() <= 5) {
            StoneCount = Rnd.get(1, 2);
        } else if (targetItem.getEnchantPvPvELevel() >= 6 && targetItem.getEnchantPvPvELevel() <= 10) {
            StoneCount = Rnd.get(3, 5);
        } else if (targetItem.getEnchantPvPvELevel() >= 11 && targetItem.getEnchantPvPvELevel() <= 15) {
            StoneCount = Rnd.get(6, 8);
        } else if (targetItem.getEnchantPvPvELevel() >= 16 && targetItem.getEnchantPvPvELevel() <= 20) {
            StoneCount = Rnd.get(8, 10);
        } if (targetItem.getItemTemplate().getDecomposeTableId() != 0) {
            DecomposeStuffTemplate template = DataManager.DECOMPOSE_STUFF_DATA.getDecomposeStuffById(targetItem.getItemTemplate().getDecomposeTableId());
            if(template != null) {
                for(DecomposeStuffResult result : template.getDecomposeStuffResults()) {
                    ItemService.addItem(player, result.getItemId(), StoneCount);
                }
                //Break Kinah.
                if (BreakKinah != 0) {
                    player.getInventory().decreaseKinah(BreakKinah);
                } if (BreakAp != 0) {
                    AbyssPointsService.addAp(player, -BreakAp);
                }
                player.getInventory().decreaseByObjectId(targetItem.getObjectId(), 1);
            }
        } else {
            //Break PVE.
            if (targetItem.getItemTemplate().getEnchantType() == EnchantType.PVE) {
                switch (targetItem.getItemTemplate().getItemQuality()) {
                    case ANCIENT:
                        PVEStone = 166023106;
                    break;
                    case RELIC:
                        PVEStone = 166023107;
                    break;
                    case FINALITY:
                        PVEStone = 166023108;
                    break;
                }
                //Break Kinah.
                if (player.getInventory().getKinah() >= BreakKinah) {
                    player.getInventory().decreaseKinah(BreakKinah);
                }
                ItemService.addItem(player, PVEStone, StoneCount);
                if (Rnd.get(1, 100) < 25) {
                    //Manastone Fastener.
                    ItemService.addItem(player, 166401000, Rnd.get(50, 100));
                }
                player.getInventory().decreaseByObjectId(targetItem.getObjectId(), 1);
            }
            //Break PVP.
            if (targetItem.getItemTemplate().getEnchantType() == EnchantType.PVP) {
                switch (targetItem.getItemTemplate().getItemQuality()) {
                    case ANCIENT:
                        PVPStone = 166033106;
                    break;
                    case RELIC:
                        PVPStone = 166033107;
                    break;
                    case FINALITY:
                        PVPStone = 166033108;
                    break;
                }
                //Break Ap.
                AbyssPointsService.addAp(player, -BreakAp);
                ItemService.addItem(player, PVPStone, StoneCount);
                if (Rnd.get(1, 100) < 25) {
                    //Manastone Fastener.
                    ItemService.addItem(player, 166401000, Rnd.get(50, 100));
                }
                player.getInventory().decreaseByObjectId(targetItem.getObjectId(), 1);
            }
        }
        return true;
    }
	
    public static int BreakKinah(Item item) {
        if (item.getItemTemplate().getItemQuality() == ItemQuality.ANCIENT) {
            return 10046;
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.RELIC) {
            return 46934;
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.FINALITY) {
            return 361341;
        } else {
            return 0;
        }
    }
	
    public static int BreakAp(Item item) {
        if (item.getItemTemplate().getItemQuality() == ItemQuality.ANCIENT) {
            return 9537;
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.RELIC) {
            return 38148;
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.FINALITY) {
            return 152592;
        } else {
            return 0;
        }
    }
	
    public static int EnchantKinah(Item item) {
        if (item.getItemTemplate().getItemQuality() == ItemQuality.ANCIENT) {
            switch (item.getEnchantLevel()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
                    return 7761;
                default:
                    return 7761;
            }
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.RELIC) {
            switch (item.getEnchantLevel()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
                    return 10183;
                default:
                    return 10183;
            }
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.FINALITY) {
            switch (item.getEnchantLevel()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
                    return 69802;
                default:
                    return 69802;
            }
        } else {
            return 0;
        }
    }
	
    public static void enchantItemAct(Player player, Item parentItem, Item targetItem, int currentEnchant, boolean result) {
        int EnchantKinah = EnchantService.EnchantKinah(targetItem);
        int rnd = Rnd.get(100);
        currentEnchant = targetItem.getEnchantPvPvELevel();
        ItemQuality targetQuality = targetItem.getItemTemplate().getItemQuality();
        if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
            return;
        }
        //Enchant Kinah.
        if (player.getInventory().getKinah() >= EnchantKinah) {
            player.getInventory().decreaseKinah(EnchantKinah);
        } if (player.getInventory().getKinah() < EnchantKinah) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        } if (result) {
            switch (targetQuality) {
                case ANCIENT:
                case RELIC:
                case FINALITY:
                    int chanceId = getChanceId(targetItem, parentItem);
                    ItemEnchantChance eItem = DataManager.ITEM_ENCHANT_CHANCES_DATA.getChanceById(chanceId);
                    ItemEnchantChanceList eData = eItem.getChancesById(targetItem.getEnchantPvPvELevel());
                    if (rnd <= eData.getCrit()) {
                        currentEnchant += Rnd.get(2, 3);
                    } else {
                        currentEnchant += 1;
                    }
                break;
            }
        } else {
            if (currentEnchant > 0) {
                currentEnchant -= 1;
            } else if (currentEnchant == 0) {
                currentEnchant = 0;
            }
        } if (targetItem.getItemTemplate().getEnchantType() == EnchantType.PVE ? currentEnchant >= targetItem.getItemTemplate().getMaxEnchantLevel() : currentEnchant >= targetItem.getItemTemplate().getMaxAuthorize()) {
            currentEnchant = targetItem.getItemTemplate().getEnchantType() == EnchantType.PVE ? targetItem.getItemTemplate().getMaxEnchantLevel() : targetItem.getItemTemplate().getMaxAuthorize();
        }
        targetItem.setEnchantPvPvELevel(currentEnchant);
        if (targetItem.isEquipped()) {
            ItemEquipmentListener.refreshEquippedItemStats(targetItem, player);
        }
		ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemUpdateType.STATS_CHANGE);
		if (targetItem.isEquipped()) {
            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } else {
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } if (result) {
            //You successfully enchanted %0 by +%num1.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEED_NEW(new DescriptionId(targetItem.getNameId()), targetItem.getEnchantPvPvELevel()));
        } else {
            //You have failed to enchant %0.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameId())));
        }
    }
	
    public static boolean enchantItem(Player player, Item parentItem, Item targetItem) {
        boolean result = false;
        float random = Rnd.get(1, 1000) / 10f;
        switch (targetItem.getItemTemplate().getItemQuality()) {
            case ANCIENT:
            case RELIC:
            case FINALITY:
                int chanceId = getChanceId(targetItem, parentItem);
                ItemEnchantChance eItem = DataManager.ITEM_ENCHANT_CHANCES_DATA.getChanceById(chanceId);
                ItemEnchantChanceList eData = eItem.getChancesById(targetItem.getEnchantPvPvELevel());
                if (player.isGM() && AdminConfig.GM_ENCHANT_NOFAIL) {
                   result = true;
                } else {
                    if (random <= eData.getChance()) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
            break;
        }
        return result;
    }
	
    private static int getChanceId(Item targetItem, Item parentItem) {
        switch (targetItem.getItemTemplate().getEnchantType()) {
            case PVP:
                switch (targetItem.getItemTemplate().getItemQuality()) {
                    case ANCIENT:
                        switch (parentItem.getItemTemplate().getItemQuality()) {
                            case ANCIENT:
                                return 2;
                            case RELIC:
                                return 5;
                            case FINALITY:
                                return 8;
                        }
                    break;
                    case RELIC:
                        switch (parentItem.getItemTemplate().getItemQuality()) {
                            case ANCIENT:
                                return 3;
                            case RELIC:
                                return 6;
                            case FINALITY:
							    return 9;
                        }
                    break;
                    case FINALITY:
                        switch (parentItem.getItemTemplate().getItemQuality()) {
                            case ANCIENT:
                                return 4;
                            case RELIC:
                                return 7;
                            case FINALITY:
                                return 10;
                        }
                    break;
                }
            break;
            case PVE:
                switch (targetItem.getItemTemplate().getItemQuality()) {
                    case ANCIENT: 
                        switch (parentItem.getItemTemplate().getItemQuality()) {
                            case ANCIENT:
                                return 11;
                            case RELIC:
                                return 14;
                            case FINALITY:
                                return 17;
                        }
                    break;
                    case RELIC: 
                        switch (parentItem.getItemTemplate().getItemQuality()) {
                            case ANCIENT:
                                return 12;
                            case RELIC:
                                return 15;
                            case FINALITY:
                                return 18;
                        }
                    break;
                    case FINALITY:
                        switch (parentItem.getItemTemplate().getItemQuality()) {
                            case ANCIENT:
                                return 13;
                            case RELIC:
                                return 16;
                            case FINALITY:
                                return 19;
                        }
                    break;
                }
            break;
        }
        return 0;
    }
	
    public static boolean socketManastone(Player player, Item parentItem, Item targetItem, int targetWeapon) {
        int targetItemLevel = 1;
        if (targetWeapon == 1) {
            targetItemLevel = targetItem.getItemTemplate().getLevel();
        } else {
            targetItemLevel = targetItem.getFusionedItemTemplate().getLevel();
        }
        int stoneLevel = parentItem.getItemTemplate().getLevel();
        int slotLevel = (int) (3 * Math.ceil((targetItemLevel + 3) / 3d));
        boolean result = false;
        float success = 30;
        int stoneCount = 0;
        if (stoneLevel > slotLevel) {
            return false;
        } if (targetWeapon == 1) {
            stoneCount = targetItem.getItemStones().size();
        } else {
            stoneCount = targetItem.getFusionStones().size();
        }
        success += parentItem.getItemTemplate().getItemQuality() == ItemQuality.ANCIENT ? 25f : 15f;
        float socketDiff = stoneCount * 1.25f + 1.75f;
        success += (slotLevel - stoneLevel) / socketDiff;
        float random = Rnd.get(1, 1000) / 10f;
        if (random <= success) {
            result = true;
        }
        return result;
    }
	
    public static void socketManastoneAct(Player player, Item parentItem, Item targetItem, int targetWeapon, boolean result) {
        int ManastoneKinah = EnchantService.ManastoneKinah(targetItem);
        if (player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1) && result) {
			//You have successfully socketed a manastone in %0.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_OPTION_SUCCEED(new DescriptionId(targetItem.getNameId())));
			///Manastone Kinah.
			if (player.getInventory().getKinah() >= ManastoneKinah) {
				player.getInventory().decreaseKinah(ManastoneKinah);
			} if (player.getInventory().getKinah() < ManastoneKinah) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
				return;
			} if (targetWeapon == 1) {
                ManaStone manaStone = ItemSocketService.addManaStone(targetItem, parentItem.getItemTemplate().getTemplateId());
                if (targetItem.isEquipped()) {
                    ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
                    player.getGameStats().updateStatsAndSpeedVisually();
                }
            } else {
                ManaStone manaStone = ItemSocketService.addFusionStone(targetItem, parentItem.getItemTemplate().getTemplateId());
                if (targetItem.isEquipped()) {
                    ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
                    player.getGameStats().updateStatsAndSpeedVisually();
                }
				ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemUpdateType.STATS_CHANGE);
				if (targetItem.isEquipped()) {
					player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
				} else {
					player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
            }
        } else {
			//You have failed in the manastone socketing of %0.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_OPTION_FAILED(new DescriptionId(targetItem.getNameId())));
        }
        ItemPacketService.updateItemAfterInfoChange(player, targetItem);
    }
	
    public static int ManastoneKinah(Item item) {
        if (item.getItemTemplate().getItemQuality() == ItemQuality.ANCIENT) {
            return 5935;
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.RELIC) {
            return 27734;
        } else if (item.getItemTemplate().getItemQuality() == ItemQuality.FINALITY) {
            return 213519;
        } else {
            return 0;
        }
    }
	
    public static void onItemEquip(Player player, Item item) {
        if (!GSConfig.ARCHSOFT_STATS_CORE_ENABLE) {
            onItemEquipLegacy(player, item);
            return;
        }
        List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
        try {
            applyEnchantTable(player, item, item.getItemTemplate().getEnchantTableId(), true, modifiers);
            applyEnchantTable(player, item, item.getItemTemplate().getTemperingTableId(), false, modifiers);

            if (!modifiers.isEmpty()) {
                player.getGameStats().addEffect(item, modifiers);
                player.getGameStats().updateStatsAndSpeedVisually();
            } else if (MameClientCompatDebug.isTarget(player) && hasVisibleEnchantOrAuthorize(item)) {
                log.info("[MAME-STATS][ENCHANT_NONE] player=" + player.getName()
                    + " itemId=" + item.getItemId()
                    + " enchant=" + item.getEnchantLevel()
                    + " enchantPvPvE=" + item.getEnchantPvPvELevel()
                    + " authorize=" + item.getAuthorizeLevel()
                    + " enchantTable=" + item.getItemTemplate().getEnchantTableId()
                    + " temperingTable=" + item.getItemTemplate().getTemperingTableId()
                    + " type=" + item.getItemTemplate().getEnchantType());
            }
        } catch (Exception ex) {
            log.error("Error on item equip.", ex);
        }
    }

    private static void onItemEquipLegacy(Player player, Item item) {
        List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
        try {
            if (item.getItemTemplate().getEnchantTableId() > 0) {
                ItemEnchantTemplate ie1 = DataManager.ITEM_ENCHANT_DATA.getEnchantePveTemplate(item.getItemTemplate().getEnchantTableId());
                int pveEnchantLevel = item.getEnchantPvPvELevel() > 0 ? item.getEnchantPvPvELevel() : item.getEnchantLevel();
                if (pveEnchantLevel > 0 && ie1 != null) {
                    try {
                        List<? extends IStatFunction> stats = ie1.getStats(pveEnchantLevel);
                        if (stats == null && pveEnchantLevel > 0) {
                            stats = ie1.getStats(Math.min(pveEnchantLevel, 15));
                        }
                        if (stats != null) {
                            for (IStatFunction stat : stats) {
                                modifiers.add(new StatEnchantFunction(item, stat.getName(), stat.getValue()));
                            }
                        }
                    } catch (Exception localException2) {
                        log.error("Cant add enchant modifiers for item: " + item.getItemId() + " , level=" + pveEnchantLevel, localException2);
                    }
                }
            }
            if (item.getItemTemplate().getTemperingTableId() > 0) {
                ItemEnchantTemplate ie2 = DataManager.ITEM_ENCHANT_DATA.getEnchantePvpTemplate(item.getItemTemplate().getTemperingTableId());
                int pvpEnchantLevel = 0;
                if (item.getItemTemplate().getEnchantType() == EnchantType.PVP && item.getEnchantPvPvELevel() > 0) {
                    pvpEnchantLevel = item.getEnchantPvPvELevel();
                } else if (item.getAuthorizeLevel() > 0) {
                    pvpEnchantLevel = item.getAuthorizeLevel();
                } else if (item.getEnchantLevel() > 0) {
                    pvpEnchantLevel = item.getEnchantLevel();
                }
                if (pvpEnchantLevel > 0 && ie2 != null) {
                    try {
                        List<? extends IStatFunction> stats = ie2.getStats(pvpEnchantLevel);
                        if (stats == null && pvpEnchantLevel > 0) {
                            stats = ie2.getStats(Math.min(pvpEnchantLevel, 15));
                        }
                        if (stats != null) {
                            for (IStatFunction stat : stats) {
                                modifiers.add(new StatEnchantFunction(item, stat.getName(), stat.getValue()));
                            }
                        }
                    } catch (Exception localException2) {
                        log.error("Cant add pvp/authorize modifiers for item: " + item.getItemId() + " , level=" + pvpEnchantLevel, localException2);
                    }
                }
            }
            if (!modifiers.isEmpty()) {
                player.getGameStats().addEffect(item, modifiers);
                player.getGameStats().updateStatsAndSpeedVisually();
            }
        } catch (Exception ex) {
            log.error("Error on item equip.", ex);
        }
    }

    private static boolean hasVisibleEnchantOrAuthorize(Item item) {
        return item != null && (item.getEnchantLevel() > 0 || item.getEnchantPvPvELevel() > 0 || item.getAuthorizeLevel() > 0);
    }

    private static void applyEnchantTable(Player player, Item item, int tableId, boolean primaryEnchantTable, List<IStatFunction> modifiers) {
        if (player == null || item == null || tableId <= 0) {
            return;
        }
        ItemEnchantTemplate template = DataManager.ITEM_ENCHANT_DATA.getTemplateByTypeOrAny(tableId, primaryEnchantTable ? EnchantType.PVE : EnchantType.PVP);
        if (template == null) {
            if (MameClientCompatDebug.isTarget(player)) {
                log.info("[MAME-STATS][ENCHANT_TABLE_MISSING] player=" + player.getName()
                    + " itemId=" + item.getItemId()
                    + " tableId=" + tableId
                    + " primary=" + primaryEnchantTable
                    + " enchant=" + item.getEnchantLevel()
                    + " enchantPvPvE=" + item.getEnchantPvPvELevel()
                    + " authorize=" + item.getAuthorizeLevel());
            }
            return;
        }

        int[] candidateLevels = primaryEnchantTable ? getPrimaryEnchantLevelCandidates(item) : getTemperingLevelCandidates(item);
        AppliedEnchantStats applied = resolveEnchantStats(template, candidateLevels);
        if (applied == null || applied.stats == null || applied.stats.isEmpty()) {
            if (MameClientCompatDebug.isTarget(player) && hasPositiveCandidate(candidateLevels)) {
                log.info("[MAME-STATS][ENCHANT_STATS_MISSING] player=" + player.getName()
                    + " itemId=" + item.getItemId()
                    + " tableId=" + tableId
                    + " tableType=" + template.getType()
                    + " primary=" + primaryEnchantTable
                    + " candidates=" + formatCandidates(candidateLevels)
                    + " maxLevel=" + template.getMaxStatLevel());
            }
            return;
        }

        for (IStatFunction stat : applied.stats) {
            if (stat != null) {
                modifiers.add(new StatEnchantFunction(item, stat.getName(), stat.getValue()));
            }
        }
        if (MameClientCompatDebug.isTarget(player)) {
            log.info("[MAME-STATS][ENCHANT_APPLY] player=" + player.getName()
                + " itemId=" + item.getItemId()
                + " tableId=" + tableId
                + " tableType=" + template.getType()
                + " primary=" + primaryEnchantTable
                + " requestedLevel=" + applied.requestedLevel
                + " resolvedLevel=" + applied.resolvedLevel
                + " candidates=" + formatCandidates(candidateLevels)
                + " stats=" + statsSummary(applied.stats));
        }
    }

    private static int[] getPrimaryEnchantLevelCandidates(Item item) {
        return uniquePositiveLevels(new int[] {
            item.getEnchantPvPvELevel(),
            item.getEnchantLevel(),
            item.getAuthorizeLevel()
        });
    }

    private static int[] getTemperingLevelCandidates(Item item) {
        return uniquePositiveLevels(new int[] {
            item.getAuthorizeLevel(),
            item.getEnchantPvPvELevel(),
            item.getEnchantLevel()
        });
    }

    private static int[] uniquePositiveLevels(int[] input) {
        int[] tmp = new int[input.length];
        int size = 0;
        for (int level : input) {
            if (level <= 0) {
                continue;
            }
            boolean exists = false;
            for (int i = 0; i < size; i++) {
                if (tmp[i] == level) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                tmp[size++] = level;
            }
        }
        int[] result = new int[size];
        System.arraycopy(tmp, 0, result, 0, size);
        return result;
    }

    private static boolean hasPositiveCandidate(int[] candidateLevels) {
        return candidateLevels != null && candidateLevels.length > 0;
    }

    private static AppliedEnchantStats resolveEnchantStats(ItemEnchantTemplate template, int[] candidateLevels) {
        if (template == null || candidateLevels == null) {
            return null;
        }
        for (int requestedLevel : candidateLevels) {
            List<? extends IStatFunction> exact = template.getStats(requestedLevel);
            if (exact != null && !exact.isEmpty()) {
                return new AppliedEnchantStats(requestedLevel, requestedLevel, exact);
            }
        }
        for (int requestedLevel : candidateLevels) {
            int resolvedLevel = template.getClosestStatLevel(requestedLevel);
            if (resolvedLevel > 0) {
                List<? extends IStatFunction> stats = template.getStats(resolvedLevel);
                if (stats != null && !stats.isEmpty()) {
                    return new AppliedEnchantStats(requestedLevel, resolvedLevel, stats);
                }
            }
        }
        return null;
    }

    private static String formatCandidates(int[] candidateLevels) {
        if (candidateLevels == null || candidateLevels.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < candidateLevels.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(candidateLevels[i]);
        }
        return sb.append(']').toString();
    }

    private static String statsSummary(List<? extends IStatFunction> stats) {
        if (stats == null || stats.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        int count = 0;
        for (IStatFunction stat : stats) {
            if (stat == null) {
                continue;
            }
            if (count > 0) {
                sb.append(',');
            }
            sb.append(stat.getName()).append('=').append(stat.getValue());
            count++;
            if (count >= 16) {
                sb.append(",...");
                break;
            }
        }
        return sb.append(']').toString();
    }

    private static final class AppliedEnchantStats {
        final int requestedLevel;
        final int resolvedLevel;
        final List<? extends IStatFunction> stats;

        AppliedEnchantStats(int requestedLevel, int resolvedLevel, List<? extends IStatFunction> stats) {
            this.requestedLevel = requestedLevel;
            this.resolvedLevel = resolvedLevel;
            this.stats = stats;
        }
    }

    /**
     * http://aionpowerbook.com/powerbook/Glory:_Shield
     *
     * @param player http://aionpowerbook.com/powerbook/5.5_-_Enchanting_System
     */
    public static void GloryShieldSkill(Player player) {
        int Enchant = 0;
        Equipment equip = player.getEquipment();
        for (Item item : equip.getEquippedItemsWithoutStigmaOld()) {
            if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor() || item.getItemTemplate().getItemSlot() == 32768) {
                if (item.getEnchantLevel() >= 15) {
                    Enchant++;
                }
            }
        } if (Enchant >= 6) {
            if (player.getSkillList().isSkillPresent(4694) || player.getSkillList().isSkillPresent(4695)) {
                return;
            } if (player.getRace() == Race.ELYOS) {
                player.getSkillList().addSkill(player, 4694, 1);
            } else if (player.getRace() == Race.ASMODIANS) {
                player.getSkillList().addSkill(player, 4695, 1);
            }
            PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
        } else {
            if (player.getSkillList().isSkillPresent(4694)) {
                SkillLearnService.removeSkill(player, 4694);
            } else if (player.getSkillList().isSkillPresent(4695)) {
                SkillLearnService.removeSkill(player, 4695);
            }
            PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
        }
    }
	
    public static void reductItemAct(Player player, Item parentItem, Item targetItem, int currentReduction, boolean result, int count) {
        if (!result) {
            PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), player.getObjectId().intValue(), parentItem.getObjectId().intValue(), parentItem.getItemId(), 0, 2, 0));
            //The reduction of %0‘s recommended level failed.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_EQUIPLEVEL_ADJ_FAIL(targetItem.getNameId()));
        } else {
            PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), player.getObjectId().intValue(), parentItem.getObjectId().intValue(), parentItem.getItemId(), 0, 1, 0));
            if (currentReduction + count > 5) {
                targetItem.setReductionLevel(5);
            } else {
                targetItem.setReductionLevel(currentReduction + count);
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_EQUIPLEVEL_ADJ_SUCCEED(targetItem.getNameId(), count));
            } if (targetItem.getReductionLevel() == 5) {
                //The max. recommended level reduction for %0 has been reached.
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_EQUIPLEVEL_ADJ_SUCCEED_MAX(targetItem.getNameId()));
            }
        }
        PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
        if (targetItem.isEquipped()) {
            ItemEquipmentListener.refreshEquippedItemStats(targetItem, player);
        }
        ItemPacketService.updateItemAfterInfoChange(player, targetItem);
        if (targetItem.isEquipped()) {
            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } else {
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
        }
    }
	
    public static void enchantDaevanionSkill(final Player player, final int skillId, final int bookObjId, final int materials) {
        final Item parentItem = player.getInventory().getItemByObjId(bookObjId);
		final ItemTemplate template = parentItem.getItemTemplate();
		final int nameId = template.getNameId();
        final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
        final boolean isSuccess = Rnd.chance(75);
        final int currentEnchant = skill.getSkillLevel();
        if (player.getInventory().getKinah() < 100000) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        }
        final ItemUseObserver moveObserver = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
                player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(nameId)));
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 2, 0), true);
            }
        };
        player.getObserveController().attach(moveObserver);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(moveObserver);
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 1), true);
                if (!player.getInventory().decreaseByObjectId(bookObjId, 1)) {
                    return;
                }
                player.getInventory().decreaseKinah(100000);
                if (materials != 0) {
                    player.getInventory().decreaseByObjectId(materials, 1);
                } if (isSuccess) {
                    int enchantLevel = currentEnchant + 1;
                    skill.setSkillLvl(enchantLevel);
                    player.getSkillList().addSkill(player, skill.getSkillId(), enchantLevel);
                    PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
                    PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_ENCHANT(skillId, skill.getSkillLevel(), currentEnchant));
                } else {
                    int enchantLevel = currentEnchant - 1;
                    skill.setSkillLvl(enchantLevel);
                    player.getSkillList().addSkill(player, skill.getSkillId(), enchantLevel);
                    PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
                    PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_ENCHANT(skillId, skill.getSkillLevel(), currentEnchant));
                } if (currentEnchant >= 15) {
                    SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), player.getLevel(), player.getRace());
                    PlayerSkillList playerSkillList = player.getSkillList();
                    for (SkillLearnTemplate template : skillTemplates) {
                        if (template.getRequiredSkill() == skillId) {
                            playerSkillList.addSkill(player, template.getSkillId(), 1);
                        }
                    }
                }
            }
        }, 100));
    }
	
    public static void combineDaevanionBook(final Player player, ArrayList<Integer> sacrificeBook) {
        for (int sacrifices : sacrificeBook) {
            player.getInventory().decreaseByObjectId(sacrifices, 1);
        }
        int result = 0;
        int chance = Rnd.get(0, 3);
        switch (player.getPlayerClass()) {
            case GLADIATOR:
                if (chance == 0) {
                    result = Rnd.get(169501640, 169501645);
                } if (chance == 1) {
                    result = Rnd.get(169501784, 169501785);
                } if (chance == 2) {
                    result = Rnd.get(169501806, 169501807);
                } if (chance == 3) {
                    result = 169501773;
                }
            break;
            case TEMPLAR:
                if (chance == 0) {
                    result = Rnd.get(169501646, 169501651);
                } if (chance == 1) {
                    result = Rnd.get(169501786, 169501787);
                } if (chance == 2) {
                    result = Rnd.get(169501808, 169501809);
                } if (chance == 3) {
                    result = 169501774;
                }
            break;
            case ASSASSIN:
                if (chance == 0) {
                    result = Rnd.get(169501652, 169501657);
                } if (chance == 1) {
                    result = Rnd.get(169501788, 169501789);
                } if (chance == 2) {
                    result = Rnd.get(169501810, 169501811);
                } if (chance == 3) {
                    result = 169501775;
                }
            break;
            case RANGER:
                if (chance == 0) {
                    result = Rnd.get(169501658, 169501663);
                } if (chance == 1) {
                    result = Rnd.get(169501790, 169501791);
                } if (chance == 2) {
                    result = Rnd.get(169501812, 169501813);
                } if (chance == 3) {
                    result = 169501776;
                }
            break;
            case SORCERER:
                if (chance == 0) {
                    result = Rnd.get(169501676, 169501681);
                } if (chance == 1) {
                    result = Rnd.get(169501792, 169501793);
                } if (chance == 2) {
                    result = Rnd.get(169501818, 169501819);
                } if (chance == 3) {
                    result = 169501777;
                }
            break;
            case SPIRIT_MASTER:
                if (chance == 0) {
                    result = Rnd.get(169501682, 169501687);
                } if (chance == 1) {
                    result = Rnd.get(169501794, 169501795);
                } if (chance == 2) {
                    result = Rnd.get(169501820, 169501821);
                } if (chance == 3) {
                    result = 169501778;
                }
            break;
            case CLERIC:
                if (chance == 0) {
                    result = Rnd.get(169501664, 169501669);
                } if (chance == 1) {
                    result = Rnd.get(169501796, 169501797);
                } if (chance == 2) {
                    result = Rnd.get(169501816, 169501817);
                } if (chance == 3) {
                    result = 169501779;
                }
            break;
            case CHANTER:
                if (chance == 0) {
                    result = Rnd.get(169501670, 169501675);
                } if (chance == 1) {
                    result = Rnd.get(169501798, 169501799);
                } if (chance == 2) {
                    result = Rnd.get(169501814, 169501815);
                } if (chance == 3) {
                    result = 169501780;
                }
            break;
            case GUNSLINGER:
                if (chance == 0) {
                    result = Rnd.get(169501688, 169501693);
                } if (chance == 1) {
                    result = Rnd.get(169501800, 169501801);
                } if (chance == 2) {
                    result = Rnd.get(169501826, 169501827);
                } if (chance == 3) {
                    result = 169501781;
                }
            break;
            case SONGWEAVER:
                if (chance == 0) {
                    result = Rnd.get(169501700, 169501705);
                } if (chance == 1) {
                    result = Rnd.get(169501802, 169501803);
                } if (chance == 2) {
                    result = Rnd.get(169501822, 169501823);
                } if (chance == 3) {
                    result = 169501782;
                }
            break;
            case AETHERTECH:
                if (chance == 0) {
                    result = Rnd.get(169501694, 169501699);
                } if (chance == 1) {
                    result = Rnd.get(169501804, 169501805);
                } if (chance == 2) {
                    result = Rnd.get(169501824, 169501825);
                } if (chance == 3) {
                    result = 169501783;
                }
            break;
            case VANDAL:
                if (chance == 0) {
                    result = Rnd.get(169501872, 169501877);
                } if (chance == 1) {
                    result = Rnd.get(169501878, 169501879);
                } if (chance == 2) {
                    result = Rnd.get(169501880, 169501881);
                } if (chance == 3) {
                    result = 169501882;
                }
            break;
        }
        PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_COMBINE(1, result));
        ItemService.addItem(player, result, 1);
    }
	
    public static void addSkillEnhance(Player player, Item targetItem) {
        List<Integer> skillLevel = new FastList<Integer>();
        ItemTemplate template = targetItem.getItemTemplate();
        if (template.isClassSpecific(player.getPlayerClass())) {
            ItemSkillEnhance enhance = DataManager.ITEM_SKILL_ENHANCE_DATA.getSkillEnhance(targetItem.getItemTemplate().getEnhanceTable());
            if (enhance != null) {
                int index = Rnd.get(0, enhance.getSkillEnhances().size() - 1);
                SkillEnhance skillEnhance = enhance.getSkillEnhances().get(index);
                for (int i = 1; i <= player.getLevel(); i++) {
                    SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                    for (SkillLearnTemplate skillTree : skillTemplates) {
                        if (skillEnhance.getSkillGroupeName().equals(skillTree.getSkillGroup())) {
                            skillLevel.add(skillTree.getSkillId());
                        }
                    }
                } if (skillLevel.size() != -1) {
                    int skillId = skillLevel.get(0); //get last index
                    targetItem.setEnhanceSkillId(skillId);
                    targetItem.setEnhanceEnchantLevel(1);
                    targetItem.setIsEnhance(true);
                }
            }
        }
    }
	
    public static void combineGrind(Player player, Item mat, Item mat2) {
        GrindCombine combine  = DataManager.GRIND_COMBINE_DATA.getCombine(player, mat.getItemTemplate().getGrindColor(), mat2.getItemTemplate().getGrindColor());
        if (combine == null) {
            return;
        }
        ///Combine Kinah.
        if (player.getInventory().getKinah() >= combine.getPrice()) {
            player.getInventory().decreaseKinah(combine.getPrice());
        } if (player.getInventory().getKinah() < combine.getPrice()) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        } if (mat == null || mat2 == null) {
            return;
        } if (player.getInventory().decreaseByObjectId(mat.getObjectId(), 1) && player.getInventory().decreaseByObjectId(mat2.getObjectId(), 1) ){
            int index = Rnd.get(0, combine.getRewards().size() - 1);
            if (index != -1) {
                int itemId = combine.getRewards().get(index).getItemId();
                ItemService.addItem(player, itemId, 1);
            }
        }
    }
	
    //Enchantement Destruction
    public static boolean enchantDesctructionItem(Player player, Item parentItem, Item targetItem) {
        boolean result = false;
        float random = Rnd.get(1, 1000) / 10f;
        int chanceId = 20;
        ItemEnchantChance eItem = DataManager.ITEM_ENCHANT_CHANCES_DATA.getChanceById(chanceId);
        ItemEnchantChanceList eData = eItem.getChancesById(targetItem.getEnchantLevel());
        if (player.isGM() && AdminConfig.GM_ENCHANT_NOFAIL) {
            result = true;
        } else {
            if (random <= eData.getChance()) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }
	
    public static void enchantDestructionItemAct(Player player, Item parentItem, Item targetItem, int currentEnchant, boolean result) {
        int EnchantKinah = EnchantService.EnchantKinah(targetItem);
        currentEnchant = targetItem.getEnchantLevel();
        ItemQuality targetQuality = targetItem.getItemTemplate().getItemQuality();
        if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
            return;
        }
        //Enchant Kinah.
        if (player.getInventory().getKinah() >= EnchantKinah) {
            player.getInventory().decreaseKinah(EnchantKinah);
        } if (player.getInventory().getKinah() < EnchantKinah) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        } if (result) {
            currentEnchant += 1;
        } else {
            currentEnchant = 0;
        }
        targetItem.setEnchantLevel(currentEnchant);
        if (targetItem.isEquipped()) {
            ItemEquipmentListener.refreshEquippedItemStats(targetItem, player);
        }
        ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemUpdateType.STATS_CHANGE);
        if (targetItem.isEquipped()) {
            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } else {
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } if (result) {
            //You successfully enchanted %0 by +%num1.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEED_NEW(new DescriptionId(targetItem.getNameId()), targetItem.getEnchantLevel()));
        } else {
            //You have failed to enchant %0.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameId())));
        }
    }

    public static void enchantGlyphItemAct(Player player, Item parentItem, Item targetItem, int currentEnchant, boolean result) {
        int EnchantKinah = EnchantService.EnchantKinah(targetItem);
        currentEnchant = targetItem.getEnchantLevel();
        ItemQuality targetQuality = targetItem.getItemTemplate().getItemQuality();
        if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
            return;
        }
        //Enchant Kinah.
        if (player.getInventory().getKinah() >= EnchantKinah) {
            player.getInventory().decreaseKinah(EnchantKinah);
        } if (player.getInventory().getKinah() < EnchantKinah) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        } if (result) {
            currentEnchant += 1;
        } else {
            currentEnchant = 0;
        }
        targetItem.setEnchantLevel(currentEnchant);
        if (targetItem.isEquipped()) {
            ItemEquipmentListener.refreshEquippedItemStats(targetItem, player);
        }
        ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemUpdateType.STATS_CHANGE);
        if (targetItem.isEquipped()) {
            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } else {
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } if (result) {
            //You successfully enchanted %0 by +%num1.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEED_NEW(new DescriptionId(targetItem.getNameId()), targetItem.getEnchantLevel()));
        } else {
            //You have failed to enchant %0.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameId())));
        }
    }
	
    //Grind Enchant
    public static boolean enchantGrindItem(Player player, Item parentItem, Item targetItem) {
        boolean result = false;
        float random = Rnd.get(1, 1000) / 10f;
        int chanceId = 21;
        ItemEnchantChance eItem = DataManager.ITEM_ENCHANT_CHANCES_DATA.getChanceById(chanceId);
        ItemEnchantChanceList eData = eItem.getChancesById(targetItem.getEnchantLevel());
        if (player.isGM() && AdminConfig.GM_ENCHANT_NOFAIL) {
            result = true;
        } else {
            if (random <= eData.getChance()) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }
	
    public static void enchantGrindItemAct(Player player, Item parentItem, Item targetItem, int currentEnchant, boolean result) {
        int EnchantKinah = EnchantService.EnchantKinah(targetItem);
        currentEnchant = targetItem.getEnchantLevel();
        if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
            return;
        }
        //Enchant Kinah.
        if (player.getInventory().getKinah() >= EnchantKinah) {
            player.getInventory().decreaseKinah(EnchantKinah);
        } if (player.getInventory().getKinah() < EnchantKinah) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        } if (result) {
            currentEnchant += 1;
        } else {
            targetItem.setContaminated(true);
			//%0 is Corrupt and needs to be Sanctified before it can be refined.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_CANT_STATUS(new DescriptionId(targetItem.getNameId())));
        }
        targetItem.setEnchantLevel(currentEnchant);
        if (targetItem.isEquipped()) {
            ItemEquipmentListener.refreshEquippedItemStats(targetItem, player);
        }
        ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemUpdateType.STATS_CHANGE);
        if (targetItem.isEquipped()) {
            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } else {
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
        } if (result) {
            //%0 has been refined successfully and is now +%num1.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_SUCCEEDED(new DescriptionId(targetItem.getNameId()), targetItem.getEnchantPvPvELevel()));
        } else {
            //Refining of %0 has failed and its level has decreased to %1.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_FAIL(new DescriptionId(targetItem.getNameId())));
        }
    }
}