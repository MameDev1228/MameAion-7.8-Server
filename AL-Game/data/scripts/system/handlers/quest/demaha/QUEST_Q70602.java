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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
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

public class QUEST_Q70602 extends QuestHandler
{
	private final static int questId = 70602;
	private final static int[] npcs = {806988, 806992, 806997, 807001, 807002, 807005, 731931};
	private final static int[] LDF8_Mission_Drakan_Fi01_80_An = {657401, 657402, 657403};
	private final static int[] LDF8_Mission_Drakan_Boss_03_AL = {657409};
	
	public QUEST_Q70602() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnDie(questId);
        qe.registerOnLogOut(questId);
		//qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LDF8_Mission_Drakan_Fi01_80_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF8_Mission_Drakan_Boss_03_AL) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		//qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_02_Q60602_A_302570000"), questId);
		//qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_02_Q60602_B_302570000"), questId);
		//qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_02_Q60602_C_302570000"), questId);
		//qe.registerOnEnterZone(ZoneName.get("IDF8_MISSION_02_Q60602_D_302570000"), questId);
	}
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 70601, true);
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 806988) {
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
            } if (targetId == 806997) {
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
            } if (targetId == 806992) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
					} case STEP_TO_3: {
                        changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 731931) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						if (player.isInGroup2()) {
							//You must leave your group or alliance to enter.
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403080));
							return true;
						} else {
							changeQuestStep(env, 3, 4, false);
							WorldMapInstance vishakaHideout = InstanceService.getNextAvailableInstance(302570000);
							InstanceService.registerPlayerWithInstance(vishakaHideout, player);
							TeleportService2.teleportTo(player, 302570000, vishakaHideout.getInstanceId(), 164.0000f, 144.0000f, 230.0000f, (byte) 10);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 807001) {
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
            } if (targetId == 807002) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 8) {
                            return sendQuestDialog(env, 3739);
                        }
					} case STEP_TO_9: {
                        changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 807005) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 12) {
                            return sendQuestDialog(env, 7182);
                        }
					} case SET_REWARD: {
                        qs.setStatus(QuestStatus.REWARD);
					    QuestService.finishQuest(env);
						TeleportService2.teleportTo(env.getPlayer(), 800060000, 1042.0000f, 1604.0000f, 728.0000f, (byte) 0);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 806988) {
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
			if (var == 5) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 4) {
					return defaultOnKillEvent(env, LDF8_Mission_Drakan_Fi01_80_An, var1, var1 + 1, 1);
				} else if (var1 == 4) {
					qs.setQuestVar(6);
					updateQuestStatus(env);
					return true;
				}
			} else if (var == 7) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, LDF8_Mission_Drakan_Boss_03_AL, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setQuestVar(8);
					updateQuestStatus(env);
					playQuestMovie(env, 1043);
					return true;
				}
			}
		}
		return false;
	}
	
	/*@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("IDF8_MISSION_02_Q60602_A_302570000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_02_Q60602_B_302570000")) {
				if (var == 9) {
					qs.setQuestVar(10);
					updateQuestStatus(env);
					playQuestMovie(env, 1044);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_02_Q60602_C_302570000")) {
				if (var == 10) {
					qs.setQuestVar(11);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDF8_MISSION_02_Q60602_D_302570000")) {
				if (var == 11) {
					qs.setQuestVar(12);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}*/
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 4 && var < 13) {
                qs.setQuestVar(3);
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
            if (var >= 4 && var < 13) {
                qs.setQuestVar(3);
                updateQuestStatus(env);
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
                return true;
            }
        }
        return false;
    }
}