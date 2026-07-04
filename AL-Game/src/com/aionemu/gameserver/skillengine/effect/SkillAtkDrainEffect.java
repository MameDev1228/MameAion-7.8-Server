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

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAttribute;

public class SkillAtkDrainEffect extends AbstractOverTimeEffect
{
    @XmlAttribute
    protected Func mode = Func.ADD;
	
    @XmlAttribute
    protected boolean shared = true;
	
    @XmlAttribute(name = "hp_percent")
    protected int hp_percent = 20;
	
    @Override
    public void applyEffect(Effect effect) {
        super.applyEffect(effect);
        if (hp_percent != 0) {
            effect.getEffector().getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, effect.getReserved1() * hp_percent / 100, effect.getSkillId(), SM_ATTACK_STATUS.LOG.SPELLATKDRAININSTANT);
        }
    }
	
    public boolean calculate(Effect effect, DamageType damageType) {
        if (!super.calculate(effect, null, null)) {
            return false;
        }
        int skillLvl = effect.getSkillLevel();
        int valueWithDelta = value + delta * skillLvl;
        ActionModifier modifier = getActionModifiers(effect);
        int accMod = this.accMod2 + this.accMod1 * skillLvl;
        int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * skillLvl;
        AttackUtil.calculateSkillResult(effect, valueWithDelta, modifier, this.getMode(), 0, accMod, this.critProbMod2, critAddDmg, true, shared, false, false);
        return true;
    }
	
    @Override
    public void endEffect(Effect effect) {
        super.endEffect(effect);
    }
	
    @Override
    public void startEffect(final Effect effect) {
        super.startEffect(effect);
    }
	
    @Override
    public void onPeriodicAction(Effect effect) {
        Creature effected = effect.getEffected();
        Creature effector = effect.getEffector();
        effected.getController().onAttack(effector, effect.getSkillId(), SM_ATTACK_STATUS.TYPE.DAMAGE, effect.getReservedInt(position), false, SM_ATTACK_STATUS.LOG.ATTACK);
        effected.getObserveController().notifyDotAttackedObservers(effector, effect);
    }
	
    public Func getMode() {
        return mode;
    }
}