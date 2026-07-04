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
package instance.crucible;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.database.dao.DAOManager;

import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.templates.tower_reward.TowerStageRewardTemplate;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.ranking.SeasonRankingService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/** Source: https://www.youtube.com/watch?v=xNIGdF9CpnU
/****/

@InstanceID(302680000)
public class Crucible_Spire_Lower_Level extends GeneralInstanceHandler
{
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	private final FastList<Future<?>> crucibleTask = FastList.newInstance();
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		int pfloor = player.getCommonData().getFloor();
		sendPacket(player, "Condition_Infinity_PRE_SEASON_Floor", 27);
		sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor", pfloor);
		sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", pfloor);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		spawnFloorRings1();
		spawnFloorRings2();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		spawn(820653, 256.0000f, 256.0000f, 241.0000f, (byte) 69); //Blessed Apostle Transformation Corridor.
		sp(247310, 279.0000f, 243.0000f, 243.0000f, (byte) 0, 57, 10000, 0, null);
		sp(247310, 279.0000f, 255.0000f, 243.0000f, (byte) 0, 60, 10000, 0, null);
		sp(247310, 1279.0000f, 1243.0000f, 243.0000f, (byte) 0, 89, 10000, 0, null);
		sp(247310, 1279.0000f, 1255.0000f, 243.0000f, (byte) 0, 90, 10000, 0, null);
    }
	
	private void sendPacket(Player player, final String variable, final int floor) {
		PacketSendUtility.sendPacket(player, new SM_CONDITION_VARIABLE(player, variable, floor));
	}
	
	private void teleportCrucibleFloor(Player player) {
		int pfloor = player.getCommonData().getFloor();
		spawnNextFloor(player, pfloor);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				despawnNpcs(instance.getNpcs(701773));
			}
		}, 2500);
		if (pfloor >= 1 && pfloor <= 15) {
			spawn(701000, 1263.0000f, 1249.0000f, 240.0000f, (byte) 0, 108);
			teleportFloor(player, 1219.0000f, 1249.0000f, 240.0000f, (byte) 0);
		}
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor", pfloor);
	}
	
	private void teleportStartCrucibleFloor(Player player) {
		int pfloor = player.getCommonData().getFloor();
		spawnNextFloor(player, pfloor);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				despawnNpcs(instance.getNpcs(701773));
			}
		}, 2500);
		spawn(701000, 1263.0000f, 1249.0000f, 240.0000f, (byte) 0, 108);
		teleportFloor(player, 1219.0000f, 1249.0000f, 240.0000f, (byte) 0);
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor", pfloor);
	}
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 232) {
			doors.get(232).setOpen(true);
			DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
			spawn(701772, 280.0000f, 249.0000f, 241.0000f, (byte) 0, 115);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							player.getCommonData().setFloor(1);
						}
					});
				}
			}, 3000);
		}
	}
	
	private void spawnFloorRings1() {
        FlyRing f1 = new FlyRing(new FlyRingTemplate("FLOOR_1", mapId,
        new Point3D(1317.41605, 1254.6891, 258.0014),
		new Point3D(1317.88123, 1249.1969, 264.8329),
        new Point3D(1317.51993, 1244.0759, 258.0506), 30), instanceId);
        f1.spawn();
    }
	private void spawnFloorRings2() {
        FlyRing f2 = new FlyRing(new FlyRingTemplate("FLOOR_2", mapId,
        new Point3D(313.00000, 254.0000, 256.0000),
		new Point3D(314.00000, 249.0000, 262.0000),
        new Point3D(313.00000, 244.0000, 256.0000), 30), instanceId);
        f2.spawn();
    }
	
	private void spawnNextFloor(Player player, int next) {
		switch (next) {
			case 1:
				sp(655141, 1241.0000f, 1242.0000f, 240.0000f, (byte) 57, 2000, 0, null);
				sp(655141, 1241.0000f, 1257.0000f, 240.0000f, (byte) 70, 2000, 0, null);
				sp(655142, 1251.0000f, 1260.0000f, 240.0000f, (byte) 76, 2000, 0, null);
				sp(655142, 1256.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655142, 1251.0000f, 1238.0000f, 240.0000f, (byte) 44, 2000, 0, null);
			break;
			case 2:
				sp(655143, 1241.0000f, 1242.0000f, 240.0000f, (byte) 57, 2000, 0, null);
				sp(655143, 1241.0000f, 1257.0000f, 240.0000f, (byte) 70, 2000, 0, null);
				sp(655144, 1251.0000f, 1260.0000f, 240.0000f, (byte) 76, 2000, 0, null);
				sp(655144, 1256.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655144, 1251.0000f, 1238.0000f, 240.0000f, (byte) 44, 2000, 0, null);
			break;
			case 3:
				sp(655145, 1241.0000f, 1249.0000f, 241.0000f, (byte) 60, 2000, 0, null);
				sp(655146, 1251.0000f, 1260.0000f, 240.0000f, (byte) 76, 2000, 0, null);
				sp(655146, 1256.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655146, 1251.0000f, 1238.0000f, 240.0000f, (byte) 44, 2000, 0, null);
			break;
			case 4:
				sp(655148, 1241.0000f, 1259.0000f, 240.0000f, (byte) 62, 2000, 0, null);
				sp(655149, 1241.0000f, 1240.0000f, 240.0000f, (byte) 62, 2000, 0, null);
			break;
			case 5:
				sp(858486, 1241.0000f, 1249.0000f, 241.0000f, (byte) 60, 2000, 0, null);
				sp(858491, 1251.0000f, 1238.0000f, 241.0000f, (byte) 45, 2000, 0, null);
                sp(858491, 1251.0000f, 1260.0000f, 241.0000f, (byte) 76, 2000, 0, null);
                sp(858491, 1230.0000f, 1238.0000f, 241.0000f, (byte) 16, 2000, 0, null);
                sp(858491, 1230.0000f, 1260.0000f, 241.0000f, (byte) 106, 2000, 0, null);
			break;
			case 6:
			    sp(655151, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(247353, 1236.0000f, 1266.0000f, 241.0000f, (byte) 95, 2000, 0, null);
                sp(247353, 1254.0000f, 1236.0000f, 241.0000f, (byte) 44, 2000, 0, null);
                sp(247353, 1253.0000f, 1262.0000f, 241.0000f, (byte) 74, 2000, 0, null);
                sp(247353, 1236.0000f, 1231.0000f, 241.0000f, (byte) 25, 2000, 0, null);
				sp(655152, 1241.0000f, 1241.0000f, 240.0000f, (byte) 57, 2000, 0, null);
				sp(655152, 1241.0000f, 1257.0000f, 240.0000f, (byte) 60, 2000, 0, null);
                sp(655152, 1249.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
                sp(655152, 1245.0000f, 1253.0000f, 240.0000f, (byte) 63, 2000, 0, null);
                sp(655152, 1245.0000f, 1245.0000f, 240.0000f, (byte) 61, 2000, 0, null);
			break;
			case 7:
				sp(655153, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
			break;
			case 8:
				sp(858650, 1241.0000f, 1249.0000f, 241.0000f, (byte) 0, 149, 2000, 0, null);
				sp(858493, 1229.0000f, 1242.0000f, 241.0000f, (byte) 86, 2000, 0, null);
				sp(858493, 1227.0000f, 1254.0000f, 241.0000f, (byte) 74, 2000, 0, null);
				sp(858493, 1233.0000f, 1259.0000f, 241.0000f, (byte) 85, 2000, 0, null);
				sp(858493, 1226.0000f, 1260.0000f, 241.0000f, (byte) 95, 2000, 0, null);
				sp(858493, 1234.0000f, 1267.0000f, 240.0000f, (byte) 10, 2000, 0, null);
				sp(858493, 1241.0000f, 1261.0000f, 241.0000f, (byte) 106, 2000, 0, null);
				sp(858493, 1240.0000f, 1266.0000f, 241.0000f, (byte) 74, 2000, 0, null);
				sp(858493, 1251.0000f, 1260.0000f, 241.0000f, (byte) 98, 2000, 0, null);
				sp(858493, 1248.0000f, 1266.0000f, 241.0000f, (byte) 69, 2000, 0, null);
				sp(858493, 1257.0000f, 1252.0000f, 241.0000f, (byte) 109, 2000, 0, null);
				sp(858493, 1255.0000f, 1242.0000f, 241.0000f, (byte) 84, 2000, 0, null);
				sp(858493, 1251.0000f, 1249.0000f, 241.0000f, (byte) 39, 2000, 0, null);
				sp(858493, 1247.0000f, 1256.0000f, 241.0000f, (byte) 51, 2000, 0, null);
				sp(858493, 1248.0000f, 1237.0000f, 241.0000f, (byte) 26, 2000, 0, null);
				sp(858493, 1239.0000f, 1235.0000f, 241.0000f, (byte) 71, 2000, 0, null);
				sp(858493, 1235.0000f, 1240.0000f, 241.0000f, (byte) 34, 2000, 0, null);
				sp(858493, 1245.0000f, 1231.0000f, 241.0000f, (byte) 35, 2000, 0, null);
				sp(858493, 1233.0000f, 1233.0000f, 241.0000f, (byte) 21, 2000, 0, null);
				sp(858493, 1223.0000f, 1245.0000f, 241.0000f, (byte) 10, 2000, 0, null);
				sp(858493, 1255.0000f, 1234.0000f, 240.0000f, (byte) 52, 2000, 0, null);
			break;
			case 9:
				sp(655154, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
			break;
			case 10:
			    sp(655150, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(858490, 1251.0000f, 1238.0000f, 241.0000f, (byte) 45, 2000, 0, null);
                sp(858490, 1251.0000f, 1260.0000f, 241.0000f, (byte) 76, 2000, 0, null);
                sp(858490, 1230.0000f, 1238.0000f, 241.0000f, (byte) 16, 2000, 0, null);
                sp(858490, 1230.0000f, 1260.0000f, 241.0000f, (byte) 106, 2000, 0, null);
			break;
			case 11:
			    sp(653929, 1258.7547f, 1259.0354f, 240.7116f, (byte) 70, 2000, 0, null);
				sp(653929, 1240.9303f, 1269.6517f, 240.7116f, (byte) 90, 2000, 0, null);
				sp(653929, 1223.5531f, 1258.9534f, 240.7116f, (byte) 109, 2000, 0, null);
				sp(653929, 1223.6501f, 1240.0538f, 240.7116f, (byte) 10, 2000, 0, null);
				sp(653929, 1241.1681f, 1229.6743f, 240.7116f, (byte) 31, 2000, 0, null);
				sp(653929, 1258.5380f, 1240.2217f, 240.7116f, (byte) 51, 2000, 0, null);
				sp(653929, 1250.7898f, 1266.9927f, 240.7116f, (byte) 81, 2000, 0, null);
                sp(653929, 1231.0481f, 1267.0629f, 240.7116f, (byte) 100, 2000, 0, null);
                sp(653929, 1231.4862f, 1232.2625f, 240.7116f, (byte) 21, 2000, 0, null);
                sp(653929, 1251.0325f, 1231.8973f, 240.7116f, (byte) 40, 2000, 0, null);
			break;
			case 12:
			    sp(655155, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655156, 1251.0000f, 1260.0000f, 240.0000f, (byte) 76, 2000, 0, null);
				sp(655156, 1256.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655156, 1251.0000f, 1238.0000f, 240.0000f, (byte) 44, 2000, 0, null);
			break;
			case 13:
			    sp(655157, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655158, 1251.0000f, 1260.0000f, 240.0000f, (byte) 76, 2000, 0, null);
				sp(655158, 1256.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655158, 1251.0000f, 1238.0000f, 240.0000f, (byte) 44, 2000, 0, null);
			break;
			case 14:
			    sp(655159, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655160, 1251.0000f, 1260.0000f, 240.0000f, (byte) 76, 2000, 0, null);
				sp(655160, 1256.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(655160, 1251.0000f, 1238.0000f, 240.0000f, (byte) 44, 2000, 0, null);
			break;
			case 15:
			    sp(858484, 1241.0000f, 1249.0000f, 240.0000f, (byte) 60, 2000, 0, null);
				sp(701344, 1228.0000f, 1236.0000f, 240.0000f, (byte) 0, 109, 2000, 0, null);
				sp(701344, 1228.0000f, 1262.0000f, 240.0000f, (byte) 0, 110, 2000, 0, null);
				sp(701344, 1253.0000f, 1262.0000f, 240.0000f, (byte) 0, 123, 2000, 0, null);
				sp(701344, 1253.0000f, 1236.0000f, 240.0000f, (byte) 0, 128, 2000, 0, null);
			break;
		}
	}
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
        switch (npc.getObjectTemplate().getTemplateId()) {
			//Floor 1
			case 655141:
			case 655142:
			    despawnNpc(npc);
				if (getNpcs(655141).isEmpty() &&
				    getNpcs(655142).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 1);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(2);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 2
			case 655143:
			case 655144:
			    despawnNpc(npc);
			    if (getNpcs(655143).isEmpty() &&
				    getNpcs(655144).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 2);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(3);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 3
			case 655145:
			case 655146:
			    despawnNpc(npc);
			    if (getNpcs(655145).isEmpty() &&
				    getNpcs(655146).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 3);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(4);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 4
			case 655148:
			case 655149:
			    despawnNpc(npc);
			    if (getNpcs(655148).isEmpty() &&
					getNpcs(655149).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 4);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(5);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 5
			case 858486:
			case 858491:
			    despawnNpc(npc);
			    if (getNpcs(858486).isEmpty() &&
				    getNpcs(858491).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 5);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(6);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 6
			case 247353:
			    despawnNpc(npc);
			    spawn(247478, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
			break;
			case 655151:
			    despawnNpc(npc);
				if (getNpcs(655151).isEmpty()) {
					despawnNpcs(instance.getNpcs(247478));
					despawnNpcs(instance.getNpcs(247353));
					despawnNpcs(instance.getNpcs(655152));
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 6);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(7);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 7
			case 655153:
			    despawnNpc(npc);
			    if (getNpcs(655153).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 7);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(8);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 8
			case 858493:
			    despawnNpc(npc);
			    if (getNpcs(858493).isEmpty()) {
					despawnNpcs(instance.getNpcs(858650));
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 8);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(9);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 9
			case 655154:
			    despawnNpc(npc);
			    if (getNpcs(655154).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 9);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(10);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 10
			case 655150:
			case 858490:
			    despawnNpc(npc);
			    if (getNpcs(655150).isEmpty() &&
				    getNpcs(858490).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 10);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(11);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 11
			case 653929: //Sanctuary Watchman.
			    despawnNpc(npc);
			    if (getNpcs(653929).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 11);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(12);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 12
			case 655155:
			case 655156:
			    despawnNpc(npc);
			    if (getNpcs(655155).isEmpty() &&
				    getNpcs(655156).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 12);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(13);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 13
			case 655157:
			case 655158:
			    despawnNpc(npc);
			    if (getNpcs(655157).isEmpty() &&
				    getNpcs(655158).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 13);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(14);
								}
							});
						}
					}, 3000);
				}
			break;
			//Floor 14
			case 655159:
			case 655160:
			    despawnNpc(npc);
			    if (getNpcs(655159).isEmpty() &&
				    getNpcs(655160).isEmpty()) {
					despawnNpcs(instance.getNpcs(701000));
					sp(701773, 1280.0000f, 1249.0000f, 240.0000f, (byte) 0, 120, 1500, 0, null);
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 14);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									player.getCommonData().setFloor(15);
								}
							});
						}
					}, 3000);
				}
			break;
			//Final Floor 15
			case 858484:
			    despawnNpc(npc);
			    if (getNpcs(858484).isEmpty()) {
					killNpc(getNpcs(701344));
					sendPacket(player, "Condition_Infinity_THIS_SEASON_Floor_Reward", 15);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									rewardForFloorId(player);
								}
							}
						}
					}, 500);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed) {
								for (Player player: instance.getPlayersInside()) {
									onExitInstance(player);
								}
							}
						}
					}, 10000);
				} if (player.getRace() == Race.ELYOS) {
					ClassChangeService.onUpdateQuest18260(player);
				} else {
					ClassChangeService.onUpdateQuest28260(player);
				}
			break;
		}
    }
	
	@Override
    public boolean onPassFlyingRing(Player player, String flyingRing) {
        if (flyingRing.equals("FLOOR_1")) {
			teleportCrucibleFloor(player);
		} else if (flyingRing.equals("FLOOR_2")) {
			teleportStartCrucibleFloor(player);
		}
		return false;
	}
	
    public void rewardForFloorId(Player player) {
		final TowerStageRewardTemplate reward = DataManager.TOWER_REWARD_DATA.getTowerReward(player.getFloor());
		int itemId1 = reward.getItemId();
		int itemCount1 = reward.getItemCount();
		if (itemId1 != 0 && itemCount1 != 0) {
			ItemService.addItem(player, itemId1, itemCount1);
		}
		int itemId2 = reward.getItemId2();
		int itemCount2 = reward.getItemCount2();
		if (itemId2 != 0 && itemCount2 != 0) {
			ItemService.addItem(player, itemId2, itemCount2);
		}
		int kinahCount = reward.getKinahCount();
		if (kinahCount != 0) {
			ItemService.addItem(player, 182400001, kinahCount);
		}
		int expCount = reward.getExpCount();
		if (expCount != 0) {
			player.getCommonData().addExp(expCount, RewardType.HUNTING);
		}
		int apCount = reward.getApCount();
		if (apCount != 0) {
			AbyssPointsService.addAp(player, apCount);
		}
		int gpCount = reward.getGpCount();
		if (gpCount != 0) {
			AbyssPointsService.addGp(player, gpCount);
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
	
	private void sendMessage(final int msgId, long delay) {
        if (delay == 0) {
            this.sendMsg(msgId);
        } else {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                public void run() {
                    sendMsg(msgId);
                }
            }, delay);
        }
    }
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpcs(List<Npc> npcs) {
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
	
	public void onFailCrucible(Player player) {
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		sp(701773, 280.0000f, 1249.0000f, 240.0000f, (byte) 0, 114, 1500, 0, null);
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
	public void onExitInstance(Player player) {
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
    public void onPlayerLogOut(Player player) {
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
    }
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}
	
	private void teleportFloor(float x, float y, float z, byte h) {
		for (Player playerInside: instance.getPlayersInside()) {
			if (playerInside.isOnline()) {
				teleportCrucibleFloor(playerInside);
			}
		}
	}
	
	protected void teleportFloor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        crucibleTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        crucibleTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	
	@Override
    public boolean onReviveEvent(Player player) {
		for (Npc npc: instance.getNpcs()) {
			npc.getController().onDelete();
		}
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INFINITY_INDUN_RESURRECT, 0, 0));
		onFailCrucible(player);
		return true;
    }
}