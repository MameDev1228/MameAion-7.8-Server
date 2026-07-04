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
package ai.worlds.gelkmaros;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.*;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("DF4_Directportal_Fame_02_03")
public class DF4_Directportal_Fame_02_03AI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
		if (player.getInventory().getFirstItemByItemId(182010094) != null && player.getPlayerFame().get(2).getLevel() >= 7) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
        }
    }
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000 && player.getInventory().decreaseByItemId(182010094, 1)) {
		    switch (getNpcId()) {
				case 840442: //DF4_Directportal_Fame_02.
					spawn(703934, 1870.2733f, 2699.658f, 535.2708f, (byte) 32);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
					    @Override
					    public void run() {
						    WorldMapInstance instance1 = getPosition().getWorldMapInstance();
						    deleteNpcs(instance1.getNpcs(703934));
				        }
			        }, 10800000); //...3 Hrs.
				break;
				case 840443: //DF4_Directportal_Fame_03.
					spawn(703934, 1904.0215f, 2715.443f, 534.98706f, (byte) 106);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
					    @Override
					    public void run() {
						    WorldMapInstance instance2 = getPosition().getWorldMapInstance();
						    deleteNpcs(instance2.getNpcs(703934));
				        }
			        }, 10800000); //...3 Hrs.
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
}