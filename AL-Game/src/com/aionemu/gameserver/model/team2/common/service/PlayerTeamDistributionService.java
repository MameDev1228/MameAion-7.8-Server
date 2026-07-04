package com.aionemu.gameserver.model.team2.common.service;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.player.XPCape;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.MinionService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.player.PlayerFameService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.google.common.base.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PlayerTeamDistributionService
{
	static Logger log = LoggerFactory.getLogger(PlayerTeamDistributionService.class);

	public static void doReward(TemporaryPlayerTeam<?> team, float damagePercent, Npc owner, AionObject winner) {
		if (team == null || owner == null) {
			return;
		}
		PlayerTeamRewardStats filteredStats = new PlayerTeamRewardStats(owner);
		team.applyOnMembers(filteredStats);
		if (filteredStats.players.isEmpty() || !filteredStats.hasLivingPlayer) {
			return;
		}
		long expReward;
		if (filteredStats.players.size() + filteredStats.mentorCount == 1) {
			expReward = (long) (StatFunctions.calculateSoloExperienceReward(filteredStats.players.get(0), owner));
		} else {
			expReward = (long) (StatFunctions.calculateGroupExperienceReward(filteredStats.highestLevel, owner));
		}
		//Party Bonus:
		//2 Members 10%
		int size = filteredStats.players.size();
		int bonus = 100;
		if (size > 1) {
			bonus = 150 + (size - 2) * 10;
		} for (Player member: filteredStats.players) {
			if (member.isMentor() || member.getLifeStats().isAlreadyDead()) {
				continue;
			}
			//Energy Of Repose.
			if (owner.getLevel() >= 10) {
				member.getCommonData().addReposteEnergy(-250000); //-1%
				PacketSendUtility.sendPacket(member, new SM_STATS_INFO(member));
			}
			//Achievement 7.x
			AchievementService.getInstance().onUpdateAchievementAction(member, owner.getNpcId(), 1, AchievementActionType.HUNT);
			//Fame Exp 7.x
			PlayerFameService.getInstance().addFameExp(member, (10 * owner.getLevel()) / 2);
			//Minion Energy.
			if (member.getMinion() != null) {
				MinionService.getInstance().onUpdateEnergy(member, 50);
			}
			long rewardXp = (long) (expReward * bonus * member.getLevel()) / (filteredStats.partyLvlSum * 100);
			int rewardDp = StatFunctions.calculateGroupDPReward(member, owner);
			float rewardAp = 1;
			//Players 10 levels below highest member get 0 reward.
			if (filteredStats.highestLevel - member.getLevel() >= 10) {
				rewardXp = 0;
				rewardDp = 0;
			} else if (filteredStats.mentorCount > 0) {
				int cape = XPCape.values()[(int) member.getLevel()].value();
				if (cape < rewardXp) {
					rewardXp = cape;
				}
			} switch (member.getWorldId()) {
				case 210050000: //Inggison.
				case 220070000: //Gelkmaros.
				case 600010000: //Silentera Canyon.
					AbyssPointsService.addAp(member, owner, Rnd.get(50, 90));
				break;
				case 600040000: //Tiamaranta's Eye.
				case 800030000: //Crimson Katalam.
				case 800040000: //Crimson Danaria.
				case 800050000: //Lakrum.
				case 800060000: //Demaha.
				case 800070000: //Underpass B1.
					AbyssPointsService.addAGp(member, Rnd.get(50, 90), Rnd.get(1, 3));
				break;
			}
			member.getCommonData().addExp(rewardXp, RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());
			member.getCommonData().addDp(rewardDp);
			if (owner.isRewardAP() && !(filteredStats.mentorCount > 0 && CustomConfig.MENTOR_GROUP_AP)) {
				rewardAp *= StatFunctions.calculatePvEApGained(member, owner);
				int ap = (int) rewardAp / filteredStats.players.size();
				if (ap >= 1) {
					AbyssPointsService.addAp(member, owner, ap);
					PacketSendUtility.sendPacket(member, new SM_STATS_INFO(member));
				}
			}
		}
		Player mostDamagePlayer = owner.getAggroList().getMostPlayerDamageOfMembers(team.getMembers(), filteredStats.highestLevel);
		if (mostDamagePlayer == null) {
			return;
		} if (winner.equals(team) && (!owner.getAi2().getName().equals("chest") || filteredStats.mentorCount == 0)) {
			DropRegistrationService.getInstance().registerDrop(owner, mostDamagePlayer, filteredStats.highestLevel, filteredStats.players);
		}
	}
	
	private static class PlayerTeamRewardStats implements Predicate<Player> {
		final List<Player> players = new ArrayList<Player>();
		int partyLvlSum = 0;
		int highestLevel = 0;
		int mentorCount = 0;
		boolean hasLivingPlayer = false;
		Npc owner;
		public PlayerTeamRewardStats(Npc owner) {
			this.owner = owner;
		}
		@Override
		public boolean apply(Player member) {
			if (member.isOnline()) {
				if (MathUtil.isIn3dRange(member, owner, GroupConfig.GROUP_MAX_DISTANCE)) {
					QuestEngine.getInstance().onKill(new QuestEnv(owner, member, 0, 0));
					if (member.isMentor()) {
						mentorCount++;
						return true;
					} if (!hasLivingPlayer && !member.getLifeStats().isAlreadyDead()) {
						hasLivingPlayer = true;
					}
					players.add(member);
					partyLvlSum += member.getLevel();
					if (member.getLevel() > highestLevel) {
						highestLevel = member.getLevel();
					}
				}
			}
			return true;
		}
	}
}