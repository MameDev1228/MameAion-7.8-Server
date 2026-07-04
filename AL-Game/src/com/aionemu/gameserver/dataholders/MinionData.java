package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.minion.MinionTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "minions")
public class MinionData
{
	@XmlElement(name = "minion")
    private List<MinionTemplate> minionTemplates;
	
	@XmlTransient
	private TIntObjectHashMap<MinionTemplate> minionData = new TIntObjectHashMap<MinionTemplate>();
	
	@XmlTransient
	private List<Integer> minionDataList = new ArrayList<Integer>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (MinionTemplate minion : minionTemplates) {
			minionData.put(minion.getId(), minion);
			minionDataList.add(minion.getId());
		}
		minionTemplates.clear();
		minionTemplates = null;
	}

	public int size() {
		return minionData.size();
	}
	
	public MinionTemplate getMinionTemplate(int id) {
		return minionData.get(id);
	}
	
	public List<Integer> getAll() {
		return minionDataList;
	}
}