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

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.VeilenthroneReward;
import com.aionemu.gameserver.model.instance.playerreward.VeilenthronePlayerReward;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/** Source: https://www.youtube.com/watch?v=9VtSLlk7Npc
/****/

@InstanceID(302510000)
public class The_Veilenthrone extends GeneralInstanceHandler
{
    private int rank;
	private Race spawnRace;
	private long startTime;
	private int shamanMunisha;
	private int ereshkigalCommander;
	private Future<?> timerPrepare;
	private Future<?> timerInstance;
	private Race theVeilenthroneRace;
	private VeilenthroneReward instanceReward;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	//Preparation Time.
	private int prepareTimerSeconds = 120000; //...2Min
	//Duration Instance Time.
	private int instanceTimerSeconds = 3600000; //...1H
	private List<Integer> movies = new ArrayList<Integer>();
	private final FastList<Future<?>> veilenthroneTask = FastList.newInstance();
	
	protected VeilenthronePlayerReward getPlayerReward(Integer object) {
		return (VeilenthronePlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new VeilenthronePlayerReward(player.getObjectId()));
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
			case 656411: //Guard Captain Baikunta.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 100901816, 1)); //Baikunta’s Sharp-edged Greatsword.
					}
				}
			break;
			case 656412: //Cleric Lord Merav.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 100101788, 1)); //Merav’s Tough Mace.
					}
				}
			break;
			case 656413: //Aether Master Sarashikal.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 100501744, 1)); //Sarashikal’s Bright Orb.
					}
				}
			break;
			case 656585: //Ereshkigal Drakan Jailor.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001007, 1)); //Freezing Prison Key.
			break;
			case 656586: //Ereshkigal Drakan Jailor.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185001008, 1)); //Frostchain Prison Key.
			break;
			case 837463: //Brilliant Veilenthrone Treasure Case.
			case 837464: //Veilenthrone Treasure Case.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188070895, 1)); //Ereshkigal Bronze Cubicle Bundle.
					}
				}
			break;
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
		}
	}
	
	@Override
	public void onDie(Npc npc) {
		int score = 0;
		int npcId = npc.getNpcId();
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 656418:
			case 656419:
			    despawnNpc(npc);
			break;
			case 657268:
			case 657269:
			case 657270:
			    despawnNpc(npc);
				ereshkigalCommander++;
				if (ereshkigalCommander == 30) {
					dragonbaneCannon();
					dragonbaneLifebinder();
                }
			break;
			case 656585: //Ereshkigal Drakan Jailor.
			    //Jotun in the area are requesting help.
				sendMsgByRace(1404782, Race.PC_ALL, 0);
			break;
			case 656409: //Dragon Lord Ereshkigal.
			    score += 3616;
				despawnNpc(npc);
				killNpc(getNpcs(656418));
				killNpc(getNpcs(656419));
				killNpc(getNpcs(656420));
				killNpc(getNpcs(656423));
				killNpc(getNpcs(657268));
				killNpc(getNpcs(657269));
				killNpc(getNpcs(657270));
				spawn(656572, 739.0000f, 499.0000f, 710.0000f, (byte) 0); //Wounded Ereshkigal.
			break;
			case 656410: //Commander Zav, The Brute.
				score += 201;
				despawnNpc(npc);
				veilenthroneBattle();
				//The 37th Assault Team Leader Zav is dead. The Aetheric Field has weakened.
				sendMsgByRace(1404798, Race.PC_ALL, 0);
			break;
			case 656411: //Guard Captain Baikunta.
			    score += 201;
				veilenthroneBattle();
				killNpc(getNpcs(656482)); //Wall.
				//The 37th Guard Captain Baikunta is dead. The Aetheric Field has weakened.
				sendMsgByRace(1404796, Race.PC_ALL, 0);
			break;
			case 656412: //Cleric Lord Merav.
			    score += 201;
				veilenthroneBattle();
				//The 37th Medic Captain Merav is dead. The Aetheric Field has weakened.
				sendMsgByRace(1404797, Race.PC_ALL, 0);
			break;
			case 656413: //Aether Master Sarashikal.
			    score += 201;
				veilenthroneBattle();
				//The 37th Sorcery Captain Sarashikal is dead. The Aetheric Field has weakened.
				sendMsgByRace(1404795, Race.PC_ALL, 0);
			break;
			case 656423: //Ereshkigal Shaman Munisha.
                Npc ereshkigal = instance.getNpc(656409); //Ereshkigal.
				shamanMunisha++;
				if (ereshkigal != null) {
					if (shamanMunisha == 2) {
						//The magic ward protecting Ereshkigal has been removed.
						sendMsgByRace(1404900, Race.PC_ALL, 0);
						//Ereshkigal’s magic ward has been destroyed.
						sendMsgByRace(1404901, Race.PC_ALL, 4000);
                        ereshkigal.getEffectController().removeEffect(18516);
                    }
                }
            break;
			case 656483: //Wall.
			    //There is a powerful object in this area.
				sendMsgByRace(1404834, Race.PC_ALL, 2000);
			break;
			case 656426:
			case 656428:
			case 656430:
			case 656432:
			case 656434:
			case 656436:
			case 656438:
			case 656444:
			case 656446:
			case 656448:
			case 656450:
			case 656452:
			case 656456:
			case 656458:
			case 656462:
			case 656496:
			    score += 63;
				despawnNpc(npc);
			break;
			case 656572: //Wounded Ereshkigal.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    stopInstance1(player);
						    }
					    });
					}
				}, 5000);
				despawnNpc(npc);
				if (checkRank(instanceReward.getPoints()) == 1) {
                    spawn(837464, 771.0000f, 499.0000f, 709.0000f, (byte) 60); //Brilliant Veilenthrone Treasure Case.
                } else if (checkRank(instanceReward.getPoints()) == 2) {
                    spawn(837463, 771.0000f, 499.0000f, 709.0000f, (byte) 60); //Veilenthrone Treasure Case.
                }
				spawn(837544, 805.0000f, 499.0000f, 709.0000f, (byte) 60); //The Veilenthrone Exit.
			break;
		} if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addNpcKill();
			instanceReward.addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		} switch (npcId) {
			case 656472: //IDF7_Ere_Minus_Guard_Li_Fi_C.
			case 656473: //IDF7_Ere_Minus_Guard_Li_As_H.
			case 656474: //IDF7_Ere_Minus_Guard_Da_Fi_C.
			case 656475: //IDF7_Ere_Minus_Guard_Da_As_H.
			case 656476: //IDF7_Ere_Minus_Npc_Li_Wi_F.
			case 656477: //IDF7_Ere_Minus_Npc_Da_Wi_F.
			case 656478: //IDF7_Ere_Minus_Base_Guard_Li_Fi_A.
			case 656479: //IDF7_Ere_Minus_Base_Guard_Da_Fi_A.
				despawnNpc(npc);
				instanceReward.addPoints(-124);
			break;
		}
	}
	
	private boolean veilenthroneBattle() {
		Npc boss1 = getNpc(656410);
		Npc boss2 = getNpc(656411);
		Npc boss3 = getNpc(656412);
		Npc boss4 = getNpc(656413);
		if (isDead(boss1) && isDead(boss2) &&
		    isDead(boss3) && isDead(boss4)) {
			killNpc(getNpcs(837448));
			//The leaders of the 37th legion are dead, and the Balaurs are rushing in to tighten their defenses. Get ready for a fight.
			sendMsgByRace(1404799, Race.PC_ALL, 2000);
			spawn(837448, 501.0000f, 503.0000f, 153.0000f, (byte) 0, 101); //The Veilenthrone Eye's Entrance.
			return true;
		}
		return false;
	}
	
	private void dragonbaneCannon() {
		final int Rusted_Dragonbane_Cannon1 = spawnRace == Race.ASMODIANS ? 656654 : 656646;
		spawn(Rusted_Dragonbane_Cannon1, 769.0000f, 523.0000f, 709.0000f, (byte) 61);
		final int Rusted_Dragonbane_Cannon2 = spawnRace == Race.ASMODIANS ? 656651 : 656643;
		spawn(Rusted_Dragonbane_Cannon2, 776.0000f, 514.0000f, 709.0000f, (byte) 60);
		final int Rusted_Dragonbane_Cannon3 = spawnRace == Race.ASMODIANS ? 656650 : 656642;
		spawn(Rusted_Dragonbane_Cannon3, 784.0000f, 506.0000f, 709.0000f, (byte) 60);
		final int Rusted_Dragonbane_Cannon4 = spawnRace == Race.ASMODIANS ? 656653 : 656645;
		spawn(Rusted_Dragonbane_Cannon4, 784.0000f, 492.0000f, 709.0000f, (byte) 61);
		final int Rusted_Dragonbane_Cannon5 = spawnRace == Race.ASMODIANS ? 656652 : 656644;
		spawn(Rusted_Dragonbane_Cannon5, 776.0000f, 484.0000f, 709.0000f, (byte) 61);
		final int Rusted_Dragonbane_Cannon6 = spawnRace == Race.ASMODIANS ? 656655 : 656647;
		spawn(Rusted_Dragonbane_Cannon6, 769.0000f, 474.0000f, 709.0000f, (byte) 60);
	}
	private void dragonbaneLifebinder() {
		final int Dragonbane_Lifebinder_Captain1 = spawnRace == Race.ASMODIANS ? 656648 : 656640;
		spawn(Dragonbane_Lifebinder_Captain1, 759.0000f, 509.0000f, 709.0000f, (byte) 62);
		spawn(Dragonbane_Lifebinder_Captain1, 759.0000f, 494.0000f, 709.0000f, (byte) 60);
		spawn(Dragonbane_Lifebinder_Captain1, 759.0000f, 489.0000f, 709.0000f, (byte) 60);
		final int Dragonbane_Lifebinder_Captain2 = spawnRace == Race.ASMODIANS ? 656649 : 656641;
		spawn(Dragonbane_Lifebinder_Captain2, 759.0000f, 503.0000f, 709.0000f, (byte) 61);
	}
	
	@Override
	public void onGather(Player player, Gatherable gatherable) {
		switch (gatherable.getObjectTemplate().getTemplateId()) {
		    case 409260: //Big Tree.
			    //There is a powerful object in this area.
				sendMsgByRace(1404834, Race.PC_ALL, 2000);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 656583: //IDF7_Ere_Minus_Guard_Li_Fi_L.
			case 656584: //IDF7_Ere_Minus_Guard_Da_Fi_L.
				despawnNpc(npc);
				if (instanceReward.getInstanceScoreType().isStartProgress()) {
					instanceReward.addPoints(124);
					sendPacket(npc.getObjectTemplate().getNameId(), 124);
				}
			break;
			case 656408: //Veilenthrone Annihilator.
			    if (player.isTransformed()) {
					//You cannot use this skill while transformed.
					sendMsgByRace(1300149, Race.PC_ALL, 0);
					//Transformation Mode.
					sendMsgByRace(1401212, Race.PC_ALL, 3000);
				} if (player.getInventory().decreaseByItemId(186040001, 1)) { //Lym Crystal.
					despawnNpc(npc);
					theVeilenThroneWeapon(player);
					//Balaurs detected the power of the Veilenthrone Weapon and are rushing in.
					sendMsgByRace(1404793, Race.PC_ALL, 10000);
					sendVariablePacket(player, "UI_Gauge_03", 1 + 1); //Board Weapon.
					SkillEngine.getInstance().applyEffectDirectly(17585, player, player, 1800000 * 1);
				} else {
					//You need a Lym Crystal to board the The Veilenthrone Weapon.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404781));
				}
			break;
			case 656912: //Arbo's Soul.
			    if (player.getInventory().decreaseByItemId(186040002, 1)) { //Veilenthrone Revival Stone.
				    despawnNpc(npc);
					sendVariablePacket(player, "UI_Gauge_02", 2 + 1); //Rescue Jotun.
					spawn(656425, 779.0000f, 499.0000f, 709.0000f, (byte) 60); //Arbo's Soul.
				} else {
					//You need a Veilenthrone Revival Stone to summon Arbo’s soul.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404783));
				}
			break;
			case 837445: //Dragon Killer Cannonball Box.
			    despawnNpc(npc);
			    ItemService.addItem(player, 186040000, 5); //Dragon Killer Cannonball.
			break;
			case 837446: //Greater Lym Gemstone.
			    despawnNpc(npc);
			    ItemService.addItem(player, 186040001, 1); //Lym Crystal.
			break;
			case 837447: //Greater Lym Gemstone.
			    despawnNpc(npc);
			break;
			case 837448: //The Veilenthrone Eye's Entrance.
			    theVeilenThroneAtkBuff();
				theVeilenThroneDefBuff();
			    theVeilenthroneEye(player, 801.0000f, 499.0000f, 709.0000f, (byte) 60);
			break;
		}
	}
	
	private void theVeilenThroneAtkBuff() {
		for (Player p: instance.getPlayersInside()) {
			SkillTemplate st =  DataManager.SKILL_DATA.getSkillTemplate(17608);
			Effect e = new Effect(p, p, st, 1, st.getEffectsDuration(9));
			e.initialize();
			e.applyEffect();
		}
	}
	private void theVeilenThroneDefBuff() {
		for (Player p: instance.getPlayersInside()) {
			SkillTemplate st =  DataManager.SKILL_DATA.getSkillTemplate(17609);
			Effect e = new Effect(p, p, st, 1, st.getEffectsDuration(9));
			e.initialize();
			e.applyEffect();
		}
	}
	
	public static final void theVeilenThroneWeapon(final Player player) {
		player.getSkillList().addSkill(player, 17586, 1);
		player.getSkillList().addSkill(player, 17587, 1);
		player.getSkillList().addSkill(player, 17588, 1);
		player.getSkillList().addSkill(player, 17589, 1);
		player.getSkillList().addSkill(player, 17590, 1);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400255, player.getName()));
		if (player.isInGroup2()) {
            PlayerGroupService.removePlayer(player);
        }
	}
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
		storage.decreaseByItemId(100101788, storage.getItemCountByItemId(100101788)); //Merav’s Tough Mace.
		storage.decreaseByItemId(100501744, storage.getItemCountByItemId(100501744)); //Sarashikal’s Bright Orb.
		storage.decreaseByItemId(100901816, storage.getItemCountByItemId(100901816)); //Baikunta’s Sharp-edged Greatsword.
    }
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(17585);
		effectController.removeEffect(17608);
		effectController.removeEffect(17609);
		SkillLearnService.removeSkill(player, 17586);
		SkillLearnService.removeSkill(player, 17587);
		SkillLearnService.removeSkill(player, 17588);
		SkillLearnService.removeSkill(player, 17589);
		SkillLearnService.removeSkill(player, 17590);
	}
	
	protected void theVeilenthroneEye(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
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
	
	private void sendVariablePacket(Player player, final String variable, final int value) {
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_CONDITION_VARIABLE(player, variable, value));
				}
			}
		});
	}
	
	private int checkRank(int totalPoints) {
		if (totalPoints >= 10280) { //Rank S.
			rank = 1;
		} else if (totalPoints >= 7760) { //Rank A.
			rank = 2;
		} else {
			rank = 6;
		}
		return rank;
	}
	
	protected void startInstanceTask() {
		veilenthroneTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				despawnNpcs(instance.getNpcs(656409));
				spawn(837544, 805.0000f, 499.0000f, 709.0000f, (byte) 60); //The Veilenthrone Exit.
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						stopInstance2(player);
				    }
			    });
            }
        }, 3600000)); //...1H
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		VeilenthronePlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		} if (theVeilenthroneRace == null) {
			theVeilenthroneRace = player.getRace();
			spawnTheVeilenthroneRace();
		} if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateQuest62810(player);
		} else {
			ClassChangeService.onUpdateQuest72810(player);
		}
		startPrepareTimer();
		//Rescuing the Elite Squad.
		sendVariablePacket(player, "UI_Gauge_01", 4 + 1);
		//Rescue Jotun.
		sendVariablePacket(player, "UI_Gauge_02", 0 + 1);
		//Board Weapon.
		sendVariablePacket(player, "UI_Gauge_03", 0 + 1);
	}
	
	private void spawnTheVeilenthroneRace() {
		final int Hans_Putio = theVeilenthroneRace == Race.ASMODIANS ? 820177 : 820176;
		Npc NpcRace = (Npc) spawn(Hans_Putio, 866.0000f, 566.0000f, 151.0000f, (byte) 116);
		//Please... help!
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502160, NpcRace.getObjectId(), 0, 3000);
		//Ah, more sacrifices for Ereshkigal....
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502161, NpcRace.getObjectId(), 0, 9000);
		//I’m sorry, I just... I couldn’t help but run....
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502162, NpcRace.getObjectId(), 0, 15000);
		//Daevas all over the Veilenthrone are suffering. Please save them!
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502202, NpcRace.getObjectId(), 0, 21000);
		//Go fight Ereshkigal. I'll be right behind you with the Dragon Killer Cannon to support you.
		NpcShoutsService.getInstance().sendMsg(NpcRace, 1502205, NpcRace.getObjectId(), 0, 27000);
		//
		final int IDF7_Ere_Help_NPC = theVeilenthroneRace == Race.ASMODIANS ? 837480 : 837479;
		spawn(IDF7_Ere_Help_NPC, 711.0000f, 459.0000f, 151.0000f, (byte) 0);
		//
		final int IDF7_Ere_Cannon_Box_NPC_Wi_01 = theVeilenthroneRace == Race.ASMODIANS ? 837490 : 837489;
		spawn(IDF7_Ere_Cannon_Box_NPC_Wi_01, 367.0000f, 531.0000f, 161.0000f, (byte) 111);
		//
		final int IDF7_Ere_CannonBox_Guard_As_01 = theVeilenthroneRace == Race.ASMODIANS ? 837488 : 837487;
		spawn(IDF7_Ere_CannonBox_Guard_As_01, 436.0000f, 581.0000f, 146.0000f, (byte) 0);
		spawn(IDF7_Ere_CannonBox_Guard_As_01, 490.0000f, 314.0000f, 158.0000f, (byte) 1);
		//
		final int IDF7_Ere_Minus_Guard_Fi_C = theVeilenthroneRace == Race.ASMODIANS ? 656474 : 656472;
		spawn(IDF7_Ere_Minus_Guard_Fi_C, 457.0000f, 312.0000f, 158.0000f, (byte) 55);
		//
		final int IDF7_Ere_Minus_Guard_As_H = theVeilenthroneRace == Race.ASMODIANS ? 656475 : 656473;
		spawn(IDF7_Ere_Minus_Guard_As_H, 576.0000f, 678.0000f, 157.0000f, (byte) 119);
		//
		final int IDF7_Ere_Minus_Npc_Wi_F = theVeilenthroneRace == Race.ASMODIANS ? 656477 : 656476;
		spawn(IDF7_Ere_Minus_Npc_Wi_F, 371.0000f, 538.0000f, 162.0000f, (byte) 68);
		//
		final int IDF7_Ere_Minus_Base_Guard_Fi_A = theVeilenthroneRace == Race.ASMODIANS ? 656479 : 656478;
		spawn(IDF7_Ere_Minus_Base_Guard_Fi_A, 695.0000f, 500.0000f, 154.0000f, (byte) 119);
		//
		final int IDF7_Ere_Minus_Guard_As_ALL = theVeilenthroneRace == Race.ASMODIANS ? 656494 : 656491;
		spawn(IDF7_Ere_Minus_Guard_As_ALL, 379.0000f, 364.0000f, 153.0000f, (byte) 30);
		//
		final int IDF7_Ere_Minus_Guard_Fi_01 = theVeilenthroneRace == Race.ASMODIANS ? 656584 : 656583;
		spawn(IDF7_Ere_Minus_Guard_Fi_01, 292.0000f, 736.0000f, 137.0000f, (byte) 91);
		//
		final int IDF7_Ere_Die_Guard_Fi_01 = theVeilenthroneRace == Race.ASMODIANS ? 656558 : 656554;
		spawn(IDF7_Ere_Die_Guard_Fi_01, 732.0000f, 502.0000f, 150.0000f, (byte) 0);
        spawn(IDF7_Ere_Die_Guard_Fi_01, 429.0000f, 403.0000f, 146.0000f, (byte) 0);
        spawn(IDF7_Ere_Die_Guard_Fi_01, 438.0000f, 406.0000f, 146.0000f, (byte) 17);
        spawn(IDF7_Ere_Die_Guard_Fi_01, 546.0000f, 648.0000f, 157.0000f, (byte) 60);
        spawn(IDF7_Ere_Die_Guard_Fi_01, 429.0000f, 390.0000f, 146.0000f, (byte) 9);
        spawn(IDF7_Ere_Die_Guard_Fi_01, 446.0000f, 392.0000f, 146.0000f, (byte) 50);
		//
		final int IDF7_Ere_Die_Guard_Fi_02 = theVeilenthroneRace == Race.ASMODIANS ? 656559 : 656555;
		spawn(IDF7_Ere_Die_Guard_Fi_02, 542.0000f, 645.0000f, 157.0000f, (byte) 16);
		//
		final int IDF7_Ere_Die_Guard_Fi_03 = theVeilenthroneRace == Race.ASMODIANS ? 656560 : 656556;
		spawn(IDF7_Ere_Die_Guard_Fi_03, 287.49158f, 719.36127f, 137.82002f, (byte) 98);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 566.16860f, 381.40817f, 158.00000f, (byte) 105);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 560.57190f, 638.35920f, 157.42761f, (byte) 21);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 439.09906f, 386.40625f, 146.91556f, (byte) 21);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 439.09906f, 398.16846f, 146.90462f, (byte) 51);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 711.51400f, 493.43198f, 150.27525f, (byte) 33);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 546.03204f, 644.13434f, 157.81796f, (byte) 52);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 288.61176f, 724.48320f, 137.82002f, (byte) 88);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 446.49695f, 403.63180f, 146.77930f, (byte) 41);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 591.23175f, 678.59830f, 157.76643f, (byte) 79);
        spawn(IDF7_Ere_Die_Guard_Fi_03, 738.09460f, 502.85220f, 150.31883f, (byte) 77);
		//
		final int IDF7_Ere_Die_Guard_Fi_04 = theVeilenthroneRace == Race.ASMODIANS ? 656561 : 656557;
		spawn(IDF7_Ere_Die_Guard_Fi_04, 576.0000f, 376.0000f, 158.0000f, (byte) 68);
        spawn(IDF7_Ere_Die_Guard_Fi_04, 565.0000f, 375.0000f, 158.0000f, (byte) 6);
        spawn(IDF7_Ere_Die_Guard_Fi_04, 591.0000f, 675.0000f, 157.0000f, (byte) 73);
		//
		final int IDF7_Ere_Die_NPC_Fi_01 = theVeilenthroneRace == Race.ASMODIANS ? 656563 : 656562;
		spawn(IDF7_Ere_Die_NPC_Fi_01, 369.0000f, 536.0000f, 171.0000f, (byte) 0);
        spawn(IDF7_Ere_Die_NPC_Fi_01, 370.0000f, 535.0000f, 161.0000f, (byte) 19);
        spawn(IDF7_Ere_Die_NPC_Fi_01, 395.0000f, 529.0000f, 162.0000f, (byte) 66);
		//
		final int IDF7_Ere_Die_NPC_Fi_02 = theVeilenthroneRace == Race.ASMODIANS ? 656565 : 656564;
		spawn(IDF7_Ere_Die_NPC_Fi_02, 393.0000f, 524.0000f, 162.0000f, (byte) 67);
        spawn(IDF7_Ere_Die_NPC_Fi_02, 367.0000f, 524.0000f, 161.0000f, (byte) 112);
		//
		final int IDF7_Ere_Cannon_Guard_Fi_01 = theVeilenthroneRace == Race.ASMODIANS ? 656633 : 656632;
		spawn(IDF7_Ere_Cannon_Guard_Fi_01, 538.0000f, 638.0000f, 157.0000f, (byte) 45);
		//
		final int IDF7_Ere_Cannon_Guard_As_01 = theVeilenthroneRace == Race.ASMODIANS ? 656635 : 656634;
		spawn(IDF7_Ere_Cannon_Guard_As_01, 566.0000f, 553.0000f, 157.0000f, (byte) 0);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new VeilenthroneReward(mapId, instanceId);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
		instanceReward.addPoints(3340);
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
				sendMsgByRace(1404595, Race.PC_ALL, 60000);
				spawn(656832, 776.0000f, 550.0000f, 709.0000f, (byte) 91); //Great Smuggler Shukirukin.
			break;
			case 2:
			    //The Smuggler Shukirukin will soon appear.
				sendMsgByRace(1404596, Race.PC_ALL, 60000);
				spawn(656833, 776.0000f, 550.0000f, 709.0000f, (byte) 91); //Smuggler Shukirukin.
			break;
			case 3:
			break;
		}
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
		startInstanceTask();
		doors.get(89).setOpen(true);
		doors.get(190).setOpen(true);
		//The member recruitment window has passed. You cannot recruit any more members.
		sendMsgByRace(1401181, Race.PC_ALL, 5000);
		//You must first defeat the Four Captains, the leaders of the 37th Legion to fight Ereshkigal.
		sendMsgByRace(1404785, Race.PC_ALL, 10000);
		//You must also rescue the Daevas of the Advance Unit fighting there.
		sendMsgByRace(1404786, Race.PC_ALL, 15000);
		//Save them quickly before they are killed by the Balaur.
		sendMsgByRace(1404787, Race.PC_ALL, 20000);
		//You can board The Veilenthrone Weapon by using a Lym Crystal extracted from the Lym Gemstone.
		sendMsgByRace(1404833, Race.PC_ALL, 25000);
		instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
		sendPacket(0, 0);
	}
	
	protected void stopInstance1(Player player) {
        stopInstanceTask();
        instanceReward.setRank(6);
		instanceReward.setRank(checkRank(instanceReward.getPoints()));
		instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		doReward(player);
		sendPacket(0, 0);
	}
	
	protected void stopInstance2(Player player) {
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
		VeilenthronePlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int veilenthroneRank = instanceReward.getRank();
			switch (veilenthroneRank) {
				case 1: //Rank S
				break;
				case 2: //Rank A
				break;
			}
		}
	}
	
	protected void despawnNpc(Npc npc) {
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
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = veilenthroneTask.head(), end = veilenthroneTask.tail(); (n = n.getNext()) != end; ) {
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
        veilenthroneTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        veilenthroneTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	public void onInstanceDestroy() {
		if (timerInstance != null) {
			timerInstance.cancel(false);
		} if (timerPrepare != null) {
			timerPrepare.cancel(false);
		}
		doors.clear();
		stopInstanceTask();
		instanceReward.clear();
		isInstanceDestroyed = true;
	}
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
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