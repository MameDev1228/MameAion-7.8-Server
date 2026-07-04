package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;

@XmlRootElement(name = "skill_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillData
{
	@XmlElement(name = "skill_template")
	private List<SkillTemplate> skillTemplates;
	
	private HashMap<Integer, ArrayList<Integer>> cooldownGroups;
	private HashMap<String, ArrayList<SkillTemplate>> skillTemplatesByGroups = new HashMap<String, ArrayList<SkillTemplate>>();
	private HashSet<String> chainPrecat = new HashSet<String>();
	
	private TIntObjectHashMap<SkillTemplate> skillData = new TIntObjectHashMap<SkillTemplate>();
	
	private final Map<String, SkillTemplate> skillGroup = new FastMap<String, SkillTemplate>().shared();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
        skillData.clear();
        skillGroup.clear();
        skillTemplatesByGroups.clear();
        chainPrecat.clear();
        cooldownGroups = null;
        for (SkillTemplate st: skillTemplates) {
            skillData.put(st.getSkillId(), st);
            skillGroup.put(st.getStack().replace("SKILL_", ""), st);
            String group = st.getGroup();
            if (group != null && !group.isEmpty() && !"NONE".equals(group)) {
                ArrayList<SkillTemplate> groupSkills = skillTemplatesByGroups.get(group);
                if (groupSkills == null) {
                    groupSkills = new ArrayList<SkillTemplate>();
                    skillTemplatesByGroups.put(group, groupSkills);
                }
                groupSkills.add(st);
            }
            if (st.getChainCondition() != null && st.getChainCondition().getPreCategory() != null
                    && !st.getChainCondition().getPreCategory().isEmpty()) {
                chainPrecat.add(st.getChainCondition().getPreCategory().toUpperCase());
            }
        }
    }
	
	public SkillTemplate getSkillTemplate(int skillId) {
        return skillData.get(skillId);
    }
	
	public int size() {
        return skillData.size();
    }
	
	public SkillTemplate getSkillTemplateByGroup(String name) {
        return skillGroup.get(name);
    }

	public List<SkillTemplate> getSkillTemplate(String group) {
        ArrayList<SkillTemplate> skills = skillTemplatesByGroups.get(group);
        return skills != null ? skills : new ArrayList<SkillTemplate>();
    }

	public boolean haveChainPrecat(String precat) {
        return precat != null && chainPrecat.contains(precat.toUpperCase());
    }
	
	public int sizeOfGroup() {
        return skillGroup.size();
    }
	
	public List<SkillTemplate> getSkillTemplates() {
        return skillTemplates;
    }
	
	public void setSkillTemplates(List<SkillTemplate> skillTemplates) {
        this.skillTemplates = skillTemplates;
        afterUnmarshal(null, null);
    }
	
	public void initializeCooldownGroups() {
        cooldownGroups = new HashMap<Integer, ArrayList<Integer>>();
        for (SkillTemplate skillTemplate: skillTemplates) {
            int delayId = skillTemplate.getDelayId();
            if (!cooldownGroups.containsKey(delayId)) {
                cooldownGroups.put(delayId, new ArrayList<Integer>());
            }
            cooldownGroups.get(delayId).add(skillTemplate.getSkillId());
        }
    }
	
	public ArrayList<Integer> getSkillsForDelayId(int delayId) {
        if (cooldownGroups == null) {
            initializeCooldownGroups();
        }
        return cooldownGroups.get(delayId);
    }

	/**
	 * ArchSoft-compatible player-aware cooldown lookup. This keeps delayId/cooldownId
	 * groups from serializing skills the client does not actually own. Runtime callers
	 * can opt in gradually without changing the authoritative cooldown map.
	 */
	public ArrayList<Integer> getSkillsForDelayId(int delayId, Player player) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (player == null || player.getSkillList() == null) {
            ArrayList<Integer> allSkills = getSkillsForDelayId(delayId);
            return allSkills != null ? allSkills : result;
        }
        for (PlayerSkillEntry skill : player.getSkillList().getAllSkills()) {
            if (skill == null) {
                continue;
            }
            SkillTemplate template = getSkillTemplate(skill.getSkillId());
            if (template != null && template.getDelayId() == delayId) {
                result.add(skill.getSkillId());
            }
        }
        return result;
    }
	
	public TIntObjectHashMap<SkillTemplate> getSkillData() {
        return skillData;
    }
}