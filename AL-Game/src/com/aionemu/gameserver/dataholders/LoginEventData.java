package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.loginevents.LoginEventTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "atreian_passport_templates")
public class LoginEventData {

    @XmlElement(name = "atreian_passport_template")
    private List<LoginEventTemplate> loginEvents;

    @XmlTransient
    private TIntObjectHashMap<LoginEventTemplate> templates = new TIntObjectHashMap<LoginEventTemplate>();

    @XmlTransient
    private Map<Integer, LoginEventTemplate> templatesMap = new HashMap<Integer, LoginEventTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (LoginEventTemplate template : loginEvents) {
            templates.put(template.getId(), template);
            templatesMap.put(template.getId(), template);
        }
        loginEvents.clear();
        loginEvents = null;
    }

    public int size() {
        return templates.size();
    }

    public LoginEventTemplate getLoginEvent(int rewardId) {
        return templates.get(rewardId);
    }
}
