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
package ai.instance.perilousContaminatedUnderpath;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Cold_Spring_Of_Soul")
public class Cold_Spring_Of_SoulAI2 extends NpcAI2
{
	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		PlayerEffectController effectController = player.getEffectController();
		//Powerful Daeva Of Soul I
		if (dialogId == 10000) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4936);
						effectController.removeEffect(4937);
						effectController.removeEffect(4938);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4935, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4941);
						effectController.removeEffect(4942);
						effectController.removeEffect(4943);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4940, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(182007405, 2); //Bright Aether.
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4936);
						effectController.removeEffect(4937);
						effectController.removeEffect(4938);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4935, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4941);
						effectController.removeEffect(4942);
						effectController.removeEffect(4943);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4940, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(186000470, 2); //War Points.
				break;
			}
		}
		//Powerful Daeva Of Soul II
		if (dialogId == 10001) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
					if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4937);
						effectController.removeEffect(4938);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4936, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4942);
						effectController.removeEffect(4943);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4941, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(182007405, 7); //Bright Aether.
				break;
				case 301632000: //Perilous Contaminated Underpath.
					if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4937);
						effectController.removeEffect(4938);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4936, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4942);
						effectController.removeEffect(4943);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4941, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(186000470, 7); //War Points.
				break;
			}
		}
		//Powerful Daeva Of Soul III
		if (dialogId == 10002) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
					if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4936);
						effectController.removeEffect(4938);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4922, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4937, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4941);
						effectController.removeEffect(4943);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4922, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4942, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(182007405, 13); //Bright Aether.
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4936);
						effectController.removeEffect(4938);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4922, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4937, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4941);
						effectController.removeEffect(4943);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4920, 1);
						player.getSkillList().addSkill(player, 4922, 1);
						player.getSkillList().addSkill(player, 4926, 1);
						SkillEngine.getInstance().applyEffectDirectly(4942, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(186000470, 13); //War Points.
				break;
			}
		}
		//Powerful Daeva Of Soul IV
		if (dialogId == 10003) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4936);
						effectController.removeEffect(4937);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4926, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4938, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4941);
						effectController.removeEffect(4942);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4926, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4943, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(182007405, 20); //Bright Aether.
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4936);
						effectController.removeEffect(4937);
						effectController.removeEffect(4939);
						player.getSkillList().addSkill(player, 4926, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4938, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4941);
						effectController.removeEffect(4942);
						effectController.removeEffect(4944);
						player.getSkillList().addSkill(player, 4926, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4943, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(186000470, 20); //War Points.
				break;
			}
		}
		//Powerful Daeva Of Soul V
		if (dialogId == 10004) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4936);
						effectController.removeEffect(4937);
						effectController.removeEffect(4938);
						player.getSkillList().addSkill(player, 4929, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4939, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4941);
						effectController.removeEffect(4942);
						effectController.removeEffect(4943);
						player.getSkillList().addSkill(player, 4929, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4944, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(182007405, 30); //Bright Aether.
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    if (player.getCommonData().getRace() == Race.ELYOS) {
						effectController.removeEffect(4935);
						effectController.removeEffect(4936);
						effectController.removeEffect(4937);
						effectController.removeEffect(4938);
						player.getSkillList().addSkill(player, 4929, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4939, player, player, 1200000 * 1);
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						effectController.removeEffect(4940);
						effectController.removeEffect(4941);
						effectController.removeEffect(4942);
						effectController.removeEffect(4943);
						player.getSkillList().addSkill(player, 4929, 1);
						player.getSkillList().addSkill(player, 4930, 1);
						player.getSkillList().addSkill(player, 4931, 1);
						SkillEngine.getInstance().applyEffectDirectly(4944, player, player, 1200000 * 1);
					}
					player.getInventory().decreaseByItemId(186000470, 30); //War Points.
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}