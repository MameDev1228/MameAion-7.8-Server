package com.aionemu.gameserver.model.event_window;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerEventsWindowDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * 
 * @author Ranastic
 *
 */
public class PlayerEventWindowList implements EventWindowList<Player>{

	private final Map<Integer, PlayerEventWindowEntry> entry;
	private int remaining;
	
	public PlayerEventWindowList() {
		this.entry = new HashMap<Integer, PlayerEventWindowEntry>(0);
	}
	
	public PlayerEventWindowList(List<PlayerEventWindowEntry> entries) {
		this();
		for (PlayerEventWindowEntry e : entries) {
			entry.put(e.getId(), e);
		}
	}
	
	public PlayerEventWindowEntry[] getAll() {
		List<PlayerEventWindowEntry> allCp = new ArrayList<PlayerEventWindowEntry>();
		allCp.addAll(entry.values());
		return allCp.toArray(new PlayerEventWindowEntry[allCp.size()]);
	}
	
	public PlayerEventWindowEntry[] getBasic() {
		return entry.values().toArray(new PlayerEventWindowEntry[entry.size()]);
	}
	
	private synchronized boolean add(Player creature, int id, Timestamp lastStamp, int elapsed, PersistentState state) {
		entry.put(id, new PlayerEventWindowEntry(id, lastStamp, elapsed, state));
		DAOManager.getDAO(PlayerEventsWindowDAO.class).store(creature.getPlayerAccount().getId(), id, lastStamp, elapsed);
		return true;
	}
	
	@Override
	public boolean add(Player creature, int id, Timestamp lastStamp, int elapsed) {
		return add(creature, id, lastStamp, elapsed, PersistentState.NEW);
	}

	@Override
	public synchronized boolean remove(Player creature, int id) {
		PlayerEventWindowEntry entries = entry.get(id);
		if (entries != null) {
			entries.setPersistentState(PersistentState.DELETED);
			entry.remove(id);
			DAOManager.getDAO(PlayerEventsWindowDAO.class).delete(creature.getPlayerAccount().getId(), id);
		}
		return entry != null;
	}

	@Override
	public int size() {
		return entry.size();
	}

	public void setRemaining(int remain_minutes) {
		this.remaining = remain_minutes;
	}
	
	public int getRenaming() {
		return remaining;
	}

}
