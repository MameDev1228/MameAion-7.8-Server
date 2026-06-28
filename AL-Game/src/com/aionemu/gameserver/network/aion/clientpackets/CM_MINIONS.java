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
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.MinionAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.MinionService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 7.8 minion client packet.
 *
 * Phase7 note:
 * FUNCTION_SETTING was previously parsing subAction but never reading
 * dopingAction, so the minion function bag could not work. The parser is now
 * tolerant to packet tail changes and the service receives concrete add/remove/
 * relocate/use operations instead of silently dropping them.
 *
 * @author Falke_34, FrozenKiller
 */
public class CM_MINIONS extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_MINIONS.class);

	private final List<Integer> sacrificeMinions = new ArrayList<Integer>();
	private int actionId;
	private MinionAction action;
	private int subAction;
	private int itemObjId;
	private int minionObjId;
	private int lock;
	private int isAuto;
	private int minionToEvolve;
	private int minionToGrowth;
	private int conbine1;
	private int conbine2;
	private int conbine3;
	private int conbine4;
	private int activateLoot;
	private int dopingItemId;
	private int dopingAction;
	private int targetSlot;
	private int destinationSlot;
	private String name;

	public CM_MINIONS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = getRemainingBytes() >= 2 ? readH() : -1;
		action = MinionAction.getActionById(actionId);
		if (action == null) {
			if (getRemainingBytes() > 0) {
				readB(getRemainingBytes());
			}
			return;
		}

		switch (action) {
			case ADOPT:
				itemObjId = readDIfPresent();
				break;
			case DISMISS:
				minionObjId = readDIfPresent();
				break;
			case RENAME:
				minionObjId = readDIfPresent();
				name = getRemainingBytes() > 0 ? readS() : "";
				break;
			case LOCK:
				minionObjId = readDIfPresent();
				lock = readCIfPresent();
				break;
			case SUMMON:
			case UNSUMMON:
				minionObjId = readDIfPresent();
				break;
			case GROWTH:
				sacrificeMinions.clear();
				minionToGrowth = readDIfPresent();
				for (int i = 0; i < 10 && getRemainingBytes() >= 4; ++i) {
					sacrificeMinions.add(readD());
				}
				break;
			case EVOLVE:
				minionToEvolve = readDIfPresent();
				break;
			case COMBINE:
				conbine1 = readDIfPresent();
				conbine2 = readDIfPresent();
				conbine3 = readDIfPresent();
				conbine4 = readDIfPresent();
				break;
			case FUNCTION_SETTING:
				readFunctionSetting();
				break;
			case ENERGY_RECHARGE:
				readCIfPresent();
				isAuto = readCIfPresent();
				break;
			case AUTO_FUNCTION:
				isAuto = readCIfPresent();
				break;
			case UNK:
				if (getRemainingBytes() > 0) {
					readB(getRemainingBytes());
				}
				break;
			case BUFFING:
				while (getRemainingBytes() > 0) {
					readC();
				}
				break;
			default:
				if (getRemainingBytes() > 0) {
					readB(getRemainingBytes());
				}
				break;
		}
	}

	private void readFunctionSetting() {
		subAction = readDIfPresent();
		switch (subAction) {
			case 1: // loot toggle
				minionObjId = readDIfPresent();
				activateLoot = readCIfPresent();
				if (getRemainingBytes() > 0) {
					readB(getRemainingBytes());
				}
				break;
			case 0: // doping bag / auto use
				// Retail 7.x sends this as a dword on the tested opcode layout.
				dopingAction = readDIfPresent();
				minionObjId = readDIfPresent();
				switch (dopingAction) {
					case 0: // add/register item into slot
					case 1: // remove/clear item from slot
					case 3: // use item now
						dopingItemId = readDIfPresent();
						targetSlot = readDIfPresent();
						break;
					case 2: // relocate item slot
						dopingItemId = readDIfPresent();
						targetSlot = readDIfPresent();
						destinationSlot = readDIfPresent();
						break;
					default:
						if (getRemainingBytes() > 0) {
							readB(getRemainingBytes());
						}
						break;
				}
				break;
			default:
				if (getRemainingBytes() > 0) {
					readB(getRemainingBytes());
				}
				break;
		}
	}

	private int readDIfPresent() {
		return getRemainingBytes() >= 4 ? readD() : 0;
	}

	private int readCIfPresent() {
		return getRemainingBytes() >= 1 ? readC() : 0;
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null || action == null) {
			return;
		}
		switch (action) {
			case ADOPT:
				if (player.getMinionList().getMinions().size() >= 30) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404322));
					break;
				}
				MinionService.getInstance().makeMinion(player, itemObjId);
				break;
			case DISMISS:
				MinionService.getInstance().dismissMinion(player, minionObjId);
				break;
			case RENAME:
				MinionService.getInstance().renameMinion(player, minionObjId, name == null ? "" : name);
				break;
			case LOCK:
				MinionService.getInstance().lockMinion(player, minionObjId, lock);
				break;
			case SUMMON:
				MinionService.getInstance().spawnMinion(player, minionObjId);
				break;
			case UNSUMMON:
				MinionService.getInstance().despawnMinion(player, minionObjId);
				break;
			case GROWTH:
				MinionService.getInstance().growthUpMinion(player, minionToGrowth, sacrificeMinions);
				break;
			case EVOLVE:
				MinionService.getInstance().EvolveMinion(player, minionToEvolve);
				break;
			case COMBINE:
				MinionService.getInstance().CombineMinion(player, conbine1, conbine2, conbine3, conbine4);
				break;
			case ENERGY_RECHARGE:
				MinionService.getInstance().EnergyRecharge(player, isAuto);
				break;
			case AUTO_FUNCTION:
				MinionService.getInstance().activateMinionFunction(player);
				break;
			case FUNCTION_SETTING:
				runFunctionSetting(player);
				break;
			default:
				log.debug("Unhandled minion action {} payload consumed for {}", actionId, player.getName());
				break;
		}
	}

	private void runFunctionSetting(Player player) {
		switch (subAction) {
			case 1:
				MinionService.getInstance().activateLoot(player, minionObjId, activateLoot != 0);
				break;
			case 0:
				switch (dopingAction) {
					case 0:
						MinionService.getInstance().addItemToDopingBag(player, dopingAction, minionObjId, dopingItemId, targetSlot);
						break;
					case 1:
						MinionService.getInstance().removeItemFromDopingBag(player, minionObjId, targetSlot);
						break;
					case 2:
						MinionService.getInstance().relocateDoping(player, minionObjId, targetSlot, destinationSlot);
						break;
					case 3:
						MinionService.getInstance().buffPlayer(player, minionObjId, dopingItemId, targetSlot);
						break;
					default:
						log.debug("Unhandled minion doping action {} for {}", dopingAction, player.getName());
						break;
				}
				break;
			default:
				log.debug("Unhandled minion function subAction {} for {}", subAction, player.getName());
				break;
		}
	}
}
