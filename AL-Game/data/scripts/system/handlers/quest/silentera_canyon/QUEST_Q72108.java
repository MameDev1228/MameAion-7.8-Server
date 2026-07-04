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
package quest.silentera_canyon;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q72108 extends QuestHandler
{
    private final static int questId = 72108;
	
	private final static int[] Underpass_Drakan_Knight_QuestNamed_01 = {661958};
	private final static int[] Underpass_Drakan_Knight_QuestNamed_02 = {661959};
	private final static int[] Underpass_Drakan_Knight_QuestNamed_03 = {661960};
	private final static int[] Underpass_Drakan_Knight_QuestNamed_04 = {661961};
	private final static int[] Underpass_Drakan_Knight_QuestNamed_05 = {661962};
	private final static int[] Underpass_Drakan_Knight_QuestNamed_06 = {661963};
	
    public QUEST_Q72108() {
        super(questId);
    }
	
	@Override
	public void register() {
		for (int mob: Underpass_Drakan_Knight_QuestNamed_01) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Drakan_Knight_QuestNamed_02) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Drakan_Knight_QuestNamed_03) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Drakan_Knight_QuestNamed_04) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Drakan_Knight_QuestNamed_05) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Drakan_Knight_QuestNamed_06) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestNpc(839956).addOnQuestStart(questId);
		qe.registerQuestNpc(839956).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.canRepeat() || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 839956) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 839956) {
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
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 0) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Underpass_Drakan_Knight_QuestNamed_04, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 1) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Underpass_Drakan_Knight_QuestNamed_03, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 2) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Underpass_Drakan_Knight_QuestNamed_02, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 3) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Underpass_Drakan_Knight_QuestNamed_01, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 4) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Underpass_Drakan_Knight_QuestNamed_06, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 5) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Underpass_Drakan_Knight_QuestNamed_05, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(6);
                    qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}