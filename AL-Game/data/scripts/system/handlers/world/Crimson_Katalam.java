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
package world;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@WorldID(800030000)
public class Crimson_Katalam extends GeneralWorldHandler
{
	private int katalamMobs;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Creature creature = (Creature) npc.getTarget();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///BASE 701.
			case 662500:
			case 662509:
			case 662510:
			    if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 701.
				    sendMsgByRace(1405285, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 701.
				    sendMsgByRace(1405286, Race.PC_ALL, 0);
				}
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(807233)); //Unstable Danuar Mysticarium.
					}
				}, 1800000); //...30 Min
			break;
			///BASE 702
			case 662516:
			case 662525:
			case 662526:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 702.
				    sendMsgByRace(1405283, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 702.
				    sendMsgByRace(1405289, Race.PC_ALL, 0);
				}
			break;
			///BASE 703
			case 662532:
			case 662541:
			case 662542:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 703.
				    sendMsgByRace(1405291, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 703.
				    sendMsgByRace(1405292, Race.PC_ALL, 0);
				}
			break;
			///BASE 704
			case 662548:
			case 662557:
			case 662558:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 704.
				    sendMsgByRace(1405294, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 704.
				    sendMsgByRace(1405295, Race.PC_ALL, 0);
				}
			break;
			///BASE 705
			case 662564:
			case 662573:
			case 662574:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 705.
				    sendMsgByRace(1405297, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 705.
				    sendMsgByRace(1405298, Race.PC_ALL, 0);
				}
			break;
			///BASE 706
			case 662580:
			case 662589:
			case 662590:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 706.
				    sendMsgByRace(1405300, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 706.
				    sendMsgByRace(1405301, Race.PC_ALL, 0);
				}
			break;
			///BASE 707
			case 662596:
			case 662605:
			case 662606:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 707.
				    sendMsgByRace(1405303, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 707.
				    sendMsgByRace(1405304, Race.PC_ALL, 0);
				}
			break;
			///BASE 708
			case 662612:
			case 662621:
			case 662622:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 708.
				    sendMsgByRace(1405306, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 708.
				    sendMsgByRace(1405307, Race.PC_ALL, 0);
				}
			break;
			///BASE 709
			case 662628:
			case 662637:
			case 662638:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 709.
				    sendMsgByRace(1405309, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 709.
				    sendMsgByRace(1405310, Race.PC_ALL, 0);
				}
			break;
			///BASE 710
			case 662644:
			case 662653:
			case 662654:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 710.
				    sendMsgByRace(1405312, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 710.
				    sendMsgByRace(1405313, Race.PC_ALL, 0);
				}
			break;
			///BASE 711
			case 662660:
			case 662669:
			case 662670:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 711.
				    sendMsgByRace(1405315, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 711.
				    sendMsgByRace(1405316, Race.PC_ALL, 0);
				}
			break;
			///BASE 712
			case 662676:
			case 662685:
			case 662686:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 712.
				    sendMsgByRace(1405318, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 712.
				    sendMsgByRace(1405319, Race.PC_ALL, 0);
				}
			break;
			///BASE 713
			case 662692:
			case 662701:
			case 662702:
				if (player.getRace() == Race.ELYOS) {
					//The Elyos have occupied Base 713.
				    sendMsgByRace(1405321, Race.PC_ALL, 0);
				} else {
					//The Asmodians have occupied Base 713.
				    sendMsgByRace(1405322, Race.PC_ALL, 0);
				}
			break;
			///WORLD RAID BOSS 7.x
			case 858505: //Assault Tumon Alpha Weapon.
			case 858656: //Assault Tumon Gamma Weapon.
				//The Devil Unit's Tumon has been destroyed.
				sendMsgByRace(1402390, Race.PC_ALL, 0);
				//The Devil Unit is preparing for its return.
				sendMsgByRace(1402385, Race.PC_ALL, 10000);
			break;
			case 858497: //Benoid Iota Weapon.
			    //The Devil Unit's Benoid has been destroyed.
				sendMsgByRace(1402392, Race.PC_ALL, 0);
				//The Devil Unit is preparing for its return.
				sendMsgByRace(1402385, Race.PC_ALL, 10000);
			break;
			case 858499: //Magnorion Beta Weapon.
			case 858654: //Magnorion Delta Weapon.
			    //The Devil Unit's Magno has been destroyed.
				sendMsgByRace(1402387, Race.PC_ALL, 0);
				//The Devil Unit is preparing for its return.
				sendMsgByRace(1402385, Race.PC_ALL, 10000);
			break;
			default:
			    if (npc.getTarget() instanceof Player) {
					katalamMobs++;
					if (katalamMobs == 500) {
						katalamMobs = 0;
						switch (Rnd.get(1, 25)) {
							case 1:
								ItemService.addItem(player, 188070899, 1); //Ultimate Manastone Box.
							break;
							case 2:
								ItemService.addItem(player, 188070898, 1); //Legendary Manastone Box.
							break;
							case 3:
								ItemService.addItem(player, 186020069, 50); //Ultimate Blood Mark.
							break;
							case 4:
								ItemService.addItem(player, 188072043, 1); //Ultimate Splen Equipment Box.
							break;
							case 5:
								ItemService.addItem(player, 188071059, 1); //Shining Skill Card Bundle.
							break;
							case 6:
								ItemService.addItem(player, 188073017, 1); //Ultimate Transformation Box.
							break;
							case 7:
								ItemService.addItem(player, 188070376, 2); //Golden Box Of Minion Contracts.
							break;
							case 8:
								ItemService.addItem(player, 190120147, 1); //Solar Unicorn - Superior Recovery.
							break;
							case 9:
								ItemService.addItem(player, 188900063, 5); //Experience Crystal.
							break;
							case 10:
								ItemService.addItem(player, 166033100, 5); //Ancient PvP Enchantment Stone.
							break;
							case 11:
								ItemService.addItem(player, 166033101, 5); //Legendary PvP Enchantment Stone.
							break;
							case 12:
								ItemService.addItem(player, 166033102, 5); //Ultimate PvP Enchantment Stone.
							break;
							case 13:
								ItemService.addItem(player, 166023100, 5); //Ancient PvE Enchantment Stone.
							break;
							case 14:
								ItemService.addItem(player, 166023101, 5); //Legendary PvE Enchantment Stone.
							break;
							case 15:
								ItemService.addItem(player, 166023102, 5); //Ultimate PvE Enchantment Stone.
							break;
							case 16:
								ItemService.addItem(player, 186020002, 100); //Gold Ingots.
							break;
							case 17:
								ItemService.addItem(player, 166401000, 1000); //Manastone Fastener.
							break;
							case 18:
								ItemService.addItem(player, 188071961, 1); //Luna Wooden Box (200).
							break;
							case 19:
								ItemService.addItem(player, 164010144, 500); //Titan Coin.
							break;
							case 20:
								ItemService.addItem(player, 188070768, 1); //Ancient Daevanion Skill Box.
							break;
							case 21:
								ItemService.addItem(player, 188070330, 1); //Legendary Daevanion Skill Box.
							break;
							case 22:
								ItemService.addItem(player, 169610397, 1); //[Title Card] Special Daeva 180 Day Pass.
							break;
							case 23:
								ItemService.addItem(player, 188074457, 1); //Gemstone Shard Box.
							break;
							case 24:
								ItemService.addItem(player, 188074458, 1); //Shining Gemstone Shard Box.
							break;
							case 25:
								ItemService.addItem(player, 188074459, 1); //Dazzling Gemstone Shard Box.
							break;
						}
					}
				}
            break;
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case -1:
				//To Do...
			break;
		}
	}
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	protected void respawnNpc(Npc npc) {
	    if (npc != null) {
            npc.getController().scheduleRespawn();
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
        List<Npc> npcs = new FastList<Npc>();
        for (Npc npc : this.map.getWorld().getNpcs()) {
            if (npc.getNpcId() == npcId) {
                npcs.add(npc);
            }
        }
        return npcs;
    }
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getWorldId() == map.getMapId() && player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
						}
					}
				});
			}
		}, time);
	}
}