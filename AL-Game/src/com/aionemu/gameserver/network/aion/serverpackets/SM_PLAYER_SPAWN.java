package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceBuff;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.robot.RobotInfo;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author Ranastic (Encom)
 */

public class SM_PLAYER_SPAWN extends AionServerPacket
{
	private final Player player;
	private static InstanceBuff instanceBuff;
	
	public SM_PLAYER_SPAWN(Player player) {
		super();
		this.player = player;
		if (player.isUseRobot() || player.getRobotId() != 0) {
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_USE_ROBOT(player, getRobotInfo(player).getRobotId()));
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(player.getWorldId());
		writeD(player.getWorldId());
		writeD(0x00);
		writeC(WorldMapType.getWorld(player.getWorldId()).isPersonal() ? 1 : 0);
		writeF(player.getX());
		writeF(player.getY());
		writeF(player.getZ());
		writeC(player.getHeading());
		writeD(100);
		writeD(0);
		if (World.getInstance().getWorldMap(player.getWorldId()).getTemplate().getBeginnerTwinCount() > 0) {
            writeC(1);
        } else {
            writeC(0);
        }
		writeC(0);
		writeD(0);
		writeC(0);
	}
	
	public static RobotInfo getRobotInfo(Player player) {
		ItemTemplate template = player.getEquipment().getMainHandWeapon().getItemSkinTemplate();
		return DataManager.ROBOT_DATA.getRobotInfo(template.getRobotId());
	}
}