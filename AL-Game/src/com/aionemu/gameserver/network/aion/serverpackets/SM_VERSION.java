package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.LunaConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.EventService;

/**
 * Created by wanke on 14/02/2017.
 */

public class SM_VERSION extends AionServerPacket {
	
    @Override
    protected void writeImpl(AionConnection con) {
		
        writeC(0x01);
        // Start //
        writeC(0x00);
        writeC(0x00); //nnn
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00); //nnn
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
		// End //
		
        writeC(0x01);
        writeH(350);
        writeH(257);
        writeH(2561);
        writeH(13061);
        writeH(257);
        writeH(0x02);
		
        writeC(GSConfig.CHARACTER_REENTRY_TIME);
        writeD(EventsConfig.ENABLE_DECOR);
        writeC(EventService.getInstance().getEventType().getId()); // 18 Summer Splash V1 / 20 Summer Splash V2
		
        writeC(4);
        writeC(1);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
		
        writeC(1);
        writeC(1);
        writeC(1);
		
        writeD(1065353216);
        writeC(1);
        writeC(5);
        writeC(0);
		
        writeC(0);
        writeC(0);
        writeC(1);
		
        writeD(100);
        writeC(1);
        writeC(GSConfig.CHARACTER_LIMIT_COUNT); //max player account (12 in 7.0)
		
        writeD(1065353216);
        writeC(1); //1
		
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
		
        writeD(190200100); //Minion Function Stone Id
        writeC(1); //1
        writeC(0);
  
        writeC(1); // 6.0 Disabling Cp Panel
        writeC(1); // 6.0 unk need find 1 on kr
		
        writeC(0); // 6.0 The privilege of the returning beginner to turn off the display
		
        writeC(1); // 6.2 Quna System KR [1 desactive / 0 activate]
        writeC(0); // 6.2
		
        writeD(50000); // 6.5
        writeD(0); // 6.5
        writeD(0); // 6.5
        writeD(0); // 6.5
        writeD(0); // 6.5

        writeD(0); // 7.2
        writeD(3000); // 7.2

        writeB("00000000B80B0000000900000090D0030000000000CDCCCC3DCDCCCC3D0000003FCDCCCC3D"); //7.7
    }
}