package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

public class CM_TARGET_SELECT extends AionClientPacket
{
	private int targetObjectId;
	private int type;
	
	public CM_TARGET_SELECT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		targetObjectId = readD();
		type = readC();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		VisibleObject obj = null;
		if (targetObjectId == player.getObjectId()) {
			obj = player;
		} else {
			obj = player.getKnownList().getObject(targetObjectId);
			if (obj == null && player.isInTeam()) {
				TeamMember<Player> member = player.getCurrentTeam().getMember(targetObjectId);
				if (member != null) {
					obj = member.getObject();
				}
			}
		} if (obj != null) {
			if (type == 1) {
				if (obj.getTarget() == null) {
					return;
				}
				player.setTarget(obj.getTarget());
			} else {
				player.setTarget(obj);
			} if (obj instanceof Player) {
				Player target = (Player) obj;
				if (player != obj && !player.canSee(target)) {
					AuditLogger.info(player,"Possible radar hacker detected, targeting on invisible Player name: " + target.getName() + " objectId: " + target.getObjectId() + " by");
				}
			} else if (obj instanceof Trap) {
				Trap target = (Trap) obj;
				boolean isSameTeamTrap = false;
				if (target.getMaster() instanceof Player) {
					isSameTeamTrap = ((Player) target.getMaster()).isInSameTeam(player);
				}
				if (player != obj && !player.canSee(target) && !isSameTeamTrap) {
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Trap name: " + target.getName() + " objectId: " + target.getObjectId() + " by");
				}
			} else if (obj instanceof Creature) {
				Creature target = (Creature) obj;
				if (player != obj && !player.canSee(target)) {
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Npc name: " + target.getName() + " objectId: " + target.getObjectId() + " by");
				}
			}
		} else {
			player.setTarget(null);
		}
		sendPacket(new SM_TARGET_SELECTED(player));
		PacketSendUtility.broadcastPacket(player, new SM_TARGET_UPDATE(player));
	}
}