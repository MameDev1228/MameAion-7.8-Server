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
package ai.worlds.lakrum;

import com.aionemu.commons.utils.Rnd;

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

@AIName("Base_Buff_Statue")
public class Base_Buff_StatueAI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		PlayerEffectController effectController = player.getEffectController();
		if (dialogId == 10000) {
		    switch (Rnd.get(1, 6)) {
				case 1:
					effectController.removeEffect(12215);
					effectController.removeEffect(12216);
					effectController.removeEffect(12217);
					effectController.removeEffect(12218);
					effectController.removeEffect(12219);
					SkillEngine.getInstance().applyEffectDirectly(12214, player, player, 1800000 * 1);
				break;
				case 2:
					effectController.removeEffect(12214);
					effectController.removeEffect(12216);
					effectController.removeEffect(12217);
					effectController.removeEffect(12218);
					effectController.removeEffect(12219);
					SkillEngine.getInstance().applyEffectDirectly(12215, player, player, 1800000 * 1);
				break;
				case 3:
					effectController.removeEffect(12214);
					effectController.removeEffect(12215);
					effectController.removeEffect(12217);
					effectController.removeEffect(12218);
					effectController.removeEffect(12219);
					SkillEngine.getInstance().applyEffectDirectly(12216, player, player, 1800000 * 1);
				break;
				case 4:
					effectController.removeEffect(12214);
					effectController.removeEffect(12215);
					effectController.removeEffect(12216);
					effectController.removeEffect(12218);
					effectController.removeEffect(12219);
					SkillEngine.getInstance().applyEffectDirectly(12217, player, player, 1800000 * 1);
				break;
				case 5:
					effectController.removeEffect(12214);
					effectController.removeEffect(12215);
					effectController.removeEffect(12216);
					effectController.removeEffect(12217);
					effectController.removeEffect(12219);
					SkillEngine.getInstance().applyEffectDirectly(12218, player, player, 1800000 * 1);
				break;
				case 6:
					effectController.removeEffect(12214);
					effectController.removeEffect(12215);
					effectController.removeEffect(12216);
					effectController.removeEffect(12217);
					effectController.removeEffect(12218);
					SkillEngine.getInstance().applyEffectDirectly(12219, player, player, 1800000 * 1);
				break;
			}
		}
		//The Buff Support Statue grants you great power.
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1406023));
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        return true;
    }
}