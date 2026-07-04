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
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.ShugoEmperorVaultReward;
import com.aionemu.gameserver.model.instance.playerreward.ShugoEmperorVaultPlayerReward;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/** Source: https://www.youtube.com/watch?v=5VtBIUetRjU
/****/

@InstanceID(301400000)
public class The_Shugo_Emperor_Vault extends GeneralInstanceHandler
{
	private int rank;
	private Race spawnRace;
	private long startTime;
	private Future<?> vaultTaskA1;
	private Future<?> vaultTaskA2;
	private Future<?> vaultTaskA3;
	private Future<?> vaultTaskA4;
	//////////////////////////////
	private Future<?> vaultTaskB1;
	private Future<?> vaultTaskB2;
	private Future<?> vaultTaskB3;
	private Future<?> timerPrepare;
	private Future<?> timerInstance;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	//Preparation Time.
	private int prepareTimerSeconds = 60000; //...1Min
	//Duration Instance Time.
	private int instanceTimerSeconds = 600000; //...10Min
	private ShugoEmperorVaultReward instanceReward;
	private final FastList<Future<?>> vaultTask = FastList.newInstance();
	
	protected ShugoEmperorVaultPlayerReward getPlayerReward(Integer object) {
		return (ShugoEmperorVaultPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new ShugoEmperorVaultPlayerReward(player.getObjectId()));
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
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 235643: //Indirunerk Jonakak's Supply Box.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
					    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 162002031, 2)); //Shugo Warrior's Minor Salve.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 162002032, 2)); //Shugo Warrior's Greater Salve.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 162002033, 2)); //Shugo Warrior's Minor Adrenaline.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 162002034, 2)); //Shugo Warrior's Greater Adrenaline.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 162002035, 2)); //Shugo Warrior's Minor Salve.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 162002036, 2)); //Shugo Warrior's Greater Salve.
					}
				}
			break;
		}
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000222, storage.getItemCountByItemId(185000222)); //Rusted Vault Key.
		storage.decreaseByItemId(162002031, storage.getItemCountByItemId(162002031)); //Shugo Warrior's Minor Salve.
		storage.decreaseByItemId(162002032, storage.getItemCountByItemId(162002032)); //Shugo Warrior's Greater Salve.
		storage.decreaseByItemId(162002033, storage.getItemCountByItemId(162002033)); //Shugo Warrior's Minor Adrenaline.
		storage.decreaseByItemId(162002034, storage.getItemCountByItemId(162002034)); //Shugo Warrior's Greater Adrenaline.
		storage.decreaseByItemId(162002035, storage.getItemCountByItemId(162002035)); //Shugo Warrior's Minor Salve.
		storage.decreaseByItemId(162002036, storage.getItemCountByItemId(162002036)); //Shugo Warrior's Greater Salve.
	}
	
	@Override
	public void onDie(Npc npc) {
		int score = 0;
		int npcId = npc.getNpcId();
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
		    case 235629: //Intruder Skirmisher.
			case 235630: //Intruder Scout.
				score += 180;
				despawnNpc(npc);
			break;
			case 235631: //Brainwashed Peon.
			    score += 160;
				despawnNpc(npc);
			break;
			case 235633: //Intruder Marksman.
			    score += 1070;
				despawnNpc(npc);
			break;
			case 235634: //Watchman Hokuruki.
				score += 2040;
				despawnNpc(npc);
				vaultTaskB1.cancel(true);
				vaultTaskB2.cancel(true);
				vaultTaskB3.cancel(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
				//Gradi's first officer has appeared! Prepare for Captain Mirez!
				sendMsgByRace(1402678, Race.PC_ALL, 4000);
				//The First Henchman of Gradi appears!
				sendMsgByRace(1402884, Race.PC_ALL, 6000);
				spawn(832924, 469.53888f, 657.56543f, 396.91852f, (byte) 0, 432); //Opened Vault Door.
				spawn(235640, 360.0000f, 757.0000f, 398.0000f, (byte) 104); //Captain Mirez.
			break;
			case 235635: //Intruder Challenger.
			case 235650: //Intruder Assassin.
				score += 700;
				despawnNpc(npc);
			break;
			case 235637: //Intruder Guard.
				score += 820;
				despawnNpc(npc);
			break;
			case 235640: //Captain Mirez.
				score += 12000;
				despawnNpc(npc);
				//Gradi's second officer has appeared! Prepare for Longknife Zodica!
				sendMsgByRace(1402679, Race.PC_ALL, 0);
				//The Second Henchman of Gradi appears!
				sendMsgByRace(1402885, Race.PC_ALL, 2000);
				spawn(235685, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Longknife Zodica.
			break;
			case 235641: //Shugo Turncoat.
				score += 660;
				despawnNpc(npc);
			break;
			case 235647: //Grand Commander Gradi.
				score += 400000;
				despawnNpc(npc);
				//All the intruders have fled. You've cleared the Vault!
				sendMsgByRace(1402681, Race.PC_ALL, 2000);
                spawn(832932, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //The Shugo Emperor's Butler.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    stopInstance(player);
						    }
					    });
					}
				}, 3000);
			break;
			case 235649: //Intruder Sniper.
				score += 760;
				despawnNpc(npc);
			break;
			case 235651: //Intruder Gladiator.
				score += 1400;
				despawnNpc(npc);
			break;
			case 235652: //Intruder Warrior.
			case 235653: //Intruder Sharpeye.
				score += 250;
				despawnNpc(npc);
			break;
			case 235660: //Ruthless Jabaraki.
				score += 1740;
				despawnNpc(npc);
				vaultTaskA1.cancel(true);
				vaultTaskA2.cancel(true);
				vaultTaskA3.cancel(true);
				vaultTaskA4.cancel(true);
				doors.get(431).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
			break;
			case 235680: //Intruder Brawler.
			case 235681: //Intruder Lookout.
				score += 530;
				despawnNpc(npc);
			break;
			case 235683: //Elite Captain Rupasha.
				score += 272000;
				despawnNpc(npc);
				//Greedy Gradi, the intruder commander, has appeared. Get ready for a fight!
				sendMsgByRace(1402743, Race.PC_ALL, 0);
				//The Fifth Henchman of Gradi appears!
				sendMsgByRace(1402888, Race.PC_ALL, 2000);
				spawn(235647, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Grand Commander Gradi.
			break;
			case 235684: //Sorcerer Budyn.
				score += 48000;
				despawnNpc(npc);
				//Gradi's final officer has appeared! Prepare for Elite Captain Rupasha!
				sendMsgByRace(1402742, Race.PC_ALL, 0);
				//The Fourth Henchman of Gradi appears!
				sendMsgByRace(1402887, Race.PC_ALL, 2000);
				spawn(235683, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Elite Captain Rupasha.
			break;
			case 235685: //Longknife Zodica.
				score += 14400;
				despawnNpc(npc);
				//Gradi's third officer has appeared! Prepare for Sorcerer Budyn!
				sendMsgByRace(1402680, Race.PC_ALL, 0);
				//The Third Henchman of Gradi appears!
				sendMsgByRace(1402886, Race.PC_ALL, 2000);
				spawn(235684, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Sorcerer Budyn.
			break;
		} if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addNpcKill();
			instanceReward.addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		}
	}
	
	private void rushVault(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player: instance.getPlayersInside()) {
						npc.setTarget(player);
						((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
						npc.setState(1);
						npc.getMoveController().moveToTargetObject();
						PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
					}
				}
			}
		}, 1000);
	}
	
   /**
	* 1st Room.
	*/
	private void startVaultA1() {
		vaultTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235629, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushVault((Npc)spawn(235629, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushVault((Npc)spawn(235629, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
			}
		}, 1000);
		vaultTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235629, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushVault((Npc)spawn(235629, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushVault((Npc)spawn(235629, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
			}
		}, 10000);
	}
	private void startVaultA2() {
		vaultTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235630, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushVault((Npc)spawn(235630, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushVault((Npc)spawn(235630, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
			}
		}, 1000);
		vaultTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235630, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushVault((Npc)spawn(235630, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushVault((Npc)spawn(235630, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
			}
		}, 10000);
	}
	private void startVaultA3() {
		vaultTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235631, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushVault((Npc)spawn(235631, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushVault((Npc)spawn(235631, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
			}
		}, 1000);
		vaultTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235631, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushVault((Npc)spawn(235631, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushVault((Npc)spawn(235631, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
			}
		}, 10000);
	}
	private void startVaultA4() {
		vaultTaskA4 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235629, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushVault((Npc)spawn(235630, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushVault((Npc)spawn(235631, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
			}
		}, 1000);
		vaultTaskA4 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235629, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushVault((Npc)spawn(235630, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushVault((Npc)spawn(235631, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
			}
		}, 10000);
	}
	
   /**
	* 3rd Room.
	*/
	private void startVaultB1() {
		vaultTaskB1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235632, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushVault((Npc)spawn(235632, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushVault((Npc)spawn(235632, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
			}
		}, 1000);
		vaultTaskB1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235632, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushVault((Npc)spawn(235632, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushVault((Npc)spawn(235632, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
			}
		}, 10000);
	}
	private void startVaultB2() {
		vaultTaskB2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235633, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushVault((Npc)spawn(235633, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushVault((Npc)spawn(235633, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
			}
		}, 1000);
		vaultTaskB2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235633, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushVault((Npc)spawn(235633, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushVault((Npc)spawn(235633, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
			}
		}, 10000);
	}
	private void startVaultB3() {
		vaultTaskB3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235649, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushVault((Npc)spawn(235649, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushVault((Npc)spawn(235649, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
			}
		}, 1000);
		vaultTaskB3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushVault((Npc)spawn(235649, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushVault((Npc)spawn(235649, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushVault((Npc)spawn(235649, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
			}
		}, 10000);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(21829);
		effectController.removeEffect(21830);
		effectController.removeEffect(21831);
		effectController.removeEffect(21832);
		effectController.removeEffect(21833);
		effectController.removeEffect(21834);
		///////////////////////////////////////////
		SkillLearnService.removeSkill(player, 324);
		SkillLearnService.removeSkill(player, 325);
		SkillLearnService.removeSkill(player, 326);
		SkillLearnService.removeSkill(player, 327);
		SkillLearnService.removeSkill(player, 328);
		SkillLearnService.removeSkill(player, 329);
		SkillLearnService.removeSkill(player, 331);
		SkillLearnService.removeSkill(player, 332);
		SkillLearnService.removeSkill(player, 334);
		SkillLearnService.removeSkill(player, 335);
		SkillLearnService.removeSkill(player, 336);
		SkillLearnService.removeSkill(player, 337);
		SkillLearnService.removeSkill(player, 338);
		SkillLearnService.removeSkill(player, 398);
		SkillLearnService.removeSkill(player, 400);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400255, player.getName()));
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
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
		} else if (totalPoints >= 463800) { //Rank A.
			rank = 2;
		} else if (totalPoints >= 165100) { //Rank B.
			rank = 3;
		} else if (totalPoints >= 54000) { //Rank C.
			rank = 4;
		} else if (totalPoints >= 180) { //Rank D.
			rank = 5;
		} else {
			rank = 6;
		}
		return rank;
	}
	
	protected void startInstanceTask() {
		vaultTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    stopInstance(player);
				    }
			    });
				spawn(832950, 362.71112f, 760.5198f, 398.42203f, (byte) 104); //The Shugo Emperor's Exit.
            }
        }, 600000));
    }
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 430) {
			startInstanceTask();
			doors.get(430).setOpen(true);
			//The member recruitment window has passed. You cannot recruit any more members.
			sendMsgByRace(1401181, Race.PC_ALL, 5000);
			//Intruders detected in the Vault!
			sendMsgByRace(1402677, Race.PC_ALL, 10000);
			//The player has 1 min to prepare !!! [Timer Red]
			if ((timerPrepare != null) && (!timerPrepare.isDone() || !timerPrepare.isCancelled())) {
				//Start the instance time !!! [Timer White]
				startMainInstanceTimer();
			}
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startVaultA1();
					startVaultA2();
					startVaultA3();
					startVaultA4();
					//Prepare for combat! More enemies swarming in!
					sendMsgByRace(1402832, Race.PC_ALL, 8000);
				}
			}, 20000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startVaultA1();
					startVaultA2();
					startVaultA3();
					startVaultA4();
				}
			}, 40000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startVaultA1();
					startVaultA2();
					startVaultA3();
					startVaultA4();
				}
			}, 60000);
		} else if (doorId == 428) {
			doors.get(428).setOpen(true);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startVaultB1();
					startVaultB2();
					startVaultB3();
					//Prepare for combat! More enemies swarming in!
					sendMsgByRace(1402832, Race.PC_ALL, 8000);
				}
			}, 20000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startVaultB1();
					startVaultB2();
					startVaultB3();
				}
			}, 40000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startVaultB1();
					startVaultB2();
					startVaultB3();
				}
			}, 60000);
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		ShugoEmperorVaultPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		} if (spawnRace == null) {
			spawnRace = player.getRace();
			spawnVault2Race();
		}
		startPrepareTimer();
	}
	
	private void spawnVault2Race() {
	    final int templarerkSoul = spawnRace == Race.ASMODIANS ? 833494 : 833491; //Brave Templarerk's Soul.
        final int gladiatorerkSoul = spawnRace == Race.ASMODIANS ? 833495 : 833492; //Furious Gladiatorerk's Soul.
        final int sorcererkSoul = spawnRace == Race.ASMODIANS ? 833496 : 833493; //Roiling Sorcererk's Soul.
		spawn(templarerkSoul, 541.1751f, 302.90582f, 400.49493f, (byte) 76);
		spawn(templarerkSoul, 465.46735f, 638.66113f, 395.375f, (byte) 100);
        spawn(templarerkSoul, 420.09814f, 688.6983f, 398.42203f, (byte) 14);
		spawn(gladiatorerkSoul, 543.03845f, 302.22098f, 400.48618f, (byte) 89);
		spawn(gladiatorerkSoul, 467.3807f, 640.5601f, 395.41f, (byte) 112);
        spawn(gladiatorerkSoul, 417.19376f, 691.69653f, 398.42203f, (byte) 14);
		spawn(sorcererkSoul, 544.87866f, 302.53723f, 400.55246f, (byte) 98);
		spawn(sorcererkSoul, 467.43195f, 643.59753f, 395.5f, (byte) 6);
        spawn(sorcererkSoul, 414.0031f, 694.8936f, 398.42203f, (byte) 14);
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
	
	private void rewardGroup() {
		for (Player p: instance.getPlayersInside()) {
			doReward(p);
		}
	}
	
	@Override
	public void doReward(Player player) {
		ShugoEmperorVaultPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int vaultRank = instanceReward.getRank();
			switch (vaultRank) {
				case 1: //Rank S
					playerReward.setRustedVaultKey(6);
					ItemService.addItem(player, 185000222, 6); //Rusted Vault Key.
				break;
				case 2: //Rank A
					playerReward.setRustedVaultKey(4);
					ItemService.addItem(player, 185000222, 4); //Rusted Vault Key.
				break;
				case 3: //Rank B
					playerReward.setRustedVaultKey(3);
					ItemService.addItem(player, 185000222, 3); //Rusted Vault Key.
				break;
				case 4: //Rank C
					playerReward.setRustedVaultKey(2);
					ItemService.addItem(player, 185000222, 2); //Rusted Vault Key.
				break;
			}
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new ShugoEmperorVaultReward(mapId, instanceId);
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
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = vaultTask.head(), end = vaultTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
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
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	private void sendMsg(final String str) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendWhiteMessageOnCenter(player, str);
			}
		});
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