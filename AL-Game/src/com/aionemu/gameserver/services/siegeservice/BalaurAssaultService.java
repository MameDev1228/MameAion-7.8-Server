package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class BalaurAssaultService
{
	private static final BalaurAssaultService instance = new BalaurAssaultService();
	private Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private final Map<Integer, FortressAssault> fortressAssaults = new FastMap<Integer, FortressAssault>().shared();
	
	public static BalaurAssaultService getInstance() {
		return instance;
	}
	
	public void onSiegeStart(final Siege<?> siege) {
		int rvrId = siege.getSiegeLocationId();
		if (siege instanceof FortressSiege) {
			if (!calculateFortressAssault(((FortressSiege) siege).getSiegeLocation())) {
				return;
			} switch (rvrId) {
				case 1011:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <DIVINE FORTRESS> !");
						}
					});
				break;
				case 2011:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <TEMPLE OF SCALES> !");
						}
					});
				break;
				case 2021:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <ALTAR OF AVARICE> !");
						}
					});
				break;
				case 3011:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <VORGALTEM CITADEL> !");
						}
					});
				break;
				case 3021:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <CRIMSON TEMPLE> !");
						}
					});
				break;
				case 5021:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <1st APSU'S ALTAR> !");
						}
					});
				break;
				case 5022:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <2nd APSU'S ALTAR> !");
						}
					});
				break;
				case 5023:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <3rd APSU'S ALTAR> !");
						}
					});
				break;
				case 5024:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <4th APSU'S ALTAR> !");
						}
					});
				break;
				case 5025:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <5th APSU'S ALTAR> !");
						}
					});
				break;
				case 5026:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <6th APSU'S ALTAR> !");
						}
					});
				break;
				case 5027:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <7th APSU'S ALTAR> !");
						}
					});
				break;
				case 5028:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <8th APSU'S ALTAR> !");
						}
					});
				break;
				case 5029:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <9th APSU'S ALTAR> !");
						}
					});
				break;
				case 5030:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <10th APSU'S ALTAR> !");
						}
					});
				break;
				case 5031:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <11th APSU'S ALTAR> !");
						}
					});
				break;
				case 5032:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <12th APSU'S ALTAR> !");
						}
					});
				break;
				case 6011:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <SILONA FORTRESS> !");
						}
					});
				break;
				case 6021:
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: the Balaur launch an assault on <PRADETH FORTRESS> !");
						}
					});
				break;
			}
		} else if (siege instanceof ArtifactSiege) {
			if (!calculateArtifactAssault(((ArtifactSiege) siege).getSiegeLocation())) {
				return;
			}
		} else {
			return;
		}
		newAssault(siege, Rnd.get(1, 600));
		if (LoggingConfig.LOG_SIEGE) {
			log.info("[RVR/SIEGE] Balaur Assault scheduled on Siege ID: " + siege.getSiegeLocationId() + "!");
		}
	}
	
	public void onSiegeFinish(Siege<?> siege) {
		int locId = siege.getSiegeLocationId();
		if (fortressAssaults.containsKey(locId)) {
			Boolean bossIsKilled = siege.isBossKilled();
			fortressAssaults.get(locId).finishAssault(bossIsKilled);
			if (bossIsKilled && siege.getSiegeLocation().getRace().equals(SiegeRace.BALAUR)) {
				log.info("[RVR/SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] has been captured by Balaur Assault!");
				switch (locId) {
					case 1011:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <DIVINE FORTRESS> has been captured by Balaur Assault!");
							}
						});
					break;
					case 2011:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <TEMPLE OF SCALES> has been captured by Balaur Assault!");
							}
						});
					break;
					case 2021:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <ALTAR OF AVARICE> has been captured by Balaur Assault!");
							}
						});
					break;
					case 3011:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <VORGALTEM CITADEL> has been captured by Balaur Assault!");
							}
						});
					break;
					case 3021:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <CRIMSON TEMPLE> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5021:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <1st APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5022:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <2nd APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5023:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <3rd APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5024:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <4th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5025:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <5th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5026:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <6th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5027:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <7th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5028:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <8th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5029:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <9th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5030:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <10th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5031:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <11th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 5032:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys5Message(player, "\uE005", "[RVR/SIEGE]: <12th APSU'S ALTAR> has been captured by Balaur Assault!");
							}
						});
					break;
					case 6011:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: <SILONA FORTRESS> has been captured by Balaur Assault!");
							}
						});
					break;
					case 6021:
						World.getInstance().doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								PacketSendUtility.sendSys4Message(player, "\uE005", "[RVR/SIEGE]: <PRADETH FORTRESS> has been captured by Balaur Assault!");
							}
						});
					break;
				}
			} else {
				log.info("[RVR/SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] Balaur Assault finished without capture!");
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendSys6Message(player, "\uE005", "[RVR/SIEGE]: the Balaur failed to capture fortress");
					}
				});
			}
			fortressAssaults.remove(locId);
		}
	}
	
	private boolean calculateFortressAssault(FortressLocation fortress) {
		boolean isBalaurea = fortress.getWorldId() != 400070000;
		int locationId = fortress.getLocationId();
		if (fortressAssaults.containsKey(locationId)) {
			return false;
		} if (!calcFortressInfluence(isBalaurea, fortress)) {
			return false;
		}
		int count = 0;
		for (FortressAssault fa : fortressAssaults.values()) {
			if (fa.getWorldId() == fortress.getWorldId()) {
				count++;
			}
		} if (count >= (isBalaurea ? 1 : 2)) {
			return false;
		}
		return true;
	}
	
	private boolean calculateArtifactAssault(ArtifactLocation artifact) {
		return false;
	}
	
	public void startAssault(Player player, int location, int delay) {
		if (fortressAssaults.containsKey(location)) {
			PacketSendUtility.sendMessage(player, "Assault on " + location + " was already started");
			return;
		}
		newAssault(SiegeService.getInstance().getSiege(location), delay);
	}
	
	private void newAssault(Siege<?> siege, int delay) {
		if (siege instanceof FortressSiege) {
			FortressAssault assault = new FortressAssault((FortressSiege) siege);
			assault.startAssault(delay);
			fortressAssaults.put(siege.getSiegeLocationId(), assault);
		} else if (siege instanceof ArtifactSiege) {
			ArtifactAssault assault = new ArtifactAssault((ArtifactSiege) siege);
			assault.startAssault(delay);
		}
	}
	
	private boolean calcFortressInfluence(boolean isBalaurea, FortressLocation fortress) {
		SiegeRace locationRace = fortress.getRace();
		if (locationRace.equals(SiegeRace.BALAUR) || !fortress.isVulnerable()) {
			return false;
		}
		int ownedForts = 0;
		float influence;
		if (isBalaurea) {
			for (FortressLocation fl : SiegeService.getInstance().getFortresses().values()) {
				if (fl.getWorldId() != 400070000 && !fortressAssaults.containsKey(fl.getLocationId()) && fl.getRace().equals(locationRace)) {
					ownedForts++;
				}
			}
			influence = ownedForts >= 2 ? 0.25f : 0.1f;
		} else {
			influence = locationRace.equals(SiegeRace.ASMODIANS) ? Influence.getInstance().getGlobalAsmodiansInfluence() : Influence.getInstance().getGlobalElyosInfluence();
		}
		return Rnd.get() < influence * SiegeConfig.BALAUR_ASSAULT_RATE;
	}
}