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
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302430000)
public class Primeth_Forge extends GeneralInstanceHandler
{
	private Race spawnRace;
	private int lymOreGrinder;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	private Future<?> chestPrimethForgeTask;
	private List<Npc> primethForgeChest = new ArrayList<Npc>();
	private final FastList<Future<?>> primethForgeTask = FastList.newInstance();
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 656830: //Smuggler Shukirukin.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						switch (Rnd.get(1, 2)) {
							case 1:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188070768, 1)); //Ancient Daevanion Skill Box.
							break;
							case 2:
								dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188071258, 1)); //Legendary Daevanion Skill Box.
							break;
						}
					}
				}
			break;
        }
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		sendPacket(player, "UI_Gauge_01", 0 + 1);
		sendPacket(player, "UI_Gauge_02", 0 + 1);
		if (spawnRace == null) {
			spawnRace = player.getRace();
			primethForgeRace();
		}
	}
	
	private void primethForgeRace() {
		final int Atis_Rith1 = spawnRace == Race.ASMODIANS ? 806818 : 806817;
		spawn(Atis_Rith1, 518.0000f, 800.0000f, 810.0000f, (byte) 51);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		//The Balaur who spotted your approach sounded the alarm. Watch out for fire!
		sendMsgByRace(1404691, Race.PC_ALL, 5000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		//Smuggler Shukirukin.
		switch (Rnd.get(1, 31)) {
			case 1:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 1191.4279f, 1090.6012f, 401.49847f, (byte) 61);
			break;
			case 2:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 959.07495f, 1116.043f, 634.8029f, (byte) 45);
			break;
			case 3:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 1112.987f, 1090.2983f, 568.39825f, (byte) 60);
			break;
			case 4:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 674.12555f, 752.60004f, 788.61957f, (byte) 29);
			break;
			case 5:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 977.57416f, 1094.8784f, 634.9581f, (byte) 70);
			break;
			case 6:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 614.5932f, 751.5792f, 788.61957f, (byte) 30);
			break;
			case 7:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 958.89166f, 1070.537f, 634.8393f, (byte) 48);
			break;
			case 8:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 939.1102f, 814.8324f, 697.1391f, (byte) 0);
			break;
			case 9:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 955.04755f, 1159.9681f, 690.9806f, (byte) 90);
			break;
			case 10:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 916.00885f, 989.8762f, 692.5784f, (byte) 107);
			break;
			case 11:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 938.50134f, 870.37964f, 697.1391f, (byte) 0);
			break;
			case 12:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 1087.3694f, 674.1943f, 701.58887f, (byte) 39);
			break;
			case 13:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 910.6424f, 836.0793f, 698.71344f, (byte) 30);
			break;
			case 14:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 1120.2046f, 778.5043f, 692.42737f, (byte) 49);
			break;
			case 15:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 1028.9359f, 709.89075f, 724.337f, (byte) 0);
			break;
			case 16:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 977.0958f, 851.4293f, 697.1447f, (byte) 90);
			break;
			case 17:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 882.7574f, 1161.0532f, 651.8337f, (byte) 0);
			break;
			case 18:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 918.0016f, 1251.89f, 656.17584f, (byte) 0);
			break;
			case 19:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 992.29834f, 1120.6741f, 624.7884f, (byte) 90);
			break;
			case 20:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 609.8654f, 792.7056f, 788.61957f, (byte) 87);
			break;
			case 21:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 935.47485f, 975.1226f, 692.5785f, (byte) 61);
			break;
			case 22:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 971.6995f, 815.0717f, 697.1391f, (byte) 61);
			break;
			case 23:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 910.17346f, 1047.8488f, 648.1572f, (byte) 60);
			break;
			case 24:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 913.40515f, 1251.4445f, 683.6889f, (byte) 0);
			break;
			case 25:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 841.2949f, 839.00415f, 698.7168f, (byte) 0);
			break;
			case 26:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 920.28064f, 745.5274f, 757.54047f, (byte) 48);
			break;
			case 27:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 1229.0371f, 1089.8674f, 402.11377f, (byte) 60);
			break;
			case 28:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 970.93726f, 710.7651f, 747.08154f, (byte) 91);
			break;
			case 29:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 796.0000f, 897.0000f, 697.64954f, (byte) 30);
			break;
			case 30:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 969.8386f, 871.6734f, 697.1391f, (byte) 60);
			break;
			case 31:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656830, 1029.3374f, 815.03546f, 697.1391f, (byte) 0);
			break;
		}
		//Yastikan & Varakan.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(650016, 900.0000f, 725.0000f, 749.0000f, (byte) 30);
			break;
			case 2:
				spawn(650017, 900.0000f, 725.0000f, 749.0000f, (byte) 30);
			break;
		}
		//Gursh.
		switch (Rnd.get(1, 6)) {
			case 1:
				spawn(650015, 1045.0000f, 876.0000f, 697.0000f, (byte) 90);
			break;
			case 2:
				spawn(650015, 1046.0000f, 805.0000f, 697.0000f, (byte) 31);
			break;
			case 3:
				spawn(650015, 1001.0000f, 882.0000f, 697.0000f, (byte) 90);
			break;
			case 4:
				spawn(650015, 1003.0000f, 804.0000f, 697.0000f, (byte) 30);
			break;
			case 5:
				spawn(650015, 954.0000f, 881.0000f, 697.0000f, (byte) 90);
			break;
			case 6:
				spawn(650015, 955.0000f, 804.0000f, 697.0000f, (byte) 30);
			break;
		}
	}
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 650016: //Yastikan.
			case 650017: //Varakan.
				doors.get(206).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
			break;
			case 650018: //Suffering Primeth.
				sp(650019, 789.0000f, 842.0000f, 694.0000f, (byte) 30, 10000, 0, null); //Liberated Primeth.
				sp(655269, 789.0000f, 870.0000f, 694.0000f, (byte) 91, 2000, 0, null); //Workshop Treasure Chest.
			break;
			case 650021: //Tarukan.
			    Npc primeth = instance.getNpc(655465); //Liberated Primeth.
				primeth.getSpawn().setWalkerId("Liberated_Primeth_2");
				WalkManager.startWalking((NpcAI2) primeth.getAi2());
				actionPrimeth(instance.getNpc(655465));
			break;
			case 650026: //Maddened Frigida.
				sp(806819, 1278.0000f, 1076.0000f, 374.0000f, (byte) 0, 0, 0, null); //Primeth's.
				final int Atis_Rith2 = spawnRace == Race.ASMODIANS ? 806845 : 806844;
				spawn(Atis_Rith2, 1323.0000f, 1090.0000f, 374.0000f, (byte) 0);
			break;
			case 653223: //Lym Ore Grinder.
				lymOreGrinder++;
				if (lymOreGrinder == 2) {
					doors.get(235).setOpen(true);
					chestPrimethForgeTask.cancel(true);
					//Both Lym Ore Grinders were destroyed. You can go to the Primeth Workstation.
					sendMsgByRace(1404592, Race.PC_ALL, 0);
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
							}
						}
					});
				}
			break;
			case 654728: //Frigida Captain.
				doors.get(179).setOpen(true);
				//The door is open and you can now go to the Lym Ore Storage.
				sendMsgByRace(1404585, Race.PC_ALL, 0);
			break;
			case 655211: //Frigida Legion Drakan Stealth Officer.
				doors.get(183).setOpen(true);
				//The door is open and you can now go to the Lym Ore Dissolving Room.
				sendMsgByRace(1404589, Race.PC_ALL, 0);
			break;
			case 650012:
			case 650027:
			case 656083:
			case 656085:
				despawnNpc(npc);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 836437: //Workshop Entrance.
			case 837096: //Primeth Furnace.
				if (player.getRace() == Race.ELYOS) {
					primethForgeE(player, 1079.0000f, 1599.0000f, 407.0000f, (byte) 57);
				} else {
					primethForgeA(player, 1774.0000f, 1788.0000f, 405.0000f, (byte) 25);
				}
			break;
		}
	}
	
	protected void primethForgeE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void primethForgeA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
	}
	
	private void actionPrimeth(final Npc primeth) {
        if (primeth != null) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isInstanceDestroyed) {
                        if (primeth != null) {
							SkillEngine.getInstance().getSkill(primeth, 17389, 60, primeth).useNoAnimationSkill();
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									if (primeth.getNpcId() == 655465) {
										doors.get(205).setOpen(true);
										despawnNpcs(instance.getNpcs(700998));
										despawnNpcs(instance.getNpcs(243697)); //Invincible Barriere.
										//Use the open entrance to move to the next area.
										sendMsgByRace(1402781, Race.PC_ALL, 0);
										//Behind the furnace is an air duct. I'll open up the passage for you, little ones.
										sendMsg(1502015, instance.getNpc(655465).getObjectId(), false, 0);
									}
								}
							}, 1500);
                        }
                    }
                }
            }, 28000);
        }
	}
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 233) {
			doors.get(233).setOpen(true);
			//When both Lym Ore Grinders are destroyed, you can go to the Primeth Workstation.
			sendMsgByRace(1404732, Race.PC_ALL, 0);
			//We need to destroy the Lym Ore Grinder as quickly as possible. We can use Supervisor Gersh's area attack to destroy the Grinder.
			sendMsgByRace(1404590, Race.PC_ALL, 4000);
			//Once the time runs out, the treasure chest will disappear. If you destroy both Grinders, you can go to the Primeth Workstation.
			sendMsgByRace(1404591, Race.PC_ALL, 8000);
			primethForgeChest.add((Npc) spawn(655269, 909.0000f, 844.0000f, 698.0000f, (byte) 0)); //Treasure Box.
			chestPrimethForgeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					primethForgeChest.get(0).getController().onDelete();
				}
			}, 180000);
			instance.doOnAllPlayers(new Visitor<Player>() {
			    @Override
			    public void visit(Player player) {
				    if (player.isOnline()) {
						PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 180));
					}
				}
			});
		}
	}
	
	private void sendPacket(Player player, final String variable, final int value) {
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_CONDITION_VARIABLE(player, variable, value));
				}
			}
		});
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = primethForgeTask.head(), end = primethForgeTask.tail(); (n = n.getNext()) != end; ) {
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
        primethForgeTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        primethForgeTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		stopInstanceTask();
		doors.clear();
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
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
}