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

public class QUEST_Q61917 extends QuestHandler
{
	private final static int questId = 61917;
	private final static int[] LF4_B5_FanaticAs_53_An = {650595, 650596};
	private final static int[] LF4_B5_FanaticCh_53_An = {650599, 650600};
	private final static int[] LF4_B5_FanaticFi_53_An = {650593, 650594};
	private final static int[] LF4_B5_FanaticRa_53_An = {650597, 650598};
	
	public QUEST_Q61917() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: LF4_B5_FanaticAs_53_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B5_FanaticCh_53_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B5_FanaticFi_53_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B5_FanaticRa_53_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(798991).addOnQuestStart(questId);
		qe.registerQuestNpc(798991).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798991) {
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
            if (targetId == 798991) {
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
				int var4 = qs.getQuestVarById(4);
				switch (targetId) {
					case 650595:
					case 650596:
						if (var1 < 1) {
							return defaultOnKillEvent(env, LF4_B5_FanaticAs_53_An, 0, 1, 1);
						} else if (var1 == 1) {
							if (var2 == 2 && var3 == 2 && var4 == 2) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B5_FanaticAs_53_An, 1, 2, 1);
							}
						}
					break;
					case 650599:
					case 650600:
						if (var2 < 1) {
							return defaultOnKillEvent(env, LF4_B5_FanaticCh_53_An, 0, 1, 2);
						} else if (var2 == 1) {
							if (var1 == 2 && var3 == 2 && var4 == 2) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B5_FanaticCh_53_An, 1, 2, 2);
							}
						}
					break;
					case 650593:
					case 650594:
						if (var3 < 1) {
							return defaultOnKillEvent(env, LF4_B5_FanaticFi_53_An, 0, 1, 3);
						} else if (var3 == 1) {
							if (var1 == 2 && var2 == 2 && var4 == 2) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B5_FanaticFi_53_An, 1, 2, 3);
							}
						}
					break;
					case 650597:
					case 650598:
						if (var4 < 1) {
							return defaultOnKillEvent(env, LF4_B5_FanaticRa_53_An, 0, 1, 4);
						} else if (var4 == 1) {
							if (var1 == 2 && var2 == 2 && var3 == 2) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B5_FanaticRa_53_An, 1, 2, 4);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}