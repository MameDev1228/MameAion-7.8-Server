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

@WorldID(220070000)
public class Gelkmaros extends GeneralWorldHandler
{
	private int entryCount;
	private int coliseumMobs;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Creature creature = (Creature) npc.getTarget();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///BOSS WINDSTREAM.
			case 650381: //Agrima.
				//Ancient Windstream is temporarily activated.
				sendMsgByRace(1401331, Race.PC_ALL, 0);
				spawn(220070000, 281817, 1719.0000f, 2301.0000f, 318.0000f, (byte) 0, 1821);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(281817));
					}
				}, 300000); //...5 Min
			break;
			///GELKMAROS GARRISON 7.x
			case 661884: //The 1st Garrison Occupier Combat Officer.
				//You have occupied the Fatebound Legion's 1st Garrison.
				sendMsgByRace(1406067, Race.PC_ALL, 2000);
			break;
			case 661894: //The 2nd Garrison Occupier Combat Officer.
				//You have occupied the Fatebound Legion's 2nd Garrison.
				sendMsgByRace(1406071, Race.PC_ALL, 2000);
			break;
			case 661904: //The 3rd Garrison Occupier Combat Officer.
				//You have occupied the Fatebound Legion's 3rd Garrison.
				sendMsgByRace(1406075, Race.PC_ALL, 2000);
			break;
			case 661914: //The 4th Garrison Occupier Combat Officer.
				//You have occupied the Fatebound Legion's 4th Garrison.
				sendMsgByRace(1406079, Race.PC_ALL, 2000);
			break;
			case 661924: //The 5th Garrison Occupier Combat Officer.
				//You have occupied the Fatebound Legion's 5th Garrison.
				sendMsgByRace(1406083, Race.PC_ALL, 2000);
			break;
			case 661890: //The 1st Garrison Combat Officer.
				//The Fatebound Legion's 1st Garrison has been destroyed.
				sendMsgByRace(1406069, Race.PC_ALL, 2000);
			break;
			case 661900: //The 2nd Garrison Combat Officer.
				//The Fatebound Legion's 2nd Garrison has been destroyed.
				sendMsgByRace(1406073, Race.PC_ALL, 2000);
			break;
			case 661910: //The 3rd Garrison Combat Officer.
				//The Fatebound Legion's 3rd Garrison has been destroyed.
				sendMsgByRace(1406077, Race.PC_ALL, 2000);
			break;
			case 661920: //The 4th Garrison Combat Officer.
				//The Fatebound Legion's 4th Garrison has been destroyed.
				sendMsgByRace(1406081, Race.PC_ALL, 2000);
			break;
			case 661930: //The 5th Garrison Combat Officer.
				//The Fatebound Legion's 5th Garrison has been destroyed.
				sendMsgByRace(1406085, Race.PC_ALL, 2000);
			break;
			///BRAHMA DREDGION 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=349&categoryId=16
			case 661868: //Captain Agra.
				//The Dredgion was destroyed.
				sendMsgByRace(1403958, Race.PC_ALL, 0);
				ConquestService.getInstance().stopConquest(4);
				//The Brahma Dredgion is about to explode! Hurry and escape!
				sendMsgByRace(1406159, Race.PC_ALL, 5000);
				//The Brahma Dredgion has retreated.
				sendMsgByRace(1406064, Race.PC_ALL, 15000);
				spawn(220070000, 662922, 784.19604f, 2506.2494f, 2058.95f, (byte) 60);
			break;
			case 661871: //Captain Lahor.
				//The Dredgion was destroyed.
				sendMsgByRace(1403958, Race.PC_ALL, 0);
				ConquestService.getInstance().stopConquest(4);
			    //The Brahma Dredgion is about to explode! Hurry and escape!
				sendMsgByRace(1406159, Race.PC_ALL, 5000);
				//The Brahma Dredgion has retreated.
				sendMsgByRace(1406065, Race.PC_ALL, 15000);
				spawn(220070000, 662923, 784.19604f, 1506.2494f, 2058.95f, (byte) 60);
			break;
			case 661874: //Captain Zizakh.
				//The Dredgion was destroyed.
				sendMsgByRace(1403958, Race.PC_ALL, 0);
				ConquestService.getInstance().stopConquest(4);
			    //The Brahma Dredgion is about to explode! Hurry and escape!
				sendMsgByRace(1406159, Race.PC_ALL, 5000);
				//The Brahma Dredgion has retreated.
				sendMsgByRace(1406066, Race.PC_ALL, 15000);
				spawn(220070000, 662924, 784.19604f, 506.2494f, 2058.95f, (byte) 60);
			break;
			///RETSHA SKY ASSAULTER 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=349&page=2&categoryId=0&viewMode=thumb&size=20
			case 661813:
			case 661814:
			case 661815:
			case 661816:
			case 661817:
			case 661818:
			case 661831:
			    //A passage to the Brahma Dredgion has opened.
				sendMsgByRace(1406061, Race.PC_ALL, 2000);
				//DF4_Dreadgion_DirectPortal_In_01.
			    spawn(220070000, 703902, npc.getX(), npc.getY(), npc.getZ() + 2, (byte) 0); //Dredgion Entrance.
			break;
			case 661819:
			case 661820:
			case 661821:
			case 661822:
			case 661823:
			case 661824:
				//A passage to the Brahma Dredgion has opened.
				sendMsgByRace(1406062, Race.PC_ALL, 2000);
				spawn(220070000, 703903, npc.getX(), npc.getY(), npc.getZ() + 2, (byte) 0); //Dredgion Entrance.
			break;
			case 661825:
			case 661826:
			case 661827:
			case 661828:
			case 661829:
			case 661830:
			case 661832:
				//A passage to the Brahma Dredgion has opened.
				sendMsgByRace(1406063, Race.PC_ALL, 2000);
				spawn(220070000, 703904, npc.getX(), npc.getY(), npc.getZ() + 2, (byte) 0); //Dredgion Entrance.
			break;
            default:
			    ///GELKMAROS COLISEUM 7.x
			    ///https://aion.plaync.com/board/aionreport/view?articleId=398&page=1&categoryId=0&viewMode=thumb&size=20
			    if (npc.getTarget() instanceof Player) {
					coliseumMobs++;
					if (coliseumMobs == 200) {
						coliseumMobs = 0;
						gelkmarosArena();
						//Gelkmaros Arena entrance has opened.
						sendMsgByRace(1406087, Race.PC_ALL, 0);
						if (player.getRace() == Race.ASMODIANS) {
							spawn(220070000, 703921, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Secret Rift.
							spawn(220070000, 839273, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Asmodian Dimensional Rift.
						} else {
							spawn(220070000, 703923, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Secret Rift.
							spawn(220070000, 839272, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Elyos Dimensional Rift.
						}
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								despawnNpcs(getNpcs(703921));
								despawnNpcs(getNpcs(703923));
								despawnNpcs(getNpcs(839272));
								despawnNpcs(getNpcs(839273));
							}
						}, 300000); //...5 Min
					}
				}
            break;
        }
    }
	
	protected void gelkmarosArena() {
		//Gelkmaros Arena battle will begin in 5 minutes.
		sendMsgByRace(1406088, Race.PC_ALL, 10000);
		//Gelkmaros Arena battle will begin in 4 minutes.
		this.sendMessage(1406089, 1 * 60 * 1000);
		//Gelkmaros Arena battle will begin in 3 minutes.
		this.sendMessage(1406090, 2 * 60 * 1000);
		//Gelkmaros Arena battle will begin in 2 minutes.
		this.sendMessage(1406091, 3 * 60 * 1000);
		//Gelkmaros Arena battle will begin in 1 minute.
		this.sendMessage(1406092, 4 * 60 * 1000);
		//Gelkmaros Arena battle has begun.
		this.sendMessage(1406093, 5 * 60 * 1000);
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			///EMPTY AETHERIC CANNON [Gelkmaros]
			case 882254:
			    if (player.isTransformed()) {
					//You cannot use this skill while transformed.
				    sendMsgByRace(1300149, Race.PC_ALL, 0);
				    //Transformation Mode.
					sendMsgByRace(1401212, Race.PC_ALL, 3000);
				} if (player.getInventory().decreaseByItemId(186000246, 1)) { //Magic Cannonball.
					despawnNpc(npc);
					SkillEngine.getInstance().applyEffectDirectly(21518, player, player, 3600000 * 1); //Get On Antiaircraft Gun.
			    } else {
					//The weapon needs auxiliary magic fuel before you can use it.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402191));
				}
			break;
			case 836442: //Rift To Tiamaranta's Eye.
			    if (player.getLevel() >= 76 && player.getLevel() <= 80) {
				    tiamarantaEyeRift(player, 754.0000f, 1340.0000f, 1201.0000f, (byte) 26);
                } else {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_DIRECT_PORTAL_LEVEL_LIMIT);
				}
			break;
			///TELEPORTATION TOWER [Vorgaltem Citadel]
			case 257833:
			case 257834:
			    vorgaltemCitadel(player, 1197.0000f, 826.0000f, 313.0000f, (byte) 30);
			break;
			///TELEPORTATION TOWER [Crimson Temple]
			case 258133:
			case 258134:
			    crimsonTemple(player, 1879.0000f, 1065.0000f, 330.0000f, (byte) 32);
			break;
			///HIDE BOSS [Gelkmaros]
			case 700772: //Fresh Fruit.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216640, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Rokbak.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Rokbak> appear!!!");
			break;
			case 700773: //Soul Invocation Jar.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216628, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Vengeful Romludon's.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Vengeful Romludon's> appear!!!");
			break;
			case 700774: //Snuffler Thistle Trap.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216641, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Nimblestump.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Nimblestump> appear!!!");
			break;
			case 700775: //Worn Spell Scroll.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216629, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sorceress Imilyana's.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Sorceress Imilyana's> appear!!!");
			break;
			case 700776: //Sandy Carcass.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216642, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Zantra.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Zantra> appear!!!");
			break;
			case 700777: //Vine Seed.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(220070000, 216643, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Vinestem.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Vinestem> appear!!!");
			break;
			///GELKMAROS RENOWN RIFT 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=348&page=3&categoryId=0&viewMode=thumb&size=20
			case 703933:
			    entryCount++;
			    if (entryCount == 96) {
					entryCount = 0;
					//LF4_Fxp_Directportal_Lv9_Start.
					killNpc(getNpcs(703933));
				} switch (Rnd.get(1, 13)) {
					case 1:
						fameRift(player, 1814.0000f, 242.0000f, 521.0000f, (byte) 10);
					break;
					case 2:
						fameRift(player, 2331.0000f, 487.0000f, 431.0000f, (byte) 15);
					break;
					case 3:
						fameRift(player, 2070.0000f, 205.0000f, 490.0000f, (byte) 31);
					break;
					case 4:
						fameRift(player, 2605.0000f, 1318.0000f, 330.0000f, (byte) 74);
					break;
					case 5:
						fameRift(player, 2240.0000f, 2092.0000f, 58.0000f, (byte) 65);
					break;
					case 6:
						fameRift(player, 191.0000f, 474.0000f, 577.0000f, (byte) 15);
					break;
					case 7:
						fameRift(player, 762.0000f, 232.0000f, 541.0000f, (byte) 66);
					break;
					case 8:
						fameRift(player, 146.0000f, 136.0000f, 558.0000f, (byte) 98);
					break;
					case 9:
						fameRift(player, 321.0000f, 82.0000f, 499.0000f, (byte) 115);
					break;
					case 10:
						fameRift(player, 1429.0000f, 1745.0000f, 162.0000f, (byte) 62);
					break;
					case 11:
						fameRift(player, 822.0000f, 1047.0000f, 213.0000f, (byte) 13);
					break;
					case 12:
						fameRift(player, 1602.0000f, 1583.0000f, 168.0000f, (byte) 37);
					break;
					case 13:
						fameRift(player, 1738.0000f, 1154.0000f, 393.0000f, (byte) 20);
					break;
				}
			break;
			case 703934:
			    entryCount++;
			    if (entryCount == 24) {
					entryCount = 0;
					//LF4_Fxp_Directportal_Lv7_Start.
					killNpc(getNpcs(703934));
				} switch (Rnd.get(1, 13)) {
					case 1:
						fameRift(player, 1814.0000f, 242.0000f, 521.0000f, (byte) 10);
					break;
					case 2:
						fameRift(player, 2331.0000f, 487.0000f, 431.0000f, (byte) 15);
					break;
					case 3:
						fameRift(player, 2070.0000f, 205.0000f, 490.0000f, (byte) 31);
					break;
					case 4:
						fameRift(player, 2605.0000f, 1318.0000f, 330.0000f, (byte) 74);
					break;
					case 5:
						fameRift(player, 2240.0000f, 2092.0000f, 58.0000f, (byte) 65);
					break;
					case 6:
						fameRift(player, 191.0000f, 474.0000f, 577.0000f, (byte) 15);
					break;
					case 7:
						fameRift(player, 762.0000f, 232.0000f, 541.0000f, (byte) 66);
					break;
					case 8:
						fameRift(player, 146.0000f, 136.0000f, 558.0000f, (byte) 98);
					break;
					case 9:
						fameRift(player, 321.0000f, 82.0000f, 499.0000f, (byte) 115);
					break;
					case 10:
						fameRift(player, 1429.0000f, 1745.0000f, 162.0000f, (byte) 62);
					break;
					case 11:
						fameRift(player, 822.0000f, 1047.0000f, 213.0000f, (byte) 13);
					break;
					case 12:
						fameRift(player, 1602.0000f, 1583.0000f, 168.0000f, (byte) 37);
					break;
					case 13:
						fameRift(player, 1738.0000f, 1154.0000f, 393.0000f, (byte) 20);
					break;
				}
			break;
			case 703935:
			    entryCount++;
			    if (entryCount == 24) {
					entryCount = 0;
					//DF4_Fxp_Directportal_Lv5_Start.
					killNpc(getNpcs(703935));
				} switch (Rnd.get(1, 13)) {
					case 1:
						fameRift(player, 1814.0000f, 242.0000f, 521.0000f, (byte) 10);
					break;
					case 2:
						fameRift(player, 2331.0000f, 487.0000f, 431.0000f, (byte) 15);
					break;
					case 3:
						fameRift(player, 2070.0000f, 205.0000f, 490.0000f, (byte) 31);
					break;
					case 4:
						fameRift(player, 2605.0000f, 1318.0000f, 330.0000f, (byte) 74);
					break;
					case 5:
						fameRift(player, 2240.0000f, 2092.0000f, 58.0000f, (byte) 65);
					break;
					case 6:
						fameRift(player, 191.0000f, 474.0000f, 577.0000f, (byte) 15);
					break;
					case 7:
						fameRift(player, 762.0000f, 232.0000f, 541.0000f, (byte) 66);
					break;
					case 8:
						fameRift(player, 146.0000f, 136.0000f, 558.0000f, (byte) 98);
					break;
					case 9:
						fameRift(player, 321.0000f, 82.0000f, 499.0000f, (byte) 115);
					break;
					case 10:
						fameRift(player, 1429.0000f, 1745.0000f, 162.0000f, (byte) 62);
					break;
					case 11:
						fameRift(player, 822.0000f, 1047.0000f, 213.0000f, (byte) 13);
					break;
					case 12:
						fameRift(player, 1602.0000f, 1583.0000f, 168.0000f, (byte) 37);
					break;
					case 13:
						fameRift(player, 1738.0000f, 1154.0000f, 393.0000f, (byte) 20);
					break;
				}
			break;
			///GELKMAROS COLISEUM 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=398&page=1&categoryId=0&viewMode=thumb&size=20
			case 703921:
			case 703923:
			    despawnNpc(npc);
				switch (Rnd.get(1, 24)) {
				    case 1:
					    coliseumIn(player, 1699.0000f, 345.0000f, 330.0000f, (byte) 0);
					break;
					case 2:
						coliseumIn(player, 1704.0000f, 277.0000f, 331.0000f, (byte) 0);
					break;
					case 3:
						coliseumIn(player, 1652.0000f, 317.0000f, 330.0000f, (byte) 0);
					break;
					case 4:
						coliseumIn(player, 1633.0000f, 290.0000f, 329.0000f, (byte) 0);
					break;
					case 5:
						coliseumIn(player, 1586.0000f, 314.0000f, 336.0000f, (byte) 0);
					break;
					case 6:
						coliseumIn(player, 1607.0000f, 244.0000f, 333.0000f, (byte) 0);
					break;
					case 7:
						coliseumIn(player, 1516.0000f, 276.0000f, 331.0000f, (byte) 0);
					break;
					case 8:
						coliseumIn(player, 1477.0000f, 351.0000f, 339.0000f, (byte) 0);
					break;
					case 9:
						coliseumIn(player, 1484.0000f, 393.0000f, 332.0000f, (byte) 0);
					break;
					case 10:
						coliseumIn(player, 1529.0000f, 410.0000f, 332.0000f, (byte) 0);
					break;
					case 11:
						coliseumIn(player, 1446.0000f, 437.0000f, 332.0000f, (byte) 0);
					break;
					case 12:
						coliseumIn(player, 1483.0000f, 479.0000f, 349.0000f, (byte) 0);
					break;
					case 13:
						coliseumIn(player, 1518.0000f, 481.0000f, 332.0000f, (byte) 0);
					break;
					case 14:
						coliseumIn(player, 1517.0000f, 550.0000f, 329.0000f, (byte) 0);
					break;
					case 15:
						coliseumIn(player, 1583.0000f, 568.0000f, 339.0000f, (byte) 0);
					break;
					case 16:
						coliseumIn(player, 1582.0000f, 506.0000f, 331.0000f, (byte) 0);
					break;
					case 17:
						coliseumIn(player, 1632.0000f, 509.0000f, 332.0000f, (byte) 0);
					break;
					case 18:
						coliseumIn(player, 1557.0000f, 365.0000f, 329.0000f, (byte) 0);
					break;
					case 19:
						coliseumIn(player, 1660.0000f, 557.0000f, 331.0000f, (byte) 0);
					break;
					case 20:
						coliseumIn(player, 1690.0000f, 477.0000f, 338.0000f, (byte) 0);
					break;
					case 21:
						coliseumIn(player, 1724.0000f, 515.0000f, 331.0000f, (byte) 0);
					break;
					case 22:
						coliseumIn(player, 1734.0000f, 435.0000f, 327.0000f, (byte) 0);
					break;
					case 23:
						coliseumIn(player, 1740.0000f, 374.0000f, 332.0000f, (byte) 0);
					break;
					case 24:
						coliseumIn(player, 1706.0000f, 392.0000f, 331.0000f, (byte) 0);
					break;
				}
			break;
			///RENOWN BOSS 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=400&categoryId=16
			case 661935: //Sealed Ancient Relic.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402928, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(220070000, 661942, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(220070000, 661943, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(220070000, 661944, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(220070000, 661945, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
			case 661937: //Suspicious Bush.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402928, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(220070000, 661946, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(220070000, 661947, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(220070000, 661948, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(220070000, 661949, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
			case 661939: //Shifting Piece Of Rock.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402928, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(220070000, 661950, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(220070000, 661951, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(220070000, 661952, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(220070000, 661953, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
			case 661941: //Abyss Deep Layer Fragment.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402928, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(220070000, 661954, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(220070000, 661955, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(220070000, 661956, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(220070000, 661957, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(220070000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void fameRift(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void coliseumIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void crimsonTemple(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void vorgaltemCitadel(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void tiamarantaEyeRift(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 600040000, 1, x, y, z, h);
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
	
	private void sendMessage(final int msgId, long delay) {
        if (delay == 0) {
            this.sendMsg(msgId);
        } else {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                public void run() {
                    sendMsg(msgId);
                }
            }, delay);
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