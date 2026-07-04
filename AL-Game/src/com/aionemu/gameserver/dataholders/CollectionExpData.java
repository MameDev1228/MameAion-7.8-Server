package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.collection.CollectionExpTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionType;
import javolution.util.FastList;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "collection_exp_templates")
public class CollectionExpData {

    @XmlElement(name = "collection_exp_template")
    private List<CollectionExpTemplate> collectionExpTemplates;

    @XmlTransient
    private FastMap<CollectionType, List<CollectionExpTemplate>> templates = new FastMap<CollectionType, List<CollectionExpTemplate>>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (CollectionExpTemplate template : collectionExpTemplates) {
            if (templates.containsKey(template.getGrade())) {
                templates.get(template.getGrade()).add(template);
            } else {
                List<CollectionExpTemplate> exp = new FastList<CollectionExpTemplate>();
                exp.add(template);
                templates.put(template.getGrade(), exp);
            }
        }
    }

    public int size() {
        return templates.size();
    }

    public CollectionExpTemplate getTemplate(int level, CollectionType grade) {
        CollectionExpTemplate template = null;
        for (CollectionExpTemplate templates : this.templates.get(grade)) {
            if(templates.getLevel() == level) {
                template = templates;
            }
        }
        return template;
    }

}
