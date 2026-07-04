package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.util.List;

public abstract class PlayerMinionsDAO implements DAO
{
	@Override
	public String getClassName() {
		return PlayerMinionsDAO.class.getName();
	}
	public abstract void insertPlayerMinion(MinionCommonData petCommonData);
	public abstract void removePlayerMinion(Player player, int minionObjectId);
	public abstract List<MinionCommonData> getPlayerMinions(Player player);
	public abstract void setTime(Player player, int petId, long time);
	public abstract void updateName(MinionCommonData petCommonData);
	public abstract void updateMinionGrowth(MinionCommonData petCommonData);
	public abstract void updateMinionLock(MinionCommonData petCommonData);
	public abstract boolean isNameUsed(int playerId, String name);
}