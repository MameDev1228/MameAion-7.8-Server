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
package com.aionemu.gameserver.services.account;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AccountMonsterCoreDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.monsterCore.MonsterCore;
import com.aionemu.gameserver.model.templates.monster_core.MonsterCoreTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MONSTER_CORE_ADD;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MONSTER_CORE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonsterCoreService
{
    private static final Logger log = LoggerFactory.getLogger(MonsterCoreService.class);
    private AccountMonsterCoreDAO dao = DAOManager.getDAO(AccountMonsterCoreDAO.class);
	
    public void onCreatePlayer(Player player) {
        for (MonsterCoreTemplate template: DataManager.MONSTER_CORE_DATA.getAll().values()) {
            MonsterCore core = new MonsterCore(template.getId());
            core.setLevel(0);
            core.setCollect(0);
            getDao().store(player.getPlayerAccount(), core);
        }
    }
	
    public void debugPlayerMonsterCore(Player player) {
        if (player.getPlayerMonsterCore().size() < DataManager.MONSTER_CORE_DATA.getAll().size()) {
            for (MonsterCoreTemplate template : DataManager.MONSTER_CORE_DATA.getAll().values()) {
                if (!player.getPlayerMonsterCore().containsKey(template.getId())) {
                    MonsterCore core = new MonsterCore(template.getId());
                    core.setLevel(0);
                    core.setCollect(0);
                    getDao().store(player.getPlayerAccount(), core);
                    player.getPlayerMonsterCore().put(core.getId(), core);
                }
            }
        }
    }
	
    public void onLogin(Player player) {
        player.setPlayerMonsterCore(DAOManager.getDAO(AccountMonsterCoreDAO.class).load(player.getPlayerAccount()));
        debugPlayerMonsterCore(player);
        for (MonsterCore core: player.getPlayerMonsterCore().values()) {
            core.end(player);
            core.apply(player);
        }
        PacketSendUtility.sendPacket(player, new SM_MONSTER_CORE_LIST(player));
        PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
    }
	
    public void onLogout(Player player) {
        for (MonsterCore core: player.getPlayerMonsterCore().values()) {
            getDao().update(player.getPlayerAccount(), core);
            core.end(player);
        }
    }
	
    public void onUseMonsterCore(Player player, int id) {
        if (!player.getInventory().decreaseByItemId(DataManager.MONSTER_CORE_DATA.getMonsterCoreId(id).getItemId(), 1)) {
            return;
        } if (player.getPlayerMonsterCore().get(id).getLevel() >= DataManager.MONSTER_CORE_DATA.getMonsterCoreId(id).getMaxRank()) {
           return;
        }
        MonsterCore core = player.getPlayerMonsterCore().get(id);
        if (levelUp(core)) {
            core.end(player);
            core.setLevel(core.getLevel() + 1);
            core.setCollect(0);
            core.apply(player);
            PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
        } else {
            core.setCollect(core.getCollect() + 1);
        } if (core.getLevel() > DataManager.MONSTER_CORE_DATA.getMonsterCoreId(id).getMaxRank()) {
            core.setLevel(DataManager.MONSTER_CORE_DATA.getMonsterCoreId(id).getMaxRank());
        }
        PacketSendUtility.sendPacket(player, new SM_MONSTER_CORE_ADD(player, core));
        PacketSendUtility.sendPacket(player, new SM_MONSTER_CORE_LIST(player));
        getDao().update(player.getPlayerAccount(), core);
    }
	
    public boolean levelUp(MonsterCore core) {
        MonsterCoreTemplate template = DataManager.MONSTER_CORE_DATA.getMonsterCoreId(core.getId());
        if (core.getLevel() + 1 >= template.getMaxRank() && core.getCollect() + 1 >= template.getStatLists().get(core.getLevel()).getLevel()) {
            return true;
        } if (core.getCollect() + 1 >= template.getStatLists().get(core.getLevel()).getLevel()) {
            return true;
        }
        return false;
    }

    public float getCubusAttack(Player player, int monster) {
        int attack = 10;
        switch (monster) {
            case 656409: //Dragon Lord Ereshkigal.
                if(player.getPlayerMonsterCore().containsKey(40)) {
                    MonsterCoreTemplate template = player.getPlayerMonsterCore().get(40).getMt();
                    attack = attack + template.getStatLists().get(player.getPlayerMonsterCore().get(40).getLevel() - 1).getValue();
                }
                if(player.getPlayerMonsterCore().containsKey(41)) {
                    MonsterCoreTemplate template = player.getPlayerMonsterCore().get(41).getMt();
                    attack = attack + template.getStatLists().get(player.getPlayerMonsterCore().get(41).getLevel() - 1).getValue();
                }
                if(player.getPlayerMonsterCore().containsKey(42)) {
                    MonsterCoreTemplate template = player.getPlayerMonsterCore().get(42).getMt();
                    attack = attack + template.getStatLists().get(player.getPlayerMonsterCore().get(42).getLevel() - 1).getValue();
                }
                break;
        }
        return attack / 100F;
    }

    public float getCubusDef(Player player, int monster) {
        int def = 20;
        switch (monster) {
            case 656409: //Dragon Lord Ereshkigal.
                if(player.getPlayerMonsterCore().containsKey(43)) {
                    MonsterCoreTemplate template = player.getPlayerMonsterCore().get(43).getMt();
                    def = def + template.getStatLists().get(player.getPlayerMonsterCore().get(43).getLevel() - 1).getValue();
                }
                if(player.getPlayerMonsterCore().containsKey(44)) {
                    MonsterCoreTemplate template = player.getPlayerMonsterCore().get(44).getMt();
                    def = def + template.getStatLists().get(player.getPlayerMonsterCore().get(44).getLevel() - 1).getValue();
                }
                if(player.getPlayerMonsterCore().containsKey(45)) {
                    MonsterCoreTemplate template = player.getPlayerMonsterCore().get(45).getMt();
                    def = def + template.getStatLists().get(player.getPlayerMonsterCore().get(45).getLevel() - 1).getValue();
                }
                break;
        }
        return def / 100F;
    }
	
    public static MonsterCoreService getInstance() {
        return MonsterCoreServiceHolder.INSTANCE;
    }
	
    public AccountMonsterCoreDAO getDao() {
        return dao;
    }
	
    private static class MonsterCoreServiceHolder {
        private static final MonsterCoreService INSTANCE = new MonsterCoreService();
    }
}