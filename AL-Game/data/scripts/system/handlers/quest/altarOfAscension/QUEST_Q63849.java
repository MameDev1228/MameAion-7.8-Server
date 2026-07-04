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
package quest.altarOfAscension;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q63849 extends QuestHandler
{
	private final static int questId = 63849;
	
	private final static int[] Dragon_Altar_Mortasha_E = {858788};
	
	public QUEST_Q63849() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: Dragon_Altar_Mortasha_E) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestItem(164010117, questId);
		qe.registerOnMovieEndQuest(1056, questId);
		qe.registerQuestNpc(806978).addOnQuestStart(questId);
		qe.registerQuestNpc(806978).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("BESPERA_PORT_302820000"), questId);
		qe.registerOnEnterZone(ZoneName.get("ASHWIND_CLIFF_302820000"), questId);
		qe.registerOnEnterZone(ZoneName.get("THE_BURNING_PATH_302820000"), questId);
		qe.registerOnEnterZone(ZoneName.get("CIRCLE_OF_REBIRTH_302820000"), questId);
		qe.registerOnEnterZone(ZoneName.get("VALLEY_PRISON_CAMP_302820000"), questId);
		qe.registerOnEnterZone(ZoneName.get("VOLCANO_RUINS_EXCAVATION_SITE_302820000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 806978) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 806978) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case SET_REWARD: {
						playQuestMovie(env, 1056);
						return closeDialogWindow(env);
					}
				}
			}
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 806978) {
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
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		if (id != 164010117) {
			return HandlerResult.UNKNOWN;
		}
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, itemObjId, id, 2000, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				removeQuestItem(env, 164010117, 1);
			}
		}, 5000);
		return HandlerResult.SUCCESS;
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("BESPERA_PORT_302820000") || zoneName == ZoneName.get("ASHWIND_CLIFF_302820000") ||
			    zoneName == ZoneName.get("THE_BURNING_PATH_302820000") || zoneName == ZoneName.get("CIRCLE_OF_REBIRTH_302820000") ||
				zoneName == ZoneName.get("VALLEY_PRISON_CAMP_302820000") || zoneName == ZoneName.get("VOLCANO_RUINS_EXCAVATION_SITE_302820000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					return true;
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
			if (var == 3) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, Dragon_Altar_Mortasha_E, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
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
        if (movieId == 1056) {
			qs.setQuestVar(5);
            qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
            return true;
        }
        return false;
    }
}