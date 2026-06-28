/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.packet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.Util;

/**
 * Central packet audit sink for the 7.8 opcode migration.
 * <p>
 * Keep this dependency-free and Java 8 compatible: it is used before the full
 * 7.8 opcode map is known, and it must never break gameplay if disk logging
 * fails.
 */
public final class PacketAuditService {

	private static final Logger log = LoggerFactory.getLogger(PacketAuditService.class);
	private static final PacketAuditService INSTANCE = new PacketAuditService();
	private static final Object LOCK = new Object();
	private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private PacketAuditService() {
	}

	public static PacketAuditService getInstance() {
		return INSTANCE;
	}

	public void logUnknown(int opcode, State state, AionConnection con, ByteBuffer rawPacket) {
		PacketProbe probe = PacketProbe.from(rawPacket);
		String line = buildConnectionPrefix("UNKNOWN", opcode, state, con)
			+ "\tlen=" + probe.length
			+ "\tfirstC=" + probe.firstC
			+ "\tfirstH=" + probe.firstH
			+ "\tfirstD=" + probe.firstD
			+ "\tnote=unregistered_opcode";
		appendLine("packet_audit.tsv", line);
		appendHex("packet_audit_hex.log", line, probe.hex);
	}

	public void logInvalid(State state, AionConnection con, String reason, ByteBuffer rawPacket) {
		PacketProbe probe = PacketProbe.from(rawPacket);
		String line = buildConnectionPrefix("INVALID", 0, state, con)
			+ "\tlen=" + probe.length
			+ "\tfirstC=" + probe.firstC
			+ "\tfirstH=" + probe.firstH
			+ "\tfirstD=" + probe.firstD
			+ "\tnote=" + safe(reason);
		appendLine("packet_audit.tsv", line);
		appendHex("packet_audit_hex.log", line, probe.hex);
	}

	public void logAction(String packetName, int opcode, Player player, int action, int objectId, int[] payload, String note) {
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp()).append('\t');
		sb.append("type=ACTION");
		sb.append("\topcode=").append(formatOpcode(opcode));
		sb.append("\tpacket=").append(safe(packetName));
		sb.append("\tstate=IN_GAME");
		appendPlayer(sb, player);
		sb.append("\taction=").append(action);
		sb.append("\tobjectId=").append(objectId);
		sb.append("\tpayload=").append(payload == null ? "[]" : Arrays.toString(payload));
		sb.append("\tnote=").append(safe(note));
		appendLine("packet_audit.tsv", sb.toString());
	}

	public void logAction(String packetName, int opcode, Player player, int action, List<Integer> payload, String note) {
		int[] arr = null;
		if (payload != null) {
			arr = new int[payload.size()];
			for (int i = 0; i < payload.size(); i++) {
				arr[i] = payload.get(i) == null ? 0 : payload.get(i).intValue();
			}
		}
		logAction(packetName, opcode, player, action, 0, arr, note);
	}

	public void logMapping(String oldName, String newName, int opcode, String reason) {
		String line = timestamp()
			+ "\topcode=" + formatOpcode(opcode)
			+ "\told=" + safe(oldName)
			+ "\tnew=" + safe(newName)
			+ "\treason=" + safe(reason);
		appendLine("opcode_name_mappings.tsv", line);
	}

	private String buildConnectionPrefix(String type, int opcode, State state, AionConnection con) {
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp()).append('\t');
		sb.append("type=").append(type);
		sb.append("\topcode=").append(formatOpcode(opcode));
		sb.append("\tpacket=-");
		sb.append("\tstate=").append(state != null ? state.toString() : "-");
		Player player = con != null ? con.getActivePlayer() : null;
		appendPlayer(sb, player);
		sb.append("\taccount=").append(con != null && con.getAccount() != null ? safe(con.getAccount().getName()) : "-");
		sb.append("\tip=").append(con != null ? safe(con.getIP()) : "-");
		return sb.toString();
	}

	private void appendPlayer(StringBuilder sb, Player player) {
		if (player != null) {
			sb.append("\tplayer=").append(safe(player.getName()));
			sb.append("\tobjectId=").append(player.getObjectId());
			sb.append("\tmap=").append(player.getWorldId());
			if (player.getPosition() != null) {
				sb.append("\tinstance=").append(player.getInstanceId());
				sb.append("\tx=").append(Math.round(player.getX() * 100f) / 100f);
				sb.append("\ty=").append(Math.round(player.getY() * 100f) / 100f);
				sb.append("\tz=").append(Math.round(player.getZ() * 100f) / 100f);
			}
		}
		else {
			sb.append("\tplayer=-\tobjectId=0\tmap=0\tinstance=0");
		}
	}

	private void appendLine(String fileName, String line) {
		synchronized (LOCK) {
			File dir = new File("log/packets");
			if (!dir.exists() && !dir.mkdirs()) {
				return;
			}
			File file = new File(dir, fileName);
			try (FileWriter writer = new FileWriter(file, true)) {
				writer.write(line);
				writer.write(System.lineSeparator());
			}
			catch (IOException e) {
				log.warn("Failed to write packet audit file " + fileName, e);
			}
		}
	}

	private void appendHex(String fileName, String header, String hex) {
		synchronized (LOCK) {
			File dir = new File("log/packets");
			if (!dir.exists() && !dir.mkdirs()) {
				return;
			}
			File file = new File(dir, fileName);
			try (FileWriter writer = new FileWriter(file, true)) {
				writer.write(header);
				writer.write(System.lineSeparator());
				writer.write(hex != null ? hex : "<no buffer>");
				writer.write(System.lineSeparator());
				writer.write(System.lineSeparator());
			}
			catch (IOException e) {
				log.warn("Failed to write packet audit hex file " + fileName, e);
			}
		}
	}

	private String timestamp() {
		return DATE.format(new Date());
	}

	private static String formatOpcode(int opcode) {
		return String.format("0x%04X", opcode & 0xFFFF);
	}

	private static String safe(String value) {
		if (value == null) {
			return "-";
		}
		return value.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
	}

	private static final class PacketProbe {
		final int length;
		final int firstC;
		final int firstH;
		final int firstD;
		final String hex;

		private PacketProbe(int length, int firstC, int firstH, int firstD, String hex) {
			this.length = length;
			this.firstC = firstC;
			this.firstH = firstH;
			this.firstD = firstD;
			this.hex = hex;
		}

		static PacketProbe from(ByteBuffer raw) {
			if (raw == null) {
				return new PacketProbe(0, -1, -1, -1, "<no buffer>");
			}
			ByteBuffer ro = raw.asReadOnlyBuffer();
			ro.position(0);
			int len = ro.remaining();
			int firstC = len >= 1 ? (ro.get(0) & 0xFF) : -1;
			int firstH = len >= 2 ? ((ro.get(0) & 0xFF) | ((ro.get(1) & 0xFF) << 8)) : -1;
			int firstD = len >= 4 ? ((ro.get(0) & 0xFF) | ((ro.get(1) & 0xFF) << 8) | ((ro.get(2) & 0xFF) << 16) | ((ro.get(3) & 0xFF) << 24)) : -1;
			ro.position(0);
			return new PacketProbe(len, firstC, firstH, firstD, Util.toHex(ro));
		}
	}
}
