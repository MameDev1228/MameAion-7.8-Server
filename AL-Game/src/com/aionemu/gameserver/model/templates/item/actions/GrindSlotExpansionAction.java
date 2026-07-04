package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GrindSlotExpansionAction")
public class GrindSlotExpansionAction  extends AbstractItemAction
{
    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        if (targetItem.getGrindSocket() >= 1) {
            return false;
        } if (parentItem == null || targetItem == null) {
            //The item cannot be found.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
            return false;
        }
        return true;
    }
	
    @Override
    public void act(final Player player, final Item parentItem, final Item targetItem) {
        final int grindSocket = targetItem.getGrindSocket();
        final boolean isSlotSuccess = Rnd.chance(65);
        final int parentItemId = parentItem.getItemId();
        final int parentObjectId = parentItem.getObjectId();
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItemId, 2000, 0, 0), true);
        final ItemUseObserver moveObserver = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
                player.getObserveController().removeObserver(this);
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentObjectId, parentItemId, 0, 2, 0), true);
                ItemPacketService.updateItemAfterInfoChange(player, targetItem);
                //%0's Manastone slot expansion was canceled.
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_OPTIONSLOT_CANCELED(targetItem.getNameId()));
            }
        };
        player.getObserveController().attach(moveObserver);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (isSlotSuccess) {
                    targetItem.setGrindSocket(grindSocket + 1);
                    if (targetItem.getItemTemplate().isOdianAccessory()) {
                        targetItem.setGrindColor(Rnd.get(1, 3));
						//%0's Gemstone slots have been expanded successfully.
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_OP_ODIAN_SUCCEEDED(new DescriptionId(targetItem.getNameId())));
                    } else {
                        targetItem.setGrindColor(Rnd.get(11, 13));
						//%0's Runestone slots have been expanded successfully.
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_OP_RUNE_SUCCEEDED(new DescriptionId(targetItem.getNameId())));
                    }
                    player.getObserveController().removeObserver(moveObserver);
                    player.getInventory().decreaseByObjectId(parentItem.getObjectId(), targetItem.getItemTemplate().getGrindSlotOpeningCost());
                    PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentObjectId, parentItemId, 0, 1, 1), true);
                    targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
                    PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
                    player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
                } else {
                    player.getObserveController().removeObserver(moveObserver);
                    targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
                    PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
                }
                PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSlotSuccess ? 1 : 2));
            }
        }, 2000));
    }
}
