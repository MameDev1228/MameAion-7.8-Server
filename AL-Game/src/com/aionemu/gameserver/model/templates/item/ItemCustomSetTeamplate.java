package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="ItemCustomSetTeamplate")
public class ItemCustomSetTeamplate
{
    @XmlAttribute(name = "id")
    private Integer id;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "custom_enchant_value")
    private int custom_enchant_value;

    @XmlAttribute(name = "custom_authorize_value")
    private int custom_authorize_value;

    @XmlElement(name = "mana_stone")
    private List<Integer> mana_stone;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getEnchantLevel() {
        return custom_enchant_value;
    }

    public int getAuthorizeLevel() {
        return custom_authorize_value;
    }

    public List<Integer> getManaStone() {
        return mana_stone;
    }
}