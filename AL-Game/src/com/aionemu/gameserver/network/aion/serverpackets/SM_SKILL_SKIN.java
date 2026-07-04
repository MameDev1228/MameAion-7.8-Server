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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skinskill.SkillSkin;
import com.aionemu.gameserver.model.skinskill.SkillSkinList;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import javolution.util.FastList;

public class SM_SKILL_SKIN extends AionServerPacket
{
    private int action;
    private SkillSkinList skillSkinList;
    private int skillAnimation;
    private int expire;
    private int isActive;

    public SM_SKILL_SKIN(int skillSkinId, int expire) {
        this.action = 0;
        this.skillAnimation = skillSkinId;
        this.expire = expire;
        this.isActive = 1;
    }

    public SM_SKILL_SKIN(Player player) {
        action = 1;
        skillSkinList = player.getSkillSkinList();
    }
	
    protected void writeImpl(AionConnection connection) {
        switch (action) {
            case 0: //Learn Action.
                writeC(0); //action

                writeH(1); //size
                writeH(this.skillAnimation);
                writeD(expire);
                writeC(isActive);
            break;
            case 1: //Skill List.
                writeC(1); //action
                writeH(skillSkinList.size());
                if (skillSkinList != null && skillSkinList.size() != 0) {
                    for (SkillSkin skillSkin : skillSkinList.getSkillSkins()) {
                        writeH(skillSkin.getId());
                        writeD(skillSkin.getExpireTime());
                        writeC(skillSkin.getIsActive());
                    }
                } else {
                    writeH(0);
                    writeD(0);
                    writeC(0);
                }
            break;
        }
    }
}