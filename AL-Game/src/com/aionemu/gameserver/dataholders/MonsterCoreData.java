package com.aionemu.gameserver.dataholders;


import com.aionemu.gameserver.model.templates.monster_core.MonsterCoreTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "monster_core_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonsterCoreData {

    @XmlElement(name = "monster_core_template")
    private List<MonsterCoreTemplate> tlist;
    private TIntObjectHashMap<MonsterCoreTemplate> mcData;
    private Map<Integer, MonsterCoreTemplate> mcDataMap;

    public MonsterCoreData() {
        this.mcData = new TIntObjectHashMap<MonsterCoreTemplate>();
        this.mcDataMap = new HashMap<Integer, MonsterCoreTemplate>(1);
    }
    void afterUnmarshal(final Unmarshaller u, final Object parent) {
        for (final MonsterCoreTemplate id : this.tlist) {
            this.mcData.put(id.getId(), id);
            this.mcDataMap.put(id.getId(), id);
        }
    }
    public MonsterCoreTemplate getMonsterCoreId(final int id) {
        return this.mcData.get(id);
    }
    public Map<Integer, MonsterCoreTemplate> getAll() {
        return this.mcDataMap;
    }

    public int size() {
        return mcData.size();
    }
}
