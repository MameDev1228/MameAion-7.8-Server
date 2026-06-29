/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.packet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Lightweight protocol trace buffer for 7.8/JDK25 migration work.
 * <p>
 * Normal packet traffic is kept in memory as a small per-connection ring buffer.
 * Disk dumps are written only on disconnect/exception/invalid packet, so it stays
 * safe enough to enable while finding missing 7.8 opcodes and packet structure
 * mismatches.
 */
public final class ProtocolTraceService {

	private static final Logger log = LoggerFactory.getLogger(ProtocolTraceService.class);
	private static final ProtocolTraceService INSTANCE = new ProtocolTraceService();
	private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final SimpleDateFormat FILE_DATE = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
	private static final SimpleDateFormat DAY = new SimpleDateFormat("yyyy-MM-dd");
	private static final Object FILE_LOCK = new Object();

	private final Map<AionConnection, TraceState> traces = new WeakHashMap<AionConnection, TraceState>();

	private ProtocolTraceService() {
	}

	public static ProtocolTraceService getInstance() {
		return INSTANCE;
	}

	public void recordClient(AionConnection con, String packetName, int opcode, State state, ByteBuffer rawPacket, String note) {
		if (!DeveloperConfig.PROTOCOL_TRACE_ENABLE || con == null) {
			return;
		}
		String line = buildPacketLine("C2S", packetName, opcode, state, con, rawPacket, note);
		state(con).addIn(line);
		if (DeveloperConfig.PROTOCOL_TRACE_LOG_CLIENT_PACKETS) {
			appendLine("protocol_packets.tsv", line);
		}
	}

	public void recordServer(AionConnection con, AionServerPacket packet, ByteBuffer rawPacket, String note) {
		if (!DeveloperConfig.PROTOCOL_TRACE_ENABLE || con == null || packet == null) {
			return;
		}
		String line = buildPacketLine("S2C", packet.getPacketName(), packet.getOpcode(), con.getState(), con, rawPacket, note);
		state(con).addOut(line);
		if (DeveloperConfig.PROTOCOL_TRACE_LOG_SERVER_PACKETS) {
			appendLine("protocol_packets.tsv", line);
		}
	}

	public void recordEvent(AionConnection con, String event, String note) {
		if (!DeveloperConfig.PROTOCOL_TRACE_ENABLE || con == null) {
			return;
		}
		String line = timestamp() + "\ttype=EVENT\tevent=" + safe(event) + "\t" + connectionInfo(con) + "\tnote=" + safe(note);
		state(con).addEvent(line);
		appendLine("protocol_events.tsv", line);
	}

	public void enterWorldStep(AionConnection con, int objectId, String step, String note) {
		if (!DeveloperConfig.PROTOCOL_TRACE_ENABLE || !DeveloperConfig.PROTOCOL_TRACE_ENTER_WORLD || con == null) {
			return;
		}
		String line = timestamp() + "\ttype=ENTER_WORLD\tobjectId=" + objectId + "\tstep=" + safe(step) + "\t" + connectionInfo(con) + "\tnote=" + safe(note);
		state(con).addEvent(line);
		appendLine("enter_world_trace.tsv", line);
	}

	public void enterWorldStep(AionConnection con, Player player, int objectId, String step, String note) {
		if (!DeveloperConfig.PROTOCOL_TRACE_ENABLE || !DeveloperConfig.PROTOCOL_TRACE_ENTER_WORLD || con == null) {
			return;
		}
		String line = timestamp() + "\ttype=ENTER_WORLD\tobjectId=" + objectId + "\tstep=" + safe(step) + "\t" + connectionInfo(con, player) + "\tnote=" + safe(note);
		state(con).addEvent(line);
		appendLine("enter_world_trace.tsv", line);
	}

