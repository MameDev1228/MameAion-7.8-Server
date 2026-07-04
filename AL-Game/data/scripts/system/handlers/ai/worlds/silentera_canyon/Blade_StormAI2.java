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
package ai.worlds.silentera_canyon;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("bladestorm")
public class Blade_StormAI2 extends AggressiveNpcAI2
{
	private Future<?> stormBladeTask;
	
	@Override
	public void think() {
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		stormBlade();
		despawn();
	}
	
	private void stormBlade() {
		stormBladeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				AI2Actions.targetCreature(Blade_StormAI2.this, getPosition().getWorldMapInstance().getNpc(858868));
				AI2Actions.useSkill(Blade_StormAI2.this, 20748); //Storm Blade.
			}
		}, 3000, 8000);
	}
	
    private void despawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				getOwner().getController().onDelete();
			}
		}, 10000);
	}
	
    @Override
	public boolean isMoveSupported() {
		return false;
	}
}