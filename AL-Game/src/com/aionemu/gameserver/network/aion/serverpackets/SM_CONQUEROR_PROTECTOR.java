package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.Collection;

public class SM_CONQUEROR_PROTECTOR extends AionServerPacket
{
	private int type;
    private int debuffLvl;
    private Collection<Player> players;
    private Player player;

	public SM_CONQUEROR_PROTECTOR(boolean showMsg, int debuffLvl) {
        this.type = showMsg ? 1 : 0;
        this.debuffLvl = debuffLvl;
    }
	
    public SM_CONQUEROR_PROTECTOR(Collection<Player> players, boolean intruderRadar) {
        this.type = intruderRadar ? 5 : 4;
        this.players = players;
    }
	
    public SM_CONQUEROR_PROTECTOR(Player player, boolean isProtector, boolean broadcastPacket, int buffLvl) {
        this.player = player;
        if (broadcastPacket) {
            this.type = isProtector ? 9 : 6;
        } else {
            this.type = isProtector ? 8 : 1;
        }
        this.debuffLvl = buffLvl;
    }
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(type);
        writeD(1);
        writeD(1);
		switch (type) {
			case 0:
            case 1:
            case 6:
            case 8:
            case 9:
                writeH(1);
                writeD(debuffLvl);
                writeD(type == 9 || type == 6 ? player.getObjectId() : 0);
            break;
			case 4: //Automatic Territory Intruder Scan.
                writeH(players.size());
                for (Player player : players) {
                    writeD(player.getProtectorInfo().getRank());
                    writeD(player.getProtectorInfo().getType());
                    writeD(player.getConquerorInfo().getRank());
                    writeD(player.getObjectId());
                    writeD(0x01);
                    writeD(player.getAbyssRank().getRank().getId());
                    writeH(player.getLevel());
                    writeF(player.getX());
                    writeF(player.getY());
                    writeS(player.getName(), 134);
                    writeH(4);
                }
            break;
			case 5: //Intruder Radar.
                writeH(players.size());
                for (Player player : players) {
                    writeD(player.getProtectorInfo().getRank());
                    writeD(player.getProtectorInfo().getType());
                    writeD(player.getConquerorInfo().getRank());
                    writeD(player.getObjectId());
                    writeD(0x01);
                    writeD(player.getAbyssRank().getRank().getId());
                    writeH(player.getLevel());
                    writeF(player.getX());
                    writeF(player.getY());
                    writeS(player.getName(), 134);
                    writeH(4);
                }
            break;
		}
	}
}