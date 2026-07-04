package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.templates.event.BoostEvents;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.HashMap;
import java.util.Map;

public class SM_BOOST_EVENTS extends AionServerPacket
{
    private Map<Integer, BoostEvents> boostEvents;
	
    private int buffId;
    private int buffValue;
    long eventStartTime;
    long eventEndTime;
    private HashMap<Integer, BoostEvents> activeEvents;
	private int count = 0;
	
    public SM_BOOST_EVENTS(int buffId, int buffValue, long eventStartTime, long eventEndTime) {
        this.buffId = buffId;
        this.buffValue = buffValue;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }
	
    public SM_BOOST_EVENTS(HashMap<Integer, BoostEvents> activeEvents) {
        this.activeEvents = activeEvents;
    }
	
    @Override
    protected void writeImpl(AionConnection con) {
        writeH(9);
        for (BoostEvents event : activeEvents.values()) {
            writeC(event.getBuffId());
            writeC(1);
            writeQ(event.getStartDate().getMillis() / 1000);
            writeQ(event.getEndDate().getMillis() / 1000);
            writeD(event.getBuffValue());
            writeQ(-1);
            writeD(0);
            writeD(0);
        }
    }
}