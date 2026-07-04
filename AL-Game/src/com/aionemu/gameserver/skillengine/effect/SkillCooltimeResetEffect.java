/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.HashMap;
import java.util.List;

/**
 * @author Rinzler
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillCooltimeResetEffect")
public class SkillCooltimeResetEffect extends EffectTemplate
{
    @XmlAttribute(name = "remove_cd", required = true)
    protected List<Integer> removeCd;
	
    @Override
    public void applyEffect(Effect effect) {
        Creature effected = effect.getEffected();
        long now = System.currentTimeMillis();
        HashMap<Integer, Long> resetSkillCoolDowns = new HashMap<Integer, Long>();
        if (removeCd == null || removeCd.isEmpty()) {
            return;
        }
        for (Integer delayId: removeCd) {
            if (delayId == null || delayId <= 0) {
                continue;
            }
            long currentEnd = effected.getSkillCoolDown(delayId);
            long remaining = currentEnd - now;
            if (remaining <= 0) {
                continue;
            }
            long newRemaining = remaining;
            if (delta > 0) {
                newRemaining = remaining - Math.round(remaining * (delta / 100.0d));
            } else if (value > 0) {
                newRemaining = remaining - value;
            }
            if (newRemaining <= 0) {
                effected.removeSkillCoolDown(delayId);
                resetSkillCoolDowns.put(delayId, 0L);
            } else {
                long newEnd = now + newRemaining;
                effected.setSkillCoolDown(delayId, newEnd);
                effected.setSkillCoolDownBase(delayId, now);
                resetSkillCoolDowns.put(delayId, newEnd);
            }
        }
        if (effected instanceof Player && resetSkillCoolDowns.size() > 0) {
            PacketSendUtility.sendPacket((Player) effected, new SM_SKILL_COOLDOWN((Player) effected, resetSkillCoolDowns, true));
        }
    }
}
