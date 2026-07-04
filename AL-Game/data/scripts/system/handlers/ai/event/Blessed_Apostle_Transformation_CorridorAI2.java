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
package ai.event;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Blessed_Apostle_Transformation_Corridor")
public class Blessed_Apostle_Transformation_CorridorAI2 extends NpcAI2
{
  	@Override
	protected void handleDialogStart(Player player) {
		//주신의 사도들의 기억이 강하게 느껴지고 있다.
		//기억의 회랑을 이용하여 축복을 받으면 주신의 사도로 변신이 가능하다.
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		PlayerEffectController effectController = player.getEffectController();
		if (player.isTransformed()) {
			//You cannot use a Transformation Contract in this state.
            PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_TRANSFORMATION_CANT_CURRENT_STATE, 0);
			//Transformation Mode.
            PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_ACT_STATE_POLYMORPH, 3000);
		} if (dialogId == 10000) {
			effectController.removeEffect(5857);
			effectController.removeEffect(5858);
			effectController.removeEffect(5859);
			effectController.removeEffect(5860);
			effectController.removeEffect(5861);
            SkillEngine.getInstance().applyEffectDirectly(5856, player, player, 1800000 * 1); //이스라펠의 사도.
		} else if (dialogId == 10001) {
			effectController.removeEffect(5856);
			effectController.removeEffect(5858);
			effectController.removeEffect(5859);
			effectController.removeEffect(5860);
			effectController.removeEffect(5861);
            SkillEngine.getInstance().applyEffectDirectly(5857, player, player, 1800000 * 1); //마르쿠탄의 사도.
		} else if (dialogId == 10002) {
			effectController.removeEffect(5856);
			effectController.removeEffect(5857);
			effectController.removeEffect(5859);
			effectController.removeEffect(5860);
			effectController.removeEffect(5861);
            SkillEngine.getInstance().applyEffectDirectly(5858, player, player, 1800000 * 1); //지켈의 사도.
		} else if (dialogId == 10003) {
			effectController.removeEffect(5856);
			effectController.removeEffect(5857);
			effectController.removeEffect(5858);
			effectController.removeEffect(5860);
			effectController.removeEffect(5861);
            SkillEngine.getInstance().applyEffectDirectly(5859, player, player, 1800000 * 1); //카이시넬의 사도.
		} else if (dialogId == 10004) {
			effectController.removeEffect(5856);
			effectController.removeEffect(5857);
			effectController.removeEffect(5858);
			effectController.removeEffect(5859);
			effectController.removeEffect(5861);
            SkillEngine.getInstance().applyEffectDirectly(5860, player, player, 1800000 * 1); //시엘의 사도.
		} else if (dialogId == 10005) {
			effectController.removeEffect(5856);
			effectController.removeEffect(5857);
			effectController.removeEffect(5858);
			effectController.removeEffect(5859);
			effectController.removeEffect(5860);
            SkillEngine.getInstance().applyEffectDirectly(5861, player, player, 1800000 * 1); //네자칸의 사도.
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}