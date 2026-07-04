package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastList;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "transform_book_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransformBookData
{
    @XmlElement(name="transform_book_template")
    private List<TransformBookTemplate> tlist;

    private FastList<TransformBookTemplate> normal, greater, ancient, legendary, ultime;

    private List<TransformBookTemplate> rndTransform = new FastList<TransformBookTemplate>();

    private Map<Integer, Integer> transformSkill = new FastMap<Integer, Integer>();
	
    @XmlTransient
    private TIntObjectHashMap<TransformBookTemplate> transformBookData = new TIntObjectHashMap<TransformBookTemplate>();
	
    void afterUnmarshal(Unmarshaller paramUnmarshaller, Object paramObject) {
        for (TransformBookTemplate book : tlist) {
            transformBookData.put(book.getId(), book);
            rndTransform.add(book);
            transformSkill.put(book.getSkillId(), book.getId());
        }
    }
	
    public int size() {
        return transformBookData.size();
    }

    public TIntObjectHashMap<TransformBookTemplate> getAllBooks() {
        return transformBookData;
    }
    public TransformBookTemplate getTransformBookById(int id) {
        return transformBookData.get(id);
    }

    public List<TransformBookTemplate> getAllTransform() {
        return rndTransform;
    }

    public List<TransformBookTemplate> getAncient() {
        List<TransformBookTemplate> list = new FastList<>();
        for (TransformBookTemplate tp : transformBookData.valueCollection()) {
            if(tp.getGrade() == 1) {
                list.add(tp);
            }
        }
        return list;
    }

    public List<TransformBookTemplate> getGreater() {
        List<TransformBookTemplate> list = new FastList<>();
        for (TransformBookTemplate tp : transformBookData.valueCollection()) {
            if(tp.getGrade() == 2) {
                list.add(tp);
            }
        }
        return list;
    }

    public List<TransformBookTemplate> getLegendary() {
        List<TransformBookTemplate> list = new FastList<>();
        for (TransformBookTemplate tp : transformBookData.valueCollection()) {
            if(tp.getGrade() == 3) {
                list.add(tp);
            }
        }
        return list;
    }

    public List<TransformBookTemplate> getNormal() {
        List<TransformBookTemplate> list = new FastList<>();
        for (TransformBookTemplate tp : transformBookData.valueCollection()) {
            if(tp.getGrade() == 4) {
                list.add(tp);
            }
        }
        return list;
    }

    public List<TransformBookTemplate> getUltime() {
        List<TransformBookTemplate> list = new FastList<>();
        for (TransformBookTemplate tp : transformBookData.valueCollection()) {
            if(tp.getGrade() == 5) {
                list.add(tp);
            }
        }
        return list;
    }

    public Integer getTransformId(int skillId) {
        int transformId = 0;
        if(transformSkill.containsKey(skillId)) {
            transformId = transformSkill.get(skillId);
        }
        return transformId;
    }
}