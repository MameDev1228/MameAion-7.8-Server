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
package quest.gelkmaros;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q71915 extends QuestHandler
{
	public static final int questId = 71915;
	private final static int[] DF4_AncientClan_Ghost_Fi = {650154, 650155};
	private final static int[] DF4_AncientClan_Ghost_Wi = {650156, 650157};
	
	public QUEST_Q71915() {
		super(questId);
	}
	
    @Override
    public void register() {
        for (int mob: DF4_AncientClan_Ghost_Fi) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: DF4_AncientClan_Ghost_Wi) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(799337).addOnQuestStart(questId);
		qe.registerQuestNpc(799337).addOnTalkEvent(questId);
		qe.registerQuestNpc(799333).addOnTalkEvent(questId);
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799337) {
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
			if (targetId == 799333) {
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
					case 650154:
					case 650155:
						if (var1 < 4) {
							return defaultOnKillEvent(env, DF4_AncientClan_Ghost_Fi, 0, 4, 1);
						} else if (var1 == 4) {
							if (var2 == 5) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_AncientClan_Ghost_Fi, 4, 5, 1);
							}
						}
					break;
					case 650156:
					case 650157:
						if (var2 < 4) {
							return defaultOnKillEvent(env, DF4_AncientClan_Ghost_Wi, 0, 4, 2);
						} else if (var2 == 4) {
							if (var1 == 5) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_AncientClan_Ghost_Wi, 4, 5, 2);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}