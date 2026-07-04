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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.AchievementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Krz on décembre 2018
 */
public class CM_COMPLETE_ACHIEVEMENT extends AionClientPacket {

    private int templateId;
    private long achievementObj;
    private long actionObj;

    private static final Logger log = LoggerFactory.getLogger(CM_COMPLETE_ACHIEVEMENT.class);

    public CM_COMPLETE_ACHIEVEMENT(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.templateId = readD();
        this.achievementObj = readQ();
        this.actionObj = readQ();
    }

    @Override
    protected void runImpl() {
        if(this.achievementObj == 0) {
            AchievementService.getInstance().onRewardAchievement(getConnection().getActivePlayer(), this.templateId);
        } else {
            AchievementService.getInstance().onRewardAction(getConnection().getActivePlayer(), (int) this.actionObj, this.templateId);
        }
        //
    }
}