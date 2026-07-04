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

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DISPUTE_LAND;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneAttributes;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rinzler (Encom)
 */ 

public class DisputeLandService
{
	private boolean active;
    private FastList<Integer> worlds = new FastList<Integer>();
	private static final int duration = CustomConfig.DISPUTE_LAND_DURATION;
	private static final Logger log = LoggerFactory.getLogger(DisputeLandService.class);
	
	private DisputeLandService() {
	}
	
	public static DisputeLandService getInstance() {
		return DisputeLandServiceHolder.INSTANCE;
	}
	
	public void initDisputeLand() {
		if (CustomConfig.DISPUTE_LAND_ENABLED) {
		    CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					if (isActive()) {
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                setActive(false);
                            }
                        }, duration * 3600 * 1000);
					}
                }
			}, CustomConfig.DISPUTE_LAND_SCHEDULE);
		}
		worlds.add(210040000); //Heiron.
        worlds.add(210050000); //Inggison.
		worlds.add(220040000); //Beluslan.
		worlds.add(220070000); //Gelkmaros.
		worlds.add(800030000); //Crimson Katalam.
		worlds.add(800040000); //Crimson Danaria.
		worlds.add(800050000); //Lakrum.
		worlds.add(800060000); //Demaha.
    }
	
	public boolean isActive() {
        return active;
    }
	
	public void setActive(boolean value) {
        active = value;
        syncState();
        broadcast();
    }
	
	private void syncState() {
        for (int world: worlds) {
            if (world == 210040000 || //Heiron.
			    world == 210050000 || //Inggison.
				world == 220040000 || //Beluslan.
				world == 220070000 || //Gelkmaros.
				world == 800030000 || //Crimson Katalam.
				world == 800040000 || //Crimson Danaria.
				world == 800050000 || //Lakrum.
				world == 800060000) { //Demaha.
                continue;
            } if (active) {
                World.getInstance().getWorldMap(world).setWorldOption(ZoneAttributes.PVP_ENABLED);
            } else {
                World.getInstance().getWorldMap(world).removeWorldOption(ZoneAttributes.PVP_ENABLED);
            }
        }
    }
	
	private void broadcast(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DISPUTE_LAND(worlds, active));
	}
	
	private void broadcast() {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                broadcast(player);
            }
        });
    }
	
	public void onLogin(Player player) {
		broadcast(player);
	}
	
	private static class DisputeLandServiceHolder {
		private static final DisputeLandService INSTANCE = new DisputeLandService();
	}
}