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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.ShatteredAbyssalSplinterReward;
import com.aionemu.gameserver.model.instance.playerreward.ShatteredAbyssalSplinterPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.ranking.SeasonRankingService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302710000)
public class Shattered_Abyssal_Splinter extends GeneralInstanceHandler
{
	Player pl;
	private int rank;
	private long startTime;
	private Future<?> timerPrepare;
	private Future<?> timerInstance;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	private ShatteredAbyssalSplinterReward instanceReward;
	//Preparation Time.
	private int prepareTimerSeconds = 120000; //...2Min
	//Duration Instance Time.
	private int instanceTimerSeconds = 300000; //...5Min
	private final FastList<Future<?>> shatteredAbyssalSplinterTask = FastList.newInstance();
	
	protected ShatteredAbyssalSplinterPlayerReward getPlayerReward(Integer object) {
		return (ShatteredAbyssalSplinterPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new ShatteredAbyssalSplinterPlayerReward(player.getObjectId()));
	}
	
	private boolean containPlayer(Integer object) {
		return instanceReward.containPlayer(object);
	}
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
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
	
	protected void startInstanceTask() {
		shatteredAbyssalSplinterTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						if (checkRank(instanceReward.getPoints()) == 1) {
							spawn(820797, 263.65216f, 215.91235f, 197.9808f, (byte) 87); //Treasure Chest.
						}
						//spawn(820808, 274.0000f, 212.0000f, 197.0000f, (byte) 78); //Cold Airo.
						spawn(820810, 270.0000f, 214.0000f, 197.0000f, (byte) 82); //Shattered Abyssal Splinter Exit.
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
						despawnNpcs(instance.getNpcs(820809)); //Mirror Of Abyss.
						despawnNpcs(instance.getNpcs(858701)); //Yamennes Painflare.
						killNpc(instance.getNpcs(858702)); //Tesinon.
						stopInstance(player);
				    }
			    });
            }
        }, 300000));
    }
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 98) {
			onUpdateScore();
			startInstanceTask();
			doors.get(98).setOpen(true);
			//The player has 2 min to prepare !!! [Timer Red]
			if ((timerPrepare != null) && (!timerPrepare.isDone() || !timerPrepare.isCancelled())) {
				//Start the instance time !!! [Timer White]
				startMainInstanceTimer();
			}
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		//Cruel Airo the Abyss Keeper has appeared.
		sendMsgByRace(1405958, Race.PC_ALL, 0);
		spawn(820807, 234.0000f, 152.0000f, 197.0000f, (byte) 60); //Cruel Airo.
		spawn(820809, 270.0000f, 215.0000f, 197.0000f, (byte) 82); //Mirror Of Abyss.
		instanceReward = new ShatteredAbyssalSplinterReward(mapId, instanceId);
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
	public void onEnterInstance(final Player player) {
		pl = player;
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		ShatteredAbyssalSplinterPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		}
		startPrepareTimer();
		//You can select one variation of Airo sleeping in the Mirror of Abyss and wake it up.
		sendMsgByRace(1405956, Race.PC_ALL, 5000);
		//Your selection determines the stat points of the Abyss Weapon you obtain.
		sendMsgByRace(1405957, Race.PC_ALL, 8000);
		//Your score depends on the damage you deal to Yamennes Painflare.
		sendMsgByRace(1405960, Race.PC_ALL, 11000);
	}
	
	private void onUpdateScore() {
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
				int score = pl.getInstanceScore();
                if (instanceReward.getInstanceScoreType().isStartProgress()) {
					instanceReward.addPoints(score);
					sendPacket(0, score);
				}
            }
        }, 1 * 500, 1 * 500);
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
		SeasonRankingService.getInstance().addDamagePlayerScore(player);
		doReward(player);
		sendPacket(0, 0);
	}
	
	@Override
    public boolean onReviveEvent(Player player) {
		for (Npc npc: instance.getNpcs()) {
			npc.getController().onDelete();
		}
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
		onFailYamennes(player);
		return true;
    }
	
	public void onFailYamennes(Player player) {
		InstanceService.destroyInstance(player.getPosition().getWorldMapInstance());
		SeasonRankingService.getInstance().addDamagePlayerScore(player);
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
	private void rewardGroup() {
		for (Player p: instance.getPlayersInside()) {
			doReward(p);
		}
	}
	
	private int checkRank(int totalPoints) {
		if (totalPoints >= 1000000) { //Rank S.
			rank = 1;
		} else if (totalPoints >= 750000) { //Rank A.
			rank = 2;
		} else if (totalPoints >= 500000) { //Rank B.
			rank = 3;
		} else {
			rank = 6;
		}
		return rank;
	}
	
	@Override
	public void doReward(Player player) {
		ShatteredAbyssalSplinterPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int shatteredRank = instanceReward.getRank();
			switch (shatteredRank) {
				case 1: //Rank S
					playerReward.setScoreAP(250000);
					playerReward.setExperienceCrystal(8);
					ItemService.addItem(player, 188900063, 8); //Experience Crystal.
					ItemService.addItem(player, 186020150, 25); //Abyssal Fragment.
				break;
				case 2: //Rank A
					playerReward.setScoreAP(100000);
					playerReward.setExperienceCrystal(5);
					ItemService.addItem(player, 188900063, 5); //Experience Crystal.
					ItemService.addItem(player, 186020150, 12); //Abyssal Fragment.
				break;
				case 3: //Rank B
					playerReward.setScoreAP(50000);
					playerReward.setExperienceCrystal(3);
					ItemService.addItem(player, 188900063, 3); //Experience Crystal.
					ItemService.addItem(player, 186020150, 6); //Abyssal Fragment.
				break;
			}
			AbyssPointsService.addAp(player, playerReward.getScoreAP());
		}
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		//Key Door.
		storage.decreaseByItemId(185001080, storage.getItemCountByItemId(185001080));
		//Wreck Of The Broken Abyss Weapon Box.
		storage.decreaseByItemId(188073005, storage.getItemCountByItemId(188073005));
		storage.decreaseByItemId(188073006, storage.getItemCountByItemId(188073006));
		storage.decreaseByItemId(188073007, storage.getItemCountByItemId(188073007));
		storage.decreaseByItemId(188073008, storage.getItemCountByItemId(188073008));
		storage.decreaseByItemId(188073009, storage.getItemCountByItemId(188073009));
		//Wreck Of The Broken Abyss Weapon.
		storage.decreaseByItemId(100050219, storage.getItemCountByItemId(100050219));
		storage.decreaseByItemId(100150214, storage.getItemCountByItemId(100150214));
		storage.decreaseByItemId(100250216, storage.getItemCountByItemId(100250216));
		storage.decreaseByItemId(100550212, storage.getItemCountByItemId(100550212));
		storage.decreaseByItemId(100650213, storage.getItemCountByItemId(100650213));
		storage.decreaseByItemId(100950214, storage.getItemCountByItemId(100950214));
		storage.decreaseByItemId(101350214, storage.getItemCountByItemId(101350214));
		storage.decreaseByItemId(101550216, storage.getItemCountByItemId(101550216));
		storage.decreaseByItemId(101750213, storage.getItemCountByItemId(101750213));
		storage.decreaseByItemId(101850214, storage.getItemCountByItemId(101850214));
		storage.decreaseByItemId(101950208, storage.getItemCountByItemId(101950208));
		storage.decreaseByItemId(102050213, storage.getItemCountByItemId(102050213));
		storage.decreaseByItemId(102150218, storage.getItemCountByItemId(102150218));
		storage.decreaseByItemId(102220162, storage.getItemCountByItemId(102220162));
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = shatteredAbyssalSplinterTask.head(), end = shatteredAbyssalSplinterTask.tail(); (n = n.getNext()) != end;) {
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
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onExitInstance(Player player) {
		removeItems(player);
		InstanceService.destroyInstance(player.getPosition().getWorldMapInstance());
		if (instanceReward.getInstanceScoreType().isEndProgress()) {
			TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
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