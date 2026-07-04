package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.decomposable.DecomposableItemList;
import com.aionemu.gameserver.model.templates.decomposable.SelectItem;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SM_SELECT_ITEM extends AionServerPacket
{
	private int uniqueItemId;
	private List<DecomposableItemList> selsetitems;
	private static final Logger log = LoggerFactory.getLogger(SM_SELECT_ITEM.class);
	
	public SM_SELECT_ITEM(Player player, List<DecomposableItemList> selsetitem, int uniqueItemId) {
		this.uniqueItemId = uniqueItemId;
		this.selsetitems = selsetitem;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(uniqueItemId);
		writeD(1);
		writeC(selsetitems.size());
		for (int slotCount = 0; slotCount < selsetitems.size(); slotCount++) {
			writeC(slotCount);
			DecomposableItemList rt = selsetitems.get(slotCount);
			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(rt.getId());
			writeD(rt.getId());
			writeD(rt.getCount());
			writeC(itemTemplate.getOptionSlotBonus() > 0 ? 255 : 0);
			writeC(itemTemplate.getMaxEnchantBonus() > 0 ? 255 : 0);
			if (itemTemplate.isArmor() || itemTemplate.isWeapon()) {
				writeC(-1);
			} else {
				writeC(0);
			} if (itemTemplate.isCloth() || itemTemplate.getOptionSlotBonus() > 0 || itemTemplate.getMaxEnchantBonus() > 0) {
				writeC(1);
			} else {
				writeC(0);
			}
		}
	}
}