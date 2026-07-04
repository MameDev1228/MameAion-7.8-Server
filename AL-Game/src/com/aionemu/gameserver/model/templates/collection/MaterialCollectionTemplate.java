package com.aionemu.gameserver.model.templates.collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaterialCollectionTemplate")
public class MaterialCollectionTemplate {

    @XmlAttribute(name="items")
    protected List<Integer> items;

    @XmlAttribute(name = "count")
    protected int count;

    @XmlAttribute(name = "enchant")
    protected int enchant;

    public List<Integer> getItems() {
        return items;
    }

    public int getCount() {
        return count;
    }

    public int getEnchant() {
        return enchant;
    }
}
