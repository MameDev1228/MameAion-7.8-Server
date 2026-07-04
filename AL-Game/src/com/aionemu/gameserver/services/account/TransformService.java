/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.account;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dao.AccountTransformDAO;
import com.aionemu.gameserver.dao.PlayerTransformDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.account.TransformCollection;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.ItemTransormList;
import com.aionemu.gameserver.model.templates.item.ItemMinionList;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;
import com.aionemu.gameserver.model.templates.transform_book.TransformCollectionTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndArray;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TransformService
{
    private static final Logger log = LoggerFactory.getLogger(TransformService.class);
    private final Map<Integer, Boolean> lastTransformInvisibleMode = new FastMap<Integer, Boolean>().shared();
	
    public void onPlayerLogin(Player player) {
        PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(0, player));
        //checkCollection(player);
    }
	
    public void makeTransform(final Player player, final int itemObjId) {
		if (player.getTransformCreated().size() >= 10) {
			player.getTransformCreated().clear();
		}
		final Item item = player.getInventory().getItemByObjId(itemObjId);
		final ItemTemplate it = item.getItemTemplate();
		final ItemTransormList transormList = DataManager.ITEM_TRANSFORM_LIST.getTransformList(it.getTransformList());
		final int transformId = transormList.getTransformId().get(Rnd.get(0, transormList.getTransformId().size() - 1));
		player.getController().cancelTask(TaskId.ITEM_USE);
		final ItemUseObserver moveObserver = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 2));
				PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(1, player, 0, 1, 2));
				//The transformation contract in progress was canceled.
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
				int transfoRnd = transformId;
				AccountTransfo newTrans = new AccountTransfo(transfoRnd, 1);
				player.getTransformCreated().add(newTrans);
				if (player.getTransformList().hasTransformation(newTrans.getCardId())) {
					AccountTransfo transfo = player.getTransformList().getTransformation(newTrans.getCardId());
					transfo.setCount(transfo.getCount() + 1);
					DAOManager.getDAO(AccountTransformDAO.class).updateTransfo(player.getPlayerAccount(), transfo);
				} else {
					player.getTransformList().addNewTransformation(player, newTrans.getCardId(), 1);
				}
				PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(player.getTransformCreated(), 1, 1));
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1));
				player.getTransformList().updateTransformationsList();
				//checkCollection(player);
				transfoRnd = 0;
			}
		}, 500));
    }
	
    public void onPlayerTransform(final Player player, final int itemObjId, final int cardId) {
        final TransformBookTemplate book = DataManager.TRANSFORM_BOOK_DATA.getTransformBookById(cardId);
        final Item item = resolveTransformScroll(player, itemObjId);
        if (book == null || item == null) {
            return;
        }
        final boolean invisibleTransform = item.getItemTemplate().isTransformInvisible();
        final int normalSkillId = book.getSkillId();
        final int castSkillId = invisibleTransform ? getTransparentSkillId(normalSkillId) : normalSkillId;
        if (player.isTransformed()) {
            //You cannot use a Transformation Contract in this state.
            PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_TRANSFORMATION_CANT_CURRENT_STATE, 0);
            //Transformation Mode.
            PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_ACT_STATE_POLYMORPH, 3000);
            return;
        } if (!player.getInventory().decreaseByObjectId(item.getObjectId(), 1)) {
            return;
        }
        // Official-like shortcut behavior: when the transform icon is triggered without
        // a concrete scroll object id, reuse the last selected scroll mode.
        lastTransformInvisibleMode.put(player.getObjectId(), invisibleTransform);
        // Normal transformation scroll: cast the normal TransformBook skill and allow visual model change.
        // Transparent transformation scroll: cast the matching transparent skill when it exists, mark the
        // TransformEffect as stats-only, and force-clear visual transform packets after casting.
        player.setInvisibleTransform(invisibleTransform);
        player.getController().useSkill(castSkillId);
        if (invisibleTransform) {
            // Stats-only transparent transformation must be deterministic at cast time.
            // Do not schedule repeated visual clear packets: they are noisy and can race with normal transforms.
            normalizeInvisibleTransform(player);
        }
        /**
         * If you use transform save the last transformation (double click on transformation scroll and apply this)
         */
        player.setLastUsedTransformation(book.getId());
        PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(0, player));
        if (invisibleTransform) {
            log.info("[MAME-TRANSFORM][INVISIBLE] player=" + player.getName() + " itemId=" + item.getItemId()
                + " cardId=" + cardId + " normalSkill=" + normalSkillId + " castSkill=" + castSkillId + " visual=stats-only");
        }
    }

    private Item resolveTransformScroll(final Player player, final int itemObjId) {
        if (player == null || player.getInventory() == null) {
            return null;
        }
        Item item = player.getInventory().getItemByObjId(itemObjId);
        if (item != null) {
            return item;
        }
        Boolean invisible = lastTransformInvisibleMode.get(player.getObjectId());
        if (Boolean.TRUE.equals(invisible)) {
            item = player.getInventory().getFirstItemByItemId(190099001);
            if (item == null) {
                item = player.getInventory().getFirstItemByItemId(190099002);
            }
            if (item == null) {
                item = player.getInventory().getFirstItemByItemId(190099000);
            }
        } else {
            item = player.getInventory().getFirstItemByItemId(190099000);
            if (item == null) {
                item = player.getInventory().getFirstItemByItemId(190099001);
            }
            if (item == null) {
                item = player.getInventory().getFirstItemByItemId(190099002);
            }
        }
        return item;
    }

    private void normalizeInvisibleTransform(final Player player) {
        if (player == null || !player.isInvisibleTransform()) {
            return;
        }
        player.getTransformModel().setModelId(0);
        player.getTransformModel().setPanelId(0);
        player.getTransformModel().setItemId(0);
        player.getTransformModel().setSkillId(0);
        player.getTransformModel().setTransformType(TransformType.PC);
        player.getTransformModel().setTransformId(0);
        player.setTransformed(true);
        player.setTransformedModelId(0);
        player.setTransformedPanelId(0);
        player.setTransformedItemId(0);
        player.setTransformedSkillId(0);
        DAOManager.getDAO(PlayerTransformDAO.class).deletePlTransfo(player.getObjectId());
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
    }

    private int getTransparentSkillId(final int normalSkillId) {
        switch (normalSkillId) {
            case 5030: return 5607;
            case 5031: return 5608;
            case 5032: return 5609;
            case 5033: return 5610;
            case 5034: return 5611;
            case 5035: return 5612;
            case 5036: return 5613;
            case 5037: return 5614;
            case 5038: return 5615;
            case 5039: return 5616;
            case 5040: return 5617;
            case 5041: return 5618;
            case 5042: return 5619;
            case 5043: return 5620;
            case 5044: return 5621;
            case 5045: return 5622;
            case 5046: return 5623;
            case 5047: return 5624;
            case 5048: return 5625;
            case 5049: return 5626;
            case 5050: return 5627;
            case 5051: return 5628;
            case 5052: return 5629;
            case 5053: return 5630;
            case 5054: return 5631;
            case 5055: return 5632;
            case 5056: return 5633;
            case 5057: return 5634;
            case 5058: return 5635;
            case 5059: return 5636;
            case 5060: return 5637;
            case 5061: return 5638;
            case 5062: return 5639;
            case 5063: return 5640;
            case 5064: return 5641;
            case 5065: return 5642;
            case 5066: return 5643;
            case 5067: return 5644;
            case 5068: return 5645;
            case 5069: return 5646;
            case 5070: return 5647;
            case 5071: return 5648;
            case 5072: return 5649;
            case 5375: return 5650;
            case 5074: return 5651;
            case 5376: return 5652;
            case 5076: return 5653;
            case 5077: return 5654;
            case 5078: return 5655;
            case 5079: return 5656;
            case 5080: return 5657;
            default: return normalSkillId;
        }
    }
	
    public void onCombineTransformation(final Player player, final ArrayList<Integer> materials) {
        List<Integer> deleteTransform = new FastList<Integer>();
        int kinahCost = 0;
        for (Integer id : materials) {
            if (id != 0) {
                switch (DataManager.TRANSFORM_BOOK_DATA.getTransformBookById(id).getGrade()) {
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
				}
            }
        } if (player.getInventory().getKinah() < kinahCost) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        }
		final Account account = player.getPlayerAccount();
        int list = 0;
        switch (DataManager.TRANSFORM_BOOK_DATA.getTransformBookById(materials.get(0)).getGrade()) {
            case 1:
                list = Rnd.chance(50) ? 1001 : 1000;
            break;
            case 2:
                list = Rnd.chance(30) ? 1002 : 1001;
            break;
            case 3:
                list = Rnd.chance(20) ? 1003 : 1002;
            break;
            case 4:
                list = Rnd.chance(10) ? 1004 : 1003;
            break;
            case 5:
                list = Rnd.chance(5) ? 1004 : 1003;
            break;
        }
        int rnd = DataManager.ITEM_TRANSFORM_LIST.getTransformList(list).getTransformId().get(Rnd.get(0, DataManager.ITEM_TRANSFORM_LIST.getTransformList(list).getTransformId().size() - 1));
        int transfoRnd = rnd;
        if (player.getTransformList().hasTransformation(transfoRnd)) {
            AccountTransfo transform = player.getTransformList().getTransformation(transfoRnd);
            transform.setCount(transform.getCount() + 1);
            DAOManager.getDAO(AccountTransformDAO.class).updateTransfo(player.getPlayerAccount(), transform);
            player.getTransformCreated().add(transform);
        } else {
            player.getTransformList().addNewTransformation(player, transfoRnd, 1);
        }
        //methode for delete transform
        for (Integer id: materials) {
            if (id != 0) {
                AccountTransfo transfo = player.getTransformList().getTransformation(id);
                if (transfo.getCount() == 0) {
                    return;
					//player.getTransformList().deleteTransformation(account, id);
                    //deleteTransform.add(id);
                } else {
                    transfo.setCount(transfo.getCount() - 1);
                    DAOManager.getDAO(AccountTransformDAO.class).updateTransfo(account, transfo);
                    deleteTransform.add(id);
                }
            }
        }
        PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(2, deleteTransform));
        PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(1, player, transfoRnd, 3, 1));
        player.getTransformList().updateTransformationsList();
		player.getInventory().decreaseKinah(kinahCost);
        //checkCollection(player);
        rnd = 0;
    }
	
    public void addAllToGM(Player player) {
        for (TransformBookTemplate template : DataManager.TRANSFORM_BOOK_DATA.getAllBooks().valueCollection()) {
            AccountTransfo transfo = new AccountTransfo(template.getId(), 4);
            player.getTransformList().addNewTransformation(player, transfo.getCardId(), transfo.getCount());
            PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(1, player, template.getId(), 0, 1));
        }
        player.getTransformList().updateTransformationsList();
    }
	
    /*
	public void checkCollection(Player player) {
        if (player.getTransformCollections().size() != 0) {
            for (TransformCollection collection : player.getTransformCollections().values()) {
                collection.getCb().end(player);
                TransformCollectionTemplate template = DataManager.TRANSFORM_COLLECTION_DATA.getTransformCollectionById(collection.getId());
                if (template.getRewarSkill() != 0) {
                    SkillLearnService.removeSkill(player, template.getRewarSkill());
                }
            }
            player.getTransformCollections().clear();
        } for (TransformCollectionTemplate template : DataManager.TRANSFORM_COLLECTION_DATA.getAllCollection().values()) {
            if (template.getRequired() != null) {
                int needCount = template.getRequired().getIds().size();
                int count = 0;
                for (Integer id : template.getRequired().getIds()) {
                    if (player.getTransformList().hasTransformation(id)) {
                        count++;
                    }
                } if (count == needCount) {
                    TransformCollection collection = new TransformCollection(template.getId());
                    collection.getCb().apply(player, template);
                    player.getTransformCollections().put(template.getId(), collection);
                    if (template.getRewarSkill() != 0) {
                        player.getSkillList().addSkill(player, template.getRewarSkill(), 1);
                    }
                }
            } if (template.getNeedCount() != 0) {
                if (player.getTransformList().getTransformations().size() >= template.getNeedCount()) {
                    TransformCollection collection = new TransformCollection(template.getId());
                    collection.getCb().apply(player, template);
                    player.getTransformCollections().put(template.getId(), collection);
                    if (template.getRewarSkill() != 0) {
                        player.getSkillList().addSkill(player, template.getRewarSkill(), 1);
                    }
                }
            }
        }
        PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(3, player));
    }*/
	
	public static TransformService getInstance() {
		return TransformServiceHolder.INSTANCE;
	}
	
	private static class TransformServiceHolder {
		private static final TransformService INSTANCE = new TransformService();
	}
}