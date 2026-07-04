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
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q61906 extends QuestHandler
{
	private final static int questId = 61906;
	private final static int[] LF4_B1_Rhinoce_NamedQ_54_N = {650735};
	private final static int[] LF4_B1_Baku_NamedQ_54_N = {650736};
	private final static int[] LF4_B1_Crestlich_NamedQ_54_N = {650737};
	
	public QUEST_Q61906() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: LF4_B1_Rhinoce_NamedQ_54_N) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B1_Baku_NamedQ_54_N) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B1_Crestlich_NamedQ_54_N) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(798981).addOnQuestStart(questId);
		qe.registerQuestNpc(798981).addOnTalkEvent(questId);
		qe.registerQuestNpc(798975).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798981) {
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
            if (targetId == 798975) {
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
			if (var == 0) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				switch (targetId) {
					case 650735:
						if (var1 < 0) {
							return defaultOnKillEvent(env, LF4_B1_Rhinoce_NamedQ_54_N, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B1_Rhinoce_NamedQ_54_N, 0, 1, 1);
							}
						}
					break;
					case 650736:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LF4_B1_Baku_NamedQ_54_N, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B1_Baku_NamedQ_54_N, 0, 1, 2);
							}
						}
					break;
					case 650737:
						if (var3 < 0) {
							return defaultOnKillEvent(env, LF4_B1_Crestlich_NamedQ_54_N, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B1_Crestlich_NamedQ_54_N, 0, 1, 3);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}