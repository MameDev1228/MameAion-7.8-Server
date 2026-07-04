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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.dorinerk_wardrobe.PlayerWardrobeEntry;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ranastic
 */

public class SM_LUNA_SHOP extends AionServerPacket
{
	private int actionId;
	private int unk1;
	private int slotSize;
	private int fail;
	private ItemTemplate item;
	//Karunerk's Workshop
	private int unkCraft1;
	private int unkCraft2;
	private int craftItemID;
	private int craftItemCount;
	//Taki's Adventure
	private int indun_id;
	//Munirunerk's Treasure
	private HashMap<Integer, Long> munirunerk_treasure;
	
	private boolean auto;
	private int dice;
	private int goldDice1;
	private int goldDice2;
	
	private int itemId1;
	private int itemId2;
	private int itemId3;
	private long count1;
	private long count2;
	private long count3;
	
	private int isApply;
	private int applySlot;
	private int itemId;
	private int itemSize;
	private int diceCount;
	private boolean isGoldenDice;
	private boolean isSuccessDice;
	private int rewardCount;
	private int diceItemId1;
	private int diceItemid2;
	private int diceItemid3;
	private int diceItemCount1;
	private int diceItemCount2;
	private int diceItemCount3;

	private Player player;

	private boolean success;
	
	private static final Logger log = LoggerFactory.getLogger(SM_LUNA_SHOP.class);
	
	public SM_LUNA_SHOP(int actionId) {
		this.actionId = actionId;
	}
	
	//Karunerk's Workshop
	public SM_LUNA_SHOP(int craftItemID, int craftItemCount, boolean success) {
		this.actionId = 3;
		this.craftItemID = craftItemID;
		this.craftItemCount = craftItemCount;
		this.success = success;
	}
	
	//Taki's Adventure
	public SM_LUNA_SHOP(int actionId, int indun_id) {
		this.actionId = actionId;
		this.indun_id = indun_id;
	}
	
	//Munirunerk's Treasure
	public SM_LUNA_SHOP(HashMap<Integer, Long> munirunerk_treasure) {
		this.actionId = 12;
		this.munirunerk_treasure = munirunerk_treasure;
	}
	
	//Dorinerk's Wardrobe
	public SM_LUNA_SHOP(int actionId, int isApply, int applySlot, int itemId, int unk1) {
		this.actionId = actionId;
		this.isApply = isApply;
		this.applySlot = applySlot;
		this.itemId = itemId;
		this.unk1 = unk1;
	}
	
	public SM_LUNA_SHOP(int actionId, int slotSize, int itemSize) {
		this.actionId = actionId;
		this.slotSize = slotSize;
		this.itemSize = itemSize;
	}
	
	public SM_LUNA_SHOP(int actionId, ItemTemplate item, int fail) {
		this.actionId = actionId;
		this.item = item;
		this.fail = fail;
	}

	public SM_LUNA_SHOP(int actionId, boolean isAuto, int dice, int goldeDice1, int goldDice2) {
		this.actionId = actionId;
		this.auto = isAuto;
		this.dice = dice;
		this.goldDice1 = goldDice1;
		this.goldDice2 = goldDice2;
	}

	public SM_LUNA_SHOP(int actionId, int itemId1, long count1, int itemid2, long count2, int itemId3, long count3) {
		this.actionId = actionId;
		this.itemId1 = itemId1;
		this.count1 = count1;
		this.itemId2 = itemid2;
		this.count2 = count2;
		this.itemId3 = itemId3;
		this.count3 = count3;
	}
	
	public SM_LUNA_SHOP(int action, int rewardCount, int diceItemId1, int diceItemCount1) {
		this.actionId = action;
		this.rewardCount = rewardCount;
		this.diceItemId1 = diceItemId1;
		this.diceItemCount1 = diceItemCount1;
	}
	
	public SM_LUNA_SHOP(int action, Player player, boolean isGoldenDice, boolean isSuccessDice) {
		this.actionId = action;
		this.player = player;
		this.isGoldenDice = isGoldenDice;
		this.isSuccessDice = isSuccessDice;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		writeC(actionId);
		switch (actionId) {
			case 0:
				writeC(0);
				writeD(indun_id);
			break;
			case 2:
				writeC(fail);
				switch (fail) {
					case 0:
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330059, new Object[0]));
					break;
					case 1:
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330050, new Object[] { this.item.getName() }));
					break;
				}
			break;
			case 3:
				writeC(success ? 0 : 1);//success ? 0x00 : 0x01
				writeH(1);
				writeD(craftItemID);//productid
				writeQ(craftItemCount);//quantity
			break;
            case 4:
                writeC(0);
			break;
			case 5:
				writeC(0);
				writeC(0);
			break;
			case 6:
				writeD(53);
			break;
			case 7:
				writeD(55);
			break;
			case 8://dorinerk's wardrobe
				writeC(0x00);
				writeC(slotSize);
				writeH(itemSize);
				for (int i=0;i<itemSize;i++) {
					for (PlayerWardrobeEntry ce : player.getWardrobe().getAllWardrobe()) {
						writeC(ce.getSlot());
						writeD(ce.getItemId());
						writeD(0x00);
						writeD(0x01);
					}
				}
			break;
			case 10:
				writeC(isApply);
				writeC(applySlot);
				writeD(itemId);
				writeD(unk1);
			break;
			case 11:
				writeC(0x00);
				writeC(indun_id);
				writeD(0x01);
			break;
			case 12://open chest
				writeC(0);//unk
				writeH(3);//size always 3
				for (Map.Entry<Integer, Long> e : munirunerk_treasure.entrySet()) {
					writeD(e.getKey());
					writeQ(e.getValue());
				}
			break;
			case 14:
				writeC(1); //free enter = 1
				writeD(indun_id);
			break;
			case 15:
				writeC(player.getLunaDiceReward());
				writeC(player.getLunaDiceCount());
				writeC(isGoldenDice ? 1 : 0);
				writeC(isSuccessDice ? 1 : 0);
			break;
			case 16:
				writeC(0);
				writeH(rewardCount);
				writeD(diceItemId1);
				writeQ(diceItemCount1);
			break;
		}
	}
}