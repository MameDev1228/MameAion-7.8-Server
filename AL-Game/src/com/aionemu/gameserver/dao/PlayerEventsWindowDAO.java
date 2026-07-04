package com.aionemu.gameserver.dao;

import java.sql.Timestamp;
import java.util.List;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.event_window.PlayerEventWindowList;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public abstract class PlayerEventsWindowDAO implements DAO
{
	public abstract PlayerEventWindowList load(Player accountId);
	public abstract void insert(int accountId, int eventId, Timestamp last_stamp);
	public abstract boolean store(int accountId, int eventId, Timestamp last_stamp, int elapsed);
	public abstract void delete(int accountId, int eventId);
	public abstract Timestamp getLastStamp(int accountId, int eventId);
	public abstract int getElapsed(int accountId, int eventId);
	public abstract void updateElapsed(int accountId, int eventId, int elapsed);
	public abstract int getRewardRecivedCount(int accountId, int eventId);
	public abstract void setRewardRecivedCount(int accountId, int eventId, int rewardRecivedCount);
	public abstract List<Integer> getEventsWindow(int accountId);
	public abstract boolean delete();
	
	public final String getClassName() {
		return PlayerEventsWindowDAO.class.getName();
	}
}