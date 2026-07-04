package admincommands;

import com.aionemu.gameserver.configs.administration.CommandsConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.EnchantType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndArray;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

import java.lang.reflect.Field;

public class Equip extends AdminCommand
{
	public Equip() {
		super("equip");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 0) {
			int i = 0;
			if ("help".startsWith(params[i])) {
				if (params[i + 1] == null) {
					showHelp(admin);
				} else if ("socket".startsWith(params[i + 1])) {
					showHelpSocket(admin);
				} else if ("enchant".startsWith(params[i + 1])) {
					showHelpEnchant(admin);
				}
				return;
			}
			Player player = null;
			player = World.getInstance().findPlayer(Util.convertName(params[i]));
			if (player == null) {
				VisibleObject target = admin.getTarget();
				if (target instanceof Player) {
					player = (Player) target;
				} else {
					player = admin;
				}
			} else
				i++;
			if ("socket".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int manastone = 167000226;
				int quant = 0;
				try {
					manastone = params[i + 1] == null ? manastone : Integer.parseInt(params[i + 1]);
					quant = params[i + 2] == null ? quant : Integer.parseInt(params[i + 2]);
				} catch (Exception ex2) {
					showHelpSocket(admin);
					return;
				}
				socket(admin, player, manastone, quant);
				return;
			} if ("enchant".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int enchant = 0;
				try {
					enchant = params[i + 1] == null ? enchant : Integer.parseInt(params[i + 1]);
				} catch (Exception ex) {
					showHelpEnchant(admin);
					return;
				}
				enchant(admin, player, enchant);
				return;
			}
		}
		showHelp(admin);
	}
	
	private void socket(Player admin, Player player, int manastone, int quant) {
		if (manastone != 0 && (manastone < 167075000 || manastone > 167079999)) {
			PacketSendUtility.sendMessage(admin, "You are suposed to give the item id for a" + " Manastone or 0 to remove all manastones.");
			return;
		} for (Item targetItem : player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isUpgradable(targetItem)) {
				if (manastone == 0) {
					ItemEquipmentListener.removeStoneStats(targetItem.getItemStones(), player.getGameStats());
					ItemSocketService.removeAllManastone(player, targetItem);
				} else {
					int counter = quant <= 0 ? getMaxSlots(targetItem) : quant;
					while (targetItem.getItemStones().size() < getMaxSlots(targetItem) && counter >= 0) {
						ManaStone manaStone = ItemSocketService.addManaStone(targetItem, manastone);
						ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
						counter--;
					}
				}
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
			}
		} if (manastone == 0) {
			if (player == admin) {
				PacketSendUtility.sendMessage(player, "All Manastones removed from all equipped Items");
			} else {
				PacketSendUtility.sendMessage(admin, "All Manastones removed from all equipped Items by the Player " + player.getName());
				PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " removed all manastones from all your equipped Items");
			}
		} else {
			if (player == admin) {
				PacketSendUtility.sendMessage(player, quant + "x [item: " + manastone + "] were added to free slots on all equipped items");
			} else {
				PacketSendUtility.sendMessage(admin, quant + "x [item: " + manastone + "] were added to free slots on all equipped items by the Player " + player.getName());
				PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " added " + quant + "x [item: " + manastone + "] to free slots on all your equipped items");
			}
		}
	}
	
	private void enchant(Player admin, Player player, int enchant) {
		for (Item targetItem : player.getEquipment().getEquippedItemsWithoutStigma()) {
				if (isUpgradable(targetItem)) {
					if (targetItem.getEnchantPvPvELevel() == enchant) {
						continue;
					} if (enchant > 20) {
						enchant = 20;
					} if (enchant < 0) {
						enchant = 0;
					}
					targetItem.setEnchantPvPvELevel(enchant);
					if (targetItem.isEquipped()) {
						player.getGameStats().updateStatsVisually();
					}
					ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				}
		} if (player == admin) {
			PacketSendUtility.sendMessage(player, "All equipped items were enchanted to level " + enchant);
		} else {
			PacketSendUtility.sendMessage(admin, "All equipped items by the Player " + player.getName() + " were enchanted to " + enchant);
			PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " enchanted all your equipped items to level " + enchant);
		}
	}
	
	public static boolean isUpgradable(Item item) {
		if (item.getItemTemplate().isNoEnchant() && !item.getItemTemplate().isStigma()) {
			return false;
		} if (item.getItemTemplate().isWeapon()) {
			return true;
		} if (item.getItemTemplate().isStigma()) {
			return true;
		} if (item.getItemTemplate().isArmor()) {
			int at = item.getItemTemplate().getItemSlot();
			if (at == 1 || /* Main Hand */
			    at == 2 || /* Sub Hand */
				at == 4 || /* Helmet */
			    at == 8 || /* Jacket */
			    at == 16 || /* Gloves */
			    at == 32 || /* Boots */
				at == 192 || /* Earrings */
				at == 768 || /* Ring */
			    at == 2048 || /* Shoulder */
			    at == 4096 || /* Pants */
				at == 32768 || /* Wing */
				at == 65536 || /* Belt/Band */
			    at == 131072 || /* Main Off Hand */
			    at == 262144 || /* Sub Off Hand */
				at == 524288 || /* Plume */
				at == 2097152 || /* Bracelet */
				at == 8388608) { /* Glyph */
				return true;
			}
		}
		return false;
	}
	
	public static int getMaxSlots(Item item) {
		int slots = 0;
		switch (item.getItemTemplate().getItemQuality()) {
			case ANCIENT:
			case RELIC:
			case FINALITY:
				slots = 6;
			break;
			default:
				slots = 0;
			break;
		} if (item.getItemTemplate().getItemType() == ItemType.DRACONIC) {
			slots = 3;
		} else if (item.getItemTemplate().getItemType() == ItemType.ABYSS) {
			slots = 3;
		}
		return slots;
	}
	
	private void showHelp(Player admin) {
		PacketSendUtility.sendMessage(admin, "[Help: Equip Command]\n"
		+ "  Use //equip help <socket|enchant|tempering|godstone> for more details on the command.\n"
		+ "  Notice: This command uses smart matching. You may abbreviate most commands.\n"
		+ "  For example: (//equip so 167000226 5) will match to (//equip socket 167000226 5)");
	}
	
	private void showHelpEnchant(Player admin) {
		PacketSendUtility.sendMessage(admin, "Syntax:  //equip [playerName] enchant [EnchantLevel = 0]\n"
		+ "  This command Enchants all items equipped up to 25.\n"
		+ "  Notice: You can ommit parameters between [], especially playerName.\n"
		+ "  Target: Named player, then targeted player, only then self.\n" + "  Default Value: EnchantLevel is 0.");
	}
	
	private void showHelpSocket(Player admin) {
		PacketSendUtility.sendMessage(admin, "Syntax:  //equip [playerName] socket [ManastoneID = 167000226] [Quantity = 0]\n"
		+ "  This command Sockets all free slots on equipped items, with the given manastone id.\n"
		+ "  Use ManastoneID = 0 to remove all Manastones. Quantity = 0 means to fill all free slots.\n"
		+ "  Notice: You can ommit parameters between [], especially playerName.\n"
		+ "  Target: Named player, then targeted player, only then self.\n"
		+ "  Default Value: ManastoneID is 167000226, Quantity is 0 meaning fill all slots.");
	}
	
	@Override
	public void onFail(Player player, String message) {
		showHelp(player);
	}
}