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

@AIName("Base_S1516")
public class Base_S1516AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1516();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1516_Dr_Killer_As.
					spawn(886294, 555.9687f, 2196.0894f, 285.7165f, (byte) 27);
					spawn(886294, 555.0190f, 2212.8264f, 284.8750f, (byte) 28);
					spawn(886294, 562.7032f, 2212.3755f, 284.8750f, (byte) 28);
					spawn(886294, 561.3083f, 2193.5483f, 284.8750f, (byte) 4);
					spawn(886294, 549.3926f, 2191.9163f, 284.8750f, (byte) 64);
					spawn(886294, 530.0015f, 2236.8528f, 285.1240f, (byte) 113);
					spawn(886294, 558.2276f, 2205.4167f, 284.8750f, (byte) 28);
					spawn(886294, 566.2392f, 2173.4258f, 284.4491f, (byte) 113);
					spawn(886294, 524.6919f, 2197.3772f, 285.1250f, (byte) 33);
					spawn(886294, 540.9062f, 2207.5070f, 284.8750f, (byte) 48);
					spawn(886294, 573.3533f, 2201.4873f, 284.8750f, (byte) 5);
					spawn(886294, 549.9487f, 2196.1660f, 284.8750f, (byte) 49);
					spawn(886294, 560.4150f, 2189.2654f, 284.8750f, (byte) 111);
					spawn(886294, 556.2813f, 2174.2270f, 284.7500f, (byte) 0);
					spawn(886294, 585.6174f, 2193.6740f, 284.8750f, (byte) 33);
					spawn(886294, 622.3351f, 2235.7300f, 284.4675f, (byte) 61);
					spawn(886294, 569.7752f, 2185.3132f, 284.8750f, (byte) 119);
					spawn(886294, 536.3664f, 2189.7750f, 284.8750f, (byte) 46);
					spawn(886294, 553.9012f, 2186.1692f, 284.8750f, (byte) 86);
					spawn(886294, 621.6230f, 2149.1462f, 291.7798f, (byte) 42);
				}
			}
		}
	}
	
	private void LDF7KillerS1516() {
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