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
package com.aionemu.gameserver.model.autogroup;

import com.aionemu.gameserver.dataholders.DataManager;

import java.util.List;

/**
 * @author Rinzler (Encom)
 */

public enum AutoGroupType
{
	//ARENA PVP 76-80
	ARENA_OF_DISCIPLINE(114, 600000, 2) { @Override AutoInstance newAutoInstance() { return new AutoDisciplineInstance(); } },
	ARENA_OF_HARMONY(115, 600000, 2) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	
	//BATTLEFIELD.
	KAMAR_BATTLEFIELD(107, 600000, 4) { @Override AutoInstance newAutoInstance() { return new AutoKamarBattlefieldInstance(); } },
	IDGEL_DOME(111, 600000, 4) { @Override AutoInstance newAutoInstance() { return new AutoIdgelDomeInstance(); } },
	ILLUMIEL_BRAWL(116, 600000, 4) { @Override AutoInstance newAutoInstance() { return new AutoIllumielBrawlInstance(); } },
	//VALLEY_OF_CHAOS(141, 600000, 4) { @Override AutoInstance newAutoInstance() { return new AutoValleyOfChaosInstance(); } },
	//IDGEL_DOME_OF_GLORY(142, 600000, 4) { @Override AutoInstance newAutoInstance() { return new AutoIllumielBrawlInstance(); } },
	//VALLEY_OF_CHAOS_N(143, 600000, 4) { @Override AutoInstance newAutoInstance() { return new AutoIllumielBrawlInstance(); } },
	
	//ASHUNATAL DREDGION.
	ASHUNATAL_DREDGION(121, 600000, 4) { @Override AutoInstance newAutoInstance() { return new AutoAshunatalDredgionInstance(); } },
	
	//Instance 7.x
	FIRE_TEMPLE_501(501, 600000, 2) { @Override AutoInstance newAutoInstance() { return new AutoFireTempleInstance(); } },
	ESOTERRACE(502, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	//INSTANCE.
	THE_SHUGO_EMPEROR_VAULT(348, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	WICKED_GRACHENI_VAULT(361, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	PRIMETH_FORGE(422, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	THE_VEILENTHRONE(425, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	STELLIN_DEVELOPMENT_LAB(430, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	STELLIN_DEVELOPMENT_LAB_EASY(433, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	INFERNAL_DRAKENSPIRE_DEPTHS_L(435, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	INFERNAL_DRAKENSPIRE_DEPTHS_D(436, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	PRIMETH_FORGE_HARD(437, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	INFERNAL_DRAKENSPIRE_DEPTHS_HARD(438, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	BENIRUNERK_ESTATE(506, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	BENIRUNERK_ESTATE_EASY(507, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	GENESIS_ARENA(509, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	ALTAR_OF_ASCENSION(512, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	ALTAR_OF_ASCENSION_EASY(514, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	ALTAR_OF_ASCENSION_OF_OPPORTUNITY(516, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	ESOTERRACE_OF_OPPORTUNITY(518, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	IDCATACOMBS_RUDRA(520, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	IDCATACOMBS_RUDRA_E(528, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } };
	
	private int instanceMaskId;
	private int time;
	private byte playerSize;
	private byte difficultId;
	private AutoGroup template;
	
	private AutoGroupType(int instanceMaskId, int time, int playerSize, int difficultId) {
        this(instanceMaskId, time, playerSize);
        this.difficultId = (byte) difficultId;
    }
	
	private AutoGroupType(int instanceMaskId, int time, int playerSize) {
        this.instanceMaskId = instanceMaskId;
        this.time = time;
        this.playerSize = (byte) playerSize;
        template = DataManager.AUTO_GROUP.getTemplateByInstaceMaskId(this.instanceMaskId);
    }
	
	public int getInstanceMapId() {
		return template.getInstanceId();
	}
	
	public byte getPlayerSize() {
		return playerSize;
	}
	
	public int getInstanceMaskId() {
		return instanceMaskId;
	}
	
	public int getNameId() {
		return template.getNameId();
	}
	
	public int getTitleId() {
		return template.getTitleId();
	}
	
	public int getTime() {
		return time;
	}
	
	public int getMinLevel() {
		return template.getMinLvl();
	}
	
	public int getMaxLevel() {
		return template.getMaxLvl();
	}
	
	public boolean hasRegisterGroup() {
		return template.hasRegisterGroup();
	}
	
	public boolean hasRegisterFast() {
		return template.hasRegisterFast();
	}
	
	public boolean hasSpecialPurpose() {
		return template.hasSpecialPurpose();
	}
	
	public boolean hasRegisterNew() {
		return template.hasRegisterNew();
	}
	
	public boolean containNpcId(int npcId) {
		return template.getNpcIds().contains(npcId);
	}
	
	public List<Integer> getNpcIds() {
		return template.getNpcIds();
	}
	
	public boolean isAshunatal() {
		switch (this) {
			case ASHUNATAL_DREDGION:
				return true;
		}
		return false;
	}
	public boolean isKamar() {
		switch (this) {
			case KAMAR_BATTLEFIELD:
				return true;
		}
		return false;
	}
	public boolean isIdgelDome() {
		switch (this) {
			case IDGEL_DOME:
				return true;
		}
		return false;
	}
	public boolean isIllumiel() {
		switch (this) {
			case ILLUMIEL_BRAWL:
				return true;
		}
		return false;
	}
	public boolean isFireTemple() {
		switch (this) {
			case FIRE_TEMPLE_501:
				return true;
		}
		return false;
	}
	
	public static AutoGroupType getAGTByMaskId(int instanceMaskId) {
		for (AutoGroupType autoGroupsType : values()) {
			if (autoGroupsType.getInstanceMaskId() == instanceMaskId) {
				return autoGroupsType;
			}
		}
		return null;
	}
	
	public static AutoGroupType getAutoGroup(int level, int npcId) {
		for (AutoGroupType agt : values()) {
			if (agt.hasLevelPermit(level) && agt.containNpcId(npcId)) {
				return agt;
			}
		}
		return null;
	}
	
	public static AutoGroupType getAutoGroupByWorld(int level, int worldId) {
		for (AutoGroupType agt : values()) {
			if (agt.getInstanceMapId() == worldId && agt.hasLevelPermit(level)) {
				return agt;
			}
		}
		return null;
	}
	
	public static AutoGroupType getAutoGroup(int npcId) {
		for (AutoGroupType agt : values()) {
			if (agt.containNpcId(npcId)) {
				return agt;
			}
		}
		return null;
	}
	
	public boolean isPvPSoloArena() {
		switch (this) {
			case ARENA_OF_DISCIPLINE:
				return true;
		}
		return false;
	}
	
	public boolean isHarmonyArena() {
		switch (this) {
			case ARENA_OF_HARMONY:
				return true;
		}
		return false;
	}
	
	public boolean isPvpArena() {
		return isHarmonyArena() || isPvPSoloArena();
	}
	
	public boolean hasLevelPermit(int level) {
		return level >= getMinLevel() && level <= getMaxLevel();
	}
	
	public byte getDifficultId() {
		return difficultId;
	}
	
	public AutoInstance getAutoInstance() {
		return newAutoInstance();
	}
	
	abstract AutoInstance newAutoInstance();
}