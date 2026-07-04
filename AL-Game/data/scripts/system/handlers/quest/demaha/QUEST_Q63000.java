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
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q63000 extends QuestHandler
{
	private final static int questId = 63000;
	private final static int[] LDF8_QuestGuard_Q63000 = {838003, 838004, 838005, 838006, 838007};
	
	public QUEST_Q63000() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(806975).addOnQuestStart(questId);
		qe.registerQuestNpc(806975).addOnTalkEvent(questId);
		qe.registerQuestNpc(703764).addOnTalkEvent(questId);
		qe.registerQuestNpc(703765).addOnTalkEvent(questId);
		qe.registerQuestNpc(703766).addOnTalkEvent(questId);
		qe.registerQuestNpc(703767).addOnTalkEvent(questId);
		qe.registerQuestNpc(820387).addOnTalkEvent(questId);
		qe.registerQuestNpc(820429).addOnTalkEvent(questId);
		qe.registerQuestNpc(807007).addOnTalkEvent(questId);
		for (int mob: LDF8_QuestGuard_Q63000) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 806975) {
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
			if (targetId == 703764) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						//Normally, the light should be on… Strange.
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806797, player.getObjectId(), 2));
						return useQuestObject(env, 0, 1, false, true);
					}
				}
			} if (targetId == 703765) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						//Something's happened.
						PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_QUEST_SAY_Q63000_02, 0);
						return useQuestObject(env, 1, 2, false, true);
					}
				}
			} if (targetId == 703766) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						//Who's there?!
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806799, player.getObjectId(), 2));
						return useQuestObject(env, 2, 3, false, true);
					}
				}
			} if (targetId == 703767) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						//I have to hurry!
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806800, player.getObjectId(), 2));
						return useQuestObject(env, 3, 4, false, true);
					}
				}
			} if (targetId == 820387) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case STEP_TO_6: {
                        changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820429) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case SET_REWARD: {
                        //qs.setQuestVar(7);
                        //qs.setStatus(QuestStatus.REWARD);
						//updateQuestStatus(env);
						qs.setStatus(QuestStatus.REWARD);
					    QuestService.finishQuest(env);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 807007) {
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
			if (var == 4) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 2) {
                    return defaultOnKillEvent(env, LDF8_QuestGuard_Q63000, var1, var1 + 1, 1);
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