package com.aionemu.gameserver.model.team2.common.legacy;

import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.player.InRoll;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.services.drop.DropDistributionService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import javolution.util.FastList;

import java.util.Collection;

public class LootGroupRules
{
    private LootRuleType lootRule;
    private LootDistribution autodistribution;
    private int common_item_above;
    private int superior_item_above;
    private int heroic_item_above;
    private int fabled_item_above;
    private int ethernal_item_above;
    private int mythic_item_above;
    private int ancien_item_above;
    private int relic_item_above;
    private int finality_item_above;
    private int misc;
    private int nrMisc;
    private int nrRoundRobin;
    private FastList<DropItem> itemsToBeDistributed = new FastList<DropItem>();

    public LootGroupRules() {
        lootRule = LootRuleType.FREEFORALL;
        autodistribution = LootDistribution.NORMAL;
        common_item_above = 0;
        superior_item_above = 0;
        heroic_item_above = 0;
        fabled_item_above = 0;
        ethernal_item_above = 0;
        mythic_item_above = 0;
        ancien_item_above = 0;
        relic_item_above = 0;
        finality_item_above = 0;
    }
	
    public LootGroupRules(LootRuleType lootRule, LootDistribution autodistribution, int ancienItemAbove, int relicItemAbove, int finalityItemAbove, int misc, int commonItemAbove, int superiorItemAbove, int heroicItemAbove, int fabledItemAbove, int ethernalItemAbove, int mythicItemAbove) {
        //super();
        this.lootRule = lootRule;
        this.autodistribution = autodistribution;
        this.misc = misc;
        this.common_item_above = commonItemAbove;
        this.superior_item_above = superiorItemAbove;
        this.heroic_item_above = heroicItemAbove;
        this.fabled_item_above = fabledItemAbove;
        this.ethernal_item_above = ethernalItemAbove;
        this.mythic_item_above = mythicItemAbove;
        this.ancien_item_above = ancienItemAbove;
        this.relic_item_above = relicItemAbove;
        this.finality_item_above = finalityItemAbove;
    }
	
    public boolean getQualityRule(ItemQuality quality) {
        switch (quality) {
            case COMMON: // White
                return common_item_above != 0;
            case RARE: // Green
                return superior_item_above != 0;
            case LEGEND: // Blue
                return heroic_item_above != 0;
            case UNIQUE: // Yellow
                return fabled_item_above != 0;
            case EPIC: // Orange
                return ethernal_item_above != 0;
            case MYTHIC: // Purple
                return mythic_item_above != 0;
            case ANCIENT: //Gold
                return ancien_item_above != 0;
            case RELIC: //Pink
                return relic_item_above != 0;
            case FINALITY: //Red
                return finality_item_above != 0;
        }
        return false;
    }
	
    public boolean isMisc(ItemQuality quality) {
        return quality.equals(ItemQuality.JUNK) && misc == 1;
    }
    public LootRuleType getLootRule() {
        return lootRule;
    }
    public LootDistribution getAutodistribution() {
        return autodistribution;
    }
    public int getCommonItemAbove() {
        return common_item_above;
    }
    public int getSuperiorItemAbove() {
        return superior_item_above;
    }
    public int getHeroicItemAbove() {
        return heroic_item_above;
    }

    public int getFabledItemAbove() {
        return fabled_item_above;
    }

    public int getEthernalItemAbove() {
        return ethernal_item_above;
    }

    public int getMythicItemAbove() {
        return mythic_item_above;
    }
    public int getAncienItemAbove() {
        return ancien_item_above;
    }
    public int getRelicItemAbove() {
        return relic_item_above;
    }
    public int getFinalityItemAbove() {
        return finality_item_above;
    }
    public int getNrMisc() {
        return nrMisc;
    }
    public void setNrMisc(int nrMisc) {
        this.nrMisc = nrMisc;
    }
	
    public void setPlayersInRoll(final Collection<Player> players, int time, final int index, final int npcId) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                for (Player player : players) {
                    if (player.isInPlayerMode(PlayerMode.IN_ROLL)) {
                        InRoll inRoll = player.inRoll;
                        switch (inRoll.getRollType()) {
                            case 2:
                                if (inRoll.getIndex() == index && inRoll.getNpcId() == npcId) {
                                    DropDistributionService.getInstance().handleRoll(player, 0, inRoll.getItemId(), inRoll.getNpcId(), inRoll.getIndex());
                                }
                            break;
                            case 3:
                                if (inRoll.getIndex() == index && inRoll.getNpcId() == npcId) {
                                    DropDistributionService.getInstance().handleBid(player, 0, inRoll.getItemId(), inRoll.getNpcId(), inRoll.getIndex());
                                }
                            break;
                        }
                    }
                }
            }
        }, time);
    }
	
    public int getNrRoundRobin() {
        return nrRoundRobin;
    }
    public void setNrRoundRobin(int nrRoundRobin) {
        this.nrRoundRobin = nrRoundRobin;
    }
    public int getMisc() {
        return misc;
    }
    public void addItemToBeDistributed(DropItem dropItem) {
        itemsToBeDistributed.add(dropItem);
    }
    public boolean containDropItem(DropItem dropItem) {
        return itemsToBeDistributed.contains(dropItem);
    }
    public void removeItemToBeDistributed(DropItem dropItem) {
        itemsToBeDistributed.remove(dropItem);
    }
    public FastList<DropItem> getItemsToBeDistributed() {
        return itemsToBeDistributed;
    }
}