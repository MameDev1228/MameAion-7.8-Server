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

@WorldID(400070000)
public class Abyss_Core extends GeneralWorldHandler
{
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///Assault Relic [Divine Fortress]
			case 835759: //Elyos Interior Gate Assault Relic.
			case 835764: //Asmodians Interior Gate Assault Relic.
			case 835769: //Balaur Interior Gate Assault Relic.
			    despawnNpc(npc);
				///The magic ward was activated. Attack and movement speeds increased!
				sendMsgByRace(1403957, Race.PC_ALL, 0);
				SkillEngine.getInstance().applyEffectDirectly(18309, player, player, 120000 * 1); //Magic Ward Energy.
				if (player.getRace() == Race.ELYOS) {
					spawn(400070000, 835738, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835759, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 1995);
				} else {
					spawn(400070000, 835743, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835764, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 2100);
				}
			break;
			case 835760: //Elyos Interior Gate Assault Relic.
			case 835765: //Asmodians Interior Gate Assault Relic.
			case 835770: //Balaur Interior Gate Assault Relic.
			    despawnNpc(npc);
				///The magic ward was activated. Attack and movement speeds increased!
				sendMsgByRace(1403957, Race.PC_ALL, 0);
				SkillEngine.getInstance().applyEffectDirectly(18309, player, player, 120000 * 1); //Magic Ward Energy.
				if (player.getRace() == Race.ELYOS) {
					spawn(400070000, 835739, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835760, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 1753);
				} else {
					spawn(400070000, 835744, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835765, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 2091);
				}
			break;
			case 835761: //Elyos Interior Gate Assault Relic.
			case 835766: //Asmodians Interior Gate Assault Relic.
			case 835771: //Balaur Interior Gate Assault Relic.
			    despawnNpc(npc);
				///The magic ward was activated. Attack and movement speeds increased!
				sendMsgByRace(1403957, Race.PC_ALL, 0);
				SkillEngine.getInstance().applyEffectDirectly(18309, player, player, 120000 * 1); //Magic Ward Energy.
			    if (player.getRace() == Race.ELYOS) {
					spawn(400070000, 835740, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835761, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 1497);
				} else {
					spawn(400070000, 835745, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835766, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 2089);
				}
			break;
			case 835762: //Elyos Interior Gate Assault Relic.
			case 835767: //Asmodians Interior Gate Assault Relic.
			case 835772: //Balaur Interior Gate Assault Relic.
			    despawnNpc(npc);
				///The magic ward was activated. Attack and movement speeds increased!
				sendMsgByRace(1403957, Race.PC_ALL, 0);
				SkillEngine.getInstance().applyEffectDirectly(18309, player, player, 120000 * 1); //Magic Ward Energy.
				if (player.getRace() == Race.ELYOS) {
					spawn(400070000, 835741, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835762, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 1218);
				} else {
					spawn(400070000, 835746, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835767, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 2058);
				}
			break;
			case 835763: //Elyos Interior Gate Assault Relic.
			case 835768: //Asmodians Interior Gate Assault Relic.
			case 835773: //Balaur Interior Gate Assault Relic.
			    despawnNpc(npc);
				///The magic ward was activated. Attack and movement speeds increased!
				sendMsgByRace(1403957, Race.PC_ALL, 0);
				SkillEngine.getInstance().applyEffectDirectly(18309, player, player, 120000 * 1); //Magic Ward Energy.
				if (player.getRace() == Race.ELYOS) {
					spawn(400070000, 835742, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835763, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 176);
				} else {
					spawn(400070000, 835747, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835768, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 2041);
				}
			break;
		    ///Rift Artifact [Divine Fortress]
			case 835774: //Iluma Rift Artifact.
			    despawnNpc(npc);
				///The Asmodians have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				sendMsgByRace(1404488, Race.PC_ALL, 0);
				///The Artifact of Dimension has appeared.
				sendMsgByRace(1404492, Race.PC_ALL, 5000);
				if (player.getRace() == Race.ASMODIANS) {
					spawn(400070000, 835755, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835776, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3255);
				}
			break;
			case 835775: //Iluma Rift Artifact.
			    despawnNpc(npc);
				///The Asmodians have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				sendMsgByRace(1404488, Race.PC_ALL, 0);
				///The Artifact of Dimension has appeared.
				sendMsgByRace(1404492, Race.PC_ALL, 5000);
				if (player.getRace() == Race.ASMODIANS) {
					spawn(400070000, 835756, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835777, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3257);
				}
			break;
			case 835776: //Norsvold Rift Artifact.
			    despawnNpc(npc);
				///The Elyos have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				sendMsgByRace(1404487, Race.PC_ALL, 0);
				///The Artifact of Dimension has appeared.
				sendMsgByRace(1404492, Race.PC_ALL, 5000);
				if (player.getRace() == Race.ELYOS) {
					spawn(400070000, 835753, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835774, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3258);
				}
			break;
			case 835777: //Norsvold Rift Artifact.
			    despawnNpc(npc);
				///The Elyos have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				sendMsgByRace(1404487, Race.PC_ALL, 0);
				///The Artifact of Dimension has appeared.
				sendMsgByRace(1404492, Race.PC_ALL, 5000);
				if (player.getRace() == Race.ELYOS) {
					spawn(400070000, 835754, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835775, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3256);
				}
			break;
			case 835778: //Balaur Rift Artifact.
			    despawnNpc(npc);
				///The Artifact of Dimension has appeared.
				sendMsgByRace(1404492, Race.PC_ALL, 5000);
				if (player.getRace() == Race.ELYOS) {
					///The Elyos have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				    sendMsgByRace(1404487, Race.PC_ALL, 0);
					spawn(400070000, 835753, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835774, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3258);
				} else {
					///The Asmodians have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				    sendMsgByRace(1404488, Race.PC_ALL, 0);
					spawn(400070000, 835755, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835776, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3255);
				}
			break;
			case 835779: //Balaur Rift Artifact.
			    despawnNpc(npc);
				///The Artifact of Dimension has appeared.
				sendMsgByRace(1404492, Race.PC_ALL, 5000);
				if (player.getRace() == Race.ELYOS) {
					///The Elyos have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				    sendMsgByRace(1404487, Race.PC_ALL, 0);
					spawn(400070000, 835754, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835775, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3256);
				} else {
					///The Asmodians have captured the artifact, and energy is now being supplied to the turret connected to the artifact.
				    sendMsgByRace(1404488, Race.PC_ALL, 0);
					spawn(400070000, 835756, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
					spawn(400070000, 835777, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 3257);
				}
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			///Empty Aetheric Cannon [Divine Fortress]
			case 882253:
			    if (player.isTransformed()) {
					//You cannot use this skill while transformed.
				    sendMsgByRace(1300149, Race.PC_ALL, 0);
				    //Transformation Mode.
					sendMsgByRace(1401212, Race.PC_ALL, 3000);
				} if (player.getInventory().decreaseByItemId(186000246, 1)) { //Magic Cannonball.
					despawnNpc(npc);
					SkillEngine.getInstance().applyEffectDirectly(21517, player, player, 3600000 * 1); //Get on Antiaircraft Gun.
				} else {
					//The weapon needs auxiliary magic fuel before you can use it.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402191));
				}
			break;
			case 882254: //Asmodian Siege Cannon.
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
			///Rift Artifact [Divine Fortress]
			case 835774: //Iluma Rift Artifact.
			    despawnNpc(npc);
				//The Artifact of Dimension activates, and an Improved Tetranon of Astera appears.
				sendMsgByRace(1404493, Race.PC_ALL, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(400070000, 885004, 1526.6850f, 1487.3833f, 2330.484f, (byte) 89);
						spawn(400070000, 885008, 1531.5736f, 1485.9515f, 2330.484f, (byte) 90);
						spawn(400070000, 885009, 1526.6060f, 1481.9915f, 2330.484f, (byte) 91);
						spawn(400070000, 885010, 1521.7411f, 1485.7452f, 2330.484f, (byte) 89);
					}
				}, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(885004));
						despawnNpcs(getNpcs(885008));
						despawnNpcs(getNpcs(885009));
						despawnNpcs(getNpcs(885010));
					}
				}, 3600000); //...1H
			break;
			case 835775: //Iluma Rift Artifact.
			    despawnNpc(npc);
				//The Artifact of Dimension activates, and an Improved Tetranon of Astera appears.
				sendMsgByRace(1404493, Race.PC_ALL, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(400070000, 885004, 1526.6604f, 1638.5061f, 2330.484f, (byte) 30);
						spawn(400070000, 885014, 1521.4648f, 1640.1941f, 2330.484f, (byte) 30);
						spawn(400070000, 885015, 1526.7015f, 1642.5360f, 2330.484f, (byte) 30);
						spawn(400070000, 885016, 1531.9664f, 1640.3715f, 2330.484f, (byte) 29);
					}
				}, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(885004));
						despawnNpcs(getNpcs(885014));
						despawnNpcs(getNpcs(885015));
						despawnNpcs(getNpcs(885016));
					}
				}, 3600000); //...1H
			break;
			case 835776: //Norsvold Rift Artifact.
			    despawnNpc(npc);
				//The Artifact of Dimension activates, and an Improved Tetranon of Astera appears.
				sendMsgByRace(1404493, Race.PC_ALL, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(400070000, 885005, 1526.6850f, 1487.3833f, 2330.484f, (byte) 89);
						spawn(400070000, 885011, 1531.5736f, 1485.9515f, 2330.484f, (byte) 90);
						spawn(400070000, 885012, 1526.6060f, 1481.9915f, 2330.484f, (byte) 91);
						spawn(400070000, 885013, 1521.7411f, 1485.7452f, 2330.484f, (byte) 89);
					}
				}, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(885005));
						despawnNpcs(getNpcs(885011));
						despawnNpcs(getNpcs(885012));
						despawnNpcs(getNpcs(885013));
					}
				}, 3600000); //...1H
			break;
			case 835777: //Norsvold Rift Artifact.
			    despawnNpc(npc);
				//The Artifact of Dimension activates, and an Improved Tetranon of Astera appears.
				sendMsgByRace(1404493, Race.PC_ALL, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(400070000, 885005, 1526.6604f, 1638.5061f, 2330.484f, (byte) 30);
						spawn(400070000, 885017, 1521.4648f, 1640.1941f, 2330.484f, (byte) 30);
						spawn(400070000, 885018, 1526.7015f, 1642.5360f, 2330.484f, (byte) 30);
						spawn(400070000, 885019, 1531.9664f, 1640.3715f, 2330.484f, (byte) 29);
					}
				}, 10000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						despawnNpcs(getNpcs(885005));
						despawnNpcs(getNpcs(885017));
						despawnNpcs(getNpcs(885018));
						despawnNpcs(getNpcs(885019));
					}
				}, 3600000); //...1H
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
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