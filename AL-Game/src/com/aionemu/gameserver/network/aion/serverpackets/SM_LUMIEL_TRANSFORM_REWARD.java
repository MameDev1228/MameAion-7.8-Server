package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielRewardItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_LUMIEL_TRANSFORM_REWARD extends AionServerPacket {

    private final int lumielId;
    private final LumielRewardItem rewardItem;

    public SM_LUMIEL_TRANSFORM_REWARD(int lumielId, LumielRewardItem rewardItem) {
        this.lumielId = lumielId;
        this.rewardItem = rewardItem;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.lumielId);
        writeD(this.rewardItem.getItemId());
        writeQ(this.rewardItem.getCount());
    }
}
