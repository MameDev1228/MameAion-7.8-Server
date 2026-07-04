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
package ai.instance.infernalDrakenspireDepthsHard;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Berserk_Lava_Protector")
public class Berserk_Lava_ProtectorAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private boolean canThink = true;
	private int curentPercent = 100;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{40, 10});
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 40:
					case 10:
					    spawn(858682, 531.1626f, 198.4477f, 1681.8224f, (byte) 30); //IDSeal_Hard_Portal_02.
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								WorldMapInstance instance = getPosition().getWorldMapInstance();
								deleteNpcs(instance.getNpcs(858682));
							}
						}, 90000);
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					chooseAttack();
				}
			}
		}, 5000, 90000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 2)) {
			case 1:
			    magmaGlutten();
			break;
			case 2:
			    flamekiteGeist();
			break;
		}
	}
	
	private void magmaGlutten() {
		//Rise! My minions!
		sendMsg(1501209, getObjectId(), false, 0);
		//Scorch them with unquenchable pain!
		sendMsg(1501210, getObjectId(), false, 3000);
		if (getPosition().getWorldMapInstance().getNpc(858686) == null) {
			//Raging Hellfire.
			AI2Actions.useSkill(this, 21645);
			rndSpawn(858686, 6);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player: getKnownList().getKnownPlayers().values()) {
					SkillEngine.getInstance().getSkill(getOwner(), 21815, 60, getTarget()).useNoAnimationSkill(); //Magma Eruption.
					if (isInRange(player, 30)) {
						spawn(855623, player.getX(), player.getY(), player.getZ(), (byte) 0);
					}
				}
			}
		}, 3000);
	}
	
	private void flamekiteGeist() {
		//Assist your master!
		sendMsg(1501215, getObjectId(), false, 0);
		//Show your true selve, my minions!
		sendMsg(1501214, getObjectId(), false, 3000);
		if (getPosition().getWorldMapInstance().getNpc(858687) == null) {
			//Raging Hellfire.
			AI2Actions.useSkill(this, 21645);
			rndSpawn(858687, 6);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player: getKnownList().getKnownPlayers().values()) {
					SkillEngine.getInstance().getSkill(getOwner(), 21815, 60, getTarget()).useNoAnimationSkill(); //Magma Eruption.
					if (isInRange(player, 30)) {
						spawn(855623, player.getX(), player.getY(), player.getZ(), (byte) 0);
					}
				}
			}
		}, 3000);
	}
	
	private void shareSource() {
	    SkillEngine.getInstance().getSkill(getOwner(), 20769, 60, getOwner()).useNoAnimationSkill(); //Lava Protector.
		SkillEngine.getInstance().getSkill(getOwner(), 21643, 60, getOwner()).useNoAnimationSkill(); //Share Source.
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
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
		percents.clear();
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
		shareSource();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		addPercent();
		canThink = true;
		isHome.set(true);
		curentPercent = 100;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(858686));
		killNpc(instance.getNpcs(858687));
		deleteNpcs(instance.getNpcs(855623));
		deleteNpcs(instance.getNpcs(858682));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		percents.clear();
		//I merely return to the Locus.
		sendMsg(1501211, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		spawn(858802, 531.1845f, 212.3287f, 1681.8224f, (byte) 60); //Twin Source.
		spawn(858682, 531.1626f, 198.4477f, 1681.8224f, (byte) 30); //IDSeal_Hard_Portal_02.
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(858686));
		killNpc(instance.getNpcs(858687));
		deleteNpcs(instance.getNpcs(855623));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}