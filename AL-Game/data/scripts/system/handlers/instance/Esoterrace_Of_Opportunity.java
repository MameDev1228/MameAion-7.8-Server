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
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302850000)
public class Esoterrace_Of_Opportunity extends GeneralInstanceHandler
{
	private Race videoRace;
	private int labManagerKilled;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 655281: //Clumsy Smuggler Shukirukin.
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
			case 701025: //Esoterrace Sundries Box.
				switch (Rnd.get(1, 6)) {
				    case 1:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190000050, 1)); //Whitebeard Manduri Egg.
					break;
					case 2:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190020089, 1)); //Blue Merek.
					break;
					case 3:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190020148, 1)); //Infernal Diabol.
					break;
					case 4:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190020204, 1)); //Cheering Dandi's.
					break;
					case 5:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190070004, 1)); //Su-ro Kim Summoning Lamp.
					break;
					case 6:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190070012, 1)); //Pink Merek Egg.
					break;
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
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		switch (Rnd.get(1, 9)) {
			case 1:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 1303.9346f, 1191.4535f, 51.493996f, (byte) 102);
			break;
			case 2:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 1029.1622f, 899.1935f, 329.4151f, (byte) 119);
			break;
			case 3:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 1133.2673f, 871.41705f, 316.68976f, (byte) 90);
			break;
			case 4:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 789.7439f, 1040.4708f, 364.2823f, (byte) 90);
			break;
			case 5:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 1380.166f, 1176.7802f, 155.8476f, (byte) 112);
			break;
			case 6:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 1340.9235f, 586.7839f, 280.6923f, (byte) 101);
			break;
			case 7:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 982.4328f, 505.27408f, 234.73889f, (byte) 95);
			break;
			case 8:
			    //Clumsy Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404776, Race.PC_ALL, 30000);
				spawn(655281, 1224.4377f, 1073.6794f, 188.3745f, (byte) 62);
			break;
			case 9:
			break;
		}
    }
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			/**
			 * From the main entrance, players have access to the larger half of the Esoterrace Secret Laboratory.
			 * In the middle of the Laboratory is a Surkana Feeder.
			 * Destroy this to face Warden Surama, the “Hard Mode” final Named Monster.
			 * Leave it alone to face Kexkra, the normal final Named Monster.
			 */
			case 282291: //Surkana Feeder.
			    despawnNpc(npc);
				despawnNpcs(instance.getNpcs(655530)); //Kexkra.
				//The Surkana Supplier has overloaded.
				sendMsgByRace(1400996, Race.PC_ALL, 0);
				//The Surkana Supplier has been broken.
				sendMsgByRace(1401037, Race.PC_ALL, 4000);
				spawn(655531, 1315.0000f, 1170.0000f, 51.0000f, (byte) 87); //Kexkra Prototype.
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(19523, player, player, 10000 * 1); //Surkana Overload.
				    }
			    });
			break;
			/**
			 * "Dalia Charlands" is the first Named Monster of Esoterrace.
			 * Before engaging the boss, be sure to clear out the surrounding area of patrolling monsters.
			 * Dalia is a straightforward encounter that doesn't have a lot of gimmicks, just be sure to watch out for the occasional area of effect attack. 
			 * When Dalia is defeated, Dalia's Watcher will appear, follow it to find the path to the next area.
			 * Be sure each player in the Group loots the Dalia Key from "Dalia Charlands"
			 * Like a Quest item, each player can loot this key, and earn access to one of the "Entwined Treasure Chest" nearby which contain Abyss relics!
			 * In addition to the "Entwined Treasure Chests" & "Huge Entwined Chest" will spawn beneath Dalia once it is defeated, which can be opened with the Swirl Key.
			 * Should the Group befall certain death after defeating Dalia,
			 * the Windstream found at the beginning of the Instanced Dungeon will now transport players directly to the Dalia Garden. 
			 */
			case 655511: //Dalia Charlands.
			    //Dalia Charlands has vanished.
				sendMsgByRace(1401036, Race.PC_ALL, 0);
				//The Surkana Steam Jet has generated an updraft.
				sendMsgByRace(1400997, Race.PC_ALL, 5000);
				//Defeat all Drana Production Lab Section Managers to open the Laboratory Yard door.
				sendMsgByRace(1400919, Race.PC_ALL, 120000);
				spawn(703109, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream A.
				spawn(703111, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream B.
				spawn(701022, 1276.0000f, 614.0000f, 296.0000f, (byte) 0, 85); //Entwined Chest.
				spawn(701022, 1291.0000f, 634.0000f, 296.0000f, (byte) 0, 86); //Entwined Chest.
				spawn(701022, 1286.0000f, 671.0000f, 296.0000f, (byte) 0, 89); //Entwined Chest.
				spawn(701022, 1235.0000f, 651.0000f, 296.0000f, (byte) 0, 91); //Entwined Chest.
				spawn(701022, 1255.0000f, 629.0000f, 296.0000f, (byte) 0, 93); //Entwined Chest.
				spawn(701022, 1248.0000f, 673.0000f, 296.0000f, (byte) 0, 206); //Entwined Chest.
				spawn(701023, 1264.0000f, 644.0000f, 296.0000f, (byte) 0, 112); //Large Entwined Chest.
            break;
			case 655539: //Esoterrace Investigator.
			case 655540: //Senior Lab Researcher.
			case 655541: //Lab Supervisor.
				labManagerKilled++;
				if (labManagerKilled == 1) {
					doors.get(367).setOpen(false);
				} else if (labManagerKilled == 2) {
					doors.get(69).setOpen(false);
				} else if (labManagerKilled == 3) {
					doors.get(111).setOpen(true);
					//The door to the Laboratory Yard is now open.
					sendMsgByRace(1400920, Race.PC_ALL, 0);
					//The Drana Production Lab walkway is now open.
					sendMsgByRace(1400923, Race.PC_ALL, 4000);
					//Use the open entrance to move to the next area.
					sendMsgByRace(1402781, Race.PC_ALL, 6000);
					//Alarm Siren.
					spawn(258262, 1344.0000f, 1039.000f, 203.0000f, (byte) 0, 467);
					spawn(258262, 1347.0000f, 1029.000f, 203.0000f, (byte) 0, 470);
					spawn(258262, 1057.0000f, 901.0000f, 347.0000f, (byte) 0, 599);
					spawn(258262, 1057.0000f, 895.0000f, 347.0000f, (byte) 0, 600);
					spawn(258262, 1057.0000f, 889.0000f, 347.0000f, (byte) 0, 601);
				}
			break;
			case 655538: //Lab Gatekeeper.
				doors.get(70).setOpen(true);
				//The door to the Laboratory Air Conditioning Room is now open.
				sendMsgByRace(1400921, Race.PC_ALL, 0);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
            break;
			case 286930: //Esoterrace Mage.
                despawnNpc(npc);
				spawn(799580, 1034.0000f, 985.0000f, 327.0000f, (byte) 105); //Keening Sirokin.
				spawn(701025, 1038.0000f, 987.0000f, 328.0000f, (byte) 0, 725); //Sundries Box.
            break;
		   /**
			* Inside the Laboratory Air Conditioning Room, players will encounter the second Named Monster of Esoterrace, "Captain Murugan"
			* Be wary of "Captain Murugan's" deadly combo skills, expect the primary target to take massive damage throughout the encounter!
			* When Captain Murugan is defeated, two doors will open in the Laboratory Air Conditioning Room,
			* granting access to Chilled Treasure chests which contain Abyss relics. 
			*/
			case 655521: //Captain Murugan.
				switch (Rnd.get(1, 2)) {
				    case 1:
						doors.get(45).setOpen(true);
						//With the gatekeeper down, the door on the left is open!
						sendMsgByRace(1401229, Race.PC_ALL, 0);
					break;
			        case 2:
						doors.get(67).setOpen(true);
						//With the gatekeeper down, the door on the right is open!
						sendMsgByRace(1401230, Race.PC_ALL, 0);
					break;
				}
				doors.get(52).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 4000);
				//The Surkana Steam Jet has generated an updraft.
				sendMsgByRace(1400997, Race.PC_ALL, 6000);
				spawn(703113, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream C
				spawn(703115, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream D
				spawn(701024, 751.0000f, 1136.000f, 365.0000f, (byte) 105, 41); //Chilled Treasure.
				spawn(701024, 827.0000f, 1136.000f, 365.0000f, (byte) 73, 77); //Chilled Treasure.
            break;
			case 282293: //Esoterrace Ventilator.
				//The Laboratory Ventilator is now open.
				sendMsgByRace(1400922, Race.PC_ALL, 0);
			break;
			case 655546: //Esoterrace Biolab Watchman.
				doors.get(122).setOpen(true);
				//The outer wall of the Bio Lab has collapsed.
				sendMsgByRace(1400924, Race.PC_ALL, 0);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
            break;
		   /**
			* When "Kexkra" is defeated, a treasure chest will spawn.
			* In addition, the treasure chest has a chance to contain Fabled armor from the Surama set.
			*/
			case 655530: //Kexkra.
				spawn(701044, 1341.0000f, 1181.0000f, 51.0000f, (byte) 67); //Esoterrace Dimensional Rift Exit.
				spawn(701027, 1326.0000f, 1173.0000f, 51.0000f, (byte) 70, 726); //Laboratory Treasure Chest.
				spawn(701027, 1321.0000f, 1179.0000f, 51.0000f, (byte) 79, 727); //Laboratory Treasure Chest.
            break;
			case 655531: //Kexkra Prototype.
			    despawnNpc(npc);
				spawn(655532, 1315.0000f, 1170.0000f, 51.0000f, (byte) 87); //Warden Surama.
				spawn(701047, 1316.0000f, 1171.0000f, 52.0000f, (byte) 0, 180); //Flame Wall.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int esoterrace = videoRace == Race.ASMODIANS ? 472 : 472;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, esoterrace));
						}
					}
				});
            break;
		   /**
			* Players will start this encounter facing the "Kexkra Prototype"
			* As the encounter wears on, an event will cause Warden Surama to join the battle.
			* When Warden Surama is defeated, two treasure chests will spawn,
			* one of which has a chance to contain Fabled armor from the Surama series, and the other Fabled weapons from the Surama series. 
			*/
            case 655532: //Warden Surama.
				spawn(701044, 1341.0000f, 1181.0000f, 51.0000f, (byte) 67); //Esoterrace Dimensional Rift Exit.
				spawn(701027, 1326.0000f, 1173.0000f, 51.0000f, (byte) 70, 726); //Laboratory Treasure Chest.
				spawn(701027, 1321.0000f, 1179.0000f, 51.0000f, (byte) 79, 727); //Laboratory Treasure Chest.
            break;
			case 655549: //Dalia Watcher.
			    despawnNpc(npc);
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 282295: //Command Gate Control.
				doors.get(39).setOpen(true);
				//A heavy door has opened somewhere.
				sendMsgByRace(1401839, Race.PC_ALL, 0);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
			break;
		}
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
        doors.clear();
    }
}