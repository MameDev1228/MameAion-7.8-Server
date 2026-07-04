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
package quest.inggison;

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

public class QUEST_Q60302 extends QuestHandler
{
	private final static int questId = 60302;
	private final static int[] npcs = {798939, 798954, 799025};
	
	public QUEST_Q60302() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q60302_A_1_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q60302_A_2_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q60302_B_1_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q60302_B_2_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q60302_C_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q60302_D_210050000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 60301, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.START) {
            if (targetId == 798939) {
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
            } if (targetId == 799025) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798954) {
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
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("LF4_Q60302_A_1_210050000") || zoneName == ZoneName.get("LF4_Q60302_A_2_210050000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					//You're on your way to the Illusion Fortress!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_LF4_Quest_Gossip_03, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("LF4_Q60302_B_1_210050000") || zoneName == ZoneName.get("LF4_Q60302_B_2_210050000")) {
				if (var == 3) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					//You'd better find a way to fly out of here quick.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_LF4_Quest_Gossip_02, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("LF4_Q60302_C_210050000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					//You took the Phnoe windstream!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_LF4_Quest_Gossip_05, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("LF4_Q60302_D_210050000")) {
				if (var == 5) {
					qs.setStatus(QuestStatus.REWARD);
					QuestService.bountyReward(env, 0);
					//You are on the windstream to the Southern Distorted Forest!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_LF4_Quest_Gossip_04, 0);
					return true;
				}
			}
		}
		return false;
	}
}