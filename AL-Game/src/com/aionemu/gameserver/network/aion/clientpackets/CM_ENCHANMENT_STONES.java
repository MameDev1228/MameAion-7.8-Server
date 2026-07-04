package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.actions.*;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.enchant.EnchantService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_ENCHANMENT_STONES extends AionClientPacket
{
	Logger log = LoggerFactory.getLogger(CM_ENCHANMENT_STONES.class);
	
	private int npcObjId;
	private int slotNum;
	private int actionType;
	private int targetFusedSlot;
	private int stoneUniqueId;
	private int targetItemUniqueId;
	private int grindStone1;
	@SuppressWarnings("unused")
	private ItemCategory actionCategory;
	@SuppressWarnings("unused")
	private int unk;
	
	public CM_ENCHANMENT_STONES(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		actionType = readC();
		targetFusedSlot = readC();
		targetItemUniqueId = readD();
		switch (actionType) {
			case 0:
				break;
		    case 1:
		    case 2:
			    stoneUniqueId = readD();
			break;
		    case 3:
			   slotNum = readC();
			   readC();
			   readH();
			   npcObjId = readD();
			break;
			case 10:
				grindStone1 = readD();
				read();
				break;
		    default:
				log.error("Unknown enchantment type? 0x" + Integer.toHexString(actionType/* !!!!! */).toUpperCase());
			break;
		}
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		VisibleObject obj = player.getKnownList().getObject(npcObjId);
		//log.error("enchantment type :" + actionType);
		switch (actionType) {
		    case 1: //Enchant Stone.
		    case 2: //Add Manastone.
			    EnchantItemAction action = new EnchantItemAction();
				EnchantStigmaAction action2 = new EnchantStigmaAction();
				MagmaticSlotExpansionAction action3 = new MagmaticSlotExpansionAction();
				ManastoneItemAction action4 = new ManastoneItemAction();
				GrindSlotExpansionAction action5 = new GrindSlotExpansionAction();
				EnchantGrindingAction action6 = new EnchantGrindingAction();
				EnchantDestructionAction action7 = new EnchantDestructionAction();
				EnchantGlyphAction action8 = new EnchantGlyphAction();
			    Item manastone = player.getInventory().getItemByObjId(stoneUniqueId);
			    Item targetItem = player.getEquipment().getEquippedItemByObjId(targetItemUniqueId);
				Item targetStone = player.getInventory().getItemByObjId(targetItemUniqueId);
			    if (targetItem == null) {
				    targetItem = player.getInventory().getItemByObjId(targetItemUniqueId);
			    }
				//Enchant Stigma.
				if (manastone.getItemTemplate().isStigma() || manastone.getItemTemplate().isEnchantmentStigmaStone()) {
					action2.act(player, manastone, targetItem);
				}
				//Magmatic Slot Expansion.
				if (manastone.getItemTemplate().isManaSlotOpen()) {
					if (action3.canAct(player, manastone, targetItem)) {
						action3.act(player, manastone, targetItem);
					}
				}
				//Grinding Slot Expansion.
				if (manastone.getItemTemplate().isGrindSlotOpen()) {
					if (action5.canAct(player, manastone, targetItem)) {
						action5.act(player, manastone, targetItem);
					}
				}
				//Enchant Stone.
				if (manastone.getItemTemplate().isEnchantmentStone()) {
					if (action.canAct(player, manastone, targetItem)) {
						action.act(player, manastone, targetItem);
					}
				}
				//Enchant Destruction Stone
				if(manastone.getItemTemplate().isEnchantmentDestructionStone()) {
					if (action7.canAct(player, manastone, targetItem)) {
						action7.act(player, manastone, targetItem);
					}
				}
				//Manastone.
				if(manastone.getItemTemplate().isManaStone()) {
					if (action4.canAct(player, manastone, targetItem)) {
						action4.act(player, manastone, targetItem, targetFusedSlot);
					}
				}
				//Enchant Grind.
				if (manastone.getItemTemplate().isGrindEnchant()) {
					if (action6.canAct(player, manastone, targetItem)) {
						action6.act(player, manastone, targetItem);
					}
				}
				//Glyph Enchant
				if (manastone.getItemTemplate().isGlyphEnchant()) {
					if (action8.canAct(player, manastone, targetItem)) {
						action8.act(player, manastone, targetItem);
					}
				}
		    break;
		    case 3: //Remove Manastone.
			    long price = PricesService.getPriceForService(221069, player.getRace());
			    if (player.getInventory().getKinah() > price) {
				    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
				    player.getInventory().decreaseKinah(price);
				    if (targetFusedSlot == 1) {
					    ItemSocketService.removeManastone(player, targetItemUniqueId, slotNum);
				    } else {
					    ItemSocketService.removeFusionstone(player, targetItemUniqueId, slotNum);
				    }
			    }
		    break;
			case 10:
				Item mat1 = player.getInventory().getItemByObjId(targetItemUniqueId);
				Item mat2 = player.getInventory().getItemByObjId(grindStone1);
				EnchantService.combineGrind(player, mat1, mat2);
				//combineGrind
			break;
		}
	}
}