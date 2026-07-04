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
package ai.worlds.gelkmaros;

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

@AIName("Base_S9")
public class Base_S9AI2 extends AggressiveNpcAI2
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
					DF4KillerS9();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//DF4_V04_Fanatic_Killer.
					spawn(661918, 2547.6033f, 1036.5574f, 291.3401f, (byte) 77);
                    spawn(661918, 2539.0872f, 1043.4993f, 291.4687f, (byte) 78);
					spawn(661918, 2544.3494f, 1062.7651f, 292.0847f, (byte) 37);
					spawn(661918, 2564.5413f, 1042.1934f, 291.6250f, (byte) 117);
					spawn(661918, 2561.6746f, 1057.2704f, 292.0859f, (byte) 80);
					spawn(661918, 2542.3809f, 1038.1294f, 291.0161f, (byte) 79);
					spawn(661918, 2547.2212f, 1054.0891f, 291.7500f, (byte) 83);
					spawn(661918, 2553.3306f, 1042.1982f, 291.7081f, (byte) 56);
				}
			}
		}
	}
	
	private void DF4KillerS9() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Assassins have appeared at the Fatebound Legion's 4th Garrison.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_DF4_V04_02, 0);
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}