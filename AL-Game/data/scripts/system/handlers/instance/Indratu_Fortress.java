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
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.summons.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(310090000)
public class Indratu_Fortress extends GeneralInstanceHandler
{
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		ClassChangeService.onUpdateMission60206(player);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		//The Drakan Assault Commander has appeared.
		sendMsgByRace(1404695, Race.PC_ALL, 20000);
		Npc roye = (Npc) spawn(799996, 572.0000f, 351.0000f, 1013.0000f, (byte) 64);
		//Nothing can stand against our combined might.
		NpcShoutsService.getInstance().sendMsg(roye, 1502067, roye.getObjectId(), 0, 6000);
		//We will prevail.
		NpcShoutsService.getInstance().sendMsg(roye, 1502068, roye.getObjectId(), 0, 9000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		Summon summon = player.getSummon();
		if ((summon != null) && (summon.isSpawned())) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		Summon summon = player.getSummon();
		if ((summon != null) && (summon.isSpawned())) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
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