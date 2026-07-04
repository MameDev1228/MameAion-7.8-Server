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
package ai.instance.tiamatStronghold;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.List;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Liberated_Primeth2")
public class Liberated_Primeth2AI2 extends GeneralNpcAI2
{
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		moveToLever();
	}
	
	private void moveToLever() {
		setStateIfNot(AIState.WALKING);
		getOwner().setState(1);
		getMoveController().moveToPoint(781, 934, 697);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		ThreadPoolManager.getInstance().schedule(new Runnable() {
		    @Override
		    public void run() {
		  	    startNpcShout();
		    }
	    }, 2000);
	}
	
	private void startNpcShout() {
		//It's time to pay back those who helped me. I'm going to open the path for them.
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1502014, getOwner().getObjectId(), 0, 4000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
		    @Override
		    public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), 17389, 60, getOwner()).useNoAnimationSkill();
				}
		    }
	    }, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
		    @Override
		    public void run() {
				if (!isAlreadyDead()) {
					getPosition().getWorldMapInstance().getDoors().get(213).setOpen(true);
					WorldMapInstance instance = getPosition().getWorldMapInstance();
					deleteNpcs(instance.getNpcs(701156));
				}
		    }
	    }, 7000);
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
}