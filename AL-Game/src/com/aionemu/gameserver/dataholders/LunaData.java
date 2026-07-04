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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.recipe.LunaTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/****/
/** Author Rinzler (Encom)
/****/

@XmlRootElement(name = "luna_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class LunaData
{
	@XmlElement(name = "luna_template")
	protected List<LunaTemplate> list;
	
	private TIntObjectHashMap<LunaTemplate> lunaData;
	
	private FastList<LunaTemplate> elyos, asmos, daily;
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		lunaData = new TIntObjectHashMap<LunaTemplate>();
		elyos = FastList.newInstance();
		asmos = FastList.newInstance();
		daily = FastList.newInstance();
		for (LunaTemplate lt: list) {
			lunaData.put(lt.getId(), lt);

			if(lt.getGroup() == 1) {
				daily.add(lt);
			} else {
				if(lt.getRace() == Race.ELYOS) {
					elyos.add(lt);
				} else {
					asmos.add(lt);
				}
			}
		}
		list = null;
	}
	
	public LunaTemplate getLunaTemplateById(int id) {
		return lunaData.get(id);
	}
	
	public TIntObjectHashMap<LunaTemplate> getLunaTemplates() {
		return lunaData;
	}

	public FastList<LunaTemplate> getLunaTemplatesDaly() {
		return daily;
	}
	public FastList<LunaTemplate> getLunaTemplatesElyos() {
		return elyos;
	}

	public FastList<LunaTemplate> getLunaTemplatesAsmo() {
		return asmos;
	}

	public int size() {
		return lunaData.size();
	}
}