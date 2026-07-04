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
package ai.worlds.demaha;

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

@AIName("Elyos_Buff_Statue")
public class Elyos_Buff_StatueAI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		PlayerEffectController effectController = player.getEffectController();
		if (dialogId == 10000 && player.getInventory().decreaseByItemId(164010028, 1)) { //Stellium.
		    effectController.removeEffect(5857);
			effectController.removeEffect(5860);
			SkillEngine.getInstance().applyEffectDirectly(5858, player, player, 300000 * 1); //Zikel's Apostle.
		} else if (dialogId == 10001 && player.getInventory().decreaseByItemId(164010028, 1)) { //Stellium.
		    effectController.removeEffect(5858);
			effectController.removeEffect(5860);
			SkillEngine.getInstance().applyEffectDirectly(5857, player, player, 300000 * 1); //Marchutan's Apostle.
		} else if (dialogId == 10002 && player.getInventory().decreaseByItemId(164010028, 1)) { //Stellium.
		    effectController.removeEffect(5857);
			effectController.removeEffect(5858);
			SkillEngine.getInstance().applyEffectDirectly(5860, player, player, 300000 * 1); //Siel's Apostle.
		}
		//Consumed 1 Stellium and received a Transparent Transformation skill buff.
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1405269));
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        return true;
    }
}