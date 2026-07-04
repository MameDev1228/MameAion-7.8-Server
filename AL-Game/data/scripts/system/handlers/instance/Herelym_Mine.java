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
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302500000)
public class Herelym_Mine extends GeneralInstanceHandler
{
	private int BossTunnel1;
	private int BossTunnel2;
	//////////////////////////
	private int IDF7MineRatman;
	private int IDF7MineSheluk;
	private int IDF7MineFOBJPot;
	private Race herelymMineRace;
	private int IDF7MinePirateZombie;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	private final FastList<Future<?>> herelymMineTask = FastList.newInstance();
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 655284: //Great Smuggler Shukirukin.
			case 655285: //Smuggler Shukirukin.
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
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		spawn(820653, 866.0000f, 528.0000f, 295.0000f, (byte) 61); //Blessed Apostle Transformation Corridor.
		switch (Rnd.get(1, 3)) {
			case 1:
			    //The great Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404595, Race.PC_ALL, 30000);
				spawn(655284, 203.0000f, 894.0000f, 277.0000f, (byte) 91); //Great Smuggler Shukirukin.
			break;
			case 2:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 30000);
				spawn(655285, 203.0000f, 894.0000f, 277.0000f, (byte) 91); //Smuggler Shukirukin.
			break;
			case 3:
			break;
		}
	}
	
	private void herelymMineTimer() {
		//Access to all shafts of Herelym Mine will be prohibited after 2 minutes.
		sendMsgByRace(1404800, Race.PC_ALL, 0);
		//Access to all shafts of Herelym Mine will be prohibited after 1 minute.
		sendMsgByRace(1404801, Race.PC_ALL, 60000);
		//Access to all shafts of Herelym Mine will be prohibited after 10 seconds.
		sendMsgByRace(1404802, Race.PC_ALL, 110000);
		//Access to all shafts of the Herelym Mine are now restricted.
		sendMsgByRace(1404803, Race.PC_ALL, 120000);
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		//Pit Of Envy.
		sendPacket(player, "UI_Gauge_01", 0 + 1);
		//Endless Fixation.
		sendPacket(player, "UI_Gauge_02", 0 + 1);
		//Miser's Fall.
		sendPacket(player, "UI_Gauge_03", 0 + 1);
		if (herelymMineRace == null) {
            herelymMineRace = player.getRace();
            ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					spawnHerelymMine();
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
	
	private void spawnHerelymMine() {
		herelymMineTimer();
		//Eliminate Girad is in charge of gathering Lym Ore.
		sendMsgByRace(1404830, Race.PC_ALL, 10000);
		//Balaurs are rushing in to tighten their defenses after the 4 Named bosses were killed. Be on your guard!
		sendMsgByRace(1404820, Race.PC_ALL, 15000);
		//You can get a better reward by wiping out all the guards in each shaft.
		sendMsgByRace(1404831, Race.PC_ALL, 20000);
        //IDF7_Mine_Info_NPC_01.
		final int IDF7_Mine_Info_NPC_01 = herelymMineRace == Race.ASMODIANS ? 837419 : 837416;
		Npc NpcRace1 = (Npc) spawn(IDF7_Mine_Info_NPC_01, 834.0000f, 548.0000f, 296.0000f, (byte) 101);
		//The entrance to the Pit of Envy is exceptionally narrow. Only one person can enter at a time.
		NpcShoutsService.getInstance().sendMsg(NpcRace1, 1502190, NpcRace1.getObjectId(), 0, 3000);
		//IDF7_Mine_Info_NPC_02.
		final int IDF7_Mine_Info_NPC_02 = herelymMineRace == Race.ASMODIANS ? 837420 : 837417;
		Npc NpcRace2 = (Npc) spawn(IDF7_Mine_Info_NPC_02, 833.0000f, 512.0000f, 296.0000f, (byte) 112);
		//Endless Fixation is notorious for its lack of oxygen. It wouldn’t be safe to have more than two people in there at a time..
		NpcShoutsService.getInstance().sendMsg(NpcRace2, 1502191, NpcRace2.getObjectId(), 0, 6000);
		//IDF7_Mine_Info_NPC_03.
		final int IDF7_Mine_Info_NPC_03 = herelymMineRace == Race.ASMODIANS ? 837421 : 837418;
		Npc NpcRace3 = (Npc) spawn(IDF7_Mine_Info_NPC_03, 827.0000f, 531.0000f, 295.0000f, (byte) 105);
		//Miser’s Fall is rife with pitfalls and unsteady ground. You won’t want any more than three people mucking around in there.
		NpcShoutsService.getInstance().sendMsg(NpcRace3, 1502192, NpcRace3.getObjectId(), 0, 9000);
		//Veradach/Ribeus.
		final int Veradach_Ribeus = herelymMineRace == Race.ASMODIANS ? 837439 : 837436;
		Npc NpcRace4 = (Npc) spawn(Veradach_Ribeus, 843.0000f, 527.0000f, 295.0000f, (byte) 9);
		//Keep fighting! Reinforcements are on the way!
		NpcShoutsService.getInstance().sendMsg(NpcRace4, 1501479, NpcRace4.getObjectId(), 0, 0);
		//Hergard/Ithnil.
		final int Hergard_Ithnil = herelymMineRace == Race.ASMODIANS ? 837440 : 837437;
		spawn(Hergard_Ithnil, 419.0000f, 504.0000f, 307.0000f, (byte) 116);
        spawn(Hergard_Ithnil, 456.0000f, 177.0000f, 275.0000f, (byte) 13);
        spawn(Hergard_Ithnil, 357.0000f, 197.0000f, 262.0000f, (byte) 33);
        spawn(Hergard_Ithnil, 546.0000f, 631.0000f, 295.0000f, (byte) 44);
        spawn(Hergard_Ithnil, 491.0000f, 919.0000f, 280.0000f, (byte) 84);
		//IDF7_Mine_Guard_Fighter_Wound_M.
		final int IDF7_Mine_Guard_Fighter_Wound_M = herelymMineRace == Race.ASMODIANS ? 837632 : 837630;
		spawn(IDF7_Mine_Guard_Fighter_Wound_M, 845.0000f, 515.0000f, 295.0000f, (byte) 119);
        spawn(IDF7_Mine_Guard_Fighter_Wound_M, 850.0000f, 546.0000f, 295.0000f, (byte) 119);
        spawn(IDF7_Mine_Guard_Fighter_Wound_M, 852.0000f, 516.0000f, 295.0000f, (byte) 1);
        spawn(IDF7_Mine_Guard_Fighter_Wound_M, 853.0000f, 542.0000f, 295.0000f, (byte) 1);
        spawn(IDF7_Mine_Guard_Fighter_Wound_M, 849.0000f, 515.0000f, 295.0000f, (byte) 1);
		//IDF7_Mine_Guard_Ranger_Gasp_M.
		final int IDF7_Mine_Guard_Ranger_Gasp_M = herelymMineRace == Race.ASMODIANS ? 837633 : 837631;
		spawn(IDF7_Mine_Guard_Ranger_Gasp_M, 853.0000f, 520.0000f, 295.0000f, (byte) 1);
        spawn(IDF7_Mine_Guard_Ranger_Gasp_M, 848.0000f, 538.0000f, 295.0000f, (byte) 119);
        spawn(IDF7_Mine_Guard_Ranger_Gasp_M, 842.0000f, 545.0000f, 295.0000f, (byte) 105);
		//IDF7_Mine_Mbrowniemwacra_50_AE_1_Fight.
		spawn(656587, 840.0000f, 524.0000f, 296.0000f, (byte) 0);
		spawn(656587, 842.0000f, 522.0000f, 296.0000f, (byte) 0);
		//Pit Of Envy Entrance.
		spawn(837422, 829.0000f, 550.0000f, 299.0000f, (byte) 0, 215);
		//Endless Fixation Entrance.
		spawn(837423, 831.0000f, 507.0000f, 300.0000f, (byte) 0, 183);
		//Miser's Fall Entrance.
		spawn(837424, 824.0000f, 528.0000f, 302.0000f, (byte) 0, 294);
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
		   /**
			* Endless Fixation.
			*/
			case 656320:
			case 656325:
			    IDF7MineSheluk++;
				if (IDF7MineSheluk == 4) {
					startRaidHererim1();
					//Prepare for combat! Enemies approaching!
					sendMsgByRace(1402785, Race.PC_ALL, 0);
				} else if (IDF7MineSheluk == 8) {
					startRaidHererim2();
					//Prepare for combat! Enemies approaching!
					sendMsgByRace(1402785, Race.PC_ALL, 0);
					//Lym Ore has been recreated. The undead are rushing in.
					sendMsgByRace(1404822, Race.PC_ALL, 3000);
				} else if (IDF7MineSheluk == 12) {
					startRaidHererim3();
					//Eliminate spawning Klaws so that the Scouts can enter.
					sendMsgByRace(1404824, Race.PC_ALL, 0);
					//The Scouts will be able to enter if you eliminate just a little more.
				    sendMsgByRace(1404825, Race.PC_ALL, 3000);
				    //It’s almost done.
				    sendMsgByRace(1404826, Race.PC_ALL, 6000);
				} else if (IDF7MineSheluk == 16) {
					killNpc(getNpcs(837600));
					//Use the open entrance to move to the next area.
					sendMsgByRace(1402781, Race.PC_ALL, 0);
					//The Scouts have successfully entered.
					sendMsgByRace(1404827, Race.PC_ALL, 3000);
					sendPacket(player, "UI_Gauge_02", 2 + 1);
					//IDF7_Mine_Info_Wave_NPC_01.
					final int IDF7_Mine_Info_Wave_NPC_01 = herelymMineRace == Race.ASMODIANS ? 837474 : 837471;
					spawn(IDF7_Mine_Info_Wave_NPC_01, 378.0000f, 207.0000f, 262.0000f, (byte) 0);
					spawn(IDF7_Mine_Info_Wave_NPC_01, 377.0000f, 200.0000f, 262.0000f, (byte) 0);
					//IDF7_Mine_Info_Wave_NPC_03.
					final int IDF7_Mine_Info_Wave_NPC_03 = herelymMineRace == Race.ASMODIANS ? 837476 : 837473;
					Npc NpcRace6 = (Npc) spawn(IDF7_Mine_Info_Wave_NPC_03, 377.0000f, 205.0000f, 262.0000f, (byte) 105);
					//We’ll take care of things from here. Move along.
					NpcShoutsService.getInstance().sendMsg(NpcRace6, 1502196, NpcRace6.getObjectId(), 0, 0);
					//The undead could reappear at any moment! ... Err, go ahead--after you....
					NpcShoutsService.getInstance().sendMsg(NpcRace6, 1502197, NpcRace6.getObjectId(), 0, 3000);
				}
			break;
			case 656335: //Vengeful Foreman Girad.
			    doors.get(217).setOpen(true);
				//A heavy door has opened somewhere.
				sendMsgByRace(1401839, Race.PC_ALL, 0);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
				sendPacket(player, "UI_Gauge_02", 3 + 1);
				spawn(837454, 116.0000f, 886.0000f, 276.0000f, (byte) 0);
				//IDF7_Mine_Info_NPC_041.
				final int IDF7_Mine_Info_NPC_041 = herelymMineRace == Race.ASMODIANS ? 837543 : 837542;
				Npc NpcRace7 = (Npc) spawn(IDF7_Mine_Info_NPC_041, 212.0000f, 201.0000f, 249.0000f, (byte) 107);
				//There’s a hidden entrance here. Do you feel that? That prickle of dark energy? If you’re going to look around, you’d better make it quick!
				NpcShoutsService.getInstance().sendMsg(NpcRace7, 1502203, NpcRace7.getObjectId(), 0, 0);
			break;
			
		   /**
			* Miser's Fall.
			*/
			case 656326:
			    killNpc(getNpcs(837601));
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 0);
				sendPacket(player, "UI_Gauge_03", 1 + 1);
			break;
			case 656331:
			case 656333:
				IDF7MinePirateZombie++;
				if (IDF7MinePirateZombie == 6) {
					killNpc(getNpcs(837603));
					//Use the open entrance to move to the next area.
					sendMsgByRace(1402781, Race.PC_ALL, 0);
					sendPacket(player, "UI_Gauge_03", 2 + 1);
				}
			break;
			case 656336: //Vengeful Foreman Girad.
			    doors.get(39).setOpen(true);
				//A heavy door has opened somewhere.
				sendMsgByRace(1401839, Race.PC_ALL, 0);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
				sendPacket(player, "UI_Gauge_03", 3 + 1);
				spawn(837455, 116.0000f, 883.0000f, 276.0000f, (byte) 0);
				//IDF7_Mine_Info_NPC_042.
				final int IDF7_Mine_Info_NPC_042 = herelymMineRace == Race.ASMODIANS ? 837543 : 837542;
				Npc NpcRace8 = (Npc) spawn(IDF7_Mine_Info_NPC_042, 219.0000f, 472.0000f, 316.0000f, (byte) 100);
				//There’s a hidden entrance here. Do you feel that? That prickle of dark energy? If you’re going to look around, you’d better make it quick!
				NpcShoutsService.getInstance().sendMsg(NpcRace8, 1502203, NpcRace8.getObjectId(), 0, 0);
			break;
			
		   /**
			* Pit Of Envy.
			*/
		    case 656314: //Mine Foreman Dimo.
			case 656315: //Junior Foreman Lorenta.
				BossTunnel1++;
				if (BossTunnel1 == 2) {
					sendPacket(player, "UI_Gauge_01", 1 + 1);
					killNpc(getNpcs(837597));
					//Use the open entrance to move to the next area.
					sendMsgByRace(1402781, Race.PC_ALL, 0);
				}
			break;
			case 656316: //Expedition Captain Torben.
			case 656317: //Research Group Leader Alimas.
				BossTunnel2++;
				if (BossTunnel2 == 2) {
					sendPacket(player, "UI_Gauge_01", 2 + 1);
					killNpc(getNpcs(837598));
					//Use the open entrance to move to the next area.
					sendMsgByRace(1402781, Race.PC_ALL, 0);
				}
			break;
			case 656334: //Vengeful Foreman Girad.
			    doors.get(86).setOpen(true);
				//A heavy door has opened somewhere.
				sendMsgByRace(1401839, Race.PC_ALL, 0);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
				sendPacket(player, "UI_Gauge_01", 3 + 1);
				spawn(837453, 116.0000f, 890.0000f, 276.0000f, (byte) 0);
				//IDF7_Mine_Info_NPC_04.
				final int IDF7_Mine_Info_NPC_04 = herelymMineRace == Race.ASMODIANS ? 837543 : 837542;
				Npc NpcRace5 = (Npc) spawn(IDF7_Mine_Info_NPC_04, 402.0000f, 804.0000f, 289.0000f, (byte) 82);
				//There’s a hidden entrance here. Do you feel that? That prickle of dark energy? If you’re going to look around, you’d better make it quick!
				NpcShoutsService.getInstance().sendMsg(NpcRace5, 1502203, NpcRace5.getObjectId(), 0, 0);
			break;
			
		   /**
			* Hidden Smuggling Spot.
			*/
			case 656353:
				IDF7MineRatman++;
				if (IDF7MineRatman == 5) {
					doors.get(481).setOpen(true);
					//A heavy door has opened somewhere.
					sendMsgByRace(1401839, Race.PC_ALL, 0);
					//Use the open entrance to move to the next area.
					sendMsgByRace(1402781, Race.PC_ALL, 2000);
				}
			break;
			case 656337: //Dark Sorcerer Bugarota.
				spawn(837432, 112.0000f, 895.0000f, 276.0000f, (byte) 0); //Herelym Mine Exit.
				spawn(837456, 124.0000f, 886.0000f, 276.0000f, (byte) 0); //Bugarota’s Special Secret Box.
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 703646: //IDF7_Mine_FOBJ_Pot_Q16052A.
			case 703647: //IDF7_Mine_FOBJ_Pot_Q16052B.
			case 703648: //IDF7_Mine_FOBJ_Pot_Q16052C.
			    IDF7MineFOBJPot++;
				if (IDF7MineFOBJPot == 1) {
					despawnNpc(npc);
				} else if (IDF7MineFOBJPot == 2) {
					despawnNpc(npc);
				} else if (IDF7MineFOBJPot == 3) {
					despawnNpc(npc);
				} else if (IDF7MineFOBJPot == 4) {
					despawnNpc(npc);
					killNpc(getNpcs(837599));
					//Use the open entrance to move to the next area.
					sendMsgByRace(1402781, Race.PC_ALL, 0);
					sendPacket(player, "UI_Gauge_02", 1 + 1);
					//IDF7_Mine_Info_Wave_NPC_01.
					final int IDF7_Mine_Info_Wave_NPC_01 = herelymMineRace == Race.ASMODIANS ? 837474 : 837471;
					spawn(IDF7_Mine_Info_Wave_NPC_01, 522.0000f, 217.0000f, 276.0000f, (byte) 12);
					spawn(IDF7_Mine_Info_Wave_NPC_01, 518.0000f, 224.0000f, 276.0000f, (byte) 12);
					//IDF7_Mine_Info_Wave_NPC_02.
					final int IDF7_Mine_Info_Wave_NPC_02 = herelymMineRace == Race.ASMODIANS ? 837475 : 837472;
					Npc NpcRace9 = (Npc) spawn(IDF7_Mine_Info_Wave_NPC_02, 519.0000f, 223.0000f, 276.0000f, (byte) 109);
					//We can take care of the rest of this. You’d better get to your next mission.
					NpcShoutsService.getInstance().sendMsg(NpcRace9, 1502198, NpcRace9.getObjectId(), 0, 0);
					//I guess we’re wrapping up here. You’d better leave before the undead decide to show back up.
					NpcShoutsService.getInstance().sendMsg(NpcRace9, 1502199, NpcRace9.getObjectId(), 0, 3000);
				}
			break;
			//Suspicious Secret Entrance.
			case 837428:
			case 837429:
			case 837430:
				suspiciousSecret(player, 217.0000f, 851.0000f, 284.0000f, (byte) 32);
			break;
		}
	}
	
	protected void suspiciousSecret(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	
	private void raidHererim(final Npc npc) {
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
	
	public void startRaidHererim1() {
	    raidHererim((Npc)spawn(656320, 384.0000f, 183.0000f, 262.0000f, (byte) 27));
		raidHererim((Npc)spawn(656320, 381.0000f, 223.0000f, 262.0000f, (byte) 112));
		raidHererim((Npc)spawn(656325, 412.0000f, 220.0000f, 262.0000f, (byte) 77));
		raidHererim((Npc)spawn(656325, 410.0000f, 180.0000f, 262.0000f, (byte) 40));
	}
	public void startRaidHererim2() {
	    raidHererim((Npc)spawn(656320, 384.0000f, 183.0000f, 262.0000f, (byte) 27));
		raidHererim((Npc)spawn(656320, 381.0000f, 223.0000f, 262.0000f, (byte) 112));
		raidHererim((Npc)spawn(656325, 412.0000f, 220.0000f, 262.0000f, (byte) 77));
		raidHererim((Npc)spawn(656325, 410.0000f, 180.0000f, 262.0000f, (byte) 40));
	}
	public void startRaidHererim3() {
	    raidHererim((Npc)spawn(656320, 384.0000f, 183.0000f, 262.0000f, (byte) 27));
		raidHererim((Npc)spawn(656320, 381.0000f, 223.0000f, 262.0000f, (byte) 112));
		raidHererim((Npc)spawn(656325, 412.0000f, 220.0000f, 262.0000f, (byte) 77));
		raidHererim((Npc)spawn(656325, 410.0000f, 180.0000f, 262.0000f, (byte) 40));
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = herelymMineTask.head(), end = herelymMineTask.tail(); (n = n.getNext()) != end;) {
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
        herelymMineTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        herelymMineTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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