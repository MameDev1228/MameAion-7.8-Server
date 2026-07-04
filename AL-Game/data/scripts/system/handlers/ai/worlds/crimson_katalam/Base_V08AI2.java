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

@AIName("Base_V08")
public class Base_V08AI2 extends AggressiveNpcAI2
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
					LDF5AKillerV08();
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					getOwner().getController().onDelete();
					//LDF5A_Killer_Sub_V08.
					spawn(662627, 2780.6917f, 779.5574f, 303.37378f, (byte) 83);
					spawn(662627, 2767.263f, 778.95557f, 303.55417f, (byte) 53);
					spawn(662627, 2764.4995f, 798.82544f, 305.75f, (byte) 47);
					spawn(662627, 2779.3303f, 807.1517f, 307.48883f, (byte) 18);
					spawn(662627, 2791.2913f, 811.255f, 307.5024f, (byte) 8);
					spawn(662627, 2797.485f, 796.86743f, 307.23007f, (byte) 111);
					spawn(662627, 2798.1382f, 775.7802f, 307.61356f, (byte) 96);
					spawn(662627, 2811.782f, 807.53546f, 308.06345f, (byte) 48);
					spawn(662627, 2802.7046f, 828.09906f, 308.30054f, (byte) 33);
					spawn(662627, 2814.2148f, 831.4046f, 308.56226f, (byte) 65);
					spawn(662627, 2819.1238f, 822.26154f, 307.93048f, (byte) 75);
				}
			}
		}
	}
	
	private void LDF5AKillerV08() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//자객이 등장해 8기지 촌장을 처치하려 합니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_killer_v08, 0);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}