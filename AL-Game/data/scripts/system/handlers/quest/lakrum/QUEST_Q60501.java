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
package quest.lakrum;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q60501 extends QuestHandler
{
    public static final int questId = 60501;
	private final static int[] npcs = {703535, 836623, 836542, 836543};
	private final static int[] LDF7_Li_Quest_Guard_Da_01 = {655436};
	private final static int[] LDF7_Li_Quest_Guard_Da_02 = {655437};
	
    public QUEST_Q60501() {
        super(questId);
    }
	
    @Override
    public void register() {
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LDF7_Li_Quest_Guard_Da_01) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF7_Li_Quest_Guard_Da_02) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
    }
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 60500, true);
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
            if (targetId == 836623) {
                switch (env.getDialog()) {
					case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 703535) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return closeDialogWindow(env);
				}
			} if (targetId == 836543) {
                switch (env.getDialog()) {
				    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setStatus(QuestStatus.REWARD);
							QuestService.finishQuest(env);
							removeQuestItem(env, 182216280, 2); //림 광석.
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
            }
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 836542) {
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
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 655436:
						if (var1 < 4) {
							return defaultOnKillEvent(env, LDF7_Li_Quest_Guard_Da_01, 0, 4, 1);
						} else if (var1 == 4) {
							if (var2 == 1) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF7_Li_Quest_Guard_Da_01, 4, 5, 1);
							}
						}
					break;
					case 655437:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LDF7_Li_Quest_Guard_Da_02, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 5) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF7_Li_Quest_Guard_Da_02, 0, 1, 2);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}