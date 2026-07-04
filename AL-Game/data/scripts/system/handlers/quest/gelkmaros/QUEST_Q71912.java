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
package quest.gelkmaros;

import com.aionemu.gameserver.model.gameobjects.Npc;
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

public class QUEST_Q71912 extends QuestHandler
{
	private final static int questId = 71912;
	private final static int[] DF4FungyDranaNmdQ54An = {650301};
	
	public QUEST_Q71912() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: DF4FungyDranaNmdQ54An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(799282).addOnQuestStart(questId);
		qe.registerQuestNpc(799282).addOnTalkEvent(questId);
		qe.registerQuestNpc(799289).addOnTalkEvent(questId);
		qe.registerQuestNpc(700727).addOnTalkEvent(questId);
		qe.registerQuestNpc(700728).addOnTalkEvent(questId);
		qe.registerQuestNpc(700729).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799282) {
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
            if (targetId == 799282) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        } else if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
					} case SET_REWARD: {
						qs.setQuestVar(5);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						giveQuestItem(env, 182216540, 1);
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							changeQuestStep(env, 0, 1, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
            } if (targetId == 700729) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 2) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							changeQuestStep(env, 2, 3, false);
							removeQuestItem(env, 182216540, 1);
							QuestService.addNewSpawn(220070000, 1, 282786, player.getX(), player.getY(), player.getZ(), (byte) 0);
							QuestService.addNewSpawn(220070000, 1, 650097, player.getX(), player.getY(), player.getZ(), (byte) 0);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 700727 || targetId == 700728) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return closeDialogWindow(env);
				}
			}
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799289) {
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
			if (var == 3) {
				switch (targetId) {
                    case 650301: {
						qs.setQuestVar(4);
						updateQuestStatus(env);
						return true;
					}
                }
			}
		}
		return false;
    }
}