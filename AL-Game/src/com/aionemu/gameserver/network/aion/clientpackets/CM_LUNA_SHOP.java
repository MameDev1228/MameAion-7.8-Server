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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.siege.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SHOP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SHOP_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.LunaShopService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ranastic
 */

public class CM_LUNA_SHOP extends AionClientPacket
{
	private int actionId;
	private LunaShopAction action;
	private int indun_id;
	private int indun_unk;
	private int recipe_id;
	private int material_item_id;
	private long material_item_count;
	private int teleportId;
	private int slot;
	private int ItemObjId;
	private int lunaCost;
	private int siegeId;
	private static final Logger log = LoggerFactory.getLogger(CM_LUNA_SHOP.class);
	
	public CM_LUNA_SHOP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		actionId = readC();
		action = LunaShopAction.getActionById(actionId);
		switch (action) {
			case INSTANCE_TELEPORT: //Taki's Missions Teleport.
				indun_id = readD();
				indun_unk = readC();
			break;
			case CRAFT: //Karunerk's Workshop.
				recipe_id = readD();
			break;
			case BUY_ITEMS: //Buy Necessary Materials.
				material_item_id = readD();
				material_item_count = readQ();
			break;
			case SIEGE:
			break;
			case TELEPORT:
				this.teleportId = readD();
			break;
			case TELEPORT_2: //Siege Training Room.
				this.siegeId = readD();
			break;
			case WARDROBE: //Dorinerk's Wardrobe.
			break;
			case WARDROBE_SLOT: //Dorinerk Wardrobe Extend Slots.
			break;
			case WARDROBE_ITEM: //Dorinerk Wardrobe Act.
				slot = readC();
				ItemObjId = readD();
			break;
			case CHANGE_OUTFIT: //Dorinerk Wardrobe [Modify Appearance]
				slot = readC();
				ItemObjId = readD();
				lunaCost = readC();
			break;
			case TREASURE: //Munirunerk's Treasure Chamber.
			break;
			case TAKI_ADVENTURE: //Taki's Adventure.
				indun_id = readD();
			break;
			case DICE_ROLL: //Daishunerk's Game Of Fate.
			case DICE_REWARD:
			break;
		}
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		} switch (action) {
		    case INSTANCE_TELEPORT:
				if (player.isInGroup2()) {
					//You must leave your group or alliance to enter.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403080));
					return;
				} else {
					LunaShopService.getInstance().takiAdventureTeleport(player, indun_unk, indun_id);
				} if (player.getLevel() >= 10) {
				LunaShopService.getInstance().takiAdventureTeleport(player, indun_unk, indun_id);
			} else {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
			}
			break;
			case CRAFT:
				LunaShopService.getInstance().specialDesign(player, recipe_id);
			break;
		    case CRAFTBOX:
				LunaShopService.getInstance().craftBox(player);
			break;
		    case BUY_ITEMS:
				LunaShopService.getInstance().buyMaterials(player, material_item_id, material_item_count);
			break;
			case SIEGE:
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(actionId));
			break;
			case TELEPORT:
				LunaShopService.getInstance().teleport(player, actionId, teleportId);
			break;
			case TELEPORT_2:
				LunaShopService.getInstance().siegeTeleport(player, actionId, siegeId);
			break;
			case WARDROBE:
				LunaShopService.getInstance().dorinerkWardrobeLoad(player);
			break;
			case WARDROBE_SLOT:
				LunaShopService.getInstance().dorinerkWardrobeExtendSlots(player);
			break;
			case WARDROBE_ITEM:
				LunaShopService.getInstance().dorinerkWardrobeAct(player, slot, ItemObjId);
			break;
			case CHANGE_OUTFIT:
				LunaShopService.getInstance().dorinerkWardrobeModifyAppearance(player, slot, ItemObjId);
			break;
			case TREASURE:
				LunaShopService.getInstance().munirunerksTreasureChamber(player);
			break;
			case TAKI_ADVENTURE:
				LunaShopService.getInstance().takiAdventure(player, indun_id);
			break;
			case DICE_ROLL:
				LunaShopService.getInstance().lunaDices(player, actionId);
			break;
			case DICE_REWARD:
				LunaShopService.getInstance().lunaDiceReward(player, actionId);
			break;
		}
	}
}