package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.equipmentset.EquipmentSetting;

public abstract class PlayerEquipmentSettingDAO implements DAO
{
    public abstract void loadEquipmentSetting(Player player);
    public abstract void insertEquipmentSetting(Player player, final EquipmentSetting equipmentSetting);
    public abstract void deleteEquipmentSetting(Player player, final int slotId);
	
    @Override
    public String getClassName() {
        return PlayerEquipmentSettingDAO.class.getName();
    }
}