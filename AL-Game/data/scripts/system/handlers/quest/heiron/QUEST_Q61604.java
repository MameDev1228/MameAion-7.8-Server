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
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q61604 extends QuestHandler
{
	private final static int questId = 61604;
	private final static int[] LabPo_Slime_41_An = {652821};
	private final static int[] LabPo_LockedKuillus_40_An = {652822};
	
	public QUEST_Q61604() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: LabPo_Slime_41_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LabPo_LockedKuillus_40_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(204576).addOnQuestStart(questId);
		qe.registerQuestNpc(204576).addOnTalkEvent(questId);
		qe.registerQuestNpc(204606).addOnTalkEvent(questId);
		qe.registerQuestNpc(820021).addOnTalkEvent(questId);
		qe.registerQuestNpc(820062).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204576) {
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
			if (targetId == 204606) {
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
            } if (targetId == 820062) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
					} case SET_REWARD: {
                        qs.setQuestVar(3);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						TeleportService2.teleportTo(env.getPlayer(), 210040000, 395.0000f, 716.0000f, 127.0000f, (byte) 0);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820021) {
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
			if (var == 1) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 652821:
						if (var1 < 2) {
							return defaultOnKillEvent(env, LabPo_Slime_41_An, 0, 2, 1);
						} else if (var1 == 2) {
							if (var2 == 3) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LabPo_Slime_41_An, 2, 3, 1);
							}
						}
					break;
					case 652822:
						if (var2 < 2) {
							return defaultOnKillEvent(env, LabPo_LockedKuillus_40_An, 0, 2, 2);
						} else if (var2 == 2) {
							if (var1 == 3) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LabPo_LockedKuillus_40_An, 2, 3, 2);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}