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
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;


/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q61619 extends QuestHandler
{
	private final static int questId = 61619;
	private final static int[] npcs = {820005};
	private final static int[] DrakeRE_44_An = {652873};
	private final static int[] Draky_44_An = {652874};
	private final static int[] LizardmanFiNMQ_43_Ae = {652880};
	
	public QUEST_Q61619() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DrakeRE_44_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Draky_44_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LizardmanFiNMQ_43_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnEnterZone(ZoneName.get("LF3_Q61619_A_210040000"), questId);
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
		if (zoneName == ZoneName.get("LF3_Q61619_A_210040000")) {
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
					case 652873:
						if (var1 < 2) {
							return defaultOnKillEvent(env, DrakeRE_44_An, 0, 2, 1);
						} else if (var1 == 2) {
							if (var2 == 3 && var3 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DrakeRE_44_An, 2, 3, 1);
							}
						}
					break;
					case 652874:
						if (var2 < 2) {
							return defaultOnKillEvent(env, Draky_44_An, 0, 2, 2);
						} else if (var2 == 2) {
							if (var1 == 3 && var3 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, Draky_44_An, 2, 3, 2);
							}
						}
					break;
					case 652880:
						if (var3 < 0) {
							return defaultOnKillEvent(env, LizardmanFiNMQ_43_Ae, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 3 && var2 == 3) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LizardmanFiNMQ_43_Ae, 0, 1, 3);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}