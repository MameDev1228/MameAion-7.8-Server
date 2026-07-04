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
package instance;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(301390000)
public class Infernal_Drakenspire_Depths extends GeneralInstanceHandler
{
	private Race videoRace;
	private Race sealSceneRace;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 655289: //Smuggler Shukirukin.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						switch (Rnd.get(1, 2)) {
							case 1:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188070768, 1)); //Ancient Daevanion Skill Box.
							break;
							case 2:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188071258, 1)); //Legendary Daevanion Skill Box.
							break;
						}
					}
				}
			break;
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		spawn(700998, 558.0000f, 152.0000f, 1683.0000f, (byte) 0, 408);
		spawn(702696, 558.0000f, 212.0000f, 1683.0000f, (byte) 0, 409);
		//Smuggler Shukirukin.
		switch (Rnd.get(1, 9)) {
			case 1:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 168.0000f, 457.0000f, 1759.0000f, (byte) 33);
			break;
			case 2:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 119.0000f, 571.0000f, 1754.0000f, (byte) 101);
			break;
			case 3:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 159.0000f, 514.0000f, 1749.0000f, (byte) 24);
			break;
			case 4:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 121.0000f, 458.0000f, 1754.0000f, (byte) 34);
			break;
			case 5:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 139.0000f, 499.0000f, 1749.0000f, (byte) 19);
			break;
			case 6:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 181.0000f, 575.0000f, 1760.0000f, (byte) 80);
			break;
			case 7:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 202.0000f, 493.0000f, 1754.0000f, (byte) 47);
			break;
			case 8:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 20000);
				spawn(655289, 211.0000f, 536.0000f, 1754.0000f, (byte) 71);
			break;
			case 9:
			break;
		}
		//Beritra.
		switch (Rnd.get(1, 3)) {
			case 1:
				spawn(654686, 151.0000f, 518.0000f, 1749.0000f, (byte) 9);
			break;
			case 2:
				spawn(654687, 151.0000f, 518.0000f, 1749.0000f, (byte) 9);
			break;
			case 3:
				spawn(654688, 151.0000f, 518.0000f, 1749.0000f, (byte) 9);
			break;
		}
	}
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (sealSceneRace == null) {
            sealSceneRace = player.getRace();
            ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					spawnDrakenspire();
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							player.getController().updateZone();
							player.getController().updateNearbyQuests();
						}
					});
				}
			}, 10000);
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 731875: //Heatvent Protector's Eternity Rift.
			    heatventProtector(player, 515.0000f, 152.0000f, 1681.0000f, (byte) 1);
			break;
			case 731876: //Lava Protector's Eternity Rift.
			    lavaProtector(player, 514.0000f, 212.0000f, 1681.0000f, (byte) 0);
			break;
			case 731877: //Returned Orissan's Eternity Rift.
			    returnedOrissan(player, 815.0000f, 543.0000f, 1701.0000f, (byte) 33);
			break;
			case 731878: //Dragon Lord's Eternity Rift.
			    dragonLord(player, 633.0000f, 948.0000f, 1613.0000f, (byte) 29);
			break;
			case 804697: //Eternal Altar Of Torment.
			    altarOfTorment(player, 174.4397f, 527.9976f, 1749.7006f, (byte) 67);
			break;
		}
	}
	
	protected void heatventProtector(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void lavaProtector(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void returnedOrissan(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void dragonLord(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void altarOfTorment(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	private void spawnDrakenspire() {
		//Advance into Drakenspire Depths with your allies.
		sendMsgByRace(1402991, Race.PC_ALL, 10000);
		//Choose a path to proceed.
		sendMsgByRace(1402992, Race.PC_ALL, 15000);
		final int pcGuard1 = sealSceneRace == Race.ASMODIANS ? 209744 : 209679;
		final int pcGuard2 = sealSceneRace == Race.ASMODIANS ? 209744 : 209679;
        final int pcGuard3 = sealSceneRace == Race.ASMODIANS ? 209745 : 209680;
        final int pcGuard4 = sealSceneRace == Race.ASMODIANS ? 209745 : 209680;
		final int Parsia_Masionel = sealSceneRace == Race.ASMODIANS ? 209763 : 209698;
		spawn(pcGuard1, 353.0000f, 188.0000f, 1684.0000f, (byte) 78);
		spawn(pcGuard2, 353.0000f, 176.0000f, 1684.0000f, (byte) 47);
        spawn(pcGuard3, 363.0000f, 186.0000f, 1684.0000f, (byte) 0);
        spawn(pcGuard4, 363.0000f, 178.0000f, 1684.0000f, (byte) 0);
		Npc NpcRace = (Npc) spawn(Parsia_Masionel, 359.0000f, 186.0000f, 1684.0000f, (byte) 60);
		//We'll take care of the Lava Protector. We plan on using magic.
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502022, NpcRace.getObjectId(), 0, 5000);
		//We'll take care of the Lava Protector. We plan on using melee attacks.
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502023, NpcRace.getObjectId(), 0, 10000);
		//Beritra... You'll need at least five more people.
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502024, NpcRace.getObjectId(), 0, 15000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawn(731875, 361.0000f, 175.0000f, 1684.0000f, (byte) 60); //Heatvent Protector's Eternity Rift.
				spawn(731876, 361.0000f, 182.0000f, 1684.0000f, (byte) 60); //Lava Protector's Eternity Rift.
				spawn(731877, 361.0000f, 190.0000f, 1684.0000f, (byte) 60); //Returned Orissan's Eternity Rift.
			}
		}, 16000);
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		if (player == null) {
            return;
        }
		Race race = player.getRace();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 654667: //Fountless Lava Protector.
			    //The Protector that shares the Fount is still alive.
				sendMsgByRace(1402690, Race.PC_ALL, 0);
				//The vanquished Protector will be resurrected by the power of the Fount in 15 seconds.
				sendMsgByRace(1402691, Race.PC_ALL, 2000);
				//The Protector that shares the Fount has successfully resurrected.
				sendMsgByRace(1402692, Race.PC_ALL, 4000);
			break;
			case 654668: //Fountless Heatvent Protector.
			    //The Protector that shares the Fount is still alive.
				sendMsgByRace(1402690, Race.PC_ALL, 0);
				//The vanquished Protector will be resurrected by the power of the Fount in 15 seconds.
				sendMsgByRace(1402691, Race.PC_ALL, 2000);
				//The Protector that shares the Fount has successfully resurrected.
				sendMsgByRace(1402692, Race.PC_ALL, 4000);
			break;
			case 654669: //Lava Protector.
				killNpc(getNpcs(654670)); //Heatvent Protector.
				despawnNpcs(instance.getNpcs(702696)); //Breakwall Twin's Boss.
				//The Protectors' Fount has been destroyed and they will not be resurrected.
				sendMsgByRace(1402688, Race.PC_ALL, 2000);
				spawn(731548, 535.0000f, 219.0000f, 1681.0000f, (byte) 18); //[Infernal] Drakenspire Depths Exit.
				spawn(804697, 635.0000f, 959.0000f, 1615.0000f, (byte) 0, 50); //Eternal Altar Of Torment Entrance.
				if (race.equals(Race.ELYOS)) {
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209693, 553.12550f, 208.44653f, 1683.7301f, (byte) 1);
							spawn(209690, 552.90247f, 215.51768f, 1683.7301f, (byte) 0);
						}
					}, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							//The Empyrean Elite started to advance.
							sendMsgByRace(1402994, Race.PC_ALL, 0);
							spawn(209694, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209695, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
							Npc PCGuard_Li_Talk_A = getNpc(209695);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402727, PCGuard_Li_Talk_A.getObjectId(), 0, 2000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402728, PCGuard_Li_Talk_A.getObjectId(), 0, 6000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402729, PCGuard_Li_Talk_A.getObjectId(), 0, 10000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402730, PCGuard_Li_Talk_A.getObjectId(), 0, 14000);
						}
					}, 30000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(209694));
							despawnNpcs(instance.getNpcs(209695));
							spawn(209698, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209700, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
						}
					}, 45000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Masionel = getNpc(209698);
							//Thanks to you, the Detachment got through without any losses. Excellent work!
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501315, Masionel.getObjectId(), 0, 0);
							//This place is protected by a dark power. It cannot be destroyed.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501313, Masionel.getObjectId(), 0, 6000);
							//Just let me blast us a path...
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501311, Masionel.getObjectId(), 0, 12000);
						}
					}, 55000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(731878, 590.0000f, 181.0000f, 1683.0000f, (byte) 61); //Dragon Lord's Eternity Rift.
							spawn(837116, 588.0000f, 181.0000f, 1683.0000f, (byte) 0); //Regret's Berth Treasure Chest.
							Npc Masionel = getNpc(209698);
							//We can get through now.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501312, Masionel.getObjectId(), 0, 0);
							//Please take care.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501314, Masionel.getObjectId(), 0, 6000);
						}
					}, 70000);
				} else if (race.equals(Race.ASMODIANS)) {
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209755, 552.90247f, 215.51768f, 1683.7301f, (byte) 0);
							spawn(209758, 553.12550f, 208.44653f, 1683.7301f, (byte) 1);
						}
					}, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							//The Empyrean Elite started to advance.
							sendMsgByRace(1402994, Race.PC_ALL, 0);
							spawn(209759, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209760, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
							Npc PCGuard_Da_Talk_A = getNpc(209760);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402727, PCGuard_Da_Talk_A.getObjectId(), 0, 2000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402728, PCGuard_Da_Talk_A.getObjectId(), 0, 6000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402729, PCGuard_Da_Talk_A.getObjectId(), 0, 10000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402730, PCGuard_Da_Talk_A.getObjectId(), 0, 14000);
						}
					}, 30000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(209759));
							despawnNpcs(instance.getNpcs(209760));
							spawn(209763, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209765, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
						}
					}, 45000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Parsia = getNpc(209763);
							//Thanks to you, the Detachment got through without any losses. Excellent work!
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501315, Parsia.getObjectId(), 0, 0);
							//This place is protected by a dark power. It cannot be destroyed.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501313, Parsia.getObjectId(), 0, 6000);
							//Just let me blast us a path...
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501311, Parsia.getObjectId(), 0, 12000);
						}
					}, 55000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(731878, 590.0000f, 181.0000f, 1683.0000f, (byte) 61); //Dragon Lord's Eternity Rift.
							spawn(837116, 588.0000f, 181.0000f, 1683.0000f, (byte) 0); //Regret's Berth Treasure Chest.
							Npc Parsia = getNpc(209763);
							//We can get through now.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501312, Parsia.getObjectId(), 0, 0);
							//Please take care.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501314, Parsia.getObjectId(), 0, 6000);
						}
					}, 70000);
				}
			break;
			case 654670: //Heatvent Protector.
				killNpc(getNpcs(654669)); //Lava Protector.
				despawnNpcs(instance.getNpcs(700998)); //Breakwall Twin's Boss.
				//The Protectors' Fount has been destroyed and they will not be resurrected.
				sendMsgByRace(1402688, Race.PC_ALL, 2000);
				spawn(731548, 541.0000f, 163.0000f, 1681.0000f, (byte) 76); //[Infernal] Drakenspire Depths Exit.
				spawn(804697, 635.0000f, 959.0000f, 1615.0000f, (byte) 0, 50); //Eternal Altar Of Torment Entrance.
				if (race.equals(Race.ELYOS)) {
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209690, 552.93365f, 155.68227f, 1683.7301f, (byte) 0);
							spawn(209693, 552.98486f, 148.64922f, 1683.7301f, (byte) 1);
						}
					}, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							//The Empyrean Elite started to advance.
							sendMsgByRace(1402994, Race.PC_ALL, 0);
							spawn(209694, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209695, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
							Npc PCGuard_Li_Talk_A = getNpc(209695);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402727, PCGuard_Li_Talk_A.getObjectId(), 0, 2000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402728, PCGuard_Li_Talk_A.getObjectId(), 0, 6000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402729, PCGuard_Li_Talk_A.getObjectId(), 0, 10000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li_Talk_A, 1402730, PCGuard_Li_Talk_A.getObjectId(), 0, 14000);
						}
					}, 30000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(209694));
							despawnNpcs(instance.getNpcs(209695));
							spawn(209698, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209700, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
						}
					}, 45000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Masionel = getNpc(209698);
							//Thanks to you, the Detachment got through without any losses. Excellent work!
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501315, Masionel.getObjectId(), 0, 0);
							//This place is protected by a dark power. It cannot be destroyed.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501313, Masionel.getObjectId(), 0, 6000);
							//Just let me blast us a path...
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501311, Masionel.getObjectId(), 0, 12000);
						}
					}, 55000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(731878, 590.0000f, 181.0000f, 1683.0000f, (byte) 61); //Dragon Lord's Eternity Rift.
							spawn(837115, 588.0000f, 181.0000f, 1683.0000f, (byte) 0); //Regret's Berth Treasure Chest.
							Npc Masionel = getNpc(209698);
							//We can get through now.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501312, Masionel.getObjectId(), 0, 0);
							//Please take care.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501314, Masionel.getObjectId(), 0, 6000);
						}
					}, 70000);
				} else if (race.equals(Race.ASMODIANS)) {
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209755, 552.93365f, 155.68227f, 1683.7301f, (byte) 0);
							spawn(209758, 552.98486f, 148.64922f, 1683.7301f, (byte) 1);
						}
					}, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							//The Empyrean Elite started to advance.
							sendMsgByRace(1402994, Race.PC_ALL, 0);
							spawn(209759, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209760, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
							Npc PCGuard_Da_Talk_A = getNpc(209760);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402727, PCGuard_Da_Talk_A.getObjectId(), 0, 2000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402728, PCGuard_Da_Talk_A.getObjectId(), 0, 6000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402729, PCGuard_Da_Talk_A.getObjectId(), 0, 10000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da_Talk_A, 1402730, PCGuard_Da_Talk_A.getObjectId(), 0, 14000);
						}
					}, 30000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(209759));
							despawnNpcs(instance.getNpcs(209760));
							spawn(209763, 583.84170f, 177.45332f, 1683.7301f, (byte) 8);
							spawn(209765, 582.48083f, 183.74684f, 1683.7301f, (byte) 116);
						}
					}, 45000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Parsia = getNpc(209763);
							//Thanks to you, the Detachment got through without any losses. Excellent work!
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501315, Parsia.getObjectId(), 0, 0);
							//This place is protected by a dark power. It cannot be destroyed.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501313, Parsia.getObjectId(), 0, 6000);
							//Just let me blast us a path...
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501311, Parsia.getObjectId(), 0, 12000);
						}
					}, 55000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(731878, 590.0000f, 181.0000f, 1683.0000f, (byte) 61); //Dragon Lord's Eternity Rift.
							spawn(837115, 588.0000f, 181.0000f, 1683.0000f, (byte) 0); //Regret's Berth Treasure Chest.
							Npc Parsia = getNpc(209763);
							//We can get through now.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501312, Parsia.getObjectId(), 0, 0);
							//Please take care.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501314, Parsia.getObjectId(), 0, 6000);
						}
					}, 70000);
				}
			break;
			case 654671: //Orissan.
			case 654673: //Exhausted Orissan.
			case 654674: //Reverted Orissan.
			case 654676: //Exhausted Orissan.
			    spawn(731548, 798.0000f, 588.0000f, 1701.0000f, (byte) 101); //[Infernal] Drakenspire Depths Exit.
				spawn(804697, 635.0000f, 959.0000f, 1615.0000f, (byte) 0, 50); //Eternal Altar Of Torment Entrance.
				if (race.equals(Race.ELYOS)) {
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								final int drakenspire1 = videoRace == Race.ASMODIANS ? 914 : 914;
								PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, drakenspire1));
							}
						}
					});
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209706, 810.42120f, 550.1993f, 1701.044f, (byte) 31);
							spawn(209707, 818.40704f, 552.7704f, 1701.044f, (byte) 36);
						}
					}, 10000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209710, 807.78894f, 578.7186f, 1701.0446f, (byte) 34);
							spawn(209710, 815.84827f, 579.7431f, 1701.0446f, (byte) 30);
							spawn(209711, 814.16930f, 588.4347f, 1701.0449f, (byte) 34);
							spawn(209711, 806.99536f, 587.9815f, 1701.0448f, (byte) 30);
						}
					}, 15000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209712, 811.50000f, 583.0642f, 1701.0447f, (byte) 32);
							spawn(209713, 810.85767f, 588.2299f, 1701.0449f, (byte) 32);
							Npc PCGuard_Li = getNpc(209713);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li, 1402727, PCGuard_Li.getObjectId(), 0, 2000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li, 1402728, PCGuard_Li.getObjectId(), 0, 6000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li, 1402729, PCGuard_Li.getObjectId(), 0, 10000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Li, 1402730, PCGuard_Li.getObjectId(), 0, 14000);
						}
					}, 20000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Masionel = getNpc(209712);
							//Thanks to you, the Detachment got through without any losses. Excellent work!
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501315, Masionel.getObjectId(), 0, 0);
							//This place is protected by a dark power. It cannot be destroyed.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501313, Masionel.getObjectId(), 0, 6000);
							//Just let me blast us a path...
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501311, Masionel.getObjectId(), 0, 12000);
						}
					}, 35000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Masionel = getNpc(209712);
							//The detachment continues to advance.
							sendMsgByRace(1403000, Race.PC_ALL, 0);
							spawn(731878, 808.0000f, 593.0000f, 1701.0000f, (byte) 92);
							//We can get through now.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501312, Masionel.getObjectId(), 0, 0);
							//Please take care.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501314, Masionel.getObjectId(), 0, 6000);
						}
					}, 50000);
				} else if (race.equals(Race.ASMODIANS)) {
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								final int drakenspire2 = videoRace == Race.ASMODIANS ? 914 : 914;
								PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, drakenspire2));
							}
						}
					});
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209771, 810.42120f, 550.1993f, 1701.044f, (byte) 31);
							spawn(209772, 818.40704f, 552.7704f, 1701.044f, (byte) 36);
						}
					}, 10000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209775, 807.78894f, 578.7186f, 1701.0446f, (byte) 34);
							spawn(209775, 815.84827f, 579.7431f, 1701.0446f, (byte) 30);
							spawn(209776, 814.16930f, 588.4347f, 1701.0449f, (byte) 34);
							spawn(209776, 806.99536f, 587.9815f, 1701.0448f, (byte) 30);
						}
					}, 15000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(209777, 811.50000f, 583.0642f, 1701.0447f, (byte) 32);
							spawn(209778, 810.85767f, 588.2299f, 1701.0449f, (byte) 32);
							Npc PCGuard_Da = getNpc(209778);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da, 1402727, PCGuard_Da.getObjectId(), 0, 2000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da, 1402728, PCGuard_Da.getObjectId(), 0, 6000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da, 1402729, PCGuard_Da.getObjectId(), 0, 10000);
							NpcShoutsService.getInstance().sendMsg(PCGuard_Da, 1402730, PCGuard_Da.getObjectId(), 0, 14000);
						}
					}, 20000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Parsia = getNpc(209777);
							//Thanks to you, the Detachment got through without any losses. Excellent work!
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501315, Parsia.getObjectId(), 0, 0);
							//This place is protected by a dark power. It cannot be destroyed.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501313, Parsia.getObjectId(), 0, 6000);
							//Just let me blast us a path...
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501311, Parsia.getObjectId(), 0, 12000);
						}
					}, 35000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							Npc Parsia = getNpc(209777);
							//The detachment continues to advance.
							sendMsgByRace(1403000, Race.PC_ALL, 0);
							spawn(731878, 808.0000f, 593.0000f, 1701.0000f, (byte) 92);
							//We can get through now.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501312, Parsia.getObjectId(), 0, 0);
							//Please take care.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501314, Parsia.getObjectId(), 0, 6000);
						}
					}, 50000);
				}
			break;
			case 855460: //Drakenspire Protector.
			case 855461: //Drakenspire Protector.
			case 855462: //Drakenspire Protector.
			case 855463: //Drakenspire Protector.
			case 855464: //Drakenspire Protector.
			case 855465: //Drakenspire Protector.
				despawnNpc(npc);
				//SkillEngine.getInstance().applyEffectDirectly(21625, player, player, 15000 * 1); //Guard Seal.
			break;
			case 654689: //Beritra [Dragon Form]
				despawnNpc(npc);
				//The Seal of Darkness has been destroyed.
				sendMsgByRace(1403008, Race.PC_ALL, 5000);
				spawn(731548, 147.0000f, 517.0000f, 1749.0000f, (byte) 2); //Drakenspire Depths Exit.
				spawn(702769, 152.0000f, 518.0000f, 1749.0000f, (byte) 68); //Ominous Darkness.
				spawn(731578, 152.0000f, 518.0000f, 1749.0000f, (byte) 0, 211);
				spawn(833012, 152.0000f, 522.0000f, 1749.0000f, (byte) 88); //Cloak Of Balaur Lord Beritra.
				spawn(833015, 161.0000f, 518.0000f, 1749.0000f, (byte) 59); //Sacred Beast Of Balaur Lord Beritra.
				if (race.equals(Race.ELYOS)) {
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								final int drakenspire3 = videoRace == Race.ASMODIANS ? 916 : 916;
								PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, drakenspire3));
							}
						}
					});
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawnIDSealSceneEnding();
							Npc Masionel = getNpc(209739);
							//Beritra may have gotten away, but we've taken Drakenspire Depths. You've done an excellent job.
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501327, Masionel.getObjectId(), 0, 12000);
							//Beritra has fled! He may have escaped, but we gave him something to remember us by!
							NpcShoutsService.getInstance().sendMsg(Masionel, 1501328, Masionel.getObjectId(), 0, 22000);
						}
					}, 10000);
				} else if (race.equals(Race.ASMODIANS)) {
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								final int drakenspire4 = videoRace == Race.ASMODIANS ? 916 : 916;
								PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, drakenspire4));
							}
						}
					});
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawnIDSealSceneEnding();
							Npc Parsia = getNpc(209804);
							//Beritra may have gotten away, but we've taken Drakenspire Depths. You've done an excellent job.
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501327, Parsia.getObjectId(), 0, 12000);
							//Beritra has fled! He may have escaped, but we gave him something to remember us by!
							NpcShoutsService.getInstance().sendMsg(Parsia, 1501328, Parsia.getObjectId(), 0, 22000);
						}
					}, 10000);
				}
			break;
			case 855621: //Magma Glutten.
			case 855625: //Flamekite Geist.
			case 855444: //Drakenspire Reaper.
			case 855445: //Drakenspire Tomescale.
			case 855446: //Drakenspire Pustule.
			case 855452: //Drakenspire Protector.
			    despawnNpc(npc);
			break;
		}
	}
	
	private void spawnIDSealSceneEnding() {
        final int Parsia_Masionel = sealSceneRace == Race.ASMODIANS ? 209804 : 209739;
		final int IDSealSceneEndingPCGuard1 = sealSceneRace == Race.ASMODIANS ? 209807 : 209742;
        final int IDSealSceneEndingPCGuard2 = sealSceneRace == Race.ASMODIANS ? 209807 : 209742;
        final int IDSealSceneEndingPCGuard3 = sealSceneRace == Race.ASMODIANS ? 209807 : 209742;
		final int IDSealSceneEndingPCGuard4 = sealSceneRace == Race.ASMODIANS ? 209807 : 209742;
		spawn(Parsia_Masionel, 156.0000f, 518.0000f, 1749.0000f, (byte) 0);
		spawn(IDSealSceneEndingPCGuard1, 155.0000f, 514.0000f, 1749.0000f, (byte) 106);
        spawn(IDSealSceneEndingPCGuard2, 155.0000f, 521.0000f, 1749.0000f, (byte) 15);
        spawn(IDSealSceneEndingPCGuard3, 148.0000f, 521.0000f, 1749.0000f, (byte) 45);
		spawn(IDSealSceneEndingPCGuard4, 148.0000f, 515.0000f, 1749.0000f, (byte) 75);
    }
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
						}
					}
				});
			}
		}, time);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
        }
    }
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}
	
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
}