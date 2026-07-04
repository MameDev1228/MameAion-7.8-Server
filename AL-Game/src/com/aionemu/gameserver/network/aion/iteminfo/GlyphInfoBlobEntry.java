package com.aionemu.gameserver.network.aion.iteminfo;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemSlot;

import java.nio.ByteBuffer;

public class GlyphInfoBlobEntry extends ItemBlobEntry
{
    GlyphInfoBlobEntry() {
        super(ItemInfoBlob.ItemBlobType.GLYPH_INFO);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        Item item = ownerItem;
        writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
        writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
    }

    @Override
    public int getSize() {
        return 16;
    }
}