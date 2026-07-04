package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.OutpostService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArtifactSiege extends Siege<ArtifactLocation>
{
	private static final Logger log = LoggerFactory.getLogger(ArtifactSiege.class.getName());
	
	public ArtifactSiege(ArtifactLocation siegeLocation) {
		super(siegeLocation);
	}
	
	@Override
	protected void onSiegeStart() {
		initSiegeBoss();
	}
	
	@Override
	protected void onSiegeFinish() {
		unregisterSiegeBossListeners();
		deSpawnNpcs(getSiegeLocationId());
		if (isBossKilled()) {
			onCapture();
			broadcastUpdate(getSiegeLocation());
		} else {
			log.error("Artifact siege (artifactId:" + getSiegeLocationId() + ") ended without killing a boss.");
		}
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);
		DAOManager.getDAO(SiegeDAO.class).updateLocation(getSiegeLocation());
		getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.unsetInsideZoneType(ZoneType.SIEGE);
				player.getController().updateZone();
			    player.getController().updateNearbyQuests();
				if (isBossKilled() && (SiegeRace.getByRace(player.getRace()) == getSiegeLocation().getRace())) {
					QuestEngine.getInstance().onKill(new QuestEnv(getBoss(), player, 0, 0));
				}
			}
		});
		startSiege(getSiegeLocationId());
	}
	
	protected void onCapture() {
		SiegeRaceCounter wRaceCounter = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(wRaceCounter.getSiegeRace());
		Integer wLegionId = wRaceCounter.getWinnerLegionId();
		getSiegeLocation().setLegionId(wLegionId != null ? wLegionId : 0);
		if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
			//The %0 Artifact has been lost to %1.
			final AionServerPacket lRacePacket = new SM_SYSTEM_MESSAGE(1320004, getSiegeLocation().getNameAsDescriptionId(), getSiegeLocation().getRace().getDescriptionId());
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player object) {
					PacketSendUtility.sendPacket(object, lRacePacket);
				}
			});
		} else {
			String wPlayerName = "";
			final Race wRace = wRaceCounter.getSiegeRace() == SiegeRace.ELYOS ? Race.ELYOS : Race.ASMODIANS;
			Legion wLegion = wLegionId != null ? LegionService.getInstance().getLegion(wLegionId) : null;
			if (!wRaceCounter.getPlayerDamageCounter().isEmpty()) {
				Integer wPlayerId = wRaceCounter.getPlayerDamageCounter().keySet().iterator().next();
				wPlayerName = PlayerService.getPlayerName(wPlayerId);
			}
			final String winnerName = wLegion != null ? wLegion.getLegionName() : wPlayerName;
			//%1 of %0 has captured the %2 Artifact.
			final AionServerPacket wRacePacket = new SM_SYSTEM_MESSAGE(1320002, wRace.getRaceDescriptionId(), winnerName, getSiegeLocation().getNameAsDescriptionId());
			//The %0 Artifact has been lost to %1.
			final AionServerPacket lRacePacket = new SM_SYSTEM_MESSAGE(1320004, getSiegeLocation().getNameAsDescriptionId(), wRace.getRaceDescriptionId());
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, player.getRace().equals(wRace) ? wRacePacket : lRacePacket);
				}
			});
		}
		
		//Outpost Lakrum.
		if (getSiegeLocation().getLocationId() == 1511) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(121, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(121, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1512) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(122, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(122, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1513) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(123, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(123, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1514) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(124, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(124, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1515) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(125, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(125, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1516) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(126, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(126, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1517) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(127, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(127, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1518) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(128, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(128, Race.ELYOS);
			}
		} else if (getSiegeLocation().getLocationId() == 1519) {
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				return;
			} if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS) {
				OutpostService.getInstance().capture(129, Race.ASMODIANS);
			} if (getSiegeLocation().getRace() == SiegeRace.ELYOS) {
				OutpostService.getInstance().capture(129, Race.ELYOS);
			}
		}
	}
	
	@Override
	public boolean isEndless() {
		return true;
	}
	
	@Override
	public void addAbyssPoints(Player player, int abysPoints) {
	}
}