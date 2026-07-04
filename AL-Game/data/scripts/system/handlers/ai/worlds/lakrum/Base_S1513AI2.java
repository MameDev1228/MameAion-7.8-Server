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

@AIName("Base_S1513")
public class Base_S1513AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1513();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1513_Dr_Killer_As.
					spawn(886291, 748.2857f, 803.7664f, 333.6482f, (byte) 5);
					spawn(886291, 745.4873f, 809.2673f, 332.6250f, (byte) 29);
					spawn(886291, 744.1551f, 796.6861f, 332.6250f, (byte) 88);
					spawn(886291, 752.8962f, 842.3929f, 333.5805f, (byte) 49);
					spawn(886291, 742.0486f, 843.6888f, 333.7275f, (byte) 33);
					spawn(886291, 783.6350f, 800.6684f, 331.8978f, (byte) 116);
					spawn(886291, 724.2113f, 855.0600f, 334.5869f, (byte) 116);
					spawn(886291, 758.4717f, 815.0291f, 332.6250f, (byte) 65);
					spawn(886291, 735.3360f, 800.7928f, 332.6250f, (byte) 64);
					spawn(886291, 712.6893f, 787.1273f, 332.5287f, (byte) 78);
					spawn(886291, 748.2859f, 797.6315f, 332.6250f, (byte) 102);
					spawn(886291, 741.4634f, 807.8727f, 332.6250f, (byte) 42);
					spawn(886291, 759.3539f, 771.9392f, 332.5442f, (byte) 105);
					spawn(886291, 731.7290f, 812.6438f, 332.6250f, (byte) 111);
					spawn(886291, 775.0922f, 847.4466f, 335.4423f, (byte) 34);
					spawn(886291, 745.1180f, 825.0708f, 332.6250f, (byte) 29);
					spawn(886291, 737.7133f, 770.2884f, 332.5702f, (byte) 92);
					spawn(886291, 708.9195f, 796.8077f, 332.5940f, (byte) 81);
					spawn(886291, 726.3558f, 839.7587f, 335.4102f, (byte) 37);
					spawn(886291, 765.2458f, 802.2760f, 332.6250f, (byte) 0);
					spawn(886291, 777.0191f, 870.2096f, 331.7500f, (byte) 58);
				}
			}
		}
	}
	
	private void LDF7KillerS1513() {
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