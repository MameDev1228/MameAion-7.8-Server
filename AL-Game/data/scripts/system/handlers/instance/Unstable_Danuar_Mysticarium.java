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
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.DanuarMysticariumReward;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.playerreward.DanuarMysticariumPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300480000)
public class Unstable_Danuar_Mysticarium extends GeneralInstanceHandler
{
    private int rank;
	private long startTime;
	private Race spawnRace;
	private Future<?> timerPrepare;
	private Future<?> timerInstance;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	private DanuarMysticariumReward instanceReward;
	//Preparation Time.
	private int prepareTimerSeconds = 60000; //...1Min
	//Duration Instance Time.
	private int instanceTimerSeconds = 1800000; //...30Min
	private final FastList<Future<?>> danuarMysticariumTask = FastList.newInstance();
	
	protected DanuarMysticariumPlayerReward getPlayerReward(Integer object) {
		return (DanuarMysticariumPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new DanuarMysticariumPlayerReward(player.getObjectId()));
	}
	
	private boolean containPlayer(Integer object) {
		return instanceReward.containPlayer(object);
	}
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 230076: //Danuar Mysticarium Archive.
                dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npc.getNpcId(), 185000126, 1)); //Test Subject Prison Key.
            break;
            case 230066: //Sheban Intelligence Warden.
                dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npc.getNpcId(), 185000165, 1)); //Idgel Storage Key.
			break;
			case 230080: //Sheban intelligence Inspector.
            case 230081: //Sheban Intelligence Captain.
                dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npc.getNpcId(), 185000127, 1)); //Idgel Storage Key.
            break;
		}
	}
	
	@Override
    public void onDie(Npc npc) {
        int score = 0;
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
            case 230062: //Einsaldir The Cursed.
            case 230063: //Living Idgel.
            case 230064: //Dread Witherthorn.
                score += 150;
            break;
            case 230066: //Sheban Intelligence Warden.
            case 230067: //Sheban Intelligence Researcher.
            case 230078: //Sheban Intelligence Warden.
            case 230079: //Sheban Intelligence Researcher.
                score += 285;
            break;
			case 230074: //Idgel Trigger.
                score += 1125;
				despawnNpc(npc);
            break;
			case 230051: //Kippy The Destroyer.
            case 230052: //Manumumu Reborn.
            case 230053: //Krukel The Infernal.
            case 230054: //Idewarped Ginseng.
            case 230055: //Panoptes The Watchful.
            case 230056: //Undying Ntuamu.
            case 230057: //Brashuna The Disgraced.
            case 230058: //Brhekman The Failed.
                score += 2010;
            break;
			case 230080: //Sheban intelligence Inspector.
            case 230081: //Sheban Intelligence Captain.
                score += 3051;
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						instance.doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								if (player.isOnline()) {
									stopInstance(player);
								}
							}
						});
					}
				}, 5000);
            break;
        } if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addNpcKill();
			instanceReward.addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		} switch (npc.getNpcId()) {
            case 230082: //Berserk Chairman Nautius.
            case 230083: //Frenzied Chairman Nautius.
			    final int mysticariumExit = spawnRace == Race.ASMODIANS ? 701573 : 701572;
		        spawn(mysticariumExit, 557.0000f, 414.0000f, 96.0000f, (byte) 42);
            break;
			case 230071: //Heavy Barrier.
			case 230072: //Tiamat Barricade.
			case 230073: //Reinforced Barrier.
			    despawnNpc(npc);
            break;
        }
    }
	
	@Override
    public void handleUseItemFinish(Player player, Npc npc) {
		int score = 0;
        switch (npc.getNpcId()) {
			case 831145: //Ancient Runish Codex.
			    score += 500;
				despawnNpc(npc);
            break;
        } if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		}
    }
	
	private int getTime() {
		long result = (int) (System.currentTimeMillis() - startTime);
		return instanceTimerSeconds - (int) result;
	}
	
	private void sendPacket(final int nameId, final int points) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (nameId != 0) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, new DescriptionId(nameId * 2 + 1), points));
				}
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(getTime(), instanceReward, null));
			}
		});
	}
	
	private int checkRank(int totalPoints) {
        if (totalPoints >= 29911) { //Rank S.
            rank = 1;
        } else if (totalPoints >= 21682) { //Rank A.
            rank = 2;
        } else if (totalPoints >= 14824) { //Rank B.
            rank = 3;
        } else if (totalPoints >= 9338) { //Rank C.
            rank = 4;
        } else if (totalPoints >= 6595) { //Rank D.
            rank = 5;
        } else {
            rank = 6;
        }
        return rank;
    }
	
	protected void startInstanceTask() {
		danuarMysticariumTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    stopInstance(player);
				    }
			    });
            }
        }, 1800000)); //...30Min
    }
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 3) {
			startInstanceTask();
			doors.get(3).setOpen(true);
			//Use the open entrance to move to the next area.
			sendMsgByRace(1402781, Race.PC_ALL, 0);
			//The player has 1 min to prepare !!! [Timer Red]
			if ((timerPrepare != null) && (!timerPrepare.isDone() || !timerPrepare.isCancelled())) {
				//Start the instance time !!! [Timer White]
				startMainInstanceTimer();
			}
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		DanuarMysticariumPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		}
		startPrepareTimer();
	}
	
	private void startPrepareTimer() {
		if (timerPrepare == null) {
			timerPrepare = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startMainInstanceTimer();
				}
			}, prepareTimerSeconds);
		}
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(prepareTimerSeconds, instanceReward, null));
			}
		});
	}
	
	private void startMainInstanceTimer() {
		if (!timerPrepare.isDone()) {
			timerPrepare.cancel(false);
		}
		startTime = System.currentTimeMillis();
		instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
		sendPacket(0, 0);
	}
	
	protected void stopInstance(Player player) {
		stopInstanceTask();
        instanceReward.setRank(6);
		instanceReward.setRank(checkRank(instanceReward.getPoints()));
		instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		doReward(player);
		sendPacket(0, 0);
	}
	
	@Override
	public void doReward(Player player) {
		DanuarMysticariumPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int danuarRank = instanceReward.getRank();
			switch (danuarRank) {
				case 1: //Rank S
					playerReward.setScoreAP(250000); //Ap.
					playerReward.setExperienceCrystal(3);
					ItemService.addItem(player, 188900063, 3); //Experience Crystal.
				break;
				case 2: //Rank A
				    playerReward.setScoreAP(200000); //Ap.
					playerReward.setExperienceCrystal(2);
					ItemService.addItem(player, 188900063, 2); //Experience Crystal.
				break;
				case 3: //Rank B
				    playerReward.setScoreAP(150000); //Ap.
					playerReward.setExperienceCrystal(1);
					ItemService.addItem(player, 188900063, 1); //Experience Crystal.
				break;
				case 4: //Rank C
				    playerReward.setScoreAP(100000); //Ap.
				break;
				case 5: //Rank D
				    playerReward.setScoreAP(50000); //Ap.
				break;
			}
			AbyssPointsService.addAp(player, playerReward.getScoreAP());
		}
	}
	
    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
		instanceReward = new DanuarMysticariumReward(mapId, instanceId);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(230080, 520.6471f, 468.8483f, 95.5875f, (byte) 40); //Sheban intelligence Inspector.
			break;
			case 2:
				spawn(230081, 520.6471f, 468.8483f, 95.5875f, (byte) 40); //Sheban intelligence Captain.
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(230082, 546.0184f, 431.3575f, 94.7859f, (byte) 42); //Berserk Chairman Nautius.
			break;
			case 2:
				spawn(230083, 546.0184f, 431.3575f, 94.7859f, (byte) 42); //Frenzied Chairman Nautius.
			break;
		}
		//Einsaldir The Cursed.
		switch (Rnd.get(1, 7)) {
			case 1:
				spawn(230062, 408.6095f, 555.2313f, 126.36708f, (byte) 97);
			break;
			case 2:
				spawn(230062, 469.55182f, 506.45465f, 106.91887f, (byte) 60);
			break;
			case 3:
				spawn(230062, 220.14043f, 371.05585f, 167.5081f, (byte) 32);
			break;
			case 4:
				spawn(230062, 391.0984f, 516.3457f, 136.33994f, (byte) 108);
			break;
			case 5:
				spawn(230062, 344.48477f, 488.8776f, 147.02097f, (byte) 16);
			break;
			case 6:
				spawn(230062, 465.7303f, 506.82822f, 108.00459f, (byte) 32);
			break;
			case 7:
				spawn(230062, 464.6685f, 510.02338f, 108.19699f, (byte) 52);
			break;
		}
    }
	
	@Override
	public void onInstanceDestroy() {
		if (timerInstance != null) {
			timerInstance.cancel(false);
		} if (timerPrepare != null) {
			timerPrepare.cancel(false);
		}
		stopInstanceTask();
		isInstanceDestroyed = true;
		instanceReward.clear();
		doors.clear();
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = danuarMysticariumTask.head(), end = danuarMysticariumTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        danuarMysticariumTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        danuarMysticariumTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
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
		}, time);
	}
}