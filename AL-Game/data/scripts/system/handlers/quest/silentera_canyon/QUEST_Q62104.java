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

public class QUEST_Q62104 extends QuestHandler
{
    private final static int questId = 62104;
	
	private final static int[] Underpass_DrakanWi_55_Ae = {656177, 656196, 656197, 656199, 656200};
	
	private final static int[] Underpass_NepilimWalker_55_Ah = {656191, 656192};
	
	private final static int[] Underpass_FanaticKnknee_55_Ae = {
	656178, 656179, 656180, 656181, 656182, 656183, 656184, 656185,
	656186, 656202, 656203, 656204, 656205, 656206, 656210, 656211,
	656212, 656214, 656216, 656217, 656218, 656219, 656220, 656221, 656223, 656225};
	
    public QUEST_Q62104() {
        super(questId);
    }
	
	@Override
	public void register() {
		for (int mob: Underpass_NepilimWalker_55_Ah) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_FanaticKnknee_55_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_DrakanWi_55_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestNpc(839958).addOnQuestStart(questId);
		qe.registerQuestNpc(839958).addOnTalkEvent(questId);
		qe.registerQuestNpc(799381).addOnTalkEvent(questId);
		qe.registerQuestNpc(799382).addOnTalkEvent(questId);
		qe.registerQuestNpc(839955).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 839958) {
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
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 799381) {
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
            } if (targetId == 799382) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 839955) {
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
			if (var == 2) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 9) {
                    return defaultOnKillEvent(env, Underpass_NepilimWalker_55_Ah, var1, var1 + 1, 1);
                } else if (var1 == 9) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 3) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 9) {
                    return defaultOnKillEvent(env, Underpass_FanaticKnknee_55_Ae, var1, var1 + 1, 1);
                } else if (var1 == 9) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 4) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 9) {
                    return defaultOnKillEvent(env, Underpass_DrakanWi_55_Ae, var1, var1 + 1, 1);
                } else if (var1 == 9) {
					qs.setQuestVar(5);
                    qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}