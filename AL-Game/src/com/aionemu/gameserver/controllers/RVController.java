package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.rift.RiftEnum;
import com.aionemu.gameserver.services.rift.RiftInformer;
import com.aionemu.gameserver.services.rift.RiftManager;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastMap;

public class RVController extends NpcController
{
	private boolean isMaster = false;
	protected FastMap<Integer, Player> passedPlayers = new FastMap<Integer, Player>();
	private SpawnTemplate slaveSpawnTemplate;
	private Npc slave;
	private Integer minLevel;
	private Integer maxLevel;
	private int deSpawnedTime;
	private Integer maxEntries;
	private Integer abyssPoint;
	private boolean isAccepting;
	private int usedEntries = 0;
	private RiftEnum riftTemplate;
	
	public RVController(Npc slave, RiftEnum riftTemplate) {
		this.riftTemplate = riftTemplate;
		this.maxEntries = riftTemplate.getEntries();
		this.abyssPoint = riftTemplate.getAbyssPoint();
		this.minLevel = riftTemplate.getMinLevel();
		this.maxLevel = riftTemplate.getMaxLevel();
		this.deSpawnedTime = ((int) (System.currentTimeMillis() / 1000)) + (RiftService.getInstance().getDuration() * 3600);
		if (slave != null) {
			this.slave = slave;
			this.slaveSpawnTemplate = slave.getSpawn();
			isMaster = true;
			isAccepting = true;
		}
	}
	
	@Override
	public void onDialogRequest(Player player) {
		if (!isMaster && !isAccepting) {
			return;
		}
		onRequest(player);
	}
	
	private void onRequest(Player player) {
		RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (onAccept(responder)) {
					int worldId = slaveSpawnTemplate.getWorldId();
					float x = slaveSpawnTemplate.getX();
					float y = slaveSpawnTemplate.getY();
					float z = slaveSpawnTemplate.getZ();
					//Update Quest/Guide.
					if (responder.getRace() == Race.ELYOS) {
						QuestState qs61952 = responder.getQuestStateList().getQuestState(61952);
						QuestState qs63803 = responder.getQuestStateList().getQuestState(63803);
						if (qs61952 != null && qs61952.getStatus() == QuestStatus.START && qs61952.getQuestVarById(0) == 0) {
							ClassChangeService.onUpdateQuest61952(responder);
						} else if (qs63803 != null && qs63803.getStatus() == QuestStatus.START && qs63803.getQuestVarById(0) == 1) {
							ClassChangeService.onUpdateGuide63803(responder);
						}
					} else {
						QuestState qs71952 = responder.getQuestStateList().getQuestState(71952);
						QuestState qs73803 = responder.getQuestStateList().getQuestState(73803);
						if (qs71952 != null && qs71952.getStatus() == QuestStatus.START && qs71952.getQuestVarById(0) == 0) {
							ClassChangeService.onUpdateQuest71952(responder);
						} else if (qs73803 != null && qs73803.getStatus() == QuestStatus.START && qs73803.getQuestVarById(0) == 1) {
							ClassChangeService.onUpdateGuide73803(responder);
						}
					}
					TeleportService2.teleportTo(responder, worldId, x, y, z);
					AchievementService.getInstance().onUpdateAchievementAction(responder, worldId, 1, AchievementActionType.ENTER_WORLD);
					PacketSendUtility.playerSendPacketTime(responder, SM_SYSTEM_MESSAGE.STR_MSG_RVR_DIRECT_PORTAL_OPEN_NOTICE, 10000);
					syncPassed(false);
				}
			}
			@Override
				public void denyRequest(Creature requester, Player responder) {
			    onDeny(responder);
			}
		};
		boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_CHAOS_DIRECT_PORTAL, responseHandler);
		if (requested) {
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_CHAOS_DIRECT_PORTAL, 0, 0));
		}
	}
	
	private void sendRequestUseAp(Player player) {
        RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {
            @Override
			public void acceptRequest(Creature requester, Player responder) {
				if (onAccept(responder)) {
					int worldId = slaveSpawnTemplate.getWorldId();
					float x = slaveSpawnTemplate.getX();
					float y = slaveSpawnTemplate.getY();
					float z = slaveSpawnTemplate.getZ();
					TeleportService2.teleportTo(responder, worldId, x, y, z);
					AbyssPointsService.addAp(responder, -getAbyssPoint()); //Rift 5.6
					PacketSendUtility.playerSendPacketTime(responder, SM_SYSTEM_MESSAGE.STR_MSG_RVR_DIRECT_PORTAL_OPEN_NOTICE, 10000);
					syncPassed(false);
				}
            }
            @Override
            public void denyRequest(Creature requester, Player responder) {
				onDeny(responder);
            }
        };
        boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL_USE_AP, responseHandler);
        if (requested) {
            PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL_USE_AP, 0, 0));
        }
    }
	
	private boolean onAccept(Player player) {
		if (!isAccepting) {
			return false;
		} if (!getOwner().isSpawned()) {
			return false;
		} if (player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_INVADE_DIRECT_PORTAL_LEVEL_LIMIT);
			return false;
		}
		return true;
	}
	
	private boolean onDeny(Player player) {
		return true;
	}
	
	@Override
	public void onDelete() {
		RiftInformer.sendRiftDespawn(getOwner().getWorldId(), getOwner().getObjectId());
		RiftManager.getSpawned().remove(getOwner());
		super.onDelete();
	}
	
	public boolean isMaster() {
		return isMaster;
	}
	
	public Integer getMaxEntries() {
		return maxEntries;
	}
	
	public Integer getAbyssPoint() {
		return abyssPoint;
	}
	
	public Integer getMinLevel() {
		return minLevel;
	}
	
	public Integer getMaxLevel() {
		return maxLevel;
	}
	
	public RiftEnum getRiftTemplate() {
		return riftTemplate;
	}
	
	public Npc getSlave() {
		return slave;
	}
	
	public int getUsedEntries() {
		return usedEntries;
	}
	
	public int getRemainTime() {
		return deSpawnedTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	public FastMap<Integer, Player> getPassedPlayers() {
		return passedPlayers;
	}
	
	public void syncPassed(boolean invasion) {
		usedEntries = invasion ? passedPlayers.size() : ++usedEntries;
		RiftInformer.sendRiftInfo(getWorldsList(this));
	}
	
	private int[] getWorldsList(RVController controller) {
		int first = controller.getOwner().getWorldId();
		if (controller.isMaster()) {
			return new int[]{first, controller.slaveSpawnTemplate.getWorldId()};
		}
		return new int[]{first};
	}
}