	public void dump(AionConnection con, String reason, Throwable throwable) {
		if (!DeveloperConfig.PROTOCOL_TRACE_ENABLE || con == null) {
			return;
		}
		TraceState state = state(con);
		File file = traceFile(con, reason);
		synchronized (FILE_LOCK) {
			File dir = file.getParentFile();
			if (dir != null && !dir.exists() && !dir.mkdirs()) {
				return;
			}
			try (FileWriter fw = new FileWriter(file, false)) {
				fw.write("MameAion 7.8 Protocol Trace Dump\n");
				fw.write("reason=" + safe(reason) + "\n");
				fw.write("time=" + timestamp() + "\n");
				fw.write(connectionInfo(con) + "\n");
				if (throwable != null) {
					fw.write("\n[THROWABLE]\n");
					fw.write(stackTrace(throwable));
					fw.write("\n");
				}
				fw.write("\n[RECENT EVENTS]\n");
				for (String line : state.eventsSnapshot()) {
					fw.write(line);
					fw.write(System.lineSeparator());
				}
				fw.write("\n[LAST RECEIVED C2S]\n");
				for (String line : state.inSnapshot()) {
					fw.write(line);
					fw.write(System.lineSeparator());
				}
				fw.write("\n[LAST SENT S2C]\n");
				for (String line : state.outSnapshot()) {
					fw.write(line);
					fw.write(System.lineSeparator());
				}
			}
			catch (IOException e) {
				log.warn("Failed to write protocol trace dump: " + file.getPath(), e);
				return;
			}
		}
		log.warn("Protocol trace dumped: " + file.getPath() + " reason=" + reason);
	}

	public void dumpAndClear(AionConnection con, String reason, Throwable throwable) {
		dump(con, reason, throwable);
		if (con != null) {
			synchronized (traces) {
				traces.remove(con);
			}
		}
	}

	private TraceState state(AionConnection con) {
		synchronized (traces) {
			TraceState state = traces.get(con);
			if (state == null) {
				state = new TraceState();
				traces.put(con, state);
			}
			return state;
		}
	}

	private String buildPacketLine(String direction, String packetName, int opcode, State state, AionConnection con, ByteBuffer rawPacket, String note) {
		PacketProbe probe = PacketProbe.from(rawPacket, DeveloperConfig.PROTOCOL_TRACE_HEX_BYTES);
		StringBuilder sb = new StringBuilder(256);
		sb.append(timestamp());
		sb.append("\ttype=").append(direction);
		sb.append("\topcode=").append(formatOpcode(opcode));
		sb.append("\tpacket=").append(safe(packetName));
		sb.append("\tstate=").append(state != null ? state.toString() : "-");
		sb.append("\tlen=").append(probe.length);
		sb.append("\tfirstC=").append(probe.firstC);
		sb.append("\tfirstH=").append(probe.firstH);
		sb.append("\tfirstD=").append(probe.firstD);
		sb.append("\t").append(connectionInfo(con));
		sb.append("\tnote=").append(safe(note));
		if (DeveloperConfig.PROTOCOL_TRACE_HEX_BYTES > 0) {
			sb.append("\thexHead=").append(probe.hexHead);
		}
		return sb.toString();
	}

	private String connectionInfo(AionConnection con) {
		return connectionInfo(con, con != null ? con.getActivePlayer() : null);
	}

	private String connectionInfo(AionConnection con, Player player) {
		StringBuilder sb = new StringBuilder(160);
		sb.append("ip=").append(con != null ? safe(con.getIP()) : "-");
		sb.append("\taccount=").append(con != null && con.getAccount() != null ? safe(con.getAccount().getName()) : "-");
		if (player != null) {
			sb.append("\tplayer=").append(safe(player.getName()));
			sb.append("\tobjectId=").append(player.getObjectId());
			sb.append("\tmap=").append(player.getWorldId());
			sb.append("\tinstance=").append(player.getInstanceId());
			if (player.getPosition() != null) {
				sb.append("\tx=").append(Math.round(player.getX() * 100f) / 100f);
				sb.append("\ty=").append(Math.round(player.getY() * 100f) / 100f);
				sb.append("\tz=").append(Math.round(player.getZ() * 100f) / 100f);
			}
		}
		else {
			sb.append("\tplayer=-\tobjectId=0\tmap=0\tinstance=0");
		}
		return sb.toString();
	}

