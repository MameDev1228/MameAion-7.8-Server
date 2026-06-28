/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EQUIPMENT_SETTING;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Legacy/alternate equipment preset save packet used by some 7.x clients.
 * Previously it only printed the values to stdout.
 *
 * @author FrozenKiller
 */
public class CM_SAVE_EQUIPMENT_SETTINGS extends AionClientPacket {

	private int slotId;
	private int show;
	private int weaponRight;
	private int weaponLeft;
	private int head;
	private int torso;
	private int hand;
	private int boots;
	private int earringLeft;
	private int earringRight;
	private int ringLeft;
	private int ringRight;
	private int necklace;
	private int shoulder;
	private int pants;
	private int powershardRight;
	private int powershardLeft;
	private int wings;
	private int belt;
	private int plume;
	private int bracelet;
	private int offhandRight;
	private int offhandLeft;

	public CM_SAVE_EQUIPMENT_SETTINGS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		slotId = readDIfPresent();
		show = readDIfPresent();
		weaponRight = readDIfPresent();
		weaponLeft = readDIfPresent();
		head = readDIfPresent();
		torso = readDIfPresent();
		hand = readDIfPresent();
		boots = readDIfPresent();
		earringLeft = readDIfPresent();
		earringRight = readDIfPresent();
		ringLeft = readDIfPresent();
		ringRight = readDIfPresent();
		necklace = readDIfPresent();
		shoulder = readDIfPresent();
		pants = readDIfPresent();
		powershardRight = readDIfPresent();
		powershardLeft = readDIfPresent();
		wings = readDIfPresent();
		belt = readDIfPresent();
		offhandRight = readDIfPresent();
		offhandLeft = readDIfPresent();
		plume = readDIfPresent();
		readDIfPresent();
		bracelet = readDIfPresent();
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	private int readDIfPresent() {
		return getRemainingBytes() >= 4 ? readD() : 0;
	}

	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
		if (player == null || !player.isSpawned() || player.getEquipmentSettingList() == null) {
			return;
		}
		player.getEquipmentSettingList().add(slotId, show, weaponRight, weaponLeft, head, torso, hand, boots, earringLeft,
			earringRight, ringLeft, ringRight, necklace, shoulder, pants, powershardLeft, powershardRight, wings, belt,
			offhandRight, offhandLeft, plume, bracelet, true);
		PacketSendUtility.sendPacket(player, new SM_EQUIPMENT_SETTING(player.getEquipmentSettingList().getEquipmentSetting()));
	}
}
