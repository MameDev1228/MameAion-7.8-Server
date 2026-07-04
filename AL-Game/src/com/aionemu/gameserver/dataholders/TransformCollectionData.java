package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;
import com.aionemu.gameserver.model.templates.transform_book.TransformCollectionTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "transform_collection_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransformCollectionData {

    @XmlElement(name="transform_collection_template")
    private List<TransformCollectionTemplate> tlist;

    @XmlTransient
    private Map<Integer, TransformCollectionTemplate> transCollectionData = new FastMap<Integer, TransformCollectionTemplate>();

    void afterUnmarshal(Unmarshaller paramUnmarshaller, Object paramObject) {
        for (TransformCollectionTemplate book : tlist) {
            transCollectionData.put(book.getId(), book);
        }
    }

    public int size() {
        return transCollectionData.size();
    }

    public Map<Integer, TransformCollectionTemplate> getAllCollection() {
        return transCollectionData;
    }

    public TransformCollectionTemplate getTransformCollectionById(int id) {
        return transCollectionData.get(id);
    }
}
