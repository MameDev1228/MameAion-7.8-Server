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
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.summons.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import javolution.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300190000)
public class Taloc_Hollow extends GeneralInstanceHandler
{
	private Race videoRace;
	private Race spawnRace;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 653390: //Shishir.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000088, 1)); //Shishir's Corrosive Fluid.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000137, 1)); //Shishir's Powerstone.
		    break;
			case 653412: //Neith.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000108, 1)); //Dorkin's Pocket Knife.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000139, 1)); //Neith's Sleepstone.
		    break;
			case 653415: //Gellmar.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000138, 1)); //Gellmar's Wardstone.
		    break;
		}
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(49).setOpen(true);
		spawn(700738, 653.0000f, 838.0000f, 1304.0000f, (byte) 0, 90); //Huge Insect Egg.
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
		if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateQuest60308(player);
			ClassChangeService.onUpdateQuest61902(player);
		} else {
			ClassChangeService.onUpdateQuest70308(player);
			ClassChangeService.onUpdateQuest71902(player);
		} if (spawnRace == null) {
			spawnRace = player.getRace();
			spawnResearchJournal();
		} switch (player.getRace()) {
			case ELYOS:
				addTalocFruitE(player);
				addTalocTearsE(player);
			break;
			case ASMODIANS:
				addTalocFruitA(player);
				addTalocTearsA(player);
		    break;
		}
		//You must destroy the enemies of Taloc. It allows you to acquire objects with great power.
		sendMsgByRace(1400704, Race.PC_ALL, 5000);
		//An object of great power waits in your cube. Transform into a mighty being with Taloc's Fruit.
		sendMsgByRace(1400752, Race.PC_ALL, 10000);
		//An object of great power waits in your cube. Launch a powerful aerial attack with Taloc's Tears.
		sendMsgByRace(1400753, Race.PC_ALL, 15000);
    }
	
	private void spawnResearchJournal() {
		final int researchJournal = spawnRace == Race.ASMODIANS ? 701526 : 700636;
		spawn(researchJournal, 546.0000f, 845.0000f, 1377.0000f, (byte) 0, 57);
	}
	
    @Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 653390: //Shishir.
				//An object of great power waits in Shishir's carcass. Obtain it, then register it in the skill window.
		        sendMsgByRace(1400754, Race.PC_ALL, 0);
            break;
			case 653412: //Neith.
				//An object of great power waits in Neith's carcass. Obtain it, then register it in the skill window.
		        sendMsgByRace(1400756, Race.PC_ALL, 0);
            break;
			case 653414: //Queen Mosqua.
			    //If you break Queen Apitan's eggs, an air stream is created that takes you up.
				sendMsgByRace(1400476, Race.PC_ALL, 5000);
				despawnNpcs(instance.getNpcs(700738)); //Huge Insect Egg.
				spawn(700739, 653.0000f, 838.0000f, 1304.0000f, (byte) 0, 11); //Cracked Huge Insect Egg.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int taloc1 = videoRace == Race.ASMODIANS ? 435 : 435;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, taloc1));
						}
					}
				});
            break;
			case 653415: //Gellmar.
				//An object of great power waits in Gellmar's carcass. Obtain it, then register it in the skill window.
		        sendMsgByRace(1400755, Race.PC_ALL, 0);
            break;
            case 653420: //Celestius.
			    //Contaminated Fragment Of Aion Tower.
			    despawnNpcs(instance.getNpcs(700740));
                spawn(799503, 539.0000f, 813.0000f, 1377.0000f, (byte) 27); //Taloc's Mirage.
				spawn(836443, 531.0000f, 815.0000f, 1377.0000f, (byte) 18); //Taloc's Hollow Exit.
				spawn(700741, 636.0000f, 769.0000f, 1387.0000f, (byte) 0, 92); //Purified Fragment Of Aion Tower.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int taloc2 = videoRace == Race.ASMODIANS ? 437 : 437;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, taloc2));
						}
					}
				});
            break;
			case 653416: //Komad Elite Watcher.
			case 653417: //Komad Elite Watchman.
			case 653418: //Komad Shaman.
			case 653419: //Komad Shaman.
			case 653448: //Komad Elite Legionary.
			case 653449: //Komad Elite Legionary.
			case 653450: //Komad Shaman.
			case 653451: //Komad Shaman.
			    despawnNpc(npc);
			    //The recovery plant has emerged.
				sendMsgByRace(1403824, Race.PC_ALL, 0);
				spawn(700940, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Healing Plant.
			break;
			case 700739: //Cracked Huge Insect Egg.
				despawnNpc(npc);
				//An ascending air current is rising from the spot where the egg was. You can fly vertically up by spreading your wings and riding the current.
				sendMsgByRace(1400477, Race.PC_ALL, 5000);
				spawn(281817, 653.0000f, 838.0000f, 1303.0000f, (byte) 0, 1308); //Geyser.
            break;
			case 701545: //Thorny Vines.
			    despawnNpc(npc);
				//The cocoons are wriggling--something's inside!
				sendMsgByRace(1400475, Race.PC_ALL, 0);
				//You can save one of the two Reians imprisoned in the cocoon.
				sendMsgByRace(1400630, Race.PC_ALL, 5000);
			break;
			case 700632: //Thorny Vines.
			case 700633: //Thorny Vines.
			case 700942: //Bug Fluid.
			    despawnNpc(npc);
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 700940: //Healing Plant.
			case 700941: //Huge Healing Plant.
				despawnNpc(npc);
				SkillEngine.getInstance().applyEffectDirectly(19230, player, player, 300000 * 1); //Taloc's Blessing.
			break;
		}
	}
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 48) {
			doors.get(48).setOpen(true);
			//Smoke is being discharged. Exposure to smoke will destroy Kinquid's Barrier.
			sendMsgByRace(1400660, Race.PC_ALL, 10000);
			instance.doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.isOnline()) {
						final int taloc3 = videoRace == Race.ASMODIANS ? 463 : 463;
						PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, taloc3));
					}
				}
			});
		} else if (doorId == 7) {
			doors.get(7).setOpen(true);
			instance.doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.isOnline()) {
						final int taloc4 = videoRace == Race.ASMODIANS ? 464 : 464;
						PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, taloc4));
					}
				}
			});
		}
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
		Summon summon = player.getSummon();
		if ((summon != null) && (summon.isSpawned())) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
		Summon summon = player.getSummon();
		if ((summon != null) && (summon.isSpawned())) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(182215618, storage.getItemCountByItemId(182215618)); //Taloc Fruit.
		storage.decreaseByItemId(182215593, storage.getItemCountByItemId(182215593)); //Taloc Fruit.
		storage.decreaseByItemId(182215619, storage.getItemCountByItemId(182215619)); //Taloc's Tears.
		storage.decreaseByItemId(182215592, storage.getItemCountByItemId(182215592)); //Taloc's Tears.
		storage.decreaseByItemId(164000137, storage.getItemCountByItemId(164000137)); //Shishir's Powerstone.
		storage.decreaseByItemId(164000138, storage.getItemCountByItemId(164000138)); //Gellmar's Wardstone.
		storage.decreaseByItemId(164000139, storage.getItemCountByItemId(164000139)); //Neith's Sleepstone.
	}
	
	private void addTalocFruitE(Player player) {
	    ItemService.addItem(player, 182215618, 1); //Taloc Fruit.
    }
	private void addTalocTearsE(Player player) {
        ItemService.addItem(player, 182215619, 1); //Taloc's Tears.
    }
	private void addTalocFruitA(Player player) {
		ItemService.addItem(player, 182215593, 1); //Taloc Fruit.
    }
	private void addTalocTearsA(Player player) {
        ItemService.addItem(player, 182215592, 1); //Taloc's Tears.
    }
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(10251); //Taloc Fruit.
		effectController.removeEffect(10252); //Taloc Fruit.
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
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
	
	@Override
    public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
    }
}