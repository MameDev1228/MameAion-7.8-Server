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
package quest.primeth_forge;

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

public class QUEST_Q23983 extends QuestHandler
{
    private final static int questId = 23983;
	private final static int[] IDF7_Weapon_Lizard_Ra_80_Ae = {650002};
	private final static int[] IDF7_Weapon_Devotee_As_80_Ae = {650005};
	private final static int[] IDF7_Weapon_Drakan_Fi_Ah = {650008};
	private final static int[] IDF7_Weapon_Boss_Final_Dragon_80_Ah = {650026};
	
    public QUEST_Q23983() {
        super(questId);
    }
	
    public void register() {
        qe.registerQuestNpc(806845).addOnTalkEvent(questId);
		for (int mob: IDF7_Weapon_Lizard_Ra_80_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDF7_Weapon_Devotee_As_80_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDF7_Weapon_Drakan_Fi_Ah) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDF7_Weapon_Boss_Final_Dragon_80_Ah) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnEnterZone(ZoneName.get("WORKSHOP_ENTRANCE_302430000"), questId);
		qe.registerOnEnterZone(ZoneName.get("WORKSHOP_ENTRANCE_302630000"), questId);
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("WORKSHOP_ENTRANCE_302430000") || zoneName == ZoneName.get("WORKSHOP_ENTRANCE_302630000")) {
			if (qs == null || qs.canRepeat() || qs.getStatus() == QuestStatus.NONE) {
				env.setQuestId(questId);
				if (QuestService.startQuest(env)) {
					return true;
				}
			}
		}
		return false;
	}
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 806845) {
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
				int var4 = qs.getQuestVarById(4);
                switch (targetId) {
                    case 650002:
                        if (var1 < 2) {
                            return defaultOnKillEvent(env, IDF7_Weapon_Lizard_Ra_80_Ae, 0, 2, 1);
                        } else if (var1 == 2) {
                            if (var2 == 3 && var3 == 3 && var4 == 1) {
                                qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
                                return true;
                            } else {
                                return defaultOnKillEvent(env, IDF7_Weapon_Lizard_Ra_80_Ae, 2, 3, 1);
                            }
                        }
                    break;
                    case 650005:
                        if (var2 < 2) {
                            return defaultOnKillEvent(env, IDF7_Weapon_Devotee_As_80_Ae, 0, 2, 2);
                        } else if (var2 == 2) {
                            if (var1 == 3 && var3 == 3 && var4 == 1) {
                                qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
                                return true;
                            } else {
                                return defaultOnKillEvent(env, IDF7_Weapon_Devotee_As_80_Ae, 2, 3, 2);
                            }
                        }
                    break;
					case 650008:
                        if (var3 < 2) {
                            return defaultOnKillEvent(env, IDF7_Weapon_Drakan_Fi_Ah, 0, 2, 3);
                        } else if (var3 == 2) {
                            if (var1 == 3 && var2 == 3 && var4 == 1) {
                                qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
                                return true;
                            } else {
                                return defaultOnKillEvent(env, IDF7_Weapon_Drakan_Fi_Ah, 2, 3, 3);
                            }
                        }
                    break;
					case 650026:
                        if (var4 < 0) {
                            return defaultOnKillEvent(env, IDF7_Weapon_Boss_Final_Dragon_80_Ah, 0, 0, 4);
                        } else if (var4 == 0) {
                            if (var1 == 3 && var2 == 3 && var3 == 3) {
                                qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
                                return true;
                            } else {
                                return defaultOnKillEvent(env, IDF7_Weapon_Boss_Final_Dragon_80_Ah, 0, 1, 4);
                            }
                        }
                    break;
                }
            }
        }
        return false;
    }
}