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

@AIName("IDCustomize_Control_NPC_01")
public class IDCustomize_Control_NPC_01AI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000) {
			WorldMapInstance instance1 = getPosition().getWorldMapInstance();
			killNpc(instance1.getNpcs(839690)); //Central Pillar.
			killNpc(instance1.getNpcs(656170)); //Flame.
			//Gladiator Statue.
		    spawn(839689, 253.0000f, 297.0000f, 72.0000f, (byte) 0, 226);
			spawn(839689, 253.0000f, 266.0000f, 72.0000f, (byte) 0, 227);
			spawn(839689, 284.0000f, 266.0000f, 72.0000f, (byte) 0, 228);
			spawn(839689, 284.0000f, 297.0000f, 72.0000f, (byte) 0, 229);
		} if (dialogId == 10001) {
		    WorldMapInstance instance2 = getPosition().getWorldMapInstance();
			killNpc(instance2.getNpcs(839689)); //Gladiator Statue.
			killNpc(instance2.getNpcs(656170)); //Flame.
			//Central Pillar.
			spawn(839690, 269.0000f, 281.0000f, 72.0000f, (byte) 0, 225);
		} if (dialogId == 10002) {
			WorldMapInstance instance3 = getPosition().getWorldMapInstance();
			killNpc(instance3.getNpcs(839689)); //Gladiator Statue.
			killNpc(instance3.getNpcs(839690)); //Central Pillar.
			//Flame.
			spawn(656170, 254.0000f, 266.0000f, 72.0000f, (byte) 15);
            spawn(656170, 249.0000f, 273.0000f, 72.0000f, (byte) 8);
            spawn(656170, 248.0000f, 281.0000f, 72.0000f, (byte) 0);
            spawn(656170, 249.0000f, 289.0000f, 72.0000f, (byte) 113);
            spawn(656170, 253.0000f, 296.0000f, 72.0000f, (byte) 105);
            spawn(656170, 284.0000f, 297.0000f, 72.0000f, (byte) 75);
            spawn(656170, 288.0000f, 288.0000f, 72.0000f, (byte) 66);
            spawn(656170, 290.0000f, 279.0000f, 72.0000f, (byte) 58);
            spawn(656170, 288.0000f, 273.0000f, 72.0000f, (byte) 53);
            spawn(656170, 283.0000f, 266.0000f, 72.0000f, (byte) 44);
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