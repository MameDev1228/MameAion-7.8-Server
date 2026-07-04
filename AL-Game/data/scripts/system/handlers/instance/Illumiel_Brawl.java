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
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.IllumielBattlefieldReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.IllumielBattlefieldPlayerReward;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.effect.*;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
//Source: https://www.youtube.com/watch?v=fSBBEZSrmlI
/****/

@InstanceID(302530000)
public class Illumiel_Brawl extends GeneralInstanceHandler
{
	private long instanceTime;
	private Race RaceKilledTank = null;
	private Map<Integer, StaticDoor> doors;
    private float loosingGroupMultiplier = 1;
    private boolean isInstanceDestroyed = false;
	protected IllumielBattlefieldReward illumielBattlefieldReward;
    protected AtomicBoolean isInstanceStarted = new AtomicBoolean(false);
    private final FastList<Future<?>> illumielTask = FastList.newInstance();
    
    protected IllumielBattlefieldPlayerReward getPlayerReward(Player player) {
        illumielBattlefieldReward.regPlayerReward(player);
        return (IllumielBattlefieldPlayerReward) illumielBattlefieldReward.getPlayerReward(player.getObjectId());
    }
	
    private boolean containPlayer(Integer object) {
        return illumielBattlefieldReward.containPlayer(object);
    }
	
