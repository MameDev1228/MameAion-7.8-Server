/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_IDENTIFICATION;
import com.aionemu.gameserver.services.item.ItemPacketService;
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
		// 7.8 Luna identification confirmation. The exact random option table is
		// client/data dependent, so Phase8 acknowledges the request and forces an
		// item/stat refresh without mutating unknown option fields.
		ItemPacketService.updateItemAfterInfoChange(player, item, ItemUpdateType.STATS_CHANGE);
		PacketSendUtility.sendPacket(player, new SM_LUNA_IDENTIFICATION(player, itemObjectId));
	}
}
