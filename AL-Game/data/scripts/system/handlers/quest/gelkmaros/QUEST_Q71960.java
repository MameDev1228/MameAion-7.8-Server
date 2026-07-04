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

public class QUEST_Q71960 extends QuestHandler
{
    private final static int questId = 71960;
	
	private final static int[] DF4_ElementalWater4_Curse_Named_45_Ae = {657904};
	private final static int[] DF4_Spakle_DR_Nmd_54_An = {650299};
	private final static int[] DF4_DrakanFi_Re_Nmd_54_An = {650296};
	private final static int[] DF4_HolyServant_Named_50_Ae = {657905};
	private final static int[] DF4_DrakanWi_Re_Nmd_54_An = {650302};
	private final static int[] DF4_DaQ_FanaticRa_54 = {650320};
	private final static int[] DF4_FanaticAs_Named_55_Ae = {657906};
	private final static int[] DF4_DayQ_DrakanFi_Re_55_An = {650390};
	
    public QUEST_Q71960() {
        super(questId);
    }
	
	@Override
	public void register() {
		for (int mob: DF4_ElementalWater4_Curse_Named_45_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_Spakle_DR_Nmd_54_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_DrakanFi_Re_Nmd_54_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_HolyServant_Named_50_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_DrakanWi_Re_Nmd_54_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_DaQ_FanaticRa_54) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_FanaticAs_Named_55_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_DayQ_DrakanFi_Re_55_An) {
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
				int var4 = qs.getQuestVarById(4);
				switch (targetId) {
					case 657904:
						if (var1 < 0) {
							return defaultOnKillEvent(env, DF4_ElementalWater4_Curse_Named_45_Ae, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(1);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_ElementalWater4_Curse_Named_45_Ae, 0, 1, 1);
							}
						}
					break;
					case 650299:
						if (var2 < 0) {
							return defaultOnKillEvent(env, DF4_Spakle_DR_Nmd_54_An, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(1);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_Spakle_DR_Nmd_54_An, 0, 1, 2);
							}
						}
					break;
					case 650296:
						if (var3 < 0) {
							return defaultOnKillEvent(env, DF4_DrakanFi_Re_Nmd_54_An, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1 && var4 == 1) {
								qs.setQuestVar(1);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_DrakanFi_Re_Nmd_54_An, 0, 1, 3);
							}
						}
					break;
					case 657905:
						if (var4 < 0) {
							return defaultOnKillEvent(env, DF4_HolyServant_Named_50_Ae, 0, 0, 4);
						} else if (var4 == 0) {
							if (var1 == 1 && var2 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_HolyServant_Named_50_Ae, 0, 1, 4);
							}
						}
					break;
				}
			} else if (var == 1) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				switch (targetId) {
					case 650302:
						if (var1 < 0) {
							return defaultOnKillEvent(env, DF4_DrakanWi_Re_Nmd_54_An, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_DrakanWi_Re_Nmd_54_An, 0, 1, 1);
							}
						}
					break;
					case 650320:
						if (var2 < 0) {
							return defaultOnKillEvent(env, DF4_DaQ_FanaticRa_54, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_DaQ_FanaticRa_54, 0, 1, 2);
							}
						}
					break;
					case 657906:
						if (var3 < 0) {
							return defaultOnKillEvent(env, DF4_FanaticAs_Named_55_Ae, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1 && var4 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_FanaticAs_Named_55_Ae, 0, 1, 3);
							}
						}
					break;
					case 650390:
						if (var4 < 0) {
							return defaultOnKillEvent(env, DF4_DayQ_DrakanFi_Re_55_An, 0, 0, 4);
						} else if (var4 == 0) {
							if (var1 == 1 && var2 == 1 && var3 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_DayQ_DrakanFi_Re_55_An, 0, 1, 4);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}