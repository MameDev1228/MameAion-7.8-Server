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

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q60306 extends QuestHandler
{
	private final static int questId = 60306;
	private final static int[] npcs = {730256, 798958, 798996, 820081, 820169};
	private final static int[] LF4_B_DrakanFi_55_An_1_Q60306 = {654831, 654832, 654833};
	private final static int[] IDUnderpass_Guard_Da_Fighter_Ae_lv60_M = {836850, 836851, 836852};
	private final static int[] IDUnderPass_BossGuard_Da_Fighter_Ah_lv60_M = {836864};
	
	public QUEST_Q60306() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LF4_B_DrakanFi_55_An_1_Q60306) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: IDUnderpass_Guard_Da_Fighter_Ae_lv60_M) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: IDUnderPass_BossGuard_Da_Fighter_Ah_lv60_M) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerOnDie(questId);
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(977, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerQuestNpc(836864).addOnKillEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("IDUNDERPASS_Q60306_A_302440000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDUNDERPASS_Q60306_B_302440000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDUNDERPASS_Q60306_C_302440000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 60309, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 798958) {
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
            } if (targetId == 798996) {
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
            } if (targetId == 730256) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						if (player.isInGroup2()) {
							//You must leave your group or alliance to enter.
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403080));
							return true;
						} else {
							changeQuestStep(env, 4, 5, false);
							WorldMapInstance silenteraCorridor = InstanceService.getNextAvailableInstance(302440000);
							InstanceService.registerPlayerWithInstance(silenteraCorridor, player);
							TeleportService2.teleportTo(player, 302440000, silenteraCorridor.getInstanceId(), 506.0000f, 411.0000f, 327.0000f, (byte) 101);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 820081) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case STEP_TO_6: {
                        changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 820169) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 11) {
                            return sendQuestDialog(env, 6841);
                        }
					} case SET_REWARD: {
						qs.setQuestVar(12);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						TeleportService2.teleportTo(env.getPlayer(), 210050000, 1079.0000f, 1599.0000f, 408.0000f, (byte) 30);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798958) {
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
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 3) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 4) {
                    return defaultOnKillEvent(env, LF4_B_DrakanFi_55_An_1_Q60306, var1, var1 + 1, 1);
                } else if (var1 == 4) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 8) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 8) {
                    return defaultOnKillEvent(env, IDUnderpass_Guard_Da_Fighter_Ae_lv60_M, var1, var1 + 1, 1);
                } else if (var1 == 8) {
					qs.setQuestVar(9);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 10) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, IDUnderPass_BossGuard_Da_Fighter_Ah_lv60_M, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					playQuestMovie(env, 977);
                    return true;
                }
			}
        }
        return false;
    }
	
	@Override
    public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 977) {
			qs.setQuestVar(11);
			updateQuestStatus(env);
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
			if (zoneName == ZoneName.get("IDUNDERPASS_Q60306_A_302440000")) {
				if (var == 6) {
					qs.setQuestVar(7);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDUNDERPASS_Q60306_B_302440000")) {
				if (var == 7) {
					qs.setQuestVar(8);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDUNDERPASS_Q60306_C_302440000")) {
				if (var == 9) {
					qs.setQuestVar(10);
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
            if (var >= 5 && var < 11) {
                qs.setQuestVar(4);
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
            if (var >= 5 && var < 11) {
                qs.setQuestVar(4);
                updateQuestStatus(env);
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
                return true;
            }
        }
        return false;
    }
}