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

@AIName("Base_S1515")
public class Base_S1515AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1515();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1515_Dr_Killer_As.
					spawn(886293, 1074.5480f, 2753.7393f, 232.2043f, (byte) 86);
					spawn(886293, 1075.6494f, 2736.6580f, 231.1250f, (byte) 92);
					spawn(886293, 1067.7756f, 2737.6255f, 231.1250f, (byte) 92);
					spawn(886293, 1069.0585f, 2756.5237f, 231.1250f, (byte) 64);
					spawn(886293, 1081.4608f, 2758.1113f, 231.1250f, (byte) 3);
					spawn(886293, 1060.9467f, 2763.7957f, 231.1250f, (byte) 67);
					spawn(886293, 1080.5347f, 2773.6257f, 231.1250f, (byte) 85);
					spawn(886293, 1072.8234f, 2744.2040f, 231.1250f, (byte) 88);
					spawn(886293, 1104.7483f, 2746.8677f, 231.1250f, (byte) 81);
					spawn(886293, 1050.2527f, 2738.4634f, 231.1250f, (byte) 76);
					spawn(886293, 1054.0867f, 2774.5230f, 231.1250f, (byte) 70);
					spawn(886293, 1089.9150f, 2742.4043f, 231.1250f, (byte) 109);
					spawn(886293, 1057.3100f, 2748.7280f, 231.1250f, (byte) 66);
					spawn(886293, 1070.1384f, 2760.6213f, 231.1250f, (byte) 51);
					spawn(886293, 1080.5571f, 2753.9329f, 231.1250f, (byte) 111);
					spawn(886293, 1071.3720f, 2735.5098f, 231.0943f, (byte) 88);
					spawn(886293, 1076.9589f, 2764.2366f, 231.1250f, (byte) 27);
					spawn(886293, 1095.5386f, 2760.6406f, 231.1250f, (byte) 108);
					spawn(886293, 1048.7557f, 2762.0070f, 231.1250f, (byte) 84);
				}
			}
		}
	}
	
	private void LDF7KillerS1515() {
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