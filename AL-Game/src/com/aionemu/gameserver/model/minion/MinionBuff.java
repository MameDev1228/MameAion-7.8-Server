package com.aionemu.gameserver.model.minion;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.*;
import com.aionemu.gameserver.model.templates.minion.*;
import com.aionemu.gameserver.skillengine.change.Func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Ranastic
 */
public class MinionBuff implements StatOwner
{
	Logger log = LoggerFactory.getLogger(MinionBuff.class);
	
	public void apply(Player player, int minionId) {
		if (minionId == 0) {
			return;
		}
		MinionTemplate minionTemplate = DataManager.MINION_DATA.getMinionTemplate(minionId);
		if (minionTemplate == null) {
			return;
		}
		List<MinionAttr> attribute = player.isMagicalTypeClass() ? minionTemplate.getMagicalAttr() : minionTemplate.getPhysicalAttr();
		if (attribute == null || attribute.isEmpty()) {
			return;
		}

		// Minion stat effects are represented by this StatOwner. Re-applying without first
		// removing the old owner effect stacks the same minion attributes and can make the
		// client tooltip say MAXHP +13875 while the real HP jumps by several times that value.
		// Always normalize first; dismiss/re-summon should not be required to correct stats.
		player.getGameStats().endEffect(this);

		List<IStatFunction> functions = new ArrayList<IStatFunction>();
		StringBuilder debug = new StringBuilder();
		for (MinionAttr minionAttribute : attribute) {
			if (minionAttribute == null || minionAttribute.getStat() == null || minionAttribute.getFunc() == null) {
				continue;
			}
			if (debug.length() > 0) {
				debug.append(',');
			}
			debug.append(minionAttribute.getStat()).append('=').append(minionAttribute.getValue()).append('/').append(minionAttribute.getFunc());
			if (minionAttribute.getFunc().equals(Func.PERCENT)) {
				functions.add(new StatRateFunction(minionAttribute.getStat(), minionAttribute.getValue(), true));
			} else {
				functions.add(new StatAddFunction(minionAttribute.getStat(), minionAttribute.getValue(), true));
			}
		}
		if (functions.isEmpty()) {
			return;
		}
		player.setBonus(true);
		player.getGameStats().addEffect(this, functions);
		log.info("[MAME-MINION][BUFF] player=" + player.getName() + " minionId=" + minionId + " classType=" + (player.isMagicalTypeClass() ? "magical" : "physical") + " attrs=" + debug.toString());
	}
	
	public void end(Player player) {
		player.setBonus(false);
		player.getGameStats().endEffect(this);
	}
}