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

public class QUEST_Q61602 extends QuestHandler
{
	private final static int questId = 61602;
	private final static int[] Centipede_37_An = {652574, 652591};
	private final static int[] FellialRE_36_An = {652657, 652658};
	
	public QUEST_Q61602() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: Centipede_37_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: FellialRE_36_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(204549).addOnQuestStart(questId);
		qe.registerQuestNpc(204549).addOnTalkEvent(questId);
		qe.registerQuestNpc(820018).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204549) {
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
            if (targetId == 820018) {
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
				switch (targetId) {
					case 652574:
					case 652591:
						if (var1 < 4) {
							return defaultOnKillEvent(env, Centipede_37_An, 0, 4, 1);
						} else if (var1 == 4) {
							if (var2 == 5) {
								qs.setStatus(QuestStatus.REWARD);
						        QuestService.bountyReward(env, 0);
								return true;
							} else {
								return defaultOnKillEvent(env, Centipede_37_An, 4, 5, 1);
							}
						}
					break;
					case 652657:
					case 652658:
						if (var2 < 4) {
							return defaultOnKillEvent(env, FellialRE_36_An, 0, 4, 2);
						} else if (var2 == 4) {
							if (var1 == 5) {
								qs.setStatus(QuestStatus.REWARD);
						        QuestService.bountyReward(env, 0);
								return true;
							} else {
								return defaultOnKillEvent(env, FellialRE_36_An, 4, 5, 2);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}