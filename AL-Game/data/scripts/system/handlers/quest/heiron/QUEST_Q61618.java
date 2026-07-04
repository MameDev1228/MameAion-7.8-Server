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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;


/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q61618 extends QuestHandler
{
	private final static int questId = 61618;
	
	public QUEST_Q61618() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182216277, questId);
		qe.registerQuestItem(182216278, questId);
		qe.registerQuestNpc(800413).addOnQuestStart(questId);
		qe.registerQuestNpc(800413).addOnTalkEvent(questId);
		qe.registerQuestNpc(820005).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_ITEM_USE_AREA_Q61618_A_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_ITEM_USE_AREA_Q61618_B_210040000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 800413) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						giveQuestItem(env, 182216278, 1);
						return sendQuestStartDialog(env, 182216277, 1);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820005) {
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
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        int id = item.getItemTemplate().getTemplateId();
        if (id == 182216277) {
            if (var == 0 && player.isInsideZone(ZoneName.get("LF3_ITEM_USE_AREA_Q61618_A_210040000"))) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
				removeQuestItem(env, 182216277, 1);
				QuestService.addNewSpawn(210040000, 1, 703521, player.getX(), player.getY(), player.getZ(), (byte) 0);
				QuestService.addNewSpawn(210040000, 1, 282786, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
            }
        } else if (id == 182216278) {
            if (var == 1 && player.isInsideZone(ZoneName.get("LF3_ITEM_USE_AREA_Q61618_B_210040000"))) {
				qs.setQuestVar(2);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				removeQuestItem(env, 182216278, 1);
				QuestService.addNewSpawn(210040000, 1, 703522, player.getX(), player.getY(), player.getZ(), (byte) 0);
				QuestService.addNewSpawn(210040000, 1, 282786, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
}