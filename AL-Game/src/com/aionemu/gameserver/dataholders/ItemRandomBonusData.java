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
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.item.bonuses.RandomBonus;
import com.aionemu.gameserver.model.templates.item.bonuses.StatBonusType;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****/
/** Author Rinzler (Encom)
/****/

@XmlRootElement(name = "real_random_templates")
public class ItemRandomBonusData
{
	@XmlElement(name = "real_random_template", required = true)
	protected List<RandomBonus> randomBonuses;

	private TIntObjectHashMap<RandomBonus> mcData;
	private Map<Integer, RandomBonus> mcDataMap;

	public ItemRandomBonusData() {
		this.mcData = new TIntObjectHashMap<RandomBonus>();
		this.mcDataMap = new HashMap<Integer, RandomBonus>(1);
	}

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (RandomBonus bonus : randomBonuses) {
			this.mcData.put(bonus.getId(), bonus);
			this.mcDataMap.put(bonus.getId(), bonus);
		}
		randomBonuses.clear();
		randomBonuses = null;
	}


	public RandomBonus getRndBonusById(final int id) {
		return this.mcData.get(id);
	}

	public ModifiersTemplate getTemplate(StatBonusType bonusType, int rndOptionSet, int number) {
		return null;
	}
	
	public int size() {
		return mcData.size();
	}
}