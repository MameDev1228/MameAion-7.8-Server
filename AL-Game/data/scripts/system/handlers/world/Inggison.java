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

@WorldID(210050000)
public class Inggison extends GeneralWorldHandler
{
	private int entryCount;
	private int coliseumMobs;
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Creature creature = (Creature) npc.getTarget();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///BOSS WINDSTREAM.
			case 650537: //Titan Starturtle.
				//Ancient Windstream is temporarily activated.
				sendMsgByRace(1401331, Race.PC_ALL, 0);
				spawn(210050000, 281817, 338.0000f, 573.0000f, 458.0000f, (byte) 0, 755);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(281817));
					}
				}, 300000); //...5 Min
			break;
			case 650920: //Watcher Garma.
				//Ancient Windstream is temporarily activated.
				sendMsgByRace(1401331, Race.PC_ALL, 0);
				spawn(210050000, 281817, 2602.0000f, 1526.0000f, 258.0000f, (byte) 0, 754);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(281817));
					}
				}, 300000); //...5 Min
			break;
			case 650859: //Illanthe Hundredyears.
				//Ancient Windstream is temporarily activated.
				sendMsgByRace(1401331, Race.PC_ALL, 0);
				spawn(210050000, 281817, 1745.0000f, 1716.0000f, 226.0000f, (byte) 0, 1039);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(281817));
					}
				}, 300000); //...5 Min
			break;
			case 650874: //Esalki The Fourth.
				//Ancient Windstream is temporarily activated.
				sendMsgByRace(1401331, Race.PC_ALL, 0);
				spawn(210050000, 281817, 2288.0000f, 1067.0000f, 285.0000f, (byte) 0, 2311);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(281817));
					}
				}, 300000); //...5 Min
			break;
			case 650875: //Huge Waterfall Starturtle.
				//Ancient Windstream is temporarily activated.
				sendMsgByRace(1401331, Race.PC_ALL, 0);
				spawn(210050000, 281817, 1660.0000f, 928.0000f, 404.0000f, (byte) 0, 2292);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(281817));
					}
				}, 300000); //...5 Min
			break;
			///HUGE EGG.
			case 650881:
			    despawnNpc(npc);
				spawn(210050000, 217097, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Lightwing Coiren.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Lightwing Coiren> appear!!!");
			break;
			///INGGISON GARRISON 7.x
			case 661745: //The 1st Garrison Occupier Combat Officer.
				//You have occupied the Wisplight Legion's 1st Garrison.
				sendMsgByRace(1406038, Race.PC_ALL, 2000);
			break;
			case 661755: //The 2nd Garrison Occupier Combat Officer.
				//You have occupied the Wisplight Legion's 2nd Garrison.
				sendMsgByRace(1406042, Race.PC_ALL, 2000);
			break;
			case 661765: //The 3rd Garrison Occupier Combat Officer.
				//You have occupied the Wisplight Legion's 3rd Garrison.
				sendMsgByRace(1406046, Race.PC_ALL, 2000);
			break;
			case 661775: //The 4th Garrison Occupier Combat Officer.
				//You have occupied the Wisplight Legion's 4th Garrison.
				sendMsgByRace(1406050, Race.PC_ALL, 2000);
			break;
			case 661785: //The 5th Garrison Occupier Combat Officer.
				//You have occupied the Wisplight Legion's 5th Garrison.
				sendMsgByRace(1406054, Race.PC_ALL, 2000);
			break;
			case 661751: //The 1st Garrison Combat Officer.
				//The Wisplight Legion's 1st Garrison has been destroyed.
				sendMsgByRace(1406040, Race.PC_ALL, 2000);
			break;
			case 661761: //The 2nd Garrison Combat Officer.
				//The Wisplight Legion's 2nd Garrison has been destroyed.
				sendMsgByRace(1406044, Race.PC_ALL, 2000);
			break;
			case 661771: //The 3rd Garrison Combat Officer.
				//The Wisplight Legion's 3rd Garrison has been destroyed.
				sendMsgByRace(1406048, Race.PC_ALL, 2000);
			break;
			case 661781: //The 4th Garrison Combat Officer.
				//The Wisplight Legion's 4th Garrison has been destroyed.
				sendMsgByRace(1406052, Race.PC_ALL, 2000);
			break;
			case 661791: //The 5th Garrison Combat Officer.
				//The Wisplight Legion's 5th Garrison has been destroyed.
				sendMsgByRace(1406056, Race.PC_ALL, 2000);
			break;
			///BRAHMA DREDGION 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=349&categoryId=16
			case 661729: //Captain Taraz.
				//The Dredgion was destroyed.
				sendMsgByRace(1403958, Race.PC_ALL, 0);
				ConquestService.getInstance().stopConquest(3);
				//The Brahma Dredgion is about to explode! Hurry and escape!
				sendMsgByRace(1406156, Race.PC_ALL, 5000);
				//The Brahma Dredgion has retreated.
				sendMsgByRace(1406035, Race.PC_ALL, 15000);
				spawn(210050000, 662913, 784.3390f, 506.1123f, 2058.9500f, (byte) 59);
			break;
			case 661732: //Captain Kabir.
			    //The Dredgion was destroyed.
				sendMsgByRace(1403958, Race.PC_ALL, 0);
			    ConquestService.getInstance().stopConquest(3);
			    //The Brahma Dredgion is about to explode! Hurry and escape!
				sendMsgByRace(1406156, Race.PC_ALL, 5000);
				//The Brahma Dredgion has retreated.
				sendMsgByRace(1406036, Race.PC_ALL, 15000);
				spawn(210050000, 662914, 784.3390f, 1506.1123f, 2058.9500f, (byte) 59);
			break;
			case 661735: //Captain Narshik.
			    //The Dredgion was destroyed.
				sendMsgByRace(1403958, Race.PC_ALL, 0);
			    ConquestService.getInstance().stopConquest(3);
			    //The Brahma Dredgion is about to explode! Hurry and escape!
				sendMsgByRace(1406156, Race.PC_ALL, 5000);
				//The Brahma Dredgion has retreated.
				sendMsgByRace(1406037, Race.PC_ALL, 15000);
				spawn(210050000, 662915, 784.3390f, 2506.1123f, 2058.9500f, (byte) 59);
			break;
			///KASARATH SKY ASSAULTER 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=349&page=2&categoryId=0&viewMode=thumb&size=20
			case 661674:
			case 661675:
			case 661676:
			case 661677:
			case 661678:
			case 661679:
			case 661692:
			    //A passage to the Brahma Dredgion has opened.
				sendMsgByRace(1406032, Race.PC_ALL, 2000);
			    spawn(210050000, 703908, npc.getX(), npc.getY(), npc.getZ() + 2, (byte) 0); //Dredgion Entrance.
			break;
			case 661680:
			case 661681:
			case 661682:
			case 661683:
			case 661684:
			case 661685:
				//A passage to the Brahma Dredgion has opened.
				sendMsgByRace(1406033, Race.PC_ALL, 2000);
				spawn(210050000, 703909, npc.getX(), npc.getY(), npc.getZ() + 2, (byte) 0); //Dredgion Entrance.
			break;
			case 661686:
			case 661687:
			case 661688:
			case 661689:
			case 661690:
			case 661691:
			case 661693:
				//A passage to the Brahma Dredgion has opened.
				sendMsgByRace(1406034, Race.PC_ALL, 2000);
				spawn(210050000, 703910, npc.getX(), npc.getY(), npc.getZ() + 2, (byte) 0); //Dredgion Entrance.
			break;
            default:
			    ///INGGISON COLISEUM 7.x
			    ///https://aion.plaync.com/board/aionreport/view?articleId=398&page=1&categoryId=0&viewMode=thumb&size=20
			    if (npc.getTarget() instanceof Player) {
					coliseumMobs++;
					if (coliseumMobs == 200) {
						coliseumMobs = 0;
						inggisonArena();
						//Inggison Arena entrance has opened.
						sendMsgByRace(1406102, Race.PC_ALL, 0);
						if (player.getRace() == Race.ELYOS) {
							spawn(210050000, 703923, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Secret Rift.
							spawn(210050000, 839272, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Elyos Dimensional Rift.
						} else {
							spawn(210050000, 703921, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Secret Rift.
							spawn(210050000, 839273, player.getX(), player.getY(), player.getZ() + 2, (byte) 0); //Asmodian Dimensional Rift.
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
	
	protected void inggisonArena() {
		//Inggison Arena battle will begin in 5 minutes.
		sendMsgByRace(1406103, Race.PC_ALL, 10000);
		//Inggison Arena battle will begin in 4 minutes.
		this.sendMessage(1406104, 1 * 60 * 1000);
		//Inggison Arena battle will begin in 3 minutes.
		this.sendMessage(1406105, 2 * 60 * 1000);
		//Inggison Arena battle will begin in 2 minutes.
		this.sendMessage(1406106, 3 * 60 * 1000);
		//Inggison Arena battle will begin in 1 minute.
		this.sendMessage(1406107, 4 * 60 * 1000);
		//Inggison Arena battle has begun.
		this.sendMessage(1406108, 5 * 60 * 1000);
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			///EMPTY AETHERIC CANNON [Inggison]
			case 882253:
			    if (player.isTransformed()) {
					//You cannot use this skill while transformed.
				    sendMsgByRace(1300149, Race.PC_ALL, 0);
				    //Transformation Mode.
					sendMsgByRace(1401212, Race.PC_ALL, 3000);
				} if (player.getInventory().decreaseByItemId(186000246, 1)) { //Magic Cannonball.
					despawnNpc(npc);
					SkillEngine.getInstance().applyEffectDirectly(21517, player, player, 3600000 * 1); //Get On Antiaircraft Gun.
			    } else {
					//The weapon needs auxiliary magic fuel before you can use it.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402191));
				}
			break;
			case 836441: //Rift To Tiamaranta's Eye.
			    if (player.getLevel() >= 76 && player.getLevel() <= 80) {
				    tiamarantaEyeRift(player, 754.0000f, 196.0000f, 1201.0000f, (byte) 91);
                } else {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_DIRECT_PORTAL_LEVEL_LIMIT);
				}
			break;
			///TELEPORTATION TOWER [Temple Of Scales]
			case 257233:
			case 257234:
			    templeOfScales(player, 1733.0000f, 2215.0000f, 328.0000f, (byte) 96);
			break;
			///TELEPORTATION TOWER [Altar Of Avarice]
			case 257533:
			case 257534:
			    altarOfAvarice(player, 875.0000f, 1958.0000f, 340.0000f, (byte) 86);
			break;
			///HIDE BOSS [Inggison]
			case 700759: //Pluma Bait.
				despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 650733, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sobi Alula Lunini.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Sobi Alula Lunini> appear!!!");
			break;
			case 700760: //Spiritcaller Incense Burner.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 650864, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Fraia.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Fraia> appear!!!");
			break;
			case 700761: //Half-Rotten Animal Carcass.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216614, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Twister.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Twister> appear!!!");
			break;
			case 700762: //Researcher's Equipment.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216615, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Valkinas The Scholar.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Valkinas The Scholar> appear!!!");
			break;
			case 700763: //Acid Canister.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216620, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Crimsonbark.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Crimsonbark> appear!!!");
			break;
			case 700764: //Tricorn Food.
			    despawnNpc(npc);
				respawnNpc(npc);
				spawn(210050000, 216621, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sweetsteppe.
				PacketSendUtility.sendSys3Message(player, "\uE005", "Boss <Sweetsteppe> appear!!!");
			break;
			///INGGISON RENOWN RIFT 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=348&page=3&categoryId=0&viewMode=thumb&size=20
			case 703929:
			    entryCount++;
			    if (entryCount == 96) {
					entryCount = 0;
					//LF4_Fxp_Directportal_Lv9_Start.
					killNpc(getNpcs(703929));
				} switch (Rnd.get(1, 13)) {
					case 1:
						fameRift(player, 519.0000f, 1843.0000f, 362.0000f, (byte) 86);
					break;
					case 2:
						fameRift(player, 304.0000f, 1757.0000f, 353.0000f, (byte) 33);
					break;
					case 3:
						fameRift(player, 804.0000f, 1981.0000f, 326.0000f, (byte) 90);
					break;
					case 4:
						fameRift(player, 907.0000f, 1383.0000f, 51.0000f, (byte) 3);
					break;
					case 5:
						fameRift(player, 740.0000f, 1366.0000f, 277.0000f, (byte) 90);
					break;
					case 6:
						fameRift(player, 451.0000f, 1453.0000f, 283.0000f, (byte) 101);
					break;
					case 7:
						fameRift(player, 1103.0000f, 1272.0000f, 281.0000f, (byte) 108);
					break;
					case 8:
						fameRift(player, 571.0000f, 1541.0000f, 277.0000f, (byte) 82);
					break;
					case 9:
						fameRift(player, 2351.0000f, 673.0000f, 142.0000f, (byte) 115);
					break;
					case 10:
						fameRift(player, 2656.0000f, 666.0000f, 141.0000f, (byte) 62);
					break;
					case 11:
						fameRift(player, 2179.0000f, 1163.0000f, 206.0000f, (byte) 13);
					break;
					case 12:
						fameRift(player, 2118.0000f, 1099.0000f, 300.0000f, (byte) 37);
					break;
					case 13:
						fameRift(player, 391.0000f, 1987.0000f, 423.0000f, (byte) 82);
					break;
				}
			break;
			case 703930:
			    entryCount++;
			    if (entryCount == 24) {
					entryCount = 0;
					//LF4_Fxp_Directportal_Lv7_Start.
					killNpc(getNpcs(703930));
				} switch (Rnd.get(1, 13)) {
					case 1:
						fameRift(player, 519.0000f, 1843.0000f, 362.0000f, (byte) 86);
					break;
					case 2:
						fameRift(player, 304.0000f, 1757.0000f, 353.0000f, (byte) 33);
					break;
					case 3:
						fameRift(player, 804.0000f, 1981.0000f, 326.0000f, (byte) 90);
					break;
					case 4:
						fameRift(player, 907.0000f, 1383.0000f, 51.0000f, (byte) 3);
					break;
					case 5:
						fameRift(player, 740.0000f, 1366.0000f, 277.0000f, (byte) 90);
					break;
					case 6:
						fameRift(player, 451.0000f, 1453.0000f, 283.0000f, (byte) 101);
					break;
					case 7:
						fameRift(player, 1103.0000f, 1272.0000f, 281.0000f, (byte) 108);
					break;
					case 8:
						fameRift(player, 571.0000f, 1541.0000f, 277.0000f, (byte) 82);
					break;
					case 9:
						fameRift(player, 2351.0000f, 673.0000f, 142.0000f, (byte) 115);
					break;
					case 10:
						fameRift(player, 2656.0000f, 666.0000f, 141.0000f, (byte) 62);
					break;
					case 11:
						fameRift(player, 2179.0000f, 1163.0000f, 206.0000f, (byte) 13);
					break;
					case 12:
						fameRift(player, 2118.0000f, 1099.0000f, 300.0000f, (byte) 37);
					break;
					case 13:
						fameRift(player, 391.0000f, 1987.0000f, 423.0000f, (byte) 82);
					break;
				}
			break;
			case 703931:
			    entryCount++;
			    if (entryCount == 24) {
					entryCount = 0;
					//LF4_Fxp_Directportal_Lv5_Start.
					killNpc(getNpcs(703931));
				} switch (Rnd.get(1, 13)) {
					case 1:
						fameRift(player, 519.0000f, 1843.0000f, 362.0000f, (byte) 86);
					break;
					case 2:
						fameRift(player, 304.0000f, 1757.0000f, 353.0000f, (byte) 33);
					break;
					case 3:
						fameRift(player, 804.0000f, 1981.0000f, 326.0000f, (byte) 90);
					break;
					case 4:
						fameRift(player, 907.0000f, 1383.0000f, 51.0000f, (byte) 3);
					break;
					case 5:
						fameRift(player, 740.0000f, 1366.0000f, 277.0000f, (byte) 90);
					break;
					case 6:
						fameRift(player, 451.0000f, 1453.0000f, 283.0000f, (byte) 101);
					break;
					case 7:
						fameRift(player, 1103.0000f, 1272.0000f, 281.0000f, (byte) 108);
					break;
					case 8:
						fameRift(player, 571.0000f, 1541.0000f, 277.0000f, (byte) 82);
					break;
					case 9:
						fameRift(player, 2351.0000f, 673.0000f, 142.0000f, (byte) 115);
					break;
					case 10:
						fameRift(player, 2656.0000f, 666.0000f, 141.0000f, (byte) 62);
					break;
					case 11:
						fameRift(player, 2179.0000f, 1163.0000f, 206.0000f, (byte) 13);
					break;
					case 12:
						fameRift(player, 2118.0000f, 1099.0000f, 300.0000f, (byte) 37);
					break;
					case 13:
						fameRift(player, 391.0000f, 1987.0000f, 423.0000f, (byte) 82);
					break;
				}
			break;
			///INGGISON COLISEUM 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=398&page=1&categoryId=0&viewMode=thumb&size=20
			case 703921:
			case 703923:
			    despawnNpc(npc);
				switch (Rnd.get(1, 24)) {
				    case 1:
						coliseumIn(player, 749.0000f, 2690.0000f, 331.0000f, (byte) 0);
					break;
					case 2:
						coliseumIn(player, 811.0000f, 2723.0000f, 331.0000f, (byte) 0);
					break;
					case 3:
						coliseumIn(player, 846.0000f, 2652.0000f, 331.0000f, (byte) 0);
					break;
					case 4:
						coliseumIn(player, 897.0000f, 2597.0000f, 328.0000f, (byte) 0);
					break;
					case 5:
						coliseumIn(player, 827.0000f, 2584.0000f, 327.0000f, (byte) 0);
					break;
					case 6:
						coliseumIn(player, 864.0000f, 2534.0000f, 332.0000f, (byte) 0);
					break;
					case 7:
						coliseumIn(player, 827.0000f, 2518.0000f, 330.0000f, (byte) 0);
					break;
					case 8:
						coliseumIn(player, 867.0000f, 2472.0000f, 331.0000f, (byte) 0);
					break;
					case 9:
						coliseumIn(player, 818.0000f, 2452.0000f, 333.0000f, (byte) 0);
					break;
					case 10:
						coliseumIn(player, 772.0000f, 2504.0000f, 329.0000f, (byte) 0);
					break;
					case 11:
						coliseumIn(player, 757.0000f, 2439.0000f, 329.0000f, (byte) 0);
					break;
					case 12:
						coliseumIn(player, 704.0000f, 2401.0000f, 332.0000f, (byte) 0);
					break;
					case 13:
						coliseumIn(player, 709.0000f, 2473.0000f, 336.0000f, (byte) 0);
					break;
					case 14:
						coliseumIn(player, 651.0000f, 2479.0000f, 332.0000f, (byte) 0);
					break;
					case 15:
						coliseumIn(player, 599.0000f, 2502.0000f, 339.0000f, (byte) 0);
					break;
					case 16:
						coliseumIn(player, 576.0000f, 2589.0000f, 332.0000f, (byte) 0);
					break;
					case 17:
						coliseumIn(player, 647.0000f, 2571.0000f, 332.0000f, (byte) 0);
					break;
					case 18:
						coliseumIn(player, 668.0000f, 2601.0000f, 331.0000f, (byte) 0);
					break;
					case 19:
						coliseumIn(player, 633.0000f, 2635.0000f, 332.0000f, (byte) 0);
					break;
					case 20:
						coliseumIn(player, 579.0000f, 2665.0000f, 332.0000f, (byte) 0);
					break;
					case 21:
						coliseumIn(player, 697.0000f, 2640.0000f, 328.0000f, (byte) 0);
					break;
					case 22:
						coliseumIn(player, 645.0000f, 2709.0000f, 328.0000f, (byte) 0);
					break;
					case 23:
						coliseumIn(player, 702.0000f, 2719.0000f, 338.0000f, (byte) 0);
					break;
					case 24:
						coliseumIn(player, 773.0000f, 2631.0000f, 329.0000f, (byte) 0);
				    break;
				}
			break;
			///RENOWN BOSS 7.x
			///https://aion.plaync.com/board/aionreport/view?articleId=400&categoryId=16
			case 661633: //Sealed Ancient Relic.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402929, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(210050000, 661640, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(210050000, 661641, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(210050000, 661642, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(210050000, 661643, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
			case 661635: //Suspicious Bush.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402929, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(210050000, 661644, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(210050000, 661645, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(210050000, 661646, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(210050000, 661647, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
			case 661637: //Shifting Piece Of Rock.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402929, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(210050000, 661648, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(210050000, 661649, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(210050000, 661650, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(210050000, 661651, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
			case 661639: //Abyss Deep Layer Fragment.
			    despawnNpc(npc);
				respawnNpc(npc);
				//A rare monster appeared.
				sendMsgByRace(1402929, Race.PC_ALL, 0);
				switch (Rnd.get(1, 4)) {
				    case 1:
						spawn(210050000, 661652, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 2:
						spawn(210050000, 661653, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 3:
						spawn(210050000, 661654, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
					case 4:
						spawn(210050000, 661655, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						spawn(210050000, 282786, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					break;
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void fameRift(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
	}
	protected void coliseumIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void templeOfScales(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	protected void altarOfAvarice(Player player, float x, float y, float z, byte h) {
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