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

@WorldID(600010000)
public class Silentera_Canyon extends GeneralWorldHandler
{
	private int silenteraMobs;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Creature creature = (Creature) npc.getTarget();
		switch (npc.getObjectTemplate().getTemplateId()) {
			default:
			    if (npc.getTarget() instanceof Player) {
					silenteraMobs++;
					if (silenteraMobs == 200) {
						silenteraMobs = 0;
						switch (Rnd.get(1, 25)) {
							case 1:
								ItemService.addItem(player, 188070899, 1); //Ultimate Manastone Box.
							break;
							case 2:
								ItemService.addItem(player, 188070898, 1); //Legendary Manastone Box.
							break;
							case 3:
								ItemService.addItem(player, 188070897, 1); //Ancient Manastone Box.
							break;
							case 4:
								ItemService.addItem(player, 188072043, 1); //Ultimate Splen Equipment Box.
							break;
							case 5:
								ItemService.addItem(player, 188070064, 1); //Motion Card Selection Box.
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
			case 730257: //Silentera Canyon To Agrief Ruins.
				inggisonGateway(player, 1066.0000f, 2005.0000f, 370.0000f, (byte) 0);
			break;
			case 730258: //Silentera Canyon To Hanarkand.
				inggisonGateway(player, 2756.0000f, 2141.0000f, 245.0000f, (byte) 0);
			break;
			case 730259: //Silentera Canyon To Pimeval Pass.
				inggisonGateway(player, 405.0000f, 2062.0000f, 332.0000f, (byte) 0);
			break;
			case 730270: //Silentera Canyon To Inggison Silentera Entrance.
				inggisonGateway(player, 1363.0000f, 2298.0000f, 296.0000f, (byte) 0);
			break;
			case 730261: //Silentera Canyon To Mitrakand.
				gelkmarosGateway(player, 691.0000f, 623.0000f, 385.0000f, (byte) 0);
			break;
			case 730262: //Silentera Canyon To Vorgaltem.
				gelkmarosGateway(player, 1623.0000f, 1135.0000f, 353.0000f, (byte) 0);
			break;
			case 730263: //Silentera Canyon To Earthfang Pass.
				gelkmarosGateway(player, 2934.0000f, 882.0000f, 310.0000f, (byte) 0);
			break;
			case 730271: //Silentera Canyon To Gelkmaros Silentera Entrance.
				gelkmarosGateway(player, 1448.0000f, 910.0000f, 294.0000f, (byte) 0);
			break;
		}
	}
	
	protected void inggisonGateway(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void gelkmarosGateway(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
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