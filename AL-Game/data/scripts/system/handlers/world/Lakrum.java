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

@WorldID(800050000)
public class Lakrum extends GeneralWorldHandler
{
	private int lakrumHunting;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Creature creature = (Creature) npc.getTarget();
		switch (npc.getObjectTemplate().getTemplateId()) {
			default:
			    if (npc.getTarget() instanceof Player) {
					lakrumHunting++;
					if (lakrumHunting == 500) {
						lakrumHunting = 0;
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