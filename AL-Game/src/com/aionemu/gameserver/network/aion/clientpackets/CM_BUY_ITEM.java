package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.model.trade.RepurchaseList;
import com.aionemu.gameserver.model.trade.TradeList;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.RepurchaseService;
import com.aionemu.gameserver.services.TradeService;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_BUY_ITEM extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_BUY_ITEM.class);
	private int sellerObjId;
	private int tradeActionId;
	private int amount;
	private int itemId;
	private long count;
	private boolean isAudit;
	private TradeList tradeList;
	private RepurchaseList repurchaseList;
	
	public CM_BUY_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		sellerObjId = readD();
		tradeActionId = readH();
		amount = readH();
		if (amount < 0 || amount > 36) {
			isAudit = true;
			AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM amount: " + amount);
			return;
		} if (tradeActionId == 2) {
			repurchaseList = new RepurchaseList(sellerObjId);
		} else {
			tradeList = new TradeList(sellerObjId);
		} for (int i = 0; i < amount; i++) {
			itemId = readD();
			count = readQ();
			if (count < 0 || (itemId <= 0 && tradeActionId != 0) || itemId == 190000073 || itemId == 190000074 || count > 999999) {
				isAudit = true;
				AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM item: " + itemId + " count: " + count);
				break;
			} switch (tradeActionId) {
				case 1: //[Sell To Shop]
				case 17: //[Pet Seller]
				case 19: //[Inventory Shop]
					tradeList.addSellItem(itemId, count);
				break;
				case 2: //[Repurchase]
					repurchaseList.addRepurchaseItem(player, itemId, count);
				break;
				case 13: //[Buy From Shop]
				case 14: //[Buy From Abyss Shop]
				case 15: //[Buy From Reward Shop]
					tradeList.addBuyItem(itemId, count);
				break;
			}
		}
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (isAudit || player == null) {
			return;
		}
		VisibleObject target = World.getInstance().findVisibleObject(sellerObjId);
		if (tradeActionId == 19 && target == null) {
			TradeService.performSellToShop(player, tradeList);
		} else if (target == null) {
			return;
		} if (target instanceof Npc) {
			Npc npc = (Npc) target;
			TradeListTemplate tlist = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
			TradeListTemplate purchaseTemplate = DataManager.TRADE_LIST_DATA.getPurchaseListTemplate(npc.getNpcId());
			switch (tradeActionId) {
				case 1: //[Sell To Shop]
				    TradeService.performSellToShop(player, tradeList);
				    TradeService.performSellForAPToShop(player, tradeList, purchaseTemplate);
					TradeService.performSellForKinahToShop(player, tradeList, purchaseTemplate);
				break;
				case 2: //[Repurchase]
					RepurchaseService.getInstance().repurchaseFromShop(player, repurchaseList);
				break;
				case 13: //[Buy From Shop]
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.NORMAL &&
					    npc.getNpcId() != 837033 || npc.getNpcId() != 837034 ||
						npc.getNpcId() != 837036 || npc.getNpcId() != 837037) {
						TradeService.performBuyFromShop(npc, player, tradeList);
					}
				break;
				case 14: //[Buy From Abyss Shop]
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.ABYSS) {
						TradeService.performBuyFromAbyssShop(npc, player, tradeList);
					}
				break;
				case 15: //[Buy From Reward Shop]
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.REWARD &&
					    npc.getNpcId() != 837035 || npc.getNpcId() != 837038) {
						TradeService.performBuyFromRewardShop(npc, player, tradeList);
					}
				break;
				case 17: //[Pet Seller]
				    TradeService.performSellForKinahToShop(player, tradeList, purchaseTemplate);
				break;
				default:
					log.info(String.format("Unhandle shop action unk1: %d", tradeActionId));
				break;
			}
		}
	}
}