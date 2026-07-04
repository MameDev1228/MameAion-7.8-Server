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

public class QUEST_Q70605 extends QuestHandler
{
	private final static int questId = 70605;
	private final static int[] npcs = {806989, 731932, 807006};
	
	public QUEST_Q70605() {
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
		qe.registerQuestItem(182216767, questId);
		qe.registerQuestItem(182216768, questId);
		qe.registerQuestItem(182216769, questId);
		qe.registerQuestItem(182216770, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("LDF8_D_ITEMUSEAREA_Q70605A"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION03_ITEMUSEAREA_Q60605B"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION03_ITEMUSEAREA_Q60605C"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION03_ITEMUSEAREA_Q60605D"), questId);
		////////////////////////////////////////////////////////////////////////////////////
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_03_Q60605_302580000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_03_Q60605_A_302580000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_03_Q60605_B_302580000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_03_Q60605_C_302580000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_03_Q60605_D_302580000"), questId);
	}
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 70604, true);
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 806989) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182216767, 1);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 731932) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						if (player.isInGroup2()) {
							//You must leave your group or alliance to enter.
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403080));
							return true;
						} else {
							changeQuestStep(env, 2, 3, false);
							WorldMapInstance secretResearchCenter = InstanceService.getNextAvailableInstance(302580000);
							InstanceService.registerPlayerWithInstance(secretResearchCenter, player);
							TeleportService2.teleportTo(player, 302580000, secretResearchCenter.getInstanceId(), 338.0000f, 151.0000f, 339.0000f, (byte) 23);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 807006) {
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
            if (targetId == 806989) {
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
        int id = item.getItemTemplate().getTemplateId();
        if (id == 182216767) {
            if (var == 1 && player.isInsideZone(ZoneName.get("LDF8_D_ITEMUSEAREA_Q70605A"))) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				removeQuestItem(env, 182216767, 1);
				//The Balaur Lord's Chest that the Stellin employees are moving has been spotted. Word is, they're using the sewage drain.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1502265, player.getObjectId(), 2));
				QuestService.addNewSpawn(800060000, 1, 703729, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
            }
        } else if (id == 182216768) {
            if (var == 4 && player.isInsideZone(ZoneName.get("IDF8_MISSION03_ITEMUSEAREA_Q60605B"))) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
				giveQuestItem(env, 182216769, 1);
				removeQuestItem(env, 182216768, 1);
				//The ST-IX Chest has been discovered at the Research Center!
				//The contents are the same as the Balaur Lord's Chest that was found at the entrance.
				//It seems that the Balaur Lord's Legacy is used for the creation of ST-IX.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1502266, player.getObjectId(), 2));
				QuestService.addNewSpawn(302580000, 1, 703729, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
			}
        } else if (id == 182216769) {
            if (var == 7 && player.isInsideZone(ZoneName.get("IDF8_MISSION03_ITEMUSEAREA_Q60605C"))) {
				qs.setQuestVar(8);
				updateQuestStatus(env);
				giveQuestItem(env, 182216770, 1);
				removeQuestItem(env, 182216769, 1);
				//The ST-IX Extraction Tube has been discovered!
				//The power extracted from the Balaur Lord is separated into pure power and then the remaining residue before being moved.
				//Where are they sending it?
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1502267, player.getObjectId(), 2));
				QuestService.addNewSpawn(302580000, 1, 703729, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
			}
        } else if (id == 182216770) {
            if (var == 10 && player.isInsideZone(ZoneName.get("IDF8_MISSION03_ITEMUSEAREA_Q60605D"))) {
				qs.setQuestVar(11);
				updateQuestStatus(env);
				giveQuestItem(env, 182216771, 1);
				removeQuestItem(env, 182216770, 1);
				//The Test Tube with the Balaur Subject has been discovered! What a strange device, full of mystery. What are they doing with it?
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1502268, player.getObjectId(), 2));
				QuestService.addNewSpawn(302580000, 1, 703729, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("IDF8_MISSION_03_Q60605_302580000")) {
				if (var == 3) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					giveQuestItem(env, 182216768, 1);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_03_Q60605_A_302580000")) {
				if (var == 5) {
					qs.setQuestVar(6);
					updateQuestStatus(env);
					playQuestMovie(env, 1045);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_03_Q60605_B_302580000")) {
				if (var == 8) {
					qs.setQuestVar(9);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_03_Q60605_C_302580000")) {
				if (var == 9) {
					qs.setQuestVar(10);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_03_Q60605_D_302580000")) {
				if (var == 11) {
					playQuestMovie(env, 1046);
					removeQuestItem(env, 182216771, 1);
					qs.setStatus(QuestStatus.REWARD);
					QuestService.finishQuest(env);
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