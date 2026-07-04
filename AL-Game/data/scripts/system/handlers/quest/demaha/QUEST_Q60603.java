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
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q60603 extends QuestHandler
{
    public static final int questId = 60603;
	private final static int[] npcs = {806983, 806984, 806985, 806997, 806998, 806999, 731926, 731927};
	private final static int[] LDF8_G_M_Shulack_Guard_L_80_Ae = {657426};
	private final static int[] LDF8_F_M_Zombie_L_80_An = {657427, 657428};
	
    public QUEST_Q60603() {
        super(questId);
    }
	
    @Override
    public void register() {
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LDF8_G_M_Shulack_Guard_L_80_Ae) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF8_F_M_Zombie_L_80_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182216752, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
    }
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 60601, true);
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 806997) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182216752, 1);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 806999) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        } else if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						} else if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_3: {
                        changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
                        changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
                        changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
                }
            } if (targetId == 806998) {
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
            } if (targetId == 806983) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 7) {
                            return sendQuestDialog(env, 3398);
                        } else if (var == 12) {
                            return sendQuestDialog(env, 7182);
                        }
					} case STEP_TO_8: {
                        changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case STEP_TO_13: {
                        changeQuestStep(env, 12, 13, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 731926) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 9) {
                            return sendQuestDialog(env, 4080);
                        }
					} case STEP_TO_10: {
                        changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 731927) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 10) {
                            return sendQuestDialog(env, 6500);
                        }
					} case STEP_TO_11: {
                        changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 806984) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 11) {
                            return sendQuestDialog(env, 6841);
                        }
					} case STEP_TO_12: {
						giveQuestItem(env, 182216754, 1);
                        changeQuestStep(env, 11, 12, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 806985) {
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
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 1) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false));
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
			if (var == 8) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, LDF8_G_M_Shulack_Guard_L_80_Ae, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(9);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 13) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 9) {
					return defaultOnKillEvent(env, LDF8_F_M_Zombie_L_80_An, var1, var1 + 1, 1);
				} else if (var1 == 9) {
					qs.setStatus(QuestStatus.REWARD);
					QuestService.finishQuest(env);
					return true;
				}
			}
		}
		return false;
	}
}