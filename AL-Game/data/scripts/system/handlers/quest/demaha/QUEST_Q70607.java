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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q70607 extends QuestHandler
{
	private final static int questId = 70607;
	private final static int[] npcs = {820520, 820524, 731949, 731952};
	
	public QUEST_Q70607() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnDie(questId);
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(164010102, questId);
		qe.registerQuestItem(182216972, questId);
		qe.registerQuestItem(182216973, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("LDF8_ITEMUSEAREA_Q70607A"), questId);
		qe.registerOnEnterZone(ZoneName.get("LDF8_ITEMUSEAREA_Q70607B"), questId);
		qe.registerOnEnterZone(ZoneName.get("LDF8_ITEMUSEAREA_Q70607C"), questId);
	}
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 70606, true);
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 820520) {
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
            } if (targetId == 820524) {
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
            } if (targetId == 731949) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case SET_REWARD: {
                        qs.setStatus(QuestStatus.REWARD);
					    QuestService.finishQuest(env);
						removeQuestItem(env, 164010102, 1);
						removeQuestItem(env, 182216972, 1);
						removeQuestItem(env, 182216973, 1);
						//(Inanna...)
						PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_GOSSHIP_LDF8_Mission_40, 0);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 731952) {
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
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
		int var1 = qs.getQuestVarById(1);
        int id = item.getItemTemplate().getTemplateId();
        if (id == 164010102 && player.isInsideZone(ZoneName.get("LDF8_ITEMUSEAREA_Q70607A"))) {
            if (var == 2) {
				if (var1 >= 0 && var1 < 1) {
					changeQuestStep(env, var1, var1 + 1, false, 1);
					giveQuestItem(env, 164010102, 1);
					giveQuestItem(env, 182216972, 1);
					return HandlerResult.SUCCESS;
				} else if (var1 == 1) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					//(This doesn't seem to be the right key. Time to try with another one.)
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_GOSSHIP_LDF8_Mission_37, 0);
					return HandlerResult.SUCCESS;
				}
            }
        } else if (id == 182216972) {
            if (var == 3 && player.isInsideZone(ZoneName.get("LDF8_ITEMUSEAREA_Q70607B"))) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				giveQuestItem(env, 182216973, 1);
				//(This doesn't seem to be the right key. It needs to be opened before the Regatus notices.)
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_GOSSHIP_LDF8_Mission_38, 0);
				return HandlerResult.SUCCESS;
			}
        } else if (id == 182216973) {
            if (var == 4 && player.isInsideZone(ZoneName.get("LDF8_ITEMUSEAREA_Q70607C"))) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
				//(It's open!)
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_GOSSHIP_LDF8_Mission_39, 0);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
}