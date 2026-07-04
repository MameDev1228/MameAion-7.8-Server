package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skinskill.SkillSkin;
import com.aionemu.gameserver.model.skinskill.SkillSkinList;

public abstract class PlayerSkillSkinListDAO implements DAO {

    public final String getClassName() {
        return PlayerSkillSkinListDAO.class.getName();
    }

    public abstract SkillSkinList loadSkillSkinList(int paramInt);

    public abstract boolean storeSkillSkins(Player paramPlayer, SkillSkin skinkillSkin);

    public abstract boolean removeSkillSkin(int paramInt1, int paramInt2);

    public abstract boolean setActive(int paramInt1, int paramInt2);

    public abstract boolean setDeactive(int paramInt1, int paramInt2);
}