	private File traceFile(AionConnection con, String reason) {
		Player player = con.getActivePlayer();
		String playerName = player != null ? player.getName() : "no_player";
		int objectId = player != null ? player.getObjectId() : 0;
		String fileName = sanitize(reason) + "_" + sanitize(playerName) + "_" + objectId + "_" + FILE_DATE.format(new Date()) + ".log";
		return new File(DeveloperConfig.PROTOCOL_TRACE_DIR + File.separator + DAY.format(new Date()) + File.separator + fileName);
	}

	private void appendLine(String fileName, String line) {
		synchronized (FILE_LOCK) {
			File dir = new File(DeveloperConfig.PROTOCOL_TRACE_DIR);
			if (!dir.exists() && !dir.mkdirs()) {
				return;
			}
			File file = new File(dir, fileName);
			try (FileWriter writer = new FileWriter(file, true)) {
				writer.write(line);
				writer.write(System.lineSeparator());
			}
			catch (IOException e) {
				log.warn("Failed to write protocol trace file " + fileName, e);
			}
		}
	}

	private static String timestamp() {
		return DATE.format(new Date());
	}

	private static String formatOpcode(int opcode) {
		return String.format("0x%04X", opcode & 0xFFFF);
	}

	private static String stackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	private static String safe(String value) {
		if (value == null) {
			return "-";
		}
		return value.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
	}

	private static String sanitize(String value) {
		String safe = safe(value);
		StringBuilder sb = new StringBuilder(safe.length());
		for (int i = 0; i < safe.length(); i++) {
			char c = safe.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.') {
				sb.append(c);
			}
			else {
				sb.append('_');
			}
		}
		return sb.length() == 0 ? "trace" : sb.toString();
	}

	private static final class TraceState {
		private final ArrayDeque<String> in = new ArrayDeque<String>();
		private final ArrayDeque<String> out = new ArrayDeque<String>();
		private final ArrayDeque<String> events = new ArrayDeque<String>();

		synchronized void addIn(String line) {
			add(in, line);
		}

		synchronized void addOut(String line) {
			add(out, line);
		}

		synchronized void addEvent(String line) {
			add(events, line);
		}

		synchronized String[] inSnapshot() {
			return in.toArray(new String[in.size()]);
		}

		synchronized String[] outSnapshot() {
			return out.toArray(new String[out.size()]);
		}

		synchronized String[] eventsSnapshot() {
			return events.toArray(new String[events.size()]);
		}

		private void add(ArrayDeque<String> deque, String line) {
			int max = DeveloperConfig.PROTOCOL_TRACE_LAST_PACKETS;
			if (max < 10) {
				max = 10;
			}
			while (deque.size() >= max) {
				deque.removeFirst();
			}
			deque.addLast(line);
		}
	}

	private static final class PacketProbe {
		final int length;
		final int firstC;
		final int firstH;
		final int firstD;
		final String hexHead;

		private PacketProbe(int length, int firstC, int firstH, int firstD, String hexHead) {
			this.length = length;
			this.firstC = firstC;
			this.firstH = firstH;
			this.firstD = firstD;
			this.hexHead = hexHead;
		}

		static PacketProbe from(ByteBuffer raw, int maxBytes) {
			if (raw == null) {
				return new PacketProbe(0, -1, -1, -1, "<no buffer>");
			}
			ByteBuffer ro = raw.asReadOnlyBuffer();
			ro.position(0);
			int len = ro.remaining();
			int firstC = len >= 1 ? (ro.get(0) & 0xFF) : -1;
			int firstH = len >= 2 ? ((ro.get(0) & 0xFF) | ((ro.get(1) & 0xFF) << 8)) : -1;
			int firstD = len >= 4 ? ((ro.get(0) & 0xFF) | ((ro.get(1) & 0xFF) << 8) | ((ro.get(2) & 0xFF) << 16) | ((ro.get(3) & 0xFF) << 24)) : -1;
			int limit = maxBytes <= 0 ? 0 : Math.min(len, maxBytes);
			StringBuilder hex = new StringBuilder(limit * 3);
			for (int i = 0; i < limit; i++) {
				if (i > 0) {
					hex.append(' ');
				}
				hex.append(String.format("%02X", ro.get(i) & 0xFF));
			}
			if (len > limit) {
				hex.append(" ...");
			}
			return new PacketProbe(len, firstC, firstH, firstD, hex.toString());
		}
	}
}
