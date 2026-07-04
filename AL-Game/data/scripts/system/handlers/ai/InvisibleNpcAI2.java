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
package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rinzler
 */

@AIName("invisible_npc")
public class InvisibleNpcAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	protected void handleSpawned() {
        super.handleSpawned();
		switch (getNpcId()) {
			case 651156:
			case 651162:
			case 651169:
			case 651175:
			case 651181:
			case 651188:
			case 651195:
			case 651201:
			case 651659:
			case 651665:
			case 651670:
			case 651676:
			case 651681:
			case 651686:
			case 651692:
			case 651698:
		        conquerorPassion();
			break;
		} switch (getNpcId()) {
			//Transidium Annex.
			case 277195:
			case 277208:
			case 277215:
			    beritraFavor();
			break;
		}
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().setVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()));
	}
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().unsetVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()));
    }
    
    @Override
	protected void handleTargetGiveup() {
    	super.handleTargetGiveup();
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().setVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()));
	}
	
	private void conquerorPassion() {
		SkillEngine.getInstance().getSkill(getOwner(), 20665, 60, getOwner()).useNoAnimationSkill(); //Conqueror's Passion.
	}
	private void beritraFavor() {
	    SkillEngine.getInstance().getSkill(getOwner(), 21135, 60, getOwner()).useNoAnimationSkill(); //Beritra's Favor.
	}
	
	@Override
    protected void handleDied() {
        super.handleDied();
	}
}