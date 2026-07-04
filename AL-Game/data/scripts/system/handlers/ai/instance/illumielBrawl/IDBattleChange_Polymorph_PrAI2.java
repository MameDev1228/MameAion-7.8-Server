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
package ai.instance.illumielBrawl;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("IDBattleChange_Polymorph_Pr")
public class IDBattleChange_Polymorph_PrAI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		PlayerEffectController effectController = player.getEffectController();
		if (dialogId == 10000) {
			switch (getNpcId()) {
			    case 656692: //IDBattleChange_Polymorph_Pr_NPC_Li.
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(20309);
						effectController.removeEffect(20310);
						effectController.removeEffect(20311);
						effectController.removeEffect(20312);
						effectController.removeEffect(20314);
						player.getSkillList().addSkill(player, 5229, 1);
						player.getSkillList().addSkill(player, 5232, 1);
						player.getSkillList().addSkill(player, 5312, 1);
						player.getSkillList().addSkill(player, 5313, 1);
						player.getSkillList().addSkill(player, 5314, 1);
						player.getSkillList().addSkill(player, 5237, 1);
						player.getSkillList().addSkill(player, 5238, 1);
						//Kn
						SkillLearnService.removeSkill(player, 5181);
						SkillLearnService.removeSkill(player, 5187);
						SkillLearnService.removeSkill(player, 5184);
						SkillLearnService.removeSkill(player, 5189);
						SkillLearnService.removeSkill(player, 5190);
						//Fi
						SkillLearnService.removeSkill(player, 5193);
						SkillLearnService.removeSkill(player, 5196);
						SkillLearnService.removeSkill(player, 5199);
						SkillLearnService.removeSkill(player, 5201);
						SkillLearnService.removeSkill(player, 5202);
						//Wi
						SkillLearnService.removeSkill(player, 5205);
						SkillLearnService.removeSkill(player, 5208);
						SkillLearnService.removeSkill(player, 5320);
						SkillLearnService.removeSkill(player, 5321);
						SkillLearnService.removeSkill(player, 5213);
						SkillLearnService.removeSkill(player, 5211);
						SkillLearnService.removeSkill(player, 5316);
						//Gu
						SkillLearnService.removeSkill(player, 5217);
						SkillLearnService.removeSkill(player, 5307);
						SkillLearnService.removeSkill(player, 5308);
						SkillLearnService.removeSkill(player, 5309);
						SkillLearnService.removeSkill(player, 5223);
						SkillLearnService.removeSkill(player, 5225);
						SkillLearnService.removeSkill(player, 5226);
						SkillLearnService.removeSkill(player, 5310);
						SkillLearnService.removeSkill(player, 5311);
						//Ch
						SkillLearnService.removeSkill(player, 5241);
						SkillLearnService.removeSkill(player, 5244);
						SkillLearnService.removeSkill(player, 5247);
						SkillLearnService.removeSkill(player, 5249);
						SkillLearnService.removeSkill(player, 5250);
					    SkillEngine.getInstance().applyEffectDirectly(20313, player, player, 1800000 * 1);
					}
			    break;
				case 656695: //IDBattleChange_Polymorph_Pr_NPC_Da.
				    if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(20315);
						effectController.removeEffect(20316);
						effectController.removeEffect(20317);
						effectController.removeEffect(20318);
						effectController.removeEffect(20320);
						player.getSkillList().addSkill(player, 5229, 1);
						player.getSkillList().addSkill(player, 5232, 1);
						player.getSkillList().addSkill(player, 5312, 1);
						player.getSkillList().addSkill(player, 5313, 1);
						player.getSkillList().addSkill(player, 5314, 1);
						player.getSkillList().addSkill(player, 5237, 1);
						player.getSkillList().addSkill(player, 5322, 1);
						//Kn
						SkillLearnService.removeSkill(player, 5181);
						SkillLearnService.removeSkill(player, 5187);
						SkillLearnService.removeSkill(player, 5184);
						SkillLearnService.removeSkill(player, 5189);
						SkillLearnService.removeSkill(player, 5190);
						//Fi
						SkillLearnService.removeSkill(player, 5193);
						SkillLearnService.removeSkill(player, 5196);
						SkillLearnService.removeSkill(player, 5199);
						SkillLearnService.removeSkill(player, 5201);
						SkillLearnService.removeSkill(player, 5202);
						//Wi
						SkillLearnService.removeSkill(player, 5205);
						SkillLearnService.removeSkill(player, 5208);
						SkillLearnService.removeSkill(player, 5320);
						SkillLearnService.removeSkill(player, 5321);
						SkillLearnService.removeSkill(player, 5213);
						SkillLearnService.removeSkill(player, 5211);
						SkillLearnService.removeSkill(player, 5316);
						//Gu
						SkillLearnService.removeSkill(player, 5217);
						SkillLearnService.removeSkill(player, 5307);
						SkillLearnService.removeSkill(player, 5308);
						SkillLearnService.removeSkill(player, 5309);
						SkillLearnService.removeSkill(player, 5223);
						SkillLearnService.removeSkill(player, 5225);
						SkillLearnService.removeSkill(player, 5226);
						SkillLearnService.removeSkill(player, 5310);
						SkillLearnService.removeSkill(player, 5311);
						//Ch
						SkillLearnService.removeSkill(player, 5241);
						SkillLearnService.removeSkill(player, 5244);
						SkillLearnService.removeSkill(player, 5247);
						SkillLearnService.removeSkill(player, 5249);
						SkillLearnService.removeSkill(player, 5250);
					    SkillEngine.getInstance().applyEffectDirectly(20319, player, player, 1800000 * 1);
					}
			    break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}