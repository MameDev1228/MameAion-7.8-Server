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

public class QUEST_Q63008 extends QuestHandler
{
	private final static int questId = 63008;
	
	public QUEST_Q63008() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182216808, questId);
		qe.registerQuestItem(182216809, questId);
		qe.registerQuestItem(182216810, questId);
		qe.registerQuestItem(182216845, questId);
		qe.registerQuestNpc(820393).addOnQuestStart(questId);
		qe.registerQuestNpc(820393).addOnTalkEvent(questId);
		qe.registerQuestNpc(820400).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ITEMUSEAREA_Q63008A"), questId);
		qe.registerOnEnterZone(ZoneName.get("ITEMUSEAREA_Q63008B"), questId);
		qe.registerOnEnterZone(ZoneName.get("ITEMUSEAREA_Q63008C"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 820393) {
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
			if (targetId == 820400) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182216808, 1);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820400) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182216808, 1);
					removeQuestItem(env, 182216809, 1);
					removeQuestItem(env, 182216810, 1);
					removeQuestItem(env, 182216845, 1);
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
		if (id == 182216808) {
            if (var == 1 && player.isInsideZone(ZoneName.get("ITEMUSEAREA_Q63008A"))) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				giveQuestItem(env, 182216809, 1);
				//That's not a medal....
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806803, player.getObjectId(), 2));
				return HandlerResult.SUCCESS;
			}
        } else if (id == 182216809) {
            if (var == 2 && player.isInsideZone(ZoneName.get("ITEMUSEAREA_Q63008B"))) {
				qs.setQuestVar(3);
				updateQuestStatus(env);
				giveQuestItem(env, 182216810, 1);
				//That's not a medal....
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806803, player.getObjectId(), 2));
				QuestService.addNewSpawn(800060000, 1, 838036, player.getX(), player.getY(), player.getZ(), (byte) 0);
				QuestService.addNewSpawn(800060000, 1, 282786, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
			}
        } else if (id == 182216810) {
            if (var == 3 && player.isInsideZone(ZoneName.get("ITEMUSEAREA_Q63008C"))) {
				qs.setQuestVar(4);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				giveQuestItem(env, 182216845, 1);
				//Found it!
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1806804, player.getObjectId(), 2));
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
}