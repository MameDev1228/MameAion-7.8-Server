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
package ai.instance.miniumVault;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("_37S21G_0M618S_J227S4")
public class _37S21G_0M618S_J227S4AI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		int instanceId = getPosition().getInstanceId();
		PlayerEffectController effectController = player.getEffectController();
		//Remove aethertech armor coz in instance the player is under a panel transformation.
		if (player.isUseRobot()) {
			player.setRobotId(0);
			player.setUseRobot(false);
			removeAethertechEffect(player);
			PacketSendUtility.broadcastPacket(player, new SM_USE_ROBOT(player, 0), true);
		}
		//37S21G_0M618S_J227S4.
		if (dialogId == 10000) {
		    switch (Rnd.get(1, 3)) {
				case 1:
					effectController.removeEffect(20816);
					effectController.removeEffect(20817);
					////////////////////////////////////////////////
					player.getSkillList().addSkill(player, 5952, 1);
					player.getSkillList().addSkill(player, 5954, 1);
					player.getSkillList().addSkill(player, 5955, 1);
					player.getSkillList().addSkill(player, 5956, 1);
					////////////////////////////////////////////
					SkillLearnService.removeSkill(player, 5957);
					SkillLearnService.removeSkill(player, 5958);
					SkillLearnService.removeSkill(player, 5959);
					SkillLearnService.removeSkill(player, 5960);
					SkillLearnService.removeSkill(player, 5961);
					SkillLearnService.removeSkill(player, 5962);
					SkillLearnService.removeSkill(player, 5963);
					SkillLearnService.removeSkill(player, 5964);
					////////////////////////////////////////////
					SkillEngine.getInstance().applyEffectDirectly(20815, player, player, 3600000 * 1);
					TeleportService2.teleportTo(player, 302641000, instanceId, 494.0000f, 192.0000f, 179.0000f, (byte) 62);
				break;
				case 2:
					effectController.removeEffect(20815);
					effectController.removeEffect(20817);
					////////////////////////////////////////////////
					player.getSkillList().addSkill(player, 5961, 1);
					player.getSkillList().addSkill(player, 5962, 1);
					player.getSkillList().addSkill(player, 5963, 1);
					player.getSkillList().addSkill(player, 5964, 1);
					////////////////////////////////////////////
					SkillLearnService.removeSkill(player, 5952);
					SkillLearnService.removeSkill(player, 5954);
					SkillLearnService.removeSkill(player, 5955);
					SkillLearnService.removeSkill(player, 5956);
					SkillLearnService.removeSkill(player, 5957);
					SkillLearnService.removeSkill(player, 5958);
					SkillLearnService.removeSkill(player, 5959);
					SkillLearnService.removeSkill(player, 5960);
					////////////////////////////////////////////
					SkillEngine.getInstance().applyEffectDirectly(20816, player, player, 3600000 * 1);
					TeleportService2.teleportTo(player, 302641000, instanceId, 560.0000f, 192.0000f, 179.0000f, (byte) 0);
				break;
				case 3:
					effectController.removeEffect(20815);
					effectController.removeEffect(20816);
					////////////////////////////////////////////////
					player.getSkillList().addSkill(player, 5957, 1);
					player.getSkillList().addSkill(player, 5958, 1);
					player.getSkillList().addSkill(player, 5959, 1);
					player.getSkillList().addSkill(player, 5960, 1);
					////////////////////////////////////////////
					SkillLearnService.removeSkill(player, 5952);
					SkillLearnService.removeSkill(player, 5954);
					SkillLearnService.removeSkill(player, 5955);
					SkillLearnService.removeSkill(player, 5956);
					SkillLearnService.removeSkill(player, 5961);
					SkillLearnService.removeSkill(player, 5962);
					SkillLearnService.removeSkill(player, 5963);
					SkillLearnService.removeSkill(player, 5964);
					////////////////////////////////////////////
					SkillEngine.getInstance().applyEffectDirectly(20817, player, player, 3600000 * 1);
					TeleportService2.teleportTo(player, 302641000, instanceId, 525.0000f, 246.0000f, 179.0000f, (byte) 30);
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        return true;
    }
	
	private void removeAethertechEffect(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		//Mobility Thrusters.
		effectController.removeEffect(2421);
		effectController.removeEffect(2422);
		//Purifier Stigma 7.x
		effectController.removeEffect(6260);
		//Kinetic Battery.
		effectController.removeEffect(2440);
		effectController.removeEffect(2441);
		effectController.removeEffect(2442);
		effectController.removeEffect(2443);
		effectController.removeEffect(2444);
		effectController.removeEffect(2445);
		effectController.removeEffect(2446);
		effectController.removeEffect(2447);
		effectController.removeEffect(2448);
		effectController.removeEffect(2449);
		//Kinetic Bulwark.
		effectController.removeEffect(2579); 
		effectController.removeEffect(2580);
		effectController.removeEffect(2581);
		//Purifier Stigma 7.x
		effectController.removeEffect(6254);
		//Stability Thrusters.
		effectController.removeEffect(2736); 
		effectController.removeEffect(2737);
		effectController.removeEffect(2738);
		effectController.removeEffect(2739);
		effectController.removeEffect(2740);
		//Embark.
		effectController.removeEffect(2767); 
		effectController.removeEffect(2768);
		effectController.removeEffect(2769);
		effectController.removeEffect(2770);
		effectController.removeEffect(2771);
		effectController.removeEffect(2772);
		effectController.removeEffect(2773);
		effectController.removeEffect(2774);
		effectController.removeEffect(2775);
		effectController.removeEffect(2776);
		effectController.removeEffect(2777);
		effectController.removeEffect(2778);
		//Mounting Frustration.
		effectController.removeEffect(2838); 
		effectController.removeEffect(2839);
		effectController.removeEffect(2840);
		effectController.removeEffect(2841);
		effectController.removeEffect(2842);
		effectController.removeEffect(2843);
		effectController.removeEffect(2844);
		effectController.removeEffect(2845);
		effectController.removeEffect(2846);
		effectController.removeEffect(2847);
		effectController.removeEffect(2848);
		//Combat Prowess.
		effectController.removeEffect(4794);
		//Magical Cover.
		effectController.removeEffect(4796);
		//Transcend Limit.
		effectController.removeEffect(4876);
		//극한 초월.
		effectController.removeEffect(6017);
		effectController.removeEffect(6019);
		//한계 탈피.
		effectController.removeEffect(6018);
		effectController.removeEffect(6020);
	}
}