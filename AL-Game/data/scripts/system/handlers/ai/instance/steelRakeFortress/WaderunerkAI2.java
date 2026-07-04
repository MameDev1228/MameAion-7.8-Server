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
package ai.instance.steelRakeFortress;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Waderunerk")
public class WaderunerkAI2 extends GeneralNpcAI2
{
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
    @Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		int instanceId = getPosition().getInstanceId();
        if (dialogId == 10000) {
            switch (getNpcId()) {
                case 837625: //Waderunerk.
					if (player.getInventory().getItemCountByItemId(164010023) < 1) {
						ItemService.addItem(player, 164010023, 1);
						ItemService.addItem(player, 164010024, 1);
						//Use the Remodeled Swift Runner Boarding Device to defeat Shulacks and Kobolds.
						PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDWaterworld_10, 0);
					} else {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
					}
                break;
				case 837627: //Waderunerk.
					TeleportService2.teleportTo(player, 302520000, instanceId, 63.0000f, 156.0000f, 119.0000f, (byte) 23);
                break;
            }
        }
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		AI2Actions.deleteOwner(this);
		return true;
    }
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 50) {
                if (startedEvent.compareAndSet(false, true)) {
                    startWaderunerk();
                }
            }
        }
    }
	
	private void startWaderunerk() {
        if (!isAlreadyDead()) {
			switch (getNpcId()) {
				case 837625:
				    //Shugo hurried to catch Daeva, nyerk!
                    sendMsg(1502245, 10000);
					//Shugo has something useful for Daeva!
                    sendMsg(1502246, 15000);
					//Daeva will need keys to free friends, nyerk!
                    sendMsg(1502253, 20000);
                break;
				case 837627:
				    //Daeva saved so many shugo friends.... Shugo has prepared a nice place for Daeva to escape the summer heat, nyerk! Shugo hopes Daeva likes it!
					sendMsg(1502254, 6000);
					//Many thanks! Be well, Daeva, nyerk!
					sendMsg(1502249, 12000);
                break;
            }
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isAlreadyDead()) {
						if (getNpcId() == 837625) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
                                        getSpawnTemplate().setWalkerId("Waderunerk_1");
                                        setStateIfNot(AIState.WALKING);
                                        think();
                                    }
                                }
                            }, 1000);
                        } else if (getNpcId() == 837627) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
                                        getSpawnTemplate().setWalkerId("Waderunerk_2");
                                        setStateIfNot(AIState.WALKING);
                                        think();
                                    }
                                }
                            }, 1000);
                        }
                    }
                }
            }, 1000);
        }
    }
	
	private void sendMsg(int msg, int delay) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, delay);
    }
}