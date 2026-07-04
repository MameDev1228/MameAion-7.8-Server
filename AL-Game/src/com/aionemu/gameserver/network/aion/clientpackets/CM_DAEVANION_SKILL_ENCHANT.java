package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.enchant.EnchantService;

public class CM_DAEVANION_SKILL_ENCHANT extends AionClientPacket
{
    private int skillId;
    private int bookObjId;
    private int materialObjId;
	
    public CM_DAEVANION_SKILL_ENCHANT(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        this.skillId = readH();
        this.bookObjId = readD();
        this.materialObjId = readD();
    }
	
    @Override
    protected void runImpl() {
        EnchantService.enchantDaevanionSkill(getConnection().getActivePlayer(),this.skillId, this.bookObjId, this.materialObjId);
    }
}