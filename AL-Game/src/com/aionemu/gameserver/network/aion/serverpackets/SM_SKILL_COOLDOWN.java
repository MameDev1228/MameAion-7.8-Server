/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.ArrayList;
import java.util.Map;

public class SM_SKILL_COOLDOWN extends AionServerPacket
{
    private Map<Integer, Long> cooldowns;

    public SM_SKILL_COOLDOWN(Map<Integer, Long> cooldowns) {
        this.cooldowns = cooldowns;
    }
    
	@Override
    protected void writeImpl(AionConnection con) {
    	writeH(calculateSize());
        writeC(1);
        long currentTime = System.currentTimeMillis();
        if (cooldowns == null) {
            return;
        }
        for (Map.Entry<Integer, Long> entry : cooldowns.entrySet()) {
            int left = (int) ((entry.getValue() - currentTime) / 1000);
            if (left <= 0) {
                continue;
            }
            ArrayList<Integer> skillsWithCooldown = DataManager.SKILL_DATA.getSkillsForDelayId(entry.getKey());
            if (skillsWithCooldown == null || skillsWithCooldown.isEmpty()) {
                continue;
            }
            for (int index = 0; index < skillsWithCooldown.size(); index++) {
                int skillId = skillsWithCooldown.get(index);
                if (DataManager.SKILL_DATA.getSkillTemplate(skillId) == null) {
                    continue;
                }
                writeH(skillId);
                writeD(left);
                writeD(DataManager.SKILL_DATA.getSkillTemplate(skillId).getCooldown());
            }
        }
    }
	
	private int calculateSize() {
        int size = 0;
        long currentTime = System.currentTimeMillis();
        if (cooldowns == null) {
            return 0;
        }
        for (Map.Entry<Integer, Long> entry : cooldowns.entrySet()) {
            if (entry.getValue() == null || entry.getValue() <= currentTime) {
                continue;
            }
            ArrayList<Integer> skillsWithCooldown = DataManager.SKILL_DATA.getSkillsForDelayId(entry.getKey());
            if (skillsWithCooldown != null) {
                for (int skillId : skillsWithCooldown) {
                    if (DataManager.SKILL_DATA.getSkillTemplate(skillId) != null) {
                        size++;
                    }
                }
            }
        }
        return size;
    }
}