    protected void startInstanceTask() {
    	instanceTime = System.currentTimeMillis();
        illumielBattlefieldReward.setInstanceStartTime();
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!illumielBattlefieldReward.isRewarded()) {
				    openFirstDoors();
					//The Illumiel Battlefield has started.
					sendMsgByRace(1404837, Race.PC_ALL, 0);
					//Kunax has been summoned.
				    sendMsgByRace(1404839, Race.PC_ALL, 5000);
					//The member recruitment window has passed. You cannot recruit any more members.
				    sendMsgByRace(1401181, Race.PC_ALL, 10000);
                    illumielBattlefieldReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
                    sendInstanceInfoPacket();
				}
            }
        }, 90000));
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				sendInstanceInfoPacket();
				recoveringEnergy();
				//Recovering Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404842, Race.PC_ALL, 0);
				sp(656660, 289.84232f, 282.53238f, 92.94253f, (byte) 106, 0, "IDBattleChange_Npc_Li_Fi_All_1");
				sp(656663, 239.72562f, 236.17613f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Da_Fi_All_2");
            }
        }, 150000)); //...2 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				sendInstanceInfoPacket();
				offenseEnergy();
				//Offense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404843, Race.PC_ALL, 0);
            }
        }, 210000)); //...3 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				sendInstanceInfoPacket();
				defenseEnergy();
				//Defense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404844, Race.PC_ALL, 0);
				sp(656661, 286.79077f, 285.77682f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Li_Fi_All_2");
				sp(656664, 242.79443f, 232.61008f, 92.94253f, (byte) 105, 0, "IDBattleChange_Npc_Da_Fi_All_1");
            }
        }, 270000)); //...4 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				recoveringEnergy();
				//Recovering Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404842, Race.PC_ALL, 0);
            }
        }, 330000)); //...5 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				offenseEnergy();
				//Offense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404843, Race.PC_ALL, 0);
				sp(656662, 289.84232f, 282.53238f, 92.94253f, (byte) 106, 0, "IDBattleChange_Npc_Li_Fi_All_1");
				sp(656665, 239.72562f, 236.17613f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Da_Fi_All_2");
            }
        }, 390000)); //...6 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				defenseEnergy();
				//Defense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404844, Race.PC_ALL, 0);
            }
        }, 450000)); //...7 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				recoveringEnergy();
				//Recovering Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404842, Race.PC_ALL, 0);
				sp(656660, 286.79077f, 285.77682f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Li_Fi_All_2");
				sp(656663, 242.79443f, 232.61008f, 92.94253f, (byte) 105, 0, "IDBattleChange_Npc_Da_Fi_All_1");
            }
        }, 510000)); //...8 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				offenseEnergy();
				//Offense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404843, Race.PC_ALL, 0);
            }
        }, 570000)); //...9 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				defenseEnergy();
				//Defense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404844, Race.PC_ALL, 0);
				sp(656661, 289.84232f, 282.53238f, 92.94253f, (byte) 106, 0, "IDBattleChange_Npc_Li_Fi_All_1");
				sp(656664, 239.72562f, 236.17613f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Da_Fi_All_2");
            }
        }, 630000)); //...10 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				recoveringEnergy();
				//Recovering Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404842, Race.PC_ALL, 0);
            }
        }, 690000)); //...11 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				offenseEnergy();
				//Offense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404843, Race.PC_ALL, 0);
				sp(656662, 286.79077f, 285.77682f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Li_Fi_All_2");
				sp(656665, 242.79443f, 232.61008f, 92.94253f, (byte) 105, 0, "IDBattleChange_Npc_Da_Fi_All_1");
            }
        }, 750000)); //...12 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				defenseEnergy();
				//Defense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404844, Race.PC_ALL, 0);
            }
        }, 810000)); //...13 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				recoveringEnergy();
				//Recovering Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404842, Race.PC_ALL, 0);
				sp(656660, 289.84232f, 282.53238f, 92.94253f, (byte) 106, 0, "IDBattleChange_Npc_Li_Fi_All_1");
				sp(656663, 239.72562f, 236.17613f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Da_Fi_All_2");
            }
        }, 870000)); //...14 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				offenseEnergy();
				//Offense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404843, Race.PC_ALL, 0);
            }
        }, 930000)); //...15 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				defenseEnergy();
				//Defense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404844, Race.PC_ALL, 0);
				sp(656661, 286.79077f, 285.77682f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Li_Fi_All_2");
				sp(656664, 242.79443f, 232.61008f, 92.94253f, (byte) 105, 0, "IDBattleChange_Npc_Da_Fi_All_1");
            }
        }, 990000)); //...16 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				recoveringEnergy();
				//Recovering Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404842, Race.PC_ALL, 0);
            }
        }, 1050000)); //...17 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				offenseEnergy();
				//Offense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404843, Race.PC_ALL, 0);
				//The Illumiel Battlefield ends in 1 minute.
				sendMsgByRace(1404838, Race.PC_ALL, 5000);
				sp(656662, 289.84232f, 282.53238f, 92.94253f, (byte) 106, 0, "IDBattleChange_Npc_Li_Fi_All_1");
				sp(656665, 239.72562f, 236.17613f, 92.94253f, (byte) 45, 0, "IDBattleChange_Npc_Da_Fi_All_2");
            }
        }, 1110000)); //...18 Minutes 30s
		illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendInstanceInfoPacket();
				defenseEnergy();
				//Defense Energy appeared in Illumiel Battleground.
				sendMsgByRace(1404844, Race.PC_ALL, 0);
            }
        }, 1170000)); //...19 Minutes 30s
        illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!illumielBattlefieldReward.isRewarded()) {
					Race winnerRace = illumielBattlefieldReward.getWinnerRaceByScore();
					stopInstance(winnerRace);
				}
            }
        }, 1200000));
    }
	
	private void recoveringEnergy() {
		switch (Rnd.get(1, 10)) {
			case 1:
				sp(656920, 252.57826f, 246.7483f, 94.84095f, (byte) 0, 0);
			break;
			case 2:
				sp(656920, 268.05219f, 234.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 3:
				sp(656920, 264.55219f, 234.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 4:
				sp(656920, 281.55219f, 241.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 5:
				sp(656920, 264.52402f, 247.5159f, 85.81963f, (byte) 31, 0);
			break;
			case 6:
				sp(656920, 261.05219f, 284.3284f, 85.37500f, (byte) 0, 0);
			break;
			case 7:
				sp(656920, 247.05219f, 277.3284f, 88.02560f, (byte) 0, 0);
			break;
			case 8:
				sp(656920, 264.55219f, 284.3284f, 88.02560f, (byte) 0, 0);
			break;
			case 9:
				sp(656920, 264.38828f, 271.0212f, 85.81963f, (byte) 91, 0);
			break;
			case 10:
				sp(656920, 276.46690f, 271.7199f, 92.94253f, (byte) 76, 0);
			break;
		}
	}
	private void offenseEnergy() {
		switch (Rnd.get(1, 10)) {
			case 1:
				sp(656921, 252.57826f, 246.7483f, 94.84095f, (byte) 0, 0);
			break;
			case 2:
				sp(656921, 268.05219f, 234.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 3:
				sp(656921, 264.55219f, 234.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 4:
				sp(656921, 281.55219f, 241.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 5:
				sp(656921, 264.52402f, 247.5159f, 85.81963f, (byte) 31, 0);
			break;
			case 6:
				sp(656921, 261.05219f, 284.3284f, 85.37500f, (byte) 0, 0);
			break;
			case 7:
				sp(656921, 247.05219f, 277.3284f, 88.02560f, (byte) 0, 0);
			break;
			case 8:
				sp(656921, 264.55219f, 284.3284f, 88.02560f, (byte) 0, 0);
			break;
			case 9:
				sp(656921, 264.38828f, 271.0212f, 85.81963f, (byte) 91, 0);
			break;
			case 10:
				sp(656921, 276.46690f, 271.7199f, 92.94253f, (byte) 76, 0);
			break;
		}
	}
	private void defenseEnergy() {
		switch (Rnd.get(1, 10)) {
			case 1:
				sp(656922, 252.57826f, 246.7483f, 94.84095f, (byte) 0, 0);
			break;
			case 2:
				sp(656922, 268.05219f, 234.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 3:
				sp(656922, 264.55219f, 234.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 4:
				sp(656922, 281.55219f, 241.3284f, 88.02562f, (byte) 0, 0);
			break;
			case 5:
				sp(656922, 264.52402f, 247.5159f, 85.81963f, (byte) 31, 0);
			break;
			case 6:
				sp(656922, 261.05219f, 284.3284f, 85.37500f, (byte) 0, 0);
			break;
			case 7:
				sp(656922, 247.05219f, 277.3284f, 88.02560f, (byte) 0, 0);
			break;
			case 8:
				sp(656922, 264.55219f, 284.3284f, 88.02560f, (byte) 0, 0);
			break;
			case 9:
				sp(656922, 264.38828f, 271.0212f, 85.81963f, (byte) 91, 0);
			break;
			case 10:
				sp(656922, 276.46690f, 271.7199f, 92.94253f, (byte) 76, 0);
			break;
		}
	}
	
    protected void stopInstance(Race race) {
        stopInstanceTask();
        illumielBattlefieldReward.setWinnerRace(race);
        illumielBattlefieldReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
        reward();
        sendRewardPacket();
        sendScoreTypePacket();
    }
	
    @Override
    public void onEnterInstance(final Player player) {
        if (!containPlayer(player.getObjectId())) {
            illumielBattlefieldReward.regPlayerReward(player);
        }
        sendPreparingPacket(player);
    }
	
	public void sendPreparingPacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(6, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, getTime(), illumielBattlefieldReward, player.getObjectId(), 0, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            }
        });
	}
	public void sendScoreTypePacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(2, getTime(), illumielBattlefieldReward, player.getObjectId()));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendRewardPacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(5, getTime(), illumielBattlefieldReward, player.getObjectId()));
            }
        });
    }
	public void sendInstanceInfoPacket() {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            	PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendNpcScorePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(11, getTime(), illumielBattlefieldReward, player.getObjectId()));
            }
        });
    }
	public void sendPlayerLeavePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(8, getTime(), illumielBattlefieldReward, player.getObjectId()));
            }
        });
    }
	public void sendPlayerDiePacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, getTime(), illumielBattlefieldReward, player.getObjectId(), 133, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, getTime(), illumielBattlefieldReward, player.getObjectId(), 133, player.getRace().getRaceId()));
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, getTime(), illumielBattlefieldReward, instance.getPlayersInside(), true));
            }
        });
    }
	public void sendPlayerRevivedPacket(final Player player) {
    	instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player GroupMember) {
            	PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, getTime(), illumielBattlefieldReward, player.getObjectId(), 0, player.getRace().getRaceId()));
            }
        });
    }
	
    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        illumielBattlefieldReward = new IllumielBattlefieldReward(mapId, instanceId, instance);
        illumielBattlefieldReward.setInstanceScoreType(InstanceScoreType.PREPARING);
        doors = instance.getDoors();
		startInstanceTask();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		Npc npc = instance.getNpc(656825); //Kunax's Specter.
		if (npc != null) {
			npc.getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
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
			IllumielBattlefieldPlayerReward playerReward = illumielBattlefieldReward.getPlayerReward(player.getObjectId());
			int abyssPoint = 360000;
			int gloryPoint = 200;
			int expPoint = 10000;
			playerReward.setRewardAp((int) abyssPoint);
            playerReward.setRewardGp((int) gloryPoint);
			playerReward.setRewardExp((int) expPoint);
			if (player.getRace().equals(illumielBattlefieldReward.getWinnerRace())) {
                abyssPoint += illumielBattlefieldReward.AbyssReward(true, isTankKilled(player.getRace()));
                gloryPoint += illumielBattlefieldReward.GloryReward(true, isTankKilled(player.getRace()));
				expPoint += illumielBattlefieldReward.ExpReward(true, isTankKilled(player.getRace()));
                playerReward.setBonusAp(illumielBattlefieldReward.AbyssReward(true, isTankKilled(player.getRace())));
                playerReward.setBonusGp(illumielBattlefieldReward.GloryReward(true, isTankKilled(player.getRace())));
				playerReward.setBonusExp(illumielBattlefieldReward.ExpReward(true, isTankKilled(player.getRace())));
				playerReward.setLegendaryRidium(152080000);
				playerReward.setWarriorMedalBundle(188070175);
				playerReward.setBattlefieldMinionBundle(188070820);
				playerReward.setBattlefieldPvPEnchantmentStoneBundle(188070819);
			} else {
                abyssPoint += illumielBattlefieldReward.AbyssReward(false, isTankKilled(player.getRace()));
                gloryPoint += illumielBattlefieldReward.GloryReward(false, isTankKilled(player.getRace()));
				expPoint += illumielBattlefieldReward.ExpReward(false, isTankKilled(player.getRace()));
				playerReward.setRewardAp(illumielBattlefieldReward.AbyssReward(false, isTankKilled(player.getRace())));
                playerReward.setRewardGp(illumielBattlefieldReward.GloryReward(false, isTankKilled(player.getRace())));
				playerReward.setRewardExp(illumielBattlefieldReward.ExpReward(false, isTankKilled(player.getRace())));
                playerReward.setWarriorMedalBundle(188070175);
				playerReward.setBattlefieldPvPEnchantmentStoneBundle(188070819);
            } if (RaceKilledTank == player.getRace()) {
				ItemService.addItem(player, 188070175, 1);
				ItemService.addItem(player, 152080000, 4);
				ItemService.addItem(player, 188070820, 2);
				ItemService.addItem(player, 188070819, 2);
			}
            ItemService.addItem(player, 152080000, 2);
			ItemService.addItem(player, 188070820, 1);
			ItemService.addItem(player, 188070819, 1);
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
					for (Player player : instance.getPlayersInside()) {
						onExitInstance(player);
					}
					AutoGroupService.getInstance().unRegisterInstance(instanceId);
				}
			}
		}, 60000);
    }
    
    private int getTime() {
        long result = System.currentTimeMillis() - instanceTime;
        if (result < 90000) {
            return (int) (90000 - result);
        } else if (result < 1200000) { //20-Mins
            return (int) (1200000 - (result - 90000));
        }
        return 0;
    }
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        illumielBattlefieldReward.portToPosition(player);
		sendPlayerRevivedPacket(player);
		return true;
    }
	
    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
		IllumielBattlefieldPlayerReward ownerReward = illumielBattlefieldReward.getPlayerReward(player.getObjectId());
		ownerReward.endBoostMoraleEffect(player);
		ownerReward.applyBoostMoraleEffect(player);
        int points = 133;
		sendPlayerDiePacket(player);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), false, 0, 8));
        if (lastAttacker instanceof Player) {
            if (lastAttacker.getRace() != player.getRace()) {
                InstancePlayerReward playerReward = illumielBattlefieldReward.getPlayerReward(player.getObjectId());
				if (getPointsByRace(lastAttacker.getRace()).compareTo(getPointsByRace(player.getRace())) < 0) {
                    points *= loosingGroupMultiplier;
                } else if (loosingGroupMultiplier == 10 || playerReward.getPoints() == 0) {
                    points = 0;
                }
				ItemService.addItem(player, 186000470, 133); //War Points.
                updateScore((Player) lastAttacker, player, points, true);
            }
        }
        updateScore(player, player, -points, false);
        return true;
    }
	
	private boolean isTankKilled(Race PlayerRace) {
    	if (PlayerRace == RaceKilledTank) {
    		return true;
    	}
    	return false;
    }
	
	private MutableInt getPvpKillsByRace(Race race) {
        return illumielBattlefieldReward.getPvpKillsByRace(race);
    }
	
    private MutableInt getPointsByRace(Race race) {
        return illumielBattlefieldReward.getPointsByRace(race);
    }
	
    private void addPointsByRace(Race race, int points) {
        illumielBattlefieldReward.addPointsByRace(race, points);
    }
	
    private void addPvpKillsByRace(Race race, int points) {
        illumielBattlefieldReward.addPvpKillsByRace(race, points);
    }
	
    private void addPointToPlayer(Player player, int points) {
        illumielBattlefieldReward.getPlayerReward(player.getObjectId()).addPoints(points);
    }
	
    private void addPvPKillToPlayer(Player player) {
        illumielBattlefieldReward.getPlayerReward(player.getObjectId()).addPvPKillToPlayer();
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
        if (illumielBattlefieldReward.hasCapPoints()) {
            stopInstance(illumielBattlefieldReward.getWinnerRaceByScore());
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
			case 656658: //IDBattleChange_Li_Tower.
			case 656659: //IDBattleChange_Da_Tower.
				score += 10000;
				despawnNpc(npc);
            break;
			case 656660: //IDBattleChange_NPC_Li_Fi_All.
			case 656661: //IDBattleChange_NPC_Li_As_All.
			case 656662: //IDBattleChange_NPC_Li_Wi_All.
			case 656663: //IDBattleChange_NPC_Da_Fi_All.
			case 656664: //IDBattleChange_NPC_Da_As_All.
			case 656665: //IDBattleChange_NPC_Da_Wi_All.
			    score += 200;
				despawnNpc(npc);
				ItemService.addItem(mostPlayerDamage, 186000470, 100);
			break;
			case 656825: //Kunax's Specter.
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				   //An Archon Siege Chariot has appeared to support the Judgement of Wisdom Legion.
				   sendMsgByRace(1404841, Race.PC_ALL, 0);
				   sp(656667, 227.0000f, 259.0000f, 89.0000f, (byte) 0, 0);
				} else if (race.equals(Race.ASMODIANS)) {
				   //A Guardian Siege Chariot has appeared to support the Justice of Life Legion.
				   sendMsgByRace(1404840, Race.PC_ALL, 0);
				   sp(656666, 301.0000f, 259.0000f, 89.0000f, (byte) 60, 0);
				}
            break;
			case 656666: //IDBattleChange_Cannon_Li.
			case 656667: //IDBattleChange_Cannon_Da.
				despawnNpc(npc);
				RaceKilledTank = mostPlayerDamage.getRace();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						if (!illumielBattlefieldReward.isRewarded()) {
							Race winnerRace = illumielBattlefieldReward.getWinnerRaceByScore();
							stopInstance(winnerRace);
						}
					}
				}, 10000);
            break;
        }
        updateScore(mostPlayerDamage, npc, score, false);
    }
	
	@Override
    public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
		    case 802192: //Flame Vent [Elyos].
			    //The Asmodian Flame Vent has been activated.\nThe Asmodians are trapped!
				sendMsgByRace(1402368, Race.PC_ALL, 0);
				sp(702404, 234.43842f, 194.1041f, 79.23065f, (byte) 105, 0);
				sp(702405, 234.13383f, 194.3959f, 79.23065f, (byte) 105, 0);
                sp(702405, 234.62419f, 193.9574f, 79.23065f, (byte) 45, 0);
                sp(702405, 234.42247f, 194.1363f, 79.23065f, (byte) 16, 0);
                sp(702405, 234.53394f, 194.2717f, 79.23065f, (byte) 75, 0);
			break;
			case 802193: //Flame Vent [Asmodians]
			    //The Elyos Flame Vent has been activated.\nThe Elyos are trapped!
				sendMsgByRace(1402369, Race.PC_ALL, 0);
				sp(702404, 294.57443f, 324.2220f, 79.23065f, (byte) 45, 0);
				sp(702405, 294.53418f, 324.0909f, 79.23065f, (byte) 105, 0);
                sp(702405, 294.66284f, 324.2917f, 79.23065f, (byte) 75, 0);
                sp(702405, 294.46340f, 323.8423f, 79.23065f, (byte) 15, 0);
                sp(702405, 294.70172f, 324.2306f, 79.23065f, (byte) 45, 0);
			break;
        }
    }
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20309);
		effectController.removeEffect(20310);
		effectController.removeEffect(20311);
		effectController.removeEffect(20312);
		effectController.removeEffect(20313);
		effectController.removeEffect(20314);
		effectController.removeEffect(20315);
		effectController.removeEffect(20316);
		effectController.removeEffect(20317);
		effectController.removeEffect(20318);
		effectController.removeEffect(20319);
		effectController.removeEffect(20320);
		//Knight.
		SkillLearnService.removeSkill(player, 5181);
		SkillLearnService.removeSkill(player, 5187);
		SkillLearnService.removeSkill(player, 5184);
		SkillLearnService.removeSkill(player, 5189);
		SkillLearnService.removeSkill(player, 5190);
		//Fighter.
		SkillLearnService.removeSkill(player, 5193);
		SkillLearnService.removeSkill(player, 5196);
		SkillLearnService.removeSkill(player, 5199);
		SkillLearnService.removeSkill(player, 5201);
		SkillLearnService.removeSkill(player, 5202);
		//Wizard.
		SkillLearnService.removeSkill(player, 5205);
		SkillLearnService.removeSkill(player, 5208);
		SkillLearnService.removeSkill(player, 5320);
		SkillLearnService.removeSkill(player, 5321);
		SkillLearnService.removeSkill(player, 5213);
		SkillLearnService.removeSkill(player, 5211);
		SkillLearnService.removeSkill(player, 5316);
		//Gunslinger.
		SkillLearnService.removeSkill(player, 5217);
		SkillLearnService.removeSkill(player, 5307);
		SkillLearnService.removeSkill(player, 5308);
		SkillLearnService.removeSkill(player, 5309);
		SkillLearnService.removeSkill(player, 5223);
		SkillLearnService.removeSkill(player, 5225);
		SkillLearnService.removeSkill(player, 5226);
		SkillLearnService.removeSkill(player, 5310);
		SkillLearnService.removeSkill(player, 5311);
		//Priest.
		SkillLearnService.removeSkill(player, 5229);
		SkillLearnService.removeSkill(player, 5232);
		SkillLearnService.removeSkill(player, 5312);
		SkillLearnService.removeSkill(player, 5313);
		SkillLearnService.removeSkill(player, 5314);
		SkillLearnService.removeSkill(player, 5237);
		SkillLearnService.removeSkill(player, 5238);
		SkillLearnService.removeSkill(player, 5322);
		//Chanter.
		SkillLearnService.removeSkill(player, 5241);
		SkillLearnService.removeSkill(player, 5244);
		SkillLearnService.removeSkill(player, 5247);
		SkillLearnService.removeSkill(player, 5249);
		SkillLearnService.removeSkill(player, 5250);
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(186000470, storage.getItemCountByItemId(186000470)); //War Points.
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
    @Override
    public void onInstanceDestroy() {
        illumielBattlefieldReward.clear();
        isInstanceDestroyed = true;
        stopInstanceTask();
        doors.clear();
    }
	
    protected void openFirstDoors() {
        openDoor(1);
		openDoor(99);
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
        illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        illumielTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        for (FastList.Node<Future<?>> n = illumielTask.head(), end = illumielTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
    @Override
    public InstanceReward<?> getInstanceReward() {
        return illumielBattlefieldReward;
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
		IllumielBattlefieldPlayerReward playerReward = illumielBattlefieldReward.getPlayerReward(player.getObjectId());
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