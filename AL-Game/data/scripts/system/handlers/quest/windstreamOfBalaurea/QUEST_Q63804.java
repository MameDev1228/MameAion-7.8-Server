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
package quest.windstreamOfBalaurea;

import com.aionemu.gameserver.model.Race;
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

public class QUEST_Q63804 extends QuestHandler
{
	private final static int questId = 63804;
	private final static int[] npcs = {820921};
	
	private final static int[] LF4_Hun_Big = {662863, 662864};
	private final static int[] LF4_Hunt_Small = {662860, 662861, 662862};
	private final static int[] LF4_Dreadgion_Pod_Dr = {662103, 662104, 662105, 662106};
	
	private final static int[] Underpass_Octacide = {656189, 656194, 656201};
	private final static int[] Underpass_Scorpion = {656188, 656190, 656195};
	private final static int[] Underpass_Drakan = {656196, 656197, 656199, 656200};
	
	private final static int[] LF4_Treasure_Spawn = {
	661640, 661641, 661642, 661643, 661644, 661645, 661646, 661647,
	661648, 661649, 661650, 661651, 661652, 661653, 661654, 661655};
	
	public QUEST_Q63804() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LF4_Hun_Big) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Hunt_Small) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Dreadgion_Pod_Dr) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Treasure_Spawn) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Octacide) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Scorpion) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Underpass_Drakan) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
	}
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ELYOS && player.getLevel() == 80) {
            QuestService.startQuest(env);
            return true;
        }
        return false;
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
            if (targetId == 820921) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
					} case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
                        changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820921) {
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
					case 662860:
					case 662861:
					case 662862:
						if (var1 < 0) {
							return defaultOnKillEvent(env, LF4_Hunt_Small, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_Hunt_Small, 0, 1, 1);
							}
						}
					break;
					case 662863:
					case 662864:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LF4_Hun_Big, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_Hun_Big, 0, 1, 2);
							}
						}
					break;
				}
			} else if (var == 2) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, LF4_Treasure_Spawn, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 3) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 9) {
                    return defaultOnKillEvent(env, LF4_Dreadgion_Pod_Dr, var1, var1 + 1, 1);
                } else if (var1 == 9) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 5) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 5) {
                    return defaultOnKillEvent(env, Underpass_Octacide, var1, var1 + 1, 1);
                } else if (var1 == 5) {
					qs.setQuestVar(6);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 6) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 5) {
                    return defaultOnKillEvent(env, Underpass_Scorpion, var1, var1 + 1, 1);
                } else if (var1 == 5) {
					qs.setQuestVar(7);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 7) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 4) {
                    return defaultOnKillEvent(env, Underpass_Drakan, var1, var1 + 1, 1);
                } else if (var1 == 4) {
					qs.setStatus(QuestStatus.REWARD);
					QuestService.finishQuest(env);
                    return true;
                }
            }
		}
		return false;
	}
}