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
package quest.demaha;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q73013 extends QuestHandler
{
	private final static int questId = 73013;
	
	public QUEST_Q73013() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(820411).addOnQuestStart(questId);
		qe.registerQuestNpc(820411).addOnTalkEvent(questId);
		qe.registerQuestNpc(820436).addOnTalkEvent(questId);
		qe.registerQuestNpc(820437).addOnTalkEvent(questId);
		qe.registerQuestNpc(820438).addOnTalkEvent(questId);
		qe.registerQuestNpc(820439).addOnTalkEvent(questId);
		qe.registerQuestNpc(820440).addOnTalkEvent(questId);
		qe.registerQuestNpc(820441).addOnTalkEvent(questId);
		qe.registerQuestNpc(820442).addOnTalkEvent(questId);
		qe.registerQuestNpc(820443).addOnTalkEvent(questId);
		qe.registerQuestNpc(820444).addOnTalkEvent(questId);
		qe.registerQuestNpc(820445).addOnTalkEvent(questId);
		qe.registerQuestNpc(820446).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 820411) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						return sendQuestStartDialog(env, 182216840, 11);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
            if (targetId == 820436) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820437) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820438) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820439) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820440) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820441) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820442) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_7: {
						changeQuestStep(env, 6, 7, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820443) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 7) {
                            return sendQuestDialog(env, 3398);
                        }
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820444) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 8) {
                            return sendQuestDialog(env, 3739);
                        }
					} case STEP_TO_9: {
						changeQuestStep(env, 8, 9, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820445) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 9) {
                            return sendQuestDialog(env, 4080);
                        }
					} case STEP_TO_10: {
						changeQuestStep(env, 9, 10, false);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820446) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 10) {
                            return sendQuestDialog(env, 6500);
                        }
					} case SET_REWARD: {
						qs.setQuestVar(11);
                        qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						removeQuestItem(env, 182216840, 1);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820411) {
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
}