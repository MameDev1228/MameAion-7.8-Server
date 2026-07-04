package com.aionemu.gameserver.model.templates.transform_book;

import com.aionemu.gameserver.model.stats.container.StatEnum;
import javolution.util.FastList;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CollectionRequired")
public class CollectionRequired {

    @XmlAttribute(name="ids")
    protected List<Integer> ids;

    public List<Integer> getIds() {
        return ids;
    }
}
