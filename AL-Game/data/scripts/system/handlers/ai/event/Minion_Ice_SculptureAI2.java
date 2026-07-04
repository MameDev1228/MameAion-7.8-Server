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

@AIName("Minion_Ice_Sculpture")
public class Minion_Ice_SculptureAI2 extends NpcAI2
{
  	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		PlayerEffectController effectController = player.getEffectController();
		if (dialogId == 10000) {
			switch (getNpcId()) {
				case 837649: //Kromede Minion Ice Sculpture.
				case 837652: //Kromede Minion Ice Sculpture.
					effectController.removeEffect(5333);
					effectController.removeEffect(5335);
					SkillEngine.getInstance().applyEffectDirectly(5334, player, player, 3600000 * 1);
				break;
				case 837650: //Grendal Minion Ice Sculpture.
				case 837653: //Grendal Minion Ice Sculpture.
					effectController.removeEffect(5334);
					effectController.removeEffect(5335);
					SkillEngine.getInstance().applyEffectDirectly(5333, player, player, 3600000 * 1);
				break;
				case 837651: //Weatha Minion Ice Sculpture.
				case 837654: //Weatha Minion Ice Sculpture.
					effectController.removeEffect(5333);
					effectController.removeEffect(5334);
					SkillEngine.getInstance().applyEffectDirectly(5335, player, player, 3600000 * 1);
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}