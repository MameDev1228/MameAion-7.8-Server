package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformTemplate;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lumiel_templates")
public class LumielTemplateData {

    @XmlElement(name = "lumiel_template")
    private List<LumielTransformTemplate> lumielTransformTemplates;

    @XmlTransient
    private FastMap<Integer, LumielTransformTemplate> templates = new FastMap<Integer, LumielTransformTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (LumielTransformTemplate template : lumielTransformTemplates) {
            templates.put(template.getId(), template);
        }
    }

    public int size() {
        return templates.size();
    }

    public LumielTransformTemplate getTemplate(int lumielId) {
        return templates.get(lumielId);
    }

    public Map<Integer,LumielTransformTemplate> getAllTemplates() {
        return templates;
    }
}
