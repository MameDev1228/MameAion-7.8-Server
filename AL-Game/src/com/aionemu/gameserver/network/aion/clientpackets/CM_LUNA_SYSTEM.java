/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SYSTEM_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.LunaShopService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_LUNA_SYSTEM extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_LUNA_SYSTEM.class);

	private int actionId;
	private int indun_id;
	private int indun_unk;
	private int recipe_id;
	private int material_item_id;
	private long material_item_count;
	private int teleportId;
	private int slot;
	private int ItemObjId;
	@SuppressWarnings("unused")
	private int lunaCost;

	public CM_LUNA_SYSTEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = readCIfPresent();
		switch (actionId) {
			case 0: // Taki's Missions Teleport.
				indun_id = readDIfPresent();
				indun_unk = readCIfPresent();
				break;
			case 2: // Karunerk's Workshop.
				recipe_id = readDIfPresent();
				break;
			case 4: // Buy Necessary Materials.
				material_item_id = readDIfPresent();
				material_item_count = readQIfPresent();
				break;
			case 6:
			case 7:
				teleportId = readDIfPresent();
				break;
			case 10: // Apply wardrobe appearance
				slot = readCIfPresent();
				ItemObjId = readDIfPresent();
				break;
			case 11: // Modify appearance
				slot = readCIfPresent();
				ItemObjId = readDIfPresent();
				lunaCost = readCIfPresent();
				break;
			case 14: // Taki's Adventure.
				indun_id = readDIfPresent();
				break;
			default:
				break;
		}
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	private int readCIfPresent() {
		return getRemainingBytes() >= 1 ? readC() : 0;
	}

	private int readDIfPresent() {
		return getRemainingBytes() >= 4 ? readD() : 0;
	}

	private long readQIfPresent() {
		return getRemainingBytes() >= 8 ? readQ() : 0L;
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		switch (actionId) {
			case 0:
				if (player.getLevel() <= 9) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
					return;
				}
				else if (player.isInGroup2()) {
					PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, "You must leave your group or alliance to enter <Luna Instance>", ChatType.BRIGHT_YELLOW_CENTER), true);
					return;
				}
				else {
					LunaShopService.getInstance().takiAdventureTeleport(player, indun_unk, indun_id);
				}
				break;
			case 2: // Karunerk's Workshop
				LunaShopService.getInstance().specialDesign(player, recipe_id);
				break;
			case 3:
				LunaShopService.getInstance().craftBox(player);
				break;
			case 4: // Buy Necessary Materials
				LunaShopService.getInstance().buyMaterials(player, material_item_id, material_item_count);
				break;
			case 5:
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(actionId));
				break;
			case 6:
			case 7:
				LunaShopService.getInstance().teleport(player, actionId, teleportId);
				break;
			case 8:
				LunaShopService.getInstance().dorinerkWardrobeLoad(player);
				break;
			case 9:
				LunaShopService.getInstance().dorinerkWardrobeExtendSlots(player);
				break;
			case 10:
				LunaShopService.getInstance().dorinerkWardrobeAct(player, slot, ItemObjId);
				break;
			case 11:
				LunaShopService.getInstance().dorinerkWardrobeModifyAppearance(player, slot, ItemObjId);
				break;
			case 12:
				LunaShopService.getInstance().munirunerksTreasureChamber(player);
				break;
			case 14:
				LunaShopService.getInstance().takiAdventure(player, indun_id);
				break;
			case 15:
				LunaShopService.getInstance().diceGame(player);
				break;
			case 16:
				LunaShopService.getInstance().diceGameReward(player);
				break;
			default:
				log.debug("Unknown Luna action {} from {}", actionId, player.getName());
				break;
		}
	}
}
