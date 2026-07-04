package com.aionemu.gameserver.controllers;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ranastic
 */

public class MinionController extends VisibleObjectController<Minion>
{
	@Override
	public void see(VisibleObject object) {
	}
	
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
	}
	
	public static class MinionUpdateTask implements Runnable {
		private final Player player;
		private long startTime = 0;
		
		public MinionUpdateTask(Player player) {
			this.player = player;
		}
		
		@Override
		public void run() {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			} try {
				Minion minion = player.getMinion();
				if (minion == null) {
					throw new IllegalStateException("Minion is null");
				}
			} catch (Exception ex) {
				player.getController().cancelTask(TaskId.MINION_UPDATE);
			}
		}
	}
}