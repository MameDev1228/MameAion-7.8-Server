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

@AIName("Base_S8")
public class Base_S8AI2 extends AggressiveNpcAI2
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
					DF4KillerS8();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//DF4_V03_Fanatic_Killer.
					spawn(661908, 2190.8757f, 1559.0951f, 364.5565f, (byte) 47);
                    spawn(661908, 2199.2178f, 1565.4668f, 364.6250f, (byte) 42);
					spawn(661908, 2215.5654f, 1559.1348f, 364.8478f, (byte) 15);
					spawn(661908, 2207.8490f, 1542.2420f, 364.7500f, (byte) 39);
					spawn(661908, 2197.2695f, 1543.2853f, 364.6250f, (byte) 29);
					spawn(661908, 2193.8125f, 1564.1854f, 364.4649f, (byte) 42);
					spawn(661908, 2207.5798f, 1557.4463f, 364.6858f, (byte) 49);
					spawn(661908, 2192.9473f, 1549.9362f, 364.6250f, (byte) 29);
				}
			}
		}
	}
	
	private void DF4KillerS8() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Assassins have appeared at the Fatebound Legion's 3rd Garrison.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_DF4_V03_02, 0);
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}