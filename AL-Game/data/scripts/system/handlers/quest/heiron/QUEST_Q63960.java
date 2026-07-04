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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q63960 extends QuestHandler
{
	private final static int questId = 63960;
	private final static int[] FrillfaimamNM1st_37_An = {652650, 652653};
	private final static int[] FrillfaimamNM1st_38_An = {652654};
	private final static int[] Frillfaimam1st_37_An = {652651, 652652};
	
	public QUEST_Q63960() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: FrillfaimamNM1st_37_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: FrillfaimamNM1st_38_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: Frillfaimam1st_37_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(839767).addOnQuestStart(questId);
		qe.registerQuestNpc(839767).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.canRepeat() || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 839767) {
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
            if (targetId == 839767) {
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
					case 652650:
					case 652653:
						if (var1 < 3) {
							return defaultOnKillEvent(env, FrillfaimamNM1st_37_An, 0, 3, 1);
						} else if (var1 == 3) {
							if (var2 == 3 && var3 == 3) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, FrillfaimamNM1st_37_An, 3, 4, 1);
							}
						}
					break;
					case 652654:
						if (var2 < 2) {
							return defaultOnKillEvent(env, FrillfaimamNM1st_38_An, 0, 2, 2);
						} else if (var2 == 2) {
							if (var1 == 4 && var3 == 3) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, FrillfaimamNM1st_38_An, 2, 3, 2);
							}
						}
					break;
					case 652651:
					case 652652:
						if (var3 < 2) {
							return defaultOnKillEvent(env, Frillfaimam1st_37_An, 0, 2, 3);
						} else if (var3 == 2) {
							if (var1 == 4 && var2 == 3) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, Frillfaimam1st_37_An, 2, 3, 3);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}