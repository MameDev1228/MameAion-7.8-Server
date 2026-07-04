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

public class QUEST_Q73803 extends QuestHandler
{
	private final static int questId = 73803;
	private final static int[] npcs = {839276};
	
	private final static int[] LF4_V05_Ettin_80_An = {662052};
	private final static int[] LF4_V04_Draky_Mark_80_Ae = {662031};
	private final static int[] LF4_V05_Agrint_Green_80_An = {662049};
	private final static int[] LF4_V04_Drake_Mark_80_Ae = {662032};
	private final static int[] LF4_Hunt_Small = {662860, 662861, 662862, 662863, 662864};
	
	private final static int[] LF4_Treasure_Spawn = {
	661640, 661641, 661642, 661643, 661644, 661645, 661646, 661647,
	661648, 661649, 661650, 661651, 661652, 661653, 661654, 661655};
	
	public QUEST_Q73803() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LF4_V05_Ettin_80_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_V04_Draky_Mark_80_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_V05_Agrint_Green_80_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_V04_Drake_Mark_80_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Hunt_Small) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Treasure_Spawn) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
	}
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ASMODIANS && player.getLevel() == 80) {
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
            if (targetId == 839276) {
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
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 839276) {
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
			if (var == 2) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				switch (targetId) {
					case 662052:
						if (var1 < 1) {
							return defaultOnKillEvent(env, LF4_V05_Ettin_80_An, 0, 1, 1);
						} else if (var1 == 1) {
							if (var2 == 2 && var3 == 2 && var4 == 2) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_V05_Ettin_80_An, 1, 2, 1);
							}
						}
					break;
					case 662031:
						if (var2 < 1) {
							return defaultOnKillEvent(env, LF4_V04_Draky_Mark_80_Ae, 0, 1, 2);
						} else if (var2 == 1) {
							if (var1 == 2 && var3 == 2 && var4 == 2) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_V04_Draky_Mark_80_Ae, 1, 2, 2);
							}
						}
					break;
					case 662049:
						if (var3 < 1) {
							return defaultOnKillEvent(env, LF4_V05_Agrint_Green_80_An, 0, 1, 3);
						} else if (var3 == 1) {
							if (var1 == 2 && var2 == 2 && var4 == 2) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_V05_Agrint_Green_80_An, 1, 2, 3);
							}
						}
					break;
					case 662032:
						if (var4 < 1) {
							return defaultOnKillEvent(env, LF4_V04_Drake_Mark_80_Ae, 0, 1, 4);
						} else if (var4 == 1) {
							if (var1 == 2 && var2 == 2 && var3 == 2) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_V04_Drake_Mark_80_Ae, 1, 2, 4);
							}
						}
					break;
				}
			} else if (var == 3) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, LF4_Hunt_Small, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 4) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, LF4_Treasure_Spawn, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setStatus(QuestStatus.REWARD);
					QuestService.finishQuest(env);
                    return true;
                }
            }
		}
		return false;
	}
}