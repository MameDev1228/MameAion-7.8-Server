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

@AIName("Base_V01")
public class Base_V01AI2 extends AggressiveNpcAI2
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
					LDF5AKillerV01();
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					getOwner().getController().onDelete();
					//LDF5A_Killer_Sub_V01.
					spawn(662515, 398.6384f, 1589.6800f, 270.5543f, (byte) 44);
					spawn(662515, 398.5188f, 1606.8505f, 270.5543f, (byte) 72);
					spawn(662515, 341.4562f, 1603.7831f, 258.6250f, (byte) 57);
					spawn(662515, 341.3511f, 1597.4385f, 258.6250f, (byte) 57);
					spawn(662515, 365.1297f, 1607.0546f, 259.5047f, (byte) 68);
					spawn(662515, 364.4376f, 1589.1455f, 259.0000f, (byte) 53);
					spawn(662515, 361.3991f, 1598.4590f, 259.1250f, (byte) 61);
					spawn(662515, 389.8524f, 1623.1658f, 271.0407f, (byte) 59);
					spawn(662515, 387.5680f, 1574.4901f, 271.0407f, (byte) 59);
					spawn(662515, 354.6668f, 1567.3915f, 259.9438f, (byte) 73);
					spawn(662515, 355.6376f, 1632.1727f, 258.8976f, (byte) 56);
					spawn(662515, 353.6235f, 1614.3145f, 259.2657f, (byte) 98);
					spawn(662515, 352.3425f, 1584.4346f, 259.3535f, (byte) 10);
					spawn(662515, 373.9274f, 1620.9215f, 259.2598f, (byte) 71);
					spawn(662515, 374.3518f, 1571.5865f, 259.1250f, (byte) 44);
				}
			}
		}
	}
	
	private void LDF5AKillerV01() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//점령군 암살 지휘관이 나타나 전투사관을 공격하려 합니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_killer_v01, 0);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}