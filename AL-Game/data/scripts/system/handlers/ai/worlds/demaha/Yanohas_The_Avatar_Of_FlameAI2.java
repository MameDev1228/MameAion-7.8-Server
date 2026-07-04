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
package ai.worlds.demaha;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Yanohas_The_Avatar_Of_Flame")
public class Yanohas_The_Avatar_Of_FlameAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private Future<?> sendPacketTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
    protected void handleMoveValidate() {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getWorldId() == getOwner().getWorldId()) {
                    PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, getOwner()));
                }
            }
        });
    }
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(final Player player) {
                sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getWorldId() == getOwner().getWorldId()) {
                            if (getOwner().isSpawned()) {
                                PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, getOwner()));
                            }
                        }
                    }
                }, 1000, 2000);
            }
        });
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(final Player player) {
                sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getWorldId() == getOwner().getWorldId()) {
                            PacketSendUtility.sendPacket(player, new SM_FLAG_UPDATE(getOwner()));
							getOwner().getController().onDelete();
                        }
                    }
                }, 1000, 2000);
            }
        });
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					kingLava();
				}
			}
		}, 5000, 180000);
	}
	
	private void kingLava() {
		//Who dares awaken me ?
		sendMsg(1501392, getObjectId(), false, 0);
		//You must want to perish. So be it!
		sendMsg(1501395, getObjectId(), false, 4000);
		//Freeze and face oblivion!
		sendMsg(1501397, getObjectId(), false, 8000);
		//Infernal Flame Explosion.
		SkillEngine.getInstance().getSkill(getOwner(), 21767, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				spawn(282746, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(282746));
			}
		}, 20000);
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(282746));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		treasureChest();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(701481, 4);
			}
		}, 10000);
		//Urgh… I... shall return...
		sendMsg(1501396, getObjectId(), false, 0);
		//Rrrargh!
		sendMsg(1501398, getObjectId(), false, 3000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(282746));
	}
	
	private void treasureChest() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//A treasure chest has appeared.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDAbRe_Core_NmdC_BoxSpawn, 10000);
			}
		});
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 5);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}