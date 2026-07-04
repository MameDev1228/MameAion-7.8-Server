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

@AIName("Base_S1512")
public class Base_S1512AI2 extends AggressiveNpcAI2
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
					LDF7KillerS1512();
					AI2Actions.deleteOwner(this);
					AI2Actions.scheduleRespawn(this);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					//LDF7_1512_Dr_Killer_As.
					spawn(886290, 1047.7078f, 536.3777f, 305.1572f, (byte) 96);
					spawn(886290, 1050.8778f, 519.1271f, 304.3750f, (byte) 98);
					spawn(886290, 1057.3295f, 522.2035f, 304.3750f, (byte) 98);
					spawn(886290, 1041.8202f, 535.8086f, 304.5000f, (byte) 74);
					spawn(886290, 1051.4111f, 543.5780f, 304.5000f, (byte) 14);
					spawn(886290, 1064.2094f, 550.3382f, 304.3961f, (byte) 15);
					spawn(886290, 1035.8777f, 554.5436f, 304.5416f, (byte) 104);
					spawn(886290, 1031.1257f, 534.5643f, 304.5000f, (byte) 65);
					spawn(886290, 1051.1649f, 527.8542f, 304.4909f, (byte) 98);
					spawn(886290, 1066.6378f, 534.4066f, 304.3750f, (byte) 119);
					spawn(886290, 1035.9929f, 523.0907f, 304.5000f, (byte) 74);
					spawn(886290, 1052.5438f, 539.4629f, 304.5000f, (byte) 1);
					spawn(886290, 1040.8270f, 539.9207f, 304.5000f, (byte) 60);
					spawn(886290, 1043.1527f, 544.6553f, 304.5000f, (byte) 42);
					spawn(886290, 1046.7580f, 545.9081f, 304.5000f, (byte) 35);
				}
			}
		}
	}
	
	private void LDF7KillerS1512() {
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