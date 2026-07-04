package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_DAEVANION_SKILL_ENCHANT extends AionServerPacket {

    private int newLevel;
    private int skillId;
    private int skillLevel;

    public SM_DAEVANION_SKILL_ENCHANT(int skillLevel, int skillId, int newLevel) {
        this.skillLevel = skillLevel;
        this.skillId = skillId;
        this.newLevel = newLevel;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.skillLevel); //level
        writeH(this.skillId); //skill_Id
        writeD(this.newLevel); //new level
    }
}
