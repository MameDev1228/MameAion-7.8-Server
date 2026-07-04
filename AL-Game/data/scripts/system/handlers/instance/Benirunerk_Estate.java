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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(302660000)
public class Benirunerk_Estate extends GeneralInstanceHandler
{
	private Race videoRace;
	private Race benirunerkRace;
	private int benirunerkBattle;
	private int benirunerkGenerator;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 656832: //Great Smuggler Shukirukin.
			case 656833: //Smuggler Shukirukin.
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
			case 839633: //Oversized Armor SVR-07 Cubic Crystal.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188072805, 1));
					}
				}
			break;
        }
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					final int Estate1 = videoRace == Race.ASMODIANS ? 1048 : 1047;
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, Estate1));
				}
			}
		});
		if (benirunerkRace == null) {
            benirunerkRace = player.getRace();
            benirunerkEstate();
        }
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
		//The high-voltage current prevents anyone from leaving the party room. The generator will need to be shut off.
		sendMsgByRace(1405211, Race.PC_ALL, 10000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		switch (Rnd.get(1, 3)) {
			case 1:
			    //The great Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404595, Race.PC_ALL, 10000);
				spawn(656832, 528.0000f, 761.0000f, 178.0000f, (byte) 101);
			break;
			case 2:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 10000);
				spawn(656833, 528.0000f, 761.0000f, 178.0000f, (byte) 101);
			break;
			case 3:
			break;
		}
    }
	
	private void benirunerkEstate() {
		//Donas-Monan.
		final int Donas_Monan = benirunerkRace == Race.ASMODIANS ? 838345 : 838344;
		Npc NpcRace = (Npc) spawn(Donas_Monan, 638.0000f, 462.0000f, 169.0000f, (byte) 62); 
		//I'm finally here. Can I talk to you for a moment before the party?
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502613, NpcRace.getObjectId(), 0, 8000);
		/////////////////////////////////////////////////////////////////////////////////////////
		//IDF8_House_Pierrot_01_M.
		final int IDF8_House_Pierrot_01_M = benirunerkRace == Race.ASMODIANS ? 839199 : 839192;
		spawn(IDF8_House_Pierrot_01_M, 663.0000f, 438.0000f, 169.0000f, (byte) 52);
        spawn(IDF8_House_Pierrot_01_M, 641.0000f, 471.0000f, 169.0000f, (byte) 20);
		//IDF8_House_Pierrot_01_F.
		final int IDF8_House_Pierrot_01_F = benirunerkRace == Race.ASMODIANS ? 839200 : 839193;
		spawn(IDF8_House_Pierrot_01_F, 642.0000f, 473.0000f, 169.0000f, (byte) 79);
		//IDF8_House_Pierrot_02_M.
		final int IDF8_House_Pierrot_02_M = benirunerkRace == Race.ASMODIANS ? 839201 : 839194;
		spawn(IDF8_House_Pierrot_02_M, 631.0000f, 436.0000f, 169.0000f, (byte) 1);
        spawn(IDF8_House_Pierrot_02_M, 661.0000f, 449.0000f, 169.0000f, (byte) 18);
        spawn(IDF8_House_Pierrot_02_M, 662.0000f, 481.0000f, 169.0000f, (byte) 86);
		//IDF8_House_Pierrot_03_M.
		final int IDF8_House_Pierrot_03_M = benirunerkRace == Race.ASMODIANS ? 839202 : 839195;
		spawn(IDF8_House_Pierrot_03_M, 642.0000f, 451.0000f, 169.0000f, (byte) 91);
        spawn(IDF8_House_Pierrot_03_M, 662.0000f, 476.0000f, 169.0000f, (byte) 107);
		//IDF8_House_Warship 1494.
		final int IDF8_House_Warship = benirunerkRace == Race.ASMODIANS ? 720240 : 720239;
		spawn(IDF8_House_Warship, 522.0000f, 763.0000f, 202.0000f, (byte) 0, 1495);
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 658676:
			case 658679:
			case 658680:
			    despawnNpc(npc);
			    benirunerkBattle++;
				if (benirunerkBattle == 20) {
					killNpc(getNpcs(659890)); //Benirung's Mansion Off 1.
					killNpc(getNpcs(659891)); //Benirung's Mansion Off 2.
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(658736, 679.0000f, 480.0000f, 169.0000f, (byte) 0, 58);
							spawn(658737, 679.0000f, 443.0000f, 169.0000f, (byte) 0, 59);
						}
					}, 8000);
				}
			break;
			case 858512: //소형기갑 VR-04.
				despawnNpc(npc);
				killNpc(getNpcs(858516)); //IDF8_House_HugeRider_Rider_80_Ae.
				//Shulack_Rider_Poly.
				spawn(858542, 562.0000f, 757.0000f, 178.0000f, (byte) 68);
				spawn(858542, 535.0000f, 770.0000f, 178.0000f, (byte) 93);
				spawn(858542, 538.0000f, 771.0000f, 178.0000f, (byte) 94);
				spawn(858542, 560.0000f, 760.0000f, 178.0000f, (byte) 72);
				spawn(858542, 514.0000f, 760.0000f, 178.0000f, (byte) 108);
				spawn(858542, 513.0000f, 756.0000f, 178.0000f, (byte) 114);
				spawn(858513, 533.0000f, 742.0000f, 179.0000f, (byte) 98); //특대기갑 SVR-07.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							final int Estate2 = videoRace == Race.ASMODIANS ? 1050 : 1049;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, Estate2));
						}
					}
				});
			break;
			case 858513: //특대기갑 SVR-07.
			    despawnNpc(npc);
				killNpc(getNpcs(858515));
				spawn(838376, 544.0000f, 747.0000f, 180.0000f, (byte) 0); //Benirunerk's Estate Treasure Box.
				spawn(839633, 525.0000f, 744.0000f, 179.0000f, (byte) 107); //Oversized Armor SVR-07 Cubic Crystal.
				spawn(838375, 525.0000f, 748.0000f, 179.0000f, (byte) 0, 61); //Benirunerk's Estate Exit.
				//Veille-Mastarius.
				final int Veille_Mastarius = benirunerkRace == Race.ASMODIANS ? 838347 : 838346;
				spawn(Veille_Mastarius, 536.0000f, 753.0000f, 178.0000f, (byte) 96);
				spawn(858537, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //특대기갑 SVR-07 Destroy.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							player.getController().updateZone();
				            player.getController().updateNearbyQuests();
							final int Estate3 = videoRace == Race.ASMODIANS ? 1052 : 1051;
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, Estate3));
						}
					}
				});
			break;
			case 858514:
			case 858515:
			    despawnNpc(npc);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		PlayerEffectController effectController = player.getEffectController();
		switch (npc.getNpcId()) {
			case 658736: //Benirunerk's Estate Generator 1.
			case 658737: //Benirunerk's Estate Generator 2.
			    despawnNpc(npc);
				benirunerkGenerator++;
				if (benirunerkGenerator == 2) {
					doors.get(60).setOpen(true);
					despawnNpcs(instance.getNpcs(701000)); //Shield.
					despawnNpcs(instance.getNpcs(243697)); //Invincible Barriere.
					//Generator successfully shut off. The door to the outside is now opening.
					sendMsgByRace(1405212, Race.PC_ALL, 0);
					//Stellin Special Forces have appeared on Benirunerk's orders.
					sendMsgByRace(1405213, Race.PC_ALL, 5000);
				}
			break;
			case 858542: //Shulack_Rider_Poly.
				if (player.isTransformed()) {
					//You cannot use this skill while transformed.
				    sendMsgByRace(1300149, Race.PC_ALL, 0);
				    //Transformation Mode.
					sendMsgByRace(1401212, Race.PC_ALL, 3000);
				} else {
					despawnNpc(npc);
				    shulackRiderPoly(player);
					SkillEngine.getInstance().applyEffectDirectly(20405, player, player, 300000 * 1);
				}
			break;
			case 658661: //IDF8_House_Fobj_Buff_Def.
			    effectController.removeEffect(20345);
				effectController.removeEffect(20346);
				SkillEngine.getInstance().applyEffectDirectly(20344, player, player, 3600000 * 1);
			break;
			case 658662: //IDF8_House_Fobj_Buff_PATK.
			    effectController.removeEffect(20344);
				effectController.removeEffect(20346);
				SkillEngine.getInstance().applyEffectDirectly(20345, player, player, 3600000 * 1);
			break;
			case 658663: //IDF8_House_Fobj_Buff_MATK.
			    effectController.removeEffect(20344);
				effectController.removeEffect(20345);
				SkillEngine.getInstance().applyEffectDirectly(20346, player, player, 3600000 * 1);
			break;
		}
	}
	
	public static final void shulackRiderPoly(final Player player) {
		player.getSkillList().addSkill(player, 19512, 1);
		player.getSkillList().addSkill(player, 19513, 1);
		player.getSkillList().addSkill(player, 19573, 1);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20405);
		///////////////////////////////////////////
		SkillLearnService.removeSkill(player, 19512);
		SkillLearnService.removeSkill(player, 19513);
		SkillLearnService.removeSkill(player, 19573);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
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