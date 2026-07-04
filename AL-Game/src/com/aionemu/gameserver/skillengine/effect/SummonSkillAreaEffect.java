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
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.concurrent.Future;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonSkillAreaEffect")
public class SummonSkillAreaEffect extends SummonServantEffect
{
	@Override
	public void applyEffect(Effect effect) {
		if (effect.getEffector().getTarget() == null) {
			effect.getEffector().setTarget(effect.getEffector());
		}
		float x = effect.getX();
		float y = effect.getY();
		float z = effect.getZ();
		if (x == 0 && y == 0 && z == 0) {
			Creature effected = effect.getEffected();
			x = effected.getX();
			y = effected.getY();
			z = effected.getZ();
		}
		int useTime = time;
		switch (effect.getSkillId()) {
			//Ice Sheet.
			case 1308:
			case 1309:
			case 1310:
			case 1311:
			case 1312:
			case 1313:
			case 1314:
			case 1315:
			case 1316:
			case 1317:
			case 1318:
			case 1319:
			case 1320:
			case 1321:
			case 1322:
			case 1323:
			//Stigma Purifier 7.x
			case 6121:
			case 6122:
			//IDBC_WI_ManaBoost.
			case 5212:
			case 5213:
			case 5315:
			case 5316:
			    useTime = 15;
			break;
			//Mounting Explosion.
			case 1431:
			case 1432:
			   useTime = 30;
			break;
			//Manifest Tornado.
			case 1460:
			case 1461:
			case 1462:
			case 1463:
			case 1464:
			case 1465:
			case 1466:
			case 1467:
			case 1468:
			case 1469:
			case 1470:
			case 1471:
			case 1472:
			case 1473:
			case 1474:
			case 1475:
			//Stigma Purifier 7.x
			case 6114:
			case 6115:
				useTime = 3;
			break;
			//Battle Call.
			case 3036:
			case 3037:
			    useTime = 11;
			break;
			//Field Of Lightning.
			case 4770:
			case 4771:
			case 4826:
			    useTime = 9;
			break;
			//Vandal 7.x
			case 5407:
			case 5408:
			case 5409:
			case 5410:
			case 5411:
			case 5412:
			case 5413:
			case 5414:
			case 5415:
			case 5416:
			case 5417:
			case 5418:
			case 5419:
			case 5420:
			case 5421:
			case 5422:
			case 5423:
			case 5424:
			case 5425:
			case 5426:
			case 5665:
			case 5666:
			case 5667:
			case 5668:
			case 5669:
			case 5670:
			case 5671:
			case 5672:
			case 5673:
			case 5674:
			//IDBC_WI_Summon_Tornado.
			case 5214:
			case 5317:
			case 5318:
			case 5319:
			case 5320:
			case 5321:
			//Stigma Purifier 7.x
			case 6279:
			case 6280:
			    useTime = 7;
			break;
			case 5528:
			case 5529:
			case 5530:
			case 5531:
			case 5532:
			case 5533:
			//Stigma Purifier 7.x
			case 6284:
			case 6285:
			    useTime = 12;
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