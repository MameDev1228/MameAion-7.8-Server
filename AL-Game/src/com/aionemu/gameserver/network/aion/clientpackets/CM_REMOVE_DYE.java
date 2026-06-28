/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Removes item dye without consuming a dye item.
 *
 * 7.x clients send this when the player uses the built-in remove-dye action.
 */
public class CM_REMOVE_DYE extends AionClientPacket {

	private int itemObjectId;

	public CM_REMOVE_DYE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		itemObjectId = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		Item item = player.getInventory().getItemByObjId(itemObjectId);
		if (item == null) {
			item = player.getEquipment().getEquippedItemByObjId(itemObjectId);
		}
		if (item == null || !item.getItemSkinTemplate().isItemDyePermitted()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return;
		}
		if (item.getItemColor() == 0 && item.getColorExpireTime() == 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_ERROR_CANNOTREMOVE);
			return;
		}
		item.setItemColor(0);
		item.setColorExpireTime(0);
		if (player.getEquipment().getEquippedItemByObjId(item.getObjectId()) != null) {
			PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedForApparence()), true);
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		} else {
			player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		ItemPacketService.updateItemAfterInfoChange(player, item, ItemPacketService.ItemUpdateType.STATS_CHANGE);
	}
}
