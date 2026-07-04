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
package ai.worlds.demaha;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Race_Check_Elyos")
public class Race_Check_ElyosAI2 extends NpcAI2
{
	@Override
    protected void handleCreatureSee(Creature creature) {
        checkDistance(this, creature);
    }
	
    @Override
    protected void handleCreatureMoved(Creature creature) {
        checkDistance(this, creature);
    }
	
    private void checkDistance(NpcAI2 ai, Creature creature) {
		if (creature instanceof Player && !creature.getLifeStats().isAlreadyDead()) {
			final Player player = (Player) creature;
			if (MathUtil.isIn3dRange(getOwner(), creature, 15)) {
				if (player.getCommonData().getRace() == Race.ASMODIANS && !player.isGM()) {
					teleportOutsideE(player);
				}
			}
		}
	}
	
	private void teleportOutsideE(Player player) {
		PacketSendUtility.sendSys3Message(player, "\uE005", "You cannot enter here, it is reserved for Elyos!!!");
		switch (player.getWorldId()) {
			case 210050000: //Inggison.
				TeleportService2.teleportTo(player, 210050000, 1601.0000f, 284.0000f, 527.0000f, (byte) 46, TeleportAnimation.NO_ANIMATION);
			break;
			case 800050000: //Lakrum.
				TeleportService2.teleportTo(player, 800050000, 2013.0000f, 927.0000f, 256.0000f, (byte) 114, TeleportAnimation.NO_ANIMATION);
			break;
			case 800060000: //Demaha.
				TeleportService2.teleportTo(player, 800060000, 948.0000f, 1721.0000f, 715.0000f, (byte) 0, TeleportAnimation.NO_ANIMATION);
			break;
		}
    }
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}