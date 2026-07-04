package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="ItemTransormList")
public class ItemTransormList {

    @XmlAttribute(name="id")
    protected int id;

    @XmlAttribute(name="transform_id")
    protected List<Integer> transformId;

    public int getId() {
        return this.id;
    }

    public List<Integer> getTransformId() {
        if (transformId == null) {
            transformId = new ArrayList<Integer>();
        }
        return transformId;
    }
}
