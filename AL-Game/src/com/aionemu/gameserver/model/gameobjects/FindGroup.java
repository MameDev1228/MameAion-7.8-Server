package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;

public class FindGroup
{
	private Player object;
	private String message;
	private int groupType, minMembers, instanceId;
	private int lastUpdate = (int) (System.currentTimeMillis() / 1000);
	
	public FindGroup(Player object, String message, int groupType) {
		this.object = object;
		this.message = message;
		this.groupType = groupType;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getGroupType() {
		return groupType;
	}
	
	public int getObjectId() {
		return object.getObjectId();
	}
	
	public int getInstanceId() {
		return instanceId;
	}
	
	public int getMinMembers() {
		return minMembers;
	}
	
	public int getClassId() {
		if (object instanceof Player) {
			return ((Player) (object)).getPlayerClass().getClassId();
		}
		return 0;
	}
	
	public int getMinLevel() {
		if (object instanceof Player) {
			return ((Player) (object)).getLevel();
		}
		return 1;
	}
	
	public int getMaxLevel() {
		if (object instanceof Player) {
			return ((Player) (object)).getLevel();
		}
		return 1;
	}
	
	public int getUnk() {
		if (object instanceof Player) {
			return 65557;
		} else {
			return 0;
		}
	}
	
	public int getLastUpdate() {
		return lastUpdate;
	}
	
	public String getName() {
		if (object instanceof Player) {
			return ((Player) object).getName();
		}
		return "";
	}
	
	public int getSize() {
		if (object instanceof Player) {
			return 1;
		}
		return 1;
	}
	
	public void setMessage(String message) {
		lastUpdate = (int) (System.currentTimeMillis() / 1000);
		this.message = message;
	}
	
	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}
}