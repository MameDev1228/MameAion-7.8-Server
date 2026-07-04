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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.item.ItemRndBonus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;

import java.util.ArrayList;
import java.util.List;

public class RndBonusEffect implements StatOwner
{
    private List<IStatFunction> functions = new ArrayList<IStatFunction>();
    private Item item;
	
    public RndBonusEffect(Item item) {
        this.item = item;
    }
	
    /**
     * Legacy builder kept for compatibility.
     * Use applyEffect(Player) when the bonus must actually affect player stats.
     */
    public void applyEffect() {
        rebuildFunctions();
    }

    /**
     * Applies item tuning/random bonus stats to the equipped player.
     * Old code only rebuilt the local function list, so HP/PvP/PvE/random bonus values
     * were visible on the item but never added to CreatureGameStats.
     */
    public void applyEffect(Player player) {
        if (player == null) {
            return;
        }
        player.getGameStats().endEffect(this);
        rebuildFunctions();
        if (!functions.isEmpty()) {
            player.getGameStats().addEffect(this, functions);
        }
    }

    private void rebuildFunctions() {
        functions.clear();
        for (ItemRndBonus rndBonus : this.item.getRndBonus().values()) {
            StatEnum stat = StatEnum.findByItemStoneMask(rndBonus.getBonus());
            if (stat != null) {
                functions.add(new StatAddFunction(stat, rndBonus.getValue(), true));
            }
        }
    }
	
    public void endEffect(Player player) {
        if (player != null) {
            player.getGameStats().endEffect(this);
        }
        functions.clear();
    }
}