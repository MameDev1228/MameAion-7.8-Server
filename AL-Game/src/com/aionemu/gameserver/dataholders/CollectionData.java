package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.collection.CollectionTemplate;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformTemplate;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "collection_templates")
public class CollectionData {

    @XmlElement(name = "collection_template")
    private List<CollectionTemplate> collectionTemplates;

    @XmlTransient
    private FastMap<Integer, CollectionTemplate> templates = new FastMap<Integer, CollectionTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (CollectionTemplate template : collectionTemplates) {
            templates.put(template.getId(), template);
        }
    }

    public int size() {
        return templates.size();
    }

    public CollectionTemplate getTemplate(int lumielId) {
        return templates.get(lumielId);
    }

    public Map<Integer,CollectionTemplate> getAllTemplates() {
        return templates;
    }
}
