package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAttribute;

public class ProcVPHealInstantEffect extends EffectTemplate
{
	@XmlAttribute(required = true)
	protected int points;
	
	public void applyEffect(Effect effect) {
		if ((effect.getEffected() instanceof Player)) {
			Player player = (Player) effect.getEffected();
			player.getCommonData().addReposteEnergy(points); //15% = 3750000 Pts.
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			if (player.getCommonData().getCurrentReposteEnergy() == player.getCommonData().getMaxReposteEnergy()) {
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				//The Energy of Repose is ineffective in your current Restriction Phase.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_BOT_CANNOT_RECEIVE_VITAL_BONUS, 0);
				return;
			}
		}
	}
}