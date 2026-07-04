package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.CubeKinahExpandEnum;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.CubeExpandTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubeExpandService
{
	private static final Logger log = LoggerFactory.getLogger(CubeExpandService.class);
	
	public static void expandCube(final Player player, Npc npc) {
		final CubeExpandTemplate expandTemplate = DataManager.CUBEEXPANDER_DATA.getCubeExpandListTemplate(npc.getNpcId());
		if (expandTemplate == null) {
			log.error("Cube Expand Template could not be found for Npc ID: " + npc.getObjectId());
			return;
		} if (npcCanExpandLevel(expandTemplate, player.getNpcExpands() + 1) && canExpand(player)) {
			if (player.getNpcExpands() >= 18) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EXTEND_INVENTORY_CANT_EXTEND_MORE);
				return;
			}
			final int price = getPriceByLevel(expandTemplate, player.getNpcExpands() + 1);
			RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {
				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (price > player.getInventory().getKinah()) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_WAREHOUSE_EXPAND_NOT_ENOUGH_MONEY);
						return;
					}
					expand(responder, true);
					player.getInventory().decreaseKinah(price);
				}
				@Override
				public void denyRequest(Creature requester, Player responder) {
				}
			};
			boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, responseHandler);
			if (result) {
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, 0,0, String.valueOf(price)));
			}
		} else
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300430));
	}

	public static void expandKinah(Player player) {
		CubeKinahExpandEnum expandEnum = CubeKinahExpandEnum.getCostById(player.getCommonData().getNpcExpands());
		if (player.getInventory().tryDecreaseKinah(expandEnum.getCost())) {
			CubeExpandService.expand(player, true);
		}
	}

	public static void expandTicket(Player player) {
		if (player.getInventory().decreaseByItemId(186000419, 1)) { //Cube Expansion Key.
			CubeExpandService.expand(player, true);
		} else if (player.getInventory().decreaseByItemId(186000440, 1)) { //Cube Expansion Key.
			CubeExpandService.expand(player, true);
		} else if (player.getInventory().decreaseByItemId(186000444, 1)) { //Cube Expansion Key.
			CubeExpandService.expand(player, true);
		} else if (player.getInventory().decreaseByItemId(186000445, 1)) { //Cube Expansion Key.
			CubeExpandService.expand(player, true);
		}
	}
	
	public static void expand(Player player, boolean isNpcExpand) {
		if (!canExpand(player)) {
			return;
		}
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300431, "9"));
		if (isNpcExpand) {
			player.setNpcExpands(player.getNpcExpands() + 1);
		} else {
			player.setQuestExpands(player.getQuestExpands() + 1);
		}
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(StorageType.CUBE, player));
	}
	
	public static boolean canExpand(Player player) {
	    return validateNewSize(player.getNpcExpands() + player.getQuestExpands() + 1);
    }
	
	public static boolean canExpandByTicket(Player player, int ticketLevel) {
	    if (!canExpand(player)) {
			return false;
		}
	    int ticketExpands = player.getQuestExpands() - getCompletedCubeQuests(player);
	    return ticketExpands < ticketLevel;
    }
	
	private static boolean validateNewSize(int level) {
		if (level < 0 || level > 18) {
			return false;
		}
		return true;
	}
	
	private static boolean npcCanExpandLevel(CubeExpandTemplate clist, int level) {
		if (!clist.contains(level)) {
			return false;
		}
		return true;
	}
	
	private static int getCompletedCubeQuests(Player player) {
	    int result = 0;
	    QuestStateList qs = player.getQuestStateList();
	    int[] questIds = {61301, 71301};
	    for (int q: questIds) {
		    if (qs.getQuestState(q) != null && qs.getQuestState(q).getStatus().equals(QuestStatus.COMPLETE)) {
				result++;
			}
	    }
	    return result > 2 ? 2 : result;
    }
	
	private static int getPriceByLevel(CubeExpandTemplate clist, int level) {
		return clist.get(level).getPrice();
	}
}