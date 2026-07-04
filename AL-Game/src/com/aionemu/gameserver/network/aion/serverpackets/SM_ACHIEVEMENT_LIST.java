package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementAction;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.player.AchievementService;

/**
 * @author Krz on décembre 2018
 */

public class SM_ACHIEVEMENT_LIST extends AionServerPacket {

    private Player player;

    public SM_ACHIEVEMENT_LIST(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeQ(AchievementService.getInstance().getLastUpdate().getTime() / 1000); //last Update
        writeH(player.getPlayerAchievements().size());
        for(PlayerAchievement achievement : player.getPlayerAchievements().values()) {
            writeC(1); //active
            writeQ(achievement.getObjectId());
            writeD(achievement.getId());
            writeD(0);
            writeD(0);
            writeC(achievement.getType().getValue());
            writeC(0);
            writeC(achievement.getState().getValue());
            writeD(achievement.getStep());
            writeC(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeQ(achievement.getStartDate().getTime() / 1000);
            writeQ(achievement.getEndateDate().getTime() / 1000);
            writeH(achievement.getActionMap().size());
            for (AchievementAction action : achievement.getActionMap().values()) {
                writeQ(action.getObjectId());
                writeD(action.getId());
                writeQ(action.getAchievementObjectId());
                writeC(0);
                writeC(0);
                writeC(action.getState().getValue());
                writeD(action.getStep());
                writeC(0);
                writeD(0);
                writeD(0);
                writeD(0);
                writeD(0);
                writeD(0);
                writeQ(action.getStartDate().getTime() / 1000);
                writeQ(action.getEndateDate().getTime() / 1000);
            }
        }
    }
}
