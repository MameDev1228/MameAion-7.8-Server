/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_IDENTIFICATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.RealRandomBonusService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Falke_34
 */
public class CM_LUNA_IDENTIFICATION extends AionClientPacket {

	private int itemObjectId;
	private int statId;

	public CM_LUNA_IDENTIFICATION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		itemObjectId = getRemainingBytes() >= 4 ? readD() : 0;
		statId = getRemainingBytes() >= 2 ? readH() : 0;
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null || !player.isSpawned() || player.getController().isInShutdownProgress()) {
			return;
		}
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjectId);
		if (item == null) {
			return;
		}
		boolean changed = rollLunaIdentification(player, item, statId);
		if (!changed) {
			ItemPacketService.updateItemAfterInfoChange(player, item, ItemUpdateType.STATS_CHANGE);
			PacketSendUtility.sendPacket(player, new SM_LUNA_IDENTIFICATION(player, itemObjectId));
			return;
		}
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
		player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		ItemPacketService.updateItemAfterInfoChange(player, item, ItemUpdateType.STATS_CHANGE);
		PacketSendUtility.sendPacket(player, new SM_LUNA_IDENTIFICATION(player, itemObjectId));
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_IDENTIFY_SUCCEED(item.getItemTemplate().getNameId()));
	}

	private boolean rollLunaIdentification(Player player, Item item, int requestedStatId) {
		boolean changed = false;
		if (item.getItemTemplate().getRandomBonusId() > 0) {
			item.setRandomStats(null);
			item.setBonusNumber(0);
			changed |= item.setRndBonus();
		}
		if (item.getItemTemplate().getOptionSlotBonus() != 0) {
			item.setOptionalSocket(Rnd.get(0, item.getItemTemplate().getOptionSlotBonus()));
			changed = true;
		}
		if (item.getItemTemplate().getRealRndBonus() > 0) {
			if (item.getRealRndBonus() == null) {
				RealRandomBonusService.setBonus(item);
				changed = item.getRealRndBonus() != null || changed;
			}
			else if (requestedStatId > 0) {
				changed |= RealRandomBonusService.rerollSingleBonus(player, item, requestedStatId);
			}
			else {
				RealRandomBonusService.rerollAllBonuses(player, item);
				changed = true;
			}
		}
		return changed;
	}
}
