package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "DecomposeStuffTemplate")
public class DecomposeStuffTemplate {

    @XmlElement(name = "decompse_item")
    private List<DecomposeStuffResult> decomposeStuffResults;

    @XmlAttribute(name = "id")
    private int id;

    public List<DecomposeStuffResult> getDecomposeStuffResults() {
        return decomposeStuffResults;
    }

    public int getId() {
        return id;
    }
}
