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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302670000)
public class Scaleshadow extends GeneralInstanceHandler
{
	private Race videoRace;
	private Race spawnRace;
	private boolean scaleshadowCaveEvent;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 660774: //IDF8_Mission04_A_Drakan_Pr_Keynamed_01.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001067, 1));
		    break;
			case 660775: //IDF8_Mission04_A_Drakan_Pr_Keynamed_02.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164010100, 1));
		    break;
			case 660776: //IDF8_Mission04_A_Drakan_Pr_Keynamed_03.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001068, 1));
		    break;
			case 660777: //IDF8_Mission04_A_Drakan_Pr_Keynamed_04.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001069, 1));
		    break;
			case 660778: //IDF8_Mission04_A_Drakan_Pr_Keynamed_05.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001070, 1));
		    break;
        }
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (spawnRace == null) {
			spawnRace = player.getRace();
			Scaleshadow();
		}
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				inannaForm(player);
				SkillEngine.getInstance().applyEffectDirectly(5686, player, player, 7200000 * 1);
			}
		});
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
    }
	
	private void Scaleshadow() {
		final int inannaDoll = spawnRace == Race.ASMODIANS ? 820655 : 820640;
		spawn(inannaDoll, 579.0000f, 661.0000f, 354.0000f, (byte) 74, 158);
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 660777:
			    killNpc(getNpcs(703831));
				PacketSendUtility.sendSys3Message(player, "\uE005", "Please use the passage in the wall on your right to exit");
			break;
			case 858471: //Warden Emfel.
			    doors.get(120).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 0);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 820521: //Benirunerk's In Prison.
				if (player.getRace() == Race.ELYOS) {
					ClassChangeService.onUpdateMission60606(player);
				} else {
					ClassChangeService.onUpdateMission70606(player);
				}
				despawnNpc(npc);
				spawn(820522, 584.0000f, 597.0000f, 355.0000f, (byte) 61);
				spawn(282786, 584.0000f, 597.0000f, 355.0000f, (byte) 61);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
		}
	}
	
	@Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("IDF8_MISSION_04_Q60606_D_302670000")) {
            if (!scaleshadowCaveEvent) {
				scaleshadowCaveEvent = true;
				spawn(820649, 363.0000f, 592.0000f, 337.0000f, (byte) 24);  //Scaleshadow Escape.
				spawn(703830, 358.0000f, 595.0000f, 336.0000f, (byte) 0, 163); //Sturdy Boulder.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int scaleshadow = videoRace == Race.ASMODIANS ? 1055 : 1055;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, scaleshadow));
						}
					}
				});
			}
		}
    }
	
	public static final void inannaForm(final Player player) {
		player.getSkillList().addSkill(player, 5680, 1);
		player.getSkillList().addSkill(player, 5681, 1);
		player.getSkillList().addSkill(player, 5682, 1);
		player.getSkillList().addSkill(player, 5683, 1);
		player.getSkillList().addSkill(player, 5684, 1);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(5686);
		////////////////////////////////////////////
		SkillLearnService.removeSkill(player, 5680);
		SkillLearnService.removeSkill(player, 5681);
		SkillLearnService.removeSkill(player, 5682);
		SkillLearnService.removeSkill(player, 5683);
		SkillLearnService.removeSkill(player, 5684);
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
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}
	
	@Override
    public void onInstanceDestroy() {
		isInstanceDestroyed = true;
    }
}