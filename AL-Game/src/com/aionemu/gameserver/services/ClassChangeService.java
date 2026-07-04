/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.player.MameBurningService;

import java.sql.Timestamp;
import java.util.Calendar;

public class ClassChangeService
{
	public static void showClassChangeDialog(Player player) {
		PlayerClass playerClass = player.getPlayerClass();
		Race playerRace = player.getRace();
		if (playerClass.isStartingClass()) {
			if (playerRace == Race.ELYOS) {
				switch (playerClass) {
					case WARRIOR:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2375, 1006));
					break;
					case SCOUT:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2716, 1006));
					break;
					case MAGE:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 1006));
					break;
					case PRIEST:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 1006));
					break;
					case TECHNIST:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 1006));
					break;
					case MUSE:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 1006));
					break;
				}
			} else if (playerRace == Race.ASMODIANS) {
				switch (playerClass) {
					case WARRIOR:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 2008));
					break;
					case SCOUT:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 2008));
					break;
					case MAGE:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 2008));
					break;
					case PRIEST:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 2008));
					break;
					case TECHNIST:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3569, 2008));
					break;
					case MUSE:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3910, 2008));
					break;
				}
			}
		}
	}
	
	public static void changeClassToSelection(final Player player, final int dialogId) {
		Race playerRace = player.getRace();
		if (playerRace == Race.ELYOS) {
			switch (dialogId) {
				case 2376:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
				break;
				case 2461:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
				break;
				case 2717:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
				break;
				case 2802:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
				break;
				case 3058:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
				break;
				case 3143:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
				break;
				case 3399:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
				break;
				case 3484:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
				break;
				case 3825:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("13")));
				break;
				case 3740:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("14")));
				break;
				case 4081:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("16")));
				break;
				case 4166:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("17")));
				break;
			}
		} else if (playerRace == Race.ASMODIANS) {
			switch (dialogId) {
				case 3058:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
				break;
				case 3143:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
				break;
				case 3399:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
				break;
				case 3484:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
				break;
				case 3740:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
				break;
				case 3825:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
				break;
				case 4081:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
				break;
				case 4166:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
				break;
				case 3591:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("13")));
				break;
				case 3570:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("14")));
				break;
				case 3911:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("16")));
				break;
				case 3932:
					setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("17")));
				break;
			}
		}
	}
	
	public static void completeQuest(Player player, int questId) {
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		Calendar calendar = Calendar.getInstance();
		Timestamp timeStamp = new Timestamp(calendar.getTime().getTime());
		if (qs == null) {
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 0, 1, null, 0, timeStamp));
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, QuestStatus.COMPLETE.value(), 0));
		} else {
			qs.setStatus(QuestStatus.COMPLETE);
			qs.setCompleteCount(qs.getCompleteCount() + 1);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
	
	public static void setClass(Player player, PlayerClass playerClass) {
		if (validateSwitch(player, playerClass)) {
			player.getCommonData().setPlayerClass(playerClass);
			player.getController().upgradePlayer();
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0, 0));
			MameBurningService.onClassSelected(player);
		}
	}
	
	private static boolean validateSwitch(Player player, PlayerClass playerClass) {
		int level = player.getLevel();
		PlayerClass oldClass = player.getPlayerClass();
		if (level < 9) {
			PacketSendUtility.sendMessage(player, "You can only switch class at level 9");
			return false;
		} if (!oldClass.isStartingClass()) {
			PacketSendUtility.sendMessage(player, "You already switched class");
			return false;
		} switch (oldClass) {
			case WARRIOR:
				if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR)
				break;
			case SCOUT:
				if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER)
				break;
			case MAGE:
				if (playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER)
				break;
			case PRIEST:
				if (playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER)
				break;
			case TECHNIST:
				if (playerClass == PlayerClass.GUNSLINGER || playerClass == PlayerClass.AETHERTECH)
				break;
			case MUSE:
				if (playerClass == PlayerClass.SONGWEAVER || playerClass == PlayerClass.VANDAL)
				break;
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return false;
		}
		return true;
	}
	
	//Elyos Quest/Mission/Guide/Episode.
	public static void onUpdateQuest15545(Player player) {
        if (player.getQuestStateList().hasQuest(15545)) {
			QuestState qs = player.getQuestStateList().getQuestState(15545);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(15545, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest16020(Player player) {
        if (player.getQuestStateList().hasQuest(16020)) {
			QuestState qs = player.getQuestStateList().getQuestState(16020);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(16020, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest18260(Player player) {
        if (player.getQuestStateList().hasQuest(18260)) {
			QuestState qs = player.getQuestStateList().getQuestState(18260);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(18260, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest60308(Player player) {
        if (player.getQuestStateList().hasQuest(60308)) {
			QuestState qs = player.getQuestStateList().getQuestState(60308);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60308, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission60009(Player player) {
        if (player.getQuestStateList().hasQuest(60009)) {
			QuestState qs = player.getQuestStateList().getQuestState(60009);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60009, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission60206(Player player) {
        if (player.getQuestStateList().hasQuest(60206)) {
			QuestState qs = player.getQuestStateList().getQuestState(60206);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60206, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission60305(Player player) {
        if (player.getQuestStateList().hasQuest(60305)) {
			QuestState qs = player.getQuestStateList().getQuestState(60305);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60305, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission60601(Player player) {
        if (player.getQuestStateList().hasQuest(60601)) {
			QuestState qs = player.getQuestStateList().getQuestState(60601);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				qs.setQuestVar(8);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60601, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission60606(Player player) {
        if (player.getQuestStateList().hasQuest(60606)) {
			QuestState qs = player.getQuestStateList().getQuestState(60606);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6) {
				qs.setQuestVar(7);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60606, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateEpisode60609(Player player) {
        if (player.getQuestStateList().hasQuest(60609)) {
			QuestState qs = player.getQuestStateList().getQuestState(60609);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(60609, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest61605(Player player) {
        if (player.getQuestStateList().hasQuest(61605)) {
			QuestState qs = player.getQuestStateList().getQuestState(61605);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61605, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest61902(Player player) {
        if (player.getQuestStateList().hasQuest(61902)) {
			QuestState qs = player.getQuestStateList().getQuestState(61902);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61902, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest61952(Player player) {
        if (player.getQuestStateList().hasQuest(61952)) {
			QuestState qs = player.getQuestStateList().getQuestState(61952);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61952, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest62110(Player player) {
        if (player.getQuestStateList().hasQuest(62110)) {
			QuestState qs = player.getQuestStateList().getQuestState(62110);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(62110, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest62810(Player player) {
        if (player.getQuestStateList().hasQuest(62810)) {
			QuestState qs = player.getQuestStateList().getQuestState(62810);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(62810, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest62820(Player player) {
        if (player.getQuestStateList().hasQuest(62820)) {
			QuestState qs = player.getQuestStateList().getQuestState(62820);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(62820, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest62890(Player player) {
        if (player.getQuestStateList().hasQuest(62890)) {
			QuestState qs = player.getQuestStateList().getQuestState(62890);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(62890, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest63605(Player player) {
        if (player.getQuestStateList().hasQuest(63605)) {
			QuestState qs = player.getQuestStateList().getQuestState(63605);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63605, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest63820(Player player) {
        if (player.getQuestStateList().hasQuest(63820)) {
			QuestState qs = player.getQuestStateList().getQuestState(63820);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63820, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest63840(Player player) {
        if (player.getQuestStateList().hasQuest(63840)) {
			QuestState qs = player.getQuestStateList().getQuestState(63840);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63840, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest63881(Player player) {
        if (player.getQuestStateList().hasQuest(63881)) {
			QuestState qs = player.getQuestStateList().getQuestState(63881);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63881, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest64400(Player player) {
        if (player.getQuestStateList().hasQuest(64400)) {
			QuestState qs = player.getQuestStateList().getQuestState(64400);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(64400, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateGuide63801(Player player) {
		if (player.getQuestStateList().hasQuest(63801)) {
			QuestState qs = player.getQuestStateList().getQuestState(63801);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63801, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateGuide63802(Player player) {
		if (player.getQuestStateList().hasQuest(63802)) {
			QuestState qs = player.getQuestStateList().getQuestState(63802);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6) {
				qs.setQuestVar(7);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				qs.setQuestVar(8);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateGuide63803(Player player) {
        if (player.getQuestStateList().hasQuest(63803)) {
			QuestState qs = player.getQuestStateList().getQuestState(63803);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(63803, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	
	//Asmodians Quest/Mission/Guide/Episode.
	public static void onUpdateQuest25545(Player player) {
        if (player.getQuestStateList().hasQuest(25545)) {
			QuestState qs = player.getQuestStateList().getQuestState(25545);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(25545, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest26020(Player player) {
        if (player.getQuestStateList().hasQuest(26020)) {
			QuestState qs = player.getQuestStateList().getQuestState(26020);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(26020, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest28260(Player player) {
        if (player.getQuestStateList().hasQuest(28260)) {
			QuestState qs = player.getQuestStateList().getQuestState(28260);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(28260, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest70308(Player player) {
        if (player.getQuestStateList().hasQuest(70308)) {
			QuestState qs = player.getQuestStateList().getQuestState(70308);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70308, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission70009(Player player) {
        if (player.getQuestStateList().hasQuest(70009)) {
			QuestState qs = player.getQuestStateList().getQuestState(70009);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70009, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission70206(Player player) {
        if (player.getQuestStateList().hasQuest(70206)) {
			QuestState qs = player.getQuestStateList().getQuestState(70206);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				qs.setQuestVar(8);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70206, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission70305(Player player) {
        if (player.getQuestStateList().hasQuest(70305)) {
			QuestState qs = player.getQuestStateList().getQuestState(70305);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70305, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission70601(Player player) {
        if (player.getQuestStateList().hasQuest(70601)) {
			QuestState qs = player.getQuestStateList().getQuestState(70601);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				qs.setQuestVar(8);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70601, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission70606(Player player) {
        if (player.getQuestStateList().hasQuest(70606)) {
			QuestState qs = player.getQuestStateList().getQuestState(70606);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6) {
				qs.setQuestVar(7);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70606, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateEpisode70609(Player player) {
        if (player.getQuestStateList().hasQuest(70609)) {
			QuestState qs = player.getQuestStateList().getQuestState(70609);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(70609, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest71605(Player player) {
        if (player.getQuestStateList().hasQuest(71605)) {
			QuestState qs = player.getQuestStateList().getQuestState(71605);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(71605, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest71902(Player player) {
        if (player.getQuestStateList().hasQuest(71902)) {
			QuestState qs = player.getQuestStateList().getQuestState(71902);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(71902, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest71952(Player player) {
        if (player.getQuestStateList().hasQuest(71952)) {
			QuestState qs = player.getQuestStateList().getQuestState(71952);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(71952, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest72110(Player player) {
        if (player.getQuestStateList().hasQuest(72110)) {
			QuestState qs = player.getQuestStateList().getQuestState(72110);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(72110, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest72810(Player player) {
        if (player.getQuestStateList().hasQuest(72810)) {
			QuestState qs = player.getQuestStateList().getQuestState(72810);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(72810, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest72820(Player player) {
        if (player.getQuestStateList().hasQuest(72820)) {
			QuestState qs = player.getQuestStateList().getQuestState(72820);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(72820, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest72890(Player player) {
        if (player.getQuestStateList().hasQuest(72890)) {
			QuestState qs = player.getQuestStateList().getQuestState(72890);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(72890, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest73605(Player player) {
        if (player.getQuestStateList().hasQuest(73605)) {
			QuestState qs = player.getQuestStateList().getQuestState(73605);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73605, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest73820(Player player) {
        if (player.getQuestStateList().hasQuest(73820)) {
			QuestState qs = player.getQuestStateList().getQuestState(73820);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73820, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest73840(Player player) {
        if (player.getQuestStateList().hasQuest(73840)) {
			QuestState qs = player.getQuestStateList().getQuestState(73840);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73840, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest73881(Player player) {
        if (player.getQuestStateList().hasQuest(73881)) {
			QuestState qs = player.getQuestStateList().getQuestState(73881);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73881, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest74400(Player player) {
        if (player.getQuestStateList().hasQuest(74400)) {
			QuestState qs = player.getQuestStateList().getQuestState(74400);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(74400, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateGuide73801(Player player) {
		if (player.getQuestStateList().hasQuest(73801)) {
			QuestState qs = player.getQuestStateList().getQuestState(73801);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73801, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateGuide73802(Player player) {
		if (player.getQuestStateList().hasQuest(73802)) {
			QuestState qs = player.getQuestStateList().getQuestState(73802);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6) {
				qs.setQuestVar(7);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			} if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				qs.setQuestVar(8);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73802, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateGuide73803(Player player) {
        if (player.getQuestStateList().hasQuest(73803)) {
			QuestState qs = player.getQuestStateList().getQuestState(73803);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(73803, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
}