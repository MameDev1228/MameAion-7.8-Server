package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.MinionAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.MinionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author Ranastic
 */

public class CM_MINIONS extends AionClientPacket 
{
	Logger log = LoggerFactory.getLogger(CM_MINIONS.class);
	
	private int actionId;
	private MinionAction action;
	private int subAction;
	private int itemObjId;
	private int minionObjId;
	private int unk2;
	private int lock;
	private int isAuto;
	private int minionToEvolve;
	private int minionToGrowth;
	private int conbine1;
	private int conbine2;
	private int conbine3;
	private int conbine4;
	private ArrayList<Integer> sacrificeMinions = new ArrayList<Integer>();
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
		actionId = readH();
		action = MinionAction.getActionById(actionId);
		switch (action) {
			case ADOPT: //Adopt
				itemObjId = readD();
			break;
			case DISMISS: //Dismiss
				minionObjId = readD();
			break;
			case RENAME: //Rename
				minionObjId = readD();
				name = readS();
			break;
			case LOCK: //Lock
				minionObjId = readD();
				lock = readC();
			break;
			case SUMMON: //Summon
			case UNSUMMON: //Unsummon
				minionObjId = readD();
			break;
			case GROWTH: //Growth
				sacrificeMinions.clear();
				minionToGrowth = readD();
				for (int i = 0; i < 10; i++) {
					sacrificeMinions.add(readD());
				}
			break;
			case EVOLVE: //Evolved
				minionToEvolve = readD();
			break;
			case COMBINE: //Combine
				conbine1 = readD();
				conbine2 = readD();
				conbine3 = readD();
				conbine4 = readD();
			break;
			case FUNCTION_SETTING: //Function Settings
				subAction = readD();
				switch (subAction) {
					case 1:
						minionObjId = readD();
						activateLoot = readC();
						readD();
						readD();
					break;
					case 0:
						switch (dopingAction) {
							case 0:
								minionObjId = readD();
								dopingItemId = readD();
								targetSlot = readD();
							break;
							case 1:
								minionObjId = readD();
								dopingItemId = readD();
								targetSlot = readD();
							break;
							case 2:
								minionObjId = readD();
								dopingItemId = readD();
								targetSlot = readD();
								destinationSlot = readD();
							break;
							case 3:
								minionObjId = readD();
								dopingItemId = readD();
								targetSlot = readD();
							break;
						}
					break;
				}
			break;
			case FUNCTION:
			break;
			case ENERGY_RECHARGE: //Energy Recharge
				unk2 = readC();
				isAuto = readC();
			break;
			case AUTO_FUNCTION: //Auto Function
				isAuto = readC();
			break;
			case UNK:
				readD();
				readC();
				readH();
			break;
			case BUFFING:
				readC();
				readC();
				readC();
			break;
			default:
			break;
		}
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		switch (action) {
			case ADOPT:
				if (player.getMinionList().getMinions().size() >= 30) {
					//You have exceeded the maximum amount and cannot use Minion contracts right now.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404322));
				} else {
					MinionService.getInstance().makeMinion(player, itemObjId);
				}
			break;
			case DISMISS:
				MinionService.getInstance().dismissMinion(player, minionObjId);
			break;
			case RENAME:
				MinionService.getInstance().renameMinion(player, minionObjId, name);
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
				switch (subAction) {
					case 1:
						MinionService.getInstance().activateLoot(player, minionObjId, activateLoot != 0);
					break;
					case 0:
						switch (dopingAction) {
							case 0:
								//MinionService.getInstance().addItemToDopingBag(player, dopingAction, minionObjId, dopingItemId, targetSlot);
							break;
							case 2:
								//MinionService.getInstance().relocateDoping(player, minionObjId, targetSlot, destinationSlot);
							break;
							case 3:
								//MinionService.getInstance().buffPlayer(player, minionObjId, dopingItemId, targetSlot);
							break;
						}
					break;
				}
			break;
		}
	}
}