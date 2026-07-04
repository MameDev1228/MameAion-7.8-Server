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

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

import javolution.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300910000)
public class IDCatacombs_Rudra extends GeneralInstanceHandler
{
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 840736: //IDCatacombs_Rudra_Treasure_Box.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188120150, 1));
						switch (Rnd.get(1, 4)) {
							case 1:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188075253, 1));
							break;
							case 2:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188075254, 1));
							break;
							case 3:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188075255, 1));
							break;
							case 4:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188075256, 1));
							break;
						}
					}
				}
			break;
			case 840719: //Stormwing Cubic Crystal.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188075127, 1));
					}
				}
			break;
        }
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				SkillEngine.getInstance().applyEffectDirectly(20832, player, player, 3600000 * 1);
			}
		});
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
        doors = instance.getDoors();
		doors.get(466).setOpen(true);
		//폭풍의 루드라의 권능으로 광란 저주를 받았습니다
		sendMsgByRace(1406231, Race.PC_ALL, 0);
		//폭풍의 루드라의 권능으로 쇠약 저주를 받았습니다
		sendMsgByRace(1406232, Race.PC_ALL, 5000);
		//폭풍의 루드라의 권능으로 뇌운 저주를 받았습니다
		sendMsgByRace(1406233, Race.PC_ALL, 10000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		//Stella Box.
		switch (Rnd.get(1, 3)) {
			case 1:
				spawn(663060, 949.4778f, 473.39780f, 234.0888f, (byte) 0, 2);
			break;
			case 2:
				spawn(663065, 941.2916f, 1068.2319f, 234.1532f, (byte) 0, 30);
			break;
			case 3:
				spawn(663070, 1227.096f, 1005.5674f, 240.2011f, (byte) 0, 32);
			break;
		}
    }
	
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 858938: //BIDCatacombs_Rudra_80_Ah.
				despawnNpc(npc);
				killNpc(getNpcs(858935));
				killNpc(getNpcs(858936));
				spawn(730287, 860.7437f, 1343.9053f, 223.7233f, (byte) 76); //Rift Orb.
				spawn(840719, 831.9377f, 1325.2513f, 223.7208f, (byte) 80); //IDCubic_Catacombs_Rudra_Cubic_Reward_01.
				spawn(840736, 840.7994f, 1321.7267f, 223.6657f, (byte) 78); //IDCatacombs_Rudra_Treasure_Box.
			break;
			case 858939: //BIDCatacombs_DrakanMonkBossNmd_80_Ah.
				//The Seal Protector has fallen. The Rift Orb shines while the seal weakens.
				sendMsgByRace(1400480, Race.PC_ALL, 3000);
				spawn(858938, 850.39905f, 1332.6586f, 223.7155f, (byte) 77); //BIDCatacombs_Rudra_80_Ah.
				spawn(730276, 1423.0000f, 1463.0000f, 311.0000f, (byte) 0, 426); //Prison Of Ice Gate.
			break;
			case 858940: //BIDCatacombs_DrakanFiNmd_80_Ah.
			    doors.get(470).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
            break;
		}
    }
	
	@Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
			case 730276: //Prison Of Ice Entrance.
				prisonOfIce(player, 822.0000f, 1302.0000f, 223.0000f, (byte) 16);
            break;
			case 663060: //Stella Box A.
			case 663065: //Stella Box B.
			case 663070: //Stella Box C.
			    stellaBuff();
				//지원물품 상자에서 강렬한 힘이 분출되었습니다
				sendMsgByRace(1406244, Race.PC_ALL, 0);
				stellaBox(player, 1215.0000f, 1187.0000f, 307.0000f, (byte) 0);
			break;
        }
    }
	
	protected void prisonOfIce(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void stellaBox(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	private void stellaBuff() {
		for (Player p: instance.getPlayersInside()) {
			SkillTemplate st =  DataManager.SKILL_DATA.getSkillTemplate(20878);
			Effect e = new Effect(p, p, st, 1, st.getEffectsDuration(9));
			e.initialize();
			e.applyEffect();
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
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20832);
		effectController.removeEffect(20878);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
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
}