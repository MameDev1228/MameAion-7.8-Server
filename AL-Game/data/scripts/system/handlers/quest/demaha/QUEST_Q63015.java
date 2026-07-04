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
package quest.demaha;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q63015 extends QuestHandler
{
	private final static int questId = 63015;
	
	public QUEST_Q63015() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(820482).addOnQuestStart(questId);
		qe.registerQuestNpc(820482).addOnTalkEvent(questId);
		qe.registerQuestNpc(820483).addOnTalkEvent(questId);
		qe.registerQuestNpc(820484).addOnTalkEvent(questId);
		qe.registerQuestNpc(820392).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LDF8_SENSORY_AREA_A_Q63015_800060000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LDF8_SENSORY_AREA_B_Q63015_800060000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LDF8_SENSORY_AREA_C_Q63015_800060000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 820482) {
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
			if (targetId == 820483) {
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
            } if (targetId == 820484) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_4: {
                        changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820392) {
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
			if (zoneName == ZoneName.get("LDF8_SENSORY_AREA_A_Q63015_800060000")) {
				if (var == 0) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
					//That was fast. At this pace, you'll arrive at the 2nd Altar in no time.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_QUEST_SAY_Q63015_01, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("LDF8_SENSORY_AREA_B_Q63015_800060000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					//You're here already? It won't be long before you reach the 1st Altar.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_QUEST_SAY_Q63015_02, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("LDF8_SENSORY_AREA_C_Q63015_800060000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					//You passed through the Circle of Rebirth!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_QUEST_SAY_Q63015_03, 0);
					return true;
				}
			}
		}
		return false;
	}
}