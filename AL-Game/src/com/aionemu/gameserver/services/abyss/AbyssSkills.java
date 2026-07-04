package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import org.slf4j.LoggerFactory;

enum AbyssSkills
{
	//Elyos.
	SUPREME_COMMANDER_E(Race.ELYOS, AbyssRankEnum.SUPREME_COMMANDER, new int[] {4846, 4852, 4855, 4858, 4861, 11889, 11898, 11900, 11903, 11904, 11905, 11906}),
	COMMANDER_E(Race.ELYOS, AbyssRankEnum.COMMANDER, new int[] {4845, 4852, 4855, 4858, 4861, 11888, 11898, 11900, 11903, 11904}),
	GREAT_GENERAL_E(Race.ELYOS, AbyssRankEnum.GREAT_GENERAL, new int[] {4845, 4851, 4854, 4857, 4860, 11887, 11897, 11899, 11903}),
	GENERAL_E(Race.ELYOS, AbyssRankEnum.GENERAL, new int[] {4845, 4851, 4854, 4857, 4860, 11886, 11896, 11899}),
	STAR5_OFFICER_E(Race.ELYOS, AbyssRankEnum.STAR5_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859, 11885, 11895}),
	STAR4_OFFICER_E(Race.ELYOS, AbyssRankEnum.STAR4_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859}),
	STAR3_OFFICER_E(Race.ELYOS, AbyssRankEnum.STAR3_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859}),
	STAR2_OFFICER_E(Race.ELYOS, AbyssRankEnum.STAR2_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859}),
	STAR1_OFFICER_E(Race.ELYOS, AbyssRankEnum.STAR1_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859}),
	
	//Asmodians.
	SUPREME_COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.SUPREME_COMMANDER, new int[] {4849, 4852, 4855, 4858, 4861, 11894, 11898, 11902, 11903, 11904, 11905, 11906}),
	COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.COMMANDER, new int[] {4845, 4852, 4855, 4858, 4861, 11893, 11898, 11902, 11903, 11904}),
	GREAT_GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GREAT_GENERAL, new int[] {4845, 4851, 4854, 4857, 4860, 11892, 11897, 11901, 11903}),
	GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GENERAL, new int[] {4845, 4851, 4854, 4857, 4860, 11891, 11896, 11901}),
	STAR5_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR5_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859, 11890, 11895}),
	STAR4_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR4_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859}),
	STAR3_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR3_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859}),
	STAR2_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR2_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859}),
	STAR1_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR1_OFFICER, new int[] {4844, 4850, 4853, 4856, 4859});
	
	private int[] skills;
	private AbyssRankEnum rankenum;
	private Race race;
	
	private AbyssSkills(Race race, AbyssRankEnum rankEnum, int[] skills) {
		this.race = race;
		rankenum = rankEnum;
		this.skills = skills;
	}
	
	public Race getRace() {
		return race;
	}
	
	public int[] getSkills() {
		return skills;
	}
	
	public static int[] getSkills(Race race, AbyssRankEnum rank) {
		for (AbyssSkills aSkills : values()) {
			if ((aSkills.race == race) && (aSkills.rankenum == rank)) {
				return aSkills.skills;
			}
		}
		LoggerFactory.getLogger(AbyssSkills.class).warn("No abyss skills for: " + race + " " + rank);
		return new int[0];
	}
}