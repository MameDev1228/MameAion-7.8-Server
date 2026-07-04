package com.aionemu.gameserver.model.gameobjects.player.equipmentset;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerEquipmentSettingDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EquipmentSettingList
{
    private Map<Integer, EquipmentSetting> equipmentSetting;
    private Player owner;
	
    public EquipmentSettingList(final Player owner) {
        this.owner = owner;
    }
	
    public void add(int slot, int display, int mHand, int sHand, int helmet, int torso, int glove, int boots, int earringsLeft,
	    int earringsRight, int ringLeft, int ringRight, int necklace, int shoulder, int pants, int powershardLeft, int powershardRight,
		int wings, int waist, int mOffHand, int sOffHand, int plume, int bracelet, boolean isNew) {
        if (equipmentSetting == null) {
            equipmentSetting = new HashMap<Integer, EquipmentSetting>();
        }
        EquipmentSetting equipmentSettings = new EquipmentSetting(slot, display, mHand, sHand, helmet, torso, glove, boots, earringsLeft,
        earringsRight, ringLeft, ringRight, necklace, shoulder, pants, powershardLeft, powershardRight, wings, waist, mOffHand,
        sOffHand, plume, bracelet);
        if (equipmentSetting.get(slot) != null) {
            equipmentSetting.remove(slot);
            DAOManager.getDAO(PlayerEquipmentSettingDAO.class).deleteEquipmentSetting(owner, slot);
        }
        equipmentSetting.put(slot, equipmentSettings);
        DAOManager.getDAO(PlayerEquipmentSettingDAO.class).insertEquipmentSetting(owner, equipmentSettings);
	}
	
    public Collection<EquipmentSetting> getEquipmentSetting() {
        if (equipmentSetting == null) {
            return Collections.emptyList();
        }
        return equipmentSetting.values();
    }
}