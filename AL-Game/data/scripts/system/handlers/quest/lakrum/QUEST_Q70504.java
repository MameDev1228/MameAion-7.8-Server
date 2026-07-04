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
package quest.lakrum;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q70504 extends QuestHandler
{
    public static final int questId = 70504;
	private final static int[] npcs = {703559, 836563, 836564};
	private final static int[] LDF7_Da_Quest_Drakan_01 = {655458};
	private final static int[] LDF7_Da_Quest_Drakan_02 = {655459};
	private final static int[] LDF7_Da_Quest_Fanatic_03 = {655460};
	private final static int[] LDF7_FOBJ_Rim_Cart_Q70504A = {703558};
	
    public QUEST_Q70504() {
        super(questId);
    }
	
    @Override
    public void register() {
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LDF7_Da_Quest_Drakan_01) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF7_Da_Quest_Drakan_02) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF7_Da_Quest_Fanatic_03) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF7_FOBJ_Rim_Cart_Q70504A) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("CONCEALED_MINE_800050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LDF7_SENSORY_AREA_Q70504_B_800050000"), questId);
    }
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 70503, true);
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
            if (targetId == 836563) {
                switch (env.getDialog()) {
					case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182216290, 1); //대행자의 지원 물품.
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 836564) {
                switch (env.getDialog()) {
				    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVar(8);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182216291, 1); //용족 물자 보급 내역서.
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
            } if (targetId == 703559) {
                switch (env.getDialog()) {
					case USE_OBJECT: {
                        if (var == 6) {
                            return sendQuestDialog(env, 2716);
                        }
					} case STEP_TO_6: {
                        changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 836563) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182216290, 1); //대행자의 지원 물품.
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
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
			if (zoneName == ZoneName.get("CONCEALED_MINE_800050000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("LDF7_SENSORY_AREA_Q70504_B_800050000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					return true;
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
			if (var == 3) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 655458:
						if (var1 < 2) {
							return defaultOnKillEvent(env, LDF7_Da_Quest_Drakan_01, 0, 2, 1);
						} else if (var1 == 2) {
							if (var2 == 1) {
								qs.setQuestVar(4);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF7_Da_Quest_Drakan_01, 2, 3, 1);
							}
						}
					break;
					case 655459:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LDF7_Da_Quest_Drakan_02, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 3) {
								qs.setQuestVar(4);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF7_Da_Quest_Drakan_02, 0, 1, 2);
							}
						}
					break;
				}
			} else if (var == 5) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 655460:
						if (var1 < 2) {
							return defaultOnKillEvent(env, LDF7_Da_Quest_Fanatic_03, 0, 2, 1);
						} else if (var1 == 2) {
							if (var2 == 1) {
								qs.setQuestVar(6);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF7_Da_Quest_Fanatic_03, 2, 3, 1);
							}
						}
					break;
					case 703558:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LDF7_FOBJ_Rim_Cart_Q70504A, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 3) {
								qs.setQuestVar(6);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF7_FOBJ_Rim_Cart_Q70504A, 0, 1, 2);
							}
						}
					break;
				}
			}
        }
        return false;
    }
}