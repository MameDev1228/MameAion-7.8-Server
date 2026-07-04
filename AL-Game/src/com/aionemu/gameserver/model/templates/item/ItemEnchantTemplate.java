package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;

import gnu.trove.map.hash.TIntObjectHashMap;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemEnchantTemplate")
public class ItemEnchantTemplate
{
    @XmlAttribute(name = "id")
    private int id;

    @XmlAttribute(name = "type")
    private EnchantType type;
	
    @XmlElement(name = "item_enchant", required = false)
    private List<ItemEnchantBonus> item_enchant;
	
    @SuppressWarnings({"rawtypes", "unchecked"})
    @XmlTransient
    private TIntObjectHashMap<List<StatFunction>> enchants = new TIntObjectHashMap();
	
    public List<StatFunction> getStats(int level) {
        if (getItemEnchant() == null) {
            return null;
		}
        for (ItemEnchantBonus ib: getItemEnchant()) {
            if (ib.getLevel() != level) {
                continue;
			}
            return ib.getModifiers();
        }
        return null;
    }

    /**
     * Returns the best available stat row for the requested enchant/authorize level.
     * 7.x/CC2 item tables often contain levels beyond 15 and non-PVE/PVP types such as
     * DESTRUCTION. Falling back to hard-coded 15 silently drops Extreme gear bonuses.
     */
    public List<StatFunction> getStatsClosest(int level) {
        int resolvedLevel = getClosestStatLevel(level);
        return resolvedLevel > 0 ? getStats(resolvedLevel) : null;
    }

    public int getClosestStatLevel(int level) {
        if (level <= 0 || getItemEnchant() == null) {
            return 0;
        }
        int closest = 0;
        for (ItemEnchantBonus ib : getItemEnchant()) {
            if (ib == null || ib.getModifiers() == null) {
                continue;
            }
            int rowLevel = ib.getLevel();
            if (rowLevel == level) {
                return rowLevel;
            }
            if (rowLevel <= level && rowLevel > closest) {
                closest = rowLevel;
            }
        }
        return closest;
    }

    public int getMaxStatLevel() {
        int max = 0;
        if (getItemEnchant() == null) {
            return 0;
        }
        for (ItemEnchantBonus ib : getItemEnchant()) {
            if (ib != null && ib.getModifiers() != null && ib.getLevel() > max) {
                max = ib.getLevel();
            }
        }
        return max;
    }
	
    public List<ItemEnchantBonus> getItemEnchant() {
        return this.item_enchant;
    }
	
    public int getId() {
        return this.id;
    }

    public EnchantType getType() {
        return type;
    }
}