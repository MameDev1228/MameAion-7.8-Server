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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q63003 extends QuestHandler
{
	private final static int questId = 63003;
	private final static int[] LDF8_Quest_SouledStone_80_An = {838031};
	
	public QUEST_Q63003() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: LDF8_Quest_SouledStone_80_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestItem(182216794, questId);
		qe.registerQuestItem(182216841, questId);
		qe.registerQuestItem(182216873, questId);
		qe.registerQuestItem(182216874, questId);
		qe.registerQuestNpc(820394).addOnQuestStart(questId);
		qe.registerQuestNpc(820394).addOnTalkEvent(questId);
		qe.registerQuestNpc(806999).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ITEMUSEAREA_Q63003C"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 820394) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						return sendQuestStartDialog(env, 182216794, 1);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 820394) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        } else if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_4: {
						giveQuestItem(env, 182216841, 1);
                        changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
                        qs.setQuestVar(7);
                        qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						giveQuestItem(env, 182216795, 1);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 806999) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182216795, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
	
	@Override
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        int id = item.getItemTemplate().getTemplateId();
        if (id == 182216794) {
			if (var == 0) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
				giveQuestItem(env, 182216873, 1);
				removeQuestItem(env, 182216794, 1);
				return HandlerResult.SUCCESS;
			}
		} else if (id == 182216873) {
			if (var == 1) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				giveQuestItem(env, 182216874, 1);
				removeQuestItem(env, 182216873, 1);
				return HandlerResult.SUCCESS;
			}
		} else if (id == 182216874) {
			if (var == 2) {
				qs.setQuestVar(3);
				updateQuestStatus(env);
				removeQuestItem(env, 182216874, 1);
				return HandlerResult.SUCCESS;
			}
		} else if (id == 182216841) {
            if (var == 4 && player.isInsideZone(ZoneName.get("ITEMUSEAREA_Q63003C"))) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
				removeQuestItem(env, 182216841, 1);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			if (var == 5) {
                if (var1 >= 0 && var1 < 9) {
                    return defaultOnKillEvent(env, LDF8_Quest_SouledStone_80_An, var1, var1 + 1, 1);
                } else if (var1 == 9) {
					qs.setQuestVar(6);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}