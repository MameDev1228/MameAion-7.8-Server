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
package quest.altarOfAscension;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q73830 extends QuestHandler
{
	private final static int questId = 73830;
	
	private final static int[] Dragon_Altar_Mortasha = {858716};
	
	public QUEST_Q73830() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: Dragon_Altar_Mortasha) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestNpc(840020).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("BESPERA_PORT_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("SEVENTH_ALTAR_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("ASHWIND_CLIFF_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("THE_BURNING_PATH_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_DRAGON_ALTAR_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("CIRCLE_OF_REBIRTH_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("VALLEY_PRISON_CAMP_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("TENTH_ALTAR_OPEN_SPACE_302810000"), questId);
		qe.registerOnEnterZone(ZoneName.get("VOLCANO_RUINS_EXCAVATION_SITE_302810000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 840020) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("IDF8_DRAGON_ALTAR_302810000")) {
			if (qs == null || qs.canRepeat() || qs.getStatus() == QuestStatus.NONE) {
				env.setQuestId(questId);
				if (QuestService.startQuest(env)) {
					return true;
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("SEVENTH_ALTAR_302810000")) {
				if (var == 0) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
					return true;
				}
			} if (zoneName == ZoneName.get("BESPERA_PORT_302810000") || zoneName == ZoneName.get("ASHWIND_CLIFF_302810000") ||
			    zoneName == ZoneName.get("THE_BURNING_PATH_302810000") || zoneName == ZoneName.get("CIRCLE_OF_REBIRTH_302810000") ||
				zoneName == ZoneName.get("VALLEY_PRISON_CAMP_302810000") || zoneName == ZoneName.get("VOLCANO_RUINS_EXCAVATION_SITE_302810000")) {
				if (var == 1) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
					return true;
				}
			} if (zoneName == ZoneName.get("TENTH_ALTAR_OPEN_SPACE_302810000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Dragon_Altar_Mortasha, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(4);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}