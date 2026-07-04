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
package quest.stellin_development_lab;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q72820 extends QuestHandler
{
	private final static int questId = 72820;
	private final static int[] BIDLDF8_LAB_Boss_04_80_Ah = {858115};
	
	public QUEST_Q72820() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: BIDLDF8_LAB_Boss_04_80_Ah) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestItem(182216855, questId);
		qe.registerQuestNpc(838047).addOnQuestStart(questId);
		qe.registerQuestNpc(838047).addOnTalkEvent(questId);
		qe.registerQuestNpc(838048).addOnTalkEvent(questId);
		qe.registerQuestNpc(838052).addOnTalkEvent(questId);
		//Stellin Development Lab.
		qe.registerOnEnterZone(ZoneName.get("IDLDF8_LAB_SENSORY_AREA_C_Q62820_302550000"), questId);
		qe.registerOnEnterZone(ZoneName.get("PRODUCT_RESEARCH_ROOM_302550000"), questId);
		qe.registerOnEnterZone(ZoneName.get("NK_PRODUCTION_ROOM_302550000"), questId);
		qe.registerOnEnterZone(ZoneName.get("CLOSED_LABORATORY_302550000"), questId);
		//Stellin Development Lab [Easy]
		qe.registerOnEnterZone(ZoneName.get("IDLDF8_LAB_SENSORY_AREA_C_Q62820_302610000"), questId);
		qe.registerOnEnterZone(ZoneName.get("PRODUCT_RESEARCH_ROOM_302610000"), questId);
		qe.registerOnEnterZone(ZoneName.get("NK_PRODUCTION_ROOM_302610000"), questId);
		qe.registerOnEnterZone(ZoneName.get("CLOSED_LABORATORY_302610000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 838047) {
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
			if (targetId == 838048) {
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
            } if (targetId == 838052) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						giveQuestItem(env, 182216855, 1);
						return useQuestObject(env, 2, 3, false, false);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 838047) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182216856, 1);
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
			if (zoneName == ZoneName.get("IDLDF8_LAB_SENSORY_AREA_C_Q62820_302550000") ||
			    zoneName == ZoneName.get("IDLDF8_LAB_SENSORY_AREA_C_Q62820_302610000")) {
				if (var == 3) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("PRODUCT_RESEARCH_ROOM_302550000") ||
			    zoneName == ZoneName.get("PRODUCT_RESEARCH_ROOM_302610000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("NK_PRODUCTION_ROOM_302550000") ||
			    zoneName == ZoneName.get("NK_PRODUCTION_ROOM_302610000")) {
				if (var == 5) {
					qs.setQuestVar(6);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("CLOSED_LABORATORY_302550000") ||
			    zoneName == ZoneName.get("CLOSED_LABORATORY_302610000")) {
				if (var == 6) {
					qs.setQuestVar(7);
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
			if (var == 7) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, BIDLDF8_LAB_Boss_04_80_Ah, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(8);
					updateQuestStatus(env);
                    return true;
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
        final int id = item.getItemTemplate().getTemplateId();
        if (id == 182216855) {
            if (var == 8) {
				giveQuestItem(env, 182216856, 1);
				return HandlerResult.fromBoolean(useQuestItem(env, item, 8, 9, true));
			}
        }
        return HandlerResult.FAILED;
    }
}