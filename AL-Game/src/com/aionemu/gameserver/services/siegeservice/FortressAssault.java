package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.ArrayList;
import java.util.List;

public class FortressAssault extends Assault<FortressSiege>
{
	private final boolean isBalaurea;
	private boolean spawned = false;
	private List<float[]> spawnLocations;
	
	public FortressAssault(FortressSiege siege) {
		super(siege);
		this.isBalaurea = worldId != 400070000;
	}
	
	@Override
	protected void scheduleAssault(int delay) {
		dredgionTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawnAttackers();
					}
				}, Rnd.get(60, 120) * 1000);
			}
		}, delay * 1000);
	}
	
	@Override
	protected void onAssaultFinish(boolean captured) {
		if (!spawned) {
			return;
		} if (!captured) {
			return;
		} else {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					//The Balaur have killed the Guardian General.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FIELDABYSS_DRAGON_BOSS_KILLED);
				}
			});
		}
		spawnLocations.clear();
	}
	
	private void spawnAttackers() {
		if (spawned) {
			return;
		}
		spawned = true;
		float x = boss.getX();
		float y = boss.getY();
		float z = boss.getZ();
		byte heading = boss.getSpawn().getHeading();
		int radius1 = isBalaurea ? 5 : Rnd.get(1, 8);
		int radius2 = isBalaurea ? 9 : Rnd.get(8, 16);
		int amount = isBalaurea ? Rnd.get(15, 30) : Rnd.get(15, 30);
		int templateId;
		SiegeSpawnTemplate spawn;
		float minAngle = MathUtil.convertHeadingToDegree(heading) - 90;
		if (minAngle < 0) {
			minAngle += 360;
		}
		double minRadian = Math.toRadians(minAngle);
		float interval = (float) (Math.PI / (amount / 2));
		float x1;
		float y1;
		List<Integer> idList = getSpawnIds();
		int commanderCount = isBalaurea ? 0 : Rnd.get(2);
		spawnRegularBalaurs();
		for (int i = 0; amount > i; i++) {
			if (i < (amount / 2)) {
				x1 = (float) (Math.cos(minRadian + interval * i) * radius1);
				y1 = (float) (Math.sin(minRadian + interval * i) * radius1);
			} else {
				x1 = (float) (Math.cos(minRadian + interval * (i - amount / 2)) * radius2);
				y1 = (float) (Math.sin(minRadian + interval * (i - amount / 2)) * radius2);
			}
			templateId = (i <= commanderCount) ? idList.get(0) : idList.get(Rnd.get(1, idList.size() - 1));
			Npc attaker;
			if ((i > Math.round(amount / 3)) && !spawnLocations.isEmpty()) {
				float[] coords = spawnLocations.get(Rnd.get(spawnLocations.size()));
				spawn = SpawnEngine.addNewSiegeSpawn(worldId, templateId, locationId, SiegeRace.BALAUR, SiegeModType.ASSAULT, coords[0], coords[1], coords[2], heading);
				attaker = (Npc) SpawnEngine.spawnObject(spawn, 1);
				attaker.getSpawn().setX(x + x1);
				attaker.getSpawn().setY(y + y1);
				attaker.getSpawn().setZ(z);
			} else {
				spawn = SpawnEngine.addNewSiegeSpawn(worldId, templateId, locationId, SiegeRace.BALAUR, SiegeModType.ASSAULT, x + x1, y + y1, z, heading);
				SpawnEngine.spawnObject(spawn, 1);
			}
		}
		idList.clear();
	}
	
	private void spawnRegularBalaurs() {
		spawnLocations = new ArrayList<float[]>();
		List<SpawnGroup2> siegeSpawns = DataManager.SPAWNS_DATA2.getSiegeSpawnsByLocId(locationId);
		for (SpawnGroup2 spawnGroup : siegeSpawns) {
			for (SpawnTemplate spawnTemplate : spawnGroup.getSpawnTemplates()) {
				SiegeSpawnTemplate temp = (SiegeSpawnTemplate) spawnTemplate;
				AbyssNpcType type = DataManager.NPC_DATA.getNpcTemplate(temp.getNpcId()).getAbyssNpcType();
				if (temp.getSiegeRace() != SiegeRace.BALAUR || !temp.isPeace() || type.equals(AbyssNpcType.ARTIFACT) || type.equals(AbyssNpcType.TELEPORTER)) {
					continue;
				}
				float[] loc = { spawnTemplate.getX() + 2, spawnTemplate.getY() + 2, spawnTemplate.getZ() };
				SiegeSpawnTemplate spawn = SpawnEngine.addNewSiegeSpawn(spawnTemplate.getWorldId(), spawnTemplate.getNpcId(), locationId, SiegeRace.BALAUR, SiegeModType.ASSAULT, loc[0], loc[1], loc[2], spawnTemplate.getHeading());
				VisibleObject attaker = SpawnEngine.spawnObject(spawn, 1);
				if (MathUtil.isIn3dRange(attaker, boss, isBalaurea ? 100 : 70)) {
					spawnLocations.add(loc);
				}
			}
		}
	}
	
	private List<Integer> getSpawnIds() {
		List<Integer> Spawns = new ArrayList<Integer>();
		switch (locationId) {
			case 1011: //Divine Fortress.
				Spawns.add(884940);
				Spawns.add(884945);
				Spawns.add(884950);
				Spawns.add(884955);
				Spawns.add(884986);
				Spawns.add(884991);
				Spawns.add(884996);
				Spawns.add(885001);
				Spawns.add(885038);
				Spawns.add(885039);
				Spawns.add(885040);
				Spawns.add(885041);
				Spawns.add(885046);
				Spawns.add(885051);
				Spawns.add(885056);
				return Spawns;
			case 2011: //Temple Of Scales.
				Spawns.add(257059);
				Spawns.add(257062);
				Spawns.add(257065);
				Spawns.add(257068);
				Spawns.add(257071);
				return Spawns;
			case 2021: //Altar Of Avarice.
				Spawns.add(257359);
				Spawns.add(257362);
				Spawns.add(257365);
				Spawns.add(257368);
				Spawns.add(257371);
				return Spawns;
			case 3011: //Vorgaltem Citadel.
				Spawns.add(257659);
				Spawns.add(257662);
				Spawns.add(257665);
				Spawns.add(257668);
				Spawns.add(257671);
				return Spawns;
			case 3021: //Crimsom Temple.
				Spawns.add(257959);
				Spawns.add(257962);
				Spawns.add(257965);
				Spawns.add(257968);
				Spawns.add(257971);
				return Spawns;
			case 6011: //Silona Fortress.
				Spawns.add(272675);
				Spawns.add(272680);
				Spawns.add(272685);
				Spawns.add(272690);
				Spawns.add(272695);
				Spawns.add(272700);
				Spawns.add(272705);
				Spawns.add(272710);
				Spawns.add(272786);
				return Spawns;
			case 6021: //Pradeth Fortress.
				Spawns.add(661455);
				Spawns.add(661456);
				Spawns.add(661457);
				Spawns.add(661458);
				return Spawns;
			case 5021: //1st Apsu's Altar.
			    Spawns.add(886518);
				Spawns.add(886521);
				Spawns.add(886524);
				Spawns.add(886527);
				return Spawns;
			case 5022: //2nd Apsu's Altar.
			    Spawns.add(886557);
				Spawns.add(886560);
				Spawns.add(886563);
				Spawns.add(886566);
				return Spawns;
			case 5023: //3rd Apsu's Altar.
			    Spawns.add(886596);
				Spawns.add(886599);
				Spawns.add(886602);
				Spawns.add(886605);
				return Spawns;
			case 5024: //4th Apsu's Altar.
			    Spawns.add(886635);
				Spawns.add(886638);
				Spawns.add(886641);
				Spawns.add(886644);
				return Spawns;
			case 5025: //5th Apsu's Altar.
			    Spawns.add(886674);
				Spawns.add(886677);
				Spawns.add(886680);
				Spawns.add(886683);
				return Spawns;
			case 5026: //6th Apsu's Altar.
			    Spawns.add(886713);
				Spawns.add(886716);
				Spawns.add(886719);
				Spawns.add(886722);
				return Spawns;
			case 5027: //7th Apsu's Altar.
			    Spawns.add(886752);
				Spawns.add(886755);
				Spawns.add(886758);
				Spawns.add(886761);
				return Spawns;
			case 5028: //8th Apsu's Altar.
			    Spawns.add(886791);
				Spawns.add(886794);
				Spawns.add(886797);
				Spawns.add(886800);
				return Spawns;
			case 5029: //9th Apsu's Altar.
			    Spawns.add(886830);
				Spawns.add(886833);
				Spawns.add(886836);
				Spawns.add(886839);
				return Spawns;
			case 5030: //10th Apsu's Altar.
			    Spawns.add(886869);
				Spawns.add(886872);
				Spawns.add(886875);
				Spawns.add(886878);
				return Spawns;
			case 5031: //11th Apsu's Altar.
			    Spawns.add(886908);
				Spawns.add(886911);
				Spawns.add(886914);
				Spawns.add(886917);
				return Spawns;
			case 5032: //12th Apsu's Altar.
			    Spawns.add(887555);
				Spawns.add(888590);
				Spawns.add(887561);
				Spawns.add(887558);
				return Spawns;	
			default:
				return Spawns;
		}
	}
	
	private void rewardDefendingPlayers() {
	}
}