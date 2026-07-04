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
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.SecretMunitionsFactoryReward;
import com.aionemu.gameserver.model.instance.playerreward.SecretMunitionsFactoryPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastList;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(301640000)
public class Secret_Munitions_Factory extends GeneralInstanceHandler
{
	private int rank;
	private long startTime;
	private Race skillRace;
	private Future<?> timerPrepare;
	private Future<?> factoryTaskA1;
	private Future<?> factoryTaskA2;
	private Future<?> factoryTaskA3;
	private Future<?> timerInstance;
	private int mechaInfantrymanKilled;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	//Preparation Time.
	private int prepareTimerSeconds = 60000; //...1Min
	//Duration Instance Time.
	private int instanceTimerSeconds = 3600000; //...1Hr
	private SecretMunitionsFactoryReward instanceReward;
	private final FastList<Future<?>> factoryTask1 = FastList.newInstance();
	private final FastList<Future<?>> factoryTask2 = FastList.newInstance();
	
	protected SecretMunitionsFactoryPlayerReward getPlayerReward(Integer object) {
		return (SecretMunitionsFactoryPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new SecretMunitionsFactoryPlayerReward(player.getObjectId()));
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
			case 245185: //Mechaturerk’s Core.
			    switch (Rnd.get(1, 7)) {
				    case 1:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 152150000, 2)); //Uncut Crystal.
				    break;
					case 2:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 152150001, 2)); //Chipped Crystal.
				    break;
					case 3:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 152150002, 2)); //Cloudy Crystal.
				    break;
					case 4:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 152150003, 2)); //Clear Crystal.
				    break;
					case 5:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 152150004, 2)); //Flawless Crystal.
				    break;
					case 6:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 152150005, 2)); //Luna’s Light.
				    break;
					case 7:
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 152150006, 2)); //Luna’s Blessing.
				    break;
			    }
			break;
			case 834443: //Mechaturerk’s Treasure Box.
			case 834444: //Mechaturerk’s Special Treasure Box.
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
			case 243993: //Mechaturerk’s Cannon.
				despawnNpc(npc);
				spawn(833835, 231.0000f, 258.0000f, 191.0000f, (byte) 59); //Mechaturerk's Cannon.
			break;
			case 245759: //Siege Factory Watcher.
				startFactoryTask1();
			break;
			case 243663: //Mechaturerk Machine Monster.
				despawnNpc(npc);
				killNpc(getNpcs(833896)); //Factory Gate.
				//The Destruction Golem has appeared!
				sendMsgByRace(1403649, Race.PC_ALL, 0);
				//The Machine Monster’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1403645, Race.PC_ALL, 5000);
		        spawn(703380, 138.84042f, 256.166f, 191.8727f, (byte) 0); //Machine Monster’s Footlocker.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
					    spawn(243664, 163.0000f, 259.0000f, 192.0000f, (byte) 1); //Mechaturerk.
					}
				}, 3000);
			break;
			case 243664: //Mechaturerk.
				score += 878600;
				//factoryTaskA1.cancel(true);
				//factoryTaskA2.cancel(true);
				//factoryTaskA3.cancel(true);
				//You killed Mechaturerk!
				sendMsgByRace(1403653, Race.PC_ALL, 0);
				//Mechaturerk’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1403646, Race.PC_ALL, 5000);
				//Mechaturerk’s Core has appeared inside the Munitions Factory.
				sendMsgByRace(1403647, Race.PC_ALL, 10000);
				//The Destruction Golem’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1403648, Race.PC_ALL, 15000);
				switch (Rnd.get(1, 2)) {
		            case 1:
				        spawn(834443, 149.0000f, 260.0000f, 191.0000f, (byte) 0); //Mechaturerk’s Treasure Box.
					break;
					case 2:
					    spawn(834444, 149.0000f, 260.0000f, 191.0000f, (byte) 0); //Mechaturerk’s Special Treasure Box.
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
			case 243968: //Remirunerk.
			    score += 500;
			    //Remirunrunerk’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1403643, Race.PC_ALL, 3000);
		        spawn(703378, 138.0000f, 263.0000f, 191.0000f, (byte) 0); //Remirunrunerk’s Footlocker.
			break;
			case 243969: //Bomirunrunerk.
			    score += 500;
			    //Bomirunrunerk’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1403644, Race.PC_ALL, 3000);
		        spawn(703379, 138.0000f, 259.0000f, 191.0000f, (byte) 0); //Bomirunrunerk’s Footlocker.
			break;
			case 244028: //Mechaturerk Gunner.
			    //The Gunner’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1403642, Race.PC_ALL, 3000);
		        spawn(703377, 138.0000f, 266.0000f, 191.0000f, (byte) 0); //Gunner’s Footlocker.
			break;
			case 244035: //Damaged Mecha Infantryman.
			    mechaInfantrymanKilled++;
				if (mechaInfantrymanKilled == 2) {
					//The Armored Soldier’s Footlocker has appeared inside the Munitions Factory.
					sendMsgByRace(1403640, Race.PC_ALL, 3000);
					spawn(703375, 138.0000f, 272.0000f, 191.0000f, (byte) 0); //Armored Soldier’s Footlocker.
				}
			break;
			case 244135: //Melee Support Destruction Golem.
				//The recovery plant has emerged.
				sendMsgByRace(1403824, Race.PC_ALL, 1000);
				spawn(836090, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Health Pod.
			break;
			case 244136: //Ranged Support Destruction Golem.
				//The recovery plant has emerged.
				sendMsgByRace(1403824, Race.PC_ALL, 1000);
				spawn(836090, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Health Pod.
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
			case 243660: //Oil Cask.
			    despawnNpc(npc);
				ItemService.addItem(player, 164002362, 5); //Mechaturerk Oil Cask.
			break;
			case 836090: //Health Pod.
			    despawnNpc(npc);
				player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 50000);
				player.getLifeStats().increaseMp(SM_ATTACK_STATUS.TYPE.MP, 50000);
			break;
			case 833833: //Bomb Chest.
			    ItemService.addItem(player, 164000417, 3); //Fear Grenade.
			    ItemService.addItem(player, 164000418, 3); //Stink Bomb.
			break;
			case 833835: //Mechaturerk’s Cannon.
			    //A heavy door has opened somewhere.
				sendMsgByRace(1401839, Race.PC_ALL, 5000);
				SkillEngine.getInstance().getSkill(npc, 21126, 60, npc).useNoAnimationSkill(); //Destroy Seal.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						killNpc(getNpcs(833835));
						killNpc(getNpcs(833869));
					}
				}, 5000);
			break;
			case 833838: //Azurespark Apparatus.
			    killNpc(getNpcs(243661)); //Blue Living Bomb.
				killNpc(getNpcs(244135)); //Melee Support Destruction Golem.
			break;
			case 833839: //Goldenspark Apparatus.
			    killNpc(getNpcs(243662)); //Golden Living Bomb.
				killNpc(getNpcs(244136)); //Melee Support Destruction Golem.
			break;
		}
	}
	
	private void startFactoryRaid1() {
		//Mechaturerk Maintenance Soldier.
		factoryTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(243853, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(243853, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 1000);
		//Mechaturerk Maintenance Soldier.
		factoryTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(243853, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(243853, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 30000);
		//Mechaturerk Maintenance Soldier.
		factoryTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(243853, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(243853, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 60000);
	}
	
	private void startFactoryRaid2() {
		//Melee Support Destruction Golem.
		factoryTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(244135, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(244135, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 1000);
		//Ranged Support Destruction Golem.
		factoryTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(244136, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(244136, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 30000);
		//Melee + Ranged Support Destruction Golem.
		factoryTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(244135, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(244136, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 60000);
	}
	
	private void startFactoryRaid3() {
		//Azure Living Bomb.
		factoryTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//The Azure Living bomb has appeared!
				sendMsgByRace(1403650, Race.PC_ALL, 0);
				//Use the blue mechanical device!
				sendMsgByRace(1403663, Race.PC_ALL, 5000);
				sp(243661, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(243661, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 1000);
		//Golden Living Bomb.
		factoryTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//The Golden Living bomb has appeared!
				sendMsgByRace(1403651, Race.PC_ALL, 0);
				//Use the yellow mechanical device!
				sendMsgByRace(1403663, Race.PC_ALL, 5000);
				sp(243662, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(243662, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 30000);
		//Azure Living Bomb.
		factoryTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//The Azure Living bomb has appeared!
				sendMsgByRace(1403650, Race.PC_ALL, 0);
				//Use the blue mechanical device!
				sendMsgByRace(1403663, Race.PC_ALL, 5000);
				sp(243661, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(243661, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
			}
		}, 60000);
		//Golden Living Bomb.
		factoryTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//The Golden Living bomb has appeared!
				sendMsgByRace(1403651, Race.PC_ALL, 0);
				//Use the yellow mechanical device!
				sendMsgByRace(1403663, Race.PC_ALL, 5000);
				sp(243662, 133.37782f, 229.28152f, 191.94075f, (byte) 15, 1000, "MunitionFactory1");
				sp(243662, 132.91176f, 289.63672f, 191.98668f, (byte) 106, 2000, "MunitionFactory2");
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
	protected void startFactoryTask1() {
		factoryTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				startFactoryRaid1();
            }
        }, 120000)); //...2Min
		factoryTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				startFactoryRaid2();
				factoryTaskA1.cancel(true);
				//The Maintenance Soldier’s Footlocker has appeared inside the Munitions Factory.
				sendMsgByRace(1403641, Race.PC_ALL, 3000);
				spawn(703376, 138.0000f, 269.0000f, 191.0000f, (byte) 0); //Maintenance Soldier’s Footlocker.
            }
        }, 240000)); //...4Min
		factoryTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				startFactoryRaid3();
				factoryTaskA2.cancel(true);
            }
        }, 360000)); //...6Min
		factoryTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						stopInstance1(player);
						factoryTaskA3.cancel(true);
				    }
			    });
            }
        }, 480000)); //...8Min
	}
	
   /**
	* Instance Timer.
	*/
	protected void startFactoryTask2() {
		factoryTask2.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
			startFactoryTask2();
			doors.get(27).setOpen(true);
			killNpc(getNpcs(833868)); //Rock Pile.
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
		SecretMunitionsFactoryPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		}
		startPrepareTimer();
		spawnLunaDetachment();
		secretMinitionsFactory(player);
		final int lunaDetachement = skillRace == Race.ASMODIANS ? 21348 : 21347;
		SkillEngine.getInstance().applyEffectDirectly(lunaDetachement, player, player, 3000000 * 1);
	}
	
	public static final void secretMinitionsFactory(final Player player) {
		player.getSkillList().addSkill(player, 416, 1);
		player.getSkillList().addSkill(player, 417, 1);
		player.getSkillList().addSkill(player, 418, 1);
		player.getSkillList().addSkill(player, 419, 1);
		player.getSkillList().addSkill(player, 420, 1);
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
	
	private void spawnLunaDetachment() {
	    spawn(833826, 385.30814f, 286.88065f, 198.56099f, (byte) 6); //Roxy.
		spawn(833827, 386.10965f, 282.91656f, 198.24266f, (byte) 11); //Mak.
		spawn(833828, 386.33496f, 290.52594f, 198.5f, (byte) 115); //Manad.
		spawn(833829, 382.25574f, 283.81686f, 198.50284f, (byte) 7); //Herez.
		spawn(833897, 388.17896f, 279.8141f, 197.98882f, (byte) 14); //Joel.
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
	
	@Override
	public void doReward(Player player) {
		SecretMunitionsFactoryPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int factoryRank = instanceReward.getRank();
			switch (factoryRank) {
				case 1: //Rank S
					playerReward.setMechaturerkSecretBox(1);
					//Mechaturerk's Secret Box.
					ItemService.addItem(player, 188055475, 1);
				break;
				case 2: //Rank A
				    playerReward.setMechaturerkNormalTreasureChest(1);
					//Mechaturerk’s Normal Treasure Chest.
					ItemService.addItem(player, 188055647, 1);
				break;
				case 3: //Rank B
				    playerReward.setMechaturerkSpecialTreasureBox(1);
					//Mechaturerk’s Special Treasure Box.
					ItemService.addItem(player, 188055648, 1);
				break;
				case 4: //Rank C
				    playerReward.setMechaturerkSpecialTreasureBox(1);
					//Mechaturerk’s Special Treasure Box.
					ItemService.addItem(player, 188055648, 1);
				break;
			}
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new SecretMunitionsFactoryReward(mapId, instanceId);
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
		stopInstanceTask1();
		stopInstanceTask2();
		isInstanceDestroyed = true;
		instanceReward.clear();
		doors.clear();
	}
	
	private void stopInstanceTask1() {
        for (FastList.Node<Future<?>> n = factoryTask1.head(), end = factoryTask1.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	private void stopInstanceTask2() {
        for (FastList.Node<Future<?>> n = factoryTask2.head(), end = factoryTask2.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        factoryTask1.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
		effectController.removeEffect(21347);
		effectController.removeEffect(21348);
		///////////////////////////////////////////
		SkillLearnService.removeSkill(player, 416);
		SkillLearnService.removeSkill(player, 417);
		SkillLearnService.removeSkill(player, 418);
		SkillLearnService.removeSkill(player, 419);
		SkillLearnService.removeSkill(player, 420);
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