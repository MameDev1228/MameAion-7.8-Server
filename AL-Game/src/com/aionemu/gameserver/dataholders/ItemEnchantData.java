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

import com.aionemu.gameserver.model.templates.item.EnchantType;
import com.aionemu.gameserver.model.templates.item.ItemEnchantTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Ranastic (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "enchant_templates")
public class ItemEnchantData
{
    @XmlElement(name = "enchant_template", required = true)
    protected List<ItemEnchantTemplate> enchantTemplates;
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @XmlTransient
    private TIntObjectHashMap<ItemEnchantTemplate> authorizes = new TIntObjectHashMap();
    @XmlTransient
    private FastMap<Integer, ItemEnchantTemplate> pve = new FastMap<Integer, ItemEnchantTemplate>();
    @XmlTransient
    private FastMap<Integer, ItemEnchantTemplate> pvp = new FastMap<Integer, ItemEnchantTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (ItemEnchantTemplate it : this.enchantTemplates) {
            getEnchantMap().put(it.getId(), it);

            if(it.getType() == EnchantType.PVE) {
                pve.put(it.getId(), it);
            } else {
                pvp.put(it.getId(), it);
            }
        }
    }
	
    private TIntObjectHashMap<ItemEnchantTemplate> getEnchantMap() {
        return this.authorizes;
    }
	
    public ItemEnchantTemplate getEnchantTemplate(int id) {
        return this.authorizes.get(id);
    }

    public ItemEnchantTemplate getEnchantePveTemplate(int id) {
        return this.pve.get(id);
    }

    public ItemEnchantTemplate getEnchantePvpTemplate(int id) {
        return this.pvp.get(id);
    }
	
    public int size() {
        return this.authorizes.size();
    }
}