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

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.model.templates.item.upgrade.ItemUpgradeTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Ranastic (Encom)
 */
 
@XmlRootElement(name = "item_upgrades")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemUpgradeData
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(GameServer.class);
	
	@XmlElement(name = "item_upgrade")
	protected List<ItemUpgradeTemplate> itemUpgradeTemplates;


	@XmlTransient
	private TIntObjectHashMap<ItemUpgradeTemplate> custom = new TIntObjectHashMap<ItemUpgradeTemplate>();


	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (ItemUpgradeTemplate upgradeTemplate : itemUpgradeTemplates) {
			getCustomMap().put(upgradeTemplate.getUpgrade_base_item_id(), upgradeTemplate);
		}
	}
	
	public ItemUpgradeTemplate getItemUpgradeTemplate(int baseItemId) {
		return getCustomMap().get(baseItemId);
	}

	private TIntObjectHashMap<ItemUpgradeTemplate> getCustomMap() {
		return custom;
	}

	public int size() {
		return getCustomMap().size();
	}
}