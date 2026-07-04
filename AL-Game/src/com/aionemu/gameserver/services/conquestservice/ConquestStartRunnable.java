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
package com.aionemu.gameserver.services.conquestservice;

import com.aionemu.gameserver.model.conquest.ConquestLocation;
import com.aionemu.gameserver.services.ConquestService;

import java.util.Map;

/**
 * @author Rinzler (Encom)
 */

public class ConquestStartRunnable implements Runnable
{
	private final int id;
	
	public ConquestStartRunnable(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		//Emperor Trillirunerk's Safe is now open !!!
		ConquestService.getInstance().trillirunerkSafeMsg(id);
		//Tiamat's Incarnation has appeared.
		ConquestService.getInstance().sunayakaMsg(id);
		//Crimson Danaria 7.x
		ConquestService.getInstance().danariaMsg(id);
		//[Inggison & Gelkmaros] 7.x
		ConquestService.getInstance().dredgionMsg(id);
		Map<Integer, ConquestLocation> locations = ConquestService.getInstance().getConquestLocations();
		for (final ConquestLocation loc : locations.values()) {
			if (loc.getId() == id) {
				ConquestService.getInstance().startConquest(loc.getId());
			}
		}
	}
}