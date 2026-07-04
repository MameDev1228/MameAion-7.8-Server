package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.luna.LunaDicesTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Wnkrz on 26/07/2017.
 */

@XmlRootElement(name = "luna_dices_items")
@XmlAccessorType(XmlAccessType.FIELD)
public class LunaDicesData
{
    @XmlElement(name = "luna_dices_item")
    protected List<LunaDicesTemplate> luna_dices_item;
	
    public LunaDicesData() {
    }
	
    private TIntObjectHashMap<LunaDicesTemplate> lunaDiceData = new TIntObjectHashMap();
	
    void afterUnmarshal(Unmarshaller u, Object parent) {
        lunaDiceData.clear();
        for (LunaDicesTemplate lunaDicesTemplate : luna_dices_item) {
            lunaDiceData.put(lunaDicesTemplate.getId(), lunaDicesTemplate);
        }
    }
	
    public int size() {
        return lunaDiceData.size();
    }
	
    public LunaDicesTemplate getId(int id) {
        return lunaDiceData.get(id);
    }
	
    public List<LunaDicesTemplate> getLunaDicesItem() {
        return luna_dices_item;
    }
}