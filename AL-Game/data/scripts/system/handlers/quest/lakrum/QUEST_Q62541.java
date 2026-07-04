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

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q62541 extends QuestHandler
{
	private final static int questId = 62541;
	
	public QUEST_Q62541() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(836551).addOnQuestStart(questId);
		qe.registerQuestNpc(836551).addOnTalkEvent(questId);
		qe.registerQuestNpc(836630).addOnTalkEvent(questId);
		qe.registerQuestNpc(836631).addOnTalkEvent(questId); 
		qe.registerQuestNpc(836632).addOnTalkEvent(questId);
		qe.registerQuestNpc(836633).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 836551) {
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
			switch (targetId) {
				case 836630: {
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						} case STEP_TO_1: {
							changeQuestStep(env, 0, 1, false);
							giveQuestItem(env, 182216433, 1); //보레노스의 보고서.
							return closeDialogWindow(env);
						}
					}
				} case 836631: {
				    switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						} case STEP_TO_2: {
							changeQuestStep(env, 1, 2, false);
							giveQuestItem(env, 182216434, 1); //노베의 보고서.
							return closeDialogWindow(env);
						}
					}
				} case 836632: {
				    switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						} case STEP_TO_3: {
							changeQuestStep(env, 2, 3, false);
							giveQuestItem(env, 182216435, 1); //레니에스의 보고서.
							return closeDialogWindow(env);
						}
					}
				} case 836633: {
				    switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						} case SET_REWARD: {
							qs.setStatus(QuestStatus.REWARD);
							QuestService.finishQuest(env);
							removeQuestItem(env, 182216433, 1); //보레노스의 보고서.
							removeQuestItem(env, 182216434, 1); //노베의 보고서.
							removeQuestItem(env, 182216435, 1); //레니에스의 보고서.
							removeQuestItem(env, 182216436, 1); //데마오페의 보고서.
							return closeDialogWindow(env);
						}
					}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 836551) {
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
}