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
package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ROUND_TRIP;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.SummonEffect;
import com.aionemu.gameserver.skillengine.effect.TransformEffect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillUseAction")
public class SkillUseAction extends AbstractItemAction
{
	@XmlAttribute
	protected int skillid;
	
	@XmlAttribute
	protected int level;
	
	@XmlAttribute(required = false)
    private Integer mapid;
	
	boolean roundTrip = false;
	
	public int getSkillid() {
		return skillid;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
        if (skill == null) {
            return false;
        }
		int nameId = parentItem.getItemTemplate().getNameId();
        byte levelRestrict = parentItem.getItemTemplate().getMaxLevelRestrict(player);
        if (levelRestrict != 0) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(levelRestrict, nameId));
            return false;
        } if (player.isTransformed()) {
			for (EffectTemplate template: skill.getSkillTemplate().getEffects().getEffects()) {
				if (template instanceof TransformEffect) {
					//You cannot use %0.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_USE_ITEM(new DescriptionId(nameId)));
					//Transformation Mode.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_ACT_STATE_POLYMORPH, 3000);
					return false;
				}
			}
		} if (player.getSummon() != null && player.isInvisibleTransform()) {
			for (EffectTemplate template: skill.getSkillTemplate().getEffects().getEffects()) {
				if (template instanceof SummonEffect) {
					//You already have a spirit following you.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_ALREADY_HAVE_A_FOLLOWER, 0);
					return false;
				}
			}
		} if (skill.getSkillId() == 10475 || skill.getSkillId() == 10491 ||
		    skill.getSkillId() == 10492 || skill.getSkillId() == 10493 ||
			skill.getSkillId() == 10494 || skill.getSkillId() == 10495 ||
			skill.getSkillId() == 10496 || skill.getSkillId() == 10497 ||
			skill.getSkillId() == 10498 || skill.getSkillId() == 10499 ||
			skill.getSkillId() == 10517 || skill.getSkillId() == 10518 ||
			skill.getSkillId() == 11015) {
			PlayerCommonData pcd = player.getCommonData();
			if (pcd.getCurrentReposteEnergy() == pcd.getMaxReposteEnergy()) {
				//The Energy of Repose is ineffective in your current Restriction Phase.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_BOT_CANNOT_RECEIVE_VITAL_BONUS, 0);
				return false;
			}
		} if (skill.getSkillId() == 10954 || skill.getSkillId() == 11012 ||
		    skill.getSkillId() == 11072 || skill.getSkillId() == 11092 ||
			skill.getSkillId() == 11172 || skill.getSkillId() == 11248) {
			PlayerCommonData pcd = player.getCommonData();
			if (pcd.getBerdinStar() == pcd.getMaxBerdinStar()) {
				//Exceeded maximum charge of Points. This item cannot be used anymore.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_PLUS_ABSOLUTEEXP_BONUS, 0);
				return false;
			}
		} if (skill.getSkillId() == 11183 || skill.getSkillId() == 11184 ||
		    skill.getSkillId() == 11354 || skill.getSkillId() == 11749 ||
			skill.getSkillId() == 11750) {
			PlayerCommonData pcd = player.getCommonData();
			if (pcd.getAbyssFavor() == pcd.getMaxAbyssFavor()) {
				//Exceeded maximum charge of Points. This item cannot be used anymore.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_PLUS_ABSOLUTEEXP_BONUS, 0);
				return false;
			}
		} if (skill.getSkillId() == 13394) { //차원의 모래시계.
			PlayerCommonData pcd = player.getCommonData();
			if (pcd.getWorldPlayTime() >= 300) {
				//You cannot use %0.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_USE_ITEM(new DescriptionId(nameId)));
				return false;
			}
		} if (skill.getSkillId() == 13395) { //차원의 모래시계.
			PlayerCommonData pcd = player.getCommonData();
			if (pcd.getWorldPlayTime() >= 270) {
				//You cannot use %0.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_USE_ITEM(new DescriptionId(nameId)));
				return false;
			}
		} if (skill.getSkillId() == 13396) { //차원의 모래시계.
			PlayerCommonData pcd = player.getCommonData();
			if (pcd.getWorldPlayTime() >= 240) {
				//You cannot use %0.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_USE_ITEM(new DescriptionId(nameId)));
				return false;
			}
		}
		return skill.canUseSkill();
	}
	
	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		final int parentNameId = parentItem.getNameId();
		final Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
		if (skill != null) {
			if (skill.getSkillId() == 8198) {
				RequestResponseHandler responseHandler = new RequestResponseHandler(player) {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						final ActionObserver moveObserver = new ActionObserver(ObserverType.MOVE) {
							@Override
							public void moved() {
								player.getController().cancelTask(TaskId.ITEM_USE);
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentNameId)));
							}
						};
						player.getObserveController().attach(moveObserver);
						player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								player.getObserveController().removeObserver(moveObserver);
								roundTripReturn(player);
							}
						}, 5000));
						skill.setItemObjectId(parentItem.getObjectId());
						skill.useSkill();
					}
					@Override
					public void denyRequest(Creature requester, Player responder) {
						player.getController().cancelUseItem();
						player.getController().cancelTask(TaskId.ITEM_USE);
						player.addItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId(), 0, 0);
						PacketSendUtility.sendPacket(player, new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));
					}
				};
				if (player.getWorldType() == WorldType.ABYSS ||
					player.getWorldType() == WorldType.ELYSEA ||
					player.getWorldType() == WorldType.ASMODAE ||
					player.getWorldType() == WorldType.BALAUREA) {
					roundTrip = true;
					player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_ROUND_RETURN_ITEM_DO_YOU_ACCEPT_MOVE, responseHandler);
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_ROUND_RETURN_ITEM_DO_YOU_ACCEPT_MOVE, parentItem.getObjectId(), 0, new DescriptionId(parentItem.getNameId())));
				} else {
					roundTrip = false;
					player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_ROUND_RETURN_ITEM_ACCEPT_MOVE_DONT_RETURN, responseHandler);
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_ROUND_RETURN_ITEM_ACCEPT_MOVE_DONT_RETURN, parentItem.getObjectId(), 0, new DescriptionId(parentItem.getNameId())));
				}
			} else {
				player.getController().cancelUseItem();
				player.setUsingItem(parentItem);
				skill.setItemObjectId(parentItem.getObjectId());
				skill.useSkill();
				QuestEnv env = new QuestEnv(player.getTarget(), player, 0, 0);
				QuestEngine.getInstance().onUseSkill(env, skillid);
			}
		}
	}
	
	private void roundTripReturn(Player player) {
		if (roundTrip) {
			player.setBattleReturnCoords(player.getWorldId(), new float[] {player.getX(), player.getY(), player.getZ()});
			PacketSendUtility.sendPacket(player, new SM_ROUND_TRIP());
			roundTrip = false;
		}
	}
	
	public int getMapid() {
		if (mapid == null) {
			return 0;
		}
		return mapid;
	}
}