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
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q70302 extends QuestHandler
{
	private final static int questId = 70302;
	private final static int[] npcs = {799237, 799249, 806836};
	
	public QUEST_Q70302() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(562, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("DF4_Q70302_B_220070000"), questId);
		qe.registerOnEnterZone(ZoneName.get("DF4_Q70302_C_220070000"), questId);
		qe.registerOnEnterZone(ZoneName.get("DF4_Q70302_D_220070000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 70301, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.START) {
            if (targetId == 799237) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 806836) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_2: {
						playQuestMovie(env, 562);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799249) {
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
    public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (movieId == 562) {
			qs.setQuestVar(2);
			updateQuestStatus(env);
			//You'd better spread your wings and take to the windstreams.
			PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF4_Quest_Goossip_05, 0);
            return true;
        }
        return false;
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("DF4_Q70302_B_220070000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					//You're on the windstream to the Gelkmaros Fortress!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF4_Quest_Goossip_06, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("DF4_Q70302_C_220070000")) {
				if (var == 3) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					//You're on the windstream to the Netherworld of Earth!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF4_Quest_Goossip_07, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("DF4_Q70302_D_220070000")) {
				if (var == 4) {
					qs.setStatus(QuestStatus.REWARD);
					QuestService.bountyReward(env, 0);
					//You're on the windstream to the Antagor Canyon!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF4_Quest_Goossip_08, 0);
					return true;
				}
			}
		}
		return false;
	}
}