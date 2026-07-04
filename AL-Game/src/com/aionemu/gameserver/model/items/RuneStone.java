package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

public class RuneStone extends ItemStone {

    private final int runeTransformGroup;
    private final ItemTemplate runeItem;

    public RuneStone(int itemObjId, int itemId, PersistentState persistentState) {
        super(itemObjId, itemId, 0, persistentState);
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        runeItem = itemTemplate;
        runeTransformGroup = itemTemplate.getRunTransformTableId();
    }

    public void onEquip(final Player player) {
        if (runeItem == null) {
            return;
        }
        //add player odian skill linked ton odian stone
    }
}
