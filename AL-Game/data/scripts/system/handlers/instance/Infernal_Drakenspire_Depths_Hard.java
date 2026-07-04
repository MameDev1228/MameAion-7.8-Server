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
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302700000)
public class Infernal_Drakenspire_Depths_Hard extends GeneralInstanceHandler
{
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 840267: //Berserk Protector Cubic Crystal.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188073375, 1));
					}
				}
			break;
			case 840690: //Berserk Immortal Orissan Cubic Crystal.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188074993, 1));
					}
				}
			break;
        }
    }
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateQuest63605(player);
		} else {
			ClassChangeService.onUpdateQuest73605(player);
		}
    }
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		spawn(700998, 558.0000f, 152.0000f, 1683.0000f, (byte) 0, 408);
		spawn(702696, 558.0000f, 212.0000f, 1683.0000f, (byte) 0, 409);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 858672: //Berserk Lava Protector.
			case 858673: //Berserk Heatvent Protector.
			    berserkProtector();
			break;
			case 858889: //Berserk Immortal Orissan.
			    despawnNpc(npc);
				killNpc(getNpcs(858891));
		        killNpc(getNpcs(858897));
		        despawnNpcs(instance.getNpcs(858896));
				spawn(840654, 809.1144f, 588.3160f, 1701.0449f, (byte) 91); //Treasure Chest.
				spawn(731544, 809.0100f, 592.9638f, 1701.0449f, (byte) 90); //[Infernal] Drakenspire Depths [Hard] Exit.
				spawn(840690, 801.7533f, 586.3165f, 1701.0447f, (byte) 99); //Berserk Immortal Orissan Cubic Crystal.
			break;
		}
	}
	
	private boolean berserkProtector() {
		Npc boss1 = getNpc(858672);
		Npc boss2 = getNpc(858673);
		if (isDead(boss1) && isDead(boss2)) {
			//The Protectors' Fount has been destroyed and they will not be resurrected.
			sendMsgByRace(1402688, Race.PC_ALL, 0);
			despawnNpcs(instance.getNpcs(656170)); //Fire Wall.
			despawnNpcs(instance.getNpcs(700998)); //Breakwall Twin's Boss.
			despawnNpcs(instance.getNpcs(702696)); //Breakwall Twin's Boss.
			spawn(731877, 590.0000f, 181.0000f, 1683.0000f, (byte) 61); //Returned Orissan's Eternity Rift.
			spawn(839625, 578.5783f, 180.5637f, 1683.7301f, (byte) 59); //Treasure Chest.
			spawn(840267, 577.9232f, 185.6348f, 1683.7301f, (byte) 70); //Berserk Protector Cubic Crystal.
			return true;
		}
		return false;
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 731877: //Returned Orissan's Eternity Rift.
			    returnedOrissan(player, 815.0000f, 543.0000f, 1701.0000f, (byte) 33);
			break;
			case 858683: //IDSeal_Hard_Portal_01.
			    boss1ToBoss2(player, 515.0000f, 211.0000f, 1681.0000f, (byte) 0);
			break;
			case 858682: //IDSeal_Hard_Portal_02.
			    boss2ToBoss1(player, 516.0000f, 151.0000f, 1681.0000f, (byte) 1);
			break;
		}
	}
	
	protected void returnedOrissan(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void boss1ToBoss2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void boss2ToBoss1(Player player, float x, float y, float z, byte h) {
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
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
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
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}
	
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
}