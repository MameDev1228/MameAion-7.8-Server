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
package ai.worlds.inggison;

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

@AIName("LF4_Directportal_Fame_04_05_06")
public class LF4_Directportal_Fame_04_05_06AI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
		if (player.getInventory().getFirstItemByItemId(182010090) != null && player.getPlayerFame().get(1).getLevel() >= 5) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
        }
    }
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000 && player.getInventory().decreaseByItemId(182010090, 1)) {
		    switch (getNpcId()) {
				case 840438: //LF4_Directportal_Fame_04.
					spawn(703931, 1477.0741f, 391.1276f, 556.8955f, (byte) 112);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
					    @Override
					    public void run() {
						    WorldMapInstance instance1 = getPosition().getWorldMapInstance();
						    deleteNpcs(instance1.getNpcs(703931));
				        }
			        }, 10800000); //...3 Hrs.
				break;
				case 840439: //LF4_Directportal_Fame_05.
					spawn(703931, 1472.1202f, 382.05807f, 556.58234f, (byte) 108);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
					    @Override
					    public void run() {
						    WorldMapInstance instance2 = getPosition().getWorldMapInstance();
						    deleteNpcs(instance2.getNpcs(703931));
				        }
			        }, 10800000); //...3 Hrs.
				break;
				case 840440: //LF4_Directportal_Fame_06.
					spawn(703931, 1465.5945f, 375.53384f, 556.72034f, (byte) 105);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
					    @Override
					    public void run() {
						    WorldMapInstance instance3 = getPosition().getWorldMapInstance();
						    deleteNpcs(instance3.getNpcs(703931));
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