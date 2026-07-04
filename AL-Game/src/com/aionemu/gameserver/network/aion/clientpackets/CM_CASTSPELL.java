package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MameClientCompatDebug;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

public class CM_CASTSPELL extends AionClientPacket
{
	private final long receiveTime = System.currentTimeMillis();

	private int spellid;
	private int targetType;
	private float x, y, z;
	@SuppressWarnings("unused")
	private int targetObjectId;
	private int hitTime;
	private int level;
	private int unk;

	public CM_CASTSPELL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		spellid = readH();
		level = readC();
		targetType = readC();
		switch (targetType) {
			case 0:
			case 3:
			case 4:
			case 87:
				targetObjectId = readD();
				break;
			case 1:
				x = readF();
				y = readF();
				z = readF();
				break;
			case 2:
				x = readF();
				y = readF();
				z = readF();
				readF();
				readF();
				readF();
				readF();
				readF();
				readF();
				readF();
				readF();
				break;
			default:
				break;
		}
		hitTime = readH();
		unk = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}

		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(spellid);
		MameClientCompatDebug.logCastPacket(player, spellid, level, targetType, targetObjectId, x, y, z, hitTime, unk, template);

		if (spellid == 0 && player.isCasting()) {
			player.getController().cancelCurrentSkill();
			return;
		}
		if (template == null || template.isPassive()) {
			return;
		}
		if (player.isProtectionActive()) {
			player.getController().stopProtectionActiveTask();
		}

		long nextSkillUse = player.getNextSkillUse();
		if (nextSkillUse > receiveTime) {
			long tooEarlyFromClient = nextSkillUse - receiveTime;
			long tooEarlyNow = nextSkillUse - System.currentTimeMillis();
			int graceMillis = Math.max(0, GSConfig.NEXT_SKILL_USE_GRACE_MILLIS);
			int auditThresholdMillis = Math.max(0, GSConfig.NEXT_SKILL_USE_AUDIT_THRESHOLD_MILLIS);

			if (auditThresholdMillis == 0 || tooEarlyFromClient > auditThresholdMillis) {
				AuditLogger.info(player, "tried to use skill " + spellid + " " + tooEarlyFromClient
					+ " ms too early. grace=" + graceMillis + "ms");
			}

			if (tooEarlyNow > graceMillis) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300021));
				return;
			}
		}

		if (!player.getLifeStats().isAlreadyDead()) {
			player.getController().useSkill(template, targetType, x, y, z, hitTime, level);
		}
	}
}
