package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.ExpTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "player_experience_table")
@XmlAccessorType(XmlAccessType.NONE)
public class PlayerExperienceTable
{
	@XmlElement(name = "exp")
	private List<ExpTemplate> expTemp;
	private TIntObjectHashMap<ExpTemplate> exp;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		exp = new TIntObjectHashMap<ExpTemplate>();
		for (ExpTemplate expT : expTemp) {
			exp.put(expT.getLevel(), expT);
		}
		expTemp = null;
	}

	public ExpTemplate getExpTemplate(int level) {
		return exp.get(level);
	}

	public long getStartExpForLevel(int level) {
		if (level > exp.size()) {
			throw new IllegalArgumentException("The given level is higher than possible max");
		}
		return level == 0 ? 0 : exp.get(level).getExp();
	}

	public int getMaxLevel() {
		return exp == null ? 0 : exp.size();
	}
}