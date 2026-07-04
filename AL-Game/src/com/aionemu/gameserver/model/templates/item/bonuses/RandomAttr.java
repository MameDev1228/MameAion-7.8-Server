/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.item.bonuses;

import com.aionemu.gameserver.model.stats.container.StatEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Krz on décembre 2018
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RandomAttr")
public class RandomAttr {

    @XmlAttribute(name = "stat_name")
    protected StatEnum stat_name;
    @XmlAttribute(name = "min_value")
    protected int min_value;
    @XmlAttribute(name = "max_value")
    protected int max_value;

    public StatEnum getStatName() {
        return stat_name;
    }

    public int getMaxValue() {
        return max_value;
    }

    public int getMinValue() {
        return min_value;
    }
}
