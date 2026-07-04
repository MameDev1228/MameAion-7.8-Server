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

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;


/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q70206 extends QuestHandler
{
	private final static int questId = 70206;
	private final static int[] npcs = {703492, 703493, 806827, 832820};
	private final static int[] DF3_DragonRobot_Q70206 = {702041};
	private final static int[] DF3_DragonObelisk_Q70206 = {702424};
	
	public QUEST_Q70206() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DF3_DragonObelisk_Q70206) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: DF3_DragonRobot_Q70206) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerOnDie(questId);
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182216408, questId);
		qe.registerQuestItem(182216409, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("BAKARMA_Q70206_320170000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 70205, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 703492) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						giveQuestItem(env, 182216408, 1);
						changeQuestStep(env, 0, 1, false);
						//You obtained the Bakarma Legion's Orders!
					    PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF3_Quest_Goossip_03, 0);
                        return closeDialogWindow(env);
					}
                }
            } if (targetId == 703493) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						giveQuestItem(env, 182216409, 1);
						changeQuestStep(env, 3, 4, false);
						//You obtained the Bakarma Legion's Orders!
					    PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF3_Quest_Goossip_03, 0);
                        return closeDialogWindow(env);
					}
                }
            } if (targetId == 806827) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_7: {
                        changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 832820) {
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
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        final int id = item.getItemTemplate().getTemplateId();
        if (id == 182216408) {
            if (var == 1) {
				//Let's find and destroy the Aetheric Field Dampener as was instructed in the letter!
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF3_Quest_Goossip_04, 0);
				return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false));
            }
        } else if (id == 182216409) {
            if (var == 4) {
				//Let's find and destroy the 45th Legion Mechanical Weapon for Infiltrations as Cheska instructed in the letter!
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_DF3_Quest_Goossip_05, 0);
				return HandlerResult.fromBoolean(useQuestItem(env, item, 4, 5, false));
            }
        }
        return HandlerResult.FAILED;
    }
	
	@Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 2) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 2) {
                    return defaultOnKillEvent(env, DF3_DragonObelisk_Q70206, var1, var1 + 1, 1);
                } else if (var1 == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 5) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 2) {
                    return defaultOnKillEvent(env, DF3_DragonRobot_Q70206, var1, var1 + 1, 1);
                } else if (var1 == 2) {
					qs.setQuestVar(6);
					updateQuestStatus(env);
                    return true;
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
			if (zoneName == ZoneName.get("BAKARMA_Q70206_320170000")) {
				if (var == 8) {
					qs.setQuestVar(9);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 7 && var < 9) {
                qs.setQuestVar(6);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
	
	@Override
    public boolean onDieEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 7 && var < 9) {
                qs.setQuestVar(6);
                updateQuestStatus(env);
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
                return true;
            }
        }
        return false;
    }
}