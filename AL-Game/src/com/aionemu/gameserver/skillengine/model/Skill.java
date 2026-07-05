/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.database.dao.DAOManager;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.manager.SkillAttackManager;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.StartMovingListener;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.*;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.skinskill.SkillSkin;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.MinionService;
import com.aionemu.gameserver.services.MotionLoggingService;
import com.aionemu.gameserver.services.abyss.AbyssService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.action.Action;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.properties.FirstTargetAttribute;
import com.aionemu.gameserver.skillengine.properties.Properties;
import com.aionemu.gameserver.skillengine.properties.TargetRangeAttribute;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.geo.GeoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

public class Skill
{
	private SkillMethod skillMethod = SkillMethod.CAST;
	private List<Creature> effectedList;
	private Creature firstTarget;
	private Creature effector;
	private int skillLevel;
	private int skillStackLvl;
	private StartMovingListener conditionChangeListener;
	private SkillTemplate skillTemplate;
	private boolean firstTargetRangeCheck = true;
	private ItemTemplate itemTemplate;
	private int itemObjectId = 0;
	private int targetType;
	private boolean chainSuccess;
	private boolean blockedPenaltySkill = false;
	private float x;
	private float y;
	private float z;
	private byte h;
	private int boostSkillCost;
	private FirstTargetAttribute firstTargetAttribute;
	private TargetRangeAttribute targetRangeAttribute;
	private ChargeSkillTemplate chargeTemplate = null;
	private Future<?> castingTask = null;
	private long castStart = 0;
	private int duration;
	private int hitTime;
	private int serverTime;
	private String chainCategory = null;
	private volatile boolean isMultiCast = false;
	
	public enum SkillMethod {
		CAST,
		ITEM,
		PASSIVE,
		PROVOKED;
	}
	
	private int skillskinId = 0;
	private int skillskinHitTIme = 0;
	
	private Logger log = LoggerFactory.getLogger(Skill.class);
	
	public Skill(SkillTemplate skillTemplate, Player effector, Creature firstTarget) {
		this(skillTemplate, effector, effector.getSkillList().getSkillLevel(skillTemplate.getSkillId()), firstTarget, null);
	}

	public Skill(SkillTemplate skillTemplate, Player effector, Creature firstTarget, int skillLevel) {
		this(skillTemplate, effector, skillLevel, firstTarget, null);
	}

	public ChargeSkillTemplate getChargeTemplate(){
		return chargeTemplate;
	}
	/**
	 * @param skillTemplate
	 * @param effector
	 * @param skillLvl
	 * @param firstTarget
	 */
	public Skill(SkillTemplate skillTemplate, Creature effector, int skillLvl, Creature firstTarget, ItemTemplate itemTemplate) {
		this.effectedList = new ArrayList<Creature>();
		this.conditionChangeListener = new StartMovingListener();
		this.firstTarget = firstTarget;
		this.skillLevel = skillLvl;
		this.skillStackLvl = skillTemplate.getLvl();
		this.skillTemplate = skillTemplate;
		this.effector = effector;
		this.duration = skillTemplate.getDuration();
		this.itemTemplate = itemTemplate;
		if (skillTemplate.getChargeSetName() != null)
			this.chargeTemplate = DataManager.CHARGE_SKILL_DATA.getChargeSkillTemplateBySetName(skillTemplate.getChargeSetName());

		if (itemTemplate != null)
			skillMethod = SkillMethod.ITEM;
		else if (skillTemplate.isPassive())
			skillMethod = SkillMethod.PASSIVE;
		else if (skillTemplate.isProvoked())
			skillMethod = SkillMethod.PROVOKED;
	}

