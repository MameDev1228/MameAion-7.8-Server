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
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.WickedGracheniVaultReward;
import com.aionemu.gameserver.model.instance.playerreward.WickedGracheniVaultPlayerReward;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
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
/** Source: https://www.youtube.com/watch?v=lVYaBLu-zOU
/****/

@InstanceID(301750000)
public class Wicked_Gracheni_Vault extends GeneralInstanceHandler
{
	private int rank;
	private long startTime;
	private Future<?> gracheniTaskA1;
	private Future<?> gracheniTaskA2;
	private Future<?> gracheniTaskA3;
	private Future<?> gracheniTaskA4;
	/////////////////////////////////
	private Future<?> gracheniTaskB1;
	private Future<?> gracheniTaskB2;
	private Future<?> gracheniTaskB3;
	private Future<?> timerPrepare;
	private Future<?> timerInstance;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	//Preparation Time.
	private int prepareTimerSeconds = 60000; //...1Min
	//Duration Instance Time.
	private int instanceTimerSeconds = 600000; //...10Min
	private WickedGracheniVaultReward instanceReward;
	private final FastList<Future<?>> gracheniTask = FastList.newInstance();
	
	protected WickedGracheniVaultPlayerReward getPlayerReward(Integer object) {
		return (WickedGracheniVaultPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new WickedGracheniVaultPlayerReward(player.getObjectId()));
	}
	
