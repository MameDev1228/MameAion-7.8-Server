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
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.EvergaleCanyonReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.EvergaleCanyonPlayerReward;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
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
/** Source: https://aionpowerbook.com/powerbook/Windy_Gorge
/****/

@InstanceID(302350000)
public class Evergale_Canyon extends GeneralInstanceHandler
{
	private long instanceTime;
	private Map<Integer, StaticDoor> doors;
	private Race RaceKilledCommander = null;
	private float loosingGroupMultiplier = 1;
	private boolean isInstanceDestroyed = false;
	protected EvergaleCanyonReward evergaleCanyonReward;
	
	private final FastList<Future<?>> mahotTask = FastList.newInstance();
	private final FastList<Future<?>> daglonTask = FastList.newInstance();
	private final FastList<Future<?>> furtiveKaisanTask = FastList.newInstance();
	private final FastList<Future<?>> evergaleCanyonTask = FastList.newInstance();
	private final FastList<Future<?>> corruptBagaturTask = FastList.newInstance();
	
	protected EvergaleCanyonPlayerReward getPlayerReward(Player player) {
        evergaleCanyonReward.regPlayerReward(player);
        return (EvergaleCanyonPlayerReward) evergaleCanyonReward.getPlayerReward(player.getObjectId());
    }
	
