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
package com.aionemu.gameserver.services;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.model.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import java.util.Iterator;

import java.util.concurrent.Future;

public class QuestUpdateService
{
	private Future<?> checkQuestUpdateTask;
	
    public void onStart() {
		checkQuestUpdate();
    }
	
	private void checkQuestUpdate() {
		checkQuestUpdateTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Iterator<Player> iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext()) {
					Player player = iter.next();
					if (player.getRace() == Race.ELYOS) {
						if (player.getQuestStateList().hasQuest(60201)) {
							QuestState qs = player.getQuestStateList().getQuestState(60201);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 36 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60201, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(60202)) {
							QuestState qs = player.getQuestStateList().getQuestState(60202);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 42 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60202, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(60203)) {
							QuestState qs = player.getQuestStateList().getQuestState(60203);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 54 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60203, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(60205)) {
							QuestState qs = player.getQuestStateList().getQuestState(60205);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 57 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60205, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(60207)) {
							QuestState qs = player.getQuestStateList().getQuestState(60207);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 75 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60207, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(60208)) {
							QuestState qs = player.getQuestStateList().getQuestState(60208);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 28 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60208, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(60303)) {
							QuestState qs = player.getQuestStateList().getQuestState(60303);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 79 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60303, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(60306)) {
							QuestState qs = player.getQuestStateList().getQuestState(60306);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60306, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(61601)) {
							QuestState qs = player.getQuestStateList().getQuestState(61601);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 14 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61601, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(61603)) {
							QuestState qs = player.getQuestStateList().getQuestState(61603);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 26 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61603, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						}
					} else if (player.getRace() == Race.ASMODIANS) {
						if (player.getQuestStateList().hasQuest(70201)) {
							QuestState qs = player.getQuestStateList().getQuestState(70201);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 31 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70201, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(70203)) {
							QuestState qs = player.getQuestStateList().getQuestState(70203);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 49 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70203, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(70204)) {
							QuestState qs = player.getQuestStateList().getQuestState(70204);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 51 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70204, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(70205)) {
							QuestState qs = player.getQuestStateList().getQuestState(70205);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 61 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70205, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(70207)) {
							QuestState qs = player.getQuestStateList().getQuestState(70207);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 75 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70207, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(70303)) {
							QuestState qs = player.getQuestStateList().getQuestState(70303);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 79 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70303, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(70306)) {
							QuestState qs = player.getQuestStateList().getQuestState(70306);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70306, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(71601)) {
							QuestState qs = player.getQuestStateList().getQuestState(71601);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 19 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(71601, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						} if (player.getQuestStateList().hasQuest(71603)) {
							QuestState qs = player.getQuestStateList().getQuestState(71603);
							if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 27 && player.getLevel() <= 80) {
								qs.setQuestVar(1);
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(71603, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							}
						}
					}
				}
			}
		}, 5 * 1000, 5 * 1000);
	}
	
	public void onQuestUpdateLogin(Player player) {
        if (player.getRace() == Race.ELYOS) {
			if (player.getQuestStateList().hasQuest(60201)) {
				QuestState qs = player.getQuestStateList().getQuestState(60201);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 36 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60201, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(60202)) {
				QuestState qs = player.getQuestStateList().getQuestState(60202);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 42 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60202, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(60203)) {
				QuestState qs = player.getQuestStateList().getQuestState(60203);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 54 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60203, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(60205)) {
				QuestState qs = player.getQuestStateList().getQuestState(60205);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 57 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60205, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(60207)) {
				QuestState qs = player.getQuestStateList().getQuestState(60207);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 75 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60207, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(60208)) {
				QuestState qs = player.getQuestStateList().getQuestState(60208);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 28 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60208, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(60303)) {
				QuestState qs = player.getQuestStateList().getQuestState(60303);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 79 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60303, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(60306)) {
				QuestState qs = player.getQuestStateList().getQuestState(60306);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60306, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(61601)) {
				QuestState qs = player.getQuestStateList().getQuestState(61601);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 14 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61601, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(61603)) {
				QuestState qs = player.getQuestStateList().getQuestState(61603);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 26 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61603, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			}
		} else if (player.getRace() == Race.ASMODIANS) {
			if (player.getQuestStateList().hasQuest(70201)) {
				QuestState qs = player.getQuestStateList().getQuestState(70201);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 31 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70201, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(70203)) {
				QuestState qs = player.getQuestStateList().getQuestState(70203);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 49 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70203, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(70204)) {
				QuestState qs = player.getQuestStateList().getQuestState(70204);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 51 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70204, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(70205)) {
				QuestState qs = player.getQuestStateList().getQuestState(70205);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 61 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70205, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(70207)) {
				QuestState qs = player.getQuestStateList().getQuestState(70207);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 75 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70207, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(70303)) {
				QuestState qs = player.getQuestStateList().getQuestState(70303);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 79 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70303, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(70306)) {
				QuestState qs = player.getQuestStateList().getQuestState(70306);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70306, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(71601)) {
				QuestState qs = player.getQuestStateList().getQuestState(71601);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 19 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(71601, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			} if (player.getQuestStateList().hasQuest(71603)) {
				QuestState qs = player.getQuestStateList().getQuestState(71603);
				if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0 && player.getLevel() >= 27 && player.getLevel() <= 80) {
					qs.setQuestVar(1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(71603, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			}
		}
    }
	
    public static QuestUpdateService getInstance() {
        return SingletonHolder.instance;
    }
	
	@SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final QuestUpdateService instance = new QuestUpdateService();
    }
}