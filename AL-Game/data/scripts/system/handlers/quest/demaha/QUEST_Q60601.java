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

import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q60601 extends QuestHandler
{
	private final static int questId = 60601;
	private final static int[] npcs = {806975, 806976, 806977, 806979, 703703, 731923, 807000, 836005, 836006};
	private final static int[] LDF8_MB_D3_Lega_Fi_80_Ae = {658425, 658429, 658433, 658437};
	private final static int[] LDF8_MB_D3_Lega_Wi_80_Ae = {658427, 658431, 658435, 658438};
	private final static int[] LDF8_B3_M_Lega_L_Fi_80_Ae = {657414};
	////////////////////////////////////////////////////////////////
	private final static int[] IDF8_Mission01_D3_Lega_80_Ae = {657434, 657435, 657436, 657437};
	private final static int[] IDF8_Mission01_D3_Lega_Named_80_Ae = {657438};
	
	public QUEST_Q60601() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnDie(questId);
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LDF8_MB_D3_Lega_Fi_80_Ae) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF8_MB_D3_Lega_Wi_80_Ae) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF8_B3_M_Lega_L_Fi_80_Ae) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDF8_Mission01_D3_Lega_80_Ae) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDF8_Mission01_D3_Lega_Named_80_Ae) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 60600, true);
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		int var1 = qs.getQuestVarById(1);
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			if (targetId == 806975) {
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
            } if (targetId == 806976) {
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
            } if (targetId == 806979) {
				switch (env.getDialog()) {
					case START_DIALOG: {
                        if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						giveQuestItem(env, 182216751, 1);
						changeQuestStep(env, 3, 4, false);
						PacketSendUtility.sendSys3Message(player, "\uE005", "Save the wounded Elyos!!!");
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
			} if (targetId == 703703) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        return closeDialogWindow(env);
					}
                }
            } if (targetId == 836005 || targetId == 836006) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 4) {
							if (var1 >= 0 && var1 < 4) {
							    Npc npc = (Npc) env.getVisibleObject();
								npc.getController().scheduleRespawn();
								npc.getController().onDelete();
								changeQuestStep(env, var1, var1 + 1, false, 1);
								QuestService.addNewSpawn(800060000, 1, 836004, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
							} else if (var1 == 4) {
								qs.setQuestVar(5);
								updateQuestStatus(env);
								removeQuestItem(env, 182216751, 1);
								return closeDialogWindow(env);
							}
						}
					}
                }
            } if (targetId == 731923) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						if (player.isInGroup2()) {
							//You must leave your group or alliance to enter.
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403080));
							return true;
						} else {
							changeQuestStep(env, 6, 7, false);
							WorldMapInstance regatusHeadquarters = InstanceService.getNextAvailableInstance(302560000);
							InstanceService.registerPlayerWithInstance(regatusHeadquarters, player);
							TeleportService2.teleportTo(player, 302560000, regatusHeadquarters.getInstanceId(), 233.0000f, 182.0000f, 229.0000f, (byte) 70);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 807000) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 9) {
                            return sendQuestDialog(env, 4080);
                        }
					} case SET_REWARD: {
                        qs.setStatus(QuestStatus.REWARD);
					    QuestService.finishQuest(env);
						//The Regatus Headquarters was wiped out, but Vishaka got away.
						//I also encountered a strange girl. This needs to be reported to the Leader.
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1502293, player.getObjectId(), 2));
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 806977) {
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
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				switch (targetId) {
					case 658425:
					case 658429:
					case 658433:
					case 658437:
						if (var1 < 4) {
							return defaultOnKillEvent(env, LDF8_MB_D3_Lega_Fi_80_Ae, 0, 4, 1);
						} else if (var1 == 4) {
							if (var2 == 5 && var3 == 1) {
								qs.setQuestVar(6);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF8_MB_D3_Lega_Fi_80_Ae, 4, 5, 1);
							}
						}
					break;
					case 658427:
					case 658431:
					case 658435:
					case 658438:
						if (var2 < 4) {
							return defaultOnKillEvent(env, LDF8_MB_D3_Lega_Wi_80_Ae, 0, 4, 2);
						} else if (var2 == 4) {
							if (var1 == 5 && var3 == 1) {
								qs.setQuestVar(6);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF8_MB_D3_Lega_Wi_80_Ae, 4, 5, 2);
							}
						}
					break;
					case 657414:
						if (var3 < 0) {
							return defaultOnKillEvent(env, LDF8_B3_M_Lega_L_Fi_80_Ae, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 5 && var2 == 5) {
								qs.setQuestVar(6);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF8_B3_M_Lega_L_Fi_80_Ae, 0, 1, 3);
							}
						}
					break;
				}
			} else if (var == 8) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 657434:
					case 657435:
					case 657436:
					case 657437:
						if (var1 < 4) {
							return defaultOnKillEvent(env, IDF8_Mission01_D3_Lega_80_Ae, 0, 4, 1);
						} else if (var1 == 4) {
							if (var2 == 1) {
								qs.setQuestVar(9);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDF8_Mission01_D3_Lega_80_Ae, 4, 5, 1);
							}
						}
					break;
					case 657438:
						if (var2 < 0) {
							return defaultOnKillEvent(env, IDF8_Mission01_D3_Lega_Named_80_Ae, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 5) {
								qs.setQuestVar(9);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDF8_Mission01_D3_Lega_Named_80_Ae, 0, 1, 2);
							}
						}
					break;
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