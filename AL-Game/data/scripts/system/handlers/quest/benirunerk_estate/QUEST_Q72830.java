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
package quest.benirunerk_estate;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q72830 extends QuestHandler
{
	private final static int questId = 72830;
	private final static int[] IDF8_House_HugeRider_80 = {858513};
	
	public QUEST_Q72830() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: IDF8_House_HugeRider_80) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestItem(182216945, questId);
		qe.registerQuestNpc(806988).addOnQuestStart(questId);
		qe.registerQuestNpc(806988).addOnTalkEvent(questId);
		qe.registerQuestNpc(806989).addOnTalkEvent(questId);
		qe.registerQuestNpc(838347).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_HOUSE_ITEMUSEAREA_Q72830A"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_HOUSE_SENSORY_AREA_A_Q62830"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_HOUSE_SENSORY_AREA_B_Q62830"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_HOUSE_SENSORY_AREA_C_Q62830"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_HOUSE_SENSORY_AREA_D_Q62830"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 806988) {
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
			if (targetId == 806989) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182216946, 1);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 838347) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182216946, 1);
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
			if (zoneName == ZoneName.get("IDF8_HOUSE_SENSORY_AREA_A_Q62830")) {
				if (var == 1) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
					return true;
				}
			} if (zoneName == ZoneName.get("IDF8_HOUSE_SENSORY_AREA_B_Q62830")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					return true;
				}
			} if (zoneName == ZoneName.get("IDF8_HOUSE_SENSORY_AREA_C_Q62830")) {
				if (var == 3) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					return true;
				}
			} if (zoneName == ZoneName.get("IDF8_HOUSE_SENSORY_AREA_D_Q62830")) {
				if (var == 5) {
					qs.setQuestVar(6);
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
        int id = item.getItemTemplate().getTemplateId();
		if (id == 182216946) {
            if (var == 4 && player.isInsideZone(ZoneName.get("IDF8_HOUSE_ITEMUSEAREA_Q72830A"))) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
				//Let's go to the cliff to wait for Lyle.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_Q72830_Gossip_01, 0);
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
			if (var == 6) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, IDF8_House_HugeRider_80, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(7);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}