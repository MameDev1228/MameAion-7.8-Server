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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetrificationEffect2")
public class PetrificationEffect2 extends AbstractOverTimeEffect
{
    @Override
    public void applyEffect(Effect effect) {
        if (!effect.getEffected().getEffectController().hasPhysicalStateEffect() && !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE)) {
			effect.addToEffectedController();
			effect.setIsPhysicalState(true);
		}
    }
	
    @Override
    public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.PETRIFICATION_RESISTANCE, null);
    }
	
    @Override
    public void startEffect(Effect effect) {
        final Creature effected = effect.getEffected();
		if (effected.isInState(CreatureState.RESTING)) {
        	effected.unsetState(CreatureState.RESTING);
		}
		effected.getMoveController().abortMove();
		effected.getController().cancelCurrentSkill();
		effected.getEffectController().setAbnormal(AbnormalState.PETRIFICATION.getId());
		effect.setAbnormal(AbnormalState.PETRIFICATION.getId());
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
    }
	
    @Override
    public void endEffect(Effect effect) {
		effect.setIsPhysicalState(false);
        effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.PETRIFICATION.getId());
    }
}