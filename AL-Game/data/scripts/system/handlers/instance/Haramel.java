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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.summons.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300200000)
public class Haramel extends GeneralInstanceHandler
{
	private Race videoRace;
	private Race spawnRace;
	
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
    }
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateMission60009(player);
		} else {
			ClassChangeService.onUpdateMission70009(player);
		} if (spawnRace == null) {
			spawnRace = player.getRace();
			spawnOdella();
			spawnCheskaRoye();
			spawnProcessedOdella();
			spawnCheskaRoyeMission();
		}
    }
	
	private void spawnCheskaRoye() {
		final int cheskaRoye = spawnRace == Race.ASMODIANS ? 799995 : 799994;
		spawn(cheskaRoye, 222.0000f, 350.0000f, 141.0000f, (byte) 28);
	}
	
	private void spawnCheskaRoyeMission() {
		final int cheskaRoyeM = spawnRace == Race.ASMODIANS ? 806883 : 820133;
		Npc NpcRace = (Npc) spawn(cheskaRoyeM, 143.0000f, 24.0000f, 144.0000f, (byte) 112);
		//Nothing can stand against our combined might.
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502067, NpcRace.getObjectId(), 0, 9000);
		//We will prevail.
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502068, NpcRace.getObjectId(), 0, 12000);
	}
	
	private void spawnOdella() {
		final int odella = spawnRace == Race.ASMODIANS ? 700385 : 700834;
		spawn(odella, 333.817f, 345.084f, 86.9499f, (byte) 0, 17);
		spawn(odella, 350.922f, 366.013f, 90.1839f, (byte) 0, 50);
		spawn(odella, 365.372f, 357.365f, 89.4211f, (byte) 0, 53);
		spawn(odella, 373.480f, 330.550f, 85.4616f, (byte) 0, 54);
		spawn(odella, 339.157f, 323.219f, 88.0086f, (byte) 0, 115);
	}
	
	private void spawnProcessedOdella() {
		final int processedOdella = spawnRace == Race.ASMODIANS ? 703531 : 700953;
		spawn(processedOdella, 314.067f, 263.260f, 89.0267f, (byte) 0, 282);
		spawn(processedOdella, 314.040f, 288.418f, 89.0267f, (byte) 0, 283);
		spawn(processedOdella, 294.047f, 250.026f, 90.1643f, (byte) 0, 285);
	}
	
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 700855: //Prison Doors.
			    despawnNpc(npc);
			break;
			case 653218: //Hamerun The Bleeder.
                despawnNpc(npc);
				//Hamerun has dropped a treasure chest.
				sendMsgByRace(1400713, Race.PC_ALL, 0);
			    spawn(700829, 224.0000f, 268.0000f, 144.0000f, (byte) 90); //Ancient Treasure Box.
				spawn(836793, 224.0000f, 262.0000f, 144.0000f, (byte) 30); //Opened Dimensional Gate.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int haramel = videoRace == Race.ASMODIANS ? 457 : 457;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, haramel));
						}
					}
				});
			break;
        }
    }
	
	@Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
			case 730322: //Tower Lift.
				towerLift(player, 230.0000f, 213.0000f, 89.0000f, (byte) 0);
            break;
        }
    }
	
	protected void towerLift(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
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
}