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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.robot.RobotInfo;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RideRobotEffect")
public class RideRobotEffect extends EffectTemplate
{
	@Override
	public void applyEffect(final Effect effect) {
		effect.addToEffectedController();
		Creature effected = effect.getEffected();
		Player player = (Player)effected;
		player.setUseRobot(true);
		player.getEffectController().updatePlayerEffectIcons();
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_USE_ROBOT(player, getRobotInfo(player).getRobotId()));
		player.setRobotId(getRobotInfo(player).getRobotId());
		ActionObserver observer = new ActionObserver(ObserverType.UNEQUIP) {
            @Override
            public void unequip(Item item, Player owner) {
                if (item.getEquipmentType() == EquipType.WEAPON) {
                    effect.endEffect();
                }
            }
        };
        player.getObserveController().addObserver(observer);
        effect.setActionObserver(observer, position);
	}
	
	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		Creature effected = effect.getEffected();
		Player player = (Player)effected;
		if (player.isUseRobot()) {
			removeSkill((Player) effect.getEffector());
			player.getEffectController().updatePlayerEffectIcons();
			PacketSendUtility.broadcastPacket(player, new SM_USE_ROBOT(player, 0), true);
			PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			player.setUseRobot(false);
			player.setRobotId(0);
		}
		ActionObserver observer = effect.getActionObserver(position);
        if (observer != null) {
            effect.getEffected().getObserveController().removeObserver(observer);
        }
	}
	
	public RobotInfo getRobotInfo(Player player) {
		ItemTemplate template = player.getEquipment().getMainHandWeapon().getItemSkinTemplate();
		return DataManager.ROBOT_DATA.getRobotInfo(template.getRobotId());
	}
	
	private void removeSkill(Player player) {
		//Mobility Thrusters.
		player.getEffectController().removeEffect(2421);
		player.getEffectController().removeEffect(2422);
		//Purifier Stigma 7.x
		player.getEffectController().removeEffect(6260);
		//Kinetic Battery.
		player.getEffectController().removeEffect(2440);
		player.getEffectController().removeEffect(2441);
		player.getEffectController().removeEffect(2442);
		player.getEffectController().removeEffect(2443);
		player.getEffectController().removeEffect(2444);
		player.getEffectController().removeEffect(2445);
		player.getEffectController().removeEffect(2446);
		player.getEffectController().removeEffect(2447);
		player.getEffectController().removeEffect(2448);
		player.getEffectController().removeEffect(2449);
		//Kinetic Bulwark.
		player.getEffectController().removeEffect(2579);
		player.getEffectController().removeEffect(2580);
		player.getEffectController().removeEffect(2581);
		//Purifier Stigma 7.x
		player.getEffectController().removeEffect(6254);
		//Stability Thrusters.
		player.getEffectController().removeEffect(2736);
		player.getEffectController().removeEffect(2737);
		player.getEffectController().removeEffect(2738);
		player.getEffectController().removeEffect(2739);
		player.getEffectController().removeEffect(2740);
		//Mounting Frustration.
		player.getEffectController().removeEffect(2838);
		player.getEffectController().removeEffect(2839);
		player.getEffectController().removeEffect(2840);
		player.getEffectController().removeEffect(2841);
		player.getEffectController().removeEffect(2842);
		player.getEffectController().removeEffect(2843);
		player.getEffectController().removeEffect(2844);
		player.getEffectController().removeEffect(2845);
		player.getEffectController().removeEffect(2846);
		player.getEffectController().removeEffect(2847);
		player.getEffectController().removeEffect(2848);
		//Combat Prowess.
		player.getEffectController().removeEffect(4794);
		//Magical Cover.
		player.getEffectController().removeEffect(4796);
		//Transcend Limit.
		player.getEffectController().removeEffect(4876);
		//Extreme Transcendence.
		player.getEffectController().removeEffect(6017);
		//(Advanced) Extreme Transcendence.
		player.getEffectController().removeEffect(6019);
		//Limit Breaker.
		player.getEffectController().removeEffect(6018);
		//(Advanced) Limit Breaker.
		player.getEffectController().removeEffect(6020);
	}
}