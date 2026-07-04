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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team2.group.PlayerFilters.ExcludePlayerFilter;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.panels.SkillPanel;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.robot.RobotInfo;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.*;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssService;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.instance.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.player.PlayerFameService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.*;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.taskmanager.tasks.TeamEffectUpdater;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.MameClientCompatDebug;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldType;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Future;

public class PlayerController extends CreatureController<Player>
{
    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

	private boolean isInShutdownProgress;
	private long lastAttackMilis = 0;
	private long lastAttackedMilis = 0;
	private int stance = 0;

	@Override
	public void see(VisibleObject object) {
		super.see(object);
		if (object instanceof Player) {
			Player player = (Player) object;
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO(player, getOwner().isAggroIconTo(player)));
			PacketSendUtility.sendPacket(getOwner(), new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			if (player.isUseRobot() || player.getRobotId() != 0) {
				player.getEffectController().updatePlayerEffectIcons();
				PacketSendUtility.sendPacket(getOwner(), new SM_USE_ROBOT(player, getRobotInfo(player).getRobotId()));
			} if (player.isTransformed()) {
				TeleportService2.playerTransformation(getOwner());
				TeleportService2.instanceTransformation(getOwner());
				TeleportService2.archdaevaTransformation(getOwner());
				player.getEffectController().updatePlayerEffectIcons();
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, true));
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, player.getTransformedModelId(), true, player.getTransformedItemId(), player.getTransformedSkillId()));
            } if (player.isInPlayerMode(PlayerMode.RIDE)) {
				PacketSendUtility.sendPacket(getOwner(), new SM_EMOTION(player, EmotionType.RIDE, 0, player.ride.getNpcId()));
			} else if (player.getPet() != null) {
				LoggerFactory.getLogger(PlayerController.class).debug("Player " + getOwner().getName() + " sees " + object.getName() + " that has Toypet");
				PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, player.getPet()));
			} else if (player.getMinion() != null) {
				LoggerFactory.getLogger(PlayerController.class).debug("Player " + getOwner().getName() + " sees " + object.getName() + " that has Minion");
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_MINION(6, player.getMinion().getCommonData(), 0));
			}
			player.getEffectController().sendEffectIconsTo(getOwner());
		} else if (object instanceof Kisk) {
			Kisk kisk = ((Kisk) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(kisk, getOwner()));
			if (getOwner().getRace() == kisk.getOwnerRace()) {
				PacketSendUtility.sendPacket(getOwner(), new SM_KISK_UPDATE(kisk));
			}
		} else if (object instanceof Npc) {
			Npc npc = ((Npc) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			PacketSendUtility.sendPacket(getOwner(), new SM_EMOTION_NPC(npc, npc.getState(), EmotionType.SELECT_TARGET));
			PacketSendUtility.sendPacket(getOwner(), new SM_HEADING_UPDATE(object.getObjectId(), (byte) object.getHeading()));
			if (!npc.getEffectController().isEmpty()) {
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
			QuestEngine.getInstance().onAtDistance(new QuestEnv(object, getOwner(), 0, 0));
		} else if (object instanceof Summon) {
			Summon npc = ((Summon) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc));
			if (!npc.getEffectController().isEmpty()) {
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
		} else if (object instanceof Gatherable || object instanceof StaticObject) {
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
		} else if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, (Pet) object));
		}
	}
	
	private RobotInfo getRobotInfo(Player player) {
		ItemTemplate template = player.getEquipment().getMainHandWeapon().getItemSkinTemplate();
		return DataManager.ROBOT_DATA.getRobotInfo(template.getRobotId());
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(4, (Pet) object));
		} else {
			int spawnCode;
			if (getOwner().getRace() == Race.ELYOS) {
				spawnCode = 0x00;
			} else if (getOwner().getRace() == Race.ELYOS) {
				spawnCode = 0x01;
			} else {
				spawnCode = 0xFF;
			}
			PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object, isOutOfRange ? 0 : 1, spawnCode));
		}
	}

	public void updateNearbyQuests() {
        HashMap<Integer, Integer> nearbyQuestList = new HashMap<>();
        for (int questId : getOwner().getPosition().getMapRegion().getParent().getQuestIds()) {
            int diff = 0;
            if (questId <= 0xFFFF) {
                diff = QuestService.getLevelRequirement(questId, getOwner().getCommonData().getLevel());
            } if (diff <= 2 && QuestService.checkStartConditions(new QuestEnv(null, getOwner(), questId, 0), false)) {
                nearbyQuestList.put(questId, diff);
            }
        }
        PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(nearbyQuestList));
    }

	@Override
	public void onEnterZone(ZoneInstance zone) {
		Player player = getOwner();
		if ((!zone.canRide()) && (player.isInPlayerMode(PlayerMode.RIDE))) {
			player.unsetPlayerMode(PlayerMode.RIDE);
		} if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.FORT) && (player.isInState(CreatureState.FLYING))) {
		   /**
		    * If a player enter in zone "Panesterra Fortress"
			* of while player flying, then the system will landing the player.
			*/
			switch (player.getWorldId()) {
			    case 210050000: //Inggison.
			    case 220070000: //Gelkmaros.
				case 400070000: //Abyss Core.
				case 800030000: //Crimson Katalam.
			    case 800040000: //Crimson Danaria.
				case 800060000: //Demaha.
					player.setFlyState(0);
					player.getFlyController().endFly(true);
					player.unsetState(CreatureState.FLYING);
					//You cannot fly in this area.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FLYING_FORBIDDEN_ZONE);
				    PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.LAND, 0, 0), true);
				break;
			}
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		if (player.getPosition().isInstanceMap()) {
			InstanceService.onEnterZone(player, zone);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onEnterZone(player, zone);
		} if (zone.getAreaTemplate().getZoneName() == null) {
			log.error("No name found for a Zone in the map " + zone.getAreaTemplate().getWorldId());
		} else {
			QuestEngine.getInstance().onEnterZone(new QuestEnv(null, player, 0, 0), zone.getAreaTemplate().getZoneName());
		}
	}
	
	@Override
	public void onLeaveZone(ZoneInstance zone) {
		Player player = getOwner();
		if (player.getPosition().isInstanceMap()) {
			InstanceService.onLeaveZone(player, zone);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onLeaveZone(player, zone);
		}
		ZoneName zoneName = zone.getAreaTemplate().getZoneName();
		if (zoneName == null) {
			log.warn("No name for zone template in " + zone.getAreaTemplate().getWorldId());
			return;
		}
		QuestEngine.getInstance().onLeaveZone(new QuestEnv(null, player, 0, 0), zoneName);
	}

	/**
	 * {@inheritDoc} Should only be triggered from one place (life stats)
	 */
	// TODO [AT] move
	public void onEnterWorld() {
		Player player = getOwner();
		InstanceService.onEnterInstance(getOwner());
		TeleportService2.playerTransformation(getOwner());
		TeleportService2.instanceTransformation(getOwner());
		WorldPlayTimeService.getInstance().onEnterWorld(player);
		for (Effect ef: getOwner().getEffectController().getAbnormalEffects()) {
			if (ef.isDeityAvatar()) {
				if (getOwner().getWorldType() != WorldType.ABYSS &&
				    getOwner().getWorldType() != WorldType.BALAUREA && getOwner().isInInstance()) {
					ef.endEffect();
					getOwner().getEffectController().clearEffect(ef);
				}
			} else if (ef.isArchDaeva()) {
				ef.endEffect();
				getOwner().getEffectController().clearEffect(ef);
			}
		}
	}

	// TODO [AT] move
	public void onLeaveWorld() {
		ProtectorConquerorService.getInstance().onLeaveMap(getOwner());
		InstanceService.onLeaveInstance(getOwner());
	}

	public void onDie(Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		player.getController().cancelCurrentSkill();
		player.setRebirthRevive(getOwner().haveSelfRezEffect());
		showPacket = player.hasResurrectBase() ? false : showPacket;
		Creature master = lastAttacker.getMaster();
		if ((PvPConfig.ENABLE_KILLING_SPREE_SYSTEM) && (getOwner().getRawKillCount() > 0)) {
			if ((master instanceof Npc)) {
				PvPSpreeService.cancelSpree(player, (Npc) master, false);
			} if (((master instanceof Player)) && (master.getRace() != player.getRace())) {
				PvPSpreeService.cancelSpree(player, (Player) master, true);
			}
		}
		AbyssRank ar = player.getAbyssRank();
		if (AbyssService.isOnPvpMap(player) && ar != null) {
			if (ar.getRank().getId() >= 1) {
				AbyssService.rankedKillAnnounce(player);
			}
		} if (DuelService.getInstance().isDueling(player.getObjectId())) {
			if (master != null && DuelService.getInstance().isDueling(player.getObjectId(), master.getObjectId())) {
				DuelService.getInstance().loseDuel(player);
				player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
				player.getLifeStats().setCurrentHp(player.getLifeStats().getMaxHp() / 3);
				return;
			}
			DuelService.getInstance().loseDuel(player);
		}
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
		Pet pet = player.getPet();
		if (pet != null) {
			PetSpawnService.dismissPet(player, true);
		} if (player.isInState(CreatureState.FLYING)) {
			player.setIsFlyingBeforeDeath(true);
		}
		player.setPlayerMode(PlayerMode.RIDE, null);
		player.unsetState(CreatureState.RESTING);
		player.unsetState(CreatureState.FLOATING_CORPSE);
		player.unsetState(CreatureState.FLYING);
		player.unsetState(CreatureState.GLIDING);
		player.setFlyState(0);
		if (player.isInInstance()) {
			if (player.getPosition().getWorldMapInstance().getInstanceHandler().onDie(player, lastAttacker)) {
				super.onDie(lastAttacker);
				return;
			}
		}
		MapRegion mapRegion = player.getPosition().getMapRegion();
		if (mapRegion != null && mapRegion.onDie(lastAttacker, getOwner())) {
			return;
		}
		this.doReward();
		if (master instanceof Npc || master == player) {
			if (player.getLevel() > 76 ){
				PlayerFameService.getInstance().onPlayerDie(player);
			}
		}
		super.onDie(lastAttacker);
		sendDieFromCreature(lastAttacker, showPacket);
		QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));
		if (player.isInGroup2()) {
			player.getPlayerGroup2().sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH(player.getName()), new ExcludePlayerFilter(player));
		}
	}
	
	@Override
	public void onDie(Creature lastAttacker) {
		this.onDie(lastAttacker, true);
	}

	public void sendDie() {
		sendDieFromCreature(getOwner(), true);
	}

	private void sendDieFromCreature(@Nonnull Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		if (showPacket) {
			if (player.isInInstance()) {
				PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8, false));
			} else {
				int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
				PacketSendUtility.sendPacket(player, new SM_DIE(player.canUseRebirthRevive(), player.haveSelfRezItem(), kiskTimeRemaining, 0));
			}
		}
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_DEATH);
	}

	@Override
	public void doReward() {
		PvpService.getInstance().doReward(getOwner());
	}

	@Override
	public void onBeforeSpawn() {
		this.onBeforeSpawn(true);
	}

	public void onBeforeSpawn(boolean blink) {
        super.onBeforeSpawn();
        if (blink) {
            startProtectionActiveTask();
        } if (getOwner().getIsFlyingBeforeDeath()) {
            getOwner().unsetState(CreatureState.FLOATING_CORPSE);
        } else {
            getOwner().unsetState(CreatureState.DEAD);
        }
        getOwner().setState(CreatureState.ACTIVE);
    }

	@Override
	public void attackTarget(Creature target, int attackNo, int time, int type) {
		PlayerGameStats gameStats = getOwner().getGameStats();
		if (!RestrictionsManager.canAttack(getOwner(), target)) {
			return;
		} if (!MathUtil.isInAttackRange(getOwner(), target, (gameStats.getAttackRange().getCurrent() / 1000) + 1)) {
			return;
		} if (!GeoService.getInstance().canSee(getOwner(), target)) {
			PacketSendUtility.sendPacket(getOwner(), SM_SYSTEM_MESSAGE.STR_ATTACK_OBSTACLE_EXIST);
			return;
		} if (target instanceof Npc) {
			QuestEngine.getInstance().onAttack(new QuestEnv(target, getOwner(), 0, 0));
		}
		int attackSpeed = gameStats.getAttackSpeed().getCurrent();
		long milis = System.currentTimeMillis();
		if (milis - lastAttackMilis < attackSpeed) {
			return;
		}
		lastAttackMilis = milis;
		super.attackTarget(target, attackNo, time, type);
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead()) {
			return;
		} else if (getOwner().isInvul() || getOwner().isProtectionActive()) {
			damage = 0;
		}
		cancelUseItem();
		cancelGathering();
		super.onAttack(creature, skillId, type, damage, notifyAttack, log);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), creature, type, skillId, damage, log), true);
		lastAttackedMilis = System.currentTimeMillis();
	}

	/**
	 * @param skillId
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 */
	public void useSkill(int skillId, int targetType, float x, float y, float z, int time) {
		Player player = getOwner();

		Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());

		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill))
				return;

			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(time);
			skill.useSkill();
		}
	}

	/**
	 * @param template
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 * @param clientHitTime
	 */
	public void useSkill(SkillTemplate template, int targetType, float x, float y, float z, int clientHitTime, int skillLevel) {
		Player player = getOwner();
		if (player.isInInstance()) {
			player.getPosition().getWorldMapInstance().getInstanceHandler().onSkillUse(player, template);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onSkillUse(player, template);
		}
		Skill skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget());
		MameClientCompatDebug.logSkillResolved(player, template, skill, targetType, x, y, z, clientHitTime, skillLevel);
		if ((skill == null) && (player.isTransformed())) {
			SkillPanel panel = DataManager.PANEL_SKILL_DATA.getSkillPanel(player.getTransformModel().getPanelId());
			if ((panel != null) && (panel.canUseSkill(template.getSkillId(), skillLevel))) {
				skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget(), skillLevel);
			}
		} if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill)) {
				return;
			}
			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(clientHitTime);
			skill.useSkill();
			QuestEnv env = new QuestEnv(player.getTarget(), player, 0, 0);
			QuestEngine.getInstance().onUseSkill(env, template.getSkillId());
		}
	}

	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}

	@Override
	public void onStopMove() {
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
		cancelCurrentSkill();
		updateZone();
		super.onStopMove();
	}

	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
		cancelUseItem();
		cancelCurrentSkill();
		super.onStartMove();
	}

	@Override
	public void cancelCurrentSkill() {
		if (getOwner().getCastingSkill() == null) {
			return;
		}
		Player player = getOwner();
		Skill castingSkill = player.getCastingSkill();
		castingSkill.cancelCast();
		player.removeSkillCoolDown(castingSkill.getSkillTemplate().getDelayId());
		player.setCasting(null);
		player.setNextSkillUse(0);
		if (castingSkill.getSkillMethod() == SkillMethod.CAST) {
			PacketSendUtility.broadcastPacket(player, new SM_SKILL_CANCEL(player, castingSkill.getSkillTemplate().getSkillId()), true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED);
		} else if (castingSkill.getSkillMethod() == SkillMethod.ITEM) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(castingSkill.getItemTemplate().getNameId())));
			player.removeItemCoolDown(castingSkill.getItemTemplate().getUseLimits().getDelayId());
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), castingSkill.getFirstTarget().getObjectId(), castingSkill.getItemObjectId(), castingSkill.getItemTemplate().getTemplateId(), 0, 3, 0), true);
		}
	}

	@Override
	public void cancelUseItem() {
		Player player = getOwner();
		Item usingItem = player.getUsingItem();
		player.setUsingItem(null);
		if (hasTask(TaskId.ITEM_USE)) {
			cancelTask(TaskId.ITEM_USE);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usingItem == null ? 0 : usingItem.getObjectId(), usingItem == null ? 0 : usingItem.getItemTemplate().getTemplateId(), 0, 3, 0), true);
		}
	}

	public void cancelGathering() {
		Player player = getOwner();
		if (player.getTarget() instanceof Gatherable) {
			Gatherable g = (Gatherable) player.getTarget();
			g.getController().finishGathering(player);
		}
	}

	public void updatePassiveStats() {
		Player player = getOwner();
		for (PlayerSkillEntry skillEntry : player.getSkillList().getAllSkills()) {
			Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if (skill != null && skill.isPassive()) {
				skill.useSkill();
			}
		}
	}

	@Override
	public Player getOwner() {
		return (Player) super.getOwner();
	}

	@Override
	public void onRestore(HealType healType, int value) {
		super.onRestore(healType, value);
		switch (healType) {
			case DP:
				getOwner().getCommonData().addDp(value);
			break;
		}
	}

	/**
	 * @param player
	 * @return
	 */
	// TODO [AT] move to Player
	public boolean isDueling(Player player) {
		return DuelService.getInstance().isDueling(player.getObjectId(), getOwner().getObjectId());
	}

	// TODO [AT] rename or remove
	public boolean isInShutdownProgress() {
		return isInShutdownProgress;
	}

	// TODO [AT] rename or remove
	public void setInShutdownProgress(boolean isInShutdownProgress) {
		this.isInShutdownProgress = isInShutdownProgress;
	}

	@Override
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex, int unk) {
		switch (dialogId) {
			case 2:
			break;
		}
	}
	
	public void upgradePlayer() {
		Player player = getOwner();
		int level = player.getLevel();
		PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		player.setPlayerStatsTemplate(statsTemplate);
		player.getLifeStats().synchronizeWithMaxStats();
		player.getLifeStats().updateCurrentStats();
		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 0, level), true);
		if (HTMLConfig.ENABLE_GUIDES) {
			HTMLService.sendGuideHtml(player);
		}
		QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		player.getController().updatePassiveStats();
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		if (level == 3) {
			if (player.getRace() == Race.ELYOS) {
				ItemService.addItem(player, 182216247, 1); //Q60003.
			} else {
				ItemService.addItem(player, 182216395, 1); //Q70003.
			}
		} if (level >= 9 && CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			if (!player.isCompleteQuest(1006) && player.getRace() == Race.ELYOS) {
				ClassChangeService.completeQuest(player, 1006);
			} else if (!player.isCompleteQuest(2008) && player.getRace() == Race.ASMODIANS) {
				ClassChangeService.completeQuest(player, 2008);
			}
		} if (level >= 20) {
			//Open "Stigma Slot" + "Completed" quest auto.
			if (!player.isCompleteQuest(1929) && player.getRace() == Race.ELYOS) {
				ClassChangeService.completeQuest(player, 1929);
			} else if (!player.isCompleteQuest(2900) && player.getRace() == Race.ASMODIANS) {
				ClassChangeService.completeQuest(player, 2900);
			}
        }
		//Morph Recipe 7.x
		if (level == 76) {
			CraftSkillUpdateService.getInstance().setMorphRecipe(player);
		}
		SkillLearnService.addMissingSkills(player);
		PacketSendUtility.sendPacket(player, new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
		if (player.isInTeam()) {
			TeamEffectUpdater.getInstance().startTask(player);
		} if (player.isLegionMember()) {
			LegionService.getInstance().updateMemberInfo(player);
		} if (level >= 1 && level <= 80) {
			reachedPlayerLvl(player);
		} if (level >= 65) {
			avatarVessel(player);
		} if (level >= 75) {
			avatarTransform(player);
		} if (level >= 76) {
			AchievementService.getInstance().onLeveUplPlayer(player);
		}
		//Update Summon.
		if (player.getSummon() != null) {
			Summon summon = player.getSummon();
			summon.setLevel(player.getLevel());
			PacketSendUtility.sendPacket(player, new SM_SUMMON_UPDATE(summon));
		}
		player.getNpcFactions().onLevelUp();
		FireTempleService.getInstance().onlevelUpPlayer(player);
		PvPSoloArenaService.getInstance().onlevelUpPlayer(player);
	}
	
	public static final void avatarVessel(final Player player) {
		player.getSkillList().addSkill(player, 4696, 1); //Transformation: Vessel Of Wind.
        player.getSkillList().addSkill(player, 4697, 1); //Mercurial Blast.
		player.getSkillList().addSkill(player, 4698, 1); //Transformation: Vessel Of Earth.
        player.getSkillList().addSkill(player, 4699, 1); //Terraform.
		player.getSkillList().addSkill(player, 4702, 1); //Transformation: Vessel Of Water.
        player.getSkillList().addSkill(player, 4703, 1); //Waterbind.
		if (player.getRace() == Race.ELYOS) {
            player.getSkillList().addSkill(player, 4700, 1); //Transformation: Vessel Of Fire.
            player.getSkillList().addSkill(player, 4701, 1); //Detonate (Elyos)
        } else if (player.getRace() == Race.ASMODIANS) {
            player.getSkillList().addSkill(player, 4700, 1); //Transformation: Vessel Of Fire.
            player.getSkillList().addSkill(player, 4704, 1); //Detonate (Asmodians)
        }
	}
	
	public static final void avatarTransform(final Player player) {
		if (player.getRace() == Race.ELYOS) {
			player.getSkillList().addSkill(player, 4752, 1); //Transformation: Avatar Of Fire (Elyos)
			player.getSkillList().addSkill(player, 4757, 1); //Transformation: Avatar Of Water (Elyos)
			player.getSkillList().addSkill(player, 4762, 1); //Transformation: Avatar Of Earth (Elyos)
			player.getSkillList().addSkill(player, 4768, 1); //Transformation: Avatar Of Wind (Elyos)
		} else if (player.getRace() == Race.ASMODIANS) {
			player.getSkillList().addSkill(player, 4804, 1); //Transformation: Avatar Of Fire (Asmodians)
			player.getSkillList().addSkill(player, 4805, 1); //Transformation: Avatar Of Water (Asmodians)
			player.getSkillList().addSkill(player, 4806, 1); //Transformation: Avatar Of Earth (Asmodians)
			player.getSkillList().addSkill(player, 4807, 1); //Transformation: Avatar Of Wind (Asmodians)
		}
	}
	
	public static final void reachedPlayerLvl(final Player player) {
	    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player players) {
				//"Player Name" has reached level %1.
				int playerLevel = player.getLevel();
				PacketSendUtility.sendPacket(players, new SM_SYSTEM_MESSAGE(1300086, player.getName(), playerLevel));
			}
		});
	}
	
	/**
	 * After entering game player char is "blinking" which means that it's in under some protection, after making an
	 * action char stops blinking. - Starts protection active - Schedules task to end protection
	 */
	public void startProtectionActiveTask() {
		if (!getOwner().isProtectionActive()) {
			TeleportService2.playerTransformation(getOwner());
			TeleportService2.instanceTransformation(getOwner());
			TeleportService2.archdaevaTransformation(getOwner());
			getOwner().setVisualState(CreatureVisualState.BLINKING);
			AttackUtil.cancelCastOn((Creature) getOwner());
            AttackUtil.removeTargetFrom((Creature) getOwner());
			PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()), true);
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					stopProtectionActiveTask();
				}
			}, 60000);
			addTask(TaskId.PROTECTION_ACTIVE, task);
		}
	}

	/**
	 * Stops protection active task after first move or use skill
	 */
	public void stopProtectionActiveTask() {
		cancelTask(TaskId.PROTECTION_ACTIVE);
		Player player = getOwner();
		if (player != null && player.isSpawned()) {
			player.unsetVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
			notifyAIOnMove();
		}
	}

	/**
	 * When player arrives at destination point of flying teleport
	 */
	public void onFlyTeleportEnd() {
		Player player = getOwner();
		if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
			player.unsetPlayerMode(PlayerMode.WINDSTREAM);
			player.getLifeStats().triggerFpReduce();
			player.unsetState(CreatureState.FLYING);
			player.setState(CreatureState.ACTIVE);
			player.setState(CreatureState.GLIDING);
			player.getGameStats().updateStatsAndSpeedVisually();
		} else {
			player.unsetState(CreatureState.FLIGHT_TELEPORT);
			player.setFlightTeleportId(0);
			if (SecurityConfig.ENABLE_FLYPATH_VALIDATOR) {
				long diff = (System.currentTimeMillis() - player.getFlyStartTime());
				FlyPathEntry path = player.getCurrentFlyPath();
				if (player.getWorldId() != path.getEndWorldId()) {
					AuditLogger.info(player, "Player tried to use flyPath #" + path.getId() + " from not native start world " + player.getWorldId() + ". expected " + path.getEndWorldId());
				} if (diff < path.getTimeInMs()) {
					AuditLogger.info(player, "Player " + player.getName() + " used flypath bug " + diff + " instead of " + path.getTimeInMs());
				}
				player.setCurrentFlypath(null);
			}
			player.setFlightDistance(0);
			player.setState(CreatureState.ACTIVE);
			player.getController().updateZone();
		    player.getController().updateNearbyQuests();
		}
	}

	public boolean addItems(int itemId, int count) {
		return ItemService.addQuestItems(getOwner(), Collections.singletonList(new QuestItems(itemId, count)));
	}

	public void startStance(final int skillId) {
		stance = skillId;
	}

	public void stopStance() {
		getOwner().getEffectController().removeEffect(stance);
		PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_STANCE(getOwner(), 0));
		stance = 0;
	}

	public int getStanceSkillId() {
		return stance;
	}

	public boolean isUnderStance() {
		return stance != 0;
	}

	public void updateSoulSickness(int skillId) {
		Player player = getOwner();
		House house = player.getActiveHouse();
		if (house != null) {
			switch (house.getHouseType()) {
				case MANSION:
				case ESTATE:
				case PALACE:
					return;
			}
		} if (!player.havePermission(MembershipConfig.DISABLE_SOULSICKNESS)) {
			int deathCount = player.getCommonData().getDeathCount();
			if (deathCount < 10) {
				deathCount++;
				player.getCommonData().setDeathCount(deathCount);
			} if (skillId == 0) {
				skillId = 8291;
			}
			SkillEngine.getInstance().getSkill(player, skillId, deathCount, player).useSkill();
		}
	}

	/**
	 * Player is considered in combat if he's been attacked or has attacked less or equal 10s before
	 * 
	 * @return true if the player is actively in combat
	 */
	public boolean isInCombat() {
		return (((System.currentTimeMillis() - lastAttackedMilis) <= 10000) || ((System.currentTimeMillis() - lastAttackMilis) <= 10000));
	}

	public boolean isNoDeathPenaltyInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isNoDeathPenalty()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isNoDeathPenaltyReduceInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isNoDeathPenaltyReduce()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isDeathPenaltyReduceInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isDeathPenaltyReduce()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isNoResurrectPenaltyInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isNoResurrectPenalty()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHiPassInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isHiPass()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHandOfReincarnationEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isHandOfReincarnation()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isChromaticAuraEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isChromaticAura()) {
				return true;
			}
		}
		return false;
	}
}