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
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.summons.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302440000)
public class Silentera_Corridor extends GeneralInstanceHandler
{
	private Race spawnRace;
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (spawnRace == null) {
			spawnRace = player.getRace();
			silenteraCorridor();
		}
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		//If you combine forces with Roye, you should easily be able to destroy the Crumbled Rock.
		sendMsgByRace(1404690, Race.ELYOS, 10000);
		//If you combine forces with Cheska, you should easily be able to destroy the Crumbled Rock.
		sendMsgByRace(1404723, Race.ASMODIANS, 10000);
		////////////////////////////////////////////////////////////////////////////////
		Npc vergelan = (Npc) spawn(806909, 558.0000f, 1137.0000f, 332.0000f, (byte) 60);
		//The Light of Fantasy Legion has left first.
		NpcShoutsService.getInstance().sendMsg(vergelan, 1502069, vergelan.getObjectId(), 0, 6000);
		//We should advance into the Giant's Square.
		NpcShoutsService.getInstance().sendMsg(vergelan, 1502070, vergelan.getObjectId(), 0, 9000);
		///////////////////////////////////////////////////////////////////////////////
		Npc asinelli = (Npc) spawn(820081, 509.0000f, 405.0000f, 327.0000f, (byte) 31);
		//The Light of Fantasy Legion has left first.
		NpcShoutsService.getInstance().sendMsg(asinelli, 1502069, asinelli.getObjectId(), 0, 6000);
		//We should advance into the Giant's Square.
		NpcShoutsService.getInstance().sendMsg(asinelli, 1502070, asinelli.getObjectId(), 0, 9000);
		//Asmodians appeared out of nowhere…
		NpcShoutsService.getInstance().sendMsg(asinelli, 1502071, asinelli.getObjectId(), 0, 12000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
    }
	
	private void silenteraCorridor() {
		final int Asinelli_Vergelan = spawnRace == Race.ASMODIANS ? 836863 : 836864;
		spawn(Asinelli_Vergelan, 746.0000f, 792.0000f, 289.0000f, (byte) 74);
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 836847: //Wisplight Legionary.
			case 836848: //Wisplight Legionary.
			case 836849: //Wisplight Legionary.
			case 836850: //Fatebound Legionnaire.
			case 836851: //Fatebound Legionnaire.
			case 836852: //Fatebound Legionnaire.
			    despawnNpc(npc);
			break;
			case 836863: //Asinelli Of The Wisplight Legion.
			    despawnNpc(npc);
				spawn(806910, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Tired Vergelan.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
			case 836864: //Vergelan Of The Fatebound Legion.
			    despawnNpc(npc);
				spawn(820169, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Tired Asinelli.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
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