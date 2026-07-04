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
package quest.crimson_katalam;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q73299 extends QuestHandler
{
	private final static int questId = 73299;
	
	public QUEST_Q73299() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnKillInWorld(800030000, questId);
		qe.registerQuestNpc(820606).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("HADRADIMS_WATCH_800030000"), questId);
		qe.registerOnEnterZone(ZoneName.get("STELLMANS_VALLEY_800030000"), questId);
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("HADRADIMS_WATCH_800030000") || zoneName == ZoneName.get("STELLMANS_VALLEY_800030000")) {
			if (qs == null || qs.canRepeat() || qs.getStatus() == QuestStatus.NONE) {
				env.setQuestId(questId);
				if (QuestService.startQuest(env)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 820606) {
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
	public boolean onKillInWorldEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		if (env.getVisibleObject() instanceof Player && player != null &&
		    !player.getEquipment().getEquippedItemsByItemId(100050200).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(100150196).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(100250198).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(100550194).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(100650195).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(100950196).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(101350196).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(101550198).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(101750195).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(101850196).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(101950190).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(102050195).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(102150200).isEmpty() &&
			!player.getEquipment().getEquippedItemsByItemId(102220124).isEmpty()) {
			if ((env.getPlayer().getLevel() >= (((Player) env.getVisibleObject()).getLevel() - 5)) &&
			    (env.getPlayer().getLevel() <= (((Player) env.getVisibleObject()).getLevel() + 9))) {
				return defaultOnKillRankedEvent(env, 0, 9, true);
			} else {
				PacketSendUtility.sendSys3Message(player, "\uE005", "You are not equipped with the correct item, please search the <Katalam Protector's Box> they are spawn on the map!!!");
			}
		}
		return false;
	}
}