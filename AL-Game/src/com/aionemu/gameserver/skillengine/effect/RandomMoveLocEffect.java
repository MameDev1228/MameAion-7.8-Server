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

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.model.DashStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RandomMoveLocEffect")
public class RandomMoveLocEffect extends EffectTemplate
{
	@XmlAttribute(name = "distance")
	private float distance;
	
	@XmlAttribute(name = "direction")
	private float direction;
	
	@Override
	public void applyEffect(Effect effect) {
		final Player effector = (Player) effect.getEffector();
		PacketSendUtility.sendPacket(effector, new SM_TARGET_UPDATE(effector));
		Skill skill = effect.getSkill();
		World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
	}
	
	@Override
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
		if (((Player) effect.getEffector()).getRobotId() != 0) {
			if (effect.getSkill().getSkillId() == 2424 || effect.getSkill().getSkillId() == 2425) { //Hypergate Detonation.
				effect.setDashStatus(DashStatus.RANDOMMOVELOC);
			} else {
				effect.setDashStatus(DashStatus.RIDERMOVELOC);
			}
		} else {
			if (effect.getSkillId() == 4697) { //Mercurial Blast.
				effect.setDashStatus(DashStatus.RIDERMOVELOC);
			} else {
				effect.setDashStatus(DashStatus.RANDOMMOVELOC);
			}
			effect.setSkillMoveType(SkillMoveType.MOVEBEHIND);
		}
		final Player effector = (Player) effect.getEffector();
		if (effect.getSkill().getSkillId() == 2424 || effect.getSkill().getSkillId() == 2425) { //Hypergate Detonation.
			removeSkill((Player) effect.getEffector());
			PacketSendUtility.broadcastPacket(effector, new SM_USE_ROBOT(effector, 0), true);
			PacketSendUtility.sendPacket(effector, new SM_MOTION(effector.getObjectId(), effector.getMotions().getActiveMotions()));
			effector.setUseRobot(false);
			effector.setRobotId(0);
			//Remove Cooldown Skill.
			List<Integer> delayIds = new ArrayList<Integer>();
			if (effector.getSkillCoolDowns() != null) {
				long currentTime = System.currentTimeMillis();
				for (Entry<Integer, Long> en: effector.getSkillCoolDowns().entrySet()) {
					delayIds.add(en.getKey());
				} for (Integer delayId: delayIds) {
					effector.setSkillCoolDown(delayId, currentTime);
				}
				PacketSendUtility.sendPacket(effector, new SM_SKILL_COOLDOWN(effector, delayIds));
				delayIds.clear();
			}
		}
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		float x1 = (float) (Math.cos(Math.PI * direction + radian) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction + radian) * distance);
		float targetZ = GeoService.getInstance().getZ(effector.getWorldId(), effector.getX() + x1, effector.getY() + y1, effector.getZ() + 1.5f, 0.2f, effector.getInstanceId());
		effector.getEffectController().updatePlayerEffectIcons();
		PacketSendUtility.broadcastPacketAndReceive(effector, new SM_TRANSFORM(effector, true));
		PacketSendUtility.broadcastPacketAndReceive(effector, new SM_TRANSFORM(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId(), effector.getTransformedSkillId()));
		byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
		Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effector, effector.getX() + x1, effector.getY() + y1, targetZ, false, intentions);
		effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effector.getHeading());
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
		//Embark.
		player.getEffectController().removeEffect(2767); 
		player.getEffectController().removeEffect(2768);
		player.getEffectController().removeEffect(2769);
		player.getEffectController().removeEffect(2770);
		player.getEffectController().removeEffect(2771);
		player.getEffectController().removeEffect(2772);
		player.getEffectController().removeEffect(2773);
		player.getEffectController().removeEffect(2774);
		player.getEffectController().removeEffect(2775);
		player.getEffectController().removeEffect(2776);
		player.getEffectController().removeEffect(2777);
		player.getEffectController().removeEffect(2778);
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