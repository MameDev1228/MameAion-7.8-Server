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
package ai.instance.theVeilenthrone;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
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

@AIName("Dragon_Lord_Ereshkigal")
public class Dragon_Lord_EreshkigalAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private Future<?> magicTask;
	private Future<?> drakanTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
			startWaveDrakan();
			startMagicSquare();
		}
	}
	
	private void startMagicSquare() {
		magicTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelMagic();
				} else {
					magicSquare();
				}
			}
		}, 5000, 180000);
	}
	private void magicSquare() {
		//The magic glyph is now active and is strengthening Ereshkigal’s power.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDF7_Ere_Weapon_start, 0);
		//A magic ward has been activated to protect Ereshkigal.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDF7_Ere_Boss_Magic_Square_01, 5000);
		//You don’t even see how pitiful you are, do you?
		sendMsg(1502223, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(656423));
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				Npc magicSquareDrakan = getPosition().getWorldMapInstance().getNpc(656423);
				if (magicSquareDrakan == null) {
					spawn(656423, 750.0000f, 551.0000f, 725.0000f, (byte) 76);
					spawn(656423, 750.0000f, 447.0000f, 725.0000f, (byte) 45);
				}
			}
		}, 5000);
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelSkillTask();
				} else {
					ereshAttack();
				}
			}
		}, 5000, 20000);
	}
	
	private void ereshAttack() {
		switch (Rnd.get(1, 2)) {
			case 1:
			    ereshIceStorm();
			break;
			case 2:
			    ereshFrostFragment();
			break;
		}
	}
	
	private void ereshFrostFragment() {
		//I’ll freeze your bones!
		sendMsg(1502224, getObjectId(), false, 0);
		//얼음결정 소환.
		AI2Actions.useSkill(this, 18514);
		Npc frostFragment = getPosition().getWorldMapInstance().getNpc(656419);
		if (frostFragment == null) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					spawn(656419, 756.0000f, 516.0000f, 710.0000f, (byte) 0);
					spawn(656419, 796.0000f, 516.0000f, 710.0000f, (byte) 60);
					spawn(656419, 756.0000f, 482.0000f, 710.0000f, (byte) 1);
					spawn(656419, 796.0000f, 482.0000f, 710.0000f, (byte) 60);
					spawn(656419, 796.0000f, 499.0000f, 710.0000f, (byte) 60);
					spawn(656419, 763.0000f, 499.0000f, 710.0000f, (byte) 60);
					spawn(656419, 779.0000f, 516.0000f, 710.0000f, (byte) 60);
					spawn(656419, 779.0000f, 482.0000f, 710.0000f, (byte) 60);
				}
			}, 2000);
		}
	}
	
	private void ereshIceStorm() {
		//The bitter winds howl!
		sendMsg(1502222, getObjectId(), false, 0);
		//폭풍 소환.
		AI2Actions.useSkill(this, 18504);
		Npc iceStorm = (Npc) spawn(656418, 787.0000f, 450.0000f, 709.0000f, (byte) 32);
		/////////////////////////////////////////////
		iceStorm.getSpawn().setWalkerId("Ice_Storm");
		WalkManager.startWalking((NpcAI2) iceStorm.getAi2());
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(656418));
			}
		}, 120000);
	}
	
	private void startWaveDrakan() {
		drakanTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelDrakan();
				} else {
					ereshWaveDrakan();
				}
			}
		}, 5000, 120000);
	}
	private void ereshWaveDrakan() {
		//HAH! You think YOU can stop me?
		sendMsg(1502221, getObjectId(), false, 0);
		//Reinforcements have appeared to protect Ereshkigal.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDF7_Ere_Boss_Wave_01, 5000);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(657268));
		killNpc(instance.getNpcs(657269));
		killNpc(instance.getNpcs(657270));
		getOwner().getEffectController().removeAllEffects();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				Npc waveDrakan1 = (Npc) spawn(657268, 765.0000f, 540.0000f, 709.0000f, (byte) 90);
				Npc waveDrakan2 = (Npc) spawn(657269, 765.0000f, 540.0000f, 709.0000f, (byte) 90);
				Npc waveDrakan3 = (Npc) spawn(657270, 765.0000f, 540.0000f, 709.0000f, (byte) 90);
				Npc waveDrakan4 = (Npc) spawn(657268, 765.0000f, 458.0000f, 709.0000f, (byte) 30);
				Npc waveDrakan5 = (Npc) spawn(657269, 765.0000f, 458.0000f, 709.0000f, (byte) 30);
				Npc waveDrakan6 = (Npc) spawn(657270, 765.0000f, 458.0000f, 709.0000f, (byte) 30);
				////////////////////////////////////////////////////
				waveDrakan1.getSpawn().setWalkerId("Wave_Drakan_1");
				WalkManager.startWalking((NpcAI2) waveDrakan1.getAi2());
				////////////////////////////////////////////////////
				waveDrakan2.getSpawn().setWalkerId("Wave_Drakan_2");
				WalkManager.startWalking((NpcAI2) waveDrakan2.getAi2());
				////////////////////////////////////////////////////
				waveDrakan3.getSpawn().setWalkerId("Wave_Drakan_3");
				WalkManager.startWalking((NpcAI2) waveDrakan3.getAi2());
				////////////////////////////////////////////////////
				waveDrakan4.getSpawn().setWalkerId("Wave_Drakan_4");
				WalkManager.startWalking((NpcAI2) waveDrakan4.getAi2());
				////////////////////////////////////////////////////
				waveDrakan5.getSpawn().setWalkerId("Wave_Drakan_5");
				WalkManager.startWalking((NpcAI2) waveDrakan5.getAi2());
				////////////////////////////////////////////////////
				waveDrakan6.getSpawn().setWalkerId("Wave_Drakan_6");
				WalkManager.startWalking((NpcAI2) waveDrakan6.getAi2());
			}
		}, 5000);
	}
	
	private void cancelMagic() {
		if (magicTask != null && !magicTask.isCancelled()) {
			magicTask.cancel(true);
		}
	}
	private void cancelDrakan() {
		if (drakanTask != null && !drakanTask.isCancelled()) {
			drakanTask.cancel(true);
		}
	}
	private void cancelSkillTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
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
		cancelMagic();
		cancelDrakan();
		cancelSkillTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelMagic();
		cancelDrakan();
		isHome.set(true);
		cancelSkillTask();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(656418));
		killNpc(instance.getNpcs(656419));
		killNpc(instance.getNpcs(656420));
		killNpc(instance.getNpcs(656423));
		killNpc(instance.getNpcs(657268));
		killNpc(instance.getNpcs(657269));
		killNpc(instance.getNpcs(657270));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelMagic();
		cancelDrakan();
		cancelSkillTask();
		//So... This was the cost?
		sendMsg(1502225, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}