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
package ai.instance.primethForge;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.EmotionType;
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

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Berserking_Frigida")
public class Berserking_FrigidaAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{90, 70, 50, 30, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				//For the queen!
				sendMsg(1502094, getObjectId(), false, 3000);
				getOwner().getController().abortCast();
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getController().cancelCurrentSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
						chooseAttack();
				  		setStateIfNot(AIState.WALKING);
						SkillEngine.getInstance().getSkill(getOwner(), 19968, 60, getOwner()).useNoAnimationSkill();
				  		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				  		WalkManager.startWalking(Berserking_FrigidaAI2.this);
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				  	}
			    }, 4000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
				  		canThink = true;
						//What part of QUEEN and ORDER do you not understand? Obey!
						sendMsg(1502016, getObjectId(), false, 0);
						WorldMapInstance instance = getPosition().getWorldMapInstance();
						killNpc(instance.getNpcs(650012));
						killNpc(instance.getNpcs(650027));
						killNpc(instance.getNpcs(656083));
						killNpc(instance.getNpcs(656085));
						EffectController ef = getOwner().getEffectController();
						if (ef.hasAbnormalEffect(19968)) {
							ef.removeEffect(19968);
						}
				  		Creature creature = getAggroList().getMostHated();
						if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
							setStateIfNot(AIState.FIGHT);
							think();
						} else {
							getMoveController().abortMove();
							getOwner().setTarget(creature);
							getOwner().getGameStats().renewLastAttackTime();
							getOwner().getGameStats().renewLastAttackedTime();
							getOwner().getGameStats().renewLastChangeTargetTime();
							getOwner().getGameStats().renewLastSkillTime();
							setStateIfNot(AIState.WALKING);
							getOwner().setState(1);
							getOwner().getMoveController().moveToTargetObject();
							PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									SkillEngine.getInstance().getSkill(getOwner(), 20278, 60, getTarget()).useNoAnimationSkill(); //Holistic Arrow.
								}
							}, 3000);
						}
				  	}
				}, 34000);
		    }
			break;
		}
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 2)) {
			case 1:
			    eyeOfTheAbyss();
				frigidaWizard();
			break;
			case 2:
			    frostCrystal();
				frozenRestraint();
			break;
		}
	}
	
	private void eyeOfTheAbyss() {
		//Frost, engulf them!
		sendMsg(1502093, getObjectId(), false, 3000);
		Npc eyeOfTheAbyss = getPosition().getWorldMapInstance().getNpc(656085);
		if (eyeOfTheAbyss == null) {
			spawn(656085, 1326.0000f, 1090.0000f, 374.0000f, (byte) 60);
		}
	}
	private void frigidaWizard() {
		if (getPosition().getWorldMapInstance().getNpc(650012) == null) {
			//Cold Wrath.
			AI2Actions.useSkill(this, 20282);
			rndSpawn(650012, 3);
		}
	}
	private void frozenRestraint() {
		//Freeze to the bone!
		sendMsg(1502092, getObjectId(), false, 3000);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			//Ice Shackles.
			AI2Actions.useSkill(this, 20277);
			if (isInRange(player, 30)) {
				spawn(656083, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
	private void frostCrystal() {
		Npc frostCrystal = getPosition().getWorldMapInstance().getNpc(650027);
		if (frostCrystal == null) {
			spawn(650027, 1312.2501f, 1102.8170f, 374.4916f, (byte) 89);
            spawn(650027, 1299.6364f, 1090.1102f, 374.4916f, (byte) 0);
            spawn(650027, 1312.3540f, 1077.4686f, 374.4916f, (byte) 31);
            spawn(650027, 1324.9097f, 1090.2418f, 374.4916f, (byte) 60);
		}
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 15);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
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
	protected void handleDespawned() {
		super.handleDespawned();
		percents.clear();
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		addPercent();
		canThink = true;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(650012));
		killNpc(instance.getNpcs(650027));
		killNpc(instance.getNpcs(656083));
		deleteNpcs(instance.getNpcs(656085));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		//I will have my revenge one day.
		sendMsg(1502026, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(650012));
		killNpc(instance.getNpcs(650027));
		killNpc(instance.getNpcs(656083));
		deleteNpcs(instance.getNpcs(656085));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}