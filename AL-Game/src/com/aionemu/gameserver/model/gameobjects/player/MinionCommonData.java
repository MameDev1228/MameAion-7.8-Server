package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.minion.MinionDopingBag;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * @author Ranastic
 */

public class MinionCommonData extends VisibleObjectTemplate implements IExpirable
{
	private int minionId;
	private Timestamp birthday;
	private int minionObjId;
	private final int masterObjectId;
	private String minionGrade;
	private String name;
	private int minionLevel;
	private int growthPoints;
	private boolean locked;
	private boolean isLooting = false;
	private boolean isBuffing = false;
	
	MinionDopingBag dopingBag = null;
	
	private Timestamp despawnTime;
	
	Logger log = LoggerFactory.getLogger(MinionCommonData.class);
	
	public MinionCommonData(int minionId, int masterObjectId, String name, String minionGrade, int minionLevel, int growthPoints, boolean locked) {
		if (minionObjId == 0) {
			this.minionObjId = IDFactory.getInstance().nextId();
		}
		this.minionId = minionId;
		this.masterObjectId = masterObjectId;
		this.name = name;
		this.minionGrade = minionGrade;
		this.minionLevel = minionLevel;
		this.growthPoints = growthPoints;
		this.locked = locked;
		dopingBag = new MinionDopingBag();
	}
	
	public void setObjectId(int minionObjId) {
		this.minionObjId = minionObjId;
	}
	
	public int getObjectId() {
		return minionObjId;
	}
	
	public int getMasterObjectId() {
		return masterObjectId;
	}
	
	public final int getMinionId() {
		return minionId;
	}
	
	public String getMinionGrade() {
		return minionGrade;
	}
	
	public int getMinionLevel() {
		return minionLevel;
	}

	public int getGrowthPoints() {
		return growthPoints;
	}

	public int getBirthday() {
		if (birthday == null) {
			return 0;
		}
		return (int) (birthday.getTime() / 1000);
	}

	public boolean isLocked(){
		return locked;
	}
	
	public Timestamp getBirthdayTimestamp() {
		return birthday;
	}
	
	public void setBirthday(Timestamp birthday) {
		this.birthday = birthday;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setGrowthPoints(int growthPoints) {
		this.growthPoints = growthPoints;
	}

	public void setLocked(boolean locked){
		this.locked = locked;
	}

	@Override
	public int getExpireTime() {
		return 0;
	}

	@Override
	public void expireEnd(Player player) {
	}

	@Override
	public boolean canExpireNow() {
		return false;
	}

	@Override
	public void expireMessage(Player player, int time) {
	}

	@Override
	public int getTemplateId() {
		return minionId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getNameId() {
		return 0;
	}

	public Timestamp getDespawnTime() {
		return despawnTime;
	}

	public void setDespawnTime(Timestamp despawnTime) {
		this.despawnTime = despawnTime;
	}

	public void setIsLooting(boolean isLooting) {
		this.isLooting = isLooting;
	}

	public boolean isLooting() {
		return this.isLooting;
	}

	public MinionDopingBag getDopingBag() {
		return dopingBag;
	}

	public void setIsBuffing(boolean isBuffing) {
		this.isBuffing = isBuffing;
	}

	public boolean isBuffing() {
		return this.isBuffing;
	}
}