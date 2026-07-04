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
package ai.instance.benirunerkEstate;

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

@AIName("SVR_07")
public class Colossal_Mecha_SVR_07AI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
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
					chooseAttack();
				}
			}
		}, 5000, 40000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 3)) {
			case 1:
			    houseSummon();
			break;
			case 2:
			    houseFireShoot();
			break;
			case 3:
				shugoStellaGunner();
			break;
		}
	}
	
	private void shugoStellaGunner() {
		//Benirunerk is the star of the party! Akakak!
		sendMsg(1502544, getObjectId(), false, 0);
		//What's a party without a little dancing?
		sendMsg(1502542, getObjectId(), false, 4000);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(858536));
		if (getPosition().getWorldMapInstance().getNpc(858515) == null) {
			rndSpawn(858515, 3);
		}
	}
	
	private void houseSummon() {
		//Fire! Light everything up!
		sendMsg(1502539, getObjectId(), false, 0);
		//IDF8_House_Fireball_SpellAtk_Paralyze.
		AI2Actions.useSkill(this, 22890);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player: getKnownList().getKnownPlayers().values()) {
					if (isInRange(player, 30)) {
						spawn(858536, player.getX(), player.getY(), player.getZ(), (byte) 0);
					}
				}
			}
		}, 3500);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(858536));
			}
		}, 18500);
	}
	
	private void houseFireShoot() {
		//The best parties have fireworks! Akakakak!
		sendMsg(1502537, getObjectId(), false, 0);
		//Agh! Once this recharges, Daevas will see who's boss!
		sendMsg(1502538, getObjectId(), false, 3000);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(858536));
		for (Player player: getKnownList().getKnownPlayers().values()) {
			//IDF8_House_FireShoot_Cinema_Skill.
			SkillEngine.getInstance().getSkill(getOwner(), 22889, 60, getTarget()).useNoAnimationSkill();
			Npc FireShoot = getPosition().getWorldMapInstance().getNpc(858519);
			if (FireShoot == null) {
				spawn(858519, 558.0847f, 714.3509f, 179.0000f, (byte) 111);
				spawn(858519, 552.9049f, 707.9489f, 178.6227f, (byte) 76);
				spawn(858519, 544.7648f, 703.2543f, 179.0000f, (byte) 68);
				spawn(858519, 535.9032f, 703.4797f, 178.9939f, (byte) 59);
				spawn(858519, 528.6259f, 704.9779f, 179.0247f, (byte) 50);
				spawn(858519, 522.4985f, 709.9137f, 179.0000f, (byte) 46);
				spawn(858519, 514.3698f, 714.4004f, 179.0000f, (byte) 83);
				spawn(858519, 516.6615f, 708.3797f, 179.0000f, (byte) 82);
				spawn(858519, 509.6461f, 719.5827f, 179.0000f, (byte) 42);
				spawn(858519, 506.9081f, 726.5063f, 179.0000f, (byte) 64);
				spawn(858519, 506.6913f, 734.1969f, 179.0000f, (byte) 61);
				spawn(858519, 504.9431f, 741.8888f, 178.9339f, (byte) 68);
				spawn(858519, 505.7577f, 750.3699f, 179.0654f, (byte) 62);
				spawn(858519, 509.0059f, 756.7926f, 179.0857f, (byte) 55);
				spawn(858519, 512.9055f, 761.8206f, 178.8750f, (byte) 12);
				spawn(858519, 518.9978f, 766.0150f, 178.8750f, (byte) 12);
				spawn(858519, 525.0557f, 768.5293f, 178.8750f, (byte) 1);
				spawn(858519, 532.0499f, 769.7147f, 178.8750f, (byte) 3);
				spawn(858519, 538.9396f, 770.6446f, 178.8087f, (byte) 3);
				spawn(858519, 545.7373f, 769.7454f, 178.8750f, (byte) 116);
				spawn(858519, 540.2804f, 703.2117f, 179.0000f, (byte) 93);
				spawn(858519, 545.7464f, 708.9034f, 179.0000f, (byte) 106);
				spawn(858519, 550.8429f, 714.4906f, 179.0000f, (byte) 108);
				spawn(858519, 563.0824f, 719.6827f, 179.0198f, (byte) 110);
				spawn(858519, 564.3843f, 727.1105f, 179.0000f, (byte) 1);
				spawn(858519, 566.1649f, 733.3186f, 179.0000f, (byte) 3);
				spawn(858519, 567.5264f, 740.1080f, 179.0000f, (byte) 5);
				spawn(858519, 567.9578f, 747.0205f, 179.0000f, (byte) 9);
				spawn(858519, 572.9156f, 743.5385f, 179.0000f, (byte) 8);
				spawn(858519, 566.5509f, 753.8021f, 179.0000f, (byte) 10);
				spawn(858519, 563.8804f, 759.9251f, 178.8750f, (byte) 10);
				spawn(858519, 558.6600f, 764.9627f, 178.8750f, (byte) 10);
				spawn(858519, 556.0268f, 769.3513f, 178.8750f, (byte) 21);
				spawn(858519, 550.8675f, 769.1499f, 178.8750f, (byte) 21);
				spawn(858519, 554.0429f, 722.2588f, 179.0000f, (byte) 109);
				spawn(858519, 557.6153f, 731.1673f, 179.0000f, (byte) 109);
				spawn(858519, 558.8436f, 725.1404f, 179.0000f, (byte) 109);
				spawn(858519, 560.2571f, 738.3495f, 179.0000f, (byte) 116);
				spawn(858519, 560.7826f, 747.2522f, 179.0000f, (byte) 32);
				spawn(858519, 558.4812f, 755.0652f, 178.8084f, (byte) 32);
				spawn(858519, 552.5414f, 761.3074f, 178.8750f, (byte) 41);
				spawn(858519, 544.3894f, 762.7432f, 178.8750f, (byte) 63);
				spawn(858519, 535.8200f, 762.4408f, 178.8750f, (byte) 63);
				spawn(858519, 525.3353f, 760.6578f, 178.8750f, (byte) 63);
				spawn(858519, 529.1991f, 764.0041f, 178.8750f, (byte) 70);
				spawn(858519, 518.2835f, 757.0138f, 178.8750f, (byte) 70);
				spawn(858519, 513.1209f, 751.9028f, 178.8750f, (byte) 76);
				spawn(858519, 511.8502f, 743.8879f, 179.0000f, (byte) 87);
				spawn(858519, 513.1913f, 736.3904f, 179.0000f, (byte) 91);
				spawn(858519, 513.4501f, 727.6294f, 179.0000f, (byte) 97);
				spawn(858519, 517.8957f, 720.1225f, 179.0000f, (byte) 104);
				spawn(858519, 523.5969f, 715.8825f, 179.0000f, (byte) 109);
				spawn(858519, 531.3421f, 711.2688f, 179.0000f, (byte) 109);
				spawn(858519, 538.5221f, 711.5029f, 179.0000f, (byte) 6);
				spawn(858519, 544.6458f, 717.2367f, 179.0846f, (byte) 12);
				spawn(858519, 548.7955f, 725.4128f, 179.0752f, (byte) 21);
				spawn(858519, 551.6482f, 734.2817f, 179.0000f, (byte) 24);
				spawn(858519, 551.6303f, 728.7469f, 179.0000f, (byte) 108);
				spawn(858519, 554.1334f, 741.9596f, 179.1250f, (byte) 0);
				spawn(858519, 553.3626f, 749.7179f, 178.8750f, (byte) 0);
				spawn(858519, 549.3821f, 754.3050f, 178.8750f, (byte) 12);
				spawn(858519, 541.1930f, 754.7842f, 178.8750f, (byte) 22);
				spawn(858519, 533.3072f, 755.7924f, 178.8750f, (byte) 34);
				spawn(858519, 525.8564f, 754.0429f, 178.8750f, (byte) 43);
				spawn(858519, 519.3833f, 749.0949f, 178.8750f, (byte) 74);
				spawn(858519, 518.6880f, 741.4671f, 179.0000f, (byte) 95);
				spawn(858519, 518.8213f, 731.9505f, 179.0000f, (byte) 93);
				spawn(858519, 520.9765f, 725.5307f, 179.0000f, (byte) 104);
				spawn(858519, 527.6368f, 719.8127f, 179.0000f, (byte) 104);
				spawn(858519, 534.8742f, 717.9278f, 179.0000f, (byte) 113);
				spawn(858519, 541.2986f, 724.0534f, 179.0000f, (byte) 8);
				spawn(858519, 544.5339f, 731.3642f, 179.0000f, (byte) 29);
				spawn(858519, 547.1470f, 739.7856f, 179.0000f, (byte) 28);
				spawn(858519, 547.0409f, 746.5766f, 178.9639f, (byte) 32);
				spawn(858519, 539.0384f, 748.9615f, 178.9399f, (byte) 54);
				spawn(858519, 531.9120f, 749.2207f, 179.0000f, (byte) 82);
				spawn(858519, 525.2456f, 745.4571f, 179.0000f, (byte) 96);
				spawn(858519, 524.0336f, 736.8716f, 179.0000f, (byte) 96);
				spawn(858519, 527.1153f, 728.2660f, 179.0000f, (byte) 96);
				spawn(858519, 534.3458f, 726.1777f, 179.0000f, (byte) 112);
				spawn(858519, 538.5400f, 734.0162f, 179.0000f, (byte) 23);
				spawn(858519, 540.7889f, 740.7491f, 179.0000f, (byte) 26);
				spawn(858519, 533.0931f, 740.7544f, 179.0000f, (byte) 54);
				spawn(858519, 530.9557f, 734.0543f, 179.0000f, (byte) 75);
				spawn(858519, 527.9215f, 740.6442f, 179.0000f, (byte) 58);
				spawn(858519, 514.0594f, 722.3531f, 179.0000f, (byte) 94);
				spawn(858519, 556.9765f, 718.7107f, 179.0000f, (byte) 115);
				spawn(858519, 544.9992f, 757.6093f, 178.8750f, (byte) 52);
				spawn(858519, 544.4784f, 750.9933f, 178.8750f, (byte) 50);
				spawn(858519, 530.5758f, 723.5675f, 179.0270f, (byte) 95);
			}
		}
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 10);
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
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(858515));
		deleteNpcs(instance.getNpcs(858519));
		deleteNpcs(instance.getNpcs(858536));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		//Agh, no! Do Daevas know how expensive Aethertech is?!
		sendMsg(1502541, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(858519));
		deleteNpcs(instance.getNpcs(858536));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}