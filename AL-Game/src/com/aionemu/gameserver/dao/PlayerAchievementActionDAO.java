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
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementAction;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;

/**
 * @author Krz on décembre 2018
 */

public abstract class PlayerAchievementActionDAO implements IDFactoryAwareDAO
{
    public abstract void loadActions(Player player, PlayerAchievement achievement);
    public abstract boolean storeAction(Player player, AchievementAction action);
    public abstract boolean update(Player player, AchievementAction action);
    public abstract void deleteAchievementsActions(AchievementType type);
	
    @Override
    public final String getClassName() {
        return PlayerAchievementActionDAO.class.getName();
    }
}