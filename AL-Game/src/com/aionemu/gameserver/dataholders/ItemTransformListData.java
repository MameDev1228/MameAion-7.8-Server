package com.aionemu.gameserver.dataholders;


import com.aionemu.gameserver.model.templates.ItemTransormList;
import com.aionemu.gameserver.model.templates.item.ItemMinionList;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="transforms_list")
public class ItemTransformListData {

    @XmlElement(name = "transform_list", required = true)
    protected List<ItemTransormList> minionlist;

    @XmlTransient
    private TIntObjectHashMap<ItemTransormList> custom = new TIntObjectHashMap<ItemTransormList>();

    public ItemTransormList getTransformList(int id) {
        return custom.get(id);
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (ItemTransormList it : minionlist) {
            getCustomMap().put(it.getId(), it);
        }
    }

    private TIntObjectHashMap<ItemTransormList> getCustomMap() {
        return custom;
    }

    public int size() {
        return custom.size();
    }
}
