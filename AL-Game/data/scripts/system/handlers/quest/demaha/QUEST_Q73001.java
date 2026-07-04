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
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q73001 extends QuestHandler
{
	private final static int questId = 73001;
	private final static int[] LDF8_QuestGuard_Rega_Q73001 = {838023, 838024, 838025, 838026};
	private final static int[] LDF8_QuestGuard_Snoowa_Q73001 = {838027, 838028, 838029, 838030};
	
	public QUEST_Q73001() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(806986).addOnQuestStart(questId);
		qe.registerQuestNpc(806986).addOnTalkEvent(questId);
		qe.registerQuestNpc(703774).addOnTalkEvent(questId);
		qe.registerQuestNpc(820409).addOnTalkEvent(questId);
		qe.registerQuestNpc(703775).addOnTalkEvent(questId);
		qe.registerQuestNpc(820410).addOnTalkEvent(questId);
		qe.registerQuestNpc(820408).addOnTalkEvent(questId);
		for (int mob: LDF8_QuestGuard_Rega_Q73001) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF8_QuestGuard_Snoowa_Q73001) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 806986) {
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
			if (targetId == 703774) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						//If it's this dark, something must have happened--just like the centurion said. I need to take a look around.
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806801, player.getObjectId(), 2));
						return useQuestObject(env, 0, 1, false, true);
					}
				}
			} if (targetId == 820409) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
					} case STEP_TO_3: {
                        changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 703775) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						//This is urgent. I need to take a look around.
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806802, player.getObjectId(), 2));
						return useQuestObject(env, 3, 4, false, true);
					}
				}
			} if (targetId == 820410) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case SET_REWARD: {
                        qs.setQuestVar(6);
                        qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820408) {
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
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 2) {
                    return defaultOnKillEvent(env, LDF8_QuestGuard_Rega_Q73001, var1, var1 + 1, 1);
                } else if (var1 == 2) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 4) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 2) {
                    return defaultOnKillEvent(env, LDF8_QuestGuard_Snoowa_Q73001, var1, var1 + 1, 1);
                } else if (var1 == 2) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}