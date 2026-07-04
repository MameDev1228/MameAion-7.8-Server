package com.aionemu.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.LoginEventDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.account.AccountLoginEvent;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.loginevents.LoginEventTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOGIN_EVENT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class LoginEventService
{
    LoginEventDAO dao = DAOManager.getDAO(LoginEventDAO.class);
	
    public void onLogin(Player player) {
        if (player.getLoginEvent() == null) {
            player.setLoginEvent(new AccountLoginEvent(11, 1, true, 0));
            dao.add(player);
        }
        PacketSendUtility.sendPacket(player, new SM_LOGIN_EVENT(player));
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ATTEND_MSG_ATTEND_REWARD_GET);
    }
	
    public void onReset() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            DAOManager.getDAO(LoginEventDAO.class).weeklyUpdate();
        } else {
            DAOManager.getDAO(LoginEventDAO.class).dailyUpdate();
        }
    }
	
    public void getReward(Player player, int atreianId) {
        AccountLoginEvent login = player.getLoginEvent();
        login.setAvailable(false);
        login.setStamp(login.getStamp() + 1);
        login.setCollected(login.getCollected() + 1);
        dao.update(player);
        //item reward
        LoginEventTemplate template = DataManager.LOGIN_EVENT_DATA.getLoginEvent(atreianId);
        ItemService.addItem(player, template.getStampRewards().get(login.getStamp() - 1).getItemId(), template.getStampRewards().get(login.getStamp() - 1).getCount());
        PacketSendUtility.sendPacket(player, new SM_LOGIN_EVENT(player));
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ATTEND_MSG_ATTEND_REWARD_GET);
    }
	
    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final LoginEventService instance = new LoginEventService();
    }
	
    public static LoginEventService getInstance() {
        return SingletonHolder.instance;
    }
}