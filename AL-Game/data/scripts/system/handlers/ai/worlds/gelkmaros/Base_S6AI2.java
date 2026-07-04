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

@AIName("Base_S6")
public class Base_S6AI2 extends AggressiveNpcAI2
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
					DF4KillerS6();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//DF4_V01_Fanatic_Killer.
					spawn(661888, 637.8815f, 2648.0757f, 387.3777f, (byte) 115);
					spawn(661888, 634.1723f, 2640.4016f, 387.3534f, (byte) 112);
					spawn(661888, 634.1427f, 2662.3335f, 387.4256f, (byte) 7);
					spawn(661888, 619.0453f, 2663.7280f, 387.7500f, (byte) 106);
					spawn(661888, 618.4355f, 2635.6047f, 387.7252f, (byte) 86);
					spawn(661888, 610.7780f, 2651.3535f, 387.8750f, (byte) 0);
					spawn(661888, 630.0079f, 2656.2715f, 387.6250f, (byte) 94);
					spawn(661888, 623.7220f, 2644.1802f, 387.8750f, (byte) 8);
					spawn(661888, 626.2612f, 2664.4377f, 387.6250f, (byte) 9);
					spawn(661888, 638.9220f, 2643.3481f, 386.8440f, (byte) 113);
				}
			}
		}
	}
	
	private void DF4KillerS6() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Assassins have appeared at the Fatebound Legion's 1st Garrison.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_DF4_V01_02, 0);
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}