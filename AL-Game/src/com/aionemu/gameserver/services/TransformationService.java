/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dao.AccountTransformDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTransformList;
import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORMATION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javolution.util.FastList;

/**
 * @author Falke_34, FrozenKiller
 */
public class TransformationService {

	public void onPlayerLogin(Player player) {
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(0, player));
		}
	}

	public void makeTransform(final Player player, final int itemObjId) {
		if (player == null || itemObjId == 0) {
			return;
		}
		if (player.getTransformCreated().size() >= 10) {
			player.getTransformCreated().clear();
		}
		final Item item = player.getInventory().getItemByObjId(itemObjId);
		if (item == null || item.getItemTemplate() == null) {
			return;
		}
		final ItemTemplate it = item.getItemTemplate();
		final ItemTransformList transformList = DataManager.ITEM_TRANSFORM_LIST.getTransformList(it.getTransformList());
		if (transformList == null || transformList.getTransformId() == null || transformList.getTransformId().isEmpty()) {
			return;
		}
		final int transformId = transformList.getTransformId().get(Rnd.get(0, transformList.getTransformId().size() - 1));
		player.getController().cancelTask(TaskId.ITEM_USE);
		final ItemUseObserver moveObserver = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 2));
				PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(1, player, 0, 1, 2));
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_TRANSFORMATION_CONTRACT_FAIL, 0);
			}
		};
		player.getObserveController().attach(moveObserver);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(moveObserver);
				if (!player.getInventory().decreaseByObjectId(itemObjId, 1)) {
					return;
				}
				AccountTransfo newTrans = new AccountTransfo(transformId, 1);
				player.getTransformCreated().add(newTrans);
				if (player.getTransformList().hasTransformation(newTrans.getCardId())) {
					AccountTransfo transfo = player.getTransformList().getTransformation(newTrans.getCardId());
					transfo.setCount(transfo.getCount() + 1);
					DAOManager.getDAO(AccountTransformDAO.class).updateTransfo(player.getPlayerAccount(), transfo);
				} else {
					player.getTransformList().addNewTransformation(player, newTrans.getCardId(), 1);
				}
				PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(player.getTransformCreated(), 1, 1));
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1));
				player.getTransformList().updateTransformationsList();
			}
		}, 500));
	}

	public void onPlayerTransform(Player player, int itemObjId, int cardId) {
		if (player == null || itemObjId == 0 || cardId == 0) {
			return;
		}
		TransformBookTemplate book = DataManager.TRANSFORM_BOOK_DATA.getTransformBookById(cardId);
		if (book == null) {
			return;
		}
		if (player.isTransformed()) {
			PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_TRANSFORMATION_CANT_CURRENT_STATE, 0);
			PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_ACT_STATE_POLYMORPH, 3000);
			return;
		}
		if (!player.getInventory().decreaseByObjectId(itemObjId, 1L)) {
			return;
		}
		player.getController().useSkill(book.getSkillId());
		player.setLastUsedTransformation(book.getId());
		PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(0, player));
	}

	public void onCombineTransformation(Player player, ArrayList<Integer> materials) {
		if (player == null || materials == null || materials.isEmpty()) {
			return;
		}
		List<Integer> deleteTransform = (List<Integer>) new FastList<Integer>();
		int kinahCost = 0;
		int baseGrade = 0;
		for (final Integer id : materials) {
			if (id == null || id == 0) {
				continue;
			}
			TransformBookTemplate template = DataManager.TRANSFORM_BOOK_DATA.getTransformBookById(id);
			AccountTransfo owned = player.getTransformList().getTransformation(id);
			if (template == null || owned == null || owned.getCount() <= 0) {
				return;
			}
			if (baseGrade == 0) {
				baseGrade = template.getGrade();
			} else if (baseGrade != template.getGrade()) {
				return;
			}
		}
		switch (baseGrade) {
			case 1:
				kinahCost = 10000;
				break;
			case 2:
				kinahCost = 42000;
				break;
			case 3:
				kinahCost = 166000;
				break;
			case 4:
				kinahCost = 664000;
				break;
			case 5:
				kinahCost = 1280000;
				break;
			default:
				return;
		}
		if (player.getInventory().getKinah() < kinahCost) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		Account account = player.getPlayerAccount();
		int list = getCombineList(baseGrade);
		ItemTransformList transformList = DataManager.ITEM_TRANSFORM_LIST.getTransformList(list);
		if (transformList == null || transformList.getTransformId() == null || transformList.getTransformId().isEmpty()) {
			return;
		}
		int transfoRnd = transformList.getTransformId().get(Rnd.get(0, transformList.getTransformId().size() - 1));
		if (player.getTransformList().hasTransformation(transfoRnd)) {
			AccountTransfo transform = player.getTransformList().getTransformation(transfoRnd);
			transform.setCount(transform.getCount() + 1);
			DAOManager.getDAO(AccountTransformDAO.class).updateTransfo(player.getPlayerAccount(), transform);
			player.getTransformCreated().add(transform);
		} else {
			player.getTransformList().addNewTransformation(player, transfoRnd, 1);
		}
		for (Integer id : materials) {
			if (id != null && id != 0) {
				AccountTransfo transfo = player.getTransformList().getTransformation(id);
				if (transfo == null || transfo.getCount() <= 0) {
					return;
				}
				transfo.setCount(transfo.getCount() - 1);
				DAOManager.getDAO(AccountTransformDAO.class).updateTransfo(account, transfo);
				deleteTransform.add(id);
			}
		}
		player.getInventory().decreaseKinah(kinahCost);
		PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(2, deleteTransform));
		PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(1, player, transfoRnd, 3, 1));
		player.getTransformList().updateTransformationsList();
	}

	private int getCombineList(int grade) {
		switch (grade) {
			case 1:
				return Rnd.chance(50) ? 1001 : 1000;
			case 2:
				return Rnd.chance(30) ? 1002 : 1001;
			case 3:
				return Rnd.chance(20) ? 1003 : 1002;
			case 4:
				return Rnd.chance(10) ? 1004 : 1003;
			case 5:
				return Rnd.chance(5) ? 1004 : 1003;
			default:
				return 0;
		}
	}

	public void addAllToGM(Player player) {
		if (player == null || DataManager.TRANSFORM_BOOK_DATA.getAllBooks() == null) {
			return;
		}
		for (TransformBookTemplate template : DataManager.TRANSFORM_BOOK_DATA.getAllBooks().valueCollection()) {
			if (template == null) {
				continue;
			}
			AccountTransfo transfo = new AccountTransfo(template.getId(), 4);
			player.getTransformList().addNewTransformation(player, transfo.getCardId(), transfo.getCount());
			PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(1, player, template.getId(), 0, 1));
		}
		player.getTransformList().updateTransformationsList();
	}

	public static TransformationService getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final TransformationService instance = new TransformationService();
	}
}
