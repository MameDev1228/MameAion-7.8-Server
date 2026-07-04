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
package ai.worlds.lakrum;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Base_S1511")
public class Base_S1511AI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
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
					LDF7KillerS1511();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1511_Dr_Killer_As.
					spawn(886289, 1788.2050f, 1121.6146f, 262.831f, (byte) 88);
					spawn(886289, 1783.1407f, 1104.7957f, 261.875f, (byte) 92);
					spawn(886289, 1790.9004f, 1104.9045f, 261.875f, (byte) 92);
					spawn(886289, 1793.9166f, 1122.4484f, 261.875f, (byte) 113);
					spawn(886289, 1783.1436f, 1127.8563f, 261.875f, (byte) 52);
					spawn(886289, 1809.6542f, 1128.4038f, 261.875f, (byte) 11);
					spawn(886289, 1781.4384f, 1140.3091f, 261.875f, (byte) 103);
					spawn(886289, 1787.5331f, 1112.1796f, 261.875f, (byte) 89);
					spawn(886289, 1768.7902f, 1127.6970f, 261.875f, (byte) 74);
					spawn(886289, 1813.3231f, 1107.0975f, 261.875f, (byte) 85);
					spawn(886289, 1782.6896f, 1123.7559f, 261.875f, (byte) 65);
					spawn(886289, 1794.4935f, 1126.5577f, 261.875f, (byte) 6);
					spawn(886289, 1804.0175f, 1111.8628f, 261.875f, (byte) 112);
					spawn(886289, 1771.8804f, 1114.7808f, 261.875f, (byte) 68);
					spawn(886289, 1787.0096f, 1102.4073f, 261.875f, (byte) 90);
					spawn(886289, 1791.4204f, 1131.2135f, 261.875f, (byte) 25);
					spawn(886289, 1787.0421f, 1131.7384f, 261.875f, (byte) 36);
				}
			}
		}
	}
	
	private void LDF7KillerS1511() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//You joined the battle against the Invading Balaur.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_Public_Quest_Accept, 0);
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}