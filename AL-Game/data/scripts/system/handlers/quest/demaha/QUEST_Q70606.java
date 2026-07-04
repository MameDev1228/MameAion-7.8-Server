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

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q70606 extends QuestHandler
{
	private final static int questId = 70606;
	private final static int[] npcs = {806997, 820520, 820522, 731947};
	private final static int[] BIDF8_Mission04_4BReward_Named_002 = {858471};
	
	public QUEST_Q70606() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: BIDF8_Mission04_4BReward_Named_002) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnDie(questId);
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182216934, questId);
		qe.registerQuestItem(164010100, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION04_ITEMUSEAREA_Q60606B"), questId);
		////////////////////////////////////////////////////////////////////////////////////
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_04_Q60606_A_302670000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_04_Q60606_B_302670000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_04_Q60606_C_302670000"), questId);
	}
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ELYOS && player.getLevel() == 80) {
            QuestService.startQuest(env);
            return true;
        }
        return false;
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 806997) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182216934, 1);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820520) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						if (player.isInGroup2()) {
							//You must leave your group or alliance to enter.
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403080));
							return true;
						} else {
							changeQuestStep(env, 3, 4, false);
							WorldMapInstance Scaleshadow = InstanceService.getNextAvailableInstance(302670000);
							InstanceService.registerPlayerWithInstance(Scaleshadow, player);
							TeleportService2.teleportTo(player, 302670000, Scaleshadow.getInstanceId(), 587.0000f, 664.0000f, 354.0000f, (byte) 68);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 820522) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 8) {
                            return sendQuestDialog(env, 3739);
                        }
					} case STEP_TO_9: {
                        changeQuestStep(env, 8, 9, false);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						QuestService.addNewSpawn(302670000, player.getInstanceId(), 820637, (float) 584.0000, (float) 597.0000, (float) 355.0000, (byte) 61);
						QuestService.addNewSpawn(302670000, player.getInstanceId(), 282786, (float) 584.0000, (float) 597.0000, (float) 355.0000, (byte) 61);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 731947) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 11) {
                            return sendQuestDialog(env, 6841);
                        }
					} case SET_REWARD: {
                        qs.setStatus(QuestStatus.REWARD);
					    QuestService.finishQuest(env);
						removeQuestItem(env, 164010100, 1);
						removeQuestItem(env, 182216934, 1);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820520) {
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
			if (zoneName == ZoneName.get("IDF8_MISSION_04_Q60606_A_302670000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_04_Q60606_B_302670000")) {
				if (var == 5) {
					qs.setQuestVar(6);
					updateQuestStatus(env);
					//Someone called for me....
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1502581, player.getObjectId(), 2));
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_04_Q60606_C_302670000")) {
				if (var == 9) {
					qs.setQuestVar(10);
					updateQuestStatus(env);
					//They're everywhere!
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1502582, player.getObjectId(), 2));
					return true;
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
        int id = item.getItemTemplate().getTemplateId();
        if (id == 182216934) {
            if (var == 1) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				return HandlerResult.SUCCESS;
            }
        } else if (id == 164010100) {
            if (var == 7 && player.isInsideZone(ZoneName.get("IDF8_MISSION04_ITEMUSEAREA_Q60606B"))) {
				qs.setQuestVar(8);
				updateQuestStatus(env);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 10) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, BIDF8_Mission04_4BReward_Named_002, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setQuestVar(11);
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
            if (var >= 3 && var < 11) {
                qs.setQuestVar(2);
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
            if (var >= 3 && var < 11) {
                qs.setQuestVar(2);
                updateQuestStatus(env);
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
                return true;
            }
        }
        return false;
    }
}