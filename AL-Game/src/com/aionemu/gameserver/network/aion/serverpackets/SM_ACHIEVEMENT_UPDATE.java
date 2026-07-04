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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementState;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Krz on décembre 2018
 */
public class SM_ACHIEVEMENT_UPDATE extends AionServerPacket {

    private int objectId;
    private int step;
    AchievementState state;

    public SM_ACHIEVEMENT_UPDATE(int objectId, int step, AchievementState state) {
        this.objectId = objectId;
        this.step = step;
        this.state = state;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(1); //size
        writeQ(this.objectId);
        writeC(this.state.getValue());
        writeD(this.step);
        writeD(0);
        writeD(0);
        writeD(0);
        writeD(0);
        writeD(0);

    }
}