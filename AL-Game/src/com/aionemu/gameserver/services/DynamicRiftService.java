/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.services.CronService;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.shedule.DynamicSchedule;
import com.aionemu.gameserver.configs.shedule.DynamicSchedule.Dynamic;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.dynamicrift.DynamicRiftLocation;
import com.aionemu.gameserver.model.dynamicrift.DynamicRiftStateType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.dynamicriftspawns.*;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.dynamicriftservice.*;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastMap;

import java.util.*;

/**
 * @author Rinzler (Encom)
 */

public class DynamicRiftService
{
	private DynamicSchedule dynamicSchedule;
	private Map<Integer, DynamicRiftLocation> dynamic;
	private static final int duration = CustomConfig.DYNAMIC_RIFT_DURATION;
	private final Map<Integer, DynamicRift<?>> activeDynamicRift = new FastMap<Integer, DynamicRift<?>>().shared();
	
	public void initDynamicRiftLocations() {
		if (CustomConfig.DYNAMIC_RIFT_ENABLED) {
			dynamic = DataManager.DYNAMIC_RIFT_DATA.getDynamicRiftLocations();
			for (DynamicRiftLocation loc: getDynamicRiftLocations().values()) {
				spawn(loc, DynamicRiftStateType.CLOSED);
			}
		} else {
			dynamic = Collections.emptyMap();
		}
	}
	
	public void initDynamicRift() {
		if (CustomConfig.DYNAMIC_RIFT_ENABLED) {
		    dynamicSchedule = DynamicSchedule.load();
		    for (Dynamic dynamic: dynamicSchedule.getDynamicsList()) {
			    for (String riftTime: dynamic.getRiftTimes()) {
				    CronService.getInstance().schedule(new DynamicStartRunnable(dynamic.getId()), riftTime);
			    }
			}
		}
	}
	
	public void startDynamicRift(final int id) {
		final DynamicRift<?> portal;
		synchronized (this) {
			if (activeDynamicRift.containsKey(id)) {
				return;
			}
			portal = new Portal(dynamic.get(id));
			activeDynamicRift.put(id, portal);
		}
		portal.start();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				stopDynamicRift(id);
			}
		}, duration * 3600 * 1000);
	}
	
	public void stopDynamicRift(int id) {
		if (!isDynamicRiftInProgress(id)) {
			return;
		}
		DynamicRift<?> portal;
		synchronized (this) {
			portal = activeDynamicRift.remove(id);
		} if (portal == null || portal.isClosed()) {
			return;
		}
		portal.stop();
	}
	
	public void spawn(DynamicRiftLocation loc, DynamicRiftStateType dstate) {
		if (dstate.equals(DynamicRiftStateType.OPEN)) {
		}
		List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getDynamicRiftSpawnsByLocId(loc.getId());
		for (SpawnGroup2 group: locSpawns) {
			for (SpawnTemplate st: group.getSpawnTemplates()) {
				DynamicRiftSpawnTemplate dynamicRifttemplate = (DynamicRiftSpawnTemplate) st;
				if (dynamicRifttemplate.getDStateType().equals(dstate)) {
					loc.getSpawned().add(SpawnEngine.spawnObject(dynamicRifttemplate, 1));
				}
			}
		}
	}
	
	public boolean shugoCaravanMsg(int id) {
        switch (id) {
            case 1:
                World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						//The Shugo Merchant League has arrived.
						PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_HF_ShugoCaravanAppear, 0);
					}
				});
			    return true;
            default:
                return false;
        }
    }
	
	public void despawn(DynamicRiftLocation loc) {
		if (loc.getSpawned() == null) {
        	return;
		} for (VisibleObject obj: loc.getSpawned()) {
            Npc spawned = (Npc) obj;
            spawned.setDespawnDelayed(true);
            if (spawned.getAggroList().getList().isEmpty()) {
                spawned.getController().cancelTask(TaskId.RESPAWN);
                obj.getController().onDelete();
            }
        }
        loc.getSpawned().clear();
	}
	
	public boolean isDynamicRiftInProgress(int id) {
		return activeDynamicRift.containsKey(id);
	}
	
	public Map<Integer, DynamicRift<?>> getActiveDynamicRift() {
		return activeDynamicRift;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public DynamicRiftLocation getDynamicRiftLocation(int id) {
		return dynamic.get(id);
	}
	
	public Map<Integer, DynamicRiftLocation> getDynamicRiftLocations() {
		return dynamic;
	}
	
	public static DynamicRiftService getInstance() {
		return DynamicRiftServiceHolder.INSTANCE;
	}
	
	private static class DynamicRiftServiceHolder {
		private static final DynamicRiftService INSTANCE = new DynamicRiftService();
	}
}