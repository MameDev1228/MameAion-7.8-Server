package com.aionemu.gameserver.model.templates.minion;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * @author Ranastic
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(namespace = "", name = "MinionTemplate")
public class MinionTemplate
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	
	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "nameid")
	private int name_id;
	
	@XmlAttribute(name = "grade")
	private String grade;
	
	@XmlAttribute(name = "grade_id")
	private int gradeId;
	
	@XmlAttribute(name = "level")
	private int level;
	
	@XmlAttribute(name = "growthPoints")
	private int growthPoints;
	
	@XmlAttribute(name = "growthMax")
	private int growthMax;
	
	@XmlAttribute(name = "growthCost")
	private int growthCost;
	
	@XmlElement(name = "modifiers", required = false)
	private ModifiersTemplate modifiers;
	
	@XmlElement(name = "actions")
    private MinionActions actions;
	
	@XmlElement(name = "minionstats")
	private MinionStatsTemplate statsTemplate;
	
	@XmlElement(name = "bound")
	private BoundRadius bound;

	@XmlElement(name = "evolved")
	private MinionEvolved evolved;

	@XmlElement(name = "nameId")
	private int nameId;
	
	@XmlElement(name="physical_attr")
    protected List<MinionAttr> physicalAttr;
	
    @XmlElement(name="magical_attr")
    protected List<MinionAttr> magicalAttr;
	
	public List<MinionAttr> getPhysicalAttr() {
        if (this.physicalAttr == null) {
            this.physicalAttr = new ArrayList<MinionAttr>();
        }
        return this.physicalAttr;
    }
	
    public List<MinionAttr> getMagicalAttr() {
        if (this.magicalAttr == null) {
            this.magicalAttr = new ArrayList<MinionAttr>();
        }
        return this.magicalAttr;
    }
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGrade() {
		return grade;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getGrowthPoints() {
		return growthPoints;
	}
	
	public int getGrowthMax() {
		return growthMax;
	}
	
	public int getGrowthCost() {
		return growthCost;
	}
	
	public BoundRadius getBoundRadius() {
		return bound;
	}
	
	public MinionEvolved getEvolved() {
		return evolved;
	}
	
	public MinionStatsTemplate getStatsTemplate() {
		return statsTemplate;
	}
	
	public int getNameId() {
		return nameId;
	}
	
	public int getGradeId() {
		return gradeId;
	}
	
	public List<StatFunction> getModifiers() {
		if (modifiers != null) {
			return modifiers.getModifiers();
		}
		return null;
	}
	
	public MinionActions getAction() {
        return actions;
    }
}