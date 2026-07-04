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
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q63010 extends QuestHandler
{
	private final static int questId = 63010;
	
	public QUEST_Q63010() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182216812, questId);
		qe.registerQuestItem(182216813, questId);
		qe.registerQuestItem(182216847, questId);
		qe.registerQuestNpc(820402).addOnQuestStart(questId);
		qe.registerQuestNpc(820402).addOnTalkEvent(questId);
		qe.registerQuestNpc(820423).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ITEMUSEAREA_Q63010A"), questId);
		qe.registerOnEnterZone(ZoneName.get("ITEMUSEAREA_Q63010B"), questId);
		qe.registerOnEnterZone(ZoneName.get("ITEMUSEAREA_Q63010C"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 820402) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						return sendQuestStartDialog(env, 182216812, 1);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 820423) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
					} case STEP_TO_3: {
                        changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820402) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_4: {
						giveQuestItem(env, 182216847, 1);
                        changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820402) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182216812, 1);
					removeQuestItem(env, 182216813, 1);
					removeQuestItem(env, 182216847, 1);
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
		if (id == 182216812) {
            if (var == 0 && player.isInsideZone(ZoneName.get("ITEMUSEAREA_Q63010A"))) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
				giveQuestItem(env, 182216813, 1);
				//The scent has been picked up by strange creatures.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_QUEST_SAY_Q63010_01, 0);
				QuestService.addNewSpawn(800060000, 1, 658049, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
			}
        } else if (id == 182216813) {
            if (var == 1 && player.isInsideZone(ZoneName.get("ITEMUSEAREA_Q63010B"))) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				//Elroco has appeared!
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_QUEST_SAY_Q63010_02, 0);
				return HandlerResult.SUCCESS;
			}
        } else if (id == 182216847) {
            if (var == 4 && player.isInsideZone(ZoneName.get("ITEMUSEAREA_Q63010C"))) {
				qs.setQuestVar(5);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				//Tracking Ink successfully applied to Unusko!
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_QUEST_SAY_Q63010_03, 0);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
}