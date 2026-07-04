package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawn;
import com.aionemu.gameserver.model.templates.spawns.conquestspawns.ConquestSpawn;
import com.aionemu.gameserver.model.templates.spawns.dynamicriftspawns.DynamicRiftSpawn;
import com.aionemu.gameserver.model.templates.spawns.outpostspawns.OutpostSpawn;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawn;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawn;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SpawnMap")
public class SpawnMap
{
	@XmlElement(name = "spawn")
	private List<Spawn> spawns;
	
	@XmlElement(name = "siege_spawn")
	private List<SiegeSpawn> siegeSpawns;
	
	@XmlElement(name = "base_spawn")
	private List<BaseSpawn> baseSpawns;
	
	@XmlElement(name = "outpost_spawn")
	private List<OutpostSpawn> outpostSpawns;
	
	@XmlElement(name = "rift_spawn")
	private List<RiftSpawn> riftSpawns;

	@XmlElement(name = "conquest_spawn")
	private List<ConquestSpawn> conquestSpawns;

	@XmlElement(name = "dynamic_rift_spawn")
	private List<DynamicRiftSpawn> dynamicRiftSpawns;

	@XmlAttribute(name = "map_id")
	private int mapId;
	
	public SpawnMap() {
	}
	
	public SpawnMap(int mapId) {
		this.mapId = mapId;
	}
	
	public int getMapId() {
		return mapId;
	}
	
	public List<Spawn> getSpawns() {
		if (spawns == null) {
			spawns = new ArrayList<Spawn>();
		}
		return spawns;
	}
	
	public void addSpawns(Spawn spawns) {
		getSpawns().add(spawns);
	}
	
	public void removeSpawns(Spawn spawns) {
		getSpawns().remove(spawns);
	}
	
	public List<SiegeSpawn> getSiegeSpawns() {
		if (siegeSpawns == null) {
			siegeSpawns = new ArrayList<SiegeSpawn>();
		}
		return siegeSpawns;
	}
	
	public List<BaseSpawn> getBaseSpawns() {
		if (baseSpawns == null) {
			baseSpawns = new ArrayList<BaseSpawn>();
		}
		return baseSpawns;
	}
	
	public List<OutpostSpawn> getOutpostSpawns() {
		if (outpostSpawns == null) {
			outpostSpawns = new ArrayList<OutpostSpawn>();
		}
		return outpostSpawns;
	}
	
	public List<RiftSpawn> getRiftSpawns() {
		if (riftSpawns == null) {
			riftSpawns = new ArrayList<RiftSpawn>();
		}
		return riftSpawns;
	}

	public List<ConquestSpawn> getConquestSpawns() {
		if (conquestSpawns == null) {
			conquestSpawns = new ArrayList<ConquestSpawn>();
		}
		return conquestSpawns;
	}

	public List<DynamicRiftSpawn> getDynamicRiftSpawns() {
		if (dynamicRiftSpawns == null) {
			dynamicRiftSpawns = new ArrayList<DynamicRiftSpawn>();
		}
		return dynamicRiftSpawns;
	}

	public void addSiegeSpawns(SiegeSpawn spawns) {
		getSiegeSpawns().add(spawns);
	}
	
	public void addBaseSpawns(BaseSpawn spawns) {
		getBaseSpawns().add(spawns);
	}
	
	public void addOutpostSpawns(OutpostSpawn spawns) {
		getOutpostSpawns().add(spawns);
	}
	
	public void addRiftSpawns(RiftSpawn spawns) {
		getRiftSpawns().add(spawns);
	}

	public void addConquestSpawns(ConquestSpawn spawns) {
		getConquestSpawns().add(spawns);
	}

	public void addDynamicRiftSpawns(DynamicRiftSpawn spawns) {
		getDynamicRiftSpawns().add(spawns);
	}
}