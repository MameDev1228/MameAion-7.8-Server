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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300230000)
public class Kromedes_Trial extends GeneralInstanceHandler
{
	private Race skillRace;
	private Race videoRace;
	private boolean grandCavernEvent;
	private boolean manorEntranceEvent;
	private boolean kaligaDungeonsEvent;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 653336: //Petrahulk Gatekeeper.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000098, 1)); //Temple Vault Door Key.
			break;
			case 653337: //Divine Hisen.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000109, 1)); //Relic Key.
			break;
			case 653348: //Warden Baal.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000099, 1)); //Dungeon Grate Key.
			break;
			case 653349: //Manor Guard Captain.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000100, 1)); //Dungeon Door Key.
			break;
			case 653364: //Jesse.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000101, 1)); //Secret Safe Key.
			break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				final int kromede = skillRace == Race.ASMODIANS ? 19270 : 19220;
				SkillEngine.getInstance().applyEffectDirectly(kromede, player, player, 3600000 * 1);
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
        switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(656254, 670.0000f, 774.0000f, 216.0000f, (byte) 59); //Shadow Judge Kaliga.
			break;
			case 2:
				spawn(653367, 670.0000f, 774.0000f, 216.0000f, (byte) 59); //Kaliga The Unjust.
			break;
        }
    }
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 700835: //Sealed Stone Door.
			    despawnNpc(npc);
			break;
			case 653350: //Hamam The Torturer.
				spawn(656253, 651.0000f, 767.0000f, 215.0000f, (byte) 59); //Wounded Hamam.
            break;
			case 653364: //Jesse.
				announceKaligaTreasury();
            break;
			case 653365: //Lady Angerr.
				spawn(656251, 650.0000f, 774.0000f, 215.0000f, (byte) 60); //Distraught Lady Angerr.
            break;
			case 653366: //Justicetaker Wyr.
				spawn(656252, 651.0000f, 780.0000f, 215.0000f, (byte) 59); //Injured Justicetaker Wyr.
            break;
			case 656254: //Shadow Judge Kaliga.
			case 653367: //Kaliga The Unjust.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int kromede4 = videoRace == Race.ASMODIANS ? 455 : 455;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, kromede4));
						}
					}
				});
            break;
			case 282093: //Mana Relic.
				Npc shadowJudgeKaliga1 = instance.getNpc(656254); //Shadow Judge Kaliga.
				Npc kaligaTheUnjust1 = instance.getNpc(653367); //Kaliga The Unjust.
				if (shadowJudgeKaliga1 != null) {
					shadowJudgeKaliga1.getEffectController().removeEffect(19248); //Mana Relic Effect.
				} if (kaligaTheUnjust1 != null) {
					kaligaTheUnjust1.getEffectController().removeEffect(19248); //Mana Relic Effect.
				}
            break;
			case 282095: //Strength Relic.
				Npc shadowJudgeKaliga2 = instance.getNpc(656254); //Shadow Judge Kaliga.
				Npc kaligaTheUnjust2 = instance.getNpc(653367); //Kaliga The Unjust.
				if (shadowJudgeKaliga2 != null) {
					shadowJudgeKaliga2.getEffectController().removeEffect(19247); //Strength Relic Effect.
				} if (kaligaTheUnjust2 != null) {
					kaligaTheUnjust2.getEffectController().removeEffect(19247); //Strength Relic Effect.
				}
            break;
        }
    }
	
	private void announceKaligaTreasury() {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The door to the Kaliga Treasury should be around here somewhere....
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1111370, player.getObjectId(), 2));
				}
			}
		});
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(19220);
		effectController.removeEffect(19270);
		effectController.removeEffect(19288); //Rage Of Kromede.
	}
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000101, storage.getItemCountByItemId(185000101)); //Secret Safe Key.
        storage.decreaseByItemId(185000109, storage.getItemCountByItemId(185000109)); //Relic Key.
		storage.decreaseByItemId(164000140, storage.getItemCountByItemId(164000140)); //Explosive Bead.
        storage.decreaseByItemId(164000142, storage.getItemCountByItemId(164000142)); //Sapping Pollen.
		storage.decreaseByItemId(164000143, storage.getItemCountByItemId(164000143)); //Maga's Potion.
		storage.decreaseByItemId(164000141, storage.getItemCountByItemId(164000141)); //Silver Blade Rotan.
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
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("GRAND_CAVERN_300230000")) {
            if (!grandCavernEvent) {
				grandCavernEvent = true;
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int kromede1 = videoRace == Race.ASMODIANS ? 453 : 453;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, kromede1));
						}
					}
				});
			}
		} else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("MANOR_ENTRANCE_300230000")) {
            if (!manorEntranceEvent) {
				manorEntranceEvent = true;
				//There is an object of great power nearby.
				sendMsgByRace(1400653, Race.PC_ALL, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int kromede2 = videoRace == Race.ASMODIANS ? 462 : 462;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, kromede2));
						}
					}
				});
			}
		} else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("KALIGA_DUNGEONS_300230000")) {
			if (!kaligaDungeonsEvent) {
				kaligaDungeonsEvent = true;
				SkillEngine.getInstance().applyEffectDirectly(19288, player, player, 3600000 * 1); //Rage Of Kromede.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int kromede3 = videoRace == Race.ASMODIANS ? 454 : 454;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, kromede3));
						}
					}
				});
			}
        }
    }
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	@Override
    public void onInstanceDestroy() {
        doors.clear();
    }
}