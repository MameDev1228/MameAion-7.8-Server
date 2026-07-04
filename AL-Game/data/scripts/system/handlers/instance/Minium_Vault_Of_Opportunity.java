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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302642000)
public class Minium_Vault_Of_Opportunity extends GeneralInstanceHandler
{
	private Race miniumVaultRace;
	private Future<?> chestHMWMiniumTask;
	private boolean isStartTimer1 = false;
	private boolean isStartTimer2 = false;
	private boolean isStartTimer3 = false;
	private boolean isStartTimer4 = false;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	private List<Npc> HMWMiniumChest = new ArrayList<Npc>();
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
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
		super.onInstanceCreate(instance);
		if (!isStartTimer1) {
			isStartTimer1 = true;
			System.currentTimeMillis();
			instance.doOnAllPlayers(new Visitor<Player>() {
			    @Override
			    public void visit(Player player) {
				    if (player.isOnline()) {
						PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 480));
					}
				}
			});
			HMWMiniumChest.add((Npc) spawn(807122, 536.5219f, 723.5650f, 192.6260f, (byte) 38));
			HMWMiniumChest.add((Npc) spawn(807123, 531.2882f, 721.3386f, 192.6297f, (byte) 33));
			HMWMiniumChest.add((Npc) spawn(807124, 523.1677f, 721.6073f, 192.6269f, (byte) 26));
			HMWMiniumChest.add((Npc) spawn(807125, 518.0409f, 724.2635f, 192.6272f, (byte) 19));
			chestHMWMiniumTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					StartTimer2();
					//One of the Ancient Treasure Boxes is missing.
					sendMsgByRace(1400245, Race.PC_ALL, 0);
					HMWMiniumChest.get(0).getController().onDelete();
				}
			}, 480000);
		}
		//Find the "Minium Box" before the Minion.
		sendPacket(player, "UI_Gauge_01", 3 + 1);
		if (miniumVaultRace == null) {
            miniumVaultRace = player.getRace();
            ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					spawnMiniumVault();
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							player.getController().updateZone();
							player.getController().updateNearbyQuests();
						}
					});
				}
			}, 10000);
        }
	}
	
	private void miniumVaultTimer() {
		//The Treasure Chest will disappear if your exploration takes too long.
		sendMsgByRace(1405861, Race.PC_ALL, 3000);
		//Other scouts have appeared.
		sendMsgByRace(1405863, Race.PC_ALL, 12000);
    }
	
	private void spawnMiniumVault() {
		miniumVaultTimer();
		//Select a warehouse to explore.
		sendMsgByRace(1405866, Race.PC_ALL, 0);
		//IDAbRe_Up3_Urf_Info_Fi_01.
		final int IDAbRe_Up3_Urf_Info_Fi_01 = miniumVaultRace == Race.ASMODIANS ? 807131 : 807130;
		spawn(IDAbRe_Up3_Urf_Info_Fi_01, 492.0000f, 195.0000f, 179.0000f, (byte) 103);
		//IDAbRe_Up3_Urf_Info_Fi_02.
		final int IDAbRe_Up3_Urf_Info_Fi_02 = miniumVaultRace == Race.ASMODIANS ? 807133 : 807132;
		spawn(IDAbRe_Up3_Urf_Info_Fi_02, 415.0000f, 396.0000f, 197.0000f, (byte) 70);
		//IDAbRe_Up3_Urf_Info_As.
		final int IDAbRe_Up3_Urf_Info_As = miniumVaultRace == Race.ASMODIANS ? 807135 : 807134;
		spawn(IDAbRe_Up3_Urf_Info_As, 527.0000f, 248.0000f, 179.0000f, (byte) 86);
		//IDAbRe_Up3_Urf_Info_Pr_01.
		final int IDAbRe_Up3_Urf_Info_Pr_01 = miniumVaultRace == Race.ASMODIANS ? 807137 : 807136;
		spawn(IDAbRe_Up3_Urf_Info_Pr_01, 562.0000f, 190.0000f, 179.0000f, (byte) 46);
		//IDAbRe_Up3_Urf_Info_Pr_02.
		final int IDAbRe_Up3_Urf_Info_Pr_02 = miniumVaultRace == Race.ASMODIANS ? 807139 : 807138;
		spawn(IDAbRe_Up3_Urf_Info_Pr_02, 639.0000f, 396.0000f, 197.0000f, (byte) 78);
		//IDAbRe_Up3_Urf_Info_Bc_Boss.
		final int IDAbRe_Up3_Urf_Info_Bc_Boss = miniumVaultRace == Race.ASMODIANS ? 807141 : 807140;
		spawn(IDAbRe_Up3_Urf_Info_Bc_Boss, 528.0000f, 453.0000f, 179.0000f, (byte) 72);
		spawn(IDAbRe_Up3_Urf_Info_Bc_Boss, 641.0000f, 547.0000f, 189.0000f, (byte) 74);
		spawn(IDAbRe_Up3_Urf_Info_Bc_Boss, 416.0000f, 547.0000f, 189.0000f, (byte) 74);
		//IDAbRe_Up3_Urf_Witch_Npc.
		final int IDAbRe_Up3_Urf_Witch_Npc = miniumVaultRace == Race.ASMODIANS ? 661348 : 661346;
		spawn(IDAbRe_Up3_Urf_Witch_Npc, 615.0000f, 272.0000f, 191.0000f, (byte) 110);
		spawn(IDAbRe_Up3_Urf_Witch_Npc, 655.0000f, 248.0000f, 191.0000f, (byte) 48);
		spawn(IDAbRe_Up3_Urf_Witch_Npc, 616.0000f, 248.0000f, 191.0000f, (byte) 11);
		spawn(IDAbRe_Up3_Urf_Witch_Npc, 653.0000f, 271.0000f, 191.0000f, (byte) 74);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//A Minion is trying to steal a box!
				sendMsgByRace(1405868, Race.PC_ALL, 0);
				//It is not a real Kerubiel.
				sendMsgByRace(1405865, Race.PC_ALL, 3000);
				spawn(661350, 523.0000f, 214.0000f, 178.0000f, (byte) 93);
				spawn(661351, 530.0000f, 214.0000f, 178.0000f, (byte) 91);
				//37S21G.
				final int _37S21G = miniumVaultRace == Race.ASMODIANS ? 807126 : 807099;
				spawn(_37S21G, 503.0000f, 198.0000f, 179.0000f, (byte) 118);
				//0M618S.
				final int _0M618S = miniumVaultRace == Race.ASMODIANS ? 807127 : 807100;
				spawn(_0M618S, 529.0000f, 238.0000f, 179.0000f, (byte) 77);
				//J227S4.
				final int _J227S4 = miniumVaultRace == Race.ASMODIANS ? 807128 : 807101;
				spawn(_J227S4, 548.0000f, 193.0000f, 179.0000f, (byte) 38);
				//_37S21G_0M618S_J227S4.
				final int _37S21G_0M618S_J227S4 = miniumVaultRace == Race.ASMODIANS ? 807129 : 807103;
				spawn(_37S21G_0M618S_J227S4, 527.0000f, 211.0000f, 178.0000f, (byte) 88);
			}
		}, 3000);
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 661322: //Up3_Urf_Bossof_Fi.
			    chestHMWMiniumTask.cancel(true);
				spawn(807102, 527.0000f, 737.0000f, 195.0000f, (byte) 90); //Minium Vault Exit.
				instance.doOnAllPlayers(new Visitor<Player>() {
			        @Override
			        public void visit(Player player) {
				        if (player.isOnline()) {
						    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
					    }
				    }
			    });
			break;
		}
	}
	
	@Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
			case 807120:
				miniumVault(player, 635.0000f, 290.0000f, 190.0000f, (byte) 28);
            break;
        }
    }
	
	protected void miniumVault(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	private void StartTimer2() {
        if (!isStartTimer2) {
			isStartTimer2 = true;
			System.currentTimeMillis();
			instance.doOnAllPlayers(new Visitor<Player>() {
			    @Override
			    public void visit(Player player) {
				    if (player.isOnline()) {
					    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 480));
					}
				}
			});
			chestHMWMiniumTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					StartTimer3();
					//One of the Ancient Treasure Boxes is missing.
					sendMsgByRace(1400245, Race.PC_ALL, 0);
					HMWMiniumChest.get(1).getController().onDelete();
				}
			}, 480000);
		}
	}
	private void StartTimer3() {
	    if (!isStartTimer3) {
			isStartTimer3 = true;
			System.currentTimeMillis();
			instance.doOnAllPlayers(new Visitor<Player>() {
			    @Override
			    public void visit(Player player) {
				    if (player.isOnline()) {
					    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 480));
					}
				}
			});
			chestHMWMiniumTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					StartTimer4();
					//One of the Ancient Treasure Boxes is missing.
					sendMsgByRace(1400245, Race.PC_ALL, 0);
					HMWMiniumChest.get(2).getController().onDelete();
				}
			}, 480000);
		}
	}
	private void StartTimer4() {
	    if (!isStartTimer4) {
			isStartTimer4 = true;
			System.currentTimeMillis();
			instance.doOnAllPlayers(new Visitor<Player>() {
			    @Override
			    public void visit(Player player) {
				    if (player.isOnline()) {
					    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 480));
					}
				}
			});
			chestHMWMiniumTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					//One of the Ancient Treasure Boxes is missing.
					sendMsgByRace(1400245, Race.PC_ALL, 0);
					//All the Ancient Treasure Boxes are missing.
					sendMsgByRace(1400244, Race.PC_ALL, 3000);
					HMWMiniumChest.get(3).getController().onDelete();
				}
			}, 480000);
		}
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20815);
		effectController.removeEffect(20816);
		effectController.removeEffect(20817);
		////////////////////////////////////////////
		SkillLearnService.removeSkill(player, 5952);
		SkillLearnService.removeSkill(player, 5954);
		SkillLearnService.removeSkill(player, 5955);
		SkillLearnService.removeSkill(player, 5956);
		SkillLearnService.removeSkill(player, 5957);
		SkillLearnService.removeSkill(player, 5958);
		SkillLearnService.removeSkill(player, 5959);
		SkillLearnService.removeSkill(player, 5960);
		SkillLearnService.removeSkill(player, 5961);
		SkillLearnService.removeSkill(player, 5962);
		SkillLearnService.removeSkill(player, 5963);
		SkillLearnService.removeSkill(player, 5964);
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
	
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
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