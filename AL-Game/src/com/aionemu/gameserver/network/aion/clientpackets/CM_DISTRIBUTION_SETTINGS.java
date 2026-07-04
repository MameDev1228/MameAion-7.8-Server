package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.common.legacy.LootDistribution;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.common.legacy.LootRuleType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_VERSION_CHECK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_DISTRIBUTION_SETTINGS extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int unk1;
	private int lootrul;
	private int misc;
	private LootRuleType lootrules;
	private LootDistribution autodistribution;
	private int common_item_above;
	private int superior_item_above;
	private int heroic_item_above;
	private int fabled_item_above;
	private int ethernal_item_above;
	private int mythic_item_above;
	private int ancien_item_above;
	private int relic_item_above;
	private int finality_item_above;
	
	@SuppressWarnings("unused")
	private int unk2;
	private int autodistr;

	private static final Logger log = LoggerFactory.getLogger(SM_VERSION_CHECK.class);
	
	public CM_DISTRIBUTION_SETTINGS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		unk1 = readD();
		lootrul = readD();
		switch (lootrul) {
			case 0:
				lootrules = LootRuleType.FREEFORALL;
			break;
			case 1:
				lootrules = LootRuleType.ROUNDROBIN;
			break;
			case 2:
				lootrules = LootRuleType.LEADER;
			break;
			default:
				lootrules = LootRuleType.FREEFORALL;
			break;
		}
		misc = readD();
		common_item_above = readD();
		superior_item_above = readD();
		heroic_item_above = readD();
		fabled_item_above = readD();
		ethernal_item_above = readD();
		mythic_item_above = readD();
		ancien_item_above = readD();
		relic_item_above = readD();
		finality_item_above = readD();
		readD();
		log.info("superior_item_above : " + this.superior_item_above);
		if (superior_item_above == 2) {
			autodistribution = LootDistribution.ROLL_DICE;
		} else {
			autodistribution = LootDistribution.NORMAL;
		}
	}
	
	@Override
	protected void runImpl() {
		Player leader = getConnection().getActivePlayer();
		PlayerGroup group = leader.getPlayerGroup2();
		if (group != null) {
			PlayerGroupService.changeGroupRules(group, new LootGroupRules(lootrules, autodistribution, ancien_item_above, relic_item_above, finality_item_above, misc, common_item_above, superior_item_above, heroic_item_above, fabled_item_above, ethernal_item_above, mythic_item_above));
		}
		com.aionemu.gameserver.model.team2.alliance.PlayerAlliance alliance = leader.getPlayerAlliance2();
		if (alliance != null) {
			PlayerAllianceService.changeGroupRules(alliance, new LootGroupRules(lootrules, autodistribution, ancien_item_above, relic_item_above, finality_item_above, misc, common_item_above, superior_item_above, heroic_item_above, fabled_item_above, ethernal_item_above, mythic_item_above));
		}
	}
}