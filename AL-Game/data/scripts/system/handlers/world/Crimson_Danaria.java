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
import com.aionemu.gameserver.services.ClassChangeService;
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

@WorldID(800040000)
public class Crimson_Danaria extends GeneralWorldHandler
{
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///BASE 81
			case 858741: //Commander Lysander.
			case 858742: //Commander Granir.
			case 858743: //Grand Commander Pashid.
				if (player.getRace() == Race.ELYOS) {
					///The Elyos have occupied Base 81. They will occupy it completely in 5 minutes.
					sendMsgByRace(1405872, Race.PC_ALL, 0);
					///The Elyos have succeeded in occupying Base 81.
					sendMsgByRace(1405874, Race.PC_ALL, 10000);
					ClassChangeService.onUpdateQuest63881(player);
					ConquestService.getInstance().startConquest(19);
				} else {
					///The Asmodians have occupied Base 81. They will occupy it completely in 5 minutes.
					sendMsgByRace(1405873, Race.PC_ALL, 0);
					///The Asmodians have succeeded in occupying Base 81.
					sendMsgByRace(1405875, Race.PC_ALL, 10000);
					ClassChangeService.onUpdateQuest73881(player);
					ConquestService.getInstance().startConquest(19);
				}
			break;
			///PANDARUNERK'S DELVE 7.x
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			//Empty Aetheric Cannon [Silona/Pradeth]
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
			//Empty Aetheric Cannon [Silona/Pradeth]
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
		   /**
			* Pradeth Fortress.
			*/
			case 701783: ///Northwestern Outer Gate Control.
				//The control device has opened the Outer Castle's Northwest gate.
				sendMsgByRace(1401690, Race.PC_ALL, 0);
				killNpc(getNpcs(273285)); //Northwestern Outer Gate.
			break;
			case 701785: ///Northwestern Outer Gate Control.
				//The control device has opened the Outer Castle's Northwest gate.
				sendMsgByRace(1401690, Race.PC_ALL, 0);
				killNpc(getNpcs(273286)); //Northwestern Outer Gate.
			break;
			case 701784: ///SouthEastern Outer Gate Control.
				//The control device has opened the Outer Castle's Southwestern gate.
				sendMsgByRace(1401691, Race.PC_ALL, 0);
				killNpc(getNpcs(273288)); //Southwestern Outer Gate.
			break;
			case 701786: ///SouthEastern Outer Gate Control.
				//The control device has opened the Outer Castle's Southwestern gate.
				sendMsgByRace(1401691, Race.PC_ALL, 0);
				killNpc(getNpcs(273289)); //Southwestern Outer Gate.
			break;
			case 701705: ///North Moat Bomb Control.
			    despawnNpc(npc);
			    //The North Moat Bomb's Control Device has activated.
				sendMsgByRace(1401694, Race.PC_ALL, 0);
				spawn(800040000, 855240, 2494.1760f, 2687.2861f, 252.8750f, (byte) 106);
				spawn(800040000, 855240, 2501.7883f, 2680.2737f, 252.7671f, (byte) 106);
				spawn(800040000, 855240, 2510.7224f, 2672.4260f, 252.8750f, (byte) 106);
				spawn(800040000, 855240, 2523.2580f, 2661.7732f, 252.7641f, (byte) 46);
				spawn(800040000, 855240, 2535.7030f, 2650.0918f, 252.8750f, (byte) 46);
				spawn(800040000, 855240, 2546.6968f, 2641.3380f, 252.8750f, (byte) 107);
				spawn(800040000, 855240, 2560.6216f, 2629.3538f, 252.7500f, (byte) 106);
				spawn(800040000, 855240, 2565.8496f, 2614.1780f, 252.7500f, (byte) 97);
				spawn(800040000, 855240, 2585.5066f, 2594.7961f, 252.7500f, (byte) 106);
				spawn(800040000, 855240, 2598.4710f, 2583.3218f, 252.7500f, (byte) 106);
				spawn(800040000, 855240, 2612.6497f, 2579.8354f, 252.7803f, (byte) 115);
				spawn(800040000, 855240, 2626.3390f, 2567.2070f, 252.8254f, (byte) 106);
				spawn(800040000, 855240, 2640.6826f, 2554.6885f, 252.7893f, (byte) 47);
				spawn(800040000, 855240, 2653.5703f, 2541.3790f, 252.8481f, (byte) 46);
				spawn(800040000, 855240, 2664.9575f, 2530.1630f, 252.8750f, (byte) 106);
			break;
			case 701706: ///West Moat Bomb Control.
			    despawnNpc(npc);
			    //The West Moat Bomb's Control Device has activated.
				sendMsgByRace(1401695, Race.PC_ALL, 0);
				spawn(800040000, 855240, 2822.8450f, 2685.1280f, 252.8750f, (byte) 74);
				spawn(800040000, 855240, 2809.4937f, 2676.7913f, 252.8750f, (byte) 12);
				spawn(800040000, 855240, 2798.1877f, 2665.6934f, 252.8750f, (byte) 15);
				spawn(800040000, 855240, 2784.0671f, 2660.3855f, 252.8750f, (byte) 99);
				spawn(800040000, 855240, 2777.4883f, 2646.2837f, 252.8750f, (byte) 16);
				spawn(800040000, 855240, 2765.1326f, 2638.0457f, 252.8750f, (byte) 17);
				spawn(800040000, 855240, 2761.9373f, 2625.2600f, 252.8750f, (byte) 24);
				spawn(800040000, 855240, 2748.2893f, 2616.3164f, 252.8750f, (byte) 16);
				spawn(800040000, 855240, 2736.7642f, 2605.3000f, 252.8750f, (byte) 16);
				spawn(800040000, 855240, 2728.3894f, 2591.5332f, 253.0000f, (byte) 18);
				spawn(800040000, 855240, 2711.1270f, 2576.4705f, 253.0000f, (byte) 17);
				spawn(800040000, 855240, 2693.2600f, 2551.6821f, 252.8750f, (byte) 16);
				spawn(800040000, 855240, 2679.8628f, 2536.2432f, 252.8750f, (byte) 15);
				spawn(800040000, 855240, 2669.4380f, 2528.0710f, 252.8750f, (byte) 32);
			break;
			case 701707: ///South Moat Bomb Control.
			    despawnNpc(npc);
			    //The South Moat Bomb's Control Device has activated.
				sendMsgByRace(1401696, Race.PC_ALL, 0);
				spawn(800040000, 855240, 2652.7102f, 2857.2102f, 252.9506f, (byte) 46);
				spawn(800040000, 855240, 2664.8152f, 2845.6492f, 252.8750f, (byte) 104);
				spawn(800040000, 855240, 2675.0032f, 2835.5747f, 252.8750f, (byte) 45);
				spawn(800040000, 855240, 2695.5261f, 2815.1968f, 252.8750f, (byte) 105);
				spawn(800040000, 855240, 2717.8296f, 2796.6892f, 252.7500f, (byte) 105);
				spawn(800040000, 855240, 2733.2925f, 2790.9680f, 252.7500f, (byte) 52);
				spawn(800040000, 855240, 2749.6555f, 2776.1606f, 252.6465f, (byte) 45);
				spawn(800040000, 855240, 2757.1865f, 2758.7358f, 252.5790f, (byte) 36);
				spawn(800040000, 855240, 2777.0698f, 2738.7676f, 252.8750f, (byte) 47);
				spawn(800040000, 855240, 2798.9446f, 2719.9326f, 252.8750f, (byte) 47);
				spawn(800040000, 855240, 2812.9160f, 2712.7422f, 252.8750f, (byte) 106);
				spawn(800040000, 855240, 2825.3154f, 2700.8780f, 252.8750f, (byte) 48);
			break;
			case 701708: ///East Moat Bomb Control.
			    despawnNpc(npc);
			    //The East Moat Bomb's Control Device has activated.
				sendMsgByRace(1401697, Race.PC_ALL, 0);
				spawn(800040000, 855240, 2640.7480f, 2856.7354f, 252.7950f, (byte) 77);
				spawn(800040000, 855240, 2632.6213f, 2847.3916f, 252.8750f, (byte) 76);
				spawn(800040000, 855240, 2626.1797f, 2834.4420f, 252.8750f, (byte) 92);
				spawn(800040000, 855240, 2613.7798f, 2824.0781f, 252.8750f, (byte) 78);
				spawn(800040000, 855240, 2602.1028f, 2811.4563f, 252.8750f, (byte) 16);
				spawn(800040000, 855240, 2590.0100f, 2798.7450f, 252.8750f, (byte) 75);
				spawn(800040000, 855240, 2580.6177f, 2780.9450f, 252.8750f, (byte) 82);
				spawn(800040000, 855240, 2566.7217f, 2764.6960f, 252.8750f, (byte) 16);
				spawn(800040000, 855240, 2547.8830f, 2765.1400f, 252.8750f, (byte) 106);
				spawn(800040000, 855240, 2544.3672f, 2752.4856f, 252.8750f, (byte) 83);
				spawn(800040000, 855240, 2535.2390f, 2739.7485f, 252.8750f, (byte) 78);
				spawn(800040000, 855240, 2519.2780f, 2724.8440f, 252.8750f, (byte) 76);
				spawn(800040000, 855240, 2504.1357f, 2708.9310f, 252.8750f, (byte) 15);
				spawn(800040000, 855240, 2494.5742f, 2699.0913f, 252.8750f, (byte) 76);
			break;
		}
	}
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
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