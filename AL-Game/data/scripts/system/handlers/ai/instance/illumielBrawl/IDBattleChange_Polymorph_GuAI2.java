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

@AIName("IDBattleChange_Polymorph_Gu")
public class IDBattleChange_Polymorph_GuAI2 extends NpcAI2
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
			    case 656686: //IDBattleChange_Polymorph_Gu_NPC_Li.
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(20309);
						effectController.removeEffect(20310);
						effectController.removeEffect(20311);
						effectController.removeEffect(20313);
						effectController.removeEffect(20314);
						player.getSkillList().addSkill(player, 5217, 1);
						player.getSkillList().addSkill(player, 5307, 1);
						player.getSkillList().addSkill(player, 5308, 1);
						player.getSkillList().addSkill(player, 5309, 1);
						player.getSkillList().addSkill(player, 5223, 1);
						player.getSkillList().addSkill(player, 5225, 1);
						player.getSkillList().addSkill(player, 5226, 1);
						player.getSkillList().addSkill(player, 5310, 1);
						player.getSkillList().addSkill(player, 5311, 1);
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
						//Pr
						SkillLearnService.removeSkill(player, 5229);
						SkillLearnService.removeSkill(player, 5232);
						SkillLearnService.removeSkill(player, 5312);
						SkillLearnService.removeSkill(player, 5313);
						SkillLearnService.removeSkill(player, 5314);
						SkillLearnService.removeSkill(player, 5237);
						SkillLearnService.removeSkill(player, 5238);
						SkillLearnService.removeSkill(player, 5322);
						//Ch
						SkillLearnService.removeSkill(player, 5241);
						SkillLearnService.removeSkill(player, 5244);
						SkillLearnService.removeSkill(player, 5247);
						SkillLearnService.removeSkill(player, 5249);
						SkillLearnService.removeSkill(player, 5250);
					    SkillEngine.getInstance().applyEffectDirectly(20312, player, player, 1800000 * 1);
					}
			    break;
				case 656689: //IDBattleChange_Polymorph_Gu_NPC_Da.
				    if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(20315);
						effectController.removeEffect(20316);
						effectController.removeEffect(20317);
						effectController.removeEffect(20319);
						effectController.removeEffect(20320);
						player.getSkillList().addSkill(player, 5217, 1);
						player.getSkillList().addSkill(player, 5307, 1);
						player.getSkillList().addSkill(player, 5308, 1);
						player.getSkillList().addSkill(player, 5309, 1);
						player.getSkillList().addSkill(player, 5223, 1);
						player.getSkillList().addSkill(player, 5225, 1);
						player.getSkillList().addSkill(player, 5226, 1);
						player.getSkillList().addSkill(player, 5310, 1);
						player.getSkillList().addSkill(player, 5311, 1);
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
						//Pr
						SkillLearnService.removeSkill(player, 5229);
						SkillLearnService.removeSkill(player, 5232);
						SkillLearnService.removeSkill(player, 5312);
						SkillLearnService.removeSkill(player, 5313);
						SkillLearnService.removeSkill(player, 5314);
						SkillLearnService.removeSkill(player, 5237);
						SkillLearnService.removeSkill(player, 5238);
						SkillLearnService.removeSkill(player, 5322);
						//Ch
						SkillLearnService.removeSkill(player, 5241);
						SkillLearnService.removeSkill(player, 5244);
						SkillLearnService.removeSkill(player, 5247);
						SkillLearnService.removeSkill(player, 5249);
						SkillLearnService.removeSkill(player, 5250);
					    SkillEngine.getInstance().applyEffectDirectly(20318, player, player, 1800000 * 1);
					}
			    break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}