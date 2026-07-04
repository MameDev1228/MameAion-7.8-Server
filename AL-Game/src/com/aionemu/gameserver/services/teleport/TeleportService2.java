package com.aionemu.gameserver.services.teleport;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.dao.PlayerTransformDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.portal.InstanceExit;
import com.aionemu.gameserver.model.templates.portal.PortalLoc;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.portal.PortalScroll;
import com.aionemu.gameserver.model.templates.revive_start_points.*;
import com.aionemu.gameserver.model.templates.robot.RobotInfo;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleportType;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.player.LunaShopService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleportService2
{
	private static final Logger log = LoggerFactory.getLogger(TeleportService2.class);
	
	public static void teleport(TeleporterTemplate template, int locId, Player player, Npc npc, TeleportAnimation animation) {
		TribeClass tribe = npc.getTribe();
		Race race = player.getRace();
		if (tribe.equals(TribeClass.FIELD_OBJECT_LIGHT) && race.equals(Race.ASMODIANS) || tribe.equals(TribeClass.FIELD_OBJECT_DARK) && race.equals(Race.ELYOS)) {
			return;
		} if (template.getTeleLocIdData() == null) {
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			}
			return;
		}
		TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if (location == null) {
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			}
			return;
		}
		TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(locId);
		if (locationTemplate == null) {
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: " + locId);
			    return;
			}
		} if (location.getRequiredQuest() == 60300) {
			if (player.getRace() == Race.ELYOS) {
				QuestState qs = player.getQuestStateList().getQuestState(location.getRequiredQuest());
				if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) { //All Aboard!
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(60300));
					return;
				}
			}
		} if (location.getRequiredQuest() == 70300) {
			if (player.getRace() == Race.ASMODIANS) {
				QuestState qs = player.getQuestStateList().getQuestState(location.getRequiredQuest());
				if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) { //Arriving in Gelkmaros.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(70300));
					return;
				}
			}
		} if (!checkKinahForTransportation(location, player)) {
			return;
		} if (location.getType() == TeleportType.FLIGHT) {
			player.unsetPlayerMode(PlayerMode.RIDE);
			player.setState(CreatureState.FLIGHT_TELEPORT);
			player.unsetState(CreatureState.ACTIVE);
			player.setFlightTeleportId(location.getTeleportId());
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, location.getTeleportId(), 0), true);
			playerTransformation(player);
			instanceTransformation(player);
			archdaevaTransformation(player);
		} else {
			int instanceId = 1;
			int mapId = locationTemplate.getMapId();
			if (player.getWorldId() == mapId) {
				instanceId = player.getInstanceId();
			}
			sendLoc(player, mapId, instanceId, locationTemplate.getX(), locationTemplate.getY(), locationTemplate.getZ(), (byte) locationTemplate.getHeading(), animation);
			playerTransformation(player);
			instanceTransformation(player);
			archdaevaTransformation(player);
		}
	}
	
	private static boolean checkKinahForTransportation(TeleportLocation location, Player player) {
		Storage inventory = player.getInventory();
		int basePrice = (int) (location.getPrice() * 0.8F);
		long transportationPrice = PricesService.getPriceForService(basePrice, player.getRace());
		if (player.getController().isHiPassInEffect()) {
			transportationPrice = 1;
		} if (!inventory.tryDecreaseKinah(transportationPrice)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(transportationPrice));
			return false;
		}
		return true;
	}
	
	private static void sendLoc(final Player player, final int mapId, final int instanceId, final float x, final float y, final float z, final byte h, final TeleportAnimation animation) {
		boolean isInstance = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).isInstance();
		PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(isInstance, instanceId, mapId, x, y, z, h, animation.getStartAnimationId()));
		player.unsetPlayerMode(PlayerMode.RIDE);
		playerTransformation(player);
		instanceTransformation(player);
		archdaevaTransformation(player);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (player.getLifeStats().isAlreadyDead() || !player.isSpawned()) {
					return;
				}
				TeleportService2.changePosition(player, mapId, instanceId, x, y, z, h, animation);
			}
		}, 2200);
	}
	
	public static void teleportTo(Player player, WorldPosition pos) {
		if (player.getWorldId() == pos.getMapId()) {
			player.getPosition().setXYZH(pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			//Pet.
			Pet pet = player.getPet();
			if (pet != null) {
				World.getInstance().setPosition(pet, pos.getMapId(), player.getInstanceId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			}
			//Summon.
			Summon summon = player.getSummon();
			if (summon != null) {
				World.getInstance().setPosition(summon, pos.getMapId(), player.getInstanceId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			}
			//Minion.
			MinionService.getInstance().onTeleportPlayer(player);
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
			player.setPortAnimation(4);
			PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
			player.getController().startProtectionActiveTask();
			PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			//Pet.
			if (pet != null) {
				World.getInstance().spawn(pet);
			}
			//Summon.
			if (summon != null) {
				World.getInstance().spawn(summon);
			}
			//Minion.
			MinionService.getInstance().onTeleportPlayer(player);
			player.updateKnownlist();
			player.getKnownList().clear();
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			DisputeLandService.getInstance().onLogin(player);
			player.getEffectController().updatePlayerEffectIcons();
			ProtectorConquerorService sgs = ProtectorConquerorService.getInstance();
			playerTransformation(player);
			instanceTransformation(player);
			archdaevaTransformation(player);
			PacketSendUtility.sendPacket(player, new SM_CONQUEROR_PROTECTOR(false, player.getProtectorInfo().getRank()));
			PacketSendUtility.sendPacket(player, new SM_CONQUEROR_PROTECTOR(false, player.getConquerorInfo().getRank()));
		} else if (player.getLifeStats().isAlreadyDead()) {
			teleportDeadTo(player, pos.getMapId(), 1, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
		} else {
			teleportTo(player, pos.getMapId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
		}
	}
	
	public static void teleportDeadTo(Player player, int worldId, int instanceId, float x, float y, float z, byte heading) {
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		World.getInstance().setPosition(player, worldId, instanceId, x, y, z, heading);
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
	}
	
	public static boolean teleportTo(Player player, int worldId, float x, float y, float z) {
		return teleportTo(player, worldId, x, y, z, player.getHeading());
	}
	
	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, byte h) {
		int instanceId = 1;
		if (player.getWorldId() == worldId) {
			instanceId = player.getInstanceId();
		} if (player.isInInstance()) {
			teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
			return true;
		}
		return teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.BEAM_ANIMATION);
	}
	
	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, byte h, TeleportAnimation animation) {
		int instanceId = 1;
		if (player.getWorldId() == worldId) {
			instanceId = player.getInstanceId();
		}
		return teleportTo(player, worldId, instanceId, x, y, z, h, animation);
	}
	
	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, byte h) {
		return teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.BEAM_ANIMATION);
	}
	
	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z) {
		return teleportTo(player, worldId, instanceId, x, y, z, player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
	}
	
	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, byte heading, TeleportAnimation animation) {
		if (player.getLifeStats().isAlreadyDead()) {
			return false;
		} if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		} if (player.getWorldId() != worldId) {
			player.getController().onLeaveWorld();
		} if (animation.isNoAnimation()) {
			playerTransformation(player);
			instanceTransformation(player);
			archdaevaTransformation(player);
			player.unsetPlayerMode(PlayerMode.RIDE);
			changePosition(player, worldId, instanceId, x, y, z, heading, animation);
		} else {
			sendLoc(player, worldId, instanceId, x, y, z, heading, animation);
		}
		return true;
	}
	
	private static void changePosition(final Player player, int worldId, int instanceId, float x, float y, float z, byte heading, TeleportAnimation animation) {
		player.getFlyController().endFly(true);
		World.getInstance().despawn(player);
		//Send 2x, is normal !!!
		playerTransformation(player);
		instanceTransformation(player);
		archdaevaTransformation(player);
		player.getController().cancelCurrentSkill();
		int currentWorldId = player.getWorldId();
		boolean isInstance = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).isInstance();
		World.getInstance().setPosition(player, worldId, instanceId, x, y, z, heading);
		//Pet.
		Pet pet = player.getPet();
		if (pet != null) {
			World.getInstance().setPosition(pet, worldId, instanceId, x, y, z, heading);
		}
		//Summon.
		Summon summon = player.getSummon();
		if (summon != null) {
			World.getInstance().setPosition(summon, worldId, instanceId, x, y, z, heading);
		}
		player.setPortAnimation(animation.getEndAnimationId());
		player.getController().startProtectionActiveTask();
		if (currentWorldId == worldId) {
			PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			player.getController().startProtectionActiveTask();
			PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			World.getInstance().spawn(player);
			player.getEffectController().updatePlayerEffectIcons();
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			DisputeLandService.getInstance().onLogin(player);
			//Send 2x, is normal !!!
			playerTransformation(player);
			instanceTransformation(player);
			archdaevaTransformation(player);
			//Pet.
			if (pet != null) {
				World.getInstance().spawn(pet);
			    player.setPortAnimation(4);
			}
			//Summon.
			if (summon != null) {
			    World.getInstance().spawn(summon);
				player.setPortAnimation(4);
			}
			player.getKnownList().clear();
			player.updateKnownlist();
			if (player.isUseRobot() || player.getRobotId() != 0) {
				PacketSendUtility.sendPacket(player, new SM_USE_ROBOT(player, getRobotInfo(player).getRobotId()));
			}
		} else {
			PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
			PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
			playerTransformation(player);
			instanceTransformation(player);
			archdaevaTransformation(player);
			if (player.isUseRobot() || player.getRobotId() != 0) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						PacketSendUtility.sendPacket(player, new SM_USE_ROBOT(player, getRobotInfo(player).getRobotId()));
					}
				}, 3000);
			}
		} if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
		sendWorldSwitchMessage(player, currentWorldId, worldId, isInstance);
		AchievementService.getInstance().onUpdateAchievementAction(player, worldId, 1, AchievementActionType.ENTER_WORLD);
	}
	
	public static RobotInfo getRobotInfo(Player player) {
		ItemTemplate template = player.getEquipment().getMainHandWeapon().getItemSkinTemplate();
		return DataManager.ROBOT_DATA.getRobotInfo(template.getRobotId());
	}
	
	private static void sendWorldSwitchMessage(Player player, int oldWorld, int newWorld, boolean enteredInstance) {
		onEnterInstance(player, oldWorld, newWorld, enteredInstance);
	}
	
	private static void onEnterInstance(Player player, int oldWorld, int newWorld, boolean enteredInstance) {
		if ((enteredInstance) && (oldWorld != newWorld) && (!WorldMapType.getWorld(newWorld).isPersonal())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_DUNGEON_OPENED_FOR_SELF(newWorld));
			LunaShopService.getInstance().sendLunaInstanceBuff(player, player.getLevel());
			AchievementService.getInstance().onUpdateAchievementAction(player, newWorld, 1, AchievementActionType.ENTER_WORLD);
			if (player.getPortalCooldownList().getPortalCooldownItem(newWorld) == null) {
				player.getPortalCooldownList().addPortalCooldown(newWorld, 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, newWorld));
			} else {
				player.getPortalCooldownList().addEntry(newWorld);
				//You have successfully entered the area, consuming one of your permitted entries.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_DUNGEON_COUNT_USE, 20000);
			}
		}
		playerTransformation(player);
		instanceTransformation(player);
		archdaevaTransformation(player);
	}
	
	public static void showMap(Player player, int targetObjectId, int npcId) {
		if (player.isInFlyingState()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_AIRPORT_WHEN_FLYING);
			return;
		}
		Npc object = (Npc) World.getInstance().findVisibleObject(targetObjectId);
		if (player.isEnemy(object)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_WRONG_NPC);
			return;
		}
		PacketSendUtility.sendPacket(player, new SM_TELEPORT_MAP(player, targetObjectId, getTeleporterTemplate(npcId)));
	}
	
	public static TeleporterTemplate getTeleporterTemplate(int npcId) {
		return DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npcId);
	}
	
	public static void moveToKiskLocation(Player player, WorldPosition kisk) {
		int mapId = kisk.getMapId();
		float x = kisk.getX();
		float y = kisk.getY();
		float z = kisk.getZ();
		byte heading = kisk.getHeading();
		teleportTo(player, mapId, x, y, z, heading);
	}
	
	public static void teleportToPrison(Player player) {
		if (player.getRace() == Race.ELYOS) {
			teleportTo(player, WorldMapType.DE_PRISON.getId(), 275.0f, 239.0f, 49.0f);
		} else if (player.getRace() == Race.ASMODIANS) {
			teleportTo(player, WorldMapType.DF_PRISON.getId(), 275.0f, 239.0f, 49.0f);
		}
	}
	
	public static void teleportToNpc(Player player, int npcId) {
		int worldId = player.getWorldId();
		SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(worldId, npcId);
		if (searchResult == null) {
			log.warn("No npc spawn found for : " + npcId);
			return;
		}
		SpawnSpotTemplate spot = searchResult.getSpot();
		WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(searchResult.getWorldId());
		WorldMapInstance newInstance = null;
		if (worldTemplate.isInstance()) {
			newInstance = InstanceService.getNextAvailableInstance(searchResult.getWorldId());
		} if (newInstance != null) {
			InstanceService.registerPlayerWithInstance(newInstance, player);
			teleportTo(player, searchResult.getWorldId(), newInstance.getInstanceId(), spot.getX(), spot.getY(), spot.getZ());
		} else {
			teleportTo(player, searchResult.getWorldId(), spot.getX(), spot.getY(), spot.getZ());
		}
	}
	
	public static void sendSetBindPoint(Player player) {
		int worldId;
		float x, y, z;
		if (player.getBindPoint() != null) {
			BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		} else {
			LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}
		PacketSendUtility.sendPacket(player, new SM_BIND_POINT_INFO(worldId, x, y, z, player));
	}
	
	public static void moveToBindLocation(Player player, boolean useTeleport) {
		moveToBindLocation(player, useTeleport, 0);
	}
	
	public static void moveToBindLocation(Player player, boolean useTeleport, int delay) {
		byte h = 0;
		int worldId;
		float x;
		float y;
		float z;
		if (player.getBindPoint() != null) {
			BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
			h = bplist.getHeading();
		} else {
			LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}
		InstanceService.onLeaveInstance(player);
		if (useTeleport) {
			teleportTo(player, worldId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
		} else {
			World.getInstance().setPosition(player, worldId, 1, x, y, z, h);
		}
	}
	
	public static void moveToInstanceExit(Player player, int worldId, Race race) {
		player.getController().cancelCurrentSkill();
		InstanceExit instanceExit = getInstanceExit(worldId, race);
		if (instanceExit == null) {
			log.warn("No instance exit found for race: " + race + " " + worldId);
			moveToBindLocation(player, true);
			return;
		} if (InstanceService.isInstanceExist(instanceExit.getExitWorld(), 1)) {
			teleportTo(player, instanceExit.getExitWorld(), instanceExit.getX(), instanceExit.getY(), instanceExit.getZ(), instanceExit.getH());
		} else {
			moveToBindLocation(player, true);
		}
	}
	
	public static InstanceExit getInstanceExit(int worldId, Race race) {
		return DataManager.INSTANCE_EXIT_DATA.getInstanceExit(worldId, race);
	}
	
	public static InstanceReviveStartPoints getReviveInstanceStartPoints(int worldId) {
		return DataManager.REVIVE_INSTANCE_START_POINTS.getReviveStartPoint(worldId);
	}
	
	public static WorldReviveStartPoints getReviveWorldStartPoints(int worldId, Race race, int level) {
		return DataManager.REVIVE_WORLD_START_POINTS.getReviveStartPoint(worldId, race, level);
	}
	
	public static void useTeleportScroll(Player player, String portalName, int worldId) {
		PortalScroll template = DataManager.PORTAL2_DATA.getPortalScroll(portalName);
		if (template == null) {
			log.warn("No portal template found for : " + portalName + " " + worldId);
			return;
		}
		Race playerRace = player.getRace();
		PortalPath portalPath = template.getPortalPath();
		if (portalPath == null) {
			log.warn("No portal scroll for " + playerRace + " on " + portalName + " " + worldId);
			return;
		}
		PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(portalPath.getLocId());
		if (loc == null) {
			log.warn("No portal loc for locId" + portalPath.getLocId());
			return;
		}
		teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ());
		AchievementService.getInstance().onUpdateAchievementAction(player, worldId, 1, AchievementActionType.ENTER_WORLD);
	}
	
	public static void teleportWorldStartPoint(Player player, int worldId) {
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		WorldReviveStartPoints startPoint = getReviveWorldStartPoints(worldId, player.getRace(), player.getLevel());
		if (startPoint != null) {
			World.getInstance().setPosition(player, startPoint.getReviveWorld(), 0, startPoint.getX(), startPoint.getY(), startPoint.getZ(), (byte) startPoint.getH());
		} else {
			moveToBindLocation(player, false);
		}
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
	}
	
	public static void teleportInstanceStartPoint(Player player, int worldId) {
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		InstanceReviveStartPoints revivePoint = getReviveInstanceStartPoints(worldId);
		if (revivePoint != null) {
			TeleportService2.teleportTo(player, worldId, worldId, revivePoint.getX(), revivePoint.getY(), revivePoint.getY(), (byte) revivePoint.getY());
		} else {
			moveToBindLocation(player, false);
		}
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
	}
	
	public static void changeChannel(Player player, int channel) {
		World.getInstance().despawn(player);
		World.getInstance().setPosition(player, player.getWorldId(), channel + 1, player.getX(), player.getY(), player.getZ(), player.getHeading());
		player.getController().startProtectionActiveTask();
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		playerTransformation(player);
		instanceTransformation(player);
		archdaevaTransformation(player);
	}

	public static void playerTransformation(Player player) {
		DAOManager.getDAO(PlayerTransformDAO.class).loadPlTransfo(player);
		PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, player.getTransformModel().getPanelId(), true, player.getTransformModel().getItemId(), player.getTransformModel().getSkillId()));
	}
	
   /**
	* Archdaeva Transformation
	* If a player is under one of the following effects,
	* and uses a "Teleport/Fly/Hotspot/Return Scroll" or use admin command "goto/movetoplayer/movetonpc"
	* Then, the "Skill Panel" linked to this effect, never disappear !!!
	*/
	public static void archdaevaTransformation(Player player) {
		if (!player.isInGroup2() || player != null) {
		    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.getWorldId() == player.getWorldId() && player != player) {
						//Archdaeva 5.x
						if (player.getEffectController().hasAbnormalEffect(4752)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(76);
								player.getTransformModel().setItemId(102301000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 76, true, 102301000, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4757)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(77);
								player.getTransformModel().setItemId(102303000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 77, true, 102303000, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4762)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(78);
								player.getTransformModel().setItemId(102302000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 78, true, 102302000, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4768)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(79);
								player.getTransformModel().setItemId(102304000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 79, true, 102304000, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4804)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(76);
								player.getTransformModel().setItemId(102301000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 76, true, 102301000, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4805)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(77);
								player.getTransformModel().setItemId(102303000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 77, true, 102303000, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4806)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(78);
								player.getTransformModel().setItemId(102302000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 78, true, 102302000, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4807)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(79);
								player.getTransformModel().setItemId(102304000);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 79, true, 102304000, 0));
							}
						}
					}
				}
			});
		}
	}
	
   /**
	* Instance + Event Transformation
	* If a player is under one of the following effects,
	* and uses a "Teleport/Fly/Hotspot/Return Scroll" or use admin command "goto/movetoplayer/movetonpc"
	* Then, the "Skill Panel" linked to this effect, never disappear !!!
	*/
	public static void instanceTransformation(Player player) {
		if (!player.isInGroup2() || player != null) {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.getWorldId() == player.getWorldId() && player != player) {
						//[PvP] Arena
						if (player.getEffectController().hasAbnormalEffect(10405)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(15);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 15, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(10406)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(15);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 15, true, 0, 0));
							}
						}
						//Contaminated Underpath 5.x
						if (player.getEffectController().hasAbnormalEffect(21345)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(68);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 68, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(21346)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(68);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 68, true, 0, 0));
							}
						}
						//[Event] Contaminated Underpath 5.x
						if (player.getEffectController().hasAbnormalEffect(4935)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(120);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 120, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4936)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(121);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 121, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4937)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(122);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 122, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4938)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(123);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 123, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4939)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(124);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 124, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4940)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(120);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 120, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4941)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(121);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 121, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4942)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(122);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 122, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4943)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(123);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 123, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(4944)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(124);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 124, true, 0, 0));
							}
						}
						//Secret Munitions Factory 5.x
						if (player.getEffectController().hasAbnormalEffect(21347)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(69);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 69, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(21348)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(69);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 69, true, 0, 0));
							}
						}
						//The Shugo Emperor Vault.
						if (player.getEffectController().hasAbnormalEffect(21829)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(64);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 64, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(21830)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(65);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 65, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(21831)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(66);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 66, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(21832)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(64);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 64, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(21833)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(65);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 65, true, 0, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(21834)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(66);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 66, true, 0, 0));
							}
						}
						//Qubrinerk's Cubic Lab 6.x
						if (player.getEffectController().hasAbnormalEffect(17426)) {
							player.getTransformModel().setPanelId(133);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 133, true, 0, 0));
						}
						//Kumuki Cave With Made In Abyss 6.x
						if (player.getEffectController().hasAbnormalEffect(5329)) {
							player.getTransformModel().setPanelId(161);
							player.getTransformModel().setItemId(100950102);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 161, true, 100950102, 0));
						}
						//The Veilenthrone 6.x
						if (player.getEffectController().hasAbnormalEffect(17585)) {
							player.getTransformModel().setPanelId(152);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 152, true, 0, 0));
						}
						//Steel Rake Fortress 6.x
						if (player.getEffectController().hasAbnormalEffect(20321)) {
							player.getTransformModel().setPanelId(156);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 156, true, 0, 0));
						} if (player.getEffectController().hasAbnormalEffect(20322)) {
							player.getTransformModel().setPanelId(157);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 157, true, 0, 0));
						}
						//Illumiel Brawl 6.x
						if (player.getEffectController().hasAbnormalEffect(20309)) {
							player.getTransformModel().setPanelId(136);
							player.getTransformModel().setItemId(100002371);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 136, true, 100002371, 0));
						} if (player.getEffectController().hasAbnormalEffect(20310)) {
							player.getTransformModel().setPanelId(139);
							player.getTransformModel().setItemId(100901787);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 139, true, 100901787, 0));
						} if (player.getEffectController().hasAbnormalEffect(20311)) {
							player.getTransformModel().setPanelId(142);
							player.getTransformModel().setItemId(100601837);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 142, true, 100601837, 0));
						} if (player.getEffectController().hasAbnormalEffect(20312)) {
							player.getTransformModel().setPanelId(139);
							player.getTransformModel().setItemId(101901422);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 145, true, 101901422, 0));
						} if (player.getEffectController().hasAbnormalEffect(20313)) {
							player.getTransformModel().setPanelId(148);
							player.getTransformModel().setItemId(100101774);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 148, true, 100101774, 0));
						} if (player.getEffectController().hasAbnormalEffect(20314)) {
							player.getTransformModel().setPanelId(151);
							player.getTransformModel().setItemId(101501792);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 151, true, 101501792, 0));
						} if (player.getEffectController().hasAbnormalEffect(20315)) {
							player.getTransformModel().setPanelId(136);
							player.getTransformModel().setItemId(100002371);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 136, true, 100002371, 0));
						} if (player.getEffectController().hasAbnormalEffect(20316)) {
							player.getTransformModel().setPanelId(139);
							player.getTransformModel().setItemId(100901787);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 139, true, 100901787, 0));
						} if (player.getEffectController().hasAbnormalEffect(20317)) {
							player.getTransformModel().setPanelId(142);
							player.getTransformModel().setItemId(100601837);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 142, true, 100601837, 0));
						} if (player.getEffectController().hasAbnormalEffect(20318)) {
							player.getTransformModel().setPanelId(139);
							player.getTransformModel().setItemId(101901422);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 145, true, 101901422, 0));
						} if (player.getEffectController().hasAbnormalEffect(20319)) {
							player.getTransformModel().setPanelId(148);
							player.getTransformModel().setItemId(100101774);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 148, true, 100101774, 0));
						} if (player.getEffectController().hasAbnormalEffect(20320)) {
							player.getTransformModel().setPanelId(151);
							player.getTransformModel().setItemId(101501792);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 151, true, 101501792, 0));
						}
						//[Altar] Demaha 7.x
						if (player.getEffectController().hasAbnormalEffect(20863)) {
							player.getTransformModel().setPanelId(162);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 162, true, 0, 0));
						}
						//Scaleshadow 7.x
						if (player.getEffectController().hasAbnormalEffect(5686)) {
							player.getTransformModel().setPanelId(167);
							player.getTransformModel().setItemId(102301002);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 167, true, 102301002, 0));
						}
						//Wicked Gracheni's Vault 7.x
						if (player.getEffectController().hasAbnormalEffect(19289)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(64);
								player.getTransformModel().setItemId(100050106);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 64, true, 100050106, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(19299)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(65);
								player.getTransformModel().setItemId(100950101);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 65, true, 100950101, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(19303)) {
							if (player.getCommonData().getRace() == Race.ELYOS) {
								player.getTransformModel().setPanelId(66);
								player.getTransformModel().setItemId(100550101);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 66, true, 100550101, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(19290)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(64);
								player.getTransformModel().setItemId(100050106);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 64, true, 100050106, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(19300)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(65);
								player.getTransformModel().setItemId(100950101);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 65, true, 100950101, 0));
							}
						} if (player.getEffectController().hasAbnormalEffect(19304)) {
							if (player.getCommonData().getRace() == Race.ASMODIANS) {
								player.getTransformModel().setPanelId(66);
								player.getTransformModel().setItemId(100550101);
								PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 66, true, 100550101, 0));
							}
						}
						//Minium Vault 7.x
						if (player.getEffectController().hasAbnormalEffect(20815)) {
							player.getTransformModel().setPanelId(170);
							player.getTransformModel().setItemId(101301709);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 170, true, 101301709, 0));
						} if (player.getEffectController().hasAbnormalEffect(20816)) {
							player.getTransformModel().setPanelId(172);
							player.getTransformModel().setItemId(100101813);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 172, true, 100101813, 0));
						} if (player.getEffectController().hasAbnormalEffect(20817)) {
							player.getTransformModel().setPanelId(171);
							player.getTransformModel().setItemId(100901841);
							PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, 171, true, 100901841, 0));
						}
					}
				}
			});
		}
	}
}