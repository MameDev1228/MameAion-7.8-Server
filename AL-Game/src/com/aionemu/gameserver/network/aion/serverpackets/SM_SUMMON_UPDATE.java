package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.stats.container.SummonGameStats;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_SUMMON_UPDATE extends AionServerPacket
{
	private Summon summon;
	
	public SM_SUMMON_UPDATE(Summon summon) {
		this.summon = summon;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		SummonGameStats stats = summon.getGameStats();
		writeC(summon.getLevel());
		writeH(summon.getMode().getId());
		writeD(0);
		writeD(0);
		//Current
		writeD(summon.getLifeStats().getCurrentHp());
		writeD(stats.getMaxHp().getCurrent());
		writeD(stats.getMainHandPAttack().getCurrent());
		writeD(stats.getPDef().getBonus());
		writeD(stats.getMResist().getCurrent());
		writeD(0);
		writeD(stats.getAccuracy().getCurrent());
		writeH(stats.getPCritical().getCurrent());
		writeD(0);
		writeD(0);
		writeD(stats.getMAccuracy().getCurrent());
		writeH(stats.getMCritical().getCurrent());
		writeD(stats.getParry().getCurrent());
		writeD(stats.getEvasion().getCurrent());
		writeD(stats.getMainHandPAttack().getCurrent());
		writeD(stats.getPDef().getCurrent());
		writeD(stats.getMAttack().getCurrent());
		writeD(stats.getMDef().getCurrent());
		//Base
		writeD(stats.getMaxHp().getBase());
		writeD(stats.getMainHandPAttack().getBase());
		writeD(0);
		writeD(stats.getMResist().getBase());
		writeD(0);
		writeD(stats.getAccuracy().getBase());
		writeH(stats.getPCritical().getBase());
		writeD(0);
		writeD(0);
		writeD(stats.getMAccuracy().getBase());
		writeH(stats.getMCritical().getBase());
		writeD(stats.getParry().getBase());
		writeD(stats.getEvasion().getBase());
		writeD(stats.getMainHandPAttack().getBase());
		writeD(stats.getPDef().getBase());
		writeD(stats.getMAttack().getBase());
		writeD(stats.getMDef().getBase());
	}
}