/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.enchant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DAEVANION_SKILL_FUSION;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CombineDaevanionBook {

	public static void combineDaevanionBook(Player player, ArrayList<Integer> sacrificeBook) {
		if (player == null || sacrificeBook == null || sacrificeBook.size() < 2) {
			return;
		}
		Set<Integer> uniqueObjects = new HashSet<Integer>();
		ItemQuality quality = null;
		for (int sacrifices : sacrificeBook) {
			if (sacrifices <= 0 || !uniqueObjects.add(sacrifices)) {
				PacketSendUtility.sendMessage(player, "Daevanion fusion failed: invalid material list.");
				return;
			}
			Item item = player.getInventory().getItemByObjId(sacrifices);
			if (item == null || item.getItemTemplate() == null || !EnchantDaevanionBook.isDaevanionSkillBook(item)) {
				PacketSendUtility.sendMessage(player, "Daevanion fusion failed: material is not a Daevanion skill book.");
				return;
			}
			if (quality == null) {
				quality = item.getItemTemplate().getItemQuality();
			}
			else if (quality != item.getItemTemplate().getItemQuality()) {
				PacketSendUtility.sendMessage(player, "Daevanion fusion failed: mixed material grades are not allowed.");
				return;
			}
		}
		int result = rollResult(player);
		if (result <= 0 || DataManager.ITEM_DATA.getItemTemplate(result) == null) {
			PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_FUSION(0, 0));
			PacketSendUtility.sendMessage(player, "Daevanion fusion failed: result template is missing.");
			return;
		}
		for (int sacrifices : uniqueObjects) {
			if (!player.getInventory().decreaseByObjectId(sacrifices, 1L)) {
				PacketSendUtility.sendMessage(player, "Daevanion fusion failed while consuming materials.");
				return;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_FUSION(1, result));
		ItemService.addItem(player, result, 1L);
	}

	private static int rollResult(Player player) {
		int chance = Rnd.get(0, 3);
		switch (player.getPlayerClass()) {
			case GLADIATOR:
				return chance == 0 ? Rnd.get(169501640, 169501645) : chance == 1 ? Rnd.get(169501784, 169501785) : chance == 2 ? Rnd.get(169501806, 169501807) : 169501773;
			case TEMPLAR:
				return chance == 0 ? Rnd.get(169501646, 169501651) : chance == 1 ? Rnd.get(169501786, 169501787) : chance == 2 ? Rnd.get(169501808, 169501809) : 169501774;
			case ASSASSIN:
				return chance == 0 ? Rnd.get(169501652, 169501657) : chance == 1 ? Rnd.get(169501788, 169501789) : chance == 2 ? Rnd.get(169501810, 169501811) : 169501775;
			case RANGER:
				return chance == 0 ? Rnd.get(169501658, 169501663) : chance == 1 ? Rnd.get(169501790, 169501791) : chance == 2 ? Rnd.get(169501812, 169501813) : 169501776;
			case SORCERER:
				return chance == 0 ? Rnd.get(169501676, 169501681) : chance == 1 ? Rnd.get(169501792, 169501793) : chance == 2 ? Rnd.get(169501818, 169501819) : 169501777;
			case SPIRIT_MASTER:
				return chance == 0 ? Rnd.get(169501682, 169501687) : chance == 1 ? Rnd.get(169501794, 169501795) : chance == 2 ? Rnd.get(169501820, 169501821) : 169501778;
			case CLERIC:
				return chance == 0 ? Rnd.get(169501664, 169501669) : chance == 1 ? Rnd.get(169501796, 169501797) : chance == 2 ? Rnd.get(169501816, 169501817) : 169501779;
			case CHANTER:
				return chance == 0 ? Rnd.get(169501670, 169501675) : chance == 1 ? Rnd.get(169501798, 169501799) : chance == 2 ? Rnd.get(169501814, 169501815) : 169501780;
			case GUNNER:
				return chance == 0 ? Rnd.get(169501688, 169501693) : chance == 1 ? Rnd.get(169501800, 169501801) : chance == 2 ? Rnd.get(169501826, 169501827) : 169501781;
			case BARD:
				return chance == 0 ? Rnd.get(169501700, 169501705) : chance == 1 ? Rnd.get(169501802, 169501803) : chance == 2 ? Rnd.get(169501822, 169501823) : 169501782;
			case RIDER:
				return chance == 0 ? Rnd.get(169501694, 169501699) : chance == 1 ? Rnd.get(169501804, 169501805) : chance == 2 ? Rnd.get(169501824, 169501825) : 169501783;
			case PAINTER:
				return chance == 0 ? Rnd.get(169501872, 169501877) : chance == 1 ? Rnd.get(169501878, 169501879) : chance == 2 ? Rnd.get(169501880, 169501881) : 169501882;
			default:
				return 0;
		}
	}
}
