package com.aionemu.gameserver.skillengine.condition;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.ChargeSkillTemplate;
import com.aionemu.gameserver.skillengine.model.ChargeTemplate;
import com.aionemu.gameserver.skillengine.model.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillChargeCondition")
public class SkillChargeCondition extends ChargeCondition {

    @Override
    public boolean validate(Skill env) {
        int castTime = 0;
        if (env.getEffector() instanceof Player) {
            //ChargeSkillTemplate skillCharge = DataManager.CHARGE_SKILL_DATA.getChargedSkillEntry(level);
            //env.getChargeSkillList().addAll(skillCharge.getCharges());
            //for (ChargeTemplate skill : env.getChargeSkillList()) {
               // castTime += skill.getTime();
            //}
            env.setDuration((int) castTime);
        }
        return true;
    }

    public int getLevel() {
        return 0;
    }
}
