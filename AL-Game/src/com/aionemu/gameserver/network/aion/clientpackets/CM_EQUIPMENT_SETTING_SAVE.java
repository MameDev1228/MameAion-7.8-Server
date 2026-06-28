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
 * Stores a 7.x equipment preset. Phase7 removes debug-only behavior and sends
 * the refreshed preset list back to the client so UI state survives relog.
 *
 * @author Falke_34
 */
public class CM_EQUIPMENT_SETTING_SAVE extends AionClientPacket {

	private int slot;
	private int displayType;
	private int mHand;
	private int sHand;
	private int helmet;
	private int torso;
	private int glove;
	private int boots;
	private int earringsLeft;
	private int earringsRight;
	private int ringLeft;
	private int ringRight;
	private int necklace;
	private int shoulder;
	private int pants;
	private int powershardLeft;
	private int powershardRight;
	private int wings;
	private int waist;
	private int mOffHand;
	private int sOffHand;
	private int plume;
	private int bracelet;

	public CM_EQUIPMENT_SETTING_SAVE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		slot = readDIfPresent();
		displayType = readDIfPresent();
		mHand = readDIfPresent();
		sHand = readDIfPresent();
		helmet = readDIfPresent();
		torso = readDIfPresent();
		glove = readDIfPresent();
		boots = readDIfPresent();
		earringsLeft = readDIfPresent();
		earringsRight = readDIfPresent();
		ringLeft = readDIfPresent();
		ringRight = readDIfPresent();
		necklace = readDIfPresent();
		shoulder = readDIfPresent();
		pants = readDIfPresent();
		powershardLeft = readDIfPresent();
		powershardRight = readDIfPresent();
		wings = readDIfPresent();
		waist = readDIfPresent();
		mOffHand = readDIfPresent();
		sOffHand = readDIfPresent();
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
		player.getEquipmentSettingList().add(slot, displayType, mHand, sHand, helmet, torso, glove, boots, earringsLeft,
			earringsRight, ringLeft, ringRight, necklace, shoulder, pants, powershardLeft, powershardRight, wings, waist,
			mOffHand, sOffHand, plume, bracelet, true);
		PacketSendUtility.sendPacket(player, new SM_EQUIPMENT_SETTING(player.getEquipmentSettingList().getEquipmentSetting()));
	}
}