    private boolean containPlayer(Integer object) {
        return evergaleCanyonReward.containPlayer(object);
    }
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(186000470, storage.getItemCountByItemId(186000470)); //War Points.
	}
	
	protected void startInstanceTask() {
		instanceTime = System.currentTimeMillis();
        evergaleCanyonReward.setInstanceStartTime();
		evergaleCanyonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!evergaleCanyonReward.isRewarded()) {
				    openFirstDoors();
				    //The member recruitment window has passed. You cannot recruit any more members.
				    sendMsgByRace(1401181, Race.PC_ALL, 5000);
                    evergaleCanyonReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
                    sendInstanceInfoPacket();
					//Teleport Statue.
				    sp(835273, 446.0000f, 752.0000f, 334.0000f, (byte) 0, 198, 0, 0, null);
					//Teleport Statue.
				    sp(835286, 1050.0000f, 752.0000f, 334.0000f, (byte) 0, 236, 0, 0, null);
					//Teleport Statue.
				    sp(835411, 719.0000f, 396.0000f, 305.0000f, (byte) 0, 253, 0, 0, null);
				    //Teleport Statue.
				    sp(835412, 746.0000f, 850.0000f, 347.0000f, (byte) 0, 65, 10000, 0, null);
				    //Teleport Statue.
				    sp(835413, 451.0000f, 1079.0000f, 347.0000f, (byte) 0, 55, 15000, 0, null);
				    //Teleport Statue.
				    sp(835414, 1035.0000f, 1065.0000f, 350.0000f, (byte) 0, 117, 20000, 0, null);
				}
            }
        }, 90000));
		evergaleCanyonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				sendInstanceInfoPacket();
				//An element of the 2nd stage was added.
				sendMsgByRace(1404174, Race.PC_ALL, 0);
				//Mahot has appeared at the Eon Anvil.
				sendMsgByRace(1404254, Race.PC_ALL, 5000);
				//Daglon has appeared at the Eternal Anvil.
				sendMsgByRace(1404255, Race.PC_ALL, 10000);
				//Furtive Kaisan has appeared at the remaining altar.
				sendMsgByRace(1404275, Race.PC_ALL, 15000);
				//Corrupt Bagatur has appeared at the Jotun Garden.
				sendMsgByRace(1404276, Race.PC_ALL, 20000);
				sp(654840, 330.0000f, 957.0000f, 353.0000f, (byte) 82, 5000);
				sp(654841, 1185.0000f, 957.0000f, 368.0000f, (byte) 111, 10000);
				sp(654842, 743.0000f, 487.0000f, 305.0000f, (byte) 23, 15000);
            }
        }, 120000));
		evergaleCanyonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				sendInstanceInfoPacket();
				//Corrupt Bagatur has appeared at the Jotun Garden.
				sendMsgByRace(1404173, Race.PC_ALL, 0);
            	sp(654843, 747.0000f, 1029.0000f, 334.0000f, (byte) 90, 0);
            }
        }, 480000));
		evergaleCanyonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
            	if (!evergaleCanyonReward.isRewarded()) {
					Race winnerRace = evergaleCanyonReward.getWinnerRaceByScore();
					stopInstance(winnerRace);
				}
            }
        }, 1800000));
    }
	
	protected void stopInstance(Race race) {
        stopMahotTask();
		stopDaglonTask();
		stopInstanceTask();
		stopFurtiveKaisanTask();
		stopCorruptBagaturTask();
        evergaleCanyonReward.setWinnerRace(race);
        evergaleCanyonReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
        reward();
		sendRewardPacket();
        sendScoreTypePacket();
    }
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
        if (!containPlayer(player.getObjectId())) {
            evergaleCanyonReward.regPlayerReward(player);
        }
        sendPreparingPacket(player);
    }
	
	public void sendPreparingPacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(6, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, getTime(), evergaleCanyonReward, player.getObjectId(), 0, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            }
        });
	}
	public void sendScoreTypePacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(2, getTime(), evergaleCanyonReward, player.getObjectId()));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendRewardPacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(5, getTime(), evergaleCanyonReward, player.getObjectId()));
            }
        });
    }
	public void sendInstanceInfoPacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendNpcScorePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(11, getTime(), evergaleCanyonReward, player.getObjectId()));
            }
        });
    }
	public void sendPlayerLeavePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(8, getTime(), evergaleCanyonReward, player.getObjectId()));
            }
        });
    }
	public void sendPlayerDiePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, getTime(), evergaleCanyonReward, player.getObjectId(), 100, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, getTime(), evergaleCanyonReward, player.getObjectId(), 100, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, getTime(), evergaleCanyonReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendPlayerRevivedPacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, getTime(), evergaleCanyonReward, player.getObjectId(), 0, player.getRace().getRaceId()));
            }
        });
    }
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		evergaleCanyonReward = new EvergaleCanyonReward(mapId, instanceId, instance);
        evergaleCanyonReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		startInstanceTask();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(835355, 725.0000f, 575.0000f, 321.0000f, (byte) 0, 105); //Firmly Closed Door.
				spawn(835356, 772.0000f, 614.0000f, 321.0000f, (byte) 0, 389); //Firmly Closed Door.
			break;
			case 2:
				spawn(835356, 725.0000f, 575.0000f, 321.0000f, (byte) 0, 105); //Firmly Closed Door.
				spawn(835355, 772.0000f, 614.0000f, 321.0000f, (byte) 0, 389); //Firmly Closed Door.
			break;
		}
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
			EvergaleCanyonPlayerReward playerReward = evergaleCanyonReward.getPlayerReward(player.getObjectId());
			int abyssPoint = 360000;
			int gloryPoint = 200;
			int expPoint = 10000;
			playerReward.setRewardAp((int) abyssPoint);
            playerReward.setRewardGp((int) gloryPoint);
			playerReward.setRewardExp((int) expPoint);
			if (player.getRace().equals(evergaleCanyonReward.getWinnerRace())) {
                abyssPoint += evergaleCanyonReward.AbyssReward(true, isCommanderKilled(player.getRace()));
                gloryPoint += evergaleCanyonReward.GloryReward(true, isCommanderKilled(player.getRace()));
				expPoint += evergaleCanyonReward.ExpReward(true, isCommanderKilled(player.getRace()));
                playerReward.setBonusAp(evergaleCanyonReward.AbyssReward(true, isCommanderKilled(player.getRace())));
                playerReward.setBonusGp(evergaleCanyonReward.GloryReward(true, isCommanderKilled(player.getRace())));
				playerReward.setBonusExp(evergaleCanyonReward.ExpReward(true, isCommanderKilled(player.getRace())));
				playerReward.setWarriorMedalBundle(188070175);
				playerReward.setLegendaryRidium(152080000);
				playerReward.setBattlefieldMinionBundle(188070820);
				playerReward.setBattlefieldPvPEnchantmentStoneBundle(188070819);
			} else {
                abyssPoint += evergaleCanyonReward.AbyssReward(false, isCommanderKilled(player.getRace()));
                gloryPoint += evergaleCanyonReward.GloryReward(false, isCommanderKilled(player.getRace()));
				expPoint += evergaleCanyonReward.ExpReward(false, isCommanderKilled(player.getRace()));
				playerReward.setRewardAp(evergaleCanyonReward.AbyssReward(false, isCommanderKilled(player.getRace())));
                playerReward.setRewardGp(evergaleCanyonReward.GloryReward(false, isCommanderKilled(player.getRace())));
				playerReward.setRewardExp(evergaleCanyonReward.ExpReward(false, isCommanderKilled(player.getRace())));
				playerReward.setWarriorMedalBundle(188070175);
				playerReward.setBattlefieldPvPEnchantmentStoneBundle(188070819);
            }
			ItemService.addItem(player, 188070175, 1);
            ItemService.addItem(player, 152080000, 10);
			ItemService.addItem(player, 188070820, 2);
			ItemService.addItem(player, 188070819, 2);
			AbyssPointsService.addAp(player, (int) abyssPoint);
            AbyssPointsService.addGp(player, (int) gloryPoint);
            player.getCommonData().addExp(expPoint, RewardType.HUNTING);
        } for (Npc npc: instance.getNpcs()) {
			npc.getController().onDelete();
		}
        ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player: instance.getPlayersInside()) {
						onExitInstance(player);
					}
					AutoGroupService.getInstance().unRegisterInstance(instanceId);
				}
			}
		}, 30000);
    }
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(11218);
		effectController.removeEffect(11219);
		effectController.removeEffect(11220);
		effectController.removeEffect(11221);
		effectController.removeEffect(11222);
		effectController.removeEffect(11223);
		effectController.removeEffect(11224);
		effectController.removeEffect(11225);
		effectController.removeEffect(11226);
		effectController.removeEffect(11227);
		effectController.removeEffect(11228);
		effectController.removeEffect(11229);
		effectController.removeEffect(11230);
		effectController.removeEffect(11231);
		effectController.removeEffect(11232);
		effectController.removeEffect(11233);
		effectController.removeEffect(11234);
		effectController.removeEffect(11235);
		effectController.removeEffect(11236);
		effectController.removeEffect(11237);
		effectController.removeEffect(11238);
		effectController.removeEffect(11239);
		effectController.removeEffect(11240);
		effectController.removeEffect(11241);
		effectController.removeEffect(11242);
		effectController.removeEffect(11243);
		effectController.removeEffect(11244);
		effectController.removeEffect(11245);
		effectController.removeEffect(11246);
		effectController.removeEffect(11247);
		effectController.removeEffect(11266); //Bagatur's Power.
		effectController.removeEffect(11267); //Bagatur's Power.
	}
	
	private int getTime() {
        long result = System.currentTimeMillis() - instanceTime;
        if (result < 90000) {
            return (int) (90000 - result);
        } else if (result < 1800000) { //30-Mins
            return (int) (1800000 - (result - 90000));
        }
        return 0;
    }
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        evergaleCanyonReward.portToPosition(player);
		sendPlayerRevivedPacket(player);
		return true;
    }
	
	@Override
    public boolean onDie(Player player, Creature lastAttacker) {
		EvergaleCanyonPlayerReward ownerReward = evergaleCanyonReward.getPlayerReward(player.getObjectId());
		ownerReward.endBoostMoraleEffect(player);
		ownerReward.applyBoostMoraleEffect(player);
        int points = 100;
		sendPlayerDiePacket(player);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), false, 0, 8));
        if (lastAttacker instanceof Player) {
            if (lastAttacker.getRace() != player.getRace()) {
                InstancePlayerReward playerReward = evergaleCanyonReward.getPlayerReward(player.getObjectId());
				if (getPointsByRace(lastAttacker.getRace()).compareTo(getPointsByRace(player.getRace())) < 0) {
                    points *= loosingGroupMultiplier;
                } else if (loosingGroupMultiplier == 10 || playerReward.getPoints() == 0) {
                    points = 0;
                }
				ItemService.addItem(player, 186000470, 100); //War Points.
                updateScore((Player) lastAttacker, player, points, true);
            }
        }
        updateScore(player, player, -points, false);
        return true;
    }
	
	private boolean isCommanderKilled(Race PlayerRace) {
    	if (PlayerRace == RaceKilledCommander) {
    		return true;
    	}
    	return false;
    }
	
	private MutableInt getPvpKillsByRace(Race race) {
        return evergaleCanyonReward.getPvpKillsByRace(race);
    }
	
    private MutableInt getPointsByRace(Race race) {
        return evergaleCanyonReward.getPointsByRace(race);
    }
	
    private void addPointsByRace(Race race, int points) {
        evergaleCanyonReward.addPointsByRace(race, points);
    }
	
    private void addPvpKillsByRace(Race race, int points) {
        evergaleCanyonReward.addPvpKillsByRace(race, points);
    }
	
    private void addPointToPlayer(Player player, int points) {
        evergaleCanyonReward.getPlayerReward(player.getObjectId()).addPoints(points);
    }
	
    private void addPvPKillToPlayer(Player player) {
        evergaleCanyonReward.getPlayerReward(player.getObjectId()).addPvPKillToPlayer();
    }
	
	protected void updateScore(Player player, Creature target, int points, boolean pvpKill) {
        if (points == 0) {
            return;
        }
        addPointsByRace(player.getRace(), points);
        List<Player> playersToGainScore = new ArrayList<Player>();
        if (target != null && player.isInGroup2()) {
            for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
                if (member.getLifeStats().isAlreadyDead()) {
                    continue;
                } if (MathUtil.isIn3dRange(member, target, GroupConfig.GROUP_MAX_DISTANCE)) {
                    playersToGainScore.add(member);
                }
            }
        } else {
            playersToGainScore.add(player);
        } for (Player playerToGainScore : playersToGainScore) {
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
		sendNpcScorePacket(player);
		if (evergaleCanyonReward.hasCapPoints()) {
            stopInstance(evergaleCanyonReward.getWinnerRaceByScore());
        }
    }
	
	@Override
	public void onDie(Npc npc) {
		int score = 0;
		Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
        if (mostPlayerDamage == null) {
            return;
        }
		Race race = mostPlayerDamage.getRace();
		switch (npc.getNpcId()) {
			case 654899: //Pearl Dracuni.
			case 654900: //Violent Draconute.
			case 654901: //Altar Kalgolem.
			case 654902: //Sky Kirrus.
			case 654903: //Fallen Leaf Manduri.
			case 654904: //Venomthorn Arachna.
			case 654905: //Fungi Rotron.
			case 654906: //Blackclaw Monitor.
			case 654907: //War Klaw.
			case 654908: //Flameshred Cellatu.
			case 654909: //Barbed Bloodwing.
			case 654910: //Vermillion Water Spirit.
			case 654911: //Ruthless Djantkel.
			case 654912: //Thick Stem Oculis.
			    score += 10;
				despawnNpc(npc);
				ItemService.addItem(mostPlayerDamage, 186000470, 10); //War Points.
			break;
			case 654840: //Mahot.
				score += 1000;
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				   //The Elyos eliminated Mahot.
				   sendMsgByRace(1404185, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				} else if (race.equals(Race.ASMODIANS)) {
				   //The Asmodians eliminated Mahot.
				   sendMsgByRace(1404186, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				}
				//"Mahot" appears every 3min after being killed.
				mahotTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
				        //"Mahot" has appeared at the remaining altar.
						sendMsgByRace(1404413, Race.PC_ALL, 0);
						spawn(654840, 330.0000f, 957.0000f, 353.0000f, (byte) 82); //Mahot.
					}
				}, 180000));
			break;
			case 654841: //Daglon.
				score += 1000;
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				   //The Elyos eliminated Daglon.
				   sendMsgByRace(1404187, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				} else if (race.equals(Race.ASMODIANS)) {
				   //The Asmodians eliminated Daglon.
				   sendMsgByRace(1404188, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				}
				//"Daglon" appears every 3min after being killed.
				daglonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//"Daglon" has appeared at the remaining altar.
						sendMsgByRace(1404414, Race.PC_ALL, 0);
						spawn(654841, 1185.0000f, 957.0000f, 368.0000f, (byte) 111); //Daglon.
					}
				}, 180000));
			break;
			case 654842: //Furtive Kaisan.
				score += 1000;
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				   //The Elyos eliminated Furtive Kaisan.
				   sendMsgByRace(1404189, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				} else if (race.equals(Race.ASMODIANS)) {
				   //The Asmodians eliminated Furtive Kaisan.
				   sendMsgByRace(1404190, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				}
				//"Furtive Kaisan" appears every 3min after being killed.
				furtiveKaisanTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//"Furtive Kaisan" has appeared at the remaining altar.
						sendMsgByRace(1404172, Race.PC_ALL, 0);
						spawn(654842, 743.0000f, 487.0000f, 305.0000f, (byte) 23); //Furtive Kaisan.
					}
				}, 180000));
			break;
			case 654843: //Corrupt Bagatur.
				score += 1000;
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				   //The Elyos eliminated Corrupt Kaisan.
				   sendMsgByRace(1404191, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				} else if (race.equals(Race.ASMODIANS)) {
				   //The Asmodians eliminated Corrupt Kaisan.
				   sendMsgByRace(1404192, Race.PC_ALL, 0);
				   ItemService.addItem(mostPlayerDamage, 186000470, 200); //War Points.
				}
				//"Corrupt Bagatur" appears every 8min after being killed.
				corruptBagaturTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//"Corrupt Bagatur" has appeared at the Jotun Garden.
						sendMsgByRace(1404173, Race.PC_ALL, 0);
						spawn(654843, 747.0000f, 1029.0000f, 334.0000f, (byte) 90); //Corrupt Bagatur.
					}
				}, 480000));
			break;
			case 654848: //Archon Detachment Captain.
				score += 2000;
				despawnNpc(npc);
				//The Elyos have eliminated the Archon Detachment Captain.
				sendMsgByRace(1404209, Race.PC_ALL, 0);
				RaceKilledCommander = mostPlayerDamage.getRace();
				ItemService.addItem(mostPlayerDamage, 186000470, 500); //War Points.
			break;
			case 654853: //Guardian Detachment Captain.
				score += 2000;
				despawnNpc(npc);
				//The Asmodians have eliminated the Guardian Detachment Captain.
				sendMsgByRace(1404210, Race.PC_ALL, 0);
				RaceKilledCommander = mostPlayerDamage.getRace();
				ItemService.addItem(mostPlayerDamage, 186000470, 500); //War Points.
			break;
        }
		updateScore(mostPlayerDamage, npc, score, false);
    }
	
	@Override
    public void handleUseItemFinish(Player player, Npc npc) {
		int score = 0;
		switch (npc.getNpcId()) {
		    //Neutral Fragment.
			case 835210: //Artifact Core Fragment.
			case 835211: //Artifact Core Fragment.
			case 835212: //Artifact Core Fragment.
			case 835213: //Artifact Core Fragment.
			case 835214: //Artifact Core Fragment.
			//Elyos Fragment.
			case 835304: //Artifact Core Fragment.
			case 835305: //Artifact Core Fragment.
			case 835306: //Artifact Core Fragment.
			case 835307: //Artifact Core Fragment.
			case 835308: //Artifact Core Fragment.
			//Asmodians Fragment.
			case 835309: //Artifact Core Fragment.
			case 835310: //Artifact Core Fragment.
			case 835311: //Artifact Core Fragment.
			case 835312: //Artifact Core Fragment.
			case 835313: //Artifact Core Fragment.
				score += 200;
				ItemService.addItem(player, 186000470, 200); //War Points.
			break;
        }
		updateScore(player, npc, score, false);
    }
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
	
	@Override
    public void onInstanceDestroy() {
		doors.clear();
		isInstanceDestroyed = true;
		evergaleCanyonReward.clear();
		stopMahotTask();
		stopDaglonTask();
		stopInstanceTask();
		stopFurtiveKaisanTask();
		stopCorruptBagaturTask();
    }
	
	protected void openFirstDoors() {
		openDoor(141);
		openDoor(169);
        openDoor(352);
		openDoor(507);
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
        evergaleCanyonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        evergaleCanyonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        evergaleCanyonTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	
    private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = evergaleCanyonTask.head(), end = evergaleCanyonTask.tail(); (n = n.getNext()) != end;) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	private void stopMahotTask() {
        for (FastList.Node<Future<?>> n = mahotTask.head(), end = mahotTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	private void stopDaglonTask() {
        for (FastList.Node<Future<?>> n = daglonTask.head(), end = daglonTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	private void stopFurtiveKaisanTask() {
        for (FastList.Node<Future<?>> n = furtiveKaisanTask.head(), end = furtiveKaisanTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	private void stopCorruptBagaturTask() {
        for (FastList.Node<Future<?>> n = corruptBagaturTask.head(), end = corruptBagaturTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	@Override
    public InstanceReward<?> getInstanceReward() {
        return evergaleCanyonReward;
    }
	
	@Override
    public void onExitInstance(Player player) {
        TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
    }
	
	@Override
    public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
		sendPlayerLeavePacket(player);
		EvergaleCanyonPlayerReward playerReward = evergaleCanyonReward.getPlayerReward(player.getObjectId());
		playerReward.endBoostMoraleEffect(player);
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400255, player.getName()));
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
    public void onPlayerLogin(Player player) {
        sendNpcScorePacket(player);
    }
}