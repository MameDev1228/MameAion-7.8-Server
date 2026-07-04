package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.enchant.EnchantService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAttribute;

public class ManastoneItemAction extends AbstractItemAction
{
    @XmlAttribute(name = "count")
    private int count;
	
    @XmlAttribute(name = "min_level")
    private Integer min_level;
	
    @XmlAttribute(name = "max_level")
    private Integer max_level;
	
    @XmlAttribute(name = "manastone_only")
    private boolean manastone_only;
	
    @XmlAttribute(name = "chance")
    private float chance;
	
    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (parentItem == null || targetItem == null) {
            //The item cannot be found.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
            return false;
        } if (player.getInventory().getKinah() < EnchantService.ManastoneKinah(targetItem)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return false;
        } if (targetItem.getEnchantLevel() == 20 && !parentItem.getItemTemplate().isManaStone()) {
            //You cannot enchant %0 any further.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_IT_CAN_NOT_BE_ENCHANTED_MORE_TIME(targetItem.getNameId()));
            return false;
        }
		int msID = parentItem.getItemTemplate().getTemplateId() / 1000000;
		int tID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		int wID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		if (msID != 166 && msID != 167 || msID != 166 && tID >= 120) {
			if (targetItem.getItemTemplate().isPlume() && targetItem.getItemTemplate().isBracelet() && targetItem.getItemTemplate().isAccessory()) {
				return true;
			}
		} else if (msID != 166) {
			if (tID >= 120 && wID != 187) {
				return false;
			}
		} if (targetItem.getItemTemplate().getArmorType() == ArmorType.WING) {
			return true;
		}
        return true;
    }
	
    @Override
    public void act(final Player player, final Item parentItem, final Item targetItem) {
        act(player, parentItem, targetItem, 1);
    }
	
    public void act(final Player player, final Item parentItem, final Item targetItem, final int targetWeapon) {
        if (player.getInventory().getKinah() < EnchantService.ManastoneKinah(targetItem)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        }
        final boolean isSuccess = isSuccess(player, parentItem, targetItem, targetWeapon);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 2000, 0, 0));
        final ItemUseObserver moveObserver = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
                player.getObserveController().removeObserver(this);
                PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), targetItem.getObjectId().intValue(), targetItem.getItemTemplate().getTemplateId(), 0, 3, 0));
                ItemPacketService.updateItemAfterInfoChange(player, targetItem);
                //You have cancelled the enchanting of %0.
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_CANCELED(targetItem.getItemTemplate().getNameId()));
            }
        };
        player.getObserveController().attach(moveObserver);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getController().cancelTask(TaskId.ITEM_USE);
                player.getObserveController().removeObserver(moveObserver);
                ItemTemplate itemTemplate = parentItem.getItemTemplate();
                EnchantService.socketManastoneAct(player, parentItem, targetItem, targetWeapon, isSuccess);
                PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2, 384));
            }
        }, 2000));
    }
	
    private boolean isSuccess(final Player player, final Item parentItem, final Item targetItem, final int targetWeapon) {
        if (parentItem.getItemTemplate() != null) {
            ItemTemplate itemTemplate = parentItem.getItemTemplate();
            return EnchantService.socketManastone(player, parentItem, targetItem, targetWeapon);
        }
        return false;
    }
	
    public int getCount() {
        return count;
    }
	
    public int getMaxLevel() {
        return max_level != null ? max_level : 0;
    }
	
    public int getMinLevel() {
        return min_level != null ? min_level : 0;
    }
	
    public boolean isManastoneOnly() {
        return manastone_only;
    }
	
    public float getChance() {
        return chance;
    }
}