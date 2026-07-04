package com.aionemu.gameserver.model.skinskill;


import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerSkillSkinListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_SKIN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkinSkillTemplate;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

import java.util.Collection;

public class SkillSkinList
{
    private final FastMap<Integer, SkillSkin> skillskins;
    private Player owner;
	
    public SkillSkinList() {
        skillskins = new FastMap();
        owner = null;
    }
	
    public Player getOwner() {
        return owner;
    }
	
    public void setOwner(Player owner) {
        this.owner = owner;
    }
	
    public boolean contains(int skinId) {
        return skillskins.containsKey(skinId);
    }
	
    public void addEntry(int skinId, int remaining, int active) {
        SkinSkillTemplate ss = DataManager.SKIN_SKILL.getSkillSkinTemplate(skinId);
        if (ss == null) {
            throw new IllegalArgumentException("Invalid skill skin id " + skinId);
        }
        skillskins.put(skinId, new SkillSkin(ss, skinId, remaining, active));
    }

    public boolean addSkillSkin(int skinId, int time, int expireTime) {
        SkinSkillTemplate sst = DataManager.SKIN_SKILL.getSkillSkinTemplate(skinId);
        if (sst == null) {
            throw new IllegalArgumentException("Invalid skin id " + skinId);
        }
        if (owner != null) {
            SkillSkin skillSkin = new SkillSkin(sst, skinId, expireTime, 1);
            if (!skillskins.containsKey(skinId)) {
                skillskins.put(skinId, skillSkin);
                if (time != 0) {
                    ExpireTimerTask.getInstance().addTask(skillSkin, owner);
                }
                DAOManager.getDAO(PlayerSkillSkinListDAO.class).storeSkillSkins(owner, skillSkin);
            }
            else {
                PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_COSTUME_SKILL_ALREADY_HAS_COSTUME);
                return false;
            }
            PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_GET_ITEM(sst.getName()));
            PacketSendUtility.sendPacket(owner, new SM_SKILL_SKIN(skinId, 0));
            return true;
        }
        return false;
    }
	
    public void removeSkillSkin(int skinId) {
        if (!skillskins.containsKey(skinId)) {
            return;
        }
        skillskins.remove(skinId);
        PacketSendUtility.sendPacket(owner, new SM_SKILL_SKIN(owner));
        DAOManager.getDAO(PlayerSkillSkinListDAO.class).removeSkillSkin(owner.getObjectId(), skinId);
    }
	
    public void setActive(int skinId) {
        DAOManager.getDAO(PlayerSkillSkinListDAO.class).setActive(owner.getObjectId(), skinId);
        owner.setSkillSkinList(DAOManager.getDAO(PlayerSkillSkinListDAO.class).loadSkillSkinList(owner.getObjectId()));
        PacketSendUtility.sendPacket(owner, new SM_SKILL_SKIN(owner));
    }
	
    public void setDeactive(int skillId) {
        int skinIdToremove = 0;
        SkillTemplate skillGroup = DataManager.SKILL_DATA.getSkillTemplate(skillId);
        if (owner.getSkillSkinList() != null) {
            for (SkillSkin skillSkin : owner.getSkillSkinList().getSkillSkins()) {
                if (skillSkin.getTemplate() != null) {
                    if (skillSkin.getTemplate().getGroup().equalsIgnoreCase(skillGroup.getGroup())
                            && skillSkin.getIsActive() == 1) {
                        skinIdToremove = skillSkin.getId();
                        break;
                    }
                }
            }
        }
        DAOManager.getDAO(PlayerSkillSkinListDAO.class).setDeactive(owner.getObjectId(), skinIdToremove);
        owner.setSkillSkinList(DAOManager.getDAO(PlayerSkillSkinListDAO.class).loadSkillSkinList(owner.getObjectId()));
        PacketSendUtility.sendPacket(owner, new SM_SKILL_SKIN(owner));
    }
	
    public int getSkinId(int SkillId) {
        int skinid = 0;
        if ((SkillId == 0) || (getOwner().getSkillSkinList() == null) || (getOwner() == null)) {
            return 0;
        } for (SkillSkin skillSkin : getOwner().getSkillSkinList().getSkillSkins()) {
            if ((DataManager.SKILL_DATA.getSkillTemplate(SkillId).getGroup() != null) && (skillSkin.getTemplate().getGroup().equalsIgnoreCase(DataManager.SKILL_DATA.getSkillTemplate(SkillId).getGroup())) && (skillSkin.getIsActive() == 1)) {
                skinid = skillSkin.getId();
            }
        }
        return skinid;
    }
	
    public int size() {
        return skillskins.size();
    }
	
    public Collection<SkillSkin> getSkillSkins() {
        return skillskins.values();
    }
}