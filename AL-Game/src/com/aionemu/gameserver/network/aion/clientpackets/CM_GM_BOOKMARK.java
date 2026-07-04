package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gm.GmCommands;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

public class CM_GM_BOOKMARK extends AionClientPacket
{
	private static final Pattern ITEM_ID_PATTERN = Pattern.compile("(?:item:|@item:)?(\\d{6,9})");
	private static final Pattern INT_PATTERN = Pattern.compile("-?\\d+");

	private GmCommands command;
	private String playerName;
	private String rawCommand;
	private String[] args;
	
	public CM_GM_BOOKMARK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		rawCommand = readS();
		String trimmed = rawCommand == null ? "" : rawCommand.trim();
		String[] parts = trimmed.length() == 0 ? new String[0] : trimmed.split("\\s+");
		command = parts.length > 0 ? GmCommands.getValue(parts[0]) : GmCommands.UNKNOWN;
		args = new String[Math.max(0, parts.length - 1)];
		for (int i = 1; i < parts.length; i++) {
			args[i - 1] = parts[i];
		}
		playerName = args.length > 0 ? args[0].trim() : null;
	}
	
	@Override
	protected void runImpl() {
		Player admin = getConnection().getActivePlayer();
		if (admin == null) {
			return;
		}
		if (admin.getAccessLevel() < AdminConfig.GM_PANEL) {
			return;
		}
		if (command == null || command == GmCommands.UNKNOWN) {
			PacketSendUtility.sendMessage(admin, "Unknown GM panel command: " + rawCommand);
			return;
		}
		Player player = resolvePlayer(admin);
		if (player == null && requiresPlayer(command)) {
			PacketSendUtility.sendMessage(admin, "Could not resolve target player for GM panel command: " + rawCommand);
			return;
		}
		switch (command) {
			case GM_DIALOG_TELEPORTTO:
				TeleportService2.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ());
			break;
			case GM_DIALOG_RECALL:
				TeleportService2.teleportTo(player, admin.getWorldId(), admin.getX(), admin.getY(), admin.getZ());
			break;
			case CLASSUP:
				handleClassUp(admin, player);
			break;
			case WISH:
			case ITEM:
			case ADDITEM:
				handleGiveItem(admin, player);
			break;
			case LEVEL:
			case SETLEVEL:
				handleSetLevel(admin, player);
			break;
			case GM_DIALOG:
			case GM_DIALOG_POS:
			case GM_DIALOG_MEMO:
			case GM_DIALOG_BOOKMARK:
			case GM_DIALOG_INVENTORY:
			case GM_DIALOG_SKILL:
			case GM_DIALOG_STATUS:
			case GM_DIALOG_QUEST:
			case GM_DIALOG_REFRESH:
			case GM_DIALOG_WAREHOUSE:
			case GM_DIALOG_MAIL:
			case GM_POLL_DIALOG:
			case GM_POLL_DIALOG_SUBMIT:
			case GM_BOOKMARK_DIALOG:
			case GM_BOOKMARK_DIALOG_ADD_BOOKMARK:
			case GM_MEMO_DIALOG:
			case GM_MEMO_DIALOG_ADD_MEMO:
			case GM_DIALOG_CHECK_BOT1:
			case GM_DIALOG_CHECK_BOT99:
			case GM_INDICATOR_DIALOG_TOOLTIP_HOUSING_MODE:
			case GM_DIALOG_CHARACTER:
			case GM_DIALOG_OPTION:
			case GM_DIALOG_BUILDER_CONTROL:
			case GM_DIALOG_BUILDER_COMMAND:
			break;
			default:
				PacketSendUtility.sendMessage(admin, "Invalid GM panel command: " + command.name() + " raw=" + rawCommand);
			break;
		}
	}

	private boolean requiresPlayer(GmCommands cmd) {
		switch (cmd) {
			case GM_DIALOG_TELEPORTTO:
			case GM_DIALOG_RECALL:
			case CLASSUP:
			case WISH:
			case ITEM:
			case ADDITEM:
			case LEVEL:
			case SETLEVEL:
				return true;
			default:
				return false;
		}
	}

	private Player resolvePlayer(Player admin) {
		if (args != null) {
			for (String arg : args) {
				if (arg == null || arg.length() == 0 || isNumericLike(arg) || looksLikeItemLink(arg)) {
					continue;
				}
				Player found = World.getInstance().findPlayer(Util.convertName(arg));
				if (found != null) {
					return found;
				}
			}
		}
		if (playerName != null && playerName.length() > 0 && !isNumericLike(playerName) && !looksLikeItemLink(playerName)) {
			Player found = World.getInstance().findPlayer(Util.convertName(playerName));
			if (found != null) {
				return found;
			}
		}
		VisibleObject target = admin.getTarget();
		if (target instanceof Player) {
			return (Player) target;
		}
		return admin;
	}

	private boolean isNumericLike(String arg) {
		return arg != null && INT_PATTERN.matcher(arg).matches();
	}

	private boolean looksLikeItemLink(String arg) {
		return arg != null && (arg.indexOf("item:") >= 0 || arg.indexOf("@item:") >= 0);
	}

	private void handleClassUp(Player admin, Player player) {
		if (!player.getPlayerClass().isStartingClass()) {
			PacketSendUtility.sendMessage(admin, player.getName() + " already switched class: " + player.getPlayerClass());
			return;
		}
		if (player.getLevel() < 9) {
			PacketSendUtility.sendMessage(admin, player.getName() + " must be level 9 or higher to class up.");
			PacketSendUtility.sendMessage(player, "You can only switch class at level 9.");
			return;
		}
		ClassChangeService.showClassChangeDialog(player);
		PacketSendUtility.sendMessage(admin, "Opened class change dialog for " + player.getName() + ".");
	}

	private void handleGiveItem(Player admin, Player receiver) {
		int itemId = findItemId();
		if (itemId <= 0) {
			PacketSendUtility.sendMessage(admin, "GM panel item/wish command has no item id: " + rawCommand);
			PacketSendUtility.sendMessage(admin, "Supported forms: wish <player> <itemId> [count] [enchant], item <itemId> [count] [enchant]");
			return;
		}
		if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
			PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + itemId);
			return;
		}
		long count = Math.max(1L, findLongAfterItemId(itemId, 1L, 0));
		int enchant = (int) Math.max(0L, findLongAfterItemId(itemId, 0L, 1));
		long notAdded = ItemService.addItemAndEnchant(receiver, itemId, count, enchant);
		if (notAdded == 0) {
			PacketSendUtility.sendMessage(admin, "GM panel gave " + count + " x [item:" + itemId + "] enchant=" + enchant + " to " + receiver.getName() + ".");
			if (receiver != admin) {
				PacketSendUtility.sendMessage(receiver, "You received " + count + " x [item:" + itemId + "] from " + admin.getName() + ".");
			}
		} else {
			PacketSendUtility.sendMessage(admin, "Item couldn't be added. notAdded=" + notAdded + " itemId=" + itemId + " receiver=" + receiver.getName());
		}
	}

	private int findItemId() {
		if (rawCommand == null) {
			return 0;
		}
		Matcher matcher = ITEM_ID_PATTERN.matcher(rawCommand);
		while (matcher.find()) {
			try {
				int value = Integer.parseInt(matcher.group(1));
				if (value >= 1000000) {
					return value;
				}
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	private long findLongAfterItemId(int itemId, long defaultValue, int occurrenceAfterItemId) {
		if (args == null) {
			return defaultValue;
		}
		boolean seenItem = false;
		int occurrence = 0;
		for (String arg : args) {
			if (arg == null) {
				continue;
			}
			Matcher matcher = INT_PATTERN.matcher(arg);
			while (matcher.find()) {
				try {
					long value = Long.parseLong(matcher.group());
					if (!seenItem && value == itemId) {
						seenItem = true;
						continue;
					}
					if (seenItem) {
						if (occurrence == occurrenceAfterItemId) {
							return value;
						}
						occurrence++;
					}
				} catch (NumberFormatException e) {
					return defaultValue;
				}
			}
		}
		return defaultValue;
	}

	private void handleSetLevel(Player admin, Player player) {
		int level = findLevel();
		if (level <= 0) {
			PacketSendUtility.sendMessage(admin, "GM panel level command has no level: " + rawCommand);
			PacketSendUtility.sendMessage(admin, "Supported forms: level <player> <level>, level <level>");
			return;
		}
		if (level > GSConfig.PLAYER_MAX_LEVEL) {
			level = GSConfig.PLAYER_MAX_LEVEL;
		}
		player.getCommonData().setExp(0);
		player.getCommonData().setLevel(level);
		PacketSendUtility.sendMessage(admin, "GM panel set " + player.getName() + " level to " + level + ".");
		if (player != admin) {
			PacketSendUtility.sendMessage(player, "Your level has been set to " + level + " by " + admin.getName() + ".");
		}
	}

	private int findLevel() {
		if (args == null) {
			return 0;
		}
		for (String arg : args) {
			if (arg == null || looksLikeItemLink(arg)) {
				continue;
			}
			if (!isNumericLike(arg)) {
				continue;
			}
			try {
				int value = Integer.parseInt(arg);
				if (value > 0 && value <= 200) {
					return value;
				}
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}
}
