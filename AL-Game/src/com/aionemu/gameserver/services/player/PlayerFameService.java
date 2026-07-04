package com.aionemu.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerFameDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.fame.FameExp;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_FAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PlayerFameService {

    private static final Logger log = LoggerFactory.getLogger(PlayerFameService.class);
    public int MAX_LEVEL = 9;

    public void init() {
        log.info("Player Fame Service Start");
    }

    public void onResetWeekly() {
        List<PlayerFame> famesReduce = getDao().weeklyFame();
        for (PlayerFame fame : famesReduce) {
            long reduce = Math.round(fame.getExp() * (2 / 100));
            if (fame.getExp() - reduce <= 0 ) {
                fame.setLevel(fame.getLevel() - 1);
                long changeExp = getExpForLevel(fame.getLevel()) - Math.round(fame.getExp() * (1.5 / 100));
                fame.setExp(changeExp);
                getDao().reduceWeekly(fame);
            } else {
                fame.setExp(fame.getExp() - reduce);
                getDao().reduceWeekly(fame);
            }
        }
        log.info("Player Fame Service : Weekly Reduce Finish");
    }

    public void recoverExpFame(Player player) {
        PlayerFame fame = fameLevelByWorld(player, player.getWorldId());
        if (fame != null) {
            addFameExp(player, fame.getExpLoss());
            fame.setExpLoss(0);
            getDao().updatePlayerFame(player, fame);
            PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
        }
    }

    public void onPlayerLogin(Player player) {
        player.setPlayerFame(getDao().loadPlayerFame(player));
		for (int i = 1;  i < 8; i++) {
			if (!player.getPlayerFame().containsKey(i)) {
				PlayerFame fame = new PlayerFame(i, 1, 0, 0, player.getObjectId());
				player.getPlayerFame().put(fame.getId(), fame);
				getDao().addPlayerFame(player, fame);
			}
		}
        PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
    }

    public void addFameExp(Player player, long points) {
        for (PlayerFame playerFame : player.getPlayerFame().values()) {
            if (player.getWorldId() == playerFame.getFameEnum().getWorldId()) {
                long exp = playerFame.getExp();
                if (playerFame.getLevel() == MAX_LEVEL && exp + points > getExpForLevel(playerFame.getLevel())) {
                    playerFame.setExp(getExpForLevel(9));
                    getDao().updatePlayerFame(player, playerFame);
                } else if (exp + points >= getExpForLevel(playerFame.getLevel())) {
                    //level up
                    long diff = (exp + points) - getExpForLevel(playerFame.getLevel());
                    playerFame.setLevel(playerFame.getLevel() + 1);
					//Your %0 Renown is now level %num1.
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAME_CHANGE_LEVEL_DONE(playerFame.getFameEnum().getDescriptionId(), playerFame.getLevel()));
                    playerFame.setExp(diff);
                    getDao().updatePlayerFame(player, playerFame);
                } else {
                    playerFame.setExp(exp + points);
                    getDao().updatePlayerFame(player, playerFame);
                }
            }
        }
        PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
    }

    public void onPlayerDie(Player player) {
        for (PlayerFame playerFame : player.getPlayerFame().values()) {
            if (player.getWorldId() == playerFame.getFameEnum().getWorldId()) {
                if (playerFame.getExp() != 0) {
                    int loss = Math.round(playerFame.getExp() * (2 / 100));
                    int unrecoverable = (int) (loss * 0.22222222);
                    int recoverable = (int) loss - unrecoverable;
                    if (playerFame.getLevel() - loss < 0) {
                        playerFame.setExp(0);
                    } else {
                        playerFame.setExp(playerFame.getExp() - loss);
                    }
                    playerFame.setExpLoss(playerFame.getExpLoss() + recoverable);
                    getDao().updatePlayerFame(player, playerFame);
                }
            }
        }
        PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
    }

    public PlayerFame fameLevelByWorld(Player player, int worldId) {
        PlayerFame playerFame = null;
        for (PlayerFame fame : player.getPlayerFame().values()) {
            if (fame.getFameEnum().getWorldId() == worldId) {
                playerFame = fame;
            }
        }
        return playerFame;
    }

    public static PlayerFameService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final PlayerFameService instance = new PlayerFameService();
    }

    public long getExpForLevel(int level) {
        return FameExp.getFameExp(level).getExp();
    }

    public PlayerFameDAO getDao() {
        return DAOManager.getDAO(PlayerFameDAO.class);
    }
}