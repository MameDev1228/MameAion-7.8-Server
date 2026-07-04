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
package ai.worlds.crimson_katalam;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.observer.*;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Crimson_Katalam_Crystal_Box")
public class Crimson_Katalam_Crystal_BoxAI2 extends NpcAI2
{
	protected int startBarAnimation = 1;
	protected int cancelBarAnimation = 2;
	
	@Override
	protected void handleDialogStart(Player player) {
		handleUseItemStart(player);
	}
	
	protected void handleUseItemStart(final Player player) {
		final int delay = getTalkDelay();
		if (delay != 0) {
			final ItemUseObserver observer = new ItemUseObserver() {
				@Override
				public void abort() {
					player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
					PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, cancelBarAnimation));
					player.getObserveController().removeObserver(this);
				}
			};
			player.getObserveController().attach(observer);
			PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), startBarAnimation));
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
			player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
					PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), cancelBarAnimation));
					player.getObserveController().removeObserver(observer);
					handleUseItemFinish(player);
				}
			}, delay));
		} else {
			handleUseItemFinish(player);
		}
	}
	
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
			case 807224: //Crimson Katalam Crystal Box [Weapon & Armor].
			    if (!player.getInventory().decreaseByItemId(185001084, 1)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1111300, player.getObjectId(), 2));
					return;
				} switch (Rnd.get(1, 2)) {
					case 1:
						ItemService.addItem(player, 188075397, 1); //wrap_ldf5a_treasure_reward_am_f_80a_re
					break;
					case 2:
						ItemService.addItem(player, 188075407, 1); //wrap_ldf5a_treasure_reward_wp_f_80a_re
					break;
				}
			break;
			case 807225: //Crimson Katalam Crystal Box [Plume & Accessory].
			    if (!player.getInventory().decreaseByItemId(185001085, 1)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1111300, player.getObjectId(), 2));
					return;
				} switch (Rnd.get(1, 3)) {
					case 1:
						ItemService.addItem(player, 188075396, 1); //wrap_ldf5a_treasure_reward_ac_f_80a_re
					break;
					case 2:
						ItemService.addItem(player, 188075406, 1); //wrap_ldf5a_treasure_reward_tucker_f_80a_re
					break;
				}
			break;
			case 807226: //Crimson Katalam Crystal Box [Enchantment Stone & Manastone].
			    if (!player.getInventory().decreaseByItemId(185001086, 1)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1111300, player.getObjectId(), 2));
					return;
				} switch (Rnd.get(1, 4)) {
					case 1:
						ItemService.addItem(player, 188072343, 1); //Ancient Manastone.
					break;
					case 2:
						ItemService.addItem(player, 188072343, 1); //Legendary Manastone.
					break;
					case 3:
						ItemService.addItem(player, 188072345, 1); //Ultimate Manastone.
					break;
					case 4:
						ItemService.addItem(player, 188058954, 1); //Random Enchantment Stone Bundle.
					break;
				}
			break;
			case 807227: //Crimson Katalam Crystal Box [Odian Stone & Rune Stone]
			    if (!player.getInventory().decreaseByItemId(185001087, 1)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1111300, player.getObjectId(), 2));
					return;
				} switch (Rnd.get(1, 9)) {
					case 1:
						ItemService.addItem(player, 188120084, 2);
					break;
					case 2:
						ItemService.addItem(player, 188120085, 2);
					break;
					case 3:
						ItemService.addItem(player, 188120086, 2);
					break;
					case 4:
						ItemService.addItem(player, 188120087, 2);
					break;
					case 5:
						ItemService.addItem(player, 188120088, 2);
					break;
					case 6:
						ItemService.addItem(player, 188120089, 2);
					break;
					case 7:
						ItemService.addItem(player, 188120090, 2);
					break;
					case 8:
						ItemService.addItem(player, 188120091, 2);
					break;
					case 9:
						ItemService.addItem(player, 188120092, 2);
					break;
				}
			break;
        }
	}
	
	protected int getTalkDelay() {
		return getObjectTemplate().getTalkDelay() * 1000;
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}