/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.PlayerAchievementActionDAO;
import com.aionemu.gameserver.dao.PlayerAchievementDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementAction;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementState;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;
import com.aionemu.gameserver.model.templates.achievement.*;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACHIEVEMENT_EVENT_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACHIEVEMENT_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACHIEVEMENT_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_COMPLETE_ACHIEVEMENT;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastList;

import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Krz on décembre 2018
 */

public class AchievementService
{
    private Logger log = LoggerFactory.getLogger(AchievementService.class);
    private List<AchievementTemplate> daily = new FastList<AchievementTemplate>();
    private List<AchievementTemplate> weekly = new FastList<AchievementTemplate>();
    private PlayerAchievementDAO dao = DAOManager.getDAO(PlayerAchievementDAO.class);
    private PlayerAchievementActionDAO dao2 = DAOManager.getDAO(PlayerAchievementActionDAO.class);
    private Timestamp lastUpdate;
    private Timestamp lastUpdateEvent;
    private List<AchievementEventTemplate> activeEventEly = new FastList<AchievementEventTemplate>();
    private List<AchievementEventTemplate> activeEventAsmo = new FastList<AchievementEventTemplate>();

    public void init() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        log.info("Lugbug Mission Reset");
        if (daily.size() == 0) {
            generateAchievements();
            lastUpdate = new Timestamp(System.currentTimeMillis());
        }
        generateAchievementsEvent();
        lastUpdateEvent = new Timestamp(System.currentTimeMillis());
    }

    public void generateAchievementsEvent() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        List<AchievementEventTemplate> achievements = DataManager.ACHIEVEMENT_EVENT_DATA.getAchievements();
        for (AchievementEventTemplate template : achievements) {
            if(template.getActive() && template.getStartDate().isBeforeNow() && template.getEndDate().isAfterNow()) {
                if(template.getRace() == Race.ELYOS) {
                    activeEventEly.add(template);
                } else if(template.getRace() == Race.ASMODIANS) {
                    activeEventAsmo.add(template);
                } else {
                    activeEventEly.add(template);
                    activeEventAsmo.add(template);
                }

            }
        }
    }
	
    public void generateAchievements() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        List<AchievementTemplate> achievements = DataManager.ACHIEVEMENT_DATA.getDaily();
        List<AchievementTemplate> achievements2 = DataManager.ACHIEVEMENT_DATA.getWeekly();
        for (AchievementTemplate template : achievements2) {
            if (template.getRepeat() == AchievementRepeat.ALL) {
                weekly.add(template);
            }
        } switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                for (AchievementTemplate template : achievements) {
                    if (template.getRepeat() == AchievementRepeat.MON) {
                        daily.add(template);
                    }
                }
            break;
            case Calendar.TUESDAY:
                for (AchievementTemplate template : achievements) {
                    if (template.getRepeat() == AchievementRepeat.TUE) {
                        daily.add(template);
                    }
                }
            break;
            case Calendar.WEDNESDAY:
                for (AchievementTemplate template : achievements) {
                    if (template.getRepeat() == AchievementRepeat.WED) {
                        daily.add(template);
                    }
                }
            break;
            case Calendar.THURSDAY:
                for (AchievementTemplate template : achievements) {
                    if (template.getRepeat() == AchievementRepeat.THU) {
                        daily.add(template);
                    }
                }
            break;
            case Calendar.FRIDAY:
                for (AchievementTemplate template : achievements) {
                    if (template.getRepeat() == AchievementRepeat.FRI) {
                        daily.add(template);
                    }
                }
            break;
            case Calendar.SATURDAY:
                for (AchievementTemplate template : achievements) {
                    if (template.getRepeat() == AchievementRepeat.SAT) {
                        daily.add(template);
                    }
                }
            break;
            case Calendar.SUNDAY:
                for (AchievementTemplate template : achievements) {
                    if (template.getRepeat() == AchievementRepeat.SUN) {
                        daily.add(template);
                    }
                }
            break;
        }
    }
	
    public void onEnterWorld(Player player) {
        AchievementTemplate template;
        Timestamp now = new Timestamp(lastUpdate.getTime());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now.getTime());
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Timestamp dailyExprire = new Timestamp(c.getTime().getTime());
        dao.loadAchievements(player);
        if (player.getPlayerAchievements().size() == 0) {
            if (player.getLevel() <= 75) {
                template = getAllraceDaily();
                PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
                for (Integer actions : template.getActions().getIds()) {
                    AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
                    AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
                    daily.getActionMap().put(action.getObjectId(), action);
                    dao2.storeAction(player, action);
                }
                player.getPlayerAchievements().put(daily.getId(), daily);
                dao.storeAchievement(player, daily);
            } else {
                if (player.getRace() == Race.ELYOS) {
                    template = getElyosDaily();
                } else {
                    template = getAsmoDaily();
                }
                PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
                for (Integer actions : template.getActions().getIds()) {
                    AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
                    AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
                    daily.getActionMap().put(action.getObjectId(), action);
                    dao2.storeAction(player, action);
                }
                player.getPlayerAchievements().put(daily.getId(), daily);
                dao.storeAchievement(player, daily);
            } if (player.getLevel() <= 75) {
                if (!playerHaveAchievement(player, template.getId())) {
                    PlayerAchievement weekly = new PlayerAchievement(template.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire);
                    for (Integer actions : template.getActions().getIds()) {
                        AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
                        AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire, weekly.getObjectId());
                        weekly.getActionMap().put(action.getObjectId(), action);
                        dao2.storeAction(player, action);
                    }
                    player.getPlayerAchievements().put(weekly.getId(), weekly);
                    dao.storeAchievement(player, weekly);
                }
            } else {
                if (player.getRace() == Race.ELYOS) {
                    template = DataManager.ACHIEVEMENT_DATA.getAchievementId(50); //Weekly_76_80_L 7.5
					//template = DataManager.ACHIEVEMENT_DATA.getAchievementId(66); //Weekly_76_80_L 7.7
                } else {
                    template = DataManager.ACHIEVEMENT_DATA.getAchievementId(58); //Weekly_76_80_D 7.5
					//template = DataManager.ACHIEVEMENT_DATA.getAchievementId(74); //Weekly_76_80_D 7.7
                } if (!playerHaveAchievement(player, template.getId())) {
                    PlayerAchievement weekly = new PlayerAchievement(template.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire);
                    for (Integer actions : template.getActions().getIds()) {
                        AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
                        AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire, weekly.getObjectId());
                        weekly.getActionMap().put(action.getObjectId(), action);
                        dao2.storeAction(player, action);
                    }
                    player.getPlayerAchievements().put(weekly.getId(), weekly);
                    dao.storeAchievement(player, weekly);
                }
            }
        } if (player.getPlayerAchievements().size() == 1) {
            if (player.getLevel() <= 75) {
                template = getAllraceDaily();
                PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
                for (Integer actions : template.getActions().getIds()) {
                    AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
                    AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
                    daily.getActionMap().put(action.getObjectId(), action);
                    dao2.storeAction(player, action);
                }
                player.getPlayerAchievements().put(daily.getId(), daily);
                dao.storeAchievement(player, daily);
            } else {
                if (player.getRace() == Race.ELYOS) {
                    template = getElyosDaily();
                } else {
                    template = getAsmoDaily();
                }
                PlayerAchievement daily = new PlayerAchievement(template.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire);
                for (Integer actions : template.getActions().getIds()) {
                    AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
                    AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.DAILY, AchievementState.START, 0, lastUpdate, dailyExprire, daily.getObjectId());
                    daily.getActionMap().put(action.getObjectId(), action);
                    dao2.storeAction(player, action);
                }
                player.getPlayerAchievements().put(daily.getId(), daily);
                dao.storeAchievement(player, daily);
            }
        }

        if(player.getPlayerEventAchievements().size() == 0) {
            if(player.getRace() == Race.ELYOS) {
                for (AchievementEventTemplate templates : getActiveEventEly()) {
                    if (player.getLevel() >= templates.getMinlevel() && player.getLevel() <= templates.getMaxlevel()) {
                        PlayerAchievement event = new PlayerAchievement(templates.getId(), templates.getType(), AchievementState.START, 0, new Timestamp(templates.getStartDate().getMillis()), new Timestamp(templates.getEndDate().getMillis()));
                        player.getPlayerEventAchievements().put(event.getId(), event);
                        dao.storeAchievement(player, event);
                    }
                }
            } else {
                for (AchievementEventTemplate templates : getActiveEventAsmo()) {
                    if (player.getLevel() >= templates.getMinlevel() && player.getLevel() <= templates.getMaxlevel()) {
                        PlayerAchievement event = new PlayerAchievement(templates.getId(), templates.getType(), AchievementState.START, 0, new Timestamp(templates.getStartDate().getMillis()), new Timestamp(templates.getEndDate().getMillis()));
                        player.getPlayerEventAchievements().put(event.getId(), event);
                    }
                }
            }

        }
        PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
        PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_EVENT_LIST(player));
    }
	
    public void onUpdateAchievementAction(Player player, int value, int count, AchievementActionType type) {
        for (PlayerAchievement achievements : player.getPlayerAchievements().values()) {
            PlayerAchievement playerAchievement = achievements;
            for (AchievementAction actions : playerAchievement.getActionMap().values()) {
                AchievementActionTemplate template = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions.getId());
                if (template.getType() == type && actions.getState() == AchievementState.START) {
                    for (Integer ids: template.getRequired().getValues()) {
                        if (ids == value && template.getRequired().getType() == ActionRequiredType.SPECIFIC) {
                            int progress = actions.getStep() + count;
                            if (progress >= template.getMaxvalue()) {
                                actions.setStep(template.getMaxvalue());
                                actions.setState(AchievementState.REWARD);
                                achievements.setStep(achievements.getStep() + 1);
                                PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.REWARD));
                                PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(achievements.getObjectId(), achievements.getStep(), AchievementState.START));
                                dao.update(player, achievements);
                                dao2.update(player, actions);
                            } else {
                                actions.setStep(progress);
                                PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.START));
                                dao2.update(player, actions);
                            }
                        }
                    } if (template.getType() == type && template.getRequired().getType() == ActionRequiredType.ALL && actions.getState() == AchievementState.START) {
                        int progress = actions.getStep() + count;
                        if (progress >= template.getMaxvalue()) {
                            actions.setStep(template.getMaxvalue());
                            actions.setState(AchievementState.REWARD);
                            achievements.setStep(achievements.getStep() + 1);
                            PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.REWARD));
                            PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(achievements.getObjectId(), achievements.getStep(), AchievementState.START));
                            dao.update(player, achievements);
                            dao2.update(player, actions);
                        } else {
                            actions.setStep(progress);
                            PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(actions.getObjectId(), actions.getStep(), AchievementState.START));
                            dao2.update(player, actions);
                        }
                    }
                }
            }
        }
    }

    public void onLeveUplPlayer(Player player) {
        AchievementTemplate template;
        Timestamp now = new Timestamp(lastUpdate.getTime());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now.getTime());
        c.add(Calendar.DAY_OF_WEEK, 7);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Timestamp dailyExprire = new Timestamp(c.getTime().getTime());
        if (player.getRace() == Race.ELYOS) {
            template = DataManager.ACHIEVEMENT_DATA.getAchievementId(50); //Weekly_76_80_L 7.5
			//template = DataManager.ACHIEVEMENT_DATA.getAchievementId(66); //Weekly_76_80_L 7.7
        } else {
            template = DataManager.ACHIEVEMENT_DATA.getAchievementId(58); //Weekly_76_80_D 7.5
			//template = DataManager.ACHIEVEMENT_DATA.getAchievementId(74); //Weekly_76_80_D 7.7
        } if (!playerHaveAchievement(player, template.getId())) {
            PlayerAchievement weekly = new PlayerAchievement(template.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire);
            for (Integer actions : template.getActions().getIds()) {
                AchievementActionTemplate actionTemplate = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(actions);
                AchievementAction action = new AchievementAction(actionTemplate.getId(), AchievementType.WEEKLY, AchievementState.START, 0, lastUpdate, dailyExprire, weekly.getObjectId());
                weekly.getActionMap().put(action.getObjectId(), action);
                dao2.storeAction(player, action);
            }
            player.getPlayerAchievements().put(weekly.getId(), weekly);
            dao.storeAchievement(player, weekly);
        }
        PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_EVENT_LIST(player));
        PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
    }
	
    public void onRewardAction(Player player, int objectId, int templateId) {
            AchievementAction action = getActionbyObj(player, objectId);
            AchievementActionTemplate template = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(templateId);
            action.setState(AchievementState.COMPLETE);
            for (ActionsItems items : template.getRewards().getAchievementItems()) {
                ItemService.addItem(player, items.getItemId(), items.getCount());
            }
            PacketSendUtility.sendPacket(player, new SM_COMPLETE_ACHIEVEMENT(templateId, action.getAchievementObjectId(), objectId));
            dao2.update(player, action);
            PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
    }

    public void onRewardAchievement(Player player, int templateId) {
            PlayerAchievement achievement = getAchievementbyId(player, templateId);
            AchievementTemplate template = DataManager.ACHIEVEMENT_DATA.getAchievementId(achievement.getId());
            achievement.setState(AchievementState.COMPLETE);
            for (AchievementItems items : template.getRewards().getAchievementItems()) {
                ItemService.addItem(player, items.getItemId(), items.getCount());
            }
            PacketSendUtility.sendPacket(player, new SM_COMPLETE_ACHIEVEMENT(templateId, achievement.getObjectId(), 0));
            dao.update(player, achievement);
            PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_LIST(player));
    }
	
    public AchievementTemplate getAllraceDaily() {
        AchievementTemplate tmp = null;
        for (AchievementTemplate template : daily) {
            if (template.getRace() == Race.PC_ALL) {
                tmp = template;
            }
        }
        return tmp;
    }
	
    public AchievementTemplate getElyosDaily() {
        AchievementTemplate tmp = null;
        for (AchievementTemplate template : daily) {
            if (template.getRace() == Race.ELYOS) {
                tmp = template;
            }
        }
        return tmp;
    }
	
    public AchievementTemplate getAsmoDaily() {
        AchievementTemplate tmp = null;
        for (AchievementTemplate template : daily) {
            if (template.getRace() == Race.ASMODIANS) {
                tmp = template;
            }
        }
        return tmp;
    }
	
    public AchievementAction getActionbyObj(Player player, int obj) {
        AchievementAction action = null;
        for (PlayerAchievement achievement : player.getPlayerAchievements().values()) {
            if (achievement.getActionMap().containsKey(obj)) {
                action = achievement.getActionMap().get(obj);
            }
        }
        return action;
    }
	
    public PlayerAchievement getAchievementbyObj(Player player, int obj) {
        PlayerAchievement achievement = null;
        if (player.getPlayerAchievements().containsKey(obj)) {
            achievement = player.getPlayerAchievements().get(obj);
        }
        return achievement;
    }

    public PlayerAchievement getAchievementbyId(Player player, int id) {
        PlayerAchievement achievement = null;
        for(PlayerAchievement ach : player.getPlayerAchievements().values()) {
            if(ach.getId() == id) {
                achievement = ach;
            }

        }
        return achievement;
    }

    public void completeAchievementEvent(Player player, int templateId) {
        AchievementEventTemplate template = DataManager.ACHIEVEMENT_EVENT_DATA.getAchievementId(templateId);
        PlayerAchievement event = getAchievementEventbyId(player, templateId);
        if(template.getType() == AchievementType.EVENT_SUB) {
            int point = template.getCompletePoint();
            AchievementActionTemplate action = DataManager.ACHIEVEMENT_ACTION_DATA.getAchievementActionId(template.getActionId());
            for (ActionsItems rewards : action.getRewards().getAchievementItems()) {
                ItemService.addItem(player, rewards.getItemId(), rewards.getCount());
            }
            event.setState(AchievementState.COMPLETE);
            dao.update(player, event);
            //PacketSendUtility.sendPacket(player, new SM_COMPLETE_ACHIEVEMENT(action.getId(), event.getObjectId(), 0));
            PlayerAchievement main = getAchievementEventMain(player);
            main.setStep(main.getStep() + point);
            dao.update(player, main);
            //PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_UPDATE(main.getId(), main.getStep(), AchievementState.START));
        } else {
            event.setState(AchievementState.COMPLETE);
            dao.update(player, event);
        }
        PacketSendUtility.sendPacket(player, new SM_ACHIEVEMENT_EVENT_LIST(player));
    }

    public PlayerAchievement getAchievementEventbyId(Player player, int id) {
        PlayerAchievement achievement = null;
        for(PlayerAchievement ach : player.getPlayerEventAchievements().values()) {
            if(ach.getId() == id) {
                achievement = ach;
            }

        }
        return achievement;
    }
    public PlayerAchievement getAchievementEventMain(Player player) {
        PlayerAchievement achievement = null;
        for(PlayerAchievement ach : player.getPlayerEventAchievements().values()) {
            if(ach.getType() == AchievementType.EVENT_MAIN) {
                achievement = ach;
            }

        }
        return achievement;
    }
	
    public boolean playerHaveAchievement(Player player, int id) {
        if (player.getPlayerAchievements().size() == 0) {
            return false;
        } else {
            for (PlayerAchievement achievement : player.getPlayerAchievements().values()) {
                if (achievement.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }
	
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
	
    public PlayerAchievementDAO getDao() {
        return dao;
    }
	
    public PlayerAchievementActionDAO getDao2() {
        return dao2;
    }

    public List<AchievementEventTemplate> getActiveEventEly() {
        return activeEventEly;
    }

    public List<AchievementEventTemplate> getActiveEventAsmo() {
        return activeEventEly;
    }

    public static AchievementService getInstance() {
        return SingletonHolder.instance;
    }
	
    private static class SingletonHolder {
        protected static final AchievementService instance = new AchievementService();
    }
}