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
package instance;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.AnimalFarmRaceReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.AnimalFarmRacePlayerReward;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(301700000)
public class Animal_Farm_Race extends GeneralInstanceHandler
{
	private int condition1;
	private int condition2;
	private int condition3;
	private int condition4;
	private int condition5;
	private long instanceTime;
	private Map<Integer, StaticDoor> doors;
	private float loosingGroupMultiplier = 1;
	private boolean isInstanceDestroyed = false;
	protected AnimalFarmRaceReward animalFarmRaceReward;
	private final FastList<Future<?>> animalFarmRaceTask = FastList.newInstance();
	
	protected AnimalFarmRacePlayerReward getPlayerReward(Player player) {
        animalFarmRaceReward.regPlayerReward(player);
        return (AnimalFarmRacePlayerReward) animalFarmRaceReward.getPlayerReward(player.getObjectId());
    }
	
    private boolean containPlayer(Integer object) {
        return animalFarmRaceReward.containPlayer(object);
    }
	
	@Override
    public boolean onPassFlyingRing(Player player, String flyingRing) {
        Race race = player.getRace();
		if (flyingRing.equals("ANIMAL_FARM_RACE_1")) {
			condition1++;
			if (race.equals(Race.ELYOS)) {
				sendVariable(player, "Stage_1_Light_Condition_1", 1 + condition1);
			} else if (race.equals(Race.ASMODIANS)) {
				sendVariable(player, "Stage_1_Dark_Condition_1", 1 + condition1);
			}
		} else if (flyingRing.equals("ANIMAL_FARM_RACE_2")) {
			condition2++;
			if (race.equals(Race.ELYOS)) {
				sendVariable(player, "Stage_2_Light_Condition_1", 1 + condition2);
			} else if (race.equals(Race.ASMODIANS)) {
				sendVariable(player, "Stage_2_Dark_Condition_1", 1 + condition2);
			}
			teleporter(player, 838.0000f, 774.0000f, 263.0000f, (byte) 30);
		} else if (flyingRing.equals("ANIMAL_FARM_RACE_3")) {
			condition3++;
			if (race.equals(Race.ELYOS)) {
				sendVariable(player, "Stage_3_Light_Condition_1", 1 + condition3);
			} else if (race.equals(Race.ASMODIANS)) {
				sendVariable(player, "Stage_3_Dark_Condition_1", 1 + condition3);
			}
		} else if (flyingRing.equals("ANIMAL_FARM_RACE_4")) {
			condition4++;
			if (race.equals(Race.ELYOS)) {
				sendVariable(player, "Stage_4_Light_Condition_1", 1 + condition4);
			} else if (race.equals(Race.ASMODIANS)) {
				sendVariable(player, "Stage_4_Dark_Condition_1", 1 + condition4);
			}
		} else if (flyingRing.equals("ANIMAL_FARM_RACE_5")) {
			condition5++;
			/*
			if (!animalFarmRaceReward.isRewarded()) {
				Race winnerRace = animalFarmRaceReward.getWinnerRaceByScore();
				stopInstance(winnerRace);
			}*/
			stopInstanceTask();
			removeEffects(player);
			//Finish!!!
			sendMsgByRace(1404564, Race.PC_ALL, 0);
			//Key Chest.
			ItemService.addItem(player, 185001079, 3);
			//Win Cup.
			ItemService.addItem(player, 182010073, 1);
			//Cup.
			ItemService.addItem(player, 182010074, 1);
			instance.doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.isOnline()) {
						PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
					}
				}
			});
			if (race.equals(Race.ELYOS)) {
				sendVariable(player, "Stage_5_Light_Condition_1", 1 + condition5);
			} else if (race.equals(Race.ASMODIANS)) {
				sendVariable(player, "Stage_5_Dark_Condition_1", 1 + condition5);
			}
		}
		return false;
	}
	
	private void spawnRings1() {
        FlyRing f1 = new FlyRing(new FlyRingTemplate("ANIMAL_FARM_RACE_1", mapId,
        new Point3D(1199.0000, 1165.0000, 380.0000),
		new Point3D(1195.0000, 1165.0000, 385.0000),
        new Point3D(1192.0000, 1166.0000, 380.0000), 85), instanceId);
        f1.spawn();
    }
	private void spawnRings2() {
        FlyRing f2 = new FlyRing(new FlyRingTemplate("ANIMAL_FARM_RACE_2", mapId,
        new Point3D(952.0000, 175.0000, 283.0000),
		new Point3D(950.0000, 178.0000, 291.0000),
        new Point3D(950.0000, 182.0000, 283.0000), 65), instanceId);
        f2.spawn();
    }
	private void spawnRings3() {
        FlyRing f3 = new FlyRing(new FlyRingTemplate("ANIMAL_FARM_RACE_3", mapId,
        new Point3D(834.0000, 775.0000, 263.0000),
		new Point3D(838.0000, 775.0000, 270.0000),
        new Point3D(842.0000, 775.0000, 263.0000), 30), instanceId);
        f3.spawn();
    }
	private void spawnRings4() {
        FlyRing f4 = new FlyRing(new FlyRingTemplate("ANIMAL_FARM_RACE_4", mapId,
        new Point3D(286.0000, 350.0000, 165.0000),
		new Point3D(285.0000, 352.0000, 173.0000),
        new Point3D(286.0000, 355.0000, 165.0000), 60), instanceId);
        f4.spawn();
    }
	private void spawnRings5() {
        FlyRing f5 = new FlyRing(new FlyRingTemplate("ANIMAL_FARM_RACE_5", mapId,
        new Point3D(192.0000, 347.0000, 180.0000),
		new Point3D(191.0000, 351.0000, 188.0000),
        new Point3D(192.0000, 356.0000, 180.0000), 65), instanceId);
        f5.spawn();
    }
	
	protected void teleporter(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	protected void startInstanceTask() {
		instanceTime = System.currentTimeMillis();
        animalFarmRaceReward.setInstanceStartTime();
		animalFarmRaceTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				openFirstDoors();
                instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 3600)); //...1Hr
					}
				});
				/*
				if (!animalFarmRaceReward.isRewarded()) {
					animalFarmRaceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
                    //sendInstanceInfoPacket();
				}*/
            }
        }, 60000));
		animalFarmRaceTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				stopInstanceTask();
				//Finish!!!
				sendMsgByRace(1404564, Race.PC_ALL, 0);
            	instance.doOnAllPlayers(new Visitor<Player>() {
			        @Override
			        public void visit(Player player) {
				        if (player.isOnline()) {
						    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
					    }
				    }
			    });
				/*
				if (!animalFarmRaceReward.isRewarded()) {
					Race winnerRace = animalFarmRaceReward.getWinnerRaceByScore();
					stopInstance(winnerRace);
				}*/
            }
        }, 3600000)); //...1H
    }
	
	protected void stopInstance(Race race) {
        stopInstanceTask();
        animalFarmRaceReward.setWinnerRace(race);
        animalFarmRaceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
        //reward();
		//sendRewardPacket();
        //sendScoreTypePacket();
    }
	
	@Override
    public void onEnterInstance(final Player player) {
        if (!containPlayer(player.getObjectId())) {
            animalFarmRaceReward.regPlayerReward(player);
        }
        //sendPreparingPacket(player);
    }
	
	private void sendVariable(Player player, final String variable, final int value) {
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_CONDITION_VARIABLE(player, variable, value));
				}
			}
		});
	}
	
	public void sendPreparingPacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(6, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, getTime(), animalFarmRaceReward, player.getObjectId(), 0, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            }
        });
	}
	public void sendScoreTypePacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(2, getTime(), animalFarmRaceReward, player.getObjectId()));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendRewardPacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(5, getTime(), animalFarmRaceReward, player.getObjectId()));
            }
        });
    }
	public void sendInstanceInfoPacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendNpcScorePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(11, getTime(), animalFarmRaceReward, player.getObjectId()));
            }
        });
    }
	public void sendPlayerLeavePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(8, getTime(), animalFarmRaceReward, player.getObjectId()));
            }
        });
    }
	public void sendPlayerDiePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, getTime(), animalFarmRaceReward, player.getObjectId(), 100, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, getTime(), animalFarmRaceReward, player.getObjectId(), 100, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, getTime(), animalFarmRaceReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendPlayerRevivedPacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, getTime(), animalFarmRaceReward, player.getObjectId(), 0, player.getRace().getRaceId()));
            }
        });
    }
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		animalFarmRaceReward = new AnimalFarmRaceReward(mapId, instanceId, instance);
        animalFarmRaceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		spawnRings1();
		spawnRings2();
		spawnRings3();
		spawnRings4();
		spawnRings5();
		startInstanceTask();
    }
	
	protected void reward() {
        int ElyosPvPKills = getPvpKillsByRace(Race.ELYOS).intValue();
        int ElyosPoints = getPointsByRace(Race.ELYOS).intValue();
        int AsmoPvPKills = getPvpKillsByRace(Race.ASMODIANS).intValue();
        int AsmoPoints = getPointsByRace(Race.ASMODIANS).intValue();
		for (Player player: instance.getPlayersInside()) {
			if (PlayerActions.isAlreadyDead(player)) {
				PlayerReviveService.duelRevive(player);
			}
			AnimalFarmRacePlayerReward playerReward = animalFarmRaceReward.getPlayerReward(player.getObjectId());
			int abyssPoint = 3163;
			int gloryPoint = 150;
			int expPoint = 10000;
			playerReward.setRewardAp((int) abyssPoint);
			playerReward.setRewardGp((int) gloryPoint);
			playerReward.setRewardExp((int) expPoint);
			if (player.getRace().equals(animalFarmRaceReward.getWinnerRace())) {
				abyssPoint += animalFarmRaceReward.AbyssReward(true, true);
				gloryPoint += animalFarmRaceReward.GloryReward(true, true);
				expPoint += animalFarmRaceReward.ExpReward(true, true);
				playerReward.setBonusAp(animalFarmRaceReward.AbyssReward(true, true));
                playerReward.setBonusGp(animalFarmRaceReward.GloryReward(true, true));
				playerReward.setBonusExp(animalFarmRaceReward.ExpReward(true, true));
				//Treasure Chest Key.
				playerReward.setCommonTreasureChestKey(185001079);
			} else {
				abyssPoint += animalFarmRaceReward.AbyssReward(false, false);
                gloryPoint += animalFarmRaceReward.GloryReward(false, false);
				expPoint += animalFarmRaceReward.ExpReward(false, false);
				playerReward.setRewardAp(animalFarmRaceReward.AbyssReward(false, false));
                playerReward.setRewardGp(animalFarmRaceReward.GloryReward(false, false));
				playerReward.setRewardExp(animalFarmRaceReward.ExpReward(false, false));
				//Treasure Chest Key.
				playerReward.setCommonTreasureChestKey(185001079);
			}
            ItemService.addItem(player, 185001079, 6);
			AbyssPointsService.addAp(player, (int) abyssPoint);
            AbyssPointsService.addGp(player, (int) gloryPoint);
            player.getCommonData().addExp(expPoint, RewardType.HUNTING);
        }
    }
	
	private int getTime() {
        long result = System.currentTimeMillis() - instanceTime;
        if (result < 60000) {
            return (int) (60000 - result);
        } else if (result < 3600000) { //...1Hr
            return (int) (3600000 - (result - 60000));
        }
        return 0;
    }
	
	@Override
    public boolean onDie(Player player, Creature lastAttacker) {
        int points = 100;
		//sendPlayerDiePacket(player);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), false, 0, 8));
        if (lastAttacker instanceof Player) {
            if (lastAttacker.getRace() != player.getRace()) {
                InstancePlayerReward playerReward = animalFarmRaceReward.getPlayerReward(player.getObjectId());
				if (getPointsByRace(lastAttacker.getRace()).compareTo(getPointsByRace(player.getRace())) < 0) {
                    points *= loosingGroupMultiplier;
                } else if (loosingGroupMultiplier == 100 || playerReward.getPoints() == 0) {
                    points = 0;
                }
                updateScore((Player) lastAttacker, player, points, true);
            }
        }
        updateScore(player, player, -points, false);
        return true;
    }
	
	private MutableInt getPvpKillsByRace(Race race) {
        return animalFarmRaceReward.getPvpKillsByRace(race);
    }
	
    private MutableInt getPointsByRace(Race race) {
        return animalFarmRaceReward.getPointsByRace(race);
    }
	
    private void addPointsByRace(Race race, int points) {
        animalFarmRaceReward.addPointsByRace(race, points);
    }
	
    private void addPvpKillsByRace(Race race, int points) {
        animalFarmRaceReward.addPvpKillsByRace(race, points);
    }
	
    private void addPointToPlayer(Player player, int points) {
        animalFarmRaceReward.getPlayerReward(player.getObjectId()).addPoints(points);
    }
	
    private void addPvPKillToPlayer(Player player) {
        animalFarmRaceReward.getPlayerReward(player.getObjectId()).addPvPKillToPlayer();
    }
	
	protected void updateScore(Player player, Creature target, int points, boolean pvpKill) {
        if (points == 0) {
            return;
        }
        addPointsByRace(player.getRace(), points);
        List<Player> playersToGainScore = new ArrayList<Player>();
        if (target != null && player.isInGroup2()) {
            for (Player member: player.getPlayerGroup2().getOnlineMembers()) {
                if (member.getLifeStats().isAlreadyDead()) {
                    continue;
                } if (MathUtil.isIn3dRange(member, target, GroupConfig.GROUP_MAX_DISTANCE)) {
                    playersToGainScore.add(member);
                }
            }
        } else {
            playersToGainScore.add(player);
        } for (Player playerToGainScore: playersToGainScore) {
            addPointToPlayer(playerToGainScore, points / playersToGainScore.size());
            if (target instanceof Npc) {
                PacketSendUtility.sendPacket(playerToGainScore, new SM_SYSTEM_MESSAGE(1400237, new DescriptionId(((Npc) target).getObjectTemplate().getNameId() * 2 + 1), points));
            } else if (target instanceof Player) {
                PacketSendUtility.sendPacket(playerToGainScore, new SM_SYSTEM_MESSAGE(1400237, target.getName(), points));
            }
        }
        int pointDifference = getPointsByRace(Race.ASMODIANS).intValue() - (getPointsByRace(Race.ELYOS)).intValue();
        if (pointDifference < 0) {
            pointDifference *= -1;
        } if (pointDifference >= 3000) {
            loosingGroupMultiplier = 10;
        } else if (pointDifference >= 1000) {
            loosingGroupMultiplier = 1.5f;
        } else {
            loosingGroupMultiplier = 1;
        } if (pvpKill && points > 0) {
            addPvpKillsByRace(player.getRace(), 1);
            addPvPKillToPlayer(player);
        }
        //sendNpcScorePacket(player);
    }
	
	@Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
		animalFarmRaceReward.clear();
        stopInstanceTask();
        doors.clear();
    }
	
	protected void openFirstDoors() {
        openDoor(8);
		openDoor(93);
    }
	
    protected void openDoor(int doorId) {
        StaticDoor door = doors.get(doorId);
        if (door != null) {
            door.setOpen(true);
        }
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        animalFarmRaceTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    spawn(npcId, x, y, z, h, entityId);
                    if (msg > 0) {
                        sendMsgByRace(msg, race, 0);
                    }
                }
            }
        }, time));
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        animalFarmRaceTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkerId);
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                }
            }
        }, time));
    }
	
    protected void sendMsgByRace(final int msg, final Race race, int time) {
        animalFarmRaceTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                instance.doOnAllPlayers(new Visitor<Player>() {
                    @Override
                    public void visit(Player player) {
                        if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
                        }
                    }
                });
            }
        }, time));
    }
	
	private void sendMsg(final String str) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendWhiteMessageOnCenter(player, str);
			}
		});
	}
	
    private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = animalFarmRaceTask.head(), end = animalFarmRaceTask.tail(); (n = n.getNext()) != end;) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	@Override
    public InstanceReward<?> getInstanceReward() {
        return animalFarmRaceReward;
    }
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		//effectController.removeEffect(11277);
		//effectController.removeEffect(11278);
		//effectController.removeEffect(11279);
		//effectController.removeEffect(11280);
		/*
		SkillLearnService.removeSkill(player, 448);
		SkillLearnService.removeSkill(player, 451);
		SkillLearnService.removeSkill(player, 463);
		SkillLearnService.removeSkill(player, 458);
		SkillLearnService.removeSkill(player, 450);
		SkillLearnService.removeSkill(player, 461);
		SkillLearnService.removeSkill(player, 457);
		SkillLearnService.removeSkill(player, 464);
		SkillLearnService.removeSkill(player, 449);
		SkillLearnService.removeSkill(player, 460);
		SkillLearnService.removeSkill(player, 465);
		SkillLearnService.removeSkill(player, 452);
		SkillLearnService.removeSkill(player, 448);
		SkillLearnService.removeSkill(player, 435);
		SkillLearnService.removeSkill(player, 455);
		SkillLearnService.removeSkill(player, 436);
		*/
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
    public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
		//sendPlayerLeavePacket(player);
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400255, player.getName()));
    }
	
	@Override
    public void onExitInstance(Player player) {
        TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
    }
	
	@Override
    public void onPlayerLogin(Player player) {
        //sendNpcScorePacket(player);
    }
}