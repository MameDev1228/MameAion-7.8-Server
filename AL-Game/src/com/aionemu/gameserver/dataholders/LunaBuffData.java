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

import com.aionemu.gameserver.model.templates.luna.LunaBonusTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Krz on décembre 2018
 */

@XmlRootElement(name = "luna_bonusattrs")
@XmlAccessorType(XmlAccessType.FIELD)
public class LunaBuffData {

    @XmlElement(name = "luna_bonusattr")
    private List<LunaBonusTemplate> tlist;

    private TIntObjectHashMap<LunaBonusTemplate> mcData;
    private Map<Integer, LunaBonusTemplate> mcDataMap;

    public LunaBuffData() {
        this.mcData = new TIntObjectHashMap<LunaBonusTemplate>();
        this.mcDataMap = new HashMap<Integer, LunaBonusTemplate>(1);
    }
    void afterUnmarshal(final Unmarshaller u, final Object parent) {
        for (final LunaBonusTemplate id : this.tlist) {
            this.mcData.put(id.getBuffId(), id);
            this.mcDataMap.put(id.getBuffId(), id);
        }
    }
    public LunaBonusTemplate getLunaBuffId(final int id) {
        return this.mcData.get(id);
    }
    public Map<Integer, LunaBonusTemplate> getAll() {
        return this.mcDataMap;
    }

    public int size() {
        return mcData.size();
    }
}
