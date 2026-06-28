/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package admincommands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Small in-game helper for checking the 7.8 opcode migration audit state.
 */
public class Packetaudit extends AdminCommand {

	public Packetaudit() {
		super("packetaudit");
	}

	@Override
	public void execute(Player admin, String... params) {
		int limit = 10;
		if (params != null && params.length > 0) {
			try {
				limit = Math.max(1, Math.min(50, Integer.parseInt(params[0])));
			}
			catch (NumberFormatException ignored) {
				// Keep default.
			}
		}

		File file = new File("log/packets/packet_audit.tsv");
		if (!file.exists()) {
			PacketSendUtility.sendMessage(admin, "packet audit log not found: log/packets/packet_audit.tsv");
			return;
		}

		Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
		int lines = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines++;
				String key = extract(line, "opcode=") + " " + extract(line, "packet=") + " " + extract(line, "note=");
				Integer old = counts.get(key);
				counts.put(key, old == null ? 1 : old + 1);
			}
		}
		catch (Exception e) {
			PacketSendUtility.sendMessage(admin, "failed to read packet audit: " + e.getMessage());
			return;
		}

		PacketSendUtility.sendMessage(admin, "Packet audit lines=" + lines + ", groups=" + counts.size());
		int sent = 0;
		for (Map.Entry<String, Integer> entry : counts.entrySet()) {
			PacketSendUtility.sendMessage(admin, entry.getValue() + "x " + entry.getKey());
			if (++sent >= limit) {
				break;
			}
		}
		PacketSendUtility.sendMessage(admin, "Full report: Tools\\MameJDK25\\phase11_packet_audit_report.bat");
	}

	private String extract(String line, String key) {
		int idx = line.indexOf(key);
		if (idx < 0) {
			return key + "-";
		}
		int start = idx + key.length();
		int end = line.indexOf('\t', start);
		String value = end >= 0 ? line.substring(start, end) : line.substring(start);
		return key + value;
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //packetaudit [groupLimit]");
	}
}
