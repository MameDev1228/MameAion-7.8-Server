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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
//Source 1: https://www.youtube.com/watch?v=e_GuwI9a5E8
//Source 2: https://event2.ncsoft.jp/1.0/aion/1902fortress/
/****/

@InstanceID(302520000)
public class Steel_Rake_Fortress extends GeneralInstanceHandler
{
	private int capturedShugo;
	private Race steelRakeRace;
	protected boolean isInstanceDestroyed = false;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 656704: //Combat Captain Muata.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001012, 1)); //Muata’s Prison Key.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 166027000, 1));
			break;
			case 656705: //Doctor Archeto.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001013, 1)); //Archeto’s Prison Key.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 166027000, 1));
			break;
			case 656708: //Madame Chichirico.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001011, 1)); //Chichirico’s Prison Key.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 166027000, 1));
			break;
			case 657362: //Prodigal Hutkin.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001014, 5)); //Steel Rake Fortress Chest Key.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 166027000, 5));
			break;
			case 837638: //Fortress Transportation Iron Hook Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164010022, 1)); //Steel Rake Fortress Grappling Hook.
			break;
			case 837640: //Steel Rake Treasure Chest.
				switch (Rnd.get(1, 3)) {
					case 1:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186020023, 5));
					break;
					case 2:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186020023, 10));
					break;
					case 3:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186020023, 15));
					break;
				}
			break;
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		spawnSteelRakeRace();
		//Install Bomb.
		sendPacket(player, "UI_Gauge_01", 0 + 1);
		//Rescue Prisoner.
		sendPacket(player, "UI_Gauge_02", 0 + 1);
		//Defeat Boss.
		sendPacket(player, "UI_Gauge_03", 0 + 1);
		Npc waderunerk1 = (Npc) spawn(806974, 937.0000f, 880.0000f, 120.0000f, (byte) 30);
		//Daeva just say when is ready!
		NpcShoutsService.getInstance().sendMsg(waderunerk1, 1502258, waderunerk1.getObjectId(), 0, 4000);
	}
	
	private void spawnSteelRakeRace() {
		final int jetski = steelRakeRace == Race.ASMODIANS ? 837594 : 837593;
		spawn(jetski, 943.0000f, 867.0000f, 119.0000f, (byte) 0);
    }
	
	private void sendPacket(Player player, final String variable, final int value) {
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_CONDITION_VARIABLE(player, variable, value));
				}
			}
		});
	}
	
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 657362: //Prodigal Hutkin.
			    actionShugo();
			    sendPacket(player, "UI_Gauge_03", 1 + 1);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(837627, 572.0000f, 540.0000f, 126.0000f, (byte) 67); //Waderunerk.
				    }
			    }, 15000);
			break;
			case 657349:
			case 657352:
			case 837579:
			    despawnNpc(npc);
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		//boolean chichiricoKey = player.getInventory().getItemCountByItemId(185001011) > 0; //Chichirico’s Prison Key.
		//boolean muataKey = player.getInventory().getItemCountByItemId(185001012) > 0; //Muata’s Prison Key.
		//boolean archetoKey = player.getInventory().getItemCountByItemId(185001013) > 0; //Archeto’s Prison Key.
		switch (npc.getNpcId()) {
			case 837593: //Jetski.
			case 837594: //Jetski.
			    if (player.isTransformed()) {
					//You cannot use this skill while transformed.
				    sendMsgByRace(1300149, Race.PC_ALL, 0);
				    //Transformation Mode.
					sendMsgByRace(1401212, Race.PC_ALL, 3000);
				} else {
					despawnNpc(npc);
					jetskiSkill(player);
					jetskiTp(player, 937.0000f, 860.0000f, 119.0000f, (byte) 81);
					SkillEngine.getInstance().applyEffectDirectly(20321, player, player, 300000 * 1);
				}
			break;
			case 837587: //Oil Pipeline Controller A.
				if (player.getInventory().decreaseByItemId(164010024, 1)) { //Oil Pipeline Destruction Bomb.
					despawnNpc(npc);
					sendPacket(player, "UI_Gauge_01", 1 + 1);
					//The bomb was installed on the oil pipeline in the Lower Fortress Defense District.
					sendMsgByRace(1404928, Race.PC_ALL, 2000);
					spawn(837590, 600.01013f, 443.83066f, 122.57171f, (byte) 0, 13);
					spawn(837590, 600.01599f, 443.82153f, 122.57250f, (byte) 0, 14);
				} else {
					//You don't have a bomb to install.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404927));
				}
			break;
			case 837588: //Oil Pipeline Controller B.
				if (player.getInventory().decreaseByItemId(164010024, 1)) { //Oil Pipeline Destruction Bomb.
					despawnNpc(npc);
					sendPacket(player, "UI_Gauge_01", 2 + 1);
					//The bomb was installed on the oil pipeline in the Lower Fortress Labor District.
					sendMsgByRace(1404929, Race.PC_ALL, 2000);
					spawn(837591, 471.31079f, 459.58273f, 122.45538f, (byte) 0, 9);
					spawn(837591, 471.31876f, 459.59009f, 122.45458f, (byte) 0, 12);
				} else {
					//You don't have a bomb to install.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404927));
				}
			break;
			case 837589: //Oil Pipeline Controller C.
				if (player.getInventory().decreaseByItemId(164010024, 1)) { //Oil Pipeline Destruction Bomb.
					despawnNpc(npc);
					sendPacket(player, "UI_Gauge_01", 3 + 1);
					//The bomb was installed on the oil pipeline in the Lower Fortress Development District.
					sendMsgByRace(1404930, Race.PC_ALL, 2000);
					spawn(837592, 446.84387f, 553.44281f, 122.55396f, (byte) 0, 15);
					spawn(837592, 446.83340f, 553.44568f, 122.55476f, (byte) 0, 16);
				} else {
					//You don't have a bomb to install.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404927));
				}
			break;
			case 657338: //Captured Shugo.
				despawnNpc(npc);
				capturedShugo++;
				sendPacket(player, "UI_Gauge_02", 1 + capturedShugo);
				if (capturedShugo == 3) {
					ItemService.addItem(player, 164010024, 1);
				} else if (capturedShugo == 6) {
					ItemService.addItem(player, 164010024, 1);
				}
			break;
			case 837595: //Teleporter.
			    removeItems(player);
			    removeEffects(player);
				teleporter(player, 572.0000f, 540.0000f, 126.0000f, (byte) 68);
			break;
		}
	}
	
	private void actionShugo() {
		spawnEndEvent(837660, "302520001", 2000);
		spawnEndEvent(837660, "302520002", 4000);
		spawnEndEvent(837660, "302520003", 6000);
		spawnEndEvent(837660, "302520004", 8000);
		spawnEndEvent(837660, "302520005", 10000);
		spawnEndEvent(837660, "302520006", 12000);
		spawnEndEvent(837660, "302520007", 14000);
		spawnEndEvent(837660, "302520008", 16000);
		spawnEndEvent(837660, "302520009", 18000);
		spawnEndEvent(837660, "302520010", 20000);
	}
	
	private void spawnEndEvent(int npcId, String walkern, int time) {
        sp(npcId, 572.0000f, 540.0000f, 126.0000f, (byte) 67, time, walkern);
    }
	
	protected void jetskiTp(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void teleporter(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	public static final void jetskiSkill(final Player player) {
		player.getSkillList().addSkill(player, 20323, 1);
		player.getSkillList().addSkill(player, 20324, 1);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20321);
		effectController.removeEffect(20322);
		SkillLearnService.removeSkill(player, 19500);
		SkillLearnService.removeSkill(player, 19501);
		SkillLearnService.removeSkill(player, 19524);
		SkillLearnService.removeSkill(player, 20323);
		SkillLearnService.removeSkill(player, 20324);
		SkillLearnService.removeSkill(player, 21803);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
		storage.decreaseByItemId(185001011, storage.getItemCountByItemId(185001011));
		storage.decreaseByItemId(185001012, storage.getItemCountByItemId(185001012));
		storage.decreaseByItemId(185001013, storage.getItemCountByItemId(185001013));
		storage.decreaseByItemId(164010022, storage.getItemCountByItemId(164010022));
		storage.decreaseByItemId(164010023, storage.getItemCountByItemId(164010023));
		storage.decreaseByItemId(164010024, storage.getItemCountByItemId(164010024));
    }
	
	@Override
    public void onInstanceDestroy() {
		isInstanceDestroyed = true;
    }
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	protected void despawnNpcs(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().onDelete();
        }
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
	
	private void stopWalk(Npc npc) {
        npc.getSpawn().setWalkerId(null);
        WalkManager.stopWalking((NpcAI2) npc.getAi2());
    }
	
	private void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkern) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkern);
                    startEndWalker(npc);
                    unSetEndWalker(npc);
                }
            }
        }, time);
    }
	
	private void startEndWalker(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                    npc.setState(1);
                    PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
                }
            }
        }, 3000);
    }
	
    private void unSetEndWalker(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    stopWalk(npc);
                }
            }
        }, 8000);
    }
}