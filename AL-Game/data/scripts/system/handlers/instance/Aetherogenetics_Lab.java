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
package instance;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(310050000)
public class Aetherogenetics_Lab extends GeneralInstanceHandler
{
    private Map<Integer, StaticDoor> doors;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
        switch (npcId) {
			case 653262: //The Keykeeper.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000001, 1)); //Lepharist Research Center Key 1.
		    break;
			case 653233: //Expert Lab Scholar.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000002, 1)); //Lepharist Research Center Key 2.
		    break;
			case 653251: //Hungry Mudthorn.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000003, 1)); //Lepharist Research Center Key 3.
		    break;
			case 653248: //Pretor Key Keeper.
			    dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000004, 1)); //Lepharist Research Center Key 4.
			break;
			case 653263: //Key Eater.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000005, 1)); //Lepharist Research Center Key 5.
		    break;
        }
    }
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
    @Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		ClassChangeService.onUpdateQuest61605(player);
	}
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 653258: //Perfected Pretor.
			    despawnNpc(npc);
				spawn(212206, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Perfected Mudthorn.
			break;
			case 212206: //Perfected Mudthorn.
			    despawnNpc(npc);
				spawn(656126, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Perfected Rotron.
			break;
			case 653259: //RM-78C.
				spawn(836794, 256.0000f, 323.0000f, 127.0000f, (byte) 59); //Aetherogenetics Lab Exit.
			break;
		}
    }
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000001, storage.getItemCountByItemId(185000001)); //Lepharist Research Center Key 1.
		storage.decreaseByItemId(185000002, storage.getItemCountByItemId(185000002)); //Lepharist Research Center Key 2.
		storage.decreaseByItemId(185000003, storage.getItemCountByItemId(185000003)); //Lepharist Research Center Key 3.
		storage.decreaseByItemId(185000004, storage.getItemCountByItemId(185000004)); //Lepharist Research Center Key 4.
		storage.decreaseByItemId(185000005, storage.getItemCountByItemId(185000005)); //Lepharist Research Center Key 5.
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
    @Override
    public void onInstanceDestroy() {
        doors.clear();
    }
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
}