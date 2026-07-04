package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.utils.MameClientCompatDebug;
import com.aionemu.gameserver.utils.stats.enums.CRIT_SPELL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SM_STATS_INFO extends AionServerPacket
{
	Logger log = LoggerFactory.getLogger(SM_STATS_INFO.class);
	private Player player;
	private PlayerGameStats pgs;
	private PlayerLifeStats pls;
	private PlayerCommonData pcd;
	
	public SM_STATS_INFO(Player player) {
		this.player = player;
		this.pcd = player.getCommonData();
		this.pgs = player.getGameStats();
		this.pls = player.getLifeStats();
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		MameClientCompatDebug.logStats(player, "SM_STATS_INFO-" + MameClientCompatDebug.getStatsInfoModeName());
		writeD(player.getObjectId());
		writeD(GameTimeManager.getGameTime().getTime());
		//Current Stats
		writeH(pgs.getPower().getCurrent());
		writeH(pgs.getHealth().getCurrent());
		writeH(pgs.getAccuracy().getCurrent());
		writeH(pgs.getAgility().getCurrent());
		writeH(pgs.getKnowledge().getCurrent());
		writeH(pgs.getWill().getCurrent());
		writeH(pgs.getStat(StatEnum.WATER_RESISTANCE, 0).getCurrent());
		writeH(pgs.getStat(StatEnum.WIND_RESISTANCE, 0).getCurrent());
		writeH(pgs.getStat(StatEnum.EARTH_RESISTANCE, 0).getCurrent());
		writeH(pgs.getStat(StatEnum.FIRE_RESISTANCE, 0).getCurrent());
		writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getCurrent());
		writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getCurrent());
		writeH(player.getLevel());
		writeH(0);
		writeH(0);
		writeH(0);
		writeQ(pcd.getExpNeed());
		writeQ(pcd.getExpRecoverable());
		writeQ(pcd.getExpShown());
		writeD(0);

		writeD(pgs.getMaxHp().getCurrent());
		writeD(pls.getCurrentHp());
		writeD(pgs.getMaxMp().getCurrent());
		writeD(pls.getCurrentMp());
		writeH(pgs.getMaxDp().getCurrent());
		writeH(pcd.getDp());
		writeD(pgs.getFlyTime().getCurrent());// [max fly time]
		writeD(pls.getCurrentFp());// [current fly time]
		writeD(player.getFlyState());// [fly state]
		writeD(pgs.getMainHandPAttack().getCurrent());
		writeD(pgs.getOffHandPAttack().getCurrent());
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getPDef().getCurrent() : 0); // current physical defence, ArchSoft confirmed
        writeD(pgs.getMainHandMAttack().getCurrent());
        writeD(pgs.getOffHandMAttack().getCurrent());
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMDef().getCurrent() : 0); // current magical defence, ArchSoft confirmed
		writeD(pgs.getMResist().getCurrent()); //magic resist
		writeF(pgs.getAttackRange().getCurrent() / 1000);
		writeD(pgs.getAttackSpeed().getCurrent());
		writeD(pgs.getEvasion().getCurrent()); //eva
		writeD(pgs.getParry().getCurrent()); //parry
		writeD(pgs.getBlock().getCurrent()); //block
		writeH(pgs.getPCritical().getCurrent());
		writeH(pgs.getOffHandPCritical().getCurrent());
		writeD(pgs.getPAccuracy().getCurrent()); //accu
		writeD(pgs.getOffHandPAccuracy().getCurrent());
		writeH(1); //unk
		writeH(0); //unk
		writeD(pgs.getMAccuracy().getCurrent()); //magic accu
		writeH(pgs.getMCritical().getCurrent()); //crit spell current
		writeH(0); //crit spell offhand
		writeF(pgs.getReverseStat(StatEnum.BOOST_CASTING_TIME, 1000).getCurrent() / 1000f);
		writeH(0);//unk
		writeH(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? 0 : 17); // ArchSoft writes this Dword as zero; keep legacy 17 only when disabled
		writeD(pgs.getPhysicPowerBoost().getCurrent()); //current physic atttak 6.0 ??
		writeD(pgs.getPhysicPowerBoostResist().getCurrent()); //current physic def 6.0//Ok
		writeD(pgs.getMagicPowerBoost().getCurrent()); //current magic atttak 6.0 //ok
		writeD(pgs.getMagicPowerBoostResist().getCurrent()); //current magic def 6.0//ok
		writeD(pgs.getPvpPowerBoost().getCurrent()); //pvp damage
        writeD(pgs.getPvpPowerBoostResist().getCurrent()); //pvp def
        writeD(pgs.getPvePowerBoost().getCurrent()); //pve damage
        writeD(pgs.getPvePowerBoostResist().getCurrent()); //pve def
		// ArchSoft hard-order current extended combat block.
		// Keep the CC2/EU7.7 packet length, but do not duplicate heal boost into the
		// magical defence slot. The previous mixed mapping made the profile tooltip compare
		// against wrong base fields and showed unrelated red/negative values.
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMagicPowerBoost().getCurrent() : (MameClientCompatDebug.isStatsInfoCc2CleanMode() ? pgs.getPhysicDamageBoost().getCurrent() : pgs.getMDef().getCurrent())); // ArchSoft current magical attack 2
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMagicPowerBoostResist().getCurrent() : pgs.getHealBoost().getCurrent()); // ArchSoft current magical defence 2
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getHealBoost().getCurrent() : 107); // ArchSoft current healing boost
		writeD(0); // ArchSoft/7.x reserved, never write fake fixed stat here

		writeH(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getPhysicDamageBoostResist().getCurrent() : 180); //7.2 Physical Damage Boost Resist / legacy hidden value
		writeH(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMagicDamageBoostResist().getCurrent() : 50); //7.2 Magical Damage Boost Resist / legacy hidden value
		writeH(pgs.getPhysicDamageBoost().getCurrent()); //7.2 Physical Damage Boost
		writeH(pgs.getMagicDamageBoost().getCurrent()); //7.2 Magical Damage Boost


		writeD((27 + (player.getInventory().size() * 9)));
		writeD(player.getInventory().size());
		writeQ(0); //unk

		writeD(pcd.getPlayerClass().getClassId()); //ok
		writeH(player.getPlayerSettings().getDisplay());
		writeH(player.getPlayerSettings().getDeny());

		writeD(1); //unk 7.2
		writeQ(pcd.getCurrentReposteEnergy());
		writeQ(pcd.getMaxReposteEnergy());

		writeQ(0);
		writeQ(0);

		writeQ(pcd.getBerdinStar()); //Berdin's Favor.
		writeQ(0);
		writeQ(pcd.getAbyssFavor()); //Abyss Favor.
		writeQ(0);
		writeQ(0);
		writeQ(0);


		// Special state resist / penetration block.
		// The old 7.7 source wrote literal 1..24 here, which makes the CC2/KR profile show
		// fake Aether/Fear/Stun/etc values. In cc2-clean mode send real stats instead.
		if (GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE || MameClientCompatDebug.isStatsInfoCc2CleanMode()) {
			writeSpecialStateResistBlock();
			writeSpecialStatePenetrationBlock();
		} else {
			writeH(1);
			writeH(2);
			writeH(3);
			writeH(4);
			writeH(5);
			writeH(6);
			writeH(7);
			writeH(8);
			writeH(9);
			writeH(10);
			writeH(11);
			writeH(12);
			writeH(13);
			writeH(14);
			writeH(15);
			writeH(16);
			writeH(17);
			writeH(18);
			writeH(19);
			writeH(20);
			writeH(21);
			writeH(22);
			writeH(23);
			writeH(24);
		}

		writeH(pgs.getPower().getBase());
		writeH(pgs.getHealth().getBase());
		writeH(pgs.getAccuracy().getBase());
		writeH(pgs.getAgility().getBase());
		writeH(pgs.getKnowledge().getBase());
		writeH(pgs.getWill().getBase());
		writeH(pgs.getStat(StatEnum.WATER_RESISTANCE, 0).getBase());
		writeH(pgs.getStat(StatEnum.WIND_RESISTANCE, 0).getBase());
		writeH(pgs.getStat(StatEnum.EARTH_RESISTANCE, 0).getBase());
		writeH(pgs.getStat(StatEnum.FIRE_RESISTANCE, 0).getBase());
		writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getBase());
		writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getBase());
		writeD(pgs.getMaxHp().getBase());
		writeD(pgs.getMaxMp().getBase());
		writeD(pgs.getMaxDp().getBase());
		writeD(pgs.getFlyTime().getBase());
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMainHandPAttack().getCurrent() : pgs.getMainHandPAttack().getBase());
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getOffHandPAttack().getCurrent() : pgs.getOffHandPAttack().getBase());
        writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMainHandMAttack().getCurrent() : pgs.getMainHandMAttack().getBase());
        writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getOffHandMAttack().getCurrent() : pgs.getOffHandMAttack().getBase());

		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? (MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getPDef().getCurrent() : pgs.getPDef().getBase()) : 0); // base/current physical defence, ArchSoft confirmed
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? (MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMDef().getCurrent() : pgs.getMDef().getBase()) : 0); // base/current magical defence, ArchSoft confirmed
		writeD(0); //unk 7.5 //6039
		writeH(0); //unk 7.5 //0
		writeH(0); //unk 7.5 //16752

		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMResist().getCurrent() : pgs.getMResist().getBase()); //unk
		writeF((MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getAttackRange().getCurrent() : pgs.getAttackRange().getBase()) / 1000f);
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getEvasion().getCurrent() : pgs.getEvasion().getBase()); //evasion

		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getParry().getCurrent() : pgs.getParry().getBase()); //base parry
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getBlock().getCurrent() : pgs.getBlock().getBase()); //base block
		writeH(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getPCritical().getCurrent() : pgs.getPCritical().getBase());
		writeH(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getOffHandPCritical().getCurrent() : pgs.getOffHandPCritical().getBase());

		writeH(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMCritical().getCurrent() : pgs.getMCritical().getBase());
		writeH(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMCritical().getCurrent() : pgs.getMCritical().getBase()); //off hand

		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getPAccuracy().getCurrent() : pgs.getPAccuracy().getBase()); //base accu
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getOffHandPAccuracy().getCurrent() : pgs.getOffHandPAccuracy().getBase());

		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? 0 : 1); // ArchSoft writes zero; keep legacy one only when disabled
		// CC2/KR 7.7 reads this tooltip-base slot for modern Physical Attack.
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getPhysicPowerBoost().getBase() : (MameClientCompatDebug.isStatsInfoCc2CleanMode() ? pgs.getPhysicPowerBoost().getBase() : (MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMAccuracy().getCurrent() : pgs.getMAccuracy().getBase())));
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMAccuracy().getBase() : 0); // base magic accuracy fallback, ArchSoft confirmed nearby slot
		// Keep the original physical-power slot for legacy mode; ArchSoft mode sends the confirmed base Physical Attack.
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getPhysicPowerBoost().getBase() : (MameClientCompatDebug.isStatsInfoCc2CleanMode() ? pgs.getMAccuracy().getBase() : (MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getPhysicPowerBoost().getCurrent() : pgs.getPhysicPowerBoost().getBase()))); //base physical attack 6.x
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getPhysicPowerBoostResist().getCurrent() : pgs.getPhysicPowerBoostResist().getBase()); //base physical defence 6.x
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMagicPowerBoost().getCurrent() : pgs.getMagicPowerBoost().getBase()); //base magical attack 6.x
		writeD(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMagicPowerBoostResist().getCurrent() : pgs.getMagicPowerBoostResist().getBase()); //base magical defence 6.x

		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMagicPowerBoost().getBase() : 0); //ArchSoft base magical attack 2 / legacy reserved
		writeD(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMagicPowerBoostResist().getBase() : (MameClientCompatDebug.isStatsInfoCc2CleanMode() ? pgs.getPhysicDamageBoost().getBase() : (MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMDef().getCurrent() : pgs.getMDef().getBase()))); //ArchSoft base magical defence 2 / legacy CC2 probe
		writeH(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getPhysicDamageBoost().getBase() : 0); //7.2 Physical Damage Boost base
		writeH(GSConfig.ARCHSOFT_STATS_DISPLAY_ENABLE ? pgs.getMagicDamageBoost().getBase() : 0); //7.2 Magical Damage Boost base
		writeH(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getPhysicDamageBoostResist().getCurrent() : pgs.getPhysicDamageBoostResist().getBase()); //7.2 Physical Damage Boost Resist
		writeH(MameClientCompatDebug.isStatsInfoBaseCurrentMode() ? pgs.getMagicDamageBoostResist().getCurrent() : pgs.getMagicDamageBoostResist().getBase()); //7.2 Magical Damage Boost Resist
	}

	private void writeSpecialStateResistBlock() {
		writeH(statCurrent(StatEnum.OPENAERIAL_RESISTANCE));
		writeH(statCurrent(StatEnum.FEAR_RESISTANCE));
		writeH(statCurrent(StatEnum.STUN_RESISTANCE));
		writeH(statCurrent(StatEnum.STUMBLE_RESISTANCE));
		writeH(statCurrent(StatEnum.PARALYZE_RESISTANCE));
		writeH(statCurrent(StatEnum.STAGGER_RESISTANCE));
		writeH(statCurrent(StatEnum.BIND_RESISTANCE));
		writeH(statCurrent(StatEnum.ROOT_RESISTANCE));
		writeH(statCurrent(StatEnum.SLEEP_RESISTANCE));
		writeH(statCurrent(StatEnum.BLIND_RESISTANCE));
		writeH(statCurrent(StatEnum.SLOW_RESISTANCE));
		writeH(statCurrent(StatEnum.SILENCE_RESISTANCE));
	}

	private void writeSpecialStatePenetrationBlock() {
		writeH(statCurrent(StatEnum.OPENAERIAL_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.FEAR_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.STUN_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.STUMBLE_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.PARALYZE_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.STAGGER_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.BIND_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.ROOT_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.SLEEP_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.BLIND_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.SLOW_RESISTANCE_PENETRATION));
		writeH(statCurrent(StatEnum.SILENCE_RESISTANCE_PENETRATION));
	}

	private int statCurrent(StatEnum stat) {
		return pgs.getStat(stat, 0).getCurrent();
	}
}
