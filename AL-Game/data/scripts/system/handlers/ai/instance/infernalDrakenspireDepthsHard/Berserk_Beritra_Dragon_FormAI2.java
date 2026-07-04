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
package ai.instance.infernalDrakenspireDepthsHard;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Berserk_Beritra_Dragon_Form")
public class Berserk_Beritra_Dragon_FormAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					chooseAttack();
				}
			}
		}, 5000, 60000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 4)) {
			case 1:
			    drakenspireReaper();
			break;
			case 2:
			    drakenspireTomescale();
			break;
			case 3:
			    drakenspirePustule();
			break;
			case 4:
			    drakenspireProtector();
			break;
		}
	}
	
	private void drakenspireReaper() {
		//Do you really think you can stand against me?
		sendMsg(1501341, getObjectId(), false, 0);
		//Suffer in agony! Struggle in torment!
		sendMsg(1501342, getObjectId(), false, 3000);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				spawn(855444, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
	private void drakenspireTomescale() {
		//Wretched, wretched Daevas!
		sendMsg(1501343, getObjectId(), false, 0);
		//You're trying my patience!
		sendMsg(1501344, getObjectId(), false, 3000);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				spawn(855445, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
	private void drakenspirePustule() {
		//Creatures of darkness! Sing of death!
		sendMsg(1501345, getObjectId(), false, 0);
		//Behold! The power of the Dragon Lord of Darkness!
		sendMsg(1501346, getObjectId(), false, 3000);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				spawn(855446, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
	private void drakenspireProtector() {
		//Muahahaha! This is the end!
		sendMsg(1501348, getObjectId(), false, 0);
		//Behold! The power of the Dragon Lord of Darkness!
		sendMsg(1501346, getObjectId(), false, 3000);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				spawn(855452, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(855444));
		killNpc(instance.getNpcs(855445));
		killNpc(instance.getNpcs(855446));
		killNpc(instance.getNpcs(855452));
	}
	
	@Override
    protected void handleDied() {
        super.handleDied();
		cancelTask();
		//Uugh... Your strength... Is nothing!
		sendMsg(1501347, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(855444));
		killNpc(instance.getNpcs(855445));
		killNpc(instance.getNpcs(855446));
		killNpc(instance.getNpcs(855452));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}