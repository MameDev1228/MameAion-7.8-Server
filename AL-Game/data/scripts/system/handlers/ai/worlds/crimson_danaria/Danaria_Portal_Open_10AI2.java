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
package ai.world.crimson_danaria;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Danaria_Portal_Open_10")
public class Danaria_Portal_Open_10AI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private Future<?> lightWallTask10;
	private boolean isStartTimer10 = false;
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
    @Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 10) {
				if (startedEvent.compareAndSet(false, true)) {
					canThink = false;
					danariaMsg10();
					if (!isStartTimer10) {
                        isStartTimer10 = true;
                        danariaCountdown10();
                    }
					getOwner().getController().onDelete();
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//Danaria [Asmodians 81th Base] To Katalam.
					spawn(703852, 833.0000f, 1555.0000f, 372.0000f, (byte) 28);
					lightWallTask10 = ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							WorldMapInstance instance = getPosition().getWorldMapInstance();
							deleteNpcs(instance.getNpcs(661409)); //Light Wall.
						}
					}, 300000);
				}
			}
		}
	}
	
	private void danariaMsg10() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The battle will begin soon. Get ready!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDTM_Lobby_01, 0);
					//4분 후에 전투가 시작됩니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_PandoraRaid_Time_01, 60000);
					//3분 후에 전투가 시작됩니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_PandoraRaid_Time_02, 120000);
					//2분 후에 전투가 시작됩니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_PandoraRaid_Time_03, 180000);
					//1분 후에 전투가 시작됩니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_PandoraRaid_Time_04, 240000);
					//The Aetheric Field is deactivated. The battle will now begin!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDLegion_Start_05, 300000);
				}
			}
		});
	}
	
	private void danariaCountdown10() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 300));
				}
			}
		});
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}