	/**
	 * Check if the skill can be used
	 * 
	 * @return True if the skill can be used
	 */
	public boolean canUseSkill() {
		Properties properties = skillTemplate.getProperties();
		if (properties != null && !properties.validate(this)) {
			log.debug("properties failed");
			return false;
		}
		//Minion Energy.
		if (effector instanceof Player) {
			Player player = (Player) effector;
			Minion minion = player.getMinion();
			if (this.skillTemplate.isMinion()) {
				player.getCommonData().setMinionEnergy(0);
				schedule(minion, player);
			}
		}
		//Battery 6.x
		if (effector instanceof Player) {
			Player player = (Player) effector;
			Equipment equipment = player.getEquipment();
			int useBattery = skillTemplate.getUseBattery();
			Item shard = equipment.getMainHandPowerShard();
			if (player.isInState(CreatureState.POWERSHARD)) {
				if (shard != null) {
					equipment.usePowerShard(shard, useBattery);
				}
			}
		}
		//Restriction "Archdaeva + Abyss Transformation"
		if (effector instanceof Player) {
			Player player = (Player) effector;
			if (this.skillTemplate.isBattlefield() && player.isInInstance()) {
				//You cannot use the skill here.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_SKILL_CANT_CAST_IN_CURRENT_POSTION, 0);
				return false;
			} else if (this.skillTemplate.isArchDaeva() && player.isTransformed()) {
				//You cannot use this skill while transformed.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_CAST_IN_SHAPECHANGE, 0);
				//Transformation Mode.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_ACT_STATE_POLYMORPH, 3000);
				return false;
			} else if (this.skillTemplate.isDeityAvatar() && player.isTransformed()) {
				//You cannot use this skill while transformed.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_CAST_IN_SHAPECHANGE, 0);
				//Transformation Mode.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_ACT_STATE_POLYMORPH, 3000);
				return false;
			}
		} if (!preCastCheck()) {
			return false;
		} if (effector instanceof Player) {
			Player player = (Player) effector;
			if (this.skillTemplate.getCounterSkill() != null) {
				long time = player.getLastCounterSkill(skillTemplate.getCounterSkill());
				if ((time + 5000) < System.currentTimeMillis()) {
					return false;
				}
			} if (skillMethod == SkillMethod.ITEM && duration > 0 && player.getMoveController().isInMove()) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(getItemTemplate().getNameId())));
				return false;
			}
		} if (!validateEffectedList()) {
			return false;
		}
		return true;
	}

	private void schedule(final Minion minion, final Player player) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (player.getMinion() != null) {
					MinionService.getInstance().despawnMinion(player, player.getMinionList().getLastUsed());
				}
			}
		}, 1000);
	}

	private boolean validateEffectedList() {
		Iterator<Creature> effectedIter = effectedList.iterator();
		while (effectedIter.hasNext()) {
			Creature effected = effectedIter.next();
			if (effected == null)
				effected = effector;

			if (effector instanceof Player) {
				if (!RestrictionsManager.canAffectBySkill((Player) effector, effected, this))
					effectedIter.remove();
			}
			else {
				if (effector.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE))
					effectedIter.remove();
			}
		}

		// TODO: Enable non-targeted, non-point AOE skills to trigger.
		if (targetType == 0 && effectedList.size() == 0 && firstTargetAttribute != FirstTargetAttribute.ME
				&& targetRangeAttribute != TargetRangeAttribute.AREA) {
			log.debug("targettype failed");
			return false;
		}

		return true;	
	}

	/**
	 * Skill entry point
	 * 
	 * @return true if usage is successfull
	 */
	public boolean useSkill() {
		return useSkill(true, true);
	}

	public boolean useNoAnimationSkill() {
		return useSkill(false, true);
	}

	public boolean useWithoutPropSkill() {
		return useSkill(false, false);
	}

	private boolean useSkill(boolean checkAnimation, boolean checkproperties) {
		if (checkproperties && !canUseSkill())
			return false;

		calculateSkillDuration();

		if (SecurityConfig.MOTION_TIME) {
			// must be after calculateskillduration
			if (checkAnimation && !checkAnimationTime()) {
				log.debug("check animation time failed");
				return false;
			}
		}

		boostSkillCost = 0;

		// notify skill use observers
		if (skillMethod == SkillMethod.CAST)
			effector.getObserveController().notifySkilluseObservers(this);

		// start casting
		effector.setCasting(this);

		// log skill time if effector instance of player
		// TODO config
		if (effector instanceof Player)
			MotionLoggingService.getInstance().logTime((Player) effector, this.getSkillTemplate(), this.getHitTime(), MathUtil.getDistance(effector, firstTarget));
		
		getSkillSkinData();
		
		// send packets to start casting
		if (skillMethod == SkillMethod.CAST || skillMethod == SkillMethod.ITEM) {
			startCast();
			if (effector instanceof Npc)
				((NpcAI2) ((Npc) effector).getAi2()).setSubStateIfNot(AISubState.CAST);
		}

		effector.getObserveController().attach(conditionChangeListener);

		if (chargeTemplate != null) {
			this.duration = 0;
			for (ChargeTemplate charge : chargeTemplate.getCharges())
				this.duration += charge.getTime();
		}
		if (this.duration > 0) {
			schedule(this.duration);
		} else {
			endCast();
		}
		return true;
	}

	private void setCooldowns() {
		int cooldown = effector.getSkillCooldown(skillTemplate);
		if (cooldown != 0) {
			if (skillTemplate.getCooldownDeltaLv() != 0)
				cooldown = skillTemplate.getCooldownForLevel(this.skillLevel);
			cooldown = StigmaEnchantCoolDown(this, cooldown);
			long now = System.currentTimeMillis();
			effector.setSkillCoolDown(skillTemplate.getDelayId(), cooldown * 100L + this.duration + now);
			effector.setSkillCoolDownBase(skillTemplate.getDelayId(), now);
		}
	}
	
   /**
	* Stigma Enchant CoolDown 7.x
	*/
	public int StigmaEnchantCoolDown(Skill skill, int cooldown) {
		if (skill == null) {
			return cooldown;
		}
		if (skill.getEffector() instanceof Player) {
			return getStigmaEnchantCoolDown((Player) skill.getEffector(), skill.getSkillId(), cooldown);
		}
		return cooldown;
	}

	/**
	 * Player-aware stigma enchant cooldown calculator.
	 * MameAion75 v86:
	 * - Use the equipped stigma stone enchant level, not the learned skill level.
	 * - This prevents unenhanced characters and stigma-set bonus skill levels from
	 *   receiving excessive cooldown reduction.
	 * - Values are server cooldown ticks (100ms units), same as skill_template cooldown.
	 */
	public static int getStigmaEnchantCoolDown(Player player, int skillId, int cooldown) {
		if (player == null || cooldown <= 0) {
			return cooldown;
		}
		int enchantLevel = getEquippedStigmaEnchantLevel(player, skillId);
		if (enchantLevel <= 0) {
			return cooldown;
		}
		return getStigmaEnchantCoolDown(skillId, enchantLevel, cooldown);
	}

	private static int getEquippedStigmaEnchantLevel(Player player, int skillId) {
		HashSet<String> skillGroups = new HashSet<String>();
		SkillLearnTemplate[] learnTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		if (learnTemplates != null) {
			for (SkillLearnTemplate learnTemplate : learnTemplates) {
				if (learnTemplate != null) {
					String group = normalizeStigmaSkillGroup(learnTemplate.getSkillGroup());
					if (group != null) {
						skillGroups.add(group);
					}
				}
			}
		}
		if (skillGroups.isEmpty()) {
			SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			if (template != null) {
				String group = normalizeStigmaSkillGroup(template.getGroup());
				if (group != null) {
					skillGroups.add(group);
				}
			}
		}
		if (skillGroups.isEmpty() || player.getEquipment() == null) {
			return 0;
		}
		int enchantLevel = 0;
		for (Item item : player.getEquipment().getEquippedItemsAllStigma()) {
			if (item == null || item.getItemTemplate() == null || !item.getItemTemplate().isStigma()) {
				continue;
			}
			String group = normalizeStigmaSkillGroup(item.getSkillGroup());
			if (group != null && skillGroups.contains(group)) {
				enchantLevel = Math.max(enchantLevel, item.getEnchantLevel());
			}
		}
		return enchantLevel;
	}

	private static String normalizeStigmaSkillGroup(String group) {
		if (group == null) {
			return null;
		}
		String normalized = group.trim().toUpperCase(Locale.ENGLISH);
		if (normalized.length() == 0 || "NONE".equals(normalized)) {
			return null;
		}
		return normalized;
	}

	/**
	 * Shared stigma-enchant cooldown calculator.
	 * The second parameter is the real equipped stigma enchant level in v86.
	 */
	public static int getStigmaEnchantCoolDown(int skillId, int SkillLevel, int cooldown) {
		// v86: Tendon/Ankle Slash +14 must be 30.0s - 4.2s = 25.8s, so 3 ticks per enchant.
		if (skillId >= 612 && skillId <= 617) {
			return Math.max(0, cooldown - 3 * SkillLevel);
		}
		switch (skillId) {
			//Lockdown.
			case 500:
			case 501:
			case 502:
			case 503:
			case 504:
			case 505:
			case 506:
			case 507:
			//Crippling Cut.
			case 575:
			case 576:
			case 577:
			case 578:
			case 579:
			case 580:
			case 581:
			case 582:
			//Spite Strike.
			case 584:
			case 585:
			case 586:
			case 587:
			case 588:
			case 589:
			//Siegebreaker.
			case 649:
			case 650:
			case 651:
			case 652:
			case 653:
			case 654:
			case 655:
			case 656:
			//Sharp Strike.
			case 676:
			case 677:
			case 678:
			case 679:
			case 680:
			case 681:
			//Explosive Arrow.
			case 1112:
			case 1113:
			case 1114:
			case 1115:
			case 1116:
			case 1117:
			//Arrow Deluge.
			case 1127:
			case 1128:
			case 1129:
			case 1130:
			case 1131:
			case 1132:
			case 1133:
			case 1134:
			//Summon Rock.
			case 1351:
			case 1352:
			case 1353:
			case 1354:
			case 1355:
			case 1356:
			//Aetherblaze.
			case 1540:
			case 1541:
			case 1542:
			//Splash Swing.
			case 1790:
			case 1791:
			case 1792:
			case 1793:
			case 1794:
			case 1795:
			case 1796:
			case 1797:
			//Healing Burst.
			case 1852:
			case 1853:
			case 1854:
			case 1855:
			case 1856:
			case 1857:
			//Incite Rage.
			case 2945:
			case 2946:
			case 2947:
			case 2948:
			case 2949:
			case 2950:
			case 2951:
			case 2952:
			//Punishment.
			case 3174:
			case 3175:
			case 3176:
			case 3177:
			case 3178:
			case 3179:
			case 3180:
			case 3181:
			//Agony Rune.
			case 3249:
			case 3250:
			case 3251:
			case 3252:
			case 3253:
			case 3254:
			//Cyclone Of Wrath.
			case 3840:
			case 3841:
			case 3842:
			case 3843:
			case 3844:
			case 3845:
			case 3846:
			case 3847:
			//Sympathetic Heal.
			case 3960:
			case 3961:
			case 3962:
			case 3963:
			case 3964:
			case 3965:
			case 3966:
			case 3967:
			    return Math.max(0, cooldown - 1 * SkillLevel);
			//Heart Shot.
			case 820:
			case 821:
			case 822:
			case 823:
			case 824:
			case 825:
			//Blazing Trap [Elyos].
			case 978:
			case 979:
			case 980:
			case 981:
			case 982:
			case 983:
			case 984:
			case 985:
			//Blazing Trap [Asmodians].
			case 986:
			case 987:
			case 988:
			case 989:
			case 990:
			case 991:
			case 992:
			case 993:
			//Agonizing Arrow.
			case 810:
			case 811:
			case 812:
			//Lethal Arrow.
			case 814:
			case 815:
			case 816:
			case 817:
			case 818:
			case 819:
			//Shellshock.
			case 2195:
			case 2198:
			case 2201:
			//Blazing Bombardment.
			case 2298:
			case 2301:
			case 2304:
			case 2307:
			case 2310:
			case 2313:
			case 2316:
			case 2319:
			//Lightning Slash.
			case 3313:
			case 3314:
			case 3315:
			case 3316:
			case 3317:
			case 3318:
			//Joyous Carol.
			case 4362:
			case 4363:
			case 4364:
			case 4365:
			case 4366:
			case 4367:
			//Wind Cut Down.
			case 4597:
			case 4598:
			case 4599:
			case 4600:
			case 4601:
			case 4602:
			//물감 난사.
			case 5489:
			case 5490:
			case 5491:
			case 5492:
			case 5493:
			case 5494:
			case 5495:
			case 5496:
			    return Math.max(0, cooldown - 2 * SkillLevel);
			//Sure Strike.
			case 691:
			case 692:
			case 693:
			case 694:
			case 695:
			case 696:
			//Whirling Strike.
			case 755:
			case 756:
			case 757:
			//Draining Blow.
			case 761:
			case 762:
			case 763:
			case 764:
			case 765:
			case 766:
			case 767:
			case 768:
			//Lightning Arrow.
			case 869:
			case 870:
			case 871:
			//Dilation Arrow.
			case 873:
			case 874:
			case 875:
			case 876:
			case 877:
			case 878:
			case 879:
			case 880:
			//Flame Spray.
			case 1520:
			case 1521:
			case 1522:
			case 1523:
			case 1524:
			case 1525:
			//Soul Lock.
			case 1760:
			case 1761:
			case 1762:
			case 1763:
			case 1764:
			case 1765:
			case 1766:
			case 1767:
			//Mountain Crash.
			case 1869:
			case 1870:
			case 1871:
			case 1872:
			case 1873:
			case 1874:
			//Resonant Strike.
			case 1901:
			case 1902:
			case 1903:
			//Juggernaut Cannon.
			case 1970:
			case 1971:
			case 1972:
			case 1973:
			case 1974:
			case 1975:
			//Hemorrhage Shot.
			case 1976:
			case 1977:
			case 1978:
			case 1979:
			case 1980:
			case 1981:
			//Pressurized Chamber.
			case 2047:
			case 2048:
			case 2049:
			case 2050:
			case 2051:
			case 2052:
			//Fiery Blast.
			case 2069:
			case 2072:
			case 2075:
			case 2078:
			case 2081:
			case 2084:
			case 2087:
			case 2090:
			//Frozen Blitz.
			case 2098:
			case 2099:
			case 2100:
			case 2101:
			case 2102:
			case 2103:
			case 2104:
			case 2105:
			//Steady Fire.
			case 2116:
			case 2117:
			case 2118:
			case 2119:
			case 2120:
			case 2121:
			case 2122:
			case 2123:
			//Power Grab.
			case 2360:
			case 2361:
			case 2362:
			case 2363:
			case 2364:
			case 2365:
			case 2366:
			case 2367:
			//Drillbore.
			case 2391:
			case 2392:
			case 2393:
			case 2394:
			case 2395:
			case 2396:
			case 2397:
			case 2398:
			//Meteor Strike.
			case 2437:
			case 2438:
			case 2439:
			//Ravager Cannon.
			case 2582:
			case 2585:
			case 2588:
			case 2591:
			case 2594:
			case 2597:
			case 2600:
			case 2603:
			//Particle Whip.
			case 2642:
			case 2645:
			case 2648:
			case 2651:
			case 2654:
			case 2657:
			case 2660:
			case 2663:
			//Steel Storm.
			case 2666:
			case 2669:
			case 2672:
			case 2675:
			case 2678:
			case 2681:
			case 2684:
			case 2687:
			//Inquisitor's Blow.
			case 2953:
			case 2954:
			case 2955:
			case 2956:
			case 2957:
			case 2958:
			case 2959:
			case 2960:
			//Magic Smash.
			case 3049:
			case 3050:
			case 3051:
			case 3052:
			case 3053:
			case 3054:
			//Searching Strike.
			case 3445:
			case 3446:
			case 3447:
			case 3448:
			case 3449:
			case 3450:
			case 3451:
			case 3452:
			case 3453:
			case 3454:
			//Spirit Ruinous Offensive.
			case 3548:
			//Magic Implosion.
			case 3550:
			case 3551:
			case 3552:
			case 3553:
			case 3554:
			case 3555:
			//Infernal Pain.
			case 3556:
			case 3557:
			case 3558:
			case 3559:
			case 3560:
			case 3561:
			//Shackle Of Vulnerability.
			case 3574:
			//Stone Scour.
			case 3751:
			case 3754:
			case 3757:
			case 3760:
			case 3763:
			case 3766:
			case 3769:
			case 3772:
			//Summon Cyclone Servant [Elyos]
			case 3797:
			case 3799:
			case 3801:
			case 3803:
			case 3805:
			case 3807:
			//Summon Cyclone Servant [Asmodians]
			case 3798:
			case 3800:
			case 3802:
			case 3804:
			case 3806:
			case 3808:
			//Summon Noble Energy [Elyos]
			case 4152:
			case 4154:
			case 4156:
			case 4158:
			case 4160:
			case 4162:
			//Summon Cyclone Servant [Asmodians]
			case 4153:
			case 4155:
			case 4157:
			case 4159:
			case 4161:
			case 4163:
			//Judge's Edict.
			case 4167:
			case 4168:
			case 4169:
			//Purging Paean.
			case 4483:
			//Inspiration.
			case 4538:
			//Half-Stop.
			case 4541:
			case 4542:
			//Stinging Note.
			case 4543:
			case 4544:
			case 4545:
			case 4546:
			case 4547:
			case 4548:
			case 4549:
			case 4550:
			//Delusional Dirge.
			case 4564:
			case 4565:
			case 4566:
			//Combustible Cacophony.
			case 4572:
			case 4573:
			case 4574:
			case 4575:
			case 4576:
			case 4577:
			case 4578:
			case 4579:
			//Leaping Flash.
			case 4615:
			case 4616:
			case 4617:
			case 4618:
			case 4619:
			case 4620:
			case 4621:
			case 4622:
			case 4623:
			case 4624:
			case 4625:
			case 4626:
			case 4627:
			case 4628:
			case 4629:
			case 4630:
			//생명의 결박.
			case 5377:
			case 5378:
			case 5379:
			case 5380:
			case 5381:
			case 5382:
			case 5383:
			case 5384:
			//자유의 결박.
			case 5427:
			//작품 파괴.
			case 5471:
			case 5472:
			case 5473:
			case 5474:
			case 5475:
			case 5476:
			//물감 주먹.
			case 5477:
			case 5478:
			case 5479:
			//시한 폭탄.
			case 5497:
			case 5498:
			case 5499:
			case 5500:
			case 5501:
			case 5502:
			case 5503:
			case 5504:
			//침묵 물감.
			case 5549:
			case 5550:
			case 5551:
			case 5552:
			case 5553:
			case 5554:
			case 5555:
			case 5556:
			//치유 봉인.
			case 5565:
			//유약 샤워.
			case 5588:
			    return Math.max(0, cooldown - 3 * SkillLevel);
			//Wind Lance.
			case 727:
			case 729:
			case 731:
			//Trap Of Clairvoyance.
			case 1100:
			case 1101:
			//Numbing Blow.
			case 1835:
			case 1836:
			case 1837:
			//Dazzling Fire.
			case 2066:
			case 2067:
			case 2068:
			//Shock & Awe.
			case 2232:
			case 2235:
			case 2238:
			case 2241:
			case 2244:
			case 2247:
			case 2250:
			case 2253:
			//Shieldburst.
			case 2923:
			case 2924:
			case 2925:
			    return Math.max(0, cooldown - 4 * SkillLevel);
			//Dauntless Spirit.
			case 564:
			case 565:
			case 566:
			case 567:
			case 568:
			case 569:
			case 570:
			case 571:
			//Wind Lance.
			case 728:
			case 730:
			case 732:
			//Severe Precision Cut.
			case 733:
			case 734:
			case 735:
			case 736:
			case 737:
			case 738:
			//Draining Sword.
			case 781:
			case 782:
			case 783:
			//Trap Of Slowing [Elyos]
			case 849:
			case 850:
			case 851:
			case 852:
			case 853:
			case 854:
			case 855:
			case 856:
			//Trap Of Slowing [Asmodians]
			case 857:
			case 858:
			case 859:
			case 860:
			case 861:
			case 862:
			case 863:
			case 864:
			//Hunter's Might.
			case 888:
			//Glacial Shard.
			case 1324:
			case 1325:
			case 1326:
			//Annihilation.
			case 1640:
			case 1641:
			case 1642:
			case 1643:
			case 1644:
			case 1645:
			case 1646:
			case 1647:
			//Word Of Life.
			case 1727:
			case 1728:
			case 1729:
			case 1730:
			case 1731:
			case 1732:
			case 1733:
			case 1734:
			//Disorienting Blow.
			case 1863:
			case 1864:
			case 1865:
			case 1866:
			case 1867:
			case 1868:
			//Blast.
			case 1883:
			case 1884:
			case 1885:
			case 1886:
			case 1887:
			case 1888:
			case 1889:
			case 1890:
			//Word Of Instigation.
			case 1907:
			case 1908:
			case 1909:
			//Nature's Favor.
			case 2033:
			case 2034:
			case 2035:
			case 2036:
			case 2037:
			case 2038:
			case 2039:
			case 2040:
			//Stopping Power.
			case 2046:
			//Autoload.
			case 2054:
			//Sighting.
			case 2268:
			case 2269:
			case 2270:
			case 2271:
			case 2272:
			case 2273:
			//Debilitating Blade.
			case 2409:
			case 2410:
			case 2411:
			case 2412:
			case 2413:
			case 2414:
			//Aether Recharge.
			case 2464:
			case 2467:
			case 2470:
			case 2473:
			case 2476:
			case 2479:
			case 2482:
			case 2485:
			//Convulsion Beam.
			case 2711:
			case 2712:
			case 2713:
			case 2714:
			case 2715:
			case 2716:
			//Explosive Exhaust.
			case 2852:
			case 2855:
			case 2858:
			//Invigorating Strike.
			case 2919:
			case 2920:
			case 2921:
			//Holy Shield.
			case 2961:
			case 2962:
			case 2963:
			case 2964:
			case 2965:
			case 2966:
			//Punishing Thrust.
			case 3147:
			case 3148:
			case 3149:
			case 3150:
			case 3151:
			case 3152:
			case 3153:
			case 3154:
			//Explosive Rebranding.
			case 3242:
			case 3243:
			case 3244:
			//Quickening Doom.
			case 3246:
			case 3247:
			case 3248:
			//Venomous Strike.
			case 3255:
			case 3256:
			case 3257:
			case 3258:
			case 3259:
			case 3260:
			case 3261:
			//Eye Of Wrath.
			case 3312:
			//Shadowfall.
			case 3330:
			//Dash & Slash.
			case 3332:
			case 3333:
			case 3334:
			case 3335:
			case 3336:
			case 3337:
			//Infernal Blight.
			case 3545:
			case 3546:
			case 3547:
			//Healing Spirit.
			case 3590:
			//Magic's Freedom.
			case 3731:
			//Emnity Swap.
			case 3739:
			//Armor Spirit.
			case 3796:
			//Saving Grace.
			case 3924:
			case 3925:
			case 3926:
			case 3927:
			case 3928:
			case 3929:
			case 3930:
			case 3931:
			//Summon Healing Servant [Elyos]
			case 3980:
			case 3982:
			case 3984:
			case 3986:
			case 3988:
			case 3990:
			//Summon Healing Servant [Asmodians]
			case 3981:
			case 3983:
			case 3985:
			case 3987:
			case 3989:
			case 3991:
			//Splendor Of Rebirth.
			case 3998:
			case 3999:
			case 4000:
			case 4001:
			case 4002:
			case 4003:
			//Festering Wound.
			case 4134:
			//Call Lightning.
			case 4164:
			case 4165:
			case 4166:
			//Resonant Hymn.
			case 4384:
			case 4385:
			case 4386:
			case 4387:
			case 4388:
			case 4389:
			case 4390:
			case 4391:
			//Blazing Requiem.
			case 4474:
			case 4477:
			case 4480:
			//Chorus Of Blessing.
			case 4484:
			case 4485:
			case 4486:
			//Treble Cleave.
			case 4487:
			case 4488:
			case 4489:
			//Mvt.2: Summer.
			case 4491:
			case 4492:
			case 4493:
			case 4494:
			case 4495:
			case 4496:
			//Mvt.3: Autumn.
			case 4497:
			case 4498:
			case 4499:
			case 4500:
			case 4501:
			case 4502:
			//Paean Of Pain.
			case 4524:
			case 4525:
			case 4526:
			case 4527:
			case 4528:
			case 4529:
			//Hymn Of Rejuvenation.
			case 4530:
			case 4531:
			case 4532:
			case 4533:
			case 4534:
			case 4535:
			case 4536:
			case 4537:
			//Shadowfall.
			case 4591:
			case 4592:
			case 4593:
			case 4594:
			case 4595:
			case 4596:
			//번개의 결박 [Elyos]
			case 5421:
			case 5423:
			case 5425:
			//번개의 결박 [Asmodians]
			case 5422:
			case 5424:
			case 5426:
			//물감 괴물.
			case 5480:
			case 5483:
			case 5486:
			//끈적 물감.
			case 5542:
			case 5543:
			case 5544:
			case 5545:
			case 5546:
			case 5547:
			//작품 만들기.
			case 5566:
			//물감 샤워.
			case 5574:
			    return Math.max(0, cooldown - 6 * SkillLevel);
			//Magical Defense.
			case 600:
			//Tendon Slice.
			case 612:
			case 613:
			case 614:
			case 615:
			case 616:
			case 617:
			//Ankle Snare.
			case 618:
			//Unraveling Assault.
			case 641:
			case 642:
			case 643:
			//Howl.
			case 683:
			case 684:
			case 685:
			case 686:
			case 687:
			case 688:
			case 689:
			case 690:
			//Earthquake Wave.
			case 698:
			case 699:
			case 700:
			case 701:
			case 702:
			case 703:
			case 704:
			case 705:
			//Night Haze.
			case 936:
			case 937:
			case 938:
			//Ripthread Shot.
			case 1006:
			case 1007:
			case 1008:
			//Nature's Resolve.
			case 1009:
			//Arcane Thunderbolt.
			case 1210:
			case 1211:
			case 1212:
			case 1213:
			case 1214:
			case 1215:
			case 4603:
			case 4604:
			//Storm Strike.
			case 1486:
			case 1487:
			case 1488:
			case 1489:
			case 1490:
			case 1491:
			case 1492:
			case 1493:
			//Acceleration Cheer.
			case 1801:
			case 1802:
			case 1803:
			case 1804:
			case 1805:
			case 1806:
			case 1807:
			case 1808:
			//Debilitating Incantation.
			case 1904:
			case 1905:
			case 1906:
			//Aethercharged Steel.
			case 2750:
			case 2751:
			case 2752:
			case 2753:
			case 2754:
			case 2755:
			//Powerspike Trigger.
			case 2861:
			case 2862:
			case 2863:
			//Punishing Wave.
			case 2968:
			case 2969:
			case 2970:
			case 2971:
			case 2972:
			case 2973:
			//Break Away.
			case 3327:
			//Withering Gloom.
			case 3575:
			case 3576:
			case 3577:
			case 3578:
			case 3579:
			case 3580:
			case 3581:
			//Power Sprint.
			case 3903:
			//Summon Vexing Energy [Elyos]
			case 3906:
			case 3908:
			case 3910:
			//Summon Vexing Energy [Asmodians]
			case 3907:
			case 3909:
			case 3911:
			//Enfeebling Burst.
			case 4182:
			case 4183:
			case 4184:
			case 4185:
			case 4186:
			case 4187:
			//중력 물감 [Elyos]
			case 5528:
			case 5529:
			case 5530:
			//중력 물감 [Asmodians]
			case 5531:
			case 5532:
			case 5533:
			//번개 물감.
			case 5536:
			case 5537:
			case 5538:
			case 5539:
			case 5540:
			case 5541:
			//물감 보호막.
			case 5573:
			    return Math.max(0, cooldown - 9 * SkillLevel);
			//Boon Of Quickness.
			case 1350:
			//Repulsion Field.
			case 1418:
			case 1419:
			case 1420:
			//Illusion Storm.
			case 1550:
			case 1551:
			case 1552:
			case 1553:
			case 1554:
			case 1555:
			//Elemental Screen.
			case 1832:
			case 1833:
			case 1834:
			//Pursuit Stance.
			case 2368:
			case 2369:
			case 2370:
			//Shield Of Vengeance.
			case 2918:
			//Cloaking Word.
			case 3544:
			    return Math.max(0, cooldown - 12 * SkillLevel);
			//Kinetic Bulwark.
			case 2579:
			case 2580:
			case 2581:
				return Math.max(0, cooldown - 18 * SkillLevel);
			//Exhausting Wave.
			case 539:
			case 540:
			case 541:
			case 542:
			case 543:
			case 544:
			//Battle Banner [Asmodians]
			case 657:
			case 659:
			case 661:
			//Battle Banner [Elyos]
			case 658:
			case 660:
			case 662:
			//Revival Wave.
			case 749:
			case 750:
			case 751:
			case 752:
			case 753:
			case 754:
			//Skybound Trap [Elyos]
			case 962:
			case 963:
			case 964:
			case 965:
			case 966:
			case 967:
			case 968:
			case 969:
			//Skybound Trap [Asmodians]
			case 970:
			case 971:
			case 972:
			case 973:
			case 974:
			case 975:
			case 976:
			case 977:
			//Bow Of Blessing.
			case 1057:
			//Staggering Trap [Asmodians]
			case 1060:
			case 1062:
			case 1064:
			//Staggering Trap [Elyos]
			case 1061:
			case 1063:
			case 1065:
			//Elemental Ward.
			case 1402:
			//Blessing Of Wind.
			case 1651:
			case 1652:
			case 1653:
			case 1654:
			case 1655:
			case 1656:
			//Paralysis Cannon.
			case 2109:
			case 2110:
			case 2111:
			case 2112:
			case 2113:
			case 2114:
			//Missile Guide.
			case 2274:
			case 2277:
			case 2280:
			case 2283:
			case 2286:
			case 2289:
			case 2292:
			case 2295:
			//Sequential Fire.
			case 2371:
			case 2374:
			case 2377:
			//Pulverizer Cannon.
			case 2380:
			case 2381:
			case 2382:
			//Life Support Trigger.
			case 2450:
			case 2451:
			case 2452:
			case 2453:
			case 2454:
			case 2455:
			case 2456:
			case 2457:
			//Trauma Plate Trigger.
			case 2458:
			case 2459:
			case 2460:
			case 2461:
			case 2462:
			case 2463:
			//Aether Armor.
			case 2934:
			case 2935:
			case 2936:
			case 2937:
			case 2938:
			//Divine Justice.
			case 2939:
			case 2940:
			case 2941:
			case 2942:
			case 2943:
			case 2944:
			//Shield Of Faith.
			case 2974:
			//Divine Fury.
			case 3035:
			//Prayer Of Resilience.
			case 3155:
			case 3156:
			case 3157:
			case 3158:
			case 3159:
			case 3160:
			//Shimmerbomb.
			case 3236:
			case 3237:
			case 3238:
			//Fangdrop Stab.
			case 3239:
			case 3240:
			case 3241:
			//Scoundrel's Bond.
			case 3245:
			//Sensory Boost.
			case 3319:
			case 4614:
			//Deadly Abandon.
			case 3320:
			//Apply Lethal Venom.
			case 3321:
			case 3322:
			case 3323:
			case 3324:
			case 3325:
			case 3326:
			//Spirit Wall Of Protection.
			case 3531:
			//Earthen Call.
			case 3562:
			case 3563:
			case 3564:
			case 3565:
			case 3566:
			case 3567:
			case 3568:
			case 3569:
			//Blood Funnel.
			case 3849:
			case 3850:
			case 3851:
			//Ripple Of Purification.
			case 3992:
			case 3993:
			case 3994:
			case 3995:
			case 3996:
			case 3997:
			//Blinding Light.
			case 4135:
			//Chain Of Suffering.
			case 4144:
			case 4145:
			case 4146:
			case 4147:
			case 4148:
			case 4149:
			//Staggered Rest.
			case 4490:
			//Healing Conduit.
			case 4631:
			case 4632:
			case 4633:
			case 4634:
			case 4635:
			case 4636:
			case 4637:
			case 4638:
			//과녁 집중.
			case 5535:
			//물감 분출.
			case 5559:
			case 5560:
			case 5561:
			case 5562:
			case 5563:
			case 5564:
				return Math.max(0, cooldown - 24 * SkillLevel);
			//Absolute Zero.
			case 1216:
			    return Math.max(0, cooldown - 30 * SkillLevel);
			//Wintry Armor.
			case 1305:
			case 1306:
			case 1307:
			//Ice Sheet [Elyos]
			case 1308:
			case 1309:
			case 1310:
			case 1311:
			case 1312:
			case 1313:
			case 1314:
			case 1315:
			//Ice Sheet [Asmodians]
			case 1316:
			case 1317:
			case 1318:
			case 1319:
			case 1320:
			case 1321:
			case 1322:
			case 1323:
			//Curse Of Weakness.
			case 1329:
			case 1330:
			case 1331:
			case 1332:
			case 1333:
			case 1334:
			case 1335:
			case 1336:
			//Sleeping Storm.
			case 1339:
			//Manifest Tornado [Asmodians]
			case 1460:
			case 1461:
			case 1462:
			case 1463:
			case 1464:
			case 1465:
			case 1466:
			case 1467:
			//Manifest Tornado [Elyos]
			case 1468:
			case 1469:
			case 1470:
			case 1471:
			case 1472:
			case 1473:
			case 1474:
			case 1475:
			//Rise.
			case 1607:
			case 1608:
			case 1609:
			case 1610:
			case 1611:
			case 1612:
			case 1613:
			//Aimbot Assist.
			case 2383:
			case 2384:
			case 2385:
			case 2386:
			case 2387:
			case 2388:
			case 2389:
			case 2390:
			//Leeching Steel.
			case 2825:
			case 2826:
			case 2827:
			case 2828:
			case 2829:
			case 2830:
			case 2831:
			case 2832:
			//Nerve Pulse.
			case 2849:
			case 2850:
			case 2851:
			//Eternal Denial.
			case 2915:
			case 2916:
			case 2917:
			//Prayer Of Victory.
			case 2926:
			case 2927:
			case 2928:
			case 2929:
			case 2930:
			case 2931:
			//Shadow Walk.
			case 3329:
			//Oath Of Accuracy.
			case 3480:
			//Spirit's Empowerment.
			case 3541:
			case 3542:
			case 3543:
			//Command: Absorb Wounds.
			case 3549:
			//Spirit Burn-To-Ashes.
			case 3836:
			//Reverse Condition.
			case 3904:
			//Restoration Relief.
			case 3932:
			case 3933:
			case 3934:
			//Noble Grace.
			case 4188:
			case 4189:
			case 4190:
			case 4191:
			case 4192:
			    return Math.max(0, cooldown - 36 * SkillLevel);
			//Exchange Vitality.
			case 1327:
			//Slumberswept Wind.
			case 1340:
			case 1341:
			case 1342:
			    return Math.max(0, cooldown - 60 * SkillLevel);
			//Empyrean Providence.
			case 2922:
			    return Math.max(0, cooldown - 80 * SkillLevel);
		}
		return cooldown;
	}


	protected void calculateSkillDuration() {
		duration = 0;
		if (isCastTimeFixed()) {
			duration = skillTemplate.getDuration();
			return;
		}
		duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME, skillTemplate.getDuration());
		switch (skillTemplate.getSubType()) {
			case SUMMON:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_SUMMON, duration);
			break;
			case SUMMONHOMING:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_SUMMONHOMING, duration);
			break;
			case SUMMONTRAP:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_TRAP, duration);
			break;
			case HEAL:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_HEAL, duration);
			break;
			case ATTACK:
				if (skillTemplate.getType() == SkillType.MAGICAL) {
					duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_ATTACK, duration);
				}
			break;
		}
		//70% of base skill duration cap
		//No cast speed cap for skill Summon: Spirit & Nimble Fingers.
		if (!effector.getEffectController().hasAbnormalEffect(3779) && !effector.getEffectController().hasAbnormalEffect(913)) {
			int baseDurationCap = Math.round(skillTemplate.getDuration() * 0.3f);
			if (duration < baseDurationCap) {
				duration = baseDurationCap;
			}
		} if (effector instanceof Player) {
			if (this.isMulticast() && ((Player) effector).getChainSkills().getChainCount((Player) effector, this.getSkillTemplate(), this.chainCategory) != 0) {
				duration = 0;
			}
		} if (duration < 0) {
			duration = 0;
		}
	}

	private boolean checkAnimationTime() {
		if (!(effector instanceof Player) || skillMethod != SkillMethod.CAST)// TODO item skills?
			return true;
		Player player = (Player) effector;

		// if player is without weapon, dont check animation time
		if (player.getEquipment().getMainHandWeaponType() == null)
			return true;

		/**
		 * exceptions for certain skills -herb and mana treatment -traps
		 */
		// dont check herb , mana treatment and concentration enhancement
		switch (this.getSkillId()) { //4.8
		    case 245: //Bandage Heal
		    case 246: //Herb Treatment I
		    case 247: //Herb Treatment II
		    case 251: //Herb Treatment III
		    case 253: //Herb Treatment IV
		    case 297: //Herb Treatment V
		    case 308: //Herb Treatment VI
		    case 309: //Herb Treatment VII
		    case 310: //Herb Treatment VIII
		    case 311: //Herb Treatment IX
		    case 312: //Herb Treatment X
		    case 313: //Herb Treatment XI
		    case 314: //Herb Treatment XII
		    case 249: //Mana Treatment I
		    case 250: //Mana Treatment II
		    case 252: //Mana Treatment III
		    case 254: //Mana Treatment IV
		    case 298: //Mana Treatment V
		    case 315: //Mana Treatment VI
		    case 316: //Mana Treatment VII
		    case 317: //Mana Treatment VIII
		    case 318: //Mana Treatment IX
		    case 319: //Mana Treatment X
		    case 320: //Mana Treatment XI
		    case 321: //Mana Treatment XII
		    case 3889: //Prayer Of Focus I
		    case 3890: //Prayer Of Focus II
		    case 3891: //Prayer Of Focus III
		    case 3892: //Prayer Of Focus IV
		    case 3893: //Prayer Of Focus V
		    case 3894: //Prayer Of Focus VI
		    case 4783: //[ArchDaeva] Prayer Of Focus 5.1
		    case 11580: //Stigma Prayer Of Focus I
			return true;
		}
		if (this.getSkillTemplate().getSubType() == SkillSubType.SUMMONTRAP)
			return true;

		Motion motion = this.getSkillTemplate().getMotion();

		if (motion == null || motion.getName() == null) {
			return true;
		}

		if (motion.getInstantSkill() && hitTime != 0) {
			return false;
		}
		else if (!motion.getInstantSkill() && hitTime == 0) {
			return false;
		}

		MotionTime motionTime = DataManager.MOTION_DATA.getMotionTime(motion.getName());

		if (motionTime == null) {
			return true;
		}

		WeaponTypeWrapper weapons = new WeaponTypeWrapper(player.getEquipment().getMainHandWeaponType(), player.getEquipment().getOffHandWeaponType());
		float serverTime = motionTime.getTimeForWeapon(player.getRace(), player.getGender(), weapons);
		int clientTime = hitTime;

		if (serverTime == 0) {
			return true;
		}
		// adjust client time with ammotime
		long ammoTime = 0;
		double distance = MathUtil.getDistance(effector, firstTarget);
		if (getSkillTemplate().getAmmoSpeed() != 0) {
			ammoTime = Math.round(distance / getSkillTemplate().getAmmoSpeed() * 1000);// checked with client
		    clientTime -= ammoTime;
		}
		// adjust servertime with motion play speed
		if (motion.getSpeed() != 100) {
			serverTime /= 100f;
			serverTime *= motion.getSpeed();
		}
		Stat2 attackSpeed = player.getGameStats().getAttackSpeed();
		// MameAion: official-like action lock.  The client plays physical skill
		// animations with the current attack-speed stat, so the server-side next-skill
		// gate must use the same motion time instead of a fixed base motion.  GameStats
		// already contains the capped/current attack-speed value; this guard only rejects
		// corrupt zero/negative values and extreme broken data.
		if (attackSpeed.getBase() > 0 && attackSpeed.getCurrent() > 0 && attackSpeed.getBase() != attackSpeed.getCurrent()) {
			float attackSpeedRatio = (float) attackSpeed.getCurrent() / (float) attackSpeed.getBase();
			if (attackSpeedRatio < 0.10f)
				attackSpeedRatio = 0.10f;
			else if (attackSpeedRatio > 2.00f)
				attackSpeedRatio = 2.00f;
			serverTime *= attackSpeedRatio;
		}

		// tolerance
		if (duration == 0)
			serverTime *= 0.9f;
		else
			serverTime *= 0.5f;

		int finalTime = Math.round(serverTime);
		if (motion.getInstantSkill() && hitTime == 0) {
			this.serverTime = (int) ammoTime;
		}
		else {
			if (clientTime < finalTime) {
				// check for no animation Hacks
				if (SecurityConfig.NO_ANIMATION) {
					float clientTme = clientTime;
					float serverTme = serverTime;
					float checkTme = clientTme / serverTme;
					// check if values are too low
					if (clientTime < 0 || checkTme < SecurityConfig.NO_ANIMATION_VALUE) {
						if (SecurityConfig.NO_ANIMATION_KICK) {
							player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
							AuditLogger.info(player, "Modified client_skills:" + this.getSkillId() + " (clientTime<finalTime:" + clientTime + "/" + finalTime + ") Kicking Player: " + player.getName());
						}
						return false;
					}
				}
			}
			this.serverTime = hitTime;
		}
		player.setNextSkillUse(System.currentTimeMillis() + duration + finalTime);
		return true;
	}

	/**
	 * Penalty success skill
	 */
	private void startPenaltySkill() {
		int penaltySkill = skillTemplate.getPenaltySkillId();
		if (penaltySkill == 0) {
			return;
		}
		///////////////////[Penalty Skill]/////////////////
		if (penaltySkill >= 8892 && penaltySkill <= 8904 ||
		    penaltySkill == 9147 || penaltySkill == 9148 ||
			penaltySkill == 9398 || penaltySkill == 9506 || penaltySkill == 9555) {
			if (effector instanceof Player) {
                Player player = (Player) effector;
                if (player.isInGroup2() || player.isInAlliance2()) {
                    Collection<Player> onlinePlayers = player.isInGroup2() ? player.getPlayerGroup2().getOnlineMembers() : player.getPlayerAllianceGroup2().getOnlineMembers();
                    for (Player member: onlinePlayers) {
                        if (MathUtil.isIn3dRange(firstTarget, member, 25)) {
                            SkillEngine.getInstance().applyEffectDirectly(penaltySkill, firstTarget, member, 0);
                        }
                    }
                }
            }
        } else {
			SkillEngine.getInstance().applyEffectDirectly(penaltySkill, firstTarget, effector, 0);
		}
    }

	/**
	 * Provoke Critical success skill
	 */
	private void startProvokeCritical() {
		int provokeCritical = skillTemplate.getProvokeCriticalId();
		if (provokeCritical == 0) {
			return;
		}
		/////////////////////[Provoke Critical]//////////////////
		if (provokeCritical == 9294 || provokeCritical == 9297 ||
		    provokeCritical == 9300 || provokeCritical == 9302 ||
			provokeCritical == 9345 || provokeCritical == 9471 ||
			provokeCritical == 9473 || provokeCritical == 9476 || provokeCritical == 9482) {
			if (effector instanceof Npc && firstTarget instanceof Player) {
				final Creature target = (Creature) effector.getTarget();
				SkillEngine.getInstance().applyEffectDirectly(provokeCritical, firstTarget, target, 0);
			}
        } else {
			SkillEngine.getInstance().applyEffectDirectly(provokeCritical, firstTarget, effector, 0);
		}
	}

	/**
	 * Provoke Skills success skill
	 */
	private void startProvokeSkill() {
		int provokeSkill = skillTemplate.getProvokeSkillId();
		if (provokeSkill == 0) {
			return;
		}
		SkillEngine.getInstance().applyEffectDirectly(provokeSkill, firstTarget, effector, 0);
	}

	private boolean isClientUnsafeTransformVisualSkill() {
		int skillId = skillTemplate != null ? skillTemplate.getSkillId() : 0;
		return (skillId >= 5030 && skillId <= 5080) || skillId == 5375 || skillId == 5376 || (skillId >= 5607 && skillId <= 5657);
	}

	/**
	 * Start casting of skill
	 */
	private void startCast() {
		// MameAion v63: CC2/EU7.7 client can crash when nearby players receive
		// transform-book skill cast visuals (crash logs showed player actor + skillId
		// 5080).  The buff/effect is still applied server-side; only the noisy visual
		// cast packet is suppressed.  The actual model/stat transform is handled by
		// SM_TRANSFORM/TransformEffect.
		if (isClientUnsafeTransformVisualSkill()) {
			return;
		}
        int targetObjId = firstTarget != null ? firstTarget.getObjectId() : 0;
        if (skillMethod == SkillMethod.CAST) {
            switch (targetType) {
                case 0:
                    PacketSendUtility.broadcastPacketAndReceive(effector, new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, targetObjId, this.duration, skillskinId));
                    if (effector instanceof Npc && firstTarget instanceof Player) {
                        NpcAI2 ai = (NpcAI2) effector.getAi2();
                        if (ai.poll(AIQuestion.CAN_SHOUT)) {
                            ShoutEventHandler.onCast(ai, firstTarget);
						}
                    }
                break;
                case 3:
                    PacketSendUtility.broadcastPacketAndReceive(effector, new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, 0, this.duration, skillskinId));
                break;
                case 1:
                    PacketSendUtility.broadcastPacketAndReceive(effector, new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, x, y, z, this.duration, skillskinId));
                break;
            }
        } else if (skillMethod == SkillMethod.ITEM && duration > 0) {
            PacketSendUtility.broadcastPacketAndReceive(effector, new SM_ITEM_USAGE_ANIMATION(effector.getObjectId(), firstTarget.getObjectId(), (this.itemObjectId == 0 ? 0 : this.itemObjectId), itemTemplate.getTemplateId(), this.duration, 0, 0));
        }
    }

	/**
	 * Set this skill as canceled
	 */
	public void cancelCast() {
		if (castingTask != null) {
            castingTask.cancel(true);
            castingTask = null;
        }
	}

	/**
	 * Apply effects and perform actions specified in skill template
	 */
	private void endCast() {
		startProvokeSkill();
		startProvokeCritical();
		if (!effector.isCasting())
			return;

		//charge skill 4.3
		if (chargeTemplate != null) {
			int time = (int) (System.currentTimeMillis() - castStart);
			time += 100; // 100ms leeway

			if (time < chargeTemplate.getMinCharge())
				return;

			int skillId = skillTemplate.getSkillId();
			for (ChargeTemplate charge : chargeTemplate.getCharges()) {
				time -= charge.getTime();
				skillId = charge.getSkillId();

				if (time < 0)
					break;
			}

			skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);

			int cooldown = skillTemplate.getCooldownForLevel(this.skillLevel);
			cooldown = StigmaEnchantCoolDown(this, cooldown);
			long now = System.currentTimeMillis();
			effector.setSkillCoolDown(skillTemplate.getDelayId(), cooldown * 100L + now);
			effector.setSkillCoolDownBase(skillTemplate.getDelayId(), now);
		}

		// if target out of range
		if (skillTemplate == null)
			return;

		// Check if target is out of skill range
		Properties properties = skillTemplate.getProperties();
		if (properties != null && !properties.endCastValidate(this)) {
			effector.getController().cancelCurrentSkill();
			return;
		}

		if (!validateEffectedList()) {
			effector.getController().cancelCurrentSkill();
			return;
		}

		if (!preUsageCheck()) {
			return;
		}

		effector.setCasting(null);

		if (this.getSkillTemplate().isDeityAvatar() && effector instanceof Player) {
			AbyssService.rankerSkillAnnounce((Player) effector, this.getSkillTemplate().getNameId());
		}

		/**
		 * try removing item, if its not possible return to prevent exploits
		 */
		if (effector instanceof Player && skillMethod == SkillMethod.ITEM) {
			Item item = ((Player) effector).getInventory().getItemByObjId(this.itemObjectId);
			if (item == null)
				return;
			if (item.getActivationCount() > 1) {
				item.setActivationCount(item.getActivationCount() - 1);
			}
			else {
				if (!((Player) effector).getInventory().decreaseByObjectId(item.getObjectId(), 1, ItemUpdateType.DEC_ITEM_USE))
					return;
			}
		}
		/**
		 * Create effects and precalculate result
		 */

		int spellStatus = 0;
		int dashStatus = 0;
		int resistCount = 0;
		boolean blockedChain = false;
		boolean blockedStance = false;
		final List<Effect> effects = new ArrayList<Effect>();
		if (skillTemplate.getEffects() != null) {
			boolean blockAOESpread = false;
			for (Creature effected : effectedList) {
				Effect effect = new Effect(this, effected, 0, itemTemplate);
				if (effected instanceof Player) {
					if (effect.getEffectResult() == EffectResult.CONFLICT)
						blockedStance = true;
				}
				// Force RESIST status if AOE spell spread must be blocked
				if (blockAOESpread)
					effect.setAttackStatus(AttackStatus.RESIST);
				effect.initialize();
				final int worldId = effector.getWorldId();
				final int instanceId = effector.getInstanceId();
				effect.setWorldPosition(worldId, instanceId, x, y, z);

				effects.add(effect);
				spellStatus = effect.getSpellStatus().getId();
				dashStatus = effect.getDashStatus().getId();

				// Block AOE propagation if firstTarget resists the spell
				if ((!blockAOESpread) && (effect.getAttackStatus() == AttackStatus.RESIST) && (isTargetAOE()))
					blockAOESpread = true;

				if (effect.getAttackStatus() == AttackStatus.RESIST || effect.getAttackStatus() == AttackStatus.DODGE) {
					resistCount++;
				}
			}

			if (!effectedList.isEmpty()) {
				if (resistCount == effectedList.size()) {
					blockedChain = true;
					blockedPenaltySkill = true;
				}
			}

			// exception for point point skills(example Ice Sheet)
			if (effectedList.isEmpty() && this.isPointPointSkill()) {
				Effect effect = new Effect(this, null, 0, itemTemplate);
				effect.initialize();
				final int worldId = effector.getWorldId();
				final int instanceId = effector.getInstanceId();
				effect.setWorldPosition(worldId, instanceId, x, y, z);
				effects.add(effect);
				spellStatus = effect.getSpellStatus().getId();
			}
		}

		if (effector instanceof Player && skillMethod == SkillMethod.CAST) {
			Player playerEffector = (Player) effector;
			if (playerEffector.getController().isUnderStance()) {
				playerEffector.getController().stopStance();
			}
			if (skillTemplate.isStance() && !blockedStance) {
				playerEffector.getController().startStance(skillTemplate.getSkillId());
			}
		}

		boolean setCooldowns = true;
		if (effector instanceof Player) {
			if (this.isMulticast() && ((Player) effector).getChainSkills().getChainCount((Player) effector, this.getSkillTemplate(), this.chainCategory) != 0) {
				setCooldowns = false;
			}
		}

		// Check Chain Skill Trigger Rate
		if (CustomConfig.SKILL_CHAIN_TRIGGERRATE) {
			final float chainProb = skillTemplate.getChainSkillProb();
			if (this.chainCategory != null && !blockedChain) {
				this.chainSuccess = Rnd.get(90) < chainProb;
			}
		} else {
			this.chainSuccess = true;
		}

		/**
		 * set variables for chaincondition check
		 */
		if (effector instanceof Player && this.chainSuccess && this.chainCategory != null) {
			((Player) effector).getChainSkills().addChainSkill(this.chainCategory, this.isMulticast());
		}

		/**
		 * Perform necessary actions (use mp,dp items etc)
		 */
		Actions skillActions = skillTemplate.getActions();
		if (skillActions != null) {
			for (Action action : skillActions.getActions()) {
				action.act(this);
			}
		}

		if (effector instanceof Player) {
			QuestEnv env = new QuestEnv(effector.getTarget(), (Player) effector, 0, 0);
			QuestEngine.getInstance().onUseSkill(env, skillTemplate.getSkillId());
		}

		if (setCooldowns) {
			this.setCooldowns();
		}

		if (((skillMethod == SkillMethod.CAST) && (getSkillTemplate().getSubType() != SkillSubType.HEAL) && (hitTime <= 0)) || ((chargeTemplate != null) && (getSkillTemplate().getSubType() != SkillSubType.HEAL))) {
			if (skillskinHitTIme > 0) {
				hitTime += (int)(skillskinHitTIme * effector.getDistanceToTarget() * 1.8F);
			} else {
				hitTime = ((int)((int)(getSkillTemplate().getAmmoSpeed() * effector.getDistanceToTarget()) * 1.8F));
			}
		}

		if (hitTime == 0)
			applyEffect(effects);
		else {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					applyEffect(effects);
				}
			}, hitTime);
		}
		if (skillMethod == SkillMethod.CAST || skillMethod == SkillMethod.ITEM)
			sendCastspellEnd(spellStatus, dashStatus, effects);

		if (effector instanceof Npc)
			SkillAttackManager.afterUseSkill((NpcAI2) ((Npc) effector).getAi2());
	}

	public void applyEffect(List<Effect> effects) {
		/**
		 * Apply effects to effected objects
		 */
		for (Effect effect : effects) {
			effect.applyEffect();
		}
		/**
		 * Use penalty skill (now 100% success)
		 */
		if (!blockedPenaltySkill) {
			startPenaltySkill();
		}
	}

	private void getSkillSkinData() {
		if (((effector instanceof Player)) && (((Player)effector).getSkillSkinList() != null)) {
			for (SkillSkin skillSkin: ((Player)effector).getSkillSkinList().getSkillSkins()) {
				if ((skillSkin.getTemplate() != null) && (skillSkin.getTemplate().getGroup().equalsIgnoreCase(skillTemplate.getGroup())) && (skillSkin.getIsActive() == 1)) {
					skillskinHitTIme = skillSkin.getTemplate().getAmmoSpeed();
					skillskinId = skillSkin.getId();
					break;
				}
			}
		}
	}

	/**
	 * @param spellStatus
	 * @param effects
	 */
	private void sendCastspellEnd(int spellStatus, int dashStatus, List<Effect> effects) {
		if (isClientUnsafeTransformVisualSkill()) {
			return;
		}
		if ((this.skillMethod == SkillMethod.CAST)) {
            switch (this.targetType) {
                case 0:
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new SM_CASTSPELL_RESULT(this, effects, this.serverTime, this.chainSuccess, spellStatus, dashStatus, this.skillskinId));
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new SM_ATTACK_STATUS(this.firstTarget, this.effector, SM_ATTACK_STATUS.TYPE.REGULAR, 0, 0, SM_ATTACK_STATUS.LOG.ATTACK));
                break;
                case 3:
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new SM_CASTSPELL_RESULT(this, effects, this.serverTime, this.chainSuccess, spellStatus, dashStatus, this.skillskinId));
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new SM_ATTACK_STATUS(this.firstTarget, this.effector, SM_ATTACK_STATUS.TYPE.REGULAR, 0, 0, SM_ATTACK_STATUS.LOG.ATTACK));
                break;
                case 1:
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new SM_CASTSPELL_RESULT(this, effects, this.serverTime, this.chainSuccess, spellStatus, dashStatus, this.targetType));
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new SM_ATTACK_STATUS(this.firstTarget, this.effector, SM_ATTACK_STATUS.TYPE.REGULAR, 0, 0, SM_ATTACK_STATUS.LOG.ATTACK));
				break;
            }
        } else if (this.skillMethod == SkillMethod.ITEM) {
            PacketSendUtility.broadcastPacketAndReceive(this.effector, new SM_ITEM_USAGE_ANIMATION(this.effector.getObjectId().intValue(), this.firstTarget.getObjectId().intValue(), this.itemObjectId == 0 ? 0 : this.itemObjectId, this.itemTemplate.getTemplateId(), 0, 1, 0));
        }
    }

	/**
	 * Schedule actions/effects of skill (channeled skills)
	 */
	private void schedule(int delay) {
		castingTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
            public void run() {
                endCast();
            }
        }, delay);

        castStart = System.currentTimeMillis();
	}

	/**
	 * Check all conditions before starting cast
	 */
	private boolean preCastCheck() {
		Conditions skillConditions = skillTemplate.getStartconditions();
		return skillConditions != null ? skillConditions.validate(this) : true;
	}

	/**
	 * Check all conditions before using skill
	 */
	private boolean preUsageCheck() {
		Conditions skillConditions = skillTemplate.getUseconditions();
		return skillConditions != null ? skillConditions.validate(this) : true;
	}

	/**
	 * @param value
	 *          is the changeMpConsumptionValue to set
	 */
	public void setBoostSkillCost(int value) {
		boostSkillCost = value;
	}

	/**
	 * @return the changeMpConsumptionValue
	 */
	public int getBoostSkillCost() {
		return boostSkillCost;
	}

	/**
	 * @return the effectedList
	 */
	public List<Creature> getEffectedList() {
		return effectedList;
	}

	/**
	 * @return the effector
	 */
	public Creature getEffector() {
		return effector;
	}

	/**
	 * @return the skillLevel
	 */
	public int getSkillLevel() {
		return skillLevel;
	}

	/**
	 * @return the skillId
	 */
	public int getSkillId() {
		return skillTemplate.getSkillId();
	}

	/**
	 * @return the skillStackLvl
	 */
	public int getSkillStackLvl() {
		return skillStackLvl;
	}

	/**
	 * @return the conditionChangeListener
	 */
	public StartMovingListener getConditionChangeListener() {
		return conditionChangeListener;
	}

	/**
	 * @return the skillTemplate
	 */
	public SkillTemplate getSkillTemplate() {
		return skillTemplate;
	}

	/**
	 * @return the firstTarget
	 */
	public Creature getFirstTarget() {
		return firstTarget;
	}

	/**
	 * @param firstTarget
	 *          the firstTarget to set
	 */
	public void setFirstTarget(Creature firstTarget) {
		this.firstTarget = firstTarget;
	}

	/**
	 * @return true or false
	 */
	public boolean isPassive() {
		return skillTemplate.getActivationAttribute() == ActivationAttribute.PASSIVE;
	}

	/**
	 * @return the firstTargetRangeCheck
	 */
	public boolean isFirstTargetRangeCheck() {
		return firstTargetRangeCheck;
	}

	/**
	 * @param FirstTargetAttribute
	 *          the firstTargetAttribute to set
	 */
	public void setFirstTargetAttribute(FirstTargetAttribute firstTargetAttribute) {
		this.firstTargetAttribute = firstTargetAttribute;
	}

	/**
	 * @return true if the present skill is a non-targeted, non-point AOE skill
	 */
	public boolean checkNonTargetAOE() {
		return (firstTargetAttribute == FirstTargetAttribute.ME && targetRangeAttribute == TargetRangeAttribute.AREA);
	}

	/**
	 * @return true if the present skill is a targeted AOE skill
	 */
	public boolean isTargetAOE() {
		return (firstTargetAttribute == FirstTargetAttribute.TARGET && targetRangeAttribute == TargetRangeAttribute.AREA);
	}

	/**
	 * @return true if the present skill is a self buff includes items (such as scroll buffs)
	 */
	public boolean isSelfBuff() {
		return (firstTargetAttribute == FirstTargetAttribute.ME && targetRangeAttribute == TargetRangeAttribute.ONLYONE
				&& skillTemplate.getSubType() == SkillSubType.BUFF && !skillTemplate.isDeityAvatar());
	}

	/**
	 * @return true if the present skill has self as first target
	 */
	public boolean isFirstTargetSelf() {
		return (firstTargetAttribute == FirstTargetAttribute.ME);
	}

	/**
	 * @return true if the present skill is a Point skill
	 */
	public boolean isPointSkill() {
		return (this.firstTargetAttribute == FirstTargetAttribute.POINT);
	}

	/**
	 * @param firstTargetRangeCheck
	 *          the firstTargetRangeCheck to set
	 */
	public void setFirstTargetRangeCheck(boolean firstTargetRangeCheck) {
		this.firstTargetRangeCheck = firstTargetRangeCheck;
	}

	/**
	 * @param itemTemplate
	 *          the itemTemplate to set
	 */
	public void setItemTemplate(ItemTemplate itemTemplate) {
		this.itemTemplate = itemTemplate;
	}

	public ItemTemplate getItemTemplate() {
		return this.itemTemplate;
	}

	public void setItemObjectId(int id) {
		this.itemObjectId = id;
	}

	public int getItemObjectId() {
		return this.itemObjectId;
	}

	/**
	 * @param targetRangeAttribute
	 *          the targetRangeAttribute to set
	 */
	public void setTargetRangeAttribute(TargetRangeAttribute targetRangeAttribute) {
		this.targetRangeAttribute = targetRangeAttribute;
	}

	/**
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTargetType(int targetType, float x, float y, float z) {
		this.targetType = targetType;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Calculated position after skill
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param h
	 */
	public void setTargetPosition(float x, float y, float z, byte h) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = h;
	}

	public void setDuration(int t) {
		this.duration = t;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public final byte getH() {
		return h;
	}

	/**
	 * @return Returns the time.
	 */
	public int getHitTime() {
		return hitTime;
	}

	/**
	 * @param time
	 *          The time to set.
	 */
	public void setHitTime(int time) {
		this.hitTime = time;
	}
	
	private boolean isCastTimeFixed() {
		if (skillMethod != SkillMethod.CAST) {
			return true;
		} switch (this.getSkillId()) {
		    case 17: //Sleep: Scarecrow
		    case 20: //Fear: Ginseng
			case 243: //Return
			case 245: //Bandage Heal
			case 246: //Herb Treatment I
		    case 247: //Herb Treatment II
		    case 251: //Herb Treatment III
		    case 253: //Herb Treatment IV
		    case 297: //Herb Treatment V
		    case 308: //Herb Treatment VI
		    case 309: //Herb Treatment VII
		    case 310: //Herb Treatment VIII
		    case 311: //Herb Treatment IX
		    case 312: //Herb Treatment X
		    case 313: //Herb Treatment XI
		    case 314: //Herb Treatment XII
			case 249: //Mana Treatment I
		    case 250: //Mana Treatment II
		    case 252: //Mana Treatment III
		    case 254: //Mana Treatment IV
		    case 298: //Mana Treatment V
		    case 315: //Mana Treatment VI
		    case 316: //Mana Treatment VII
		    case 317: //Mana Treatment VIII
		    case 318: //Mana Treatment IX
		    case 319: //Mana Treatment X
		    case 320: //Mana Treatment XI
		    case 321: //Mana Treatment XII
			case 302: //Escape
			case 1337: //Sleep
			case 1338: //Tranquilizing Cloud
			case 1339: //Sleeping Storm.
			case 1416: //Curse Of Old Roots
			case 1417: //Curse Of Roots
		    case 3589: //Fear Shriek
			case 3775: //Fear
			//ArchDaeva Transformation 5.1 [Elyos]
			case 4752: //Transformation: Avatar Of Fire.
			case 4757: //Transformation: Avatar Of Water.
			case 4762: //Transformation: Avatar Of Earth.
			case 4768: //Transformation: Avatar Of Wind.
			//ArchDaeva Transformation 5.1 [Asmodians]
			case 4804: //Transformation: Avatar Of Fire.
			case 4805: //Transformation: Avatar Of Water.
			case 4806: //Transformation: Avatar Of Earth.
			case 4807: //Transformation: Avatar Of Wind.
			//Elyos [Guardian General]
		    case 11885: //Transformation: Guardian General I
		    case 11886: //Transformation: Guardian General II
		    case 11887: //Transformation: Guardian General III
		    case 11888: //Transformation: Guardian General IV
		    case 11889: //Transformation: Guardian General V
			//Asmodians [Guardian General]
		    case 11890: //Transformation: Guardian General I
		    case 11891: //Transformation: Guardian General II
		    case 11892: //Transformation: Guardian General III
		    case 11893: //Transformation: Guardian General IV
		    case 11894: //Transformation: Guardian General V
			return true;
		}
		return false;
	}

	public boolean isGroundSkill() {
		return skillTemplate.isGroundSkill();
	}

	public boolean shouldAffectTarget(VisibleObject object) {
		// If creature is at least 2 meters above the terrain, ground skill cannot be applied
		if (GeoDataConfig.GEO_ENABLE) {
			if (isGroundSkill()) {
				if ((object.getZ() - GeoService.getInstance().getZ(object) > 1.0f) || (object.getZ() - GeoService.getInstance().getZ(object) < -2.0f))
					return false;
			}
			return GeoService.getInstance().canSee(getFirstTarget(), object);
		}
		return true;
	}

	public void setChainCategory(String chainCategory) {
		this.chainCategory = chainCategory;
	}

	public String getChainCategory() {
    	return this.chainCategory;
    }

	public SkillMethod getSkillMethod() {
		return this.skillMethod;
	}

	public boolean isPointPointSkill() {
		if (this.getSkillTemplate().getProperties().getFirstTarget() == FirstTargetAttribute.POINT && this.getSkillTemplate().getProperties().getTargetType() == TargetRangeAttribute.POINT)
			return true;

		return false;
	}

	public boolean isMulticast() {
		return this.isMultiCast;
	}

	public void setIsMultiCast(boolean isMultiCast) {
		this.isMultiCast = isMultiCast;
	}

	public void stopCharging() {
        if (chargeTemplate == null)
            return;

        cancelCast();

        endCast();
    }
}