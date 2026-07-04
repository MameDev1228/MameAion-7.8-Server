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

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("IDWaterWorld_Set_Npc_01")
public class IDWaterWorld_Set_Npc_01AI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
    @Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			int instanceId = getPosition().getInstanceId();
			PlayerEffectController effectController = player.getEffectController();
			if (MathUtil.getDistance(getOwner(), player) <= 10) {
				if (startedEvent.compareAndSet(false, true)) {
					canThink = false;
					steelRakeMsg();
					effectController.removeEffect(20321);
					getOwner().getController().onDelete();
					player.getSkillList().addSkill(player, 19500, 1);
					player.getSkillList().addSkill(player, 19501, 1);
					player.getSkillList().addSkill(player, 19524, 1);
					player.getSkillList().addSkill(player, 21803, 1);
					//Steel Rake Alarm.
					spawn(837583, 455.0000f, 459.0000f, 127.0000f, (byte) 0, 59);
					spawn(837584, 596.0000f, 427.0000f, 127.0000f, (byte) 0, 31);
					spawn(837585, 438.0000f, 564.0000f, 127.0000f, (byte) 0, 93);
					//Waderunerk.
					spawn(837625, 649.0000f, 563.0000f, 122.0000f, (byte) 58);
					TeleportService2.teleportTo(player, 302520000, instanceId, 634.0000f, 579.0000f, 122.0000f, (byte) 98);
				}
			}
		}
	}
	
	private void steelRakeMsg() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Get useful items from Waderunerk who has come to help!
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDWaterworld_09, 5000);
					//When the alarm sounds, Steel Rake Kobolds will appear to prevent you from installing bombs.
					//Close the door of the security office to shut off the alarm.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDWaterworld_11, 10000);
					//You need the key that Hutkin’s henchman has to release the Shugo slaves.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDWaterworld_13, 15000);
					//Check the slave contract that Hutkin’s henchman dropped to view the names of the Shugo slaves.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDWaterworld_14, 20000);
					//There is a door around here that is linked to the Fortress Defense Warehouse.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDWaterworld_15, 25000);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}