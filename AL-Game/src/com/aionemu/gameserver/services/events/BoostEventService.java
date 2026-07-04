package com.aionemu.gameserver.services.events;

import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.bonus_service.BoostEventBonus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.templates.event.BoostEvents;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BOOST_EVENTS;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanke on 18/02/2017.
 */

public class BoostEventService implements StatOwner
{

    private static final Logger log = LoggerFactory.getLogger(BoostEventService.class);
	
    public Map<Integer, BoostEvents> data = new HashMap<Integer, BoostEvents>(1);
	
    public void onStart() {
        if (!EventsConfig.ENABLE_BOOST_EVENT) {
            log.info("[BoostEventService] Disabled by config.");
            return;
        }
        Map<Integer, BoostEvents> raw = DataManager.BOOST_EVENT_DATA.getAll();
        if (raw.size() != 0) {
            getBoostEvent(raw);
        }
    }
	
    public void onLogin(Player player) {
        if (!EventsConfig.ENABLE_BOOST_EVENT) {
            return;
        }
        for (BoostEvents boostEvents : getCurrentBoost().values()) {
            final int bonus = boostEvents.getBuffValue() - 100;
            player.getGameStats().getStat(boostEvents.getStatEnum(), 100).addToBonus(bonus);
        }
		PacketSendUtility.sendPacket(player, new SM_BOOST_EVENTS((HashMap<Integer, BoostEvents>) getCurrentBoost()));
    }

    public void onLogout(Player player) {
        if (!EventsConfig.ENABLE_BOOST_EVENT) {
            return;
        }
        for (BoostEvents boostEvents : getCurrentBoost().values()) {
            final int bonus = boostEvents.getBuffValue() - 100;
            player.getGameStats().getStat(boostEvents.getStatEnum(), 100).addToBonus(-bonus);
        }
    }
	
    public Map<Integer, BoostEvents> getCurrentBoost() {
        Map<Integer, BoostEvents> boost = new HashMap<Integer, BoostEvents>();
        for (BoostEvents be : data.values()) {
            if (be.getStartDate().isBeforeNow() && be.getEndDate().isAfterNow()) {
                boost.put(be.getId(), be);
            }
        }
        return boost;
    }
	
    public void getBoostEvent(int id, BoostEvents be) {
        if (data.containsValue(id)) {
            return;
		}
        data.put(id, be);
    }
	
    public void getBoostEvent(Map<Integer, BoostEvents> raw) {
        data.putAll(raw);
        for (BoostEvents be : data.values()) {
            getBoostEvent(be.getId(), be);
        }
        log.info("[BoostEventService] Loaded " + data.size() + " Event Boost");
    }
	
    public static final BoostEventService getInstance() {
        return SingletonHolder.instance;
    }
	
    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final BoostEventService instance = new BoostEventService();
    }
}