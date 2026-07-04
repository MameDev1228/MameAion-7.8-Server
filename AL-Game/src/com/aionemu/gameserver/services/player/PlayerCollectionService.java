package com.aionemu.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerCollectionDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollection;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionInfos;
import com.aionemu.gameserver.model.templates.collection.CollectionExpTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionType;
import com.aionemu.gameserver.model.templates.collection.RewardCollectionTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerCollectionService {
    private static final Logger log = LoggerFactory.getLogger(PlayerCollectionService.class);

    public void init() {
        log.info("Player Collection Service Start");
    }

    public void onLogin(Player player) {
        player.setPlayerCollection(new PlayerCollection());
        player.getPlayerCollection().setCollectionInfos(getDao().loadPlayerCollection(player));
        getDao().loadCollectionEntry(player);
        if(player.getPlayerCollection().getCollectionInfos().size() == 0) {
            PlayerCollection collection = new PlayerCollection();
            collection.getCollectionInfos().put(CollectionType.COMMON, new PlayerCollectionInfos(CollectionType.COMMON, 1 , 0));
            collection.getCollectionInfos().put(CollectionType.ANCIENT, new PlayerCollectionInfos(CollectionType.ANCIENT, 1 , 0));
            collection.getCollectionInfos().put(CollectionType.RELIC, new PlayerCollectionInfos(CollectionType.RELIC, 1 , 0));
            collection.getCollectionInfos().put(CollectionType.EVENT, new PlayerCollectionInfos(CollectionType.EVENT, 1 , 0));
            player.setPlayerCollection(collection);
            for (PlayerCollectionInfos infos : player.getPlayerCollection().getCollectionInfos().values()) {
                getDao().insertPlayerCollection(player, infos);
            }
        }
        PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION(player));
        PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_FINISH(player));
        PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_UNK());
        PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_PROGRESS(player));
        for(PlayerCollectionEntry complete : player.getPlayerCollection().getCompleteCollection()) {
            complete.apply(player);
        }
        for (PlayerCollectionInfos infos : player.getPlayerCollection().getCollectionInfos().values()) {
            infos.apply(player);
        }
    }

    public void onLogout(Player player) {
        for(PlayerCollectionEntry complete : player.getPlayerCollection().getCompleteCollection()) {
            complete.end(player);
        }
        for (PlayerCollectionInfos infos : player.getPlayerCollection().getCollectionInfos().values()) {
            infos.end(player);
        }
    }

    public void registerCollection(Player player, int id, int index, int objectId, int count) {
        log.info("index : " + index);
        int matSize = DataManager.COLLECTION_TEMPLATE_DATA.getTemplate(id).getMaterials().size();
        PlayerCollectionEntry entry = null;
        if(player.getPlayerCollection().getPlayerCollectionEntry().containsKey(id)) {
            entry = player.getPlayerCollection().getPlayerCollectionEntry().get(id);
        } else {
            entry = new PlayerCollectionEntry(id, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0);
            getDao().insertCollection(player, entry);
            player.getPlayerCollection().getPlayerCollectionEntry().put(entry.getId(), entry);
        }
        boolean delete = player.getInventory().decreaseByObjectId(objectId, count);
        if(delete) {
            entry.update(index);
            entry.setStep(entry.getStep() + 1);
            PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_REGISTER(entry));
            if(entry.getStep() == matSize) {
                completeCollection(player, entry);
            }
            getDao().updateCollection(player, entry);
        }

    }

    public void completeCollection(Player player, PlayerCollectionEntry entry) {
        CollectionTemplate template = DataManager.COLLECTION_TEMPLATE_DATA.getTemplate(entry.getId());
        entry.setComplete(true);
        getDao().updateCollection(player, entry);
        PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_COMPLETE(entry));
        player.getPlayerCollection().getPlayerCollectionEntry().remove(entry.getId());
        player.getPlayerCollection().getCompleteCollection().add(entry);
        if(template.getRewards() != null) {
            for (RewardCollectionTemplate rewards : template.getRewards()) {
                ItemService.addItem(player, rewards.getItemId(), rewards.getCount());
            }
        }
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEMCOLLECT_COMPLETE);
        entry.apply(player);
        addCollectionExp(player, entry);
    }

    public void addCollectionExp(Player player, PlayerCollectionEntry entry) {
        CollectionTemplate template = DataManager.COLLECTION_TEMPLATE_DATA.getTemplate(entry.getId());
        PlayerCollectionInfos info = player.getPlayerCollection().getCollectionInfos().get(template.getGrade());
        int level = info.getLevel();
        int exp = info.getExp();
        if(template.getGrade() == CollectionType.EVENT) {
            info.addexp();
        } else {
            CollectionExpTemplate expTemplate = DataManager.COLLECTION_EXP_DATA.getTemplate(level+1, template.getGrade());
            if(exp + 1 >= expTemplate.getExp()) {
                info.onLevelUp();
            } else {
                info.addexp();
            }
        }
        getDao().updatePlayerCollection(player, info);
        PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION(player));
    }

    public PlayerCollectionDAO getDao() {
        return DAOManager.getDAO(PlayerCollectionDAO.class);
    }

    public static PlayerCollectionService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final PlayerCollectionService instance = new PlayerCollectionService();
    }
}
