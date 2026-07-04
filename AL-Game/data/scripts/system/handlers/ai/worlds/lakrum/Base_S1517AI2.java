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

@AIName("Base_S1517")
public class Base_S1517AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1517();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1517_Dr_Killer_As.
					spawn(886295, 2763.6140f, 1459.3776f, 198.9827f, (byte) 111);
					spawn(886295, 2732.9731f, 1465.5026f, 197.6891f, (byte) 52);
					spawn(886295, 2736.7330f, 1472.1584f, 197.6942f, (byte) 52);
					spawn(886295, 2822.4167f, 1441.2766f, 199.2500f, (byte) 3);
					spawn(886295, 2788.3555f, 1422.7384f, 199.0117f, (byte) 77);
					spawn(886295, 2780.6545f, 1480.9424f, 197.6250f, (byte) 75);
					spawn(886295, 2795.4280f, 1468.9194f, 197.6250f, (byte) 94);
					spawn(886295, 2759.5369f, 1454.7687f, 197.8750f, (byte) 87);
					spawn(886295, 2765.7850f, 1465.0477f, 197.8750f, (byte) 13);
					spawn(886295, 2775.2407f, 1453.0442f, 197.7622f, (byte) 93);
					spawn(886295, 2775.3447f, 1427.0700f, 199.3060f, (byte) 94);
					spawn(886295, 2808.2842f, 1469.4591f, 199.7558f, (byte) 25);
					spawn(886295, 2835.6792f, 1446.3367f, 199.2500f, (byte) 23);
					spawn(886295, 2743.6917f, 1453.0600f, 198.5587f, (byte) 40);
					spawn(886295, 2766.3657f, 1478.5295f, 198.2704f, (byte) 70);
					spawn(886295, 2718.6587f, 1456.6799f, 197.6250f, (byte) 39);
					spawn(886295, 2735.2840f, 1485.0889f, 197.6697f, (byte) 41);
					spawn(886295, 2796.9478f, 1493.1658f, 200.4929f, (byte) 20);
					spawn(886295, 2825.7244f, 1423.2472f, 199.2500f, (byte) 50);
					spawn(886295, 2756.9204f, 1482.2310f, 197.9801f, (byte) 45);
					spawn(886295, 2732.1758f, 1446.6726f, 198.2189f, (byte) 70);
					spawn(886295, 2808.0740f, 1418.1581f, 199.2500f, (byte) 96);
					spawn(886295, 2808.7378f, 1456.5837f, 199.2500f, (byte) 24);
					spawn(886295, 2750.6924f, 1442.1826f, 198.7500f, (byte) 35);
					spawn(886295, 2762.0508f, 1467.5361f, 197.7789f, (byte) 26);
					spawn(886295, 2755.8232f, 1457.2278f, 197.8860f, (byte) 73);
					spawn(886295, 2710.9373f, 1449.4370f, 197.5000f, (byte) 22);
					spawn(886295, 2761.9614f, 1439.1849f, 198.7500f, (byte) 6);
					spawn(886295, 2797.1060f, 1440.6149f, 199.6018f, (byte) 50);
					spawn(886295, 2755.2285f, 1464.7420f, 198.0000f, (byte) 50);
					spawn(886295, 2740.5947f, 1498.3557f, 197.4628f, (byte) 76);
				}
			}
		}
	}
	
	private void LDF7KillerS1517() {
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