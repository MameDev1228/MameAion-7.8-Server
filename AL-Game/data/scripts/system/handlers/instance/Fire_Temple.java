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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(320100000)
public class Fire_Temple extends GeneralInstanceHandler
{
	protected boolean isInstanceDestroyed = false;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
        switch (npcId) {
			case 655494: //Silver Blade Rotan.
                switch (Rnd.get(1, 3)) {
				    case 1:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190000016, 1)); //Sawteeth Rotan Egg.
					break;
					case 2:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190000037, 1)); //Sawteeth Rotan Egg.
					break;
					case 3:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 190000038, 1)); //Sawteeth Rotan Egg.
					break;
				}
            break;
        }
    }
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
    }
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		spawn(655496, 421.96918f, 93.555084f, 117.30522f, (byte) 51); //Kromede The Corrupt.
        switch (Rnd.get(1, 8)) {
		    case 1:
				spawn(655489, 153.20758f, 161.87619f, 101.83779f, (byte) 32); //Blue Crystal Molgat.
			break;
			case 2:
				spawn(655490, 151.76723f, 302.36328f, 122.874084f, (byte) 24); //Lava Gatneri.
			break;
			case 3:
				spawn(655491, 350.2237f, 354.806f, 146.613f, (byte) 65); //Flame Branch Flavi.
			break;
			case 4:
				spawn(655492, 304.33118f, 419.86487f, 133.9806f, (byte) 17); //Black Smoke Asparn.
			break;
			case 5:
				spawn(655493, 297.6783f, 202.19652f, 119.36518f, (byte) 61); //Tough Sipus.
			break;
			case 6:
				spawn(655495, 293.1684f, 97.34582f, 128.40712f, (byte) 91); //Broken Wing Kutisen.
			break;
			case 7:
				//Vile Judge Kromede has appeared.
				sendMsgByRace(1404997, Race.PC_ALL, 0);
				despawnNpcs(instance.getNpcs(655496)); //Kromede The Corrupt.
				spawn(655502, 421.96918f, 93.555084f, 117.30522f, (byte) 51); //Vile Judge Kromede.
			break;
			case 8:
				spawn(798109, 144.581f, 162.128f, 100.931f, (byte) 23); //Liurerk.
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 730048: //Fire Temple Exit.
				if (player.getRace() == Race.ELYOS) {
					fireTempleExitE(player, 920.0000f, 2322.0000f, 159.0000f, (byte) 44);
				} else {
					fireTempleExitA(player, 2270.0000f, 1278.0000f, 473.0000f, (byte) 46);
				}
			break;
		}
	}
	
	protected void fireTempleExitE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210040000, 1, x, y, z, h);
	}
	protected void fireTempleExitA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220040000, 1, x, y, z, h);
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
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