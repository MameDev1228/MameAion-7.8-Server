package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielMaterialTemplate;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lumiel_material_templates")
public class LumielMaterialData {

    @XmlElement(name = "lumiel_material_template")
    private List<LumielMaterialTemplate> materialTemplate;

    @XmlTransient
    private FastMap<Integer, LumielMaterialTemplate> templates = new FastMap<Integer, LumielMaterialTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (LumielMaterialTemplate template : materialTemplate) {
            templates.put(template.getId(), template);
        }
    }

    public int size() {
        return templates.size();
    }

    public LumielMaterialTemplate getTemplate(int lumielId, int itemId) {
        LumielMaterialTemplate lumiel = null;
        for (LumielMaterialTemplate template : templates.values()) {
            if(template.getLumielId() == lumielId && template.getItemId() == itemId) {
                lumiel = template;
            }
        }
        return lumiel;
    }
}
