package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.grind.GrindCombine;
import com.aionemu.gameserver.model.templates.luna.LunaBonusTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "grind_combines")
@XmlAccessorType(XmlAccessType.FIELD)
public class GrindCombineData {

    @XmlElement(name = "grind_combine")
    private List<GrindCombine> tlist;

    public GrindCombine getCombine(Player player, int color1, int color2) {
        GrindCombine result = null;
        for (GrindCombine cmb : tlist) {
            if (cmb.getPlayerClass() == player.getPlayerClass() && cmb.getColor1() == color1 && cmb.getColor2() == color2) {
                result = cmb;
            }
        }
        return result;
    }

    public int size() {
        return tlist.size();
    }
}
