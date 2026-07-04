package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;

public class FlyRingService
{
	private static class SingletonHolder {
		protected static final FlyRingService instance = new FlyRingService();
	}
	
	public static final FlyRingService getInstance() {
		return SingletonHolder.instance;
	}
	
	private FlyRingService() {
		for (FlyRingTemplate t: DataManager.FLY_RING_DATA.getFlyRingTemplates()) {
			FlyRing f = new FlyRing(t, 0);
			f.spawn();
		}
	}
}