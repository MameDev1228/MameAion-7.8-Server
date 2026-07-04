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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;


/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q61916 extends QuestHandler
{
	private final static int questId = 61916;
	
	public QUEST_Q61916() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182216318, questId);
		qe.registerQuestItem(182216319, questId);
		qe.registerQuestNpc(798997).addOnQuestStart(questId);
		qe.registerQuestNpc(798997).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q61916_210050000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798997) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ACCEPT_QUEST_SIMPLE: {
						giveQuestItem(env, 182216319, 1);
						return sendQuestStartDialog(env, 182216318, 1);
					} case REFUSE_QUEST_SIMPLE: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798997) {
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
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        final int id = item.getItemTemplate().getTemplateId();
        if (id == 182216318) {
            if (var == 0 && player.isInsideZone(ZoneName.get("LF4_Q61916_210050000"))) {
				//… Failed to hatch a Klaw. Let's try again with the Red Klaw Egg.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_LF4_Quest_Gossip_07, 0);
				QuestService.addNewSpawn(210050000, 1, 282786, player.getX(), player.getY(), player.getZ(), (byte) 0);
                return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false));
            }
        } else if (id == 182216319) {
            if (var == 1 && player.isInsideZone(ZoneName.get("LF4_Q61916_210050000"))) {
				//A Klaw was born!
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_CHAT_LF4_Quest_Gossip_08, 0);
				QuestService.addNewSpawn(210050000, 1, 282786, player.getX(), player.getY(), player.getZ(), (byte) 0);
				QuestService.addNewSpawn(210050000, 1, 820072, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, true));
			}
        }
        return HandlerResult.FAILED;
    }
}