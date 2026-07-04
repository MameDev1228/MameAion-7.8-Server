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
package quest.ishalgen;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ASCENSION_MORPH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.ArrayList;
import java.util.List;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q2008 extends QuestHandler
{
	private final static int questId = 2008;
	private final static int[] npcs = {203546, 203550, 205020, 806810, 806814, 806848};
	private final static int[] assassin = {205040};
	
	public QUEST_Q2008() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: assassin) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			return;
		}
		qe.registerOnDie(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(152, questId);
		qe.registerQuestNpc(205041).addOnAttackEvent(questId);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 806814:
				    switch (env.getDialog()) {
					    case START_DIALOG: {
						    if (var == 0) {
							    return sendQuestDialog(env, 1011);
						    } else if (var == 4) {
							    return sendQuestDialog(env, 2375);
						    }
					    } case STEP_TO_1: {
						    changeQuestStep(env, 0, 1, false);
						    return closeDialogWindow(env);
					    } case STEP_TO_5: {
						    qs.setQuestVar(99);
						    updateQuestStatus(env);
						    WorldMapInstance KaramatisC = InstanceService.getNextAvailableInstance(320020000);
						    InstanceService.registerPlayerWithInstance(KaramatisC, player);
						    TeleportService2.teleportTo(player, 320020000, KaramatisC.getInstanceId(), 457.0000f, 426.0000f, 230.0000f);
						    return closeDialogWindow(env);
					    }
				    }
			    break;
				case 806848:
				    switch (env.getDialog()) {
					    case START_DIALOG: {
						    if (var == 6) {
							    return sendQuestDialog(env, 2716);
						    }
					    } case STEP_TO_6: {
						    if (var == 6) {
								PlayerClass playerClass = player.getCommonData().getPlayerClass();
								if (playerClass == PlayerClass.WARRIOR) {
									return sendQuestDialog(env, 3057);
								} else if (playerClass == PlayerClass.SCOUT) {
									return sendQuestDialog(env, 3398);
								} else if (playerClass == PlayerClass.MAGE) {
									return sendQuestDialog(env, 3739);
								} else if (playerClass == PlayerClass.PRIEST) {
									return sendQuestDialog(env, 4080);
								} else if (playerClass == PlayerClass.TECHNIST) {
									return sendQuestDialog(env, 3612);
								} else if (playerClass == PlayerClass.MUSE) {
									return sendQuestDialog(env, 3910);
								}
							}
						} case STEP_TO_7: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.GLADIATOR);
						    }
						} case STEP_TO_8: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.TEMPLAR);
							}
						} case STEP_TO_9: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.ASSASSIN);
							}
						} case STEP_TO_10: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.RANGER);
							}
						} case STEP_TO_11: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.SORCERER);
							}
						} case STEP_TO_12: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
							}
						} case STEP_TO_13: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.CHANTER);
							}
						} case STEP_TO_14: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.CLERIC);
							}
						} case STEP_TO_15: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.GUNSLINGER);
							}
						} case STEP_TO_16: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.SONGWEAVER);
							}
						} case STEP_TO_17: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.AETHERTECH);
							}
						} case STEP_TO_18: {
							if (var == 6) {
								return setPlayerClass(env, qs, PlayerClass.VANDAL);
							}
						}
					}
				break;
				case 203516:
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
			    break;
			    case 203550:
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
				break;
				case 806810:
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
			    break;
				case 205020:
				    switch (env.getDialog()) {
						case START_DIALOG: {
							if (qs.getQuestVars().getQuestVars() == 99) {
								flyTeleport(player, 3001);
								qs.setQuestVar(50);
							    updateQuestStatus(env);
								final QuestEnv qe = env;
							    final int instanceId = player.getInstanceId();
							    ThreadPoolManager.getInstance().schedule(new Runnable() {
								    @Override
								    public void run() {
									    changeQuestStep(qe, 50, 51, false);
									    List<Npc> guardianAssassin = new ArrayList<Npc>();
									    guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 307.0000f, 280.0000f, 206.0000f, (byte) 112));
									    for (Npc mob: guardianAssassin) {
										    mob.getAggroList().addDamage(player, 1000);
									    }
								    }
							    }, 43000);
							    return true;
						    }
					    }
				    }
			    break;
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 806848) {
				switch (env.getDialog()) {
					case SELECT_NO_REWARD: {
						if (player.getWorldId() == 320020000) {
							TeleportService2.teleportTo(player, 220010000, 571.3323f, 2539.2310f, 271.99982f, (byte) 105);
						}
					}
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	private void flyTeleport(Player player, int id) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(id);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, id, 0));
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 152) {
			return false;
		}
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5) {
			return false;
		}
		int instanceId = player.getInstanceId();
		QuestService.addNewSpawn(320020000, instanceId, 806848, 313.0000f, 274.0000f, 206.0000f, (byte) 61);
		qs.setQuestVar(6);
		updateQuestStatus(env);
		return true;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int instanceId = player.getInstanceId();
		List<Npc> guardianAssassin = new ArrayList<Npc>();
		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (targetId == 205040) {
			if (var >= 51 && var <= 53) {
				switch (var) {
					case 51:
						guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 307.0000f, 280.0000f, 206.0000f, (byte) 112));
					break;
					case 52:
						guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 307.0000f, 266.0000f, 205.0000f, (byte) 14));
					break;
					case 53:
						guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 304.0000f, 272.0000f, 205.0000f, (byte) 2));
					break;
				}
				qs.setQuestVar(qs.getQuestVars().getQuestVars() + 1);
				updateQuestStatus(env);
				return true;
			} else if (var == 54) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
				Npc mob = (Npc) QuestService.addNewSpawn(320020000, instanceId, 205041, 313.0000f, 274.0000f, 206.0000f, (byte) 0);
				mob.getAggroList().addDamage(player, 1000);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onAttackEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5) {
			return false;
		}
		int targetId = env.getTargetId();
		if (targetId != 205041) {
			return false;
		}
		Npc npc = (Npc) env.getVisibleObject();
		if (npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 2) {
			playQuestMovie(env, 152);
			npc.getController().onDelete();
		}
		return false;
	}
	
	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass) {
		Player player = env.getPlayer();
		ClassChangeService.setClass(player, playerClass);
		player.getController().upgradePlayer();
		qs.setStatus(QuestStatus.REWARD);
		QuestService.finishQuest(env);
		giveQuestItem(env, 188070387, 1);
		player.getCommonData().setExp(126070);
		TeleportService2.teleportTo(player, 220010000, 571.3323f, 2539.2310f, 271.99982f, (byte) 105);
		return closeDialogWindow(env);
	}
	
	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() != QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 5 || (var == 6 && player.getPlayerClass().isStartingClass()) || (var >= 51 && var <= 53)) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 5 || (var == 6 && player.getPlayerClass().isStartingClass()) || (var >= 50 && var <= 55) || var == 99) {
				if (player.getWorldId() != 320020000) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
				} else {
					PacketSendUtility.sendPacket(player, new SM_ASCENSION_MORPH(1));
					return true;
				}
			}
		}
		return false;
	}
}