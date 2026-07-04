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
package quest.chaoticVale;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q73835 extends QuestHandler
{
	private final static int questId = 73835;
	
	public QUEST_Q73835() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182217009, questId);
		qe.registerQuestItem(182217010, questId);
		qe.registerQuestItem(182217011, questId);
		qe.registerQuestNpc(806989).addOnQuestStart(questId);
		qe.registerQuestNpc(806989).addOnTalkEvent(questId);
		//qe.registerOnEnterZone(ZoneName.get("IDTAGWAR_ITEMUSEAREA_Q63835A"), questId);
		//qe.registerOnEnterZone(ZoneName.get("IDTAGWAR_ITEMUSEAREA_Q63835B"), questId);
		//qe.registerOnEnterZone(ZoneName.get("IDTAGWAR_ITEMUSEAREA_Q63835C"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 806989) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						giveQuestItem(env, 182217009, 1);
						giveQuestItem(env, 182217010, 1);
						return sendQuestStartDialog(env, 182217011, 1);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 806989) {
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
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        int id = item.getItemTemplate().getTemplateId();
        if (id == 182217009) {
			if (var == 0 && player.isInsideZone(ZoneName.get("IDTAGWAR_ITEMUSEAREA_Q63835A"))) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
				giveQuestItem(env, 182217010, 1);
				removeQuestItem(env, 182217009, 1);
				return HandlerResult.SUCCESS;
			}
		} else if (id == 182217010) {
			if (var == 1 && player.isInsideZone(ZoneName.get("IDTAGWAR_ITEMUSEAREA_Q63835B"))) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				giveQuestItem(env, 182217011, 1);
				removeQuestItem(env, 182217010, 1);
				return HandlerResult.SUCCESS;
			}
		} else if (id == 182217011) {
			if (var == 2 && player.isInsideZone(ZoneName.get("IDTAGWAR_ITEMUSEAREA_Q63835C"))) {
				qs.setQuestVar(3);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				removeQuestItem(env, 182217011, 1);
				return HandlerResult.SUCCESS;
			}
		}
        return HandlerResult.FAILED;
    }
}