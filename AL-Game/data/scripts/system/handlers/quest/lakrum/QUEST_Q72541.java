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

public class QUEST_Q72541 extends QuestHandler
{
	private final static int questId = 72541;
	
	public QUEST_Q72541() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(836565).addOnQuestStart(questId);
		qe.registerQuestNpc(836565).addOnTalkEvent(questId);
		qe.registerQuestNpc(836650).addOnTalkEvent(questId);
		qe.registerQuestNpc(836651).addOnTalkEvent(questId); 
		qe.registerQuestNpc(836652).addOnTalkEvent(questId);
		qe.registerQuestNpc(836653).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 836565) {
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
				case 836650: {
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						} case STEP_TO_1: {
							changeQuestStep(env, 0, 1, false);
							giveQuestItem(env, 182216461, 1); //보르간드의 보고서.
							return closeDialogWindow(env);
						}
					}
				} case 836651: {
				    switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						} case STEP_TO_2: {
							changeQuestStep(env, 1, 2, false);
							giveQuestItem(env, 182216462, 1); //바우두린의 보고서.
							return closeDialogWindow(env);
						}
					}
				} case 836652: {
				    switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						} case STEP_TO_3: {
							changeQuestStep(env, 2, 3, false);
							giveQuestItem(env, 182216463, 1); //힐드미르의 보고서.
							return closeDialogWindow(env);
						}
					}
				} case 836653: {
				    switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						} case SET_REWARD: {
							qs.setStatus(QuestStatus.REWARD);
							QuestService.finishQuest(env);
							removeQuestItem(env, 182216461, 1); //보르간드의 보고서.
							removeQuestItem(env, 182216462, 1); //바우두린의 보고서.
							removeQuestItem(env, 182216463, 1); //힐드미르의 보고서.
							removeQuestItem(env, 182216464, 1); //마그렐의 보고서.
							return closeDialogWindow(env);
						}
					}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 836565) {
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