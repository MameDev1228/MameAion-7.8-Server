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
package ai.instance.stellinDevelopmentLab;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Blinding_Light")
public class Blinding_LightAI2 extends NpcAI2
{
	private Npc draug;
	private Npc weakenedDraug;
	private Future<?> blindingLightTask;
	
	@Override
	public void think() {
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		draug = getPosition().getWorldMapInstance().getNpc(858112); //Draug.
		weakenedDraug = getPosition().getWorldMapInstance().getNpc(858274); //Weakened Draug.
		blindingLight();
	}
	
	private void blindingLight() {
		blindingLightTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (draug != null && !NpcActions.isAlreadyDead(draug)) {
					SkillEngine.getInstance().applyEffectDirectly(20561, getOwner(), draug, 0); //Blinding Light.
				} else if (weakenedDraug != null && !NpcActions.isAlreadyDead(weakenedDraug)) {
					SkillEngine.getInstance().applyEffectDirectly(20561, getOwner(), weakenedDraug, 0); //Blinding Light.
				}
			}
		}, 3000, 10000);
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		blindingLightTask.cancel(true);
	}
	
    @Override
	public boolean isMoveSupported() {
		return false;
	}
}