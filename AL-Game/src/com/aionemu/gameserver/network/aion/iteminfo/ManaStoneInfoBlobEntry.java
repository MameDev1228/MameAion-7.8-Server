package com.aionemu.gameserver.network.aion.iteminfo;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.item.ItemRndBonus;
import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.templates.item.EnchantType;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;

public class ManaStoneInfoBlobEntry extends ItemBlobEntry
{
	ManaStoneInfoBlobEntry() {
		super(ItemBlobType.MANA_SOCKETS);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;
		writeC(buf, item.isSoulBound() ? 1 : 0);
		writeC(buf, item.getEnchantLevel());
		writeD(buf, item.getItemSkinTemplate().getTemplateId());
		writeC(buf, item.getOptionalSocket());
		writeC(buf, item.getOptionalFusionSocket());
		writeItemStones(buf);
		ItemStone god = item.getGodStone();
		writeD(buf, god == null ? 0 : god.getItemId());
		int itemColor = item.getItemColor();
		int dyeExpiration = item.getColorTimeLeft();
		if ((dyeExpiration > 0 && item.getColorExpireTime() > 0 || dyeExpiration == 0 && item.getColorExpireTime() == 0) && item.getItemTemplate().isItemDyePermitted()) {
			writeC(buf, itemColor == 0 ? 0 : 1);
			writeD(buf, itemColor);
			writeD(buf, 0);
			writeD(buf, dyeExpiration);
		} else {
			writeC(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
		}
		IdianStone idianStone = item.getIdianStone();
		if (idianStone != null && idianStone.getPolishNumber() > 0) {
			writeD(buf, idianStone.getItemId());
			writeC(buf, idianStone.getPolishNumber());
		} else {
			writeD(buf, 0);
			writeC(buf, 0);
		}
		writeC(buf, item.getAuthorizeLevel());
		writeH(buf, 0);
		writeB(buf, new byte[100]);
		writeC(buf, item.isAmplified() ? 1 : 0);
		writeD(buf, item.getAmplificationSkill());
		writeB(buf, new byte[12]);
		writeD(buf, item.getEnhanceSkillId());
		writeD(buf, item.getEnhanceEnchantLevel());
		writeD(buf, item.isLunaReskin() ? 1 : 0);
		writeC(buf, item.getReductionLevel());
		writeTuning(buf); //7.5 ok
		writeD(buf, item.getItemSkinTemplate().getTemplateId());
		writeGrind(buf); //17 Bytes
	}

	private void writeGrind(ByteBuffer buf) {
		Item item = this.ownerItem;
		writeC(buf, item.getGrindSocket()); //grinding slot
		writeC(buf, item.getGrindColor()); //grinding slot color
		writeQ(buf,0); //grind objectId
		writeC(buf, item.isContaminated() ? 1 : 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeD(buf, 0);
		//writeB(buf, "01 00 E7 11 A8 1E 00 00 00 00 00 00 00 03 00 00 00");
	}

	private void writeTuning(ByteBuffer buf) {
		Item item = this.ownerItem;
		int diff = 10 - item.getRndBonus().size();
		//tuning stats
		for (ItemRndBonus rndBonus : item.getRndBonus().values()) {
			writeH(buf, rndBonus.getBonus());
		}
		for (int i=0; i < diff; i++) {
			writeH(buf, 0);
		}
		//tuning stats value
		for (ItemRndBonus rndBonus : item.getRndBonus().values()) {
			writeH(buf, rndBonus.getValue());
		}
		for (int j=0; j < diff; j++) {
			writeH(buf, 0);
		}
	}

	private void writeItemStones(ByteBuffer buf) {
		Item item = ownerItem;
		int count = 0;
		if (item.hasManaStones()) {
			Set<ManaStone> itemStones = item.getItemStones();
			ArrayList<ManaStone> basicStones = new ArrayList<ManaStone>();
			for (ManaStone itemStone : itemStones) {
				basicStones.add(itemStone);
			}
			for (ManaStone basicStone : basicStones) {
				if (count == 6) {
					break;
				}
				writeD(buf, basicStone.getItemId());
				count++;
			}
			skip(buf, (6 - count) * 4);
		} else {
			skip(buf, 24);
		}
	}

	@Override
	public int getSize() {
		return 248; // 7.5
	}
}