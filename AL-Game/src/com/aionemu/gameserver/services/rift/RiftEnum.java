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
package com.aionemu.gameserver.services.rift;

import com.aionemu.gameserver.model.Race;

/****/
/** Author Rinzler (Encom)
/****/

public enum RiftEnum
{
   /**
	* Elyos Rift
	*/
	//Inggison Rift
	INGGISON_AM(2150, "INGGISON_AM", "GELKMAROS_AS", 150, 24, 75, 80, Race.ASMODIANS),
	INGGISON_BM(2151, "INGGISON_BM", "GELKMAROS_BS", 150, 24, 75, 80, Race.ASMODIANS),
	INGGISON_CM(2152, "INGGISON_CM", "GELKMAROS_CS", 150, 24, 75, 80, Race.ASMODIANS),
	INGGISON_DM(2153, "INGGISON_DM", "GELKMAROS_DS", 150, 24, 75, 80, Race.ASMODIANS),
	INGGISON_EM(2154, "INGGISON_EM", "GELKMAROS_ES", 6, 36, 75, 80, Race.ASMODIANS),
	INGGISON_FM(2155, "INGGISON_FM", "GELKMAROS_FS", 6, 36, 75, 80, Race.ASMODIANS),
	INGGISON_GM(2156, "INGGISON_GM", "GELKMAROS_GS", 6, 36, 75, 80, Race.ASMODIANS),
	INGGISON_HM(2157, "INGGISON_HM", "GELKMAROS_HS", 6, 36, 75, 80, Race.ASMODIANS),
	INGGISON_IM(2158, "INGGISON_IM", "GELKMAROS_IS", 6, 36, 75, 80, Race.ASMODIANS),
	INGGISON_JM(2159, "INGGISON_JM", "GELKMAROS_JS", 6, 36, 75, 80, Race.ASMODIANS),
	
   /**
	* Asmodians Rift
	*/
	//Gelkmaros Rift
	GELKMAROS_AM(2270, "GELKMAROS_AM", "INGGISON_AS", 150, 24, 75, 80, Race.ELYOS),
	GELKMAROS_BM(2271, "GELKMAROS_BM", "INGGISON_BS", 150, 24, 75, 80, Race.ELYOS),
	GELKMAROS_CM(2272, "GELKMAROS_CM", "INGGISON_CS", 150, 24, 75, 80, Race.ELYOS),
	GELKMAROS_DM(2273, "GELKMAROS_DM", "INGGISON_DS", 150, 24, 75, 80, Race.ELYOS),
	GELKMAROS_EM(2274, "GELKMAROS_EM", "INGGISON_ES", 6, 36, 75, 80, Race.ELYOS),
	GELKMAROS_FM(2275, "GELKMAROS_FM", "INGGISON_FS", 6, 36, 75, 80, Race.ELYOS),
	GELKMAROS_GM(2276, "GELKMAROS_GM", "INGGISON_GS", 6, 36, 75, 80, Race.ELYOS),
	GELKMAROS_HM(2277, "GELKMAROS_HM", "INGGISON_HS", 6, 36, 75, 80, Race.ELYOS),
	GELKMAROS_IM(2278, "GELKMAROS_IM", "INGGISON_IS", 6, 36, 75, 80, Race.ELYOS),
	GELKMAROS_JM(2279, "GELKMAROS_JM", "INGGISON_JS", 6, 36, 75, 80, Race.ELYOS);
	
	private int id;
	private String master;
	private String slave;
	private int entries;
	private int abyssPoint;
	private int minLevel;
	private int maxLevel;
	private Race destination;
	
	private RiftEnum(int id, String master, String slave, int entries, int abyssPoint, int minLevel, int maxLevel, Race destination) {
		this.id = id;
		this.master = master;
		this.slave = slave;
		this.entries = entries;
		this.abyssPoint = abyssPoint;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.destination = destination;
	}
	
	public static RiftEnum getRift(int id) throws IllegalArgumentException {
		for (RiftEnum rift : RiftEnum.values()) {
			if (rift.getId() == id) {
				return rift;
			}
		}
		throw new IllegalArgumentException("Unsupported rift id: " + id);
	}
	
	public int getId() {
		return id;
	}
	
	public String getMaster() {
		return master;
	}
	
	public String getSlave() {
		return slave;
	}
	
	public int getEntries() {
		return entries;
	}
	
	public int getAbyssPoint() {
		return abyssPoint;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public Race getDestination() {
		return destination;
	}
}