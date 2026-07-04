/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.versionning.Version;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.dao.PlayerPunishmentsDAO;
import com.aionemu.gameserver.dao.WeddingDAO;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.bonus_service.PlayersBonus;
import com.aionemu.gameserver.model.bonus_service.ServiceBuff;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.model.gameobjects.player.title.Title;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.unk_60.*;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.account.MonsterCoreService;
import com.aionemu.gameserver.services.account.TransformService;
import com.aionemu.gameserver.services.account.MameTransformUnlockService;
import com.aionemu.gameserver.services.PunishmentService.PunishmentType;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.abyss.AbyssSkillService;
import com.aionemu.gameserver.services.craft.RelinquishCraftStatus;
import com.aionemu.gameserver.services.enchant.EnchantService;
import com.aionemu.gameserver.services.events.BoostEventService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.mail.MailService;
import com.aionemu.gameserver.services.ranking.SeasonRankingService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.toypet.PetService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.audit.GMService;
import com.aionemu.gameserver.utils.collections.ListSplitter;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public final class PlayerEnterWorldService
{
	private static final Logger log = LoggerFactory.getLogger("GAMECONNECTION_LOG");
	private static final String serverName = GSConfig.SERVER_NAME + " へようこそ！";
	////
	private static final String serverIntro = "不具合報告・要望はDiscordまでお願いします。";
	private static final String serverInfo;
	private static final String alInfo;
	private static final Set<Integer> pendingEnterWorld = new HashSet<Integer>();
	private static ServiceBuff serviceBuff;
	private static PlayersBonus playersBonus;
	static ScheduledFuture<?> adv = null;
	
	static {
		String infoBuffer = "お知らせ: MameAionへようこそ。\n";
		infoBuffer = infoBuffer + "=============================\n";
		infoBuffer = infoBuffer + "Info: .faction でサーバー全体チャットを利用できます。\n";
		infoBuffer = infoBuffer + "=============================\n";
		String alBuffer = "=============================\n";
		alBuffer = alBuffer + "MameAion 7.7 compatible server\n";
		alBuffer = alBuffer + "Have fun and enjoy MameAion.\n";
		if (GSConfig.SERVER_MOTD_DISPLAYREV) {
			alBuffer = alBuffer + "=============================\n";
			alBuffer = alBuffer + "Server Revision: " + String.format("%-6s", new Object[] { new Version(GameServer.class).getRevision() }) + "\n";
		}
		alBuffer = alBuffer + "=============================\n";
		serverInfo = infoBuffer;
		alInfo = alBuffer;
		infoBuffer = null;
		alBuffer = null;
	}
	
	public static final void startEnterWorld(final int objectId, final AionConnection client) {
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		Timestamp lastOnline = playerAccData.getPlayerCommonData().getLastOnline();
		if (lastOnline != null && client.getAccount().getAccessLevel() < AdminConfig.GM_LEVEL) {
			if (System.currentTimeMillis() - lastOnline.getTime() < (GSConfig.CHARACTER_REENTRY_TIME * 1000)) {
				client.sendPacket(new SM_ENTER_WORLD_CHECK((byte) 6)); // 20 sec time
				return;
			}
		}
		CharacterBanInfo cbi = client.getAccount().getPlayerAccountData(objectId).getCharBanInfo();
		if (cbi != null) {
			if (cbi.getEnd() > System.currentTimeMillis() / 1000) {
				client.close(new SM_QUIT_RESPONSE(), false);
				return;
			} else {
				DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(objectId, PunishmentType.CHARBAN);
			}
		} if (SecurityConfig.PASSKEY_ENABLE && !client.getAccount().getCharacterPasskey().isPass()) {
			showPasskey(objectId, client);
		} else {
			validateAndEnterWorld(objectId, client);
		}
	}
	
	private static final void showPasskey(final int objectId, final AionConnection client) {
		client.getAccount().getCharacterPasskey().setConnectType(ConnectType.ENTER);
		client.getAccount().getCharacterPasskey().setObjectId(objectId);
		boolean isExistPasskey = DAOManager.getDAO(PlayerPasskeyDAO.class).existCheckPlayerPasskey(client.getAccount().getId());
		if (!isExistPasskey) {
			client.sendPacket(new SM_CHARACTER_SELECT(0));
		} else {
			client.sendPacket(new SM_CHARACTER_SELECT(1));
		}
	}
	
	private static final void validateAndEnterWorld(final int objectId, final AionConnection client) {
		synchronized (pendingEnterWorld) {
			if (pendingEnterWorld.contains(objectId)) {
				log.warn("Skipping enter world " + objectId);
				return;
			}
			pendingEnterWorld.add(objectId);
		}
		int delay = 0;
		if (World.getInstance().findPlayer(objectId) != null) {
			delay = 15000;
			log.warn("Postponed enter world " + objectId);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					Player player = World.getInstance().findPlayer(objectId);
					if (player != null) {
						AuditLogger.info(player, "Duplicate player in world");
						client.close(new SM_QUIT_RESPONSE(), false);
						return;
					}
					enterWorld(client, objectId);
				}
				catch (Throwable ex) {
					log.error("Error during enter world " + objectId, ex);
				}
				finally {
					synchronized (pendingEnterWorld) {
						pendingEnterWorld.remove(objectId);
					}
				}
			}
		}, delay);
	}

	public static final void enterWorld(AionConnection client, int objectId) {
		Account account = client.getAccount();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		if (playerAccData == null) {
			return;
		}
		final Player player = PlayerService.getPlayer(objectId, account);
		if (player != null && client.setActivePlayer(player)) {
			player.setClientConnection(client);
			log.info("[MAC_AUDIT] Player " + player.getName() + " (account " + account.getName() + ") has entered world with " + client.getMacAddress() + " MAC.");
			World.getInstance().storeObject(player);
			StigmaService.onPlayerLogin(player);
			if (playerAccData.getPlayerCommonData().getLastOnline() != null) {
				long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
				PlayerCommonData pcd = player.getCommonData();
				long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
				//Time of "Crucible Spire Floor" will now be maintained up to 4 hour after disconnect.
				if (secondsOffline > 240 * 60) {
					player.getCommonData().setFloor(0);
				}
				//Time of "Dimensional Hourglass" will now be maintained up to 6 hour after disconnect.
				if (secondsOffline > 360 * 60 && !WorldPlayTimeService.isTimelessWorld(player.getWorldId())) {
					player.getCommonData().setWorldPlayTime(0);
				} if (pcd.isReadyForBerdinStar()) {
					//The level of "Berdin's Star" will now be maintained up to 4 hour after disconnect.
					if (secondsOffline > 240 * 60) {
						pcd.checkBerdinStarPercent();
						player.getCommonData().setBerdinStar(0);
					}
				} if (pcd.isReadyForAbyssFavor()) {
					//The level of "Abyss Favor" will now be maintained up to 1 hour after disconnect.
					if (secondsOffline > 60 * 60) {
						pcd.checkAbyssFavorPercent();
						player.getCommonData().setAbyssFavor(0);
					}
				} if (pcd.isReadyForReposteEnergy()) {
					pcd.updateMaxReposte();
					if (secondsOffline > 60 * 60) {
						double hours = Math.round(secondsOffline / 3600.0);
						long maxRespose = player.getCommonData().getMaxReposteEnergy();
						long reposePerHour = (maxRespose * 4) / 100;
						if (hours > 24.0) {
							hours = 24.0;
						}
						long addResposeEnergy = Math.round(hours * reposePerHour);
						if (player.getHouseOwnerId() / 10000 * 10000 == player.getWorldId()) {
							switch (player.getActiveHouse().getHouseType()) {
								case STUDIO:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.05);
								break;
								case MANSION:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.10);
								break;
								case ESTATE:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.15);
								break;
								case PALACE:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.20);
								break;
								default:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.1);
							}
						}
						pcd.addReposteEnergy(addResposeEnergy > maxRespose ? maxRespose : addResposeEnergy);
					}
				} if (System.currentTimeMillis() / 1000 - lastOnline > 300) {
					player.getCommonData().setDp(0);
				}
			}
			InstanceService.onPlayerLogin(player);
			AbyssSkillService.onEnterWorld(player);
			client.sendPacket(new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
			for (PlayerSkillEntry stigmaSkill: player.getSkillList().getStigmaSkills()) {
				client.sendPacket(new SM_SKILL_LIST(player, stigmaSkill));
			} if (player.getSkillCoolDowns() != null) {
				client.sendPacket(new SM_SKILL_COOLDOWN(player, player.getSkillCoolDowns(), false));
			} if (player.getItemCoolDowns() != null) {
				client.sendPacket(new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));
			}
			//Skin Skill.
			client.sendPacket(new SM_SKILL_SKIN(player));
			//Upgrade Arcade 4.7
			ArcadeUpgradeService.getInstance().onEnterWorld(player);
			FastList<QuestState> questList = FastList.newInstance();
			FastList<QuestState> completeQuestList = FastList.newInstance();
			for (QuestState qs : player.getQuestStateList().getAllQuestState()) {
				if (qs.getStatus() == QuestStatus.NONE && qs.getCompleteCount() == 0) {
					continue;
				} if (qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE) {
					questList.add(qs);
				} if (qs.getCompleteCount() > 0) {
					completeQuestList.add(qs);
				}
			}
			client.sendPacket(new SM_QUEST_COMPLETED_LIST(completeQuestList));
			client.sendPacket(new SM_QUEST_LIST(questList));
			client.sendPacket(new SM_TITLE_INFO(player.getCommonData().getTitleId()));
			client.sendPacket(new SM_TITLE_INFO(6, player.getCommonData().getBonusTitleId()));
			client.sendPacket(new SM_MOTION(player.getMotions().getMotions().values()));
			client.sendPacket(new SM_ENTER_WORLD_CHECK());
			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();
			byte[] houseBuddies = player.getPlayerSettings().getHouseBuddies();
			if (uiSettings != null) {
				client.sendPacket(new SM_UI_SETTINGS(uiSettings, 0));
			} if (shortcuts != null) {
				client.sendPacket(new SM_UI_SETTINGS(shortcuts, 1));
			} if (houseBuddies != null) {
				client.sendPacket(new SM_UI_SETTINGS(houseBuddies, 2));
			}
			sendItemInfos(client, player);
			playerLoggedIn(player);
			client.sendPacket(new SM_INSTANCE_INFO(player, false, player.getCurrentTeam()));
			client.sendPacket(new SM_CHANNEL_INFO(player.getPosition()));
			KiskService.getInstance().onLogin(player);
		   /**
			* If a user logs out in any hostile territory, they will be transported back to the last 
			* registered Obelisk.
			*/
			if (CustomConfig.ENABLE_RECONNECT_TO_BIND_POINT) {
				TeleportService2.moveToBindLocation(player, true);
			}
			//TeleportService2.sendSetBindPoint(player);
			World.getInstance().preSpawn(player);
			client.sendPacket(new SM_PLAYER_SPAWN(player));
			client.sendPacket(new SM_GAME_TIME());
			ProtectorConquerorService.getInstance().onProtectorConquerorLogin(player);
			//Legion Request 4.9.1
			if (player.isLegionMember()) {
				LegionService.getInstance().onLogin(player);
				if (player.getLegionMember().isBrigadeGeneral() && !player.getLegion().getJoinRequestMap().isEmpty()) {
					client.sendPacket(new SM_LEGION_REQUEST_LIST(player.getLegion().getJoinRequestMap().values()));
				}
			} else {
				DAOManager.getDAO(PlayerDAO.class).getJoinRequestState(player);
				LegionService.getInstance().handleJoinRequestGetAnswer(player);
			}
			client.sendPacket(new SM_TITLE_INFO(player));
			client.sendPacket(new SM_EMOTION_LIST((byte) 0, player.getEmotions().getEmotions()));
			SiegeService.getInstance().onPlayerLogin(player);
			AbyssPointsService.AbyssRankCheck(player);
			// TODO: Send Rift Announce Here
			client.sendPacket(new SM_PRICES());
			DisputeLandService.getInstance().onLogin(player);
			//Event Window.
			if (EventsConfig.ENABLE_EVENT_WINDOW) {
				EventWindowService.getInstance().onLogin(player);
			}
			client.sendPacket(new SM_ABYSS_RANK(player.getAbyssRank()));
			//Intro Msg.
			PacketSendUtility.sendWhiteMessage(player, serverName);
			PacketSendUtility.sendWhiteMessage(player, serverIntro);
			PacketSendUtility.sendWhiteMessage(player, serverInfo);
			PacketSendUtility.sendWhiteMessage(player, alInfo);
			//"\uE026" //Timer.
			//"\uE027" //Speaker.
			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));
			if (CustomConfig.PREMIUM_NOTIFY) {
				showPremiumAccountInfo(client, account);
			} if (player.isGM()) {
				if (AdminConfig.INVULNERABLE_GM_CONNECTION || AdminConfig.INVISIBLE_GM_CONNECTION || AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")
					|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy") || AdminConfig.VISION_GM_CONNECTION || AdminConfig.WHISPER_GM_CONNECTION) {
					PacketSendUtility.sendMessage(player, "=============================");
					if (AdminConfig.INVULNERABLE_GM_CONNECTION) {
						player.setInvul(true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invulnerable mode <<");
					} if (AdminConfig.INVISIBLE_GM_CONNECTION) {
						player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
						player.setVisualState(CreatureVisualState.HIDE3);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invisible mode <<");
					} if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")) {
						player.setAdminNeutral(3);
						player.setAdminEnmity(0);
						PacketSendUtility.sendMessage(player, ">> Connection in Neutral mode <<");
					} if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy")) {
						player.setAdminNeutral(0);
						player.setAdminEnmity(3);
						PacketSendUtility.sendMessage(player, ">> Connection in Enemy mode <<");
					} if (AdminConfig.VISION_GM_CONNECTION) {
						player.setSeeState(CreatureSeeState.SEARCH10);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Vision mode <<");
					} if (AdminConfig.WHISPER_GM_CONNECTION) {
						player.setUnWispable();
						PacketSendUtility.sendMessage(player, ">> Accepting Whisper : OFF <<");
					}
					PacketSendUtility.sendMessage(player, "=============================");
				}
			}
			PacketSendUtility.sendPacket(player, new SM_EVERGALE_CANYON(2));
			//Service Security Buff. Disabled by default for MameAion stat/damage validation.
			if (EventsConfig.ENABLE_SECURITY_SERVICE_BUFF && player.getMembership() >= 0) {
				serviceBuff = new ServiceBuff(2);
				serviceBuff.applyEffect(player, 2);
			}
			//PC Cafe Login Benefits. Disabled by default for MameAion stat/damage validation.
			if (EventsConfig.ENABLE_PC_CAFE_LOGIN_BUFF && player.getLevel() >= 76 && player.getLevel() <= 80) {
				serviceBuff = new ServiceBuff(4);
				serviceBuff.applyEffect(player, 4);
			}
			//Abyss Logon.
			if (player.getRace() == Race.ELYOS) {
				abyssLightLogon(player);
			} else if (player.getRace() == Race.ASMODIANS) {
				abyssDarkLogon(player);
			} if (!player.getEquipmentSettingList().getEquipmentSetting().isEmpty()) {
				client.sendPacket(new SM_EQUIPMENT_SETTING(player.getEquipmentSettingList().getEquipmentSetting()));
			}
			gloryPointLoose(player);
			if (EventsConfig.ENABLE_BOOST_EVENT) {
				BoostEventService.getInstance().onLogin(player);
			}
			//Color Chat.
			PacketSendUtility.sendBrightYellowMessageOnCenter(player, ColorChat.colorChat("MameAion へようこそ", "1 0 5 0"));
			LoginServerInfo(player);
			if (EventsConfig.ENABLE_F2P_AUTO_BENEFITS) {
				F2pService.getInstance().onEnterWorld(player);
			}
			//"Auto PowerShard ON"
			if (player.getEquipment().isPowerShardEquipped()) {
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_WEAPON_BOOST_BOOST_MODE_STARTED, 7000);
				player.setState(CreatureState.POWERSHARD);
				PacketSendUtility.playerSendPacketTime(player, new SM_EMOTION(player, EmotionType.POWERSHARD_ON, 0, 0), 7000);
			}
			//Alliance Packet after SetBindPoint.
			PlayerAllianceService.onPlayerLogin(player);
			if (player.isInPrison()) {
				PunishmentService.updatePrisonStatus(player);
			} if (player.isNotGatherable()) {
				PunishmentService.updateGatherableStatus(player);
			}
			PlayerGroupService.onPlayerLogin(player);
			PetService.getInstance().onPlayerLogin(player);
			MinionService.getInstance().onLoggedIn(player);
			WindyGorgeService.getInstance().onLogin(player);
			TransformService.getInstance().onPlayerLogin(player);
			MameTransformUnlockService.getInstance().onPlayerLogin(player);
			MameBurningService.onPlayerLogin(player);
			MailService.getInstance().onPlayerLogin(player);
			HousingService.getInstance().onPlayerLogin(player);
			BrokerService.getInstance().onPlayerLogin(player);
			sendMacroList(client, player);
			client.sendPacket(new SM_FRIEND_STATUS((byte)1));
			client.sendPacket(new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));
			PetitionService.getInstance().onPlayerLogin(player);
			if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
				AutoGroupService.getInstance().onPlayerLogin(player);
			} if (player.getLevel() >= 9 && CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
				ClassChangeService.showClassChangeDialog(player);
			}
			GMService.getInstance().onPlayerLogin(player);
			player.getLifeStats().updateCurrentStats();
			player.getEquipment().checkRankLimitItems();
			if (HTMLConfig.ENABLE_HTML_WELCOME) {
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("welcome.xhtml"));
			}
			player.getNpcFactions().sendDailyQuest();
			if (HTMLConfig.ENABLE_GUIDES) {
				HTMLService.onPlayerLogin(player);
			} for (StorageType st : StorageType.values()) {
				if (st == StorageType.LEGION_WAREHOUSE) {
					continue;
				}
				IStorage storage = player.getStorage(st.getId());
				if (storage != null) {
					for (Item item : storage.getItemsWithKinah()) {
						if (item.getExpireTime() > 0) {
							ExpireTimerTask.getInstance().addTask(item, player);
						}
					}
				}
			} for (Item item : player.getEquipment().getEquippedItems()) {
				if (item.getExpireTime() > 0) {
					ExpireTimerTask.getInstance().addTask(item, player);
				}
			} for (Motion motion : player.getMotions().getMotions().values()) {
				if (motion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(motion, player);
				}
			} for (Emotion emotion : player.getEmotions().getEmotions()) {
				if (emotion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(emotion, player);
				}
			} for (Title title : player.getTitleList().getTitles()) {
				if (title.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(title, player);
				}
			} if (player.getHouseRegistry() != null) {
				for (HouseObject<?> obj : player.getHouseRegistry().getObjects()) {
					if (obj.getPersistentState() != PersistentState.DELETED) {
						if (obj.getObjectTemplate().getUseDays() > 0) {
							ExpireTimerTask.getInstance().addTask(obj, player);
						}
					}
				}
			}
			player.getController().addTask(TaskId.PLAYER_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new GeneralUpdateTask(player.getObjectId()), PeriodicSaveConfig.PLAYER_GENERAL * 1000, PeriodicSaveConfig.PLAYER_GENERAL * 1000));
			player.getController().addTask(TaskId.INVENTORY_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemUpdateTask(player.getObjectId()), PeriodicSaveConfig.PLAYER_ITEMS * 1000, PeriodicSaveConfig.PLAYER_ITEMS * 1000));
			SurveyService.getInstance().showAvailable(player);
			if (EventsConfig.ENABLE_EVENT_SERVICE) {
				EventService.getInstance().onPlayerLogin(player);
			}
			RelinquishCraftStatus.removeExcessCraftStatus(player, false);
			player.setPartnerId(DAOManager.getDAO(WeddingDAO.class).loadPartnerId(player));
			EnchantService.GloryShieldSkill(player);
			ShugoSweepService.getInstance().onLogin(player);
			LunaShopService.getInstance().onLogin(player);
			MonsterCoreService.getInstance().onLogin(player);
			LoginEventService.getInstance().onLogin(player);
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			SeasonRankingService.getInstance().onPlayerLogin(player);
			//Check Mission Step 7.x
			QuestUpdateService.getInstance().onQuestUpdateLogin(player);
			//World Play Time 7.x
			WorldPlayTimeService.ensureTimelessTime(player);
			if (player.getCommonData().getWorldPlayTime() >= 1) {
				client.sendPacket(new SM_WORLD_PLAYTIME(player));
			}
			//Player Fame 7.x
			PlayerFameService.getInstance().onPlayerLogin(player);
			WorldPlayTimeService.getInstance().onEnterWorld(player);
			LumielTransformService.getInstance().onLogin(player);
			PlayerCollectionService.getInstance().onLogin(player);
			//6.x + unkPacket
			client.sendPacket(new SM_UNK_7E(0));
			client.sendPacket(new SM_UNK_7E(1));
			client.sendPacket(new SM_UNK_131());
			client.sendPacket(new SM_ABYSS_POINTS());
			client.sendPacket(new SM_ESTIMA_BUFF());
			client.sendPacket(new SM_UNK_106());
			doTest(player);
		} else {
			log.info("[DEBUG] Enter World" + objectId + ", Player: " + player);
		}
	}
	
	/**
	 * [Glory Point Loose]
	 */
	public static final void gloryPointLoose(Player player) {
		if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR1_OFFICER.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 400));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR2_OFFICER.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 450));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR3_OFFICER.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 600));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR4_OFFICER.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 800));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR5_OFFICER.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 1500));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.GENERAL.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 2000));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.GREAT_GENERAL.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 2500));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.COMMANDER.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 3000));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.SUPREME_COMMANDER.getId()) {
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), 4000));
		}
		//A set amount of Glory Points are deducted every day based on your Abyss Rank.
		PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_LOSE_COMMON, 20000);
	}
	
	/**
	 * [Abyss Logon] 4.9
	 */
	public static final void abyssLightLogon(final Player player) {
		if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.SUPREME_COMMANDER.getId()) {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player players) {
					//Elyos Governor "Player Name" has graced Atreia.
					PacketSendUtility.sendPacket(players, new SM_SYSTEM_MESSAGE(1403134, player.getName()));
				}
			});
		}
	}
	public static final void abyssDarkLogon(final Player player) {
		if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.SUPREME_COMMANDER.getId()) {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player players) {
					//Asmodian Governor "Player Name" has graced Atreia.
					PacketSendUtility.sendPacket(players, new SM_SYSTEM_MESSAGE(1403135, player.getName()));
				}
			});
		}
	}
	
	private static void sendItemInfos(AionConnection client, Player player) {
		int questExpands = player.getQuestExpands();
		int npcExpands = player.getNpcExpands();
		player.getInventory().setLimit(StorageType.CUBE.getLimit() + (questExpands + npcExpands) * 9);
		player.getWarehouse().setLimit(StorageType.REGULAR_WAREHOUSE.getLimit() + player.getWarehouseSize() * 8);
		Storage inventory = player.getInventory();
		List<Item> allItems = new ArrayList<Item>();
		if (inventory.getKinah() == 0) {
			inventory.increaseKinah(0);
		}
		allItems.add(inventory.getKinahItem());
		allItems.addAll(player.getEquipment().getEquippedItems());
		allItems.addAll(inventory.getItems());
		client.sendPacket(new SM_INVENTORY_INFO(true, new ArrayList<Item>(0), npcExpands, questExpands, player));
		ListSplitter<Item> splitter = new ListSplitter<Item>(allItems, 10);
		while (!splitter.isLast()) {
			client.sendPacket(new SM_INVENTORY_INFO(false, splitter.getNext(), npcExpands, questExpands, player));
		}
		client.sendPacket(new SM_INVENTORY_INFO(false, new ArrayList<Item>(0), npcExpands, questExpands, player));
		client.sendPacket(new SM_STATS_INFO(player));
		client.sendPacket(SM_CUBE_UPDATE.stigmaSlots(player.getCommonData().getAdvencedStigmaSlotSize()));
	}
	
	private static void sendMacroList(AionConnection client, Player player) {
		client.sendPacket(new SM_MACRO_LIST(player, false));
		if (player.getMacroList().getSize() > 7) {
			client.sendPacket(new SM_MACRO_LIST(player, true));
		}
	}
	
	private static void playerLoggedIn(Player player) {
		log.info("Player logged in: " + player.getName() + " Account: " + player.getClientConnection().getAccount().getName());
		player.getCommonData().setOnline(true);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, true);
		player.onLoggedIn();
		player.setOnlineTime();
	}
	
	private static void showPremiumAccountInfo(AionConnection client, Account account) {
		byte membership = account.getMembership();
		if (membership > 0) {
			String accountType = "";
			switch (account.getMembership()) {
			    case 1:
				    accountType = "PREMIUM";
			    break;
			    case 2:
				    accountType = "VIP";
			    break;
			}
			client.sendPacket(new SM_MESSAGE(0, null, "Your account is " + accountType, ChatType.GOLDEN_YELLOW));
		}
	}
	
	public static final void LoginServerInfo(Player player) {
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "============================"));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "[color:Info：;0 1 0] MameAion へようこそ"));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "[color:Info：;0 1 0] 不具合報告・要望はDiscordまでお願いします。"));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "[color:Info：;0 1 0] このウィンドウは案内専用です。入力しないでください。"));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "============================"));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "[color:Info：;0 1 0] アカウント情報"));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "[color:Info：;0 1 0][color:トールポイント：;0 1 0] " + player.getClientConnection().getAccount().getToll()));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "[color:Info：;0 1 0][color:ルナポイント：;0 1 0] " + player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "============================"));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300564, "[color:Info：;0 1 0][color:キャラクター名：;0 1 0] " + player.getName()));
	}

	public static void doTest(Player player) {
		AchievementService.getInstance().onEnterWorld(player);
	}
}