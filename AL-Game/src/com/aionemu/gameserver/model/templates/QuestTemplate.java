package com.aionemu.gameserver.model.templates;

import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.quest.*;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quest")

public class QuestTemplate
{
	@XmlElement(name = "collect_items")
	protected CollectItems collectItems;
	@XmlElement(name = "inventory_items")
	protected InventoryItems inventoryItems;
	@XmlElement(name = "rewards")
	protected List<Rewards> rewards;
	@XmlElement(name = "bonus")
    protected List<QuestBonuses> bonus;
	@XmlElement(name = "extended_rewards")
	protected List<Rewards> extendedRewards;
	@XmlElement(name = "quest_drop")
	protected List<QuestDrop> questDrop;
	@XmlElement(name = "quest_kill")
	protected List<QuestKill> questKill;
	@XmlElement(name = "start_conditions")
	protected List<XMLStartCondition> startConds;
	@XmlList
	@XmlElement(name = "class_permitted")
	protected List<PlayerClass> classPermitted;
	@XmlElement(name = "gender_permitted")
	protected Gender genderPermitted;
	@XmlElement(name = "quest_work_items")
	protected QuestWorkItems questWorkItems;
	@XmlElement(name = "fighter_selectable_reward")
	protected List<QuestItems> fighterSelectableReward;
	@XmlElement(name = "knight_selectable_reward")
	protected List<QuestItems> knightSelectableReward;
	@XmlElement(name = "ranger_selectable_reward")
	protected List<QuestItems> rangerSelectableReward;
	@XmlElement(name = "assassin_selectable_reward")
	protected List<QuestItems> assassinSelectableReward;
	@XmlElement(name = "wizard_selectable_reward")
	protected List<QuestItems> wizardSelectableReward;
	@XmlElement(name = "elementalist_selectable_reward")
	protected List<QuestItems> elementalistSelectableReward;
	@XmlElement(name = "priest_selectable_reward")
	protected List<QuestItems> priestSelectableReward;
	@XmlElement(name = "chanter_selectable_reward")
	protected List<QuestItems> chanterSelectableReward;
	@XmlElement(name = "gunslinger_selectable_reward")
	protected List<QuestItems> gunslingerSelectableReward;
	@XmlElement(name = "songweaver_selectable_reward")
	protected List<QuestItems> songweaverSelectableReward;
	@XmlElement(name = "aethertech_selectable_reward")
	protected List<QuestItems> aethertechSelectableReward;
	@XmlElement(name = "vandal_selectable_reward")
	protected List<QuestItems> vandalSelectableReward;
	
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "nameId")
	protected Integer nameId;
	@XmlAttribute(name = "minlevel_permitted")
	protected Integer minlevelPermitted;
	@XmlAttribute(name = "maxlevel_permitted")
	protected int maxlevelPermitted;
	@XmlAttribute(name = "max_repeat_count")
	protected Integer maxRepeatCount;
	@XmlAttribute(name = "quest_cooltime")
	protected int questCooltime;
	@XmlAttribute(name = "rank")
	private int rank;
	@XmlAttribute(name = "max_count_limited_quest")
	protected Integer maxCountLimitedQuest;
	@XmlAttribute(name = "count_recover_limited_quest")
	protected Integer countRecoverLimitedQuest;
	@XmlAttribute(name = "cannot_share")
	protected Boolean cannotShare;
	@XmlAttribute(name = "cannot_giveup")
	protected Boolean cannotGiveup;
	@XmlAttribute(name = "can_report")
    protected Boolean canReport;
	@XmlAttribute(name = "tutorial")
	protected Boolean tutorial;
	@XmlAttribute(name = "use_class_reward")
	protected Integer useClassReward;
	@XmlAttribute(name = "race_permitted")
	protected Race racePermitted;
	@XmlAttribute(name = "combineskill")
	protected Integer combineskill;
	@XmlAttribute(name = "combine_skillpoint")
	protected Integer combineSkillpoint;
	@XmlAttribute(name = "timer")
	protected Boolean timer;
	@XmlAttribute(name = "category")
	protected QuestCategory category;
	@XmlAttribute(name = "repeat_cycle")
	protected List<QuestRepeatCycle> repeatCycle;
	@XmlAttribute(name = "npcfaction_id")
	protected int npcFactionId;
	@XmlAttribute(name = "mentor_type")
	protected QuestMentorType mentorType = QuestMentorType.NONE;
	@XmlAttribute(name = "target_type")
	private QuestTargetType targetType = QuestTargetType.NONE;
	@XmlAttribute(name = "titleId")
	protected int titleId;

	@XmlAttribute(name = "random_reward")
	protected int randomReward;
	
	public CollectItems getCollectItems() {
		return collectItems;
	}

	public InventoryItems getInventoryItems() {
		return inventoryItems;
	}

	public List<Rewards> getRewards() {
		if (rewards == null) {
			rewards = new ArrayList<Rewards>();
		}
		return this.rewards;
	}

	public List<Rewards> getExtendedRewards() {
		if (extendedRewards == null) {
			extendedRewards = new ArrayList<Rewards>();
		}
		return this.extendedRewards;
	}

	public List<QuestBonuses> getBonus() {
		if (bonus == null) {
			bonus = new ArrayList<QuestBonuses>();
		}
		return this.bonus;
	}

	public List<QuestDrop> getQuestDrop() {
		if (questDrop == null) {
			questDrop = new ArrayList<QuestDrop>();
		}
		return this.questDrop;
	}

	public List<QuestKill> getQuestKill() {
		if (questKill == null) {
			questKill = new ArrayList<QuestKill>();
		}
		return this.questKill;
	}

	public List<XMLStartCondition> getXMLStartConditions() {
		if (startConds == null) {
			startConds = new ArrayList<XMLStartCondition>();
		}
		return startConds;
	}

	public List<PlayerClass> getClassPermitted() {
		if (classPermitted == null) {
			classPermitted = new ArrayList<PlayerClass>();
		}
		return this.classPermitted;
	}

	public Gender getGenderPermitted() {
		return genderPermitted;
	}

	public QuestWorkItems getQuestWorkItems() {
		return questWorkItems;
	}

	public List<QuestItems> getFighterSelectableReward() {
		if (fighterSelectableReward == null) {
			fighterSelectableReward = new ArrayList<QuestItems>();
		}
		return this.fighterSelectableReward;
	}

	public List<QuestItems> getKnightSelectableReward() {
		if (knightSelectableReward == null) {
			knightSelectableReward = new ArrayList<QuestItems>();
		}
		return this.knightSelectableReward;
	}

	public List<QuestItems> getRangerSelectableReward() {
		if (rangerSelectableReward == null) {
			rangerSelectableReward = new ArrayList<QuestItems>();
		}
		return this.rangerSelectableReward;
	}

	public List<QuestItems> getAssassinSelectableReward() {
		if (assassinSelectableReward == null) {
			assassinSelectableReward = new ArrayList<QuestItems>();
		}
		return this.assassinSelectableReward;
	}

	public List<QuestItems> getWizardSelectableReward() {
		if (wizardSelectableReward == null) {
			wizardSelectableReward = new ArrayList<QuestItems>();
		}
		return this.wizardSelectableReward;
	}

	public List<QuestItems> getElementalistSelectableReward() {
		if (elementalistSelectableReward == null) {
			elementalistSelectableReward = new ArrayList<QuestItems>();
		}
		return this.elementalistSelectableReward;
	}

	public List<QuestItems> getPriestSelectableReward() {
		if (priestSelectableReward == null) {
			priestSelectableReward = new ArrayList<QuestItems>();
		}
		return this.priestSelectableReward;
	}

	public List<QuestItems> getChanterSelectableReward() {
		if (chanterSelectableReward == null) {
			chanterSelectableReward = new ArrayList<QuestItems>();
		}
		return this.chanterSelectableReward;
	}

	public List<QuestItems> getGunslingerSelectableReward() {
		if (gunslingerSelectableReward == null) {
			gunslingerSelectableReward = new ArrayList<QuestItems>();
		}
		return this.gunslingerSelectableReward;
	}

	public List<QuestItems> getSongweaverSelectableReward() {
		if (songweaverSelectableReward == null) {
			songweaverSelectableReward = new ArrayList<QuestItems>();
		}
		return this.songweaverSelectableReward;
	}

	public List<QuestItems> getAethertechSelectableReward() {
		if (aethertechSelectableReward == null) {
			aethertechSelectableReward = new ArrayList<QuestItems>();
		}
		return this.aethertechSelectableReward;
	}
	
	public List<QuestItems> getVandalSelectableReward() {
		if (vandalSelectableReward == null) {
			vandalSelectableReward = new ArrayList<QuestItems>();
		}
		return this.vandalSelectableReward;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getNameId() {
		return nameId;
	}

	public Integer getMinlevelPermitted() {
		return minlevelPermitted;
	}

	public int getMaxlevelPermitted() {
		return maxlevelPermitted;
	}

	public int getRequiredRank() {
		return rank;
	}

	public Integer getMaxRepeatCount() {
		if (maxRepeatCount == null || !(maxRepeatCount > 1)) {
			return 1;
		}
		return maxRepeatCount;
	}

	public Integer getMaxCountLimitedQuest() {
		if (maxCountLimitedQuest == null || !(maxCountLimitedQuest > 1)) {
			return 1;
		}
		return maxCountLimitedQuest;
	}

	public Integer getCountRecoverLimitedQuest() {
		if (countRecoverLimitedQuest == null || !(countRecoverLimitedQuest > 1)) {
			return 1;
		}
		return countRecoverLimitedQuest;
	}

	public boolean isCannotShare() {
		if (cannotShare == null) {
			return false;
		} else {
			return cannotShare;
		}
	}

	public boolean isCannotGiveup() {
		if (cannotGiveup == null) {
			return false;
		} else {
			return cannotGiveup;
		}
	}

	public boolean isCanReport() {
        if (canReport == null) {
            return false;
        } else {
            return canReport;
        }
    }

	public boolean isTutorial() {
		if (tutorial == null) {
			return false;
		} else {
			return tutorial;
		}
	}

	public boolean isUseSingleClassReward() {
		if (useClassReward == null) {
			return false;
		} else {
			return useClassReward == 1;
		}
	}
	
	public boolean isUseRepeatedClassReward() {
		if (useClassReward == null) {
			return false;
		} else {
			return useClassReward == 2;
		}
	}

	public boolean isRepeatable() {
		return getMaxRepeatCount() > 1;
	}

	public Race getRacePermitted() {
		return racePermitted;
	}

	public Integer getCombineSkill() {
		return combineskill;
	}

	public Integer getCombineSkillPoint() {
		return combineSkillpoint;
	}

	public boolean isTimer() {
		if (timer == null) {
			return false;
		} else {
			return timer;
		}
	}

	public QuestCategory getCategory() {
		if (category == null) {
			category = QuestCategory.QUEST;
		}
		return category;
	}

	public boolean isMentor() {
		return mentorType != QuestMentorType.NONE;
	}

	public QuestMentorType getMentorType() {
		return mentorType;
	}

	public QuestTargetType getTargetType() {
		return targetType;
	}

	public List<QuestRepeatCycle> getRepeatCycle() {
		return repeatCycle;
	}

	public int getTitleId() {
        return titleId;
    }

	public int getNpcFactionId() {
		return npcFactionId;
	}

	public boolean isTimeBased() {
		return repeatCycle != null;
	}

	public int getQuestCoolTime() {
		return questCooltime;
	}

	public boolean isDaily() {
		return isTimeBased() && repeatCycle.size() == 1 && repeatCycle.get(0) == QuestRepeatCycle.ALL;
	}

	public boolean isWeekly() {
		return isTimeBased() && !isDaily();
	}

	public boolean isMaster() {
		return getCombineSkillPoint() != null && getCombineSkillPoint() == 499;
	}

	public boolean isExpert() {
		return getCombineSkillPoint() != null && getCombineSkillPoint() == 399;
	}

	public boolean isNoCount() {
	   return category.equals(QuestCategory.NON_COUNT) || category.equals(QuestCategory.EVENT);
    }

	public Boolean getTutorial() {
		return tutorial;
	}
	
	public int getRandomReward() {
		return randomReward;
	}
}