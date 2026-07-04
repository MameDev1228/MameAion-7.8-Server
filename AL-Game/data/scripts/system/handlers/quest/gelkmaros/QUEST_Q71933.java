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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q71933 extends QuestHandler
{
    private final static int questId = 71933;
	
	private final static int[] LF4_OP1_Boss_Heranas = {840190};
	private final static int[] LF4_Gateway_01_Guard_Boss = {840060};
	private final static int[] LF4_Gateway_02_Guard_Boss = {840067};
	private final static int[] LF4_Gateway_03_Guard_Boss = {840074};
	private final static int[] LF4_Gateway_04_Guard_Boss = {840081};
	private final static int[] LF4_Gateway_05_Guard_Boss = {840088};
	
    public QUEST_Q71933() {
        super(questId);
    }
	
	@Override
	public void register() {
		for (int mob: LF4_OP1_Boss_Heranas) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Gateway_01_Guard_Boss) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Gateway_02_Guard_Boss) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Gateway_03_Guard_Boss) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Gateway_04_Guard_Boss) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_Gateway_05_Guard_Boss) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestNpc(839971).addOnQuestStart(questId);
		qe.registerQuestNpc(839971).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 839971) {
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
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 839971) {
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
			if (var == 0) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				switch (targetId) {
					case 840060:
						if (var1 < 0) {
							return defaultOnKillEvent(env, LF4_Gateway_01_Guard_Boss, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_Gateway_01_Guard_Boss, 0, 1, 1);
							}
						}
					break;
					case 840067:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LF4_Gateway_02_Guard_Boss, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_Gateway_02_Guard_Boss, 0, 1, 2);
							}
						}
					break;
					case 840074:
						if (var3 < 0) {
							return defaultOnKillEvent(env, LF4_Gateway_03_Guard_Boss, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1) {
								qs.setQuestVar(1);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_Gateway_03_Guard_Boss, 0, 1, 3);
							}
						}
					break;
				}
			} else if (var == 1) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 840081:
						if (var1 < 0) {
							return defaultOnKillEvent(env, LF4_Gateway_04_Guard_Boss, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_Gateway_04_Guard_Boss, 0, 1, 1);
							}
						}
					break;
					case 840088:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LF4_Gateway_05_Guard_Boss, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_Gateway_05_Guard_Boss, 0, 1, 2);
							}
						}
					break;
				}
			} else if (var == 2) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, LF4_OP1_Boss_Heranas, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setQuestVar(3);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}