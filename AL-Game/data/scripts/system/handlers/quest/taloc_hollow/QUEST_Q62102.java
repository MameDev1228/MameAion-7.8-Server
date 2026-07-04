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
package quest.taloc_hollow;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q62102 extends QuestHandler
{
	private final static int questId = 62102;
	private final static int[] IDElim_1F_Sheluk_KeyNM_52_Ae = {653390};
	private final static int[] IDElim_1F_Clodworm_ItemNM_52_Ae = {653415};
	private final static int[] IDElim_2F_Octaside_KeyNM_52_Ae = {653412};
	
	public QUEST_Q62102() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: IDElim_1F_Sheluk_KeyNM_52_Ae) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: IDElim_1F_Clodworm_ItemNM_52_Ae) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: IDElim_2F_Octaside_KeyNM_52_Ae) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(820005).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("IDELIM_300190000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs.getStatus() == QuestStatus.REWARD) {
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
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("IDELIM_300190000")) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				env.setQuestId(questId);
				if (QuestService.startQuest(env)) {
					return true;
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
					case 653390:
						if (var1 < 0) {
							return defaultOnKillEvent(env, IDElim_1F_Sheluk_KeyNM_52_Ae, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDElim_1F_Sheluk_KeyNM_52_Ae, 0, 1, 1);
							}
						}
					break;
					case 653415:
						if (var2 < 0) {
							return defaultOnKillEvent(env, IDElim_1F_Clodworm_ItemNM_52_Ae, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDElim_1F_Clodworm_ItemNM_52_Ae, 0, 1, 2);
							}
						}
					break;
					case 653412:
						if (var3 < 0) {
							return defaultOnKillEvent(env, IDElim_2F_Octaside_KeyNM_52_Ae, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDElim_2F_Octaside_KeyNM_52_Ae, 0, 1, 3);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}