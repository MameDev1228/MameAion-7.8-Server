package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.*;
import com.aionemu.gameserver.model.instance.playerreward.*;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import javolution.util.FastList;

@SuppressWarnings("rawtypes")
public class SM_INSTANCE_SCORE extends AionServerPacket {
    private final Logger log = LoggerFactory.getLogger(SM_INSTANCE_SCORE.class);

    private int type;
    private int mapId;
    private int instanceTime;
    private InstanceScoreType instanceScoreType;
    private InstanceReward instanceReward;
    private List<Player> players;
    private Integer object;
    private int PlayerStatus = 0;
    private int PlayerRaceId = 0;
    private Player player;
    private Player opponent;

    public SM_INSTANCE_SCORE(int type, int instanceTime, InstanceReward instanceReward, Integer object, int PlayerStatus, int PlayerRaceId) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.object = object;
        this.PlayerStatus = PlayerStatus;
        this.PlayerRaceId = PlayerRaceId;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

    public SM_INSTANCE_SCORE(int type, int instanceTime, InstanceReward instanceReward, Integer object) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.object = object;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

    public SM_INSTANCE_SCORE(int instanceTime, InstanceReward instanceReward, List<Player> players) {
        this.mapId = instanceReward.getMapId();
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.players = players;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

    public SM_INSTANCE_SCORE(int type, int instanceTime, InstanceReward instanceReward, List<Player> players, boolean tis) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.players = players;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

    public SM_INSTANCE_SCORE(InstanceReward instanceReward, InstanceScoreType instanceScoreType) {
        this.mapId = instanceReward.getMapId();
        this.instanceReward = instanceReward;
        this.instanceScoreType = instanceScoreType;
    }

    public SM_INSTANCE_SCORE(InstanceReward instanceReward) {
        this.mapId = instanceReward.getMapId();
        this.instanceReward = instanceReward;
        this.instanceScoreType = instanceReward.getInstanceScoreType();
    }

    public SM_INSTANCE_SCORE(int type, Player player, int instanceTime, InstanceReward instanceReward) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.player = player;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

    public SM_INSTANCE_SCORE(int type, Player player, Player opponent, int instanceTime, InstanceReward instanceReward) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.player = player;
        this.opponent = opponent;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

    public SM_INSTANCE_SCORE(int mapId, int instanceTime, InstanceScoreType scoreType) {
        this.mapId = mapId;
        this.instanceTime = instanceTime;
        instanceScoreType = scoreType;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void writeImpl(AionConnection con) {
        int playerCount = 0;
        Player owner = con.getActivePlayer();
        Integer ownerObject = owner.getObjectId();
        writeD(mapId);
        writeD(instanceTime);
        writeD(instanceScoreType.getId());
        switch (mapId) {
            case 300450000: //Arena Of Harmony
                HarmonyArenaReward harmonyArena = (HarmonyArenaReward) instanceReward;
                if (object == null) {
                    object = ownerObject;
                }
                HarmonyGroupReward harmonyGroupReward = harmonyArena.getHarmonyGroupReward(object);
                writeC(type);
                switch (type) {
                    case 2:
                        writeD(0);
                        writeD(harmonyArena.getRound());
                    break;
                    case 3:
                        writeD(harmonyGroupReward.getOwner() - 1);
                        writeS(harmonyGroupReward.getAGPlayer(object).getName(), 52);
                        writeD(harmonyGroupReward.getOwner());
                        writeD(object);
                    break;
                    case 4:
                        writeD(harmonyArena.getPlayerReward(object).getRemaningTime());
                        writeD(0);
                        writeD(0);
                        writeD(object);
                    break;
                    case 5:
                        writeD(harmonyGroupReward.getBasicAP());
                        writeD(harmonyGroupReward.getBasicGP());
                        writeD(harmonyGroupReward.getScoreAP());
                        writeD(harmonyGroupReward.getScoreGP());
                        writeD(harmonyGroupReward.getRankingAP());
                        writeD(harmonyGroupReward.getRankingGP());
                        writeD(0);
						writeD(harmonyGroupReward.getBasicKinah());
                        writeD(harmonyGroupReward.getScoreKinah());
                        writeD(harmonyGroupReward.getRankingKinah());
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD((int) harmonyGroupReward.getParticipation() * 100);
                        writeD(harmonyGroupReward.getPoints());
                    break;
                    case 6:
                        writeD(3);
                        writeD(harmonyArena.getCapPoints());
                        writeD(3);
                        writeD(1);
                        writeD(harmonyArena.getBuffId());
                        writeD(2);
                        writeD(0);
                        writeD(harmonyArena.getRound());
                        FastList<HarmonyGroupReward> groups = harmonyArena.getHarmonyGroupInside();
                        writeC(groups.size());
                        for (HarmonyGroupReward group : groups) {
                            writeC(harmonyArena.getRank(group.getPoints()));
                            writeD(group.getPvPKills());
                            writeD(group.getPoints());
                            writeD(group.getOwner());
                            FastList<Player> members = harmonyArena.getPlayersInside(group);
                            writeC(members.size());
                            int i = 0;
                            for (Player p : members) {
                                PvPArenaPlayerReward rewardedPlayer = harmonyArena.getPlayerReward(p.getObjectId());
                                writeD(0);
                                writeD(rewardedPlayer.getRemaningTime());
                                writeD(0);
                                writeC(group.getOwner());
                                writeC(i);
                                writeH(0);
                                writeS(p.getName(), 52);
                                writeD(p.getObjectId());
                                i++;
                            }
                        }
                    break;
                    case 10:
                        writeC(harmonyArena.getRank(harmonyGroupReward.getPoints()));
                        writeD(harmonyGroupReward.getPvPKills());
                        writeD(harmonyGroupReward.getPoints());
                        writeD(harmonyGroupReward.getOwner());
                    break;
                }
            break;
            case 301650000: //Ashunatal Dredgion.
                fillTableWithGroup(Race.ELYOS);
                fillTableWithGroup(Race.ASMODIANS);
                DredgionReward dredgionReward = (DredgionReward) instanceReward;
                int elyosScore = dredgionReward.getPointsByRace(Race.ELYOS).intValue();
                int asmosScore = dredgionReward.getPointsByRace(Race.ASMODIANS).intValue();
                writeD(instanceScoreType.isEndProgress() ? (asmosScore > elyosScore ? 1 : 0) : 255);
                writeD(elyosScore);
                writeD(asmosScore);
                writeH(0);
                for (DredgionReward.DredgionRooms dredgionRoom : dredgionReward.getDredgionRooms()) {
                    writeC(dredgionRoom.getState());
                }
            break;
			case 301120000: //Kamar Battlefield 7.x
				KamarBattlefieldReward kbr = (KamarBattlefieldReward) instanceReward;
				if (object == null) {
					object = ownerObject;
				}
				KamarBattlefieldPlayerReward kbpr = kbr.getPlayerReward(object);
				writeC(type);
			    switch (type) {
					case 2:
						InstanceScoreType isc1 = kbr.getInstanceScoreType();
                        writeD(isc1 == instanceScoreType.START_PROGRESS ? 0 : (isc1 == instanceScoreType.END_PROGRESS) ? 2 : 0);
					break;
					case 3:
						writeD(15);
						writeD(PlayerStatus);
						writeD(object);
						writeD(PlayerRaceId);
					break;
					case 4:
						writeD(15);
						writeD(PlayerStatus);
						writeD(object);
					break;
					case 5:
						writeD((int) kbpr.getParticipation());
						writeD(kbpr.getRewardExp());
						writeD(kbpr.getBonusExp());
						writeD(kbpr.getRewardAp());
						writeD(kbpr.getBonusAp());
						writeD(kbpr.getRewardGp());
						writeD(kbpr.getBonusGp());
						//Reward.
						writeD(kbpr.getKamarStigmaBox());
						writeQ(kbpr.getRewardCount());
					break;
					case 6:
						int counter = 0;
						writeD(100);
						for (Player player : players) {
							if (player.getRace() != Race.ELYOS) {
								continue;
							}
							writeD(15);
							writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
							writeD(player.getObjectId());
							counter++;
						} if (counter < 12) {
							writeB(new byte[12 * (12 - counter)]);
						}
						counter = 0;
						for (Player player : players) {
							if (player.getRace() != Race.ASMODIANS) {
								continue;
							}
							writeD(15);
							writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
							writeD(player.getObjectId());
							counter++;
						} if (counter < 12) {
							writeB(new byte[12 * (12 - counter)]);
						}
						writeC(0);
						writeD(kbr.getPvpKillsByRace(Race.ELYOS).intValue());
						writeD(kbr.getPointsByRace(Race.ELYOS).intValue());
						writeD(0);
						writeD((kbr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
						writeC(0);
						writeD(kbr.getPvpKillsByRace(Race.ASMODIANS).intValue());
						writeD(kbr.getPointsByRace(Race.ASMODIANS).intValue());
						writeD(1);
						writeD((kbr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
					break;
					case 7:
						kamarBattlefieldTable(Race.ELYOS);
						kamarBattlefieldTable(Race.ASMODIANS);
					break;
					case 8:
						writeD(object);
					break;
					case 10:
						writeC(0);
						writeD(kbr.getPvpKillsByRace(kbpr.getRace()).intValue());
						writeD(kbr.getPointsByRace(kbpr.getRace()).intValue());
						writeD(kbpr.getRace().getRaceId());
						writeD(object);
					break;
					case 11:
						int TeamScore1 = kbr.getPointsByRace(kbpr.getRace()).intValue();
						int OppositeTeamScore1 = kbr.getPointsByRace(getOppositeRace(kbpr.getRace())).intValue();
						writeC(0);
						writeD(kbr.getPvpKillsByRace(kbpr.getRace()).intValue());
						writeD(TeamScore1);
						writeD(kbpr.getRace().getRaceId());
						writeD(TeamScore1 == OppositeTeamScore1 ? 65535 : 0);
					break;
				}
			break;
            case 301310000: //Idgel Dome 4.x
                IdgelDomeReward idr = (IdgelDomeReward) instanceReward;
                if (object == null) {
                    object = ownerObject;
                }
                IdgelDomePlayerReward idpr = idr.getPlayerReward(object);
                writeC(type);
                switch (type) {
                    case 2:
                        InstanceScoreType isc2 = idr.getInstanceScoreType();
                        writeD(isc2 == instanceScoreType.START_PROGRESS ? 0 : (isc2 == instanceScoreType.END_PROGRESS) ? 2 : 0);
                    break;
                    case 3:
                        writeD(15);
                        writeD(PlayerStatus);
                        writeD(object);
                        writeD(PlayerRaceId);
                    break;
                    case 4:
                        writeD(15);
                        writeD(PlayerStatus);
                        writeD(object);
                    break;
                    case 5:
                        writeD((int) idpr.getParticipation());
                        writeD(idpr.getRewardExp());
                        writeD(idpr.getBonusExp());
                        writeD(idpr.getRewardAp());
                        writeD(idpr.getBonusAp());
                        writeD(idpr.getRewardGp());
                        writeD(idpr.getBonusGp());
                        //Reward.
                        writeD(idpr.getWarriorMedalBundle());
                        writeQ(idpr.getRewardCount());
                        writeD(idpr.getLegendaryRidium());
                        writeQ(idpr.getRewardCount());
						writeD(idpr.getBattlefieldMinionBundle());
                        writeQ(idpr.getRewardCount());
						writeD(idpr.getBattlefieldPvPEnchantmentStoneBundle());
                        writeQ(idpr.getRewardCount());
                    break;
                    case 6:
                        int counter = 0;
                        writeD(100);
                        for (Player player : players) {
                            if (player.getRace() != Race.ELYOS) {
                                continue;
                            }
                            writeD(15);
                            writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                            writeD(player.getObjectId());
                            counter++;
                        } if (counter < 6) {
                            writeB(new byte[12 * (6 - counter)]);
                        }
                        counter = 0;
                        for (Player player : players) {
                            if (player.getRace() != Race.ASMODIANS) {
                                continue;
                            }
                            writeD(15);
                            writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                            writeD(player.getObjectId());
                            counter++;
                        } if (counter < 6) {
                            writeB(new byte[12 * (6 - counter)]);
                        }
                        writeC(0);
                        writeD(idr.getPvpKillsByRace(Race.ELYOS).intValue());
                        writeD(idr.getPointsByRace(Race.ELYOS).intValue());
                        writeD(0);
                        writeD((idr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                        writeC(0);
                        writeD(idr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                        writeD(idr.getPointsByRace(Race.ASMODIANS).intValue());
                        writeD(1);
                        writeD((idr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                    break;
                    case 7:
                        idgelDomeTable(Race.ELYOS);
                        idgelDomeTable(Race.ASMODIANS);
                    break;
                    case 8:
                        writeD(object);
                    break;
                    case 10:
                        writeC(0);
                        writeD(idr.getPvpKillsByRace(idpr.getRace()).intValue());
                        writeD(idr.getPointsByRace(idpr.getRace()).intValue());
                        writeD(idpr.getRace().getRaceId());
                        writeD(object);
                    break;
                    case 11:
                        int TeamScore2 = idr.getPointsByRace(idpr.getRace()).intValue();
                        int OppositeTeamScore2 = idr.getPointsByRace(getOppositeRace(idpr.getRace())).intValue();
                        writeC(0);
                        writeD(idr.getPvpKillsByRace(idpr.getRace()).intValue());
                        writeD(TeamScore2);
                        writeD(idpr.getRace().getRaceId());
                        writeD(TeamScore2 == OppositeTeamScore2 ? 65535 : 0);
                    break;
                }
            break;
            case 302350000: //Evergale Canyon 5.x
                EvergaleCanyonReward ecr = (EvergaleCanyonReward) instanceReward;
                if (object == null) {
                    object = ownerObject;
                }
                EvergaleCanyonPlayerReward ecpr = ecr.getPlayerReward(object);
                writeC(type);
                switch (type) {
                    case 2:
                        InstanceScoreType isc3 = ecr.getInstanceScoreType();
                        writeD(isc3 == instanceScoreType.START_PROGRESS ? 0 : (isc3 == instanceScoreType.END_PROGRESS) ? 2 : 0);
                    break;
                    case 3:
                        writeD(15);
                        writeD(PlayerStatus);
                        writeD(object);
                        writeD(PlayerRaceId);
                    break;
                    case 4:
                        writeD(15);
                        writeD(PlayerStatus);
                        writeD(object);
                    break;
                    case 5:
                        writeD((int) ecpr.getParticipation());
                        writeD(ecpr.getRewardExp());
                        writeD(ecpr.getBonusExp());
                        writeD(ecpr.getRewardAp());
                        writeD(ecpr.getBonusAp());
                        writeD(ecpr.getRewardGp());
                        writeD(ecpr.getBonusGp());
                        //Reward.
                        writeD(ecpr.getWarriorMedalBundle());
                        writeQ(ecpr.getRewardCount());
                        writeD(ecpr.getLegendaryRidium());
                        writeQ(ecpr.getRewardCount());
						writeD(ecpr.getBattlefieldMinionBundle());
                        writeQ(ecpr.getRewardCount());
						writeD(ecpr.getBattlefieldPvPEnchantmentStoneBundle());
                        writeQ(ecpr.getRewardCount());
                    break;
                    case 6:
                        int counter = 0;
                        writeD(100);
                        for (Player player : players) {
                            if (player.getRace() != Race.ELYOS) {
                                continue;
                            }
                            writeD(15);
                            writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                            writeD(player.getObjectId());
                            counter++;
                        } if (counter < 96) {
                            writeB(new byte[12 * (96 - counter)]);
                        }
                        counter = 0;
                        for (Player player : players) {
                            if (player.getRace() != Race.ASMODIANS) {
                                continue;
                            }
                            writeD(15);
                            writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                            writeD(player.getObjectId());
                            counter++;
                        } if (counter < 96) {
                            writeB(new byte[12 * (96 - counter)]);
                        }
                        writeC(0);
                        writeD(ecr.getPvpKillsByRace(Race.ELYOS).intValue());
                        writeD(ecr.getPointsByRace(Race.ELYOS).intValue());
                        writeD(0);
                        writeD((ecr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                        writeC(0);
                        writeD(ecr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                        writeD(ecr.getPointsByRace(Race.ASMODIANS).intValue());
                        writeD(1);
                        writeD((ecr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                    break;
                    case 7:
                        evergaleCanyonTable(Race.ELYOS);
                        evergaleCanyonTable(Race.ASMODIANS);
                    break;
                    case 8:
                        writeD(object);
                    break;
                    case 10:
                        writeC(0);
                        writeD(ecr.getPvpKillsByRace(ecpr.getRace()).intValue());
                        writeD(ecr.getPointsByRace(ecpr.getRace()).intValue());
                        writeD(ecpr.getRace().getRaceId());
                        writeD(object);
                    break;
                    case 11:
                        int TeamScore3 = ecr.getPointsByRace(ecpr.getRace()).intValue();
                        int OppositeTeamScore3 = ecr.getPointsByRace(getOppositeRace(ecpr.getRace())).intValue();
                        writeC(0);
                        writeD(ecr.getPvpKillsByRace(ecpr.getRace()).intValue());
                        writeD(TeamScore3);
                        writeD(ecpr.getRace().getRaceId());
                        writeD(TeamScore3 == OppositeTeamScore3 ? 65535 : 0);
                    break;
                }
            break;
			case 302530000: //Illumiel Brawl 6.x
                IllumielBattlefieldReward ibr = (IllumielBattlefieldReward) instanceReward;
                if (object == null) {
                    object = ownerObject;
                }
                IllumielBattlefieldPlayerReward ibpr = ibr.getPlayerReward(object);
                writeC(type);
                switch (type) {
                    case 2:
                        InstanceScoreType isc4 = ibr.getInstanceScoreType();
                        writeD(isc4 == instanceScoreType.START_PROGRESS ? 0 : (isc4 == instanceScoreType.END_PROGRESS) ? 2 : 0);
                    break;
                    case 3:
                        writeD(15);
                        writeD(PlayerStatus);
                        writeD(object);
                        writeD(PlayerRaceId);
                    break;
                    case 4:
                        writeD(15);
                        writeD(PlayerStatus);
                        writeD(object);
                    break;
                    case 5:
                        writeD((int) ibpr.getParticipation());
                        writeD(ibpr.getRewardExp());
                        writeD(ibpr.getBonusExp());
                        writeD(ibpr.getRewardAp());
                        writeD(ibpr.getBonusAp());
                        writeD(ibpr.getRewardGp());
                        writeD(ibpr.getBonusGp());
                        //Reward.
                        writeD(ibpr.getWarriorMedalBundle());
                        writeQ(ibpr.getRewardCount());
                        writeD(ibpr.getLegendaryRidium());
                        writeQ(ibpr.getRewardCount());
						writeD(ibpr.getBattlefieldMinionBundle());
                        writeQ(ibpr.getRewardCount());
						writeD(ibpr.getBattlefieldPvPEnchantmentStoneBundle());
                        writeQ(ibpr.getRewardCount());
                    break;
                    case 6:
                        int counter = 0;
                        writeD(100);
                        for (Player player : players) {
                            if (player.getRace() != Race.ELYOS) {
                                continue;
                            }
                            writeD(15);
                            writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                            writeD(player.getObjectId());
                            counter++;
                        } if (counter < 6) {
                            writeB(new byte[12 * (6 - counter)]);
                        }
                        counter = 0;
                        for (Player player : players) {
                            if (player.getRace() != Race.ASMODIANS) {
                                continue;
                            }
                            writeD(15);
                            writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                            writeD(player.getObjectId());
                            counter++;
                        } if (counter < 6) {
                            writeB(new byte[12 * (6 - counter)]);
                        }
                        writeC(0);
                        writeD(ibr.getPvpKillsByRace(Race.ELYOS).intValue());
                        writeD(ibr.getPointsByRace(Race.ELYOS).intValue());
                        writeD(0);
                        writeD((ibr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                        writeC(0);
                        writeD(ibr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                        writeD(ibr.getPointsByRace(Race.ASMODIANS).intValue());
                        writeD(1);
                        writeD((ibr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                    break;
                    case 7:
                        illumielBattlefieldTable(Race.ELYOS);
                        illumielBattlefieldTable(Race.ASMODIANS);
                    break;
                    case 8:
                        writeD(object);
                    break;
                    case 10:
                        writeC(0);
                        writeD(ibr.getPvpKillsByRace(ibpr.getRace()).intValue());
                        writeD(ibr.getPointsByRace(ibpr.getRace()).intValue());
                        writeD(ibpr.getRace().getRaceId());
                        writeD(object);
                    break;
                    case 11:
                        int TeamScore4 = ibr.getPointsByRace(ibpr.getRace()).intValue();
                        int OppositeTeamScore4 = ibr.getPointsByRace(getOppositeRace(ibpr.getRace())).intValue();
                        writeC(0);
                        writeD(ibr.getPvpKillsByRace(ibpr.getRace()).intValue());
                        writeD(TeamScore4);
                        writeD(ibpr.getRace().getRaceId());
                        writeD(TeamScore4 == OppositeTeamScore4 ? 65535 : 0);
                    break;
                }
            break;
			case 300480000: //Unstable Danuar Mysticarium.
                for (DanuarMysticariumPlayerReward playerReward : (FastList<DanuarMysticariumPlayerReward>) instanceReward.getInstanceRewards()) {
					DanuarMysticariumReward dmr = (DanuarMysticariumReward) instanceReward;
					writeD(dmr.getPoints());
					writeD(dmr.getNpcKills());
					writeD(0);
					writeD(dmr.getRank());
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
				}
            break;
            case 301400000: //The Shugo Emperor's Vault.
                for (ShugoEmperorVaultPlayerReward playerReward : (FastList<ShugoEmperorVaultPlayerReward>) instanceReward.getInstanceRewards()) {
                    ShugoEmperorVaultReward sevr = (ShugoEmperorVaultReward) instanceReward;
                    writeD(sevr.getPoints());
                    writeD(sevr.getNpcKills());
                    writeD(0);
                    writeD(sevr.getRank());
                    writeD(0);
                    writeD(0);
                    writeD(instanceScoreType.isEndProgress() ? playerReward.getRustedVaultKey() : 0);
                    writeD(instanceScoreType.isEndProgress() ? playerReward.getRustedVaultKey() : 0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                }
            break;
            case 301630000: //Contaminated Underpath 5.x
                for (ContaminatedUnderpathPlayerReward playerReward : (FastList<ContaminatedUnderpathPlayerReward>) instanceReward.getInstanceRewards()) {
                    ContaminatedUnderpathReward cur = (ContaminatedUnderpathReward) instanceReward;
                    writeD(cur.getPoints());
                    writeD(cur.getNpcKills());
                    writeD(0);
                    writeD(cur.getRank());
                    writeD(0);
                    writeD(playerReward.getScoreAP());
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    if (cur.getPoints() >= 50) {
                        writeD(188055664); //Contaminated Underpath Special Pouch.
                        writeD(playerReward.getContaminatedUnderpathSpecialPouch());
                        writeD(188055599); //Contaminated Highest Reward Bundle.
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    } if (cur.getPoints() >= 549000) {
                        writeD(188055598); //Contaminated Premium Reward Bundle.
                        writeD(playerReward.getContaminatedPremiumRewardBundle());
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    }
                }
            break;
			case 301632000: //Perilous Contaminated Underpath 5.x
				for (IDEventDefPlayerReward playerReward : (FastList<IDEventDefPlayerReward>) instanceReward.getInstanceRewards()) {
					IDEventDefReward def = (IDEventDefReward) instanceReward;
					writeD(def.getPoints());
					writeD(def.getNpcKills());
					writeD(0);
					writeD(def.getRank());
					writeD(0);
					writeD(playerReward.getScoreAP());
					writeD(0);
					writeD(0);
					writeD(0);
					if (def.getPoints() >= 220000) {
						writeD(188071413); //Prestige Case.
						writeD(playerReward.getPrestigeCase());
					} else {
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
					} if (def.getPoints() >= 500000) {
						writeD(188071413); //Prestige Case.
						writeD(playerReward.getPrestigeCase());
					} else {
						writeD(0);
						writeD(0);
						writeD(0);
					}
				}
			break;
            case 301640000: //Secret Munitions Factory 5.x
                for (SecretMunitionsFactoryPlayerReward playerReward : (FastList<SecretMunitionsFactoryPlayerReward>) instanceReward.getInstanceRewards()) {
                    SecretMunitionsFactoryReward smfr = (SecretMunitionsFactoryReward) instanceReward;
                    writeD(smfr.getPoints());
                    writeD(smfr.getNpcKills());
                    writeD(0);
                    writeD(smfr.getRank());
                    writeD(0);
                    writeD(playerReward.getScoreAP());
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    if (smfr.getPoints() >= 878600) {
                        writeD(188055648); //Mechaturerk's Special Treasure Box.
                        writeD(playerReward.getMechaturerkSpecialTreasureBox());
                        writeD(188055647); //Mechaturerk's Normal Treasure Chest.
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    } if (smfr.getPoints() >= 878600) {
                        writeD(188055475); //Mechaturerk's Secret Box.
                        writeD(playerReward.getMechaturerkSecretBox());
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    }
                }
            break;
			case 301750000: //Wicked Gracheni's Vault 7.x
                for (WickedGracheniVaultPlayerReward playerReward : (FastList<WickedGracheniVaultPlayerReward>) instanceReward.getInstanceRewards()) {
                    WickedGracheniVaultReward wgvr = (WickedGracheniVaultReward) instanceReward;
                    writeD(wgvr.getPoints());
                    writeD(wgvr.getNpcKills());
                    writeD(0);
                    writeD(wgvr.getRank());
                    writeD(0);
                    writeD(playerReward.getScoreAP());
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    if (wgvr.getPoints() >= 54000) {
                        writeD(185001062); //Gracheni's Treasure Chest Key.
                        writeD(playerReward.getGracheniTreasureChestKey());
						writeD(186020057); //[Event] Ranger's Mark.
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    } if (wgvr.getPoints() >= 878600) {
                        writeD(185001062); //Gracheni's Treasure Chest Key.
                        writeD(playerReward.getGracheniTreasureChestKey());
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    }
                }
            break;
            case 302460000: //Qubrinerk's Cubic Lab 6.x
                for (QubrinerkPlayerReward playerReward : (FastList<QubrinerkPlayerReward>) instanceReward.getInstanceRewards()) {
					QubrinerkReward qclr = (QubrinerkReward) instanceReward;
					writeD(qclr.getPoints());
					writeD(qclr.getNpcKills());
					writeD(0);
					writeD(qclr.getRank());
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
				}
            break;
			case 302510000: //The Veilenthrone 6.x
				for (VeilenthronePlayerReward playerReward : (FastList<VeilenthronePlayerReward>) instanceReward.getInstanceRewards()) {
					VeilenthroneReward veilenthrone = (VeilenthroneReward) instanceReward;
					writeD(veilenthrone.getPoints());
					writeD(veilenthrone.getNpcKills());
					writeD(0);
					writeD(veilenthrone.getRank());
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
				}
			break;
			case 302651000: //The Red Cellar 7.x
                for (RedCellarPlayerReward playerReward : (FastList<RedCellarPlayerReward>) instanceReward.getInstanceRewards()) {
                    RedCellarReward rcr = (RedCellarReward) instanceReward;
                    writeD(rcr.getPoints());
                    writeD(rcr.getNpcKills());
                    writeD(0);
                    writeD(rcr.getRank());
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    if (rcr.getPoints() >= 1200) {
                        writeD(188074458); //Shining Gemstone Shard Box.
                        writeD(playerReward.getShiningGemstoneShardBox());
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    } if (rcr.getPoints() >= 1400) {
                        writeD(188074459); //Dazzling Gemstone Shard Box.
                        writeD(playerReward.getDazzlingGemstoneShardBox());
                    } else {
                        writeD(0);
                        writeD(0);
                        writeD(0);
                    }
                }
            break;
			case 302710000: //Shattered Abyssal Splinter 7.x
				for (ShatteredAbyssalSplinterPlayerReward playerReward : (FastList<ShatteredAbyssalSplinterPlayerReward>) instanceReward.getInstanceRewards()) {
					ShatteredAbyssalSplinterReward sasr = (ShatteredAbyssalSplinterReward) instanceReward;
					writeD(sasr.getPoints());
					writeD(sasr.getNpcKills());
					writeD(0);
					writeD(sasr.getRank());
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
				}
			break;
            case 300360000: //Arena Of Discipline.
                PvPArenaReward arenaReward = (PvPArenaReward) instanceReward;
                PvPArenaPlayerReward rewardedPlayer = arenaReward.getPlayerReward(ownerObject);
                int rank, points;
                boolean isRewarded = arenaReward.isRewarded();
                for (Player player : players) {
                    InstancePlayerReward reward = arenaReward.getPlayerReward(player.getObjectId());
                    PvPArenaPlayerReward playerReward = (PvPArenaPlayerReward) reward;
                    points = playerReward.getPoints();
                    rank = arenaReward.getRank(playerReward.getScorePoints());
                    writeD(playerReward.getOwner());
                    writeD(playerReward.getPvPKills());
                    writeD(isRewarded ? points + playerReward.getTimeBonus() : points);
                    writeD(0);
                    writeC(0);
                    writeC(player.getPlayerClass().getClassId());
                    writeC(1);
                    writeC(rank);
                    writeD(playerReward.getRemaningTime());
                    writeD(isRewarded ? playerReward.getTimeBonus() : 0);
                    writeD(0);
                    writeD(0);
                    writeH(isRewarded ? (short) (playerReward.getParticipation() * 100) : 0);
                    writeS(player.getName(), 54);
                    playerCount++;
                } if (playerCount < 12) {
                    writeB(new byte[92 * (12 - playerCount)]);
                } if (isRewarded && arenaReward.canRewarded() && rewardedPlayer != null) {
                    writeD(rewardedPlayer.getBasicAP());
                    writeD(rewardedPlayer.getBasicGP());
                    writeD(rewardedPlayer.getRankingAP());
                    writeD(rewardedPlayer.getRankingGP());
                    writeD(rewardedPlayer.getScoreAP());
                    writeD(rewardedPlayer.getScoreGP());
                    writeD(0);
                    writeD(rewardedPlayer.getBasicKinah());
                    writeD(rewardedPlayer.getScoreKinah());
                    writeD(rewardedPlayer.getRankingKinah());
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
					writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
                    writeD(0);
					writeD(0);
                    writeD(0);
                    writeD(0);
                } else {
                    writeB(new byte[60]);
                }
                writeD(arenaReward.getBuffId());
                writeD(0);
                writeD(arenaReward.getRound());
                writeD(arenaReward.getCapPoints());
                writeD(3);
                writeD(0);
            break;
			case 302590000: //Arena 3Vs3 7.x
				writeD(type);
				switch (type) {
					case 4:
						writeC(player.getHOTVSId());//TODO 0 | 1 you and your opponent slot id
						writeC(0x0E);//unk static
						writeH(0x10);//unk static
					break;
					case 5:
						writeC(player.getHOTVSId());//TODO 0 | 1 you and your opponent slot id
						writeC(0x0E);//unk static
						writeH(0x10);//unk static
					break;
					case 6:
						writeC(player.getHOTVSId());//TODO 0 | 1 you and your opponent slot id
						writeC(0x0E);//unk static
						writeH(0x10);//unk static
						writeD(0);//this player win count
						writeC(opponent.getHOTVSId());//TODO 0 | 1 you and your opponent slot id
						writeC(0x0E);//unk static
						writeH(0x10);//unk static
						writeD(0);//this player win count
					break;
					case 11:
						writeD(1);//unk
						writeD(opponent.getHOTMyOpponentObjId());//player objectId
						writeD(player.getObjectId());//opponent objectId
					break;
				}
			break;
			case 302600000: //Hall 3Vs3 7.x
				HallOfTenacityReward hot = (HallOfTenacityReward) instanceReward;
				FastList<Player> members = hot.getPlayersInside();
				writeD(type);
				switch (type) {
					case 0: //Enter Hall 3Vs3 7.x
						writeD(5); //unk
						writeD(players.size());
						for (Player p : players) {
							writeD(p.getObjectId());
							writeS(p.getName() + " - ENCOM", 54);
							writeD(p.getPlayerClass().getClassId());
							writeH(p.getLevel());
							writeC(p.getHOTVSId());//TODO 0 | 1 you and your opponent slot id
							writeC(0x0E);//unk static
							writeH(0x10);//unk static
							writeD(p.getHOTCoupleId());//TODO 0 to 15 couple slot id
							writeD(0);//unk
							writeD(0);//unk
							writeH(0);//unk
							writeC(0);//unk
						}
					break;
					case 1:
						//TODO
					break;
					case 2:
						//TODO
					break;
					case 9://competition points
						for (Player p: players) {
							HallOfTenacityPlayerReward hotRewardedPlayer = hot.getPlayerReward(p.getObjectId()); 
							writeD(p.getObjectId());
							writeD(hotRewardedPlayer.getCompetitionPoint());
						}
					break;
				}
			break;
        }
    }
	
    private void fillTableWithGroup(Race race) {
        int count = 0;
        DredgionReward dredgionReward = (DredgionReward) instanceReward;
        for (Player player : players) {
            if (!race.equals(player.getRace())) {
                continue;
            }
            InstancePlayerReward playerReward = dredgionReward.getPlayerReward(player.getObjectId());
            DredgionPlayerReward dpr = (DredgionPlayerReward) playerReward;
            writeD(playerReward.getOwner());
            writeD(player.getAbyssRank().getRank().getId());
            writeD(dpr.getPvPKills());
            writeD(dpr.getMonsterKills());
            writeD(dpr.getZoneCaptured());
            writeD(dpr.getPoints());
            if (instanceScoreType.isEndProgress()) {
                boolean winner = race.equals(dredgionReward.getWinningRace());
                writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints()) + (int) (dpr.getPoints() * 1.6f));
                writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints()));
            } else {
                writeB(new byte[8]);
            }
            writeC(player.getPlayerClass().getClassId());
            writeC(0);
            writeS(player.getName(), 54);
            count++;
        } if (count < 6) {
            writeB(new byte[88 * (6 - count)]);
        }
    }
	
	private Race getOppositeRace(Race race) {
    	return (race.getRaceId() ^ 1) == 0 ? Race.ELYOS : Race.ASMODIANS;
    }
	
    private void idgelDomeTable(Race race) {
        IdgelDomeReward idr = (IdgelDomeReward) instanceReward;
        for (Player player : players) {
            if (!race.equals(player.getRace())) {
                continue;
            }
            IdgelDomePlayerReward idpr = idr.getPlayerReward(player.getObjectId());
            writeD(player.getObjectId());
            writeC(player.getPlayerClass().getClassId());
            writeC(player.getAbyssRank().getRank().getId());
            writeC(0);
            writeH(0);
            writeD(idpr.getPvPKills());
            writeD(idpr.getPoints());
            writeS(player.getName(), 54);
        }
		writeB(new byte[897]);
    }
	
    private void evergaleCanyonTable(Race race) {
        EvergaleCanyonReward ecr = (EvergaleCanyonReward) instanceReward;
        for (Player player : players) {
            if (!race.equals(player.getRace())) {
                continue;
            }
            EvergaleCanyonPlayerReward ecpr = ecr.getPlayerReward(player.getObjectId());
            writeD(player.getObjectId());
            writeC(player.getPlayerClass().getClassId());
            writeC(player.getAbyssRank().getRank().getId());
            writeC(0);
            writeH(0);
            writeD(ecpr.getPvPKills());
            writeD(ecpr.getPoints());
            writeS(player.getName(), 54);
        }
		writeB(new byte[828]);
    }
	
	private void illumielBattlefieldTable(Race race) {
        IllumielBattlefieldReward ibr = (IllumielBattlefieldReward) instanceReward;
        for (Player player: players) {
            if (!race.equals(player.getRace())) {
                continue;
            }
            IllumielBattlefieldPlayerReward ibpr = ibr.getPlayerReward(player.getObjectId());
            writeD(player.getObjectId());
            writeC(player.getPlayerClass().getClassId());
            writeC(player.getAbyssRank().getRank().getId());
            writeC(0);
            writeH(0);
            writeD(ibpr.getPvPKills());
            writeD(ibpr.getPoints());
            writeS(player.getName(), 54);
        }
		writeB(new byte[828]);
    }
	
	private void kamarBattlefieldTable(Race race) {
		KamarBattlefieldReward kbr = (KamarBattlefieldReward) instanceReward;
		for (Player player : players) {
			if (!race.equals(player.getRace())) {
				continue;
			}
			KamarBattlefieldPlayerReward kbpr = kbr.getPlayerReward(player.getObjectId());
			writeD(player.getObjectId());
			writeC(player.getPlayerClass().getClassId());
			writeC(player.getAbyssRank().getRank().getId());
			writeC(0);
			writeH(0);
			writeD(kbpr.getPvPKills());
			writeD(kbpr.getPoints());
			writeS(player.getName(), 54);
		}
		writeB(new byte[828]);
	}
}