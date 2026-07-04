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
package ai.instance.shugoEmperorVault;

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

@AIName("Emperor_Premium_Treasure_Box")
public class Emperor_Premium_Treasure_BoxAI2 extends NpcAI2
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
		if (!player.getInventory().decreaseByItemId(185000222, 3)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1111300, player.getObjectId(), 2));
			return;
		} switch (Rnd.get(1, 22)) {
			case 1:
			    ItemService.addItem(player, 188071500, 1); //Finality Manastone Box.
			break;
			case 2:
			    ItemService.addItem(player, 188071499, 1); //Relic Manastone Box.
			break;
			case 3:
			    ItemService.addItem(player, 188071498, 1); //Ancient Manastone Box.
			break;
			case 4:
			    ItemService.addItem(player, 188072043, 1); //[이벤트] 궁극의 스플렌 장비 상자.
			break;
			case 5:
			    ItemService.addItem(player, 188070064, 1); //Motion Card Selection Box.
			break;
			case 6:
			    ItemService.addItem(player, 188070378, 1); //Heavy Kinah Pouch.
			break;
			case 7:
			    ItemService.addItem(player, 188070376, 2); //Golden Box Of Minion Contracts.
			break;
			case 8:
			    ItemService.addItem(player, 190100294, 1); //White Tiger Mount.
			break;
			case 9:
			    ItemService.addItem(player, 188900055, 2); //Strengthened Secret Remedy Of Growth.
			break;
			case 10:
			    ItemService.addItem(player, 166033100, 5); //Ancient PvP Enchantment Stone.
			break;
			case 11:
			    ItemService.addItem(player, 166033101, 5); //Legendary PvP Enchantment Stone.
			break;
			case 12:
			    ItemService.addItem(player, 166033102, 5); //Ultimate PvP Enchantment Stone.
			break;
			case 13:
			    ItemService.addItem(player, 166023100, 5); //Ancient PvE Enchantment Stone.
			break;
			case 14:
			    ItemService.addItem(player, 166023101, 5); //Legendary PvE Enchantment Stone.
			break;
			case 15:
			    ItemService.addItem(player, 166023102, 5); //Ultimate PvE Enchantment Stone.
			break;
			case 16:
			    ItemService.addItem(player, 186020002, 5); //Gold Ingots.
			break;
			case 17:
			    ItemService.addItem(player, 166401000, 500); //Manastone Fastener.
			break;
			case 18:
			    ItemService.addItem(player, 188071961, 1); //Luna Wooden Box.
			break;
			case 19:
			    ItemService.addItem(player, 188071054, 1); //Stigma Selection Box.
			break;
			case 20:
			    ItemService.addItem(player, 188070768, 1); //Ancient Daevanion Skill Box.
			break;
			case 21:
			    ItemService.addItem(player, 188070330, 1); //Legendary Daevanion Skill Box.
			break;
			case 22:
			    ItemService.addItem(player, 169610397, 1); //[Title Card] Special Daeva 180 Day Pass.
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