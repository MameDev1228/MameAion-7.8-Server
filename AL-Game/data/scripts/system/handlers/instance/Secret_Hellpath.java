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

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(301631000)
public class Secret_Hellpath extends GeneralInstanceHandler
{
	private Race skillRace;
	private long instanceTime;
	private Future<?> underpathTaskA1;
	private Future<?> underpathTaskA2;
	private Future<?> underpathTaskA3;
	private Future<?> underpathTaskA4;
	private boolean isInstanceDestroyed;
	private int IDEventDefZombieVampire1;
	private int IDEventDefZombieVampire2;
	private int IDEventDefZombieVampire3;
	private Map<Integer, StaticDoor> doors;
	private final FastList<Future<?>> secretHellpathTask = FastList.newInstance();
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		sendMsg("If you enter inside the <Contaminated Basin>, you are dead!!!");
		doors = instance.getDoors();
	}
	
	@Override
    public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
		    case 703473: //The 1st Passage Control Device.
				despawnNpc(npc);
				startInstanceTask();
				startContaminedUnderPath1();
				sendMsg("[START]: Wave <1/4>");
				//Gringol the Devourer has appeared.
				sendMsgByRace(1404531, Race.PC_ALL, 5000);
				//The 1st Passage Control Device has been disabled.
				sendMsgByRace(1404504, Race.PC_ALL, 10000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 1200)); //...20 Min.
					}
				});
			break;
			case 703474: //The 2nd Passage Control Device.
			    despawnNpc(npc);
			    startContaminedUnderPath2();
				underpathTaskA1.cancel(true);
				sendMsg("[START]: Wave <2/4>");
				//Gringol the Devourer has appeared.
				sendMsgByRace(1404531, Race.PC_ALL, 5000);
				//The 2nd Passage Control Device has been disabled.
				sendMsgByRace(1404505, Race.PC_ALL, 10000);
			break;
			case 703475: //The 3rd Passage Control Device.
			    despawnNpc(npc);
			    startContaminedUnderPath3();
				underpathTaskA2.cancel(true);
				sendMsg("[START]: Wave <3/4>");
				//Gringol the Devourer has appeared.
				sendMsgByRace(1404531, Race.PC_ALL, 5000);
				//The 3rd Passage Control Device has been disabled.
				sendMsgByRace(1404506, Race.PC_ALL, 10000);
			break;
	       /**
			* 5. Kill The Final Boss Monster:
			* A total of "4 levels of control units" appear, and monsters of different characteristics come in each stage.
			* Be careful that the character dies when entering the contaminated floor.
			* Step 4 After you click on the controller, kill the dead boss monster, the body resuscitator Voodoo, and the attack will be completed.
			*/
			case 703476: //The 4th Passage Control Device.
			    despawnNpc(npc);
			    startCorpseRaiserVodoosh();
				underpathTaskA3.cancel(true);
				sendMsg("[START]: Wave <4/4>");
				//Necromancer Vodoosh has appeared.
				sendMsgByRace(1404507, Race.PC_ALL, 5000);
			break;
			case 836149: //Access To Hellpath.
				if (player.getInventory().decreaseByItemId(186000495, 1)) {
					killNpc(getNpcs(836149));
			    } else {
					//You need a key to open this door.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404524));
				}
            break;
        }
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 248502: //Cursed Daeva's Soul.
			    IDEventDefZombieVampire1++;
				if (IDEventDefZombieVampire1 == 4) {
					underpathTaskA1.cancel(true);
					despawnNpcs(instance.getNpcs(703473));
					//The 2nd Passage Control Device has appeared.
					sendMsgByRace(1404508, Race.PC_ALL, 5000);
					spawn(703474, 230.0000f, 206.0000f, 160.0000f, (byte) 30);
				}
			break;
			case 248512: //Cursed Daeva's Soul.
			    IDEventDefZombieVampire2++;
				if (IDEventDefZombieVampire2 == 4) {
					underpathTaskA2.cancel(true);
					despawnNpcs(instance.getNpcs(703474));
					//The 3rd Passage Control Device has appeared.
					sendMsgByRace(1404509, Race.PC_ALL, 5000);
					spawn(703475, 230.0000f, 206.0000f, 160.0000f, (byte) 30);
				}
			break;
			case 248522: //Cursed Daeva's Soul.
			    IDEventDefZombieVampire3++;
				if (IDEventDefZombieVampire3 == 4) {
					underpathTaskA3.cancel(true);
					despawnNpcs(instance.getNpcs(703475));
					//The 4th Passage Control Device has appeared.
					sendMsgByRace(1404510, Race.PC_ALL, 5000);
					spawn(703476, 230.0000f, 206.0000f, 160.0000f, (byte) 30);
				}
			break;
			case 248923: //Contaminated Zaif.
			    spawnPrototype();
			    //The 1st Passage will be forcibly disabled shortly. Please be ready.
				sendMsgByRace(1404511, Race.PC_ALL, 0);
				//Use the Empty Turret to summon the turret.
				sendMsgByRace(1404528, Race.PC_ALL, 5000);
				//You can gain great power by using the Powerful Daeva of Life statue.
				sendMsgByRace(1404530, Race.PC_ALL, 10000);
			    ItemService.addItem(player, 182007405, 15); //Bright Aether.
				sp(703473, 230.0000f, 206.0000f, 160.0000f, (byte) 30, 5000, 0, null); //The 1st Passage Control Device.
			break;
			case 248525: //Corpse Raiser Vodoosh.
				stopInstanceTask();
				underpathTaskA4.cancel(true);
				sendMsg("[SUCCES]: You have finished <Secret Hellpath>");
				instance.doOnAllPlayers(new Visitor<Player>() {
			        @Override
			        public void visit(Player player) {
				        if (player.isOnline()) {
						    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
					    }
				    }
			    });
			break;
		}
	}
	
	private void startContaminedUnderPath1() {
		underpathTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248495, 222.78767f, 276.12140f, 160.4131f, (byte) 89, 1000, "IDEVENT_Def_1");
				sp(248496, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248497, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248498, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248499, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248500, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 1000);
		underpathTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248501, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248502, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248503, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248504, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248495, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248496, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 30000);
		underpathTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248501, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248502, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248503, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248504, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248495, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248496, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 60000);
		underpathTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248497, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248498, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248499, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248500, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248501, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248502, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 90000);
		underpathTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248497, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248498, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248499, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248500, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248501, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248502, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 120000);
	}
	
	private void startContaminedUnderPath2() {
		underpathTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248505, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248506, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248507, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248508, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248509, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248510, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 1000);
		underpathTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248511, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248512, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248513, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248514, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248505, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248506, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 30000);
		underpathTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248511, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248512, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248513, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248514, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248505, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248506, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 60000);
		underpathTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248507, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248508, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248509, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248510, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248511, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248512, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 90000);
		underpathTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248507, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248508, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248509, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248510, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248511, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248512, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 120000);
	}
	
	private void startContaminedUnderPath3() {
		underpathTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248515, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248516, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248517, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248518, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248519, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248520, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 1000);
		underpathTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248521, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248522, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248523, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248524, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248515, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248516, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 30000);
		underpathTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248521, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248522, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248523, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248524, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248515, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248516, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 60000);
		underpathTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248517, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248518, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248519, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248520, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248521, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248522, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 90000);
		underpathTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248517, 222.78767f, 276.12140f, 160.4131f, (byte) 90, 1000, "IDEVENT_Def_1");
				sp(248518, 225.05133f, 275.86157f, 160.3114f, (byte) 89, 1500, "IDEVENT_Def_1");
				sp(248519, 227.54712f, 275.85287f, 160.3114f, (byte) 89, 2000, "IDEVENT_Def_1");
				sp(248520, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 2500, "IDEVENT_Def_1");
				sp(248521, 232.00526f, 275.83752f, 160.3114f, (byte) 89, 3000, "IDEVENT_Def_1");
				sp(248522, 234.10661f, 275.83023f, 160.3114f, (byte) 89, 3500, "IDEVENT_Def_1");
			}
		}, 120000);
	}
	
	private void startCorpseRaiserVodoosh() {
		underpathTaskA4 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(248525, 229.59123f, 275.84586f, 160.3114f, (byte) 89, 1000, "IDEVENT_Def_1");
			}
		}, 1000);
	}
	
   /**
	* 3. Installing The Turret:
	* You can install various types of turrets on an empty turret, or upgrade the installed turret using the "Aura Of Patience".
	* The turrets become increasingly stronger turrets as you upgrade.
	*/
	private void spawnPrototype() {
		sp(836050, 235.97508f, 215.58057f, 160.34032f, (byte) 30, 2000, 0, null);
		sp(836050, 231.52914f, 215.41585f, 160.28148f, (byte) 30, 2500, 0, null);
		sp(836050, 227.33563f, 215.30028f, 160.28148f, (byte) 30, 3000, 0, null);
		sp(836050, 223.48090f, 215.20303f, 160.28148f, (byte) 30, 3500, 0, null);
		sp(836050, 223.44165f, 226.83798f, 160.28148f, (byte) 31, 4000, 0, null);
		sp(836050, 227.34960f, 226.85701f, 160.28148f, (byte) 30, 4500, 0, null);
		sp(836050, 231.63043f, 226.90276f, 160.28148f, (byte) 30, 5000, 0, null);
		sp(836050, 236.04927f, 226.95053f, 160.28148f, (byte) 30, 5500, 0, null);
		sp(836050, 232.67305f, 236.96098f, 160.28148f, (byte) 30, 6000, 0, null);
		sp(836050, 226.52002f, 236.88406f, 160.28148f, (byte) 30, 6500, 0, null);
		sp(836050, 223.52928f, 247.17563f, 159.90181f, (byte) 30, 7000, 0, null);
		sp(836050, 227.39925f, 247.25684f, 159.90181f, (byte) 30, 7500, 0, null);
		sp(836050, 231.61705f, 247.26353f, 159.90181f, (byte) 22, 8000, 0, null);
		sp(836050, 236.08197f, 247.40280f, 159.90181f, (byte) 30, 8500, 0, null);
	}
	
	protected void startInstanceTask() {
		instanceTime = System.currentTimeMillis();
		secretHellpathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    failInstance(player);
						despawnNpcs(instance.getNpcs(248525)); //Corpse Raiser Vodoosh.
				    }
			    });
            }
        }, 1200000)); //...20Min
    }
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 57) {
			doors.get(57).setOpen(true);
			sendMsg("Kill <Contaminated Zaif> under stair!!!");
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
	   /**
		* 2. Start Combat:
		* When you click on the entrance, the battle begins, and after a certain time, the monster starts to gather.
		* If you kill a monster, you can acquire a 'Guardian energy', which can be used to build a turret or to strengthen your skills.
		*
		* ■ Tip 1. Let's use 'prison keys' to get items faster.
		* After the battle begins, you can use your key to open the prison door between the stairs to kill the contaminated Dog.
		* It can help you to shorten your attack time because you can acquire 50 points of 'Guardian Power' when you deal with the contaminated Dog.
		* The 'prison key' will be paid through three surveys.
		* Additional purchases can be made through a dedicated store if necessary.
		* ■ Tip 2. Let's get rid of the gangs!
		* Gold stems often appear inside the interior.
		* At the time of the treatment, you can acquire the 'Power of Suho' at random, so let's do not miss it.
		*
		* 4. Enhance Your Skills:
		* If you click on the stone statue located on the entrance side, you can strengthen your ability by using the power of guardian.
		*/
		ItemService.addItem(player, 186000495, 1); //Prison Key (Open Door Prison)
	   /**
		* 1. Transformation:
		* When entering the contaminated underground passage, it automatically transforms into a form of transfer.
		* Basically, you can use the 'Berta' skill, you cannot use your skills.
		*/
		SecretHellpath(player);
		final int secretHellpath = skillRace == Race.ASMODIANS ? 4940 : 4935;
		SkillEngine.getInstance().applyEffectDirectly(secretHellpath, player, player, 1200000 * 1);
	}
	
	public static final void SecretHellpath(final Player player) {
		player.getSkillList().addSkill(player, 4920, 1);
		player.getSkillList().addSkill(player, 4926, 1);
	}
	
	protected void failInstance(Player player) {
		stopInstanceTask();
		onInstanceDestroy();
		onExitInstance(player);
		sendMsg("[EPIC FAIL]: You Loose!!!");
	}
	
	@Override
	public void onInstanceDestroy() {
        doors.clear();
		stopInstanceTask();
		isInstanceDestroyed = true;
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = secretHellpathTask.head(), end = secretHellpathTask.tail(); (n = n.getNext()) != end;) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        secretHellpathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    spawn(npcId, x, y, z, h, entityId);
                    if (msg > 0) {
                        sendMsgByRace(msg, race, 0);
                    }
                }
            }
        }, time));
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        secretHellpathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkerId);
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                }
            }
        }, time));
    }
	
	protected void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
	}
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
        }
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
	private int getTime() {
		long result = System.currentTimeMillis() - instanceTime;
		if (result < 10000) {
			return (int) (10000 - result);
		} else if (result < 1200000) { //...20 Min.
			return (int) (1200000 - (result - 10000));
		}
		return 0;
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(182007405, storage.getItemCountByItemId(182007405)); //Bright Aether.
		storage.decreaseByItemId(186000495, storage.getItemCountByItemId(186000495)); //Key.
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(4935);
		effectController.removeEffect(4936);
		effectController.removeEffect(4937);
		effectController.removeEffect(4938);
		effectController.removeEffect(4939);
		effectController.removeEffect(4940);
		effectController.removeEffect(4941);
		effectController.removeEffect(4942);
		effectController.removeEffect(4943);
		effectController.removeEffect(4944);
		////////////////////////////////////////////
		SkillLearnService.removeSkill(player, 4920);
		SkillLearnService.removeSkill(player, 4921);
		SkillLearnService.removeSkill(player, 4922);
		SkillLearnService.removeSkill(player, 4923);
		SkillLearnService.removeSkill(player, 4924);
		SkillLearnService.removeSkill(player, 4925);
		SkillLearnService.removeSkill(player, 4926);
		SkillLearnService.removeSkill(player, 4927);
		SkillLearnService.removeSkill(player, 4928);
		SkillLearnService.removeSkill(player, 4929);
		SkillLearnService.removeSkill(player, 4930);
		SkillLearnService.removeSkill(player, 4931);
		SkillLearnService.removeSkill(player, 4932);
		SkillLearnService.removeSkill(player, 4933);
		SkillLearnService.removeSkill(player, 4934);
	}
	
	private void sendMsg(final String str) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendWhiteMessageOnCenter(player, str);
			}
		});
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
}