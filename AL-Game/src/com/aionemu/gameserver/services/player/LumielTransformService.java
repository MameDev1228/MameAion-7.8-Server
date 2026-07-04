package com.aionemu.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.PlayerLumielDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.LumielTransform;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielMaterialTemplate;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielRewardItem;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformReward;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LumielTransformService {


    private static final Logger log = LoggerFactory.getLogger(LumielTransformService.class);
    private List<LumielTransformTemplate> activeLumiel = new FastList<LumielTransformTemplate>();
    private static Map<Integer, List<LumielRewardItem>> playerRewards = new FastMap<Integer, List<LumielRewardItem>>();

    public void init() {
        log.info("Lumiel Transform Service Start");
        generateActiveLumiel();
    }

    public void generateActiveLumiel() {
        for (LumielTransformTemplate template : DataManager.LUMIEL_TEMPLATE_DATA.getAllTemplates().values()) {
            if(template.isActivate()) {
                activeLumiel.add(template);
            }
        }
        log.info("Lumiel template actived size : " + activeLumiel.size());
    }

    public void onRewardPoints(Player player, int lumielId, Map<Integer, Long> matrials) {
        long finalScore = 0;
        LumielTransform lumielTransform = player.getPlayerLumiel().get(lumielId);
        for(Map.Entry<Integer, Long> mats : matrials.entrySet()) {
            Item mat = player.getInventory().getItemByObjId(mats.getKey());
            Long count = mats.getValue();
            LumielMaterialTemplate materialTemplate = DataManager.LUMIEL_MATERIAL_DATA.getTemplate(lumielId, mat.getItemId());
            finalScore = finalScore + (materialTemplate.getPoint() * count);
            player.getInventory().decreaseByItemId(mat.getItemId(), count);
        }
        lumielTransform.setPoints(lumielTransform.getPoints() + finalScore);
        getDao().updateLumielTransform(player, lumielTransform);
        PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM_EXP(lumielId, finalScore));
    }

    public void onGenerateReward(Player player, int lumielId) {
        LumielTransformTemplate template = DataManager.LUMIEL_TEMPLATE_DATA.getTemplate(lumielId);
        List<LumielRewardItem> rewardItems = new FastList<LumielRewardItem>();
        for (LumielTransformReward reward : template.getLumielTransformRewards()) {
            int index = Rnd.get(0, reward.getLumielTransformRewards().size() - 1);
            LumielRewardItem item = reward.getLumielTransformRewards().get(index);
            rewardItems.add(item);
        }
        playerRewards.put(player.getObjectId(), rewardItems);
        PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM_REWARD_LIST(lumielId, rewardItems));
    }

    public void sendLumielPacket(Player player) {
        PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRASFORM(player));
    }

    public void onRewardPlayer(Player player, int lumielId) {
        if(player.getInventory().isFull()) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REINVENT_MSG_NOT_ENOUGH_INVENTORY);
            return;
        }
        List<LumielRewardItem> rewards = getPlayerRewardList(player);
        LumielTransform lumielTransform = player.getPlayerLumiel().get(lumielId);
        int index = Rnd.get(0, rewards.size() -1);
        LumielRewardItem reward = rewards.get(index);

        PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM_REWARD(lumielId, reward));
        ItemService.addItem(player, reward.getItemId(), reward.getCount());

        lumielTransform.setPoints(0);
        getDao().updateLumielTransform(player, lumielTransform);
        PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRASFORM(player));

        playerRewards.remove(player.getObjectId());
    }

    public List<LumielRewardItem> getPlayerRewardList(Player player) {
        List<LumielRewardItem> rewards = new FastList<LumielRewardItem>();
        for (Map.Entry<Integer, List<LumielRewardItem>> playerReward : playerRewards.entrySet()) {
            if(playerReward.getKey() == player.getObjectId()) {
                for (LumielRewardItem item : playerReward.getValue()) {
                    rewards.add(item);
                }
            }
        }
        return rewards;
    }

    public void onLogin(Player player) {
        player.setPlayerLumiel(getDao().loadPlayerLumiel(player));
        if(player.getPlayerLumiel().size() == 0) {
            for(LumielTransformTemplate template : activeLumiel) {
                LumielTransform lumiel = new LumielTransform(template.getId(), 0);
                getDao().addPlayerLumiel(player, lumiel);
                player.getPlayerLumiel().put(lumiel.getId(), lumiel);
            }
        }
        PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRASFORM(player));
    }

    public static LumielTransformService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final LumielTransformService instance = new LumielTransformService();
    }

    public PlayerLumielDAO getDao() {
        return DAOManager.getDAO(PlayerLumielDAO.class);
    }
}
