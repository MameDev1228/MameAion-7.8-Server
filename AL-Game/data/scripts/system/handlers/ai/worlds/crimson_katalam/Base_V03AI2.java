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

@AIName("Base_V03")
public class Base_V03AI2 extends AggressiveNpcAI2
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
					LDF5AKillerV03();
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					getOwner().getController().onDelete();
					//LDF5A_Killer_Sub_V03.
					spawn(662547, 2217.1775f, 2479.1414f, 151.3750f, (byte) 91);
					spawn(662547, 2205.9834f, 2467.3110f, 152.8194f, (byte) 118);
					spawn(662547, 2241.1711f, 2453.0596f, 150.9930f, (byte) 102);
					spawn(662547, 2236.6880f, 2448.3290f, 151.1240f, (byte) 102);
					spawn(662547, 2204.8525f, 2450.3662f, 156.4290f, (byte) 94);
					spawn(662547, 2243.0557f, 2483.1438f, 152.9583f, (byte) 1);
					spawn(662547, 2210.8142f, 2500.6785f, 152.5518f, (byte) 109);
					spawn(662547, 2187.7263f, 2475.7378f, 152.5518f, (byte) 108);
					spawn(662547, 2220.4502f, 2450.2847f, 151.5459f, (byte) 13);
					spawn(662547, 2240.1848f, 2468.0369f, 150.9861f, (byte) 75);
					spawn(662547, 2230.0115f, 2494.6765f, 153.6688f, (byte) 13);
				}
			}
		}
	}
	
	private void LDF5AKillerV03() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//자객이 등장해 3기지 촌장을 처치하려 합니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_killer_v03, 0);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}