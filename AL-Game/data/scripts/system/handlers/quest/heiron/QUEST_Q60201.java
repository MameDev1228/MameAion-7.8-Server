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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q60201 extends QuestHandler
{
	private final static int questId = 60201;
	private final static int[] npcs = {204622, 204626, 204627, 204628, 820026, 204625, 700270, 820167};
	
	public QUEST_Q60201() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(204, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 60208, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		boolean spinaBones = player.getInventory().getItemCountByItemId(182216254) > 0;
		boolean mempionBones = player.getInventory().getItemCountByItemId(182216255) > 0;
		boolean ladonsBones = player.getInventory().getItemCountByItemId(182216256) > 0;
		if (qs == null || qs.getStatus() == QuestStatus.START) {
            if (targetId == 820026) {
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
            } if (targetId == 204625) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
					} case STEP_TO_3: {
						playQuestMovie(env, 204);
						PacketSendUtility.sendSys3Message(player, "\uE005", "Gathered Bones from Ladon, Spina, Mempion and give them to Kalkas");
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204622) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 3) {
							//Ladon's Bones.
							giveQuestItem(env, 182216256, 1);
							return closeDialogWindow(env);
						}
					}
				}
            } if (targetId == 204626) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 3) {
							//Spina's Bones.
							giveQuestItem(env, 182216254, 1);
							return closeDialogWindow(env);
						}
					}
				}
            } if (targetId == 204627) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 3) {
						    //Mempion's Bones.
							giveQuestItem(env, 182216255, 1);
							return closeDialogWindow(env);
						}
					}
				}
            } if (targetId == 204628) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (spinaBones || mempionBones || ladonsBones) {
							//Manor Worker's Remains.
							giveQuestItem(env, 182216253, 1);
							return closeDialogWindow(env);
						} else {
							PacketSendUtility.sendSys3Message(player, "\uE005", "Gathered Bones from Ladon, Spina, Mempion");
							return true;
						}
					}
				}
            } if (targetId == 700270) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        } else if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
					} case STEP_TO_5: {
                        changeQuestStep(env, 4, 5, false);
						QuestService.addNewSpawn(210040000, 1, 820167, 190.0000f, 1924.0000f, 120.0000f, (byte) 31);
						QuestService.addNewSpawn(210040000, 1, 282786, 190.0000f, 1924.0000f, 120.0000f, (byte) 31);
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
            } if (targetId == 820167) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case SET_REWARD: {
						qs.setQuestVar(6);
                        qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						TeleportService2.teleportTo(env.getPlayer(), 210040000, 150.0000f, 1562.0000f, 124.0000f, (byte) 0);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820026) {
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
        if (movieId == 204) {
			qs.setQuestVar(3);
			updateQuestStatus(env);
            return true;
        }
        return false;
    }
}