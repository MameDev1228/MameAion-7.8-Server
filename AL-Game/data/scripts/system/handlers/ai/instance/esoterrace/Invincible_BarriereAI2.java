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
package ai.instance.esoterrace;

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

@AIName("Invincible_Barriere")
public class Invincible_BarriereAI2 extends NpcAI2
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
			if (MathUtil.isIn3dRange(getOwner(), creature, 8)) {
				if (!player.isGM()) {
					returnStart(player);
				}
			}
		}
	}
	
	private void returnStart(Player player) {
		int instanceId = getPosition().getInstanceId();
		switch (player.getWorldId()) {
			case 302430000: //Primeth's Forge.
				TeleportService2.teleportTo(player, 302430000, instanceId, 1014.0000f, 1090.0000f, 624.0000f, (byte) 0, TeleportAnimation.NO_ANIMATION);
			break;
			case 302630000: //Primeth's Forge [Hard].
				TeleportService2.teleportTo(player, 302630000, instanceId, 1014.0000f, 1090.0000f, 624.0000f, (byte) 0, TeleportAnimation.NO_ANIMATION);
			break;
			case 302660000: //Benirunerk's Estate.
			    TeleportService2.teleportTo(player, 302660000, instanceId, 631.0000f, 461.0000f, 169.0000f, (byte) 1, TeleportAnimation.NO_ANIMATION);
			break;
			case 302690000: //Benirunerk's Estate [Easy].
			    TeleportService2.teleportTo(player, 302690000, instanceId, 631.0000f, 461.0000f, 169.0000f, (byte) 1, TeleportAnimation.NO_ANIMATION);
			break;
		}
    }
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		getOwner().getController().onDelete();
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}