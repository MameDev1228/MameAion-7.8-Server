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

@AIName("Base_S1518")
public class Base_S1518AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1518();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1518_Dr_Killer_As.
					spawn(886296, 1918.3452f, 1488.2698f, 216.0662f, (byte) 0);
					spawn(886296, 1888.8424f, 1477.8628f, 215.2500f, (byte) 63);
					spawn(886296, 1888.7545f, 1485.6035f, 215.2500f, (byte) 62);
					spawn(886296, 1936.4486f, 1454.3490f, 215.0000f, (byte) 81);
					spawn(886296, 1925.1962f, 1454.6053f, 215.0000f, (byte) 90);
					spawn(886296, 1963.3851f, 1509.1120f, 215.1805f, (byte) 27);
					spawn(886296, 1963.4660f, 1470.3925f, 214.9666f, (byte) 93);
					spawn(886296, 1917.4043f, 1494.1816f, 215.1250f, (byte) 25);
					spawn(886296, 1913.0049f, 1482.5745f, 215.0359f, (byte) 85);
					spawn(886296, 1883.2163f, 1449.7452f, 215.1250f, (byte) 115);
					spawn(886296, 1906.5627f, 1470.5872f, 215.1250f, (byte) 84);
					spawn(886296, 1898.6998f, 1482.8768f, 215.1250f, (byte) 63);
					spawn(886296, 1930.5231f, 1506.4350f, 215.1250f, (byte) 46);
					spawn(886296, 1933.4504f, 1469.2500f, 215.1250f, (byte) 92);
					spawn(886296, 1951.1041f, 1516.6292f, 215.1250f, (byte) 40);
					spawn(886296, 1982.7955f, 1489.8068f, 215.2002f, (byte) 90);
					spawn(886296, 1966.5779f, 1453.6196f, 215.1487f, (byte) 67);
					spawn(886296, 1968.9019f, 1527.1337f, 215.1958f, (byte) 39);
					spawn(886296, 1897.4438f, 1509.1407f, 215.2500f, (byte) 49);
					spawn(886296, 1928.4557f, 1488.8506f, 215.1250f, (byte) 1);
					spawn(886296, 1894.6500f, 1454.3752f, 215.1250f, (byte) 93);
					spawn(886296, 1898.4268f, 1497.6708f, 215.1250f, (byte) 39);
					spawn(886296, 1895.8190f, 1463.9736f, 215.2500f, (byte) 82);
					spawn(886296, 1944.1676f, 1503.2129f, 215.1250f, (byte) 48);
					spawn(886296, 1944.2954f, 1476.5375f, 215.0000f, (byte) 73);
					spawn(886296, 1913.0197f, 1494.2628f, 215.1250f, (byte) 36);
					spawn(886296, 1917.1971f, 1482.9122f, 215.0570f, (byte) 96);
					spawn(886296, 1955.2693f, 1529.1112f, 215.1250f, (byte) 47);
					spawn(886296, 1948.5153f, 1456.7401f, 215.1572f, (byte) 104);
					spawn(886296, 1965.5708f, 1489.8008f, 215.6049f, (byte) 61);
					spawn(886296, 1912.9303f, 1517.9929f, 215.2500f, (byte) 49);
					spawn(886296, 1954.8441f, 1539.9425f, 215.3714f, (byte) 67);
					spawn(886296, 1908.3875f, 1488.4907f, 215.1250f, (byte) 60);
					spawn(886296, 1910.3915f, 1507.4253f, 215.2500f, (byte) 55);
					spawn(886296, 1926.0726f, 1524.5458f, 215.1250f, (byte) 50);
					spawn(886296, 1912.7272f, 1458.0674f, 215.2500f, (byte) 94);
					spawn(886296, 1875.9165f, 1507.8312f, 216.3486f, (byte) 90);
				}
			}
		}
	}
	
	private void LDF7KillerS1518() {
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