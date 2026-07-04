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
package ai.instance.genesisArena;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("IDCustomize_Control_NPC_03")
public class IDCustomize_Control_NPC_03AI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000) {
			WorldMapInstance instance1 = getPosition().getWorldMapInstance();
			killNpc(instance1.getNpcs(661344)); //Anoha.
			killNpc(instance1.getNpcs(661345)); //Primeth's.
		    spawn(661343, 269.0000f, 281.0000f, 72.0000f, (byte) 2); //Kunax.
		} if (dialogId == 10001) {
		    WorldMapInstance instance2 = getPosition().getWorldMapInstance();
			killNpc(instance2.getNpcs(661343)); //Kunax.
			killNpc(instance2.getNpcs(661345)); //Primeth's.
			spawn(661344, 269.0000f, 281.0000f, 72.0000f, (byte) 2); //Anoha.
		} if (dialogId == 10002) {
			WorldMapInstance instance3 = getPosition().getWorldMapInstance();
			killNpc(instance3.getNpcs(661343)); //Kunax.
			killNpc(instance3.getNpcs(661344)); //Anoha.
			spawn(661345, 269.0000f, 281.0000f, 72.0000f, (byte) 2); //Primeth's.
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        return true;
    }
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
}