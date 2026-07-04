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

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.QubrinerkReward;
import com.aionemu.gameserver.model.instance.playerreward.QubrinerkPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.SkillLearnService;
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

@InstanceID(302460000)
public class Qubrinerk_Cubic_Lab extends GeneralInstanceHandler
{
	private int rank;
	private long startTime;
	private Race skillRace;
	private Future<?> timerPrepare;
	private Future<?> timerInstance;
	private Future<?> qubrinerkTaskA1;
	private Future<?> qubrinerkTaskA2;
	private Future<?> qubrinerkTaskA3;
	private int mechaInfantrymanKilled;
	private boolean isInstanceDestroyed;
	private QubrinerkReward instanceReward;
	private Map<Integer, StaticDoor> doors;
	//Preparation Time.
	private int prepareTimerSeconds = 60000; //...1Min
	//Duration Instance Time.
	private int instanceTimerSeconds = 3600000; //...1Hr
	private final FastList<Future<?>> qubrinerkTask1 = FastList.newInstance();
	private final FastList<Future<?>> qubrinerkTask2 = FastList.newInstance();
	
	protected QubrinerkPlayerReward getPlayerReward(Integer object) {
		return (QubrinerkPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new QubrinerkPlayerReward(player.getObjectId()));
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
			case 837246: //Survivalist's Cube.
			case 837247: //Defender's Cube.
			case 837248: //Aggressor's Cube.
				switch (Rnd.get(1, 4)) {
					case 1:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071302, 1));
					break;
					case 2:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071303, 1));
					break;
					case 3:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071304, 1));
					break;
					case 4:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071305, 1));
					break;
				} switch (Rnd.get(1, 4)) {
					case 1:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071306, 1));
					break;
					case 2:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071307, 1)); 
					break;
					case 3:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071308, 1));
					break;
					case 4:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071309, 1));
					break;
				} switch (Rnd.get(1, 4)) {
					case 1:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071310, 1));
					break;
					case 2:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071311, 1));
					break;
					case 3:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071312, 1));
					break;
					case 4:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188071313, 1));
					break;
				}
			break;
		}
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(164000417, storage.getItemCountByItemId(164000417)); //Fear Grenade.
		storage.decreaseByItemId(164000418, storage.getItemCountByItemId(164000418)); //Stink Bomb.
		storage.decreaseByItemId(164002362, storage.getItemCountByItemId(164002362)); //Mechaturerk Oil Cask.
	}
	
	@Override
	public void onDie(Npc npc) {
		int score = 0;
		int npcId = npc.getNpcId();
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 655369: //Madrukin's Cannon.
				despawnNpc(npc);
				spawn(836745, 231.0000f, 258.0000f, 191.0000f, (byte) 59); //Madrukin's Cannon.
			break;
			case 655408: //Cobalt Watcher.
				startCubicTask1();
			break;
			case 655355: //Mechanikin.
				despawnNpc(npc);
				killNpc(getNpcs(836752)); //Murder Weapon Entrance.
				//The Shulack Mechanical Soldier has appeared.
				sendMsgByRace(1404746, Race.PC_ALL, 0);
				//The Machine Monster’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1404742, Race.PC_ALL, 5000);
		        spawn(836734, 138.0000f, 256.0000f, 191.0000f, (byte) 0); //Mechanikin’s Footlocker.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
					    spawn(655356, 163.0000f, 259.0000f, 192.0000f, (byte) 1); //Madrukin.
					}
				}, 3000);
			break;
			case 655356: //Madrukin.
				score += 878600;
				qubrinerkTaskA1.cancel(true);
				qubrinerkTaskA2.cancel(true);
				qubrinerkTaskA3.cancel(true);
				//You took down Madrukin and took back Qubrinerk's Cubic Lab!
				sendMsgByRace(1404750, Race.PC_ALL, 0);
				//Mechaturerk’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1404738, Race.PC_ALL, 5000);
				//Mechaturerk’s Core has appeared inside the Munitions Factory.
				sendMsgByRace(1404743, Race.PC_ALL, 10000);
				//The Rolling Golem's Belongings have appeared inside the Munitions Factory.
				sendMsgByRace(1404745, Race.PC_ALL, 15000);
				switch (Rnd.get(1, 2)) {
		            case 1:
				        spawn(836758, 149.0000f, 260.0000f, 191.0000f, (byte) 0); //Madrukin’s Treasure Box.
					break;
					case 2:
					    spawn(836759, 149.0000f, 260.0000f, 191.0000f, (byte) 0); //Madrukin’s Special Treasure Box.
					break;
				}
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    stopInstance1(player);
								stopInstance2(player);
						    }
					    });
					}
				}, 5000);
			break;
			case 655362: //Norakiki.
			    score += 500;
				//Norakiki’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1404740, Race.PC_ALL, 3000);
		        spawn(836732, 138.0000f, 263.0000f, 191.0000f, (byte) 0); //Norakiki’s Footlocker.
			break;
			case 655363: //Tarukiki.
			    score += 500;
				//Tarukiki’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1404741, Race.PC_ALL, 3000);
		        spawn(836733, 138.0000f, 259.0000f, 191.0000f, (byte) 0); //Tarukiki’s Footlocker.
			break;
			case 655376: //Madrukin's Demolisher.
			    //The Madrukin's Demolisher’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1404739, Race.PC_ALL, 3000);
		        spawn(836731, 138.0000f, 266.0000f, 191.0000f, (byte) 0); //Madrukin's Demolisher’s Footlocker.
			break;
			case 655379: //Shulack Pilot.
			    mechaInfantrymanKilled++;
				if (mechaInfantrymanKilled == 2) {
					//The Shulack Pilot's Footlocker has appeared inside the Munitions Factory.
					sendMsgByRace(1404737, Race.PC_ALL, 3000);
					spawn(703375, 138.0000f, 272.0000f, 191.0000f, (byte) 0); //Shulack Pilot’s Footlocker.
				}
			break;
			case 655383: //Mecha Bruiser.
				//Refined Aetheric Water of Life has appeared.
				sendMsgByRace(1404754, Race.PC_ALL, 1000);
				spawn(836728, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Refined Aetheric Water Of Life.
			break;
			case 655384: //Mecha Sniper.
				//Refined Aetheric Water of Life has appeared.
				sendMsgByRace(1404754, Race.PC_ALL, 1000);
				spawn(836728, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Refined Aetheric Water Of Life.
			break;
		} if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addNpcKill();
			instanceReward.addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 655352: //Oil Cask.
			    despawnNpc(npc);
				ItemService.addItem(player, 164002362, 5); //Mechaturerk Oil Cask.
			break;
			case 836728: //Refined Aetheric Water Of Life.
			    despawnNpc(npc);
				player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 50000);
				player.getLifeStats().increaseMp(SM_ATTACK_STATUS.TYPE.MP, 50000);
			break;
			case 836743: //Bomb Chest.
			    ItemService.addItem(player, 164000417, 3); //Fear Grenade.
			    ItemService.addItem(player, 164000418, 3); //Stink Bomb.
			break;
			case 836745: //Madrukin Cannon’s Cannon.
			    //A heavy door has opened somewhere.
				sendMsgByRace(1401839, Race.PC_ALL, 5000);
				SkillEngine.getInstance().getSkill(npc, 21126, 60, npc).useNoAnimationSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						killNpc(getNpcs(836745));
						killNpc(getNpcs(836751));
					}
				}, 5000);
			break;
			case 836748: //Parakin's Control Device.
			    killNpc(getNpcs(655353)); //Parakin's Mechanical Bomb.
			    killNpc(getNpcs(655383)); //Azure Living Bomb.
			break;
			case 836749: //Norakin's Control Device.
			    killNpc(getNpcs(655354)); //Norakin's Mechanical Bomb.
			    killNpc(getNpcs(655384)); //Golden Living Bomb.
			break;
		}
	}
	
	private void startCubicRaid1() {
		//Masked Shulak Mechanic.
		qubrinerkTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(655361, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655361, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 1000);
		//Masked Shulak Mechanic.
		qubrinerkTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(655361, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655361, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 30000);
		//Masked Shulak Mechanic.
		qubrinerkTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(655361, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655361, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 60000);
	}
	
	private void startCubicRaid2() {
		//Mecha Bruiser.
		qubrinerkTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(655383, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655383, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 1000);
		//Mecha Sniper.
		qubrinerkTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(655384, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655384, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 30000);
		//Mecha Bruiser + Mecha Sniper.
		qubrinerkTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(655383, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655384, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 60000);
	}
	
	private void startCubicRaid3() {
		//Parakin's Mechanical Bomb.
		qubrinerkTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//Parakin's Mechanical Bomb has appeared.
				sendMsgByRace(1404747, Race.PC_ALL, 0);
				//Use Parakin's control device!
				sendMsgByRace(1404752, Race.PC_ALL, 5000);
				sp(655353, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655353, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 1000);
		//Norakin's Mechanical Bomb.
		qubrinerkTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//Norakin's Mechanical Bomb has appeared.
				sendMsgByRace(1404748, Race.PC_ALL, 0);
				//Use Norakin's control device!
				sendMsgByRace(1404753, Race.PC_ALL, 5000);
				sp(655354, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655354, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 30000);
		//Parakin's Mechanical Bomb.
		qubrinerkTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//Parakin's Mechanical Bomb has appeared.
				sendMsgByRace(1404747, Race.PC_ALL, 0);
				//Use Parakin's control device!
				sendMsgByRace(1404752, Race.PC_ALL, 5000);
				sp(655353, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655353, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 60000);
		//Norakin's Mechanical Bomb.
		qubrinerkTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//Norakin's Mechanical Bomb has appeared.
				sendMsgByRace(1404748, Race.PC_ALL, 0);
				//Use Norakin's control device!
				sendMsgByRace(1404753, Race.PC_ALL, 5000);
				sp(655354, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(655354, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 90000);
	}
	
	private int getTime() {
		long result = (int) (System.currentTimeMillis() - startTime);
		return instanceTimerSeconds - (int) result;
	}
	
	private void sendPacket(final int nameId, final int point) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (nameId != 0) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, new DescriptionId(nameId * 2 + 1), point));
				}
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(getTime(), instanceReward, null));
			}
		});
	}
	
	private int checkRank(int totalPoints) {
		if (totalPoints >= 878600) { //Rank S.
			rank = 1;
		} else {
			rank = 6;
		}
		return rank;
	}
	
   /**
	* Raid Instance.
	*/
	protected void startCubicTask1() {
		qubrinerkTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				startCubicRaid1();
            }
        }, 120000)); //...2Min
		qubrinerkTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				startCubicRaid2();
				qubrinerkTaskA1.cancel(true);
            }
        }, 240000)); //...4Min
		qubrinerkTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				startCubicRaid3();
				qubrinerkTaskA2.cancel(true);
            }
        }, 360000)); //...6Min
		qubrinerkTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						stopInstance1(player);
						qubrinerkTaskA3.cancel(true);
				    }
			    });
            }
        }, 480000)); //...8Min
	}
	
   /**
	* Instance Timer.
	*/
	protected void startCubicTask2() {
		qubrinerkTask2.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    stopInstance2(player);
				    }
			    });
            }
        }, 3600000)); //1 Hour.
    }
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 27) {
			startCubicTask2();
			doors.get(27).setOpen(true);
			killNpc(getNpcs(836750)); //Rock Pile.
			//Detonation imminent. Prepare for battle!
			sendMsgByRace(1404775, Race.PC_ALL, 2000);
			//The player has 1 min to prepare !!! [Timer Red]
			if ((timerPrepare != null) && (!timerPrepare.isDone() || !timerPrepare.isCancelled())) {
				//Start the instance time !!! [Timer White]
				startMainInstanceTimer();
			}
		}
	}
	
	public static final void qubrinerkCubicLab(final Player player) {
		player.getSkillList().addSkill(player, 17427, 1);
		player.getSkillList().addSkill(player, 17428, 1);
		player.getSkillList().addSkill(player, 17429, 1);
		player.getSkillList().addSkill(player, 17430, 1);
		player.getSkillList().addSkill(player, 17431, 1);
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		QubrinerkPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		}
		startPrepareTimer();
		if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateGuide63802(player);
		} else {
			ClassChangeService.onUpdateGuide73802(player);
		}
		Npc qubrinerk = (Npc) spawn(806918, 380.0000f, 288.0000f, 198.0000f, (byte) 97);
		//If Daeva punches that detonator, then BOOM! No more rocks, akakak!
		NpcShoutsService.getInstance().sendMsg(qubrinerk, 1502155, qubrinerk.getObjectId(), 0, 6000);
		//If Daeva use the shocker device, living bombs will go at once, nyerk nyerk!
		NpcShoutsService.getInstance().sendMsg(qubrinerk, 1502123, qubrinerk.getObjectId(), 0, 12000);
		//Thank you, Daeva! Now, to go find missing friends!
		NpcShoutsService.getInstance().sendMsg(qubrinerk, 1502151, qubrinerk.getObjectId(), 0, 18000);
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				qubrinerkCubicLab(player);
				SkillEngine.getInstance().applyEffectDirectly(17426, player, player, 3600000 * 1);
			}
		});
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
	
	protected void stopInstance1(Player player) {
		stopInstanceTask1();
	}
	
	protected void stopInstance2(Player player) {
        stopInstanceTask2();
		instanceReward.setRank(6);
		instanceReward.setRank(checkRank(instanceReward.getPoints()));
		instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		doReward(player);
		sendPacket(0, 0);
	}
	
	private void rewardGroup() {
		for (Player p: instance.getPlayersInside()) {
			doReward(p);
		}
	}
	
	@Override
	public void doReward(Player player) {
		QubrinerkPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int qubrinerkRank = instanceReward.getRank();
			switch (qubrinerkRank) {
				case 1: //Rank S
				break;
			}
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new QubrinerkReward(mapId, instanceId);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	@Override
	public void onInstanceDestroy() {
		if (timerInstance != null) {
			timerInstance.cancel(false);
		} if (timerPrepare != null) {
			timerPrepare.cancel(false);
		}
		doors.clear();
		stopInstanceTask1();
		stopInstanceTask2();
		instanceReward.clear();
		isInstanceDestroyed = true;
	}
	
	private void stopInstanceTask1() {
        for (FastList.Node<Future<?>> n = qubrinerkTask1.head(), end = qubrinerkTask1.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	private void stopInstanceTask2() {
        for (FastList.Node<Future<?>> n = qubrinerkTask2.head(), end = qubrinerkTask2.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        qubrinerkTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
        }
    }
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
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
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(17426);
		SkillLearnService.removeSkill(player, 17427);
		SkillLearnService.removeSkill(player, 17428);
		SkillLearnService.removeSkill(player, 17429);
		SkillLearnService.removeSkill(player, 17430);
		SkillLearnService.removeSkill(player, 17431);
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