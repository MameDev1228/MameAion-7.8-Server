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

@AIName("Base_V12")
public class Base_V12AI2 extends AggressiveNpcAI2
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
					LDF5AKillerV12();
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					getOwner().getController().onDelete();
					//LDF5A_Killer_Sub_V12.
					spawn(662691, 1342.3538f, 1356.4758f, 112.6250f, (byte) 82);
					spawn(662691, 1354.5576f, 1356.3997f, 112.2908f, (byte) 100);
					spawn(662691, 1350.9108f, 1316.1042f, 107.7107f, (byte) 96);
					spawn(662691, 1357.1416f, 1316.8744f, 107.8583f, (byte) 96);
					spawn(662691, 1376.4028f, 1344.4420f, 108.7524f, (byte) 109);
					spawn(662691, 1381.7748f, 1350.4071f, 110.9204f, (byte) 109);
					spawn(662691, 1318.6687f, 1336.6956f, 109.7972f, (byte) 70);
					spawn(662691, 1317.1780f, 1343.2871f, 111.9003f, (byte) 65);
					spawn(662691, 1323.3014f, 1371.3765f, 115.4092f, (byte) 43);
					spawn(662691, 1354.5994f, 1383.8154f, 115.8857f, (byte) 32);
					spawn(662691, 1382.6234f, 1377.9437f, 116.8290f, (byte) 16);
					spawn(662691, 1372.8458f, 1360.6912f, 112.3603f, (byte) 80);
					spawn(662691, 1359.3513f, 1347.3137f, 109.7071f, (byte) 65);
					spawn(662691, 1335.4583f, 1334.3514f, 110.7500f, (byte) 4);
				}
			}
		}
	}
	
	private void LDF5AKillerV12() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//자객이 등장해 12기지 촌장을 처치하려 합니다.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_killer_v12, 0);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}