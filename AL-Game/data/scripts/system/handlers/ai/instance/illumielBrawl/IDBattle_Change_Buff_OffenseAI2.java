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
package ai.instance.illumielBrawl;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.handler.CreatureEventHandler;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("IDBattle_Change_Buff_Offense")
public class IDBattle_Change_Buff_OffenseAI2 extends AggressiveNpcAI2
{
	@Override
    protected void handleCreatureMoved(Creature creature) {
        CreatureEventHandler.onCreatureSee(this, creature);
    	if (creature instanceof Player) {
			final Player player = (Player) creature;
    		if (!creature.getEffectController().hasAbnormalEffect(17801)) { //Asmodian Bonus Damage.
				if (player.getCommonData().getRace() == Race.ELYOS) {
    		        SkillEngine.getInstance().getSkill(getOwner(), 17801, 1, (Player) creature).useNoAnimationSkill(); //Asmodian Bonus Damage.
				}
			} else if (!creature.getEffectController().hasAbnormalEffect(17800)) { //Elyos Bonus Damage.
				if (player.getCommonData().getRace() == Race.ASMODIANS) {
    		        SkillEngine.getInstance().getSkill(getOwner(), 17800, 1, (Player) creature).useNoAnimationSkill(); //Elyos Bonus Damage.
				}
			}
    	}
    }
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		despawn();
	}
	
	private void despawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				getOwner().getController().onDelete();
			}
		}, 60000);
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}