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

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealAndFireOnZeroHpEffect")
public class HealAndFireOnZeroHpEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		if (effect.getEffected() != effect.getEffector() && effect.getEffector() instanceof Player) {
			//You have received the effect of [%SkillName].
			//While the effect is active, you do not die if you take damage under a certain threshold.
			//Additionally you recover some of your HP.
            PacketSendUtility.sendPacket((Player) effect.getEffector(), new SM_SYSTEM_MESSAGE(1405096, new DescriptionId(effect.getSkillTemplate().getNameId())));
        }
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected() instanceof Player) {
			super.calculate(effect, null, null);
		}
	}
	
	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);
		final Player player = (Player) effect.getEffector();
		final int hpValue = value * effect.getSkillLevel();
		ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {
			@Override
			public void attacked(Creature creature) {
				if (effect.getEffected().getLifeStats().getCurrentHp() <= (int) (10 / 100f * effect.getEffected().getLifeStats().getMaxHp())) {
					removeSkill((Player) effect.getEffector());
					player.getController().onRestore(HealType.HP, hpValue);
					//The effect of [%SkillName] was activated and a portion of your HP has been healed.
					PacketSendUtility.sendPacket((Player) effect.getEffector(), new SM_SYSTEM_MESSAGE(1405080, new DescriptionId(effect.getSkillTemplate().getNameId())));
				}
			}
		};
		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}
	
	private void removeSkill(Player player) {
		//Resurrection Chroma.
		player.getEffectController().removeEffect(5569);
		player.getEffectController().removeEffect(5570);
		player.getEffectController().removeEffect(5571);
		player.getEffectController().removeEffect(5658);
		player.getEffectController().removeEffect(5659);
		player.getEffectController().removeEffect(5660);
		player.getEffectController().removeEffect(5661);
		player.getEffectController().removeEffect(5662);
		player.getEffectController().removeEffect(5663);
		player.getEffectController().removeEffect(5664);
		//Transcendent Chroma.
		player.getEffectController().removeEffect(5592);
		//Chromatic Resistance.
		player.getEffectController().removeEffect(5593);
		//Chromatic Aura.
		player.getEffectController().removeEffect(5594);
		//(Advanced) Chromatic Resistance.
		player.getEffectController().removeEffect(5838);
		//(Advanced) Transcendent Chroma.
		player.getEffectController().removeEffect(5839);
	}
}