	private boolean containPlayer(Integer object) {
		return instanceReward.containPlayer(object);
	}
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185001062, storage.getItemCountByItemId(185001062)); //Gracheni's Treasure Chest Key.
	}
	
	@Override
	public void onDie(Npc npc) {
		int score = 0;
		int npcId = npc.getNpcId();
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 858451: //Combat Captain Mynez.
				score += 12000;
				despawnNpc(npc);
				//Gracheni's second minion is here! Staff Captain Bernadi has appeared!
				sendMsgByRace(1405201, Race.PC_ALL, 0);
				spawn(858452, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Staff Captain Bernadi.
			break;
			case 858452: //Staff Captain Bernadi.
				score += 14400;
				despawnNpc(npc);
				//Gracheni's third minion is here! Maintenance Chief Notaki has appeared!
				sendMsgByRace(1405202, Race.PC_ALL, 0);
				spawn(858453, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Maintenance Chief Notaki.
			break;
			case 858453: //Maintenance Chief Notaki.
				score += 16000;
				despawnNpc(npc);
				//Wicked Baroness Gracheni has appeared!
				sendMsgByRace(1405204, Race.PC_ALL, 0);
				spawn(858455, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Baroness Gracheni.
			break;
			case 858455: //Baroness Gracheni.
				score += 88000;
				despawnNpc(npc);
				//Wicked Baron Gracheni has appeared!
				sendMsgByRace(1405205, Race.PC_ALL, 0);
				spawn(858456, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Baron Gracheni.
			break;
			case 858456: //Baron Gracheni.
				score += 224000;
				despawnNpc(npc);
				gracheniTreasures();
				despawnNpcs(instance.getNpcs(858458));
				despawnNpcs(instance.getNpcs(858465));
				despawnNpcs(instance.getNpcs(858466));
				despawnNpcs(instance.getNpcs(858467));
				despawnNpcs(instance.getNpcs(858468));
				despawnNpcs(instance.getNpcs(858469));
				//The Shulacks couldn't withstand the Daeva Ranger's attack. All have fled.
				sendMsgByRace(1405203, Race.PC_ALL, 2000);
				spawn(838366, 359.0000f, 745.0000f, 398.0000f, (byte) 105); //Weakened Green Ranger.
				spawn(838367, 359.0000f, 751.0000f, 398.0000f, (byte) 105); //Weakened Green Ranger.
				spawn(838369, 374.0000f, 760.0000f, 398.0000f, (byte) 105); //Weakened Black Ranger.
				spawn(838370, 368.0000f, 760.0000f, 398.0000f, (byte) 104); //Weakened Black Ranger.
				spawn(832950, 362.0000f, 760.0000f, 398.0000f, (byte) 104); //Vault Exit.
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
			case 858458: //Gracheni Elite Soldier.
				score += 1400;
				despawnNpc(npc);
			break;
			case 858459: //Gracheni Excavator.
				score += 660;
				despawnNpc(npc);
			break;
			case 858460: //Gracheni Guard.
			case 858461: //Gracheni Scout.
				score += 180;
				despawnNpc(npc);
			break;
			case 858462: //Gracheni Elite Sniper.
			    score += 1070;
				despawnNpc(npc);
			break;
			case 858463: //Gracheni Sniper.
				score += 760;
				despawnNpc(npc);
			break;
			case 858464: //Prophet Lataki.
				score += 1740;
				despawnNpc(npc);
				gracheniTaskA1.cancel(true);
				gracheniTaskA2.cancel(true);
				gracheniTaskA3.cancel(true);
				gracheniTaskA4.cancel(true);
				doors.get(431).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
			break;
			case 858465: //Gracheni Shaman.
			case 858466: //Gracheni Assassin.
				score += 530;
				despawnNpc(npc);
			break;
			case 858468: //Gracheni Elite Bodyguard.
				score += 820;
				despawnNpc(npc);
			break;
			case 858467: //Gracheni Elite Scout.
			case 858469: //Gracheni Elite Assassin.
				score += 700;
				despawnNpc(npc);
			break;
			case 858470: //Sentinel Leader Hotaki.
				score += 2040;
				despawnNpc(npc);
				gracheniTaskB1.cancel(true);
				gracheniTaskB2.cancel(true);
				gracheniTaskB3.cancel(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
				//Gracheni's first minion is here! Combat Captain Mynez has appeared!
				sendMsgByRace(1405200, Race.PC_ALL, 4000);
				//Justice Ranger! Activate Special Attack!
				sendMsgByRace(1405207, Race.PC_ALL, 6000);
				spawn(838374, 469.0000f, 657.0000f, 396.0000f, (byte) 0, 432); //Opened Vault Door.
				spawn(858451, 360.0000f, 757.0000f, 398.0000f, (byte) 104); //Combat Captain Mynez.
			break;
		} if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addNpcKill();
			instanceReward.addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		}
	}
	
	private void rushGracheni(final Npc npc) {
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
	private void startGracheniA1() {
		gracheniTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858460, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushGracheni((Npc)spawn(858460, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushGracheni((Npc)spawn(858460, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
			}
		}, 1000);
		gracheniTaskA1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858460, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushGracheni((Npc)spawn(858460, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
				rushGracheni((Npc)spawn(858460, 532.0000f, 387.0000f, 396.0000f, (byte) 6));
			}
		}, 10000);
	}
	private void startGracheniA2() {
		gracheniTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858461, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushGracheni((Npc)spawn(858461, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushGracheni((Npc)spawn(858461, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
			}
		}, 1000);
		gracheniTaskA2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858461, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushGracheni((Npc)spawn(858461, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
				rushGracheni((Npc)spawn(858461, 564.0000f, 378.0000f, 396.0000f, (byte) 54));
			}
		}, 10000);
	}
	private void startGracheniA3() {
		gracheniTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858462, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushGracheni((Npc)spawn(858462, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushGracheni((Npc)spawn(858462, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
			}
		}, 1000);
		gracheniTaskA3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858462, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushGracheni((Npc)spawn(858462, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
				rushGracheni((Npc)spawn(858462, 573.0000f, 404.0000f, 397.0000f, (byte) 67));
			}
		}, 10000);
	}
	private void startGracheniA4() {
		gracheniTaskA4 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858460, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushGracheni((Npc)spawn(858461, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushGracheni((Npc)spawn(858462, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
			}
		}, 1000);
		gracheniTaskA4 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858460, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushGracheni((Npc)spawn(858461, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
				rushGracheni((Npc)spawn(858462, 534.0000f, 411.0000f, 396.0000f, (byte) 115));
			}
		}, 10000);
	}
	
   /**
	* 3rd Room.
	*/
	private void startGracheniB1() {
		gracheniTaskB1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858462, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushGracheni((Npc)spawn(858462, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushGracheni((Npc)spawn(858462, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
			}
		}, 1000);
		gracheniTaskB1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858462, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushGracheni((Npc)spawn(858462, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
				rushGracheni((Npc)spawn(858462, 479.0000f, 626.0000f, 395.0000f, (byte) 20));
			}
		}, 10000);
	}
	private void startGracheniB2() {
		gracheniTaskB2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858463, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushGracheni((Npc)spawn(858463, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushGracheni((Npc)spawn(858463, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
			}
		}, 1000);
		gracheniTaskB2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858463, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushGracheni((Npc)spawn(858463, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
				rushGracheni((Npc)spawn(858463, 496.0000f, 652.0000f, 395.0000f, (byte) 81));
			}
		}, 10000);
	}
	private void startGracheniB3() {
		gracheniTaskB3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858463, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushGracheni((Npc)spawn(858463, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushGracheni((Npc)spawn(858463, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
			}
		}, 1000);
		gracheniTaskB3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rushGracheni((Npc)spawn(858463, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushGracheni((Npc)spawn(858463, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
				rushGracheni((Npc)spawn(858463, 472.0000f, 651.0000f, 395.0000f, (byte) 107));
			}
		}, 10000);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(19289);
		effectController.removeEffect(19290);
		effectController.removeEffect(19299);
		effectController.removeEffect(19300);
		effectController.removeEffect(19303);
		effectController.removeEffect(19304);
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
		gracheniTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    stopInstance(player);
				    }
			    });
				spawn(832950, 362.0000f, 760.0000f, 398.0000f, (byte) 104); //Vault Exit.
            }
        }, 600000));
    }
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		PlayerEffectController effectController = player.getEffectController();
		switch (npc.getNpcId()) {
			case 820539: //Red Ranger.
			    if (player.getCommonData().getRace() == Race.ELYOS) {
					redRanger(player);
				    effectController.removeEffect(19299);
					effectController.removeEffect(19300);
					effectController.removeEffect(19303);
					effectController.removeEffect(19304);
				    SkillEngine.getInstance().getSkill(npc, 19289, 1, player).useNoAnimationSkill();
				} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
					redRanger(player);
				    effectController.removeEffect(19299);
					effectController.removeEffect(19300);
					effectController.removeEffect(19303);
					effectController.removeEffect(19304);
				    SkillEngine.getInstance().getSkill(npc, 19290, 1, player).useNoAnimationSkill();
				}
			break;
			case 820540: //Blue Ranger.
				if (player.getCommonData().getRace() == Race.ELYOS) {
					blueRanger(player);
				    effectController.removeEffect(19289);
				    effectController.removeEffect(19290);
					effectController.removeEffect(19303);
				    effectController.removeEffect(19304);
					SkillEngine.getInstance().getSkill(npc, 19299, 1, player).useNoAnimationSkill();
				} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
					blueRanger(player);
					effectController.removeEffect(19289);
				    effectController.removeEffect(19290);
					effectController.removeEffect(19303);
				    effectController.removeEffect(19304);
					SkillEngine.getInstance().getSkill(npc, 19300, 1, player).useNoAnimationSkill();
				}
			break;
			case 820541: //Pink Ranger.
			    if (player.getCommonData().getRace() == Race.ELYOS) {
					pinkRanger(player);
				    effectController.removeEffect(19289);
					effectController.removeEffect(19290);
					effectController.removeEffect(19299);
					effectController.removeEffect(19300);
				    SkillEngine.getInstance().getSkill(npc, 19303, 1, player).useNoAnimationSkill();
				} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
					pinkRanger(player);
				    effectController.removeEffect(19289);
					effectController.removeEffect(19290);
					effectController.removeEffect(19299);
					effectController.removeEffect(19300);
				    SkillEngine.getInstance().getSkill(npc, 19304, 1, player).useNoAnimationSkill();
				}
			break;
		}
	}
	
	public static final void redRanger(final Player player) {
		player.getSkillList().addSkill(player, 324, 1);
		player.getSkillList().addSkill(player, 325, 1);
		player.getSkillList().addSkill(player, 326, 1);
		player.getSkillList().addSkill(player, 327, 1);
		player.getSkillList().addSkill(player, 398, 1);
		///////////////////////////////////////////
		SkillLearnService.removeSkill(player, 328);
		SkillLearnService.removeSkill(player, 329);
		SkillLearnService.removeSkill(player, 331);
		SkillLearnService.removeSkill(player, 332);
		SkillLearnService.removeSkill(player, 400);
		SkillLearnService.removeSkill(player, 334);
		SkillLearnService.removeSkill(player, 335);
		SkillLearnService.removeSkill(player, 336);
		SkillLearnService.removeSkill(player, 337);
		SkillLearnService.removeSkill(player, 338);
	}
	
	public static final void blueRanger(final Player player) {
		player.getSkillList().addSkill(player, 328, 1);
		player.getSkillList().addSkill(player, 329, 1);
		player.getSkillList().addSkill(player, 331, 1);
		player.getSkillList().addSkill(player, 332, 1);
		player.getSkillList().addSkill(player, 400, 1);
		///////////////////////////////////////////
		SkillLearnService.removeSkill(player, 324);
		SkillLearnService.removeSkill(player, 325);
		SkillLearnService.removeSkill(player, 326);
		SkillLearnService.removeSkill(player, 327);
		SkillLearnService.removeSkill(player, 398);
		SkillLearnService.removeSkill(player, 334);
		SkillLearnService.removeSkill(player, 335);
		SkillLearnService.removeSkill(player, 336);
		SkillLearnService.removeSkill(player, 337);
		SkillLearnService.removeSkill(player, 338);
	}
	
	public static final void pinkRanger(final Player player) {
		player.getSkillList().addSkill(player, 334, 1);
		player.getSkillList().addSkill(player, 335, 1);
		player.getSkillList().addSkill(player, 336, 1);
		player.getSkillList().addSkill(player, 337, 1);
		player.getSkillList().addSkill(player, 338, 1);
		/////////////////////////////////////////////
		SkillLearnService.removeSkill(player, 324);
		SkillLearnService.removeSkill(player, 325);
		SkillLearnService.removeSkill(player, 326);
		SkillLearnService.removeSkill(player, 327);
		SkillLearnService.removeSkill(player, 398);
		SkillLearnService.removeSkill(player, 328);
		SkillLearnService.removeSkill(player, 329);
		SkillLearnService.removeSkill(player, 331);
		SkillLearnService.removeSkill(player, 332);
		SkillLearnService.removeSkill(player, 400);
	}
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 430) {
			startInstanceTask();
			doors.get(430).setOpen(true);
			//The member recruitment window has passed. You cannot recruit any more members.
			sendMsgByRace(1401181, Race.PC_ALL, 2500);
			//The righteous Daeva Ranger has been dispatched!
			sendMsgByRace(1405199, Race.PC_ALL, 5000);
			//The Green Ranger and the Black Ranger are preparing their Special Attacks.\nCombine powers to use the Special Attack.
			sendMsgByRace(1405206, Race.PC_ALL, 10000);
			//The player has 1 min to prepare !!! [Timer Red]
			if ((timerPrepare != null) && (!timerPrepare.isDone() || !timerPrepare.isCancelled())) {
				//Start the instance time !!! [Timer White]
				startMainInstanceTimer();
			}
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startGracheniA1();
					startGracheniA2();
					startGracheniA3();
					startGracheniA4();
					//Prepare for combat! More enemies swarming in!
					sendMsgByRace(1402832, Race.PC_ALL, 8000);
				}
			}, 20000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startGracheniA1();
					startGracheniA2();
					startGracheniA3();
					startGracheniA4();
				}
			}, 40000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startGracheniA1();
					startGracheniA2();
					startGracheniA3();
					startGracheniA4();
				}
			}, 60000);
		} else if (doorId == 428) {
			doors.get(428).setOpen(true);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startGracheniB1();
					startGracheniB2();
					startGracheniB3();
					//Prepare for combat! More enemies swarming in!
					sendMsgByRace(1402832, Race.PC_ALL, 8000);
				}
			}, 20000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startGracheniB1();
					startGracheniB2();
					startGracheniB3();
				}
			}, 40000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startGracheniB1();
					startGracheniB2();
					startGracheniB3();
				}
			}, 60000);
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		WickedGracheniVaultPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		}
		startPrepareTimer();
	}
	
	private void gracheniTreasures() {
		spawn(820545, 374.0000f, 766.0000f, 398.0000f, (byte) 97);
		spawn(820545, 402.0000f, 725.0000f, 398.0000f, (byte) 97);
		spawn(820545, 383.0000f, 763.0000f, 398.0000f, (byte) 97);
		spawn(820545, 388.0000f, 716.0000f, 398.0000f, (byte) 97);
		spawn(820545, 415.0000f, 747.0000f, 398.0000f, (byte) 97);
		spawn(820545, 364.0000f, 713.0000f, 398.0000f, (byte) 97);
		spawn(820545, 417.0000f, 736.0000f, 398.0000f, (byte) 97);
		spawn(820545, 405.0000f, 714.0000f, 398.0000f, (byte) 97);
		spawn(820545, 399.0000f, 735.0000f, 398.0000f, (byte) 97);
		spawn(820545, 390.0000f, 706.0000f, 398.0000f, (byte) 97);
		spawn(820545, 361.0000f, 731.0000f, 398.0000f, (byte) 97);
		spawn(820545, 406.0000f, 757.0000f, 398.0000f, (byte) 97);
		spawn(820545, 393.0000f, 756.0000f, 398.0000f, (byte) 97);
		spawn(820545, 379.0000f, 710.0000f, 398.0000f, (byte) 97);
		spawn(820545, 376.0000f, 732.0000f, 398.0000f, (byte) 97);
		spawn(820545, 375.0000f, 751.0000f, 398.0000f, (byte) 97);
		spawn(820545, 388.0000f, 745.0000f, 398.0000f, (byte) 97);
	}
	
	private void gracheniRanger() {
		spawn(820539, 541.0000f, 302.0000f, 400.0000f, (byte) 79);
		spawn(820539, 465.0000f, 638.0000f, 395.0000f, (byte) 100);
        spawn(820539, 420.0000f, 688.0000f, 398.0000f, (byte) 14);
		spawn(820540, 543.0000f, 302.0000f, 400.0000f, (byte) 89);
		spawn(820540, 467.0000f, 640.0000f, 395.0000f, (byte) 112);
        spawn(820540, 417.0000f, 691.0000f, 398.0000f, (byte) 14);
		spawn(820541, 545.0000f, 302.0000f, 400.0000f, (byte) 100);
		spawn(820541, 467.0000f, 643.0000f, 395.0000f, (byte) 6);
        spawn(820541, 414.0000f, 694.0000f, 398.0000f, (byte) 14);
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
		despawnNpcs(instance.getNpcs(701156));
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
		WickedGracheniVaultPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int gracheniRank = instanceReward.getRank();
			switch (gracheniRank) {
				case 1: //Rank S
				    playerReward.setScoreAP(100000);
					playerReward.setGracheniTreasureChestKey(6);
					ItemService.addItem(player, 185001062, 6);
					ItemService.addItem(player, 186020057, 20);
				break;
				case 2: //Rank A
				    playerReward.setScoreAP(80000);
					playerReward.setGracheniTreasureChestKey(4);
					ItemService.addItem(player, 185001062, 4);
					ItemService.addItem(player, 186020057, 20);
				break;
				case 3: //Rank B
				    playerReward.setScoreAP(50000);
					playerReward.setGracheniTreasureChestKey(3);
					ItemService.addItem(player, 185001062, 3);
					ItemService.addItem(player, 186020057, 20);
				break;
				case 4: //Rank C
					playerReward.setGracheniTreasureChestKey(2);
					ItemService.addItem(player, 185001062, 2);
					ItemService.addItem(player, 186020057, 20);
				break;
			}
			AbyssPointsService.addAp(player, playerReward.getScoreAP());
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new WickedGracheniVaultReward(mapId, instanceId);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
		gracheniRanger();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = gracheniTask.head(), end = gracheniTask.tail(); (n = n.getNext()) != end; ) {
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
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
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