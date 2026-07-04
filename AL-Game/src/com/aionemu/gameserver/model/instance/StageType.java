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
package com.aionemu.gameserver.model.instance;

public enum StageType
{
	DEFAULT(0, 0),
    PVP_STAGE_1(1, 0),
    PVP_STAGE_2(2, 0),
    PVP_STAGE_3(3, 0),
    PVP_STAGE_4(4, 0),
    PVP_STAGE_5(5, 0),
    PVP_STAGE_6(6, 0),
    PVP_STAGE_OVER(0, 0);
	
	private int id;
	private int type;
	
	private StageType(int id, int type) {
		this.id = id;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
}