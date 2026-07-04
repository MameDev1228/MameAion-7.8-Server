package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.enchant.EnchantService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Iterator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantGrindingAction")
public class EnchantGrindingAction extends AbstractItemAction
{
	@XmlAttribute(name = "count")
    private int count;
	
    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        if (parentItem == null || targetItem == null) {
            ///The item cannot be found.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
            return false;
        } if (targetItem.getEnchantLevel() >= 15) {
            ///%0 has reached its max refining level and cannot be refined anymore.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_LIMIT(targetItem.getNameId()));
            return false;
        } if (parentItem.getItemTemplate().isGrindEnchant() && player.getInventory().getKinah() < EnchantService.EnchantKinah(targetItem)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return false;
        }
        return true;
    }
	
    @Override
    public void act(final Player player, final Item parentItem, final Item targetItem) {
        if (player.getInventory().getKinah() < EnchantService.EnchantKinah(targetItem)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        }
		int enchantCast = 0;
		if (player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent() != 0) {
			enchantCast = EnchantsConfig.ENCHANT_SPEED / 2 - (EnchantsConfig.ENCHANT_SPEED * player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent() / 100);
		} else {
			enchantCast = EnchantsConfig.ENCHANT_SPEED;
		}
        final boolean isSuccess = isSuccess(player, parentItem, targetItem);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), enchantCast, 0, 0));
        final ItemUseObserver moveObserver = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
                player.getObserveController().removeObserver(this);
                PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), targetItem.getObjectId().intValue(), targetItem.getItemTemplate().getTemplateId(), 0, 3, 0));
                ItemPacketService.updateItemAfterInfoChange(player, targetItem);
                //Refining of %0 has been canceled.
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_CANCEL(targetItem.getItemTemplate().getNameId()));
            }
        };
        player.getObserveController().attach(moveObserver);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getController().cancelTask(TaskId.ITEM_USE);
                player.getObserveController().removeObserver(moveObserver);
                EnchantService.enchantGrindItemAct(player, parentItem, targetItem, targetItem.getEnchantPvPvELevel(), isSuccess);
                PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2, 384));
            }
        }, enchantCast));
    }
	
    private boolean isSuccess(final Player player, final Item parentItem, final Item targetItem) {
        if (parentItem.getItemTemplate() != null) {
            return EnchantService.enchantGrindItem(player, parentItem, targetItem);
        }
        return false;
    }
}