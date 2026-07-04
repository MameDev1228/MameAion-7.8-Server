/*
 * This file is part of aion-unique <www.aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.enchant.EnchantService;

import javolution.util.FastList;

public class SM_UPDATE_PLAYER_APPEARANCE extends AionServerPacket
{
	public int playerId;
    public int size;
    public FastList<Item> items;

    public SM_UPDATE_PLAYER_APPEARANCE(int playerId, FastList<Item> items) {
        this.playerId = playerId;
        this.items = items;
        this.size = items.size();
    }

    @Override
	protected void writeImpl(AionConnection con) {
		writeD(playerId);
		int mask = 0;
		for (Item item : items) {
			if (item.getItemTemplate().isTwoHandWeapon()) {
				ItemSlot[] slots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
				mask |= slots[0].getSlotIdMask();
			} else {
				mask |= item.getEquipmentSlot();
			}
		}
		writeD(mask); // DBS size
		for (Item item : items) {
			writeD(item.getItemSkinTemplate().getTemplateId());
			GodStone godStone = item.getGodStone();
			writeD(godStone != null ? godStone.getItemId() : 0);
			writeD(item.getItemColor());
			if (item.getItemTemplate().isAccessory()) {
				if (item.getItemTemplate().isPlume()) {
					float enchant = item.getEnchantPvPvELevel() / 5;
					if (item.getEnchantPvPvELevel() >= 5) {
						enchant = enchant > 2.0F ? 2.0F : enchant;
						writeD((int) enchant << 3);
					} else {
						writeD(0);
					}
				} else if (item.getItemTemplate().isBracelet()) {
					if (item.getEnchantPvPvELevel() >= 5 && item.getEnchantPvPvELevel() < 10) {
						writeD(96);
					} else if (item.getEnchantPvPvELevel() >= 10) {
						writeD(160);
					} else {
						writeD(32);
					}
				} else {
					writeD(item.getEnchantPvPvELevel() >= 5 ? 2 : 0);
				}
			} else if ((item.getItemTemplate().isWeapon()) || (item.getItemTemplate().isTwoHandWeapon())) {
				writeD(item.getEnchantPvPvELevel() == 15 ? 2 : item.getEnchantPvPvELevel() >= 15 ? 4 : 0);
			} else {
				writeD(0);
			}
		}
	}
}