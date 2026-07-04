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

@AIName("Base_S1519")
public class Base_S1519AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1519();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1519_Dr_Killer_As.
					spawn(886297, 1280.2201f, 1505.1301f, 254.6461f, (byte) 60);
					spawn(886297, 1326.7413f, 1558.4302f, 253.8023f, (byte) 0);
					spawn(886297, 1326.9030f, 1566.9340f, 253.8025f, (byte) 0);
					spawn(886297, 1324.9274f, 1452.6559f, 253.8031f, (byte) 0);
					spawn(886297, 1324.8830f, 1443.6848f, 253.7974f, (byte) 0);
					spawn(886297, 1137.3643f, 1624.4100f, 244.5000f, (byte) 31);
					spawn(886297, 1146.5920f, 1625.6204f, 244.8610f, (byte) 41);
					spawn(886297, 1253.1344f, 1510.5411f, 253.6250f, (byte) 61);
					spawn(886297, 1252.6871f, 1503.2843f, 253.6250f, (byte) 61);
					spawn(886297, 1286.0146f, 1486.5249f, 253.6250f, (byte) 79);
					spawn(886297, 1281.9724f, 1510.9244f, 253.6250f, (byte) 36);
					spawn(886297, 1281.0627f, 1499.1530f, 253.6250f, (byte) 83);
					spawn(886297, 1306.4761f, 1493.5251f, 255.9120f, (byte) 74);
					spawn(886297, 1310.9329f, 1488.8214f, 255.9116f, (byte) 74);
					spawn(886297, 1307.0747f, 1518.9666f, 255.9554f, (byte) 44);
					spawn(886297, 1311.7749f, 1523.5479f, 255.9116f, (byte) 44);
					spawn(886297, 1278.1058f, 1573.0626f, 253.6250f, (byte) 86);
					spawn(886297, 1229.9166f, 1502.8820f, 253.6250f, (byte) 91);
					spawn(886297, 1294.5728f, 1550.9135f, 253.6250f, (byte) 46);
					spawn(886297, 1245.2875f, 1541.1431f, 253.6250f, (byte) 69);
					spawn(886297, 1267.2561f, 1507.0265f, 253.6250f, (byte) 60);
					spawn(886297, 1279.8911f, 1474.0989f, 253.6250f, (byte) 90);
					spawn(886297, 1221.6259f, 1458.5377f, 254.1250f, (byte) 71);
					spawn(886297, 1179.6140f, 1557.3280f, 253.3098f, (byte) 50);
					spawn(886297, 1259.4177f, 1571.0623f, 253.6250f, (byte) 109);
					spawn(886297, 1281.2310f, 1523.1875f, 253.6250f, (byte) 30);
					spawn(886297, 1255.1606f, 1473.8944f, 254.0458f, (byte) 92);
					spawn(886297, 1253.0928f, 1447.5594f, 253.7351f, (byte) 41);
					spawn(886297, 1241.8564f, 1507.5194f, 253.6250f, (byte) 59);
					spawn(886297, 1286.6758f, 1462.6700f, 253.6250f, (byte) 96);
					spawn(886297, 1242.7736f, 1469.9408f, 253.6250f, (byte) 68);
					spawn(886297, 1263.5685f, 1525.3989f, 253.6250f, (byte) 34);
					spawn(886297, 1303.9364f, 1549.5425f, 253.6272f, (byte) 73);
					spawn(886297, 1285.8987f, 1510.7024f, 253.6250f, (byte) 23);
					spawn(886297, 1285.3456f, 1499.0962f, 253.6250f, (byte) 97);
					spawn(886297, 1221.7720f, 1476.0030f, 254.2500f, (byte) 108);
					spawn(886297, 1323.7552f, 1471.1644f, 255.8988f, (byte) 75);
					spawn(886297, 1354.3724f, 1505.3402f, 255.9121f, (byte) 0);
					spawn(886297, 1325.4548f, 1541.0378f, 255.8958f, (byte) 41);
					spawn(886297, 1279.9618f, 1447.4950f, 253.6250f, (byte) 0);
					spawn(886297, 1230.1594f, 1509.4778f, 253.6250f, (byte) 29);
					spawn(886297, 1279.7775f, 1562.8585f, 253.6250f, (byte) 0);
					spawn(886297, 1289.4185f, 1534.8323f, 253.6250f, (byte) 51);
					spawn(886297, 1289.2352f, 1485.3942f, 253.6250f, (byte) 87);
					spawn(886297, 1260.3980f, 1487.7058f, 253.6250f, (byte) 80);
					spawn(886297, 1263.9610f, 1542.3372f, 253.6250f, (byte) 49);
					spawn(886297, 1289.2385f, 1567.9396f, 253.6250f, (byte) 110);
					spawn(886297, 1290.5815f, 1504.8167f, 253.6250f, (byte) 0);
					spawn(886297, 1272.0391f, 1466.0869f, 253.6250f, (byte) 103);
					spawn(886297, 1225.7648f, 1524.7341f, 253.6250f, (byte) 109);
					spawn(886297, 1238.0325f, 1459.7372f, 253.6250f, (byte) 0);
					spawn(886297, 1243.3434f, 1554.2867f, 253.6250f, (byte) 0);
				}
			}
		}
	}
	
	private void LDF7KillerS1519() {
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