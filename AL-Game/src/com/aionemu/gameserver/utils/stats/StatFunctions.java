package com.aionemu.gameserver.utils.stats;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.FallDamageConfig;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.AttackerCriticalStatus;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.WeaponStats;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.utils.MameClientCompatDebug;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatFunctions
{
	private static final Logger log = LoggerFactory.getLogger(StatFunctions.class);
	private static SkillElement elements = null;
	
	/**
	 * @param player
	 * @param target
	 * @return "XP Solo" reward from target
	 */
	public static long calculateSoloExperienceReward(Player player, Creature target) {
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		int baseXP = ((Npc) target).getObjectTemplate().getStatsTemplate().getMaxXp();
		int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		return (int) Math.floor(baseXP * xpPercentage / 100);
	}
	
	/**
	 * @param player
	 * @param target
	 * @return "XP Group" reward from target
	 */
	public static long calculateGroupExperienceReward(int maxLevelInRange, Creature target) {
		int targetLevel = target.getLevel();
		int baseXP = ((Npc) target).getObjectTemplate().getStatsTemplate().getMaxXp();
		int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - maxLevelInRange);
		return (int) Math.floor(baseXP * xpPercentage / 100);
	}
	
	/**
	 * @param player
	 * @param target
	 * @return DP reward from target
	 */

	public static int calculateSoloDPReward(Player player, Creature target) {
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		NpcRating npcRating = ((Npc) target).getObjectTemplate().getRating();
		int baseDP = targetLevel * calculateRatingMultipler(npcRating);
		int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		return (int) Math.floor(baseDP * xpPercentage / 100);
	}
	
	/**
	 * @param player
	 * @param target
	 * @return AP reward
	 */
	public static int calculatePvEApGained(Player player, Creature target) {
		float apPercentage = target instanceof SiegeNpc ? 100f : APRewardEnum.apReward(player.getAbyssRank().getRank().getId());
		boolean lvlDiff = player.getCommonData().getLevel() - target.getLevel() > 10;
		float apNpcRate = ApNpcRating(((Npc) target).getObjectTemplate().getRating());
		return (int) (lvlDiff ? 1 : RewardType.AP_NPC.calcReward(player, (int) Math.floor(15 * apPercentage * apNpcRate / 100)));
	}
	
	/**
	 * @param defeated
	 * @param winner
	 * @return Points Lost in PvP Death
	 */
	public static int calculatePvPApLost(Player defeated, Player winner) {
		int pointsLost = Math.round(defeated.getAbyssRank().getRank().getPointsLost() * defeated.getRates().getApPlayerLossRate());
		int difference = winner.getLevel() - defeated.getLevel();
		if (difference > 4) {
			pointsLost = Math.round(pointsLost * 0.1f);
		} else {
			switch (difference) {
				case 3:
					pointsLost = Math.round(pointsLost * 0.85f);
				break;
				case 4:
					pointsLost = Math.round(pointsLost * 0.65f);
				break;
			}
		}
		return pointsLost;
	}
	
	/**
	 * @param defeated
	 * @param winner
	 * @return Points Gained in PvP Kill
	 */
	public static int calculatePvpApGained(Player defeated, int maxRank, int maxLevel) {
		int pointsGained = defeated.getAbyssRank().getRank().getPointsGained();
		int difference = maxLevel - defeated.getLevel();
		if (difference > 4) {
			pointsGained = Math.round(pointsGained * 0.1f);
		} else if (difference < -3) {
			pointsGained = Math.round(pointsGained * 1.3f);
		} else {
			switch (difference) {
				case 3:
					pointsGained = Math.round(pointsGained * 0.85f);
				break;
				case 4:
					pointsGained = Math.round(pointsGained * 0.65f);
				break;
				case -2:
					pointsGained = Math.round(pointsGained * 1.1f);
				break;
				case -3:
					pointsGained = Math.round(pointsGained * 1.2f);
				break;
			}
		}
		int winnerAbyssRank = maxRank;
		int defeatedAbyssRank = defeated.getAbyssRank().getRank().getId();
		int abyssRankDifference = winnerAbyssRank - defeatedAbyssRank;
		if (winnerAbyssRank <= 7 && abyssRankDifference > 0) {
			float penaltyPercent = abyssRankDifference * 0.05f;
			pointsGained -= Math.round(pointsGained * penaltyPercent);
		}
		return pointsGained;
	}
	
	public static int calculatePvpXpGained(Player defeated, int maxRank, int maxLevel) {
		int pointsGained = 5000;
		int difference = maxLevel - defeated.getLevel();
		if (difference > 4) {
			pointsGained = Math.round(pointsGained * 0.1f);
		} else if (difference < -3) {
			pointsGained = Math.round(pointsGained * 1.3f);
		} else {
			switch (difference) {
				case 3:
					pointsGained = Math.round(pointsGained * 0.85f);
				break;
				case 4:
					pointsGained = Math.round(pointsGained * 0.65f);
				break;
				case -2:
					pointsGained = Math.round(pointsGained * 1.1f);
				break;
				case -3:
					pointsGained = Math.round(pointsGained * 1.2f);
				break;
			}
		}
		int winnerAbyssRank = maxRank;
		int defeatedAbyssRank = defeated.getAbyssRank().getRank().getId();
		int abyssRankDifference = winnerAbyssRank - defeatedAbyssRank;
		if (winnerAbyssRank <= 7 && abyssRankDifference > 0) {
			float penaltyPercent = abyssRankDifference * 0.05f;
			pointsGained -= Math.round(pointsGained * penaltyPercent);
		}
		return pointsGained;
	}
	
	public static int calculatePvpDpGained(Player defeated, int maxRank, int maxLevel) {
		int pointsGained = 0;
		int baseDp = 1064;
		int dpPerRank = 57;
		pointsGained = (defeated.getAbyssRank().getRank().getId() - maxRank) * dpPerRank + baseDp;  
		pointsGained = StatFunctions.adjustPvpDpGained(pointsGained, defeated.getLevel(), maxLevel);
		return pointsGained;
	}
	
	public static int adjustPvpDpGained(int points, int defeatedLvl, int killerLvl) {
		int pointsGained = points;
		int difference = killerLvl - defeatedLvl;
		if (difference >= 10) {
			pointsGained = 0;
		} else if (difference < 10 && difference >= 0) {
			pointsGained -= pointsGained * difference * 0.1;
		} else if (difference <= -10) {
			pointsGained *= 1.1;
		} else if (difference > -10 && difference < 0) {
			pointsGained += pointsGained * Math.abs(difference) * 0.01;
		}
		return pointsGained;
	}
	
	public static int calculateGroupDPReward(Player player, Creature target) {
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		NpcRating npcRating = ((Npc) target).getObjectTemplate().getRating();
		int baseDP = targetLevel * calculateRatingMultipler(npcRating);
		int xpPercentage = XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		float rate = player.getRates().getDpNpcRate();
		return (int) Math.floor(baseDP * xpPercentage * rate / 100);
	}
	
	/**
	 * Hate based on BOOST_HATE stat Now used only from skills, probably need to use for regular attack
	 * 
	 * @param creature
	 * @param value
	 * @return
	 */
	public static int calculateHate(Creature creature, int value) {
		Stat2 stat = new AdditionStat(StatEnum.BOOST_HATE, value, creature, 0.1f);
		return (int) (creature.getGameStats().getStat(StatEnum.BOOST_HATE, stat).getCurrent());
	}
	
	/**
	 * @param player
	 * @param target
	 * @param isMainHand
	 * @param	element
	 * @return Damage made to target (-hp value)
	 */
	public static int calculateAttackDamage(Creature attacker, Creature target, boolean isMainHand, SkillElement element) {
        int resultDamage = 0;
        if (element == SkillElement.NONE) {
            resultDamage = calculatePhysicalAttackDamage(attacker, target, isMainHand);
        } else {
            resultDamage = calculateMagicalAttackDamage(attacker, target, element, isMainHand);
        }
		elements = element;
        resultDamage = (int) adjustDamages(attacker, target, resultDamage, 0, true);
		if (target instanceof Npc) {
            return target.getAi2().modifyDamage((int) resultDamage);
        } if (attacker instanceof Npc) {
            return attacker.getAi2().modifyOwnerDamage(resultDamage);
        }
        return resultDamage;
    }
	

	private static boolean useArchSoftDamageFormula() {
		return GSConfig.ARCHSOFT_DAMAGE_FORMULA_ENABLE;
	}

	private static boolean useReFlyDamageFormula() {
		return GSConfig.REFLY_DAMAGE_FORMULA_ENABLE;
	}

	private static final int REFLY_OLD_CONTEXT_ATTACK_MAX = 1000; // PDF: Total old PvE/PvP attack maximum 100%.
	private static final int REFLY_OLD_CONTEXT_NET_MIN = -900; // PDF: Net old PvE/PvP attack minimum -90%.
	private static final int REFLY_ANET_MAX = 20000; // PDF: A_net maximum value 20,000.
	private static final float REFLY_PVP_GLOBAL_REDUCTION = 0.26f; // PDF: Global PvP damage reduction.

	private static int clampReFlyAnet(int value) {
		if (value < 0)
			return 0;
		return Math.min(value, REFLY_ANET_MAX);
	}

	private static int getReFlyOldPvePvpNet(Creature attacker, Creature target) {
		if (attacker == null || target == null)
			return 0;
		int attack = attacker.isPvpTarget(target) ? attacker.getGameStats().getPvpPowerBoost().getCurrent() : attacker.getGameStats().getPvePowerBoost().getCurrent();
		int defence = attacker.isPvpTarget(target) ? target.getGameStats().getPvpPowerBoostResist().getCurrent() : target.getGameStats().getPvePowerBoostResist().getCurrent();
		// ReFly spec: Total PvE/PvP attack is capped at +100%, then defence is subtracted.
		attack = Math.min(Math.max(attack, 0), REFLY_OLD_CONTEXT_ATTACK_MAX);
		return Math.max(REFLY_OLD_CONTEXT_NET_MIN, attack - defence);
	}

	private static int getReFlyPhysicalAnet(Creature attacker, Creature target) {
		int attack = attacker.getGameStats().getPhysicPowerBoost().getCurrent();
		int defence = target.getGameStats().getPhysicPowerBoostResist().getCurrent();
		return clampReFlyAnet(attack + getReFlyOldPvePvpNet(attacker, target) - defence);
	}

	private static int getReFlyMagicalAnet(Creature attacker, Creature target) {
		int attack = attacker.getGameStats().getMagicPowerBoost().getCurrent();
		int defence = target.getGameStats().getMagicPowerBoostResist().getCurrent();
		return clampReFlyAnet(attack + getReFlyOldPvePvpNet(attacker, target) - defence);
	}

	private static int getBasePower(Creature creature) {
		return Math.max(1, creature.getGameStats().getPower().getBase());
	}

	private static int getBaseKnowledge(Creature creature) {
		return Math.max(1, creature.getGameStats().getKnowledge().getBase());
	}

	private static float getReFlyShardMultiplier(Creature creature) {
		return creature instanceof Player && creature.isInState(CreatureState.POWERSHARD) ? 1.25f : 1f;
	}

	private static float getReFlyAutoStatPercent(Stat2 handStat, int weaponMeanDamage) {
		if (handStat == null || weaponMeanDamage <= 0)
			return 0f;
		// Stat system stores weapon mastery / weapon attack % as rate changes on the hand stat base.
		// Convert the observed base delta back to the WM/D_pct term used by the ReFly formula.
		return Math.max(0f, (handStat.getBase() - weaponMeanDamage) * 100f / weaponMeanDamage);
	}

	private static int getReFlyAutoAdditiveDamage(Stat2 handStat) {
		if (handStat == null)
			return 0;
		// Existing stat bucket for old weapon attack additions / manastones / additive weapon attack.
		return Math.max(0, handStat.getBonus());
	}

	private static void logReFlyDamage(String type, Creature attacker, Creature target, float raw, float after, int anet, int pvpDamage) {
		if (!GSConfig.REFLY_DAMAGE_DEBUG_ENABLE || !MameClientCompatDebug.involves(attacker, target))
			return;
		log.info("[MAME-DAMAGE][REFLY_STRICT] type=" + type
			+ " attacker=" + MameClientCompatDebug.describe(attacker)
			+ " target=" + MameClientCompatDebug.describe(target)
			+ " anet=" + anet
			+ " oldContextNet=" + getReFlyOldPvePvpNet(attacker, target)
			+ " pvp=" + (attacker != null && target != null && attacker.isPvpTarget(target))
			+ " pvpDamage=" + pvpDamage
			+ " raw=" + Math.round(raw)
			+ " after=" + Math.round(after));
	}

	/**
	 * @param player
	 * @param target
	 * @param effectTemplate
	 * @param skillDamages
	 * @return Damage made to target (-hp value)
	 */
	public static int calculatePhysicalAttackDamage(Creature attacker, Creature target, boolean isMainHand) {
		Stat2 pAttack = isMainHand ? attacker.getGameStats().getMainHandPAttack() : ((Player) attacker).getGameStats().getOffHandPAttack();

		if (useReFlyDamageFormula()) {
			int anet = getReFlyPhysicalAnet(attacker, target);
			float resultDamage;
			if (attacker instanceof Player) {
				Player player = (Player) attacker;
				Equipment equipment = player.getEquipment();
				Item weapon = isMainHand ? equipment.getMainHandWeapon() : equipment.getOffHandWeapon();
				if (weapon == null || weapon.getItemTemplate().getWeaponStats() == null) {
					int dw = Rnd.get(16, 20);
					resultDamage = dw * (getBasePower(attacker) / 100f + anet / 1000f);
				} else {
					WeaponStats weaponStat = weapon.getItemTemplate().getWeaponStats();
					int min = weaponStat.getMinDamage();
					int max = Math.max(min, weaponStat.getMaxDamage());
					int dw = Rnd.get(min, max);
					int mean = Math.max(1, weaponStat.getMeanDamage());
					float wmAndDpct = getReFlyAutoStatPercent(pAttack, mean);
					int dAdd = getReFlyAutoAdditiveDamage(pAttack);
					resultDamage = dw * (getBasePower(attacker) / 100f + wmAndDpct / 100f + anet / 1000f) + dAdd;
					if (!isMainHand)
						resultDamage *= 0.8f;
				}
			} else {
				// NPC templates in this source expose a single attack stat rather than weapon min/max.
				// Use it as D_w fallback, then apply the same ReFly auto-attack coefficient.
				float dw = Math.max(1, pAttack.getCurrent());
				resultDamage = dw * (getBasePower(attacker) / 100f + anet / 1000f);
			}
			resultDamage *= getReFlyShardMultiplier(attacker);
			if (resultDamage <= 0)
				resultDamage = 1;
			logReFlyDamage("physical-auto", attacker, target, resultDamage, resultDamage, anet, 0);
			return Math.round(resultDamage);
		}

		// Legacy fallback when ReFly strict formula is disabled.
		float resultDamage = pAttack.getCurrent();
		if (resultDamage <= 0)
			resultDamage = 1;
		return Math.round(resultDamage);
	}
	
	public static int calculateMagicalAttackDamage(Creature attacker, Creature target, SkillElement element, boolean isMainHand) {
		Stat2 mAttack = isMainHand ? attacker.getGameStats().getMainHandMAttack() : ((Player) attacker).getGameStats().getOffHandMAttack();

		if (useReFlyDamageFormula()) {
			int anet = getReFlyMagicalAnet(attacker, target);
			float resultDamage;
			if (attacker instanceof Player) {
				Player player = (Player) attacker;
				Equipment equipment = player.getEquipment();
				Item weapon = isMainHand ? equipment.getMainHandWeapon() : equipment.getOffHandWeapon();
				if (weapon == null || weapon.getItemTemplate().getWeaponStats() == null) {
					int dw = Rnd.get(16, 20);
					resultDamage = dw * (getBaseKnowledge(attacker) / 100f + anet / 1000f);
				} else {
					WeaponStats weaponStat = weapon.getItemTemplate().getWeaponStats();
					int min = weaponStat.getMinDamage();
					int max = Math.max(min, weaponStat.getMaxDamage());
					int dw = Rnd.get(min, max);
					int mean = Math.max(1, weaponStat.getMeanDamage());
					float wmAndDpct = getReFlyAutoStatPercent(mAttack, mean);
					int dAdd = getReFlyAutoAdditiveDamage(mAttack);
					resultDamage = dw * (getBaseKnowledge(attacker) / 100f + wmAndDpct / 100f + anet / 1000f) + dAdd;
					if (!isMainHand)
						resultDamage *= 0.8f;
				}
			} else {
				float dw = Math.max(1, mAttack.getCurrent());
				resultDamage = dw * (getBaseKnowledge(attacker) / 100f + anet / 1000f);
			}
			resultDamage *= getReFlyShardMultiplier(attacker);
			if (resultDamage <= 0)
				resultDamage = 1;
			logReFlyDamage("magical-auto", attacker, target, resultDamage, resultDamage, anet, 0);
			return Math.round(resultDamage);
		}

		float resultDamage = mAttack.getCurrent();
		if (resultDamage <= 0)
			resultDamage = 1;
		return Math.round(resultDamage);
	}
	
	public static int calculatePhysicalSkillDamage(Creature attacker, Creature target, int skillDamage, int bonus, int pvpDamage, SkillElement element, boolean noReduce) {
		if (!useReFlyDamageFormula()) {
			int damage = calculatePhysicalAttackDamage(attacker, target, true) + skillDamage + bonus;
			damage = (int) adjustDamages(attacker, target, damage, pvpDamage, true);
			return Math.max(0, damage);
		}
		int anet = getReFlyPhysicalAnet(attacker, target);
		// ReFly spec: physical attack skills use K=100 and no longer take Power in the final formula.
		// The incoming value is the client tooltip damage D_t, so derive D_s = 100 * D_t / base Power.
		float baseSkillDamage = skillDamage * (100f / getBasePower(attacker));
		float skillMultiplier = attacker.getObserveController().getBasePhysicalDamageMultiplier(true);
		float raw = (baseSkillDamage * (1f + anet / 1000f) + bonus) * skillMultiplier;
		raw *= getReFlyShardMultiplier(attacker);
		float adjusted = adjustDamages(attacker, target, raw, pvpDamage, true, element, noReduce);
		logReFlyDamage("physical-skill", attacker, target, raw, adjusted, anet, pvpDamage);
		return Math.round(Math.max(0, adjusted));
	}

    public static int calculateMagicalSkillDamage(Creature speller, Creature target, int baseDamages, int bonus, SkillElement element, boolean useMagicBoost, boolean useKnowledge, boolean noReduce, int pvpDamage) {
		CreatureGameStats<?> sgs = speller.getGameStats();

		if (useReFlyDamageFormula()) {
			int anet = useMagicBoost ? getReFlyMagicalAnet(speller, target) : 0;
			float skillMultiplier = speller.getObserveController().getBaseMagicalDamageMultiplier();
			float k = useKnowledge ? Math.max(1, sgs.getKnowledge().getCurrent()) : 100f;
			// ReFly spec: magical DoT / SM damage-on-dispel can force K=100. For ordinary spells,
			// derive D_s from tooltip D_t using base Knowledge, then multiply by current K.
			float ds = useKnowledge ? baseDamages * (100f / getBaseKnowledge(speller)) : baseDamages;
			float raw = (ds * (k / 100f + anet / 1000f) + bonus) * skillMultiplier;
			raw *= getReFlyShardMultiplier(speller);
			elements = element;
			float damages = adjustDamages(speller, target, raw, pvpDamage, useKnowledge, element, noReduce);
			MameClientCompatDebug.logMagicalSkillFormula(speller, target, baseDamages, bonus, raw, raw, damages, element, useMagicBoost, useKnowledge, noReduce, pvpDamage);
			logReFlyDamage("magical-skill", speller, target, raw, damages, anet, pvpDamage);
			if (damages <= 0)
				damages = 1;
			if (target instanceof Npc)
				return target.getAi2().modifyDamage((int) damages);
			return Math.round(damages);
		}

		float damages = baseDamages + bonus;
		damages = adjustDamages(speller, target, damages, pvpDamage, useKnowledge);
		if (damages <= 0)
			damages = 1;
		if (target instanceof Npc)
			return target.getAi2().modifyDamage((int) damages);
		return Math.round(damages);
	}

	/**
	 * Calculates MAGICAL CRITICAL chance
	 * 
	 * @param attacker
	 * @param attacke
	 * @return boolean
	 */
	public static boolean calculateMagicalCriticalRate(Creature attacker, Creature attacked, float critProbMod2) {
		if (attacker instanceof Servant || attacker instanceof Homing) {
		    return false;
		}
		int critical = attacker.getGameStats().getMCritical().getCurrent();
        critical = attacked.getGameStats().getPositiveReverseStat(StatEnum.MAGICAL_CRITICAL_RESIST, critical);
		critical *= (float) critProbMod2 / 100f;
		double criticalRate;
		if (critical <= 500) {
			criticalRate = critical * 0.1f;
		} else if (critical <= 600) {
			criticalRate = (500 * 0.1f) + ((critical - 500) * 0.05f);
		} else {
			criticalRate = (500 * 0.1f) + (160 * 0.05f) + ((critical - 600) * 0.02f);
		}
		return Rnd.nextInt(100) < criticalRate;
	}
	
	/**
	 * npcRating
	 */
	public static int calculateRatingMultipler(NpcRating npcRating) {
		int multipler;
		switch (npcRating) {
			case NORMAL:
				multipler = 1;
			break;
			case ELITE:
				multipler = 2;
			break;
			default:
				multipler = 1;
		}
		return multipler;
	}
	
	/**
	 * ApNpcRating
	 */
	public static int ApNpcRating(NpcRating npcRating) {
		int multipler;
		switch (npcRating) {
			case NORMAL:
				multipler = 1;
			break;
			case ELITE:
				multipler = 2;
			break;
			default:
				multipler = 1;
		}
		return multipler;
	}
	
	/**
	 * Adjust baseDamages according to their level.
	 **/
	public static float adjustDamages(Creature attacker, Creature target, float damages, int pvpDamage, boolean useMovement) {
		return adjustDamages(attacker, target, damages, pvpDamage, useMovement, elements, false);
	}

	public static float adjustDamages(Creature attacker, Creature target, float damages, int pvpDamage, boolean useMovement, SkillElement element, boolean noReduce) {
		if (attacker.isPvpTarget(target)) {
			if (pvpDamage > 0)
				damages *= pvpDamage * 0.01f;
			if (useReFlyDamageFormula() && !noReduce)
				damages = Math.round(damages * REFLY_PVP_GLOBAL_REDUCTION);
			else if (useArchSoftDamageFormula() && !noReduce)
				damages = Math.round(damages * GSConfig.ARCHSOFT_DAMAGE_PVP_REDUCTION);
			else if (!useArchSoftDamageFormula() && !MameClientCompatDebug.isModernDamageMode()) {
				damages = Math.round(damages * 0.01f);
				float pvpPowerBoost = attacker.getGameStats().getStat(StatEnum.PVP_POWER_BOOST, 0).getCurrent();
				float pvpPowerBoostResist = target.getGameStats().getStat(StatEnum.PVP_POWER_BOOST_RESIST, 0).getCurrent();
				pvpPowerBoost = pvpPowerBoost * 0.01f;
				pvpPowerBoostResist = pvpPowerBoostResist * 0.01f;
				damages = Math.round((damages / 0.5f) + (damages * pvpPowerBoost) - (damages * pvpPowerBoostResist));
			}
		} else if (target instanceof Npc) {
			int levelDiff = target.getLevel() - attacker.getLevel();
			if (useReFlyDamageFormula()) {
				if (levelDiff >= 12)
					damages = 1;
				else if (levelDiff > 2)
					damages *= Math.max(0f, 1f - ((levelDiff - 2) * 0.1f));
			} else {
				damages *= (1f - getNpcLevelDiffMod(levelDiff, 0));
			}
		}
		if (useMovement)
			damages = movementDamageBonus(attacker, damages);
		return damages;
	}
	
	/**
	 * Calculates DODGE chance
	 * 
	 * @param attacker
	 * @param attacked
	 * @return boolean
	 */
	public static boolean calculatePhysicalDodgeRate(Creature attacker, Creature attacked, int accMod) {
        if (attacker.getObserveController().checkAttackerStatus(AttackStatus.DODGE)) {
            return true;
        } if (attacked.getObserveController().checkAttackStatus(AttackStatus.DODGE)) {
            return true;
        }
        float accuracy = attacker.getGameStats().getPAccuracy().getCurrent() + accMod;
        float dodge = attacked.getGameStats().getEvasion().getBonus() + getMovementModifier(attacked, StatEnum.EVASION, attacked.getGameStats().getEvasion().getBase());
        if (useArchSoftDamageFormula()) {
            if (attacked instanceof Npc && ((Npc) attacked).hasEntity())
                return false;
            float dodgeRate = archSoftAvoidRate(dodge, accuracy, 30f);
            return Rnd.nextInt(100) < dodgeRate;
        }
        float dodgeRate = dodge - accuracy;
        if (attacked instanceof Npc) {
            int levelDiff = attacked.getLevel() - attacker.getLevel();
            dodgeRate *= 1 + getNpcLevelDiffMod(levelDiff, 0);
            if (((Npc) attacked).hasEntity()) {
                return false;
            }
        }
        return calculatePhysicalEvasion(dodgeRate, 300);
    }
	
	/**
	 * Calculates PARRY chance
	 * 
	 * @param attacker
	 * @param attacked
	 * @return int
	 */
	public static boolean calculatePhysicalParryRate(Creature attacker, Creature attacked) {
        if (attacked.getObserveController().checkAttackStatus(AttackStatus.PARRY)) {
            return true;
        }
        float accuracy = attacker.getGameStats().getPAccuracy().getCurrent();
        float parry = attacked.getGameStats().getParry().getBonus() + getMovementModifier(attacked, StatEnum.PARRY, attacked.getGameStats().getParry().getBase());
        if (useArchSoftDamageFormula()) {
            float parryRate = archSoftAvoidRate(parry, accuracy, 40f);
            return Rnd.nextInt(100) < parryRate;
        }
        float parryRate = parry - accuracy;
        return calculatePhysicalEvasion(parryRate, 400);
    }
	
	/**
	 * Calculates BLOCK chance
	 * 
	 * @param attacker
	 * @param attacked
	 * @return int
	 */
	public static boolean calculatePhysicalBlockRate(Creature attacker, Creature attacked) {
        if (attacked.getObserveController().checkAttackStatus(AttackStatus.BLOCK)) {
            return true;
        }
        float accuracy = attacker.getGameStats().getPAccuracy().getCurrent();
        float block = attacked.getGameStats().getBlock().getBonus() + getMovementModifier(attacked, StatEnum.BLOCK, attacked.getGameStats().getBlock().getBase());
        if (useArchSoftDamageFormula()) {
            float blockRate = archSoftAvoidRate(block, accuracy, 50f);
            return Rnd.nextInt(100) < blockRate;
        }
        float blockRate = block - accuracy;
        if (blockRate > 500) {
            blockRate = 500;
        }
        return Rnd.nextInt(1000) < blockRate;
    }
	

	private static float archSoftAvoidRate(float defenceStat, float accuracy, float capPercent) {
		float diff = defenceStat - accuracy;
		float denominator = 24000f + diff;
		if (denominator <= 1f)
			denominator = 1f;
		float rate = 220f * diff / denominator;
		if (rate <= 0f)
			return 0f;
		return capPercent > 0f ? Math.min(rate, capPercent) : rate;
	}

	/**
	 * Accuracy (includes evasion/parry/block formulas): Accuracy formula is based on opponents evasion/parry/block vs
	 * your own Accuracy. If your Accuracy is 300 or more above opponents evasion/parry/block then you can not be evaded,
	 * parried or blocked. <br>
	 * https://docs.google.com/spreadsheet/ccc?key=0AqxBGNJV9RrzdF9tOWpwUlVLOXE5bVRWeHQtbGQxaUE&hl=en_US#gid=2
	 */
	public static boolean calculatePhysicalEvasion(float diff, int upperCap) {
		diff = diff * 0.6f + 50;
		if (diff > upperCap) {
			diff = upperCap;
		}
		return Rnd.nextInt(1000) < diff;
	}
	
	/**
	 * Calculates CRITICAL chance
	 * http://www.wolframalpha.com/input/?i=quadratic+fit+%7B%7B300%2C+30.97%7D%2C+%7B320%2C+31.68%7D%2C+%7B340%2C+33.30%7D%2C+%7B360%2C+36.09%7D%2C+%7B380%2C+37.81%7D%2C+%7B400%2C+40.72%7D%2C+%7B420%2C+42.12%7D%2C+%7B440%2C+44.03%7D%2C+%7B480%2C+44.66%7D%2C+%7B500%2C+45.96%7D%2C%7B604%2C+51.84%7D%2C+%7B649%2C+52.69%7D%7D
	 * http://www.aionsource.com/topic/40542-character-stats-xp-dp-origin-gerbatorteam-july-2009/
	 * http://www.wolframalpha.com/input/?i=-0.000126341+x%5E2%2B0.184411+x-13.7738
	 * https://docs.google.com/spreadsheet/ccc?key=0AqxBGNJV9RrzdGNjbEhQNHN3S3M5bUVfUVQxRkVIT3c&hl=en_US#gid=0
	 * @param attacker
	 * @return double
	 */
	public static boolean calculatePhysicalCriticalRate(Creature attacker, Creature attacked, boolean isMainHand, float critProbMod2, boolean isSkill) {
        if (attacker instanceof Servant || attacker instanceof Homing) {
            return false;
        }
        int critical;
        if (attacker instanceof Player && !isMainHand) {
            critical = ((PlayerGameStats) attacker.getGameStats()).getOffHandPCritical().getCurrent();
        } else {
            critical = attacker.getGameStats().getPCritical().getCurrent();
        }
        AttackerCriticalStatus acStatus = attacker.getObserveController().checkAttackerCriticalStatus(AttackStatus.CRITICAL, isSkill);
        if (acStatus.isResult()) {
            if (acStatus.isPercent()) {
                critical *= (1 + acStatus.getValue() / 100);
            } else {
                return Rnd.nextInt(1000) < acStatus.getValue();
            }
        }
        critical = attacked.getGameStats().getPositiveReverseStat(StatEnum.PHYSICAL_CRITICAL_RESIST, critical);
        critical *= (float) critProbMod2 / 100f;
        double criticalRate;
        if (critical <= 500) {
            criticalRate = critical * 0.1f;
        } else if (critical <= 600) {
            criticalRate = (500 * 0.1f) + ((critical - 500) * 0.05f);
        } else {
            criticalRate = (500 * 0.1f) + (160 * 0.05f) + ((critical - 600) * 0.02f);
        }
        return Rnd.nextInt(100) < criticalRate;
    }
	
	/**
	 * Calculates RESIST chance
	 * 
	 * @param attacker
	 * @param attacked
	 * @return int
	 */
	public static int calculateMagicalResistRate(Creature attacker, Creature attacked, int accMod) {
        if (attacked.getObserveController().checkAttackStatus(AttackStatus.RESIST)) {
            return 1000;
        }
        int attackerLevel = attacker.getLevel();
        int targetLevel = attacked.getLevel();
        if (useArchSoftDamageFormula()) {
            float accuracy = attacker.getGameStats().getMAccuracy().getCurrent() + accMod;
            float resist = attacked.getGameStats().getMResist().getCurrent();
            float resistRate = 220f * (resist - accuracy) / (24000f + resist - accuracy);
            if ((targetLevel - attackerLevel) > 2)
                resistRate += (targetLevel - attackerLevel - 2) * 10f;
            if (resistRate <= 0)
                resistRate = 1f; // 0.1% because caller uses 0..1000 threshold
            if (resistRate > 50f)
                resistRate = 50f;
            return Math.round(resistRate * 10f);
        }
        int resistRate = attacked.getGameStats().getMResist().getCurrent() - attacker.getGameStats().getMAccuracy().getCurrent() - accMod;
        if ((targetLevel - attackerLevel) > 2) {
            resistRate += (targetLevel - attackerLevel - 2) * 100;
        } if (resistRate <= 0) {
            resistRate = 1;
        } if (resistRate > 500) {
            resistRate = 500;
        }
        return resistRate;
    }
	
	/**
	 * Calculates the fall damage
	 * 
	 * @param player
	 * @param distance
	 * @return True if the player is forced to his bind location.
	 */
	public static boolean calculateFallDamage(Player player, float distance, boolean stoped) {
		if (player.isInvul()) {
			return false;
		} if (distance >= FallDamageConfig.MAXIMUM_DISTANCE_DAMAGE || !stoped) {
			player.getController().onStopMove();
			player.getFlyController().onStopGliding(false);
			player.getLifeStats().reduceHp(player.getLifeStats().getMaxHp() + 1, player);
			return true;
		} else if (distance >= FallDamageConfig.MINIMUM_DISTANCE_DAMAGE) {
			float dmgPerMeter = player.getLifeStats().getMaxHp() * FallDamageConfig.FALL_DAMAGE_PERCENTAGE / 100f;
			int damage = (int) (distance * dmgPerMeter);
			player.getLifeStats().reduceHp(damage, player);
			player.getObserveController().notifyAttackedObservers(player);
			PacketSendUtility.sendPacket(player, new SM_ATTACK_STATUS(player, player, SM_ATTACK_STATUS.TYPE.FALL_DAMAGE, 0, -damage));
		}
		return false;
	}
	
	public static float getMovementModifier(Creature creature, StatEnum stat, float value) {
        if (!(creature instanceof Player) || stat == null) {
            return value;
		}
        Player player = (Player) creature;
        int h = player.getMoveController().getMovementHeading();
        if (h < 0) {
            return value;
		} switch (h) {
            case 7:
            case 0:
            case 1:
                switch (stat) {
                    case WATER_RESISTANCE:
                    case WIND_RESISTANCE:
                    case FIRE_RESISTANCE:
                    case EARTH_RESISTANCE:
                    case ELEMENTAL_RESISTANCE_DARK:
                    case ELEMENTAL_RESISTANCE_LIGHT:
					case PHYSICAL_POWER_BOOST_RESIST:
					case MAGICAL_POWER_BOOST_RESIST:
                        return value * 0.8f;
				    default:
					break;
                }
            break;
            case 6:
            case 2:
                switch (stat) {
                    case EVASION:
                        return value + 300;
                    case SPEED:
                        return value * 0.8f;
				    default:
					break;
                }
            break;
            case 5:
            case 4:
            case 3:
                switch (stat) {
                    case PARRY:
                    case BLOCK:
                        return value + 500;
                    case SPEED:
                        return value * 0.6f;
				    default:
					break;
                }
            break;
        }
        return value;
    }
	
	private static float movementDamageBonus(Creature creature, float value) {
        if (!(creature instanceof Player)) {
            return value;
		}
        Player player = (Player) creature;
        int h = player.getMoveController().getMovementHeading();
        if (h < 0) {
            return value;
		} switch (h) {
            case 7:
            case 0:
            case 1:
                value = value * 1.1f;
            break;
            case 6:
            case 2:
                value *= 0.8f;
            break;
            case 5:
            case 4:
            case 3:
                value *= 0.8f;
            break;
        }
        return value;
    }
	
	private static float getNpcLevelDiffMod(int levelDiff, int base) {
        switch (levelDiff) {
            case 3:
                return 0.1f;
            case 4:
                return 0.2f;
            case 5:
                return 0.3f;
            case 6:
                return 0.4f;
            case 7:
                return 0.5f;
            case 8:
                return 0.6f;
            case 9:
                return 0.7f;
            default:
                if (levelDiff > 9) {
					return 0.8f;
				}
        }
        return base;
    }
}