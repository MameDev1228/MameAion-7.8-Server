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
package com.aionemu.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerHouseOwnerFlags;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.craft.RelinquishCraftStatus;
import com.aionemu.gameserver.services.item.ItemChargeService;
import com.aionemu.gameserver.services.player.PlayerFameService;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialogService
{
	private static final Logger log = LoggerFactory.getLogger(DialogService.class);
	
	public static void onCloseDialog(Npc npc, Player player) {
		switch (npc.getObjectTemplate().getTitleId()) {
			case 350409:
			case 358046:
			case 358047:
			case 358048:
			case 358049:
			case 463212:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npc.getObjectId(), 0));
				Legion legion = player.getLegion();
				if (legion != null) {
					LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
					if (lwh.getWhUser() == player.getObjectId()) {
						lwh.setWhUser(0);
					}
				}
			break;
			case 314362: //Stigma Master A.
			case 314365: //Stigma Master B.
			case 314366: //Stigma Master C.
			case 350410: //Stigma Master.
			case 469968: //Stigma 5.8
			case 469977: //Stigma 5.8
			case 469982: //Stigma 5.8
			case 469991: //Stigma 5.8
			case 470033: //Stigma 5.8
			case 470042: //Stigma 5.8
			case 470047: //Stigma 5.8
			case 470056: //Stigma 5.8
			case 469969: //Trade Broker 5.8
			case 469976: //Trade Broker 5.8
			case 469983: //Trade Broker 5.8
			case 469990: //Trade Broker 5.8
			case 470034: //Trade Broker 5.8
			case 470041: //Trade Broker 5.8
			case 470048: //Trade Broker 5.8
			case 470055: //Trade Broker 5.8
			case 350419: //Trade Broker.
			case 358063: //Trade Broker 4.8
			case 358493: //Stigma Master 4.9
			case 358523: //Stigma Master 4.9
			case 370243: //Aethercraft Technician.
			case 370503: //<Gogorunerk Solution,INC>
			case 462878: //<Village Alliance>
			case 466226: //Stigma Researcher.
			case 472253: //비밀의 현인 7.x
			case 472254: //미니온 명인 7.x
			case 472255: //의문의 데바 7.x
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npc.getObjectId(), 0));
			break;
		}
	}
	
	public static void onDialogSelect(int dialogId, final Player player, final Npc npc, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(npc, player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env)) {
			return;
		} if (player.isGM()) {
			PacketSendUtility.sendMessage(player, "<Quest Id>: " + questId);
			PacketSendUtility.sendMessage(player, "<Dialog Id>: " + dialogId);
		}
		int targetObjectId = npc.getObjectId();
		int titleId = npc.getObjectTemplate().getTitleId();
		switch (dialogId) {
			case 2: {
				//Buy Item's.
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "Buy <List> is missing !!");
					break;
				} if (player.isInPlayerMode(PlayerMode.RIDE)) {
				    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_SELL_WHILE_IN_RIDE);
					return;
				}
				int tradeModifier = tradeListTemplate.getSellPriceRate();
				PacketSendUtility.sendPacket(player, new SM_TRADELIST(player, npc, tradeListTemplate, PricesService.getVendorBuyModifier() * tradeModifier / 100));
				//Gold Shop.
				VisibleObject visibleObject = player.getKnownList().getObject(targetObjectId);
				if (visibleObject == null || !(visibleObject instanceof Npc) || MathUtil.getDistance(visibleObject, player) > 999999999 * 999999999 * 999999999 * 999999999 * 999999999 * 999999999) {
					switch (npc.getNpcId()) {
						case 837033:
						case 837034:
						case 837035:
						case 837036:
						case 837037:
						case 837038:
							PacketSendUtility.sendPacket(player, new SM_TRADELIST(player, npc, tradeListTemplate, PricesService.getVendorBuyModifier() * tradeModifier / 100));
						break;
					}
					break;
				} if (npc.getObjectTemplate().isSaveTradeCount()) {
					switch (npc.getNpcId()) {
						//Event.
						case 820543:
						case 820722:
						case 835553:
						case 840794:
						case 870795:
						//Evergale Canyon.
						case 835223:
						case 835224:
						case 835385:
						case 835447:
						case 835474:
						case 835476:
						case 835478:
						case 835480:
						//Shattered Abyssal Splinter 7.x
						case 840667:
						case 840668:
						case 840669:
						case 840670:
						//7.x
						case 837001:
						case 837002:
						case 837375:
						case 837376:
						case 837680:
						case 837681:
						case 837682:
						case 837683:
						case 837741:
						case 837742:
						case 837761:
						case 837762:
					    case 837871:
						case 837872:
						case 837873:
						case 837874:
						case 837875:
						case 837876:
						case 837877:
						case 837878:
						case 837879:
						case 837880:
						case 837881:
						case 837882:
						case 837883:
						case 837884:
						case 837885:
						case 837886:
						case 837887:
						case 837888:
						case 837889:
						case 837890:
						case 837891:
						case 837892:
						case 837893:
						case 837894:
						case 837895:
						case 837896:
						case 837897:
						case 837898:
						case 837899:
						case 837900:
						case 837901:
						case 837902:
						case 837903:
						case 837904:
						case 837905:
						case 837906:
						case 837907:
						case 837908:
						case 837909:
						case 837910:
						case 837911:
						case 837912:
						case 837913:
						case 837914:
						case 837915:
						case 837916:
						case 838042:
						case 838080:
						case 838081:
						case 839490:
						case 839491:
						case 839564:
						case 839565:
						case 839652:
						case 839653:
						case 839655:
						case 839665:
						case 839666:
						//Inggison 7.x
						case 840456:
						case 840466:
						//Gelkmaros 7.x
						case 840457:
						case 840475:
						//Renown Seller 7.x
						case 840538:
						case 840539:
						case 840540:
						case 840541:
						case 840542:
						case 840543:
						case 840544:
						case 840545:
						case 840546:
						case 840547:
						case 840548:
						case 840549:
						case 840551:
						case 840552:
						case 840553:
						case 840554:
						case 840555:
						case 840556:
						case 840557:
						case 840558:
						case 840559:
						case 840561:
						case 840562:
						case 840563:
						case 840564:
						case 840565:
						case 840566:
						case 840567:
						case 840568:
						case 840569:
						case 840571:
						case 840572:
						case 840573:
						case 840574:
						//Underpass B1.
						case 821014:
						case 840662:
						case 840665:
						case 840721:
						case 840722:
						case 840723:
						case 840725:
						case 840726:
						case 840727:
							PacketSendUtility.sendPacket(player, new SM_TRADELIST(player, npc, tradeListTemplate, PricesService.getVendorBuyModifier() * tradeModifier / 100));
						break;
					}
					break;
				}
			}
			case 3: {
				//Sell Item's.
				if (player.isInPlayerMode(PlayerMode.RIDE)) {
				    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_SELL_WHILE_IN_RIDE);
					return;
				}
				PacketSendUtility.sendPacket(player, new SM_SELL_ITEM(targetObjectId, PricesService.getVendorSellModifier(player.getRace())));
				break;
			}
			case 4: {
				//Stigma Open.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1));
				break;
			}
			case 5: {
				//Create Legion.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 2));
				break;
			}
			case 6: {
				//Disband Legion.
				LegionService.getInstance().requestDisbandLegion(npc, player);
				break;
			}
			case 7: {
				//Recreate Legion.
				LegionService.getInstance().recreateLegion(npc, player);
				break;
			}
			case 26: {
				//Warehouse.
				if (player.isInPlayerMode(PlayerMode.RIDE)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PERSONAL_SHOP_RESTRICTION_RIDE);
					return;
				} if (!RestrictionsManager.canUseWarehouse(player)) {
					return;
				}
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 26));
				WarehouseService.sendWarehouseInfo(player, true);
				break;
			}
			case 31: {
				//Quest.
				if (questId != 0) {
					QuestState qs = player.getQuestStateList().getQuestState(questId);
					if (qs != null) {
						if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
							if (!"useitem".equals(npc.getAi2().getName())) {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 10));
							} else {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
							}
						}
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 10));
				} else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
				}
				break;
			}
			case 33: {
				//Trade Broker.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 13));
				break;
			}
			case 35: {
				//Soul Healing.
				final long expLost = player.getCommonData().getExpRecoverable();
				if (expLost == 0) {
					player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
					player.getCommonData().setDeathCount(0);
				}
				final double factor = (expLost < 1000000 ? 0.25 - (0.00000015 * expLost) : 0.1);
				final int price = (int) (expLost * factor);
				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						if (player.getInventory().getKinah() >= price) {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_EXP2(expLost));
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SUCCESS_RECOVER_EXPERIENCE);
							player.getCommonData().resetRecoverableExp();
							player.getInventory().decreaseKinah(price);
							player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
							player.getCommonData().setDeathCount(0);
						} else {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
						}
					}
					@Override
					public void denyRequest(Creature requester, Player responder) {
					}
				};
				if (player.getCommonData().getExpRecoverable() > 0) {
					boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, responseHandler);
					if (result) {
						PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, 0, 0, String.valueOf(price)));
					}
				} else {
					//All HP and MP has been recharged and there is no XP to restore.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_RECOVER_CONFIRM_FULL);
				}
				break;
			} case 36: {
				//Arena City Teleporter.
				int level = player.getLevel();
				switch (npc.getNpcId()) {
					case 204089: //Garm.
						TeleportService2.teleportTo(player, 120010000, 984.0000f, 1543.0000f, 222.1000f, (byte) 0);
					break;
					case 203764: //Epeios.
						TeleportService2.teleportTo(player, 110010000, 1462.5000f, 1326.1000f, 564.1000f, (byte) 0);
					break;
			    }
			} case 37: {
				//Arena City Teleporter.
				switch (npc.getNpcId()) {
					case 204087: //Gunnar.
						TeleportService2.teleportTo(player, 120010000, 1005.1000f, 1528.0000f, 222.0000f, (byte) 0);
					break;
					case 203875: //Nepis.
						TeleportService2.teleportTo(player, 110010000, 1470.0000f, 1343.0000f, 563.0000f, (byte) 0);
					break;
				}
				break;
			}
			case 42: {
				//Remove Manastone.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 20));
				break;
			}
			case 43: {
				//Modify Appearance.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 19));
				break;
			}
			case 44: {
				//Flight & Teleport.
				if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
					int level = player.getLevel();
					if (level < 9) {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
					} else {
						TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
					}
				} else {
					switch (npc.getNpcId()) {
						case 203194: //Daines [Poeta]
							if (player.getRace() == Race.ELYOS) {
								QuestState qs = player.getQuestStateList().getQuestState(1006); //Asension Quest.
								if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
								    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(1006));
								    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
								} else {
									TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
								}
							}
						break;
						case 203679: //Osmar [Ishalgen]
							if (player.getRace() == Race.ASMODIANS) {
								QuestState qs = player.getQuestStateList().getQuestState(2008); //Asension Quest.
								if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
									PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(2008));
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
								} else {
									TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
								}
							}
						break;
						default: {
							TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
						}
					}
				}
				break;
			}
			case 45:
			case 46: {
				//Learn Craft.
				//Improve Extraction.
				//CraftSkillUpdateService.getInstance().learnSkill(player, npc);
				break;
			}
			case 47: {
				//Expand Cube.
				CubeExpandService.expandCube(player, npc);
				break;
			}
			case 48: {
				//Expand Warehouse.
				WarehouseService.expandWarehouse(player, npc);
				break;
			}
			case 53: {
				//Legion Warehouse.
				if (player.isInPlayerMode(PlayerMode.RIDE)) {
				    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PERSONAL_SHOP_RESTRICTION_RIDE);
					return;
				} if (player.getLegion() == null) {
				    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GUILD_LEAVE_I_AM_NOT_BELONG_TO_GUILD);
				    return;
				}
				LegionService.getInstance().openLegionWarehouse(player, npc);
				break;
			}
			case 56: {
				//Close Legion Warehouse.
				break;
			}
			case 58: {
			    //Work Order.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 28));
				break;
			}
			case 59: {
				//Coin's Reward.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 3));
				break;
			}
			case 61:
			case 62: {
				byte changesex = 0;
				byte check_ticket = 2;
				if (dialogId == 62) {
					//Gender Switch.
					changesex = 1;
					if (player.getInventory().getItemCountByItemId(169660000) > 0 || //Gender Switch Ticket
					    player.getInventory().getItemCountByItemId(169660001) > 0 || //[Event] Gender Switch Ticket
						player.getInventory().getItemCountByItemId(169660002) > 0 || //Gender Switch Ticket (60 min)
						player.getInventory().getItemCountByItemId(169660003) > 0 || //[Event] Gender Switch Ticket
						player.getInventory().getItemCountByItemId(169660004) > 0 || //[Event] Gender Switch Ticket
						player.getInventory().getItemCountByItemId(169660005) > 0) { //Gender Switch Ticket
						check_ticket = 1;
					}
				} else {
					//Plastic Surgery.
					if (player.getInventory().getItemCountByItemId(169650000) > 0 || //Plastic Surgery Ticket
					    player.getInventory().getItemCountByItemId(169650001) > 0 || //[Event] Plastic Surgery Ticket
				        player.getInventory().getItemCountByItemId(169650002) > 0 || //[Special] Plastic Surgery Ticket
				        player.getInventory().getItemCountByItemId(169650003) > 0 || //[Special] Plastic Surgery Ticket
			            player.getInventory().getItemCountByItemId(169650004) > 0 || //Plastic Surgery Ticket (60 min)
				        player.getInventory().getItemCountByItemId(169650005) > 0 || //Plastic Surgery Ticket (60 min)
						player.getInventory().getItemCountByItemId(169650006) > 0 || //[Event] Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650007) > 0 || //[Event] Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650008) > 0 || //Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650009) > 0 || //Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650010) > 0 || //Plastic Surgery Ticket (60 min)
						player.getInventory().getItemCountByItemId(169650011) > 0 || //[Stamp] Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650012) > 0 || //Plastic Surgery Ticket (60 min)
						player.getInventory().getItemCountByItemId(169652000) > 0 || //[Event] Plastic Surgery Ticket (60 min)
						player.getInventory().getItemCountByItemId(169652001) > 0 || //[Event] Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169691000) > 0 || //Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(186000449) > 0) { //Plastic Surgery Ticket
						check_ticket = 1;
					}
				}
				PacketSendUtility.sendPacket(player, new SM_PLASTIC_SURGERY(player, check_ticket, changesex));
				player.setEditMode(true);
				break;
			}
			case 66: {
				//Armsfusion.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 29));
				break;
			}
			case 67: {
				//Armsbreaking.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 30));
				break;
			}
			case 68: {
				//Join Faction.
				player.getNpcFactions().enterGuild(npc);
				break;
			}
			case 69: {
				//Leave Faction.
				player.getNpcFactions().leaveNpcFaction(npc);
				break;
			}
			case 70: {
				//Repurchase.
				if (player.isInPlayerMode(PlayerMode.RIDE)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_SELL_WHILE_IN_RIDE);
					return;
				}
				PacketSendUtility.sendPacket(player, new SM_REPURCHASE(player, npc.getObjectId()));
				break;
			}
			case 71: {
				//Adopt Pet.
				PacketSendUtility.sendPacket(player, new SM_PET(6));
				break;
			}
			case 72: {
				//Surrender Pet.
				PacketSendUtility.sendPacket(player, new SM_PET(7));
				break;
			}
			case 73: {
				//Housing Build.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 32));
				break;
			}
			case 74: {
				//Housing Destruct.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 33));
				break;
			}
			case 75: {
				//Deep Conditioning Individual Item.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 35));
				break;
			}
			case 76: {
				//Deep Conditioning All Item.
				ItemChargeService.startChargingEquippedItems(player, targetObjectId, 1);
				break;
			}
			case 78: {
				//News Mod Buy/Exchange.
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeInListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "Buy <Trade In List> is missing !!");
					break;
				} if (player.isInPlayerMode(PlayerMode.RIDE)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_SELL_WHILE_IN_RIDE);
					return;
				}
				PacketSendUtility.sendPacket(player, new SM_TRADE_IN_LIST(npc, tradeListTemplate, 100));
				break;
			}
			case 79: {
				//Give Up Craft Expert.
				RelinquishCraftStatus.getInstance();
				RelinquishCraftStatus.relinquishExpertStatus(player, npc);
				break;
			}
			case 80: {
				//Give Up Craft Master.
				RelinquishCraftStatus.getInstance();
				RelinquishCraftStatus.relinquishMasterStatus(player, npc);
				break;
			}
			case 84: {
				//Sell & Buy House.
				if ((player.getBuildingOwnerStates() & PlayerHouseOwnerFlags.BIDDING_ALLOWED.getId()) == 0) {
					if (player.getRace() == Race.ELYOS) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(18802));
					} else {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(28802));
					}
					return;
				}
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 38));
				break;
			}
			case 92: {
				//Pet's [Great Sidekick]
				PacketSendUtility.sendPacket(player, new SM_PET(16));
				break;
			}
			case 93: {
				//Pet's [Bannish Sidekick]
				PacketSendUtility.sendPacket(player, new SM_PET(17));
				break;
			}
			case 94: {
				//Augmenting Individual Item.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 42));
				break;
			}
			case 95: {
				//Augmenting All Item.
				ItemChargeService.startChargingEquippedItems(player, targetObjectId, 2);
				break;
			}
			case 96: {
				//Housing Studio.
				HousingService.getInstance().recreatePlayerStudio(player);
				break;
			}
			case 100: {
				//Town.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 43));
				break;
			}
			case 103: {
			    //Purchase List.
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getPurchaseListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "Buy <Purchase List> is missing !!");
					break;
				} if (player.isInPlayerMode(PlayerMode.RIDE)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_SELL_WHILE_IN_RIDE);
					return;
				}
				PacketSendUtility.sendPacket(player, new SM_SELL_ITEM(targetObjectId, tradeListTemplate, 100));
				break;
			}
			case 104: {
				//Teleport Simple.
				if (player.getRace() == Race.ELYOS) {
                    switch (npc.getNpcId()) {
                        //Walk Of Fame Entrance Manager
						case 802437: //Tisiphone
                            if (player.getAbyssRank().getRank().getId() < AbyssRankEnum.GENERAL.getId()) {
                                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
                                return;
                            }
                            TeleportService2.teleportTo(player, 110070000, 503.80746f, 417.2141f, 126.789635f, (byte) 68);
                        break;
                    }
                } else {
                    switch (npc.getNpcId()) {
                        //Walk Of Fame Entrance Manager
						case 802439: //Bulundur.
                            if (player.getAbyssRank().getRank().getId() < AbyssRankEnum.GENERAL.getId()) {
                                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
                                return;
                            }
                            TeleportService2.teleportTo(player, 120080000, 385.92166f, 251.25146f, 93.129425f, (byte) 24);
                        break;
                    }
                }
                break;
			}
			case 106: {
				//Move Item Skin.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 51));
				break;
			}
			case 107: {
				//Trade In Upgrade.
				break;
			}
			case 109: {
				//Item Upgrade.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 52));
				break;
			}
			case 125: {
				//Stigma Enchant.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 53));
				break;
			}
			case 126: {
			    //Event Evolution.
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 55));
				break;
			}
			case 128: {
				//Soul Healing Free.
				final long expLost = player.getCommonData().getExpRecoverable();
				if (expLost == 0) {
					player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
					player.getCommonData().setDeathCount(0);
				}
				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_EXP2(expLost));
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SUCCESS_RECOVER_EXPERIENCE);
						player.getCommonData().resetRecoverableExp();
						player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
						player.getCommonData().setDeathCount(0);
					}
					@Override
					public void denyRequest(Creature requester, Player responder) {
					}
				};
				if (player.getCommonData().getExpRecoverable() > 0) {
					boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, responseHandler);
					if (result) {
						PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, 0, 0, 0));
					}
				} else {
					//All HP and MP has been recharged and there is no XP to restore.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_RECOVER_CONFIRM_FULL);
				}
				break;
			}
			case 130: {
				//Soul Healing Free.
				final long expLost = player.getCommonData().getExpRecoverable();
				if (expLost == 0) {
					player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
					player.getCommonData().setDeathCount(0);
				}
				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_EXP2(expLost));
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SUCCESS_RECOVER_EXPERIENCE);
						player.getCommonData().resetRecoverableExp();
						player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
						player.getCommonData().setDeathCount(0);
					}
					@Override
					public void denyRequest(Creature requester, Player responder) {
					}
				};
				if (player.getCommonData().getExpRecoverable() > 0) {
					boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, responseHandler);
					if (result) {
						PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, 0, 0, 0));
					}
				} else {
					//All HP and MP has been recharged and there is no XP to restore.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_RECOVER_CONFIRM_FULL);
				}
				break;
			}
			case 10000:
			case 10001:
			case 10002: {
				if (questId == 0) {
					TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npc.getNpcId());
					PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(npc.getNpcId(), dialogId, player.getRace());
					if (portalPath != null) {
						PortalService.port(portalPath, player, targetObjectId);
					} else if (template != null) {
						TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
						if (loc != null) {
							TeleportService2.teleport(template, loc.getLocId(), player, npc, npc.getAi2().getName().equals("general") ? TeleportAnimation.JUMP_ANIMATION : TeleportAnimation.BEAM_ANIMATION);
						}
					}
				} else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				}
				break;
			} default: {
				if (questId > 0) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				} else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId));
				}
				break;
			}
		}
	}
}