package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticDoorService
{
	private static final Logger log = LoggerFactory.getLogger(StaticDoorService.class);
	
	public static StaticDoorService getInstance() {
		return SingletonHolder.instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final StaticDoorService instance = new StaticDoorService();
	}
	
	public void openStaticDoor(final Player player, int doorId) {
		if (player.getAccessLevel() >= 3) {
			PacketSendUtility.sendMessage(player, "Door Id: " + doorId);
		}
		StaticDoor door = player.getPosition().getWorldMapInstance().getDoors().get(doorId);
		if (door == null) {
			log.warn("Not spawned door worldId: "+ player.getWorldId()+" doorId: "+doorId);
			return;
		}
		int keyId = door.getObjectTemplate().getKeyId();
		if (player.getAccessLevel() >= 3) {
			PacketSendUtility.sendMessage(player, "Key Id: " + keyId);
		} if (checkStaticDoorKey(player, doorId, keyId)) {
			door.setOpen(true);
		} if (player.getPosition().isInstanceMap()) {
			InstanceService.onOpenDoor(player, doorId);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onOpenDoor(player, doorId);
		}
	}
	
	public boolean checkStaticDoorKey(Player player, int doorId, int keyId) {
		if (player.getAccessLevel() >= AdminConfig.DOORS_OPEN) {
			return true;
		} if (keyId == 0) {
			return true;
		} if (keyId == 1) {
			return false;
		} if (!player.getInventory().decreaseByItemId(keyId, 1)) {
			//You need a key to open the door.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_OPEN_DOOR_NEED_KEY_ITEM);
			return false;
		}
		return true;
	}
}