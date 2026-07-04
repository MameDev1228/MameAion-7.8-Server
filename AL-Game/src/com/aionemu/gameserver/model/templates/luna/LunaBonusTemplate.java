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
package com.aionemu.gameserver.model.templates.luna;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Krz on décembre 2018
 */

@XmlType(name = "luna_bonusattr")
@XmlAccessorType(XmlAccessType.NONE)
public class LunaBonusTemplate {

    @XmlElement(name = "bonus_attr")
    protected List<LunaBonusAttr> bonusAttr;

    @XmlAttribute(name = "buff_id", required = true)
    protected int buffId;

    public List<LunaBonusAttr> getPenaltyAttr() {
        if (bonusAttr == null) {
            bonusAttr = new ArrayList<LunaBonusAttr>();
        }
        return bonusAttr;
    }

    public int getBuffId() {
        return buffId;
    }
}
