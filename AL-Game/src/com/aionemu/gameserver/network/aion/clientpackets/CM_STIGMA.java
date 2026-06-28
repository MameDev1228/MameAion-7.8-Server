/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 7.8 stigma equip/unequip packet.
 *
 * Phase8: make it tail-safe and always resync skills/stats after a successful
 * stigma change. 7.x clients are sensitive to stale stigma skill lists.
 *
 * @author Falke_34, FrozenKiller
 */
public class CM_STIGMA extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_STIGMA.class);

	private int action;
	private long slotRead;
	private int itemObjectId;

	public CM_STIGMA(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = getRemainingBytes() >= 1 ? readC() : -1; // add=0, remove=1
		slotRead = getRemainingBytes() >= 8 ? readQ() : 0L;
		itemObjectId = getRemainingBytes() >= 4 ? readD() : 0;
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();
		if (activePlayer == null || !activePlayer.isSpawned() || activePlayer.getController().isInShutdownProgress()) {
			return;
		}
		activePlayer.getController().cancelUseItem();

		Equipment equipment = activePlayer.getEquipment();

		if (!RestrictionsManager.canChangeEquip(activePlayer)) {
			return;
		}
		if (activePlayer.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE)) {
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_ACT_WHILE_IN_ABNORMAL_STATE);
			return;
		}
		Item changedItem = null;
		switch (action) {
			case 0:
				changedItem = equipment.equipItem(itemObjectId, slotRead);
				break;
			case 1:
				changedItem = equipment.unEquipItem(itemObjectId, slotRead);
				break;
			default:
				log.debug("Unhandled stigma action " + action + " from " + activePlayer.getName() + " item=" + itemObjectId + " slot=" + slotRead);
				break;
		}
		if (changedItem != null) {
			StigmaService.resyncStigmaState(activePlayer);
			PacketSendUtility.sendPacket(activePlayer, new SM_SKILL_LIST(activePlayer, activePlayer.getSkillList().getBasicSkills()));
			PacketSendUtility.sendPacket(activePlayer, new SM_STATS_INFO(activePlayer));
		}
	}
}
