package com.aionemu.gameserver.model.templates.siegelocation;

import com.aionemu.gameserver.model.siege.SiegeType;

import javax.xml.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "siegelocation")
public class SiegeLocationTemplate
{
	@XmlAttribute(name = "id")
	protected int id;
	
	@XmlAttribute(name = "type")
	protected SiegeType type;
	
	@XmlAttribute(name = "world")
	protected int world;
	
	@XmlElement(name = "artifact_activation")
	protected ArtifactActivation artifactActivation;
	
	@XmlElement(name = "door_repair")
	protected DoorRepair doorRepair;
	
	@XmlElement(name = "siege_reward")
	protected List<SiegeReward> siegeRewards;
	
	@XmlElement(name = "legion_reward")
	protected List<SiegeLegionReward> siegeLegionRewards;
	
	@XmlAttribute(name = "name_id")
	protected int nameId = 0;
	
	@XmlAttribute(name = "buff_id")
	protected int buffId = 0;
	
	@XmlAttribute(name = "buff_idA")
	protected int buffIdA = 0;
	
	@XmlAttribute(name = "buff_idE")
	protected int buffIdE = 0;
	
	@XmlAttribute(name = "owner_ap")
	protected int ownerAp = 0;
	
	@XmlAttribute(name = "owner_gp")
	protected int ownerGp = 0;
	
	@XmlAttribute(name = "owner_kinah")
	protected long ownerKinah = 0;
	
	@XmlAttribute(name = "repeat_count")
	protected int repeatCount = 1;
	
	@XmlAttribute(name = "repeat_interval")
	protected int repeatInterval = 1;
	
	@XmlAttribute(name = "siege_duration")
	protected int siegeDuration;
	
	@XmlAttribute(name = "influence")
	protected int influenceValue;
	
	@XmlAttribute(name = "occupy_count")
	protected int occupyCount = 0;
	
	@XmlList
	@XmlAttribute(name = "fortress_dependency")
	protected List<Integer> fortressDependency;
	
	@XmlAttribute(name = "outpost_id")
	protected int outpostId;
	
	public int getId() {
		return this.id;
	}
	
	public SiegeType getType() {
		return this.type;
	}
	
	public int getWorldId() {
		return this.world;
	}
	
	public ArtifactActivation getActivation() {
		return this.artifactActivation;
	}
	
	public DoorRepair getRepair() {
		return this.doorRepair;
	}
	
	public List<SiegeReward> getSiegeRewards() {
		return this.siegeRewards;
	}
	
	public List<SiegeLegionReward> getSiegeLegionRewards() {
		return this.siegeLegionRewards;
	}
	
	public int getNameId() {
		return nameId;
	}
	
	public int getBuffId() {
		return buffId;
	}
	
	public int getBuffIdA() {
		return buffIdA;
	}
	
	public int getBuffIdE() {
		return buffIdE;
	}
	
	public int getOwnerAp() {
		return ownerAp;
	}
	
	public int getOwnerGp() {
		return ownerGp;
	}
	
	public long getOwnerKinah() {
		return ownerKinah;
	}
	
	public int getOccupyCount() {
		return occupyCount;
	}
	
	public int getRepeatCount() {
		return repeatCount;
	}
	
	public int getRepeatInterval() {
		return repeatInterval;
	}
	
	public List<Integer> getFortressDependency() {
		if (fortressDependency == null) {
			return Collections.emptyList();
		}
		return fortressDependency;
	}
	
	public int getSiegeDuration() {
		return siegeDuration;
	}
	
	public int getInfluenceValue() {
		return influenceValue;
	}
	
	public int getOutpostId() {
		return outpostId;
	}
}