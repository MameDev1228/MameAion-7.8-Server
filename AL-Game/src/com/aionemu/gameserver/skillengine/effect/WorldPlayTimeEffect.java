package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WORLD_PLAYTIME;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAttribute;

public class WorldPlayTimeEffect extends EffectTemplate
{
	@XmlAttribute(required = true)
	protected int points;
	
	public void applyEffect(Effect effect) {
		if ((effect.getEffected() instanceof Player)) {
			Player player = (Player) effect.getEffected();
			if (player.getCommonData().getWorldPlayTime() < 300) {
				player.getCommonData().setWorldPlayTime(player.getCommonData().getWorldPlayTime() + points);
				PacketSendUtility.sendPacket(player, new SM_WORLD_PLAYTIME(player));
			}
		}
	}
}