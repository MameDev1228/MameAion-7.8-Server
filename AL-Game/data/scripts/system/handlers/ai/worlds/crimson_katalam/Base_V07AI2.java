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
package ai.worlds.crimson_katalam;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
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

@AIName("Base_V07")
public class Base_V07AI2 extends AggressiveNpcAI2
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
					LDF5AKillerV07();
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					getOwner().getController().onDelete();
					//LDF5A_Killer_Sub_V07.
					spawn(662611, 2123.8748f, 2847.1155f, 301.6250f, (byte) 81);
					spawn(662611, 2133.5024f, 2835.0603f, 301.5628f, (byte) 63);
					spawn(662611, 2130.0586f, 2884.5330f, 301.9297f, (byte) 45);
					spawn(662611, 2134.0570f, 2887.7751f, 301.6250f, (byte) 43);
					spawn(662611, 2154.9763f, 2863.0876f, 301.8750f, (byte) 60);
					spawn(662611, 2159.3047f, 2851.5732f, 301.7233f, (byte) 79);
					spawn(662611, 2143.2297f, 2898.4302f, 301.7500f, (byte) 55);
					spawn(662611, 2169.2864f, 2833.8718f, 301.6250f, (byte) 41);
					spawn(662611, 2146.8271f, 2832.9620f, 301.6250f, (byte) 9);
					spawn(662611, 2151.4143f, 2842.0442f, 301.6250f, (byte) 37);
					spawn(662611, 2135.1628f, 2864.5847f, 301.6250f, (byte) 114);
				}
			}
		}
	}
	
	private void LDF5AKillerV07() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//자객이 등장해 7기지 촌장을 처치하려 합니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_killer_v07, 0);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}