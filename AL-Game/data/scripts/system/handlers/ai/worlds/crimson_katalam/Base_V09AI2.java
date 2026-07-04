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

@AIName("Base_V09")
public class Base_V09AI2 extends AggressiveNpcAI2
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
					LDF5AKillerV09();
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					getOwner().getController().onDelete();
					//LDF5A_Killer_Sub_V09.
					spawn(662643, 2745.6067f, 2274.2634f, 286.0839f, (byte) 35);
					spawn(662643, 2735.8060f, 2266.1995f, 286.0000f, (byte) 53);
					spawn(662643, 2727.0170f, 2286.0183f, 282.8750f, (byte) 45);
					spawn(662643, 2722.4065f, 2281.6812f, 283.3695f, (byte) 45);
					spawn(662643, 2758.3013f, 2243.5444f, 286.9040f, (byte) 107);
					spawn(662643, 2755.7560f, 2275.4630f, 285.2562f, (byte) 22);
					spawn(662643, 2718.6548f, 2257.1047f, 284.7205f, (byte) 60);
					spawn(662643, 2767.2732f, 2253.2327f, 287.2451f, (byte) 113);
					spawn(662643, 2743.2720f, 2281.5579f, 284.6802f, (byte) 77);
					spawn(662643, 2728.6204f, 2268.3690f, 285.1089f, (byte) 13);
					spawn(662643, 2765.3960f, 2268.9300f, 286.1220f, (byte) 42);
				}
			}
		}
	}
	
	private void LDF5AKillerV09() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//자객이 등장해 9기지 촌장을 처치하려 합니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_killer_v09, 0);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}