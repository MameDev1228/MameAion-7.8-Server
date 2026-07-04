package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.MinionController;
import com.aionemu.gameserver.controllers.movement.MinionMoveController;
import com.aionemu.gameserver.controllers.movement.MoveController;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.minion.MinionTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author Ranastic
 */

public class Minion extends VisibleObject
{
	private final Player master;
	private MoveController moveController;
	private final MinionTemplate minionTemplate;
	
	public Minion(MinionTemplate minionTemplate, MinionController controller, MinionCommonData commonData, Player master) {
		super(commonData.getObjectId(), controller, null, commonData, new WorldPosition(master.getWorldId()));
		controller.setOwner(this);
		this.master = master;
		this.minionTemplate = minionTemplate;
		this.moveController = new MinionMoveController();
	}
	
	public Player getMaster() {
		return master;
	}
	public int getMinionId() {
		return objectTemplate.getTemplateId();
	}
	@Override
	public String getName() {
		return objectTemplate.getName();
	}
	public final MinionCommonData getCommonData() {
		return (MinionCommonData) objectTemplate;
	}
	public final MoveController getMoveController() {
		return moveController;
	}
	public final MinionTemplate getMinionTemplate() {
		return minionTemplate;
	}
}