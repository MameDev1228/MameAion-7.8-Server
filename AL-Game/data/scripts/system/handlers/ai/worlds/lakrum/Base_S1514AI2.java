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

@AIName("Base_S1514")
public class Base_S1514AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1514();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1514_Dr_Killer_As.
					spawn(886292, 1901.3204f, 2128.1045f, 286.1710f, (byte) 89);
					spawn(886292, 1904.1700f, 2111.0005f, 285.2500f, (byte) 93);
					spawn(886292, 1896.2151f, 2111.1575f, 285.2500f, (byte) 93);
					spawn(886292, 1895.6421f, 2130.1873f, 285.2500f, (byte) 65);
					spawn(886292, 1907.5320f, 2128.6775f, 285.2500f, (byte) 112);
					spawn(886292, 1883.1667f, 2130.2980f, 285.1606f, (byte) 62);
					spawn(886292, 1886.8618f, 2135.6997f, 285.0538f, (byte) 55);
					spawn(886292, 1889.1339f, 2134.6072f, 285.1955f, (byte) 53);
					spawn(886292, 1900.6030f, 2118.7715f, 285.2500f, (byte) 90);
					spawn(886292, 1925.1420f, 2110.0144f, 285.3745f, (byte) 74);
					spawn(886292, 1908.0690f, 2133.0470f, 285.2500f, (byte) 6);
					spawn(886292, 1896.2499f, 2134.4004f, 285.2500f, (byte) 52);
					spawn(886292, 1918.2320f, 2118.2673f, 285.2500f, (byte) 49);
					spawn(886292, 1884.4998f, 2120.3167f, 285.0822f, (byte) 6);
					spawn(886292, 1886.8046f, 2144.6816f, 285.0503f, (byte) 57);
					spawn(886292, 1867.1368f, 2132.1812f, 282.2558f, (byte) 93);
					spawn(886292, 1919.0784f, 2137.9102f, 285.2500f, (byte) 72);
					spawn(886292, 1902.6248f, 2138.1953f, 285.2500f, (byte) 28);
					spawn(886292, 1906.5675f, 2148.6290f, 285.2893f, (byte) 89);
					spawn(886292, 1900.3358f, 2108.6987f, 285.2500f, (byte) 90);
				}
			}
		}
	}
	
	private void LDF7KillerS1514() {
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