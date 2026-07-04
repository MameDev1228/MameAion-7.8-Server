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
package ai.instance.theVeilenthrone;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.skillengine.SkillEngine;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Arbo_Soul")
public class Arbo_SoulAI2 extends NpcAI2
{
	private Future<?> shieldOfTheSoul;
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startShieldOfTheSoul();
	}
	
	private void startShieldOfTheSoul() {
		shieldOfTheSoul = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					SkillEngine.getInstance().getSkill(getOwner(), 17271, 60, getOwner()).useNoAnimationSkill();
					spawn(656420, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
				}
			}
		}, 3000, 60000);
	}
	
	private void cancelTask() {
		if (shieldOfTheSoul != null && !shieldOfTheSoul.isDone()) {
			shieldOfTheSoul.cancel(true);
		}
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}
}