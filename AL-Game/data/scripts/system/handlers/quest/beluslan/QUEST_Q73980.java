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
package quest.beluslan;

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

public class QUEST_Q73980 extends QuestHandler
{
	private final static int questId = 73980;
	private final static int[] MahishaDrakyD_33_An = {651960, 651961};
	private final static int[] MimicAmethystD_32_An = {651954, 651955};
	private final static int[] KuillusVioletD_32_An = {651957, 651958};
	
	public QUEST_Q73980() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: MahishaDrakyD_33_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: MimicAmethystD_32_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: KuillusVioletD_32_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(798118).addOnQuestStart(questId);
		qe.registerQuestNpc(798118).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.canRepeat() || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798118) {
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
            if (targetId == 798118) {
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
				int var3 = qs.getQuestVarById(3);
				switch (targetId) {
					case 651960:
					case 651961:
						if (var1 < 2) {
							return defaultOnKillEvent(env, MahishaDrakyD_33_An, 0, 2, 1);
						} else if (var1 == 2) {
							if (var2 == 3 && var3 == 4) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, MahishaDrakyD_33_An, 2, 3, 1);
							}
						}
					break;
					case 651954:
					case 651955:
						if (var2 < 2) {
							return defaultOnKillEvent(env, MimicAmethystD_32_An, 0, 2, 2);
						} else if (var2 == 2) {
							if (var1 == 3 && var3 == 4) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, MimicAmethystD_32_An, 2, 3, 2);
							}
						}
					break;
					case 651957:
					case 651958:
						if (var3 < 3) {
							return defaultOnKillEvent(env, KuillusVioletD_32_An, 0, 3, 3);
						} else if (var3 == 3) {
							if (var1 == 3 && var2 == 3) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, KuillusVioletD_32_An, 3, 4, 3);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}