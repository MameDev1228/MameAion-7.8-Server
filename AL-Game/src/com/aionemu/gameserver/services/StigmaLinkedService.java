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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

import java.util.*;

/**
 * @author Rinzler
 */

public class StigmaLinkedService
{
	public static void onLogOut(Player player) {
		StigmaLinkedService.DeleteLinkedSkills(player);
		player.setStigmaSet(0);
	}
	
	public static void checkEquipConditions(Player player, List<Integer> list) {
		boolean check = false;
		for (Integer Stigma: list) {
			ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(Stigma);
			if (it.getName().contains("(Inert)") ||
			    it.getName().contains("stigma_a") ||
				it.getName().contains("STIGMA_NE_")) {
				check = true;
			}
		} switch (player.getPlayerClass()) {
			case GLADIATOR:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001119) && list.contains(140001107) && list.contains(140001108)
						|| list.contains(140001119) && list.contains(140001107) && list.contains(140001106)
						|| list.contains(140001119) && list.contains(140001108) && list.contains(140001106)) {
						player.getSkillList().addLinkedSkill(player, 643);
					} else if (list.contains(140001118) && list.contains(140001103) && list.contains(140001105)
						|| list.contains(140001118) && list.contains(140001103) && list.contains(140001104)
						|| list.contains(140001118) && list.contains(140001105) && list.contains(140001104)) {
						player.getSkillList().addLinkedSkill(player, 731);
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 662);
						} else {
							player.getSkillList().addLinkedSkill(player, 661);
						}
					}
				}
			return;
			case TEMPLAR:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001134) && list.contains(140001120) && list.contains(140001125)
						|| list.contains(140001134) && list.contains(140001120) && list.contains(140001122)
						|| list.contains(140001134) && list.contains(140001125) && list.contains(140001122)) {
						player.getSkillList().addLinkedSkill(player, 2921);
					} else if (list.contains(140001135) && list.contains(140001124) && list.contains(140001123)
						|| list.contains(140001135) && list.contains(140001124) && list.contains(140001121)
						|| list.contains(140001135) && list.contains(140001123) && list.contains(140001121)) {
						player.getSkillList().addLinkedSkill(player, 2918);
					} else {
						player.getSkillList().addLinkedSkill(player, 2917);
					}
				}
			return;
			case ASSASSIN:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001151) && list.contains(140001136) && list.contains(140001140)
						|| list.contains(140001151) && list.contains(140001136) && list.contains(140001137)
						|| list.contains(140001151) && list.contains(140001140) && list.contains(140001137)) {
						player.getSkillList().addLinkedSkill(player, 3241);
					} else if (list.contains(140001152) && list.contains(140001138) && list.contains(140001141)
						|| list.contains(140001152) && list.contains(140001138) && list.contains(140001139)
						|| list.contains(140001152) && list.contains(140001141) && list.contains(140001139)) {
						player.getSkillList().addLinkedSkill(player, 3238);
					} else {
						player.getSkillList().addLinkedSkill(player, 3244);
					}
				}
			return;
			case RANGER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001173) && list.contains(140001154) && list.contains(140001156)
						|| list.contains(140001173) && list.contains(140001154) && list.contains(140001158)
						|| list.contains(140001173) && list.contains(140001156) && list.contains(140001158)) {
						player.getSkillList().addLinkedSkill(player, 938);
					} else if (list.contains(140001172) && list.contains(140001153) && list.contains(140001157)
						|| list.contains(140001172) && list.contains(140001153) && list.contains(140001155)
						|| list.contains(140001172) && list.contains(140001157) && list.contains(140001155)) {
						player.getSkillList().addLinkedSkill(player, 1008);
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 1065);
						} else {
							player.getSkillList().addLinkedSkill(player, 1064);
						}
					}
				}
			return;
			case SORCERER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001191) && list.contains(140001178) && list.contains(140001174)
						|| list.contains(140001191) && list.contains(140001178) && list.contains(140001181)
						|| list.contains(140001191) && list.contains(140001174) && list.contains(140001181)) {
						player.getSkillList().addLinkedSkill(player, 1342);
					} else if (list.contains(140001192) && list.contains(140001176) && list.contains(140001177)
					    || list.contains(140001192) && list.contains(140001176) && list.contains(140001184)
						|| list.contains(140001192) && list.contains(140001176) && list.contains(140001185)
						|| list.contains(140001192) && list.contains(140001177) && list.contains(140001184)
						|| list.contains(140001192) && list.contains(140001177) && list.contains(140001185)) {
						player.getSkillList().addLinkedSkill(player, 1542);
					} else {
						player.getSkillList().addLinkedSkill(player, 1420);
					}
				}
			return;
			case SPIRIT_MASTER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001209) && list.contains(140001195) && list.contains(140001193)
						|| list.contains(140001209) && list.contains(140001195) && list.contains(140001194)
						|| list.contains(140001209) && list.contains(140001193) && list.contains(140001194)) {
						player.getSkillList().addLinkedSkill(player, 3543);
					} else if (list.contains(140001210) && list.contains(140001199) && list.contains(140001197)
					    || list.contains(140001210) && list.contains(140001199) && list.contains(140001198)
						|| list.contains(140001210) && list.contains(140001199) && list.contains(140001196)
						|| list.contains(140001210) && list.contains(140001197) && list.contains(140001196)
						|| list.contains(140001210) && list.contains(140001198) && list.contains(140001196)) {
						player.getSkillList().addLinkedSkill(player, 3549);
					} else {
						player.getSkillList().addLinkedSkill(player, 3851);
					}
				}
			return;
			case CLERIC:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001246) && list.contains(140001234) && list.contains(140001233)
						|| list.contains(140001246) && list.contains(140001234) && list.contains(140001232)
						|| list.contains(140001246) && list.contains(140001233) && list.contains(140001232)
						|| list.contains(140001246) && list.contains(140001235) && list.contains(140001232)
						|| list.contains(140001246) && list.contains(140001235) && list.contains(140001233)) {
						player.getSkillList().addLinkedSkill(player, 3934);
					} else if (list.contains(140001245) && list.contains(140001230) && list.contains(140001228)
						|| list.contains(140001245) && list.contains(140001231) && list.contains(140001228)
						|| list.contains(140001245) && list.contains(140001231) && list.contains(140001229)
						|| list.contains(140001245) && list.contains(140001230) && list.contains(140001229)
						|| list.contains(140001245) && list.contains(140001228) && list.contains(140001229)) {
						player.getSkillList().addLinkedSkill(player, 4169);
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 3910);
						} else {
							player.getSkillList().addLinkedSkill(player, 3911);
						}
					}
				}
			return;
			case CHANTER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001227) && list.contains(140001216) && list.contains(140001215)
						|| list.contains(140001227) && list.contains(140001216) && list.contains(140001214)
						|| list.contains(140001227) && list.contains(140001215) && list.contains(140001214)) {
						player.getSkillList().addLinkedSkill(player, 1903);
					} else if (list.contains(140001226) && list.contains(140001213) && list.contains(140001211)
						|| list.contains(140001226) && list.contains(140001213) && list.contains(140001212)
						|| list.contains(140001226) && list.contains(140001211) && list.contains(140001212)) {
						player.getSkillList().addLinkedSkill(player, 1909);
					} else {
						player.getSkillList().addLinkedSkill(player, 1906);
					}
				}
			return;
			case GUNSLINGER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001263) && list.contains(140001251) && list.contains(140001252)
						|| list.contains(140001263) && list.contains(140001251) && list.contains(140001250)
						|| list.contains(140001263) && list.contains(140001252) && list.contains(140001250)) {
						player.getSkillList().addLinkedSkill(player, 2377);
					} else if (list.contains(140001262) && list.contains(140001248) && list.contains(140001249)
						|| list.contains(140001262) && list.contains(140001248) && list.contains(140001247)
						|| list.contains(140001262) && list.contains(140001249) && list.contains(140001247)) {
						player.getSkillList().addLinkedSkill(player, 2370);
					} else {
						player.getSkillList().addLinkedSkill(player, 2382);
					}
				}
			return;
			case SONGWEAVER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001297) && list.contains(140001286) && list.contains(140001285)
						|| list.contains(140001297) && list.contains(140001286) && list.contains(140001283)
						|| list.contains(140001297) && list.contains(140001285) && list.contains(140001283)) {
						player.getSkillList().addLinkedSkill(player, 4483);
					} else if (list.contains(140001296) && list.contains(140001284) && list.contains(140001281)
						|| list.contains(140001296) && list.contains(140001284) && list.contains(140001282)
						|| list.contains(140001296) && list.contains(140001281) && list.contains(140001282)) {
						player.getSkillList().addLinkedSkill(player, 4480);
					} else {
						player.getSkillList().addLinkedSkill(player, 4566);
					}
				}
			return;
			case AETHERTECH:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001280) && list.contains(140001268) && list.contains(140001267)
						|| list.contains(140001280) && list.contains(140001268) && list.contains(140001266)
						|| list.contains(140001280) && list.contains(140001267) && list.contains(140001266)) {
						player.getSkillList().addLinkedSkill(player, 2863);
					} else if (list.contains(140001279) && list.contains(140001269) && list.contains(140001265)
						|| list.contains(140001279) && list.contains(140001269) && list.contains(140001264)
						|| list.contains(140001279) && list.contains(140001265) && list.contains(140001264)) {
						player.getSkillList().addLinkedSkill(player, 2858);
					} else {
						player.getSkillList().addLinkedSkill(player, 2851);
					}
				}
			return;
			case VANDAL:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001493) && list.contains(140001497) && list.contains(140001495)
						|| list.contains(140001493) && list.contains(140001496) && list.contains(140001495)
				        || list.contains(140001493) && list.contains(140001496) && list.contains(140001497)) {
						player.getSkillList().addLinkedSkill(player, 5486);
					} else if (list.contains(140001494) && list.contains(140001499) && list.contains(140001498)
						|| list.contains(140001494) && list.contains(140001500) && list.contains(140001498)
						|| list.contains(140001494) && list.contains(140001500) && list.contains(140001499)) {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 5530);
						} else {
							player.getSkillList().addLinkedSkill(player, 5533);
						}
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 5423);
						} else {
							player.getSkillList().addLinkedSkill(player, 5426);
						}
					}
				}
			return;
			default:
			break;
		}
		check = false;
	}
	
	/**
	 * Remove "Linked Skill"
	 */
	public static void DeleteLinkedSkills(Player player) {
		if (player == null) {
			return;
		}
		//Fi
		SkillLearnService.removeLinkedSkill(player, 641);
		SkillLearnService.removeLinkedSkill(player, 642);
		SkillLearnService.removeLinkedSkill(player, 643);
		SkillLearnService.removeLinkedSkill(player, 657);
		SkillLearnService.removeLinkedSkill(player, 658);
		SkillLearnService.removeLinkedSkill(player, 659);
		SkillLearnService.removeLinkedSkill(player, 660);
		SkillLearnService.removeLinkedSkill(player, 661);
		SkillLearnService.removeLinkedSkill(player, 662);
		SkillLearnService.removeLinkedSkill(player, 727);
		SkillLearnService.removeLinkedSkill(player, 728);
		SkillLearnService.removeLinkedSkill(player, 729);
		SkillLearnService.removeLinkedSkill(player, 730);
		SkillLearnService.removeLinkedSkill(player, 731);
		//Kn
		SkillLearnService.removeLinkedSkill(player, 2915);
		SkillLearnService.removeLinkedSkill(player, 2916);
		SkillLearnService.removeLinkedSkill(player, 2917);
		SkillLearnService.removeLinkedSkill(player, 2918);
		SkillLearnService.removeLinkedSkill(player, 2919);
		SkillLearnService.removeLinkedSkill(player, 2920);
		SkillLearnService.removeLinkedSkill(player, 2921);
		//As
		SkillLearnService.removeLinkedSkill(player, 3236);
		SkillLearnService.removeLinkedSkill(player, 3237);
		SkillLearnService.removeLinkedSkill(player, 3238);
		SkillLearnService.removeLinkedSkill(player, 3239);
		SkillLearnService.removeLinkedSkill(player, 3240);
		SkillLearnService.removeLinkedSkill(player, 3241);
		SkillLearnService.removeLinkedSkill(player, 3242);
		SkillLearnService.removeLinkedSkill(player, 3243);
		SkillLearnService.removeLinkedSkill(player, 3244);
		//Ra
		SkillLearnService.removeLinkedSkill(player, 936);
		SkillLearnService.removeLinkedSkill(player, 937);
		SkillLearnService.removeLinkedSkill(player, 938);
		SkillLearnService.removeLinkedSkill(player, 1006);
		SkillLearnService.removeLinkedSkill(player, 1007);
		SkillLearnService.removeLinkedSkill(player, 1008);
		SkillLearnService.removeLinkedSkill(player, 1060);
		SkillLearnService.removeLinkedSkill(player, 1061);
		SkillLearnService.removeLinkedSkill(player, 1062);
		SkillLearnService.removeLinkedSkill(player, 1063);
		SkillLearnService.removeLinkedSkill(player, 1064);
		SkillLearnService.removeLinkedSkill(player, 1065);
		//Wi
		SkillLearnService.removeLinkedSkill(player, 1340);
		SkillLearnService.removeLinkedSkill(player, 1341);
		SkillLearnService.removeLinkedSkill(player, 1342);
		SkillLearnService.removeLinkedSkill(player, 1418);
		SkillLearnService.removeLinkedSkill(player, 1419);
		SkillLearnService.removeLinkedSkill(player, 1420);
		SkillLearnService.removeLinkedSkill(player, 1540);
		SkillLearnService.removeLinkedSkill(player, 1541);
		SkillLearnService.removeLinkedSkill(player, 1542);
		//Ma
		SkillLearnService.removeLinkedSkill(player, 3541);
		SkillLearnService.removeLinkedSkill(player, 3542);
		SkillLearnService.removeLinkedSkill(player, 3543);
		SkillLearnService.removeLinkedSkill(player, 3549);
		SkillLearnService.removeLinkedSkill(player, 3849);
		SkillLearnService.removeLinkedSkill(player, 3850);
		SkillLearnService.removeLinkedSkill(player, 3851);
		//Pr
		SkillLearnService.removeLinkedSkill(player, 3906);
		SkillLearnService.removeLinkedSkill(player, 3907);
		SkillLearnService.removeLinkedSkill(player, 3908);
		SkillLearnService.removeLinkedSkill(player, 3909);
		SkillLearnService.removeLinkedSkill(player, 3910);
		SkillLearnService.removeLinkedSkill(player, 3911);
		SkillLearnService.removeLinkedSkill(player, 3932);
		SkillLearnService.removeLinkedSkill(player, 3933);
		SkillLearnService.removeLinkedSkill(player, 3934);
		SkillLearnService.removeLinkedSkill(player, 4167);
		SkillLearnService.removeLinkedSkill(player, 4168);
		SkillLearnService.removeLinkedSkill(player, 4169);
		//Ch
		SkillLearnService.removeLinkedSkill(player, 1901);
		SkillLearnService.removeLinkedSkill(player, 1902);
		SkillLearnService.removeLinkedSkill(player, 1903);
		SkillLearnService.removeLinkedSkill(player, 1904);
		SkillLearnService.removeLinkedSkill(player, 1905);
		SkillLearnService.removeLinkedSkill(player, 1906);
		SkillLearnService.removeLinkedSkill(player, 1907);
		SkillLearnService.removeLinkedSkill(player, 1908);
		SkillLearnService.removeLinkedSkill(player, 1909);
		//Gu
		SkillLearnService.removeLinkedSkill(player, 2368);
		SkillLearnService.removeLinkedSkill(player, 2369);
		SkillLearnService.removeLinkedSkill(player, 2370);
		SkillLearnService.removeLinkedSkill(player, 2371);
		SkillLearnService.removeLinkedSkill(player, 2372);
		SkillLearnService.removeLinkedSkill(player, 2373);
		SkillLearnService.removeLinkedSkill(player, 2374);
		SkillLearnService.removeLinkedSkill(player, 2375);
		SkillLearnService.removeLinkedSkill(player, 2376);
		SkillLearnService.removeLinkedSkill(player, 2377);
		SkillLearnService.removeLinkedSkill(player, 2378);
		SkillLearnService.removeLinkedSkill(player, 2379);
		SkillLearnService.removeLinkedSkill(player, 2380);
		SkillLearnService.removeLinkedSkill(player, 2381);
		SkillLearnService.removeLinkedSkill(player, 2382);
		//Ba
		SkillLearnService.removeLinkedSkill(player, 4474);
		SkillLearnService.removeLinkedSkill(player, 4475);
		SkillLearnService.removeLinkedSkill(player, 4476);
		SkillLearnService.removeLinkedSkill(player, 4477);
		SkillLearnService.removeLinkedSkill(player, 4478);
		SkillLearnService.removeLinkedSkill(player, 4479);
		SkillLearnService.removeLinkedSkill(player, 4480);
		SkillLearnService.removeLinkedSkill(player, 4483);
		SkillLearnService.removeLinkedSkill(player, 4564);
		SkillLearnService.removeLinkedSkill(player, 4565);
		SkillLearnService.removeLinkedSkill(player, 4566);
		//Ri
		SkillLearnService.removeLinkedSkill(player, 2849);
		SkillLearnService.removeLinkedSkill(player, 2850);
		SkillLearnService.removeLinkedSkill(player, 2851);
		SkillLearnService.removeLinkedSkill(player, 2852);
		SkillLearnService.removeLinkedSkill(player, 2853);
		SkillLearnService.removeLinkedSkill(player, 2854);
		SkillLearnService.removeLinkedSkill(player, 2855);
		SkillLearnService.removeLinkedSkill(player, 2856);
		SkillLearnService.removeLinkedSkill(player, 2857);
		SkillLearnService.removeLinkedSkill(player, 2858);
		SkillLearnService.removeLinkedSkill(player, 2859);
		SkillLearnService.removeLinkedSkill(player, 2860);
		SkillLearnService.removeLinkedSkill(player, 2861);
		SkillLearnService.removeLinkedSkill(player, 2862);
		SkillLearnService.removeLinkedSkill(player, 2863);
		//Pa
		SkillLearnService.removeLinkedSkill(player, 5421);
		SkillLearnService.removeLinkedSkill(player, 5422);
		SkillLearnService.removeLinkedSkill(player, 5423);
		SkillLearnService.removeLinkedSkill(player, 5424);
		SkillLearnService.removeLinkedSkill(player, 5425);
		SkillLearnService.removeLinkedSkill(player, 5426);
		SkillLearnService.removeLinkedSkill(player, 5480);
		SkillLearnService.removeLinkedSkill(player, 5481);
		SkillLearnService.removeLinkedSkill(player, 5482);
		SkillLearnService.removeLinkedSkill(player, 5483);
		SkillLearnService.removeLinkedSkill(player, 5484);
		SkillLearnService.removeLinkedSkill(player, 5485);
		SkillLearnService.removeLinkedSkill(player, 5486);
		SkillLearnService.removeLinkedSkill(player, 5528);
		SkillLearnService.removeLinkedSkill(player, 5529);
		SkillLearnService.removeLinkedSkill(player, 5530);
		SkillLearnService.removeLinkedSkill(player, 5531);
		SkillLearnService.removeLinkedSkill(player, 5532);
		SkillLearnService.removeLinkedSkill(player, 5533);
		player.setLinkedSkill(0);
	}
}