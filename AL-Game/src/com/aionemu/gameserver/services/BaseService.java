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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.dao.BaseDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FLAG_INFO;
import com.aionemu.gameserver.services.base.Base;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Rinzler
 */

public class BaseService
{
	private static final Logger log = LoggerFactory.getLogger(BaseService.class);
	private final Map<Integer, Base<?>> active = new FastMap<Integer, Base<?>>().shared();
	private Map<Integer, BaseLocation> bases;
	
	public void initBaseLocations() {
		log.info("Initializing <Bases>...");
		bases = DataManager.BASE_DATA.getBaseLocations();
		DAOManager.getDAO(BaseDAO.class).loadBaseLocations(bases);
	}
	
	public void initBases() {
		for (BaseLocation base : getBaseLocations().values()) {
			start(base.getId());
		}
	}
	
	public void initBaseReset() {
		Race race = null;
		//Crimson Danaria MON-SUN "12PM-15PM-18PM-21PM-0AM"
		String daily = "0 0 12,15,18,21,0 ? * * *";
		//Crimson Danaria.
		CronService.getInstance().schedule(new Runnable() {
			public void run() {
				capture(81, Race.NPC);
				log.info("<Crimson Danaria 81th Base Reset>");
			}
		}, daily);
	}
	
	public Map<Integer, BaseLocation> getBaseLocations() {
		return bases;
	}
	
	public BaseLocation getBaseLocation(int id) {
		return bases.get(id);
	}
	
	public void start(final int id) {
		final Base<?> base;
		synchronized (this) {
			if (active.containsKey(id)) {
				return;
			}
			base = new Base<BaseLocation>(getBaseLocation(id));
			active.put(id, base);
		}
		base.start();
	}
	
	public void stop(int id) {
		if (!isActive(id)) {
			log.info("Trying to stop not active base:" + id);
			return;
		}
		Base<?> base;
		synchronized (this) {
			base = active.remove(id);
		} if (base == null || base.isFinished()) {
			log.info("Trying to stop null or finished base:" + id);
			return;
		}
		base.stop();
		start(id);
	}
	
	public void capture(int id, Race race) {
		if (!isActive(id)) {
			log.info("Detecting not active base capture.");
			return;
		}
		getActiveBase(id).setRace(race);
		stop(id);
		World.getInstance().getWorldMap(getBaseLocation(id).getWorldId()).getMainWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				onEnterBaseWorld(player);
			}
		});
		getDAO().updateLocation(getBaseLocation(getBaseLocation(id).getId()));
	}
	
	public boolean isActive(int id) {
		return active.containsKey(id);
	}
	
	public Base<?> getActiveBase(int id) {
		return active.get(id);
	}
	
	public void onEnterBaseWorld(Player player) {
		for (BaseLocation baseLocation : getBaseLocations().values()) {
			if (baseLocation.getWorldId() == player.getWorldId() && isActive(baseLocation.getId())) {
				Base<?> base = getActiveBase(baseLocation.getId());
				PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, base.getFlag()));
				player.getController().updateZone();
			    player.getController().updateNearbyQuests();
			}
		}
	}
	
	public void broadcastUpdate(final BaseLocation baseLocation) {
		World.getInstance().getWorldMap(baseLocation.getWorldId()).getMainWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (isActive(baseLocation.getId())) {
					Base<?> base = getActiveBase(baseLocation.getId());
					PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, base.getFlag()));
					player.getController().updateZone();
			        player.getController().updateNearbyQuests();
				}
			}
		});
	}
	
	public static BaseService getInstance() {
		return BaseServiceHolder.INSTANCE;
	}
	
	private static class BaseServiceHolder {
		private static final BaseService INSTANCE = new BaseService();
	}
	
	private BaseDAO getDAO() {
        return DAOManager.getDAO(BaseDAO.class);
    }
}