package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

public class OdianStone extends ItemStone {

    private final int odianSkill;
    private final int odianSkillLevel;
    private final ItemTemplate odianItem;

    public OdianStone(int itemObjId, int itemId, PersistentState persistentState) {
        super(itemObjId, itemId, 0, persistentState);
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        odianItem = itemTemplate;
        odianSkill = itemTemplate.getOdianSkillId();
        odianSkillLevel = itemTemplate.getOdianSkillLevel();
    }

    public void onEquip(final Player player) {
        if (odianItem == null) {
            return;
        }
        //add player odian skill linked ton odian stone
    }
}
