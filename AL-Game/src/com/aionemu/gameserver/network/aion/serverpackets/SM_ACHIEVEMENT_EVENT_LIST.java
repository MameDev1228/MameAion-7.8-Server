package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;
import com.aionemu.gameserver.model.templates.achievement.AchievementEventTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ACHIEVEMENT_EVENT_LIST extends AionServerPacket {

    private Player player;

    public SM_ACHIEVEMENT_EVENT_LIST(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(player.getPlayerEventAchievements().size());
        for(PlayerAchievement achievement : player.getPlayerEventAchievements().values()) {
            writeC(1); //active
            writeQ(achievement.getObjectId());
            writeD(achievement.getId());
            writeD(0);
            writeD(0);
            writeC(achievement.getType().getValue());
            writeC(achievement.getType() == AchievementType.EVENT_MAIN ? 0 : 2);
            writeC(achievement.getState().getValue());
            writeD(achievement.getStep());
            writeC(0); //??
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeQ(achievement.getStartDate().getTime() / 1000);
            writeQ(achievement.getEndateDate().getTime() / 1000);
        }
        /*
            07 00 //size
            01 //active
            2E DD 4F 00 00 00 00 00 //object id
            07 04 00 00 //id
            00 00 00 00
            00 00 00 00
            05 // type 5 (event)
            01 //1
            01 //state
            00 00 00 00 //step
            00
            00 00 00 00
            00 00 00 00
            00 00 00 00   ................
            00 00 00 00
            00 00 00 00
            80 0C 62 5C 00 00 00 00  //start date
            16 5B 62 5C 00 00 00 00 //end date

         */

       // writeB("0700012EDD4F0000000000 0704 0000000000000000000005010100000000000000000000000000000000000000000000000000800C625C00000000165B625C00000000012FDD4F00000000000804000000000000000000000503010000000000000000000000000000000000000000000000000080235A5C00000000165B625C000000000131DD4F00000000000904000000000000000000000502010000000000000000000000000000000000000000000000000080235A5C00000000165B625C000000000133DD4F00000000000A04000000000000000000000504010000000000000000000000000000000000000000000000000080235A5C00000000165B625C000000000136DD4F00000000000B04000000000000000000000502010000000000000000000000000000000000000000000000000080235A5C00000000165B625C000000000138DD4F00000000000D04000000000000000000000503010000000000000000000000000000000000000000000000000080235A5C00000000165B625C00000000012DDD4F00000000000604000000000000000000000400010000000000000000000000000000000000000000000000000080235A5C00000000165B625C00000000");
    }
}
