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
package ai.instance.frozenMonolith;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Berserking_Tarukan")
public class Berserking_TarukanAI2 extends AggressiveNpcAI2
{
	private Future<?> checkTask;
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
				switch (percent) {
					case 90:
					case 70:
					case 50:
					case 30:
					case 10:
					    canThink = false;
					    tarukanWard1();
					    tarukanWard2();
						frigidaScaleblade();
						spawn(858397, 1014.50806f, 1070.472f, 624.8251f, (byte) 30); //Berserk Tarukan.
					break;
				}
				percents.remove(percent);
				canThink = false;
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						for (Player player: getKnownList().getKnownPlayers().values()) {
							PacketSendUtility.sendSys3Message(player, "\uE005", "Kill the 4 <Frigida Scaleblade> as soon as possible!!!");
						}
					}
				}, 2500);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						checkFrigidaScaleblade();
					}
				}, 3000);
		    }
			break;
		}
	}
	
	private void checkFrigidaScaleblade() {
		checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Npc scaleblade = getPosition().getWorldMapInstance().getNpc(858402);
				if (scaleblade == null) {
					canThink = true;
					checkTask.cancel(true);
					//I won't allow you to kill any more of my men!
		            sendMsg(1502459, getObjectId(), false, 0);
					//I will turn you into a slave of the queen.
					sendMsg(1502463, getObjectId(), false, 4000);
					WorldMapInstance instance = getPosition().getWorldMapInstance();
					//Berserk Tarukan.
					deleteNpcs(instance.getNpcs(858397));
					//Enraged Jotun Laborer.
					deleteNpcs(instance.getNpcs(858403));
					deleteNpcs(instance.getNpcs(858404));
					deleteNpcs(instance.getNpcs(858405));
					deleteNpcs(instance.getNpcs(858406));
					//Tarukan Ward.
					deleteNpcs(instance.getNpcs(858415));
					deleteNpcs(instance.getNpcs(858441));
					deleteNpcs(instance.getNpcs(858442));
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
					}
				}
			}
		}, 1 * 500, 1 * 500);
	}
	
	private void tarukanWard1() {
		Npc ward1 = getPosition().getWorldMapInstance().getNpc(858441);
		if (ward1 == null) {
			spawn(858415, 1014.2528f, 1090.1620f, 624.9850f, (byte) 0);
            spawn(858415, 1025.3575f, 1090.4048f, 624.9814f, (byte) 30);
            spawn(858415, 1014.1823f, 1100.3640f, 624.9830f, (byte) 60);
            spawn(858415, 1003.7544f, 1090.1696f, 624.9861f, (byte) 90);
            spawn(858415, 1014.3601f, 1079.0186f, 624.9815f, (byte) 0);
			spawn(858441, 1014.1777f, 1090.2600f, 624.9856f, (byte) 15);
			spawn(858441, 1014.2941f, 1090.2822f, 624.9851f, (byte) 45);
		}
	}
	private void tarukanWard2() {
		Npc ward2 = getPosition().getWorldMapInstance().getNpc(858442);
		if (ward2 == null) {
			spawn(858415, 1023.1201f, 1081.1558f, 624.9861f, (byte) 15);
            spawn(858415, 1005.1065f, 1099.4200f, 624.9861f, (byte) 15);
            spawn(858415, 1005.5419f, 1081.5610f, 624.9861f, (byte) 105);
            spawn(858415, 1023.2025f, 1099.1810f, 624.9861f, (byte) 104);
			spawn(858442, 1014.2122f, 1090.2657f, 624.9855f, (byte) 60);
			spawn(858442, 1014.2151f, 1090.1891f, 624.9856f, (byte) 90);
		}
	}
	
	private void frigidaScaleblade() {
		//Who... are you...?
		sendMsg(1502454, getObjectId(), false, 0);
		//Are you afraid of me? Then... die...
		sendMsg(1502455, getObjectId(), false, 4000);
		//I am Tarukan!
		sendMsg(1502456, getObjectId(), false, 8000);
		//You won't defeat me by being this slow!
		sendMsg(1502457, getObjectId(), false, 12000);
		Npc jotunLaborer1 = (Npc) spawn(858403, 1001.5987f, 1102.8242f, 624.9861f, (byte) 104);
		Npc jotunLaborer2 = (Npc) spawn(858404, 1027.7832f, 1076.6627f, 624.9861f, (byte) 45);
		Npc jotunLaborer3 = (Npc) spawn(858405, 1001.4663f, 1077.2726f, 624.9861f, (byte) 14);
		Npc jotunLaborer4 = (Npc) spawn(858406, 1027.1534f, 1102.9706f, 624.9861f, (byte) 74);
		Npc frigidaScaleblade1 = (Npc) spawn(858402, 1004.6131f, 1100.1654f, 624.9861f, (byte) 45);
		Npc frigidaScaleblade2 = (Npc) spawn(858402, 1024.8905f, 1079.6274f, 624.9861f, (byte) 105);
		Npc frigidaScaleblade3 = (Npc) spawn(858402, 1004.6076f, 1080.4962f, 624.9861f, (byte) 75);
		Npc frigidaScaleblade4 = (Npc) spawn(858402, 1023.6632f, 1099.5704f, 624.9861f, (byte) 15);
		//Drive out Balaur!
		NpcShoutsService.getInstance().sendMsg(jotunLaborer1, 1502037, jotunLaborer1.getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(jotunLaborer2, 1502037, jotunLaborer2.getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(jotunLaborer3, 1502037, jotunLaborer3.getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(jotunLaborer4, 1502037, jotunLaborer4.getObjectId(), 0, 0);
		//My soul will be watching you.
		NpcShoutsService.getInstance().sendMsg(jotunLaborer1, 1502461, jotunLaborer1.getObjectId(), 0, 10000);
		NpcShoutsService.getInstance().sendMsg(jotunLaborer2, 1502461, jotunLaborer2.getObjectId(), 0, 10000);
		NpcShoutsService.getInstance().sendMsg(jotunLaborer3, 1502461, jotunLaborer3.getObjectId(), 0, 10000);
		NpcShoutsService.getInstance().sendMsg(jotunLaborer4, 1502461, jotunLaborer4.getObjectId(), 0, 10000);
		//What part of QUEEN and ORDER do you not understand? Obey!
		NpcShoutsService.getInstance().sendMsg(frigidaScaleblade1, 1502016, frigidaScaleblade1.getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(frigidaScaleblade2, 1502016, frigidaScaleblade2.getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(frigidaScaleblade3, 1502016, frigidaScaleblade3.getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(frigidaScaleblade4, 1502016, frigidaScaleblade4.getObjectId(), 0, 0);
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
		checkTask.cancel(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(858402));
		deleteNpcs(instance.getNpcs(858397)); //Berserk Tarukan.
		deleteNpcs(instance.getNpcs(858403));
		deleteNpcs(instance.getNpcs(858404));
		deleteNpcs(instance.getNpcs(858405));
		deleteNpcs(instance.getNpcs(858406));
		deleteNpcs(instance.getNpcs(858415));
		deleteNpcs(instance.getNpcs(858441));
		deleteNpcs(instance.getNpcs(858442));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		//Fight to the bitter end...
		sendMsg(1502462, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		//Tarukan's spell has been destroyed.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_v70_HardMode_Instance_MSG_01, 0);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(858402));
		deleteNpcs(instance.getNpcs(858397)); //Berserk Tarukan.
		deleteNpcs(instance.getNpcs(858403));
		deleteNpcs(instance.getNpcs(858404));
		deleteNpcs(instance.getNpcs(858405));
		deleteNpcs(instance.getNpcs(858406));
		deleteNpcs(instance.getNpcs(858415));
		deleteNpcs(instance.getNpcs(858441));
		deleteNpcs(instance.getNpcs(858442));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}