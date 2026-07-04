package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.item.DecomposableItemInfo;
import com.aionemu.gameserver.model.templates.item.DecomposeStuffTemplate;
import com.aionemu.gameserver.model.templates.item.ExtractedItemsCollection;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "decompose_stuff_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecomposeStuffData {

    @XmlElement(name = "decompose_stuff_template")
    private List<DecomposeStuffTemplate> decomposeStuffTemplates;
    private TIntObjectHashMap<DecomposeStuffTemplate> decomposeStuffs = new TIntObjectHashMap<DecomposeStuffTemplate>();


    void afterUnmarshal(Unmarshaller u, Object parent) {
        decomposeStuffs.clear();
        for (DecomposeStuffTemplate template : decomposeStuffTemplates)
            decomposeStuffs.put(template.getId(), template);
    }

    public int size() {
        return decomposeStuffs.size();
    }

    public DecomposeStuffTemplate getDecomposeStuffById(int itemId) {
        return decomposeStuffs.get(itemId);
    }
}
