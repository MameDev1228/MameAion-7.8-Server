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
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q61930 extends QuestHandler
{
	private final static int questId = 61930;
	
	private final static int[] LDF5a_HyperionNamed_Light_Q = {661572};
	
	public QUEST_Q61930() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: LDF5a_HyperionNamed_Light_Q) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(798926).addOnQuestStart(questId);
		qe.registerQuestNpc(798926).addOnTalkEvent(questId);
		qe.registerQuestNpc(839957).addOnTalkEvent(questId);
		qe.registerQuestNpc(836540).addOnTalkEvent(questId);
		qe.registerQuestNpc(806977).addOnTalkEvent(questId);
		qe.registerQuestNpc(820546).addOnTalkEvent(questId);
		qe.registerQuestNpc(820547).addOnTalkEvent(questId);
		qe.registerQuestNpc(820548).addOnTalkEvent(questId);
		qe.registerQuestNpc(820549).addOnTalkEvent(questId);
		qe.registerQuestNpc(820550).addOnTalkEvent(questId);
		qe.registerQuestNpc(820551).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("KAISINELS_BEACON_800030000"), questId);
		qe.registerOnEnterZone(ZoneName.get("OUTPOST_OF_VICTORY_800060000"), questId);
		qe.registerOnEnterZone(ZoneName.get("ANCIENT_SANCTUM_OF_LIFE_800050000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798926) {
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
            if (targetId == 839957) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_1: {
						playQuestMovie(env, 550);
						giveQuestItem(env, 182217054, 1); //거대 병기의 금속 파편.
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 836540) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 806977) {
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
            } if (targetId == 820546 || targetId == 820547 || targetId == 820548 || targetId == 820549 ||  targetId == 820550 || targetId == 820551) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 7) {
                            return sendQuestDialog(env, 3398);
                        }
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798926) {
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
			if (var == 8) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, LDF5a_HyperionNamed_Light_Q, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setQuestVar(9);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("ANCIENT_SANCTUM_OF_LIFE_800050000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("OUTPOST_OF_VICTORY_800060000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("KAISINELS_BEACON_800030000")) {
				if (var == 6) {
					qs.setQuestVar(7);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}