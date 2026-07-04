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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
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

@WorldID(800060000)
public class Demaha extends GeneralWorldHandler
{
	private int demahaHunting;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Creature creature = (Creature) npc.getTarget();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///DEMAHA DRILL CAMP 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=355&page=2&categoryId=0&viewMode=thumb&size=20
			case 887579: //Neglected Stellin Drill.
			    if (player.getRace() == Race.ELYOS) {
					spawn(800060000, 887584, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Elyos Drill.
				} else {
					spawn(800060000, 887589, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Asmodian Drill.
				}
				spawn(800060000, 807209, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //Gemstone Ore.
				spawn(800060000, 807211, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //Hard Gemstone Ore.
				spawn(800060000, 807212, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //High-Purity Gemstone Dust.
			break;
			case 887584: //Elyos Drill.
			    if (player.getRace() == Race.ASMODIANS) {
					spawn(800060000, 887589, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
				}
				spawn(800060000, 807209, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //Gemstone Ore.
				spawn(800060000, 807211, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //Hard Gemstone Ore.
				spawn(800060000, 807212, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //High-Purity Gemstone Dust.
			break;
			case 887589: //Asmodian Drill.
			    if (player.getRace() == Race.ELYOS) {
					spawn(800060000, 887584, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
				}
				spawn(800060000, 807209, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //Gemstone Ore.
				spawn(800060000, 807211, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //Hard Gemstone Ore.
				spawn(800060000, 807212, npc.getX(), npc.getY() + 5, npc.getZ() + 1, (byte) 0); //High-Purity Gemstone Dust.
			break;
			///ALTAR APSU'S TORCHES.
			//5th Altar.
			case 886647:
			case 886652:
			case 886657:
			//6th Altar.
			case 886686:
			case 886691:
			case 886696:
			//7th Altar.
			case 886725:
			case 886730:
			case 886735:
			//8th Altar.
			case 886764:
			case 886769:
			case 886774:
			    if (player.getRace() == Race.ELYOS) {
					///The Elyos control all of Apsu's Torches!
					sendMsgByRace(1405056, Race.PC_ALL, 10000);
				} else {
					///The Asmodians control all of Apsu's Torches!
					sendMsgByRace(1405057, Race.PC_ALL, 10000);
				}
			break;
			///STELLIN INDUSTRIES SENTINEL.
			//9th Altar.
			case 886803:
			case 886808:
			case 886813:
			//10th Altar.
			case 886842:
			case 886847:
			case 886852:
			//11th Altar.
			case 886881:
			case 886886:
			case 886891:
			    if (player.getRace() == Race.ELYOS) {
					///A weapon that can be used by the Elyos has been summoned!
					sendMsgByRace(1405056, Race.PC_ALL, 10000);
				} else {
					///A weapon that can be used by the Asmodians has been summoned!
					sendMsgByRace(1405057, Race.PC_ALL, 10000);
				}
			break;
			default:
			    if (npc.getTarget() instanceof Player) {
					demahaHunting++;
					if (demahaHunting == 500) {
						demahaHunting = 0;
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
	public void handleUseItemFinish(final Player player, Npc npc) {
		switch (npc.getNpcId()) {
			///RVR [Apsus's Altar - Apsu's Torch]
			case 837747:
			case 837748:
			case 837749:
			case 837750:
			case 837751:
			case 837752:
			case 837753:
			case 837754:
				SkillEngine.getInstance().applyEffectDirectly(17805, player, player, 600000 * 1); //Apsu's Torch.
			break;
			///RVR [Apsus's Altar - Stellin Industries Sentinel]
			case 657719:
			case 657720:
			case 657721:
			case 657722:
			case 657723:
			case 657724:
			    if (player.isTransformed()) {
					///You cannot use this skill while transformed.
				    sendMsgByRace(1300149, Race.PC_ALL, 0);
				    ///Transformation Mode.
					sendMsgByRace(1401212, Race.PC_ALL, 3000);
				} else {
					despawnNpc(npc);
					industriesSentinel(player);
					SkillEngine.getInstance().applyEffectDirectly(20863, player, player, 180000 * 1); //Board Stellin Weapon.
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							removeEffects(player);
						}
					}, 180000);
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	public static final void industriesSentinel(final Player player) {
		player.getSkillList().addSkill(player, 19512, 1);
		player.getSkillList().addSkill(player, 19513, 1);
		player.getSkillList().addSkill(player, 19573, 1);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20863); //Board Stellin Weapon.
		SkillLearnService.removeSkill(player, 19512);
		SkillLearnService.removeSkill(player, 19513);
		SkillLearnService.removeSkill(player, 19573);
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