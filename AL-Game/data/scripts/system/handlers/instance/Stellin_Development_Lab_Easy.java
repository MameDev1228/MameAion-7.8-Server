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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/** Source: https://www.youtube.com/watch?v=OWASvo4ELR4
/****/

@InstanceID(302610000)
public class Stellin_Development_Lab_Easy extends GeneralInstanceHandler
{
	private int controlSwitchL;
	private int controlSwitchR;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 656830: //Smuggler Shukirukin.
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
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateGuide63802(player);
		} else {
			ClassChangeService.onUpdateGuide73802(player);
		}
    }
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
		//The intruder alarm has been sounded.
		sendMsgByRace(1405025, Race.PC_ALL, 0);
		//Draug is hiding in the sewers.
		sendMsgByRace(1405030, Race.PC_ALL, 8000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		//Smuggler Shukirukin.
		switch (Rnd.get(1, 2)) {
			case 1:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 199.0000f, 251.0000f, 570.0000f, (byte) 33);
			break;
			case 2:
			break;
		}
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
		    case 858274: //Weakened Draug.
			    doors.get(161).setOpen(true);
			    doors.get(162).setOpen(true);
				//The door to the drain is open. You can now enter.
				sendMsgByRace(1405024, Race.PC_ALL, 0);
				//The entrance to the Research Room is open.
				sendMsgByRace(1405088, Race.PC_ALL, 5000);
				//The security door to the NK Production Room is closed.
				sendMsgByRace(1405090, Race.PC_ALL, 10000);
				//Shadon blocked the entrance to the Product Research Room.
				sendMsgByRace(1405087, Race.PC_ALL, 15000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
			case 858275: //Weakened Shadon.
				doors.get(193).setOpen(true);
				//The security door to the NK Production Room is open.
				sendMsgByRace(1405089, Race.PC_ALL, 0);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
			case 858276: //Weakened Bronze Guardian.
			    killNpc(getNpcs(858186));
				spawn(858187, 214.0000f, 197.0000f, 396.0000f, (byte) 0, 195);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
			case 858277: //Weakened Mutated Daeva.
				spawn(838086, 210.0000f, 275.0000f, 570.0000f, (byte) 90);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
			case 858161: //Small Explosive Unit.
			case 858162: //Small Explosive Unit.
			case 858163: //Small Explosive Unit.
			case 858164: //Small Explosive Unit.
			    despawnNpc(npc);
			    instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(19720, player, player, 10000 * 1);
				    }
			    });
			break;
			case 858188: //Suspicious Sewer Slime.
			    //You defeated the slime blocking the switch circuit. Now you can operate the switch.
			    sendMsgByRace(1405019, Race.PC_ALL, 0);
			    spawn(858184, 418.0000f, 172.0000f, 340.0000f, (byte) 0, 128);
			    spawn(858184, 399.0000f, 112.0000f, 340.0000f, (byte) 0, 129);
				spawn(858185, 334.0000f, 141.0000f, 340.0000f, (byte) 0, 156);
			    spawn(858185, 386.0000f, 196.0000f, 340.0000f, (byte) 0, 157);
			break;
			case 858135: //BIDLDF8_LAB_Boss_04_Summon_L_80_An.
			case 858136: //BIDLDF8_LAB_Boss_04_Summon_R_80_An.
			case 858169: //BIDLDF8_LAB_Boss_03_Summon_01_80_An.
			case 858170: //BIDLDF8_LAB_Boss_03_Summon_02_80_An.
			case 858284: //BIDLDF8_LAB_Boss_02_Human_Fi_80_Ae.
			case 858285: //BIDLDF8_LAB_Boss_02_Human_As_80_Ae.
			case 858286: //BIDLDF8_LAB_Boss_02_Shugo_As_01_80_Ae.
			case 858287: //BIDLDF8_LAB_Boss_02_Shugo_As_02_80_Ae.
			case 858288: //BIDLDF8_LAB_Boss_02_Ratman_Fi_80_Ae.
			    despawnNpc(npc);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 858132: //Steel Control Unit.
			    despawnNpc(npc);
				doors.get(15).setOpen(true);
			    spawn(858134, 227.0000f, 214.0000f, 567.0000f, (byte) 30);
			break;
			case 858133: //Steel Control Unit.
			    despawnNpc(npc);
				doors.get(10).setOpen(true);
			    spawn(858134, 195.0000f, 215.0000f, 566.0000f, (byte) 30);
			break;
			//Teleportation Portal [Left].
			case 858137:
			    teleportationPortalL(player, 195.0000f, 202.0000f, 581.0000f, (byte) 104);
			break;
			//Teleportation Portal [Right].
			case 858138:
			    teleportationPortalR(player, 227.0000f, 202.0000f, 581.0000f, (byte) 57);
			break;
			//Barrier Control Switch [Left].
			case 858242:
			case 858243:
			case 858244:
			    controlSwitchL++;
				if (controlSwitchL == 3) {
					despawnNpc(npc);
					despawnNpcs(instance.getNpcs(858148)); //Electromagnetic Field.
					spawn(858139, 211.3902f, 205.0632f, 578.1517f, (byte) 0); //Protective Dome [Center].
				}
			break;
			//Barrier Control Switch [Right].
			case 858245:
			case 858246:
			case 858247:
			    controlSwitchR++;
				if (controlSwitchR == 3) {
					despawnNpc(npc);
					despawnNpcs(instance.getNpcs(858148)); //Electromagnetic Field.
					spawn(858139, 211.3902f, 205.0632f, 578.1517f, (byte) 0); //Protective Dome [Center].
				}
			break;
			case 858184: //Suspicious Slime.
			    despawnNpc(npc);
				//The switch has been activated. The alarm turns off and a powerful light radiates.
				sendMsgByRace(1405026, Race.PC_ALL, 0);
				//A powerful light has been activated.
				sendMsgByRace(1405028, Race.PC_ALL, 4000);
				spawn(858181, 402.4207f, 122.2503f, 339.1386f, (byte) 15);
				spawn(858181, 375.6573f, 122.6382f, 339.1386f, (byte) 85);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(858181));
						//The powerful light becomes more faint.
						sendMsgByRace(1405023, Race.PC_ALL, 0);
					}
				}, 120000);
			break;
			case 858185: //Suspicious Slime.
			    despawnNpc(npc);
			    //The switch has been activated. The alarm turns off and a powerful light radiates.
				sendMsgByRace(1405026, Race.PC_ALL, 0);
				//A powerful light has been activated.
				sendMsgByRace(1405028, Race.PC_ALL, 4000);
			    spawn(858181, 341.5995f, 134.3489f, 339.1386f, (byte) 51);
                spawn(858181, 349.1745f, 180.3541f, 339.1386f, (byte) 29);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(858181));
						//The powerful light becomes more faint.
						sendMsgByRace(1405023, Race.PC_ALL, 0);
					}
				}, 120000);
			break;
			
			/*
			case 858242: //BIDLDF8_LAB_Boss_04_Safe_Switch_01.
			    despawnNpc(npc);
			    spawn(858257, 221.0000f, 199.0000f, 581.0000f, (byte) 0, 208);
			break;
			case 858243: //BIDLDF8_LAB_Boss_04_Safe_Switch_02.
			    despawnNpc(npc);
			    spawn(858258, 233.0000f, 200.0000f, 581.0000f, (byte) 0, 210);
			break;
			case 858244: //BIDLDF8_LAB_Boss_04_Safe_Switch_03.
			    despawnNpc(npc);
			    spawn(858259, 227.0000f, 209.0000f, 581.0000f, (byte) 0, 237);
			break;
			case 858245: //BIDLDF8_LAB_Boss_04_Safe_Switch_04.
			    despawnNpc(npc);
			    spawn(858260, 195.0000f, 209.0000f, 581.0000f, (byte) 0, 242);
			break;
			case 858246: //BIDLDF8_LAB_Boss_04_Safe_Switch_05.
			    despawnNpc(npc);
			    spawn(858261, 202.0000f, 200.0000f, 581.0000f, (byte) 0, 243);
			break;
			case 858247: //BIDLDF8_LAB_Boss_04_Safe_Switch_06.
			    despawnNpc(npc);
			    spawn(858262, 189.0000f, 199.0000f, 581.0000f, (byte) 0, 244);
			break;*/
			
			case 858187: //BIDLDF8_LAB_C1_Switch4_80_Ae.
			    stellarBossTeleport(player, 210.0000f, 272.0000f, 570.0000f, (byte) 90);
			break;
			case 858316: //BIDLDF8_LAB_B2_Switch1_80_Ae.
			    stellarTeleport1(player, 78.0000f, 282.0000f, 320.0000f, (byte) 78);
			break;
			case 858317: //BIDLDF8_LAB_B2_Switch2_80_Ae.
			    stellarTeleport2(player, 63.0000f, 246.0000f, 320.0000f, (byte) 34);
			break;
			case 858318: //BIDLDF8_LAB_B2_Switch3_80_Ae.
			    stellarTeleport3(player, 49.0000f, 280.0000f, 320.0000f, (byte) 106);
			break;
		}
	}
	
	protected void stellarBossTeleport(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void stellarTeleport1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void stellarTeleport2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void stellarTeleport3(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void teleportationPortalL(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void teleportationPortalR(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
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
	
	@Override
    public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
    }
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
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
}