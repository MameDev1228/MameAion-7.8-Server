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
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
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

@InstanceID(302810000)
public class Altar_Of_Ascension extends GeneralInstanceHandler
{
	private Race spawnRace;
	private Map<Integer, StaticDoor> doors;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 656830: //Smuggler Shukirukin.
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
			case 840268: //Mortasha Cubic Crystal.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188073376, 1));
					}
				}
			break;
        }
    }
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (spawnRace == null) {
			spawnRace = player.getRace();
			altarOfAscensionRace();
		}
    }
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		//You sense a mysterious power of healing in the Spirits' Haven.
		sendMsgByRace(1405984, Race.PC_ALL, 7000);
		//Yanohas has awakened from slumber.
		sendMsgByRace(1405985, Race.PC_ALL, 12000);
		//Complete the raid as quickly as possible to obtain an additional Altar of Ascension (Normal) Reward Box.
		sendMsgByRace(1405939, Race.PC_ALL, 17000);
		spawn(858784, 2876.2680f, 2261.586f, 720.6205f, (byte) 105); //Weakened Mortasha.
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		switch (Rnd.get(1, 6)) {
			case 1:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 30000);
				spawn(656830, 2250.3306f, 2018.016f, 737.3909f, (byte) 112);
			break;
			case 2:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 30000);
				spawn(656830, 2278.0552f, 1955.4362f, 737.3958f, (byte) 18);
			break;
			case 3:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 30000);
				spawn(656830, 2319.767f, 1954.7932f, 737.394f, (byte) 42);
			break;
			case 4:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 30000);
				spawn(656830, 2331.3083f, 2004.8306f, 737.3943f, (byte) 70);
			break;
			case 5:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 30000);
				spawn(656830, 2279.8596f, 2031.3428f, 737.393f, (byte) 98);
			break;
			case 6:
			break;
		}
    }
	
	private void altarOfAscensionRace() {
		//Artifact.
		spawn(840206, 2480.0000f, 711.0000f, 791.0000f, (byte) 0, 25);
		spawn(840206, 2552.0000f, 960.0000f, 786.0000f, (byte) 0, 31);
		spawn(840206, 2718.0000f, 1157.000f, 776.0000f, (byte) 0, 84);
		//Tracker Advance Unit Commander.
		final int trackerAdvanceUnitCommander1 = spawnRace == Race.ASMODIANS ? 840278 : 840277;
		spawn(trackerAdvanceUnitCommander1, 2290.2905f, 2248.5833f, 721.5339f, (byte) 114);
		//Tracker Advance Unit Commander.
		final int trackerAdvanceUnitCommander2 = spawnRace == Race.ASMODIANS ? 840402 : 840401;
		spawn(trackerAdvanceUnitCommander2, 2896.1328f, 2239.0070f, 723.6730f, (byte) 117);
		//Tracker Advance Unit Combatant.
		final int trackerAdvanceUnitCombatant1 = spawnRace == Race.ASMODIANS ? 662157 : 662145;
		spawn(trackerAdvanceUnitCombatant1, 2888.7020f, 2243.6545f, 720.37665f, (byte) 46);
        spawn(trackerAdvanceUnitCombatant1, 2895.2160f, 2250.5710f, 720.37665f, (byte) 46);
		//Tracker Advance Unit Combatant.
		final int trackerAdvanceUnitCombatant2 = spawnRace == Race.ASMODIANS ? 662159 : 662147;
		spawn(trackerAdvanceUnitCombatant2, 2889.7292f, 2261.8167f, 720.7295f, (byte) 62);
        spawn(trackerAdvanceUnitCombatant2, 2863.0273f, 2260.9084f, 720.7247f, (byte) 1);
		//Tracker Advance Unit Sniper.
		final int trackerAdvanceUnitSniper = spawnRace == Race.ASMODIANS ? 662163 : 662151;
		spawn(trackerAdvanceUnitSniper, 2877.0198f, 2247.9856f, 720.7160f, (byte) 31);
        spawn(trackerAdvanceUnitSniper, 2875.9683f, 2274.8623f, 720.7289f, (byte) 91);
	}
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 858715: //Mortasha [1st Boss].
			    despawnNpc(npc);
				//Power of an ascended entity detected in the 10th Altar Plaza.
				sendMsgByRace(1405978, Race.PC_ALL, 0);
				//Mortasha's power has affected the relic extractor.
				sendMsgByRace(1405983, Race.PC_ALL, 5000);
				spawn(858716, 2293.6052f, 1988.4974f, 737.4075f, (byte) 114); //Mortasha [Final Boss].
			break;
			case 858716: //Mortasha [Final Boss].
				spawn(840211, 2293.0300f, 1987.1031f, 737.4070f, (byte) 75); //Altar Of Ascension Exit.
				spawn(840268, 2295.6497f, 1994.1733f, 737.4056f, (byte) 86); //Mortasha Cubic Crystal.
				//Tracker Advance Unit Commander.
		        final int trackerAdvanceUnitCommander3 = spawnRace == Race.ASMODIANS ? 840280 : 840279;
		        spawn(trackerAdvanceUnitCommander3, 2299.4429f, 1993.0573f, 737.40515f, (byte) 76);
				//Tracker Advance Unit Combatant.
				final int trackerAdvanceUnitCombatant3 = spawnRace == Race.ASMODIANS ? 661560 : 661548;
				spawn(trackerAdvanceUnitCombatant3, 2287.1690f, 1969.0819f, 737.4009f, (byte) 15);
				spawn(trackerAdvanceUnitCombatant3, 2288.9850f, 1970.9429f, 737.4017f, (byte) 15);
				spawn(trackerAdvanceUnitCombatant3, 2291.0427f, 1973.0700f, 737.4024f, (byte) 15);
				spawn(trackerAdvanceUnitCombatant3, 2292.9631f, 1975.0685f, 737.4031f, (byte) 15);
				spawn(trackerAdvanceUnitCombatant3, 2294.9700f, 1976.9666f, 737.4037f, (byte) 15);
				spawn(trackerAdvanceUnitCombatant3, 2277.8716f, 1977.8779f, 737.4014f, (byte) 14);
				spawn(trackerAdvanceUnitCombatant3, 2279.9082f, 1979.8739f, 737.4023f, (byte) 14);
				spawn(trackerAdvanceUnitCombatant3, 2281.9817f, 1981.7716f, 737.4032f, (byte) 14);
				spawn(trackerAdvanceUnitCombatant3, 2283.9314f, 1983.4727f, 737.4040f, (byte) 14);
				spawn(trackerAdvanceUnitCombatant3, 2286.1562f, 1985.5774f, 737.4049f, (byte) 14);
				spawn(trackerAdvanceUnitCombatant3, 2299.6420f, 1986.8821f, 737.4055f, (byte) 74);
				spawn(trackerAdvanceUnitCombatant3, 2298.7295f, 1997.0077f, 737.4043f, (byte) 74);
				spawn(trackerAdvanceUnitCombatant3, 2295.9907f, 1999.8890f, 737.4037f, (byte) 74);
				spawn(trackerAdvanceUnitCombatant3, 2303.8306f, 1991.7759f, 737.4040f, (byte) 75);
				spawn(trackerAdvanceUnitCombatant3, 2306.3596f, 1989.0900f, 737.4034f, (byte) 74);
				//Tracker Advance Unit Assassin.
				final int trackerAdvanceUnitAssassin2 = spawnRace == Race.ASMODIANS ? 661565 : 661553;
				spawn(trackerAdvanceUnitAssassin2, 2241.9473f, 1978.8008f, 737.3905f, (byte) 3);
				spawn(trackerAdvanceUnitAssassin2, 2288.9934f, 1936.6600f, 737.3907f, (byte) 30);
				spawn(trackerAdvanceUnitAssassin2, 2293.9692f, 1936.8356f, 737.3909f, (byte) 30);
				spawn(trackerAdvanceUnitAssassin2, 2354.9094f, 1988.9658f, 736.6574f, (byte) 60);
				spawn(trackerAdvanceUnitAssassin2, 2354.9146f, 1993.4469f, 736.7561f, (byte) 60);
				spawn(trackerAdvanceUnitAssassin2, 2295.7010f, 2036.1357f, 737.3922f, (byte) 91);
				spawn(trackerAdvanceUnitAssassin2, 2290.8096f, 2036.0604f, 737.3921f, (byte) 91);
				spawn(trackerAdvanceUnitAssassin2, 2242.7710f, 1974.0769f, 737.3905f, (byte) 4);
				spawn(trackerAdvanceUnitAssassin2, 2275.9055f, 1979.8981f, 737.4012f, (byte) 13);
				spawn(trackerAdvanceUnitAssassin2, 2278.0083f, 1981.7415f, 737.4020f, (byte) 14);
				spawn(trackerAdvanceUnitAssassin2, 2280.1719f, 1983.6719f, 737.4029f, (byte) 13);
				spawn(trackerAdvanceUnitAssassin2, 2282.1850f, 1985.4663f, 737.4037f, (byte) 13);
				spawn(trackerAdvanceUnitAssassin2, 2284.4036f, 1987.4949f, 737.4045f, (byte) 14);
				spawn(trackerAdvanceUnitAssassin2, 2284.9530f, 1971.1687f, 737.4013f, (byte) 15);
				spawn(trackerAdvanceUnitAssassin2, 2286.7627f, 1973.2120f, 737.4021f, (byte) 16);
				spawn(trackerAdvanceUnitAssassin2, 2288.7761f, 1975.1967f, 737.4029f, (byte) 16);
				spawn(trackerAdvanceUnitAssassin2, 2290.7537f, 1977.1772f, 737.4037f, (byte) 16);
				spawn(trackerAdvanceUnitAssassin2, 2292.7869f, 1979.1985f, 737.4045f, (byte) 16);
				spawn(trackerAdvanceUnitAssassin2, 2293.6858f, 1993.1825f, 737.4060f, (byte) 74);
				spawn(trackerAdvanceUnitAssassin2, 2299.5823f, 2006.4933f, 737.4014f, (byte) 80);
				spawn(trackerAdvanceUnitAssassin2, 2304.8810f, 2002.8066f, 737.4017f, (byte) 76);
				spawn(trackerAdvanceUnitAssassin2, 2309.9258f, 1996.8009f, 737.4017f, (byte) 72);
				spawn(trackerAdvanceUnitAssassin2, 2312.4954f, 1993.1798f, 737.4012f, (byte) 73);
            break;
        }
    }
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 16) {
			doors.get(16).setOpen(true);
			//Help the guards defeat the ascended entity.
			sendMsgByRace(1405979, Race.PC_ALL, 2000);
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 840206: //12th Altar Artifact.
			    despawnNpc(npc);
				//The 12th Altar Artifact has been activated.
		        sendMsgByRace(1405986, Race.PC_ALL, 0);
				//The spirits are angered by your intrusion of the Spirits' Haven, but you are replete with the power of healing.
				sendMsgByRace(1405981, Race.PC_ALL, 5000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						//SkillEngine.getInstance().applyEffectDirectly(20832, player, player, 3600000 * 1);
					}
				});
			break;
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	@Override
    public void onInstanceDestroy() {
        doors.clear();
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