/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.player;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.dao.PlayerCubicsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.cubics.PlayerMCEntry;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.cubics.CubicsTemplate;
import com.aionemu.gameserver.model.templates.cubics.StatCoreList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBIC;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBIC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 7.x Cubic registration/stat bridge.
 *
 * Phase7 fixes the previous per-singleton mutable stat list, nested login packet
 * spam, and missing null/item checks that could crash the opcode path.
 *
 * @author Phantom_KNA
 */
public class PlayerCubicService implements StatOwner {

	private PlayerCubicService() {
		GameServer.log.info("[PlayerCubic] loaded ...");
	}

	public void onLogin(Player player) {
		if (player == null) {
			return;
		}
		player.getGameStats().endEffect(this);
		player.setBonus(false);
		player.setMonsterCubic(DAOManager.getDAO(PlayerCubicsDAO.class).load(player));
		PacketSendUtility.sendPacket(player, new SM_CUBIC_INFO(124));
		if (player.getMonsterCubic() != null) {
			for (PlayerMCEntry entry : player.getMonsterCubic().getAllMC()) {
				sendCubicInfo(player, entry);
			}
		}
		rebuildStats(player);
	}

	private void sendCubicInfo(Player player, PlayerMCEntry entry) {
		if (player == null || entry == null) {
			return;
		}
		CubicsTemplate template = DataManager.CUBICS_DATA.getCubicsId(entry.getCubeId());
		if (template == null) {
			return;
		}
		long itemsCubicInBag = player.getInventory().getItemCountByItemId(template.getItemIdCubic());
		PacketSendUtility.sendPacket(player, new SM_CUBIC(entry.getCubeId(), entry.getRank(), entry.getLevel(), (int) itemsCubicInBag));
	}

	private void rebuildStats(Player player) {
		player.getGameStats().endEffect(this);
		List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
		if (player.getMonsterCubic() != null) {
			for (PlayerMCEntry entry : player.getMonsterCubic().getAllMC()) {
				StatEnum stat = getStatValueByCategory(entry.getCategory());
				if (stat != null && entry.getStatValue() != 0) {
					modifiers.add(new StatAddFunction(stat, entry.getStatValue(), true));
				}
			}
		}
		player.setBonus(true);
		player.getGameStats().addEffect(this, modifiers);
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}

	private StatEnum getStatValueByCategory(int category) {
		switch (category) {
			case 88:
				return StatEnum.cubic_stat_catacombs_3rd;
			case 89:
				return StatEnum.idseal_hard_boss_3rd;
			case 90:
				return StatEnum.idseal_hard_boss_2nd;
			case 91:
				return StatEnum.idf8_Dragon_Altar;
			case 92:
				return StatEnum.idseal_hard_boss_1st;
			case 93:
				return StatEnum.IDF8_House_HugeRider;
			case 94:
				return StatEnum.IDLDF8_Lab_Boss;
			case 95:
				return StatEnum.IDF7_Weapon_Hard_Boss_1st;
			case 96:
				return StatEnum.IDF7_Weapon_Hard_Boss_2nd;
			case 97:
				return StatEnum.IDF7_Weapon_Hard_Boss_3rd;
			case 98:
				return StatEnum.IDF7_Weapon_Hard_Boss_Final;
			case 99:
				return StatEnum.REDUCE_ERESHKIGAL_DAMAGE;
			case 100:
				return StatEnum.MAXHP;
			case 101:
				return StatEnum.MAXMP;
			case 102:
				return StatEnum.HEAL_BOOST;
			case 103:
				return StatEnum.PHYSICAL_ATTACK;
			case 104:
				return StatEnum.BOOST_MAGICAL_SKILL;
			case 105:
				return StatEnum.PHYSICAL_DEFENSE;
			case 106:
				return StatEnum.MAGICAL_DEFEND;
			case 107:
				return StatEnum.PHYSICAL_ACCURACY;
			case 108:
				return StatEnum.MAGICAL_ACCURACY;
			case 109:
				return StatEnum.EVASION;
			case 110:
				return StatEnum.PARRY;
			case 111:
				return StatEnum.BLOCK;
			case 112:
				return StatEnum.MAGICAL_RESIST;
			default:
				return null;
		}
	}

	public void registerCubic(Player player, int cubicId) {
		if (player == null || cubicId <= 0) {
			return;
		}
		CubicsTemplate monsterCubic = DataManager.CUBICS_DATA.getCubicsId(cubicId);
		if (monsterCubic == null) {
			return;
		}
		if (player.getInventory().getItemCountByItemId(monsterCubic.getItemIdCubic()) <= 0) {
			return;
		}
		int rankById = DAOManager.getDAO(PlayerCubicsDAO.class).getRankById(player.getObjectId(), cubicId);
		int level = DAOManager.getDAO(PlayerCubicsDAO.class).getLevelById(player.getObjectId(), cubicId);
		int statValue = DAOManager.getDAO(PlayerCubicsDAO.class).getStatValueById(player.getObjectId(), cubicId);
		level++;
		if (monsterCubic.getStatLists() != null) {
			for (StatCoreList coreList : monsterCubic.getStatLists()) {
				if (coreList != null && rankById < monsterCubic.getMaxRank() && level == coreList.getLevel()) {
					rankById++;
					statValue = coreList.getValue();
					break;
				}
			}
		}
		player.getInventory().decreaseByItemId(monsterCubic.getItemIdCubic(), 1);
		if (player.getMonsterCubic() == null) {
			player.setMonsterCubic(DAOManager.getDAO(PlayerCubicsDAO.class).load(player));
		}
		player.getMonsterCubic().add(player, cubicId, rankById, level, statValue, monsterCubic.getCategory());
		PacketSendUtility.sendPacket(player, new SM_CUBIC(cubicId, rankById, level, 0));
		long itemsCubicInBag = player.getInventory().getItemCountByItemId(monsterCubic.getItemIdCubic());
		if (itemsCubicInBag > 0 && monsterCubic.getMaxRank() != rankById) {
			PacketSendUtility.sendPacket(player, new SM_CUBIC(cubicId, rankById, level, (int) itemsCubicInBag));
		}
		rebuildStats(player);
	}

	public static PlayerCubicService getInstance() {
		return NewSingletonHolder.INSTANCE;
	}

	private static class NewSingletonHolder {
		private static final PlayerCubicService INSTANCE = new PlayerCubicService();
	}
}
