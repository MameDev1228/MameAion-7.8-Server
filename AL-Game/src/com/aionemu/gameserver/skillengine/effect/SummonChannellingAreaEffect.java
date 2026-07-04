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

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.*;

import javax.xml.bind.annotation.*;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonChannellingAreaEffect")
public class SummonChannellingAreaEffect extends SummonServantEffect
{
	@Override
	public void applyEffect(Effect effect) {
		if (effect.getEffector().getTarget() == null) {
			effect.getEffector().setTarget(effect.getEffector());
		} if (effect.getEffected() != effect.getEffector() && effect.getEffector() instanceof Player) {
			//You used [%SkillName]. The effect continues during the skill’s duration.
            PacketSendUtility.sendPacket((Player) effect.getEffector(), new SM_SYSTEM_MESSAGE(1405079, new DescriptionId(effect.getSkillTemplate().getNameId())));
        }
		float x = effect.getX();
		float y = effect.getY();
		float z = effect.getZ();
		if (x == 0 && y == 0) {
			Creature effected = effect.getEffected();
			x = effected.getX();
			y = effected.getY();
			z = effected.getZ();
		}
		int useTime = time;
		switch (effect.getSkillId()) {
			//Electric Paint 7.x
			case 5421:
			case 5422:
			case 5423:
			case 5424:
			case 5425:
			case 5426:
			    useTime = 7;
			break;
		}
		final Servant servant = spawnServant(effect, useTime, NpcObjectType.SKILLAREA, x, y, z);
		final int finalSkillId = servant.getSkillList() != null ? servant.getSkillList().getRandomSkill().getSkillId() : 0;
		Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				servant.getController().useSkill(finalSkillId);
			}
		}, 0, 1000);
		servant.getController().addTask(TaskId.SKILL_USE, task);
	}
}