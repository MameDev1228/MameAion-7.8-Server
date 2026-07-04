package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielRewardItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.List;

public class SM_LUMIEL_TRANSFORM_REWARD_LIST extends AionServerPacket {

    private final int lumielId;
    private final List<LumielRewardItem> itemList;

    public SM_LUMIEL_TRANSFORM_REWARD_LIST(int lumielId, List<LumielRewardItem> itemList) {
        this.lumielId = lumielId;
        this.itemList = itemList;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.lumielId);
        writeH(this.itemList.size());
        for (LumielRewardItem rewardItem : this.itemList) {
            writeD(rewardItem.getItemId());
            writeQ(rewardItem.getCount());
        }
    }
}
