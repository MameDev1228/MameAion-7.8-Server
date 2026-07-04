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

public class StigmaUpgradeLinkedService
{
	public static void onLogOut(Player player) {
		StigmaUpgradeLinkedService.DeleteUpgradeLinkedSkills(player);
		player.setStigmaSet(0);
	}
	
	public static void checkEquipUpgradeConditions(Player player, List<Integer> list) {
		boolean check = false;
		for (Integer Stigma: list) {
			ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(Stigma);
			if (it.getName().contains("(Inert)") ||
			    it.getName().contains("STIGMA_N_") ||
				it.getName().contains("STIGMA_NE_")) {
				check = true;
			}
		} switch (player.getPlayerClass()) {
			case GLADIATOR:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001721) && list.contains(140001726) && list.contains(140001727)
						|| list.contains(140001721) && list.contains(140001726) && list.contains(140001725)
						|| list.contains(140001721) && list.contains(140001727) && list.contains(140001725)) {
						player.getSkillList().addLinkedSkill(player, 6024);
					} else if (list.contains(140001720) && list.contains(140001722) && list.contains(140001724)
						|| list.contains(140001720) && list.contains(140001722) && list.contains(140001723)
						|| list.contains(140001720) && list.contains(140001724) && list.contains(140001723)) {
						player.getSkillList().addLinkedSkill(player, 6023);
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 6022);
						} else {
							player.getSkillList().addLinkedSkill(player, 6021);
						}
					}
				}
			return;
			case TEMPLAR:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001737) && list.contains(140001739) && list.contains(140001744)
						|| list.contains(140001737) && list.contains(140001739) && list.contains(140001741)
						|| list.contains(140001737) && list.contains(140001744) && list.contains(140001741)) {
						player.getSkillList().addLinkedSkill(player, 6043);
					} else if (list.contains(140001738) && list.contains(140001743) && list.contains(140001742)
						|| list.contains(140001738) && list.contains(140001743) && list.contains(140001740)
						|| list.contains(140001738) && list.contains(140001742) && list.contains(140001740)) {
						player.getSkillList().addLinkedSkill(player, 6045);
					} else {
						player.getSkillList().addLinkedSkill(player, 6044);
					}
				}
			return;
			case ASSASSIN:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001753) && list.contains(140001755) && list.contains(140001759)
						|| list.contains(140001753) && list.contains(140001755) && list.contains(140001756)
						|| list.contains(140001753) && list.contains(140001759) && list.contains(140001756)) {
						player.getSkillList().addLinkedSkill(player, 6062);
					} else if (list.contains(140001754) && list.contains(140001757) && list.contains(140001760)
						|| list.contains(140001754) && list.contains(140001757) && list.contains(140001758)
						|| list.contains(140001754) && list.contains(140001760) && list.contains(140001758)) {
						player.getSkillList().addLinkedSkill(player, 6064);
					} else {
						player.getSkillList().addLinkedSkill(player, 6063);
					}
				}
			return;
			case RANGER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001771) && list.contains(140001773) && list.contains(140001775)
						|| list.contains(140001771) && list.contains(140001773) && list.contains(140001777)
						|| list.contains(140001771) && list.contains(140001775) && list.contains(140001777)) {
						player.getSkillList().addLinkedSkill(player, 6083);
					} else if (list.contains(140001770) && list.contains(140001772) && list.contains(140001776)
						|| list.contains(140001770) && list.contains(140001772) && list.contains(140001774)
						|| list.contains(140001770) && list.contains(140001776) && list.contains(140001774)) {
						player.getSkillList().addLinkedSkill(player, 6082);
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 6085);
						} else {
							player.getSkillList().addLinkedSkill(player, 6084);
						}
					}
				}
			return;
			case SORCERER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001791) && list.contains(140001796) && list.contains(140001793)
						|| list.contains(140001791) && list.contains(140001796) && list.contains(140001797)
						|| list.contains(140001791) && list.contains(140001793) && list.contains(140001797)) {
						player.getSkillList().addLinkedSkill(player, 6109);
					} else if (list.contains(140001792) && list.contains(140001794) && list.contains(140001795)
					    || list.contains(140001792) && list.contains(140001794) && list.contains(140001798)
						|| list.contains(140001792) && list.contains(140001794) && list.contains(140001799)
						|| list.contains(140001792) && list.contains(140001795) && list.contains(140001798)
						|| list.contains(140001792) && list.contains(140001795) && list.contains(140001799)) {
						player.getSkillList().addLinkedSkill(player, 6107);
					} else {
						player.getSkillList().addLinkedSkill(player, 6108);
					}
				}
			return;
			case SPIRIT_MASTER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001810) && list.contains(140001814) && list.contains(140001812)
						|| list.contains(140001810) && list.contains(140001814) && list.contains(140001813)
						|| list.contains(140001810) && list.contains(140001812) && list.contains(140001813)) {
						player.getSkillList().addLinkedSkill(player, 6129);
					} else if (list.contains(140001811) && list.contains(140001818) && list.contains(140001816)	
						|| list.contains(140001811) && list.contains(140001818) && list.contains(140001817)
						|| list.contains(140001811) && list.contains(140001818) && list.contains(140001815)
						|| list.contains(140001811) && list.contains(140001816) && list.contains(140001196)
						|| list.contains(140001811) && list.contains(140001817) && list.contains(140001196)) {
						player.getSkillList().addLinkedSkill(player, 6131);
					} else {
						player.getSkillList().addLinkedSkill(player, 6130);
					}
				}
			return;
			case CLERIC:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001846) && list.contains(140001853) && list.contains(140001851)
						|| list.contains(140001846) && list.contains(140001853) && list.contains(140001852)
						|| list.contains(140001846) && list.contains(140001852) && list.contains(140001851)
						|| list.contains(140001846) && list.contains(140001854) && list.contains(140001851)
						|| list.contains(140001846) && list.contains(140001854) && list.contains(140001852)) {
						player.getSkillList().addLinkedSkill(player, 6175);
					} else if (list.contains(140001845) && list.contains(140001849) && list.contains(140001855)
					    || list.contains(140001845) && list.contains(140001849) && list.contains(140001848)
					    || list.contains(140001845) && list.contains(140001850) && list.contains(140001855)
						|| list.contains(140001845) && list.contains(140001850) && list.contains(140001848)
						|| list.contains(140001845) && list.contains(140001855) && list.contains(140001848)) {
						player.getSkillList().addLinkedSkill(player, 6172);
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 6173);
						} else {
							player.getSkillList().addLinkedSkill(player, 6174);
						}
					}
				}
			return;
			case CHANTER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001829) && list.contains(140001835) && list.contains(140001834)
						|| list.contains(140001829) && list.contains(140001835) && list.contains(140001833)
						|| list.contains(140001829) && list.contains(140001834) && list.contains(140001833)) {
						player.getSkillList().addLinkedSkill(player, 6151);
					} else if (list.contains(140001828) && list.contains(140001832) && list.contains(140001830)
						|| list.contains(140001828) && list.contains(140001832) && list.contains(140001831)
						|| list.contains(140001828) && list.contains(140001830) && list.contains(140001831)) {
						player.getSkillList().addLinkedSkill(player, 6153);
					} else {
						player.getSkillList().addLinkedSkill(player, 6152);
					}
				}
			return;
			case GUNSLINGER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001865) && list.contains(140001870) && list.contains(140001871)
						|| list.contains(140001865) && list.contains(140001870) && list.contains(140001869)
						|| list.contains(140001865) && list.contains(140001871) && list.contains(140001869)) {
						player.getSkillList().addLinkedSkill(player, 6195);
					} else if (list.contains(140001864) && list.contains(140001867) && list.contains(140001868)
						|| list.contains(140001864) && list.contains(140001867) && list.contains(140001866)
						|| list.contains(140001864) && list.contains(140001868) && list.contains(140001866)) {
						player.getSkillList().addLinkedSkill(player, 6198);
					} else {
						player.getSkillList().addLinkedSkill(player, 6199);
					}
				}
			return;
			case SONGWEAVER:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001899) && list.contains(140001904) && list.contains(140001906)
						|| list.contains(140001899) && list.contains(140001904) && list.contains(140001902)
						|| list.contains(140001899) && list.contains(140001906) && list.contains(140001902)) {
						player.getSkillList().addLinkedSkill(player, 6227);
					} else if (list.contains(140001898) && list.contains(140001903) && list.contains(140001900)
						|| list.contains(140001898) && list.contains(140001903) && list.contains(140001901)
						|| list.contains(140001898) && list.contains(140001900) && list.contains(140001901)) {
						player.getSkillList().addLinkedSkill(player, 6228);
					} else {
						player.getSkillList().addLinkedSkill(player, 6229);
					}
				}
			return;
			case AETHERTECH:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001882) && list.contains(140001887) && list.contains(140001886)
						|| list.contains(140001882) && list.contains(140001887) && list.contains(140001885)
						|| list.contains(140001882) && list.contains(140001886) && list.contains(140001885)) {
						player.getSkillList().addLinkedSkill(player, 6253);
					} else if (list.contains(140001881) && list.contains(140001888) && list.contains(140001884)
						|| list.contains(140001881) && list.contains(140001888) && list.contains(140001883)
						|| list.contains(140001881) && list.contains(140001884) && list.contains(140001883)) {
						player.getSkillList().addLinkedSkill(player, 6250);
					} else {
						player.getSkillList().addLinkedSkill(player, 6249);
					}
				}
			return;
			case VANDAL:
				if (list.size() >= 6 && !check) {
					if (list.contains(140001915) && list.contains(140001919) && list.contains(140001917)
						|| list.contains(140001915) && list.contains(140001918) && list.contains(140001917)
				        || list.contains(140001915) && list.contains(140001918) && list.contains(140001919)) {
						player.getSkillList().addLinkedSkill(player, 6281);
					} else if (list.contains(140001916) && list.contains(140001921) && list.contains(140001920)
						|| list.contains(140001916) && list.contains(140001922) && list.contains(140001920)
						|| list.contains(140001916) && list.contains(140001922) && list.contains(140001921)) {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 6284);
						} else {
							player.getSkillList().addLinkedSkill(player, 6285);
						}
					} else {
						if (player.getRace() == Race.ELYOS) {
							player.getSkillList().addLinkedSkill(player, 6279);
						} else {
							player.getSkillList().addLinkedSkill(player, 6280);
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
	 * Remove "Upgrade Linked Skill"
	 */
	public static void DeleteUpgradeLinkedSkills(Player player) {
		if (player == null) {
			return;
		}
		SkillLearnService.removeLinkedSkill(player, 6021);
		SkillLearnService.removeLinkedSkill(player, 6022);
		SkillLearnService.removeLinkedSkill(player, 6023);
		SkillLearnService.removeLinkedSkill(player, 6024);
		SkillLearnService.removeLinkedSkill(player, 6043);
		SkillLearnService.removeLinkedSkill(player, 6044);
		SkillLearnService.removeLinkedSkill(player, 6045);
		SkillLearnService.removeLinkedSkill(player, 6062);
		SkillLearnService.removeLinkedSkill(player, 6063);
		SkillLearnService.removeLinkedSkill(player, 6064);
		SkillLearnService.removeLinkedSkill(player, 6082);
		SkillLearnService.removeLinkedSkill(player, 6083);
		SkillLearnService.removeLinkedSkill(player, 6084);
		SkillLearnService.removeLinkedSkill(player, 6085);
		SkillLearnService.removeLinkedSkill(player, 6107);
		SkillLearnService.removeLinkedSkill(player, 6108);
		SkillLearnService.removeLinkedSkill(player, 6109);
		SkillLearnService.removeLinkedSkill(player, 6129);
		SkillLearnService.removeLinkedSkill(player, 6130);
		SkillLearnService.removeLinkedSkill(player, 6131);
		SkillLearnService.removeLinkedSkill(player, 6151);
		SkillLearnService.removeLinkedSkill(player, 6152);
		SkillLearnService.removeLinkedSkill(player, 6153);
		SkillLearnService.removeLinkedSkill(player, 6172);
		SkillLearnService.removeLinkedSkill(player, 6173);
		SkillLearnService.removeLinkedSkill(player, 6174);
		SkillLearnService.removeLinkedSkill(player, 6175);
		SkillLearnService.removeLinkedSkill(player, 6195);
		SkillLearnService.removeLinkedSkill(player, 6196);
		SkillLearnService.removeLinkedSkill(player, 6197);
		SkillLearnService.removeLinkedSkill(player, 6198);
		SkillLearnService.removeLinkedSkill(player, 6199);
		SkillLearnService.removeLinkedSkill(player, 6227);
		SkillLearnService.removeLinkedSkill(player, 6228);
		SkillLearnService.removeLinkedSkill(player, 6229);
		SkillLearnService.removeLinkedSkill(player, 6249);
		SkillLearnService.removeLinkedSkill(player, 6250);
		SkillLearnService.removeLinkedSkill(player, 6251);
		SkillLearnService.removeLinkedSkill(player, 6252);
		SkillLearnService.removeLinkedSkill(player, 6253);
		SkillLearnService.removeLinkedSkill(player, 6279);
		SkillLearnService.removeLinkedSkill(player, 6280);
		SkillLearnService.removeLinkedSkill(player, 6281);
		SkillLearnService.removeLinkedSkill(player, 6282);
		SkillLearnService.removeLinkedSkill(player, 6283);
		SkillLearnService.removeLinkedSkill(player, 6284);
		SkillLearnService.removeLinkedSkill(player, 6285);
		player.setLinkedSkill(0);
	}
}